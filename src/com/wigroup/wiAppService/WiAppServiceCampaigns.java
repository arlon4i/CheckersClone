package com.wigroup.wiAppService;

import com.wigroup.wiAppService.responsehandlers.WiAppCampaignResponseHandler;
import com.wigroup.wiAppService.responsehandlers.WiAppDisclaimerResponseHandler;
import com.wigroup.wiAppService.responsehandlers.WiAppLoginResponseHandler;
import com.wigroup.wiAppService.responsehandlers.WiAppResponseHandler;
import com.wigroup.wiAppService.responsehandlers.WiAppWiCodeResponseHandler;
import com.wigroup.wiAppService.responsehandlers.WiappCouponCategoriesResponseHandler;
import com.wigroup.wiAppService.responsehandlers.WiappGetTotalsavingsResponseHandler;

import fi.bb.checkers.helpers.PersistentStoreHelper;
import fi.bb.checkers.helpers.RuntimeStoreHelper;

public class WiAppServiceCampaigns
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

	/**
	 * 
	 * @param username
	 * @param sessionID
	 * @param cvsRegionId
	 * @param vouchertype
	 * @return
	 */
	public static WiappGetTotalsavingsResponseHandler getTotalSavings(String username, String cvsRegionId, String vouchertype)
	{
		String request;

		if (RuntimeStoreHelper.getSessionID() == null)
		{
			if (cvsRegionId != null)
			{
				request = "<wiapp ver=\"" + apiver + "\"" + " appid=\"" + appId + "\"" + " password=\"" + password + "\"" + " reqid=\"2\">" + "<cvgettotalsavingsrx>"
						+ "<channelid>CHECKERS_BLACKBERRY</channelid>" + "<campaigntype>" + vouchertype + "</campaigntype><cvprovinceid>" + cvsRegionId + "</cvprovinceid>"
						+ "</cvgettotalsavingsrx></wiapp>";
			}
			else

				request = "<wiapp ver=\"" + apiver + "\"" + " appid=\"" + appId + "\"" + " password=\"" + password + "\"" + " reqid=\"2\">" + "<cvgettotalsavingsrx>"
						+ "<channelid>CHECKERS_BLACKBERRY</channelid>" + "<campaigntype>" + vouchertype + "</campaigntype>" + "</cvgettotalsavingsrx>" + "</wiapp>";
		}
		else
		{
			if (cvsRegionId != null)
			{
				request = "<wiapp ver=\"" + apiver + "\"" + " appid=\"" + appId + "\"" + " password=\"" + password + "\"" + " reqid=\"2\">" + "<cvgettotalsavingsrx>" + "<username>" + username
						+ "</username>" + "<sessionid>" + RuntimeStoreHelper.getSessionID() + "</sessionid>" + "<campaigntype>" + vouchertype + "</campaigntype>"
						+ "<channelid>CHECKERS_BLACKBERRY</channelid><cvprovinceid>" + cvsRegionId + "</cvprovinceid>" + "</cvgettotalsavingsrx>" + "</wiapp>";
			}
			else
				request = "<wiapp ver=\"" + apiver + "\"" + " appid=\"" + appId + "\"" + " password=\"" + password + "\"" + " reqid=\"2\">" + "<cvgettotalsavingsrx>" + "<username>" + username
						+ "</username>" + "<sessionid>" + RuntimeStoreHelper.getSessionID() + "</sessionid>" + "<campaigntype>" + vouchertype + "</campaigntype>"
						+ "<channelid>CHECKERS_BLACKBERRY</channelid>" + "</cvgettotalsavingsrx>" + "</wiapp>";
		}

		WiappGetTotalsavingsResponseHandler response = new WiappGetTotalsavingsResponseHandler();
		WiAppServiceRequest.sendRequest(request, response);
		if (response.getResponseCode().equals("042"))
		{
			// session expired login user
			response = new WiappGetTotalsavingsResponseHandler();
			WiAppLoginResponseHandler loginResponse = WiAppServiceEssentials.login(PersistentStoreHelper.getUsername(), PersistentStoreHelper.getPIN());
			// if user logged in successfully ... update session details and resend request
			if (loginResponse.getResponseCode().equalsIgnoreCase("-1"))
			{

				RuntimeStoreHelper.setSessionID(loginResponse.getSessionId().equals("0") ? null : loginResponse.getSessionId());
				// refresh coupons

				if (RuntimeStoreHelper.getSessionID() == null)
				{
					if (cvsRegionId != null)
					{
						request = "<wiapp ver=\"" + apiver + "\"" + " appid=\"" + appId + "\"" + " password=\"" + password + "\"" + " reqid=\"2\">" + "<cvgettotalsavingsrx>"
								+ "<channelid>CHECKERS_BLACKBERRY</channelid>" + "<campaigntype>" + vouchertype + "</campaigntype><cvprovinceid>" + cvsRegionId + "</cvprovinceid>"
								+ "</cvgettotalsavingsrx></wiapp>";
					}
					else

						request = "<wiapp ver=\"" + apiver + "\"" + " appid=\"" + appId + "\"" + " password=\"" + password + "\"" + " reqid=\"2\">" + "<cvgettotalsavingsrx>"
								+ "<channelid>CHECKERS_BLACKBERRY</channelid>" + "<campaigntype>" + vouchertype + "</campaigntype>" + "</cvgettotalsavingsrx>" + "</wiapp>";
				}
				else
				{
					if (cvsRegionId != null)
					{
						request = "<wiapp ver=\"" + apiver + "\"" + " appid=\"" + appId + "\"" + " password=\"" + password + "\"" + " reqid=\"2\">" + "<cvgettotalsavingsrx>" + "<username>" + username
								+ "</username>" + "<sessionid>" + RuntimeStoreHelper.getSessionID() + "</sessionid>" + "<campaigntype>" + vouchertype + "</campaigntype>"
								+ "<channelid>CHECKERS_BLACKBERRY</channelid><cvprovinceid>" + cvsRegionId + "</cvprovinceid>" + "</cvgettotalsavingsrx>" + "</wiapp>";
					}
					else
						request = "<wiapp ver=\"" + apiver + "\"" + " appid=\"" + appId + "\"" + " password=\"" + password + "\"" + " reqid=\"2\">" + "<cvgettotalsavingsrx>" + "<username>" + username
								+ "</username>" + "<sessionid>" + RuntimeStoreHelper.getSessionID() + "</sessionid>" + "<campaigntype>" + vouchertype + "</campaigntype>"
								+ "<channelid>CHECKERS_BLACKBERRY</channelid>" + "</cvgettotalsavingsrx>" + "</wiapp>";
				}
				WiAppServiceRequest.sendRequest(request, response);
			}
		}
		return response;

	}

	/**
	 * 
	 * @param username
	 * @param sessionID
	 * @param cvsRegionId
	 * @return
	 */
	public static WiAppCampaignResponseHandler getCampaignList(String username, String cvsRegionId)
	{

		String request;

		if (RuntimeStoreHelper.getSessionID() == null)
		{
			if (cvsRegionId != null)
			{
				request = "<wiapp ver=\"" + apiver + "\"" + " appid=\"" + appId + "\"" + " password=\"" + password + "\"" + " reqid=\"2\">" + "<cvcampaignlistrx>"
						+ "<channelid>CHECKERS_BLACKBERRY</channelid>" + "<campaigntype>COUPON</campaigntype><cvprovinceid>" + cvsRegionId + "</cvprovinceid>" + "</cvcampaignlistrx></wiapp>";
			}
			else
			{
				request = "<wiapp ver=\"" + apiver + "\"" + " appid=\"" + appId + "\"" + " password=\"" + password + "\"" + " reqid=\"2\">" + "<cvcampaignlistrx>"
						+ "<channelid>CHECKERS_BLACKBERRY</channelid>" + "<campaigntype>COUPON</campaigntype>" + "</cvcampaignlistrx>" + "</wiapp>";
			}
		}
		else
		{
			if (cvsRegionId != null)
			{
				request = "<wiapp ver=\"" + apiver + "\"" + " appid=\"" + appId + "\"" + " password=\"" + password + "\"" + " reqid=\"2\">" + "<cvcampaignlistrx>" + "<username>" + username
						+ "</username>" + "<sessionid>" + RuntimeStoreHelper.getSessionID() + "</sessionid>" + "<campaigntype>COUPON</campaigntype>"
						+ "<channelid>CHECKERS_BLACKBERRY</channelid><cvprovinceid>" + cvsRegionId + "</cvprovinceid>" + "</cvcampaignlistrx>" + "</wiapp>";
			}
			else
				request = "<wiapp ver=\"" + apiver + "\"" + " appid=\"" + appId + "\"" + " password=\"" + password + "\"" + " reqid=\"2\">" + "<cvcampaignlistrx>" + "<username>" + username
						+ "</username>" + "<sessionid>" + RuntimeStoreHelper.getSessionID() + "</sessionid>" + "<campaigntype>COUPON</campaigntype>" + "<channelid>CHECKERS_BLACKBERRY</channelid>"
						+ "</cvcampaignlistrx>" + "</wiapp>";
		}

		WiAppCampaignResponseHandler response = new WiAppCampaignResponseHandler();

		WiAppServiceRequest.sendRequest(request, response);

		if (response.getResponseCode().equals("042"))
		{
			// session expired login user
			response = new WiAppCampaignResponseHandler();
			WiAppLoginResponseHandler loginResponse = WiAppServiceEssentials.login(PersistentStoreHelper.getUsername(), PersistentStoreHelper.getPIN());
			// if user logged in successfully ... update session details and resend request
			if (loginResponse.getResponseCode().equalsIgnoreCase("-1"))
			{
				RuntimeStoreHelper.setSessionID(loginResponse.getSessionId().equals("0") ? null : loginResponse.getSessionId());

				request = "<wiapp ver=\"" + apiver + "\"" + " appid=\"" + appId + "\"" + " password=\"" + password + "\"" + " reqid=\"2\">" + "<cvcampaignlistrx>" + "<username>" + username
						+ "</username>" + "<sessionid>" + RuntimeStoreHelper.getSessionID() + "</sessionid>" + "<campaigntype>COUPON</campaigntype>" + "<channelid>CHECKERS_BLACKBERRY</channelid>"
						+ "</cvcampaignlistrx>" + "</wiapp>";
				WiAppServiceRequest.sendRequest(request, response);
			}
		}

		return response;
	}

	public static WiAppCampaignResponseHandler getSpecialsList(String channelid)
	{
		String request = "<wiapp ver=\"" + apiver + "\"" + " appid=\"" + appId + "\"" + " password=\"" + password + "\"" + " reqid=\"2\">" + "<cvcampaignlistrx>" + "<channelid>" + channelid
				+ "</channelid>" + /*"<campaigntype>VOUCHER</campaigntype>" +*/ "</cvcampaignlistrx>" + "</wiapp>";

		WiAppCampaignResponseHandler response = new WiAppCampaignResponseHandler();
		WiAppServiceRequest.sendRequest(request, response);

		return response;
	}

	// <addwordlist>
	// <addword>UCT</addword>
	// <addword>US</addword>
	// </addwordlist>

	public static WiAppCampaignResponseHandler getFilteredSpecialsList(String channelid, String username, String[] addwords)
	{
		String request = "";
		String addwordsStr = "";
		for (int x = 0; x < addwords.length; x++)
		{
			addwordsStr = addwordsStr + "<addword>" + addwords[x] + "</addword>";
		}

		if (RuntimeStoreHelper.getSessionID() == null)
		{
			request = "<wiapp ver=\"" + apiver + "\"" + " appid=\"" + appId + "\"" + " password=\"" + password + "\"" + " reqid=\"2\">" + "<cvcampaignlistrx>" + "<username>" + username
					+ "</username>" + "<channelid>" + channelid + "</channelid>" + "<addwordlist>" + addwordsStr + "</addwordlist>" + "</cvcampaignlistrx>" + "</wiapp>";
		}
		else
		{
			request = "<wiapp ver=\"" + apiver + "\"" + " appid=\"" + appId + "\"" + " password=\"" + password + "\"" + " reqid=\"2\">" + "<cvcampaignlistrx>" + "<username>" + username
					+ "</username>" + "<sessionid>" + RuntimeStoreHelper.getSessionID() + "</sessionid>" + "<channelid>" + channelid + "</channelid>" + "<addwordlist>" + addwordsStr
					+ "</addwordlist>" + "</cvcampaignlistrx>" + "</wiapp>";

		}

		WiAppCampaignResponseHandler response = new WiAppCampaignResponseHandler();

		WiAppServiceRequest.sendRequest(request, response);

		if (response.getResponseCode().equals("042"))
		{
			// session expired login user
			response = new WiAppCampaignResponseHandler();
			WiAppLoginResponseHandler loginResponse = WiAppServiceEssentials.login(PersistentStoreHelper.getUsername(), PersistentStoreHelper.getPIN());
			// if user logged in successfully ... update session details and resend request
			if (loginResponse.getResponseCode().equalsIgnoreCase("-1"))
			{
				RuntimeStoreHelper.setSessionID(loginResponse.getSessionId().equals("0") ? null : loginResponse.getSessionId());
				// refresh coupons

				request = "<wiapp ver=\"" + apiver + "\"" + " appid=\"" + appId + "\"" + " password=\"" + password + "\"" + " reqid=\"2\">" + "<cvcampaignlistrx>" + "<username>" + username
						+ "</username>" + "<sessionid>" + RuntimeStoreHelper.getSessionID() + "</sessionid>" + "<channelid>" + channelid + "</channelid>" + "<addwordlist>" + addwordsStr
						+ "</addwordlist>" + "</cvcampaignlistrx>" + "</wiapp>";
				WiAppServiceRequest.sendRequest(request, response);
			}
		}

		return response;

	}

	public static WiAppCampaignResponseHandler getFilteredCampaignList(String username, String[] addwords)
	{

		String request;
		String addwordsStr = "";
		for (int x = 0; x < addwords.length; x++)
		{
			addwordsStr = addwordsStr + "<addword>" + addwords[x] + "</addword>";
		}

		if (RuntimeStoreHelper.getSessionID() == null)
		{
			request = "<wiapp ver=\"" + apiver + "\"" + " appid=\"" + appId + "\"" + " password=\"" + password + "\"" + " reqid=\"2\">" + "<cvcampaignlistrx>" + "<channelid>COUPON</channelid>"
					+ "<addwordlist>" + addwordsStr + "</addwordlist>" + "</cvcampaignlistrx>" + "</wiapp>";
		}
		else
		{
			request = "<wiapp ver=\"" + apiver + "\"" + " appid=\"" + appId + "\"" + " password=\"" + password + "\"" + " reqid=\"2\">" + "<cvcampaignlistrx>" + "<username>" + username
					+ "</username>" + "<sessionid>" + RuntimeStoreHelper.getSessionID() + "</sessionid>" + "<campaigntype>COUPON</campaigntype>" + "<channelid>COUPON</channelid>" + "<addwordlist>"
					+ addwordsStr + "</addwordlist>" + "</cvcampaignlistrx>" + "</wiapp>";
		}

		WiAppCampaignResponseHandler response = new WiAppCampaignResponseHandler();

		WiAppServiceRequest.sendRequest(request, response);

		if (response.getResponseCode().equals("042"))
		{
			// session expired login user
			response = new WiAppCampaignResponseHandler();
			WiAppLoginResponseHandler loginResponse = WiAppServiceEssentials.login(PersistentStoreHelper.getUsername(), PersistentStoreHelper.getPIN());
			// if user logged in successfully ... update session details and resend request
			if (loginResponse.getResponseCode().equalsIgnoreCase("-1"))
			{
				RuntimeStoreHelper.setSessionID(loginResponse.getSessionId().equals("0") ? null : loginResponse.getSessionId());
				// refresh coupons

				request = "<wiapp ver=\"" + apiver + "\"" + " appid=\"" + appId + "\"" + " password=\"" + password + "\"" + " reqid=\"2\">" + "<cvcampaignlistrx>" + "<username>" + username
						+ "</username>" + "<sessionid>" + RuntimeStoreHelper.getSessionID() + "</sessionid>" + "<campaigntype>COUPON</campaigntype>" + "<channelid>COUPON</channelid>"
						+ "<addwordlist>" + addwordsStr + "</addwordlist>" + "</cvcampaignlistrx>" + "</wiapp>";
				WiAppServiceRequest.sendRequest(request, response);
			}
		}

		return response;
	}

	public static WiAppDisclaimerResponseHandler getChannelDisclaimer(String channelid, String provinceId)
	{
		String request;
		if (provinceId != null)
		{
			request = "<wiapp ver=\"" + apiver + "\"" + " appid=\"" + appId + "\"" + " password=\"" + password + "\"" + " reqid=\"2\">" + "<cvchanneldisclaimerrx>" + "<channelid>" + channelid
					+ "</channelid>" + "<provinceid>" + provinceId + "</provinceid>" + "</cvchanneldisclaimerrx>" + "</wiapp>";
		}
		else
		{
			request = "<wiapp ver=\"" + apiver + "\"" + " appid=\"" + appId + "\"" + " password=\"" + password + "\"" + " reqid=\"2\">" + "<cvchanneldisclaimerrx>" + "<channelid>" + channelid
					+ "</channelid>" + "</cvchanneldisclaimerrx>" + "</wiapp>";
		}

		WiAppDisclaimerResponseHandler response = new WiAppDisclaimerResponseHandler();

		WiAppServiceRequest.sendRequest(request, response);

		return response;
	}

	public static WiAppWiCodeResponseHandler issueCampaign(String username, String campaignID)
	{

		String request = "<wiapp ver=\"" + apiver + "\"" + " appid=\"" + appId + "\"" + " password=\"" + password + "\"" + " reqid=\"2\">" + "<cvissuerx>" + "<username>" + username + "</username>"
				+ "<sessionid>" + RuntimeStoreHelper.getSessionID() + "</sessionid>" + "<campaignid>" + campaignID + "</campaignid>" + "<channelid>COUPON</channelid>" + "</cvissuerx>" + "</wiapp>";

		WiAppWiCodeResponseHandler response = new WiAppWiCodeResponseHandler();

		WiAppServiceRequest.sendRequest(request, response);

		if (response.getResponseCode().equals("042"))
		{
			// session expired login user
			response = new WiAppWiCodeResponseHandler();
			WiAppLoginResponseHandler loginResponse = WiAppServiceEssentials.login(PersistentStoreHelper.getUsername(), PersistentStoreHelper.getPIN());
			// if user logged in successfully ... update session details and resend request
			if (loginResponse.getResponseCode().equalsIgnoreCase("-1"))
			{
				RuntimeStoreHelper.setSessionID(loginResponse.getSessionId().equals("0") ? null : loginResponse.getSessionId());
				// refresh coupons

				request = "<wiapp ver=\"" + apiver + "\"" + " appid=\"" + appId + "\"" + " password=\"" + password + "\"" + " reqid=\"2\">" + "<cvissuerx>" + "<username>" + username + "</username>"
						+ "<sessionid>" + RuntimeStoreHelper.getSessionID() + "</sessionid>" + "<campaignid>" + campaignID + "</campaignid>" + "<channelid>COUPON</channelid>" + "</cvissuerx>"
						+ "</wiapp>";
				WiAppServiceRequest.sendRequest(request, response);
			}
		}

		return response;
	}

	public static WiAppResponseHandler expireCampaign(String username, String wiCode)
	{

		String request = "<wiapp ver=\"" + apiver + "\"" + " appid=\"" + appId + "\"" + " password=\"" + password + "\"" + " reqid=\"2\">" + "<cvexpirerx>" + "<username>" + username + "</username>"
				+ "<sessionid>" + RuntimeStoreHelper.getSessionID() + "</sessionid>" + "<wicode>" + wiCode + "</wicode>" + "<channelid>COUPON</channelid>" + "</cvexpirerx>" + "</wiapp>";

		WiAppResponseHandler response = new WiAppResponseHandler();

		WiAppServiceRequest.sendRequest(request, response);

		return response;
	}

	public static WiAppWiCodeResponseHandler getWicode(String username)
	{

		String request = "<wiapp ver=\"" + apiver + "\"" + " appid=\"" + appId + "\"" + " password=\"" + password + "\"" + " reqid=\"2\">" + "<cvgetopenwicoderx>" + "<username>" + username
				+ "</username>" + "<sessionid>" + RuntimeStoreHelper.getSessionID() + "</sessionid>" + "<channelid>CHECKERS_BLACKBERRY</channelid>" + "<campaigntype>COUPON</campaigntype>"
				+ "</cvgetopenwicoderx>" + "</wiapp>";

		WiAppWiCodeResponseHandler response = new WiAppWiCodeResponseHandler();

		WiAppServiceRequest.sendRequest(request, response);

		if (response.getResponseCode().equals("042"))
		{
			// session expired login user
			response = new WiAppWiCodeResponseHandler();
			WiAppLoginResponseHandler loginResponse = WiAppServiceEssentials.login(PersistentStoreHelper.getUsername(), PersistentStoreHelper.getPIN());
			// if user logged in successfully ... update session details and resend request
			if (loginResponse.getResponseCode().equalsIgnoreCase("-1"))
			{
				RuntimeStoreHelper.setSessionID(loginResponse.getSessionId().equals("0") ? null : loginResponse.getSessionId());
				// refresh coupons

				request = "<wiapp ver=\"" + apiver + "\"" + " appid=\"" + appId + "\"" + " password=\"" + password + "\"" + " reqid=\"2\">" + "<cvgetopenwicoderx>" + "<username>" + username
						+ "</username>" + "<sessionid>" + RuntimeStoreHelper.getSessionID() + "</sessionid>" + "<channelid>CHECKERS_BLACKBERRY</channelid>" + "<campaigntype>COUPON</campaigntype>"
						+ "</cvgetopenwicoderx>" + "</wiapp>";
				WiAppServiceRequest.sendRequest(request, response);
			}
		}

		return response;
	}
	
	public static WiappCouponCategoriesResponseHandler getSpecialCategories()
	{
		return getCouponCategories("6_SPECIAL");
	}

	public static WiappCouponCategoriesResponseHandler getCouponCategories(String channelId)
	{
		String request = "<wiapp ver=\"" + apiver + "\"" + " appid=\"" + appId + "\"" + " password=\"" + password + "\"" + " reqid=\"2\">" + "<cvcategorylistrx>" + "<channelid>" + channelId
				+ "</channelid>" + "</cvcategorylistrx>" + "</wiapp>";

		WiappCouponCategoriesResponseHandler response = new WiappCouponCategoriesResponseHandler();

		WiAppServiceRequest.sendRequest(request, response);

		return response;

	}

}
