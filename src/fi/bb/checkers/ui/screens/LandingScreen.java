package fi.bb.checkers.ui.screens;

import java.util.Calendar;
import java.util.Hashtable;

import net.rim.device.api.system.Characters;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.BackgroundFactory;
import fi.bb.checkers.MainApplication;
import fi.bb.checkers.datatypes.UserData;
import fi.bb.checkers.helpers.FlurryHelper;
import fi.bb.checkers.helpers.LocationHelper;
import fi.bb.checkers.helpers.ResourceHelper;
import fi.bb.checkers.prompts.LoadingDialog;
import fi.bb.checkers.ui.components.TextImageButton;

public class LandingScreen extends MainScreen implements FieldChangeListener
{

	MainApplication app;

	TextImageButton loginButton;
	TextImageButton registerButton;
	TextImageButton couponsButton;
	LoadingDialog loading;

	public LandingScreen()
	{
		super(NO_VERTICAL_SCROLL | NO_HORIZONTAL_SCROLL);
		app = (MainApplication) UiApplication.getUiApplication();

		getMainManager().setBackground(BackgroundFactory.createSolidBackground(ResourceHelper.color_background_app));
		
		/*Bitmap logoImage = ResourceHelper.getImage("logo_welcome");
		int imageWidth = Display.getWidth() - ResourceHelper.convert(55);
		int imageHeight = (int) ((double) imageWidth * (double) logoImage.getHeight() / logoImage.getWidth());

		logoImage = BitmapTools.resizeTransparentBitmap(logoImage, imageWidth, imageHeight, Bitmap.FILTER_BILINEAR, Bitmap.SCALE_TO_FIT);*/
		
		BitmapField logoField = new BitmapField(ResourceHelper.getImage("logo_welcome"), Field.FOCUSABLE | Field.FIELD_HCENTER)
		{
			protected void drawFocus(Graphics graphics, boolean on)
			{
			}

			protected boolean navigationMovement(int dx, int dy, int status, int time)
			{
				if (dy < 0)
				{
					loginButton.setFocus();
					return true;
				}
				return super.navigationMovement(dx, dy, status, time);
			}

			protected boolean navigationClick(int status, int time)
			{
				return true;
			}
		};
		logoField.setPadding(ResourceHelper.convert(21), 0, ResourceHelper.convert(32), 0);

		registerButton = new TextImageButton("Sign up now", "btn_default", "btn_hover")
		{
			protected boolean navigationMovement(int dx, int dy, int status, int time)
			{
				if (dy < 0) return true; // don't select the logo image
				return super.navigationMovement(dx, dy, status, time);
			}
		};
		couponsButton = new TextImageButton("Sign up later", "btn_grey_default", "btn_grey_hover");
		loginButton = new TextImageButton("Login", "btn_grey_default", "btn_grey_hover");

		registerButton.setTextColor(ResourceHelper.color_white);
		registerButton.setTextColorHover(ResourceHelper.color_primary);
		registerButton.setTextColorPressed(ResourceHelper.color_primary);

		couponsButton.setTextColor(ResourceHelper.color_primary);
		couponsButton.setTextColorHover(ResourceHelper.color_primary);
		couponsButton.setTextColorPressed(ResourceHelper.color_primary);

		loginButton.setTextColor(ResourceHelper.color_primary);
		loginButton.setTextColorHover(ResourceHelper.color_primary);
		loginButton.setTextColorPressed(ResourceHelper.color_primary);

		couponsButton.setChangeListener(this);
		registerButton.setChangeListener(this);
		loginButton.setChangeListener(this);

		couponsButton.setMargin(ResourceHelper.convert(4), 0, 0, 0);
		loginButton.setMargin(ResourceHelper.convert(4), 0, 0, 0);

		VerticalFieldManager buttonManager = new VerticalFieldManager(FIELD_HCENTER | FIELD_VCENTER);
		buttonManager.add(registerButton);
		buttonManager.add(couponsButton);
		buttonManager.add(loginButton);

		if (Display.getWidth() == 360)
		{
			// portrait screen
			int margin = (Display.getHeight() - logoField.getPreferredHeight() - buttonManager.getPreferredHeight()) / 2;
			buttonManager.setMargin(margin, 0, 0, 0);
		}

		add(logoField);
		add(buttonManager);

		if (LocationHelper.isGPSEnabled()) LocationHelper.determineLocation();
	}

	public void fieldChanged(Field field, int context)
	{
		if (field == couponsButton)
		{
			logUserTapsSignUpLater();
			ViewPagerScreen.push();
		}
		else if (field == registerButton)
		{			
			logUserTappedSignUp();
			app.slideScreen(new RegistrationScreen(0, new UserData()));
		}
		else if (field == loginButton)
		{
			app.slideScreen(new LoginScreen());
		}
	}

	private void logUserTappedSignUp()
	{
		//first event starts register process
		Hashtable eventParams = new Hashtable();

		eventParams.put(FlurryHelper.PARAM_SIGNUP_REGISTER, "1");		
		eventParams.put(FlurryHelper.PARAM_TIMESTAMP, FlurryHelper.getFlurryFormatDate(Calendar.getInstance()));

		FlurryHelper.logEvent(FlurryHelper.EVENT_REGISTRATION, eventParams, true);

		//second event user taps signup
		eventParams = new Hashtable();

		eventParams.put(FlurryHelper.PARAM_TAPPED, "1");
		FlurryHelper.addProvinceParam(eventParams);
		eventParams.put(FlurryHelper.PARAM_TIMESTAMP, FlurryHelper.getFlurryFormatDate(Calendar.getInstance()));

		FlurryHelper.logEvent(FlurryHelper.EVENT_SIGN_UP, eventParams, false);
	}

	private void logUserTapsSignUpLater()
	{
		Hashtable eventParams = new Hashtable();
		eventParams.put(FlurryHelper.PARAM_TIMESTAMP, FlurryHelper.getFlurryFormatDate(Calendar.getInstance()));
	
//		FlurryHelper.logEvent(FlurryHelper.EVENT_VIEW_COUPONS, eventParams, false);
		FlurryHelper.logEvent(FlurryHelper.EVENT_WELCOME_SIGN_UP_LATER, eventParams, false);
	}

	protected boolean onSavePrompt()
	{
		return true;
	}

	protected boolean keyChar(char c, int status, int time)
	{
		if (c == Characters.ESCAPE)
		{
			/*try
			{
				BBMBridge.stopBBM();
				RemoteLogger.log("STOP_BBM", "landingscreen YAY");
			}
			catch (Exception e)
			{
				RemoteLogger.log("STOP_BBM", "landingscreen failed: e: " + e.getMessage());
			}*/
			
			System.exit(0);
			return true;
		}
		return super.keyChar(c, status, time);
	}
}