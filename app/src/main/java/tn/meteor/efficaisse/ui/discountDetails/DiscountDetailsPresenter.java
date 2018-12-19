package tn.meteor.efficaisse.ui.discountDetails;

import java.util.List;

import tn.meteor.efficaisse.data.repository.DiscountRepository;
import tn.meteor.efficaisse.model.Discount;
import tn.meteor.efficaisse.ui.base.BasePresenter;

/**
 * Created by lilk on 20/01/2018.
 */

public class DiscountDetailsPresenter extends BasePresenter implements DiscountDetailsContract.Presenter {


    private DiscountDetailsContract.View view;

    public DiscountDetailsPresenter(DiscountDetailsContract.View view) {


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
