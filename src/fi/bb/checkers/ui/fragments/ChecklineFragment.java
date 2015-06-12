package fi.bb.checkers.ui.fragments;

import net.rim.blackberry.api.invoke.Invoke;
import net.rim.blackberry.api.invoke.PhoneArguments;
import net.rim.device.api.browser.field2.BrowserField;
import net.rim.device.api.browser.field2.BrowserFieldConfig;
import net.rim.device.api.browser.field2.BrowserFieldRequest;
import net.rim.device.api.browser.field2.ProtocolController;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.NullField;
import fi.bb.checkers.helpers.ResourceHelper;
import fi.bb.checkers.logger.RemoteLogger;
import fi.bb.checkers.prompts.CustomDialog;
import fi.bb.checkers.prompts.InfoDialog;
import fi.bb.checkers.ui.screens.ViewPagerScreen;
import fi.bb.checkers.utils.StringUtil;

public class ChecklineFragment extends Fragment implements FieldChangeListener
{
	public static final int FRAGMENT_ID = getUUID();
	private final String telnumber = "0800 33 33 85";
	private BrowserField text_field;

	// use html here so we can use href
	private final String copytext = "<html><body bgcolor=\"#"
			+ Integer.toHexString(ResourceHelper.color_background_app)
			+ "\" style=\"font-family:arial;\"><p>Welcome to Checkline, we are here to help. You can call our Customer Service Centre toll-free from a land line on <a href=\"call\">"
			+ telnumber
			+ "</a>.</p><p>Checkline operates Monday to Saturday from 08:00 to 17:00, Sunday and Public Holidays from 09:00 to 14:00.</p><p>You can also get in touch with Customer Care by <a href=\"feedback\">Sending Us a Message</a> via the app.</p></body></html>";
	//private TextImageButton button_call;

	public ChecklineFragment()
	{
		super(VERTICAL_SCROLL | NO_HORIZONTAL_SCROLL);

		/*int padding = ResourceHelper.convert(12);
		shadow = BitmapTools.resizeTransparentBitmap(shadow, getWidth(), ResourceHelper.convert(5), Bitmap.FILTER_BILINEAR, Bitmap.SCALE_STRETCH);
		setPadding(shadow.getHeight(), padding, 0, padding); // top padding of 0 doesn't work on os7, because the browserfield seems to supersede the framgent's paint method, and ignores the shadow.
		*/
		add(new NullField(FOCUSABLE));

		/*Font font = ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(16), Ui.UNITS_px);
		//content.setMargin(shadow.getHeight(), 0, 0, 0);

		LabelField label = new LabelField("Hours", ResourceHelper.color_black, 0);
		label.setMargin(0, 0, 0, ResourceHelper.convert(10));
		label.setFont(font);
		label.setMargin(0, 0, 0, ResourceHelper.convert(10));
		add(label);

		// ---------------- trading times ------------------
		String[] tokens = new String[]{"Monday", "08:00", "17:00", "Tuesday", "08:00", "17:00", "Wednesday", "08:00", "17:00", "Thursday", "08:00", "17:00", "Friday", "08:00", "17:00", "Saturday",
				"08:00", "17:00", "Sunday", "09:00", "14:00", "Public Holidays", "09:00", "14:00"};
		int widest = 0;
		for (int i = 0; i < tokens.length; i += 3)
		{
			widest = Math.max(widest, font.getAdvance(tokens[i] + ":"));
		}

		for (int i = 0; i < tokens.length; i += 3)
		{
			HorizontalFieldManager time_manager = new HorizontalFieldManager(NO_VERTICAL_SCROLL | NO_HORIZONTAL_SCROLL);
			label = new LabelField(tokens[i] + ":", ResourceHelper.color_grey, 0);
			label.setFont(font);
			time_manager.add(label);

			int margin = widest - font.getAdvance(tokens[i] + ":") + ResourceHelper.convert(20);
			label.setMargin(0, margin, 0, 0);

			label = new LabelField(tokens[i + 1] + " - " + tokens[i + 2], ResourceHelper.color_grey, 0);
			label.setFont(font);
			time_manager.add(label);
			time_manager.setMargin(0, 0, 0, ResourceHelper.convert(10));
			add(time_manager);
		}

		// ---------------- tel number ------------------
		HorizontalFieldManager contact_manager = new HorizontalFieldManager(NO_VERTICAL_SCROLL | NO_HORIZONTAL_SCROLL);
		contact_manager.setMargin(ResourceHelper.convert(24), 0, ResourceHelper.convert(24), 0);
		label = new LabelField("Tel:", ResourceHelper.color_black, 0);
		label.setFont(font);
		label.setMargin(0, ResourceHelper.convert(20), 0, 0);
		contact_manager.add(label);

		label = new LabelField(telnumber, ResourceHelper.color_grey, 0);
		label.setFont(font);
		contact_manager.add(label);
		contact_manager.setMargin(0, 0, 0, ResourceHelper.convert(10));
		add(contact_manager);

		button_call = new TextImageButton("Call", "btn_sml_default", "btn_sml_hover");
		button_call.setChangeListener(this);
		button_call.setTextColor(ResourceHelper.color_white);
		button_call.setTextColorHover(ResourceHelper.color_primary);
		button_call.setTextColorPressed(ResourceHelper.color_primary);
		button_call.setMargin(0, 0, ResourceHelper.convert(12), ResourceHelper.convert(10));
		add(button_call);*/
	}

	protected void onVisibilityChange(boolean visible)
	{
		super.onVisibilityChange(visible);
		if (visible)
		{
			// Need to do this here, otherwise the browserfield text disappears :(
			// Need to re-initialize https://supportforums.blackberry.com/t5/Java-Development/Dealing-with-several-BrowserFields-Blank/td-p/1111787
			if (text_field != null) delete(text_field);

			Font font = ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(12), Ui.UNITS_px);

			BrowserFieldConfig browserConfig = new BrowserFieldConfig();
			browserConfig.setProperty(BrowserFieldConfig.VIEWPORT_WIDTH, new Integer(Display.getWidth()));

			text_field = new BrowserField(browserConfig);
			browserConfig.setProperty(BrowserFieldConfig.CONTROLLER, new ProtocolController(text_field)
			{
				public void handleNavigationRequest(BrowserFieldRequest request) throws Exception
				{
					String href = request.getURL().substring(request.getURL().indexOf("/") + 1); // remove the "http:/" that is prepended by the browser
					if (href.equals("call"))
					{
						call();
					}
					else if (href.equals("feedback"))
					{
						((ViewPagerScreen) UiApplication.getUiApplication().getActiveScreen()).transition(FeedbackFragment.FRAGMENT_ID, null);
					}
				}
			});
			text_field.setFont(font);
			text_field.displayContent(copytext, "");

			insert(text_field, 0);
		}
	}

	public void fieldChanged(Field field, int context)
	{
		call();
	}

	private void call()
	{
		int choice = CustomDialog.doModal(telnumber, new String[]{"Cancel", "Call"}, new int[]{Dialog.CANCEL, Dialog.YES});
		if (choice == Dialog.YES)
		{
			UiApplication.getUiApplication().invokeLater(new Runnable()
			{
				public void run()
				{
					try
					{
						PhoneArguments call = new PhoneArguments(PhoneArguments.ARG_CALL, StringUtil.remove(telnumber, " "));
						Invoke.invokeApplication(Invoke.APP_TYPE_PHONE, call);

					} catch (Exception e)
					{
//						RemoteLogger.log("ChecklineFragment", e.toString());
						InfoDialog.doModal("Error", e.getMessage(), "Okay");
					}
				}
			});
		}
	}

}
