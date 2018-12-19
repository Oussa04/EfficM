package tn.meteor.efficaisse.ui.customerDetails;

import tn.meteor.efficaisse.ui.base.BasePresenter;


/**
 * Created by lilk on 27/01/2018.
 */

public class CustomerDetailsPresenter extends BasePresenter implements CustomerDetailsContract.Presenter{

    private CustomerDetailsContract.View view;

    public CustomerDetailsPresenter(CustomerDetailsContract.View view) {
        this.view = view;
    }




}
