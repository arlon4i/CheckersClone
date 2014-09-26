package fi.bb.checkers.prompts;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.TouchEvent;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.BackgroundFactory;
import net.rim.device.api.ui.decor.BorderFactory;

public class CouponDialog extends PopupScreen {

    public static final int ADDED = 0;
    public static final int REMOVED = 1;

    public CouponDialog(int type) {
	super(new VerticalFieldManager());

	setBackground(BackgroundFactory.createSolidTransparentBackground(Color.BLACK, 220));
	Bitmap borderBitmap = Bitmap.getBitmapResource("rounded-border-trans.png");
	setBorder(BorderFactory.createBitmapBorder(new XYEdges(12, 12, 12, 12), borderBitmap));

	BitmapField image = new BitmapField(null, Field.FIELD_HCENTER);
	LabelField text = new LabelField("", Field.FIELD_HCENTER);
	text.setFont(Font.getDefault().derive(Font.BOLD, 8, Ui.UNITS_pt));

	if (type == ADDED) {
	    image.setBitmap(Bitmap.getBitmapResource("couponadded.png"));
	    text.setText("EeziCoupon Added");
    
	    
	} else {
	    image.setBitmap(Bitmap.getBitmapResource("couponremoved.png"));
	    text.setText("EeziCoupon Removed");
	}

	add(image);
	add(text);
	
    }
    
    public CouponDialog(int type, int fade,int x) {
    	super(new VerticalFieldManager());

//    	setBackground(BackgroundFactory.createSolidTransparentBackground(Color.BLACK, 220));
//    	Bitmap borderBitmap = Bitmap.getBitmapResource("rounded-border-trans.png");
//    	setBorder(BorderFactory.createBitmapBorder(new XYEdges(12, 12, 12, 12), borderBitmap));
//
//    	BitmapField image = new BitmapField(null, Field.FIELD_HCENTER);
//    	LabelField text = new LabelField("", Field.FIELD_HCENTER);
//    	text.setFont(Font.getDefault().derive(Font.BOLD, 8, Ui.UNITS_pt));

    	if (type == ADDED) {
    	  //  image.setBitmap(Bitmap.getBitmapResource("couponadded.png"));
    	  //  text.setText("EeziCoupon Added");
    	    
    	    Status.show("EeziCoupon Added *****", fade);
    	    
    	    
    	} else {
    	//    image.setBitmap(Bitmap.getBitmapResource("couponremoved.png"));
    	//    text.setText("EeziCoupon Removed");
    	    Status.show("EeziCoupon Removed ******", fade);
    	}

//    	add(image);
//    	add(text);
    	
        }

    
    
    
    
    
    
    

    protected boolean keyDown(int keycode, int status) {
	if (Keypad.key(keycode) == Keypad.KEY_ESCAPE) {
	    this.close();
	}
	return false;
    }

    protected boolean navigationClick(int status, int time) {
	this.close();
	return false;
    }

    protected boolean touchEvent(TouchEvent message) {
	if (message.getEvent() == TouchEvent.UNCLICK) {
	    close();
	}

	return super.touchEvent(message);
    }
}
