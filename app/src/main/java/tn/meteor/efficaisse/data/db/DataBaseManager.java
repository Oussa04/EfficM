package tn.meteor.efficaisse.data.db;

import android.graphics.Bitmap;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;
import retrofit2.HttpException;
import tn.meteor.efficaisse.data.DataManager;
import tn.meteor.efficaisse.data.network.CommandeConverterModel;
import tn.meteor.efficaisse.data.network.DiscountConverter;
import tn.meteor.efficaisse.data.network.IngredientModel;
import tn.meteor.efficaisse.data.network.IngredientProduct;
import tn.meteor.efficaisse.data.network.PaymentModel;
import tn.meteor.efficaisse.model.Cashier;
import tn.meteor.efficaisse.model.Cashier_Table;
import tn.meteor.efficaisse.model.Category;
import tn.meteor.efficaisse.model.Category_Table;
import tn.meteor.efficaisse.model.Commande;
import tn.meteor.efficaisse.model.Commande_Table;
import tn.meteor.efficaisse.model.ContreBon;
import tn.meteor.efficaisse.model.ContreBon_Table;
import tn.meteor.efficaisse.model.Customer;
import tn.meteor.efficaisse.model.CustomerGroup;
import tn.meteor.efficaisse.model.CustomerGroup_Table;
import tn.meteor.efficaisse.model.Customer_Table;
import tn.meteor.efficaisse.model.Discount;
import tn.meteor.efficaisse.model.Discount_Table;
import tn.meteor.efficaisse.model.ErrorSync;
import tn.meteor.efficaisse.model.History;
import tn.meteor.efficaisse.model.History_Table;
import tn.meteor.efficaisse.model.Ingredient;
import tn.meteor.efficaisse.model.Ingredient_Table;
import tn.meteor.efficaisse.model.LoungeTable;
import tn.meteor.efficaisse.model.LoungeTable_Table;
import tn.meteor.efficaisse.model.Payment;
import tn.meteor.efficaisse.model.Product;
import tn.meteor.efficaisse.model.Product_Ingredient;
import tn.meteor.efficaisse.model.Product_Ingredient_Table;
import tn.meteor.efficaisse.model.Product_Table;
import tn.meteor.efficaisse.model.Session;
import tn.meteor.efficaisse.model.Session_Table;
import tn.meteor.efficaisse.model.StackTrace;
import tn.meteor.efficaisse.model.Sync;
import tn.meteor.efficaisse.model.Sync_Table;
import tn.meteor.efficaisse.service.ServiceCategory;
import tn.meteor.efficaisse.service.ServiceCommande;
import tn.meteor.efficaisse.service.ServiceContreBon;
import tn.meteor.efficaisse.service.ServiceCustomer;
import tn.meteor.efficaisse.service.ServiceDiscount;
import tn.meteor.efficaisse.service.ServiceFactory;
import tn.meteor.efficaisse.service.ServiceHistory;
import tn.meteor.efficaisse.service.ServiceIngredient;
import tn.meteor.efficaisse.service.ServiceProduct;
import tn.meteor.efficaisse.service.ServiceProductIngredient;
import tn.meteor.efficaisse.service.ServiceSession;
import tn.meteor.efficaisse.service.ServiceStackTrace;
import tn.meteor.efficaisse.service.ServiceUser;
import tn.meteor.efficaisse.utils.CacheStore;
import tn.meteor.efficaisse.utils.Common;
import tn.meteor.efficaisse.utils.SyncPushListener;

/**
 * Created by SKIIN on 07/02/2018.
 */

public class DataBaseManager implements DataManager {


    private ServiceProduct serviceProduct;
    private ServiceIngredient serviceIngredient;
    private ServiceCategory serviceCategory;
    private ServiceProductIngredient serviceProductIngredient;
    private ServiceCommande serviceCommande;
    private ServiceHistory serviceHistory;
    private ServiceContreBon serviceContreBon;
    private ServiceCustomer serviceCustomer;
    private ServiceSession serviceSession;
    private ServiceUser serviceUser;
    private ServiceStackTrace serviceStackTrace;
    private ServiceDiscount serviceDiscount;
    private CacheStore cacheStore;
    private List<Sync> syncList;
    private static DataBaseManager dataBaseManager;
    private List<ErrorSync> listErrors;
    private List<History> history;
    private List<ContreBon> contreBons;
    private List<Sync> cBList;


    public static DataBaseManager getInstance() {
        if (dataBaseManager == null)
            dataBaseManager = new DataBaseManager();
        return dataBaseManager;
    }

    private DataBaseManager() {
        serviceProduct = ServiceFactory.getApiClient().create(ServiceProduct.class);
        serviceCategory = ServiceFactory.getApiClient().create(ServiceCategory.class);
        serviceProductIngredient = ServiceFactory.getApiClient().create(ServiceProductIngredient.class);
        serviceIngredient = ServiceFactory.getApiClient().create(ServiceIngredient.class);
        serviceCommande = ServiceFactory.getApiClient().create(ServiceCommande.class);
        serviceHistory = ServiceFactory.getApiClient().create(ServiceHistory.class);
        serviceContreBon = ServiceFactory.getApiClient().create(ServiceContreBon.class);
        serviceCustomer = ServiceFactory.getApiClient().create(ServiceCustomer.class);
        serviceSession = ServiceFactory.getApiClient().create(ServiceSession.class);
        serviceUser = ServiceFactory.getApiClient().create(ServiceUser.class);
        serviceDiscount = ServiceFactory.getApiClient().create(ServiceDiscount.class);
        serviceStackTrace = ServiceFactory.getApiClient().create(ServiceStackTrace.class);
        listErrors = new ArrayList<>();
        cacheStore = CacheStore.getInstance();
    }

    @Override
    public void clearData() {
        FlowManager.getDatabase(EfficaisseDatabase.class).reset();

    }

    @Override
    public Long logSize() {
        return SQLite.select().from(Sync.class).count();
    }

    @Override
    public void synchronizationData(SyncPushListener listener) {


        Common.changeProgressText("Syncronization En cours");
        syncList = SQLite.select().from(Sync.class).where(Sync_Table.entity.notEq("ContreBon")).and(Sync_Table.entity.notEq("LoungeTable")).orderBy(Sync_Table.date, true).queryList();
        history = new ArrayList<>();
        cBList = SQLite.select().from(Sync.class).where(Sync_Table.entity.eq("ContreBon")).orderBy(Sync_Table.date, true).queryList();


        listErrors.clear();
        syncContreBon(listener);



    }

    private void syncTable(SyncPushListener listener) {

        List<Sync> sl = SQLite.select().from(Sync.class).where(Sync_Table.entity.eq("LoungeTable")).queryList();
        List<LoungeTable> ltl = new ArrayList<>();

        for (Sync s : sl) {
            LoungeTable h = SQLite.select().from(LoungeTable.class).where(LoungeTable_Table.id.eq(Integer.valueOf(s.getIdReference()))).querySingle();
            if (h != null) {
                ltl.add(h);
            }

        }
        if (!ltl.isEmpty()) {
            serviceStackTrace.addTable(ltl, sl.get(0).getRegisterDC()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                    cn -> {
                        SQLite.delete().from(Sync.class).where(Sync_Table.entity.eq("LoungeTable")).execute();

                        syncStackTrace(listener);

                    },
                    e -> {
                        if (e instanceof HttpException)
                            listErrors.add(new ErrorSync(sl.get(0), ((HttpException) e).response().message()));
                        else
                            listErrors.add(new ErrorSync(sl.get(0), e.getMessage()));
                        syncStackTrace(listener);
                    }

            );
        } else {
            syncStackTrace(listener);
        }

    }

    private void syncContreBon(SyncPushListener listener) {

        contreBons = new ArrayList<>();
        for (Sync s : cBList) {
            ContreBon h = SQLite.select().from(ContreBon.class).where(ContreBon_Table.id.eq(Long.valueOf(s.getIdReference()))).querySingle();
            if (h != null) {
                contreBons.add(h);
            }

        }
        if (!contreBons.isEmpty()) {
            serviceContreBon.addContreBon(contreBons, cBList.get(0).getRegisterDC()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                    cn -> {
                        SQLite.delete().from(Sync.class).where(Sync_Table.entity.eq("ContreBon")).execute();

                        syncStackTrace(listener);

                    },
                    e -> {
                        if (e instanceof HttpException)
                            listErrors.add(new ErrorSync(cBList.get(0), ((HttpException) e).response().message()));
                        else
                            listErrors.add(new ErrorSync(cBList.get(0), e.getMessage()));
                        syncTable(listener);
                    }

            );
        } else {
            syncTable(listener);
        }

    }

    private void syncStackTrace(SyncPushListener listener) {

        List<StackTrace> stackTraceList = SQLite.select().from(StackTrace.class).queryList();
        if(!stackTraceList.isEmpty()){
            serviceStackTrace.addStackTrace(stackTraceList).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<JsonObject>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(JsonObject jsonObject) {
                        SQLite.delete().from(StackTrace.class).execute();
                        syncRec(0,listener);
                }

                @Override
                public void onError(Throwable e) {
                    if (e instanceof HttpException)
                        listErrors.add(new ErrorSync(null, ((HttpException) e).response().message()));
                    else
                        listErrors.add(new ErrorSync(null, e.getMessage()));
                    syncRec(0,listener);
                }

                @Override
                public void onComplete() {

                }
            });

        }else{
            syncRec(0, listener);
        }

    }
    private void syncRec(int index, SyncPushListener listener) {
        if (index >= syncList.size()) {
            listener.onSyncPushFinished(listErrors);
        } else {
            Sync sync = syncList.get(index);
            if (sync.getEntity().equals("Category")) {
                syncCat(index, listener);

            }
            if (sync.getEntity().equals("Ingredient")) {
                syncIngredient(index, listener);
            }
            if (sync.getEntity().equals("Product")) {
                syncProduct(index, listener);
            }
            if (sync.getEntity().equals("Product_Ingredient")) {
                syncIP(index, listener);
            }
            if (sync.getEntity().equals("Commande")) {
                syncCommande(index, listener);
            }
            if (sync.getEntity().equals("History")) {
                syncHistory(index, listener);
            }
            if (sync.getEntity().equals("CustomerGroup")) {
                syncCustomerGroup(index, listener);
            }
            if (sync.getEntity().equals("Customer")) {
                syncCustomer(index, listener);
            }
            if (sync.getEntity().equals("Cashier")) {
                syncPatternUser(index, listener);
            }
            if(sync.getEntity().equals("Session")){
                syncSession(index,listener);
            }if(sync.getEntity().equals("Discount")){
                syncDiscount(index,listener);
            }
        }

    }

    private void syncHistory(int index, SyncPushListener listener) {
        History h = SQLite.select().from(History.class).where(History_Table.id.eq(Long.valueOf(syncList.get(index).getIdReference()))).querySingle();
        history = new ArrayList<>();
        history.add(h);
        serviceHistory.addHistory(history, syncList.get(index).getRegisterDC()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                cn -> {
                    syncList.get(index).delete();
                    syncRec(index + 1, listener);

                },
                e -> {
                    if (e instanceof HttpException)
                        listErrors.add(new ErrorSync(syncList.get(index), ((HttpException) e).response().message()));
                    else
                        listErrors.add(new ErrorSync(syncList.get(index), e.getMessage()));
                    syncRec(index + 1, listener);
                }

        );

    }

    private void syncPatternUser(int index, SyncPushListener listener) {
        Sync sync = syncList.get(index);
        Cashier user = SQLite.select().from(Cashier.class).where(Cashier_Table.username.eq(syncList.get(index).getIdReference())).querySingle();
        if (user == null) {
            listErrors.add(new ErrorSync(syncList.get(index), "User not Found"));
            syncRec(index + 1, listener);

        } else {
            if (sync.getType().equals("save")){
                Observable<JsonObject> addCashier = serviceUser.addCashier(user);
                addCashier.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(r->{
                    sync.delete();
                    syncRec(index + 1, listener);


                },e->{

                    if (e instanceof HttpException)
                        listErrors.add(new ErrorSync(syncList.get(index), ((HttpException) e).response().errorBody().string()));
                    else
                        listErrors.add(new ErrorSync(syncList.get(index), e.getMessage()));
                    syncRec(index + 1, listener);
                });
            }else{
                serviceUser.changePatten(user.getPattern(), user.getUsername(),syncList.get(index).getRegisterDC()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                        cn -> {
                            sync.delete();
                            syncRec(index + 1, listener);

                        },
                        e -> {
                            if (e instanceof HttpException)
                                listErrors.add(new ErrorSync(syncList.get(index), ((HttpException) e).response().errorBody().string()));
                            else
                                listErrors.add(new ErrorSync(syncList.get(index), e.getMessage()));
                            syncRec(index + 1, listener);
                        }

                );
            }


        }


    }
    private void syncDiscount(int index,SyncPushListener listener){

        Observable<JsonObject> request;

        Discount discount = SQLite.select().from(Discount.class).where(Discount_Table.id.eq(Integer.valueOf(syncList.get(index).getIdReference()))).querySingle();
        if(syncList.get(index).getType().equals("save")){
            request = serviceDiscount.addDiscount(DiscountConverter.fromDbModel(discount));
        }else{
            request = serviceDiscount.updateDiscount(DiscountConverter.fromDbModel(discount));
        }

        request.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<JsonObject>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(JsonObject jsonObject) {
                syncList.get(index).delete();
                syncRec(index+1,listener);

            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpException) {
                    listErrors.add(new ErrorSync(syncList.get(index), ((HttpException) e).response().message()));

                }else
                    listErrors.add(new ErrorSync(syncList.get(index), e.getMessage()));

                syncRec(index+1,listener);
            }

            @Override
            public void onComplete() {

            }
        });


    }
    private void syncSession(int index, SyncPushListener listener) {
        Sync sync = syncList.get(index);
        Session session = SQLite.select().from(Session.class).where(Session_Table.id.eq(Integer.valueOf(sync.getIdReference()))).querySingle();
        Observable<JsonObject> addSessions = serviceSession.addSessions(session, sync.getRegisterDC());
        addSessions.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(jsonObject -> {
            syncList.get(index).delete();
            syncRec(index+1,listener);
        },e -> {
            if (e instanceof HttpException) {
                listErrors.add(new ErrorSync(sync, ((HttpException) e).response().message()));

            }else
                listErrors.add(new ErrorSync(sync, e.getMessage()));

            syncRec(index+1,listener);

        }
    );


}


    private void syncCommande(int index, SyncPushListener listener) {
        Sync sync = syncList.get(index);
        Commande commande = SQLite.select().from(Commande.class).where(Commande_Table.commandeNumber.eq(Integer.valueOf(sync.getIdReference()))).querySingle();
        if (sync.getType().equals("save")) {


            Observable<JsonObject> addCommande = serviceCommande.addCommande(commande.getUsername(), CommandeConverterModel.fromDbModel(commande), commande.getCommandeNumber(), sync.getRegisterDC());
            addCommande.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                    cn -> {


                        sync.delete();
                        syncRec(index + 1, listener);


                    },
                    e -> {
                        if (e instanceof HttpException)
                            listErrors.add(new ErrorSync(sync, ((HttpException) e).response().message()));
                        else
                            listErrors.add(new ErrorSync(sync, e.getMessage()));
                        syncRec(index + 1, listener);
                    }

            );
        } else {
            List<PaymentModel> listPM = new ArrayList<>();
            for (Payment p : commande.getPayments()) {
                listPM.add(PaymentModel.fromDbModel(p, sync.getRegisterDC()));
            }
            Observable<JsonObject> addPayment = serviceCommande.addPayment(listPM, sync.getRegisterDC(), commande.getCommandeNumber());
            addPayment.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                    cn -> {


                        sync.delete();
                        syncRec(index + 1, listener);


                    },
                    e -> {
                        if (e instanceof HttpException)
                            listErrors.add(new ErrorSync(sync, ((HttpException) e).response().message()));
                        else
                            listErrors.add(new ErrorSync(sync, e.getMessage()));
                        syncRec(index + 1, listener);
                    }

            );
        }
    }

    private void syncIP(int index, SyncPushListener listener) {
        Sync sync = syncList.get(index);
        int productId = Integer.valueOf(sync.getIdReference().split(",")[1]);
        int ingredientId = Integer.valueOf(sync.getIdReference().split(",")[0]);
        Product_Ingredient pi = SQLite.select().from(Product_Ingredient.class).where(Product_Ingredient_Table.ingredient_id.eq(ingredientId)).and(Product_Ingredient_Table.product_id.eq(productId)).querySingle();
        if (sync.getType().equals("save")) {
            List<IngredientProduct> ip = new ArrayList<>();
            ip.add(IngredientProduct.fromDbModel(pi));
            Observable<JsonObject> addPI = serviceProductIngredient.addIngredientProducts(ip, sync.getRegisterDC());

            addPI.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                    cn -> {


                        sync.delete();
                        syncRec(index + 1, listener);


                    },
                    e -> {
                        if (e instanceof HttpException)
                            listErrors.add(new ErrorSync(sync, ((HttpException) e).response().message()));
                        else
                            listErrors.add(new ErrorSync(sync, e.getMessage()));
                        syncRec(index + 1, listener);
                    }

            );
        } else if (sync.getType().equals("delete")) {
            Product_Ingredient ip = new Product_Ingredient();
            ip.setQuantity(0);
            ip.setProduct_id(productId);
            ip.setIngredient_id(ingredientId);

            Observable<JsonObject> deletePI = serviceProductIngredient.deleteIngredientProducts(IngredientProduct.fromDbModel(ip), sync.getRegisterDC());

            deletePI.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                    cn -> {


                        sync.delete();
                        syncRec(index + 1, listener);


                    },
                    e -> {
                        if (e instanceof HttpException)
                            listErrors.add(new ErrorSync(sync, ((HttpException) e).response().message()));
                        else
                            listErrors.add(new ErrorSync(sync, e.getMessage()));
                        syncRec(index + 1, listener);
                    }

            );
        } else {
            Observable<JsonObject> updatePI = serviceProductIngredient.updateIngredientProducts(IngredientProduct.fromDbModel(pi), sync.getRegisterDC());

            updatePI.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                    cn -> {


                        sync.delete();
                        syncRec(index + 1, listener);


                    },
                    e -> {
                        if (e instanceof HttpException)
                            listErrors.add(new ErrorSync(sync, ((HttpException) e).response().message()));
                        else
                            listErrors.add(new ErrorSync(sync, e.getMessage()));
                        syncRec(index + 1, listener);
                    }

            );
        }

    }

    private void syncProduct(int index, SyncPushListener listener) {
        Sync sync = syncList.get(index);
        Product product = SQLite.select().from(Product.class).where(Product_Table.id.eq(Integer.parseInt(sync.getIdReference()))).querySingle();
        RequestBody cost = null, stock = null;
        if (product != null && product.getCost() != null) {
            cost = Common.toRequestBody(product.getCost());
        }
        if (product != null && product.getStockQuantity() != null) {
            stock = Common.toRequestBody(product.getStockQuantity());
        }
        if (sync.getType().equals("save")) {
            if (SQLite.select().from(Sync.class).where(Sync_Table.type.eq("save")).and(Sync_Table.entity.eq("Category")).querySingle() == null) {


                Observable<JsonObject> addProduct = serviceProduct.addProduct(Common.toMutliPartBody(product.getPhoto()), Common.toRequestBody(product.getName()), Common.toRequestBody(product.getCategory().getName()), Common.toRequestBody(Boolean.valueOf(product.isFavoris())), cost, stock, Common.toRequestBody(Double.valueOf(product.getPrice())),
                        Common.toRequestBody(Integer.valueOf(product.getId())), Common.toRequestBody(sync.getRegisterDC()));
                addProduct.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                        cn -> {

                            Gson gson = new Gson();
                            IngredientModel cat = gson.fromJson(cn, IngredientModel.class);
                            Bitmap bm = cacheStore.getCacheFile(product.getPhoto());

                            cacheStore.deleteCacheFile(product.getPhoto());
                            cacheStore.saveCacheFile(cat.photo, bm);

                            product.setPhoto(cat.photo);
                            product.update();
                            if (!product.getIngredients().isEmpty()) {
                                List<IngredientProduct> ip = new ArrayList<>();
                                for (Product_Ingredient pi : product.getIngredients()) {
                                    ip.add(IngredientProduct.fromDbModel(pi));
                                }
                                Observable<JsonObject> addProductIngredient = serviceProductIngredient.addIngredientProducts(ip, sync.getRegisterDC());
                                addProductIngredient.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(cnx -> {

                                    sync.delete();
                                    syncRec(index + 1, listener);
                                }, e -> {
                                    if (e instanceof HttpException)
                                        listErrors.add(new ErrorSync(sync, ((HttpException) e).response().message()));
                                    else
                                        listErrors.add(new ErrorSync(sync, e.getMessage()));
                                    syncRec(index + 1, listener);
                                });
                            } else {

                                sync.delete();
                                syncRec(index + 1, listener);

                            }

                        },
                        e -> {
                            if (e instanceof HttpException)
                                listErrors.add(new ErrorSync(sync, ((HttpException) e).response().message()));
                            else
                                listErrors.add(new ErrorSync(sync, e.getMessage()));
                            syncRec(index + 1, listener);
                        }

                );
            } else {

                listErrors.add(new ErrorSync(sync, "category not found"));
                syncRec(index + 1, listener);
            }
        } else if (sync.getType().equals("delete")) {

            Observable<JsonObject> deleteIngredient = serviceProduct.deleteProduct(Integer.valueOf(sync.getIdReference()), sync.getRegisterDC());
            deleteIngredient.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                    cn -> {

                        sync.delete();
                        syncRec(index + 1, listener);


                    },
                    e -> {
                        if (e instanceof HttpException)
                            listErrors.add(new ErrorSync(sync, ((HttpException) e).response().message()));
                        else
                            listErrors.add(new ErrorSync(sync, e.getMessage()));
                        syncRec(index + 1, listener);
                    }

            );
        } else {
            if (product.getCost() != null) {
                cost = Common.toRequestBody(product.getCost());
            }
            if (product.getStockQuantity() != null) {
                cost = Common.toRequestBody(product.getStockQuantity());
            }
            if (sync.isUpload()) {
                Observable<JsonObject> updateProduct = serviceProduct.updateProduct(Common.toMutliPartBody(product.getPhoto()), Common.toRequestBody(product.getName()), Common.toRequestBody(Boolean.valueOf(product.isFavoris())), cost, stock, Common.toRequestBody(Double.valueOf(product.getPrice())),
                        Common.toRequestBody(Integer.valueOf(product.getId())), Common.toRequestBody(sync.getRegisterDC()));

                updateProduct.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                        cn -> {

                            Gson gson = new Gson();
                            IngredientModel cat = gson.fromJson(cn, IngredientModel.class);
                            Bitmap bm = cacheStore.getCacheFile(product.getPhoto());
                            cacheStore.deleteCacheFile(product.getPhoto());
                            cacheStore.saveCacheFile(cat.photo, bm);

                            product.setPhoto(cat.photo);
                            product.update();
                            sync.delete();
                            syncRec(index + 1, listener);


                        },
                        e -> {
                            if (e instanceof HttpException)
                                listErrors.add(new ErrorSync(sync, ((HttpException) e).response().message()));
                            else
                                listErrors.add(new ErrorSync(sync, e.getMessage()));
                            syncRec(index + 1, listener);
                        }

                );
            } else {
                Observable<JsonObject> updateProduct = serviceProduct.updateProduct(Common.toMutliPartBody(null), Common.toRequestBody(product.getName()), Common.toRequestBody(Boolean.valueOf(product.isFavoris())), cost, stock, Common.toRequestBody(Double.valueOf(product.getPrice())),
                        Common.toRequestBody(Integer.valueOf(product.getId())), Common.toRequestBody(sync.getRegisterDC()));

                updateProduct.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                        cn -> {

                            sync.delete();
                            syncRec(index + 1, listener);


                        },
                        e -> {
                            if (e instanceof HttpException)
                                listErrors.add(new ErrorSync(sync, ((HttpException) e).response().message()));
                            else
                                listErrors.add(new ErrorSync(sync, e.getMessage()));
                            syncRec(index + 1, listener);
                        }

                );
            }

        }
    }

    private void syncIngredient(int index, SyncPushListener listener) {
        Sync sync = syncList.get(index);
        Ingredient ingredient = SQLite.select().from(Ingredient.class).where(Ingredient_Table.id.eq(Integer.parseInt(sync.getIdReference()))).querySingle();
        if (sync.getType().equals("save")) {
            Observable<JsonObject> addIngredient = serviceIngredient.addIngredient(Common.toMutliPartBody(ingredient.getPhoto()), Common.toRequestBody(ingredient.getName()), Common.toRequestBody(Double.valueOf(ingredient.getPrice())), Common.toRequestBody(Double.valueOf(ingredient.getStockQuantity())),
                    Common.toRequestBody(ingredient.getUnit().toString()), Common.toRequestBody(Integer.valueOf(ingredient.getId())), Common.toRequestBody(sync.getRegisterDC()));
            addIngredient.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                    cn -> {

                        Gson gson = new Gson();
                        IngredientModel cat = gson.fromJson(cn, IngredientModel.class);

                        Bitmap bm = cacheStore.getCacheFile(ingredient.getPhoto());
                        cacheStore.deleteCacheFile(ingredient.getPhoto());
                        cacheStore.saveCacheFile(cat.photo, bm);

                        ingredient.setPhoto(cat.photo);
                        ingredient.update();
                        sync.delete();
                        syncRec(index + 1, listener);


                    },
                    e -> {
                        if (e instanceof HttpException)
                            listErrors.add(new ErrorSync(sync, ((HttpException) e).response().message()));
                        else
                            listErrors.add(new ErrorSync(sync, e.getMessage()));

                        syncRec(index + 1, listener);
                    }

            );
        } else if (sync.getType().equals("delete")) {

            Observable<JsonObject> deleteIngredient = serviceIngredient.deleteIngredient(Integer.valueOf(sync.getIdReference()), sync.getRegisterDC());
            deleteIngredient.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                    cn -> {

                        sync.delete();
                        syncRec(index + 1, listener);


                    },
                    e -> {
                        if (e instanceof HttpException)
                            listErrors.add(new ErrorSync(sync, ((HttpException) e).response().message()));
                        else
                            listErrors.add(new ErrorSync(sync, e.getMessage()));
                        syncRec(index + 1, listener);
                    }

            );
        } else {
            if (sync.isUpload()) {
                Observable<JsonObject> updateIngredient = serviceIngredient.updateIngredient(Common.toMutliPartBody(ingredient.getPhoto()), Common.toRequestBody(ingredient.getName())
                        , Common.toRequestBody(Double.valueOf(ingredient.getPrice())), Common.toRequestBody(Double.valueOf(ingredient.getStockQuantity())), Common.toRequestBody(ingredient.getUnit().toString())
                        , Common.toRequestBody(Integer.valueOf(ingredient.getId())), Common.toRequestBody(sync.getRegisterDC()));

                updateIngredient.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                        cn -> {

                            Gson gson = new Gson();
                            IngredientModel cat = gson.fromJson(cn, IngredientModel.class);

                            Bitmap bm = cacheStore.getCacheFile(ingredient.getPhoto());
                            cacheStore.deleteCacheFile(ingredient.getPhoto());
                            cacheStore.saveCacheFile(cat.photo, bm);
                            ingredient.setPhoto(cat.photo);
                            ingredient.update();
                            sync.delete();
                            syncRec(index + 1, listener);


                        },
                        e -> {
                            if (e instanceof HttpException)
                                listErrors.add(new ErrorSync(sync, ((HttpException) e).response().message()));
                            else
                                listErrors.add(new ErrorSync(sync, e.getMessage()));
                            syncRec(index + 1, listener);
                        }

                );
            } else {
                Observable<JsonObject> updateIngredient = serviceIngredient.updateIngredient(Common.toMutliPartBody(null), Common.toRequestBody(ingredient.getName())
                        , Common.toRequestBody(Double.valueOf(ingredient.getPrice())), Common.toRequestBody(Double.valueOf(ingredient.getStockQuantity())), Common.toRequestBody(ingredient.getUnit().toString())
                        , Common.toRequestBody(Integer.valueOf(ingredient.getId())), Common.toRequestBody(sync.getRegisterDC()));

                updateIngredient.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                        cn -> {

                            sync.delete();
                            syncRec(index + 1, listener);


                        },
                        e -> {
                            if (e instanceof HttpException)
                                listErrors.add(new ErrorSync(sync, ((HttpException) e).response().message()));
                            else
                                listErrors.add(new ErrorSync(sync, e.getMessage()));
                            syncRec(index + 1, listener);
                        }

                );
            }

        }
    }

    private void syncCat(int index, SyncPushListener listener) {
        Sync sync = syncList.get(index);
        Category category = SQLite.select().from(Category.class).where(Category_Table.name.eq(sync.getIdReference())).querySingle();
        if (sync.getType().equals("save")) {
            Observable<JsonObject> addCategory = serviceCategory.addCategory(Common.toMutliPartBody(category.getPhoto()), Common.toRequestBody(category.getName()), Common.toRequestBody(sync.getRegisterDC()));
            addCategory.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                    cn -> {

                        Gson gson = new Gson();
                        IngredientModel cat = gson.fromJson(cn, IngredientModel.class);

                        Bitmap bm = cacheStore.getCacheFile(category.getPhoto());
                        cacheStore.deleteCacheFile(category.getPhoto());
                        cacheStore.saveCacheFile(cat.photo, bm);
                        category.setPhoto(cat.photo);
                        category.update();
                        sync.delete();
                        syncRec(index + 1, listener);


                    },
                    e -> {
                        if (e instanceof HttpException)
                            listErrors.add(new ErrorSync(sync, ((HttpException) e).response().message()));
                        else
                            listErrors.add(new ErrorSync(sync, e.getMessage()));
                        syncRec(index + 1, listener);
                    }

            );
        } else if (sync.getType().equals("delete")) {

            Observable<JsonObject> deleteCategory = serviceCategory.deleteCategory(sync.getIdReference(), sync.getRegisterDC());
            deleteCategory.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                    cn -> {

                        sync.delete();
                        syncRec(index + 1, listener);


                    },
                    e -> {
                        if (e instanceof HttpException)
                            listErrors.add(new ErrorSync(sync, ((HttpException) e).response().message()));
                        else
                            listErrors.add(new ErrorSync(sync, e.getMessage()));
                        syncRec(index + 1, listener);
                    }

            );
        } else {
            Observable<JsonObject> updateCategory = serviceCategory.updateCategory(category.getName(), Common.toMutliPartBody(category.getPhoto()), Common.toRequestBody(sync.getRegisterDC()));
            updateCategory.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                    cn -> {

                        Gson gson = new Gson();
                        IngredientModel cat = gson.fromJson(cn, IngredientModel.class);

                        Bitmap bm = cacheStore.getCacheFile(category.getPhoto());
                        cacheStore.deleteCacheFile(category.getPhoto());
                        cacheStore.saveCacheFile(cat.photo, bm);
                        category.setPhoto(cat.photo);
                        category.update();
                        sync.delete();
                        syncRec(index + 1, listener);


                    },
                    e -> {
                        if (e instanceof HttpException)
                            listErrors.add(new ErrorSync(sync, ((HttpException) e).response().message()));
                        else
                            listErrors.add(new ErrorSync(sync, e.getMessage()));
                        syncRec(index + 1, listener);
                    }

            );
        }

    }

    private void syncCustomerGroup(int index, SyncPushListener listener) {
        Sync sync = syncList.get(index);
        CustomerGroup cg = SQLite.select().from(CustomerGroup.class).where(CustomerGroup_Table.name.eq(sync.getIdReference())).querySingle();
        if (sync.getType().equals("save")) {
            Observable<JsonObject> addCustomerGroup = serviceCustomer.addCustomerGroup(cg, sync.getRegisterDC());
            addCustomerGroup.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                    cn -> {


                        sync.delete();
                        syncRec(index + 1, listener);


                    },
                    e -> {
                        if (e instanceof HttpException)
                            listErrors.add(new ErrorSync(sync, ((HttpException) e).response().message()));
                        else
                            listErrors.add(new ErrorSync(sync, e.getMessage()));
                        syncRec(index + 1, listener);
                    }

            );
        } else if (sync.getType().equals("delete")) {

            Observable<JsonObject> deleteCustomerGroup = serviceCustomer.deleteCustomerGroup(sync.getIdReference(), sync.getRegisterDC());
            deleteCustomerGroup.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                    cn -> {

                        sync.delete();
                        syncRec(index + 1, listener);


                    },
                    e -> {
                        if (e instanceof HttpException)
                            listErrors.add(new ErrorSync(sync, ((HttpException) e).response().message()));
                        else
                            listErrors.add(new ErrorSync(sync, e.getMessage()));
                        syncRec(index + 1, listener);
                    }

            );
        } else {
            Observable<JsonObject> updateCustomerGroup = serviceCustomer.updateCustomerGroup(cg, sync.getRegisterDC());
            updateCustomerGroup.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                    cn -> {


                        sync.delete();
                        syncRec(index + 1, listener);


                    },
                    e -> {
                        if (e instanceof HttpException)
                            listErrors.add(new ErrorSync(sync, ((HttpException) e).response().message()));
                        else
                            listErrors.add(new ErrorSync(sync, e.getMessage()));
                        syncRec(index + 1, listener);
                    }

            );
        }

    }

    private void syncCustomer(int index, SyncPushListener listener) {
        Sync sync = syncList.get(index);
        Customer customer = SQLite.select().from(Customer.class).where(Customer_Table.code.eq(sync.getIdReference())).querySingle();
        if (sync.getType().equals("save")) {
            Observable<JsonObject> addCustomer;
            if (customer.getCustomerGroup() != null)
                addCustomer = serviceCustomer.addCustomer(customer, customer.getCustomerGroup().getName(), sync.getRegisterDC());
            else
                addCustomer = serviceCustomer.addCustomer(customer, null, sync.getRegisterDC());
            addCustomer.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                    cn -> {


                        sync.delete();
                        syncRec(index + 1, listener);


                    },
                    e -> {
                        if (e instanceof HttpException)
                            listErrors.add(new ErrorSync(sync, ((HttpException) e).response().message()));
                        else
                            listErrors.add(new ErrorSync(sync, e.getMessage()));
                        syncRec(index + 1, listener);
                    }

            );
        } else {
            Observable<JsonObject> updateCustomer;
            if (customer.getCustomerGroup() != null)
                updateCustomer = serviceCustomer.updateCustomer(customer, customer.getCustomerGroup().getName(), sync.getRegisterDC());
            else
                updateCustomer = serviceCustomer.updateCustomer(customer, null, sync.getRegisterDC());
            updateCustomer.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                    cn -> {


                        sync.delete();
                        syncRec(index + 1, listener);


                    },
                    e -> {
                        if (e instanceof HttpException)
                            listErrors.add(new ErrorSync(sync, ((HttpException) e).response().message()));
                        else
                            listErrors.add(new ErrorSync(sync, e.getMessage()));
                        syncRec(index + 1, listener);
                    }

            );
        }

    }
}
