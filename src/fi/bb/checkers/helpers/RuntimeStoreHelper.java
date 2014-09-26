package fi.bb.checkers.helpers;

import java.util.Vector;

import fi.bb.checkers.datatypes.LocationData;
import fi.bb.checkers.datatypes.UserData;
import fi.bb.checkers.interfaces.InterfaceCouponsFinishedLoading;
import fi.bb.checkers.logger.RemoteLogger;
import fi.bb.checkers.prompts.InfoDialog;
import fi.bb.checkers.prompts.LoadingDialog;
import fi.bb.checkers.utils.AsyncTask;

public class RuntimeStoreHelper
{
	private static String sessionId = null;
	private static Vector featured_list;

	private static InterfaceCouponsFinishedLoading interfaceCouponsFinishedLoading;
	private static boolean showLoadingDialog;
	private static String loadingMsg;

	private static String lastLocationIdUsedForSpecialsSearch = "";
	private static Vector specials;
	
	private static String lastLocationIdUsedForCouponsSearch = "";
	private static Vector coupons;
	
	private static LocationData provinceLoggedOutUser;
	
	public static Vector getFeaturedList()
	{
		return featured_list;
	}

	public static void setFeaturedList(Vector featured_list)
	{
		RuntimeStoreHelper.featured_list = featured_list;
	}

	private static UserData user_data;

	/*public static void setProvinces(Vector province_list)
	{
		RuntimeStoreHelper.province_list = province_list;
	}

	public static Vector getProvinces()
	{
		return province_list;
	}*/

	/*public static void setCategories(Vector categories_list)
	{
		RuntimeStoreHelper.categories_list = categories_list;
	}

	public static Vector getCategories()
	{
		return categories_list;
	}*/
	
	public static void setSpecialsForLocationId(Vector specials, String locationId)
	{
		RemoteLogger.log("TEST_SPECIALS_RUNTIME", "RuntimeStoreHelper.setSpecialsForLocationId: RuntimeStoreHelper.getLastLocationIdUsedForSpecialsSearch(): " + RuntimeStoreHelper.getLastLocationIdUsedForSpecialsSearch() + " new loc: " + locationId);
		RuntimeStoreHelper.lastLocationIdUsedForSpecialsSearch = locationId;
		RuntimeStoreHelper.specials = specials;
	}
	
	public static void setCouponsForLocationId(Vector coupons, String locationId)
	{
		RemoteLogger.log("TEST_SPECIALS_RUNTIME", "RuntimeStoreHelper.setCouponsForLocationId: RuntimeStoreHelper.getCouponsForLocationId(): " + RuntimeStoreHelper.getLastLocationIdUsedForCouponsSearch() + " new loc: " + locationId);
		RuntimeStoreHelper.lastLocationIdUsedForCouponsSearch = locationId;
		RuntimeStoreHelper.coupons = coupons;
	}

	public static Vector getSpecials()
	{
		return RuntimeStoreHelper.specials ;
	}

	public static String getLastLocationIdUsedForSpecialsSearch()
	{
		return lastLocationIdUsedForSpecialsSearch;
	}
	
	public static String getLastLocationIdUsedForCouponsSearch()
	{
		return lastLocationIdUsedForCouponsSearch;
	}
	
	/*private static void setLastLocationIdUsedForCouponsSearch(String locationId)
	{
		RuntimeStoreHelper.lastLocationIdUsedForCouponsSearch = locationId;
	}
	
	private static String getLastLocationIdUsedForCouponsSearch()
	{
		return lastLocationIdUsedForCouponsSearch;
	}*/

	/*public static void setCoupons(Vector coupons)
	{
		RuntimeStoreHelper.coupons = coupons;
	}*/

	public static Vector getCoupons(String locationId)
	{
		return getCoupons(null, false, null, locationId);
	}

	public static Vector getCoupons(InterfaceCouponsFinishedLoading interfaceCouponsFinishedLoading, boolean showLoadingDialog, String loadingMsg, String locationId)
	{
		if (RuntimeStoreHelper.getSessionID() != null)//logged in
		{
			if (locationId.equals(getLastLocationIdUsedForCouponsSearch()) == false)//not same location call
			{
				RuntimeStoreHelper.interfaceCouponsFinishedLoading = interfaceCouponsFinishedLoading;
				RuntimeStoreHelper.showLoadingDialog = showLoadingDialog;
				RuntimeStoreHelper.loadingMsg = loadingMsg;
				new DownloadCouponsTask().execute(new Object[]{locationId});
				return null;
			}
			else
			{
				return coupons;
			}
		}
		else//logged out
		{
			if (locationId.equals(getLastLocationIdUsedForCouponsSearch()) == false)//not same location call
			{
				RuntimeStoreHelper.interfaceCouponsFinishedLoading = interfaceCouponsFinishedLoading;
				RuntimeStoreHelper.showLoadingDialog = showLoadingDialog;
				RuntimeStoreHelper.loadingMsg = loadingMsg;
				new DownloadCouponsTask().execute(new Object[]{locationId});
				return null;
			}
			else
			{
				return coupons;
			}
		}
		//RemoteLogger.log("COUPONS_CHECK", "interfaceCouponsFinishedLoading: " + interfaceCouponsFinishedLoading + " showLoadingDialog: " + showLoadingDialog + " coupons: " + coupons);
		/*if ((interfaceCouponsFinishedLoading != null) && ((coupons == null) || ((coupons != null) && (coupons.size()==0))))
		{
			if ((RuntimeStoreHelper.getSessionID() != null) && (getLastLocationIdUsedForCouponsSearch().equals(RuntimeStoreHelper.user_data.getProvinceLocationData()) == false))
			{
				RuntimeStoreHelper.lastLocationIdUsedForCouponsSearch = RuntimeStoreHelper.user_data.getProvinceLocationData();
				RuntimeStoreHelper.interfaceCouponsFinishedLoading = interfaceCouponsFinishedLoading;
				RuntimeStoreHelper.showLoadingDialog = showLoadingDialog;
				RuntimeStoreHelper.loadingMsg = loadingMsg;
				new DownloadCouponsTask().execute(new Object[]{});
				return null;
			}
			else
			{
				if (RuntimeStoreHelper.getLastLocationIdUsedForCouponsSearch().equals(RuntimeStoreHelper.getProvinceLoggedOutUser().getId()) == false)
				{
					RuntimeStoreHelper.lastLocationIdUsedForCouponsSearch = RuntimeStoreHelper.getProvinceLoggedOutUser().getId();
					RuntimeStoreHelper.interfaceCouponsFinishedLoading = interfaceCouponsFinishedLoading;
					RuntimeStoreHelper.showLoadingDialog = showLoadingDialog;
					RuntimeStoreHelper.loadingMsg = loadingMsg;
					new DownloadCouponsTask().execute(new Object[]{});
					return null;
				}
				else
				{
					return coupons;
				}
			}
		}
		else
		{
			return coupons;
		}*/
	}

	public static void setUserData(UserData user_data)
	{
		RuntimeStoreHelper.user_data = user_data;
	}

	public static UserData getUserData()
	{
		return user_data;
	}

	public static String getSessionID()
	{
		return sessionId;
	}

	public static void setSessionID(String sessionId)
	{
		RuntimeStoreHelper.sessionId = sessionId;
	}
	
	public static void setProvinceLoggedOutUser(LocationData province)
	{
		RuntimeStoreHelper.provinceLoggedOutUser = province;
	}

	public static LocationData getProvinceLoggedOutUser()
	{
		return provinceLoggedOutUser;
	}

	/*public static Vector getFeedbackTypes()
	{
		return feedback_types;
	}

	public static void setFeedbackTypes(Vector feedback_types)
	{
		RuntimeStoreHelper.feedback_types = feedback_types;
	}*/

	//New method for loading coupons before using mylist basically
	/**
	 * 
	 * @param interfaceCouponsFinishedLoading
	 * @param showLoadingDialog
	 * @return boolean true, if neccessary to load coupons
	 */
	public static boolean loadCouponsinBackground(InterfaceCouponsFinishedLoading interfaceCouponsFinishedLoading, boolean showLoadingDialog, String loadingMessage)
	{
		if ((coupons == null) || ((coupons != null) && (coupons.size()==0)))
		{
			RuntimeStoreHelper.interfaceCouponsFinishedLoading = interfaceCouponsFinishedLoading;
			RuntimeStoreHelper.showLoadingDialog = showLoadingDialog;
			RuntimeStoreHelper.loadingMsg = loadingMessage;
			new DownloadCouponsTask().execute(new Object[]{RuntimeStoreHelper.getUserData().getProvinceLocationData()});
			return true;
		}
		else 
		{
			return false;
		}
	}

	private static class DownloadCouponsTask extends AsyncTask
	{
		LoadingDialog prompt;
		protected void onPreExecute()
		{
			super.onPreExecute();

			if (RuntimeStoreHelper.showLoadingDialog == true)
			{
				if ((RuntimeStoreHelper.loadingMsg == null) || ((RuntimeStoreHelper.loadingMsg != null) && (RuntimeStoreHelper.loadingMsg.equals(""))))
				{
					RuntimeStoreHelper.loadingMsg = "Downloading coupons...";
				}
				prompt = LoadingDialog.push(RuntimeStoreHelper.loadingMsg);
			}
		}

		protected void onPostExecute(Object result)
		{
			super.onPostExecute(result);

			if (RuntimeStoreHelper.showLoadingDialog == true)
			{
				prompt.close();
			}

			//RemoteLogger.log("COUPONS_CHECK", "Coupons loaded result: " + result);

			if (result.equals("success"))
			{
				//RemoteLogger.log("COUPONS_CHECK", "Coupons loaded success");

				if (RuntimeStoreHelper.interfaceCouponsFinishedLoading!=null)
				{
					RuntimeStoreHelper.interfaceCouponsFinishedLoading.onCouponsFinishedLoading(true);
				}
			}
			else
			{
				//RemoteLogger.log("COUPONS_CHECK", "Coupons loaded fail");

				String msg = ((Exception) result).getMessage();
				if (msg.length() == 0) msg = "An unexpected error occured.";
				InfoDialog.doModal("Error", msg, "Okay");

				if (RuntimeStoreHelper.interfaceCouponsFinishedLoading!=null)
				{
					RuntimeStoreHelper.interfaceCouponsFinishedLoading.onCouponsFinishedLoading(false);
				}
			}

			//reset values for next run
			RuntimeStoreHelper.interfaceCouponsFinishedLoading = null;
			RuntimeStoreHelper.showLoadingDialog = false;
			RuntimeStoreHelper.loadingMsg = null;
		}

		public Object doInBackground(Object[] params)
		{
			try
			{
				ServerHelper.getCouponList((String)params[0]);
				return "success";

			} catch (Exception e)
			{
				//RemoteLogger.log("RuntimeStoreHelper", "DownloadCouponsTask: " + e.toString());
				return e;
			}

		}
		
		public void onThreadIterrupted() {
			super.onThreadIterrupted();
			try
			{
				if (RuntimeStoreHelper.showLoadingDialog == true)
				{
					prompt.close();
				}	
			}
			catch (Exception e) {
			}
		}
	}
}
