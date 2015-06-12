package fi.bb.checkers.helpers;

import java.util.Hashtable;
import net.rim.device.api.system.Bitmap;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class ZebraCrossingHelper
{
	/**
	 * Generate QRCode for a given input text, using Google Zebra Crossing library
	 * 
	 * @param inputtext
	 *            the input text
	 * @param width
	 *            the QRCode desired output width
	 * @param height
	 *            the QRCode desired output height
	 */
	public static Bitmap generateQRCode(String inputtext, int width, int height) 
	{
		MultiFormatWriter writer = new MultiFormatWriter();
		BitMatrix matrix = null;
		Bitmap qrcode = null; 
	    
	    try
	    {
	        Hashtable hints = new Hashtable(2);
	        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
	        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
	        matrix = writer.encode(inputtext, BarcodeFormat.QR_CODE, width, height, hints);
	        qrcode = new Bitmap(width, height);
	        BitMatrixtoBitmap(matrix).scaleInto(qrcode, Bitmap.FILTER_LANCZOS, Bitmap.SCALE_TO_FILL);
	    } 
	    catch (WriterException e)
	    {
	    	
	    }
	    finally
	    {
	    	return qrcode;
	    }
	}
	
	private static Bitmap BitMatrixtoBitmap(BitMatrix matrix)
	{		
		int width = matrix.width;
		int height = matrix.height;
        int[] imgdata = new int[width];
        Bitmap bitmap = new Bitmap(width, height);
        
        for(int y = 0; y < height; y++)
        {
            for(int x = 0; x < width; x++)
            {
                imgdata[x] = (!matrix.get(x, y)) ? 0xFFFFFFFF : 0xFF000000;
            }
            
            bitmap.setARGB(imgdata, 0, width, 0, y, width, 1);
        }
        
        return bitmap;
	}
}
