package tn.meteor.efficaisse.ui.category;


import tn.meteor.efficaisse.data.repository.CategoryRepository;

import tn.meteor.efficaisse.model.Category;
import tn.meteor.efficaisse.ui.base.BasePresenter;

/**
 * Created by lilk on 27/01/2018.
 */

public class CategoryPresenter extends BasePresenter implements CategoryContract.Presenter {


    private CategoryContract.View view;

    public CategoryPresenter(CategoryContract.View view) {
        this.view = view;
    }


    @Override
    public void addCategory(Category category) {
        CategoryRepository categoryRepository = new CategoryRepository();
        categoryRepository.save(category);
        view.updateUI();
    }
}

