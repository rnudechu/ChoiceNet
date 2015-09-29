#Packet sniffer in python
#For Linux
# Original: http://www.binarytides.com/python-packet-sniffer-code-linux/
# Original: https://github.com/koehlma/snippets/blob/master/python/network/sniffer.py
# http://ilab.cs.byu.edu/python/select/echoserver.html
# Modified to read ALL Data from a single interface

import sys
import socket
from struct import *
import thread
import select
import time
import math
import firewallParser

logger = ""
firewall_array = []

def listenForManagementTraffic(listeningSocket):
    # receive a packet
    packet = listeningSocket.recvfrom(65565)
    #packet string from tuple
    packet = packet[0]
    print '======================================================'
    print 'Management Traffic'
    pkt = parse_packet(packet)

def listenForTraffic(listeningSocket, transmittingSocket):
    # receive a packet
    packet = listeningSocket.recvfrom(65565)
    #packet string from tuple
    packet = packet[0]
    print '======================================================'
    print 'General Traffic'
    pkt = modify_payload(packet, transmittingSocket)

#Convert a string of 6 characters of ethernet address into a dash separated hex string
def eth_addr (a):
  b = "%.2x:%.2x:%.2x:%.2x:%.2x:%.2x" % (ord(a[0]) , ord(a[1]) , ord(a[2]), ord(a[3]), ord(a[4]) , ord(a[5]))
  return b

def parse_packet(packet):
    global firewall_array
    pktData = packet
    #parse ethernet header
    eth_length = 14

    eth_header = packet[:eth_length]
    eth = unpack('!6s6sH' , eth_header)
    eth_protocol = socket.ntohs(eth[2])
    print 'Destination MAC : ' + eth_addr(packet[0:6]) + ' Source MAC : ' + eth_addr(packet[6:12]) + ' Protocol : ' + str(eth_protocol)

    #Parse IP packets, IP Protocol number = 8
    if eth_protocol == 8 :
        #Parse IP header
        #take first 20 characters for the ip header
        ip_header = packet[eth_length:20+eth_length]

        #now unpack them :)
        iph = unpack('!BBHHHBBH4s4s' , ip_header)

        version_ihl = iph[0]
        version = version_ihl >> 4
        ihl = version_ihl & 0xF

        iph_length = ihl * 4

        ttl = iph[5]
        protocol = iph[6]
        s_addr = socket.inet_ntoa(iph[8]);
        d_addr = socket.inet_ntoa(iph[9]);

        print 'Version : ' + str(version) + ' IP Header Length : ' + str(ihl) + ' TTL : ' + str(ttl) + ' Protocol : ' + str(protocol) + ' Source Address : ' + str(s_addr) + ' Destination Address : ' + str(d_addr)

        #UDP packets
        if protocol == 17 :
            u = iph_length + eth_length
            udph_length = 8
            udp_header = packet[u:u+8]

            #now unpack them :)
            udph = unpack('!HHHH' , udp_header)

            source_port = udph[0]
            dest_port = udph[1]
            length = udph[2]
            checksum = udph[3]

            print 'Source Port : ' + str(source_port) + ' Dest Port : ' + str(dest_port) + ' Length : ' + str(length) + ' Checksum : ' + str(checksum)

            h_size = eth_length + iph_length + udph_length
            data_size = len(packet) - h_size

            #get data from the packet
            data = packet[h_size:]

            print 'Data : ' + data
            # Only attempt to parse the data as OpenFlow when the following criteria is met
            # UDP payload with an IP address of 0.0.0.100
            if (len(data) > 36) and ((s_addr) == '10.0.1.1') and ((d_addr) == '10.0.1.100'):
                print "Contains Management payload"
                fw = firewallParser.decode(data)
                currentTime = math.floor(time.time())
                print "Duration was initially ", fw.duration
                duration = int(fw.duration)
                try:
                   thread.start_new_thread( updateRules, ( duration+3, ) )
                except:
                   print "Error: unable to start thread"
                newDuration = currentTime + duration
                fw.duration = str(newDuration)
                if("/" in fw.sourceAddress):
                    temp = fw.sourceAddress.split("/")
                    fw.sourceAddress = temp[0]
                if("/" in fw.destinationAddress):
                    temp = fw.destinationAddress.split("/")
                    fw.destinationAddress = temp[0]
                print "Duration is now ", fw.duration
                print "Inserting a new rule"
                firewall_array.append(fw)

        #some other IP packet like IGMP
        else :
            print 'Protocol other than UDP'

        print
        #return pktData

def modify_payload(packet, outgoingSocket):
    global logger
    #parse ethernet header
    eth_length = 14
    eth_header = packet[:eth_length]
    eth = unpack('!6s6sH' , eth_header)
    eth_protocol = socket.ntohs(eth[2])
    #Parse IP packets, IP Protocol number = 8
    if eth_protocol == 8 :
        #Parse IP header
        #take first 20 characters for the ip header
        ip_header = packet[eth_length:20+eth_length]
        #now unpack them :)
        iph = unpack('!BBHHHBBH4s4s' , ip_header)
        version_ihl = iph[0]
        version = version_ihl >> 4
        ihl = version_ihl & 0xF
        iph_length = ihl * 4
        ttl = iph[5]
        protocol = iph[6]
        s_addr = socket.inet_ntoa(iph[8]);
        d_addr = socket.inet_ntoa(iph[9]);
        #TCP protocol
        if protocol == 6 :
            protocolName = "TCP"
            t = iph_length + eth_length
            tcp_header = packet[t:t+20]
            #now unpack them :)
            tcph = unpack('!HHLLBBHHH' , tcp_header)
            source_port = tcph[0]
            dest_port = tcph[1]
            sequence = tcph[2]
            acknowledgement = tcph[3]
            doff_reserved = tcph[4]
            tcph_length = doff_reserved >> 4
            print 'Source Port : ' + str(source_port) + ' Dest Port : ' + str(dest_port) + ' Sequence Number : ' + str(sequence) + ' Acknowledgement : ' + str(acknowledgement) + ' TCP header length : ' + str(tcph_length)
            h_size = eth_length + iph_length + tcph_length * 4
            data_size = len(packet) - h_size
            #get data from the packet
            data = packet[h_size:]
            print 'Data : ' + data
        #UDP packets
        elif protocol == 17 :
            protocolName = "UDP"
            u = iph_length + eth_length
            udph_length = 8
            udp_header = packet[u:u+8]
            #now unpack them :)
            udph = unpack('!HHHH' , udp_header)
            source_port = udph[0]
            dest_port = udph[1]
            length = udph[2]
            checksum = udph[3]
            print 'Source Port : ' + str(source_port) + ' Dest Port : ' + str(dest_port) + ' Length : ' + str(length) + ' Checksum : ' + str(checksum)
            h_size = eth_length + iph_length + udph_length
            data_size = len(packet) - h_size
            #get data from the packet
            data = packet[h_size:]

            print 'Data : ' + data

        match = trafficTreatmentBasedOnSubstring("substring:", source_port, dest_port,s_addr,d_addr)
        if match != "":
            if match in data:
                dataStr = data[::-1]
                data = dataStr[:len(data)]
        match = trafficTreatment("md5", source_port, dest_port,s_addr,d_addr)
        if match:
            import md5
            m = md5.new()
            m.update(data)
            dataStr = m.digest()
            data = dataStr[:len(data)]
        match = trafficTreatment("Plaintext:Uppercase", source_port, dest_port,s_addr,d_addr)
        if match:
            dataStr = data.upper()
            data = dataStr[:len(data)]
        match = trafficTreatment("Plaintext:Lowercase", source_port, dest_port,s_addr,d_addr)
        if match:
            dataStr = data.lower()
            data = dataStr[:len(data)]
        match = trafficTreatment("packet size:file", source_port, dest_port,s_addr,d_addr)
        if match:
            import datetime
            ts = time.time()
            st = datetime.datetime.fromtimestamp(ts).strftime('%Y-%m-%d %H:%M:%S')
            print st , ": ", protocolName, " packet with ", data_size, " bit payload"
            logger = st + ", " + protocolName + ", " + str(data_size) + str(data) + "\n"
            print "Size of logger parameter ", len(logger)
            # Open a file
            fo = open("foo.txt", "ab")
            fo.write(logger);
            # Close opend file
            fo.close()

        print 'Modified Data : ' + data
        pktData = packet[:h_size] + data
        raw(outgoingSocket, pktData)

# http://www.binarytides.com/python-packet-sniffer-code-linux/
# http://stackoverflow.com/questions/12229155/how-do-i-send-an-raw-ethernet-frame-in-python
def raw(s, pkt):
    if pkt != None:
        s.send(pkt)

def trafficTreatment(treatmentType, srcPort, dstPort,srcIPAddr,dstIPAddr):
	global firewall_array
	#vod_print_out()
    #x.value == content and
	match = any(x.subject == treatmentType and
               (x.sourceAddress == srcIPAddr  or x.sourceAddress =="ANY" ) and
               (x.destinationAddress == dstIPAddr or x.destinationAddress =="ANY" ) and
               (x.sourcePort == srcPort  or x.sourcePort =="ANY" ) and
               ( x.destinationPort == dstPort  or x.destinationPort =="ANY" )
               for x in firewall_array )
	# If the flows URI matches then send a multicast equivalent traffic, then forward it out the desired port
	if match:
		return True
	return False

def check(treatmentType, srcPort, dstPort,srcIPAddr,dstIPAddr, item):
    if treatmentType in item.subject and (item.sourceAddress == srcIPAddr  or item.sourceAddress =="ANY" ) and (item.destinationAddress == dstIPAddr or item.destinationAddress =="ANY" ) and (item.sourcePort == srcPort  or item.sourcePort =="ANY" ) and ( item.destinationPort == dstPort  or item.destinationPort =="ANY" ):
        return True
    else:
        return False

def trafficTreatmentBasedOnSubstring(treatmentType, srcPort, dstPort,srcIPAddr,dstIPAddr):
    global firewall_array
    match = any(treatmentType in x.subject and
               (x.sourceAddress == srcIPAddr  or x.sourceAddress =="ANY" ) and
               (x.destinationAddress == dstIPAddr or x.destinationAddress =="ANY" ) and
               (x.sourcePort == srcPort  or x.sourcePort =="ANY" ) and
               ( x.destinationPort == dstPort  or x.destinationPort =="ANY" )
               for x in firewall_array )
	# If the flows URI matches then send a multicast equivalent traffic, then forward it out the desired port
    if match:
        for item in firewall_array:
            match = check(treatmentType, srcPort, dstPort,srcIPAddr,dstIPAddr, item)
            if match:
                temp = item.subject
                temp = temp.split(treatmentType)
                content = temp[1]
                return content
    return ""

def updateRules(delay):
    global firewall_array
    time.sleep(delay)
    currentTime = math.floor(time.time())
    # check if any rule has timed out
    for item in firewall_array:
        if item.duration < currentTime:
            print "Removing Firewall Rule ", item.serviceName," for ", item.sourceAddress, " to ",  item.destinationAddress
            firewall_array.remove(item)

if(len(sys.argv) != 4):
    # Default port directions
    print "python application-payload-service.py <listening_interface> <transmitting_interface> <management_interface>"
    print "Exiting, you failed supply the correct arguments"
    exit()
else:
    intf = str(sys.argv[1])
    intf2 = str(sys.argv[2])
    intf3 = str(sys.argv[3])

#create an INET, raw socket
try:
	receivingIntf = socket.socket(socket.AF_PACKET, socket.SOCK_RAW, socket.ntohs(3))
	receivingIntf.bind((intf, 0))
	transmittingIntf = socket.socket(socket.AF_PACKET, socket.SOCK_RAW, socket.ntohs(3))
	transmittingIntf.bind((intf2, 0))
	managementIntf = socket.socket(socket.AF_PACKET, socket.SOCK_RAW, socket.ntohs(3))
	managementIntf.bind((intf3, 0))
except socket.error , msg:
	print 'Socket could not be created. Error Code : ' + str(msg[0]) + ' Message ' + msg[1]
	sys.exit()

# Listen for traffic

input = [managementIntf,receivingIntf]
#output = [transmittingIntf]
while True:
    inputready,outputready,exceptready = select.select(input,[],[])
    for s in inputready:
        if s == managementIntf:
            # handle Managment socket
            listenForManagementTraffic(managementIntf)
        elif s == receivingIntf:
            # handle general traffic socket
            listenForTraffic(receivingIntf, transmittingIntf)
