"""Simple layer-2 learning switch logic using OpenFlow Protocol v1.3."""
"""
No experimenter action included with no EPB control message
"""

from ryu.base.app_manager import RyuApp
from ryu.controller.ofp_event import EventOFPSwitchFeatures
from ryu.controller.ofp_event import EventOFPPacketIn
from ryu.controller.handler import set_ev_cls
from ryu.controller.handler import CONFIG_DISPATCHER
from ryu.controller.handler import MAIN_DISPATCHER
from ryu.ofproto.ofproto_v1_2 import OFPG_ANY
from ryu.ofproto.ofproto_v1_3 import OFP_VERSION
from ryu.ofproto import ether
from ryu.ofproto import inet
from ryu.lib.packet import packet
from ryu.lib.packet import ethernet
from ryu.lib.packet import udp
from ryu.lib.packet import ipv4
import time
import datetime
import firewallParser
import ConfigParser
from ryu.lib.mac import haddr_to_bin
from ryu.lib.mac import haddr_to_str

import struct
from ryu import utils

# Topology
# LINC-Switch
#	Port 2:  PC
#	Port 3:  Media Server
#	Port 4:  EPB - classified traffic (eth3)
#	Port 5:  EPB - Data Traffic (eth1)
# 	Port 6:  EPB - Control Traffic (eth2)
#	Port 7:  PC2

class L2Switch(RyuApp):
    OFP_VERSIONS = [OFP_VERSION]
    table = {}
    epbTable = {}

    def spawn(*args, **kwargs):
        def _launch(func, *args, **kwargs):
            # mimic gevent's default raise_error=False behaviour
            # by not propergating an exception to the joiner.
            try:
                func(*args, **kwargs)
            except greenlet.GreenletExit:
                pass
            except:
                # log uncaught exception.
                # note: this is an intentional divergence from gevent
                # behaviour. gevent silently ignores such exceptions.
                LOG.error('hub: uncaught exception: %s',
                          traceback.format_exc())

        return eventlet.spawn(_launch, *args, **kwargs)
    def __init__(self, *args, **kwargs):
        super(L2Switch, self).__init__(*args, **kwargs)

    @set_ev_cls(EventOFPSwitchFeatures, CONFIG_DISPATCHER)
    def switch_features_handler(self, ev):
        """Handle switch features reply to install table miss flow entries."""
        datapath = ev.msg.datapath
        [self.install_table_miss(datapath, n) for n in [0, 1]]
	#'''Install ARP Request paths'''
	#self.handle_arp_request(datapath, '10.2.1.1', 6)
	#self.handle_arp_request(datapath, '10.3.1.1', 7)
	#self.handle_arp_request(datapath, '10.4.1.1', 6)
	#print "Installing Layer 2 Match"
	#self.install_l2_match(ofproto, datapath, in_port, eth_dst, eth_src)
	# This is the packet that just came in -- we want to
	# install the rule and also resend the packet.
	#self.install_l2_match(ofproto, datapath, dst_port, eth_src, eth_dst)
	self.handleFirewallPacket(datapath, 1)
	global table
	table = self.load_config("/config.ini", "Mapping")
	print table
	global epbTable
	epbTable = self.load_config("/config.ini", "EPB")
	print epbTable

    def load_config(self, configFile, section):
        Config = ConfigParser.ConfigParser()
        Config.read(configFile)
        dstPortMapping = {}
        options = Config.options(section)
        for dstPort in options:
            try:
                value = Config.get(section, dstPort)
                dstPortMapping[value] = dstPort
                if dstPortMapping[value] == -1:
                    DebugPrint("skip: %s" % dstPort)
            except:
                print("exception on %s!" % dstPort)
                #dstPortMapping[dstPort] = None
        return dstPortMapping

    def create_match(self, parser, fields):
        """Create OFP match struct from the list of fields."""
        match = parser.OFPMatch()
        for a in fields:
            match.append_field(*a)
        return match

    def append_match(self, match, fields):
        """Append fields to OFP match struct."""
        for a in fields:
            match.append_field(*a)
        return match

    def create_flow_mod(self, datapath, idle_timeout, hard_timeout, priority,
                        table_id, match, instructions):
        """Create OFP flow mod message."""
        ofproto = datapath.ofproto
        flow_mod = datapath.ofproto_parser.OFPFlowMod(datapath, 0, 0, table_id,
                                                      ofproto.OFPFC_ADD, idle_timeout,
                                                      hard_timeout, priority,
                                                      ofproto.OFPCML_NO_BUFFER,
                                                      ofproto.OFPP_ANY,
                                                      OFPG_ANY, 0,
                                                      match, instructions)
        return flow_mod

    def install_table_miss(self, datapath, table_id):
        """Create and install table miss flow entries."""
        parser = datapath.ofproto_parser
        ofproto = datapath.ofproto
        empty_match = parser.OFPMatch()
        output = parser.OFPActionOutput(ofproto.OFPP_CONTROLLER,
                                        ofproto.OFPCML_NO_BUFFER)
        write = parser.OFPInstructionActions(ofproto.OFPIT_WRITE_ACTIONS,
                                        [output])
        instructions = [write]
        flow_mod = self.create_flow_mod(datapath, 0, 0, 0, table_id,
                                        empty_match, instructions)
        datapath.send_msg(flow_mod)

    # Test to see UDP traffic flows
    def install_test_flow_udp(self, datapath, ip_addr, dst_port):
        parser = datapath.ofproto_parser
        ofproto = datapath.ofproto
	ip_addr = self.ipv4_to_int(ip_addr)
        match = self.create_match(parser,
                                [(ofproto.OXM_OF_ETH_TYPE,ether.ETH_TYPE_IP),
                                 (ofproto.OXM_OF_IPV4_DST, ip_addr),
                                 (ofproto.OXM_OF_IP_PROTO, 17)])
        output = parser.OFPActionOutput(dst_port, ofproto.OFPCML_NO_BUFFER)
        action = parser.OFPInstructionActions(ofproto.OFPIT_WRITE_ACTIONS,
                                              [output])
        flow_mod = self.create_flow_mod(datapath, 0, 0, 120, 0, match, [action])
        datapath.send_msg(flow_mod)

    def install_flow_udp(self, datapath, src_addr, dst_addr, dst_port):
        parser = datapath.ofproto_parser
        ofproto = datapath.ofproto
        src_addr = self.ipv4_to_int(src_addr)
        dst_addr = self.ipv4_to_int(dst_addr)
        output = parser.OFPActionOutput(dst_port, ofproto.OFPCML_NO_BUFFER)
        #UDP RTP/RTCP Test Traffic .... requires more testing
        match = self.create_match(parser,
                                [(ofproto.OXM_OF_ETH_TYPE,ether.ETH_TYPE_IP),
                                 (ofproto.OXM_OF_IPV4_DST, dst_addr),
                                 (ofproto.OXM_OF_IPV4_SRC, src_addr),
                                 (ofproto.OXM_OF_IP_PROTO, 17)])
        action = parser.OFPInstructionActions(ofproto.OFPIT_WRITE_ACTIONS,
                                              [output])
        flow_mod = self.create_flow_mod(datapath, 0, 0, 126, 0, match, [action])
        datapath.send_msg(flow_mod)
	print "Installed Basic UDP Rule for External Processing ", self.ipv4_to_str(src_addr), " to ", self.ipv4_to_str(dst_addr)

    def media_server_flow(self, datapath, ip_addr, dst_port, port):
    #TCP SRC RTSP Test Traffic .... requires more testing
        parser = datapath.ofproto_parser
        ofproto = datapath.ofproto
        ip_addr = self.ipv4_to_int(ip_addr)
        output = parser.OFPActionOutput(dst_port, ofproto.OFPCML_NO_BUFFER)
        match = self.create_match(parser,
                                [(ofproto.OXM_OF_ETH_TYPE,ether.ETH_TYPE_IP),
                                 (ofproto.OXM_OF_IPV4_SRC, ip_addr),
                                 (ofproto.OXM_OF_IP_PROTO, 6),
                                 (ofproto.OXM_OF_TCP_SRC, port)])
        action = parser.OFPInstructionActions(ofproto.OFPIT_WRITE_ACTIONS,
                                                  [output])
        flow_mod = self.create_flow_mod(datapath, 0, 0, 124, 0, match, [action])
        datapath.send_msg(flow_mod)
        print "Installed Media Server Rule returning traffic to go out port ", dst_port

    def allow_ip_traffic(self, datapath, src_ip_addr, dst_ip_addr, dst_port):
 #TCP SRC RTSP Test Traffic .... requires more testing
        parser = datapath.ofproto_parser
        ofproto = datapath.ofproto
        src_ip_addr = self.ipv4_to_int(src_ip_addr)
        dst_ip_addr = self.ipv4_to_int(dst_ip_addr)
        output = parser.OFPActionOutput(dst_port, ofproto.OFPCML_NO_BUFFER)
        match = self.create_match(parser,
                                [(ofproto.OXM_OF_ETH_TYPE,ether.ETH_TYPE_IP),
                                 (ofproto.OXM_OF_IPV4_SRC, src_ip_addr),
                                 (ofproto.OXM_OF_IPV4_DST, dst_ip_addr)])
        action = parser.OFPInstructionActions(ofproto.OFPIT_WRITE_ACTIONS,
                                                  [output])
        flow_mod = self.create_flow_mod(datapath, 0, 0, 100, 0, match, [action])
        datapath.send_msg(flow_mod)
        print "Installed default traffic for ", src_ip_addr," -> ", dst_ip_addr, " goes out port ",dst_port

    # Drop broadcasting traffic from the cloud
    def block_incoming_traffic(self, datapath, in_port):
 #TCP SRC RTSP Test Traffic .... requires more testing
        parser = datapath.ofproto_parser
        ofproto = datapath.ofproto
        match = self.create_match(parser,
                                    [(ofproto.OXM_OF_IN_PORT, in_port)])
        action = parser.OFPInstructionActions(ofproto.OFPIT_WRITE_ACTIONS,
                                                  [])
        flow_mod = self.create_flow_mod(datapath, 0, 0, 224, 0, match, [action])
        datapath.send_msg(flow_mod)
        print "Block incoming traffic from port ", in_port

    # Allow broadcasting traffic from the cloud
    def allow_arp_request(self, datapath, in_port, dst_port):
        parser = datapath.ofproto_parser
        ofproto = datapath.ofproto
        # ARP Request
        output = parser.OFPActionOutput(dst_port, ofproto.OFPCML_NO_BUFFER)
        match = self.create_match(parser,
                                    [(ofproto.OXM_OF_ETH_TYPE,ether.ETH_TYPE_ARP),
                                     (ofproto.OXM_OF_ARP_OP, 1),
                                     (ofproto.OXM_OF_IN_PORT, in_port)])
        action = parser.OFPInstructionActions(ofproto.OFPIT_WRITE_ACTIONS,
                                                  [output])
        flow_mod = self.create_flow_mod(datapath, 0, 0, 124, 0, match, [action])
        datapath.send_msg(flow_mod)
        print "Allow ARP Requests from port ", in_port, " to port ", dst_port

    def allow_arp_reply(self, datapath, in_port, dst_port):
        parser = datapath.ofproto_parser
        ofproto = datapath.ofproto
        # ARP Reply
        output = parser.OFPActionOutput(dst_port, ofproto.OFPCML_NO_BUFFER)
        match = self.create_match(parser,
                                    [(ofproto.OXM_OF_ETH_TYPE,ether.ETH_TYPE_ARP),
                                     (ofproto.OXM_OF_ARP_OP, 2),
                                     (ofproto.OXM_OF_IN_PORT, in_port)])
        action = parser.OFPInstructionActions(ofproto.OFPIT_WRITE_ACTIONS,
                                                  [output])
        flow_mod = self.create_flow_mod(datapath, 0, 0, 124, 0, match, [action])
        datapath.send_msg(flow_mod)
        print "Allow ARP Reply from port ", in_port, " to port ", dst_port

    # Drop broadcasting traffic from the cloud
    def block_broadcast_flow(self, datapath, in_port):
 #TCP SRC RTSP Test Traffic .... requires more testing
        parser = datapath.ofproto_parser
        ofproto = datapath.ofproto
        match = self.create_match(parser,
                                    [(ofproto.OXM_OF_ETH_TYPE,ether.ETH_TYPE_ARP),
                                     (ofproto.OXM_OF_ARP_OP, 1),
                                     (ofproto.OXM_OF_IN_PORT, in_port)])
        action = parser.OFPInstructionActions(ofproto.OFPIT_WRITE_ACTIONS,
                                                  [])
        flow_mod = self.create_flow_mod(datapath, 0, 0, 124, 0, match, [action])
        datapath.send_msg(flow_mod)
        print "Block ARP Requests from port ", in_port

    def install_experimental_flow(self, datapath, ip_addr, rtsp_port, dst_port):
        parser = datapath.ofproto_parser
        ofproto = datapath.ofproto
        output = parser.OFPActionOutput(dst_port, ofproto.OFPCML_NO_BUFFER)
        #TCP DST RTSP Test Traffic
        ip_addr = self.ipv4_to_int(ip_addr)
        match = self.create_match(parser,
                                [(ofproto.OXM_OF_ETH_TYPE, ether.ETH_TYPE_IP),
                                 (ofproto.OXM_OF_IPV4_DST, ip_addr),
                                 (ofproto.OXM_OF_IP_PROTO, 6),
                                 (ofproto.OXM_OF_TCP_DST, rtsp_port)])
        action = parser.OFPInstructionActions(ofproto.OFPIT_WRITE_ACTIONS,
                                             [output])
        flow_mod = self.create_flow_mod(datapath, 0, 0, 125, 0, match, [action])
        datapath.send_msg(flow_mod)
        #TCP SRC RTSP Test Traffic .... requires more testing
        # send to EPB but do not orchestrate any events ... dpiActionRTSP is already handling that
        result = self.external_processing('URL', '', 6, 5, 0, 0)
        noDPIAction = parser.OFPActionExperimenter(3735929054,result)
        match = self.create_match(parser,
                                [(ofproto.OXM_OF_ETH_TYPE,ether.ETH_TYPE_IP),
                                 (ofproto.OXM_OF_IPV4_SRC, ip_addr),
                                 (ofproto.OXM_OF_IP_PROTO, 6),
                                 (ofproto.OXM_OF_TCP_SRC, rtsp_port)])
        action = parser.OFPInstructionActions(ofproto.OFPIT_WRITE_ACTIONS,
                                             [output])
        flow_mod = self.create_flow_mod(datapath, 0, 0, 124, 0, match, [action])
        #datapath.send_msg(flow_mod)
        print "Experimental Flow done for TCP port ", rtsp_port


    def create_VLAN_to_port(self, datapath, dpi_egress_port):
#have a flow installed that takes VLAN, pop's it, and forward out of the port with that VLAN id
# for all the applicable ports on the switch create a matching parser for VLAN traffic coming out of the EPB switch
	parser = datapath.ofproto_parser
        ofproto = datapath.ofproto
	for m_vid in range(2,12):
# in_port should not be the same as out_port .. Why would I want something to go into the EPB's classified traffic port
# port 6,7 is being used to send data to EPB ... OpenFlow will report an Error if check is not done
		if(m_vid != dpi_egress_port):
			popVlan = parser.OFPActionPopVlan()
			output = parser.OFPActionOutput(m_vid,0)
			match = self.create_match(parser,
                                                [#(ofproto.OXM_OF_ETH_TYPE,ether.ETH_TYPE_IP),
                                                 (ofproto.OXM_OF_IN_PORT, dpi_egress_port),
                                                 (ofproto.OXM_OF_VLAN_VID, m_vid)])
			action = parser.OFPInstructionActions(ofproto.OFPIT_WRITE_ACTIONS,
                                                             [popVlan,output])
			flow_mod = self.create_flow_mod(datapath, 0, 0, 200, 0, match, [action])
			datapath.send_msg(flow_mod)
			print "Created DPI VLAN parser for port ", m_vid

    def create_flow_mod(self, datapath, idle_timeout, hard_timeout, priority,
                        table_id, match, instructions):
        """Create OFP flow mod message."""
        ofproto = datapath.ofproto
        flow_mod = datapath.ofproto_parser.OFPFlowMod(datapath, 0, 0, table_id,
                                                      ofproto.OFPFC_ADD, idle_timeout,
                                                      hard_timeout, priority,
                                                      ofproto.OFPCML_NO_BUFFER,
                                                      ofproto.OFPP_ANY,
                                                      OFPG_ANY, 0,
                                                      match, instructions)
        return flow_mod

    def install_table_miss(self, datapath, table_id):
        """Create and install table miss flow entries."""
        parser = datapath.ofproto_parser
        ofproto = datapath.ofproto
        empty_match = parser.OFPMatch()
        output = parser.OFPActionOutput(ofproto.OFPP_CONTROLLER,
                                        ofproto.OFPCML_NO_BUFFER)
        write = parser.OFPInstructionActions(ofproto.OFPIT_WRITE_ACTIONS,
                                        [output])
        instructions = [write]
        flow_mod = self.create_flow_mod(datapath, 0, 0, 0, table_id,
                                        empty_match, instructions)
        datapath.send_msg(flow_mod)

    def install_regular_flow(self, datapath, in_port, ipv4_src, ipv4_dst):
        parser = datapath.ofproto_parser
        ofproto = datapath.ofproto
        #install regular IP flow
        match = self.create_match(parser,
                                [(ofproto.OXM_OF_ETH_TYPE,ether.ETH_TYPE_IP),
                                 (ofproto.OXM_OF_IP_PROTO, 6),
                                 (ofproto.OXM_OF_IPV4_SRC, ipv4_src),
                                 (ofproto.OXM_OF_IPV4_DST, ipv4_dst)])
        output = parser.OFPActionOutput(in_port, ofproto.OFPCML_NO_BUFFER)
        action = parser.OFPInstructionActions(ofproto.OFPIT_WRITE_ACTIONS,
                                              [output])
        flow_mod = self.create_flow_mod(datapath, 0, 0, 129, 0, match, [action])
        datapath.send_msg(flow_mod)
        print "Regular Flow done"

    @set_ev_cls(EventOFPPacketIn, MAIN_DISPATCHER)
    def _packet_in_handler(self, ev):
        """Handle packet_in events."""
        msg = ev.msg
        datapath = msg.datapath
        ofproto = datapath.ofproto
        table_id = msg.table_id
        fields = msg.match.fields
        ipv4_src_str = "EMPTY"
        ipv4_dst_str = "EMPTY"
        #Install Experimental
        #if table_id == 0:
            #self.install_experimental_flow(datapath)
        #return
        # Extract fields
        for f in fields:
            if f.header == ofproto.OXM_OF_IN_PORT:
                in_port = f.value
            elif f.header == ofproto.OXM_OF_ETH_SRC:
                eth_src = f.value
            elif f.header == ofproto.OXM_OF_ETH_DST:
                eth_dst = f.value
            elif f.header == ofproto.OXM_OF_ETH_TYPE:
                eth_type = f.value
            elif f.header == ofproto.OXM_OF_IPV4_SRC:
                ipv4_src = f.value
            elif f.header == ofproto.OXM_OF_IPV4_DST:
                ipv4_dst = f.value
                ipv4_dst_str = self.ipv4_to_str(ipv4_dst)
                ipv4_dst_str = ipv4_dst_str.strip()
                print "Found DST IP ADDRESS:", ipv4_dst_str
            elif f.header == ofproto.OXM_OF_IP_PROTO:
                protocol = f.value
                print "Protocol is ", protocol
                if(protocol == 17 and in_port == 1):
                    self.decodeFirewallPacket(datapath, msg.data)
                    self.decodeFirewallPacketInReverse(datapath, msg.data)

	# Learn the source and fill up routing table
	print "Packet In Retrieved ",(eth_src, in_port)

    def handleFirewallPacket(self, datapath, inPort):
        print "Install Firewall Packet rule"
        ofproto = datapath.ofproto
        parser = datapath.ofproto_parser
        match = self.create_match(parser,
                                  [(ofproto.OXM_OF_IN_PORT, inPort),
                                   (ofproto.OXM_OF_ETH_TYPE,ether.ETH_TYPE_IP),
                                   (ofproto.OXM_OF_IP_PROTO, 17)])
        output = parser.OFPActionOutput(ofproto.OFPP_CONTROLLER,
                                        ofproto.OFPCML_NO_BUFFER)
        test = parser.OFPInstructionActions(ofproto.OFPIT_WRITE_ACTIONS,
                                            [output])
        idle_timeout = 0
        hard_timeout = 0
        flow_mod = self.create_flow_mod(datapath, idle_timeout, hard_timeout, 122, 0, match, [test])
        datapath.send_msg(flow_mod)

    def decodeFirewallPacket(self, datapath, data):
        print "Attempting Decode Packet"
        global table
        global epbTable
        parser = datapath.ofproto_parser
        ofproto = datapath.ofproto
        match = parser.OFPMatch()
        fw = firewallParser.decode(data[42:])
        if(fw.addressVersion == 'IPv4'):
            self.append_match(match,[(ofproto.OXM_OF_ETH_TYPE,ether.ETH_TYPE_IP)])
        if(fw.sourceAddress != 'ANY'):
            if(fw.addressVersion == 'IPv4'):
                temp = fw.sourceAddress.split("/")
                # currently ignoring the mask
                src_ip_addr = temp[0]
                src_ip_addr = self.ipv4_to_int(src_ip_addr)
                print "1) Source IP Address ", temp[0]
                print "2) Source IP Address ", src_ip_addr
                self.append_match(match,[(ofproto.OXM_OF_IPV4_SRC, src_ip_addr)])
        if(fw.sourcePort != 'ANY'):
            port = fw.destinationPort
            print "Source Port ", port
            if(fw.addressVersion == 'IPv4'):
                print "Valid IPv4 Source Port ", fw.protocol
                if(fw.protocol == "UDP"):
                    self.append_match(match,[(ofproto.OXM_OF_UDP_SRC, port)])
                if(fw.protocol == "TCP"):
                    self.append_match(match,[(ofproto.OXM_OF_TCP_SRC, port)])
        if(fw.destinationAddress != 'ANY'):
            if(fw.addressVersion == 'IPv4'):
                temp = fw.destinationAddress.split("/")
                # currently ignoring the mask
                dst_ip_addr = temp[0]
                dst_ip_addr = self.ipv4_to_int(dst_ip_addr)
                print "1) Source IP Address ", temp[0]
                print "2) Source IP Address ", dst_ip_addr
                self.append_match(match,[(ofproto.OXM_OF_IPV4_DST, dst_ip_addr)])
        if(fw.destinationPort != 'ANY'):
            port = fw.destinationPort
            print "Destination Port ", port
            if(fw.addressVersion == 'IPv4'):
                print "Valid IPv4 Destination Port ", fw.protocol
                if(fw.protocol == "UDP"):
                    self.append_match(match,[(ofproto.OXM_OF_UDP_DST, port)])
                if(fw.protocol == "TCP"):
                    self.append_match(match,[(ofproto.OXM_OF_TCP_DST, port)])
        if(fw.protocol != "ANY"):
            if(fw.protocol == "UDP"):
                proto = 17
            if(fw.protocol == "TCP"):
                proto = 6
            if(fw.protocol == "ICMP"):
                proto = 1
            self.append_match(match,[(ofproto.OXM_OF_IP_PROTO, proto)])
        # default action is to drop all matching packets
        action = parser.OFPInstructionActions(ofproto.OFPIT_WRITE_ACTIONS,
                                    [])
        actions = []
        if(fw.action == "ACCEPT"):
            if fw.provisioningParameter in table.keys():
                suggestedPort = table[fw.provisioningParameter]
                if((suggestedPort).isdigit()):
                    switchPort = int(suggestedPort)
                    output = parser.OFPActionOutput(switchPort,ofproto.OFPCML_NO_BUFFER)
                    actions.append(output)
                    # if provisioned network was a EPB entry then push EPB configuration message
                    if fw.provisioningParameter in epbTable.keys():
                        suggestedPort = epbTable[fw.provisioningParameter]
                        if((suggestedPort).isdigit()):
                            # send to EPB configuration message
                            switchPort = int(suggestedPort)
                            self.send_packet(datapath, switchPort, data)
                        #    vlanTag = int(suggestedPort)
                        #    pushVlan = parser.OFPActionPushVlan(ether.ETH_TYPE_8021Q)
                        #    actions.append(pushVlan)
                        #    f = parser.OFPMatchField.make(ofproto.OXM_OF_VLAN_VID, vlanTag)
                        #    setVlan = parser.OFPActionSetField(f)
                        #    actions.append(setVlan)
                        # if previous network is present then set the in port
                    if(fw.previousNetwork != "") and fw.previousNetwork in table.keys():
                        suggestedPort = table[fw.previousNetwork]
                        if((suggestedPort).isdigit()):
                            switchPort = int(suggestedPort)
                            # If the previous network is a EPB then traffic should occur in the next mapped interface
                            if fw.previousNetwork in epbTable.keys():
                                switchPort = switchPort + 1
                            self.append_match(match,[(ofproto.OXM_OF_IN_PORT, switchPort)])
                        else:
                            print "Firewall Message does not have a valid inPort:", suggestedPort
                else:
                    print "Firewall Message does not have a valid outputPort:", suggestedPort
            else:
                print "WARNING: Provisioning Parameter ",fw.provisioningParameter," in the config.ini file"
                print "WARNING: Traffic matching this criteria will be dropped!"
        action = parser.OFPInstructionActions(ofproto.OFPIT_WRITE_ACTIONS, actions)
        print "Match ", match
        print "Actions ", actions
        print "Action ", action
        instructions = [action]
        hard_timeout = 900
        if((fw.duration).isdigit()):
            hard_timeout = int(fw.duration)
        flow_mod = self.create_flow_mod(datapath, 0, hard_timeout, 0, 0,
                                        match, instructions)
        datapath.send_msg(flow_mod)

    def decodeFirewallPacketInReverse(self, datapath, data):
        print "Attempting Decode Packet"
        parser = datapath.ofproto_parser
        ofproto = datapath.ofproto
        match = parser.OFPMatch()
        fw = firewallParser.decode(data[42:])
        if(fw.addressVersion == 'IPv4'):
            self.append_match(match,[(ofproto.OXM_OF_ETH_TYPE,ether.ETH_TYPE_IP)])
        if(fw.sourceAddress != 'ANY'):
            if(fw.addressVersion == 'IPv4'):
                temp = fw.sourceAddress.split("/")
                # currently ignoring the mask
                src_ip_addr = temp[0]
                src_ip_addr = self.ipv4_to_int(src_ip_addr)
                print "1) Source IP Address ", temp[0]
                print "2) Source IP Address ", src_ip_addr
                self.append_match(match,[(ofproto.OXM_OF_IPV4_DST, src_ip_addr)])
        if(fw.sourcePort != 'ANY'):
            port = fw.destinationPort
            print "Source Port ", port
            if(fw.addressVersion == 'IPv4'):
                print "Valid IPv4 Source Port ", fw.protocol
                if(fw.protocol == "UDP"):
                    self.append_match(match,[(ofproto.OXM_OF_UDP_DST, port)])
                if(fw.protocol == "TCP"):
                    self.append_match(match,[(ofproto.OXM_OF_TCP_DST, port)])
        if(fw.destinationAddress != 'ANY'):
            if(fw.addressVersion == 'IPv4'):
                temp = fw.destinationAddress.split("/")
                # currently ignoring the mask
                dst_ip_addr = temp[0]
                dst_ip_addr = self.ipv4_to_int(dst_ip_addr)
                print "1) Source IP Address ", temp[0]
                print "2) Source IP Address ", dst_ip_addr
                self.append_match(match,[(ofproto.OXM_OF_IPV4_SRC, dst_ip_addr)])
        if(fw.destinationPort != 'ANY'):
            port = fw.destinationPort
            print "Destination Port ", port
            if(fw.addressVersion == 'IPv4'):
                print "Valid IPv4 Destination Port ", fw.protocol
                if(fw.protocol == "UDP"):
                    self.append_match(match,[(ofproto.OXM_OF_UDP_SRC, port)])
                if(fw.protocol == "TCP"):
                    self.append_match(match,[(ofproto.OXM_OF_TCP_SRC, port)])
        if(fw.protocol != "ANY"):
            if(fw.protocol == "UDP"):
                proto = 17
            if(fw.protocol == "TCP"):
                proto = 6
            if(fw.protocol == "ICMP"):
                proto = 1
            self.append_match(match,[(ofproto.OXM_OF_IP_PROTO, proto)])
        # default action is to drop all matching packets
        action = parser.OFPInstructionActions(ofproto.OFPIT_WRITE_ACTIONS,
                                    [])
        if(fw.action == "ACCEPT"):
            switchPort = 2
            #popVlan = parser.OFPActionPopVlan()
            output = parser.OFPActionOutput(switchPort,
                                    ofproto.OFPCML_NO_BUFFER)
            action = parser.OFPInstructionActions(ofproto.OFPIT_WRITE_ACTIONS,
                                    [output])
        instructions = [action]
        hard_timeout = 900
        if((fw.duration).isdigit()):
            hard_timeout = int(fw.duration)
        flow_mod = self.create_flow_mod(datapath, 0, hard_timeout, 0, 0,
                                        match, instructions)
        datapath.send_msg(flow_mod)

    # This function does not properly create a packet
    def sendEPBControlPacket(datapath, port, data):
        pkt = packet.Packet()
        pkt.add_protocol(ethernet.ethernet(ethertype=ether.ETH_TYPE_IP,
                                           dst='00:00:00:00:00:00',
                                           src='00:00:00:00:00:00'))
        pkt.add_protocol(ipv4.ipv4(dst='0.0.0.0',
                                   src='0.0.0.0',
                                   proto=inet.IPPROTO_UDP))
        pkt.add_protocol(udp.udp(src_port=5000,
                                 dst_port=5000))
        pkt.add_protocol(data)
        print "EPB Control packet generated and sent to port: ", port
        self.send_packet(datapath, port, pkt)

    def send_packet(self, datapath, port, pkt):
        ofproto = datapath.ofproto
        parser = datapath.ofproto_parser
        data = pkt
        actions = [parser.OFPActionOutput(port, ofproto.OFPCML_NO_BUFFER)]
        out = parser.OFPPacketOut(datapath=datapath,
                                  buffer_id=ofproto.OFP_NO_BUFFER,
                                  in_port=ofproto.OFPP_CONTROLLER,
                                  actions=actions,
                                  data=data)
        datapath.send_msg(out)

    def handle_arp_request(self, datapath, ip_addr, dst_port):
        ofproto = datapath.ofproto
        ip_addr = self.ipv4_to_int(ip_addr)
        parser = datapath.ofproto_parser
        match = self.create_match(parser,
                                  [(ofproto.OXM_OF_ETH_TYPE,ether.ETH_TYPE_ARP),
                                   (ofproto.OXM_OF_ARP_OP, 1),
                                   (ofproto.OXM_OF_ARP_TPA, ip_addr)])
        output = parser.OFPActionOutput(dst_port, ofproto.OFPCML_NO_BUFFER)
        test = parser.OFPInstructionActions(ofproto.OFPIT_WRITE_ACTIONS,
                                            [output])
        idle_timeout = 0
        hard_timeout = 0
        flow_mod = self.create_flow_mod(datapath, idle_timeout, hard_timeout, 122, 0, match, [test])
        datapath.send_msg(flow_mod)

    def install_l2_match(self, ofproto, datapath, dst_port, frame_src, frame_dst):
        #Install flow entry matching on eth_src in table 0.
        print "Dst Port =", dst_port
        parser = datapath.ofproto_parser
        match = self.create_match(parser,
                                  [(ofproto.OXM_OF_ETH_SRC, frame_src),
                                   (ofproto.OXM_OF_ETH_DST, frame_dst)])
        output = parser.OFPActionOutput(dst_port, ofproto.OFPCML_NO_BUFFER)
        test = parser.OFPInstructionActions(ofproto.OFPIT_WRITE_ACTIONS,
                                            [output])
        idle_timeout = 0
        hard_timeout = 0
        flow_mod = self.create_flow_mod(datapath, idle_timeout, hard_timeout, 123, 0, match, [test])
        datapath.send_msg(flow_mod)

    def flood(self, datapath, in_port, data):
        """Send a packet_out with output to all ports."""
        parser = datapath.ofproto_parser
        ofproto = datapath.ofproto
        output_all = parser.OFPActionOutput(ofproto.OFPP_ALL,
                                            ofproto.OFPCML_NO_BUFFER)
        packet_out = parser.OFPPacketOut(datapath, 0xffffffff,
                                         in_port,
                                         [output_all], data)
        datapath.send_msg(packet_out)

    def ipv4_to_str(self, integre):
        ip_list = [str((integre >> (24 - (n * 8)) & 255)) for n in range(4)]
        return '.'.join(ip_list)

    def ipv4_to_int(self, string):
        ip = string.split('.')
        assert len(ip) == 4
        i = 0
        for b in ip:
            b = int(b)
            i = (i << 8) | b
        return i

    '''Creates a external processing payload to send to the data path'''
    def external_processing(self, field_type, url, control_port, data_port, lib_id, lib_options):
        if (field_type == 'URL'):
            epb_search_field_type = [0,0,1]
        if (field_type == 'Delay'):
            epb_search_field_type = [0,0,2]
        if (field_type == 'Jitter'):
            epb_search_field_type = [0,0,3]
        if (field_type == 'Loss'):
            epb_search_field_type = [0,0,4]
        epb_search_field_type = struct.pack("!3B", *epb_search_field_type)
        expected_size = 40
	epb_search_field_len = struct.pack("!B", expected_size)
        size = len(url)
        pad_len = expected_size - size
        epb_search_value = struct.pack("!"+("B"*size), *map(ord,url))
        # pad the remaining length with x00
        epb_search_value += struct.pack("!"+("B"*pad_len), *(pad_len*[0]))
        control_port = struct.pack("!B", control_port)
        data_port = struct.pack("!B", data_port)
        lib_id = struct.pack("!B", lib_id)
        lib_options = '{0:08b}'.format(lib_options)
        # Library Options is a 9 byte attribute
        len_ = utils.round_up(len(lib_options),72)
        pad_len = len_ - len(lib_options)
        lib_options_binary = str(lib_options)+("0"*pad_len)
        first_byte = int(lib_options_binary[:8], 2)
        next_four_bytes = int(lib_options_binary[8:40], 2)
        final_four_bytes = int(lib_options_binary[40:], 2)
        lib_options = struct.pack("!BII", first_byte, next_four_bytes, final_four_bytes)
        result = control_port+data_port+lib_id+lib_options+epb_search_field_type+epb_search_field_len+epb_search_value
        return result

    ''' Creates the Library Options value '''
    def library_options(self, polling, vodType, port, fromClientVipPort, fromClientRegularPort, toClientVIPPort, toClientRegularPort):
        if polling == 1:
            polling = 128
        port = hex(port)
        arr = [polling+vodType, int(port[2:4], 16), int(port[4:], 16), fromClientVipPort, fromClientRegularPort, toClientVIPPort,toClientRegularPort,0,0]
        result = ''.join(format(x,'02x') for x in arr)
        result = int(result,16)
        return result
