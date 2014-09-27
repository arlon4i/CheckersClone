package fi.bb.checkers.ui.screens;

import java.util.Calendar;
import java.util.Vector;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.GridFieldManager;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.BackgroundFactory;
import net.rim.device.api.ui.text.TextFilter;

import com.wigroup.wiAppService.WiAppServiceEssentials;
import com.wigroup.wiAppService.responsehandlers.WiAppResponseHandler;

import fi.bb.checkers.MainApplication;
import fi.bb.checkers.datatypes.LocationData;
import fi.bb.checkers.datatypes.Title;
import fi.bb.checkers.datatypes.UserData;
import fi.bb.checkers.helpers.PersistentStoreHelper;
import fi.bb.checkers.helpers.ResourceHelper;
import fi.bb.checkers.helpers.StringHelper;
import fi.bb.checkers.prompts.CustomDialog;
import fi.bb.checkers.prompts.InfoDialog;
import fi.bb.checkers.prompts.LoadingDialog;
import fi.bb.checkers.ui.components.Actionbar;
import fi.bb.checkers.ui.components.Checkbox;
import fi.bb.checkers.ui.components.DatePickerField;
import fi.bb.checkers.ui.components.HyperlinkButton;
import fi.bb.checkers.ui.components.InputItemContainer;
import fi.bb.checkers.ui.components.TextButton;
import fi.bb.checkers.ui.components.TextImageButton;
import fi.bb.checkers.ui.components.TextInputField;
import fi.bb.checkers.ui.fragments.InfoFragment;
import fi.bb.checkers.ui.fragments.TermsFragment;
import fi.bb.checkers.utils.StringUtil;

public class RegistrationScreen extends MainScreen implements FieldChangeListener
{
	MainApplication app;

	TextImageButton nextButton;
	HyperlinkButton pinButton;
	HyperlinkButton whyButton;
	VerticalFieldManager userDetailsManager;

	TextButton titleInputField;
	TextInputField firstnameField;
	TextInputField surnameField;
	TextInputField cellField;
	TextInputField emailField;
	DatePickerField dateField;
	TextButton provinceInputField;

	String firstname;
	String surname;
	String cellphone;
	String email;
	String birthday;
	String birthmonth;
	String birthyear;

	boolean fromTerms;
	private UserData user;
	private LoadingDialog loading;
	private HyperlinkButton termsLink;
	private Checkbox checkBox;
	private LocationData selected_province;
	private Title selected_title;

	LocationData defaultProvince;

	public static Calendar startRegistrationProcess;

	VerticalFieldManager mainVertManager;

	public RegistrationScreen(int screenNum, UserData user)
	{
		super(NO_SYSTEM_MENU_ITEMS);

		startRegistrationProcess = Calendar.getInstance();

		app = (MainApplication) UiApplication.getUiApplication();
		this.user = user;

		VerticalFieldManager mainManager = (VerticalFieldManager) getMainManager();
		mainManager.setBackground(BackgroundFactory.createSolidBackground(0xe9e9e9));

		mainVertManager = new VerticalFieldManager();
		add(mainVertManager);

		fromTerms = false;
		setBanner(new Actionbar("Sign Up", false, false));
		displayUserDetailsInput();
	}

	public void fieldChanged(Field field, int context)
	{
		if (field == provinceInputField)
		{
			if (context != FieldChangeListener.PROGRAMMATIC)
			{
				Vector selection = new Vector();
				if (selected_province != null) selection.addElement(selected_province);

				Vector choice = SelectionScreen.doModal("Select your province", selection,null, PersistentStoreHelper.getProvinces(), false, false);
				if (choice != null)
				{
					selected_province = (LocationData) choice.elementAt(0);

					provinceInputField.setText(selected_province.getDesc());
					provinceInputField.setTextColor(ResourceHelper.color_checkers_teal);
					updateLayout();// else the manager seems to shrink
				}
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
		else if (field == nextButton)
		{
			String error = "";

			firstname = firstnameField.getText();
			surname = surnameField.getText();
			cellphone = cellField.getText();
			email = emailField.getText();

			birthday = String.valueOf(dateField.getDate().get(Calendar.DAY_OF_MONTH));
			birthmonth = String.valueOf(dateField.getDate().get(Calendar.MONTH)+1);
			birthyear = String.valueOf(dateField.getDate().get(Calendar.YEAR));

			if (birthday.length() == 1) birthday = 0 + birthday;
			if (birthmonth.length() == 1) birthmonth = 0 + birthmonth;
			Calendar currentDate = Calendar.getInstance();

			if (titleInputField.getText().equalsIgnoreCase("select"))
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
			else if (cellphone.equalsIgnoreCase(""))
			{
				error = "Please enter your Mobile number\n";
			}
			else if (cellphone.length() < 10)
			{
				error = "Please enter a valid 10-digit Mobile number\n";
			}
			else if (!cellphone.startsWith("0"))
			{
				error = "Please enter a valid Mobile number\n";
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
			
			if(error.equals(""))//might have gone into the birthdate error fields
			{
				if (provinceInputField.getText().equalsIgnoreCase("select"))
				{
					error = "Please select your province\n";
				}
				else if (!email.equalsIgnoreCase("") && !StringUtil.isEmailAddress(email))
				{
					error = "Please enter a valid email address\n";
				}
				// terms and conditions
				if (!checkBox.isChecked())
				{
					error = StringHelper.error_agree_to_terms;
				}
			}

			if (error.equals(""))
			{
				// instead of asking terms go to Validate My account screen....
				user.setTitleId(selected_title.getId());
				user.setUsername(cellphone);
				user.setFirstname(firstname);
				user.setSurname(surname);
				user.setCellphone(cellphone);
				user.setEmail(email);
				user.setBirthdate(birthday + birthmonth + birthyear);
				user.setProvinceLocationData(selected_province.getId());

				PersistentStoreHelper.setUsername(cellphone);

				// get province id

				register();

				// askTerms();
			}
			else
			{
				String title = "Info";
				String detail = error;
				String button = "Okay";
				InfoDialog.doModal(title, detail, button);
			}
		}
		else if (field == pinButton)
		{
			InfoFragment info = new InfoFragment("whypin.html", true);
			app.pushScreen(new FragmentContainerScreen("Why do I need a Confirmation Code?", info));
		}
		else if (field == whyButton)
		{
			InfoFragment info = new InfoFragment("whydetails.html", true);
			app.pushScreen(new FragmentContainerScreen("Information", info));
		}
		else if (field == termsLink)
		{
			pushTerms();
		}
	}
	protected void onExposed()
	{
		if (fromTerms)
		{
			fromTerms = false;
			askTerms();
		}
	}

	private void askTerms()
	{
		int choice = CustomDialog.doModal(StringHelper.error_agree_to_terms, new String[]{"I Agree", "View the Terms", "I don't Agree"}, new int[]{
				Dialog.YES, Dialog.OK, Dialog.CANCEL});
		if (choice == Dialog.YES)
		{
			user.setUsername(cellphone);
			user.setFirstname(firstname);
			user.setSurname(surname);
			user.setCellphone(cellphone);
			user.setEmail(email);
			user.setBirthdate(birthday + birthmonth + birthyear);
			PersistentStoreHelper.setUsername(cellphone);
			app.slideScreen(new RegistrationScreen(1, user));
		}
		else if (choice == Dialog.OK)
		{
			fromTerms = true;
			pushTerms();
		}
	}

	protected void register()
	{
		new Thread()
		{
			public void run()
			{
				loading = LoadingDialog.push("You will receive a SMS with your Confirmation Code.");//TODO change loading text

				WiAppResponseHandler response = WiAppServiceEssentials.register(user);

				if (response.getResponseCode().equalsIgnoreCase("-1"))
				{
					// wait for 8 seconds
					try
					{
						Thread.sleep(8000);
					} catch (InterruptedException e)
					{
						e.printStackTrace();
					}
					// show sms message
					synchronized (Application.getEventLock())
					{
						//
						loading.close();
						PersistentStoreHelper.setSpecialsRegion(defaultProvince);
						app.pushScreen(new AccountValidationScreen(user));
						//InfoDialog.doModal("Account Validation", "To Validate your Account, enter your Confirmation Code which we have SMS'd to you.", "Okay");
					}

					// doSignIn();
				}
				else
				{
					synchronized (Application.getEventLock())
					{
						// wait for 8 seconds
						try
						{
							Thread.sleep(8000);
						} catch (InterruptedException e)
						{
							e.printStackTrace();
						}
						loading.close();
						InfoDialog.doModal("Error", response.getResponseMessage(), "Okay");
					}
				}
			}
		}.start();
	}

	/**
	 * 
	 */
	public void displayUserDetailsInput()
	{
		userDetailsManager = new VerticalFieldManager();

		firstnameField = new TextInputField("", false, false, ResourceHelper.color_checkers_teal, FIELD_VCENTER);
		surnameField = new TextInputField("", false, false, ResourceHelper.color_checkers_teal, FIELD_VCENTER);
		cellField = new TextInputField("", false, false, ResourceHelper.color_checkers_teal, FIELD_VCENTER);
		emailField = new TextInputField("", false, false, ResourceHelper.color_checkers_teal, FIELD_VCENTER);
		dateField = new DatePickerField(Calendar.getInstance(), FIELD_VCENTER);
		dateField.setFont(ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(17), Ui.UNITS_px));
		provinceInputField = new TextButton("Select", FIELD_VCENTER);
		provinceInputField.setChangeListener(this);
		titleInputField = new TextButton("Select", FIELD_VCENTER);
		titleInputField.setChangeListener(this);

		cellField.setFilter(TextFilter.get(TextFilter.NUMERIC));
		cellField.setMaxSize(10);

		userDetailsManager.add(new InputItemContainer("Title*", titleInputField));
		userDetailsManager.add(new InputItemContainer("Name*", firstnameField));
		userDetailsManager.add(new InputItemContainer("Surname*", surnameField));
		userDetailsManager.add(new InputItemContainer("Mobile*", cellField));
		userDetailsManager.add(new InputItemContainer("Birth Date*", dateField));
		userDetailsManager.add(new InputItemContainer("Province*", provinceInputField));
		userDetailsManager.add(new InputItemContainer("Email", emailField));

		nextButton = new TextImageButton("Create a free account", "btn_sign-up_create-free-account_default", "btn_sign-up_create-free-account_hover");
		nextButton.setTextColor(ResourceHelper.color_white);
		nextButton.setTextColorHover(ResourceHelper.color_primary);
		nextButton.setTextColorPressed(ResourceHelper.color_primary);
		nextButton.setMargin(0, 0, ResourceHelper.convert(10), 0);
		nextButton.setChangeListener(this);

		whyButton = new HyperlinkButton("Why do I need to give these details?", ResourceHelper.convert(17), true);
		whyButton.setChangeListener(this);
		whyButton.setMargin(ResourceHelper.convert(15), 0, ResourceHelper.convert(10), 0);

		mainVertManager.add(userDetailsManager);

		checkBox = new Checkbox(false)
		{
			protected void paint(Graphics graphics)
			{
				Font font = Font.getDefault().derive(Font.PLAIN, 5, Ui.UNITS_pt);
				graphics.setFont(font);
				super.paint(graphics);
			}

			protected void drawFocus(Graphics graphics, boolean on)
			{
			}

		};
		checkBox.setChangeListener(this);

		GridFieldManager termsGridFieldManager = new GridFieldManager(1, 3, Field.FIELD_LEFT);
		termsGridFieldManager.add(checkBox);

		LabelField agreeLabelField = new LabelField(" I agree to the", FIELD_LEFT);
		agreeLabelField.setFont(ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(17), Ui.UNITS_px));
		termsGridFieldManager.add(agreeLabelField);

		termsLink = new HyperlinkButton("Terms", agreeLabelField.getFont().getHeight(Ui.UNITS_px), true, FIELD_LEFT);
		termsLink.setChangeListener(this);

		termsGridFieldManager.add(termsLink);

		VerticalFieldManager manager = new VerticalFieldManager();
		manager.setMargin(ResourceHelper.convert(10), 0, 0, ResourceHelper.convert(10));

		manager.add(termsGridFieldManager);
		manager.add(whyButton);
		manager.add(nextButton);

		mainVertManager.add(manager);

		return;
	}

	private void pushTerms()
	{
		TermsFragment terms = new TermsFragment()
		{
			public void displayQ1help()
			{
				InfoFragment info = new InfoFragment("checkersterms.html", true);
				app.pushScreen(new FragmentContainerScreen(StringHelper.terms_title_1, info));
			}

			public void displayQ2help()
			{
				InfoFragment info = new InfoFragment("customerconsent.html", true);
				app.pushScreen(new FragmentContainerScreen(StringHelper.terms_title_2, info));
			}

			public void displayQ3help()
			{
				InfoFragment info = new InfoFragment("infoprocessingpolicy.html", true);
				app.pushScreen(new FragmentContainerScreen(StringHelper.terms_title_3, info));
			}

			public void displayQ4help()
			{
				InfoFragment info = new InfoFragment("complaintsandgeneral.html", true);
				app.pushScreen(new FragmentContainerScreen(StringHelper.terms_title_4, info));
			}

			public void displayQ5help()
			{
				InfoFragment info = new InfoFragment("voucherissueconditions.html", true);
				app.pushScreen(new FragmentContainerScreen(StringHelper.terms_title_5, info));
			}

			public void displayQ6help()
			{
				InfoFragment info = new InfoFragment("enduserlicence.html", true);
				app.pushScreen(new FragmentContainerScreen(StringHelper.terms_title_6, info));
			}
		};
		app.pushScreen(new FragmentContainerScreen("Terms", terms));
	}

	protected boolean onSavePrompt()
	{
		return true;
	}

	protected void makeMenu(Menu menu, int instance)
	{
		super.makeMenu(menu, instance);
	}

	public void close() {
		super.close();
	}
}