package fi.bb.checkers.ui.components;

import fi.bb.checkers.helpers.ResourceHelper;
import fi.bb.checkers.utils.StringUtil;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Ui;

public class ImageSubtitleButton extends BaseButton
{
	private static final int FOCUS_THINKNESS = ResourceHelper.convert(2);
	String[] title;
	Bitmap image_default;

	int height;

	public ImageSubtitleButton(String title, String image_default)
	{
		this.image_default = ResourceHelper.getImage(image_default);

		setFont(ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(13), Ui.UNITS_px));
		setTitle(title);
	}

	public void setTitle(String title)
	{
		this.title = StringUtil.ellipsize(getFont(), title, getPreferredWidth(), 2);
		height = image_default.getHeight() + (getFont().getHeight() * this.title.length) + (FOCUS_THINKNESS * 2);

		updateLayout();
		invalidate();
	}

	public int getPreferredWidth()
	{
		return ResourceHelper.convert(100);
	}

	public int getPreferredHeight()
	{
		return height;
	}

	protected void paint(Graphics graphics)
	{
		int widthFocus = 0;
		int heightFocus = 0;
		int tempFontWidth = 0;
		
		int x = (getWidth() - image_default.getWidth()) / 2;
		int y = (getPreferredHeight() - (image_default.getHeight() + title.length*getFont().getHeight()))/2;
		
		graphics.drawBitmap(x, y, image_default.getWidth(), image_default.getHeight(), image_default, 0, 0);

		graphics.setColor(ResourceHelper.color_black);
		y = image_default.getHeight();
		for (int i = 0; i < title.length; i++)
		{
			tempFontWidth = getFont().getAdvance(title[i]);
			if (tempFontWidth > widthFocus)
			{
				widthFocus = tempFontWidth;
			}
			
			x = (getWidth() - tempFontWidth) / 2;
			graphics.drawText(title[i], x, y);
			y += getFont().getHeight();
		}
		
		widthFocus = Math.max(widthFocus, image_default.getWidth());//most probs prefferedWidth
		
		if ((widthFocus + ResourceHelper.convert(5)*2) < getPreferredWidth())
		{
			widthFocus += ResourceHelper.convert(5)*2;
		}
		
		heightFocus = getPreferredHeight();
		
		if (_focus)
		{
			graphics.setColor(ResourceHelper.color_primary);
			x = (getPreferredWidth() - widthFocus)/2;
			y = 0;
			for (int i = 0; i < FOCUS_THINKNESS; i++)
			{
				//graphics.drawRect(x + i, y + i, image_default.getWidth() - (i * 2), image_default.getHeight() - (i * 2));//was around images only
				graphics.drawRect(x + i, y + i, widthFocus - (i * 2), heightFocus - (i * 2));
			}
		}
	}
}
