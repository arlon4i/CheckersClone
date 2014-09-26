package fi.bb.checkers.datatypes;


public class MerchantData
{
	private String id;
	private String name;
	private String physicalAddress;
	private double distance;
	private String contactDetails;
	private String tradingHours;
	private String province;
	private String latitude;
	private String longitude;
	private String brand;

	public MerchantData()
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

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getPhysicalAddress()
	{
		return physicalAddress;
	}

	public void setPhysicalAddress(String physicalAddress)
	{
		this.physicalAddress = physicalAddress;
	}

	public double getDistance()
	{
		return distance;
	}

	public void setDistance(double distance)
	{
		this.distance = distance;
	}

	public String getContactDetails()
	{
		return contactDetails;
	}

	public void setContactDetails(String contactDetails)
	{
		this.contactDetails = contactDetails;
	}

	public String getTradingHours()
	{
		return tradingHours;
	}

	public void setTradingHours(String tradingHours)
	{
		this.tradingHours = tradingHours;
	}

	public String getProvince()
	{
		return province;
	}

	public void setProvince(String province)
	{
		this.province = province;
	}

	public boolean equals(Object obj)
	{
		if (obj instanceof MerchantData)
		{
			return id.equals(((MerchantData) obj).getId());
		}
		return super.equals(obj);
	}

	public String getLatitude()
	{
		return latitude;
	}

	public void setLatitude(String latitude)
	{
		this.latitude = latitude;
	}

	public String getLongitude()
	{
		return longitude;
	}

	public void setLongitude(String longitude)
	{
		this.longitude = longitude;
	}

	public String getBrand()
	{
		return brand;
	}

	public void setBrand(String brand)
	{
		this.brand = brand;
	}
}
