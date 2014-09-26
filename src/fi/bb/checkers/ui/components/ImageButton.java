package fi.bb.checkers.ui.components;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Graphics;


import fi.bb.checkers.helpers.ResourceHelper;
import fi.bb.checkers.utils.BitmapTools;

public class ImageButton extends BaseButton
{
	protected Bitmap bitmap_normal;
	protected Bitmap bitmap_focus;
	protected int width;
	protected int height;

	public ImageButton(String imageUrl, String imageUrlSelected)
	{
		this(imageUrl, imageUrlSelected, -1, -1, 0);
	}

	public ImageButton(String imageUrl, String imageUrlSelected, int width)
	{
		this(imageUrl, imageUrlSelected, width, -1, 0);
	}

	public ImageButton(String imageUrl, String imageUrlSelected, int width, long style)
	{
		this(imageUrl, imageUrlSelected, width, -1, style);
	}

	public ImageButton(String imageUrl, String imageUrlSelected, int width, int height, long style)
	{
		super(style);
		if (imageUrl != null) this.bitmap_normal = ResourceHelper.getImage(imageUrl);
		if (imageUrlSelected != null) this.bitmap_focus = ResourceHelper.getImage(imageUrlSelected);

		bitmap_normal = BitmapTools.resizeTransparentBitmap(bitmap_normal, width, height, Bitmap.FILTER_BILINEAR, Bitmap.SCALE_TO_FILL);//was strecth
		bitmap_focus = BitmapTools.resizeTransparentBitmap(bitmap_focus, width, height, Bitmap.FILTER_BILINEAR, Bitmap.SCALE_TO_FILL);//was stretch

		this.width = bitmap_normal.getWidth();
		this.height = bitmap_normal.getHeight();
	}

	public int getPreferredWidth()
	{
		return width;
	}

	public int getPreferredHeight()
	{
		return height;
	}

	protected void paint(Graphics g)
	{
		if (_visible)
		{
			Bitmap image;
			if (isFocus())
			{
				image = bitmap_focus;
			}
			else
			{
				image = bitmap_normal;
			}

			if (image != null)
			{
				g.drawBitmap(0, 0, width, height, image, 0, 0);
			}
		}
	}
	public void setImage(String imageStatic, String imageHover)	
	{
		this.bitmap_normal = ResourceHelper.getImage(imageStatic);
		this.bitmap_focus = ResourceHelper.getImage(imageHover);
		
		bitmap_normal = BitmapTools.resizeTransparentBitmap(bitmap_normal, width, height, Bitmap.FILTER_BILINEAR, Bitmap.SCALE_TO_FILL);//TODO was stretched
		bitmap_focus = BitmapTools.resizeTransparentBitmap(bitmap_focus, width, height, Bitmap.FILTER_BILINEAR, Bitmap.SCALE_TO_FILL);
	}
}
