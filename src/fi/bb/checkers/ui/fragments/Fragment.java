package fi.bb.checkers.ui.fragments;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.ScrollChangeListener;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.VerticalFieldManager;
import fi.bb.checkers.ui.components.Actionbar;
import fi.bb.checkers.ui.screens.ViewPagerScreen;

public abstract class Fragment extends VerticalFieldManager implements ScrollChangeListener
{
	private static int next_id = 0;
	protected VerticalFieldManager content;
	//protected Bitmap shadow = ResourceHelper.getImage("top_gradient");

	public Fragment(long style)
	{
		super(NO_VERTICAL_SCROLL | NO_HORIZONTAL_SCROLL);
		content = new VerticalFieldManager(style)
		{
			public int getPreferredHeight()
			{
				return Fragment.this.getPreferredHeight();
			}

			public int getPreferredWidth()
			{
				return Fragment.this.getPreferredWidth();
			}
		};
		content.setScrollListener(this);
		super.add(content);
	}

	// Unique id to identify the fragment. Use a method instead of hard-coding so its easier to add/remove
	protected static final int getUUID()
	{
		return next_id++;
	}

	protected void paint(Graphics graphics)
	{
		/*if (shadow.getWidth() != getWidth())
		{
			shadow = BitmapTools.resizeTransparentBitmap(shadow, getWidth(), ResourceHelper.convert(5), Bitmap.FILTER_BILINEAR, Bitmap.SCALE_STRETCH);
		}
		super.paint(graphics);

		graphics.drawBitmap(0, getVerticalScroll(), shadow.getWidth(), shadow.getHeight(), shadow, 0, 0);*/
		super.paint(graphics);
	}

	public int getPreferredHeight()
	{
		return Display.getHeight() - Actionbar.height;
	}

	public int getPreferredWidth()
	{
		return Display.getWidth();
	}

	protected void sublayout(int maxWidth, int maxHeight)
	{
		maxWidth = getPreferredWidth();
		maxHeight = getPreferredHeight();
		super.sublayout(maxWidth, maxHeight);
		setExtent(maxWidth, maxHeight);
	}

	public void setPadding(int arg0, int arg1, int arg2, int arg3)
	{
		content.setPadding(arg0, arg1, arg2, arg3);
	}

	public void add(Field field)
	{
		content.add(field);
	}

	public void insert(Field field, int index)
	{
		content.insert(field, index);
	}

	public void delete(Field field)
	{
		content.delete(field);
	}

	public void deleteAll()
	{
		content.deleteAll();
	}

	public void scrollChanged(Manager manager, int newHorizontalScroll, int newVerticalScroll)
	{
		// prevent the shadow from being dragged when scrolling
		manager.invalidate();
	}

	public void makeMenu(Menu menu)
	{

	}

	public void onClose()
	{

	}

	public final void close()
	{
		onClose();
		((ViewPagerScreen) UiApplication.getUiApplication().getActiveScreen()).popStack();
	}
}
