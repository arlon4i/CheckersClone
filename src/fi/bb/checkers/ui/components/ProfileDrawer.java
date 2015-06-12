package fi.bb.checkers.ui.components;

import java.util.Calendar;
import java.util.Hashtable;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.container.AbsoluteFieldManager;
import net.rim.device.api.ui.container.FullScreen;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.BackgroundFactory;
import net.rim.device.api.ui.decor.Border;
import net.rim.device.api.ui.decor.BorderFactory;
import net.rim.device.api.ui.picker.FilePicker;
import fi.bb.checkers.MainApplication;
import fi.bb.checkers.datatypes.InboxMessage;
import fi.bb.checkers.datatypes.UserData;
import fi.bb.checkers.helpers.FlurryHelper;
import fi.bb.checkers.helpers.PersistentStoreHelper;
import fi.bb.checkers.helpers.ResourceHelper;
import fi.bb.checkers.helpers.RuntimeStoreHelper;
import fi.bb.checkers.helpers.StringHelper;
import fi.bb.checkers.helpers.ZebraCrossingHelper;
import fi.bb.checkers.interfaces.InterfaceCouponsFinishedLoading;
import fi.bb.checkers.logger.RemoteLogger;
import fi.bb.checkers.prompts.InfoDialog;
import fi.bb.checkers.prompts.PhotoPrompt;
import fi.bb.checkers.ui.fragments.MylistFragment;
import fi.bb.checkers.ui.screens.CameraScreen;
import fi.bb.checkers.ui.screens.EditProfileScreen;
import fi.bb.checkers.ui.screens.LoginScreen;
import fi.bb.checkers.ui.screens.MagnifiedQRCodeScreen;
import fi.bb.checkers.ui.screens.QRSelectedStoresScreen;
import fi.bb.checkers.ui.screens.RegistrationScreen;
import fi.bb.checkers.ui.screens.ViewPagerScreen;
import fi.bb.checkers.utils.ApplicationInterface;
import fi.bb.checkers.utils.BitmapTools;
import fi.bb.checkers.utils.StringUtil;

public class ProfileDrawer extends VerticalFieldManager implements InterfaceCouponsFinishedLoading, FieldChangeListener
{
	private static final String EXTENTIONS = ".jpg:.jpeg:.png:.bmp";

	private Bitmap shadow;
	private Bitmap qrcode;
	private BaseButton name_button;
	private LabelField name_label;
	private Bitmap profile_image;
	private TextImageButton button_profile;
	private VerticalFieldManager content;
	private HorizontalFieldManager qrcode_heading_manager;
	private int mylistsize = 0;
	private int inboxsize = 0;
	private LabelField label_wicode;
	private LabelField qrcode_heading;
	private LabelField wicode_heading;
	private BitmapField qrcode_image;
	private ImageButton button_lens;
	private HyperlinkButton qrcode_message;
//	private LabelField qrcode_message;

	private HorizontalFieldManager manager;//profile button basically
	AbsoluteFieldManager afm;

	boolean buildDone;
	boolean isVisible;

	public ProfileDrawer()
	{
		super(NO_VERTICAL_SCROLL | NO_VERTICAL_SCROLLBAR | NO_HORIZONTAL_SCROLL | USE_ALL_HEIGHT);
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
		button_profile.setTextColor(ResourceHelper.color_primary);
		button_profile.setTextColorHover(ResourceHelper.color_primary);
		button_profile.setTextColorPressed(ResourceHelper.color_primary);
		button_profile.setMargin(0, ResourceHelper.convert(24), 0, ResourceHelper.convert(14));
//		button_profile.setBorder(BorderFactory.createSimpleBorder(new XYEdges(1, 1, 1, 1)));

		name_button = new BaseButton()
		{
			private String firstname = (userdata == null) ? "----" : userdata.getFirstname();
			private String surname = (userdata == null) ? "----" : userdata.getSurname();
			private final String name = ((firstname == null) ? "----" : firstname) + " " + ((surname == null) ? "----" : surname);

			public int getPreferredHeight()
			{
				return button_profile.getPreferredHeight();
			}

			public int getPreferredWidth()
			{
				return ProfileDrawer.this.getPreferredWidth() - button_profile.getPreferredWidth() - button_profile.getMarginLeft() - button_profile.getMarginRight() - ResourceHelper.convert(5);
			}

			protected void layout(int width, int height)
			{
				width = getPreferredWidth() - ResourceHelper.convert(2);
				height = getPreferredHeight() - ResourceHelper.convert(2);
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
//		name_button.setBorder(BorderFactory.createSimpleBorder(new XYEdges(1, 1, 1, 1)));

		name_label = new LabelField("Guest", ResourceHelper.color_black, 0);
		name_label.setMargin((((button_profile.getPreferredHeight() - name_label.getPreferredHeight()) / 2) + button_profile.getMarginTop()), 0, 0, ResourceHelper.convert(5));
		name_label.setFont(ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(20), Ui.UNITS_px));		
		
		manager = new HorizontalFieldManager();
		manager.add(button_profile);
		add(manager);
		
		qrcode_heading_manager = new HorizontalFieldManager();
		add(qrcode_heading_manager);
		
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
						String wicode = "";
						
						if (RuntimeStoreHelper.getUserData().getWicode() == null || RuntimeStoreHelper.getUserData().getWicode().equals(""))
						{
							wicode = " -- -- ---";
						}
						else
						{
							wicode = " " + StringUtil.formatDivisions(2, RuntimeStoreHelper.getUserData().getWicode());
						}
						
						label_wicode.setText(wicode);
						
						
//						RemoteLogger.log("QRCode Debugging", (ZebraCrossingHelper.generateQRCode(wicode, 100, 100) == null) ? "Null" : "Something Returned");
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

		HorizontalFieldManager wicode_manager = new HorizontalFieldManager(NO_VERTICAL_SCROLL | NO_HORIZONTAL_SCROLL)
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
//		wicode_manager.setBorder(BorderFactory.createSimpleBorder(new XYEdges(1, 1, 1, 1)));
		
		Font label_wicode_font = ResourceHelper.helveticaMed().getFont(Font.PLAIN, ResourceHelper.convert(26), Ui.UNITS_px);
		int button_info_size = label_wicode_font.getHeight();

		final ImageButton button_info = new ImageButton("icon_terms_default", "icon_terms_default", button_info_size, button_info_size, FIELD_VCENTER)
		{
			public void clickButton()
			{
				ScrollableInfoDialog.doModal();
			}
		};
		button_info.setMargin(ResourceHelper.convert(2), ResourceHelper.convert(10), ResourceHelper.convert(2), 0);
//		button_info.setBorder(BorderFactory.createSimpleBorder(new XYEdges(1, 1, 1, 1)));

		label_wicode = new LabelField(" -- -- ---", ResourceHelper.color_dark_grey, 0, label_wicode_font)
		{
			protected void layout(int width, int height)
			{
				width = ProfileDrawer.this.getPreferredWidth() - button_info.getPreferredWidth() - ResourceHelper.convert(10 * 3) - ResourceHelper.convert(92);
				height = getPreferredHeight();
				super.layout(width, height);
				setExtent(width, height);
			}
		};
//		label_wicode.setBorder(BorderFactory.createSimpleBorder(new XYEdges(1, 1, 1, 1)));
		
		int label_wicode_margin = ResourceHelper.convert(2); //button_info.getHeight() - label_wicode.getFont().getHeight();
//		label_wicode_margin /= 2;
//		label_wicode_margin = (label_wicode_margin < 0) ? 0 : label_wicode_margin;
		label_wicode.setMargin(label_wicode_margin, label_wicode.getMarginRight(), label_wicode_margin, label_wicode.getMarginLeft());
		
		wicode_manager.add(label_wicode);
		wicode_manager.add(button_info);
		
		VerticalFieldManager vertical_container = new VerticalFieldManager();
//		vertical_container.setBorder(BorderFactory.createSimpleBorder(new XYEdges(1, 1, 1, 1)));
		vertical_container.add(wicode_manager);
		
		qrcode_message = new HyperlinkButton("QR Code only at selected stores", ResourceHelper.convert(12), true);
		qrcode_message.setChangeListener(this);
		vertical_container.add(qrcode_message);
		
		LabelField text_label_field = new LabelField("Click QR Code to enlarge", ResourceHelper.color_black, 0, ResourceHelper.helveticaMed().getFont(Font.PLAIN, ResourceHelper.convert(12), Ui.UNITS_px));
//		text_label_field.setMargin(0, 0, 0, ResourceHelper.convert(2));
		vertical_container.add(text_label_field);

		BaseButton button_mylist = new BaseButton()
		{
			private final int padding = ResourceHelper.convert(2);
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
				int y = (int) Math.floor((double)((getHeight() - img.getHeight()) / 2));
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
				}	
				graphics.setFont(font);
			}

			public int getPreferredWidth()
			{
				return ProfileDrawer.this.getPreferredWidth();
			}

			public int getPreferredHeight()
			{
				return ResourceHelper.convert(26);
			}

			public void clickButton()
			{
				logMyListClicked();
				((ViewPagerScreen) UiApplication.getUiApplication().getActiveScreen()).transition(MylistFragment.FRAGMENT_ID, null);
			}
		};
		button_mylist.setFont(ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(15), Ui.UNITS_px));

		if (RuntimeStoreHelper.getUserData().getWicode() == null || RuntimeStoreHelper.getUserData().getWicode().equals(""))
			label_wicode.setText(" -- -- ---");
		else
			label_wicode.setText(" " + StringUtil.formatDivisions(2, RuntimeStoreHelper.getUserData().getWicode()));
		
		
		qrcode = ZebraCrossingHelper.generateQRCode(RuntimeStoreHelper.getUserData().getWicode(), ResourceHelper.convert(92), ResourceHelper.convert(92));
		Bitmap cropped_image = cropQRCode(qrcode);
		
		qrcode_image = new BitmapField(cropped_image, FOCUSABLE)
		{
			public void layout(int width, int height)
			{
				height = width = getPreferredWidth();
				super.layout(width, height);
				setExtent(width, height);
			}
			
			public int getPreferredHeight()
			{
				return getBitmapHeight(); //button_profile.getWidth() + ResourceHelper.convert(24);
			}
			
			public int getPreferredWidth()
			{
				return getPreferredHeight();
			}
		};	

		final XYEdges afm_border_sizes = new XYEdges(ResourceHelper.convert(2), ResourceHelper.convert(2), ResourceHelper.convert(2), ResourceHelper.convert(2));
		final XYEdges afm_border_colors = new XYEdges(ResourceHelper.color_primary, ResourceHelper.color_primary, ResourceHelper.color_primary, ResourceHelper.color_primary);
		
		afm = new AbsoluteFieldManager()
		{
			public void sublayout(int width, int height)
			{
				height = width = qrcode_image.getPreferredWidth();
				super.sublayout(width, height);
				setExtent(width, height);
			}
			
			public void drawFocus(Graphics graphics, boolean on)
			{
				
			}
			
			public void onFocus(int direction)
			{
				setBorder(BorderFactory.createSimpleBorder(afm_border_sizes, afm_border_colors, new XYEdges(4, 4, 4, 4)));
			}
			
			public void onUnfocus()
			{
				setBorder(BorderFactory.createSimpleBorder(afm_border_sizes, afm_border_colors, new XYEdges(2, 2, 2, 2)));
			}
			
			protected boolean navigationClick(int status, int time)
			{
				MagnifiedQRCodeScreen.push(qrcode);
				return true;
			}
		};
		afm.setBorder(BorderFactory.createSimpleBorder(afm_border_sizes, afm_border_colors, new XYEdges(2, 2, 2, 2)));
		afm.setMargin(ResourceHelper.convert(8), ResourceHelper.convert(12), ResourceHelper.convert(6), ResourceHelper.convert(12));		
		
		afm.add(qrcode_image, 0, 0);
		
		button_lens = new ImageButton("icon_enlarge_default", "icon_enlarge_selected", -1, 0);	
		int position = qrcode_image.getPreferredWidth() - button_lens.getPreferredWidth();
		
		afm.add(button_lens, position, position);
		
		int qrcode_message_margin = qrcode_image.getBitmapHeight() - (label_wicode.getFont().getHeight() + qrcode_message.getFont().getHeight() + (wicode_manager.getMarginTop() * 2));
		qrcode_message_margin =  ResourceHelper.convert(6); //0;
		qrcode_message.setMargin(ResourceHelper.convert(8), 0, qrcode_message_margin, label_wicode.getMarginLeft());
		
		addHeadings();
		
		HorizontalFieldManager hfm = new HorizontalFieldManager(HorizontalFieldManager.USE_ALL_WIDTH);
		hfm.setMargin(0, 0, ResourceHelper.convert(11), 0);
		
		hfm.add(afm);		
		hfm.add(vertical_container);
		
//		int margin = afm.getPreferredHeight() - (button_info.getPreferredHeight() + ResourceHelper.convert(14));
		wicode_manager.setMargin(ResourceHelper.convert(6), 0, ResourceHelper.convert(2), 0);
		
		content.add(hfm);
		content.add(button_mylist);
		
//		RemoteLogger.log("wiCode", ("" + RuntimeStoreHelper.getUserData().getWicode()));
		
		buildDone = true;
	}

	private void logMyListClicked()
	{
		Hashtable eventParams = new Hashtable();

		eventParams.put(FlurryHelper.PARAM_TIMESTAMP, FlurryHelper.getFlurryFormatDate(Calendar.getInstance()));
		eventParams.put(FlurryHelper.PARAM_LISTS, "1");//For bb it's currently only one list//TODO change when it changes for bb
		eventParams.put(FlurryHelper.PARAM_ITEMS, "" + PersistentStoreHelper.getMylist().size());

		FlurryHelper.logEvent(FlurryHelper.EVENT_MY_LIST, eventParams, true);
	}

	private void logInboxClicked()
	{
		Hashtable eventParams = new Hashtable();

		eventParams.put(FlurryHelper.PARAM_TIMESTAMP, FlurryHelper.getFlurryFormatDate(Calendar.getInstance()));

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

		eventParams.put(FlurryHelper.PARAM_TIMESTAMP, FlurryHelper.getFlurryFormatDate(Calendar.getInstance()));

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
	
	private void addHeadings()
	{		
		Font font = ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(18), Ui.UNITS_px);
		
		qrcode_heading = new LabelField("QR Code:", ResourceHelper.color_black, 0, font) {
			public void layout(int width, int height)
			{
				width = getFont().getAdvance(getText()) + ResourceHelper.convert(2); //qrcode_image.getBitmapWidth();
				height = getPreferredHeight();
				super.layout(width, height);
				setExtent(width, height);
			}
		};
		qrcode_heading.setMargin(0, ResourceHelper.convert(6), 0, ResourceHelper.convert(12));
//		qrcode_heading.setBorder(BorderFactory.createSimpleBorder(new XYEdges(1, 1, 1, 1)));
		
		wicode_heading = new LabelField("WiCode:", ResourceHelper.color_black, Field.FIELD_LEFT, font);		
//		int margin = label_wicode.getFont().getAdvance(label_wicode.getText()) - wicode_heading.getFont().getAdvance(wicode_heading.getText());
//		
//		RemoteLogger.log(label_wicode.getText(), "" + label_wicode.getFont().getAdvance(label_wicode.getText()));
//		RemoteLogger.log(wicode_heading.getText(), "" + wicode_heading.getFont().getAdvance(wicode_heading.getText()));
		
		int margin = label_wicode.getPreferredWidth() - wicode_heading.getPreferredWidth();
		margin = (margin / 2) + (margin % 2);
//		margin = ResourceHelper.convert(margin);
		
		RemoteLogger.log("Margin: ", "" + margin);
		wicode_heading.setMargin(0, 0, 0, margin); //(name_button.getMarginLeft() + ResourceHelper.convert(4))
//		wicode_heading.setBorder(BorderFactory.createSimpleBorder(new XYEdges(1, 1, 1, 1)));
		
		qrcode_heading_manager.add(qrcode_heading);
		qrcode_heading_manager.add(wicode_heading);
	}
	
	private Bitmap cropQRCode(Bitmap bitmap)
	{
		int size = (bitmap.getWidth() - ResourceHelper.convert(28));
        Bitmap btmp = new Bitmap(size, size);
	    Graphics g = new Graphics(btmp);
	    g.drawBitmap(0, 0, size, size, bitmap, ResourceHelper.convert(14), ResourceHelper.convert(14));
	    
	    return btmp;
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

	public void onCouponsFinishedLoading(boolean success)
	{
		mylistsize = PersistentStoreHelper.mylistSize();
		inboxsize = PersistentStoreHelper.inboxUnread();

		build();
	}

	public void fieldChanged(Field field, int arg1)
	{
		if(field == qrcode_message)
		{
			QRSelectedStoresScreen.push();
		}
	}
	
	class CustomLabelField extends LabelField
	{
		public CustomLabelField(String text, int color, int style) 
		{
			super(text, color, style|FOCUSABLE);
		}
		
		public void drawFocus(Graphics graphics, boolean on)
		{
			
		}
	}
	
	class CustomHorizontalFieldManager extends HorizontalFieldManager
	{
		public CustomHorizontalFieldManager()
		{
			super(NO_VERTICAL_SCROLL | NO_HORIZONTAL_SCROLL | FIELD_LEFT | FOCUSABLE);
		}
		
		public void drawFocus(Graphics graphics, boolean on)
		{
			
		}
	}
	
	static class ScrollableInfoDialog extends PopupScreen implements FieldChangeListener {
		
		private ScrollableInfoDialog()
		{
			super(new VerticalFieldManager(FIELD_HCENTER | FIELD_VCENTER | VERTICAL_SCROLL | FOCUSABLE));
			setBackground(BackgroundFactory.createSolidBackground(ResourceHelper.color_white));

			addDetails();

			TextImageButton okButton = new TextImageButton("Okay", "btn_sml_default", "btn_sml_hover", FIELD_HCENTER);
			okButton.setTextColor(ResourceHelper.color_white);
			okButton.setTextColorHover(ResourceHelper.color_primary);
			okButton.setTextColorPressed(ResourceHelper.color_primary);
			okButton.setChangeListener(this);
			okButton.setMargin(ResourceHelper.convert(10), 0, 0, 0);

			add(okButton);

			Bitmap borderBitmap = Bitmap.getBitmapResource("rounded-border.png");
			setBorder(BorderFactory.createBitmapBorder(new XYEdges(12, 12, 12, 12), borderBitmap));
		}
		
		private void addDetails() {
			Font font = ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(17), Ui.UNITS_px);
			
			LabelField label = new LabelField("Use this WiCode to redeem your EeziCoupons in three simple steps:", ResourceHelper.color_black, 0);
			label.setMargin(ResourceHelper.convert(5), 0, 0, 0);
			label.setFont(font);
			add(label);

			HorizontalFieldManager bullet_manager = new HorizontalFieldManager();
			bullet_manager.setMargin(ResourceHelper.convert(10), 0, 0, 0);

			label = new LabelField("1. ", ResourceHelper.color_black, 0);
			label.setFont(font);
			bullet_manager.add(label);
			label = new LabelField(StringHelper.visit_nearest_store, ResourceHelper.color_black, 0);
			label.setFont(font);
			bullet_manager.add(label);
			add(bullet_manager);

			bullet_manager = new HorizontalFieldManager();
			label = new LabelField("2. ", ResourceHelper.color_black, 0);
			label.setFont(font);
			bullet_manager.add(label);
			label = new LabelField(StringHelper.buy_items_desc, ResourceHelper.color_black, 0);
			label.setFont(font);
			bullet_manager.add(label);
			add(bullet_manager);

			bullet_manager = new HorizontalFieldManager();
			label = new LabelField("3. ", ResourceHelper.color_black, 0);
			label.setFont(font);
			bullet_manager.add(label);
			label = new LabelField(StringHelper.enter_your_wicode_desc, ResourceHelper.color_black, 0);
			label.setFont(font);
			bullet_manager.add(label);
			add(bullet_manager);
			
			/* label = new LabelField("QR Codes are only currently available at these selected stores:", ResourceHelper.color_black, 0);
			label.setMargin(ResourceHelper.convert(10), 0, 0, 0);
			label.setFont(font);
			add(label);
			
			bullet_manager = new HorizontalFieldManager();
			bullet_manager.setMargin(ResourceHelper.convert(10), 0, 0, 0);
			label = new LabelField("1. ", ResourceHelper.color_black, 0);
			label.setFont(font);
			bullet_manager.add(label);
			label = new LabelField("Checkers Hyper Fairbridge Mall, Brackenfell", ResourceHelper.color_black, 0);
			label.setFont(font);
			bullet_manager.add(label);
			add(bullet_manager);

			bullet_manager = new HorizontalFieldManager();
			label = new LabelField("2. ", ResourceHelper.color_black, 0);
			label.setFont(font);
			bullet_manager.add(label);
			label = new LabelField("Checkers Hyper Helderberg Mall, Somerset West", ResourceHelper.color_black, 0);
			label.setFont(font);
			bullet_manager.add(label);
			add(bullet_manager); */
		}
		
		public static void doModal()
		{
			if (Application.isEventDispatchThread())
			{
				push();
			}
			else
			{
				UiApplication.getUiApplication().invokeLater(new Runnable()
				{
					public void run()
					{
						push();
					}
				});
			}
		}
	
		private static void push()
		{
			FullScreen trans = new FullScreen();
			trans.setBackground(BackgroundFactory.createSolidTransparentBackground(Color.BLACK, 200));
			((MainApplication) UiApplication.getUiApplication()).fadeScreen(trans, false);
	
			ScrollableInfoDialog dialog = new ScrollableInfoDialog();
			UiApplication.getUiApplication().pushModalScreen(dialog);
			trans.close();
		}

		public void fieldChanged(Field field, int context)
		{
			this.close();
		}

		protected boolean keyDown(int keycode, int status)
		{
			if (Keypad.key(keycode) == Keypad.KEY_ESCAPE)
			{
				this.close();
			}
			return false;
		}
	}
}