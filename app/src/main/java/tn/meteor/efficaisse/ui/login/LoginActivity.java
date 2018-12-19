package tn.meteor.efficaisse.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.List;

import tn.meteor.efficaisse.ui.Homie;
import tn.meteor.efficaisse.R;
import tn.meteor.efficaisse.data.preferences.AppPreferencesHelper;
import tn.meteor.efficaisse.data.repository.CashierRepository;
import tn.meteor.efficaisse.data.repository.SessionRepository;
import tn.meteor.efficaisse.model.Cashier;
import tn.meteor.efficaisse.model.Session;
import tn.meteor.efficaisse.ui.base.BaseActivity;
import tn.meteor.efficaisse.ui.offlineLogin.OfflineLoginActivity;
import tn.meteor.efficaisse.utils.Constants;

public class LoginActivity extends BaseActivity implements LoginContract.View {


    private Button connexion;
    private Button offline;
    private EditText username;
    private EditText password;
    private LoginPresenter loginPresenter;
    private AppPreferencesHelper preferences2;
    private PatternLockView mPatternLockView;
    private String firstValue ;
    private String secondValue;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ImageLoaderConfiguration config = ImageLoaderConfiguration.createDefault(this);
        ImageLoader.getInstance().init(config);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        preferences2 = new AppPreferencesHelper(getApplicationContext(), Constants.SHARED_PREFERENCES_NAME.PREFERENCES_ACCES_TYPE);
        if(preferences2.getAccesType()!= null && !preferences2.getAccesType().equals("")){
            finish();
            startActivity(new Intent(this, Homie.class));
        }
        bindComponent();


        connexion.setOnClickListener(v ->
                loginPresenter.login(username.getText().toString(), password.getText().toString())
        );
        AppPreferencesHelper preferences = new AppPreferencesHelper(getApplicationContext(), Constants.SHARED_PREFERENCES_NAME.PREFERENCES_USER);



        if (preferences.getStoreRDC() == null) {
            offline.setVisibility(View.GONE);
        } else {
            offline.setVisibility(View.VISIBLE);
        }


        offline.setOnClickListener(view -> open());

    }


    @Override
    public void bindComponent() {
        connexion = findViewById(R.id.signin);
        offline = findViewById(R.id.offline);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        loginPresenter = new LoginPresenter(this, getApplicationContext());
    }

    @Override
    public void updateUI(Cashier c) {
        if (c.getPattern() == null) {
            hideLoading();
            MaterialDialog.Builder builder = new MaterialDialog.Builder(this)
                    .customView(R.layout.dialog_pattern, false);

            MaterialDialog dialog = builder.build();

            mPatternLockView = (PatternLockView) dialog.findViewById(R.id.pattern_lock);
            TextView texty = (TextView) dialog.findViewById(R.id.texty);
            Button confirmer = (Button) dialog.findViewById(R.id.confirmer);
            confirmer.setVisibility(View.GONE);
            texty.setText("Veuillez créer un nouveau schéma");

            PatternLockViewListener mPatternLockViewListener = new PatternLockViewListener() {
                @Override
                public void onStarted() {
                    Log.d(getClass().getName(), "Pattern drawing started");
                }

                @Override
                public void onProgress(List<PatternLockView.Dot> progressPattern) {
                    Log.d(getClass().getName(), "Pattern progress: " +
                            PatternLockUtils.patternToString(mPatternLockView, progressPattern));
                }

                @Override
                public void onComplete(List<PatternLockView.Dot> pattern) {
                    Log.d(getClass().getName(), "Pattern complete: " +
                            PatternLockUtils.patternToString(mPatternLockView, pattern));
                    if (PatternLockUtils.patternToString(mPatternLockView, pattern).length() > 2) {


                        if (firstValue==null) {
                            firstValue = PatternLockUtils.patternToString(mPatternLockView, pattern);
                            mPatternLockView.clearPattern();

                            texty.setText("Veuillez redissiner le schéma");
                            confirmer.setVisibility(View.VISIBLE);
                            confirmer.setText("Terminer");
                        } else {

                            if (PatternLockUtils.patternToString(mPatternLockView, pattern).equals(firstValue)) {

                                mPatternLockView.setViewMode(PatternLockView.PatternViewMode.CORRECT); // HAMAX
                                confirmer.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        c.setSchema(Long.parseLong(firstValue));
                                        CashierRepository.update(c);
                                        Session session = new Session();
                                        session.setUser(c);
                                        SessionRepository.open(session);
                                        finish();
                                        startActivity(new Intent(LoginActivity.this, Homie.class));

                                    }
                                });

                            } else {

                                mPatternLockView.setViewMode(PatternLockView.PatternViewMode.WRONG);

                            }

                        }


                    } else {

                        mPatternLockView.setViewMode(PatternLockView.PatternViewMode.WRONG);
                        Toast.makeText(LoginActivity.this, "Schéma trés court", Toast.LENGTH_SHORT).show();

                    }
                }

                @Override
                public void onCleared() {
                    Log.d(getClass().getName(), "Pattern has been cleared");
                }
            };
            mPatternLockView.addPatternLockListener(mPatternLockViewListener);
            dialog.setCancelable(false);
            dialog.show();
        } else {
            Session session = new Session();
            session.setUser(c);
            SessionRepository.open(session);
            finish();
            startActivity(new Intent(this, Homie.class));

        }
    }

    public void open() {
        startActivity(new Intent(this, OfflineLoginActivity.class));


    }


}

