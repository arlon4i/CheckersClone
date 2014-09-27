package fi.bb.checkers.ui.screens;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.BackgroundFactory;
import net.rim.device.api.ui.text.TextFilter;

import com.wigroup.wiAppService.WiAppServiceEssentials;
import com.wigroup.wiAppService.responsehandlers.WiAppResponseHandler;

import fi.bb.checkers.MainApplication;
import fi.bb.checkers.datatypes.UserData;
import fi.bb.checkers.helpers.FlurryHelper;
import fi.bb.checkers.helpers.PersistentStoreHelper;
import fi.bb.checkers.helpers.ResourceHelper;
import fi.bb.checkers.helpers.RuntimeStoreHelper;
import fi.bb.checkers.helpers.ServerHelper;
import fi.bb.checkers.helpers.StringHelper;
import fi.bb.checkers.logger.RemoteLogger;
import fi.bb.checkers.prompts.InfoDialog;
import fi.bb.checkers.prompts.LoadingDialog;
import fi.bb.checkers.ui.components.Actionbar;
import fi.bb.checkers.ui.components.ColorButtonField;
import fi.bb.checkers.ui.components.HyperlinkButton;
import fi.bb.checkers.ui.components.InputItemContainer;
import fi.bb.checkers.ui.components.LabelField;
import fi.bb.checkers.ui.components.PinField;
import fi.bb.checkers.ui.components.TextImageButton;
import fi.bb.checkers.ui.components.TextInputField;
import fi.bb.checkers.utils.AsyncTask;

public class AccountValidationScreen extends MainScreen implements FieldChangeListener
{
	MainApplication app;
	PinField pin_field;
	ColorButtonField validateButton;
	ColorButtonField resendHyperlink;
	UserData user;
	
	private int buttonHeight = ResourceHelper.convert(25);
	
	public AccountValidationScreen(UserData user)
	{
		super(NO_SYSTEM_MENU_ITEMS);
		this.user = user;
		app = (MainApplication) UiApplication.getUiApplication();

		VerticalFieldManager mainManager = (VerticalFieldManager) getMainManager();
		mainManager.setBackground(BackgroundFactory.createSolidBackground(0xe9e9e9));
		setBanner(new Actionbar("Confirmation Code", false, false));

		// prepopulated cell number
		TextInputField cellNumberField = new TextInputField("", false, false, ResourceHelper.color_checkers_teal, 0);
		cellNumberField.setDisabledColor(ResourceHelper.color_checkers_teal);
		cellNumberField.setTextFont(ResourceHelper.helveticaLight().getFont(Font.BOLD, ResourceHelper.convert(17), Ui.UNITS_px));
		cellNumberField.setText(user.getCellphone());
		cellNumberField.setFilter(TextFilter.get(TextFilter.NUMERIC));
		cellNumberField.setMaxSize(10);
		cellNumberField.setEditable(false);
		cellNumberField.setFocusable(false);
		mainManager.add(new InputItemContainer("Sent to:", cellNumberField, true));

		//mainManager.add(new InputItemContainer("Enter your 4-digit Checkers App PIN", null, true));
		
		LabelField loginText = new LabelField(StringHelper.acc_validation_message, ResourceHelper.color_black, 0, ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(17), Ui.UNITS_px));
		loginText.setMargin(ResourceHelper.convert(5), 0, 0, ResourceHelper.convert(5));
		mainManager.add(loginText);
		mainManager.add(getSeperator());
		
		pin_field = new PinField();
		mainManager.add(new InputItemContainer(null, pin_field));

		// validate button
		
		validateButton = new ColorButtonField(ResourceHelper.color_checkers_teal, ResourceHelper.color_white, ResourceHelper.convert(100),  buttonHeight);
		validateButton.setButtonOutlineStates(ResourceHelper.color_checkers_teal, ResourceHelper.color_checkers_teal, ResourceHelper.color_checkers_teal);
		validateButton.setButtonText("Enter");
		validateButton.setTextStates(ResourceHelper.color_white, ResourceHelper.color_checkers_teal, ResourceHelper.color_checkers_teal);
		validateButton.setTextFont(ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(17), Ui.UNITS_px));
		
		/*validateButton= new TextImageButton("Enter", "btn_sml_default", "btn_sml_hover");
		validateButton.setTextColor(ResourceHelper.color_white);
		validateButton.setTextColorHover(ResourceHelper.color_primary);
		validateButton.setTextColorPressed(ResourceHelper.color_primary);*/
		validateButton.setChangeListener(this);
		validateButton.setMargin(10, 0, 0, 10);

		// resend password
		/*resendHyperlink = new TextImageButton("Resend Confirmation Code", "btn_grey_default", "btn_grey_hover");
		resendHyperlink.setTextColor(ResourceHelper.color_primary);
		resendHyperlink.setTextColorHover(ResourceHelper.color_primary);
		resendHyperlink.setTextColorPressed(ResourceHelper.color_primary);*/
		
		resendHyperlink = new ColorButtonField(ResourceHelper.color_checkers_teal, ResourceHelper.color_white, ResourceHelper.convert(215),  buttonHeight);
		resendHyperlink.setButtonOutlineStates(ResourceHelper.color_checkers_teal, ResourceHelper.color_checkers_teal, ResourceHelper.color_checkers_teal);
		resendHyperlink.setButtonText("Resend Confirmation Code");
		resendHyperlink.setTextStates(ResourceHelper.color_white, ResourceHelper.color_checkers_teal, ResourceHelper.color_checkers_teal);
		resendHyperlink.setTextFont(ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(17), Ui.UNITS_px));
		
		//resendHyperlink = new HyperlinkButton("Resend Confirmation Code", ResourceHelper.convert(17));
		resendHyperlink.setMargin(ResourceHelper.convert(10), 0, ResourceHelper.convert(10), ResourceHelper.convert(10));
		resendHyperlink.setChangeListener(this);

		mainManager.add(validateButton);
		mainManager.add(resendHyperlink);
	}

	private VerticalFieldManager getSeperator()
	{
		VerticalFieldManager vfm = new VerticalFieldManager()
		{
			protected void sublayout(int maxWidth, int maxHeight) 
			{
				int width = Display.getWidth();
				int height = 1;
				
				super.sublayout(width, height);
				setExtent(width, height);
			};
		};
		vfm.setBackground(BackgroundFactory.createSolidBackground(ResourceHelper.color_grey));
		vfm.setMargin(ResourceHelper.convert(10), 0, 0, 0);
		
		return vfm;
	}
	
	/**
	 * 
	 */
	public void fieldChanged(Field field, int context)
	{
		if (field == validateButton)
		{

			if (pin_field.getPIN().length() != 4)
			{
				InfoDialog.doModal("", "To Validate your Account, enter your Confirmation Code which we have SMS'd to you.", "Okay");
				return;
			}
			doSignIn();

		}
		else if (field == resendHyperlink)
		{
			// send reset pin request
			new ResetTask().execute(null);
		}
	}

	private void doSignIn()
	{
		Thread test = new Thread()
		{
			LoadingDialog loading;
			boolean interrupted = false;
			public void run()
			{
				loading = LoadingDialog.push(StringHelper.validating_account_text);
				user.setPin(pin_field.getPIN());
				try
				{
					ServerHelper.login(user.getUsername(), pin_field.getPIN());
					ServerHelper.getUserDetails();
					ServerHelper.getCouponList(PersistentStoreHelper.getSpecialsRegion().getId());

					synchronized (Application.getEventLock())
					{

						FlurryHelper.logEvent( FlurryHelper.EVENT_REGISTRATION_CONFIRMED, null, false);

						ViewPagerScreen.push();
						FlurryHelper.endTimedEvent(FlurryHelper.EVENT_REGISTRATION);

						/*String detail = "Thank you for signing up to the Checkers Mobile App.";//was Registration successful
						String button = "Okay";//Next
						InfoDialog.doModal("", detail, button);*/
					}
				} catch (Exception e)//was ioexception
				{
					RuntimeStoreHelper.setSessionID(null);
					PersistentStoreHelper.setPIN("");
					try
					{
						loading.close();
					}
					catch (Exception e2)
					{
					}
					if ((e.getMessage() != null) && (e.getMessage().toLowerCase().indexOf("interrupt") == -1) && (interrupted == false))
					{
						InfoDialog.doModal("Info", e.getMessage(), "Okay");
					}
				} finally
				{
					synchronized (Application.getEventLock())
					{
						try
						{
							loading.close();
						}
						catch (Exception e)
						{					
						}
					}	
				}
			}

			public void interrupt() {
				super.interrupt();
				interrupted = true;
				try
				{
					synchronized (Application.getEventLock())
					{
						loading.close();
					}			
				}
				catch (Exception e)
				{					
				}
				finally
				{
					synchronized (Application.getEventLock())
					{
						RuntimeStoreHelper.setSessionID(null);
						PersistentStoreHelper.setPIN("");
						//InfoDialog.doModal("Oops!", "Something went wrong.\nYou can try and log in with your PIN received.", "Okay");

						Screen screen;

						while (UiApplication.getUiApplication().getScreenCount() > 1)
						{
							screen = UiApplication.getUiApplication().getActiveScreen();
							UiApplication.getUiApplication().popScreen(screen);	
						}

						UiApplication.getUiApplication().pushScreen(new LoginScreen());
					}
				}
			}
		};
		test.start();
	}

	/**
	 * 
	 */
	public boolean onSavePrompt()
	{
		return true;
	}

	private class ResetTask extends AsyncTask
	{
		private LoadingDialog loading;

		protected void onPreExecute()
		{
			loading = LoadingDialog.push(StringHelper.resending_code_message);
		}

		protected void onPostExecute(Object result)
		{
			loading.close();

			if (result instanceof WiAppResponseHandler)
			{
				WiAppResponseHandler response = (WiAppResponseHandler) result;
				if (response.getResponseCode().equalsIgnoreCase("-1"))
				{
					InfoDialog.doModal("",StringHelper.code_help_message, "Okay");
				}
				else
				{
					InfoDialog.doModal("Error", response.getResponseMessage(), "Okay");
				}
			}

			else
			{
				InfoDialog.doModal("Error", ((Exception) result).getMessage(), "Okay");
			}
		}

		public Object doInBackground(Object[] params)
		{
			WiAppResponseHandler response;
			try
			{
				response = WiAppServiceEssentials.resetPin(user.getCellphone());
			} catch (Exception e)
			{
				return e;
			}

			return response;
		}
	}

}
