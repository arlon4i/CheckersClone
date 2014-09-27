package fi.bb.checkers.ui.components;

import java.util.Calendar;

import fi.bb.checkers.helpers.ResourceHelper;


import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.picker.DateTimePicker;


public class DatePickerField extends BaseButton
{
	private static final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
	private Calendar date;
	private int textColor;

	public DatePickerField(Calendar date, long style)
	{
		super(FOCUSABLE | style);
		this.date = date;
		drawfocus = true;
		textColor = ResourceHelper.color_grey;
	}

	public Calendar getDate()
	{
		return date;
	}

	public int getPreferredWidth()
	{
		return getFont().getAdvance("00/00/0000");
	}

	public int getPreferredHeight()
	{
		return getFont().getHeight();
	}

	public void setDate(Calendar date)
	{
		this.date = date;
		invalidate();
	}
	
	public void setTextColor(int color)
	{
		this.textColor = color;
		invalidate();
	}

	protected void paint(Graphics graphics)
	{
		graphics.setColor(textColor);
		graphics.drawText(formatter.format(date.getTime()), 0, 0);
	}

	public void clickButton()
	{
		super.clickButton();

		DateTimePicker picker = DateTimePicker.createInstance(date, "dd/MM/yyyy", null);
		picker.doModal();
		textColor = ResourceHelper.color_primary;
		setDate(picker.getDateTime());
	}
}
