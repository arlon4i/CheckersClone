package fi.bb.checkers.ui.components;

import net.rim.device.api.system.Characters;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.TouchEvent;

//http://developer.blackberry.com/bbos/java/documentation/tutorial_create_custom_button_1969896_11.html
public abstract class BaseButton extends Field
{
	// flags to indicate the current visual state
	protected boolean _visible = true;
	protected boolean _active;
	protected boolean _focus;

	protected boolean drawfocus = false;

	private int touch_top = 0;
	private int touch_right = 0;
	private int touch_bottom = 0;
	private int touch_left = 0;

	protected boolean fire_on_click = true; // false fires on unclick

	public BaseButton()
	{
		this(0);
	}

	public BaseButton(long style)
	{
		super((style & Field.NON_FOCUSABLE) == Field.NON_FOCUSABLE ? style : style | Field.FOCUSABLE);
	}

	/**
	 * Sets the radius around the button to trigger touch events.
	 * <p>
	 * (0,0,0,0) by default.
	 * </p>
	 */
	public void setTouchRadius(int top, int right, int bottom, int left)
	{
		touch_top = top;
		touch_right = right;
		touch_bottom = bottom;
		touch_left = left;
	}

	protected void onFocus(int direction)
	{
		_focus = true;
		invalidate();
		super.onFocus(direction);
	}

	protected void onUnfocus()
	{
		if (_active || _focus)
		{
			_focus = false;
			_active = false;
			invalidate();
		}
		super.onUnfocus();
	}

	public void set_visible(boolean visible)
	{
		_visible = visible;
		invalidate();
	}

	public boolean is_visible()
	{
		return _visible;
	}

	protected void drawFocus(Graphics g, boolean on)
	{
		if (drawfocus) super.drawFocus(g, on);
	}

	protected void layout(int width, int height)
	{
		setExtent(Math.min(width, getPreferredWidth()), Math.min(height, getPreferredHeight()));
	}

	protected boolean keyUp(int keycode, int time)
	{
		if (Keypad.map(Keypad.key(keycode), Keypad.status(keycode)) == Characters.ENTER)
		{
			_active = false;
			invalidate();
			return true;
		}

		return false;
	}

	protected boolean keyDown(int keycode, int time)
	{
		if (Keypad.map(Keypad.key(keycode), Keypad.status(keycode)) == Characters.ENTER)
		{
			_active = true;
			invalidate();
		}

		return super.keyDown(keycode, time);
	}

	protected boolean keyChar(char character, int status, int time)
	{
		if (character == Characters.ENTER)
		{
			clickButton();
			return true;
		}

		return super.keyChar(character, status, time);
	}

	protected boolean navigationClick(int status, int time)
	{
		if (status != 0)
		{ // non-touch event
			_active = true;
			invalidate();
			if (fire_on_click) clickButton();
		}
		return true;
	}

	protected boolean trackwheelClick(int status, int time)
	{
		if (status != 0)
		{ // non-touch event
			_active = true;
			invalidate();
			if (fire_on_click) clickButton();
		}
		return true;
	}

	protected boolean navigationUnclick(int status, int time)
	{
		if (status != 0)
		{ // non-touch event
			_active = false;
			invalidate();
			if (!fire_on_click) clickButton();
		}
		return true;
	}

	protected boolean trackwheelUnclick(int status, int time)
	{
		if (status != 0)
		{ // non-touch event
			_active = false;
			invalidate();
			if (!fire_on_click) clickButton();
		}
		return true;
	}

	protected boolean invokeAction(int action)
	{
		switch (action)
		{
			case ACTION_INVOKE :
			{
				clickButton();
				return true;
			}
		}

		return super.invokeAction(action);
	}

	protected boolean touchEvent(TouchEvent message)
	{
		boolean isOutOfBounds = touchEventOutOfBounds(message);
		switch (message.getEvent())
		{
			case TouchEvent.CLICK :
				if (!_active)
				{
					_active = true;
					invalidate();
				}

				if (!isOutOfBounds)
				{
					if (fire_on_click) clickButton();
					return true;
				}

			case TouchEvent.DOWN :
				if (!isOutOfBounds)
				{
					if (!_active)
					{
						_active = true;
						invalidate();
					}
					return true;
				}
				return false;

			case TouchEvent.UNCLICK :
				if (_active)
				{
					_active = false;
					invalidate();
				}

				if (!isOutOfBounds)
				{
					if (!fire_on_click) clickButton();
					return true;
				}

			case TouchEvent.UP :
				if (_active)
				{
					_active = false;
					invalidate();
				}

			default :
				return false;
		}
	}

	private boolean touchEventOutOfBounds(TouchEvent message)
	{
		int x = message.getX(1);
		int y = message.getY(1);
		return (x < 0 - touch_left || y < 0 - touch_top || x > getWidth() + touch_right || y > getHeight() + touch_bottom);
	}

	public void setDirty(boolean dirty)
	{
	}

	public void setMuddy(boolean muddy)
	{
	}

	public void clickButton()
	{
		if (_visible) fieldChangeNotify(0);
	}
}
