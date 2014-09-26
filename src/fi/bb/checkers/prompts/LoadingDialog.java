package fi.bb.checkers.prompts;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.FullScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.BackgroundFactory;
import net.rim.device.api.ui.decor.BorderFactory;
import fi.bb.checkers.MainApplication;
import fi.bb.checkers.helpers.ResourceHelper;
import fi.bb.checkers.ui.components.AnimatedGIFField;
import fi.bb.checkers.ui.components.LabelField;

public class LoadingDialog extends FullScreen implements FieldChangeListener
{

	String detail;

	RichTextField detailField;

	// Font titleFont = Font.getDefault().derive(Font.BOLD, 20);
	// Font detailFont = Font.getDefault().derive(Font.PLAIN, 16);

	private LoadingDialog(String detail)
	{

		this.detail = detail;

		VerticalFieldManager infoManager = new VerticalFieldManager(FIELD_HCENTER | FIELD_VCENTER);
		// infoManager.setMargin(50, 50, 50, 50);
		int vM = Display.getHeight() / 5;
		int hM = Display.getWidth() / 5;
		infoManager.setMargin(vM, hM, 10, hM);

		LabelField titleField = new LabelField("Please wait...", ResourceHelper.color_primary, Field.FIELD_HCENTER );
		titleField.setFont(Font.getDefault().derive(Font.BOLD, 8, Ui.UNITS_pt));
		titleField.setMargin(0, 0, 10, 0);

		infoManager.add(titleField);

		infoManager.add(new AnimatedGIFField("loading.gif", Field.FIELD_HCENTER));

		LabelField detailField = new LabelField(detail, ResourceHelper.color_black, FIELD_HCENTER | DrawStyle.HCENTER);
		detailField.setFont(Font.getDefault().derive(Font.PLAIN, 6, Ui.UNITS_pt));
		detailField.setMargin(10, 0, 10, 0);

		infoManager.add(detailField);

		Bitmap borderBitmap = Bitmap.getBitmapResource("rounded-border.png");
		infoManager.setBorder(BorderFactory.createBitmapBorder(new XYEdges(12, 12, 12, 12), borderBitmap));

		add(infoManager);

	}

	protected void sublayout(int width, int height)
	{
		super.sublayout(getPreferredWidth(), getPreferredHeight());
		setExtent(getPreferredWidth(), getPreferredHeight());

		setPosition(0, 0);

		this.setBackground(BackgroundFactory.createSolidTransparentBackground(0xff000000, 200));
	}

	public int getPreferredWidth()
	{
		return Display.getWidth();
	}

	public int getPreferredHeight()
	{
		return Display.getHeight();
	}
	public void fieldChanged(Field field, int context)
	{

	}

	protected boolean keyDown(int keycode, int status)
	{
		if (Keypad.key(keycode) == Keypad.KEY_ESCAPE)
		{
			// this.close();
		}
		return false;
	}

	public static LoadingDialog push(String detail)
	{
		final LoadingDialog dialog = new LoadingDialog(detail);
		UiApplication.getUiApplication().invokeLater(new Runnable()
		{
			public void run()
			{
				((MainApplication) UiApplication.getUiApplication()).fadeScreen(dialog, false);
			}
		});
		return dialog;
	}
}
