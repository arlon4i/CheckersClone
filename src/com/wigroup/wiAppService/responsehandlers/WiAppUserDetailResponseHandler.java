package com.wigroup.wiAppService.responsehandlers;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class WiAppUserDetailResponseHandler extends WiAppResponseHandler
{

	private String titleId;
	private String firstname;
	private String surname;
	private String mobile;
	private String email;
	private String dateOfBirth;
	private String preferredStore;
	private String cvprovinceid;

	public String getTitleId()
	{
		return titleId;
	}
	
	public String getFirstname()
	{
		return firstname;
	}

	public String getSurname()
	{
		return surname;
	}

	public String getMobile()
	{
		return mobile;
	}

	public String getEmail()
	{
		return email;
	}

	public String getDateOfBirth()
	{
		return dateOfBirth;
	}

	public String getCvprovinceid()
	{
		return cvprovinceid;
	}

	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
	{
		super.startElement(uri, localName, qName, attributes);
	}

	public void characters(char ch[], int start, int length) throws SAXException
	{
		super.characters(ch, start, length);

		if (tagName.equalsIgnoreCase("title"))
		{
			titleId = new String(ch, start, length);
		}
		else if (tagName.equalsIgnoreCase("name"))
		{
			firstname = new String(ch, start, length);
		}
		else if (tagName.equalsIgnoreCase("surname"))
		{
			surname = new String(ch, start, length);
		}
		else if (tagName.equalsIgnoreCase("mobilenumber"))
		{
			mobile = new String(ch, start, length);
		}
		else if (tagName.equalsIgnoreCase("email"))
		{
			email = new String(ch, start, length);
		}
		else if (tagName.equalsIgnoreCase("dateofbirth"))
		{
			dateOfBirth = new String(ch, start, length);
		}
		else if (tagName.equalsIgnoreCase("favmerchantid"))
		{
			preferredStore = new String(ch, start, length);
		}
		else if (tagName.equalsIgnoreCase("cvprovinceid"))
		{
			cvprovinceid = new String(ch, start, length);
		}
	}

	public String getPreferredStore()
	{
		return preferredStore;
	}

	public void setPreferredStore(String preferredStore)
	{
		this.preferredStore = preferredStore;
	}
}
