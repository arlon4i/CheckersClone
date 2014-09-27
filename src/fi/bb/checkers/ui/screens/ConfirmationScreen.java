package fi.bb.checkers.ui.screens;

import java.util.Hashtable;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.BackgroundFactory;

import com.wigroup.wiAppService.WiAppServiceEssentials;
import com.wigroup.wiAppService.responsehandlers.WiAppResponseHandler;

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
import fi.bb.checkers.utils.AsyncTask;

public class ConfirmationScreen extends MainScreen implements FieldChangeListener
{
	private VerticalFieldManager loginDetailsManager;
	private PinField pinField;
	private ColorButtonField loginButton;
	private ColorButtonField forgotButton;
	
	private String cellNumber;
	private int buttonHeight = ResourceHelper.convert(25);

	public ConfirmationScreen(String cellNumber)
	{
		super(NO_SYSTEM_MENU_ITEMS);

		this.cellNumber = cellNumber;
		
		VerticalFieldManager mainManager = (VerticalFieldManager) getMainManager();
		mainManager.setBackground(BackgroundFactory.createSolidBackground(0xf9f9f9));

		setBanner(new Actionbar("Confirmation Code", false, false));
		displayLogin();
	}

	public void fieldChanged(Field field, int context)
	{

		if (field == loginButton)
		{
			login();
		}
		else if (field == forgotButton)
		{
			new ResetTask().execute(null);
		}
	}
	public void displayLogin()
	{
		this.deleteAll();

		loginDetailsManager = new VerticalFieldManager();

		pinField = new PinField(FIELD_VCENTER);

		loginDetailsManager.add(new InputItemContainer(null, pinField));
		//loginDetailsManager.add(new InputItemContainer("Enter your 4-digit Checkers App PIN", null, true));

		LabelField loginText = new LabelField("If you do not get your confirmation code, you can request it again by clicking on the \"Resend Confirmation Code\" button.", ResourceHelper.color_black, 0, ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(17), Ui.UNITS_px));
		loginText.setMargin(ResourceHelper.convert(5), 0, 0, ResourceHelper.convert(5));
		loginDetailsManager.add(loginText);
		
		loginDetailsManager.add(getSeperator());
		
		/*loginButton = new TextImageButton("Enter", "btn_sml_default", "btn_sml_hover");
		loginButton.setTextColor(ResourceHelper.color_white);
		loginButton.setTextColorHover(ResourceHelper.color_primary);
		loginButton.setTextColorPressed(ResourceHelper.color_primary);*/
		loginButton = new ColorButtonField(ResourceHelper.color_checkers_teal, ResourceHelper.color_white, ResourceHelper.convert(100),  buttonHeight);
		loginButton.setButtonOutlineStates(ResourceHelper.color_checkers_teal, ResourceHelper.color_checkers_teal, ResourceHelper.color_checkers_teal);
		loginButton.setButtonText("Enter");
		loginButton.setTextStates(ResourceHelper.color_white, ResourceHelper.color_checkers_teal, ResourceHelper.color_checkers_teal);
		loginButton.setTextFont(ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(17), Ui.UNITS_px));
		
		loginButton.setChangeListener(this);
		loginButton.setMargin(ResourceHelper.convert(10), 0, 0, ResourceHelper.convert(10));
		loginDetailsManager.add(loginButton);
		
		/*forgotButton = new TextImageButton("Resend Confirmation Code", "btn_grey_default", "btn_grey_hover");
		forgotButton.setTextColor(ResourceHelper.color_primary);
		forgotButton.setTextColorHover(ResourceHelper.color_primary);
		forgotButton.setTextColorPressed(ResourceHelper.color_primary);*/
		forgotButton = new ColorButtonField(ResourceHelper.color_checkers_teal, ResourceHelper.color_white, ResourceHelper.convert(215),  buttonHeight);
		forgotButton.setButtonOutlineStates(ResourceHelper.color_checkers_teal, ResourceHelper.color_checkers_teal, ResourceHelper.color_checkers_teal);
		forgotButton.setButtonText("Resend Confirmation Code");
		forgotButton.setTextStates(ResourceHelper.color_white, ResourceHelper.color_checkers_teal, ResourceHelper.color_checkers_teal);
		forgotButton.setTextFont(ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(17), Ui.UNITS_px));
		
		//forgotButton = new HyperlinkButton("Resend Confirmation Code", ResourceHelper.convert(17));
		forgotButton.setChangeListener(this);
		forgotButton.setMargin(ResourceHelper.convert(10), 0, 0, ResourceHelper.convert(10));
		loginDetailsManager.add(forgotButton);

		add(loginDetailsManager);

		pinField.setFocus();

		return;
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

	protected void login()
	{

		String error = "";

		final String username = cellNumber;
		final String pin = pinField.getPIN();

		if (username.equalsIgnoreCase(""))
		{
			error = StringHelper.error_mobile;
		}
		else if (pin.length() != 4)
		{
			error = StringHelper.error_enter_code;
		}

		if (error.equals(""))
		{
			new LoginTask().execute(new String[]{username, pin});

		}
		else
		{
			String title = "Error";
			String detail = error;
			String button = "Okay";
			InfoDialog.doModal(title, detail, button);
		}
	}

	protected boolean onSavePrompt()
	{
		return true;
	}

	protected void makeMenu(Menu menu, int instance)
	{
		super.makeMenu(menu, instance);
	}

	private class LoginTask extends AsyncTask
	{
		private LoadingDialog loading;

		protected void onPreExecute()
		{
			loading = LoadingDialog.push("Loading");
		}

		protected void onPostExecute(Object result)
		{
			loading.close();
			if (!"success".equals(result))
			{
				InfoDialog.doModal("Error", (String) result, "Okay");
			}
			else
			{
				logLogin();
				
				ViewPagerScreen.push();
			}
		}

		private void logLogin() 
		{
			Hashtable eventParams = new Hashtable();
			eventParams.put(FlurryHelper.PARAM_TAPPED, "1");
			eventParams.put(FlurryHelper.PARAM_PIN_ENTERED, pinField.getPIN());
			
			FlurryHelper.logEvent(FlurryHelper.EVENT_LOG_IN, eventParams, false);
		}

		public Object doInBackground(Object[] params)
		{
			try
			{
				ServerHelper.login((String) params[0], (String) params[1]);
				
				ServerHelper.getUserDetails();
				ServerHelper.getCouponList(PersistentStoreHelper.getSpecialsRegion().getId());//because you need it for myList
			} catch (Exception e)
			{
				RuntimeStoreHelper.setSessionID(null);
				PersistentStoreHelper.setPIN("");
				return e.getMessage();
			}
			return "success";
		}
	}

	private class ResetTask extends AsyncTask
	{
		private LoadingDialog loading;

		protected void onPreExecute()
		{
			loading = LoadingDialog.push(StringHelper.resending_code_message_2);
		}

		protected void onPostExecute(Object result)
		{
			loading.close();

			if (result instanceof WiAppResponseHandler)
			{
				WiAppResponseHandler response = (WiAppResponseHandler) result;
				if (response.getResponseCode().equalsIgnoreCase("-1"))
				{
					//InfoDialog.doModal("Confirmation Code Reset Success", "You will receive a SMS with your new Confirmation Code. Should you continue experiencing a problem, please contact 0800 33 33 85.", "Okay");
					InfoDialog.doModal("", "You will receive a SMS with your new confirmation code. Should you continue experiencing a problem, please contact 0800 33 33 85.", "Okay");
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
				response = WiAppServiceEssentials.resetPin(cellNumber);
			} catch (Exception e)
			{
				return e;
			}

			return response;
		}
	}
}