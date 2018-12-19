package tn.meteor.efficaisse.ui.customers;

import java.util.List;

import tn.meteor.efficaisse.data.repository.CustomerGroupRepository;
import tn.meteor.efficaisse.data.repository.CustomerRepository;
import tn.meteor.efficaisse.model.Customer;
import tn.meteor.efficaisse.model.CustomerGroup;
import tn.meteor.efficaisse.ui.base.BasePresenter;

/**
 * Created by lilk on 20/01/2018.
 */

public class CustomersPresenter extends BasePresenter implements CustomersContract.Presenter {


    private CustomersContract.View view;

    public CustomersPresenter(CustomersContract.View view) {
        this.view = view;
    }


    @Override
    public void initCustomerGroupsList(List<CustomerGroup> customerGroups) {
        CustomerGroupRepository customerGroupRepository = new CustomerGroupRepository();
        customerGroups.clear();

        customerGroups.addAll(customerGroupRepository.findAll());
        view.updateCustomerGroups();
    }

    @Override
    public void initCustomersList(List<Customer> customerList) {
        CustomerRepository repo = new CustomerRepository();
        customerList.clear();
        customerList.addAll(repo.findAll());
        view.updateCustomers();
    }

    @Override
    public void getCustomersByGroup(CustomerGroup customerGroup, List<Customer> customerList) {
        customerList.clear();
        customerList.addAll(customerGroup.getCustomers());
        view.updateCustomers();
    }

    @Override
    public void getCustomersWithoutGroup(List<Customer> customerList) {
        CustomerRepository customerRepository = new CustomerRepository();
        customerList.clear();
        customerList.addAll(customerRepository.getCustomersWithoutGroup());
        view.updateCustomers();

    }
}
