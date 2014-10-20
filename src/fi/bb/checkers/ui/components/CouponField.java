package fi.bb.checkers.ui.components;

import javax.microedition.global.Formatter;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Ui;

import fi.bb.checkers.datatypes.CampaignData;
import fi.bb.checkers.helpers.PersistentStoreHelper;
import fi.bb.checkers.helpers.ResourceHelper;
import fi.bb.checkers.helpers.StringHelper;
import fi.bb.checkers.imageloader.ImageLoader;
import fi.bb.checkers.imageloader.ImageLoaderInterface;
import fi.bb.checkers.utils.BitmapTools;
import fi.bb.checkers.utils.StringUtil;

public class CouponField extends BaseButton implements ImageLoaderInterface
{
	private static final Bitmap default_image = ResourceHelper.getImage("eezicoupon_image_error");

	private static final int text_origin = ResourceHelper.convert(120);
	private static final Bitmap tick = ResourceHelper.getImage("eezicoupons_added-to-list_tick");
	private static final Font font_save = ResourceHelper.helveticaMed().getFont(Font.PLAIN, ResourceHelper.convert(17), Ui.UNITS_px);
	private static final Font font_rand = ResourceHelper.helveticaMed().getFont(Font.PLAIN, ResourceHelper.convert(52), Ui.UNITS_px);
	private static final Font font_cent = font_rand.derive(Font.PLAIN, font_rand.getHeight() / 2, Ui.UNITS_px);
	private static final Font font_name = ResourceHelper.helveticaMed().getFont(Font.PLAIN, ResourceHelper.convert(13), Ui.UNITS_px);
	private static final Font font_desc = ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(13), Ui.UNITS_px);
	private static final int height = ResourceHelper.convert(115);
	private static final int padding_y = (height - (font_save.getHeight() + font_rand.getHeight() + font_name.getHeight() + (font_desc.getHeight() * 2))) / 2;
	private static final int padding_x = ResourceHelper.convert(12);

	private final CampaignData coupon;
	private final String price;
	private final String name;
	private final String terms_line1;
	private final String terms_line2;
	private Bitmap image;
	
	private int imageWidth;
	private int imageHeight;
	
	private String tag;
	private String orderTag;

	public CouponField(CampaignData coupon)
	{
		this(coupon, 0);
	}
	public CouponField(CampaignData coupon, long style)
	{
		super((style & Field.NON_FOCUSABLE) == Field.NON_FOCUSABLE ? Field.NON_FOCUSABLE : 0);
		this.coupon = coupon;

		int width = getPreferredWidth() - text_origin - padding_x * 2;
		if (coupon.getName() != null)
		{
			String[] lines = StringUtil.ellipsize(font_name, StringUtil.replace(coupon.getName(), "_", " "), width, 1);
			if (lines.length != 0)
				name = lines[0];
			else
				name = "";
		}
		else
		{
			name = "";
		}

		if (coupon.getValue() != null)
		{
			double value = Double.parseDouble(coupon.getValue()) / 100;
			price = new Formatter().formatNumber(value, 2);
		}
		else
		{
			price = "0.00";
		}

		if (coupon.getTerms() != null)
		{
			String[] lines = StringUtil.ellipsize(font_name, StringUtil.replace(coupon.getTerms(), "_", " "), width, 2);
			if (lines.length > 0)
				terms_line1 = lines[0];
			else
				terms_line1 = "";
			if (lines.length > 1)
				terms_line2 = lines[1];
			else
				terms_line2 = "";
		}
		else
		{
			terms_line1 = "";
			terms_line2 = "";
		}

		int margin = ResourceHelper.convert(2);
		setMargin(0, margin, 0, margin);

		setImage(default_image);
		
		if (PersistentStoreHelper.shouldLoadImages() == true)
		{
			ImageLoader.loadImage(coupon.getImageURL(), this);
		}
	}

	public int getPreferredHeight()
	{
		return height;
	}

	public int getPreferredWidth()
	{
		return Display.getWidth();
	}

	protected void paint(Graphics graphics)
	{
		graphics.setColor(ResourceHelper.color_white);
		graphics.fillRect(0, 0, text_origin, getHeight());

		int x, y;
		if (image != null)
		{
			x = padding_x + (text_origin - (padding_x * 2) - image.getWidth()) / 2;
			y = padding_y + (getPreferredHeight() - (padding_y * 2) - image.getHeight()) / 2;
			graphics.drawBitmap(x, y, image.getWidth(), image.getHeight(), image, 0, 0);
		}

		x = text_origin + padding_x;
		y = padding_y;
		graphics.setFont(font_save);
		graphics.setColor(ResourceHelper.color_primary);
		graphics.drawText("Save", x, y);

		y += font_save.getBaseline() - font_rand.getLeading() + ResourceHelper.convert(5);
		graphics.setFont(font_cent);
		int x1 = x;
		graphics.drawText(StringHelper.currency_symbol, x1, y + font_rand.getLeading() - font_cent.getLeading());
		x1 += graphics.getFont().getAdvance(StringHelper.currency_symbol);
		graphics.setFont(font_rand);
		String[] pricetokens = StringUtil.split(price, ".");
		graphics.drawText(pricetokens[0], x1, y);
		x1 += font_rand.getAdvance(pricetokens[0]);
		graphics.setFont(font_cent);
		graphics.drawText(pricetokens[1], x1, y + font_rand.getLeading() - font_cent.getLeading());

		y += font_rand.getHeight();
		graphics.setFont(font_name);
		graphics.setColor(ResourceHelper.color_black);
		graphics.drawText(name, x, y);

		y += font_name.getHeight();
		graphics.setFont(font_desc);
		graphics.setColor(ResourceHelper.color_dark_grey);
		graphics.drawText(terms_line1, x, y);

		y += font_desc.getHeight();
		graphics.drawText(terms_line2, x, y);

		if (coupon.isChecked())
		{
			graphics.setColor(ResourceHelper.color_white);
			graphics.setGlobalAlpha(160);
			graphics.fillRect(0, 0, text_origin, getHeight());
			graphics.setGlobalAlpha(255);

			x = padding_x + (text_origin - (padding_x * 2) - tick.getWidth()) / 2;
			y = padding_y + (getPreferredHeight() - (padding_y * 2) - tick.getHeight()) / 2;
			graphics.drawBitmap(x, y, tick.getWidth(), tick.getHeight(), tick, 0, 0);
		}

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
		if (image == null) return;
		/*if (image.getWidth() > text_origin - (padding_x * 2))
		{
			image = BitmapTools.resizeTransparentBitmap(image, text_origin - (padding_x * 2), 0, Bitmap.FILTER_BILINEAR, Bitmap.SCALE_STRETCH);
		}
		if (image.getHeight() > getPreferredHeight() - (padding_y * 2))
		{
			image = BitmapTools.resizeTransparentBitmap(image, 0, getPreferredHeight() - (padding_y * 2), Bitmap.FILTER_BILINEAR, Bitmap.SCALE_STRETCH);
		}*/

		if (image.getHeight() > getPreferredHeight() - (padding_y * 2))
		{
			imageHeight = getPreferredHeight() - (padding_y * 2);
		}
		else
		{
			imageHeight = 0;
		}
		
		if (image.getWidth() > text_origin - (padding_x * 2))
		{
			imageWidth = text_origin - (padding_x * 2);
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

	public Bitmap getImage()
	{
		return image;
	}

	public void repaint()
	{
		invalidate();
	}

	public CampaignData getCoupon()
	{
		return coupon;
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