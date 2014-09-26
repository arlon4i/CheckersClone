package fi.bb.checkers.ui.components;

import fi.bb.checkers.MainApplication;
import fi.bb.checkers.helpers.ResourceHelper;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.decor.BackgroundFactory;

public class TextInputField extends EditField
{
	private String placeHolder;
	private boolean isPassword;
	boolean focusable;
	private int defaultColor = Color.BLACK;
	private int disabledColor = Color.GRAY;
	private boolean showBoarder = true;

	public TextInputField(String placeHolder, boolean isPassword)
	{
		this.placeHolder = placeHolder;
		this.isPassword = isPassword;
		this.setBackground(BackgroundFactory.createSolidBackground(Color.WHITE));

		focusable = true;
		setFont(ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(17), Ui.UNITS_px));
	}

	public TextInputField(String placeHolder, boolean isPassword, boolean showboarder)
	{
		this(placeHolder, isPassword, showboarder, Color.BLACK, 0);
	}

	public TextInputField(String placeHolder, long style, boolean isPassword)
	{
		super(style|DrawStyle.VCENTER);
		this.placeHolder = placeHolder;
		this.isPassword = isPassword;
		this.setBackground(BackgroundFactory.createSolidBackground(Color.WHITE));

		focusable = true;
		setFont(ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(17), Ui.UNITS_px));
	}

	public TextInputField(String placeHolder, boolean isPassword, int defaultColor)
	{
		super(DrawStyle.VCENTER);
		this.placeHolder = placeHolder;
		this.isPassword = isPassword;
		this.defaultColor = defaultColor;
		this.setBackground(BackgroundFactory.createSolidBackground(Color.WHITE));

		focusable = true;
		setFont(ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(17), Ui.UNITS_px));
	}

	public TextInputField(String placeHolder, boolean isPassword, boolean showboarder, int color, long style)
	{
		super(style|DrawStyle.VCENTER);
		this.placeHolder = placeHolder;
		this.isPassword = isPassword;
		this.showBoarder = showboarder;
		this.defaultColor = color;
		this.setBackground(BackgroundFactory.createSolidBackground(Color.WHITE));

		focusable = true;
		setFont(ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(17), Ui.UNITS_px));
	}
	
	public void setDisabledColor(int color)
	{
		disabledColor = color;
	}
	
	public void setTextFont(Font font)
	{
		setFont(font);
	}

	public int getPreferredWidth()
	{
		return Display.getWidth();
	}

	public int getPreferredHeight()
	{
		return getFont().getHeight();
	}

	protected void onUnfocus()
	{
		((MainApplication) UiApplication.getUiApplication()).hideKeyboard();
		super.onUnfocus();
	}

	public void layout(int width, int height)
	{
		super.layout(getPreferredWidth(), getPreferredHeight());

		setExtent(getPreferredWidth(), getPreferredHeight());
	}

	public void paint(Graphics g)
	{
		g.setColor(Color.LIGHTGRAY);

		if (showBoarder)
		{
			g.drawRect(0, 0, getWidth(), getPreferredHeight());
		}
		String fieldText = "";

		// Populate text
		if (getTextLength() < 1)
		{
			fieldText = placeHolder;
			if (!showBoarder)
			{
				g.setColor(disabledColor);
			}
			else
			{
				g.setColor(disabledColor);
			}

			g.drawText(fieldText, 0, ((getPreferredHeight()-getFont().getHeight())/2));
		}
		else
		{
			if (isEditable() && defaultColor == Color.BLACK)
			{
				g.setColor(Color.BLACK);
			}
			else if (isEditable() && defaultColor != Color.BLACK)
			{
				g.setColor(defaultColor);
			}
			else
			{
				g.setColor(disabledColor);
			}

			if (isPassword)
			{
				for (int i = 0; i < getTextLength() - 1; i++)
				{
					fieldText += "*";
				}
				fieldText += getText().substring(getTextLength() - 1);
			}
			else
			{
				fieldText = getText();
			}

			g.drawText(fieldText, 0, ((getPreferredHeight()-getFont().getHeight())/2));

		}
	}

	public void setFocusable(boolean focusableFlag)
	{
		focusable = focusableFlag;
	}

	public boolean isFocusable()
	{
		return focusable;
	}

	protected void displayFieldFullMessage()
	{

	}
}
