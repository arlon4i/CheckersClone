package fi.bb.checkers.sharepointService.responsehandlers;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class SharepointLoginResponseHandler extends SharepointResponseHandler
{
	private String cookiename = "";

	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
	{
		super.startElement(uri, localName, qName, attributes);
	}
	
	public void characters(char ch[], int start, int length) throws SAXException
	{
		if (tagName.equalsIgnoreCase("CookieName"))
		{
			setCookiename(new String(ch, start, length));
		}
		else if (tagName.equalsIgnoreCase("ErrorCode"))
		{
			setResponseDesc(new String(ch, start, length));
		}
	}

	public String getCookiename()
	{
		return cookiename;
	}

	public void setCookiename(String cookiename)
	{
		this.cookiename = cookiename;
	}
}
