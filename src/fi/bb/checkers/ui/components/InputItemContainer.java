package fi.bb.checkers.ui.components;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.decor.BackgroundFactory;
import fi.bb.checkers.helpers.ResourceHelper;
import fi.bb.checkers.utils.StringUtil;

public class InputItemContainer extends HorizontalFieldManager
{
	private static final int DEFAULT_WIDTH_LEFT_LABEL = ResourceHelper.convert(115);
	private LabelField label;
	private Field field;

	protected boolean _active;
	protected boolean _focus;

	Font font = ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(17), Ui.UNITS_px);
	int widthLeft;
	
	public InputItemContainer(String label, Field field)
	{
		this(label, field, false);
	}
	
	public InputItemContainer(String label, Field field, boolean wrapLabel)
	{
		setBackground(BackgroundFactory.createSolidBackground(ResourceHelper.color_background_inputfields));

		this.field = field;

		if (label != null)
		{
			if (wrapLabel == true)
			{
				if (font.getAdvance(label) > (Display.getWidth() - ResourceHelper.convert(10)*2))
				{
					widthLeft = Display.getWidth() - ResourceHelper.convert(10)*2;
					String[] newLabel = StringUtil.ellipsize(font, label, widthLeft, 1); 
					label = newLabel[0];
				}
				else
				{
					widthLeft = font.getAdvance(label);
				}
			}
			else
			{
				this.widthLeft = DEFAULT_WIDTH_LEFT_LABEL;
			}
			
			this.label = new LabelField(label, ResourceHelper.color_black, DrawStyle.VCENTER)
			{
				protected void layout(int width, int height)
				{
					if ((InputItemContainer.this.field != null))
					{
						super.layout(widthLeft, font.getHeight());
						setExtent(widthLeft, font.getHeight());
					}
					else
					{
						super.layout(widthLeft, font.getHeight());
					}			
				}
			};
			/*{
				protected void layout(int width, int height)
				{
					if ((InputItemContainer.this.field != null))
					{
						super.layout(widthLeft, height);
						setExtent(widthLeft, height);
					}
					else
					{
						super.layout(widthLeft, height);
					}
				}
			};*/
			this.label.setFont(font);
			this.label.setMargin((getPreferredHeight() - font.getHeight()) / 2, 0, 0, ResourceHelper.convert(10));
			add(this.label);
		}

		if (this.field != null)
		{
			this.field.setMargin((getPreferredHeight() - this.field.getPreferredHeight()) / 2, ResourceHelper.convert(10), 0, ResourceHelper.convert(10));
			add(this.field);
		}
	}

	protected void paint(Graphics graphics)
	{
		super.paint(graphics);
		graphics.setColor(ResourceHelper.color_grey);
		graphics.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
	}

	public int getPreferredHeight()
	{
		return ResourceHelper.convert(40);
	}

	protected void sublayout(int maxWidth, int maxHeight)
	{
		maxHeight = getPreferredHeight();
		super.sublayout(maxWidth, maxHeight);
		setExtent(maxWidth, maxHeight);
	}

	public void setDirty(boolean dirty)
	{
	}

	public void setMuddy(boolean muddy)
	{
	}


}
