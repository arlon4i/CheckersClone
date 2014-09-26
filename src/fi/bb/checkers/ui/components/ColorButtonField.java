package fi.bb.checkers.ui.components;

import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Ui;
import fi.bb.checkers.helpers.ResourceHelper;
import fi.bb.checkers.utils.StringUtil;

//http://developer.blackberry.com/bbos/java/documentation/tutorial_create_custom_button_1969896_11.html
public class ColorButtonField extends BaseButton
{
	public static final int INFINITE_NUM_LINES = -1;

	public static final int TRANSPARENT = 0xFF000000;

	public static final String CENTER = "CENTER";
	public static final String LEFT = "LEFT";
	public static final String RIGHT = "RIGHT";

	private String tag;



	private int normalState;
	private int focusState;
	private int pressedState;

	private int width;
	private int height;

	// button text
	private Font textFont;
	private String text;
	private int normalStateText;
	private int focusStateText;
	private int pressedStateText;

	private int normalStateOutline;
	private int focusStateOutline;
	private int pressedStateOutline;

	private String alignment;

	private int numLines = 1;

	private String[] titleArray;

	public ColorButtonField(int normalState, int width, int height)
	{
		this(normalState, normalState, width, height);
	}

	public ColorButtonField(int normalState, int focusState, int width, int height)
	{
		this(normalState, focusState, focusState, width, height);
	}

	public ColorButtonField(int normalState, int focusState, int pressedState, int width, int height)
	{
		this(normalState, focusState, pressedState, 0, width, height);
	}

	public ColorButtonField(int normalState, int focusState, int pressedState, long style, int width, int height)
	{
		super(Field.FOCUSABLE | style);

		// default text stuff
		this.textFont = ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(15), Ui.UNITS_px);
		this.text = "Button";
		normalStateText = Color.BLACK;
		focusStateText = Color.BLACK;
		pressedStateText = Color.BLACK;

		normalStateOutline = TRANSPARENT;
		focusStateOutline = TRANSPARENT;
		pressedStateOutline = TRANSPARENT;

		this.width = width;
		this.height = height;

		this.normalState = normalState;
		this.focusState = focusState;
		this.pressedState = pressedState;

		this.alignment = CENTER;

		this.numLines = 1;

		setExtent(width, height);
	}

	public int getPreferredWidth()
	{
		return this.width;
	}

	public int getPreferredHeight()
	{
		return this.height;
	}

	public void setTextFont(Font font)
	{
		this.textFont = font;
	}

	public void setTextStates(int normalStateText, int focusStateText, int pressedStateText)
	{
		this.normalStateText = normalStateText;
		this.focusStateText = focusStateText;
		this.pressedState = pressedStateText;
	}

	public void setButtonText(String text)
	{
		this.text = text;
	}

	public void reloadButtonText()
	{
		invalidate();
	}

	public void setButtonOutlineStates(int normalStateOutline, int focusStateOutline, int pressedStateOutline)
	{
		this.normalStateOutline = normalStateOutline;
		this.focusStateOutline = focusStateOutline;
		this.pressedStateOutline = pressedStateOutline;
	}

	public void setTag(String tag)
	{
		this.tag = tag;
	}

	public String getTag()
	{
		return this.tag;
	}

	public void setTextImageAlignment(String alignment)
	{
		this.alignment = alignment;
	}

	protected void paint(Graphics g)
	{
		int textWidth = textFont.getAdvance(text);

		int textStartX;

		if (this.alignment.equals(LEFT))
		{
			textStartX = ResourceHelper.convert(5);
		}
		else if (this.alignment.equals(RIGHT))
		{
			if (textWidth < (width - (ResourceHelper.convert(5)*2)))
			{
				textStartX = (width - textWidth) - ResourceHelper.convert(5);
			}
			else
			{
				textStartX = ResourceHelper.convert(5);
			}
		}
		else
		{
			if (textWidth <= (width - (ResourceHelper.convert(5)*2)))
			{
				textStartX = (width - textWidth) / 2;
			}
			else
			{
				textStartX = ResourceHelper.convert(5);
			}
		}

		int borderWidth = 0;

		if (_visible)
		{
			if (isFocus())
			{
				if (focusStateOutline != TRANSPARENT)
				{
					borderWidth = ResourceHelper.convert(2);
				}

				g.setColor(focusState);
				g.fillRect(borderWidth, borderWidth, width - borderWidth, height - borderWidth);

				g.setColor(focusStateOutline);

				if (focusStateOutline != TRANSPARENT)
				{
					for (int i = 0; i < borderWidth; i++)
					{
						g.drawRoundRect(i, i, getWidth() - (i * 2), getHeight() - (i * 2), 0, 0);
					}
				}
				g.setColor(focusStateText);
			} 
			else
			{
				if (normalStateOutline != TRANSPARENT)
				{
					borderWidth = ResourceHelper.convert(2);
				}

				g.setColor(normalState);
				g.fillRect(borderWidth, borderWidth, width - borderWidth, height - borderWidth);

				g.setColor(normalStateOutline);

				if (normalStateOutline != TRANSPARENT)
				{
					for (int i = 0; i < borderWidth; i++)
					{
						g.drawRoundRect(i, i, getWidth() - (i * 2), getHeight() - (i * 2), 0, 0);
					}
				}
				g.setColor(normalStateText);
			}
			g.setFont(textFont);

			if (numLines > 0)
			{
				titleArray = StringUtil.ellipsize(textFont, text, width - ResourceHelper.convert(5)*2, numLines);
				for (int i = 0; i < titleArray.length; i++)
				{
					g.drawText(titleArray[i], textStartX, ((height / 2) - ((textFont.getHeight()*titleArray.length) / 2)) + textFont.getHeight()*i);
				}
			}
		}
	}

	public void setNormalState(int normalState)
	{
		this.normalState = normalState;
	}

	public void setFocusState(int focusState)
	{
		this.focusState = focusState;
	}

	public void setPressedState(int pressedState)
	{
		this.pressedState = pressedState;
	}

	public void setText(String text)
	{
		this.text = text;
		invalidate();
	}

	public void setMaxNumLines(int maxLines)
	{
		//first check if number of lines is necessary
		int textWidth = textFont.getAdvance(text);
		int tempNumLines = (int)Math.ceil((double)textWidth/(double)(width - (ResourceHelper.convert(5)*2)));

		if (tempNumLines < maxLines)
		{
			this.numLines = tempNumLines;
		}
		else
		{
			this.numLines = maxLines;
		}

		this.height = numLines*textFont.getHeight() + ResourceHelper.convert(5)*2;//top and bottom margins
		setExtent(width, height);
		invalidate();
	}

	public void setNumLines(int numLinesIn)
	{		
		/*if (numLinesIn == INFINITE_NUM_LINES)
		{
			//int textWidth = textFont.getAdvance(text);
			//this.numLines = (int)Math.ceil((double)textWidth/(double)(width - (ResourceHelper.convert(5)*2)));

			String[] titleArray = StringUtil.breakupTextToWidth(textFont, text, width - ResourceHelper.convert(5)*2);

			this.numLines = titleArray.length;
			this.height = numLines*textFont.getHeight() + ResourceHelper.convert(5)*2;//top and bottom margins

			RemoteLogger.log("NUMLINES", "-1 ---> " + numLines + "   :   new height: " + height);

			setExtent(width, height);
		}
		else
		{*/			
		this.numLines = numLinesIn;
		this.height = numLines*textFont.getHeight() + ResourceHelper.convert(5)*2;//top and bottom margins
		setExtent(width, height);
		//	}

		invalidate();
	}
}