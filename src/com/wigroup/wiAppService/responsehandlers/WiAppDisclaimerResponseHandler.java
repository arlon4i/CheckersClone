package com.wigroup.wiAppService.responsehandlers;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class WiAppDisclaimerResponseHandler extends WiAppResponseHandler {

    private String disclaimer;

    public String getDisclaimer() {
	return disclaimer;
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
	super.startElement(uri, localName, qName, attributes);
    }

    public void characters(char ch[], int start, int length) throws SAXException {
	super.characters(ch, start, length);

	if (tagName.equalsIgnoreCase("disclaimer")) {
	    disclaimer = new String(ch, start, length);
	}
    }
}
