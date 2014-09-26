package com.wigroup.wiAppService.responsehandlers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class WiAppAddItemToShoppingListResponse extends WiAppResponseHandler {

    private Vector linkedCampaignList = new Vector();

    public Vector getLinkedCampaignList() {
	return linkedCampaignList;
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
	super.startElement(uri, localName, qName, attributes);
    }

    public void characters(char ch[], int start, int length) throws SAXException {
	super.characters(ch, start, length);

	if (tagName.equalsIgnoreCase("id")) {
	    linkedCampaignList.addElement(new String(ch, start, length));
	}
    }
}
