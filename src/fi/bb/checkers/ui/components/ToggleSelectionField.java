package fi.bb.checkers.ui.components;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Ui;
import fi.bb.checkers.datatypes.CouponCategory;
import fi.bb.checkers.datatypes.LocationData;
import fi.bb.checkers.datatypes.MerchantData;
import fi.bb.checkers.datatypes.Title;
import fi.bb.checkers.helpers.ResourceHelper;
import fi.bb.checkers.utils.StringUtil;

public class ToggleSelectionField extends BaseButton
{
	private boolean checked = false;
	private Object obj;
	private String title;
	private String[] titleArray;

	private int prefferedWidth =  Display.getWidth();
	
	private int numLines;

	private int tag;
	
	public ToggleSelectionField(Object obj)
	{
		this(obj, 1);
	}
	
	public ToggleSelectionField(Object obj, int numLineAllowed)
	{
		this.obj = obj;
				
		if (obj instanceof CouponCategory)
			title = ((CouponCategory) obj).getName();
		else if (obj instanceof LocationData)
			title = ((LocationData) obj).getDesc();
		else if (obj instanceof Title)
			title = ((Title) obj).getDescription();
		else if (obj instanceof MerchantData)
			title = ((MerchantData)obj).getName();
		else if (obj instanceof String) 
			title = ((String) obj);
		else
			title = "";//THIS SHOULD NEVER BE REACHED

		setFont(ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(15), Ui.UNITS_px));

		numLines = numLineAllowed;
		titleArray = StringUtil.ellipsize(getFont(), title, getPreferredWidth() - ResourceHelper.convert(20), numLineAllowed); 
	}

	protected void drawFocus(Graphics graphics, boolean on)
	{

	}

	public int getPreferredHeight()
	{
		if (numLines == 1)
		{
			return ResourceHelper.convert(30);
		}
		else
		{
			return ((titleArray.length*getFont().getHeight()) + ResourceHelper.convert(10));
		}
	}

	public int getPreferredWidth()
	{
		return prefferedWidth;
	}

	public void setPrefferedWidth(int prefferedWidth)
	{
		this.prefferedWidth = prefferedWidth;
		//setExtent(prefferedWidth, getPreferredHeight());
		//invalidate();
	}

	protected void layout(int maxWidth, int maxHeight)
	{
		super.layout(getPreferredWidth(), getPreferredHeight());
		setExtent(getPreferredWidth(), getPreferredHeight());
	}

	public void setChecked(boolean checked)
	{
		this.checked = checked;
		invalidate();
	}

	public boolean isChecked()
	{
		return checked;
	}

	protected void paint(Graphics graphics)
	{
		if (isFocus())
		{
			graphics.setColor(ResourceHelper.color_primary);
			graphics.fillRect(0, 0, getWidth(), getHeight());
			graphics.setColor(ResourceHelper.color_white);
		}
		else
		{
			graphics.setColor(ResourceHelper.color_dark_grey);
		}

		int x = ResourceHelper.convert(10);
		int y = (getHeight() - getFont().getHeight()) / 2;//for one liners
		
		if (titleArray.length > 1)
		{
			y = ResourceHelper.convert(5);
		}
		
		for (int i=0; i < titleArray.length; i++)
		{
			graphics.drawText(titleArray[i], x, y);	
			y+= getFont().getHeight();
		}

		if (checked)
		{
			Bitmap tick = isFocus() ? ResourceHelper.getImage("tick_sml_hover") : ResourceHelper.getImage("tick_sml_green_default");

			x = getWidth() - tick.getWidth() - ResourceHelper.convert(10);
			y = (getHeight() - tick.getHeight()) / 2;

			graphics.drawBitmap(x, y, tick.getWidth(), tick.getHeight(), tick, 0, 0);
		}

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

	public String getTitle()
	{
		return title;
	}

	public Object getObject()
	{
		return obj;
	}
	
	public void setTag(int tag)
	{
		this.tag = tag;
	}
	
	public int getTag()
	{
		return tag;
	}
}
