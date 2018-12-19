package tn.meteor.efficaisse.ui.base;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import net.steamcrafted.loadtoast.LoadToast;

import tn.meteor.efficaisse.R;
import tn.meteor.efficaisse.data.preferences.AppPreferencesHelper;
import tn.meteor.efficaisse.data.preferences.PreferencesHelper;
import tn.meteor.efficaisse.utils.Common;
import tn.meteor.efficaisse.utils.Constants;
import tn.meteor.efficaisse.utils.EfficaisseApplication;
import tn.meteor.efficaisse.utils.Network;


public abstract class BaseActivity extends AppCompatActivity implements BaseContract.View {

    private ProgressDialog mProgressDialog;
    protected float percentage;
    protected boolean isCharging;


    private BroadcastReceiver mBroadcastReceiver =  new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            isCharging = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1) == BatteryManager.BATTERY_STATUS_CHARGING ||
                    intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1) == BatteryManager.BATTERY_STATUS_FULL;
            percentage = level / (float) scale;
            onBatteryChanged(isCharging,percentage);

        }
    };


    protected LoadToast loadToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);

        getApplicationContext().registerReceiver(mBroadcastReceiver, iFilter);
        PreferencesHelper preferencesHelper = new AppPreferencesHelper(EfficaisseApplication.getInstance().getContext(), Constants.SHARED_PREFERENCES_NAME.PREFERENCES_USER);
        if (loadToast != null && !preferencesHelper.isSyncing()) {
            EfficaisseApplication.toast.hide();
        }
        if (preferencesHelper.isSyncing()) {
            EfficaisseApplication.toast = new LoadToast(this);
            loadToast = EfficaisseApplication.toast;
            EfficaisseApplication.toast.setText("Syncronisation en cours ...");
            EfficaisseApplication.toast.show();
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        PreferencesHelper preferencesHelper = new AppPreferencesHelper(EfficaisseApplication.getInstance().getContext(), Constants.SHARED_PREFERENCES_NAME.PREFERENCES_USER);


        if (loadToast != null && !preferencesHelper.isSyncing()) {

            loadToast.hide();
        }
    }

    @Override
    protected void onDestroy() {
        if (mBroadcastReceiver != null)
            getApplicationContext().unregisterReceiver(mBroadcastReceiver);

        super.onDestroy();
    }

    @Override
    public void showLoading() {
        hideLoading();
        mProgressDialog = Common.showLoadingDialog(this);
    }

    @Override
    public void hideLoading() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
    }

    private void showSnackBar(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),
                message, Snackbar.LENGTH_SHORT);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView
                .findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
        snackbar.show();
    }

    @Override
    public void onError(String message) {
        if (message != null) {
            showSnackBar(message);
        } else {
            showSnackBar("Some Error Occurred!");
        }
    }

    @Override
    public void onError(@StringRes int resId) {
        onError(getString(resId));
    }

    @Override
    public void showMessage(String message) {
        if (message != null) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Some Error Occurred!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void showMessage(@StringRes int resId) {
        showMessage(getString(resId));
    }


    @Override
    public boolean isNetworkConnected() {
        return Network.isNetworkConnected(getApplicationContext());
    }

    @Override
    public void onBackPressed() {

        if (!isTaskRoot() || getFragmentManager().getBackStackEntryCount() > 0) {
            super.onBackPressed();

        }


    }

    protected  void onBatteryChanged(boolean isCharging, float percentage){



    };
}
