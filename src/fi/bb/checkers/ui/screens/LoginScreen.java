package fi.bb.checkers.ui.screens;

import java.util.Hashtable;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.BackgroundFactory;
import net.rim.device.api.ui.text.TextFilter;

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
import fi.bb.checkers.ui.components.InputItemContainer;
import fi.bb.checkers.ui.components.LabelField;
import fi.bb.checkers.ui.components.TextInputField;
import fi.bb.checkers.utils.AsyncTask;

public class LoginScreen extends MainScreen implements FieldChangeListener
{
	private VerticalFieldManager loginDetailsManager;
	private TextInputField cellField;
	//private PinField pinField;
	private ColorButtonField loginButton;
	//private HyperlinkButton forgotButton;
	
	private int buttonHeight = ResourceHelper.convert(25);

	public LoginScreen()
	{
		super(NO_SYSTEM_MENU_ITEMS);

		VerticalFieldManager mainManager = (VerticalFieldManager) getMainManager();
		mainManager.setBackground(BackgroundFactory.createSolidBackground(0xf9f9f9));

		setBanner(new Actionbar("Login", false, false));
		displayLogin();
	}

	public void fieldChanged(Field field, int context)
	{

		if (field == loginButton)
		{
			sendConfirmationCode();
		}
		/*else if (field == forgotButton)
		{
			if (cellField.getText().equalsIgnoreCase("") || cellField.getText().length() < 10 || !cellField.getText().startsWith("0"))
			{
				InfoDialog.doModal("Error", "Enter your Mobile number in order to reset your Checkers App Confirmation Code", "Okay");
			}
			else
			{
				new ResetTask().execute(null);
			}
		}*/
	}
	public void displayLogin()
	{
		this.deleteAll();

		loginDetailsManager = new VerticalFieldManager();

		cellField = new TextInputField("0821234567", false, false, ResourceHelper.color_checkers_teal, FIELD_VCENTER);
		cellField.setFilter(TextFilter.get(TextFilter.NUMERIC));
		cellField.setMaxSize(10);

		//pinField = new PinField(FIELD_VCENTER);

		loginDetailsManager.add(new InputItemContainer("Mobile*", cellField, true));
		//loginDetailsManager.add(new InputItemContainer("Enter your 4-digit Checkers App PIN", null, true));
		//loginDetailsManager.add(new InputItemContainer(null, pinField));

		/*forgotButton = new HyperlinkButton("Forgot your PIN?", ResourceHelper.convert(17));
		forgotButton.setChangeListener(this);
		forgotButton.setMargin(ResourceHelper.convert(10), 0, 0, ResourceHelper.convert(10));
		loginDetailsManager.add(forgotButton);*/

		LabelField loginText = new LabelField("We care about protecting your personal information in the App.\n\nPlease verify your mobile number by clicking on the \"Send Confirmation Code\" button.", ResourceHelper.color_black, 0, ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(17), Ui.UNITS_px));
		//loginText.setPadding(ResourceHelper.convert(5), 0, 0, ResourceHelper.convert(5));
		loginText.setMargin(ResourceHelper.convert(5), 0, 0, ResourceHelper.convert(5));
		loginDetailsManager.add(loginText);
		
		loginDetailsManager.add(getSeperator());
		
		loginButton = new ColorButtonField(ResourceHelper.color_checkers_teal, ResourceHelper.color_white, ResourceHelper.convert(215),  buttonHeight);
		loginButton.setButtonOutlineStates(ResourceHelper.color_checkers_teal, ResourceHelper.color_checkers_teal, ResourceHelper.color_checkers_teal);
		loginButton.setButtonText("Send Confirmation Code");
		loginButton.setTextStates(ResourceHelper.color_white, ResourceHelper.color_checkers_teal, ResourceHelper.color_checkers_teal);
		loginButton.setTextFont(ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(17), Ui.UNITS_px));
		loginButton.setChangeListener(this);
		loginButton.setMargin(ResourceHelper.convert(10), 0, 0, ResourceHelper.convert(10));
		loginDetailsManager.add(loginButton);
		
		/*loginButton = new TextImageButton("Send Confirmation Code", "btn_default", "btn_hover");
		loginButton.setTextColor(ResourceHelper.color_white);
		loginButton.setTextColorHover(ResourceHelper.color_primary);
		loginButton.setTextColorPressed(ResourceHelper.color_primary);*/
		
		add(loginDetailsManager);

		cellField.setFocus();
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

		final String username = cellField.getText();
		//final String pin = pinField.getPIN();

		if (username.equalsIgnoreCase(""))
		{
			error = "Please enter your Mobile Number\n";
		}
		/*else if (pin.length() != 4)
		{
			error = "Please enter your 4-digit Checkers App PIN\n";
		}*/

		if (error.equals(""))
		{
			//new LoginTask().execute(new String[]{username, pin});
			UiApplication.getUiApplication().pushScreen(new ConfirmationScreen(username));
		}
		else
		{
			String title = "Error";
			String detail = error;
			String button = "Okay";
			InfoDialog.doModal(title, detail, button);
		}
	}
	
	protected void sendConfirmationCode()
	{

		String error = "";

		final String username = cellField.getText();

		if (username.equalsIgnoreCase(""))
		{
			error = "Please enter your Mobile Number\n";
		}

		if (error.equals(""))
		{
			new ResetTask().execute(null);
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
				InfoDialog.doModal("Info", (String) result, "Okay");
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
			//eventParams.put(FlurryHelper.PARAM_PIN_ENTERED, pinField.getPIN());//TODO Not useful here anymore and not sure if we should log user pins ???
			
			FlurryHelper.logEvent(FlurryHelper.EVENT_LOG_IN, eventParams, false);
		}

		public Object doInBackground(Object[] params)
		{
			try
			{
				ServerHelper.login((String) params[0], (String) params[1]);
				ServerHelper.getUserDetails();
				
				ServerHelper.getCouponList(PersistentStoreHelper.getSpecialsRegion().getId());//because you need it for myList
				int size = PersistentStoreHelper.mylistSize();
				
				RemoteLogger.log("ANJE", "mylist size: " + size);
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
			loading = LoadingDialog.push(StringHelper.sending_code_message);
		}

		protected void onPostExecute(Object result)
		{
			loading.close();

			if (result instanceof WiAppResponseHandler)
			{
				WiAppResponseHandler response = (WiAppResponseHandler) result;
				if (response.getResponseCode().equalsIgnoreCase("-1"))
				{
					InfoDialog.doModal("", "You will receive a SMS with your new confirmation code. Should you continue experiencing a problem, please contact 0800 33 33 85.", "Okay");
					UiApplication.getUiApplication().pushScreen(new ConfirmationScreen(cellField.getText()));
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
				response = WiAppServiceEssentials.resetPin(cellField.getText());
			} catch (Exception e)
			{
				return e;
			}

			return response;
		}
	}
}