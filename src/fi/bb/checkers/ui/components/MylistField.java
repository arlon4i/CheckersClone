package fi.bb.checkers.ui.components;

import javax.microedition.global.Formatter;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import fi.bb.checkers.datatypes.CampaignData;
import fi.bb.checkers.helpers.ResourceHelper;
import fi.bb.checkers.imageloader.ImageLoader;
import fi.bb.checkers.imageloader.ImageLoaderInterface;
import fi.bb.checkers.ui.fragments.DetailedCouponFragment;
import fi.bb.checkers.ui.screens.ViewPagerScreen;
import fi.bb.checkers.utils.BitmapTools;
import fi.bb.checkers.utils.StringUtil;

public class MylistField extends HorizontalFieldManager
{
	private CampaignData coupon;
	
	private TickButton tickButton;
	
	private boolean strikeThrough = false;

	public MylistField(CampaignData coupon)
	{
		this.coupon = coupon;
		int margin = ResourceHelper.convert(2);
		setMargin(0, margin, 0, margin);

		add(new CouponButton());
		add(tickButton = new TickButton());
	}

	public CampaignData getCoupon()
	{
		return coupon;
	}

	protected void paint(Graphics graphics)
	{
		super.paint(graphics);
		graphics.setColor(ResourceHelper.color_grey);
		graphics.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
	}

	public void setStrikethrough(boolean striked)
	{
		strikeThrough = striked;
		invalidate();

		//PersistentStoreHelper.mylistAdd(coupon);// save change
	}

	public void toggle()
	{
		//setStrikethrough(!coupon.isStrikethrough());
	}

	private class TickButton extends BaseButton
	{
		private final int height = ResourceHelper.convert(110);
		private final int width = ResourceHelper.convert(40);
		private final Bitmap tick = ResourceHelper.getImage("check-box_selected.png");
		private final Bitmap untick = ResourceHelper.getImage("check-box_default.png");
		private final int separator_padding = ResourceHelper.convert(15);
		private boolean checked = false;

		public int getPreferredHeight()
		{
			return height;
		}

		public int getPreferredWidth()
		{
			return width;
		}

		protected void paint(Graphics graphics)
		{
			int x = (getWidth() - tick.getWidth()) / 2;
			int y = (getHeight() - tick.getHeight()) / 2;
			if (checked)
			{
				graphics.drawBitmap(x, y, tick.getWidth(), tick.getHeight(), tick, 0, 0);
			}
			else
			{
				graphics.drawBitmap(x, y, tick.getWidth(), tick.getHeight(), untick, 0, 0);
			}

			graphics.setColor(ResourceHelper.color_grey);
			graphics.drawLine(0, separator_padding, 0, getHeight() - separator_padding);

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
			checked = !checked;
			setStrikethrough(checked);
		}
		
		public boolean isChecked()
		{
			return checked;
		}
		
		public void setChecked(boolean checked)
		{
			this.checked = checked;
			setStrikethrough(checked);
		}
	}

	private class CouponButton extends BaseButton implements ImageLoaderInterface
	{
		private final Bitmap default_image = ResourceHelper.getImage("eezicoupon_image_error");

		private final int text_origin = ResourceHelper.convert(120);
		private final Font font_name = ResourceHelper.helveticaMed().getFont(Font.PLAIN, ResourceHelper.convert(13), Ui.UNITS_px);
		private final Font font_save = ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(10), Ui.UNITS_px);
		private final int height = ResourceHelper.convert(110);
		private final int width = Display.getWidth() - ResourceHelper.convert(40);
		private final int padding_x = ResourceHelper.convert(12);
		private final int padding_y;
		private final String price;
		private final String[] name;
		private Bitmap image;

		public CouponButton()
		{
			int width = getPreferredWidth() - text_origin - padding_x * 2;
			int height = 0;
			if (coupon.getName() != null)
			{
				String[] lines = StringUtil.ellipsize(font_name, StringUtil.replace(coupon.getName(), "_", " "), width, 2);
				name = lines;

				for (int i = 0; i < name.length; i++)
				{
					height += font_name.getHeight();
				}
			}
			else
			{
				name = null;
			}

			if (coupon.getValue() != null)
			{
				double value = Double.parseDouble(coupon.getValue()) / 100;
				price = "Save R" + new Formatter().formatNumber(value, 2);
			}
			else
			{
				price = "Save R0.00";
			}
			height += font_save.getHeight();
			padding_y = (this.height - height) / 2;

			setImage(default_image);
			ImageLoader.loadImage(coupon.getImageURL(), this);
		}
		public int getPreferredHeight()
		{
			return height;
		}

		public int getPreferredWidth()
		{
			return width;
		}

		protected void paint(Graphics graphics)
		{
			graphics.setColor(ResourceHelper.color_white);
			graphics.fillRect(0, 0, text_origin, getHeight());

			int x, y;
			if (image != null)
			{
				x = padding_x + (text_origin - (padding_x * 2) - image.getWidth()) / 2;
				y = (getPreferredHeight() - image.getHeight()) / 2;
				graphics.drawBitmap(x, y, image.getWidth(), image.getHeight(), image, 0, 0);
			}

			x = text_origin + padding_x;
			y = padding_y;
			if (name != null)
			{
				graphics.setFont(font_name);
				graphics.setColor(ResourceHelper.color_black);
				for (int i = 0; i < name.length; i++)
				{
					graphics.drawText(name[i], x, y);
					y += font_name.getHeight();
				}
			}

			graphics.setFont(font_save);
			graphics.setColor(ResourceHelper.color_dark_grey);
			graphics.drawText(price, x, y);

			if (strikeThrough == true)
			{
				graphics.setColor(ResourceHelper.color_white);
				graphics.setGlobalAlpha(125);
				graphics.fillRect(0, 0, getWidth(), getHeight());
				graphics.setGlobalAlpha(255);
			}

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
			if (image.getWidth() > text_origin - (padding_x * 2))
			{
				image = BitmapTools.resizeTransparentBitmap(image, text_origin - (padding_x * 2), 0, Bitmap.FILTER_BILINEAR, Bitmap.SCALE_STRETCH);
			}
			if (image.getHeight() > getPreferredHeight())
			{
				image = BitmapTools.resizeTransparentBitmap(image, 0, getPreferredHeight(), Bitmap.FILTER_BILINEAR, Bitmap.SCALE_STRETCH);
			}

			this.image = image;
			invalidate();
		}

		public void clickButton()
		{
			super.clickButton();
			toggle();
			((ViewPagerScreen) UiApplication.getUiApplication().getActiveScreen()).transition(DetailedCouponFragment.FRAGMENT_ID, new Object[]{coupon});
		}
	}
	
	public boolean isChecked()
	{
		return  tickButton.isChecked();
	}
	
	public void setChecked(boolean checked)
	{
		tickButton.setChecked(checked);
	}
}
