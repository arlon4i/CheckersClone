package fi.bb.checkers.ui.fragments;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.decor.BackgroundFactory;
import fi.bb.checkers.helpers.ResourceHelper;
import fi.bb.checkers.ui.components.ToggleSelectionField;
import fi.bb.checkers.ui.screens.ViewPagerScreen;

public class HelpFragment extends Fragment implements FieldChangeListener
{
	public static final int FRAGMENT_ID = getUUID();

	ToggleSelectionField q1Button = new ToggleSelectionField("How do I contact Checkers?",3);
	ToggleSelectionField q2Button = new ToggleSelectionField("How do EeziCoupons work?",3);
	ToggleSelectionField q3Button = new ToggleSelectionField("What is an EeziCoupon WiCode?",3);
	ToggleSelectionField q4Button = new ToggleSelectionField("How long does my EeziCoupon WiCode last?",3);
	//ToggleSelectionField q5Button = new ToggleSelectionField("How do I refresh my EeziCoupon WiCode?",3);
	ToggleSelectionField q6Button = new ToggleSelectionField("What can I do if an error occurs with my EeziCoupon WiCode?",3);
	ToggleSelectionField q7Button = new ToggleSelectionField("What is My List?",3);
	ToggleSelectionField q8Button = new ToggleSelectionField("How do I clear or delete items from My List?",3);
	ToggleSelectionField q9Button = new ToggleSelectionField("What if I have no data connection?",3);
	ToggleSelectionField q10Button = new ToggleSelectionField("How can I share Checkers EeziCoupons and Specials with others?",3);
	ToggleSelectionField q11Button = new ToggleSelectionField("How do I reset my confirmation code?",3);
	ToggleSelectionField q12Button = new ToggleSelectionField("Why do I need to allow the app to use my current location?",3);
	ToggleSelectionField q13Button = new ToggleSelectionField("What happens when I deny Checkers permission to use my current location?",3);
	ToggleSelectionField q14Button = new ToggleSelectionField("If I did not grant Checkers access to use my location initially, how do I change that?",3);
	ToggleSelectionField q15Button = new ToggleSelectionField("I am a Cell C customer who is experiencing app issues at times, what can I do?",3);

	public HelpFragment()
	{
		super(NO_HORIZONTAL_SCROLL | VERTICAL_SCROLL);
		
		setBackground(BackgroundFactory.createSolidBackground(ResourceHelper.color_background_app));

		q1Button.setTag(1);
		q2Button.setTag(2);
		q3Button.setTag(3);
		q4Button.setTag(4);
		q6Button.setTag(6);
		q7Button.setTag(7);
		q8Button.setTag(8);
		q9Button.setTag(9);
		q10Button.setTag(10);
		q11Button.setTag(11);
		q12Button.setTag(12);
		q13Button.setTag(13);
		q14Button.setTag(14);
		q15Button.setTag(15);
		
		q1Button.setChangeListener(this);
		q2Button.setChangeListener(this);
		q3Button.setChangeListener(this);
		q4Button.setChangeListener(this);
		//q5Button.setChangeListener(this);
		q6Button.setChangeListener(this);
		q7Button.setChangeListener(this);
		q8Button.setChangeListener(this);
		q9Button.setChangeListener(this);
		q10Button.setChangeListener(this);
		q11Button.setChangeListener(this);
		q12Button.setChangeListener(this);
		q13Button.setChangeListener(this);
		q14Button.setChangeListener(this);
		q15Button.setChangeListener(this);

		add(q1Button);
		add(q2Button);
		add(q3Button);
		add(q4Button);
		//add(q5Button);
		add(q6Button);
		add(q7Button);
		add(q8Button);
		add(q9Button);
		add(q10Button);
		add(q11Button);
		add(q12Button);
		add(q13Button);
		add(q14Button);
		add(q15Button);
	}

	public void fieldChanged(Field field, int context)
	{
		ToggleSelectionField button = (ToggleSelectionField) field;
		((ViewPagerScreen) UiApplication.getUiApplication().getActiveScreen()).transition(InfoFragment.FRAGMENT_ID, new Object[]{"Help", "help" + button.getTag() + ".html",
				new Boolean(true)});
	}

	protected boolean onSavePrompt()
	{
		return true;
	}
}
