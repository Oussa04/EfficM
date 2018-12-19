package tn.meteor.efficaisse.data.network;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import tn.meteor.efficaisse.data.DataManager;
import tn.meteor.efficaisse.model.Commande;
import tn.meteor.efficaisse.model.Customer;
import tn.meteor.efficaisse.model.CustomerGroup;
import tn.meteor.efficaisse.model.DetailCommandeIngredient;

/**
 * Created by ahmed on 03/11/18.
 */

public class CustomerGroupConverter {
    private String name;
    private float discount;
    private List<CustomerConverter> customers;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getDiscount() {
        return discount;
    }

    public void setDiscount(float discount) {
        this.discount = discount;
    }

    public List<CustomerConverter> getCustomers() {
        return customers;
    }

    public void setCustomers(List<CustomerConverter> customers) {
        this.customers = customers;
    }

    public CustomerGroup fromDbModel() {
        CustomerGroup cg = new CustomerGroup();
        cg.setDiscount(discount);
        cg.setName(name);
        cg.setCustomers(new ArrayList<>());
        for (CustomerConverter customer :
                customers) {
            Customer c = customer.fromDbModel();
            c.setCustomerGroup(cg);
            cg.getCustomers().add(c);
        }
        return cg;

    }


    public static class CustomerConverter {
        private String name;
        private String email;
        private String phone;
        private String code;
        private Date joinDate;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public Date getJoinDate() {
            return joinDate;
        }

        public void setJoinDate(Date joinDate) {
            this.joinDate = joinDate;
        }
        public static CustomerConverter toDbModel(Customer cs) {
            CustomerConverter c = new CustomerConverter();
            c.setCode(cs.getCode());
            c.setEmail(cs.getEmail());
            c.setJoinDate(cs.getJoinDate());
            c.setPhone(cs.getPhone());
            c.setName(cs.getName());
            return c;


        }
        public Customer fromDbModel() {
            Customer c = new Customer();
            c.setCode(code);
            c.setEmail(email);
            c.setJoinDate(joinDate);
            c.setPhone(phone);
            c.setName(name);
            return c;


        }

    }
}
