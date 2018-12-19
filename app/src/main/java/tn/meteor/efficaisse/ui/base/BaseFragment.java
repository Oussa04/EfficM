package tn.meteor.efficaisse.ui.base;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import tn.meteor.efficaisse.R;
import tn.meteor.efficaisse.utils.Common;
import tn.meteor.efficaisse.utils.Network;


/**
 * Created by ahmed on 11/25/17.
 */

public class BaseFragment extends Fragment implements BaseContract.View {


    private ProgressDialog mProgressDialog;
    @Override
    public void showLoading() {
        hideLoading();
        mProgressDialog = Common.showLoadingDialog(this.getActivity());
    }

    @Override
    public void hideLoading() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
    }

    private void showSnackBar(String message) {
        Snackbar snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content),
                message, Snackbar.LENGTH_SHORT);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView
                .findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(ContextCompat.getColor(this.getActivity(), R.color.colorAccent));
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
            Toast.makeText(this.getActivity(), message, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this.getActivity(), "Some Error Occurred!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void showMessage(@StringRes int resId) {
        showMessage(getString(resId));
    }



    @Override
    public boolean isNetworkConnected() {
        return Network.isNetworkConnected(this.getActivity().getApplicationContext());
    }

    @Override
    public void recreate() {
        getActivity().recreate();
    }



    public void setTitle(String string){

    }



}
