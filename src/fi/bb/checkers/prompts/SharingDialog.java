package fi.bb.checkers.prompts;

import java.util.Calendar;
import java.util.Hashtable;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.container.FullScreen;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.BackgroundFactory;
import net.rim.device.api.ui.decor.BorderFactory;
import fi.bb.checkers.MainApplication;
import fi.bb.checkers.datatypes.CampaignData;
import fi.bb.checkers.datatypes.CouponCategory;
import fi.bb.checkers.helpers.FlurryHelper;
import fi.bb.checkers.helpers.ResourceHelper;
import fi.bb.checkers.helpers.SharingHelper;
import fi.bb.checkers.ui.components.ImageSubtitleButton;

public class SharingDialog extends PopupScreen
{
	public static final int SMS = 0;
	public static final int EMAIL = 1;
	public static final int BBM = 2;
	public static final int FACEBOOK = 3;
	public static final int TWITTER = 4;

	public int selected = -1;
	ImageSubtitleButton button_sms;
	ImageSubtitleButton button_email;
	ImageSubtitleButton button_bbm;
	ImageSubtitleButton button_facebook;
	ImageSubtitleButton button_twitter;

	private SharingDialog()
	{
		super(new VerticalFieldManager(FIELD_HCENTER | FIELD_VCENTER | VERTICAL_SCROLL));
		setBackground(BackgroundFactory.createSolidBackground(ResourceHelper.color_white));
		setBorder(BorderFactory.createBitmapBorder(new XYEdges(12, 12, 12, 12), Bitmap.getBitmapResource("rounded-border.png")));

		HorizontalFieldManager manager;

		manager = new HorizontalFieldManager(NO_VERTICAL_SCROLL | NO_HORIZONTAL_SCROLL);
		add(manager);
		button_sms = new ImageSubtitleButton("SMS", "icon_share_sms_default")
		{
			public void clickButton()
			{
				selected = SMS;
				close();
			}
		};
		manager.add(button_sms);
		button_email = new ImageSubtitleButton("Email", "icon_share_email_default")
		{
			public void clickButton()
			{
				selected = EMAIL;
				close();
			}

			protected boolean navigationMovement(int dx, int dy, int status, int time)
			{
				if (dy > 0)
				{
					button_facebook.setFocus();
					return true;
				}
				return super.navigationMovement(dx, dy, status, time);
			}
		};
		manager.add(button_email);

		manager = new HorizontalFieldManager(NO_VERTICAL_SCROLL | NO_HORIZONTAL_SCROLL);
		add(manager);
		button_twitter = new ImageSubtitleButton("Twitter", "icon_share_twitter_default")
		{
			public void clickButton()
			{
				selected = TWITTER;
				close();
			}

			protected boolean navigationMovement(int dx, int dy, int status, int time)
			{
				if (dy < 0)
				{
					button_sms.setFocus();
					return true;
				}
				return super.navigationMovement(dx, dy, status, time);
			}
		};
		manager.add(button_twitter);
		button_facebook = new ImageSubtitleButton("Facebook", "icon_share_facebook_default")
		{
			public void clickButton()
			{
				selected = FACEBOOK;
				close();
			}
		};
		manager.add(button_facebook);

		manager = new HorizontalFieldManager(NO_VERTICAL_SCROLL | NO_HORIZONTAL_SCROLL);
		add(manager);

		button_bbm = new ImageSubtitleButton("BBM", "icon_share_bbm_default")
		{
			public void clickButton()
			{
				selected = BBM;
				close();
			}

			protected boolean navigationMovement(int dx, int dy, int status, int time)
			{
				if (dy < 0)
				{
					button_twitter.setFocus();
					return true;
				}
				return super.navigationMovement(dx, dy, status, time);
			}
		};
		if (SharingHelper.hasBBM()) manager.add(button_bbm);

		int margin_horizontal = ResourceHelper.convert(5);
		int margin_vertical = ResourceHelper.convert(2);
		button_sms.setMargin(margin_vertical, margin_horizontal, margin_vertical, margin_horizontal);
		button_email.setMargin(margin_vertical, margin_horizontal, margin_vertical, margin_horizontal);
		button_bbm.setMargin(margin_vertical, margin_horizontal, margin_vertical, margin_horizontal);
		button_facebook.setMargin(margin_vertical, margin_horizontal, margin_vertical, margin_horizontal);
		button_twitter.setMargin(margin_vertical, margin_horizontal, margin_vertical, margin_horizontal);
	}

	public int getSelected()
	{
		return selected;
	}

	protected boolean keyDown(int keycode, int status)
	{
		if (Keypad.key(keycode) == Keypad.KEY_ESCAPE)
		{
			close();
		}
		return false;
	}

	public static int doModal()
	{
		FullScreen trans = new FullScreen();
		trans.setBackground(BackgroundFactory.createSolidTransparentBackground(Color.BLACK, 200));
		((MainApplication) UiApplication.getUiApplication()).fadeScreen(trans, false);

		SharingDialog screen = new SharingDialog();
		UiApplication.getUiApplication().pushModalScreen(screen);
		trans.close();
		return screen.getSelected();
	}
}
