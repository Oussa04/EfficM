package tn.meteor.efficaisse.utils;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.HashMap;
import java.util.Map;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

/**
 * Created by lilk on 19/02/2018.
 */

public class QrCodeUtils {

    public static Bitmap GenerateClick(String code) {
        Bitmap finalCode = null;
        try {
            //setting size of qr code
            int width = 300;
            int height = 300;
            int smallestDimension = width < height ? width : height;


            //setting parameters for qr code
            String charset = "UTF-8";
            Map<EncodeHintType, ErrorCorrectionLevel> hintMap = new HashMap<EncodeHintType, ErrorCorrectionLevel>();
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            finalCode = CreateQRCode(code, charset, hintMap, smallestDimension, smallestDimension);

        } catch (Exception ex) {
            Log.e("QrGenerate", ex.getMessage());
        }
        return finalCode;
    }

    public static Bitmap CreateQRCode(String qrCodeData, String charset, Map hintMap, int qrCodeheight, int qrCodewidth) {

        Bitmap bitmap = null;
        try {
            //generating qr code in bitmatrix type
            BitMatrix matrix = new MultiFormatWriter().encode(new String(qrCodeData.getBytes(charset), charset),
                    BarcodeFormat.QR_CODE, qrCodewidth, qrCodeheight, hintMap);
            //converting bitmatrix to bitmap

            int width = matrix.getWidth();
            int height = matrix.getHeight();
            int[] pixels = new int[width * height];
            // All are 0, or black, by default
            for (int y = 0; y < height; y++) {
                int offset = y * width;
                for (int x = 0; x < width; x++) {
                    pixels[offset + x] = matrix.get(x, y) ? BLACK : WHITE;


                }
            }

            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            //setting bitmap to image view


        } catch (Exception er) {
            Log.e("QrGenerate", er.getMessage());
        }
        return bitmap;
    }

}
