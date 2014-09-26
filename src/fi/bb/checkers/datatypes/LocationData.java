package fi.bb.checkers.datatypes;

import net.rim.device.api.util.Persistable;

public class LocationData implements Persistable
{
	private String id;
	private String desc;

	public LocationData()
	{

	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getDesc()
	{
		return desc;
	}

	public void setDesc(String desc)
	{
		this.desc = desc;
	}

	public boolean equals(Object obj)
	{
		if (obj == null || !(obj instanceof LocationData)) return false;

		LocationData loc = (LocationData) obj;
		return id.equals(loc.id);
	}

	public int compareTo(Object obj)
	{

		LocationData loc = (LocationData) obj;
		return this.desc.compareTo(loc.getDesc());
	}
}
