package fi.bb.checkers.helpers;

import java.util.Calendar;
import java.util.Hashtable;

import javax.microedition.io.Connector;
import javax.wireless.messaging.MessageConnection;
import javax.wireless.messaging.TextMessage;

import net.rim.blackberry.api.invoke.Invoke;
import net.rim.blackberry.api.invoke.MessageArguments;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.BorderFactory;

import com.samples.bbm.ForName.BBMInterface.BBMBridge;
import com.samples.bbm.ForName.BBMInterface.BBMBridgeCallback;

import fi.bb.checkers.datatypes.CampaignData;
import fi.bb.checkers.datatypes.CouponCategory;
import fi.bb.checkers.logger.RemoteLogger;
import fi.bb.checkers.prompts.InfoDialog;
import fi.bb.checkers.prompts.SharingDialog;
import fi.bb.checkers.ui.components.AnimatedGIFField;
import fi.bb.checkers.ui.components.LabelField;
import fi.bb.checkers.ui.screens.WebViewScreen;
import fi.bb.checkers.utils.StringUtil;

public class SharingHelper
{
	// http://developer.blackberry.com/bbos/java/documentation/compatibility_chart_1842296_11.html
	private static final String MIN_VERSION = "6.1.0";
	private static boolean hasBBM = false;
	private static BBMBridge bbm_bridge;

	public static void init()
	{
		try
		{
			final int bbmHandle = CodeModuleManager.getModuleHandle("net_rim_bb_qm_peer_lib");
			final int bbmPlatformHandle = CodeModuleManager.getModuleHandle("net_rim_bb_qm_platform");
			if (bbmHandle == 0 || bbmPlatformHandle == 0)
			{
				// BBM not found
				RemoteLogger.log("SharingHelper", "BBM not installed");
			}
			else
			{
				final String version = CodeModuleManager.getModuleVersion(bbmHandle);
				if (compareVersion(version, MIN_VERSION) >= 0)
				{
					// Minimum BBM version reached
					RemoteLogger.log("SharingHelper", "BBM Enabled:\t" + version + " > " + MIN_VERSION);
					bbm_bridge = BBMBridge.startBBM(new BBMBridgeCallback()
					{
						public void onInitialized(boolean success)
						{
							RemoteLogger.log("SharingHelper", "BBMBridgeImpl " + (success ? "successful" : "unsuccessful"));
							hasBBM = success;
						}

						public void exitApp()
						{
							BBMBridge.stopBBM();
						}
					});
					/*if (bbm_bridge.isConnected() == true)
					{
						RemoteLogger.log("SharingHelper", "BBMBridgeImpl bbm_bridge.isConnected() successful");
						hasBBM = true;
					}
					else
					{
						RemoteLogger.log("SharingHelper", "BBMBridgeImpl bbm_bridge.isConnected() unsuccessful");
						hasBBM = false;
					}*/
				}
				else
				{
					// Minimum BBM version not reached
					RemoteLogger.log("SharingHelper", "BBM min version NOT reached:\t" + version + " < " + MIN_VERSION);
				}
			}
		} catch (Exception e)
		{
		}
	}

	private static int compareVersion(String v1, String v2)
	{
		String[] tokens1 = StringUtil.split(v1, ".");
		String[] tokens2 = StringUtil.split(v2, ".");

		int length = Math.max(tokens1.length, tokens2.length);
		for (int i = 0; i < length; i++)
		{
			int int1 = tokens1.length > i ? Integer.parseInt(tokens1[i]) : 0;
			int int2 = tokens2.length > i ? Integer.parseInt(tokens2[i]) : 0;

			if (int1 > int2) return 1;
			if (int1 < int2) return -1;
		}

		return 0;
	}

	public static void shareCoupon(CampaignData coupon)
	{		
		String priceValueText = "";

		try
		{
			double priceValue = Double.parseDouble(coupon.getValue())/100.0;
			priceValueText = StringHelper.currency_symbol + FormatHelper.getPriceFormattedHome(priceValue);
		}
		catch (Exception e) 
		{
			priceValueText = coupon.getValue();
		}

		int choice = SharingDialog.doModal();

		switch (choice)
		{
			case SharingDialog.SMS :
				logShareCoupon("Message", coupon);
				//if (RuntimeStoreHelper.getSessionID() == null)
				//sms("Check out this great EeziCoupon from Checkers! Save " + priceValueText + " on " + coupon.getName()
				//			+ ". Get great savings, quality and service at Checkers. http://m.checkers.co.za..");
				sms(StringHelper.share_coupon_sms_1 + priceValueText  + StringHelper.share_coupon_sms_2 + coupon.getName() + StringHelper.share_coupon_sms_3);
				
				/*else
					sms(RuntimeStoreHelper.getUserData().getFirstname() + " wants you to check out this great EeziCoupon from Checkers! Save " + priceValueText + " on " + coupon.getName()
							+ ". Get great savings, quality and service at Checkers. http://m.checkers.co.za.");*/
				break;
	
			case SharingDialog.EMAIL :
				logShareCoupon("Email", coupon);
				if (RuntimeStoreHelper.getSessionID() == null)
				{
					email(StringHelper.share_coupon_email_1 + priceValueText + " on " + coupon.getName() + StringHelper.share_coupon_email_3);
					
					//email("I want you to check out this great EeziCoupon saving from Checkers. Save " + priceValueText + " on " + coupon.getName()
					//		+ ". Get great savings, quality and service at Checkers. http://m.checkers.co.za.");
				}
				else
				{
					email(RuntimeStoreHelper.getUserData().getFirstname() + StringHelper.share_coupon_email_loggedin_1 + priceValueText + " on "
									+ coupon.getName() + StringHelper.share_coupon_email_loggedin_3);
							
					//email(RuntimeStoreHelper.getUserData().getFirstname() + " wants you to check out this great EeziCoupon saving from Checkers. Save " + priceValueText + " on "
					//		+ coupon.getName() + ". Get great savings, quality and service at Checkers. http://m.checkers.co.za.");
				}
				break;
	
			case SharingDialog.BBM :
				logShareCoupon("BBM", coupon);
				bbm(StringHelper.share_coupon_bbm_1+ priceValueText + " on " + coupon.getName() + StringHelper.share_coupon_bbm_3);
				//bbm("Check out this great EeziCoupon from Checkers! Save " + priceValueText + " on " + coupon.getName() + ". Get great savings, quality and service at Checkers. http://m.checkers.co.za");
				break;
	
			case SharingDialog.FACEBOOK :
				logShareCoupon("Facebook", coupon);
				facebook(StringHelper.share_coupon_fb_1 + priceValueText + " on " + coupon.getName() + StringHelper.share_coupon_fb_3);
				//facebook("Get this great EeziCoupon from Checkers! Save " + priceValueText + " on " + coupon.getName() + ". Get great savings, quality and service at Checkers. http://m.checkers.co.za");
				break;
	
			case SharingDialog.TWITTER :
				logShareCoupon("Twitter", coupon);				
				twitter(StringHelper.share_coupon_tw_1 + priceValueText + " on " + coupon.getName() + StringHelper.share_coupon_tw_3);
				//twitter("Save " + priceValueText + " on " + coupon.getName() + " at Checkers. http://m.checkers.co.za");
				break;
		}
	}

	public static void shareSpecial(CampaignData special)
	{		
		String priceValueText = "";

		try
		{
			double priceValue = Double.parseDouble(special.getValue())/100.0;
			priceValueText = StringHelper.currency_symbol + FormatHelper.getPriceFormattedHome(priceValue);
		}
		catch (Exception e) 
		{
			priceValueText = special.getValue();
		}
		
		int choice = SharingDialog.doModal();
		switch (choice)
		{
		case SharingDialog.SMS :
			logShareSpecial("Message");
			//if (RuntimeStoreHelper.getSessionID() == null)
			
			sms(StringHelper.share_special_sms_1 + special.getName() + StringHelper.share_special_sms_2 + priceValueText
								+ StringHelper.share_special_sms_3);
					
			//sms("Check out this great deal from Checkers! " + special.getName() + " on special for " + priceValueText
			//			+ ". Get great savings, quality and service at Checkers. http://m.checkers.co.za.");
			/*else
				sms(RuntimeStoreHelper.getUserData().getFirstname() + " recommends this great deal from Checkers. " + special.getName() + " on special for " + priceValueText
						+ ". Get great savings, quality and service at Checkers. http://m.checkers.co.za.");*/
			break;

		case SharingDialog.EMAIL :
			logShareSpecial("Email");
			if (RuntimeStoreHelper.getSessionID() == null)
			{
				email(StringHelper.share_special_email_1 + special.getName() + StringHelper.share_special_email_2 + priceValueText
								+ StringHelper.share_special_email_3);
						
				//email("Check out this great deal from Checkers! " + special.getName() + " on special for " + priceValueText
				//		+ ". Get great savings, quality and service at Checkers. http://m.checkers.co.za.");
			}
			else
			{
				email(RuntimeStoreHelper.getUserData().getFirstname() + StringHelper.share_special_email_loggedin_1 + special.getName() + StringHelper.share_special_email_loggedin_2
								+ priceValueText + StringHelper.share_special_email_loggedin_3);
				//email(RuntimeStoreHelper.getUserData().getFirstname() + " wants you to check out this great deal from Checkers. " + special.getName() + " on special for "
				//		+ priceValueText + ". Get great savings, quality and service at Checkers. http://m.checkers.co.za.");
			}
			break;

		case SharingDialog.BBM :
			logShareSpecial("BBM");
			//bbm("Check out this great deal from Checkers! " + special.getName() + " on special for " + priceValueText
			//		+ ". Get great savings, quality and service at Checkers. http://m.checkers.co.za");
			bbm(StringHelper.share_special_bbm_1 + special.getName() + StringHelper.share_special_bbm_2 + priceValueText
							+ StringHelper.share_special_bbm_3);
					
			break;

		case SharingDialog.FACEBOOK :
			logShareSpecial("Facebook");
			facebook(StringHelper.share_special_bbm_1 + special.getName() + StringHelper.share_special_bbm_2 + priceValueText
					+ StringHelper.share_special_bbm_3);
			break;

		case SharingDialog.TWITTER :
			logShareSpecial("Twitter");
			twitter(special.getName() + StringHelper.share_special_tw_1 + priceValueText + StringHelper.share_special_tw_2);
			//twitter(special.getName() + " on special for " + priceValueText + " at Checkers. http://m.checkers.co.za");
			break;
		}
	}

	public static void shareApp()
	{
		int choice = SharingDialog.doModal();
		switch (choice)
		{
		case SharingDialog.SMS :
			logShareApp("Message");
			sms(StringHelper.share_app_sms_1);
			//sms("I'm using the Checkers Mobile App. It's free and you can get great savings from Checkers and Checkers Hyper stores. Check it out! http://m.checkers.co.za");
			break;

		case SharingDialog.EMAIL :
			logShareApp("Email");
			if (RuntimeStoreHelper.getSessionID() == null)
			{
				email(StringHelper.share_app_email_1);
				//email("I'm using the Checkers Mobile App. It's free and you can get great savings from Checkers and Checkers Hyper stores. Check it out! http://m.checkers.co.za");
			}
			else
			{
				email(RuntimeStoreHelper.getUserData().getFirstname()+ StringHelper.share_app_email_loggedin_1);
				//email(RuntimeStoreHelper.getUserData().getFirstname()+ " is using the Checkers Mobile App. It's free and you can get great savings from Checkers and Checkers Hyper stores. Check it out! http://m.checkers.co.za");
			}
			break;

		case SharingDialog.BBM :
			logShareApp("BBM");
			//bbm("I'm using the Checkers Mobile App. It's free and you can get great savings from Checkers and Checkers Hyper stores. Check it out! http://m.checkers.co.za");
			bbm(StringHelper.share_app_bbm_1);
			break;

		case SharingDialog.FACEBOOK :
			logShareApp("Facebook");
			facebook(StringHelper.share_app_fb_1);
			//facebook("I'm using the Checkers Mobile App. It's free and you can get great savings from Checkers and Checkers Hyper stores. Check it out! http://m.checkers.co.za");
			break;

		case SharingDialog.TWITTER :
			logShareApp("Twitter");
			//twitter("I'm using the Checkers Mobile App. It's free and you can get great savings. Check it out! http://m.checkers.co.za");
			twitter(StringHelper.share_app_tw_1);
			break;
		}
	}
	// ------------------- sms ---------------------
	private static void sms(String message)
	{
		try
		{
			MessageConnection mc = (MessageConnection) Connector.open("sms://");
			TextMessage textMessage = (TextMessage) mc.newMessage(MessageConnection.TEXT_MESSAGE);
			textMessage.setPayloadText(message);
			textMessage.setAddress("sms://");
			Invoke.invokeApplication(Invoke.APP_TYPE_MESSAGES, new MessageArguments(textMessage));

		} catch (Exception e)
		{
			InfoDialog.doModal("Error", e.getMessage(), "Okay");
		}
	}

	// ------------------- email ---------------------
	private static void email(String message)
	{
		try
		{
			Invoke.invokeApplication(Invoke.APP_TYPE_MESSAGES, new MessageArguments(MessageArguments.ARG_NEW, "", "", message + "\n"));
		} catch (Exception e)
		{
			InfoDialog.doModal("Error", e.getMessage(), "Okay");
		}
	}

	// ------------------ facebook -------------------
	private static boolean facebook(String message)
	{
		String url = StringHelper.fb_app_url + encode(message);
		RemoteLogger.log("FB_URL", url);
		try
		{
			pushURLScreen(url);
			return true;
		}
		catch (Exception e)
		{
			return false;
		}	
		
		/*FacebookInterface facebook = new FacebookInterface("102994653123713");//("1420743434866348");
		String[] permissions = new String[]{FacebookInterface.PERMISSION_PUBLISH};
		facebook.setPermissions(permissions);
		facebook.setLoadingField(createLoadingField("Connecting to Facebook..."));

		try
		{
			facebook.publishStatus(message);
			return true;
		} catch (Exception e)
		{
			RemoteLogger.log("SHARE_SOCIAL", "facebook fail: e: " + e.getMessage());
			return false;
		}*/
	}
	
	private static void pushURLScreen(String url)
	{
		WebViewScreen screen = new WebViewScreen(url, false);
		UiApplication.getUiApplication().pushScreen(screen);
	}
	
	// http://www.w3.org/International/URLUTF8Encoder.java
		final static String[] hex = {"%00", "%01", "%02", "%03", "%04", "%05", "%06", "%07", "%08", "%09", "%0a", "%0b", "%0c", "%0d", "%0e", "%0f", "%10", "%11", "%12", "%13", "%14", "%15", "%16", "%17",
				"%18", "%19", "%1a", "%1b", "%1c", "%1d", "%1e", "%1f", "%20", "%21", "%22", "%23", "%24", "%25", "%26", "%27", "%28", "%29", "%2a", "%2b", "%2c", "%2d", "%2e", "%2f", "%30", "%31",
				"%32", "%33", "%34", "%35", "%36", "%37", "%38", "%39", "%3a", "%3b", "%3c", "%3d", "%3e", "%3f", "%40", "%41", "%42", "%43", "%44", "%45", "%46", "%47", "%48", "%49", "%4a", "%4b",
				"%4c", "%4d", "%4e", "%4f", "%50", "%51", "%52", "%53", "%54", "%55", "%56", "%57", "%58", "%59", "%5a", "%5b", "%5c", "%5d", "%5e", "%5f", "%60", "%61", "%62", "%63", "%64", "%65",
				"%66", "%67", "%68", "%69", "%6a", "%6b", "%6c", "%6d", "%6e", "%6f", "%70", "%71", "%72", "%73", "%74", "%75", "%76", "%77", "%78", "%79", "%7a", "%7b", "%7c", "%7d", "%7e", "%7f",
				"%80", "%81", "%82", "%83", "%84", "%85", "%86", "%87", "%88", "%89", "%8a", "%8b", "%8c", "%8d", "%8e", "%8f", "%90", "%91", "%92", "%93", "%94", "%95", "%96", "%97", "%98", "%99",
				"%9a", "%9b", "%9c", "%9d", "%9e", "%9f", "%a0", "%a1", "%a2", "%a3", "%a4", "%a5", "%a6", "%a7", "%a8", "%a9", "%aa", "%ab", "%ac", "%ad", "%ae", "%af", "%b0", "%b1", "%b2", "%b3",
				"%b4", "%b5", "%b6", "%b7", "%b8", "%b9", "%ba", "%bb", "%bc", "%bd", "%be", "%bf", "%c0", "%c1", "%c2", "%c3", "%c4", "%c5", "%c6", "%c7", "%c8", "%c9", "%ca", "%cb", "%cc", "%cd",
				"%ce", "%cf", "%d0", "%d1", "%d2", "%d3", "%d4", "%d5", "%d6", "%d7", "%d8", "%d9", "%da", "%db", "%dc", "%dd", "%de", "%df", "%e0", "%e1", "%e2", "%e3", "%e4", "%e5", "%e6", "%e7",
				"%e8", "%e9", "%ea", "%eb", "%ec", "%ed", "%ee", "%ef", "%f0", "%f1", "%f2", "%f3", "%f4", "%f5", "%f6", "%f7", "%f8", "%f9", "%fa", "%fb", "%fc", "%fd", "%fe", "%ff"};
	
	/**
	 * Encode a string to the "x-www-form-urlencoded" form, enhanced with the UTF-8-in-URL proposal. This is what happens:
	 * 
	 * <ul>
	 * <li>
	 * <p>
	 * The ASCII characters 'a' through 'z', 'A' through 'Z', and '0' through '9' remain the same.
	 * 
	 * <li>
	 * <p>
	 * The unreserved characters - _ . ! ~ * ' ( ) remain the same.
	 * 
	 * <li>
	 * <p>
	 * The space character ' ' is converted into a plus sign '+'.
	 * 
	 * <li>
	 * <p>
	 * All other ASCII characters are converted into the 3-character string "%xy", where xy is the two-digit hexadecimal representation of the character code
	 * 
	 * <li>
	 * <p>
	 * All non-ASCII characters are encoded in two steps: first to a sequence of 2 or 3 bytes, using the UTF-8 algorithm; secondly each of these bytes is encoded as "%xx".
	 * </ul>
	 * 
	 * @param s
	 *            The string to be encoded
	 * @return The encoded string
	 */
	private static String encode(String s)
	{
		StringBuffer sbuf = new StringBuffer();
		int len = s.length();
		for (int i = 0; i < len; i++)
		{
			int ch = s.charAt(i);
			if ('A' <= ch && ch <= 'Z')
			{ // 'A'..'Z'
				sbuf.append((char) ch);
			}
			else if ('a' <= ch && ch <= 'z')
			{ // 'a'..'z'
				sbuf.append((char) ch);
			}
			else if ('0' <= ch && ch <= '9')
			{ // '0'..'9'
				sbuf.append((char) ch);
			}
			else if (ch == ' ')
			{ // space
				sbuf.append('+');
			}
			else if (ch == '-' || ch == '_' // unreserved
					|| ch == '.' || ch == '!' || ch == '~' || ch == '*' || ch == '\'' || ch == '(' || ch == ')')
			{
				sbuf.append((char) ch);
			}
			else if (ch <= 0x007f)
			{ // other ASCII
				sbuf.append(String.valueOf(hex[ch]).toUpperCase());
			}
			else if (ch <= 0x07FF)
			{ // non-ASCII <= 0x7FF
				sbuf.append(String.valueOf(hex[0xc0 | (ch >> 6)]).toUpperCase());
				sbuf.append(String.valueOf(hex[0x80 | (ch & 0x3F)]).toUpperCase());
			}
			else
			{ // 0x7FF < ch <= 0xFFFF
				sbuf.append(String.valueOf(hex[0xe0 | (ch >> 12)]).toUpperCase());
				sbuf.append(String.valueOf(hex[0x80 | ((ch >> 6) & 0x3F)]).toUpperCase());
				sbuf.append(String.valueOf(hex[0x80 | (ch & 0x3F)]).toUpperCase());
			}
		}
		return sbuf.toString();
	}
	
	// ---------------- twitter -------------------
	private static boolean twitter(String message)
	{
		
		String url = "http://twitter.com/home?status="+encode(message);
		RemoteLogger.log("TWEET_URL", url);
		try
		{
			pushURLScreen(url);
			return true;
		}
		catch (Exception e)
		{
			return false;
		}	
		
		/*TwitterInterface twitter = new TwitterInterface("U7Rmy7LHP4LCmcO3j5GoJRpMW", "HjJV6xbx0L2lNrYNJoUT2zQwVcDXSltJ9tYLacgHg6OPNwdyoh");//("gAN5b9c2d3kydmUvXzPs2snEg", "AUa9GI7xGqwm095rPz5VcoJ9sLRa04MiFZInh8o9ExY6Xr9OVs");//("4vehnVZEpe9gBe2UPmhyOKVZp", "ttfwezslggNLhlFMTsdiR8rFmBLMUNMAXnnxs6qjtXtwErJdEd");
		twitter.setLoadingField(createLoadingField("Connecting to Twitter..."));
		try
		{
			twitter.tweet(message);
			return true;
		} catch (Exception e)
		{
			RemoteLogger.log("SHARE_SOCIAL", "twitter fail: e: " + e.getMessage());
			return false;
		}*/
	}

	// ---------------- bbm -------------------
	private static void bbm(String message)
	{
		bbm_bridge.sendMessage(message);
	}

	public static boolean hasBBM()
	{
		return hasBBM;
	}

	private static Field createLoadingField(String text)
	{
		VerticalFieldManager loading_field = new VerticalFieldManager()
		{
			public int getPreferredHeight()
			{
				return super.getPreferredHeight() + ResourceHelper.convert(50); // GIFField doesn't seem to get the preferred height correctly, so just hardcoding
			}
		};

		LabelField titleField = new LabelField(text, ResourceHelper.color_primary, Field.FIELD_HCENTER | DrawStyle.HCENTER);
		titleField.setFont(Font.getDefault().derive(Font.BOLD, 8, Ui.UNITS_pt));
		titleField.setMargin(0, 0, 10, 0);

		loading_field.add(titleField);

		loading_field.add(new AnimatedGIFField("loading.gif", Field.FIELD_HCENTER));

		Bitmap borderBitmap = Bitmap.getBitmapResource("rounded-border.png");
		loading_field.setBorder(BorderFactory.createBitmapBorder(new XYEdges(12, 12, 12, 12), borderBitmap));

		return loading_field;
	}

	private static void logShareCoupon(String channel, CampaignData coupon)
	{
		Hashtable eventParams = new Hashtable();

		eventParams.put(FlurryHelper.PARAM_COUPON_MONETARY_VALUE, coupon.getValue());
		eventParams.put(FlurryHelper.PARAM_CATEGORY, ((CouponCategory)coupon.getCategoryList().elementAt(0)).getName());
		eventParams.put(FlurryHelper.PARAM_CHANNEL, channel);
		FlurryHelper.addProvinceParam(eventParams);
		FlurryHelper.addLoginParams(eventParams);
		eventParams.put(FlurryHelper.PARAM_TIMESTAMP, FlurryHelper.getFlurryFormatDate(Calendar.getInstance()));

		FlurryHelper.logEvent(FlurryHelper.EVENT_SHARE_EEZI_COUPON, eventParams, false);
	}

	private static void logShareSpecial(String channel)
	{
		Hashtable eventParams = new Hashtable();

		eventParams.put(FlurryHelper.PARAM_CHANNEL, channel);
		FlurryHelper.addProvinceParam(eventParams);
		FlurryHelper.addLoginParams(eventParams);
		eventParams.put(FlurryHelper.PARAM_TIMESTAMP, FlurryHelper.getFlurryFormatDate(Calendar.getInstance()));

		FlurryHelper.logEvent(FlurryHelper.EVENT_SHARE_SPECIAL, eventParams, false);
	}

	private static void logShareApp(String channel)
	{
		Hashtable eventParams = new Hashtable();

		eventParams.put(FlurryHelper.PARAM_CHANNEL, channel);
		FlurryHelper.addProvinceParam(eventParams);
		FlurryHelper.addLoginParams(eventParams);
		eventParams.put(FlurryHelper.PARAM_TIMESTAMP, FlurryHelper.getFlurryFormatDate(Calendar.getInstance()));
		FlurryHelper.addFirstLaunchParam(eventParams);

		FlurryHelper.logEvent(FlurryHelper.EVENT_SHARE_APP, eventParams, false);
	}
}