package tn.meteor.efficaisse.ui.discountAdd;

import tn.meteor.efficaisse.data.repository.DiscountRepository;
import tn.meteor.efficaisse.model.Discount;
import tn.meteor.efficaisse.ui.base.BasePresenter;

/**
 * Created by lilk on 27/01/2018.
 */

public class DiscountAddPresenter extends BasePresenter implements DiscountAddContract.Presenter {


    private DiscountAddContract.View view;

    public DiscountAddPresenter(DiscountAddContract.View view) {
        this.view = view;

    }

    @Override
    public void addDiscount(Discount discount) {

        DiscountRepository discountRepository = new DiscountRepository();
        discountRepository.save(discount);
view.updateUI();
    }
}

