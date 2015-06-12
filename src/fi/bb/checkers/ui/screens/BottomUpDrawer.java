package fi.bb.checkers.ui.screens;

import net.rim.device.api.system.Characters;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.TouchEvent;
import net.rim.device.api.ui.TouchGesture;
import net.rim.device.api.ui.container.MainScreen;
import fi.bb.checkers.ui.components.Actionbar;

public class BottomUpDrawer extends MainScreen
{
	private Actionbar actionbar;
	
	public BottomUpDrawer(String screen_heading)
	{
		super(VERTICAL_SCROLL | NO_HORIZONTAL_SCROLL);
		
		actionbar = new Actionbar(screen_heading, false, false);
		add(actionbar);
	}
	
	protected int getAvailableHeight() {
		return Display.getHeight() - actionbar.getPreferredHeight();
	}
	
	protected boolean navigationClick(int status, int time)
	{
		close();
		return true;
	}

	protected boolean touchEvent(TouchEvent message)
	{
		switch (message.getEvent())
		{
			case TouchEvent.CLICK:

			case TouchEvent.UNCLICK:
				close();
				return true;
				
			case TouchEvent.GESTURE:
				TouchGesture gesture = message.getGesture();
				switch (gesture.getEvent())
				{
					case TouchGesture.TAP: 
						if(gesture.getTapCount() >= 2)
						{
							close();
							return true;
						}
					case TouchGesture.SWIPE:
						switch(gesture.getSwipeDirection())
						{
							case TouchGesture.SWIPE_SOUTH:
								close();
								return true;
						}
				}
		}
		
		return super.touchEvent(message);
	}

	protected boolean keyChar(char c, int status, int time)
	{
		if (c == Characters.ESCAPE)
		{
			close();
			return true;
		}
		return super.keyChar(c, status, time);
	}
}
