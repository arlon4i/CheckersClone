package fi.bb.checkers.prompts;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.container.FullScreen;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.BackgroundFactory;
import net.rim.device.api.ui.decor.BorderFactory;
import fi.bb.checkers.MainApplication;
import fi.bb.checkers.helpers.ResourceHelper;
import fi.bb.checkers.ui.components.LabelField;
import fi.bb.checkers.ui.components.TextImageButton;

public class CustomDialog extends PopupScreen implements FieldChangeListener
{
	private TextImageButton[] buttons;
	private int[] choices;
	private int selected;

	private CustomDialog(String message, String[] choices, int[] values, int initial_index, boolean vertical)
	{
		super(new VerticalFieldManager());

		setBackground(BackgroundFactory.createSolidBackground(Color.WHITE));

		Bitmap borderBitmap = Bitmap.getBitmapResource("rounded-border.png");
		int padding = ResourceHelper.convert(10);
		setBorder(BorderFactory.createBitmapBorder(new XYEdges(padding, padding, padding, padding), borderBitmap));

		add(new LabelField(message, Color.BLACK, Field.FIELD_HCENTER));

		buttons = new TextImageButton[choices.length];
		this.choices = values;

		Manager button_manager;
		if (vertical)
		{
			button_manager = new VerticalFieldManager(NO_VERTICAL_SCROLL | NO_HORIZONTAL_SCROLL | FIELD_HCENTER);
		}
		else
		{
			button_manager = new HorizontalFieldManager(NO_VERTICAL_SCROLL | NO_HORIZONTAL_SCROLL | FIELD_HCENTER);
		}
		for (int i = 0; i < choices.length; i++)
		{
			if (values[i] == Dialog.YES)
			{
				buttons[i] = new TextImageButton(choices[i], "btn_sml_default", "btn_sml_hover");
				buttons[i].setTextColor(ResourceHelper.color_white);
				buttons[i].setTextColorHover(ResourceHelper.color_primary);
				buttons[i].setTextColorPressed(ResourceHelper.color_primary);
			}
			else
			{
				buttons[i] = new TextImageButton(choices[i], "btn_sml_grey_default", "btn_sml_hover");
				buttons[i].setTextColor(ResourceHelper.color_primary);
				buttons[i].setTextColorHover(ResourceHelper.color_primary);
				buttons[i].setTextColorPressed(ResourceHelper.color_primary);
			}
			buttons[i].setChangeListener(this);
			buttons[i].setMargin(ResourceHelper.convert(5), ResourceHelper.convert(2), 0, ResourceHelper.convert(2));
			button_manager.add(buttons[i]);
		}

		add(button_manager);

		if (initial_index >= 0 && initial_index < buttons.length) buttons[initial_index].setFocus();
	}

	public void fieldChanged(Field field, int context)
	{
		for (int i = 0; i < buttons.length; i++)
		{
			if (buttons[i] == field)
			{
				selected = choices[i];
				close();
			}
		}
	}

	public int getSelected()
	{
		return selected;
	}

	protected boolean keyDown(int keycode, int status)
	{
		if (Keypad.key(keycode) == Keypad.KEY_ESCAPE)
		{
			selected = Dialog.CANCEL;
			close();
		}
		return false;
	}

	/**
	 * Performs {@link #doModal(String, String[], int[], int, boolean) doModal(message, choices, values, 0, false)}
	 * @param message
	 * @param choices
	 * @param values
	 * @return
	 */
	public static int doModal(String message, String[] choices, int[] values)
	{
		return doModal(message, choices, values, 0);
	}

	/**
	 * Performs {@link #doModal(String, String[], int[], int, boolean) doModal(message, choices, values, initial_index, false)}
	 * 
	 * @param message
	 * @param choices
	 * @param values
	 * @return
	 */
	public static int doModal(String message, String[] choices, int[] values, int initial_index)
	{
		return doModal(message, choices, values, initial_index, false);
	}

	/**
	 * Creates a new dialog and returns the user choice on exit.
	 * 
	 * @param message
	 *            - Text to be displayed in the window
	 * @param choices
	 *            - Text to be displayed on each button
	 * @param values
	 *            - Value to be returned on each button
	 * @param initial_index
	 *            - Initial index of the button to focus
	 * @param vertical
	 *            - Layout the buttons vertically.
	 * @return - index of the button clicked
	 */
	public static int doModal(String message, String[] choices, int[] values, int initial_index, boolean vertical)
	{
		FullScreen trans = new FullScreen();
		trans.setBackground(BackgroundFactory.createSolidTransparentBackground(Color.BLACK, 200));
		((MainApplication) UiApplication.getUiApplication()).fadeScreen(trans, false);

		CustomDialog screen = new CustomDialog(message, choices, values, initial_index, vertical);
		UiApplication.getUiApplication().pushModalScreen(screen);
		trans.close();
		return screen.getSelected();
	}
}
