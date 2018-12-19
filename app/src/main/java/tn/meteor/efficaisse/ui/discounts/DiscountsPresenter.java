package tn.meteor.efficaisse.ui.discounts;

import java.util.List;

import tn.meteor.efficaisse.data.repository.DiscountRepository;
import tn.meteor.efficaisse.model.Discount;
import tn.meteor.efficaisse.ui.base.BasePresenter;

/**
 * Created by lilk on 20/01/2018.
 */

public class DiscountsPresenter extends BasePresenter implements DiscountsContract.Presenter {


    private DiscountsContract.View view;

    public DiscountsPresenter(DiscountsContract.View view) {


        this.view = view;


    }




    @Override
    public void initDicountList(List<Discount> discountList) {
        DiscountRepository repo = new DiscountRepository();
        discountList.clear();
        discountList.addAll(repo.findAll());
        view.updateUI();
    }
}
