package fi.bb.checkers.sharepointService.responsehandlers;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SharepointResponseHandler extends DefaultHandler
{
	protected String tagName;
	private String responseDesc;

	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
	{
		tagName = qName;
	}

	public String getResponseDesc()
	{
		return responseDesc;
	}

	public void setResponseDesc(String responseDesc)
	{
		this.responseDesc = responseDesc;
	}
}
