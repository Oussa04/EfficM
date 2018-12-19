package tn.meteor.efficaisse.data.repository;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;
import java.util.List;

import tn.meteor.efficaisse.data.db.LogSystem;
import tn.meteor.efficaisse.model.CustomerGroup;
import tn.meteor.efficaisse.model.CustomerGroup_Table;

/**
 * Created by lilk on 10/03/2018.
 */

public class CustomerGroupRepository {


    public CustomerGroup find(CustomerGroup customerGroup) {
        return SQLite.select().from(CustomerGroup.class).where(CustomerGroup_Table.name.eq(customerGroup.getName())).querySingle();
    }

    public CustomerGroup find(String customerGroupName) {
        return SQLite.select().from(CustomerGroup.class).where(CustomerGroup_Table.name.eq(customerGroupName)).querySingle();
    }


    public void delete(CustomerGroup customerGroup) {
        CustomerGroup cat = find(customerGroup);
        if (cat != null) {
            cat.delete();
            LogSystem.persist(customerGroup, "delete", "CustomerGroup", false);

        }

    }


    public List<CustomerGroup> findAll() {
        List<CustomerGroup> customerGroups = new ArrayList<>();

        CustomerGroup customerGroup = new CustomerGroup();
        customerGroup.setDiscount(0);
        customerGroup.setName("- Sans Groupe -");
        customerGroups.add(customerGroup);
        customerGroups.addAll(SQLite.select().from(CustomerGroup.class).queryList());

        return customerGroups;

    }
    public List<CustomerGroup> findall() {

        return SQLite.select().from(CustomerGroup.class).queryList();

    }

    public void save(CustomerGroup customerGroup) {
        LogSystem.persist(customerGroup, "save", "CustomerGroup", false);
        customerGroup.save();
    }


    public void update(CustomerGroup customerGroup) {
        customerGroup.update();
        LogSystem.persist(customerGroup, "update", "CustomerGroup", false);


    }
}
