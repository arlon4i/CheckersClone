package com.wigroup.wiAppService.responsehandlers;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class WiappGetTotalsavingsResponseHandler  extends WiAppResponseHandler  {

	private String amountInCents;
	
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
	}
	
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		super.endElement(uri, localName, qName);
	}

	public void characters(char[] ch, int start, int length)
			throws SAXException {
		super.characters(ch, start, length);

		if (tagName.equalsIgnoreCase("amountincents")) {
			amountInCents =new String(ch, start, length);
		} 
	}

	public String getAmountInCents() {
		return amountInCents;
	}

	public void setAmountInCents(String amountInCents) {
		this.amountInCents = amountInCents;
	}
	
	
	
}
