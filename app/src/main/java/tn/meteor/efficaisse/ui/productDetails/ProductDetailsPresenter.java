package tn.meteor.efficaisse.ui.productDetails;

import tn.meteor.efficaisse.ui.base.BasePresenter;


/**
 * Created by lilk on 27/01/2018.
 */

public class ProductDetailsPresenter extends BasePresenter implements ProductDetailsContract.Presenter{

    private ProductDetailsContract.View view;

    public ProductDetailsPresenter(ProductDetailsContract.View view) {
        this.view = view;
    }




}
