package fi.bb.checkers.ui.components;

import fi.bb.checkers.helpers.ResourceHelper;
import fi.bb.checkers.utils.BitmapTools;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Ui;

public class TextImageButton extends ImageButton
{
	protected String text;
	protected int text_color = Color.BLACK;
	protected int text_color_hover = Color.BLACK;
	protected int text_color_pressed = Color.BLACK;

	public TextImageButton(String text, String imageStatic, String imageHover)
	{
		this(text, imageStatic, imageHover, 0);
		setFont(ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(14), Ui.UNITS_px));
	}
	
	public TextImageButton(String text, String imageStatic, String imageHover, Font font)
	{
		this(text, imageStatic, imageHover, 0);
		setFont(font);
	}

	public TextImageButton(String text, String imageStatic, String imageHover, long style)
	{
		super(imageStatic, imageHover, -1, -1, style);
		setFont(ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(17), Ui.UNITS_px));
		setText(text);
	}
	
	public TextImageButton(String text, String imageStatic, String imageHover, long style, int width)
	{
		super(imageStatic, imageHover, width, -1, style);
		setFont(ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(17), Ui.UNITS_px));
		setText(text);
	}
	
	protected void paint(Graphics g)
	{
		super.paint(g);

		Font font = g.getFont();

		if (_active)
			g.setColor(text_color_pressed);
		else if (isFocus())
			g.setColor(text_color_hover);
		else
			g.setColor(text_color);
		g.drawText(text, (width - font.getAdvance(text)) / 2, (height - font.getHeight()/*(font.getBaseline() + font.getDescent())*/) / 2);
	}

	public void setText(String text)
	{
		this.text = text;

		width = Math.max(getFont().getAdvance(text) + ResourceHelper.convert(10), width); // always have at least 5 on either side of the text

		if (width > getWidth())
		{
			// resize button to fit larger label
			bitmap_normal = BitmapTools.resizeTransparentBitmap(bitmap_normal, width, height, Bitmap.FILTER_BILINEAR, Bitmap.SCALE_STRETCH);
			bitmap_focus = BitmapTools.resizeTransparentBitmap(bitmap_focus, width, height, Bitmap.FILTER_BILINEAR, Bitmap.SCALE_STRETCH);
			updateLayout();
		}
		invalidate();
	}

	public String getText()
	{
		return text;
	}

	public void setTextColor(int color)
	{
		text_color = color;
		invalidate();
	}

	public void setTextColorHover(int color)
	{
		text_color_hover = color;
		invalidate();
	}

	public void setTextColorPressed(int color)
	{
		text_color_pressed = color;
		invalidate();
	}
}