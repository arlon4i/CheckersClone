package com.wigroup.wiAppService.responsehandlers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import fi.bb.checkers.datatypes.LocationData;

public class WiAppLocationResponseHandler extends WiAppResponseHandler
{

	private LocationData location;
	private Vector locationList = new Vector();

	public Vector getLocationList()
	{
		return sort(locationList);
	}

	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
	{
		super.startElement(uri, localName, qName, attributes);

		if (qName.equalsIgnoreCase("province") || qName.equalsIgnoreCase("region") || qName.equalsIgnoreCase("suburb"))
		{
			location = new LocationData();
			locationList.addElement(location);
		}
	}

	public void characters(char ch[], int start, int length) throws SAXException
	{
		super.characters(ch, start, length);

		if (tagName.equalsIgnoreCase("id"))
		{
			location.setId(new String(ch, start, length));
		}
		else if (tagName.equalsIgnoreCase("desc"))
		{
			location.setDesc(new String(ch, start, length));
		}
	}

	/**
	 * This ensures that location objects are sorted alphabetically on description
	 * 
	 * @param sort
	 * @return
	 */
	public Vector sort(Vector sort)
	{
		// do nothing if no collection
		if (sort == null || sort.isEmpty())
		{
			return sort;
		}
		Vector v = new Vector();
		for (int count = 0; count < sort.size(); count++)
		{
			LocationData s = (LocationData) sort.elementAt(count);
			int i = 0;
			for (i = 0; i < v.size(); i++)
			{
				int c = s.compareTo(v.elementAt(i));
				if (c < 0)
				{
					v.insertElementAt(s, i);
					break;
				}
				else if (c == 0)
				{
					break;
				}
			}
			if (i >= v.size())
			{
				v.addElement(s);
			}
		}
		return v;
	}
}
