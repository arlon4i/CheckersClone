package fi.bb.checkers.ui.screens;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.TransitionContext;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BitmapField;
import fi.bb.checkers.MainApplication;
import fi.bb.checkers.helpers.ResourceHelper;
import fi.bb.checkers.ui.components.LabelField;
import fi.bb.checkers.utils.BitmapTools;


public class MagnifiedQRCodeScreen extends BottomUpDrawer
{
	public MagnifiedQRCodeScreen(Bitmap qrcocde)
	{
		super("QR Code");		
		
		LabelField qrcode_text = new LabelField("QR Code only at selected stores", ResourceHelper.color_qrcode_drawer_grey, Field.FIELD_HCENTER);
		qrcode_text.setFont(ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(12), Ui.UNITS_px));
		
		int bitmap_size = qrcode_text.getFont().getHeight() + ResourceHelper.convert(10) + ResourceHelper.convert(10) + ResourceHelper.convert(20);
		bitmap_size = getAvailableHeight() - bitmap_size; //ResourceHelper.convert(250);
		bitmap_size = Math.min((Display.getWidth() - ResourceHelper.convert(80)), bitmap_size);
		
		Bitmap magnified_qrcode = BitmapTools.resizeImage(qrcocde, bitmap_size, bitmap_size);
		BitmapField qrcode_image = new BitmapField(magnified_qrcode, BitmapField.FIELD_HCENTER);
		qrcode_image.setMargin(ResourceHelper.convert(5), qrcode_image.getMarginRight(), ResourceHelper.convert(5), qrcode_image.getMarginLeft());
		add(qrcode_image);		
		
		add(qrcode_text);
	}
	
	public static void push(Bitmap qrcocde)
	{
		MainApplication app = (MainApplication) UiApplication.getUiApplication();
		app.slideScreen(new MagnifiedQRCodeScreen(qrcocde), TransitionContext.DIRECTION_UP, TransitionContext.DIRECTION_DOWN, 2000);
	}
}
