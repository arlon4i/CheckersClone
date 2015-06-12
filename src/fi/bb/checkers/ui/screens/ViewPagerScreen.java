package fi.bb.checkers.ui.screens;

import java.util.Calendar;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;

import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.Characters;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.ScrollChangeListener;
import net.rim.device.api.ui.TouchEvent;
import net.rim.device.api.ui.TouchGesture;
import net.rim.device.api.ui.TransitionContext;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.UiEngineInstance;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.BackgroundFactory;
import fi.bb.checkers.MainApplication;
import fi.bb.checkers.datatypes.CampaignData;
import fi.bb.checkers.datatypes.InboxMessage;
import fi.bb.checkers.datatypes.LocationData;
import fi.bb.checkers.datatypes.MerchantData;
import fi.bb.checkers.helpers.FlurryHelper;
import fi.bb.checkers.helpers.LocationHelper;
import fi.bb.checkers.helpers.PersistentStoreHelper;
import fi.bb.checkers.helpers.ResourceHelper;
import fi.bb.checkers.helpers.RuntimeStoreHelper;
import fi.bb.checkers.imageloader.ImageLoader;
import fi.bb.checkers.logger.RemoteLogger;
import fi.bb.checkers.ui.components.Actionbar;
import fi.bb.checkers.ui.components.NavigationDrawer;
import fi.bb.checkers.ui.components.ProfileDrawer;
import fi.bb.checkers.ui.fragments.AboutFragment;
import fi.bb.checkers.ui.fragments.ChecklineFragment;
import fi.bb.checkers.ui.fragments.CouponsFragment;
import fi.bb.checkers.ui.fragments.DetailedCouponFragment;
import fi.bb.checkers.ui.fragments.DetailedSpecialFragment;
import fi.bb.checkers.ui.fragments.FeedbackFragment;
import fi.bb.checkers.ui.fragments.Fragment;
import fi.bb.checkers.ui.fragments.HelpFragment;
import fi.bb.checkers.ui.fragments.HomeFragment;
import fi.bb.checkers.ui.fragments.InboxFragment;
import fi.bb.checkers.ui.fragments.InboxMessageFragment;
import fi.bb.checkers.ui.fragments.InfoFragment;
import fi.bb.checkers.ui.fragments.MylistFragment;
import fi.bb.checkers.ui.fragments.SelectProvinceFragment;
import fi.bb.checkers.ui.fragments.SelectStoreFragment;
import fi.bb.checkers.ui.fragments.SpecialsFragment;
import fi.bb.checkers.ui.fragments.StoreInfoFragment;
import fi.bb.checkers.ui.fragments.TermsFragment;
import fi.bb.checkers.utils.AsyncTask;

public class ViewPagerScreen extends MainScreen implements Actionbar.OnClickListener, ScrollChangeListener, NavigationDrawer.DrawerListener
{
	private Stack fragment_stack;

	private Actionbar actionbar;
	private NavigationDrawer drawer;
	private ProfileDrawer profile;
	private HorizontalFieldManager manager;
	private VerticalFieldManager fragment_manager;
	private volatile boolean opening = false;
	private volatile boolean closing = false;
	private ScrollAnimateTask animation_task;
	private int current_fragment_id = -1;

	private boolean loggedTimedEventForProfileClick;
	private boolean loggedTimedEventForNavMenuClick;
	
	// prevent the user from clicking many links and having them all open.
	private volatile boolean loading_fragment = false;
	
	private ViewPagerScreen()
	{
		super(NO_VERTICAL_SCROLL | NO_VERTICAL_SCROLLBAR | NO_HORIZONTAL_SCROLL | NO_HORIZONTAL_SCROLLBAR);

		VerticalFieldManager content = new VerticalFieldManager()
		{
			protected void onFocus(int direction)
			{
				if (direction == 1)
				{
					actionbar.selectDrawer();
				}
				else if (direction == -1)
				{
					actionbar.selectProfile();
				}
				else
				{
					super.onFocus(direction);
				}
			}
		};
		content.setBackground(BackgroundFactory.createSolidBackground(ResourceHelper.color_background_app));

		actionbar = new Actionbar(new BitmapField(ResourceHelper.getImage("logo_title-bar")), true, true);
		actionbar.setListener(this);
		content.add(actionbar);

		drawer = new NavigationDrawer();
		drawer.setDrawerListener(this);
		profile = new ProfileDrawer();

		manager = new HorizontalFieldManager(USE_ALL_WIDTH | USE_ALL_HEIGHT | NO_VERTICAL_SCROLL | HORIZONTAL_SCROLL | NO_HORIZONTAL_SCROLLBAR)
		{
			protected boolean touchEvent(TouchEvent message)
			{
				if (opening || closing) return true;

				// handle left and right swiping to open drawers. Seems to consume when this is an if instead of switch
				switch (message.getEvent())
				{
				case TouchEvent.GESTURE :
					TouchGesture gesture = message.getGesture();
					switch (gesture.getEvent())
					{
					case TouchGesture.SWIPE :
						if (actionbar.isNavigationVisible() && profile.getManager() == null)
						{
							if (drawer.getManager() == null && gesture.getSwipeDirection() == TouchGesture.SWIPE_EAST && message.getX(1) <= ResourceHelper.convert(50))
							{
								openDrawer(0);
								return true;
							}
							else if (drawer.getManager() != null)
							{
								if (gesture.getSwipeDirection() == TouchGesture.SWIPE_WEST && message.getX(1) >= manager.getWidth() - ResourceHelper.convert(50)) openDrawer(1);
								return true;
							}
						}
						if (actionbar.isProfileVisible() && drawer.getManager() == null)
						{
							if (profile.getManager() == null && gesture.getSwipeDirection() == TouchGesture.SWIPE_WEST && message.getX(1) >= manager.getWidth() - ResourceHelper.convert(50))
							{
								openDrawer(2);
								return true;
							}
							else if (profile.getManager() != null)
							{
								if (gesture.getSwipeDirection() == TouchGesture.SWIPE_EAST && message.getX(1) <= ResourceHelper.convert(50)) openDrawer(1);
								return true;
							}
						}
					}
				}
				return super.touchEvent(message);
			}
		};
		manager.setScrollListener(this);
		manager.add(content);

		fragment_manager = new VerticalFieldManager();
		content.add(fragment_manager);

		add(manager);

		fragment_stack = new Stack();
		HomeFragment.initial = true;
		transition(HomeFragment.FRAGMENT_ID, null);

		if (LocationHelper.isGPSEnabled()) LocationHelper.determineLocation();

		//Moved check the coupons part to coupons fragment or where-ever coupons is called.
		/*new Thread(new Runnable()
		{
			public void run()
			{
				try
				{
					if (RuntimeStoreHelper.getSessionID() != null)
					{
						PersistentStoreHelper.getMylist();
					}
					else
					{
						for (int i = 0; i < RuntimeStoreHelper.getCoupons().size(); i++)
						{
							CampaignData coupon = (CampaignData) RuntimeStoreHelper.getCoupons().elementAt(i);
							coupon.setStrikethrough(false);
							coupon.setChecked(false);
						}
					}
				} catch (Exception e)
				{
					RemoteLogger.log("ViewPagerScreen", "Coupon State Set: " + e.toString());
				}
			}
		}).start();*/

		logAppLaunchSuccess();	
		
//		com.omniture.AppMeasurement test = new com.omniture.AppMeasurement();
	}

	private void logAppLaunchSuccess()
	{
		Hashtable eventParams = new Hashtable();
		Calendar dateNow = Calendar.getInstance();
		eventParams.put(FlurryHelper.PARAM_OPEN_SUCCESS, "1");
		FlurryHelper.addRegistrationStatusParam(eventParams);		
		eventParams.put(FlurryHelper.PARAM_TIMESTAMP, FlurryHelper.getFlurryFormatDate(dateNow));
		FlurryHelper.addProvinceParam(eventParams);
		eventParams.put(FlurryHelper.PARAM_VERSION, ApplicationDescriptor.currentApplicationDescriptor().getVersion());
		eventParams.put(FlurryHelper.PARAM_OPERATING_SYSTEM, DeviceInfo.getPlatformVersion());
		eventParams.put(FlurryHelper.PARAM_DEVICE, DeviceInfo.getDeviceName());

		FlurryHelper.endTimedEvent(FlurryHelper.EVENT_OPEN_APP, eventParams);
	}

	protected void makeMenu(Menu menu, int instance)
	{
		super.makeMenu(menu, instance);

		if (instance == Menu.INSTANCE_DEFAULT)
		{
			if (fragment_manager.getFieldCount() > 0)
			{
				((Fragment) fragment_manager.getField(0)).makeMenu(menu);
			}
		}

		MenuItem item = new MenuItem("Home", 0x00080000, 0)
		{
			public void run()
			{
				transition(HomeFragment.FRAGMENT_ID, null);
			}
		};
		menu.add(item);

		item = new MenuItem("App Menu", 0x00080000, 1)
		{
			public void run()
			{
				drawerClick();
			}
		};
		menu.add(item);

		item = new MenuItem("Profile", 0x00080000, 2)
		{
			public void run()
			{
				profileClick();
			}
		};
		menu.add(item);

		item = new MenuItem("Help", 0x00090000, 0)
		{
			public void run()
			{
				transition(HelpFragment.FRAGMENT_ID, null);
			}
		};
		menu.add(item);
	}

	/**
	 * Clears screen stack and pushes this screen
	 */
	public static void push()
	{
		UiApplication app = UiApplication.getUiApplication();
		
		int screenCount = app.getScreenCount();
		
		for (int x = 0; x < screenCount - 1; x++)
		{
			app.getActiveScreen().close();
//			x--;
		}
		
		try {
			Screen screen = app.getActiveScreen();
			app.pushScreen(new ViewPagerScreen());
			screen.close();
			RemoteLogger.log("Some Point: ", "Here");
		} catch(Exception e) {
			RemoteLogger.log("ViewPager Exception: ", e + " " + e.getMessage());
		}
	}

	public void pushThemedSpecialsScreen(String categoryId)
	{ 		
		TransitionContext transitionContextIn = new TransitionContext(TransitionContext.TRANSITION_SLIDE);
		transitionContextIn =
				new TransitionContext(
						TransitionContext.TRANSITION_SLIDE);
		transitionContextIn.setIntAttribute(
				TransitionContext.ATTR_DURATION, 1000);
		transitionContextIn.setIntAttribute(
				TransitionContext.ATTR_DIRECTION,
				TransitionContext.DIRECTION_UP);

		TransitionContext transitionContextOut = new TransitionContext(TransitionContext.TRANSITION_SLIDE);
		transitionContextOut =
				new TransitionContext(
						TransitionContext.TRANSITION_SLIDE);
		transitionContextOut.setIntAttribute(
				TransitionContext.ATTR_DURATION, 1000);
		transitionContextOut.setIntAttribute(
				TransitionContext.ATTR_DIRECTION,
				TransitionContext.DIRECTION_DOWN);
		transitionContextOut
		.setIntAttribute(TransitionContext.ATTR_KIND,
				TransitionContext.KIND_OUT);

		UiEngineInstance engine = Ui.getUiEngineInstance();
		ThemedSpecialsScreen screen = new ThemedSpecialsScreen(categoryId);

		engine.setTransition(null, screen,
				UiEngineInstance.TRIGGER_PUSH, transitionContextIn);
		engine.setTransition(screen, null,
				UiEngineInstance.TRIGGER_POP, transitionContextOut);

		UiApplication.getUiApplication().pushScreen(screen);
	}

	// --------- CORE TO THE NAV DRAWERS -----------
	private void openDrawer(int tab)
	{
		if (tab == 0)
		{
			if (drawer.getManager() != null)
			{
				return;
			}
			logNavMenuIconEvent();
			opening = true;
			closing = false;
			actionbar.allowScroll(false);
			manager.insert(drawer, 0);
			manager.setHorizontalScroll(drawer.getPreferredWidth(), false);

			if (animation_task != null) animation_task.cancel(true);
			animation_task = new ScrollAnimateTask();
			animation_task.execute(new Integer[]{new Integer(0)});
		}
		else if (tab == 1)
		{
			if (drawer.getManager() == null && profile.getManager() == null)
			{
				return;
			}
			//end timed events for profile click or navmenu click
			endTimedEventProfileClick();
			endTimedEventNavMenuIconClick();
			
			opening = false;
			closing = true;
			if (drawer.getManager() != null)
			{
				if (animation_task != null) animation_task.cancel(true);
				animation_task = new ScrollAnimateTask();
				animation_task.execute(new Integer[]{new Integer(drawer.getPreferredWidth())});
			}
			else if (profile.getManager() != null)
			{
				if (animation_task != null) animation_task.cancel(true);
				animation_task = new ScrollAnimateTask();
				animation_task.execute(new Integer[]{new Integer(0)});
			}
		}
		else
		{
			if (profile.getManager() != null)
			{
				return;
			}
			logProfileClickEvent();
			opening = true;
			closing = false;
			actionbar.allowScroll(false);
			manager.add(profile);

			if (animation_task != null) animation_task.cancel(true);
			animation_task = new ScrollAnimateTask();
			animation_task.execute(new Integer[]{new Integer(profile.getPreferredWidth())});
		}
	}

	public void drawerClick()
	{
		if (opening || closing) return;

		openDrawer(drawer.getManager() != null || profile.getManager() != null ? 1 : 0);
	}

	public void profileClick()
	{
		if (opening || closing) return;

		openDrawer(drawer.getManager() != null || profile.getManager() != null ? 1 : 2);
	}

	private void logProfileClickEvent()
	{
		loggedTimedEventForProfileClick = true;
		Hashtable eventParams = new Hashtable();

		FlurryHelper.addLoginParams(eventParams);

		FlurryHelper.logEvent(FlurryHelper.EVENT_PROFILE_ICON, eventParams, true);
	}

	private void endTimedEventProfileClick()
	{
		if (loggedTimedEventForProfileClick == true)
		{
			loggedTimedEventForProfileClick = false;
			FlurryHelper.endTimedEvent(FlurryHelper.EVENT_PROFILE_ICON);
		}
	}
	
	private void logNavMenuIconEvent()
	{
		loggedTimedEventForNavMenuClick = true;
		Hashtable eventParams = new Hashtable();

		FlurryHelper.addLoginParams(eventParams);

		FlurryHelper.logEvent(FlurryHelper.EVENT_NAVIGATION_MENU_ICON, eventParams, true);
	}
	
	private void endTimedEventNavMenuIconClick()
	{
		if (loggedTimedEventForNavMenuClick == true)
		{
			loggedTimedEventForNavMenuClick = false;
			FlurryHelper.endTimedEvent(FlurryHelper.EVENT_NAVIGATION_MENU_ICON);
		}
	}

	protected boolean keyChar(char c, int status, int time)
	{
		boolean ret = super.keyChar(c, status, time);
		if (ret == true) return true;

		if (c == Characters.ESCAPE)
		{
			if (drawer.getManager() != null)
			{
				if (!drawer.collapse()) openDrawer(1);
				return true;
			}
			else if (profile.getManager() != null)
			{
				openDrawer(1);
				return true;
			}
			else
			{
				popStack();
			}

		}

		return true;
	}

	public void popStack()
	{
		if (fragment_stack.size() > 1)
		{
			fragment_stack.pop(); // current
			FragmentStackObject stack_object = (FragmentStackObject) fragment_stack.peek(); // previous
			current_fragment_id = stack_object.fragment_id;

			fragment_manager.deleteAll();
			fragment_manager.add(stack_object.fragment);
			actionbar.setTitle(stack_object.actionbar_title);
			stack_object.fragment.invalidate();
		}
		else if (!(fragment_manager.getField(0) instanceof HomeFragment))
		{
			transition(HomeFragment.FRAGMENT_ID, null);
		}
		else
		{
			/*try
			{
				BBMBridge.stopBBM();
				RemoteLogger.log("STOP_BBM", "viewpagerscreen YAY");
			}
			catch (Exception e)
			{
				RemoteLogger.log("STOP_BBM", "viewpagerscreen failed: e: " + e.getMessage());
			}*/
			
			//BBMBridge.stopBBM();
			
			((MainApplication) UiApplication.getUiApplication()).exit(0);
		}
	}

	private void emptyStack()
	{
		while (!fragment_stack.isEmpty())
		{
			FragmentStackObject stack_object = (FragmentStackObject) fragment_stack.pop();
			stack_object.fragment.onClose();
		}
		fragment_stack.removeAllElements();
	}

	public int getStackSize()
	{
		return fragment_stack.size();
	}

	// --------- CORE TO THE NAV DRAWERS -----------
	// detect when drawers are completely open or closed, remove them from the screen when closed
	public void scrollChanged(Manager arg0, int arg1, int arg2)
	{
		if (drawer.getManager() != null)
		{
			if (opening && arg1 == 0)
			{
				opening = false;
				actionbar.selectDrawer();
			}
			// nav drawer closed OR if it reaches this point unexpectedly
			else if (closing && arg1 == drawer.getPreferredWidth())
			{
				closing = false;

				// have to add a spacing on the right otherwise deleting the navigation drawer causes the manager to animate left toward the content on OS 7.
				manager.setMargin(0, drawer.getPreferredWidth(), 0, 0);
				manager.delete(drawer);
				manager.setHorizontalScroll(0, false);

				try
				{
					// block the ui thread to consume the spacing being shown, and the animation if it happens (depends on the thread timing :/).
					Thread.sleep(75);
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}

				manager.setMargin(0, 0, 0, 0);
				updateLayout();
				invalidate();

				actionbar.allowScroll(true);
			}
			else if (!opening && !closing)
			{
				manager.setHorizontalScroll(0, false);
			}
		}
		else if (profile.getManager() != null)
		{
			if (opening && arg1 == profile.getPreferredWidth())
			{
				opening = false;
				actionbar.selectProfile();

				if (PersistentStoreHelper.isShowTutorial2() && (RuntimeStoreHelper.getSessionID() != null))
				{
					TutorialScreen.push(TutorialScreen.PROFILE);
					PersistentStoreHelper.setShowTutorial2(false);
				}
			}
			// profile drawer closed OR if it reaches this point unexpectedly
			else if (closing && arg1 == 0)
			{
				closing = false;
				try
				{
					// have the pause here too so that the animation is consistent.
					Thread.sleep(75);
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				manager.delete(profile);
				updateLayout();
				invalidate();

				actionbar.allowScroll(true);
			}
			else if (!opening && !closing) // dont allow the user to scroll the drawer closed
			{
				manager.setHorizontalScroll(profile.getPreferredWidth(), false);
			}
		}
		else
		{
			manager.setHorizontalScroll(0, false);
		}
	}

	// because OS5 can't animate a horizontal scroll, even though it explicitly states it can.
	private class ScrollAnimateTask extends AsyncTask
	{
		private final int step = ResourceHelper.convert(20); // so that the slide speed is uniform across screensizes
		private final int interval = 25;

		public Object doInBackground(Object[] params)
		{
			int destination = ((Integer) params[0]).intValue();
			int current_pos = manager.getHorizontalScroll();
			int direction = destination > current_pos ? 1 : -1;

			while (current_pos != destination)
			{
				final int new_pos = Math.abs(current_pos - destination) <= step ? destination : current_pos + (direction * step);
				current_pos = new_pos;
				UiApplication.getUiApplication().invokeLater(new Runnable()
				{
					public void run()
					{
						manager.setHorizontalScroll(new_pos);
					}
				});

				if (new_pos != destination)
				{
					try
					{
						Thread.sleep(interval);
					} catch (InterruptedException e)
					{
					}
				}
			}
			return null;
		}
	}

	public void itemClicked(int action, Object[] params)
	{
		transition(action, params);
	}

	public void transition(int index, Object[] params)
	{
		if (loading_fragment) return;
		loading_fragment = true;

		openDrawer(1);
		Object[] args;

		if (params != null)
		{
			args = new Object[params.length + 1];
			args[0] = new Integer(index);
			for (int i = 0; i < params.length; i++)
			{
				args[i + 1] = params[i];
			}
		}
		else
		{
			args = new Object[1];
			args[0] = new Integer(index);
		}

		// create the new layout in a thread, so the ui thread is free to animate the drawer closing
		new AsyncTask()
		{
			protected void onPostExecute(Object result)
			{
				if (result != null)
				{
					fragment_manager.deleteAll();
					fragment_manager.add(((FragmentStackObject) result).fragment);
					actionbar.setTitle(((FragmentStackObject) result).actionbar_title);

					fragment_stack.push(result);
				}

				loading_fragment = false;
			};

			public Object doInBackground(Object[] params)
			{
				int fragment_id = ((Integer) params[0]).intValue();
				Object actionbar_title;
				Fragment new_fragment;

				// empty stack when a new root is chosen (screen selected from nav drawer)
				// and empty the image download queue when the screens are no longer on the stack
				if (fragment_id == HomeFragment.FRAGMENT_ID)
				{
					if (current_fragment_id == fragment_id) return null;
					emptyStack();
					ImageLoader.emptyQueue();
					actionbar_title = new BitmapField(ResourceHelper.getImage("logo_title-bar"));
					new_fragment = new HomeFragment();
				}
				else if (fragment_id == CouponsFragment.FRAGMENT_ID)
				{
					if (current_fragment_id == fragment_id) return null;
					emptyStack();
					ImageLoader.emptyQueue();
					actionbar_title = "EeziCoupons";
					new_fragment = new CouponsFragment();
				}
				else if (fragment_id == SpecialsFragment.FRAGMENT_ID)
				{
					if (current_fragment_id == fragment_id) return null;
					emptyStack();
					ImageLoader.emptyQueue();
					actionbar_title = "Specials";
					new_fragment = new SpecialsFragment();
				}
				else if (fragment_id == DetailedCouponFragment.FRAGMENT_ID)
				{
					actionbar_title = "EeziCoupon Info";
					new_fragment = new DetailedCouponFragment((CampaignData) params[1]);
				}
				else if (fragment_id == DetailedSpecialFragment.FRAGMENT_ID)
				{
					actionbar_title = "Specials Info";//went from specials info, to special info, back to specials info.... really
					new_fragment = new DetailedSpecialFragment((CampaignData) params[1], (Vector) params[2]);
					/*if (params.length>3)
					{
						new_fragment = new DetailedSpecialFragment((CampaignData) params[1], (Vector) params[2], (params[3].equals("true"))?true:false);
					}
					else
					{
						new_fragment = new DetailedSpecialFragment((CampaignData) params[1], (Vector) params[2]);
					}*/					
				}
				else if (fragment_id == SelectProvinceFragment.FRAGMENT_ID)
				{
					if (current_fragment_id == fragment_id) return null;
					emptyStack();
					ImageLoader.emptyQueue();
					actionbar_title = "Find a Store";
					new_fragment = new SelectProvinceFragment();
				}
				else if (fragment_id == SelectStoreFragment.FRAGMENT_ID)
				{
					actionbar_title = "Find a Store";
					
					if ((params[1] != null) && (params[1] instanceof LocationData))
					{
						new_fragment = new SelectStoreFragment((LocationData) params[1]);
					}
					else
					{
						new_fragment = new SelectStoreFragment((String) params[1]);//search, all provinces with this text
					}
				}
				else if (fragment_id == StoreInfoFragment.FRAGMENT_ID)
				{
					actionbar_title = "Find a Store";
					if (params.length > 2)
					{
						if ((params[2] instanceof String) && (params[2].equals("true")))//do set actionbar heading as My Preffered Store
						{
							actionbar_title = "My Preferred Store";
						}
					}
					
					new_fragment = new StoreInfoFragment((MerchantData) params[1]);
				}
				else if (fragment_id == MylistFragment.FRAGMENT_ID)
				{
					if (current_fragment_id == fragment_id) return null;
					emptyStack();
					ImageLoader.emptyQueue();
					actionbar_title = "My List";
					new_fragment = new MylistFragment();
				}
				else if (fragment_id == TermsFragment.FRAGMENT_ID)
				{
					if (current_fragment_id == fragment_id) return null;
					emptyStack();
					ImageLoader.emptyQueue();
					actionbar_title = "Terms";
					new_fragment = new TermsFragment();
				}
				else if (fragment_id == HelpFragment.FRAGMENT_ID)
				{
					if (current_fragment_id == fragment_id) return null;
					emptyStack();
					ImageLoader.emptyQueue();
					actionbar_title = "Help";
					new_fragment = new HelpFragment();
				}
				else if (fragment_id == InfoFragment.FRAGMENT_ID)
				{
					actionbar_title = params[1];
					new_fragment = new InfoFragment((String) params[2], ((Boolean) params[3]).booleanValue());
				}
				else if (fragment_id == ChecklineFragment.FRAGMENT_ID)
				{
					if (current_fragment_id == fragment_id) return null;
					actionbar_title = "Checkline";
					new_fragment = new ChecklineFragment();
				}
				else if (fragment_id == FeedbackFragment.FRAGMENT_ID)
				{
					if (current_fragment_id == fragment_id) return null;
					actionbar_title = "Send Us a Message";
					new_fragment = new FeedbackFragment();
				}
				else if (fragment_id == AboutFragment.FRAGMENT_ID)
				{
					if (current_fragment_id == fragment_id) return null;
					emptyStack();
					ImageLoader.emptyQueue();
					actionbar_title = "About";
					new_fragment = new AboutFragment();
				}
				else if (fragment_id == InboxFragment.FRAGMENT_ID)
				{
					if (current_fragment_id == fragment_id) return null;
					emptyStack();
					ImageLoader.emptyQueue();
					actionbar_title = "Inbox";
					new_fragment = new InboxFragment();
				}
				else if (fragment_id == InboxMessageFragment.FRAGMENT_ID)
				{
					if (current_fragment_id == fragment_id) return null;
					actionbar_title = "Inbox";
					new_fragment = new InboxMessageFragment((InboxMessage) params[1]);
				}
				else
					return null;

				current_fragment_id = fragment_id;
				return new FragmentStackObject(actionbar_title, fragment_id, new_fragment);
			}
		}.execute(args);
	}

	private static class FragmentStackObject
	{
		public final int fragment_id;
		public final Object actionbar_title;
		public final Fragment fragment;

		public FragmentStackObject(Object actionbar_title, int fragment_id, Fragment fragment)
		{
			this.actionbar_title = actionbar_title;
			this.fragment = fragment;
			this.fragment_id = fragment_id;
		}
	}
}