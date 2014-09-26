package fi.bb.checkers.prompts;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.FullScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.BackgroundFactory;
import fi.bb.checkers.ui.components.LabelField;
import fi.bb.checkers.ui.components.TextImageButton;

public class TransparentDialog extends FullScreen implements FieldChangeListener
{

	RichTextField detailField;

	// Font titleFont = Font.getDefault().derive(Font.BOLD, 20);
	// Font detailFont = Font.getDefault().derive(Font.PLAIN, 16);

	public TransparentDialog(String title, String content, String buttonLabel)
	{

		VerticalFieldManager infoManager = new VerticalFieldManager(Field.USE_ALL_WIDTH);

		LabelField titleField = new LabelField(title, Color.GRAY, Field.FIELD_HCENTER);
		titleField.setFont(Font.getDefault().derive(Font.BOLD, 8, Ui.UNITS_pt));
		titleField.setMargin(20, 0, 0, 0);

		infoManager.add(titleField);

		LabelField titleField1 = new LabelField(content, 0xffffff, Field.FIELD_HCENTER);
		titleField1.setFont(Font.getDefault().derive(Font.PLAIN, 8, Ui.UNITS_pt));
		titleField1.setMargin(20, 0, 0, 0);

		infoManager.add(titleField1);

		TextImageButton okButton = new TextImageButton(buttonLabel, "btn_sml_default", "btn_sml_hover");
		okButton.setChangeListener(this);
		okButton.setMargin(20, 50, 0, 50);

		infoManager.add(okButton);

		add(infoManager);
	}

	protected void sublayout(int width, int height)
	{
		super.sublayout(getPreferredWidth(), getPreferredHeight());
		setExtent(getPreferredWidth(), getPreferredHeight());

		setPosition(0, 0);

		this.setBackground(BackgroundFactory.createSolidTransparentBackground(0x000000, 200));
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
