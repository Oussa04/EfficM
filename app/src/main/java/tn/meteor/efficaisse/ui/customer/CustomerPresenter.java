package tn.meteor.efficaisse.ui.customer;

import tn.meteor.efficaisse.data.repository.CustomerRepository;
import tn.meteor.efficaisse.model.Customer;
import tn.meteor.efficaisse.ui.base.BasePresenter;

/**
 * Created by lilk on 27/01/2018.
 */

public class CustomerPresenter extends BasePresenter implements CustomerContract.Presenter {


    private CustomerContract.View view;

    public CustomerPresenter(CustomerContract.View view) {
        this.view = view;
    }

    @Override
    public void addCustomer(Customer customer) {
        CustomerRepository repository = new CustomerRepository();
        repository.save(customer);

    }
}

