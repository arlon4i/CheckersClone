package com.wigroup.wiAppService.responsehandlers;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class WiAppResponseHandler extends DefaultHandler
{

	protected String tagName;

	private String responseCode;
	private String responseDesc;
	private String responseMessage;

	public String getResponseCode()
	{
		return responseCode;
	}

	public void setResponseCode(String responseCode)
	{
		this.responseCode = responseCode;
	}

	public String getResponseDesc()
	{
		return responseDesc;
	}

	public void setResponseDesc(String responseDesc)
	{
		this.responseDesc = responseDesc;
	}

	public String getResponseMessage()
	{
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage)
	{
		this.responseMessage = responseMessage;
	}

	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
	{
		tagName = qName;
	}

	public void characters(char ch[], int start, int length) throws SAXException
	{
		if (tagName.equalsIgnoreCase("responsecode"))
		{
			responseCode = new String(ch, start, length);
		}
		else if (tagName.equalsIgnoreCase("responsedesc"))
		{
			responseDesc = new String(ch, start, length);
		}
		else if (tagName.equalsIgnoreCase("message"))
		{
			responseMessage = new String(ch, start, length);
		}
	}
}
