package fi.bb.checkers.ui.fragments;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.io.http.HttpDateParser;
import net.rim.device.api.system.Characters;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.decor.Border;
import net.rim.device.api.ui.decor.BorderFactory;
import net.rim.device.api.ui.text.TextFilter;
import fi.bb.checkers.datatypes.LocationData;
import fi.bb.checkers.datatypes.Title;
import fi.bb.checkers.helpers.FlurryHelper;
import fi.bb.checkers.helpers.PersistentStoreHelper;
import fi.bb.checkers.helpers.ResourceHelper;
import fi.bb.checkers.helpers.RuntimeStoreHelper;
import fi.bb.checkers.helpers.ServerHelper;
import fi.bb.checkers.logger.RemoteLogger;
import fi.bb.checkers.prompts.InfoDialog;
import fi.bb.checkers.prompts.LoadingDialog;
import fi.bb.checkers.ui.components.DatePickerField;
import fi.bb.checkers.ui.components.InputItemContainer;
import fi.bb.checkers.ui.components.TextArea;
import fi.bb.checkers.ui.components.TextButton;
import fi.bb.checkers.ui.components.TextImageButton;
import fi.bb.checkers.ui.components.TextInputField;
import fi.bb.checkers.ui.screens.SelectionScreen;
import fi.bb.checkers.utils.AsyncTask;
import fi.bb.checkers.utils.StringUtil;

public class FeedbackFragment extends Fragment implements FieldChangeListener
{
	public static final int FRAGMENT_ID = getUUID();

	Title selected_title;
	LocationData selected_province;

	TextButton titleInputField;
	TextInputField firstnameField;
	TextInputField surnameField;
	TextInputField cellField;
	TextInputField emailField;
	TextInputField prefferedStoreField;
	TextButton provinceInputField;
	TextButton typeInputField;

	DatePickerField dateField;

	TextArea message_area;
	TextImageButton button_send;

	public FeedbackFragment()
	{
		super(VERTICAL_SCROLL | NO_HORIZONTAL_SCROLL);

		logLeaveFeedbackEvent();

		button_send = new TextImageButton("Send", "btn_sml_default", "btn_sml_hover");
		button_send.setTextColor(ResourceHelper.color_white);
		button_send.setTextColorHover(ResourceHelper.color_primary);
		button_send.setTextColorPressed(ResourceHelper.color_primary);
		button_send.setChangeListener(this);

		if (RuntimeStoreHelper.getSessionID() == null)
		{
			titleInputField = new TextButton("Select", FIELD_VCENTER);
			titleInputField.setChangeListener(this);
			firstnameField = new TextInputField("", false, false, ResourceHelper.color_primary, FIELD_VCENTER);
			surnameField = new TextInputField("", false, false, ResourceHelper.color_primary, FIELD_VCENTER);
			cellField = new TextInputField("", false, false, ResourceHelper.color_primary, FIELD_VCENTER);
			emailField = new TextInputField("", false, false, ResourceHelper.color_primary, FIELD_VCENTER);
			prefferedStoreField = new TextInputField("", false, false, ResourceHelper.color_primary, FIELD_VCENTER);
			provinceInputField = new TextButton("Select", FIELD_VCENTER);
			provinceInputField.setChangeListener(this);

			dateField = new DatePickerField(Calendar.getInstance(), FIELD_VCENTER);
			dateField.setFont(ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(17), Ui.UNITS_px));

			cellField.setFilter(TextFilter.get(TextFilter.NUMERIC));
			cellField.setMaxSize(10);

			Calendar cal = Calendar.getInstance();
			dateField.setDate(cal);
			dateField.setTextColor(ResourceHelper.color_primary);

			add(new InputItemContainer("Title*", titleInputField));
			add(new InputItemContainer("Name*", firstnameField));
			add(new InputItemContainer("Surname*", surnameField));
			add(new InputItemContainer("Mobile*", cellField));
			add(new InputItemContainer("Birth Date*", dateField));
			add(new InputItemContainer("Province*", provinceInputField));
			add(new InputItemContainer("Email", emailField));
			add(new InputItemContainer("Preferred Store", prefferedStoreField));
		}

		//must be created whether or not the user is logged in
		typeInputField = new TextButton("Select", FIELD_VCENTER);
		typeInputField.setChangeListener(this);

		final int padding = ResourceHelper.convert(12);
		message_area = new TextArea("Type your message here", ResourceHelper.color_primary)
		{
			public int getPreferredWidth()
			{
				return FeedbackFragment.this.getPreferredWidth() - (padding * 2);
			}
			public int getPreferredHeight()
			{
				return ResourceHelper.convert(110);
				// return FeedbackFragment.this.getPreferredHeight() - button_send.getPreferredHeight() - ResourceHelper.convert(45);
			}
		};
		message_area.setPadding(padding, padding, padding, padding);
		message_area.setBorder(BorderFactory.createSimpleBorder(new XYEdges(0, 0, 1, 0), new XYEdges(ResourceHelper.color_grey, ResourceHelper.color_grey, ResourceHelper.color_grey,
				ResourceHelper.color_grey), Border.STYLE_SOLID));
		button_send.setMargin(ResourceHelper.convert(10), 0, ResourceHelper.convert(10), padding);

		add(new InputItemContainer("Subject*", typeInputField));
		add(message_area);
		add(button_send);
	}

	public void fieldChanged(Field field, int context)
	{
		if (field == button_send)
		{
			String error = "";
			if (RuntimeStoreHelper.getSessionID() == null && selected_title == null)
				error = "Please select your title\n";
			else if (RuntimeStoreHelper.getSessionID() == null && firstnameField != null && firstnameField.getTextLength() == 0)
				error = "Please enter your First Name\n";
			else if (RuntimeStoreHelper.getSessionID() == null && surnameField != null && surnameField.getTextLength() == 0)
				error = "Please enter your Surname\n";
			else if (RuntimeStoreHelper.getSessionID() == null && emailField != null && emailField.getText().length() != 0 && !StringUtil.isEmailAddress(emailField.getText()))
				error = "Please enter a valid email address\n";
			/*else if (RuntimeStoreHelper.getSessionID() == null && cellField != null && cellField.getTextLength() == 0)
				error = "Please enter a valid Mobile number\n";*/
			else if (RuntimeStoreHelper.getSessionID() == null && cellField != null && cellField.getText().equalsIgnoreCase(""))
				error = "Please enter your Mobile number\n";
			else if (RuntimeStoreHelper.getSessionID() == null && cellField != null && cellField.getText().length() < 10)
				error = "Please enter a valid 10-digit Mobile number\n";
			else if (RuntimeStoreHelper.getSessionID() == null && cellField != null && !cellField.getText().startsWith("0"))
				error = "Please enter a valid Mobile number\n";
			else if (RuntimeStoreHelper.getSessionID() == null && selected_province == null)
				error = "Please select your province\n";
			else if (typeInputField.getText().equalsIgnoreCase("select"))
				error = "Please select your feedback subject.";
			/*else if (RuntimeStoreHelper.getSessionID() == null && prefferedStoreField.getText().length()==0)
				error = "Please enter your Preferred Store.";*/
			else if (message_area.getText().length() == 0) error = "Please enter a message to be sent to us.";

			if (error.equals(""))
			{
				new SendTask().execute(null);
			}
			else
			{
				InfoDialog.doModal("Error", error, "Okay");
			}
		}
		else if (field == provinceInputField)
		{
			if (context != FieldChangeListener.PROGRAMMATIC)
			{
				Vector selection = new Vector();
				if (selected_province != null) selection.addElement(selected_province);

				Vector choice = SelectionScreen.doModal("Select your province", selection, null, PersistentStoreHelper.getProvinces(), false, false);//RuntimeStoreHelper.getProvinces()
				if (choice != null)
				{
					selected_province = (LocationData) choice.elementAt(0);

					provinceInputField.setText(selected_province.getDesc());
					provinceInputField.setTextColor(ResourceHelper.color_primary);
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
					titleInputField.setTextColor(ResourceHelper.color_primary);
					updateLayout();// else the manager seems to shrink
				}
			}
		}
		else if (field == typeInputField)
		{
			if (context != FieldChangeListener.PROGRAMMATIC)
			{

				Vector selection = new Vector();
				selection.addElement(typeInputField.getText());

				Vector choice = SelectionScreen.doModal("Select feedback subject", selection, null, PersistentStoreHelper.getFeedbackTypes(), false, false);//RuntimeStoreHelper.getFeedbackTypes()
				if (choice != null)
				{
					typeInputField.setText((String) choice.elementAt(0));
					typeInputField.setTextColor(ResourceHelper.color_primary);
					updateLayout();// else the manager seems to shrink
				}
			}
		}
	}

	public void onClose() {
		FlurryHelper.endTimedEvent(FlurryHelper.EVENT_LEAVE_FEEDBACK);
		super.onClose();
	}

	private void logSendMessage(boolean success) 
	{
		Hashtable eventParams = new Hashtable();

		if (success == true)
		{
			eventParams.put(FlurryHelper.PARAM_MESSAGE_STATUS, "Success");
		}
		else
		{
			eventParams.put(FlurryHelper.PARAM_MESSAGE_STATUS, "Not Completed");
		}
		FlurryHelper.addProvinceParam(eventParams);

		FlurryHelper.logEvent(FlurryHelper.EVENT_SEND_A_MESSAGE, eventParams, false);
	}

	private void logLeaveFeedbackEvent()
	{
		Hashtable eventParams = new Hashtable();

		FlurryHelper.addProvinceParam(eventParams);
		FlurryHelper.addLoginParams(eventParams);

		FlurryHelper.logEvent(FlurryHelper.EVENT_LEAVE_FEEDBACK, eventParams, true);
	}

	private class SendTask extends AsyncTask
	{
		LoadingDialog dialog;

		protected void onPreExecute()
		{
			dialog = LoadingDialog.push("Sending...");
		}

		protected void onPostExecute(Object result)
		{
			dialog.close();
			if (result instanceof IOException)
			{
				InfoDialog.doModal("Error", ((IOException) result).getMessage(), "Okay");
			}
			else
			{
				logSendMessage(true);
				InfoDialog.doModal("", "Thanks for your feedback!", "Okay");
				close();
			}
		}

		public Object doInBackground(Object[] params)
		{
			try
			{
				if (RuntimeStoreHelper.getSessionID() == null){

					/*String birthday = String.valueOf(dateField.getDate().get(Calendar.DAY_OF_MONTH));
					String birthmonth = String.valueOf(dateField.getDate().get(Calendar.MONTH)+1);
					String birthyear = String.valueOf(dateField.getDate().get(Calendar.YEAR));

					if (birthday.length() == 1) birthday = 0 + birthday;
					if (birthmonth.length() == 1) birthmonth = 0 + birthmonth;
					
					String birthdateServer = "";*/
					
					//String birthDate = new SimpleDateFormat("dd/MM/yyyy").format(dateField.getDate().getTime());
					
					//TODO confirm with checkers, but this is actiually never sent to the server

					ServerHelper.sendFeedback(selected_title.getId(), firstnameField.getText(), surnameField.getText(), emailField.getText(), cellField.getText(), selected_province.getId(), prefferedStoreField.getText(),typeInputField.getText(),
							message_area.getText());
				}
				else
				{
					ServerHelper.sendFeedback(typeInputField.getText(), message_area.getText());
				}
			} catch (IOException e)
			{
//				RemoteLogger.log("FeedbackFragment", e.toString());
				return e;
			}
			return null;
		}
	}

	protected boolean keyChar(char ch, int status, int time) {

		if (ch == Characters.ESCAPE)
		{
			logSendMessage(false);
		}

		return super.keyChar(ch, status, time);
	}
}
