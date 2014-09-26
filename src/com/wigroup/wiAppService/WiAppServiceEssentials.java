package com.wigroup.wiAppService;

import net.rim.device.api.ui.UiApplication;

import com.wigroup.wiAppService.responsehandlers.WiAppFeedbackTypesResponseHandler;
import com.wigroup.wiAppService.responsehandlers.WiAppLocationResponseHandler;
import com.wigroup.wiAppService.responsehandlers.WiAppLoginResponseHandler;
import com.wigroup.wiAppService.responsehandlers.WiAppMerchantInfoResponseHandler;
import com.wigroup.wiAppService.responsehandlers.WiAppMerchantListResponseHandler;
import com.wigroup.wiAppService.responsehandlers.WiAppResponseHandler;
import com.wigroup.wiAppService.responsehandlers.WiAppUserDetailResponseHandler;
import com.wigroup.wiAppService.responsehandlers.WiappTitleResponseHandler;

import fi.bb.checkers.MainApplication;
import fi.bb.checkers.datatypes.UserData;
import fi.bb.checkers.helpers.PersistentStoreHelper;
import fi.bb.checkers.helpers.RuntimeStoreHelper;
import fi.bb.checkers.logger.RemoteLogger;

public class WiAppServiceEssentials
{

	// CHECKERS QA
	/*final static String apiver = "2.4";
		final static String appId = "TESTAPP1";
		final static String password = "test";*/

	// CHECKERS LIVE

	final static String apiver = "2.4";
	final static String appId = "BLACKBERRY";
	final static String password = "blackb3rry_int3rfac3_wiapp";

	// SHOPRITE
	/*
	 * final static String apiver = "2.4"; final static String appId = "TEST_SHOPRITE_BLACKBERRY"; final static String password = "test";
	 */

	static MainApplication app = (MainApplication) UiApplication.getUiApplication();

	public static WiAppLoginResponseHandler login(String mobile, String pin)
	{
		try
		{

		String request = "<wiapp ver=\"" + apiver + "\"" + " appid=\"" + appId + "\"" + " password=\"" + password + "\"" + " reqid=\"2\">" + "<loginrx>" + "<username>" + mobile + "</username>"
				+ "<password>" + pin + "</password>" + "<mobilenum>" + mobile + "</mobilenum>" + "<subscribertype>GENERAL</subscribertype>" + "</loginrx>" + "</wiapp>";

		WiAppLoginResponseHandler response = new WiAppLoginResponseHandler();

		WiAppServiceRequest.sendRequest(request, response);
		return response;
		}
		catch (Exception e) {
			RemoteLogger.log("CHECKERS", "NULL NULL NULL: " + e.getMessage());
			return null;
		}

		//return response;
	}

	public static WiAppResponseHandler register(UserData user)
	{

		String request = "<wiapp ver=\"" + apiver + "\"" + " appid=\"" + appId + "\"" + " password=\"" + password + "\"" + " reqid=\"2\">" + "<regrx>" + "<title_id>" + user.getTitleId() + "</title_id>" + "<username>" + user.getUsername()
				+ "</username>" + "<mobilenum>" + user.getCellphone() + "</mobilenum>" + "<subscribertype>GENERAL</subscribertype>" + "<name>" + user.getFirstname() + "</name>" + "<surname>"
				+ user.getSurname() + "</surname>" + "<countryid>ZA</countryid>" + "<dateofbirth>" + user.getBirthdate() + "</dateofbirth>" + "<email>" + user.getEmail() + "</email>"
				+ "<cvprovinceid>" + user.getProvinceLocationData() + "</cvprovinceid>" + "</regrx>" + "</wiapp>";

		WiAppResponseHandler response = new WiAppResponseHandler();

		WiAppServiceRequest.sendRequest(request, response);

		return response;
	}

	public static WiAppUserDetailResponseHandler getUserDetails(String username, String sessionID)
	{

		String request = "<wiapp ver=\"" + apiver + "\"" + " appid=\"" + appId + "\"" + " password=\"" + password + "\"" + " reqid=\"2\">" + "<getsubinforx>" + "<username>" + username + "</username>"
				+ "<sessionid>" + sessionID + "</sessionid>" + "</getsubinforx>" + "</wiapp>";

		WiAppUserDetailResponseHandler response = new WiAppUserDetailResponseHandler();

		WiAppServiceRequest.sendRequest(request, response);

		if (response.getResponseCode().equals("042"))
		{
			// session expired login user
			response = new WiAppUserDetailResponseHandler();
			WiAppLoginResponseHandler loginResponse = WiAppServiceEssentials.login(PersistentStoreHelper.getUsername(), PersistentStoreHelper.getPIN());
			// if user logged in successfully ... update session details and re send request
			if (loginResponse.getResponseCode().equalsIgnoreCase("-1"))
			{
				RuntimeStoreHelper.setSessionID(loginResponse.getSessionId());
				sessionID = loginResponse.getSessionId();
				// refresh coupons

				request = "<wiapp ver=\"" + apiver + "\"" + " appid=\"" + appId + "\"" + " password=\"" + password + "\"" + " reqid=\"2\">" + "<getsubinforx>" + "<username>" + username
						+ "</username>" + "<sessionid>" + sessionID + "</sessionid>" + "</getsubinforx>" + "</wiapp>";
				WiAppServiceRequest.sendRequest(request, response);
			}
		}

		return response;
	}

	public static WiAppFeedbackTypesResponseHandler getFeedbackTypes()
	{
		String request = "<wiapp ver=\"" + apiver + "\"" + " appid=\"" + appId + "\"" + " password=\"" + password + "\" reqid=\"2\"><customerfeedbacktypesrx></customerfeedbacktypesrx></wiapp>";
		WiAppFeedbackTypesResponseHandler response = new WiAppFeedbackTypesResponseHandler();
		WiAppServiceRequest.sendRequest(request, response);

		return response;
	}

	public static WiAppResponseHandler sendCustomerFeedback(String username, String feedbackMessage, String complaintType)
	{
		String request;

		request = "<wiapp ver=\"" + apiver + "\"" + " appid=\"" + appId + "\"" + " password=\"" + password + "\"" + " reqid=\"2\"><customerfeedbackrx>";
		request = request + "<username>" + username + "</username>";
		request = request + "<type>" + complaintType + "</type>";
		request = request + "<message>" + feedbackMessage + "</message>";
		request = request + "<debuginfo><device>Blackberry</device></debuginfo>";
		request = request + "</customerfeedbackrx></wiapp>";

		WiAppResponseHandler response = new WiAppResponseHandler();
		WiAppServiceRequest.sendRequest(request, response);

		return response;
	}

	public static WiAppResponseHandler sendCustomerFeedback(String titleId, String firstname, String surname, String email, String mobilenumber, String provinceid, String prefferedStore, String feedbackMessage, String complaintType)
	{
		String request;

		request = "<wiapp ver=\"" + apiver + "\"" + " appid=\"" + appId + "\"" + " password=\"" + password + "\"" + " reqid=\"2\"><customerfeedbackrx>";
		String customerInfoTag = "<customerinfo>";
		customerInfoTag = customerInfoTag + "<title_id>" + titleId + "</title_id>";
		customerInfoTag = customerInfoTag + "<name>" + firstname + "</name>";
		customerInfoTag = customerInfoTag + "<surname>" + surname + "</surname>";
		customerInfoTag = customerInfoTag + "<mobilenumber>" + mobilenumber + "</mobilenumber>";
		
		if ((email !=null) && (!email.equals("")))
		{
			customerInfoTag = customerInfoTag + "<email>" + email + "</email>";	
		}
		else
		{
			customerInfoTag = customerInfoTag + "<email>Not Available</email>";	
		}
		
		customerInfoTag = customerInfoTag + "<provinceid>" + provinceid + "</provinceid>";
		customerInfoTag = customerInfoTag + "<neareststore>" + prefferedStore + "</neareststore>";
		customerInfoTag = customerInfoTag + "</customerinfo>";
		request = request + customerInfoTag;
		request = request + "<type>" + complaintType + "</type>";
		request = request + "<message>" + feedbackMessage + "</message>";
		request = request + "<debuginfo><device>Blackberry</device></debuginfo>";
		request = request + "</customerfeedbackrx></wiapp>";
		
		RemoteLogger.log("ANJE", "request: " + request);

		WiAppResponseHandler response = new WiAppResponseHandler();
		WiAppServiceRequest.sendRequest(request, response);

		return response;
	}

	public static WiAppResponseHandler changeDetails(String titleId, String username, String sessionId, String newName, String newSurname, String newEmail, String newDob, String prefferedStoreId, String cvprovinceId)
	{
		String request;

		if (prefferedStoreId == null) prefferedStoreId = "";

		String titleTag = "";

		if (!titleId.equals(""))
		{
			titleTag = "<title_id>" + titleId + "</title_id>";
		}

		if (prefferedStoreId.equalsIgnoreCase(""))
		{
			request = "<wiapp ver=\"" + apiver + "\"" + " appid=\"" + appId + "\"" + " password=\"" + password + "\"" + " reqid=\"2\">" + "<updatesubinforx>" + titleTag + "<username>" + username + "</username>"
					+ "<sessionid>" + sessionId + "</sessionid>" + "<subscriberinfo>" + "<name>" + newName + "</name>" + "<surname>" + newSurname + "</surname>" + "<email>" + newEmail + "</email>"
					+ "<dateofbirth>" + newDob + "</dateofbirth>" + "<cvprovinceid>" + cvprovinceId + "</cvprovinceid>" + "</subscriberinfo>"
					+ "</updatesubinforx>" + "</wiapp>";
		}
		else
		{
			request = "<wiapp ver=\"" + apiver + "\"" + " appid=\"" + appId + "\"" + " password=\"" + password + "\"" + " reqid=\"2\">" + "<updatesubinforx>" + titleTag + "<username>" + username + "</username>"
					+ "<sessionid>" + sessionId + "</sessionid>" + "<subscriberinfo>" + "<name>" + newName + "</name>" + "<surname>" + newSurname + "</surname>" + "<email>" + newEmail + "</email>"
					+ "<dateofbirth>" + newDob + "</dateofbirth>" + "<favmerchantid>" + prefferedStoreId + "</favmerchantid>" + "<cvprovinceid>" + cvprovinceId + "</cvprovinceid>" + "</subscriberinfo>"
					+ "</updatesubinforx>" + "</wiapp>";
		}
		
		/*request = "<wiapp ver=\"" + apiver + "\"" + " appid=\"" + appId + "\"" + " password=\"" + password + "\"" + " reqid=\"2\">" + "<updatesubinforx>" + titleTag + "<username>" + username + "</username>"
				+ "<sessionid>" + sessionId + "</sessionid>" + "<subscriberinfo>" + "<name>" + newName + "</name>" + "<surname>" + newSurname + "</surname>" + "<email>" + newEmail + "</email>"
				+ "<dateofbirth>" + newDob + "</dateofbirth>" + "<favmerchantid>" + prefferedStoreId + "</favmerchantid>" + "<cvprovinceid>" + cvprovinceId + "</cvprovinceid>" + "</subscriberinfo>"
				+ "</updatesubinforx>" + "</wiapp>";*/

		WiAppResponseHandler response = new WiAppResponseHandler();
		WiAppServiceRequest.sendRequest(request, response);

		if (response.getResponseCode().equals("042"))
		{
			response = new WiAppUserDetailResponseHandler();
			WiAppLoginResponseHandler loginResponse = WiAppServiceEssentials.login(PersistentStoreHelper.getUsername(), PersistentStoreHelper.getPIN());

			if (loginResponse.getResponseCode().equalsIgnoreCase("-1"))
			{
				RuntimeStoreHelper.setSessionID(loginResponse.getSessionId());
				sessionId = loginResponse.getSessionId();

				if (prefferedStoreId.equalsIgnoreCase(""))
				{
					request = "<wiapp ver=\"" + apiver + "\"" + " appid=\"" + appId + "\"" + " password=\"" + password + "\"" + " reqid=\"2\">" + "<updatesubinforx>" + titleTag + "<username>" + username + "</username>"
							+ "<sessionid>" + sessionId + "</sessionid>" + "<subscriberinfo>" + "<name>" + newName + "</name>" + "<surname>" + newSurname + "</surname>" + "<email>" + newEmail + "</email>"
							+ "<dateofbirth>" + newDob + "</dateofbirth>" + "<cvprovinceid>" + cvprovinceId + "</cvprovinceid>" + "</subscriberinfo>"
							+ "</updatesubinforx>" + "</wiapp>";
				}
				else
				{
					request = "<wiapp ver=\"" + apiver + "\"" + " appid=\"" + appId + "\"" + " password=\"" + password + "\"" + " reqid=\"2\">" + "<updatesubinforx>" + titleTag + "<username>" + username + "</username>"
							+ "<sessionid>" + sessionId + "</sessionid>" + "<subscriberinfo>" + "<name>" + newName + "</name>" + "<surname>" + newSurname + "</surname>" + "<email>" + newEmail + "</email>"
							+ "<dateofbirth>" + newDob + "</dateofbirth>" + "<favmerchantid>" + prefferedStoreId + "</favmerchantid>" + "<cvprovinceid>" + cvprovinceId + "</cvprovinceid>" + "</subscriberinfo>"
							+ "</updatesubinforx>" + "</wiapp>";
				}
				
				/*if (prefferedStoreId.equalsIgnoreCase(""))
				{
					request = "<wiapp ver=\"" + apiver + "\"" + " appid=\"" + appId + "\"" + " password=\"" + password + "\"" + " reqid=\"2\">" + "<updatesubinforx>" + "<username>" + username
							+ "</username>" + "<sessionid>" + sessionId + "</sessionid>" + "<subscriberinfo>" + "<title_id>" + titleId + "</title_id>" + "<name>" + newName + "</name>" + "<surname>" + newSurname + "</surname>" + "<email>"
							+ newEmail + "</email>" + "<dateofbirth>" + newDob + "</dateofbirth>" + "<cvprovinceid>" + cvprovinceId + "</cvprovinceid>" + "</subscriberinfo>" + "</updatesubinforx>"
							+ "</wiapp>";
				}
				else
				{
					request = "<wiapp ver=\"" + apiver + "\"" + " appid=\"" + appId + "\"" + " password=\"" + password + "\"" + " reqid=\"2\">" + "<updatesubinforx>" + "<username>" + username
							+ "</username>" + "<sessionid>" + sessionId + "</sessionid>" + "<subscriberinfo>" + "<title_id>" + titleId + "</title_id>" + "<name>" + newName + "</name>" + "<surname>" + newSurname + "</surname>" + "<email>"
							+ newEmail + "</email>" + "<dateofbirth>" + newDob + "</dateofbirth>" + "<favmerchantid>" + prefferedStoreId + "</favmerchantid>" + "<cvprovinceid>" + cvprovinceId
							+ "</cvprovinceid>" + "</subscriberinfo>" + "</updatesubinforx>" + "</wiapp>";
				}*/
				WiAppServiceRequest.sendRequest(request, response);
			}
		}

		return response;
	}

	public static WiAppResponseHandler changePin(String username, String sessionId, String oldPin, String newPin)
	{

		String request = "<wiapp ver=\"" + apiver + "\"" + " appid=\"" + appId + "\"" + " password=\"" + password + "\"" + " reqid=\"2\">" + "<subchangepasswordrx>" + "<username>" + username
				+ "</username>" + "<sessionid>" + sessionId + "</sessionid>" + "<oldpassword>" + oldPin + "</oldpassword>" + "<newpassword>" + newPin + "</newpassword>" + "</subchangepasswordrx>"
				+ "</wiapp>";

		WiAppResponseHandler response = new WiAppResponseHandler();

		WiAppServiceRequest.sendRequest(request, response);

		if (response.getResponseCode().equals("042"))
		{
			// session expired login user
			response = new WiAppUserDetailResponseHandler();
			WiAppLoginResponseHandler loginResponse = WiAppServiceEssentials.login(PersistentStoreHelper.getUsername(), PersistentStoreHelper.getPIN());
			// if user logged in successfully ... update session details and resend request
			if (loginResponse.getResponseCode().equalsIgnoreCase("-1"))
			{
				RuntimeStoreHelper.setSessionID(loginResponse.getSessionId());
				sessionId = loginResponse.getSessionId();
				// refresh coupons
				request = "<wiapp ver=\"" + apiver + "\"" + " appid=\"" + appId + "\"" + " password=\"" + password + "\"" + " reqid=\"2\">" + "<subchangepasswordrx>" + "<username>" + username
						+ "</username>" + "<sessionid>" + sessionId + "</sessionid>" + "<oldpassword>" + oldPin + "</oldpassword>" + "<newpassword>" + newPin + "</newpassword>"
						+ "</subchangepasswordrx>" + "</wiapp>";
				WiAppServiceRequest.sendRequest(request, response);
			}
		}

		return response;
	}

	public static WiAppResponseHandler resetPin(String mobile)
	{

		String request = "<wiapp ver=\"" + apiver + "\"" + " appid=\"" + appId + "\"" + " password=\"" + password + "\"" + " reqid=\"2\">" + "<resetsubpasswordrx>" + "<username>" + mobile
				+ "</username>" + "</resetsubpasswordrx>" + "</wiapp>";

		WiAppResponseHandler response = new WiAppResponseHandler();

		WiAppServiceRequest.sendRequest(request, response);

		return response;
	}

	public static WiAppLocationResponseHandler getProvinceList()
	{

		String request = "<wiapp ver=\"" + apiver + "\"" + " appid=\"" + appId + "\"" + " password=\"" + password + "\"" + " reqid=\"2\">" + "<provincelistrx>" + "<countryid>ZA</countryid>"
				+ "</provincelistrx>" + "</wiapp>";

		WiAppLocationResponseHandler response = new WiAppLocationResponseHandler();

		WiAppServiceRequest.sendRequest(request, response);

		return response;
	}

	public static WiAppMerchantListResponseHandler getStoreList(String provinceId, String latitude, String longitude)
	{
		String request;

		if (provinceId == null)//get all provinces' stores
		{
			request = "<wiapp ver=\"" + apiver + "\"" + " appid=\"" + appId + "\"" + " password=\"" + password + "\"" + " reqid=\"2\">" + "<getmerchantlistrx>" + "<pagesize>500</pagesize>" + "</getmerchantlistrx>" + "</wiapp>";
		}
		else
		{

			if (latitude == null || longitude == null)
				request = "<wiapp ver=\"" + apiver + "\"" + " appid=\"" + appId + "\"" + " password=\"" + password + "\"" + " reqid=\"2\">" + "<getmerchantlistrx>" + "<provinceid>" + provinceId
				+ "</provinceid>" + "<gps>" + "<longitude>" + longitude + "</longitude>" + "<latitude>" + latitude + "</latitude>" + "</gps>" + "<pagesize>500</pagesize>" + "</getmerchantlistrx>"
				+ "</wiapp>";
			else
				request = "<wiapp ver=\"" + apiver + "\"" + " appid=\"" + appId + "\"" + " password=\"" + password + "\"" + " reqid=\"2\">" + "<getmerchantlistrx>" + "<provinceid>" + provinceId
				+ "</provinceid>" + "<pagesize>500</pagesize>" + "</getmerchantlistrx>" + "</wiapp>";
		}

		WiAppMerchantListResponseHandler response = new WiAppMerchantListResponseHandler();
		WiAppServiceRequest.sendRequest(request, response);

		return response;
	}

	public static WiAppMerchantInfoResponseHandler getStoreInfo(String merchantId)
	{

		String request = "<wiapp ver=\"" + apiver + "\"" + " appid=\"" + appId + "\"" + " password=\"" + password + "\"" + " reqid=\"2\">" + "<getmerchantinforx>" + "<merchantid>" + merchantId
				+ "</merchantid>" + "</getmerchantinforx>" + "</wiapp>";

		WiAppMerchantInfoResponseHandler response = new WiAppMerchantInfoResponseHandler();

		WiAppServiceRequest.sendRequest(request, response);

		return response;
	}

	public static WiappTitleResponseHandler getTitles()
	{
		String request = "<wiapp ver=\"" + apiver + "\"" + " appid=\"" + appId + "\"" + " password=\"" + password + "\"" + " reqid=\"2\">" + "<gettitlesrx></gettitlesrx></wiapp>";
		WiappTitleResponseHandler response = new WiappTitleResponseHandler();
		WiAppServiceRequest.sendRequest(request, response);

		return response;
	}
}
