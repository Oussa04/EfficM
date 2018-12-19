package tn.meteor.efficaisse.data.repository;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

import tn.meteor.efficaisse.data.db.LogSystem;
import tn.meteor.efficaisse.model.Commande;
import tn.meteor.efficaisse.model.Commande_Table;
import tn.meteor.efficaisse.model.Customer;
import tn.meteor.efficaisse.model.Customer_Table;

/**
 * Created by lilk on 10/03/2018.
 */

public class CustomerRepository {


    public Customer find(Customer customer) {
        return SQLite.select().from(Customer.class).where(Customer_Table.code.eq(customer.getCode())).querySingle();
    }








    public List<Customer> findAll() {
        return SQLite.select().from(Customer.class).queryList();
    }


    public void save(Customer customer) {
        LogSystem.persist(customer, "save", "Customer", false);
        customer.save();
    }


    public void update(Customer customer) {
        customer.update();
        LogSystem.persist(customer, "update", "Customer", false);


    }


    public List<Commande> getCustomersCommande(Customer customer) {

        return SQLite.select().from(Commande.class).where(Commande_Table.customerCode.eq(customer.getCode())).queryList();

    }



    public List<Customer> getCustomersWithoutGroup() {

        return SQLite.select().from(Customer.class).where(Customer_Table.customerGroup.isNull()).queryList();

    }
}
