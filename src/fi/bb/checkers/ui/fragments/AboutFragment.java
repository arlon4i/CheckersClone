package fi.bb.checkers.ui.fragments;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.decor.BackgroundFactory;
import fi.bb.checkers.helpers.ResourceHelper;
import fi.bb.checkers.helpers.StringHelper;
import fi.bb.checkers.prompts.ExternalUrlPrompt;
import fi.bb.checkers.ui.components.HyperlinkButton;
import fi.bb.checkers.ui.components.ListField;

// Using hard coded string instead of the html page due to a bug when opening the drawers, which causes the browserfield to be smaller the next time this fragment is opened.
// Doesn't seem to happen on the InfoFragment. No idea why.
public class AboutFragment extends Fragment implements FieldChangeListener
{
	public static final int FRAGMENT_ID = getUUID();

	private RichTextField text_field;
	private Font font;
	private final String text_full = StringHelper.about_full_description;
	
	private final String text_reduced = StringHelper.about_reduced_description;
	private HyperlinkButton button_read;

	private ListField button_facebook;
	private ListField button_twitter;

	public AboutFragment()
	{
		super(VERTICAL_SCROLL | NO_HORIZONTAL_SCROLL);

		setBackground(BackgroundFactory.createSolidBackground(Color.WHITE));

		int margin = ResourceHelper.convert(10);
		
		BitmapField logo = new BitmapField(ResourceHelper.getImage("logo_welcome"));
		logo.setMargin(margin, 0, 0, margin);
		add(logo);

		font = ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(17), Ui.UNITS_px);
		

		text_field = new RichTextField();
		text_field.setMargin(margin, margin, 0, margin);
		text_field.setFont(font);
		text_field.setText(text_reduced);
		add(text_field);

		button_read = new HyperlinkButton("Read More...", ResourceHelper.convert(17), false);
		button_read.setMargin(0, 0, 0, Display.getWidth() - button_read.getPreferredWidth() - ResourceHelper.convert(20));
		button_read.setChangeListener(this);
		add(button_read);

		button_facebook = new ListField("Facebook", "icon_about_facebook", "icon_about_facebook");
		button_twitter = new ListField("Twitter", "icon_about_twitter", "icon_about_twitter");

		button_facebook.setImagePadding(margin, margin);
		button_twitter.setImagePadding(margin, margin);

		button_facebook.setChangeListener(this);
		button_twitter.setChangeListener(this);

		add(button_facebook);
		add(button_twitter);
	}

	public void fieldChanged(Field arg0, int arg1)
	{
		if (arg0 == button_read)
		{
			if (button_read.getText().equals("Read More..."))
			{
				Font[] fonts = new Font[]{font, font.derive(Font.BOLD)};
				int[] offsets = new int[]{0, 85, 101, 229, 245, 478, 495, 708};
				byte[] attributes = new byte[]{0, 1, 0, 1, 0, 1, 0};

				button_read.setText("Read Less...");
				text_field.setText(text_full, offsets, attributes, fonts);
			}
			else
			{
				button_read.setText("Read More...");
				text_field.setText(text_reduced);
			}
		}
		else if (arg0 == button_facebook)
		{
			ExternalUrlPrompt.prompt(StringHelper.facebook_url);
		}
		else if (arg0 == button_twitter)
		{
			ExternalUrlPrompt.prompt(StringHelper.twitter_url);
		}
	}
}