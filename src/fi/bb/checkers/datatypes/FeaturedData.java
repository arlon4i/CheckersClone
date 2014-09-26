package fi.bb.checkers.datatypes;



public class FeaturedData
{
	public static final String serverLiveDateFormat = "yyyy-MM-dd'T'HH:mm:ss";
	
	private String imageurl;
	private String action;
	private String action_detail;
	private int order;
	private String liveDate;
	
	public String getImageURL()
	{
		return imageurl;
	}
	public void setImageURL(String imageurl)
	{
		this.imageurl = imageurl;
	}
	public String getAction()
	{
		return action;
	}
	public void setAction(String action)
	{
		this.action = action;
	}
	public String getActionDetail()
	{
		return action_detail;
	}
	public void setActionDetail(String action_detail)
	{
		this.action_detail = action_detail;
	}
	public int getOrder()
	{
		return order;
	}
	public void setOrder(int order)
	{
		this.order = order;
	}
	
	public String getLiveDate()
	{
		return liveDate;
	}

	public void setLiveDate(String liveDate)
	{
		this.liveDate = liveDate;
	}
}
