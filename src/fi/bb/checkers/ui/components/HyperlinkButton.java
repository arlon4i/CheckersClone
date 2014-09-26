package fi.bb.checkers.ui.components;

import fi.bb.checkers.helpers.ResourceHelper;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Ui;

public class HyperlinkButton extends BaseButton
{
	private String text;
	private Font font;
	private boolean underline;

	public HyperlinkButton(String text, int size)
	{
		super(Field.FIELD_VCENTER);

		this.text = text;
		this.font = Font.getDefault().derive(Font.PLAIN, size, Ui.UNITS_px);
	}

	public HyperlinkButton(String text, int size, boolean underline)
	{
		this(text, size, underline, 0);
	}

	public HyperlinkButton(String text, int size, boolean underline, long style)
	{
		super(Field.FIELD_LEFT | style);

		this.text = text;
		this.font = ResourceHelper.helveticaLight().getFont(Font.PLAIN, size, Ui.UNITS_px);
		this.underline = underline;
	}

	public int getPreferredWidth()
	{
		return font.getAdvance(text) + 6;
	}

	public int getPreferredHeight()
	{
		return font.getHeight();
	}

	public void setText(String text)
	{
		this.text = text;
		invalidate();
	}

	public String getText()
	{
		return text;
	}

	protected void paint(Graphics g)
	{
		g.setColor(ResourceHelper.color_primary);

		if (isFocus())
		{
			g.setGlobalAlpha(75);
			g.fillRect(0, 0, getPreferredWidth(), getPreferredHeight());
			g.setGlobalAlpha(255);
			g.drawRect(0, 0, getPreferredWidth(), getPreferredHeight());
		}

		g.setColor(ResourceHelper.color_primary);
		g.setFont(font);
		g.drawText(text, 3, 0);

		if (underline) g.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
	}
}
