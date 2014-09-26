package fi.bb.checkers.ui.components;

import java.util.Calendar;
import java.util.Date;
import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.util.DateTimeUtilities;
import fi.bb.checkers.datatypes.InboxMessage;
import fi.bb.checkers.helpers.ResourceHelper;
import fi.bb.checkers.imageloader.ImageLoader;
import fi.bb.checkers.imageloader.ImageLoaderInterface;
import fi.bb.checkers.ui.fragments.InboxMessageFragment;
import fi.bb.checkers.ui.screens.ViewPagerScreen;
import fi.bb.checkers.utils.BitmapTools;
import fi.bb.checkers.utils.StringUtil;

public class InboxField extends BaseButton implements ImageLoaderInterface
{
	private static final Bitmap default_image = ResourceHelper.getImage("eezicoupon_image_error");

	private static final int text_origin_x = ResourceHelper.convert(50);
	private static final Font font_name = ResourceHelper.helveticaMed().getFont(Font.PLAIN, ResourceHelper.convert(13), Ui.UNITS_px);
	private static final Font font_desc = ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(13), Ui.UNITS_px);
	private static final int height = ResourceHelper.convert(50);
	private static final int text_origin_y = (height - (font_name.getHeight() + font_desc.getHeight())) / 2;
	private static final int padding = ResourceHelper.convert(6);

	private final InboxMessage inbox_message;
	private final String description;
	private final String date;
	private Bitmap image;

	public InboxField(InboxMessage inbox_message)
	{
		this.inbox_message = inbox_message;
		this.description = StringUtil.ellipsize(font_desc, inbox_message.description, getPreferredWidth() - (text_origin_x * 2), 1)[0];
		this.date = getDate(inbox_message.date_recieved);

		setImage(default_image);

		// use checkers icon by default
		if (inbox_message.thumbnail_url == null)
			setImage(ResourceHelper.getImage("icon"));
		else
			ImageLoader.loadImage(inbox_message.thumbnail_url, this);
	}

	public int getPreferredHeight()
	{
		return height;
	}

	public int getPreferredWidth()
	{
		return Display.getWidth();
	}

	public void setImage(Bitmap image)
	{
		if (image == null) return;
		if (image.getWidth() > text_origin_x - (padding * 2))
		{
			image = BitmapTools.resizeTransparentBitmap(image, text_origin_x - (padding * 2), 0, Bitmap.FILTER_BILINEAR, Bitmap.SCALE_STRETCH);
		}
		if (image.getHeight() > getPreferredHeight() - (padding * 2))
		{
			image = BitmapTools.resizeTransparentBitmap(image, 0, getPreferredHeight() - (padding * 2), Bitmap.FILTER_BILINEAR, Bitmap.SCALE_STRETCH);
		}

		this.image = image;
		invalidate();
	}

	protected void paint(Graphics graphics)
	{
		int y = (getPreferredHeight() - image.getHeight()) / 2;
		graphics.drawBitmap(padding, y, image.getWidth(), image.getHeight(), image, 0, 0);

		y = text_origin_y;
		graphics.setColor(ResourceHelper.color_black);
		graphics.setFont(inbox_message.read ? font_name : font_name.derive(Font.BOLD));
		graphics.drawText(StringUtil.ellipsize(graphics.getFont(), inbox_message.title, getPreferredWidth() - (text_origin_x * 2), 1)[0], text_origin_x, y);

		y += font_name.getHeight();
		graphics.setColor(ResourceHelper.color_grey);
		graphics.setFont(font_desc);
		graphics.drawText(description, text_origin_x, y);

		graphics.drawText(date, getPreferredWidth() - font_desc.getAdvance(date) - padding, padding);

		graphics.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);

		if (_focus)
		{
			graphics.setColor(ResourceHelper.color_primary);
			int line_width = ResourceHelper.convert(2);
			for (int i = 0; i < line_width; i++)
			{
				graphics.drawRect(i, i, getWidth() - (i * 2), getHeight() - (i * 2));
			}
		}
	}

	public void clickButton()
	{
		((ViewPagerScreen) UiApplication.getUiApplication().getActiveScreen()).transition(InboxMessageFragment.FRAGMENT_ID, new Object[]{inbox_message});
	}

	public InboxMessage getMessage()
	{
		return inbox_message;
	}

	private static String getDate(long date)
	{
		Calendar c = Calendar.getInstance();
		DateTimeUtilities.zeroCalendarTime(c);

		Date today = c.getTime();
		long time_in_millis = today.getTime();

		if (date > time_in_millis) return "Today";

		time_in_millis -= DateTimeUtilities.ONEDAY;
		if (date > time_in_millis) return "Yesterday";

		return new SimpleDateFormat("dd MMM").format(new Date(date));
	}
}
