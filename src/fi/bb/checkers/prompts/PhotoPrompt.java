package fi.bb.checkers.prompts;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Characters;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.container.FullScreen;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.BackgroundFactory;
import net.rim.device.api.ui.decor.BorderFactory;

import fi.bb.checkers.MainApplication;
import fi.bb.checkers.helpers.ResourceHelper;
import fi.bb.checkers.ui.components.BaseButton;

public class PhotoPrompt extends PopupScreen
{
	public static final int CANCEL = 0;
	public static final int CAMERA = 1;
	public static final int GALLERY = 2;

	private int response = CANCEL;

	private PhotoPrompt()
	{
		super(new VerticalFieldManager());
		Bitmap borderBitmap = ResourceHelper.getImage("rounded-border.png");
		setBorder(BorderFactory.createBitmapBorder(new XYEdges(12, 12, 12, 12), borderBitmap));
		ListItem button_camera = new ListItem("Camera", CAMERA);
		ListItem button_gallery = new ListItem("Gallery", GALLERY);

		add(button_camera);
		add(button_gallery);
	}

	public int getPreferredWidth()
	{
		return ResourceHelper.convert(300);
	}

	public int getPreferredHeight()
	{
		return ResourceHelper.convert(100);
	}

	protected void applyTheme(Graphics arg0, boolean arg1)
	{
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

	public static int doModal()
	{
		FullScreen trans = new FullScreen();
		trans.setBackground(BackgroundFactory.createSolidTransparentBackground(Color.BLACK, 200));
		((MainApplication) UiApplication.getUiApplication()).fadeScreen(trans, false);

		PhotoPrompt screen = new PhotoPrompt();
		UiApplication.getUiApplication().pushModalScreen(screen);
		trans.close();
		return screen.response;
	}

	private class ListItem extends BaseButton
	{
		private final String label;
		private final int response;

		public ListItem(String label, int response)
		{
			this.label = label;
			this.response = response;
			setFont(ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(17), Ui.UNITS_px));
		}

		protected void paint(Graphics graphics)
		{
			graphics.setColor(_focus ? ResourceHelper.color_primary : ResourceHelper.color_white);
			graphics.fillRect(0, 0, getWidth(), getHeight());

			int x = (getWidth() - getFont().getAdvance(label)) / 2;
			int y = (getHeight() - getFont().getHeight()) / 2;

			graphics.setColor(_focus ? ResourceHelper.color_white : ResourceHelper.color_primary);
			graphics.drawText(label, x, y);
		}

		public int getPreferredWidth()
		{
			return PhotoPrompt.this.getPreferredWidth();
		}

		public int getPreferredHeight()
		{
			return PhotoPrompt.this.getPreferredHeight() / 2;
		}

		public void clickButton()
		{
			super.clickButton();
			PhotoPrompt.this.response = response;
			close();
		}
	}
}
