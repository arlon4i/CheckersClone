package fi.bb.checkers.ui.screens;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.io.http.HttpDateParser;
import net.rim.device.api.system.Characters;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.BackgroundFactory;
import fi.bb.checkers.datatypes.LocationData;
import fi.bb.checkers.datatypes.MerchantData;
import fi.bb.checkers.datatypes.Title;
import fi.bb.checkers.datatypes.UserData;
import fi.bb.checkers.helpers.FlurryHelper;
import fi.bb.checkers.helpers.PersistentStoreHelper;
import fi.bb.checkers.helpers.ResourceHelper;
import fi.bb.checkers.helpers.RuntimeStoreHelper;
import fi.bb.checkers.helpers.ServerHelper;
import fi.bb.checkers.interfaces.InterfaceStoreChanged;
import fi.bb.checkers.prompts.CustomDialog;
import fi.bb.checkers.prompts.InfoDialog;
import fi.bb.checkers.prompts.LoadingDialog;
import fi.bb.checkers.ui.components.Actionbar;
import fi.bb.checkers.ui.components.DatePickerField;
import fi.bb.checkers.ui.components.InputItemContainer;
import fi.bb.checkers.ui.components.LabelField;
import fi.bb.checkers.ui.components.TextButton;
import fi.bb.checkers.ui.components.TextImageButton;
import fi.bb.checkers.ui.components.TextInputField;
import fi.bb.checkers.ui.fragments.SelectStoreFragment;
import fi.bb.checkers.ui.fragments.StoreInfoFragment;
import fi.bb.checkers.utils.AsyncTask;
import fi.bb.checkers.utils.StringUtil;

public class EditProfileScreen extends MainScreen implements FieldChangeListener, InterfaceStoreChanged
{
	TextButton titleInputField;
	TextInputField firstnameField;
	TextInputField surnameField;
	LabelField cellField;
	TextInputField emailField;
	DatePickerField dateField;
	TextButton provinceInputField;
	TextButton preferredstoreInputField;

	TextImageButton button_save;
	TextImageButton button_logout;

	LocationData selected_province;
	MerchantData selected_store;
	MerchantData changed_store;
	private Title selected_title;
	private Title selected_title_original;

	// set whether the preferred store must be updated on visible
	boolean perform_onresult = false;
	Screen screenToPop;

	public EditProfileScreen()
	{
		super(NO_VERTICAL_SCROLL | NO_HORIZONTAL_SCROLL);
		add(new Actionbar("Personal Details", false, false));

		final UserData profile = RuntimeStoreHelper.getUserData();

		titleInputField = new TextButton("Select", FIELD_VCENTER);
		firstnameField = new TextInputField("", false, false, ResourceHelper.color_checkers_teal, FIELD_VCENTER);
		surnameField = new TextInputField("", false, false, ResourceHelper.color_checkers_teal, FIELD_VCENTER);
		cellField = new LabelField(profile.getCellphone(), ResourceHelper.color_checkers_teal, DrawStyle.VCENTER, ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(17), Ui.UNITS_px));
		emailField = new TextInputField("", false, false, ResourceHelper.color_checkers_teal, FIELD_VCENTER);
		dateField = new DatePickerField(Calendar.getInstance(), FIELD_VCENTER);
		dateField.setFont(ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(17), Ui.UNITS_px));
		provinceInputField = new TextButton("Select", FIELD_VCENTER);
		preferredstoreInputField = new TextButton("Select Preferred Store", FIELD_VCENTER);
		preferredstoreInputField.setTextColor(ResourceHelper.color_black);
		preferredstoreInputField.setTextColorHover(ResourceHelper.color_white);
		preferredstoreInputField.setTextColorPressed(ResourceHelper.color_white);
		preferredstoreInputField.setMargin(0, 0, 0, 0);

		VerticalFieldManager field_manager = new VerticalFieldManager(VERTICAL_SCROLL | NO_HORIZONTAL_SCROLL);
		field_manager.add(new InputItemContainer("Title*", titleInputField));
		field_manager.add(new InputItemContainer("Name*", firstnameField));
		field_manager.add(new InputItemContainer("Surname*", surnameField));
		InputItemContainer mobile_container = new InputItemContainer("Mobile*", cellField);
		mobile_container.setBackground(BackgroundFactory.createSolidBackground(ResourceHelper.color_light_grey));
		field_manager.add(mobile_container);
		field_manager.add(new InputItemContainer("Birth Date*", dateField));
		field_manager.add(new InputItemContainer("Province*", provinceInputField));
		field_manager.add(new InputItemContainer("Email", emailField));
		field_manager.add(new InputItemContainer(null, preferredstoreInputField));

		button_save = new TextImageButton("Save", "btn_sml_default", "btn_sml_hover");
		button_save.setTextColor(ResourceHelper.color_white);
		button_save.setTextColorHover(ResourceHelper.color_primary);
		button_save.setTextColorPressed(ResourceHelper.color_primary);

		button_logout = new TextImageButton("Sign Out", "btn_sml_grey_default", "btn_sml_hover");
		button_logout.setTextColor(ResourceHelper.color_primary);
		button_logout.setTextColorHover(ResourceHelper.color_primary);
		button_logout.setTextColorPressed(ResourceHelper.color_primary);
		button_logout.setMargin(0, 0, 0, ResourceHelper.convert(10));

		HorizontalFieldManager button_manager = new HorizontalFieldManager(NO_VERTICAL_SCROLL | NO_HORIZONTAL_SCROLL);
		button_manager.setMargin(ResourceHelper.convert(10), ResourceHelper.convert(10), ResourceHelper.convert(10), ResourceHelper.convert(10));
		button_manager.add(button_save);
		button_manager.add(button_logout);
		field_manager.add(button_manager);

		add(field_manager);

		titleInputField.setChangeListener(this);
		provinceInputField.setChangeListener(this);
		preferredstoreInputField.setChangeListener(this);
		button_save.setChangeListener(this);
		button_logout.setChangeListener(this);



		for (int i = 0; i < PersistentStoreHelper.getProvinces().size(); i++)//RuntimeStoreHelper.getProvinces()
		{
			if (((LocationData) PersistentStoreHelper.getProvinces().elementAt(i)).getId().equals(profile.getProvinceLocationData()))
			{
				selected_province = (LocationData) PersistentStoreHelper.getProvinces().elementAt(i);
				break;
			}
		}

		for (int i = 0; i < PersistentStoreHelper.getTitles().size(); i++)//RuntimeStoreHelper.getProvinces()
		{
			if (((Title) PersistentStoreHelper.getTitles().elementAt(i)).getId().equals(profile.getTitleId()))
			{
				selected_title_original = (Title) PersistentStoreHelper.getTitles().elementAt(i);
				selected_title = (Title) PersistentStoreHelper.getTitles().elementAt(i);
				break;
			}
		}

		firstnameField.setText(profile.getFirstname());
		surnameField.setText(profile.getSurname());
		emailField.setText(profile.getEmail());

		String[] tokens = StringUtil.split(profile.getBirthdate(), "/");
		String httpparserformat = tokens[2] + "-" + tokens[1] + "-" + tokens[0] + " 00:00:00.000";
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date(HttpDateParser.parse(httpparserformat)));
		dateField.setDate(cal);
		dateField.setTextColor(ResourceHelper.color_checkers_teal);
		
		provinceInputField.setText(selected_province.getDesc());
		provinceInputField.setTextColor(ResourceHelper.color_checkers_teal);

		if (selected_title != null)
		{
			titleInputField.setText(selected_title.getDescription());
			titleInputField.setTextColor(ResourceHelper.color_checkers_teal);
		}

		selected_store = RuntimeStoreHelper.getUserData().getPreferredStore();
		changed_store = selected_store;
		if (selected_store != null) 
		{
			preferredstoreInputField.setText(selected_store.getName()); 
			preferredstoreInputField.setTextColor(ResourceHelper.color_checkers_teal);
		}
	}

	public void fieldChanged(Field field, int context)
	{
		if (field == provinceInputField)
		{
			Vector selection = new Vector();
			if (selected_province != null) selection.addElement(selected_province);

			Vector choice = SelectionScreen.doModal("Select your province", selection, null, PersistentStoreHelper.getProvinces(), false, false);
			if (choice != null)
			{
				selected_province = (LocationData) choice.elementAt(0);
				//selected_store = null;

				provinceInputField.setText(selected_province.getDesc());
				provinceInputField.setTextColor(ResourceHelper.color_checkers_teal);

				//preferredstoreInputField.setText("Select Preferred Store");
				updateLayout();// else the manager seems to shrink
			}
		}
		else if (field == titleInputField)
		{
			if (context != FieldChangeListener.PROGRAMMATIC)
			{
				Vector selection = new Vector();
				if (selected_title != null) selection.addElement(selected_title);

				Vector choice = SelectionScreen.doModal("Select your title", selection, null, PersistentStoreHelper.getTitles(), false, false);
				if (choice != null)
				{
					selected_title = (Title) choice.elementAt(0);

					titleInputField.setText(selected_title.getDescription());
					titleInputField.setTextColor(ResourceHelper.color_checkers_teal);
					updateLayout();// else the manager seems to shrink
				}
			}

		}
		else if (field == preferredstoreInputField)
		{
			if (selected_store == null)
			{
				//new DownloadTask().execute(new Object[]{selected_province});
				perform_onresult = true;
				screenToPop = new FragmentContainerScreen("Preferred Store", new SelectStoreFragment(selected_province, this));
				UiApplication.getUiApplication().pushScreen(screenToPop);
			}
			else
			{
				perform_onresult = true;
				UiApplication.getUiApplication().pushScreen(new FragmentContainerScreen("My Preferred Store", new StoreInfoFragment(selected_store, true, this)));
			}
		}
		else if (field == button_save)
		{
			String firstname = firstnameField.getText();
			String surname = surnameField.getText();
			String email = emailField.getText();

			String birthday = String.valueOf(dateField.getDate().get(Calendar.DAY_OF_MONTH));
			String birthmonth = String.valueOf(dateField.getDate().get(Calendar.MONTH));
			String birthyear = String.valueOf(dateField.getDate().get(Calendar.YEAR));

			if (birthday.length() == 1) birthday = 0 + birthday;
			if (birthmonth.length() == 1) birthmonth = 0 + birthmonth;
			Calendar currentDate = Calendar.getInstance();

			String error = null;
			if ((titleInputField.getText().equalsIgnoreCase("")) && (selected_title_original != null))//for old users this field is not compulsory
			{
				error = "Please select your title\n";
			}
			else if (firstname.equalsIgnoreCase(""))
			{
				error = "Please enter your First Name\n";
			}
			else if (surname.equalsIgnoreCase(""))
			{
				error = "Please enter your Surname\n";
			}
			else if (Integer.parseInt(birthyear) > currentDate.get(Calendar.YEAR) - 18)
			{
				error = "You must be 18 years or older to use this application.\n";
			}
			else if (Integer.parseInt(birthyear) == currentDate.get(Calendar.YEAR) - 18)
			{
				if (Integer.parseInt(birthmonth) == currentDate.get(Calendar.MONTH) + 1)
				{
					if (Integer.parseInt(birthday) > currentDate.get(Calendar.DAY_OF_MONTH))
					{
						error = "You need to be over 18 years old in order to register for this service.\n";
					}
				}
				else if (Integer.parseInt(birthmonth) > currentDate.get(Calendar.MONTH))
				{
					error = "You need to be over 18 years old in order to register for this service.\n";
				}
			}
			else if (provinceInputField.getText().equalsIgnoreCase(""))
			{
				error = "Please select your province\n";
			}
			else if (!email.equalsIgnoreCase(""))
			{
				if (!StringUtil.isEmailAddress(email)) error = "Please enter a valid email address\n";
			}

			if (error == null)
			{
				UserData profile = new UserData();

				String titleId = (selected_title==null)?"":selected_title.getId();

				profile.setTitleId(titleId);
				profile.setUsername(PersistentStoreHelper.getUsername());
				profile.setFirstname(firstnameField.getText());
				profile.setSurname(surnameField.getText());
				profile.setCellphone(cellField.getText());
				profile.setEmail(emailField.getText());
				profile.setBirthdate(new SimpleDateFormat("dd/MM/yyyy").format(dateField.getDate().getTime()));
				profile.setProvinceLocationData(selected_province.getId());
				profile.setPreferredStore(selected_store);
				new UpdateTask().execute(new Object[]{profile});
			}
			else
			{
				InfoDialog.doModal("Info", error, "Okay");
			}
		}
		else if (field == button_logout)
		{
			int choice = CustomDialog.doModal("Are you sure you want to sign out?", new String[]{"Cancel", "Yes"}, new int[]{Dialog.CANCEL, Dialog.YES});
			if (choice == Dialog.YES)
			{
				PersistentStoreHelper.setPIN("");
				RuntimeStoreHelper.setSessionID(null);

				ViewPagerScreen.push();
			}
		}
	}

	protected void onVisibilityChange(boolean visible)
	{
		super.onVisibilityChange(visible);

		if (visible)
		{
			if (perform_onresult)
			{
				perform_onresult = false;
				selected_store = changed_store;
				if (selected_store == null)
					preferredstoreInputField.setText("Select Preferred Store");
				else
					preferredstoreInputField.setText(selected_store.getName());
			}
		}
	}

	public boolean onClose() {

		FlurryHelper.endTimedEvent(FlurryHelper.EVENT_EDIT_PROFILE);
		return super.onClose();
	}

	protected boolean onSavePrompt()
	{
		return false;
	}

	public boolean isDirty()
	{
		UserData profile = RuntimeStoreHelper.getUserData();

		LocationData loc = null;
		for (int i = 0; i < PersistentStoreHelper.getProvinces().size(); i++)
		{
			if (((LocationData) PersistentStoreHelper.getProvinces().elementAt(i)).getId().equals(profile.getProvinceLocationData()))
			{
				loc = (LocationData) PersistentStoreHelper.getProvinces().elementAt(i);
				break;
			}
		}
		if (loc != selected_province) return true;
		if (!profile.getFirstname().equals(firstnameField.getText())) return true;
		if (!profile.getSurname().equals(surnameField.getText())) return true;
		if (!profile.getCellphone().equals(cellField.getText())) return true;
		if (profile.getEmail() != null && !profile.getEmail().equals(emailField.getText())) return true;

		String[] tokens = StringUtil.split(profile.getBirthdate(), "/");
		String httpparserformat = tokens[2] + "-" + tokens[1] + "-" + tokens[0] + " 00:00:00.000";
		Date date = new Date(HttpDateParser.parse(httpparserformat));
		if (!date.equals(dateField.getDate().getTime())) return true;
		if (profile.getPreferredStore() == null && selected_store == null) return false;
		if (profile.getPreferredStore() == null || selected_store == null) return true;
		if (!profile.getPreferredStore().equals(selected_store)) return true;

		return false;
	}

	protected boolean keyChar(char c, int status, int time)
	{
		if (c == Characters.ESCAPE)
		{
			if (isDirty())
			{
				int choice = CustomDialog.doModal("Are you sure you want to discard changes ?", new String[]{"Yes", "No"}, new int[]{Dialog.YES, Dialog.NO});
				if (choice == Dialog.YES)
				{
					close();
				}
			}
		}
		return super.keyChar(c, status, time);
	}

	private class UpdateTask extends AsyncTask
	{
		LoadingDialog dialog;
		protected void onPreExecute()
		{
			super.onPreExecute();
			dialog = LoadingDialog.push("Updating...");
		}

		protected void onPostExecute(Object result)
		{
			super.onPostExecute(result);
			dialog.close();

			if (result instanceof UserData)
			{
				// so that the profile drawer gets updated
				ViewPagerScreen.push();
			}
			else if (result instanceof String)
			{
				InfoDialog.doModal("Error", (String) result, "Okay");
			}
			else if (result instanceof Exception)
			{
				InfoDialog.doModal(((Exception) result).getClass().getName(), ((Exception) result).getMessage(), "Okay");
			}
		}

		public Object doInBackground(Object[] params)
		{
			try
			{
				String wicode = RuntimeStoreHelper.getUserData().getWicode();
				String response = ServerHelper.updateUserDetails((UserData) params[0]);
				// preserve previous wicode, instead of waiting for the webcall
				if (RuntimeStoreHelper.getUserData().getWicode() == null || RuntimeStoreHelper.getUserData().getWicode().equals("")) RuntimeStoreHelper.getUserData().setWicode(wicode);

				if ("success".equals(response)) return params[0];

				return response;
			} catch (Exception e)
			{
				return e;
			}
		}
	}

	private class DownloadTask extends AsyncTask
	{
		LoadingDialog prompt;

		protected void onPreExecute()
		{
			super.onPreExecute();
			prompt = LoadingDialog.push("Loading");
		}

		protected void onPostExecute(Object result)
		{
			super.onPostExecute(result);
			prompt.close();

			if (result instanceof Vector)
			{
				Vector selection = new Vector();
				if (selected_store != null) selection.addElement(selected_store);
				selection = SelectionScreen.doModal("Preferred Store", selection, null, (Vector) result, false, false);
				if (selection != null)
				{
					selected_store = (MerchantData) selection.elementAt(0);
					preferredstoreInputField.setText(selected_store.getName());
				}
			}
			else
			{
				String msg = ((Exception) result).getMessage();
				if (msg.length() == 0) msg = "An unexpected error occured.";
				InfoDialog.doModal("Error", msg, "Okay");
			}
		}

		public Object doInBackground(Object[] params)
		{
			try
			{
				return ServerHelper.getStores((LocationData) params[0]);
			} catch (IOException e)
			{
				return e;
			}
		}
	}

	public void onStoreChanged(MerchantData merchant) 
	{
		changed_store = merchant;

		if ((screenToPop != null) && (UiApplication.getUiApplication().getActiveScreen() == screenToPop))
		{
			UiApplication.getUiApplication().popScreen(screenToPop);	
			screenToPop = null;
		}

		//onvisibilitychanged handles the rest
	}
}
