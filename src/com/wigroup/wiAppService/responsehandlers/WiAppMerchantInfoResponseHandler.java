package com.wigroup.wiAppService.responsehandlers;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import fi.bb.checkers.datatypes.MerchantData;

public class WiAppMerchantInfoResponseHandler extends WiAppResponseHandler
{

	private boolean isPhysicalAddress = false;
	private MerchantData merchant = new MerchantData();

	public MerchantData getMerchant()
	{
		return merchant;
	}

	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
	{
		super.startElement(uri, localName, qName, attributes);

		if (qName.equalsIgnoreCase("physicaladdress"))
		{
			isPhysicalAddress = true;
		}
	}

	public void endElement(String uri, String localName, String qName) throws SAXException
	{
		if (qName.equalsIgnoreCase("physicaladdress"))
		{
			isPhysicalAddress = false;
		}
	}

	public void characters(char ch[], int start, int length) throws SAXException
	{
		super.characters(ch, start, length);

		if (tagName.equalsIgnoreCase("name"))
		{
			merchant.setName(new String(ch, start, length));
		}
		else if (tagName.equalsIgnoreCase("address"))
		{
			if (isPhysicalAddress)
			{
				merchant.setPhysicalAddress(new String(ch, start, length));
			}
		}
		else if (tagName.equalsIgnoreCase("contactperson"))
		{
			merchant.setContactDetails(new String(ch, start, length));
		}
		else if (tagName.equalsIgnoreCase("tradinghours"))
		{
			merchant.setTradingHours(new String(ch, start, length));
		}
		// else if (tagName.equalsIgnoreCase("facilities")) {
		// merchant.setFacilities(new String(ch, start, length));
		// }
		// else if (tagName.equalsIgnoreCase("departments")) {
		// merchant.setDepartments(new String(ch, start, length));
		// }
	}
}
