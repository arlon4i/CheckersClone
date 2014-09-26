package fi.bb.checkers.ui.screens;

import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.MainScreen;
import fi.bb.checkers.ui.components.Actionbar;
import fi.bb.checkers.ui.fragments.DetailedSpecialFragment;
import fi.bb.checkers.ui.fragments.Fragment;

/**
 * used to display fragments that aren't displayed in the view pager
 * @author kevin
 *
 */
public class FragmentContainerScreen extends MainScreen
{
	Actionbar actionbar;
	Fragment fragment;
	
	boolean isThemedSpecialDetailScreen;
	
	public FragmentContainerScreen(String title, Fragment fragment)
	{
		this(title, fragment, false);
	}
	
	public FragmentContainerScreen(String title, Fragment fragment, boolean useGreyTheme)
	{
		super(NO_VERTICAL_SCROLL | NO_VERTICAL_SCROLLBAR | NO_HORIZONTAL_SCROLL | NO_HORIZONTAL_SCROLLBAR);
		actionbar = new Actionbar(title, false, false, useGreyTheme);
		this.fragment = fragment;
		
		isThemedSpecialDetailScreen = false;

		add(actionbar);
		add(fragment);
	}
	
	public void setIsThemedSpecialDetailScreen(boolean value)
	{
		this.isThemedSpecialDetailScreen = value;
	}
	
	protected void makeMenu(Menu menu, int instance)
	{
		super.makeMenu(menu, instance);

		if (instance == Menu.INSTANCE_DEFAULT)
		{
			if (fragment != null && isThemedSpecialDetailScreen == true)
			{
				((DetailedSpecialFragment)fragment).makeMenu(menu);
			}
		}
	}
}
