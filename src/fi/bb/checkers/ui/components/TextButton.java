package fi.bb.checkers.ui.components;

import fi.bb.checkers.helpers.ResourceHelper;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Ui;

public class TextButton extends BaseButton
{
	private int text_color = ResourceHelper.color_grey;
	private int text_color_hover = ResourceHelper.color_grey;
	private int text_color_pressed = ResourceHelper.color_grey;

	private String text;
	public TextButton(String text)
	{
		this(text, 0);
	}

	public TextButton(String text, long style)
	{
		this(text, true, style);
	}

	public TextButton(String text, boolean draw_focus, long style)
	{
		super(style);
		this.text = text;
		setFont(ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(17), Ui.UNITS_px));
		drawfocus = draw_focus;
	}

	public void setText(String text)
	{
		this.text = text;
		updateLayout(); // space occupied has changed
	}

	public String getText()
	{
		return text;
	}

	public int getPreferredWidth()
	{
		return Math.max(getFont().getAdvance(text), getFont().getAdvance("."));
	}

	public int getPreferredHeight()
	{
		return getFont().getHeight();
	}

	protected void paint(Graphics graphics)
	{
		if (_active)
		{
			graphics.setColor(text_color_pressed);
		}
		else if (_focus)
		{
			graphics.setColor(text_color_hover);
		}
		else
		{
			graphics.setColor(text_color);
		}

		graphics.drawText(text, 0, 0);
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
