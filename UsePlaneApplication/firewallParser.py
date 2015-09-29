#!/usr/bin/python
# Based on http://www.tutorialspoint.com/python/python_xml_processing.htm
import xml.sax
from StringIO import StringIO

class FirewallMessageHandler( xml.sax.ContentHandler ):
   def __init__(self):
      self.CurrentData = ""
      self.action = ""
      self.addressVersion = ""
      self.protocol = ""
      self.sourceAddress = ""
      self.destinationAddress = ""
      self.sourcePort = ""
      self.destinationPort = ""
      self.provisioningParameter = ""
      self.previousNetwork = ""
      self.serviceType = ""
      self.serviceName = ""
      self.subject = ""
      self.duration = ""

   # Call when an element starts
   def startElement(self, tag, attributes):
      self.CurrentData = tag
      if tag == "openFlowFirewallMessage":
         print "*****OpenFlowFirewallMessage*****"
         operation = attributes["operation"]
         print "Operation:", operation

   # Call when an elements ends
   def endElement(self, tag):
      if self.CurrentData == "action":
         print "action:", self.action
      elif self.CurrentData == "serviceType":
         print "serviceType:", self.serviceType
      elif self.CurrentData == "serviceName":
         print "serviceName:", self.serviceName
      elif self.CurrentData == "subject":
         print "subject:", self.subject
      elif self.CurrentData == "addressVersion":
         print "addressVersion:", self.addressVersion
      elif self.CurrentData == "protocol":
         print "protocol:", self.protocol
      elif self.CurrentData == "sourceAddress":
         print "sourceAddress:", self.sourceAddress
      elif self.CurrentData == "destinationAddress":
         print "destinationAddress:", self.destinationAddress
      elif self.CurrentData == "sourcePort":
         print "sourcePort:", self.sourcePort
      elif self.CurrentData == "destinationPort":
         print "destinationPort:", self.destinationPort
      elif self.CurrentData == "provisioningParameter":
         print "provisioningParameter:", self.provisioningParameter
      elif self.CurrentData == "previousNetwork":
         print "previousNetwork:", self.previousNetwork
      elif self.CurrentData == "duration":
         print "duration:", self.duration
      self.CurrentData = ""

   # Call when a character is read
   def characters(self, content):
      if self.CurrentData == "action":
         self.action = content
      elif self.CurrentData == "serviceType":
         self.serviceType = content
      elif self.CurrentData == "serviceName":
         self.serviceName = content
      elif self.CurrentData == "subject":
         self.subject = content
      elif self.CurrentData == "addressVersion":
         self.addressVersion = content
      elif self.CurrentData == "protocol":
         self.protocol = content
      elif self.CurrentData == "sourceAddress":
         self.sourceAddress = content
      elif self.CurrentData == "destinationAddress":
         self.destinationAddress = content
      elif self.CurrentData == "sourcePort":
         self.sourcePort = content
      elif self.CurrentData == "destinationPort":
         self.destinationPort = content
      elif self.CurrentData == "provisioningParameter":
         self.provisioningParameter = content
      elif self.CurrentData == "previousNetwork":
         self.previousNetwork = content
      elif self.CurrentData == "duration":
         self.duration = content

def decode(xmlString):

   # create an XMLReader
   parser = xml.sax.make_parser()
   # turn off namepsaces
   parser.setFeature(xml.sax.handler.feature_namespaces, 0)

   # override the default ContextHandler
   Handler = FirewallMessageHandler()
   parser.setContentHandler( Handler )

   parser.parse(StringIO(xmlString))

   return Handler
