package fi.bb.checkers.ui.fragments;

import java.util.Calendar;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.NullField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.BackgroundFactory;
import fi.bb.checkers.datatypes.FeaturedData;
import fi.bb.checkers.datatypes.UserData;
import fi.bb.checkers.helpers.FlurryHelper;
import fi.bb.checkers.helpers.FormatHelper;
import fi.bb.checkers.helpers.PersistentStoreHelper;
import fi.bb.checkers.helpers.ResourceHelper;
import fi.bb.checkers.helpers.RuntimeStoreHelper;
import fi.bb.checkers.logger.RemoteLogger;
import fi.bb.checkers.ui.components.ColorButtonField;
import fi.bb.checkers.ui.components.FeaturedItem;
import fi.bb.checkers.ui.components.LabelField;
import fi.bb.checkers.ui.screens.TutorialScreen;
import fi.bb.checkers.ui.screens.ViewPagerScreen;
import fi.bb.checkers.utils.StringUtil;

public class HomeFragment extends Fragment
{
	public static final int FRAGMENT_ID = getUUID();
	public static boolean initial = true;
	private VerticalFieldManager content;
	private WelcomeField label_greeting;

	public HomeFragment()
	{
		super(VERTICAL_SCROLL | NO_HORIZONTAL_SCROLL);
		setBackground(BackgroundFactory.createSolidBackground(ResourceHelper.color_background_app));

		label_greeting = new WelcomeField();
		if (initial && RuntimeStoreHelper.getSessionID() != null && PersistentStoreHelper.isPreviousUser(PersistentStoreHelper.getUsername()))
		{
			label_greeting.setGreeting("Welcome back, " + RuntimeStoreHelper.getUserData().getFirstname());
			new Timer().schedule(new TimerTask()
			{
				public void run()
				{
					doWelcomeMessage();
				}
			}, 10000);
		}
		else
		{
			doWelcomeMessage();
		}
		add(label_greeting);

		content = new VerticalFieldManager(NO_VERTICAL_SCROLL | NO_HORIZONTAL_SCROLL | NO_VERTICAL_SCROLLBAR)
		{
			protected void paint(Graphics graphics)
			{
				super.paint(graphics);

				//graphics.drawBitmap(0, getVerticalScroll(), shadow.getWidth(), shadow.getHeight(), shadow, 0, 0);
			}
		};
		content.setScrollListener(this);
		add(content);

		build();

		initial = false;
	}

	protected void onVisibilityChange(boolean visible)
	{
		if (visible)
		{
			logBrowseHomeScreen();
			if (PersistentStoreHelper.isShowTutorial1())
			{
				TutorialScreen.push(TutorialScreen.HOME);
				PersistentStoreHelper.setShowTutorial1(false);
			}
		}
		else
		{
			FlurryHelper.endTimedEvent(FlurryHelper.EVENT_HOME_SCREEN);
		}
		super.onVisibilityChange(visible);
	}

	private void doWelcomeMessage()
	{
		try
		{
			StringBuffer builder = new StringBuffer();
			int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

			if (RuntimeStoreHelper.getSessionID() != null && isUserBirthday())
			{

				builder.append("Happy Birthday");
			}
			else
			{
				if (hour >= 0 && hour < 12)
					builder.append("Good Morning");
				else if (hour >= 12 && hour < 18)
					builder.append("Good Afternoon");
				else
					builder.append("Good Evening");
			}

			if (RuntimeStoreHelper.getSessionID() != null)
			{
				builder.append(", ");
				builder.append(RuntimeStoreHelper.getUserData().getFirstname());
			}
			builder.append(".");

			label_greeting.setGreeting(builder.toString());
		} catch (Exception e)
		{
			RemoteLogger.log("HomeFragment", "doWelcomeMessage: " + e.toString());
		}
	}
	private boolean isUserBirthday()
	{
		UserData userdata = RuntimeStoreHelper.getUserData();
		Calendar current_date = Calendar.getInstance();
		String[] tokens = StringUtil.split(userdata.getBirthdate(), "/");
		return current_date.get(Calendar.DAY_OF_MONTH) == Integer.parseInt(tokens[0]) && (current_date.get(Calendar.MONTH)+1) == Integer.parseInt(tokens[1]);
	}

	private class WelcomeField extends Field
	{
		private String greeting = "";

		public WelcomeField()
		{
			setFont(ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(18), Ui.UNITS_px));
		}

		public void setGreeting(String greeting)
		{
			String[] lines = StringUtil.ellipsize(getFont(), greeting, getPreferredWidth(), 1);
			if (lines.length != 0) greeting = lines[0];

			this.greeting = greeting;
			invalidate();
		}

		protected void layout(int width, int height)
		{
			setExtent(getPreferredWidth(), getPreferredHeight());
		}

		public int getPreferredWidth()
		{
			return Display.getWidth();
		}

		public int getPreferredHeight()
		{
			return ResourceHelper.convert(30);
		}

		protected void paint(Graphics graphics)
		{
			graphics.setColor(0xe2e2e2);
			graphics.fillRect(0, 0, getWidth(), getHeight());

			if (greeting.indexOf(',') != -1)
			{
				String greetingPart1 = greeting.substring(0, greeting.indexOf(',')+1);
				String greetingPart2 = greeting.substring(greeting.indexOf(',')+1);

				int x = (getWidth() - getFont().getAdvance(greeting)) / 2;
				int y = (getHeight() - getFont().getHeight()) / 2;
				graphics.setColor(ResourceHelper.color_black);
				graphics.drawText(greetingPart1, x, y);

				x = x + getFont().getAdvance(greetingPart1);
				graphics.setColor(ResourceHelper.color_checkers_teal);
				graphics.drawText(greetingPart2, x, y);	
			}
			else
			{
				int x = (getWidth() - getFont().getAdvance(greeting)) / 2;
				int y = (getHeight() - getFont().getHeight()) / 2;
				graphics.setColor(ResourceHelper.color_black);
				graphics.drawText(greeting, x, y);
			}
		}
	};

	private void build()
	{

		new Thread()
		{
			public void run()
			{
				// static panes
				Field field = buildCouponPane();
				synchronized (Application.getEventLock())
				{
					content.add(field);
				}
				field = buildSpecials();
				field.setMargin(ResourceHelper.convert(3), 0, 0, 0);
				synchronized (Application.getEventLock())
				{
					content.add(field);
				}
				field = buildFindAStore();
				field.setMargin(ResourceHelper.convert(3), 0, 0, 0);
				synchronized (Application.getEventLock())
				{
					content.add(field);
				}

				// dynamic panes
				for (int i = 0; i < RuntimeStoreHelper.getFeaturedList().size(); i++)
				{
					FeaturedData data = (FeaturedData) RuntimeStoreHelper.getFeaturedList().elementAt(i);
					field = new FeaturedItem(data)
					{
						public void clickButton() {
							super.clickButton();
						}
					};
					field.setMargin(ResourceHelper.convert(3), 0, 0, 0);

					RemoteLogger.log("DEBUG_FEATURE", "action: " + data.getAction() + " action_detail: " +data.getActionDetail() + " url: " + data.getImageURL());
					((FeaturedItem)field).setTag((data.getAction().equals("THEME_SPECIALS") == true)?data.getActionDetail():null);//categoryId

					synchronized (Application.getEventLock())
					{
						content.add(field);
						((FeaturedItem)field).setBlockNumber(content.getFieldCount());
					}
				}
			};
		}.start();
	}

	private Field buildCouponPane()
	{
		ColorButtonField button;
		VerticalFieldManager vertical_manager;
		HorizontalFieldManager horizontal_manager;
		LabelField label;
		BitmapField icon;
		final Bitmap backgroundimage = ResourceHelper.getImage("background_cell_eezicoupons.jpg");

		/*int totalsaving = 0;
		for (int i = 0; i < RuntimeStoreHelper.getCoupons().size(); i++)
		{
			CampaignData coupon = (CampaignData) RuntimeStoreHelper.getCoupons().elementAt(i);
			totalsaving += (Integer.parseInt(coupon.getValue()) / 100);
		}*/

		double totalSavings;
		try
		{
			totalSavings = (Double.parseDouble(PersistentStoreHelper.getTotalSavings())/100.0);
		}
		catch (Exception e) 
		{
			totalSavings = 0.0;
		}

		// content
		final VerticalFieldManager content_manager = new VerticalFieldManager(NO_VERTICAL_SCROLL | NO_HORIZONTAL_SCROLL)
		{
			protected void sublayout(int maxWidth, int maxHeight)
			{
				maxWidth = Display.getWidth();
				maxHeight = backgroundimage.getHeight();
				super.sublayout(maxWidth, maxHeight);
				setExtent(maxWidth, maxHeight);
			}

			protected void paint(Graphics graphics)
			{
				graphics.drawBitmap((getWidth() - backgroundimage.getWidth()) / 2, 0, backgroundimage.getWidth(), backgroundimage.getHeight(), backgroundimage, 0, 0);

				super.paint(graphics);
			}
		};
		vertical_manager = new VerticalFieldManager(NO_VERTICAL_SCROLL | NO_HORIZONTAL_SCROLL)
		{
			protected void sublayout(int maxWidth, int maxHeight)
			{
				maxWidth = Display.getWidth();
				maxHeight = backgroundimage.getHeight() - ResourceHelper.convert(44+2);
				super.sublayout(maxWidth, maxHeight);
				setExtent(maxWidth, maxHeight);
			}
		};
		horizontal_manager = new HorizontalFieldManager(NO_VERTICAL_SCROLL | NO_HORIZONTAL_SCROLL);
		vertical_manager.add(horizontal_manager);
		horizontal_manager.setMargin(ResourceHelper.convert(10), ResourceHelper.convert(10), ResourceHelper.convert(10), ResourceHelper.convert(10));
		icon = new BitmapField(ResourceHelper.getImage("icon_home_eezicoupon"));
		icon.setMargin(0, ResourceHelper.convert(5), 0, 0);
		horizontal_manager.add(icon);
		label = new LabelField("EeziCoupons", ResourceHelper.color_dark_grey, DrawStyle.VCENTER)
		{
			public int getPreferredHeight()
			{
				return ResourceHelper.getImage("icon_home_eezicoupon").getHeight();
			}
		};
		label.setFont(ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(24), Ui.UNITS_px));
		horizontal_manager.add(label);

		horizontal_manager = new HorizontalFieldManager(NO_VERTICAL_SCROLL | NO_HORIZONTAL_SCROLL);
		vertical_manager.add(horizontal_manager);
		horizontal_manager.setMargin(ResourceHelper.convert(10), ResourceHelper.convert(10), ResourceHelper.convert(10), ResourceHelper.convert(10));
		label = new LabelField("SAVE R", ResourceHelper.color_primary, DrawStyle.LEADING);
		label.setFont(ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(44), Ui.UNITS_px));
		horizontal_manager.add(label);

		String priceText = FormatHelper.getPriceFormattedHome(totalSavings);
		int indexOfDot = priceText.indexOf('.');

		if (indexOfDot!=-1)
		{
			priceText = priceText.substring(0, indexOfDot);
		}

		label = new LabelField("" + priceText, ResourceHelper.color_primary, DrawStyle.LEADING);
		label.setFont(ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(70), Ui.UNITS_px));
		horizontal_manager.add(label);

		final int buttonHeight = ResourceHelper.convert(30);
		
		button = new ColorButtonField(ResourceHelper.color_checkers_teal, ResourceHelper.color_white, ResourceHelper.convert(144),  buttonHeight)
		{
			public void clickButton() {
				super.clickButton();
				logRedeemAll();
				((ViewPagerScreen) UiApplication.getUiApplication().getActiveScreen()).profileClick();
			}
		};
		button.setButtonOutlineStates(ResourceHelper.color_checkers_teal, ResourceHelper.color_checkers_teal, ResourceHelper.color_checkers_teal);
		button.setButtonText("Redeem All");
		button.setTextStates(ResourceHelper.color_white, ResourceHelper.color_checkers_teal, ResourceHelper.color_checkers_teal);
		
		/*button = new TextImageButton("Redeem All", "btn_home_default", "btn_home_hover")//, buttonWidth)
		{
			public void clickButton()
			{
				super.clickButton();
				logRedeemAll();
				((ViewPagerScreen) UiApplication.getUiApplication().getActiveScreen()).profileClick();
			}
		};
		button.setTextColor(ResourceHelper.color_white);
		button.setTextColorHover(ResourceHelper.color_primary);
		button.setTextColorPressed(ResourceHelper.color_primary);*/
		// button manager
		final HorizontalFieldManager button_manager = new HorizontalFieldManager(NO_VERTICAL_SCROLL | NO_HORIZONTAL_SCROLL)
		{
			protected void sublayout(int maxWidth, int maxHeight)
			{
				maxWidth = Display.getWidth();
				maxHeight = ResourceHelper.convert(44);
				super.sublayout(maxWidth, maxHeight);
				setExtent(maxWidth, maxHeight);
			}
		};
		//vertical_manager.setBorder(BorderFactory.createSimpleBorder(new XYEdges(1, 1, 1, 1), new XYEdges(ResourceHelper.color_shoprite_red, ResourceHelper.color_shoprite_red, ResourceHelper.color_shoprite_red, ResourceHelper.color_shoprite_red), Border.STYLE_SOLID));
		//button_manager.setBorder(BorderFactory.createSimpleBorder(new XYEdges(1, 1, 1, 1), new XYEdges(ResourceHelper.color_checkers_teal, ResourceHelper.color_checkers_teal, ResourceHelper.color_checkers_teal, ResourceHelper.color_checkers_teal), Border.STYLE_SOLID));

		button_manager.add(button);
		int margin = (Display.getWidth() - ResourceHelper.convert(144)*2)/3;
		button.setMargin(ResourceHelper.convert(44-30)/2, 0, 0, margin);

		button = new ColorButtonField(ResourceHelper.color_checkers_teal, ResourceHelper.color_white, ResourceHelper.convert(144),  buttonHeight)
		{
			public void clickButton() {
				super.clickButton();
				logViewAllEeziCoupons();
				((ViewPagerScreen) UiApplication.getUiApplication().getActiveScreen()).transition(CouponsFragment.FRAGMENT_ID, null);
			}
		};
		button.setButtonOutlineStates(ResourceHelper.color_checkers_teal, ResourceHelper.color_checkers_teal, ResourceHelper.color_checkers_teal);
		button.setButtonText("Browse All");
		button.setTextStates(ResourceHelper.color_white, ResourceHelper.color_checkers_teal, ResourceHelper.color_checkers_teal);
		button.setMargin(ResourceHelper.convert(44-30)/2, 0, 0, margin);
		button_manager.add(button);
		/*button = new TextImageButton("Browse All", "btn_home_default", "btn_home_hover")//, buttonWidth)
		{
			public void clickButton()
			{
				super.clickButton();
				logViewAllEeziCoupons();
				((ViewPagerScreen) UiApplication.getUiApplication().getActiveScreen()).transition(CouponsFragment.FRAGMENT_ID, null);
			}
		};
		button.setTextColor(ResourceHelper.color_white);
		button.setTextColorHover(ResourceHelper.color_primary);
		button.setTextColorPressed(ResourceHelper.color_primary);
		button_manager.add(button);*/
		//button.setMargin(0, 0, 0, 0);//(ResourceHelper.convert(55) - button.getPreferredHeight()) / 2
		
		content_manager.add(new NullField(FOCUSABLE));
		content_manager.add(vertical_manager);
		
		//button_manager.setMargin(ResourceHelper.convert(10), 0, 0, 0);
		
		content_manager.add(button_manager);
		return content_manager;
	}

	private void logRedeemAll()
	{
		Calendar dateNow = Calendar.getInstance();
		Hashtable eventParams = new Hashtable();

		eventParams.put(FlurryHelper.PARAM_TAPPED, "1");
		eventParams.put(FlurryHelper.PARAM_BLOCK_NUMBER, "1");//Always one... and added this param, since it is the duplicate's extra param...see home screen section: EeziCoupon Redeem All button...
		eventParams.put(FlurryHelper.PARAM_TIME, FlurryHelper.getFlurryFormatDate(dateNow));
		FlurryHelper.addProvinceParam(eventParams);
		FlurryHelper.addLoginParams(eventParams);	
		FlurryHelper.addFirstLaunchParam(eventParams);

		FlurryHelper.logEvent(FlurryHelper.EVENT_REDEEM_ALL_HOME, eventParams, false);
	}

	private void logViewAllEeziCoupons()
	{
		Hashtable eventParams = new Hashtable();

		eventParams.put(FlurryHelper.PARAM_TAPPED, "1");
		eventParams.put(FlurryHelper.PARAM_TIME, FlurryHelper.getFlurryFormatDate(Calendar.getInstance()));
		FlurryHelper.addProvinceParam(eventParams);
		FlurryHelper.addLoginParams(eventParams);			

		FlurryHelper.logEvent(FlurryHelper.EVENT_EEZI_COUPON_VIEW_ALL_BUTTON, eventParams, true);
	}

	private void logViewAllSpecials()
	{
		Hashtable eventParams = new Hashtable();

		eventParams.put(FlurryHelper.PARAM_TAPPED, "1");
		eventParams.put(FlurryHelper.PARAM_TIME, FlurryHelper.getFlurryFormatDate(Calendar.getInstance()));
		FlurryHelper.addProvinceParam(eventParams);
		FlurryHelper.addLoginParams(eventParams);			

		FlurryHelper.logEvent(FlurryHelper.EVENT_SPECIALS_VIEW_ALL, eventParams, true);
	}

	private void logBrowseHomeScreen()
	{
		Hashtable eventParams = new Hashtable();

		eventParams.put(FlurryHelper.PARAM_TIME, FlurryHelper.getFlurryFormatDate(Calendar.getInstance()));

		FlurryHelper.logEvent(FlurryHelper.EVENT_HOME_SCREEN, eventParams, true);
	}

	private void logViewStores()
	{
		Hashtable eventParams = new Hashtable();

		eventParams.put(FlurryHelper.PARAM_TAPPED, "1");
		eventParams.put(FlurryHelper.PARAM_TIME, FlurryHelper.getFlurryFormatDate(Calendar.getInstance()));
		eventParams.put(FlurryHelper.PARAM_BLOCK_NUMBER, "3");
		FlurryHelper.addProvinceParam(eventParams);
		FlurryHelper.addLoginParams(eventParams);			

		FlurryHelper.logEvent(FlurryHelper.EVENT_FIND_A_STORE_BLOCK, eventParams, true);
	}

	private Field buildSpecials()
	{
		ColorButtonField button;
		HorizontalFieldManager horizontal_manager;
		LabelField label;
		BitmapField icon;
		final Bitmap backgroundimage = ResourceHelper.getImage("background_cell_specials.jpg");

		VerticalFieldManager content_manager = new VerticalFieldManager(NO_VERTICAL_SCROLL | NO_HORIZONTAL_SCROLL)
		{

			protected void sublayout(int maxWidth, int maxHeight)
			{
				maxWidth = Display.getWidth();
				maxHeight = backgroundimage.getHeight();
				super.sublayout(maxWidth, maxHeight);
				setExtent(maxWidth, maxHeight);
			}

			protected void paint(Graphics graphics)
			{
				graphics.drawBitmap((getWidth() - backgroundimage.getWidth()) / 2, 0, backgroundimage.getWidth(), backgroundimage.getHeight(), backgroundimage, 0, 0);

				super.paint(graphics);
			}
		};
		content_manager.add(new NullField(FOCUSABLE));

		horizontal_manager = new HorizontalFieldManager(NO_VERTICAL_SCROLL | NO_HORIZONTAL_SCROLL)
		{
			protected void sublayout(int maxWidth, int maxHeight)
			{
				maxWidth = Display.getWidth();
				maxHeight = ResourceHelper.convert(44);
				super.sublayout(maxWidth, maxHeight);
				setExtent(maxWidth, maxHeight);
			}
		};
		
		//horizontal_manager.setBorder(BorderFactory.createSimpleBorder(new XYEdges(1, 1, 1, 1), new XYEdges(ResourceHelper.color_checkers_teal, ResourceHelper.color_checkers_teal, ResourceHelper.color_checkers_teal, ResourceHelper.color_checkers_teal), Border.STYLE_SOLID));
		
		horizontal_manager.setMargin(backgroundimage.getHeight() - ResourceHelper.convert(44), 0, 0, 0);
		
		content_manager.add(horizontal_manager);
		
		horizontal_manager.setMargin(backgroundimage.getHeight() - ResourceHelper.convert(44+2), 0, 0, 0);
		icon = new BitmapField(ResourceHelper.getImage("icon_home_specials"), 0);
		RemoteLogger.log("HOME_DEBUG","icon height: bitmaph|" + icon.getBitmapHeight()+"| height: |" + icon.getHeight() +"| prefHeight:|" + icon.getPreferredHeight() + "|");
		icon.setMargin((ResourceHelper.convert(44) - icon.getBitmapHeight()) / 2, ResourceHelper.convert(5), 0, ResourceHelper.convert(10));
		horizontal_manager.add(icon);

		Font labelFont = ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(24), Ui.UNITS_px);
		label = new LabelField("Specials", ResourceHelper.color_dark_grey, 0);
		label.setMargin((ResourceHelper.convert(44) - labelFont.getHeight()) / 2, 0, 0, 0);
		label.setFont(labelFont);
		horizontal_manager.add(label);

		button = new ColorButtonField(ResourceHelper.color_checkers_teal, ResourceHelper.color_white, ResourceHelper.convert(144),  ResourceHelper.convert(30))
		{
			public void clickButton() {
				super.clickButton();
				RemoteLogger.log("CATEGORY", "VIEW ALL! specials");
				logViewAllSpecials();
				((ViewPagerScreen) UiApplication.getUiApplication().getActiveScreen()).transition(SpecialsFragment.FRAGMENT_ID, null);
			}
		};
		button.setButtonOutlineStates(ResourceHelper.color_checkers_teal, ResourceHelper.color_checkers_teal, ResourceHelper.color_checkers_teal);
		button.setButtonText("View All");
		button.setTextStates(ResourceHelper.color_white, ResourceHelper.color_checkers_teal, ResourceHelper.color_checkers_teal);
		
		/*button = new TextImageButton("View All", "btn_home_default", "btn_home_hover")
		{
			public void clickButton()
			{
				super.clickButton();
				RemoteLogger.log("CATEGORY", "VIEW ALL! specials");
				logViewAllSpecials();
				((ViewPagerScreen) UiApplication.getUiApplication().getActiveScreen()).transition(SpecialsFragment.FRAGMENT_ID, null);
			}
		};
		button.setTextColor(ResourceHelper.color_white);
		button.setTextColorHover(ResourceHelper.color_primary);
		button.setTextColorPressed(ResourceHelper.color_primary);*/
		RemoteLogger.log("HOME_DEBUG","button margin: |" + (ResourceHelper.convert(44-30) / 2) + "|");
		button.setMargin(ResourceHelper.convert(44-30) / 2, 0, 0, Display.getWidth() - icon.getPreferredWidth() - label.getPreferredWidth() - button.getPreferredWidth()
				- ResourceHelper.convert(25));
		horizontal_manager.add(button);

		return content_manager;

	}

	private Field buildFindAStore()
	{
		ColorButtonField button;
		HorizontalFieldManager horizontal_manager;
		LabelField label;
		BitmapField icon;
		final Bitmap backgroundimage = ResourceHelper.getImage("background_cell_find_a_store.jpg");

		VerticalFieldManager content_manager = new VerticalFieldManager(NO_VERTICAL_SCROLL | NO_HORIZONTAL_SCROLL)
		{
			protected void sublayout(int maxWidth, int maxHeight)
			{
				maxWidth = Display.getWidth();
				maxHeight = backgroundimage.getHeight();
				super.sublayout(maxWidth, maxHeight);
				setExtent(maxWidth, maxHeight);
			}

			protected void paint(Graphics graphics)
			{
				graphics.drawBitmap((getWidth() - backgroundimage.getWidth()) / 2 , 0, backgroundimage.getWidth(), backgroundimage.getHeight(), backgroundimage, 0, 0);

				super.paint(graphics);
			}
		};
		content_manager.add(new NullField(FOCUSABLE));

		horizontal_manager = new HorizontalFieldManager(NO_VERTICAL_SCROLL | NO_HORIZONTAL_SCROLL)
		{
			protected void sublayout(int maxWidth, int maxHeight)
			{
				maxWidth = Display.getWidth();
				maxHeight = ResourceHelper.convert(44);
				super.sublayout(maxWidth, maxHeight);
				setExtent(maxWidth, maxHeight);
			}
		};
		//horizontal_manager.setBorder(BorderFactory.createSimpleBorder(new XYEdges(1, 1, 1, 1), new XYEdges(ResourceHelper.color_shoprite_red, ResourceHelper.color_shoprite_red, ResourceHelper.color_shoprite_red, ResourceHelper.color_shoprite_red), Border.STYLE_SOLID));
		horizontal_manager.setMargin(backgroundimage.getHeight() - ResourceHelper.convert(44+2), 0, 0, 0);
		content_manager.add(horizontal_manager);
		icon = new BitmapField(ResourceHelper.getImage("icon_home_find_a_store"), 0);
		icon.setMargin((ResourceHelper.convert(44) - icon.getPreferredHeight()) / 2, ResourceHelper.convert(5), 0, ResourceHelper.convert(10));
		horizontal_manager.add(icon);

		Font labelFont = ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(24), Ui.UNITS_px);
		label = new LabelField("Find a Store", ResourceHelper.color_dark_grey, 0);
		label.setMargin((ResourceHelper.convert(44) - labelFont.getHeight()) / 2, 0, 0, 0);
		label.setFont(labelFont);
		horizontal_manager.add(label);

		button = new ColorButtonField(ResourceHelper.color_checkers_teal, ResourceHelper.color_white, ResourceHelper.convert(144),  ResourceHelper.convert(30))
		{
			public void clickButton() {
				super.clickButton();
				logViewStores();
				((ViewPagerScreen) UiApplication.getUiApplication().getActiveScreen()).transition(SelectProvinceFragment.FRAGMENT_ID, null);
			}
		};
		button.setButtonOutlineStates(ResourceHelper.color_checkers_teal, ResourceHelper.color_checkers_teal, ResourceHelper.color_checkers_teal);
		button.setButtonText("View Stores");
		button.setTextStates(ResourceHelper.color_white, ResourceHelper.color_checkers_teal, ResourceHelper.color_checkers_teal);
		
		/*button = new TextImageButton("View Stores", "btn_home_default", "btn_home_hover")
		{
			public void clickButton()
			{
				super.clickButton();
				logViewStores();
				((ViewPagerScreen) UiApplication.getUiApplication().getActiveScreen()).transition(SelectProvinceFragment.FRAGMENT_ID, null);
			}
		};
		button.setTextColor(ResourceHelper.color_white);
		button.setTextColorHover(ResourceHelper.color_primary);
		button.setTextColorPressed(ResourceHelper.color_primary);*/
		horizontal_manager.add(button);
		button.setMargin((ResourceHelper.convert(44-30)) / 2, 0, 0, Display.getWidth() - icon.getPreferredWidth() - label.getPreferredWidth() - button.getPreferredWidth()
				- ResourceHelper.convert(25));

		return content_manager;

	}
}