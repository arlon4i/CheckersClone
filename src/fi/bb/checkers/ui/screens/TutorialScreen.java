package fi.bb.checkers.ui.screens;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Characters;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.container.FullScreen;
import net.rim.device.api.ui.decor.BackgroundFactory;
import fi.bb.checkers.MainApplication;
import fi.bb.checkers.helpers.ResourceHelper;
import fi.bb.checkers.utils.StringUtil;

public class TutorialScreen extends FullScreen
{
	public static final int HOME = 0;
	public static final int PROFILE = 1;
	public static final int COUPONS = 2;
	public static final int SPECIALS = 3;
	public static final int LIST = 4;
	public static final int FINDSTORE = 5;

	private static final Font font_title = ResourceHelper.helveticaMed().getFont(Font.PLAIN, ResourceHelper.convert(20), Ui.UNITS_px);
	private static final Font font_subtitle = ResourceHelper.helveticaMed().getFont(Font.PLAIN, ResourceHelper.convert(16), Ui.UNITS_px);
	private static final Font font_body = ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(16), Ui.UNITS_px);
	private static final int text_area = Display.getWidth() - ResourceHelper.convert(50);

	private int tutorial;

	private TutorialScreen(int tutorial)
	{
		super(Manager.VERTICAL_SCROLL | Manager.VERTICAL_SCROLLBAR);
		setBackground(BackgroundFactory.createSolidTransparentBackground(0xffffff, 0));

		this.tutorial = tutorial;
	}

	// draw instead of using fields, because the arrows causes problems with layouts
	protected void paint(Graphics graphics)
	{
		graphics.setColor(ResourceHelper.color_white);
		// graphics.setGlobalAlpha(200);
		graphics.fillRect(0, 0, getWidth(), getHeight());
		// graphics.setGlobalAlpha(255);

		String heading = "";
		String body = "";
		switch (tutorial)
		{
			case HOME :
				heading = "Welcome to your Home Screen";
				body = "Get easy access to all the best deals at Checkers. Save over R2000 with EeziCoupons, find your closest store and much more from Checkers.";

				Bitmap square = ResourceHelper.getImage("icon_tutorial_menu");
				int y = 0;
				graphics.drawBitmap(0, y, square.getWidth(), square.getHeight(), square, 0, 0);

				square = ResourceHelper.getImage("icon_tutorial_profile");
				graphics.drawBitmap(getWidth() - square.getWidth(), y, square.getWidth(), square.getHeight(), square, 0, 0);

				y += square.getHeight();
				Bitmap arrow = ResourceHelper.getImage("left_arrow");
				graphics.drawBitmap(ResourceHelper.convert(5), y, arrow.getWidth(), arrow.getHeight(), arrow, 0, 0);

				graphics.setColor(ResourceHelper.color_black);
				graphics.setFont(font_subtitle);
				graphics.drawText("Access Additional Features", ResourceHelper.convert(5) + arrow.getWidth(), y + arrow.getHeight() - graphics.getFont().getHeight() / 2);
				graphics.setColor(ResourceHelper.color_dark_grey);
				graphics.setFont(font_body);
				graphics.drawText("Click on your Menu Button", ResourceHelper.convert(5) + arrow.getWidth(), y + arrow.getHeight() + graphics.getFont().getHeight() / 2);

				arrow = ResourceHelper.getImage("right_arrow");
				graphics.drawBitmap(getWidth() - arrow.getWidth() - ResourceHelper.convert(5), y, arrow.getWidth(), arrow.getHeight(), arrow, 0, 0);

				graphics.setColor(ResourceHelper.color_black);
				graphics.setFont(font_subtitle);
				String str = "Access Profile, My List or WiCode";
				graphics.drawText(str, getWidth() - arrow.getWidth() - ResourceHelper.convert(5) - graphics.getFont().getAdvance(str), y + arrow.getHeight() - graphics.getFont().getHeight() / 2);
				graphics.setColor(ResourceHelper.color_dark_grey);
				graphics.setFont(font_body);
				str = "Click on your Profile Button";
				graphics.drawText(str, getWidth() - arrow.getWidth() - ResourceHelper.convert(5) - graphics.getFont().getAdvance(str), y + arrow.getHeight() + graphics.getFont().getHeight() / 2);

				break;

			case PROFILE :
				heading = "This is your Profile";
				body = "For your convenience, this is where you will find a list of all your selected EeziCoupons.\n\nThe unique WiCode number that you need to redeem your EeziCoupons is also located here. Simply click on your Profile button to retreive your WiCode whenever you need it.";
				break;

			case COUPONS :
				heading = "Your EeziCoupon Screen";
				body = "Save over R2000 with instant shopping discounts on your cell phone with the latest EeziCoupons available from Checkers. Use your WiCode number to redeem all available coupons, or select your favourites to add to your list. You have the option to turn on the images using your hard menu button.";
				break;

			case SPECIALS :
				heading = "Your Specials Screen";
				body = "Easy access to all the best deals currently available at Checkers. Start saving now. You have the option to turn on the images using your hard menu button.";
				break;

			case LIST :
				heading = "Your List";
				body = "My List makes it easy to manage your personal selection of EeziCoupons.\n\nAdd, delete and share your EeziCoupons.\n\nRedeem your entire list of EeziCoupons by tapping on Redeem All.";//"My List makes it easy to manage your personal selection of EeziCoupons.\n\nAdd, delete, share or Redeem EeziCoupons.\n\nRedeem your entire list of EeziCoupons by tapping on Redeem All.";
				break;

			case FINDSTORE :
				heading = "Find a Store";
				body = "Here you can locate all the Checkers and Shoprite Stores.";
				break;
		}

		graphics.setFont(font_title);
		graphics.setColor(ResourceHelper.color_primary);
		String[] lines = StringUtil.ellipsize(font_title, heading, text_area, Integer.MAX_VALUE);

		int y = ResourceHelper.convert(20);
		for (int i = 0; i < lines.length; i++)
		{
			graphics.drawText(lines[i], (getWidth() - font_title.getAdvance(lines[i])) / 2, y);
			y += font_title.getHeight();
		}

		graphics.setFont(font_body);
		graphics.setColor(ResourceHelper.color_dark_grey);
		lines = StringUtil.ellipsize(font_body, body, text_area, Integer.MAX_VALUE);

		y += ResourceHelper.convert(10);
		for (int i = 0; i < lines.length; i++)
		{
			graphics.drawText(lines[i], (getWidth() - font_body.getAdvance(lines[i])) / 2, y);
			y += font_body.getHeight();
		}

		if (tutorial == FINDSTORE)
		{
			int x = (getWidth() - ResourceHelper.convert(20) - font_body.getAdvance("Checkers Stores")) / 2;

			graphics.setFont(font_body);
			graphics.setColor(ResourceHelper.color_checkers_teal);
			y += ResourceHelper.convert(15);
			graphics.fillArc(x, y + (font_body.getHeight() - ResourceHelper.convert(8)) / 2, ResourceHelper.convert(8), ResourceHelper.convert(8), 0, 360);
			graphics.setColor(ResourceHelper.color_black);
			graphics.drawText("Checkers Stores", x + ResourceHelper.convert(20), y);
			y += font_body.getHeight();

			graphics.setColor(ResourceHelper.color_shoprite_red);
			graphics.fillArc(x, y + (font_body.getHeight() - ResourceHelper.convert(8)) / 2, ResourceHelper.convert(8), ResourceHelper.convert(8), 0, 360);
			graphics.setColor(ResourceHelper.color_black);
			graphics.drawText("Shoprite Stores", x + ResourceHelper.convert(20), y);
			y += font_body.getHeight();

			body = "Use the hard menu button to access key areas of the App as well as additional features.";
			lines = StringUtil.ellipsize(font_body, body, text_area, Integer.MAX_VALUE);

			graphics.setColor(ResourceHelper.color_dark_grey);
			y += ResourceHelper.convert(15);
			for (int i = 0; i < lines.length; i++)
			{
				graphics.drawText(lines[i], (getWidth() - font_body.getAdvance(lines[i])) / 2, y);
				y += font_body.getHeight();
			}
		}
	}
	protected boolean navigationClick(int status, int time)
	{
		close();
		return true;
	}

	protected boolean keyChar(char c, int status, int time)
	{
		if (c == Characters.ESCAPE)
		{
			close();
			return true;
		}
		return super.keyChar(c, status, time);
	}

	public static void push(int tutorial)
	{
		MainApplication app = (MainApplication) UiApplication.getUiApplication();
		app.fadeScreen(new TutorialScreen(tutorial), false);
	}
}
