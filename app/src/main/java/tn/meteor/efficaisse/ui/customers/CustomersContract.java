package tn.meteor.efficaisse.ui.customers;

import java.util.List;

import tn.meteor.efficaisse.model.Customer;
import tn.meteor.efficaisse.model.CustomerGroup;
import tn.meteor.efficaisse.ui.base.BaseContract;

/**
 * Created by lilk on 20/01/2018.
 */

public interface CustomersContract {

    interface View extends BaseContract.View {


        void updateCustomers();

        void updateCustomerGroups();

        void showCustomer(Customer customer);


    }


    interface Presenter extends BaseContract.Presenter {
        void initCustomerGroupsList(List<CustomerGroup> customerGroups);

        void initCustomersList(List<Customer> customerList);

        void getCustomersByGroup(CustomerGroup customerGroup, List<Customer> customerList);

        void getCustomersWithoutGroup(List<Customer> customerList);


    }
}
