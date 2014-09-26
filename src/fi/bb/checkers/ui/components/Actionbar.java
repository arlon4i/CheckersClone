package fi.bb.checkers.ui.components;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.decor.BackgroundFactory;
import fi.bb.checkers.helpers.ResourceHelper;
import fi.bb.checkers.utils.StringUtil;

public class Actionbar extends HorizontalFieldManager implements FieldChangeListener
{
	public static final int height = ResourceHelper.convert(31);
	private Field title;
	private OnClickListener listener;
	private boolean allow_scroll = true;

	private final int notification_offset = ResourceHelper.convert(5);
	
	private int backgroundColor;

	private final ImageButton drawer = new ImageButton("icon_menu_default", "icon_menu_hover", -1, FIELD_VCENTER)
	{
		public int getPreferredWidth()
		{
			// so that the field in the center is not offset to the left
			return super.getPreferredWidth() + notification_offset;
		};

		protected boolean navigationMovement(int dx, int dy, int status, int time)
		{
			if (!allow_scroll && (dx > 0 || dy != 0)) return true;
			return super.navigationMovement(dx, dy, status, time);
		};
	};

	private final ImageButton profile = new ImageButton("icon_profile_default", "icon_profile_hover", -1, FIELD_VCENTER)
	{
		protected boolean navigationMovement(int dx, int dy, int status, int time)
		{
			if (!allow_scroll && (dx < 0 || dy != 0)) return true;
			return super.navigationMovement(dx, dy, status, time);
		};

		public int getPreferredWidth()
		{
			// so that the red icon isn't directly over the face
			return super.getPreferredWidth() + notification_offset;
		};

		protected void paint(Graphics g)
		{
			if (_visible)
			{
				Bitmap image;
				if (isFocus())
				{
					image = bitmap_focus;
				}
				else
				{
					image = bitmap_normal;
				}

				if (image != null)
				{
					g.drawBitmap(getWidth() - image.getWidth(), 0, width, height, image, 0, 0);
				}

				/*int unread_messages = PersistentStoreHelper.inboxUnread();

				if (unread_messages > 0)
				{
					Bitmap inbox = ResourceHelper.getImage("icon_notification");
					g.drawBitmap(0, 0, inbox.getWidth(), inbox.getHeight(), inbox, 0, 0);

					g.setFont(ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(10), Ui.UNITS_px));
					g.setColor(ResourceHelper.color_white);
					String text = unread_messages > 9 ? "9+" : String.valueOf(unread_messages);
					int x = (inbox.getWidth() - g.getFont().getAdvance(text)) / 2;
					int y = (inbox.getHeight() - g.getFont().getHeight()) / 2;

					g.setDrawingStyle(DrawStyle.VCENTER, true);
					g.drawText(text, x, y);
				}*/
			}
		};
	};

	public Actionbar(String title, boolean show_drawer, boolean show_profile)
	{
		this(title, show_drawer, show_profile, false);
	}
	
	public Actionbar(String title, boolean show_drawer, boolean show_profile, boolean greyTheme)
	{
		int titleTextColor;
		if (greyTheme == true)
		{
			backgroundColor = ResourceHelper.color_light_grey;
			titleTextColor = ResourceHelper.color_black;
		}
		else
		{
			backgroundColor = ResourceHelper.color_primary;
			titleTextColor = ResourceHelper.color_white;
		}
		
		Font font = ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(20), Ui.UNITS_px);
		int width = getPreferredWidth() - drawer.getPreferredWidth() - profile.getPreferredWidth();
		String[] lines = StringUtil.ellipsize(font, title, width, 1);
		if (lines.length != 0)
			title = lines[0];
		else
			title = "";
		this.title = new LabelField(title, titleTextColor, 0);
		this.title.setFont(font);

		drawer.set_visible(show_drawer);
		profile.set_visible(show_profile);

		build();
	}

	public Actionbar(Field title, boolean show_drawer, boolean show_profile)
	{
		this.title = title;
		drawer.set_visible(show_drawer);
		profile.set_visible(show_profile);

		backgroundColor = ResourceHelper.color_primary;
		
		build();
	}

	private void build()
	{
		setBackground(BackgroundFactory.createSolidBackground(backgroundColor));

		drawer.setMargin((getPreferredHeight() - drawer.getPreferredHeight()) / 2, 0, 0, 0);
		profile.setMargin((getPreferredHeight() - profile.getPreferredHeight()) / 2, 0, 0, 0);

		int width = getPreferredWidth() - this.title.getPreferredWidth() - drawer.getPreferredWidth() - profile.getPreferredWidth();
		this.title.setMargin((getPreferredHeight() - this.title.getPreferredHeight()) / 2, width / 2, 0, width / 2);

		add(drawer);
		add(this.title);
		add(profile);

		drawer.setChangeListener(this);
		profile.setChangeListener(this);
	}

	public int getPreferredHeight()
	{
		return height;
	}

	public int getPreferredWidth()
	{
		return Display.getWidth();
	}

	protected void sublayout(int maxWidth, int maxHeight)
	{
		int width = getPreferredWidth();
		int height = getPreferredHeight();
		super.sublayout(width, height);
		setExtent(width, height);
	}

	public void setTitle(Object title)
	{
		if (title instanceof String)
		{
			String str = (String) title;
			Font font = ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(20), Ui.UNITS_px);
			int width = getPreferredWidth() - drawer.getPreferredWidth() - profile.getPreferredWidth();
			final String[] lines = StringUtil.ellipsize(font, str, width, 1);
			if (lines.length != 0)
				str = lines[0];
			else
				str = "";
			this.title = new LabelField(str, ResourceHelper.color_white, 0);
			this.title.setFont(font);

			synchronized (Application.getEventLock())
			{
				width = getPreferredWidth() - this.title.getPreferredWidth() - drawer.getPreferredWidth() - profile.getPreferredWidth();
				this.title.setMargin((getPreferredHeight() - this.title.getPreferredHeight()) / 2, width / 2, 0, width / 2);

				replace(getField(1), this.title);
			}
		}
		else if (title instanceof Field)
		{
			this.title = (Field) title;
			synchronized (Application.getEventLock())
			{
				int width = getPreferredWidth() - this.title.getPreferredWidth() - drawer.getPreferredWidth() - profile.getPreferredWidth();
				this.title.setMargin((getPreferredHeight() - this.title.getPreferredHeight()) / 2, width / 2, 0, width / 2);

				replace(getField(1), this.title);
			}
		}
	}

	public void setNavigationVisibility(boolean visibility)
	{
		drawer.set_visible(visibility);
	}

	public void setProfileVisibility(boolean visibility)
	{
		profile.set_visible(visibility);
	}

	public boolean isNavigationVisible()
	{
		return drawer.is_visible();
	}

	public boolean isProfileVisible()
	{
		return profile.is_visible();
	}

	public void setListener(OnClickListener listener)
	{
		this.listener = listener;
	}

	public static interface OnClickListener
	{
		public void drawerClick();
		public void profileClick();
	}

	public void fieldChanged(Field arg0, int arg1)
	{
		if (listener == null) return;

		if (arg0 == drawer)
			listener.drawerClick();
		else if (arg0 == profile) listener.profileClick();
	}

	public void allowScroll(boolean allow_scroll)
	{
		this.allow_scroll = allow_scroll;
	}

	public boolean isScrollAllowed()
	{
		return allow_scroll;
	}

	public void selectDrawer()
	{
		drawer.setFocus();
	}

	public void selectProfile()
	{
		profile.setFocus();
	}
}
