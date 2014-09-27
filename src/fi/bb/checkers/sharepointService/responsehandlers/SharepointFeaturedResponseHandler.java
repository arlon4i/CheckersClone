package fi.bb.checkers.sharepointService.responsehandlers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import fi.bb.checkers.datatypes.FeaturedData;
import fi.bb.checkers.helpers.StringHelper;

public class SharepointFeaturedResponseHandler extends SharepointResponseHandler
{
	private String url;
	private FeaturedData item;
	private Vector list = new Vector();

	public Vector getList()
	{
		return list;
	}

	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
	{
		super.startElement(uri, localName, qName, attributes);
		if (qName.equalsIgnoreCase("m:properties"))
		{
			item = new FeaturedData();
			item.setImageURL(url);
		}
		else if (qName.equalsIgnoreCase("content"))
		{
			url = attributes.getValue("src");
		}
	}

	public void characters(char ch[], int start, int length) throws SAXException
	{
		if (tagName.equalsIgnoreCase("d:Order"))
		{
			item.setOrder(Integer.parseInt(new String(ch, start, length)));
		}
		else if (tagName.equalsIgnoreCase("d:Action"))
		{
			item.setAction(new String(ch, start, length));
		}
		else if (tagName.equalsIgnoreCase("d:ActionDetail"))
		{
			item.setActionDetail(new String(ch, start, length));
		}
		else if (tagName.equalsIgnoreCase("d:LiveDate"))
		{
			item.setLiveDate(new String(ch, start, length));
		}
		else if (tagName.equalsIgnoreCase("d:BrandValue"))
		{
			if (new String(ch, start, length).equalsIgnoreCase(StringHelper.sharepoint_app_identifier))
			{
				list.addElement(item);
			}
		}
	}
}
