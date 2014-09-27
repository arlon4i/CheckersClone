package fi.bb.checkers.datatypes;

import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import fi.bb.checkers.helpers.StringHelper;
import net.rim.device.api.util.Persistable;

public class PersistentData implements Persistable
{
	public static final String PROVINCES_UPDATE_DATE = "PROVINCE_UPDATE_DATE";
	public static final String COUPON_CATEGORIES_UPDATE_DATE = "COUPON_CATEGORIES_UPDATE_DATE";
	public static final String SPECIAL_CATEGORIES_UPDATE_DATE = "SPECIAL_CATEGORIES_UPDATE_DATE";
	public static final String FEEDBACK_TYPES_UPDATE_DATE = "FEEDBACK_TYPES_UPDATE_DATE";
	public static final String TITLES_UPDATE_DATE = "TITLES_UPDATE_DATE";

	//private String username; //Now using CrossAppPersistentHelper
	//private String PIN; //Now using CrossAppPersistentHelper

	private boolean showWelcome;
	private boolean showGPS;
	private boolean useGPS;

	// Home
	private boolean showTutorial1;
	// profile
	private boolean showTutorial2;
	// coupons
	private boolean showTutorial3;
	// specials
	private boolean showTutorial4;
	// list
	private boolean showTutorial5;
	// store
	private boolean showTutorial6;

	private Hashtable user_info = new Hashtable();
	private Vector inbox = new Vector();
	private LocationData specialsRegion;
	
	private String lastUpdateProvinceDate;//yyyy-mm-dd
	private String lastUpdateCouponCategoriesDate;//yyyy-mm-dd
	private String lastUpdateSpecialCategoriesDate;//yyyy-mm-dd
	private String lastUpdateFeedbackTypesDate;//yyyy-mm-dd
	private String lastUpdateTitlesDate;//yyyy-mm-dd
	
	private Vector provinces;//<LocationData>
	private Vector categories;//<CouponCategory>
	private Vector specialCategories;//<CouponCategory>
	private Vector feedbackTypes;//<String>
	
	private Vector titles;//<Title>
	
	private String totalSavings;
	
	private boolean shouldLoadImages;

	private boolean appFirstLaunch;
	
	public PersistentData()
	{
		InboxMessage message = new InboxMessage();
		message.message_id = 0;
		//message.title = "Welcome to your Checkers Inbox.";
		message.title = StringHelper.welcome_inbox_title;
		//message.description = "Get easy access to the best deals and save over R2000 with EeziCoupons plus much more from your favourite Checkers and Checkers Hyper stores."; //"Get all the latest Promotions, Competitions, News, Special offers and great savings from your favourite Checkers and Checkers Hyper.\n\nDon't miss out!";
		message.description = StringHelper.welcome_inbox_desc;
		message.date_recieved = new Date().getTime();

		inbox.addElement(message);
	}

	/*public String getPIN()
	{
		//return PIN;
		return CrossAppPersistentHelper.getPIN();
	}

	public void setPIN(String PIN)
	{
		//this.PIN = PIN;
		CrossAppPersistentHelper.setPIN(PIN);
	}

	public String getUsername()
	{
		//return username;
		return CrossAppPersistentHelper.getUsername();
	}

	public void setUsername(String username)
	{
		//this.username = username;
		CrossAppPersistentHelper.setUsername(username);
		insertUser();
	}*/

	public boolean showGPS()
	{
		return showGPS;
	}

	public void setShowGPS(boolean showGPS)
	{
		this.showGPS = showGPS;
	}

	public boolean useGPS()
	{
		return useGPS;
	}

	public void setUseGPS(boolean useGPS)
	{
		this.useGPS = useGPS;
	}

	public boolean isShowTutorial1()
	{
		return showTutorial1;
	}

	public void setShowTutorial1(boolean showTutorial1)
	{
		this.showTutorial1 = showTutorial1;
	}

	public boolean isShowTutorial2()
	{
		return showTutorial2;
	}

	public void setShowTutorial2(boolean showTutorial2)
	{
		this.showTutorial2 = showTutorial2;
	}

	public boolean isShowTutorial3()
	{
		return showTutorial3;
	}

	public void setShowTutorial3(boolean showTutorial3)
	{
		this.showTutorial3 = showTutorial3;
	}

	public boolean isShowTutorial4()
	{
		return showTutorial4;
	}

	public void setShowTutorial4(boolean showTutorial4)
	{
		this.showTutorial4 = showTutorial4;
	}

	public boolean isShowTutorial5()
	{
		return showTutorial5;
	}

	public void setShowTutorial5(boolean showTutorial5)
	{
		this.showTutorial5 = showTutorial5;
	}

	public boolean isShowTutorial6()
	{
		return showTutorial6;
	}

	public void setShowTutorial6(boolean showTutorial6)
	{
		this.showTutorial6 = showTutorial6;
	}

	public LocationData getSpecialsRegion()
	{
		return specialsRegion;
	}

	public void setSpecialsRegion(LocationData specialsRegion)
	{
		this.specialsRegion = specialsRegion;
	}

	/*public boolean hasLoginDetails() 
	{
		return getUsername() != null && !getUsername().equals("") && getPIN() != null && getPIN().length() == 4;
	}*/

	public Vector getInbox()
	{
		return inbox;
	}

	public void insertUser(String username)
	{
		if (!isPreviousUser(username)) user_info.put(username, new PersistentUserData());
	}

	public void insertProfileUrl(String username, String url)
	{
		if (url == null) url = "";
		PersistentUserData userdata = (PersistentUserData) user_info.get(username);
		if (userdata == null)
		{
			userdata = new PersistentUserData();
			user_info.put(username, userdata);
		}

		userdata.profile_picture_url = url;

		user_info.put(username, userdata);
	}

	public String getProfileUrl(String username)
	{
		PersistentUserData userdata = (PersistentUserData) user_info.get(username);
		if (userdata == null) return null;

		String url = userdata.profile_picture_url;
		return "".equals(url) ? null : url;
	}

	public boolean isPreviousUser(String username)
	{
		return user_info.containsKey(username);
	}

	public void mylistAdd(String coupon_id, String username)
	{
		PersistentUserData userdata = (PersistentUserData) user_info.get(username);
		userdata.mylist.removeElement(coupon_id);
		userdata.mylist.addElement(coupon_id);
	}

	public void mylistDelete(String coupon_id, String username)
	{
		PersistentUserData userdata = (PersistentUserData) user_info.get(username);
		userdata.mylist.removeElement(coupon_id);
	}

	public void mylistClear(String username)
	{
		PersistentUserData userdata = (PersistentUserData) user_info.get(username);
		userdata.mylist.removeAllElements();
	}

	public Vector getMylist(String username)
	{
		PersistentUserData userdata = (PersistentUserData) user_info.get(username);
		return userdata.mylist;
	}

	public boolean showWelcome()
	{
		return showWelcome;
	}

	public void setShowWelcome(boolean showWelcome)
	{
		this.showWelcome = showWelcome;
	}
	
	public String getLastProvinceUpateDate()
	{
		return lastUpdateProvinceDate;
	}
	
	public void setLastProvinceUpateDate(String lastUpdateProvinceDate)
	{
		this.lastUpdateProvinceDate = lastUpdateProvinceDate;
	}
	
	public String getLastUpdateCouponCategoriesDate()
	{
		return lastUpdateCouponCategoriesDate;
	}
	
	public void setLastUpdateCouponCategoriesDate(String lastUpdateCouponCategoriesDate)
	{
		this.lastUpdateCouponCategoriesDate = lastUpdateCouponCategoriesDate;
	}
	
	public String getLastUpdateSpecialCategoriesDate()
	{
		return lastUpdateSpecialCategoriesDate;
	}
	
	public void setLastUpdateSpecialCategoriesDate(String lastUpdateSpecialCategoriesDate)
	{
		this.lastUpdateSpecialCategoriesDate = lastUpdateSpecialCategoriesDate;
	}
	
	public String getLastUpdateFeedbackTypesDate()
	{
		return lastUpdateFeedbackTypesDate;
	}
	
	public void setLastUpdateFeedbackTypesDate(String lastUpdateFeedbackTypesDate)
	{
		this.lastUpdateFeedbackTypesDate = lastUpdateFeedbackTypesDate;
	}
	
	public String getLastUpdateTitlesDate()
	{
		return lastUpdateTitlesDate;
	}
	
	public void setLastUpdateTitlesDate(String lastUpdateTitlesDate)
	{
		this.lastUpdateTitlesDate = lastUpdateTitlesDate;
	}
	
	public Vector getProvinces()
	{
		return provinces;
	}
	
	public void setProvinces(Vector provinces)
	{
		this.provinces = provinces;
	}
	
	public Vector getCouponCategories()
	{
		return categories;
	}
	
	public void setCouponCategories(Vector categories)
	{
		this.categories = categories;
	}
	
	public Vector getSpecialCategories()
	{
		return specialCategories;
	}
	
	public void setSpecialCategories(Vector specialCategories)
	{
		this.specialCategories = specialCategories;
	}
	
	public Vector getFeedbackTypes()
	{
		return feedbackTypes;
	}
	
	public void setFeedbackTypes(Vector feedbackTypes)
	{
		this.feedbackTypes = feedbackTypes;
	}
	
	public void setTotalSavings(String totalSavings)
	{
		this.totalSavings = totalSavings;
	}
	
	public String getTotalSavings()
	{
		return this.totalSavings;
	}
	
	public boolean shouldLoadImages()
	{
		return shouldLoadImages;
	}

	public void setshouldLoadImages(boolean shouldLoadImages)
	{
		this.shouldLoadImages = shouldLoadImages;
	}
	
	public Vector getTitles()
	{
		return titles;
	}
	
	public void setTitles(Vector titles)
	{
		this.titles = titles;
	}
	
	public boolean isAppFirstLaunch()
	{
		return appFirstLaunch;
	}

	public void setAppFirstLaunch(boolean appFirstLaunch)
	{
		this.appFirstLaunch = appFirstLaunch;
	}

}
