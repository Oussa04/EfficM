package tn.meteor.efficaisse.data.repository;

import android.database.Cursor;
import android.util.Log;

import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.raizlabs.android.dbflow.sql.language.Method;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import tn.meteor.efficaisse.data.db.LogSystem;
import tn.meteor.efficaisse.data.preferences.AppPreferencesHelper;
import tn.meteor.efficaisse.data.preferences.PreferencesHelper;
import tn.meteor.efficaisse.model.Commande;
import tn.meteor.efficaisse.model.Commande_Table;
import tn.meteor.efficaisse.model.Customer;
import tn.meteor.efficaisse.model.DetailCommande;
import tn.meteor.efficaisse.model.DetailCommandeIngredient;
import tn.meteor.efficaisse.model.Product;
import tn.meteor.efficaisse.model.Product_Ingredient;
import tn.meteor.efficaisse.model.Session;
import tn.meteor.efficaisse.utils.Constants;
import tn.meteor.efficaisse.utils.DateCommande;
import tn.meteor.efficaisse.utils.EfficaisseApplication;

/**
 * Created by ahmed on 01/28/18.
 */

public class DetailCommandeRepository {


    public Commande find(Commande parent) {
        return SQLite.select().from(Commande.class).where(Commande_Table.commandeNumber.eq(parent.getCommandeNumber())).querySingle();
    }


    public Commande find(int id) {
        return SQLite.select().from(Commande.class).where(Commande_Table.commandeNumber.eq(id)).querySingle();
    }

    public void delete(Commande parent) {
        Commande commande = find(parent);
        for (DetailCommande detail : commande.getProducts()) {
            detail.delete();
        }
        commande.delete();
    }

    public void delete(Commande parent, Product child) {
        Commande commande = find(parent);

        for (DetailCommande detail : commande.getProducts()) {
            if (detail.getProductId() == child.getId()) {
                detail.delete();
            }
        }
    }


    public List<Commande> findAll() {
        return SQLite.select().from(Commande.class).orderBy(Commande_Table.commandeNumber,false).queryList();
    }


    PreferencesHelper prefs = new AppPreferencesHelper(EfficaisseApplication.getInstance().getApplicationContext(), Constants.SHARED_PREFERENCES_NAME.PREFERENCES_USER);

    public List<Commande> findWithStatus(boolean status) {
        return SQLite.select().from(Commande.class).where(Commande_Table.status.is(status)).orderBy(Commande_Table.commandeNumber,false).queryList();
    }

    public void save(Commande parent, Customer customer) {
        List<DetailCommande> details = parent.getProducts();
        Log.d("commande", "save: " + details.size());

        parent.setUsername(prefs.getUsername());
        parent.setProducts(null);
        for (DetailCommande detail : details) {
            detail.setProductId(detail.getProduct().getId());

        }
        parent.save();
        LogSystem.persist(parent, "save", "Commande", false);
        for (DetailCommande detail : details) {
            detail.setProductName(detail.getProduct().getName());
            detail.setPrice((float) detail.getProduct().getDiscounted(customer));
            detail.setCommandeNumber(parent.getCommandeNumber());
            detail.save();
        }
        List<DetailCommandeIngredient> listIngredients = new ArrayList<>();
        for (DetailCommande detailCommande : details) {

            if (detailCommande.getProduct().getStockQuantity() == null || detailCommande.getProduct().getCost() == null) {
                for (Product_Ingredient product_ingredient : detailCommande.getProduct().getIngredients()) {

                    Optional<DetailCommandeIngredient> dci = Stream.of(listIngredients).filter(value -> value.getIngredientName().equals(product_ingredient.getIngredient().getName())).findFirst();
                    if (dci.isPresent()) {
                        dci.get().setQuantity(dci.get().getQuantity() + product_ingredient.getQuantity());
                    } else {
                        DetailCommandeIngredient d = new DetailCommandeIngredient();
                        d.setCommandeNumber(parent.getCommandeNumber());
                        d.setIngredient(product_ingredient.getIngredient());
                        d.setQuantity(product_ingredient.getQuantity());
                        listIngredients.add(d);

                    }
                    product_ingredient.getIngredient().setStockQuantity(product_ingredient.getIngredient().getStockQuantity() - product_ingredient.getQuantity() * detailCommande.getQuantity());
                    product_ingredient.getIngredient().update();
                }
            } else {
                detailCommande.getProduct().setStockQuantity(detailCommande.getProduct().getStockQuantity() - detailCommande.getQuantity());
                detailCommande.getProduct().update();
            }
        }
        for (DetailCommandeIngredient dci : listIngredients) {
            dci.save();
        }


    }


    public void save(List<Commande> parents, Customer customer) {
        for (Commande commande : parents) {
            save(commande, customer);
        }

    }


    public void updateCommande(Commande parent) {

        Commande commande = find(parent);
        for (DetailCommande detail : commande.getProducts()) {
            if (detail.getId() == 0) {
                detail.setCommandeNumber(parent.getCommandeNumber());
                detail.save();
            }
        }
        commande.update();


    }


    public static double getCommandeSum(Commande commande) {
        double sum = 0;
        for (DetailCommande detailCommande : commande.getProducts()) {
            sum = sum + detailCommande.getPrice() * detailCommande.getQuantity();
        }


        return sum;
    }

    public static double getCommandeSumdiscounted(Commande commande) {
        double sum = getCommandeSum(commande);
        if (commande.getCustomer() != null && commande.getCustomer().getCustomerGroup()!=null) {
            sum = sum - sum * (commande.getCustomer().getCustomerGroup().getDiscount() / 100);

        }
        return sum;
    }

    public int getCommandeQuantity(Commande commande) {
        int quantity = 0;
        for (DetailCommande detailCommande : commande.getProducts()) {
            quantity = quantity + detailCommande.getQuantity();
        }
        return quantity;

    }

    private Date getOldestCommande() {

        Cursor cursor = SQLite.select(Method.min(Commande_Table.date)).from(Commande.class).query();

        Long l = null;
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToNext();
            l = cursor.getLong(0);
            // Log.e("hellooooooo", cursor.getInt(0)+"");
        }

        Date d = null;
        if (l != null && l != 0)
            d = new Date(l);
        return d;
    }

    public static List<DateCommande> getCommandeGroupedBySession() throws ParseException {

        List<Session> sessions = SessionRepository.findAll();
        List<DateCommande> dateCommandeList = new ArrayList<>();


        for (Session session : sessions) {

            if (session.getClosed() == null) {
                session.setClosed(new Date());
            }

            List<Commande> commandeList = SQLite.select().from(Commande.class).where(Commande_Table.date.between(session.getDate()).and(session.getClosed())).orderBy(Commande_Table.commandeNumber,false).queryList();

            DateCommande dc = new DateCommande();
            dc.setCommandeList(commandeList);
            dc.setDate(session);
            dc.setSum(0);
            for (Commande commande : commandeList) {
                if (commande.isStatus()) {
                    dc.setSum(dc.getSum() + getCommandeSum(commande));
                }
            }
            dateCommandeList.add(dc);

        }

        return dateCommandeList;


    }


}
