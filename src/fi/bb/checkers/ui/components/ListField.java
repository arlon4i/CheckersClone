package fi.bb.checkers.ui.components;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Ui;
import fi.bb.checkers.helpers.ResourceHelper;

public class ListField extends BaseButton
{
	private String title;
	private Bitmap img;
	private Bitmap img_hover;
	private int image_padding_left = 0;
	private int image_padding_right = 0;

	public ListField(String title, String image, String image_hover)
	{
		setFont(ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(17), Ui.UNITS_px));

		if (image != null) img = ResourceHelper.getImage(image);
		if (image_hover != null) img_hover = ResourceHelper.getImage(image_hover);
		this.title = title;
	}

	public void setImagePadding(int left, int right)
	{
		image_padding_left = left;
		image_padding_right = right;
	}

	protected void drawFocus(Graphics graphics, boolean on)
	{

	}

	public int getPreferredHeight()
	{
		return ResourceHelper.convert(40);
	}

	public int getPreferredWidth()
	{
		return getManager().getPreferredWidth();
	}

	protected void paint(Graphics graphics)
	{
		if (isFocus())
		{
			graphics.setColor(ResourceHelper.color_primary);
			graphics.fillRect(0, 0, getWidth(), getHeight());
		}
		else
		{
			graphics.setColor(ResourceHelper.color_white);
			graphics.fillRect(0, 0, getWidth(), getHeight());
		}

		int x = image_padding_left;
		int y;
		if (isFocus() && img_hover != null)
		{
			y = (getHeight() - img_hover.getHeight()) / 2;
			graphics.drawBitmap(x, y, img_hover.getWidth(), img_hover.getHeight(), img_hover, 0, 0);
			x += img_hover.getWidth();
			x += image_padding_right;
		}
		else if (img != null)
		{
			y = (getHeight() - img.getHeight()) / 2;
			graphics.drawBitmap(x, y, img.getWidth(), img.getHeight(), img, 0, 0);
			x += img.getWidth();
			x += image_padding_right;
		}
		else
		{
			x = ResourceHelper.convert(10);
		}

		if (isFocus())
			graphics.setColor(ResourceHelper.color_white);
		else
			graphics.setColor(ResourceHelper.color_black);
		y = (getHeight() - getFont().getHeight()) / 2;
		graphics.drawText(title, x, y);

		graphics.setColor(ResourceHelper.color_light_grey);
		graphics.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
	}

	protected void onFocus(int direction)
	{
		super.onFocus(direction);
		invalidate();
	}

	protected void onUnfocus()
	{
		super.onUnfocus();
		invalidate();
	}
}
