package fi.bb.checkers.datatypes;

import java.util.Vector;

import fi.bb.checkers.utils.StringUtil;

import net.rim.device.api.util.Persistable;

public class CampaignData implements Persistable
{
	private String id;
	private String name;
	private String value;
	private String description;
	private String terms;
	private String expireDate;
	private String wiCode;
	private String imageURL;
	private Vector CategoryList = new Vector();
	private boolean checked = false;
	private boolean strikethrough = false;

	private boolean nameIsCapitalLized = false;
	
	public String getName()
	{
		if (nameIsCapitalLized == false)
		{
			name = getNameCapitalized(name);
			nameIsCapitalLized = true;
			return name;
		}
		else
		{
			return name;
		}
	}

	public String getNameCapitalized(String nameServer)
	{
		String nameNew = "";

		try
		{
			String[] nameArray = StringUtil.split(nameServer, " ");

			String tempString;
			String start;
			String end;

			for (int i=0; i < nameArray.length; i++)
			{
				tempString = nameArray[i].toLowerCase();
				start = tempString.substring(0, 1).toUpperCase();
				end = tempString.substring(1);
				
				if(i==0)
				{
					nameNew += (start+end);	
				}
				else
				{
					nameNew += " " + (start+end);
				}
				
			}

			return nameNew;

		}
		catch (Exception e)
		{
			return nameServer;
		}
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String string)
	{
		this.id = string;
	}

	public String getTerms()
	{
		return terms;
	}

	public void setTerms(String terms)
	{
		this.terms = terms;
	}

	public String getExpireDate()
	{
		return expireDate;
	}

	public void setExpireDate(String expireDate)
	{
		this.expireDate = expireDate;
	}

	public String getWiCode()
	{
		return wiCode;
	}

	public void setWiCode(String wiCode)
	{
		this.wiCode = wiCode;
	}

	/**
	 * @return the imageURL
	 */
	public String getImageURL()
	{
		return imageURL;
	}

	/**
	 * @param imageURL
	 *            the imageURL to set
	 */
	public void setImageURL(String imageURL)
	{
		this.imageURL = imageURL;
	}

	public Vector getCategoryList()
	{
		return CategoryList;
	}

	public void setCategoryList(Vector categoryList)
	{
		CategoryList = categoryList;
	}

	public boolean equals(Object obj)
	{
		if (obj != null && obj instanceof CampaignData)
		{
			return id.equals(((CampaignData) obj).id);
		}
		return false;
	}

	public boolean isChecked()
	{
		return checked;
	}

	public void setChecked(boolean checked)
	{
		this.checked = checked;
	}

	public boolean isStrikethrough()
	{
		return strikethrough;
	}

	public void setStrikethrough(boolean strikethrough)
	{
		this.strikethrough = strikethrough;
	}
}
