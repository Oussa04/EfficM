package tn.meteor.efficaisse.data.db;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import tn.meteor.efficaisse.data.preferences.AppPreferencesHelper;
import tn.meteor.efficaisse.data.preferences.PreferencesHelper;
import tn.meteor.efficaisse.data.repository.SyncRepository;
import tn.meteor.efficaisse.model.Cashier;
import tn.meteor.efficaisse.model.Category;
import tn.meteor.efficaisse.model.Commande;
import tn.meteor.efficaisse.model.ContreBon;
import tn.meteor.efficaisse.model.Customer;
import tn.meteor.efficaisse.model.CustomerGroup;
import tn.meteor.efficaisse.model.Discount;
import tn.meteor.efficaisse.model.History;
import tn.meteor.efficaisse.model.Ingredient;
import tn.meteor.efficaisse.model.LoungeTable;
import tn.meteor.efficaisse.model.Product;
import tn.meteor.efficaisse.model.Product_Ingredient;
import tn.meteor.efficaisse.model.Session;
import tn.meteor.efficaisse.model.Sync;
import tn.meteor.efficaisse.model.User;
import tn.meteor.efficaisse.utils.Constants;
import tn.meteor.efficaisse.utils.EfficaisseApplication;

/**
 * Created by SKIIN on 05/02/2018.
 */

public class LogSystem {


    public static String getClassName(Class c) {

        String ClassName = c.getName();
        int firstChar;
        firstChar = ClassName.lastIndexOf('.') + 1;
        if (firstChar > 0) {
            ClassName = ClassName.substring(firstChar);
        }
        return ClassName;
    }


    public static void persist(Object t, String type, String className,boolean upload) {

        PreferencesHelper prefs = new AppPreferencesHelper(EfficaisseApplication.getInstance().getApplicationContext(), Constants.SHARED_PREFERENCES_NAME.PREFERENCES_USER);
        String productClassName = "Product";
        String ingredientClassName = "Ingredient";
        String categorieClassName = "Category";
        String productIngredientClassName = "Product_Ingredient";
        String commandeClassName = "Commande";
        String historyClassName = "History";
        String contreBonClassName = "ContreBon";
        String customerClassName = "Customer";
        String customerGroupClassName = "CustomerGroup";
        String sessionClassName = "Session";
        String userClassName = "User";
        List<Sync> toDelete =new ArrayList<>();
        SyncRepository syncRepository = new SyncRepository();

        if (className.equals(productClassName)) {

            Product product = (Product) t;



            if(type.equals("delete")){

                toDelete.addAll(syncRepository.find(String.valueOf(product.getId()),prefs.getStoreRDC(),getClassName(t.getClass())));
                toDelete.addAll(syncRepository.findIngredientProductByProduct(String.valueOf(product.getId()),prefs.getStoreRDC()));
                if(syncRepository.find(String.valueOf(product.getId()),prefs.getStoreRDC(),"save",getClassName(t.getClass()))!= null){
                    for(Sync s : toDelete){
                        syncRepository.delete(s);
                    }
                    return;
                }
            }else if (type.equals("update")){
                Sync added  = syncRepository.find(String.valueOf(product.getId()),prefs.getStoreRDC(),"save",getClassName(t.getClass()));
                Sync modified  = syncRepository.find(String.valueOf(product.getId()),prefs.getStoreRDC(),"update",getClassName(t.getClass()));
                if(added!=null){
                    return;
                }
                if(modified != null){
                    if(modified.isUpload()){
                        return;
                    }else{
                        modified.delete();
                    }
                }
            }else{
                toDelete.addAll( syncRepository.find(String.valueOf(product.getId()),prefs.getStoreRDC(),getClassName(t.getClass())));
                toDelete.addAll(syncRepository.findIngredientProductByProduct(String.valueOf(product.getId()),prefs.getStoreRDC()));
            }
            for(Sync s : toDelete){
                syncRepository.delete(s);
            }
            syncRepository.save(new Sync(String.valueOf(product.getId()), getClassName(t.getClass()), type, upload, prefs.getStoreRDC()));


        } else if (className.equals(ingredientClassName)) {

            Ingredient ingredient = (Ingredient) t;
            if(type.equals("delete")){

                toDelete.addAll( syncRepository.find(String.valueOf(ingredient.getId()),prefs.getStoreRDC(),getClassName(t.getClass())));
                if(syncRepository.find(String.valueOf(ingredient.getId()),prefs.getStoreRDC(),"save",getClassName(t.getClass()))!= null){
                    for(Sync s : toDelete){
                        syncRepository.delete(s);
                    }
                    return;
                }
            }else if (type.equals("update")){
                Sync added  = syncRepository.find(String.valueOf(ingredient.getId()),prefs.getStoreRDC(),"save",getClassName(t.getClass()));
                Sync modified  = syncRepository.find(String.valueOf(ingredient.getId()),prefs.getStoreRDC(),"update",getClassName(t.getClass()));
                if(added!=null){
                    return;
                }
                if(modified != null){
                    if(modified.isUpload()){
                        return;
                    }else{
                        modified.delete();
                    }
                }
            }else{
                toDelete.addAll( syncRepository.find(String.valueOf(ingredient.getId()),prefs.getStoreRDC(),getClassName(t.getClass())));
            }
            for(Sync s : toDelete){
                syncRepository.delete(s);
            }

                syncRepository.save(new Sync(String.valueOf(ingredient.getId()), getClassName(ingredient.getClass()), type, upload, prefs.getStoreRDC()));


        } else if (className.equals(categorieClassName)) {

            Category category = (Category) t;

            for(Product p :category.getProducts()){
                persist(p,"delete","Product",false);
            }

            if(type.equals("delete")){

                toDelete.addAll( syncRepository.find(category.getName(),prefs.getStoreRDC(),getClassName(t.getClass())));
                if(syncRepository.find(category.getName(),prefs.getStoreRDC(),"save",getClassName(t.getClass()))!= null){
                    for(Sync s : toDelete){
                        syncRepository.delete(s);
                    }
                    return;
                }
            }else if (type.equals("update")){
                Sync added  = syncRepository.find(category.getName(),prefs.getStoreRDC(),"save",getClassName(t.getClass()));
                Sync modified  = syncRepository.find(category.getName(),prefs.getStoreRDC(),"update",getClassName(t.getClass()));
                if(added!=null){
                    return;
                }
                if(modified != null){
                    if(modified.isUpload()){
                        return;
                    }else{
                        modified.delete();
                    }
                }
            }
                syncRepository.save(new Sync(category.getName(), getClassName(category.getClass()), type, upload, prefs.getStoreRDC()));


        }
       else if(className.equals(productIngredientClassName)){

            String type1=type.concat("");
            Product_Ingredient product_ingredient = (Product_Ingredient)t;
            String id  = product_ingredient.getIngredient_id()+","+product_ingredient.getProduct_id();
            if(type.equals("delete")){

                toDelete.addAll( syncRepository.find(id,prefs.getStoreRDC(),getClassName(t.getClass())));
                if(syncRepository.find(id,prefs.getStoreRDC(),"save",getClassName(t.getClass()))!= null){
                    for(Sync s : toDelete){
                        syncRepository.delete(s);
                    }
                    return;
                }
            }else if (type.equals("update")){
                Sync added  = syncRepository.find(id,prefs.getStoreRDC(),"save",getClassName(t.getClass()));
                Sync modified  = syncRepository.find(id,prefs.getStoreRDC(),"update",getClassName(t.getClass()));
                if(added!=null){
                    return;
                }
                if(modified != null){
                    if(modified.isUpload()){
                        return;
                    }else{
                        modified.delete();
                    }
                }
            }else{
                toDelete.addAll( syncRepository.find(id,prefs.getStoreRDC(),getClassName(t.getClass())));
                if(!toDelete.isEmpty()){
                    type1="update";
                }
            }
            for(Sync s : toDelete){
                syncRepository.delete(s);
            }

            Sync added  = syncRepository.find(String.valueOf(product_ingredient.getProduct_id()),prefs.getStoreRDC(),"save","Product");
            if(added!=null){
                added.setDate(new Date());
                added.update();
                return;
            }
            syncRepository.save(new Sync(product_ingredient.getIngredient_id()+","+product_ingredient.getProduct_id(),getClassName(product_ingredient.getClass()),type1,false,prefs.getStoreRDC()));

        }else if(className.equals(commandeClassName)){
            Commande commande = (Commande) t;
            if (type.equals("update")){
                Sync modified  = syncRepository.find(String.valueOf(commande.getCommandeNumber()),prefs.getStoreRDC(),"update",getClassName(t.getClass()));

                if(modified != null){
                    return;
                }
            }

            syncRepository.save(new Sync(String.valueOf(commande.getCommandeNumber()), getClassName(commande.getClass()), type, upload, prefs.getStoreRDC()));
        }else if(className.equals(historyClassName)){
            History history = (History) t;
            syncRepository.save(new Sync(String.valueOf(history.getId()),historyClassName,type,upload,prefs.getStoreRDC()));
        }else if(className.equals(contreBonClassName)){
            ContreBon contreBon = (ContreBon) t;
            if (type.equals("update")){
                Sync added  = syncRepository.find(String.valueOf(contreBon.getId()),prefs.getStoreRDC(),"save",getClassName(t.getClass()));
                Sync modified = syncRepository.find(String.valueOf(contreBon.getId()),prefs.getStoreRDC(),"update",getClassName(t.getClass()));
                if(added != null ||modified!=null){
                    return;
                }
            }

            syncRepository.save(new Sync(String.valueOf(contreBon.getId()),contreBonClassName, type, upload, prefs.getStoreRDC()));
        }else if(className.equals(customerClassName)){
            Customer customer = (Customer) t;
            if (type.equals("update")){
                Sync added  = syncRepository.find(String.valueOf(customer.getCode()),prefs.getStoreRDC(),"save",getClassName(t.getClass()));
                Sync modified = syncRepository.find(String.valueOf(customer.getCode()),prefs.getStoreRDC(),"update",getClassName(t.getClass()));
                if(added != null ||modified!=null){
                    return;
                }
            }

            syncRepository.save(new Sync(String.valueOf(customer.getCode()),customerClassName, type, upload, prefs.getStoreRDC()));
        }else if(className.equals(customerGroupClassName)){
            CustomerGroup group = (CustomerGroup) t;
            if (type.equals("update")){
                Sync added  = syncRepository.find(group.getName(),prefs.getStoreRDC(),"save",getClassName(t.getClass()));
                Sync modified = syncRepository.find(String.valueOf(group.getName()),prefs.getStoreRDC(),"update",getClassName(t.getClass()));
                if(added != null ||modified!=null){
                    return;
                }
            }else{
                if (type.equals("delete")){
                    Sync added  = syncRepository.find(group.getName(),prefs.getStoreRDC(),"save",getClassName(t.getClass()));
                    Sync modified = syncRepository.find(group.getName(),prefs.getStoreRDC(),"update",getClassName(t.getClass()));
                    if(modified!=null){
                        modified.delete();
                    }
                    if(added != null){
                       added.delete();
                       return;
                    }
                }
            }

            syncRepository.save(new Sync(group.getName(),customerGroupClassName, type, upload, prefs.getStoreRDC()));
        }else if(className.equals(sessionClassName)){
            Session s = (Session) t;

            syncRepository.save(new Sync(s.getId()+"",sessionClassName, type, upload, prefs.getStoreRDC()));
        }
        else if(className.equals("Cashier")){
            Cashier s = (Cashier) t;

            syncRepository.save(new Sync(s.getUsername(),"Cashier", type, upload, prefs.getStoreRDC()));
        } else if(className.equals("Discount")){
            Discount s = (Discount) t;
            if(syncRepository.find(s.getId()+"",prefs.getStoreRDC(),"save","Discount") == null){
                syncRepository.save(new Sync(s.getId()+"" ,"Discount", type, upload, prefs.getStoreRDC()));
            }else if(type.equals("save")){
                syncRepository.save(new Sync(s.getId()+"" ,"Discount", type, upload, prefs.getStoreRDC()));
            }


        }else if(className.equals("LoungeTable")){
            LoungeTable lt = (LoungeTable) t;
            syncRepository.save(new Sync(lt.getId()+"" ,"LoungeTable", type, upload, prefs.getStoreRDC()));
        }
    }
}
