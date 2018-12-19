package tn.meteor.efficaisse.ui.category;

import tn.meteor.efficaisse.model.Category;
import tn.meteor.efficaisse.ui.base.BaseContract;

public interface CategoryContract {
    interface View extends BaseContract.View {

        void bindComponent();

        void updateUI();


    }


    interface Presenter extends BaseContract.Presenter {

        void addCategory(Category category);


    }
}
