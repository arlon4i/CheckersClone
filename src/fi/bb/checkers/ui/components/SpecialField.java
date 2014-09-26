package fi.bb.checkers.ui.components;

import java.util.Date;

import javax.microedition.global.Formatter;

import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.io.http.HttpDateParser;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Ui;
import fi.bb.checkers.datatypes.CampaignData;
import fi.bb.checkers.helpers.PersistentStoreHelper;
import fi.bb.checkers.helpers.ResourceHelper;
import fi.bb.checkers.imageloader.ImageLoader;
import fi.bb.checkers.imageloader.ImageLoaderInterface;
import fi.bb.checkers.utils.BitmapTools;
import fi.bb.checkers.utils.StringUtil;
import fi.bb.socialsharing.logger.RemoteLogger;

/**
 * A container for a special.
 * 
 * @author kevin
 * 
 */
public class SpecialField extends BaseButton implements ImageLoaderInterface
{

	private static final Font font_rand = ResourceHelper.helveticaMed().getFont(Font.PLAIN, ResourceHelper.convert(52), Ui.UNITS_px);
	private static final Font font_cent = font_rand.derive(Font.PLAIN, font_rand.getHeight() / 2, Ui.UNITS_px);
	private static final Font font_unit = ResourceHelper.helveticaMed().getFont(Font.PLAIN, ResourceHelper.convert(11), Ui.UNITS_px);
	private static final Font font_name = ResourceHelper.helveticaMed().getFont(Font.PLAIN, ResourceHelper.convert(14), Ui.UNITS_px);
	private static final Font font_date = ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(13), Ui.UNITS_px);

	private static final int padding = ResourceHelper.convert(10);
	private final int origin_x;
	private final int origin_y;
	private final long position;

	CampaignData special;
	private Bitmap image;
	private final String price_rand;
	private final String price_cent;
	private final String unit;
	private final String name_line1;
	private final String name_line2;
	private final String date;

	private final int height;

	private int imageWidth;
	private int imageHeight;
	
	private String tag;
	private String orderTag;
	
	public SpecialField(CampaignData special, long style)
	{
		super((style & Field.NON_FOCUSABLE) == Field.NON_FOCUSABLE ? Field.NON_FOCUSABLE : 0);
		this.special = special;
		position = style;
		setImage(ResourceHelper.getImage("eezicoupon_image_error"));

		String price = special.getValue();
		if (price != null)
		{
			double value = Double.parseDouble(price) / 100;
			price = new Formatter().formatNumber(value, 2);
		}
		else
		{
			price = "0.00";
		}

		int index = price.indexOf('.');
		this.price_rand = price.substring(0, index);
		this.price_cent = price.substring(index + 1);

		this.unit = "";

		// line wrap the name
		if (special.getName() != null)
		{
			int width = getPreferredWidth() - getPreferredHeight() - padding * 2;
			String[] lines = StringUtil.ellipsize(font_name, StringUtil.replace(special.getName(), "_", " "), width, 2);
			if (lines.length > 0)
				name_line1 = lines[0];
			else
				name_line1 = "";
			if (lines.length > 1)
				name_line2 = lines[1];
			else
				name_line2 = "";
		}
		else
		{
			name_line1 = "";
			name_line2 = "";
		}

		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
	
		//Server date format yyyyMMdd
		String newDate = "";
		String formattedDate = "";
		
		try
		{
			newDate = special.getExpireDate().substring(0,4) + "-" + special.getExpireDate().substring(4,6) + "-" + special.getExpireDate().substring(6) ;
			formattedDate = formatter.format(new Date(HttpDateParser.parse(newDate)));
		}
		catch(Exception e)
		{
			formattedDate = special.getExpireDate();
		}
		
		this.date = "Valid until: " + formattedDate;

		int margin = ResourceHelper.convert(2);
		setMargin(0, margin, 0, margin);

		height = (padding * 2) + font_rand.getHeight() + font_name.getHeight() + font_date.getHeight() + (name_line2.equals("") ? 0 : font_name.getHeight());

		if ((style & FIELD_RIGHT) == FIELD_RIGHT)
			origin_x = padding;//image.getWidth() + padding;
		else
			origin_x = image.getWidth() + padding;//padding;
		
		/*if ((style & FIELD_RIGHT) == FIELD_RIGHT)
			origin_x = padding;
		else
			origin_x = image.getWidth() + padding;*/

		origin_y = padding + (getPreferredHeight() - height) / 2;

		if (PersistentStoreHelper.shouldLoadImages() == true)
		{
			ImageLoader.loadImage(special.getImageURL(), this);
		}
	}

	public int getPreferredWidth()
	{
		return ResourceHelper.convert(316);
	}

	public int getPreferredHeight()
	{
		return ResourceHelper.convert(115);
	}

	protected void paint(Graphics graphics)
	{
		int x, y;

		if ((position & FIELD_RIGHT) == FIELD_RIGHT)
		{
			x = getWidth()-image.getWidth();//(getHeight() - image.getWidth()) / 2;
			y = (getHeight() - image.getHeight()) / 2;
			graphics.drawBitmap(x, y, image.getWidth(), image.getHeight(), image, 0, 0);
		}
		else if ((position & FIELD_LEFT) == FIELD_LEFT)
		{
			x = 0;//getWidth() - getHeight() + ((getHeight() - image.getWidth()) / 2);
			y = (getHeight() - image.getHeight()) / 2;
			graphics.drawBitmap(x, y, image.getWidth(), image.getHeight(), image, 0, 0);
		}

		x = origin_x;
		y = origin_y;

		graphics.setColor(ResourceHelper.color_primary);
		graphics.setFont(font_cent);
		graphics.drawText("R", x, y + font_rand.getLeading() - font_cent.getLeading());
		graphics.setFont(font_rand);
		graphics.drawText(price_rand, x + font_cent.getAdvance("R"), y);

		x += font_rand.getAdvance(price_rand) + font_cent.getAdvance("R");
		y += font_rand.getLeading() - font_cent.getLeading();
		graphics.setFont(font_cent);
		graphics.drawText(price_cent, x, y);

		y = origin_y + font_rand.getBaseline() - font_unit.getBaseline();
		graphics.setFont(font_unit);
		graphics.drawText(unit, x, y);

		x = origin_x;
		y = origin_y + font_rand.getHeight();
		graphics.setColor(ResourceHelper.color_black);
		graphics.setFont(font_name);
		graphics.drawText(name_line1, x, y);

		if (!name_line2.equals(""))
		{
			y += font_name.getHeight();
			graphics.drawText(name_line2, x, y);
		}

		y += font_name.getHeight();
		graphics.setFont(font_date);
		graphics.setColor(ResourceHelper.color_dark_grey);
		graphics.drawText(date, x, y);

		graphics.setColor(ResourceHelper.color_grey);
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
	
	public void setImage(Bitmap image)
	{
		if (image.getHeight() > getPreferredHeight())
		{
			imageHeight = getPreferredHeight();
			//image = BitmapTools.resizeTransparentBitmap(image, 0, getPreferredHeight(), Bitmap.FILTER_BILINEAR, Bitmap.SCALE_STRETCH);
		}
		else
		{
			imageHeight = 0;
		}
		
		if (image.getWidth() > getPreferredWidth())
		{
			imageWidth = getPreferredWidth();
			//image = BitmapTools.resizeTransparentBitmap(image, getPreferredWidth(), 0, Bitmap.FILTER_BILINEAR, Bitmap.SCALE_STRETCH);
		}
		else
		{
			imageWidth = 0;
		}
		
		if (imageWidth>0 || imageHeight > 0)
		{
			image = BitmapTools.resizeTransparentBitmap(image, imageWidth, imageHeight, Bitmap.FILTER_BILINEAR, Bitmap.SCALE_STRETCH);
		}

		this.image = image;
		invalidate();
	}

	public CampaignData getSpecial()
	{
		return special;
	}
	
	public String getTag()
	{
		return tag;
	}
	
	public void setTag(String tag)
	{
		this.tag = tag;
	}
	
	public String getOrderTag()
	{
		return orderTag;
	}
	
	public void setOrderTag(String orderTag)
	{
		this.orderTag = orderTag;
	}
}
