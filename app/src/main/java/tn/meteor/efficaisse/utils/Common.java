package tn.meteor.efficaisse.utils;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.provider.Settings;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import tn.meteor.efficaisse.R;


public final class Common {

    private static final String TAG = "Common";
    private static ProgressDialog progressDialog;

    private Common() {

    }


    public static ProgressDialog showLoadingDialog(Context context) {
        progressDialog = new ProgressDialog(context);
        progressDialog.show();
        if (progressDialog.getWindow() != null) {
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);


        return progressDialog;
    }

    public static void changeProgressText(String text) {
        if (progressDialog != null) {
            TextView textView = progressDialog.findViewById(R.id.progressText);
            textView.setText(text);
        }
    }

    @SuppressLint("all")
    public static String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static boolean isEmailValid(String email) {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN =
                "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static String loadJSONFromAsset(Context context, String jsonFileName)
            throws IOException {

        AssetManager manager = context.getAssets();
        InputStream is = manager.open(jsonFileName);

        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();

        return new String(buffer, "UTF-8");
    }

    public static String getTimeStamp() {
        return new SimpleDateFormat(Constants.TIMESTAMP_FORMAT, Locale.US).format(new Date());
    }

    public static RequestBody toRequestBody(Object t) {
        return RequestBody.create(MediaType.parse("text/plain"), t.toString());
    }


    public static MultipartBody.Part toMutliPartBody(String image) {

        if (image != null) {
            File imageFile = CacheStore.getInstance().getFileUri(image);
            return MultipartBody.Part.createFormData("file", imageFile.getName(), RequestBody.create(MediaType.parse("images/*"), imageFile));
        } else {
            return null;
        }
    }

    public static String dateConverter(long longFormat) {

        Date date = new Date(longFormat);
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        return df.format(date);
    }


    public static String getDateDifference(Date startDate) {

        //milliseconds

        Date now = new Date();
        long different = now.getTime() - startDate.getTime();


        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;


        String result = "";

        result = result + elapsedHours + ":";


        result = result + elapsedMinutes+":";
        result = result + elapsedSeconds;


        return result;
    }

}
