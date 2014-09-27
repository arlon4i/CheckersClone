package fi.bb.checkers.ui.components;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Ui;
import fi.bb.checkers.datatypes.MerchantData;
import fi.bb.checkers.helpers.ResourceHelper;
import fi.bb.checkers.utils.StringUtil;

public class StoreItemField extends BaseButton
{
	private MerchantData merchant;
	private String title;

	public StoreItemField(MerchantData merchant)
	{
		this.merchant = merchant;
		title = merchant.getName();

		setFont(ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(15), Ui.UNITS_px));

		title = StringUtil.ellipsize(getFont(), title, getPreferredWidth() - ResourceHelper.convert(20), 1)[0];
	}

	protected void drawFocus(Graphics graphics, boolean on)
	{

	}

	public int getPreferredHeight()
	{
		return ResourceHelper.convert(30);
	}

	public int getPreferredWidth()
	{
		return Display.getWidth();
	}

	protected void layout(int maxWidth, int maxHeight)
	{
		super.layout(getPreferredWidth(), getPreferredHeight());
		setExtent(getPreferredWidth(), getPreferredHeight());
	}

	protected void paint(Graphics graphics)
	{
		int circleColor = ResourceHelper.color_white;
		if (isFocus())
		{
			graphics.setColor(ResourceHelper.color_primary);
			graphics.fillRect(0, 0, getWidth(), getHeight());			
		}
		else
		{
			if (merchant.getBrand().equals("checkers"))
			{
				circleColor = ResourceHelper.color_checkers_teal;
			}
			else
			{
				circleColor = ResourceHelper.color_shoprite_red;
			}
		}

		int diameter = ResourceHelper.convert(8);
		int x = ResourceHelper.convert(10);
		int y = (getHeight() - diameter) / 2;
		
		graphics.setColor(circleColor);
		graphics.fillArc(x, y, diameter, diameter, 0, 360);

		x += ResourceHelper.convert(10) + diameter;
		y = (getHeight() - getFont().getHeight()) / 2;
		graphics.setColor(isFocus() ? ResourceHelper.color_white : ResourceHelper.color_dark_grey);
		graphics.drawText(title, x, y);

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
		return merchant;
	}
}
