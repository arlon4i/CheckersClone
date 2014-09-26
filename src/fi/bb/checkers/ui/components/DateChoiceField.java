package fi.bb.checkers.ui.components;

import java.util.Calendar;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.component.ObjectChoiceField;
import fi.bb.checkers.helpers.ResourceHelper;
import fi.bb.checkers.utils.BitmapTools;

public class DateChoiceField extends ObjectChoiceField
{

	public static final int DAY = 0;
	public static final int MONTH = 1;
	public static final int YEAR = 2;

	private int width;

	// private String hint;
	private Font font;
	// private boolean didSelect;
	private boolean hasFocus;

	public DateChoiceField(int type)
	{
		super("", null, 0, Field.FIELD_RIGHT);

		font = Font.getDefault();

		// int ddHeight = (int) (height/1.5);

		switch (type)
		{
			case DAY :
				// hint = "Day";
				width = font.getAdvance("00") + 40;
				String choicesd[] = new String[32];
				choicesd[0] = "Day";
				for (int i = 1; i <= 31; i++)
				{
					choicesd[i] = Integer.toString(i);
				}
				setChoices(choicesd);
				break;
			case MONTH :
				// hint = "Month";
				width = font.getAdvance("September") + 30;
				String choicesm[] = {"Month", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
				setChoices(choicesm);
				break;
			case YEAR :
				// hint = "Year";
				width = font.getAdvance("0000") + 40;
				String choicesy[] = new String[100];
				choicesy[0] = "Year";
				int year = Calendar.getInstance().get(Calendar.YEAR) + 1;
				for (int i = 1; i < 100; i++)
				{
					choicesy[i] = Integer.toString(year - i);
				}
				setChoices(choicesy);
				break;
		}

		// setMinimalWidth(width);

		// Bitmap borderBitmap = Bitmap.getBitmapResource("square-border.png");
		// setBorder(BorderFactory.createBitmapBorder(new XYEdges(10,6,10,6),
		// borderBitmap));

		// didSelect=false;
	}

	protected void onFocus(int direction)
	{
		hasFocus = true;
		invalidate();
	}

	protected void onUnfocus()
	{
		hasFocus = false;
		invalidate();
	}

	protected void layout(int width, int height)
	{
		setExtent(this.width, getPreferredHeight());
	}

	public int getPreferredHeight()
	{
		if (Display.getHeight() > 360)
		{
			return Display.getHeight() / 12;
		}
		else
		{
			return Display.getHeight() / 8;
		}
	}

	protected void paint(Graphics g)
	{
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, width, getPreferredHeight());

		if (hasFocus)
		{
			//g.setColor(0x00a9a1);
			g.setColor(ResourceHelper.color_primary);
			g.drawRect(0, 0, width, getPreferredHeight());

			// g.setColor(0x00a9a1);
			// g.setGlobalAlpha(60);
			// g.fillRect(0, 0, this.width, getPreferredHeight());
		}
		else
		{
			g.setColor(Color.LIGHTGRAY);
			g.drawRect(0, 0, width, getPreferredHeight());
		}

		g.setColor(Color.BLACK);

		// if(didSelect){
		// font = Font.getDefault();
		// g.setFont(font);
		// g.drawText((String) this.getChoice(this.getSelectedIndex()), 10,
		// getPreferredHeight()/2 - font.getHeight()/2);
		// }
		// else{
		// g.setColor(0xadadad);
		// font = Font.getDefault().derive(Font.ITALIC, 7, Ui.UNITS_pt);
		// g.setFont(font);
		// g.drawText(hint, 10, getPreferredHeight()/2 - font.getHeight()/2);
		// }

		if (getSelectedIndex() == 0)
		{
			g.setColor(0xadadad);
			font = Font.getDefault().derive(Font.ITALIC, 7, Ui.UNITS_pt);
			g.setFont(font);
		}
		else
		{
			font = Font.getDefault();
			g.setFont(font);
		}

		g.drawText((String) getChoice(getSelectedIndex()), 10, getPreferredHeight() / 2 - font.getHeight() / 2);

		Bitmap dd = BitmapTools.resizeTransparentBitmap(Bitmap.getBitmapResource("dropdown.png"), getPreferredHeight() / 4, getPreferredHeight() / 4, Bitmap.FILTER_BILINEAR, Bitmap.SCALE_TO_FIT);
		g.drawBitmap(width - dd.getWidth() - 3, getPreferredHeight() / 2 - dd.getHeight() / 2, dd.getWidth(), dd.getHeight(), dd, 0, 0);

	}

	protected void fieldChangeNotify(int context)
	{
		// didSelect = true;

	}
}
