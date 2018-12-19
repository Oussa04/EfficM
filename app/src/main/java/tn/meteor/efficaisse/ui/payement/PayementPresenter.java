package tn.meteor.efficaisse.ui.payement;

import android.content.Context;

import java.util.List;

import tn.meteor.efficaisse.model.Payment;
import tn.meteor.efficaisse.ui.base.BasePresenter;


public class PayementPresenter extends BasePresenter implements PayementContract.Presenter {


    private PayementContract.View view;


    public PayementPresenter(PayementContract.View view, Context context) {
        this.view = view;
    }



    @Override
    public void addPayement(Payment payment, List<Payment> payments) {
        payments.add(payment);
    }


}
