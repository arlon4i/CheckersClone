package com.wigroup.wiAppService.responsehandlers;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class WiAppLoginResponseHandler extends WiAppResponseHandler
{

	private String sessionId;
	private String version;
	private String updateMessage;
	private String updateUrl;

	public String getSessionId()
	{
		return sessionId;
	}

	public String getVersion()
	{
		return version;
	}

	public String getUpdateMessage()
	{
		return updateMessage;
	}

	public String getUpdateUrl()
	{
		return updateUrl;
	}

	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
	{
		super.startElement(uri, localName, qName, attributes);
	}

	public void characters(char ch[], int start, int length) throws SAXException
	{
		super.characters(ch, start, length);

		if (tagName.equalsIgnoreCase("sessionid"))
		{
			sessionId = new String(ch, start, length);
		}
		else if (tagName.equalsIgnoreCase("version"))
		{
			version = new String(ch, start, length);
		}
		else if (tagName.equalsIgnoreCase("updatemsg"))
		{
			updateMessage = new String(ch, start, length);
		}
		else if (tagName.equalsIgnoreCase("updateurl"))
		{
			updateUrl = new String(ch, start, length);
		}
	}
}
