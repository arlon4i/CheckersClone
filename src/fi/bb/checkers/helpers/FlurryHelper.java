package fi.bb.checkers.helpers;

import java.util.Calendar;
import java.util.Hashtable;
import java.util.Vector;

import net.rim.device.api.i18n.SimpleDateFormat;

import com.flurry.blackberry.FlurryAgent;

import fi.bb.checkers.datatypes.LocationData;
import fi.bb.checkers.datatypes.TimedEvent;
import fi.bb.checkers.logger.RemoteLogger;

public class FlurryHelper {

	public static final String FLURRY_KEY = "C29NWKVPD7F6KRYHYTYN";
	
	public static final String FLURRY_DATE_FORMAT_STRING = "yyyy/MM/dd HH:mm:ss";
	public static final SimpleDateFormat FLURRY_DATE_FORMAT = new SimpleDateFormat(FLURRY_DATE_FORMAT_STRING);

	//events
	//********************Home screen******************************//
	public static final String EVENT_THEME_VIEWED = "Theme Viewed";
	public static final String EVENT_THEME_VIEW_ALL_SPECIALS = "Theme View All Specials";
	public static final String EVENT_HOME_SCREEN = "Home Screen";
	public static final String EVENT_EEZI_COUPON_BLOCK = "EeziCoupon Block";
	public static final String EVENT_EEZI_COUPON_REDEEM_ALL_BUTTON = "EeziCoupon Redeem All button";
	public static final String EVENT_EEZI_COUPON_VIEW_ALL_BUTTON = "EeziCoupon View All button";
	public static final String EVENT_SPECIALS_BLOCK = "Specials Block";
	public static final String EVENT_SPECIALS_VIEW_ALL = "Specials View All button";
	public static final String EVENT_FIND_A_STORE_BLOCK = "Find a Store Block";
	public static final String EVENT_FIND_A_STORE_MAP = "Find a Store Map button";
	public static final String EVENT_CONTENT_BLOCK_VIEW = "Content Block Viewed";

	//********************Sharing******************************//
	public static final String EVENT_SHARE_EEZI_COUPON = "Share EeziCoupon";
	public static final String EVENT_SHARE_SPECIAL = "Share Special";
	public static final String EVENT_SHARE_APP = "Share App";

	//********************User Status******************************//
	public static final String EVENT_OPEN_APP = "Open App";
	public static final String EVENT_USER_STATUS = "User Status";
	public static final String EVENT_REGISTRATION = "Registration";
	public static final String EVENT_REGISTRATION_CONFIRMED = "Registration Confirmed";
	public static final String EVENT_VIEW_COUPONS = "View Coupons";
	public static final String EVENT_LOG_IN = "Log in";
	public static final String EVENT_SIGN_UP = "Sign Up";

	//********************Redeem******************************//

	public static final String EVENT_REDEEM_ALL_HOME = "Redeem All (Home)";
	public static final String EVENT_REDEEM = "Redeem";

	//********************Inbox******************************//
	public static final String EVENT_READ_MESSAGE = "Read Message";
	public static final String EVENT_MESSAGE_ACTION = "Message Action";
	public static final String EVENT_UNREAD_MESSAGE = "Unread Message";

	//********************EeziCoupons******************************//
	public static final String EVENT_FILTER_EEZI_COUPONS = "Filter (EeziCoupons)";
	public static final String EVENT_SORT_EEZI_COUPONS = "Sort (EeziCoupons)";
	public static final String EVENT_BROWSE_EEZI_COUPONS = "Browse EeziCoupons";
	public static final String EVENT_SEARCH_EEZI_COUPONS = "Search EeziCoupons";
	public static final String EVENT_SEARCH_RESULT_EEZI_COUPONS = "Search Result for EeziCoupons";
	public static final String EVENT_SELECT_EEZI_COUPONS = "Select EeziCoupon";
	public static final String EVENT_ADD_EEZI_COUPON_TO_LIST = "Add EeziCoupon to List";
	public static final String EVENT_SELECT_RELATED_PRODUCTS = "Select Related Products";
	public static final String EVENT_ADD_RELATED_EEZI_COUPONS = "Add Related EeziCoupons";
	public static final String EVENT_REDEEM_ALL_EEZI_COUPONS = "Redeem All (EeziCoupons)";

	//********************Filter Specials******************************//
	public static final String EVENT_FILTER_SPECIALS = "Filter (Specials)";
	public static final String EVENT_SORT_SPECIALS = "Sort (Specials)";
	public static final String EVENT_BROWSE_SPECIALS = "Browse Specials";
	public static final String EVENT_SEARCH_SPECIALS = "Search Specials";
	public static final String EVENT_SEARCH_RESULT_SPECIALS = "Search Result Specials";

	//*****************Find a Store**************************************//
	public static final String EVENT_POINT_OF_INTEREST = "Point of Interest";
	public static final String EVENT_SEARCH_STORES = "Search Stores";
	public static final String EVENT_SEARCH_RESULTS_FIND_A_STORE = "Search Results (Find a Store)";
	public static final String EVENT_PREFFERED_STORE = "Preffered Store";

	//*****************Navigation**************************************//	
	public static final String EVENT_PROFILE_ICON = "Profile Icon";
	public static final String EVENT_MY_LIST = "My List";
	public static final String EVENT_INBOX = "Inbox";
	public static final String EVENT_EDIT_PROFILE = "Edit Profile";
	public static final String EVENT_NAVIGATION_MENU_ICON = "Navigation Menu Icon";
	public static final String EVENT_HOME = "Home";
	public static final String EVENT_EEZI_COUPONS = "EeziCoupons";
	public static final String EVENT_SPECIALS = "Specials";
	public static final String EVENT_FIND_A_STORE = "Find a Store";
	public static final String EVENT_LEAVE_FEEDBACK = "Leave Feedback";
	public static final String EVENT_HELP_AND_CONTACT_US = "Help and Contact Us";
	public static final String EVENT_NOTIFICATION_SHOWN = "Notification Shown";

	//*****************Engagement**************************************//
	public static final String EVENT_PUSH_NOTIFICATION_SERVED = "Push Notification Served";
	public static final String EVENT_APP_OPEN_FROM_PUSH = "App open from Push";
	public static final String EVENT_OPT_OUT_OPT_IN = "Opt Out? Opt In";

	//*****************Leave Feedback**************************************//
	public static final String EVENT_HAPPY = "Happy";
	public static final String EVENT_RATE_THIS_APP = "Rate This App";
	public static final String EVENT_SEND_A_MESSAGE = "Send a Message";
	public static final String EVENT_SHARE_ON_FACEBOOK = "Share on Facebook";
	public static final String EVENT_TWEET_ABOUT_US = "Tweet About Us";
	public static final String EVENT_UNHAPPY = "UnHappy";
	public static final String EVENT_SPEAK_TO_A_CONSULTANT = "Speak to a Consultant";

	//params
	public static final String PARAM_TAPPED = "Tapped";
	public static final String PARAM_TIME = "Time";
	public static final String PARAM_PROVINCE = "Province";
	public static final String PARAM_LOGIN = "Login";
	public static final String PARAM_THEME_ID = "Theme ID";
	public static final String PARAM_BLOCK_NUMBER = "Block Number";
	public static final String PARAM_DURATION = "Duration";
	public static final String PARAM_CATEGORY = "Category";
	public static final String PARAM_PRODUCT_NAME = "Product Name";
	public static final String PARAM_RELATED_PRODUCT = "Related Product";
	public static final String PARAM_FIRST_LAUNCH = "First Launch";
	public static final String PARAM_CHANNEL = "Channel";
	public static final String PARAM_COUPON_MONETARY_VALUE = "Coupon Monetary Value";
	public static final String PARAM_OPEN_SUCCESS = "Open Success";
	public static final String PARAM_REGISTRATION_STATUS = "Registration Status";
	public static final String PARAM_VERSION = "Version";
	public static final String PARAM_OPERATING_SYSTEM = "Operating System";
	public static final String PARAM_DEVICE = "Device";
	public static final String PARAM_SIGNUP_REGISTER = "Sign up, Register";
	public static final String PARAM_PIN_ENTERED = "Pin Entered";
	public static final String PARAM_TOTAL_EEZI_COUPON_SAVINGS = "Total EeziCoupon Savings";
	public static final String PARAM_ITEMS = "Items";
	public static final String PARAM_NON_COUPON_ITEMS = "Non-Coupon Items";
	public static final String PARAM_NUMBER_OF_EEZI_COUPONS = "Number of EeziCoupons";
	public static final String PARAM_CHECKERS_POI = "Checkers POI";
	public static final String PARAM_SHOPRITE_POI = "Shoprite POI";
	public static final String PARAM_STORE_ID = "Store ID";
	public static final String PARAM_STORE_ADDED = "Store Added";
	public static final String PARAM_LISTS = "Lists";
	public static final String PARAM_UNREAD_NOTIFICATIONS = "unread_notifications";
	public static final String PARAM_BLOCKS = "Blocks";
	public static final String PARAM_EEZI_COUPONS = "EeziCoupons";
	public static final String PARAM_TOTAL_SAVINGS = "Total Savings";
	public static final String PARAM_SPECIALS = "Specials";
	public static final String PARAM_NOTIFICATION_TYPE = "Notification Type";
	public static final String PARAM_PUSH_MESSAGE_TYPE = "Push Message Type";
	public static final String PARAM_USER_TITLE = "User Title";
	public static final String PARAM_MESSAGE_STATUS = "Message Status";
	public static final String PARAM_CAMPAIGN_ID = "Campaign ID";
	public static final String PARAM_TOTAL_SAVINGS_AVAILABLE = "Total Savings Available";

	private static Vector timedEvents;//<TimedEvent>

	public static void init()
	{
		timedEvents = new Vector();
	}

	public static String getDuration(Calendar startTime, Calendar endTime)
	{
		long diffInSeconds = (endTime.getTime().getTime() - startTime.getTime().getTime()) / 1000;

		long diff[] = new long[] { 0, 0, 0, 0 };
		/* sec */diff[3] = (diffInSeconds >= 60 ? diffInSeconds % 60 : diffInSeconds);
		/* min */diff[2] = (diffInSeconds = (diffInSeconds / 60)) >= 60 ? diffInSeconds % 60 : diffInSeconds;
		/* hours */diff[1] = (diffInSeconds = (diffInSeconds / 60)) >= 24 ? diffInSeconds % 24 : diffInSeconds;
		/* days */diff[0] = (diffInSeconds = (diffInSeconds / 24));

		/*  System.out.println(String.format(
	        "%d day%s, %d hour%s, %d minute%s, %d second%s ago",
	        diff[0],
	        diff[0] > 1 ? "s" : "",
	        diff[1],
	        diff[1] > 1 ? "s" : "",
	        diff[2],
	        diff[2] > 1 ? "s" : "",
	        diff[3],
	        diff[3] > 1 ? "s" : ""));*/

		String duration = "";

		if (diff[1] < 10)
		{
			duration += "0"+diff[1];
		}
		else 
		{
			duration += ""+diff[1];
		}

		duration += ":";
		if (diff[2] < 10)
		{
			duration += "0"+diff[2];
		}
		else 
		{
			duration += ""+diff[2];
		}

		duration += ":";
		if (diff[3] < 10)
		{
			duration += "0"+diff[3];
		}
		else 
		{
			duration += ""+diff[3];
		}
		//RemoteLogger.log("EVENT", "hh:mm:ss |" + duration + "|");
		return duration;
	}

	public static String getFlurryFormatDate(Calendar date)
	{
		String dateFormatted = "";
		try
		{
			dateFormatted = FLURRY_DATE_FORMAT.format(date.getTime());
		}
		catch (Exception e)
		{
			dateFormatted = date.getTime().toString();
		}

		return dateFormatted;
	}

	public static void addFirstLaunchParam(Hashtable eventParams)
	{
		if (PersistentStoreHelper.isAppFirstLaunch() == true)
		{
			eventParams.put(FlurryHelper.PARAM_FIRST_LAUNCH, "1");
		}
		else
		{
			eventParams.put(FlurryHelper.PARAM_FIRST_LAUNCH, "0");
		}
	}

	public static void addProvinceParam(Hashtable eventParams)
	{
		if (RuntimeStoreHelper.getSessionID() != null)
		{
			eventParams.put(FlurryHelper.PARAM_PROVINCE, RuntimeStoreHelper.getUserData().getProvinceLocationData());
		}
		else
		{
			try
			{
				eventParams.put(FlurryHelper.PARAM_PROVINCE, ((LocationData)PersistentStoreHelper.getProvinces().elementAt(0)).getId());//defaults to first province in the list as per everywhere in the app
			}
			catch (Exception e)//provinces not yet setup for first app launch...
			{
				eventParams.put(FlurryHelper.PARAM_PROVINCE, "");//Might need to implement that google thing here
			}
		}
	}

	public static void addRegistrationStatusParam(Hashtable eventParams)
	{
		//if you are logged in, you are registered
		if (RuntimeStoreHelper.getSessionID() != null)
		{
			eventParams.put(FlurryHelper.PARAM_REGISTRATION_STATUS, "1");
		}
		else
		{
			eventParams.put(FlurryHelper.PARAM_REGISTRATION_STATUS, "0");
		}
	}

	public static void addLoginParams(Hashtable eventParams)
	{
		if (RuntimeStoreHelper.getSessionID() != null)
		{
			eventParams.put(FlurryHelper.PARAM_LOGIN, "1");
		}
		else
		{
			eventParams.put(FlurryHelper.PARAM_LOGIN, "0");
		}
	}

	/*public static void startTimedEvent(String eventName, Hashtable eventParams, Calendar startTime)
	{
		TimedEvent timedEvent = new TimedEvent(eventName, eventParams, startTime);
		timedEvents.addElement(timedEvent);
	}*/

	public static void endTimedEvent(String eventName)
	{
		endTimedEvent(eventName, null);
	}
	
	public static void endTimedEvent(String eventName, Hashtable eventParams)
	{
		Calendar endTime = Calendar.getInstance();

		TimedEvent tempEvent;

		for (int i=0; i < timedEvents.size(); i++)
		{
			tempEvent = ((TimedEvent)timedEvents.elementAt(i));
			if (tempEvent.getEventName().equals(eventName) == true)
			{
				if (eventParams == null)
				{
					eventParams = tempEvent.getEventParams();
				}
				eventParams.put(PARAM_DURATION, getDuration(tempEvent.getStartTime(), endTime));

				//RemoteLogger.log("EVENT", "event name: " + eventName + " params: " + eventParams);
				FlurryAgent.onEvent(eventName, eventParams);

				timedEvents.removeElementAt(i);
				break;
			}
		}
	}

	public static void logEvent(String eventName, Hashtable eventParams, boolean timed)
	{		
		if(timed == true)//timed events will be logged when ending timed event
		{
			TimedEvent timedEvent = new TimedEvent(eventName, eventParams, Calendar.getInstance());
			timedEvents.addElement(timedEvent);
		}
		else
		{			
			if (eventParams == null)
			{
				//RemoteLogger.log("EVENT", "event name: " + eventName);
				FlurryAgent.onEvent(eventName);
			}
			else
			{
				//RemoteLogger.log("EVENT", "event name: " + eventName + " params: " + eventParams);
				FlurryAgent.onEvent(eventName, eventParams);
			}
		}
	}
}
