package tn.meteor.efficaisse.ui.home;

import java.util.List;

import tn.meteor.efficaisse.model.Product;
import tn.meteor.efficaisse.ui.base.BaseContract;

/**
 * Created by SKIIN on 27/01/2018.
 */

public interface HomeContract {

    interface View extends BaseContract.View{


    }


    interface Presenter extends BaseContract.Presenter{

        void testGet();

        void addProducts(List<Product> products);

    }



}
