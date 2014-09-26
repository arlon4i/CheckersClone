package com.wigroup.wiAppService.responsehandlers;

import java.util.Vector;

import org.xml.sax.SAXException;

public class WiAppFeedbackTypesResponseHandler extends WiAppResponseHandler
{
	private Vector types = new Vector();

	public Vector getTypes()
	{
		return types;
	}

	public void characters(char ch[], int start, int length) throws SAXException
	{
		super.characters(ch, start, length);

		if (tagName.equalsIgnoreCase("value"))
		{
			String type = new String(ch, start, length);
			types.addElement(type);
		}
	}
}
