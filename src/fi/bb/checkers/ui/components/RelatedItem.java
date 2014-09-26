package fi.bb.checkers.ui.components;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Graphics;
import fi.bb.checkers.datatypes.CampaignData;
import fi.bb.checkers.helpers.PersistentStoreHelper;
import fi.bb.checkers.helpers.ResourceHelper;
import fi.bb.checkers.imageloader.ImageLoader;
import fi.bb.checkers.imageloader.ImageLoaderInterface;
import fi.bb.checkers.utils.BitmapTools;

//related coupon block
public class RelatedItem extends BaseButton implements ImageLoaderInterface
{
	public static final int height = ResourceHelper.convert(110);
	private static final Bitmap default_image = ResourceHelper.getImage("eezicoupon_image_error");
	private static final Bitmap tick = ResourceHelper.getImage("eezicoupons_added-to-list_tick");
	private final CampaignData coupon;
	private Bitmap image;

	public RelatedItem(CampaignData coupon)
	{
		this.coupon = coupon;
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
		return getPreferredHeight();
	}

	protected void paint(Graphics graphics)
	{
		graphics.setColor(ResourceHelper.color_white);
		graphics.drawRect(0, 0, getWidth(), getHeight());

		int x = (getWidth() - image.getWidth()) / 2;
		int y = (getHeight() - image.getHeight()) / 2;
		graphics.drawBitmap(x, y, image.getWidth(), image.getHeight(), image, 0, 0);

		if (coupon.isChecked())
		{
			graphics.setColor(ResourceHelper.color_white);
			graphics.setGlobalAlpha(160);
			graphics.fillRect(0, 0, getWidth(), getHeight());
			graphics.setGlobalAlpha(255);

			graphics.drawBitmap(0, 0, getWidth(), getHeight(), tick, 0, 0);
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

		if (image.getWidth() > getPreferredWidth())
		{
			image = BitmapTools.resizeTransparentBitmap(image, getPreferredWidth(), 0, Bitmap.FILTER_BILINEAR, Bitmap.SCALE_STRETCH);
		}
		if (image.getHeight() > getPreferredHeight())
		{
			image = BitmapTools.resizeTransparentBitmap(image, 0, getPreferredHeight(), Bitmap.FILTER_BILINEAR, Bitmap.SCALE_STRETCH);
		}

		this.image = image;
		invalidate();
	}

	protected boolean navigationMovement(int dx, int dy, int status, int time)
	{
		if (getIndex() == 0 && dx < 0)
		{
			return true;
		}
		if (getIndex() == getManager().getFieldCount() - 1 && dx > 0)
		{
			return true;
		}

		return super.navigationMovement(dx, dy, status, time);
	};

	public CampaignData getCoupon()
	{
		return coupon;
	}
}