package fi.bb.checkers.ui.fragments;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.BackgroundFactory;
import fi.bb.checkers.helpers.ResourceHelper;
import fi.bb.checkers.ui.components.ToggleSelectionField;
import fi.bb.checkers.ui.screens.ViewPagerScreen;

public class TermsFragment extends Fragment implements FieldChangeListener
{
	public static final int FRAGMENT_ID = getUUID();
	ToggleSelectionField q1Button = new ToggleSelectionField("Checkers Terms");
	ToggleSelectionField q2Button = new ToggleSelectionField("Customer Consent");
	ToggleSelectionField q3Button = new ToggleSelectionField("Info Processing Policy");
	ToggleSelectionField q4Button = new ToggleSelectionField("Complaints and General");
	ToggleSelectionField q5Button = new ToggleSelectionField("Coupon and Voucher Issue Conditions");
	ToggleSelectionField q6Button = new ToggleSelectionField("End-User Licence Agreement");

	public TermsFragment()
	{
		super(NO_HORIZONTAL_SCROLL | VERTICAL_SCROLL);
		
		setBackground(BackgroundFactory.createSolidBackground(ResourceHelper.color_background_app));

		q1Button.setChangeListener(this);
		q2Button.setChangeListener(this);
		q3Button.setChangeListener(this);
		q4Button.setChangeListener(this);
		q5Button.setChangeListener(this);
		q6Button.setChangeListener(this);

		VerticalFieldManager seperator = new VerticalFieldManager()
		{
			protected void sublayout(int maxWidth, int maxHeight) {
				super.sublayout(Display.getWidth(), 1);
				setExtent(Display.getWidth(), 1);
			}
		};
		seperator.setBackground(BackgroundFactory.createSolidBackground(ResourceHelper.color_light_grey));
		
		add(seperator);
		add(q1Button);
		add(q2Button);
		add(q3Button);
		add(q4Button);
		add(q5Button);
		add(q6Button);
	}

	public void fieldChanged(Field field, int context)
	{
		if (field == q1Button)
		{
			displayQ1help();
		}
		else if (field == q2Button)
		{
			displayQ2help();
		}
		else if (field == q3Button)
		{
			displayQ3help();
		}
		else if (field == q4Button)
		{
			displayQ4help();
		}
		else if (field == q5Button)
		{
			displayQ5help();
		}
		else if (field == q6Button)
		{
			displayQ6help();
		}
	}

	public void displayQ1help()
	{
		((ViewPagerScreen) UiApplication.getUiApplication().getActiveScreen()).transition(InfoFragment.FRAGMENT_ID, new Object[]{"Checkers Terms", "checkersterms.html", new Boolean(true)});
	}

	public void displayQ2help()
	{
		((ViewPagerScreen) UiApplication.getUiApplication().getActiveScreen()).transition(InfoFragment.FRAGMENT_ID, new Object[]{"Customer Consent", "customerconsent.html", new Boolean(true)});
	}

	public void displayQ3help()
	{
		((ViewPagerScreen) UiApplication.getUiApplication().getActiveScreen()).transition(InfoFragment.FRAGMENT_ID, new Object[]{"Info Processing Policy", "infoprocessingpolicy.html",
				new Boolean(true)});
	}

	public void displayQ4help()
	{
		((ViewPagerScreen) UiApplication.getUiApplication().getActiveScreen()).transition(InfoFragment.FRAGMENT_ID, new Object[]{"Complaints and General", "complaintsandgeneral.html",
				new Boolean(true)});
	}

	public void displayQ5help()
	{
		((ViewPagerScreen) UiApplication.getUiApplication().getActiveScreen()).transition(InfoFragment.FRAGMENT_ID, new Object[]{"Coupon and Voucher Issue Conditions", "voucherissueconditions.html",
				new Boolean(true)});
	}

	public void displayQ6help()
	{
		((ViewPagerScreen) UiApplication.getUiApplication().getActiveScreen()).transition(InfoFragment.FRAGMENT_ID,
				new Object[]{"End-User License Agreement", "enduserlicence.html", new Boolean(true)});
	}

	protected boolean onSavePrompt()
	{
		return true;
	}
}
