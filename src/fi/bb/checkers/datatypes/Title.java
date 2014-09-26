package fi.bb.checkers.datatypes;

import net.rim.device.api.util.Persistable;


public class Title implements Persistable
{

	private String id;
	private String description;
	public String getId()
	{
		return id;
	}
	public void setId(String id)
	{
		this.id = id;
	}
	public String getDescription()
	{
		return description;
	}
	public void setDescription(String description)
	{
		this.description = description;
	}

	public boolean equals(Object obj)
	{
		if (obj != null && obj instanceof Title)
		{
			return id.equals(((Title) obj).id);
		}
		return false;
	}

}
