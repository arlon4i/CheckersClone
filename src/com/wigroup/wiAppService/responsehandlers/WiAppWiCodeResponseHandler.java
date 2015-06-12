package com.wigroup.wiAppService.responsehandlers;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import fi.bb.checkers.logger.RemoteLogger;

public class WiAppWiCodeResponseHandler extends WiAppResponseHandler {

    private String wiCode;
    private String redeemDate;

    public String getWiCode() {
	return wiCode;
    }

    public String getRedeemDate() {
	return redeemDate;
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
	super.startElement(uri, localName, qName, attributes);

	tagName = qName;
    }

    public void characters(char ch[], int start, int length) throws SAXException {
	super.characters(ch, start, length);

	if (tagName.equalsIgnoreCase("wicode")) {
	    wiCode = new String(ch, start, length);
	} else if (tagName.equalsIgnoreCase("redeemtodate")) {
	    redeemDate = new String(ch, start, length);
	}
    }
}
