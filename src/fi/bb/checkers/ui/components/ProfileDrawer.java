package fi.bb.checkers.ui.components;

import java.util.Calendar;
import java.util.Hashtable;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.BackgroundFactory;
import net.rim.device.api.ui.picker.FilePicker;
import fi.bb.checkers.datatypes.InboxMessage;
import fi.bb.checkers.datatypes.UserData;
import fi.bb.checkers.helpers.FlurryHelper;
import fi.bb.checkers.helpers.PersistentStoreHelper;
import fi.bb.checkers.helpers.ResourceHelper;
import fi.bb.checkers.helpers.RuntimeStoreHelper;
import fi.bb.checkers.helpers.StringHelper;
import fi.bb.checkers.interfaces.InterfaceCouponsFinishedLoading;
import fi.bb.checkers.prompts.InfoDialog;
import fi.bb.checkers.prompts.PhotoPrompt;
import fi.bb.checkers.ui.fragments.MylistFragment;
import fi.bb.checkers.ui.screens.CameraScreen;
import fi.bb.checkers.ui.screens.EditProfileScreen;
import fi.bb.checkers.ui.screens.LoginScreen;
import fi.bb.checkers.ui.screens.RegistrationScreen;
import fi.bb.checkers.ui.screens.ViewPagerScreen;
import fi.bb.checkers.utils.ApplicationInterface;
import fi.bb.checkers.utils.BitmapTools;
import fi.bb.checkers.utils.StringUtil;

public class ProfileDrawer extends VerticalFieldManager implements InterfaceCouponsFinishedLoading
{
	private static final String EXTENTIONS = ".jpg:.jpeg:.png:.bmp";

	private Bitmap shadow;
	private BaseButton name_button;
	private LabelField name_label;
	private Bitmap profile_image;
	private TextImageButton button_profile;
	private VerticalFieldManager content;
	private int mylistsize = 0;
	private int inboxsize = 0;
	private LabelField label_wicode;

	private HorizontalFieldManager manager;//profile button basically

	boolean buildDone;
	boolean isVisible;

	public ProfileDrawer()
	{
		super(VERTICAL_SCROLL | NO_VERTICAL_SCROLLBAR | NO_HORIZONTAL_SCROLL | USE_ALL_HEIGHT);
		setBackground(BackgroundFactory.createSolidBackground(ResourceHelper.color_drawer_grey));
		shadow = BitmapTools.resizeTransparentBitmap(ResourceHelper.getImage("left_gradient"), ResourceHelper.convert(5), getPreferredHeight(), Bitmap.FILTER_LANCZOS, Bitmap.SCALE_STRETCH);

		isVisible = false;

		final UserData userdata = RuntimeStoreHelper.getUserData();

		String username = RuntimeStoreHelper.getSessionID() == null ? "guest" : userdata.getUsername();
		String text = "";

		String path = null;

		if (RuntimeStoreHelper.getUserData() != null)
		{
			path = PersistentStoreHelper.getProfileUrl(RuntimeStoreHelper.getUserData().getUsername());
		}

		if (!username.equals("guest") && (path == null))
		{
			text = "Add Photo";
		}

		//new ImageButton("profile-picture_mask_default", "profile-picture_mask_hover")
		button_profile = new TextImageButton(text, "profile-picture_mask_default", "profile-picture_mask_hover")
		{
			protected void paint(Graphics g)
			{
				if (profile_image != null) g.drawBitmap(0, 0, getWidth(), getHeight(), profile_image, 0, 0);

				super.paint(g);
			}

			public void clickButton()
			{
				super.clickButton();

				String username = RuntimeStoreHelper.getSessionID() == null ? "guest" : userdata.getUsername();

				if (!username.equals("guest"))
				{
					int choice = PhotoPrompt.doModal();
					if (choice == PhotoPrompt.CAMERA)
					{
						Bitmap pic = CameraScreen.doModal();
						if (pic != null)
						{
							String path = BitmapTools.saveBitmapToDisk("profile_" + username + ".jpg", pic);
							if (path != null)
							{
								PersistentStoreHelper.insertProfileUrl(username, path);
								setText("");
								profile_image = BitmapTools.resizeTransparentBitmap(pic, getWidth(), getHeight(), Bitmap.FILTER_BILINEAR, Bitmap.SCALE_STRETCH);
								invalidate();
							}
						}
					}
					else if (choice == PhotoPrompt.GALLERY)
					{
						String path = getFromGallery(PersistentStoreHelper.getProfileUrl(username));
						if (path != null)
						{
							PersistentStoreHelper.insertProfileUrl(username, path);
							setText("");
							profile_image = BitmapTools.resizeTransparentBitmap(ResourceHelper.getImageFromFile(path), getWidth(), getHeight(), Bitmap.FILTER_BILINEAR, Bitmap.SCALE_STRETCH);
							invalidate();
						}
					}
				}
			}
		};
		button_profile.setTextColor(ResourceHelper.color_checkers_teal);
		button_profile.setTextColorHover(ResourceHelper.color_checkers_teal);
		button_profile.setTextColorPressed(ResourceHelper.color_checkers_teal);

		name_button = new BaseButton()
		{
			private final String name = userdata == null ? "" : userdata.getFirstname() + " " + userdata.getSurname();

			public int getPreferredHeight()
			{
				return button_profile.getPreferredHeight();
			}

			public int getPreferredWidth()
			{
				return ProfileDrawer.this.getPreferredWidth() - button_profile.getPreferredWidth() - ResourceHelper.convert(5);
			}

			protected void layout(int width, int height)
			{
				width = getPreferredWidth();
				height = getPreferredHeight();
				super.layout(width, height);
				setExtent(width, height);
			}

			protected void paint(Graphics graphics)
			{
				int x;
				int y;

				Bitmap img = ResourceHelper.getImage("icon_edit_default");
				y = (getHeight() - getFont().getHeight()) / 2 - img.getHeight();
				x = getWidth() - img.getWidth() - ResourceHelper.convert(5);
				graphics.drawBitmap(x, y, img.getWidth(), img.getHeight(), img, 0, 0);

				graphics.setColor(ResourceHelper.color_black);

				String[] nameArray = StringUtil.ellipsize(getFont(), name, getPreferredWidth(), 2);

				for (int i=0; i < nameArray.length; i++)
				{
					graphics.drawText(nameArray[i], ResourceHelper.convert(5), ((getHeight() - getFont().getHeight()) / 2) + (i*(getFont().getHeight())));
				}

				if (isFocus())
				{
					graphics.setColor(ResourceHelper.color_primary);
					int line_width = ResourceHelper.convert(2);
					y = (getHeight() - getFont().getHeight()) / 2 - img.getHeight() - ResourceHelper.convert(10);
					int y1 = getHeight() - y;

					for (int i = 0; i < line_width; i++)
					{
						graphics.drawRect(i, y + i, getWidth() - (i * 2), y1 - (i * 2));
					}
				}
			}

			public void clickButton()
			{
				super.clickButton();
				logEditProfileClicked();
				UiApplication.getUiApplication().pushScreen(new EditProfileScreen());
			}
		};
		name_button.setFont(ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(20), Ui.UNITS_px));

		name_label = new LabelField("Guest", ResourceHelper.color_black, 0);
		name_label.setMargin((button_profile.getPreferredHeight() - name_label.getPreferredHeight()) / 2, 0, 0, ResourceHelper.convert(5));
		name_label.setFont(ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(20), Ui.UNITS_px));

		manager = new HorizontalFieldManager();
		manager.setMargin(0, 0, ResourceHelper.convert(10), 0);
		manager.add(button_profile);
		add(manager);

		content = new VerticalFieldManager(NO_HORIZONTAL_SCROLL);
		add(content);

		build();
	}

	private void build()
	{
		buildDone = false;

		if (RuntimeStoreHelper.getSessionID() == null)
		{
			//manager.add(name_label);
			buildOffline();
		}
		else
		{
			if (isVisible == true)
			{
				if (RuntimeStoreHelper.loadCouponsinBackground(this, true, "Loading") == true)
				{
					return;
				}

				manager.add(name_button);
				buildOnline();
			}
		}
	}

	protected void onVisibilityChange(boolean visible)
	{
		isVisible = visible;

		if (visible)
		{			
			if (RuntimeStoreHelper.getSessionID() != null)
			{
				if (buildDone == true)
				{
					mylistsize = PersistentStoreHelper.mylistSize();
					inboxsize = PersistentStoreHelper.inboxUnread();

					if (label_wicode != null)
					{
						if (RuntimeStoreHelper.getUserData().getWicode() == null || RuntimeStoreHelper.getUserData().getWicode().equals(""))
							label_wicode.setText(" -- -- ---");
						else
							label_wicode.setText(" " + StringUtil.formatDivisions(2, RuntimeStoreHelper.getUserData().getWicode()));
					}
				}
				else
				{
					try
					{
						mylistsize = PersistentStoreHelper.mylistSize();
					}
					catch (Exception e)
					{
						//on first load might be null the list
					}
					build();
				}
			}
		}
		super.onVisibilityChange(visible);
	}

	protected void sublayout(int maxWidth, int maxHeight)
	{
		super.sublayout(getPreferredWidth(), getPreferredHeight());
		setExtent(getPreferredWidth(), getPreferredHeight());
	}

	protected void paint(Graphics graphics)
	{
		super.paint(graphics);

		graphics.drawBitmap(0, 0, shadow.getWidth(), shadow.getHeight(), shadow, 0, 0);
	}

	public int getPreferredWidth()
	{
		return Display.getWidth() - ResourceHelper.convert(50);
	}

	public int getPreferredHeight()
	{
		return Display.getHeight();
	}

	private void buildOnline()
	{
		content.deleteAll();

		String path = PersistentStoreHelper.getProfileUrl(RuntimeStoreHelper.getUserData().getUsername());
		if (path != null)
		{
			profile_image = ResourceHelper.getImageFromFile(path);
		}

		if (profile_image == null)
		{
			profile_image = ResourceHelper.getImage("profile-picture_default");
		}
		else
		{
			profile_image = BitmapTools.resizeTransparentBitmap(profile_image, button_profile.getPreferredWidth(), button_profile.getPreferredHeight(), Bitmap.FILTER_BILINEAR, Bitmap.SCALE_STRETCH);
		}

		name_button.set_visible(true);

		HorizontalFieldManager manager = new HorizontalFieldManager(NO_VERTICAL_SCROLL | NO_HORIZONTAL_SCROLL)
		{
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

			protected void paint(Graphics graphics)
			{
				super.paint(graphics);

				if (isFocus())
				{
					graphics.setColor(ResourceHelper.color_primary);
					int line_width = ResourceHelper.convert(2);
					for (int i = 0; i < line_width; i++)
					{
						graphics.drawRect(i, i, getWidth() - (i * 2), getHeight() - (i * 2));
					}
				}
			}
		};
		manager.setMargin(0, ResourceHelper.convert(10), 0, ResourceHelper.convert(10));

		final ImageButton button_info = new ImageButton("icon_terms_default", "icon_terms_default", -1, FIELD_VCENTER)
		{
			public void clickButton()
			{
				// build bulleted text views. Html here breaks the terms fragment.
				Font font = ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(17), Ui.UNITS_px);
				VerticalFieldManager manager = new VerticalFieldManager(NO_VERTICAL_SCROLL | NO_HORIZONTAL_SCROLL | FIELD_LEFT);

				LabelField label = new LabelField("Use this WiCode to redeem your EeziCoupons in three simple steps:", ResourceHelper.color_black, 0);
				label.setMargin(ResourceHelper.convert(5), 0, 0, 0);
				label.setFont(font);
				manager.add(label);

				HorizontalFieldManager bullet_manager = new HorizontalFieldManager(NO_VERTICAL_SCROLL | NO_HORIZONTAL_SCROLL | FIELD_LEFT);
				bullet_manager.setMargin(ResourceHelper.convert(10), 0, 0, 0);

				label = new LabelField("1. ", ResourceHelper.color_black, 0);
				label.setFont(font);
				bullet_manager.add(label);
				label = new LabelField(StringHelper.visit_nearest_store, ResourceHelper.color_black, 0);
				label.setFont(font);
				bullet_manager.add(label);
				manager.add(bullet_manager);

				bullet_manager = new HorizontalFieldManager(NO_VERTICAL_SCROLL | NO_HORIZONTAL_SCROLL | FIELD_LEFT);
				label = new LabelField("2. ", ResourceHelper.color_black, 0);
				label.setFont(font);
				bullet_manager.add(label);
				label = new LabelField(StringHelper.buy_items_desc, ResourceHelper.color_black, 0);
				label.setFont(font);
				bullet_manager.add(label);
				manager.add(bullet_manager);

				bullet_manager = new HorizontalFieldManager(NO_VERTICAL_SCROLL | NO_HORIZONTAL_SCROLL | FIELD_LEFT);
				label = new LabelField("3. ", ResourceHelper.color_black, 0);
				label.setFont(font);
				bullet_manager.add(label);
				label = new LabelField(StringHelper.enter_your_wicode_desc, ResourceHelper.color_black, 0);
				label.setFont(font);
				bullet_manager.add(label);
				manager.add(bullet_manager);

				InfoDialog.doModal("", manager, "Got It");
			}
		};
		//button_info.setMargin(0, 0, 0, ResourceHelper.convert(10));
		//button_info.setBorder(BorderFactory.createSimpleBorder(new XYEdges(1, 1, 1, 1)));//just for testing

		label_wicode = new LabelField(" -- -- ---", ResourceHelper.color_dark_grey, 0, ResourceHelper.helveticaMed().getFont(Font.PLAIN, ResourceHelper.convert(48), Ui.UNITS_px))
		{
			protected void layout(int width, int height)
			{
				width = ProfileDrawer.this.getPreferredWidth() - button_info.getPreferredWidth() - ResourceHelper.convert(10*3);
				height = getPreferredHeight();
				super.layout(width, height);
				setExtent(width, height);
			}
		};
		//label_wicode.setBorder(BorderFactory.createSimpleBorder(new XYEdges(1, 1, 1, 1)));//just for testing
		label_wicode.setMargin(0, 0, 0, ResourceHelper.convert(10));

		manager.add(label_wicode);
		manager.add(button_info);

		/*BaseButton button_inbox = new BaseButton()
		{
			private final int padding = ResourceHelper.convert(10);
			private final Bitmap image = ResourceHelper.getImage("ic_inbox_default");
			private final Bitmap image_hover = ResourceHelper.getImage("ic_inbox_hover");

			protected void paint(Graphics graphics)
			{
				if (_focus)
				{
					graphics.setColor(ResourceHelper.color_primary);
					graphics.fillRect(0, 0, getWidth(), getHeight());
				}
				else
				{
					graphics.setColor(ResourceHelper.color_white);
					graphics.fillRect(0, 0, getWidth(), getHeight());
				}

				Bitmap img = _focus ? image_hover : image;
				int y = (getHeight() - img.getHeight()) / 2;
				graphics.drawBitmap(padding, y, img.getWidth(), img.getHeight(), img, 0, 0);

				graphics.setColor(_focus ? ResourceHelper.color_white : ResourceHelper.color_black);
				graphics.drawText("Inbox", padding * 2 + img.getWidth(), (getHeight() - getFont().getHeight()) / 2);

				Font font = graphics.getFont();
				if (inboxsize > 0)
				{
					Bitmap notification_image;

					if (_focus)
					{
						graphics.setColor(ResourceHelper.color_primary);
						notification_image = ResourceHelper.getImage("ic_inbox_notification_hover");
					}
					else
					{
						graphics.setColor(ResourceHelper.color_white);
						notification_image = ResourceHelper.getImage("ic_inbox_notification_default");
					}

					graphics.drawBitmap(getWidth() - (padding * 2) - (notification_image.getWidth() / 2), (getHeight() - notification_image.getHeight()) / 2, notification_image.getWidth(),
							notification_image.getHeight(), notification_image, 0, 0);


					graphics.setFont(font.derive(Font.BOLD));

					String text = "";

					if (inboxsize > 9)
					{
						text = "9+";
					}
					else
					{
						text = String.valueOf(inboxsize);
					}
					graphics.drawText(text, getWidth() - (padding * 2) - (getFont().getAdvance(text) / 2), (getHeight() - getFont().getHeight()) / 2);
				}
				graphics.setFont(font);
			}

			public int getPreferredWidth()
			{
				return ProfileDrawer.this.getPreferredWidth();
			}

			public int getPreferredHeight()
			{
				return ResourceHelper.convert(40);
			}

			public void clickButton()
			{
				logInboxClicked();
				((ViewPagerScreen) UiApplication.getUiApplication().getActiveScreen()).transition(InboxFragment.FRAGMENT_ID, null);
			}
		};
		button_inbox.setFont(ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(15), Ui.UNITS_px));*/

		BaseButton button_mylist = new BaseButton()
		{
			private final int padding = ResourceHelper.convert(10);
			private final int paddingExtra = ResourceHelper.convert(2);
			private final Bitmap image = ResourceHelper.getImage("icon_list_default");
			private final Bitmap image_hover = ResourceHelper.getImage("icon_list_hover");

			protected void paint(Graphics graphics)
			{
				if (_focus)
				{
					graphics.setColor(ResourceHelper.color_primary);
					graphics.fillRect(0, 0, getWidth(), getHeight());
				}
				else
				{
					graphics.setColor(ResourceHelper.color_white);
					graphics.fillRect(0, 0, getWidth(), getHeight());
				}

				Bitmap img = _focus ? image_hover : image;
				int y = (getHeight() - img.getHeight()) / 2;
				graphics.drawBitmap(padding, y, img.getWidth(), img.getHeight(), img, 0, 0);

				graphics.setColor(_focus ? ResourceHelper.color_white : ResourceHelper.color_black);
				graphics.drawText("My List", padding * 2 +  + img.getWidth(), (getHeight() - getFont().getHeight()) / 2);

				graphics.setColor(_focus ? ResourceHelper.color_white : ResourceHelper.color_primary);
				Font font = graphics.getFont();
				graphics.setFont(font.derive(Font.BOLD));

				String text = "";

				if (mylistsize > 0)
				{
					text = String.valueOf(mylistsize);
					graphics.drawText(text, (getWidth() - padding*2 - paddingExtra - getFont().getAdvance(text)), (getHeight() - getFont().getHeight()) / 2);
					//graphics.drawText(text, getWidth() - (padding * 2) - (getFont().getAdvance(text) / 2), (getHeight() - getFont().getHeight()) / 2);
				}	
				graphics.setFont(font);
			}

			public int getPreferredWidth()
			{
				return ProfileDrawer.this.getPreferredWidth();
			}

			public int getPreferredHeight()
			{
				return ResourceHelper.convert(40);
			}

			public void clickButton()
			{
				logMyListClicked();
				((ViewPagerScreen) UiApplication.getUiApplication().getActiveScreen()).transition(MylistFragment.FRAGMENT_ID, null);
			}
		};
		button_mylist.setFont(ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(15), Ui.UNITS_px));

		content.add(manager);
		//content.add(button_inbox);
		content.add(button_mylist);

		if (RuntimeStoreHelper.getUserData().getWicode() == null || RuntimeStoreHelper.getUserData().getWicode().equals(""))
			label_wicode.setText(" -- -- ---");
		else
			label_wicode.setText(" " + StringUtil.formatDivisions(2, RuntimeStoreHelper.getUserData().getWicode()));

		buildDone = true;
	}

	private void logMyListClicked()
	{
		Hashtable eventParams = new Hashtable();

		eventParams.put(FlurryHelper.PARAM_TIME, FlurryHelper.getFlurryFormatDate(Calendar.getInstance()));
		eventParams.put(FlurryHelper.PARAM_LISTS, "1");//For bb it's currently only one list//TODO change when it changes for bb
		eventParams.put(FlurryHelper.PARAM_ITEMS, ""+PersistentStoreHelper.getMylist().size());

		FlurryHelper.logEvent(FlurryHelper.EVENT_MY_LIST, eventParams, true);
	}

	private void logInboxClicked()
	{
		Hashtable eventParams = new Hashtable();

		eventParams.put(FlurryHelper.PARAM_TIME, FlurryHelper.getFlurryFormatDate(Calendar.getInstance()));

		int unreadNotificationsCount = 0;

		for (int i = 0; i < PersistentStoreHelper.getInbox().size(); i ++)
		{
			if (((InboxMessage)PersistentStoreHelper.getInbox().elementAt(i)).read == false)
			{
				unreadNotificationsCount++;
			}
		}

		eventParams.put(FlurryHelper.PARAM_UNREAD_NOTIFICATIONS, ""+unreadNotificationsCount);

		FlurryHelper.logEvent(FlurryHelper.EVENT_INBOX, eventParams, true);
	}

	private void logEditProfileClicked()
	{
		Hashtable eventParams = new Hashtable();

		eventParams.put(FlurryHelper.PARAM_TIME, FlurryHelper.getFlurryFormatDate(Calendar.getInstance()));

		FlurryHelper.logEvent(FlurryHelper.EVENT_EDIT_PROFILE, eventParams, true);
	}

	private void buildOffline()
	{
		content.deleteAll();

		String path = PersistentStoreHelper.getProfileUrl("guest");
		if (path != null)
		{
			profile_image = ResourceHelper.getImageFromFile(path);
		}

		if (profile_image == null)
		{
			profile_image = ResourceHelper.getImage("profile-picture_default");
		}
		else
		{
			profile_image = BitmapTools.resizeTransparentBitmap(profile_image, button_profile.getPreferredWidth(), button_profile.getPreferredHeight(), Bitmap.FILTER_BILINEAR, Bitmap.SCALE_STRETCH);
		}

		name_button.set_visible(false);

		TextImageButton button_signup = new TextImageButton("Sign Up", "btn_default", "btn_hover")
		{
			public void clickButton()
			{
				UiApplication.getUiApplication().pushScreen(new RegistrationScreen(0, new UserData()));
			}
		};
		button_signup.setTextColor(ResourceHelper.color_white);
		button_signup.setTextColorHover(ResourceHelper.color_primary);
		button_signup.setTextColorPressed(ResourceHelper.color_primary);

		TextImageButton button_login = new TextImageButton("Login", "btn_grey_default", "btn_grey_hover")
		{
			public void clickButton()
			{
				UiApplication.getUiApplication().pushScreen(new LoginScreen());
				/*UserData user = new UserData();
				user.setCellphone("0739336932");
				new AccountValidationScreen(user);
				UiApplication.getUiApplication().pushScreen(new AccountValidationScreen(user));*///Anje added this for testing n bug in authenticate screen
			}
		};
		button_login.setTextColor(ResourceHelper.color_primary);
		button_login.setTextColorHover(ResourceHelper.color_primary);
		button_login.setTextColorPressed(ResourceHelper.color_primary);

		button_signup.setMargin(0, 0, 0, (getPreferredWidth() - button_signup.getPreferredWidth()) / 2);
		button_login.setMargin(ResourceHelper.convert(5), 0, 0, (getPreferredWidth() - button_login.getPreferredWidth()) / 2);

		content.add(button_signup);
		content.add(button_login);

		buildDone = true;
	}

	private String getFromGallery(String path)
	{
		FilePicker filePicker = FilePicker.getInstance();
		filePicker.setFilter(EXTENTIONS);

		if (path != null)
		{
			filePicker.setPath(path);
		}
		else if (ApplicationInterface.isSDCardIn())
		{
			filePicker.setPath("file:///SDCard/BlackBerry/pictures/");
		}

		path = filePicker.show();

		return path;
	}

	public void onCouponsFinishedLoading(boolean success) {
		mylistsize = PersistentStoreHelper.mylistSize();
		inboxsize = PersistentStoreHelper.inboxUnread();

		build();
	}
}
