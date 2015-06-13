package fi.bb.checkers.helpers;

import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.io.http.HttpDateParser;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import fi.bb.checkers.datatypes.CampaignData;
import fi.bb.checkers.datatypes.InboxMessage;
import fi.bb.checkers.datatypes.LocationData;
import fi.bb.checkers.datatypes.PersistentData;

public class PersistentStoreHelper
{
	// Store Key : fi.bb.checkers.helpers.PersistentStoreHelper.STORE_KEY
	private static final long STORE_KEY = 0xb943d10865509ccbL;
	private static final PersistentObject store = PersistentStore.getPersistentObject(STORE_KEY);
	private static PersistentData store_data;

	private static Vector mylist = null;

	//Anje new for cross app login/logout
	public static final String KEY_PERSISTENT_DATA = "PersistentData";
	public static final String KEY_USERNAME = "KEY_USERNAME";
	public static final String KEY_USER_PIN = "KEY_USER_PIN";
	public static final String KEY_TIMESTAMP = "KEY_TIMESTAMP";
	public static final String DATE_FORMAT = "yyyy/MM/dd hh:mm:ss";

	// Store Key : fi.bb.shoprite.helpers.PersistentStoreHelper.STORE_KEY
	private static final long STORE_KEY2 = 0xb01797c1dfcfe599L;
	private static final PersistentObject store2 = PersistentStore.getPersistentObject(STORE_KEY2);
	private static Hashtable thisAppPersistentStore;//functionaluty of my previous idea of crossapppersistent storehelper...

	private static void init()
	{
		initStoreDataHashTable();
		initPersistentDataObject();
	}

	private static void initStoreDataHashTable()
	{
		if (thisAppPersistentStore == null)//should only beinitialized once
		{
			thisAppPersistentStore = getAppPersistentStoreDetails();
			Hashtable otherAppPersistentStore = getOtherAppPersistentStoreDetails();

			String timestampThisApp = "1990/12/12 00:00:00";
			String timestampOtherApp = "1990/12/12 00:00:00";

			if (thisAppPersistentStore != null)
			{
				if ((thisAppPersistentStore.get(KEY_TIMESTAMP) != null) && (!thisAppPersistentStore.get(KEY_TIMESTAMP).equals("")))
				{
					timestampThisApp = (String)thisAppPersistentStore.get(KEY_TIMESTAMP);
				}
			}
			else
			{
				thisAppPersistentStore = new Hashtable();
			}

			if (otherAppPersistentStore != null)
			{
				if ((otherAppPersistentStore.get(KEY_TIMESTAMP) != null) && (!otherAppPersistentStore.get(KEY_TIMESTAMP).equals("")))
				{
					timestampOtherApp = (String)otherAppPersistentStore.get(KEY_TIMESTAMP);
				}
			}

			Date dateThis = new Date(HttpDateParser.parse(timestampThisApp));
			Date dateOther = new Date(HttpDateParser.parse(timestampOtherApp));

			if (dateOther.getTime() > dateThis.getTime())//other app has latest log in details
			{
				if (otherAppPersistentStore.get(PersistentStoreHelper.KEY_USERNAME) != null)
				{
					thisAppPersistentStore.put(KEY_USERNAME, otherAppPersistentStore.get(PersistentStoreHelper.KEY_USERNAME));
					if (otherAppPersistentStore.get(PersistentStoreHelper.KEY_USER_PIN) != null)
					{
						thisAppPersistentStore.put(KEY_USER_PIN, otherAppPersistentStore.get(PersistentStoreHelper.KEY_USER_PIN));
					}
					else
					{
						thisAppPersistentStore.put(KEY_USER_PIN, "");
					}
				}
				else
				{
					thisAppPersistentStore.put(KEY_USERNAME, "");
					thisAppPersistentStore.put(KEY_USER_PIN, "");
				}

				//set this app to have latest timestamp
				putTimeStampNow();
				//Save latest info
				store.setContents(thisAppPersistentStore);
				store.commit();
			}
			else //same/latest so use this app's login details
			{
				if (thisAppPersistentStore.get(PersistentStoreHelper.KEY_USERNAME) == null)
				{
					thisAppPersistentStore.put(KEY_USERNAME, "");
				}
				if (thisAppPersistentStore.get(PersistentStoreHelper.KEY_USER_PIN) == null)
				{
					thisAppPersistentStore.put(KEY_USER_PIN, "");
				}
			}
		}
	}

	private static void initPersistentDataObject()
	{
		if (store_data == null)
		{
			try
			{
				store_data = (PersistentData)thisAppPersistentStore.get(KEY_PERSISTENT_DATA);//(PersistentData) store.getContents();
			} 
			catch (Exception e)
			{
				store_data = null;
			}

			if (store_data == null)
			{
				store_data = new PersistentData();
				//store_data.setUsername("");
				store_data.setShowGPS(true);
				store_data.setShowWelcome(true);
				store_data.setShowTutorial1(true);
				store_data.setShowTutorial2(true);
				store_data.setShowTutorial3(true);
				store_data.setShowTutorial4(true);
				store_data.setShowTutorial5(true);
				store_data.setShowTutorial6(true);
				store_data.setshouldLoadImages(true);
				store_data.setAppFirstLaunch(true);
			}
		}

	}

	private static void putTimeStampNow()
	{
		//set timestamp here since, it is then latest on this app
		SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
		Date now = new Date();
		String nowS = formatter.format(now);
		thisAppPersistentStore.put(KEY_TIMESTAMP, nowS);
	}

	private static PersistentData getUserData()
	{
		if (thisAppPersistentStore == null || store_data == null)//not initialised yet
		{
			init();
		}

		return store_data;
	}

	private static void saveUserData()
	{	
		thisAppPersistentStore.put(KEY_PERSISTENT_DATA, store_data);

		//Save latest info
		store.setContents(thisAppPersistentStore);
		store.commit();
	}

	// *****************
	public static String getPIN()
	{
		//return getUserData().getPIN();

		if (thisAppPersistentStore == null || store_data == null)//not initialised yet
		{
			init();
		}

		return (String)thisAppPersistentStore.get(KEY_USER_PIN);
	}

	/* public static void setPIN(String PIN)
	{
		mylist = null;

		if (thisAppPersistentStore == null || store_data == null)//not initialised yet
		{
			init();
		}

		thisAppPersistentStore.put(KEY_USER_PIN, PIN);
		//getUserData().setPIN(PIN);
		saveUserData();
	} */

	public static String getUsername()
	{
		//return getUserData().getUsername();

		if (thisAppPersistentStore == null || store_data == null)//not initialised yet
		{
			init();
		}

		return (String)thisAppPersistentStore.get(KEY_USERNAME);
	}

	public static void setUsername(String username)
	{
		//getUserData().setUsername(username);
		if (thisAppPersistentStore == null || store_data == null)//not initialised yet
		{
			init();
		}

		thisAppPersistentStore.put(KEY_USERNAME, username);

		putTimeStampNow();
		saveUserData();

		getUserData().insertUser(username);//this was done in persistentdata usually
	}

	public static boolean showWelcome()
	{
		return getUserData().showWelcome();
	}

	public static void setShowWelcome(boolean showWelcome)
	{
		getUserData().setShowWelcome(showWelcome);
		saveUserData();
	}

	public static boolean showGPS()
	{
		return getUserData().showGPS();
	}

	public static void setShowGPS(boolean showGPS)
	{
		getUserData().setShowGPS(showGPS);
		saveUserData();
	}

	public static boolean useGPS()
	{
		return getUserData().useGPS();
	}

	public static void setUseGPS(boolean useGPS)
	{
		getUserData().setUseGPS(useGPS);
		saveUserData();
	}

	public static boolean isShowTutorial1()
	{
		return getUserData().isShowTutorial1();
	}

	public static void setShowTutorial1(boolean showTutorial1)
	{
		getUserData().setShowTutorial1(showTutorial1);
		saveUserData();
	}

	public static boolean isShowTutorial2()
	{
		return getUserData().isShowTutorial2();
	}

	public static void setShowTutorial2(boolean showTutorial2)
	{
		getUserData().setShowTutorial2(showTutorial2);
		saveUserData();
	}

	public static boolean isShowTutorial3()
	{
		return getUserData().isShowTutorial3();
	}

	public static void setShowTutorial3(boolean showTutorial3)
	{
		getUserData().setShowTutorial3(showTutorial3);
		saveUserData();
	}

	public static boolean isShowTutorial4()
	{
		return getUserData().isShowTutorial4();
	}

	public static void setShowTutorial4(boolean showTutorial4)
	{
		getUserData().setShowTutorial4(showTutorial4);
		saveUserData();
	}

	public static boolean isShowTutorial5()
	{
		return getUserData().isShowTutorial5();
	}

	public static void setShowTutorial5(boolean showTutorial5)
	{
		getUserData().setShowTutorial5(showTutorial5);
		saveUserData();
	}

	public static boolean isShowTutorial6()
	{
		return getUserData().isShowTutorial6();
	}

	public static void setShowTutorial6(boolean showTutorial6)
	{
		getUserData().setShowTutorial6(showTutorial6);
		saveUserData();
	}

	public static LocationData getSpecialsRegion()
	{
		return getUserData().getSpecialsRegion();
	}

	public static void setSpecialsRegion(LocationData specialsRegion)
	{
		getUserData().setSpecialsRegion(specialsRegion);
	}

	public static boolean hasLoginDetails()
	{
		//return getUserData().hasLoginDetails();
		return getUsername() != null && !getUsername().equals("") && getPIN() != null && getPIN().length() == 4;
	}

	public static Hashtable getAppPersistentStoreDetails()
	{
		Hashtable hashtable = null;
		try
		{
			hashtable = (Hashtable)store.getContents();
		} 
		catch (Exception e)
		{
			hashtable = null;
		}
		return hashtable;
	}

	public static Hashtable getOtherAppPersistentStoreDetails()
	{
		Hashtable hashtable = null;
		try
		{
			hashtable = (Hashtable)store2.getContents();
		} 
		catch (Exception e)
		{
			hashtable = null;
		}

		return hashtable;
	}

	public static void insertProfileUrl(String username, String url)
	{
		getUserData().insertProfileUrl(username, url);
		saveUserData();
	}

	public static String getProfileUrl(String username)
	{

		return getUserData().getProfileUrl(username);
	}

	public static boolean isPreviousUser(String username)
	{
		return getUserData().isPreviousUser(username);
	}

	public static void mylistAdd(CampaignData coupon)
	{
		coupon.setChecked(true);
		coupon.setStrikethrough(true);

		getUserData().mylistAdd(coupon.getId(), getUsername());
		saveUserData();

		getMylist().removeElement(coupon);
		getMylist().addElement(coupon);
	}

	public static void mylistDelete(CampaignData coupon)
	{
		coupon.setChecked(false);
		coupon.setStrikethrough(false);

		getUserData().mylistDelete(coupon.getId(), getUsername());
		saveUserData();

		getMylist().removeElement(coupon);
	}

	public static void mylistClear()
	{
		getUserData().mylistClear(getUsername());
		saveUserData();

		for (int i = 0; i < getMylist().size(); i++)
		{
			((CampaignData) getMylist().elementAt(i)).setChecked(false);
		}
		getMylist().removeAllElements();
	}

	public static int mylistSize()
	{
		if (getMylist()==null)
		{
			return 0;
		}
		return getMylist().size();
	}

	/**
	 * Can be cpu intensive on first call. Worst case scenario is O(n^2).
	 * 
	 * @return List of coupons in the current user's mylist. Coupons reference objects in the runtime coupon list.
	 */
	public static Vector getMylist()
	{
		if (mylist != null) return mylist;

		Vector toreturn = new Vector();
		Vector mylist = getUserData().getMylist(getUsername());

		/*if (mylist!=null)
		{*/
		Vector coupons = RuntimeStoreHelper.getCoupons(RuntimeStoreHelper.getUserData().getProvinceLocationData());
		/*if (coupons == null)
			{
				coupons = new Vector();
			}*/

		for (int i = 0; i < coupons.size(); i++)
		{
			CampaignData coupon = (CampaignData) coupons.elementAt(i);
			if (mylist.contains(coupon.getId()))
			{
				coupon.setChecked(true);
				toreturn.addElement(coupon);
			}
			else
			{
				coupon.setChecked(false);
			}

			if (toreturn.size() == mylist.size()) break;
		}

		PersistentStoreHelper.mylist = toreturn;
		return toreturn;
		/*}
		else
		{
			return null;
		}*/
	}

	public static void checkMyListCoupons()
	{
		if (mylist != null) return;

		Vector mylist = getUserData().getMylist(getUsername());
		Vector coupons = RuntimeStoreHelper.getCoupons(RuntimeStoreHelper.getUserData().getProvinceLocationData());

		CampaignData coupon;

		if (RuntimeStoreHelper.getSessionID() != null)
		{
			for (int i = 0; i < coupons.size(); i++)
			{
				coupon = (CampaignData) coupons.elementAt(i);
				if (mylist.contains(coupon.getId()))
				{
					coupon.setChecked(true);
				}
				else
				{
					coupon.setChecked(false);
				}
			}
		}
		else
		{
			for (int i = 0; i < coupons.size(); i++)
			{
				coupon = (CampaignData) coupons.elementAt(i);
				coupon.setChecked(false);
			}
		}

		mylist = null;
		coupons = null;
	}

	public static Vector getInbox()
	{
		return getUserData().getInbox();
	}

	public static void inboxAdd(InboxMessage message)
	{
		getUserData().getInbox().removeElement(message);
		getUserData().getInbox().addElement(message);
		saveUserData();
	}

	public static void inboxDelete(InboxMessage message)
	{
		getUserData().getInbox().removeElement(message);
		saveUserData();
	}

	public static void inboxDeleteAll()
	{
		getUserData().getInbox().removeAllElements();
		saveUserData();
	}

	public static void inboxMarkAll(boolean read)
	{
		Vector inbox = getInbox();
		for (int i = 0; i < inbox.size(); i++)
		{
			InboxMessage message = (InboxMessage) inbox.elementAt(i);
			message.read = read;
		}

		saveUserData();
	}

	public static int inboxUnread()
	{
		if (RuntimeStoreHelper.getSessionID() == null) return 0;

		int unread = 0;
		Vector inbox = getInbox();
		for (int i = 0; i < inbox.size(); i++)
		{
			InboxMessage message = (InboxMessage) inbox.elementAt(i);
			if (!message.read) unread++;
		}

		return unread;
	}

	public static boolean shouldUpdateProvincesPersistentData()
	{
		return shouldUpdatePersistentData(PersistentData.PROVINCES_UPDATE_DATE);
	}

	public static boolean shouldUpdateCouponCategoriesPersistentData()
	{
		return shouldUpdatePersistentData(PersistentData.COUPON_CATEGORIES_UPDATE_DATE);
	}

	public static boolean shouldUpdateSpecialsPersistentData()
	{
		return shouldUpdatePersistentData(PersistentData.SPECIAL_CATEGORIES_UPDATE_DATE);
	}

	public static boolean shouldUpdateFeedbackTypesPersistentData()
	{
		return shouldUpdatePersistentData(PersistentData.FEEDBACK_TYPES_UPDATE_DATE);
	}

	public static boolean shouldUpdateTitlesPersistentData()
	{
		return shouldUpdatePersistentData(PersistentData.TITLES_UPDATE_DATE);
	}

	public static void setLastPersistentProvinceUpdateDate()
	{
		Date now = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		getUserData().setLastProvinceUpateDate(dateFormat.format(now));
		saveUserData();
	}

	public static void setLastPersistentCouponCategoriesUpdateDate()
	{
		Date now = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		getUserData().setLastUpdateCouponCategoriesDate(dateFormat.format(now));
		saveUserData();
	}

	public static void setLastPersistentSpecialCategoriesDate()
	{
		Date now = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		getUserData().setLastUpdateSpecialCategoriesDate(dateFormat.format(now));
		saveUserData();
	}

	public static void setLastPersistentFeedbackTypesUpdateDate()
	{
		Date now = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		getUserData().setLastUpdateFeedbackTypesDate(dateFormat.format(now));
		saveUserData();
	}

	public static void setLastPersistentTitlesUpdateDate()
	{
		Date now = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		getUserData().setLastUpdateTitlesDate(dateFormat.format(now));
		saveUserData();
	}

	public static boolean shouldUpdatePersistentData(String whichList)//whichList refers to the static final strings in PersistentData like: PROVINCES_UPDATE_DATE
	{
		try
		{
			String lastDate = "";

			if (whichList.equals(PersistentData.PROVINCES_UPDATE_DATE) == true)
			{
				lastDate = getUserData().getLastProvinceUpateDate();
			}
			else if (whichList.equals(PersistentData.COUPON_CATEGORIES_UPDATE_DATE) == true)
			{
				lastDate = getUserData().getLastUpdateCouponCategoriesDate();
			}
			else if (whichList.equals(PersistentData.SPECIAL_CATEGORIES_UPDATE_DATE) == true)
			{
				lastDate = getUserData().getLastUpdateSpecialCategoriesDate();
			}
			else if (whichList.equals(PersistentData.FEEDBACK_TYPES_UPDATE_DATE) == true)
			{
				lastDate = getUserData().getLastUpdateFeedbackTypesDate();
			}
			else if (whichList.equals(PersistentData.TITLES_UPDATE_DATE) == true)
			{
				lastDate = getUserData().getLastUpdateTitlesDate();
			}

			if (lastDate==null || lastDate.equals(""))
			{
				return true;
			}
			else
			{
				Date now = new Date();

				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				String nowS = dateFormat.format(now);

				int yearNow = Integer.parseInt(nowS.substring(0,4));
				int yearLast = Integer.parseInt(lastDate.substring(0,4));

				int monthNow = Integer.parseInt(nowS.substring(5,7));
				int monthLast = Integer.parseInt(lastDate.substring(5,7));

				if (yearNow==yearLast)
				{
					if (monthNow==monthLast)
					{
						return false;
					}
					else
					{
						return true;
					}
				}
				else
				{
					return true;
				}
			}
		}
		catch (Exception e)
		{
			return true;
		}
	}

	public static void setProvinces(Vector provinces)
	{
		getUserData().setProvinces(provinces);
		saveUserData();
	}

	public static Vector getProvinces()
	{
		return getUserData().getProvinces();
	}

	public static void setCouponCategories(Vector categories)
	{
		getUserData().setCouponCategories(categories);
		saveUserData();
	}

	public static Vector getCouponCategories()
	{
		return getUserData().getCouponCategories();
	}

	public static void setSpecialCategories(Vector specialCategories)
	{
		getUserData().setSpecialCategories(specialCategories);
		saveUserData();
	}

	public static Vector getSpecialCategories()
	{
		return getUserData().getSpecialCategories();
	}

	public static void setFeedbackTypes(Vector feedbackTypes)
	{
		getUserData().setFeedbackTypes(feedbackTypes);
		saveUserData();
	}

	public static Vector getFeedbackTypes()
	{
		return getUserData().getFeedbackTypes();
	}

	public static void setTotalSavings(String totalSavings)
	{
		getUserData().setTotalSavings(totalSavings);
		saveUserData();
	}

	public static String getTotalSavings()
	{
		return getUserData().getTotalSavings();
	}

	public static boolean shouldLoadImages()
	{
		return getUserData().shouldLoadImages();
	}

	public static void setshouldLoadImages(boolean shouldLoadImages)
	{
		getUserData().setshouldLoadImages(shouldLoadImages);
		saveUserData();
	}

	public static void setTitles(Vector titles)
	{
		getUserData().setTitles(titles);
		saveUserData();
	}

	public static Vector getTitles()
	{
		return getUserData().getTitles();
	}

	public static boolean isAppFirstLaunch()
	{
		return getUserData().isAppFirstLaunch();
	}

	public static void setAppFirstLaunch(boolean appFirstLaunch)
	{
		getUserData().setAppFirstLaunch(appFirstLaunch);
		saveUserData();
	}
}
