package fi.bb.checkers.ui.components;

import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import fi.bb.checkers.utils.StringUtil;

// Made my own label field, because the native one caps the font size rather low
/**
 * <p>
 * Custom LabelField that does not have the same Font size cap as {@link net.rim.device.api.ui.component.LabelField}.
 * </p>
 * 
 * Currently supports the following flags:
 * <dl>
 * <dt>Field.USE_ALL_WIDTH</dt>
 * <dd>- Use all available width</dd>
 * <dt>Field.USE_ALL_HEIGHT</dt>
 * <dd>- Use all available height</dd>
 * <dt>Field.FIELD_HCENTER</dt>
 * <dd>- Center field horizontally within it's manager.</dd>
 * <dt>Field.FIELD_VCENTER</dt>
 * <dd>- Center field vertically within it's manager.</dd>
 * <dt>DrawStyle.HCENTER</dt>
 * <dd>- Center text horizontally within it's own bounds.</dd>
 * <dt>DrawStyle.VCENTER</dt>
 * <dd>- Center text vertically within it's own bounds.</dd>
 * <dt>DrawStyle.LEADING</dt>
 * <dd>- Ignore leading spacing the given font.</dd>
 * <dt>DrawStyle.ELLIPSIS</dt>
 * <dd>- Add ellipsis to the text when there is not enough space available.</dd>
 * </dl>
 * 
 * @author kevin
 */
public class LabelField extends Field
{
	protected String text;
	protected int color;
	protected long style;
	protected Font font = Font.getDefault();

	private String[] lines;
	
	private String tag;

	public LabelField(String text, int color, long style)
	{
		this(text, color, style, Font.getDefault());
	}
	
	public LabelField(String text, int color, long style, Font font)
	{
		this.text = text;
		this.color = color;
		this.font = font;

		if ((style & FIELD_HCENTER) == FIELD_HCENTER) style = style | USE_ALL_WIDTH | DrawStyle.HCENTER;
		if ((style & FIELD_VCENTER) == FIELD_VCENTER) style = style | USE_ALL_HEIGHT | DrawStyle.VCENTER;

		this.style = style;
	}

	protected void paint(Graphics graphics)
	{
		graphics.setColor(this.color);
		graphics.setFont(font);

		int x = getPaddingLeft();
		int y;
		if ((style & DrawStyle.VCENTER) == DrawStyle.VCENTER)
		{
			int font_height = ((style & DrawStyle.LEADING) == DrawStyle.LEADING) ? font.getHeight() - font.getLeading() : font.getHeight();
			y = (getHeight() - (font_height * lines.length)) / 2;
		}
		else
		{
			y = getPaddingTop();
		}

		for (int i = 0; i < lines.length; i++)
		{
			if ((style & DrawStyle.HCENTER) == DrawStyle.HCENTER) x = (getWidth() - font.getAdvance(lines[i])) / 2;
			if ((style & DrawStyle.LEADING) == DrawStyle.LEADING) y -= font.getLeading();
			graphics.drawText(lines[i], x, y);
			y += font.getHeight();
		}
	}

	public final String getText()
	{
		return text;
	}

	public final void setText(String text)
	{
		this.text = text;
		updateLayout();
		invalidate();
	}

	public final Font getFont()
	{
		return font;
	}

	public final void setFont(Font font)
	{
		this.font = font;
		updateLayout();
		invalidate();
	}

	public final int getColor()
	{
		return color;
	}

	public int getPreferredWidth()
	{
		return font.getAdvance(text);
	}

	public int getPreferredHeight()
	{
		return (style & DrawStyle.LEADING) == DrawStyle.LEADING ? font.getHeight() - font.getLeading() : font.getHeight();
	}

	public final void setColor(int color)
	{
		this.color = color;
	}
	
	public void setTag(String tag)
	{
		this.tag = tag;
	}
	
	public String getTag()
	{
		return tag;
	}

	// Use up to maximum available width and height. Else wrap the text and ellipsize
	protected void layout(int availablewidth, int availableheight)
	{
		int width = 0;
		int height = 0;

		int preferredwidth = getPreferredWidth();
		int preferredheight = getPreferredHeight();

		if (preferredwidth <= availablewidth)
		{
			if ((style & DrawStyle.ELLIPSIS) == DrawStyle.ELLIPSIS)
				lines = StringUtil.ellipsize(font, text, availablewidth - getPaddingLeft() - getPaddingRight(), 1);
			else
				lines = StringUtil.wrapText(font, text, availablewidth - getPaddingLeft() - getPaddingRight(), 1);

			width = preferredwidth;
			height = Math.min(preferredheight, availableheight);
		}
		else
		{
			int max_lines = Math.max((availableheight - getPaddingTop() - getPaddingBottom()) / preferredheight, 1);
			if ((style & DrawStyle.ELLIPSIS) == DrawStyle.ELLIPSIS)
				lines = StringUtil.ellipsize(font, text, availablewidth - getPaddingLeft() - getPaddingRight(), max_lines);
			else
				lines = StringUtil.wrapText(font, text, availablewidth - getPaddingLeft() - getPaddingRight(), max_lines);
			width = availablewidth;
			height = Math.min(preferredheight * lines.length, availableheight);
		}

		if ((style & USE_ALL_WIDTH) == USE_ALL_WIDTH) width = availablewidth;
		if ((style & USE_ALL_HEIGHT) == USE_ALL_HEIGHT) height = availableheight;
		setExtent(width, height);
	}
}
