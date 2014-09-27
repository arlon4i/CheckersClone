package fi.bb.checkers.ui.screens;

import com.samples.bbm.ForName.BBMInterface.BBMBridge;

import net.rim.device.api.system.Characters;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.decor.BackgroundFactory;
import fi.bb.checkers.MainApplication;
import fi.bb.checkers.helpers.PersistentStoreHelper;
import fi.bb.checkers.helpers.ResourceHelper;
import fi.bb.checkers.logger.RemoteLogger;
import fi.bb.checkers.ui.components.ColorButtonField;

public class WelcomeScreen extends MainScreen implements FieldChangeListener
{
	private int current_page;
	private HorizontalFieldManager progress_manager;
	private HorizontalFieldManager button_manager;

	/*private TextImageButton button_next;
	private TextImageButton button_skip;*/
	private ColorButtonField button_next;
	private ColorButtonField button_skip;

	public WelcomeScreen()
	{
		super(NO_VERTICAL_SCROLL | NO_HORIZONTAL_SCROLL);

		/*button_next = new TextImageButton("Next", "button_default", "button_hover",ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(15), Ui.UNITS_px));
		button_skip = new TextImageButton("Start Saving!", "button_default", "button_hover",ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(15), Ui.UNITS_px));*/

		button_next = new ColorButtonField(ResourceHelper.color_primary, ResourceHelper.color_white, ResourceHelper.convert(100),  ResourceHelper.convert(28));
		button_next.setButtonOutlineStates(ResourceHelper.color_primary, ResourceHelper.color_primary, ResourceHelper.color_primary);
		button_next.setButtonText("Next");
		button_next.setTextStates(ResourceHelper.color_white, ResourceHelper.color_primary, ResourceHelper.color_primary);

		button_skip = new ColorButtonField(ResourceHelper.color_primary, ResourceHelper.color_white, ResourceHelper.convert(100),  ResourceHelper.convert(28));
		button_skip.setButtonOutlineStates(ResourceHelper.color_primary, ResourceHelper.color_primary, ResourceHelper.color_primary);
		button_skip.setButtonText("Start Saving!");
		button_skip.setTextStates(ResourceHelper.color_white, ResourceHelper.color_primary, ResourceHelper.color_primary);

		button_next.setMargin(0, ResourceHelper.convert(10), 0, 0);
		button_manager = new HorizontalFieldManager(NO_VERTICAL_SCROLL | NO_HORIZONTAL_SCROLL | FIELD_HCENTER);

		progress_manager = new HorizontalFieldManager(NO_VERTICAL_SCROLL | NO_HORIZONTAL_SCROLL | FIELD_HCENTER);
		int bullet_margin = ResourceHelper.convert(5);
		Bullet bullet = new Bullet(ResourceHelper.convert(6), ResourceHelper.color_white, ResourceHelper.color_primary);
		bullet.setMargin(bullet_margin * 2, bullet_margin, bullet_margin * 2, bullet_margin);
		progress_manager.add(bullet);

		bullet = new Bullet(ResourceHelper.convert(6), ResourceHelper.color_white, ResourceHelper.color_primary);
		bullet.setMargin(bullet_margin * 2, bullet_margin, bullet_margin * 2, bullet_margin);
		progress_manager.add(bullet);

		bullet = new Bullet(ResourceHelper.convert(6), ResourceHelper.color_white, ResourceHelper.color_primary);
		bullet.setMargin(bullet_margin * 2, bullet_margin, bullet_margin * 2, bullet_margin);
		progress_manager.add(bullet);

		bullet = new Bullet(ResourceHelper.convert(6), ResourceHelper.color_white, ResourceHelper.color_primary);
		bullet.setMargin(bullet_margin * 2, bullet_margin, bullet_margin * 2, bullet_margin);
		progress_manager.add(bullet);

		add(button_manager);
		add(progress_manager);

		buildPage(1);

		button_manager.setMargin(Display.getHeight() - progress_manager.getPreferredHeight() - button_manager.getPreferredHeight() - ResourceHelper.convert(10), 0, 0, 0);

		button_next.setChangeListener(this);
		button_skip.setChangeListener(this);
	}

	protected boolean keyChar(char c, int status, int time)
	{
		if (current_page != 1 && c == Characters.ESCAPE)
		{
			buildPage(current_page - 1);
			return true;
		}
		else if (current_page == 1 && c == Characters.ESCAPE)
		{
			/*try
			{
				BBMBridge.stopBBM();
				RemoteLogger.log("STOP_BBM", "welcomescreen YAY");
			}
			catch (Exception e)
			{
				RemoteLogger.log("STOP_BBM", "welcomescreen failed: e: " + e.getMessage());
			}*/
		}
		return super.keyChar(c, status, time);
	}

	private void buildPage(int page)
	{
		current_page = page;

		getMainManager().setBackground(BackgroundFactory.createBitmapBackground(ResourceHelper.getImage("_welcome_" + page + ".jpg")));

		button_manager.deleteAll();
		switch (page)
		{
		case 1 :

			button_manager.add(button_next);
			button_manager.add(button_skip);
			break;

		case 2 :

			button_manager.add(button_next);
			button_manager.add(button_skip);
			break;

		case 3 :

			button_manager.add(button_next);
			button_manager.add(button_skip);
			break;

		case 4 :

			button_manager.add(button_skip);
			break;
		}

		for (int i = 0; i < progress_manager.getFieldCount(); i++)
		{
			Bullet bullet = (Bullet) progress_manager.getField(i);
			if (i + 1 == page)
			{
				bullet.fillcolor = ResourceHelper.color_primary;
				bullet.outlinecolor = ResourceHelper.color_white;
			}
			else
			{
				bullet.fillcolor = ResourceHelper.color_white;
				bullet.outlinecolor = ResourceHelper.color_primary;
			}
		}

		progress_manager.invalidate();
	}

	private static class Bullet extends Field
	{
		private final int radius;
		private int fillcolor;
		private int outlinecolor;

		public Bullet(int radius, int fillcolor, int outlinecolor)
		{
			this.radius = radius;
			this.fillcolor = fillcolor;
			this.outlinecolor = outlinecolor;
		}

		public int getPreferredWidth()
		{
			return radius * 2;
		}

		public int getPreferredHeight()
		{
			return radius * 2;
		}

		protected void layout(int width, int height)
		{
			setExtent(getPreferredWidth(), getPreferredHeight());
		}

		protected void paint(Graphics graphics)
		{
			graphics.setColor(fillcolor);
			graphics.setGlobalAlpha(190);
			graphics.fillArc(0, 0, getWidth(), getHeight(), 0, 360);
			graphics.setGlobalAlpha(255);
			graphics.setColor(outlinecolor);
			graphics.drawArc(0, 0, getWidth(), getHeight(), 0, 360);
		}
	}

	public void fieldChanged(Field field, int context)
	{
		if (field == button_next)
		{
			buildPage(current_page + 1);
		}
		else if (field == button_skip)
		{
			PersistentStoreHelper.setShowWelcome(false);
			((MainApplication) UiApplication.getUiApplication()).slideScreen(new LandingScreen());
			close();
		}
	}


}
