package fi.bb.checkers.prompts;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Keypad;
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
import fi.bb.checkers.ui.components.LabelField;
import fi.bb.checkers.ui.components.TextImageButton;

public class InfoDialog extends PopupScreen implements FieldChangeListener
{
	private InfoDialog(String title, Field detail, String buttonText)
	{
		super(new VerticalFieldManager(FIELD_HCENTER | FIELD_VCENTER | VERTICAL_SCROLL));
		setBackground(BackgroundFactory.createSolidBackground(ResourceHelper.color_white));

		if (!title.equals(""))
		{
			LabelField titleField = new LabelField(title, ResourceHelper.color_primary, FIELD_HCENTER | DrawStyle.HCENTER);
			titleField.setFont(ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(20), Ui.UNITS_px));
			titleField.setMargin(0, 0, 10, 0);
			add(titleField);
		}

		add(detail);

		TextImageButton okButton = new TextImageButton(buttonText, "btn_sml_default", "btn_sml_hover", FIELD_HCENTER);
		okButton.setTextColor(ResourceHelper.color_white);
		okButton.setTextColorHover(ResourceHelper.color_primary);
		okButton.setTextColorPressed(ResourceHelper.color_primary);
		okButton.setChangeListener(this);
		okButton.setMargin(ResourceHelper.convert(10), 0, 0, 0);

		add(okButton);

		Bitmap borderBitmap = Bitmap.getBitmapResource("rounded-border.png");
		setBorder(BorderFactory.createBitmapBorder(new XYEdges(12, 12, 12, 12), borderBitmap));
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

	public static void doModal(String title, String detail, String buttonText)
	{
		LabelField detailField = new LabelField(detail, ResourceHelper.color_black,Field.FIELD_HCENTER);
		detailField.setFont(ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(15), Ui.UNITS_px));

		doModal(title, detailField, buttonText);
	}

	public static void doModal(final String title, final Field detail, final String buttonText)
	{
		if (Application.isEventDispatchThread())
		{
			push(title, detail, buttonText);
		}
		else
		{
			UiApplication.getUiApplication().invokeLater(new Runnable()
			{
				public void run()
				{
					push(title, detail, buttonText);
				}
			});
		}
	}

	private static void push(String title, Field detail, String buttonText)
	{
		FullScreen trans = new FullScreen();
		trans.setBackground(BackgroundFactory.createSolidTransparentBackground(Color.BLACK, 200));
		((MainApplication) UiApplication.getUiApplication()).fadeScreen(trans, false);

		InfoDialog dialog = new InfoDialog(title, detail, buttonText);
		UiApplication.getUiApplication().pushModalScreen(dialog);
		trans.close();
	}
}
