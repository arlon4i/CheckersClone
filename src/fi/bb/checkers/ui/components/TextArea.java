package fi.bb.checkers.ui.components;

import fi.bb.checkers.helpers.ResourceHelper;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.BackgroundFactory;

public class TextArea extends VerticalFieldManager
{
	private EditField inputfield;
	private String placeholder;
	private int textColor;

	public TextArea(String placeholder)
	{
		this(placeholder, Color.BLACK);	
	}
	
	public TextArea(String placeholder, int color)
	{
		super(VERTICAL_SCROLL | NO_HORIZONTAL_SCROLL);
		setBackground(BackgroundFactory.createSolidBackground(ResourceHelper.color_white));

		this.placeholder = placeholder;
		this.textColor = color;
		inputfield = new EditField()
		{
			protected void paint(Graphics graphics)
			{
				if (getText().length() == 0)
				{
					graphics.setColor(ResourceHelper.color_grey);
					graphics.drawText(TextArea.this.placeholder, 0, 0);
					graphics.setColor(textColor);
				}
				else
				{
					graphics.setColor(textColor);
				}
				super.paint(graphics);
			}
		};
		inputfield.setFont(ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(17), Ui.UNITS_px));

		add(inputfield);
	}

	public String getText()
	{
		return inputfield.getText();
	}

	public void setText(String text)
	{
		inputfield.setText(text);
	}

	protected void sublayout(int maxWidth, int maxHeight)
	{
		int width = getPreferredWidth();
		int height = getPreferredHeight();
		super.sublayout(width, height);
		setExtent(width, height);
	}
}
