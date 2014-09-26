package fi.bb.checkers.helpers;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

import net.rim.device.api.io.ConnectionClosedException;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.ui.component.Dialog;

import org.json.me.JSONArray;
import org.json.me.JSONException;
import org.json.me.JSONObject;

import com.wigroup.wiAppService.WiAppServiceCampaigns;
import com.wigroup.wiAppService.WiAppServiceEssentials;
import com.wigroup.wiAppService.responsehandlers.WiAppCampaignResponseHandler;
import com.wigroup.wiAppService.responsehandlers.WiAppDisclaimerResponseHandler;
import com.wigroup.wiAppService.responsehandlers.WiAppFeedbackTypesResponseHandler;
import com.wigroup.wiAppService.responsehandlers.WiAppLocationResponseHandler;
import com.wigroup.wiAppService.responsehandlers.WiAppLoginResponseHandler;
import com.wigroup.wiAppService.responsehandlers.WiAppMerchantInfoResponseHandler;
import com.wigroup.wiAppService.responsehandlers.WiAppMerchantListResponseHandler;
import com.wigroup.wiAppService.responsehandlers.WiAppResponseHandler;
import com.wigroup.wiAppService.responsehandlers.WiAppUserDetailResponseHandler;
import com.wigroup.wiAppService.responsehandlers.WiAppWiCodeResponseHandler;
import com.wigroup.wiAppService.responsehandlers.WiappCouponCategoriesResponseHandler;
import com.wigroup.wiAppService.responsehandlers.WiappGetTotalsavingsResponseHandler;
import com.wigroup.wiAppService.responsehandlers.WiappTitleResponseHandler;

import fi.bb.checkers.datatypes.CampaignData;
import fi.bb.checkers.datatypes.CouponCategory;
import fi.bb.checkers.datatypes.LocationData;
import fi.bb.checkers.datatypes.MerchantData;
import fi.bb.checkers.datatypes.Title;
import fi.bb.checkers.datatypes.UserData;
import fi.bb.checkers.logger.RemoteLogger;
import fi.bb.checkers.network.HttpInterface;
import fi.bb.checkers.network.PINException;
import fi.bb.checkers.sharepointService.SharepointServiceSpecials;
import fi.bb.checkers.sharepointService.responsehandlers.SharepointFeaturedResponseHandler;
import fi.bb.checkers.utils.StringUtil;

public class ServerHelper
{
	private static final String ERROR_NULL = "Connection interrupted.";
	private static WicodeUpdateThread wicodethread;

	public static void getProvinces() throws IOException
	{
		WiAppLocationResponseHandler locationResponse = WiAppServiceEssentials.getProvinceList();
		if (locationResponse == null || locationResponse.getResponseCode() == null)
		{
			RemoteLogger.log("ServerHelper", "getProvinces null");
			throw new IOException(ERROR_NULL);
		}
		else if (!locationResponse.getResponseCode().equals("-1"))
		{
			RemoteLogger.log("ServerHelper", "getProvinces " + locationResponse.getResponseMessage());
			throw new IOException(locationResponse.getResponseMessage());
		}

		Vector provinces = new Vector();
		int length = locationResponse.getLocationList().size();
		for (int i = 0; i < length; i++)
		{
			LocationData location = (LocationData) locationResponse.getLocationList().elementAt(i);
			provinces.addElement(location);
		}
		PersistentStoreHelper.setProvinces(provinces);
		PersistentStoreHelper.setLastPersistentProvinceUpdateDate();
	}

	public static Vector getStores(LocationData province) throws IOException
	{
		WiAppMerchantListResponseHandler response;

		if (province == null)//get all provinces' stores
		{
			response = WiAppServiceEssentials.getStoreList(null, null, null);
		}
		else
		{	
			if (LocationHelper.coords != null)
			{
				response = WiAppServiceEssentials.getStoreList(province.getId(), String.valueOf(LocationHelper.coords.getLatitude()), String.valueOf(LocationHelper.coords.getLongitude()));
			}
			else
			{
				response = WiAppServiceEssentials.getStoreList(province.getId(), null, null);
			}
		}

		if (response == null || response.getResponseCode() == null)
		{
			throw new IOException(ERROR_NULL);
		}
		else if (!response.getResponseCode().equals("-1"))
		{
			throw new IOException(response.getResponseMessage());
		}

		return response.getMerchantList();
	}

	public static MerchantData getStoreInfo(MerchantData merchant) throws IOException
	{
		return getStoreInfo(merchant.getId());
	}
	public static MerchantData getStoreInfo(String id) throws IOException
	{
		WiAppMerchantInfoResponseHandler response = WiAppServiceEssentials.getStoreInfo(id);

		if (response == null || response.getResponseCode() == null)
		{
			throw new IOException(ERROR_NULL);
		}
		else if (!response.getResponseCode().equals("-1"))
		{
			throw new IOException(response.getResponseMessage());
		}

		return response.getMerchant();
	}

	public static void getCouponCategories() throws IOException
	{
		WiappCouponCategoriesResponseHandler categoriesResponse = WiAppServiceCampaigns.getCouponCategories("COUPON");
		if (categoriesResponse == null || categoriesResponse.getResponseCode() == null)
		{
			RemoteLogger.log("getCategories", "getStores null");
			throw new IOException(ERROR_NULL);
		}
		else if (!categoriesResponse.getResponseCode().equals("-1"))
		{
			RemoteLogger.log("ServerHelper", "getCategories " + categoriesResponse.getResponseMessage());
			throw new IOException(categoriesResponse.getResponseMessage());
		}

		Vector categories = new Vector();
		int length = categoriesResponse.getCouponCategories().size();
		for (int i = 0; i < length; i++)
		{
			CouponCategory category = (CouponCategory) categoriesResponse.getCouponCategories().elementAt(i);
			if (category.getId().equals("17")) continue; // ignore the "All" category
			categories.addElement(category);
		}
		PersistentStoreHelper.setCouponCategories(categories);
		PersistentStoreHelper.setLastPersistentCouponCategoriesUpdateDate();
	}

	public static void getTitles() throws IOException
	{
		WiappTitleResponseHandler titleResponse = WiAppServiceEssentials.getTitles();
		if (titleResponse == null || titleResponse.getResponseCode() == null)
		{
			RemoteLogger.log("getTitles", "getTitles null");
			throw new IOException(ERROR_NULL);
		}
		else if (!titleResponse.getResponseCode().equals("-1"))
		{
			RemoteLogger.log("ServerHelper", "getTitles " + titleResponse.getResponseMessage());
			throw new IOException(titleResponse.getResponseMessage());
		}

		Vector titles = new Vector();
		int length = titleResponse.getTitles().size();
		Title title;
		for (int i = 0; i < length; i++)
		{
			title = (Title) titleResponse.getTitles().elementAt(i);
			RemoteLogger.log("titles", "title: " + title.getId() + " : " + title.getDescription());
			titles.addElement(title);
		}
		PersistentStoreHelper.setTitles(titles);
		PersistentStoreHelper.setLastPersistentTitlesUpdateDate();
	}

	public static void getSpecialCategories() throws IOException
	{
		WiappCouponCategoriesResponseHandler categoriesResponse = WiAppServiceCampaigns.getSpecialCategories();
		if (categoriesResponse == null || categoriesResponse.getResponseCode() == null)
		{
			RemoteLogger.log("getSpecialCategories", "getSpecialCategories null");
			throw new IOException(ERROR_NULL);
		}
		else if (!categoriesResponse.getResponseCode().equals("-1"))
		{
			RemoteLogger.log("ServerHelper", "getSpecialCategories " + categoriesResponse.getResponseMessage());
			throw new IOException(categoriesResponse.getResponseMessage());
		}

		Vector categories = new Vector();
		int length = categoriesResponse.getCouponCategories().size();
		for (int i = 0; i < length; i++)
		{
			CouponCategory category = (CouponCategory) categoriesResponse.getCouponCategories().elementAt(i);
			//if (category.getId().equals("17")) continue; // ignore the "All" category
			categories.addElement(category);
		}
		PersistentStoreHelper.setSpecialCategories(categories);
		PersistentStoreHelper.setLastPersistentSpecialCategoriesDate();
	}

	/**
	 * Returns a string array containing the update message and url if an update is available. else returns null
	 * 
	 * @param username
	 * @param pin
	 * @return
	 * @throws IOException
	 */
	public static String[] login(String username, String pin) throws IOException
	{
		// user -1 to get a response with update data from server, and its impossible for it to be an actual user
		WiAppLoginResponseHandler response = WiAppServiceEssentials.login(username == null ? "-1" : username, pin == null ? "-1" : pin);

		// if user or pin are null, this call is just to check for updates. so ignore the response messages
		if (username != null && pin != null)
		{
			if (response == null || response.getResponseCode() == null)
			{
				throw new IOException(ERROR_NULL);
			}
			else if (response.getResponseCode().equalsIgnoreCase("026"))
			{
				// incorrect pin. Will happen if a difference device resets pin or it expires
				RuntimeStoreHelper.setSessionID(null);
				PersistentStoreHelper.setPIN("");
				throw new PINException("Confirmation Code invalid. Click on \"Resend Confirmation Code\" to receive a new confirmation code.");/*response.getResponseMessage()*/
			}
			else if (!response.getResponseCode().equalsIgnoreCase("-1"))
			{
				throw new IOException(response.getResponseMessage());
			}

			PersistentStoreHelper.setUsername(username);
			RuntimeStoreHelper.setSessionID(response.getSessionId());
			PersistentStoreHelper.setPIN(pin);

			logUserStatus();
		}

		if (response != null)
		{
			if (response.getVersion() != null && response.getVersion().compareTo(ApplicationDescriptor.currentApplicationDescriptor().getVersion()) > 0)
			{
				return new String[]{response.getUpdateMessage(), response.getUpdateUrl()};
			}
		}

		return null;
	}

	private static void logUserStatus()
	{
		Hashtable eventParams = new Hashtable();
		FlurryHelper.addRegistrationStatusParam(eventParams);

		FlurryHelper.logEvent(FlurryHelper.EVENT_USER_STATUS, eventParams, false);
	}

	public static String[] getUpdateUrl()
	{
		try
		{
			WiAppLoginResponseHandler response = WiAppServiceEssentials.login("-1", "-1");

			if (response.getVersion() != null && response.getVersion().compareTo(ApplicationDescriptor.currentApplicationDescriptor().getVersion()) > 0)
			{
				return new String[]{response.getUpdateMessage(), response.getUpdateUrl()};
			}
		} catch (Exception e)
		{
		}
		return null;
	}

	public static void getUserDetails() throws IOException
	{
		WiAppUserDetailResponseHandler userdetailsResponse = WiAppServiceEssentials.getUserDetails(PersistentStoreHelper.getUsername(), RuntimeStoreHelper.getSessionID());

		if (userdetailsResponse == null || userdetailsResponse.getResponseCode() == null)
		{
			throw new IOException(ERROR_NULL);
		}
		else if (!userdetailsResponse.getResponseCode().equalsIgnoreCase("-1"))
		{
			throw new IOException(userdetailsResponse.getResponseMessage());
		}

		UserData user_data = new UserData();
		user_data.setTitleId(userdetailsResponse.getTitleId());
		user_data.setFirstname(userdetailsResponse.getFirstname());
		user_data.setSurname(userdetailsResponse.getSurname());
		user_data.setUsername(PersistentStoreHelper.getUsername());
		user_data.setPin(PersistentStoreHelper.getPIN());
		user_data.setEmail(userdetailsResponse.getEmail());
		String cellphone = userdetailsResponse.getMobile();
		if (cellphone.startsWith("27")) cellphone = "0" + cellphone.substring(2);
		user_data.setCellphone(cellphone);
		user_data.setBirthdate(userdetailsResponse.getDateOfBirth());
		user_data.setProvinceLocationData(userdetailsResponse.getCvprovinceid());

		RuntimeStoreHelper.setUserData(user_data);
		
		//Have to set specialsregion, actually what is the use of specials region!!!???
		for (int i = 0; i < PersistentStoreHelper.getProvinces().size(); i++)//RuntimeStoreHelper.getProvinces()
		{
			if (((LocationData) PersistentStoreHelper.getProvinces().elementAt(i)).getId().equals(userdetailsResponse.getCvprovinceid()))
			{
				PersistentStoreHelper.setSpecialsRegion((LocationData) PersistentStoreHelper.getProvinces().elementAt(i));
				break;
			}
		}

		getWicode();
		if (userdetailsResponse.getPreferredStore() != null && !userdetailsResponse.getPreferredStore().equals(""))
		{
			MerchantData merchant = new MerchantData();
			merchant.setId(userdetailsResponse.getPreferredStore());
			merchant = getStoreInfo(merchant);
			merchant.setId(userdetailsResponse.getPreferredStore());

			user_data.setPreferredStore(merchant);
			RuntimeStoreHelper.setUserData(user_data);
		}
	}

	public static void getWicode()// throws IOException
	{
		if (wicodethread != null && wicodethread.isAlive())
		{
			wicodethread.stop = true;
		}

		wicodethread = new WicodeUpdateThread();
		wicodethread.setPriority(Thread.MIN_PRIORITY);
		wicodethread.start();
	}

	public static String updateUserDetails(UserData profile) throws IOException
	{
		WiAppResponseHandler response;

		if (profile.getPreferredStore() == null)
		{
			response = WiAppServiceEssentials.changeDetails(profile.getTitleId(), PersistentStoreHelper.getUsername(), RuntimeStoreHelper.getSessionID(), profile.getFirstname(), profile.getSurname(), profile.getEmail(),
					StringUtil.remove(profile.getBirthdate(), "/"), "", profile.getProvinceLocationData());
		}
		else
		{
			response = WiAppServiceEssentials.changeDetails(profile.getTitleId(), PersistentStoreHelper.getUsername(), RuntimeStoreHelper.getSessionID(), profile.getFirstname(), profile.getSurname(), profile.getEmail(),
					StringUtil.remove(profile.getBirthdate(), "/"), profile.getPreferredStore().getId(), profile.getProvinceLocationData());
		}

		if (response.getResponseCode().equals("-1"))
		{
			RuntimeStoreHelper.setUserData(profile);
			for (int i = 0; i < PersistentStoreHelper.getProvinces().size(); i++)//RuntimeStoreHelper.getProvinces()
			{
				if (((LocationData) PersistentStoreHelper.getProvinces().elementAt(i)).getId().equals(profile.getProvinceLocationData()))
				{
					PersistentStoreHelper.setSpecialsRegion((LocationData) PersistentStoreHelper.getProvinces().elementAt(i));
					break;
				}
			}
			return "success";
		}
		else
		{
			return response.getResponseMessage();
		}
	}

	public static void getCouponList(String region_id) throws IOException
	{
		/*String defaultCouponId = (RuntimeStoreHelper.getProvinceLoggedOutUser()!=null)?RuntimeStoreHelper.getProvinceLoggedOutUser().getId():null;
		WiAppCampaignResponseHandler response = WiAppServiceCampaigns.getCampaignList(PersistentStoreHelper.getUsername(),
				PersistentStoreHelper.getSpecialsRegion() != null && (RuntimeStoreHelper.getSessionID() != null) ? PersistentStoreHelper.getSpecialsRegion().getId() : defaultCouponId);*/
		
		WiAppCampaignResponseHandler response = WiAppServiceCampaigns.getCampaignList(PersistentStoreHelper.getUsername(), region_id);

		if (response == null || response.getResponseCode() == null)
		{
			RemoteLogger.log("ServerHelper", "getCouponList null");
			throw new IOException(ERROR_NULL);
		}
		else if (!response.getResponseCode().equalsIgnoreCase("-1"))
		{
			RemoteLogger.log("ServerHelper", "getCouponList " + response.getResponseMessage());
			throw new IOException(response.getResponseMessage());
		}

		Vector coupons = new Vector();
		int length = response.getCampaignList().size();
		for (int i = 0; i < length; i++)
		{
			CampaignData campaign = (CampaignData) response.getCampaignList().elementAt(i);
			coupons.addElement(campaign);
		}
		
		RuntimeStoreHelper.setCouponsForLocationId(coupons, region_id);
		//RuntimeStoreHelper.setCoupons(coupons);
	}

	public static Vector getSpecialList(String region_id) throws IOException
	{

		WiAppCampaignResponseHandler response = WiAppServiceCampaigns.getSpecialsList(region_id + "_SPECIAL");

		if (response == null || response.getResponseCode() == null)
		{
			RemoteLogger.log("ServerHelper", "getSpecialList null");
			throw new IOException(ERROR_NULL);
		}
		else if (!response.getResponseCode().equalsIgnoreCase("-1"))
		{
			RemoteLogger.log("ServerHelper", "getSpecialList " + response.getResponseMessage());
			throw new IOException(response.getResponseMessage());
		}

		Vector specials = new Vector();
		int length = response.getCampaignList().size();
		for (int i = 0; i < length; i++)
		{
			CampaignData campaign = (CampaignData) response.getCampaignList().elementAt(i);
			specials.addElement(campaign);
		}

		RuntimeStoreHelper.setSpecialsForLocationId(specials, region_id);
		return specials;
	}

	public static String getDisclaimer(LocationData location) throws IOException
	{
		WiAppDisclaimerResponseHandler response = WiAppServiceCampaigns.getChannelDisclaimer(location.getId() + "_SPECIAL", location.getId());
		return response.getDisclaimer();
	}

	public static void getFeedbackTypes()
	{
		WiAppFeedbackTypesResponseHandler response = WiAppServiceEssentials.getFeedbackTypes();
		PersistentStoreHelper.setFeedbackTypes(response.getTypes());
		PersistentStoreHelper.setLastPersistentFeedbackTypesUpdateDate();
	}

	public static void getTotalSavings()
	{
		WiappGetTotalsavingsResponseHandler response = WiAppServiceCampaigns.getTotalSavings(PersistentStoreHelper.getUsername(), (PersistentStoreHelper.getSpecialsRegion() != null && (RuntimeStoreHelper.getSessionID() != null) ? PersistentStoreHelper.getSpecialsRegion().getId() : null), "COUPON");
		PersistentStoreHelper.setTotalSavings(response.getAmountInCents());
	}

	public static void sendFeedback(String titleId, String firstname, String surname, String email, String mobileNumber, String provinceId, String prefferedStore, String type, String message) throws IOException
	{
		WiAppResponseHandler response = WiAppServiceEssentials.sendCustomerFeedback(titleId, firstname, surname, email, mobileNumber, provinceId, prefferedStore, message, type);

		if (response == null || response.getResponseCode() == null)
		{
			RemoteLogger.log("ServerHelper", "sendFeedback null");
			throw new IOException(ERROR_NULL);
		}
		else if (!response.getResponseCode().equalsIgnoreCase("-1"))
		{
			RemoteLogger.log("ServerHelper", "sendFeedback " + response.getResponseMessage());
			throw new IOException(response.getResponseMessage());
		}
	}

	public static void sendFeedback(String type, String message) throws IOException
	{
		UserData profile = RuntimeStoreHelper.getUserData();
		WiAppResponseHandler response = WiAppServiceEssentials.sendCustomerFeedback(profile.getUsername(), message, type);

		if (response == null || response.getResponseCode() == null)
		{
			RemoteLogger.log("ServerHelper", "sendFeedback null");
			throw new IOException(ERROR_NULL);
		}
		else if (!response.getResponseCode().equalsIgnoreCase("-1"))
		{
			RemoteLogger.log("ServerHelper", "sendFeedback " + response.getResponseMessage());
			throw new IOException(response.getResponseMessage());
		}
	}

	public static LocationData getCurrentProvince(double lat, double lng) throws IOException, JSONException, Exception
	{
		Hashtable headers = new Hashtable();
		headers.put("Content-Type", "text/json");

		String jsonstring = HttpInterface.readStream(HttpInterface.doGET("http://maps.googleapis.com/maps/api/geocode/json?latlng=" + lat + "," + lng + "&sensor=false", headers));
		JSONObject response = new JSONObject(jsonstring);
		if ("OK".equals(response.getString("status")))
		{
			JSONArray json_array = response.getJSONArray("results");
			for (int i = 0; i < json_array.length(); i++)
			{
				JSONArray address_components = json_array.getJSONObject(i).getJSONArray("address_components");
				for (int j = 0; j < address_components.length(); j++)
				{
					String long_name = address_components.getJSONObject(j).getString("long_name");

					for (int k = 0; k < PersistentStoreHelper.getProvinces().size(); k++)//RuntimeStoreHelper.getProvinces()
					{
						LocationData location = (LocationData) PersistentStoreHelper.getProvinces().elementAt(k);
						if (location.getDesc().equalsIgnoreCase(long_name)) return location;
					}
				}
			}
		}

		return null;
	}

	private static class WicodeUpdateThread extends Thread
	{
		volatile boolean stop = false;
		public void run()
		{
			// end if logged out
			while (RuntimeStoreHelper.getSessionID() != null && !stop)
			{
				WiAppWiCodeResponseHandler wicode_response = WiAppServiceCampaigns.getWicode(PersistentStoreHelper.getUsername());
				if (wicode_response == null || wicode_response.getResponseCode() == null)
				{
					try
					{
						if (!stop) Thread.sleep(5000); // wait 5s before trying again
					} catch (InterruptedException e)
					{

					}
				}
				else if (!wicode_response.getResponseCode().equalsIgnoreCase("-1"))
				{
					try
					{
						if (!stop) Thread.sleep(5000); // wait 5s before trying again
					} catch (InterruptedException e)
					{

					}
				}
				else
				{
					// if successful wait to refresh, else retry immediately
					RuntimeStoreHelper.getUserData().setWicode(wicode_response.getWiCode());
					try
					{
						if (!stop) Thread.sleep(180000); // 3 min interval
					} catch (InterruptedException e)
					{

					}
				}
			}
		}
	}

	public static void getFeaturedData() throws Exception
	{
		try
		{
			String resp = SharepointServiceSpecials.login();// get auth token
			if (!resp.equalsIgnoreCase("noerror")) throw new IOException(ERROR_NULL);

			SharepointFeaturedResponseHandler response = SharepointServiceSpecials.getSpecials();
			if (response == null)
			{
				RemoteLogger.log("ServerHelper", "getFeaturedData null");
				throw new IOException(ERROR_NULL);
			}

			RuntimeStoreHelper.setFeaturedList(response.getList());
		} catch (ConnectionClosedException e)
		{
			RemoteLogger.log("ServerHelper", "getFeaturedData null");
			throw new IOException(ERROR_NULL);
		}
	}
}
