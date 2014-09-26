package fi.bb.checkers.datatypes;

import net.rim.device.api.util.Persistable;


public class CouponCategory implements Persistable
{

	private String id;
	private String name;
	public String getId()
	{
		return id;
	}
	public void setId(String id)
	{
		this.id = id;
	}
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}

	public boolean equals(Object obj)
	{
		if (obj != null && obj instanceof CouponCategory)
		{
			return id.equals(((CouponCategory) obj).id);
		}
		return false;
	}

}
