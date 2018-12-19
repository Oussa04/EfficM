package tn.meteor.efficaisse.ui.home;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;

import com.annimon.stream.Stream;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tn.meteor.efficaisse.R;
import tn.meteor.efficaisse.data.preferences.AppPreferencesHelper;
import tn.meteor.efficaisse.data.preferences.PreferencesHelper;
import tn.meteor.efficaisse.data.repository.ContreBonRepository;
import tn.meteor.efficaisse.model.ContreBon;
import tn.meteor.efficaisse.model.ErrorSync;
import tn.meteor.efficaisse.service.ServiceProduct;
import tn.meteor.efficaisse.ui.base.BaseActivity;
import tn.meteor.efficaisse.ui.cashier.CashierActivity;
import tn.meteor.efficaisse.utils.Constants;
import tn.meteor.efficaisse.utils.EfficaisseApplication;
import tn.meteor.efficaisse.utils.SyncPushListener;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;


public class HomeActivity extends BaseActivity implements HomeContract.View, SyncPushListener {

    private HomePresenter homePresenter;
    private ServiceProduct serviceProduct;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        homePresenter = new HomePresenter(this);

        PreferencesHelper prefs = new AppPreferencesHelper(EfficaisseApplication.getInstance().getApplicationContext(), Constants.SHARED_PREFERENCES_NAME.PREFERENCES_USER);
        prefs.setCipherKey("b28f9a601dea428be41c7e652b80f05");
        EditText code = findViewById(R.id.code);
        EditText crypt = findViewById(R.id.cypher);
        imageView = findViewById(R.id.imageView);

        findViewById(R.id.generate).setOnClickListener(view -> {
            startActivity(new Intent(this, CashierActivity.class));
            ContreBon contreBon = new ContreBon(25);
            code.setText(contreBon.getCode());
            crypt.setText(contreBon.getCrypted());
            ContreBonRepository.save(contreBon);
            GenerateClick(contreBon.getCrypted());

        });
        findViewById(R.id.decrypt).setOnClickListener(view -> {
            startActivity(new Intent(this, CashierActivity.class));

            ContreBon contreBon = new ContreBon(crypt.getText().toString());
            code.setText(contreBon.getCode());
        });
        Double[] ints = {5.0,10.0,10.0,15.0,20.0,20.0,30.0,35.0,45.0,50.0};

        ArrayList<Double> list = new ArrayList<>(Arrays.asList(ints));
        list = filter(list);
        Log.e("hehe",list.toString());



    }

    public void GenerateClick(String code) {
        try {
            //setting size of qr code
            int width = 300;
            int height = 300;
            int smallestDimension = width < height ? width : height;


            //setting parameters for qr code
            String charset = "UTF-8";
            Map<EncodeHintType, ErrorCorrectionLevel> hintMap = new HashMap<EncodeHintType, ErrorCorrectionLevel>();
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            CreateQRCode(code, charset, hintMap, smallestDimension, smallestDimension);

        } catch (Exception ex) {
            Log.e("QrGenerate", ex.getMessage());
        }
    }

    public void CreateQRCode(String qrCodeData, String charset, Map hintMap, int qrCodeheight, int qrCodewidth) {


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

            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            //setting bitmap to image view



            imageView.setImageBitmap(bitmap);

        } catch (Exception er) {
            Log.e("QrGenerate", er.getMessage());
        }
    }


    @Override
    public void onSyncPushFinished(List<ErrorSync> errors) {

    }
    public ArrayList<Double> filter(ArrayList<Double> array)
    {
        boolean test = false;

        if(array.isEmpty()){
            return array;
        }
        Double max = array.get(array.size()-1);
        Double min  = array.get(0);
        if(min.equals(max)){
            return array;
        }
        double avg = Stream.of(array).mapToDouble(i -> i).sum() / array.size();

        if(errorMargin(avg , max)> 0.5){
            array.remove(array.size()-1);
            test=true;
        }
        if(errorMargin(avg,min)>0.5){
            array.remove(0);
            test=true;
        }
        if(test){
            return filter(array);
        }
        return array;


    }
    private double errorMargin(double avg , double value){
        return Math.abs(value - avg)/avg;
    }
}
