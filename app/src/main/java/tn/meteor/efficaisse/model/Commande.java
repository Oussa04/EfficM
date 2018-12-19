package tn.meteor.efficaisse.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.converter.DateConverter;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


import tn.meteor.efficaisse.data.db.EfficaisseDatabase;
/**
 * Created by SKIIN on 28/01/2018.
 */

@Table(database = EfficaisseDatabase.class)
public class Commande extends BaseModel {

    @PrimaryKey(autoincrement = true)
    private Integer commandeNumber;

    @Column(typeConverter = DateConverter.class)
    private Date date;

    @Column
    private boolean status = true;

    @Column
    private String username;

    @Column
    private String customerCode;

    private Customer customer;


    private List<DetailCommande> products;
    private List<DetailCommandeIngredient> ingredients;

    private List<Payment> payments;


    public List<LoungeTable> getTableList() {
                return SQLite.select()
                .from(LoungeTable.class)
                .where(LoungeTable_Table.commande_number.eq(commandeNumber))
                .queryList();
    }



    @OneToMany(methods = {OneToMany.Method.ALL}, variableName = "payments")
    public List<Payment> getPayments() {
        if (payments==null || payments.isEmpty()  )
            payments = SQLite.select()
                    .from(Payment.class)
                    .where(Payment_Table.commande.invertProperty().eq(commandeNumber))
                    .queryList();

        return payments;
    }

    @OneToMany(methods = {OneToMany.Method.ALL}, variableName = "products")
    public List<DetailCommande> getProducts() {
        if (products==null || products.isEmpty()  )
        products = SQLite.select()
                .from(DetailCommande.class)
                .where(DetailCommande_Table.commandeNumber.eq(commandeNumber))
                .queryList();

        return products;
    }

    public List<DetailCommandeIngredient> getIngredients() {
        if (ingredients==null || ingredients.isEmpty()  )
            ingredients = SQLite.select()
                    .from(DetailCommandeIngredient.class)
                    .where(DetailCommandeIngredient_Table.commandeNumber.eq(commandeNumber))
                    .queryList();

        return ingredients;
    }

    public void setIngredients(List<DetailCommandeIngredient> ingredients) {
        this.ingredients = ingredients;
    }


    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public Customer getCustomer() {
        return SQLite.select().from(Customer.class).where(Customer_Table.code.eq(customerCode)).querySingle();
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }

    public void setProducts(List<DetailCommande> products) {
        this.products = products;
    }

    public Integer getCommandeNumber() {
        return commandeNumber;
    }

    public void setCommandeNumber(Integer commandeNumber) {
        this.commandeNumber = commandeNumber;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Commande()  {

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            this.date = sdf.parse(sdf.format(new Date()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public Commande(Integer number) {
        this.commandeNumber = number;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {

        return "Commande{" +
                "commandeNumber=" + commandeNumber +
                ", date=" + date +
                ", status=" + status +
                ", products=" + products +
                '}';
    }
}
