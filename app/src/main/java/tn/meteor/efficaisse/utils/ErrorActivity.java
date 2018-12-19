

package tn.meteor.efficaisse.utils;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.EditText;

import java.util.Date;

import cat.ereza.customactivityoncrash.CustomActivityOnCrash;
import cat.ereza.customactivityoncrash.config.CaocConfig;
import tn.meteor.efficaisse.R;
import tn.meteor.efficaisse.data.preferences.AppPreferencesHelper;
import tn.meteor.efficaisse.data.preferences.PreferencesHelper;
import tn.meteor.efficaisse.model.StackTrace;
import tn.meteor.efficaisse.ui.Homie;
import tn.meteor.efficaisse.ui.base.BaseActivity;

public class ErrorActivity extends BaseActivity {
    private Button restart;
    private EditText description;
    private AppPreferencesHelper preferences2;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_custom_error);
        restart = findViewById(R.id.restart);
        description = findViewById(R.id.description);
        preferences2 = new AppPreferencesHelper(getApplicationContext(), Constants.SHARED_PREFERENCES_NAME.PREFERENCES_ACCES_TYPE);


        PreferencesHelper prefs = new AppPreferencesHelper(EfficaisseApplication.getInstance().getContext(), Constants.SHARED_PREFERENCES_NAME.PREFERENCES_USER);


        String crashText = CustomActivityOnCrash.getStackTraceFromIntent(getIntent());
        crashText = crashText.replace(System.getProperty("line.separator"), "<br/>\n");
        StackTrace stackTrace = new StackTrace();
        stackTrace.setDate(new Date());
        stackTrace.setStackTrace(crashText);
        stackTrace.setStore(prefs.getStoreRDC());
        stackTrace.save();


        final CaocConfig config = CustomActivityOnCrash.getConfigFromIntent(getIntent());

        if (config == null) {
            finish();
            return;
        }


        restart.setOnClickListener(v -> {


            if (!description.getText().toString().trim().isEmpty()) {

                stackTrace.setDescription(  description.getText().toString().replace(System.getProperty("line.separator"), "<br/>\n"));
                stackTrace.update();
            }


            if (preferences2.getAccesType() == null || preferences2.getAccesType().equals("")) {
                CustomActivityOnCrash.restartApplication(ErrorActivity.this, config);
            } else {
                CustomActivityOnCrash.restartApplicationWithIntent(ErrorActivity.this, new Intent(ErrorActivity.this, Homie.class), config);
            }

        });


    }
}
