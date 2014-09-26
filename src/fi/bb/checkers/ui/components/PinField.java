package fi.bb.checkers.ui.components;

import net.rim.device.api.system.Characters;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.text.TextFilter;
import fi.bb.checkers.helpers.ResourceHelper;

public class PinField extends HorizontalFieldManager
{
	private TextInputField digit_1;
	private TextInputField digit_2;
	private TextInputField digit_3;
	private TextInputField digit_4;

	public PinField()
	{
		this(0);
	}

	public PinField(long style)
	{
		super(style);
		Font font = ResourceHelper.helveticaLight().getFont(Font.BOLD, ResourceHelper.convert(17), Ui.UNITS_px);
		final int margin = ResourceHelper.convert(10);
		final int char_width = font.getAdvance("0") + margin;
		final int char_height = font.getHeight();

		digit_1 = new TextInputField(String.valueOf('\u2022'), false, false, ResourceHelper.color_checkers_teal, 0)
		{

			protected void onUnfocus()
			{

			}
			public void layout(int width, int height)
			{
				super.layout(char_width, char_height);
				setExtent(char_width, char_height);
			}

			protected boolean keyChar(char key, int status, int time)
			{
				setText("");
				super.keyChar(key, status, time);

				if (getText().length() == 0)
					return super.keyChar(key, status, time);
				else
				{
					digit_2.setFocus();
					return true;
				}
			}
		};
		digit_2 = new TextInputField(String.valueOf('\u2022'), false, false, ResourceHelper.color_checkers_teal, 0)
		{
			protected void onUnfocus()
			{

			}
			public void layout(int width, int height)
			{
				super.layout(char_width, char_height);
				setExtent(char_width, char_height);
			}

			protected boolean keyChar(char key, int status, int time)
			{
				setText("");
				super.keyChar(key, status, time);

				if (Characters.BACKSPACE == key)
				{
					setText("");
					digit_1.setFocus();
					return true;
				}
				else if (getText().length() == 0)
					return super.keyChar(key, status, time);
				else
				{
					digit_3.setFocus();
					return true;
				}

			}
		};
		digit_3 = new TextInputField(String.valueOf('\u2022'), false, false, ResourceHelper.color_checkers_teal, 0)
		{
			protected void onUnfocus()
			{

			}
			public void layout(int width, int height)
			{
				super.layout(char_width, char_height);
				setExtent(char_width, char_height);
			}

			protected boolean keyChar(char key, int status, int time)
			{

				setText("");
				super.keyChar(key, status, time);

				if (Characters.BACKSPACE == key)
				{
					setText("");
					digit_2.setFocus();
					return true;
				}
				else if (getText().length() == 0)
					return super.keyChar(key, status, time);
				else
				{
					digit_4.setFocus();
					return true;
				}
			}
		};
		digit_4 = new TextInputField(String.valueOf('\u2022'), false, false, ResourceHelper.color_checkers_teal, 0)
		{
			protected void onUnfocus()
			{

			}
			public void layout(int width, int height)
			{
				super.layout(char_width, char_height);
				setExtent(char_width, char_height);
			}

			protected boolean keyChar(char key, int status, int time)
			{
				setText("");
				super.keyChar(key, status, time);
				if (Characters.BACKSPACE == key)
				{
					setText("");
					digit_3.setFocus();
					return true;
				}
				else if (getText().length() == 0)
					return super.keyChar(key, status, time);
				else
					return true;
			}
		};

		digit_1.setFilter(TextFilter.get(TextFilter.NUMERIC));
		digit_2.setFilter(TextFilter.get(TextFilter.NUMERIC));
		digit_3.setFilter(TextFilter.get(TextFilter.NUMERIC));
		digit_4.setFilter(TextFilter.get(TextFilter.NUMERIC));

		digit_1.setMaxSize(1);
		digit_2.setMaxSize(1);
		digit_3.setMaxSize(1);
		digit_4.setMaxSize(1);

		digit_1.setMargin(0, 0, 0, margin);
		digit_2.setMargin(0, 0, 0, margin);
		digit_3.setMargin(0, 0, 0, margin);
		digit_4.setMargin(0, 0, 0, margin);

		digit_1.setFont(font);
		digit_2.setFont(font);
		digit_3.setFont(font);
		digit_4.setFont(font);

		add(digit_1);
		add(digit_2);
		add(digit_3);
		add(digit_4);
	}

	public String getPIN()
	{
		return digit_1.getText() + digit_2.getText() + digit_3.getText() + digit_4.getText();
	}
}
