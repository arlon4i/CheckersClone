package com.wigroup.wiAppService.responsehandlers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import fi.bb.checkers.datatypes.MerchantData;

public class WiAppMerchantListResponseHandler extends WiAppResponseHandler
{

	private MerchantData merchant;
	private Vector merchantList = new Vector();

	public Vector getMerchantList()
	{
		return merchantList;
	}

	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
	{
		super.startElement(uri, localName, qName, attributes);

		if (qName.equalsIgnoreCase("merchant"))
		{
			merchant = new MerchantData();
			merchantList.addElement(merchant);
		}
	}

	public void characters(char ch[], int start, int length) throws SAXException
	{
		super.characters(ch, start, length);

		if (tagName.equalsIgnoreCase("id"))
		{
			merchant.setId(new String(ch, start, length));
		}
		else if (tagName.equalsIgnoreCase("name"))
		{
			merchant.setName(new String(ch, start, length));
			String brand = "";
			if (merchant.getName().toLowerCase().indexOf("checkers") != -1)
				brand = "checkers";
			else if (merchant.getName().toLowerCase().indexOf("shoprite") != -1) brand = "shoprite";
			merchant.setBrand(brand);
		}
		else if (tagName.equalsIgnoreCase("distance"))
		{
			merchant.setDistance(Double.parseDouble(new String(ch, start, length)));
		}
	}
}
