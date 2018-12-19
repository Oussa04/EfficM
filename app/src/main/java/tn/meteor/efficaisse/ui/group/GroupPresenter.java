package tn.meteor.efficaisse.ui.group;

import tn.meteor.efficaisse.data.repository.CustomerGroupRepository;
import tn.meteor.efficaisse.data.repository.CustomerRepository;
import tn.meteor.efficaisse.model.Customer;
import tn.meteor.efficaisse.model.CustomerGroup;
import tn.meteor.efficaisse.ui.base.BasePresenter;

/**
 * Created by lilk on 27/01/2018.
 */

public class GroupPresenter extends BasePresenter implements GroupContract.Presenter {


    private GroupContract.View view;

    public GroupPresenter(GroupContract.View view) {
        this.view = view;
    }

    @Override
    public void addCustomerGroup(CustomerGroup customerGroup) {
        CustomerGroupRepository repository = new CustomerGroupRepository();
        repository.save(customerGroup);
        view.updateUI();
    }
}

