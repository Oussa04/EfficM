package tn.meteor.efficaisse.data.db;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import tn.meteor.efficaisse.data.network.CategoryConverterModel;
import tn.meteor.efficaisse.data.network.CommandeConverterModel;
import tn.meteor.efficaisse.data.network.ContreBonConverter;
import tn.meteor.efficaisse.data.network.CustomerGroupConverter;
import tn.meteor.efficaisse.data.network.DiscountConverter;
import tn.meteor.efficaisse.data.network.HistoryModel;
import tn.meteor.efficaisse.data.network.IngredientConverterModel;
import tn.meteor.efficaisse.data.network.ProductConverterModel;
import tn.meteor.efficaisse.data.network.SessionConverter;
import tn.meteor.efficaisse.model.Category;
import tn.meteor.efficaisse.model.Commande;
import tn.meteor.efficaisse.model.Customer;
import tn.meteor.efficaisse.model.CustomerGroup;
import tn.meteor.efficaisse.model.DetailCommande;
import tn.meteor.efficaisse.model.DetailCommandeIngredient;
import tn.meteor.efficaisse.model.Discount;
import tn.meteor.efficaisse.model.Discount_Group;
import tn.meteor.efficaisse.model.Discount_Product;
import tn.meteor.efficaisse.model.Ingredient;
import tn.meteor.efficaisse.model.LoungeTable;
import tn.meteor.efficaisse.model.Payment;
import tn.meteor.efficaisse.model.Product;
import tn.meteor.efficaisse.model.Product_Ingredient;
import tn.meteor.efficaisse.service.ServiceCategory;
import tn.meteor.efficaisse.service.ServiceCommande;
import tn.meteor.efficaisse.service.ServiceContreBon;
import tn.meteor.efficaisse.service.ServiceCustomer;
import tn.meteor.efficaisse.service.ServiceDiscount;
import tn.meteor.efficaisse.service.ServiceFactory;
import tn.meteor.efficaisse.service.ServiceHistory;
import tn.meteor.efficaisse.service.ServiceIngredient;
import tn.meteor.efficaisse.service.ServiceProduct;
import tn.meteor.efficaisse.service.ServiceSession;
import tn.meteor.efficaisse.service.ServiceStackTrace;
import tn.meteor.efficaisse.utils.CacheStore;
import tn.meteor.efficaisse.utils.Common;
import tn.meteor.efficaisse.utils.Constants;
import tn.meteor.efficaisse.utils.SyncPullListener;

/**
 * Created by ahmed on 02/15/18.
 */

public class PullDb {

    private ServiceProduct serviceProduct;
    private ServiceCategory serviceCategory;
    private ServiceIngredient serviceIngredient;
    private CacheStore cacheStore;
    private ServiceCommande serviceCommande;
    private ServiceCustomer serviceCustomer;
    private ServiceHistory serviceHistory;
    private ServiceContreBon serviceContreBon;
    private ServiceSession serviceSession;
    private ServiceDiscount serviceDiscount;
    private ServiceStackTrace serviceStackTrace;
    private static PullDb pullDb;

    private PullDb() {
        serviceProduct = ServiceFactory.getApiClient().create(ServiceProduct.class);
        serviceCategory = ServiceFactory.getApiClient().create(ServiceCategory.class);
        serviceIngredient = ServiceFactory.getApiClient().create(ServiceIngredient.class);
        serviceCommande = ServiceFactory.getApiClient().create(ServiceCommande.class);
        serviceHistory = ServiceFactory.getApiClient().create(ServiceHistory.class);
        serviceContreBon = ServiceFactory.getApiClient().create(ServiceContreBon.class);
        serviceCustomer = ServiceFactory.getApiClient().create(ServiceCustomer.class);
        serviceSession = ServiceFactory.getApiClient().create(ServiceSession.class);
        serviceDiscount = ServiceFactory.getApiClient().create(ServiceDiscount.class);
        serviceStackTrace = ServiceFactory.getApiClient().create(ServiceStackTrace.class);
        cacheStore = CacheStore.getInstance();
    }

    public static PullDb getInstance() {
        if (pullDb == null) {
            pullDb = new PullDb();
        }
        return pullDb;
    }

    public void startPulling(SyncPullListener listener) {
        Common.changeProgressText("Chargement des categorie");
        Observable<JsonArray> getCategories = serviceCategory.getCategories();
        getCategories.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(x ->

        {
            CategoryConverterModel categoryConverterModel = new CategoryConverterModel();
            List<CategoryConverterModel> categoryConverterModels;
            List<Category> categories = new ArrayList<>();
            Gson gson = new Gson();
            TypeToken<List<CategoryConverterModel>> token = new TypeToken<List<CategoryConverterModel>>() {
            };
            categoryConverterModels = gson.fromJson(x, token.getType());
            for (CategoryConverterModel cc : categoryConverterModels) {
                categories.add(categoryConverterModel.toDBModel(cc));
            }
            for (Category c : categories) {
                c.save();
            }
            loadCategoryImages(categories, 0, listener);
        });
    }

    private void loadCategoryImages(List<Category> list, int i, SyncPullListener listener) {

        if (i < list.size()) {
            ImageLoader.getInstance().loadImage(Constants.HTTP.IMAGE_URL_CAT + list.get(i).getPhoto(), new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                    onLoadingCancelled(imageUri, view);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    cacheStore.saveCacheFile(list.get(i).getPhoto(), loadedImage);
                    loadCategoryImages(list, i + 1, listener);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                    loadCategoryImages(list, i + 1, listener);
                }
            });
        } else {
            Observable<JsonArray> getIngredients = serviceIngredient.getIngredients();
            Common.changeProgressText("Chargement des Ingredients");
            Gson gson = new Gson();
            getIngredients.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(y -> {

                        IngredientConverterModel icm = new IngredientConverterModel();
                        List<IngredientConverterModel> icms;
                        List<Ingredient> ingredients = new ArrayList<>();

                        TypeToken<List<IngredientConverterModel>> tic = new TypeToken<List<IngredientConverterModel>>() {
                        };

                        icms = gson.fromJson(y, tic.getType());
                        for (IngredientConverterModel cc : icms) {
                            ingredients.add(icm.toDBModel(cc));
                        }
                        for (Ingredient c : ingredients) {
                            c.save();
                        }
                        loadIngredientImages(ingredients, 0, listener);
                    });
        }
    }

    private void loadIngredientImages(List<Ingredient> list, int i, SyncPullListener listener) {
        if (i < list.size()) {
            list.get(i).save();
            ImageLoader.getInstance().loadImage(Constants.HTTP.IMAGE_URL_ING + list.get(i).getPhoto(), new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                    onLoadingCancelled(imageUri, view);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    cacheStore.saveCacheFile(list.get(i).getPhoto(), loadedImage);
                    loadIngredientImages(list, i + 1, listener);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                    loadIngredientImages(list, i + 1, listener);
                }
            });
        } else {
            Observable<JsonArray> getProducts = serviceProduct.getProducts();
            Common.changeProgressText("Chargement des Produits");
            Gson gson = new Gson();
            getProducts.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(pr -> {
                        List<Product> listP = new ArrayList<>();
                        List<ProductConverterModel> pcms;
                        TypeToken<List<ProductConverterModel>> tpc = new TypeToken<List<ProductConverterModel>>() {
                        };


                        pcms = gson.fromJson(pr, tpc.getType());
                        for (ProductConverterModel pcm : pcms) {
                            listP.add(pcm.toDbModel());
                        }
                        for (Product p : listP) {
                            p.save();
                            for (Product_Ingredient pi : p.getIngredients()) {
                                pi.save();
                            }
                        }
                        loadProductImages(listP, 0, listener);
                    });
        }
    }

    private void loadProductImages(List<Product> list, int i, SyncPullListener listener) {
        if (i < list.size()) {
            ImageLoader.getInstance().loadImage(Constants.HTTP.IMAGE_URL_PRO + list.get(i).getPhoto(), new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                    onLoadingCancelled(imageUri, view);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    cacheStore.saveCacheFile(list.get(i).getPhoto(), loadedImage);
                    loadProductImages(list, i + 1, listener);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                    loadProductImages(list, i + 1, listener);
                }
            });
        } else {
            Common.changeProgressText("Chargement des Clients");
            Observable<JsonArray> getCustomerGroups = serviceCustomer.getGroups();
            getCustomerGroups.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(cg -> {
                        List<CustomerGroup> listcg = new ArrayList<>();
                        List<CustomerGroupConverter> cgc;
                        TypeToken<List<CustomerGroupConverter>> tcgc = new TypeToken<List<CustomerGroupConverter>>() {
                        };
                        Gson gson = new Gson();
                        cgc = gson.fromJson(cg, tcgc.getType());

                        for (CustomerGroupConverter customerg :
                                cgc) {
                            listcg.add(customerg.fromDbModel());
                        }

                        for (CustomerGroup cgroup : listcg) {
                            cgroup.save();
                            for (Customer customer :
                                    cgroup.getCustomers()) {
                                customer.save();
                            }
                        }
                        Observable<JsonArray> getCustomers = serviceCustomer.getCustomers();
                        getCustomers.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(cs -> {
                                    List<Customer> listcs = new ArrayList<>();
                                    List<CustomerGroupConverter.CustomerConverter> cc;
                                    TypeToken<List<CustomerGroupConverter.CustomerConverter>> tccc = new TypeToken<List<CustomerGroupConverter.CustomerConverter>>() {
                                    };
                                    cc = gson.fromJson(cs, tccc.getType());

                                    for (CustomerGroupConverter.CustomerConverter customer :
                                            cc) {
                                        customer.fromDbModel().save();
                                    }


                                    Common.changeProgressText("Chargement des Commandes");
                                    Observable<JsonArray> getCommandes = serviceCommande.getCommande();

                                    getCommandes.subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(cm -> {
                                                List<Commande> listC = new ArrayList<>();
                                                List<CommandeConverterModel> ccms;
                                                TypeToken<List<CommandeConverterModel>> tcc = new TypeToken<List<CommandeConverterModel>>() {
                                                };


                                                ccms = gson.fromJson(cm, tcc.getType());

                                                for (CommandeConverterModel ccm : ccms) {
                                                    listC.add(ccm.toDbModel());

                                                }
                                                for (Commande c : listC) {
                                                    c.save();
                                                    for (DetailCommande dc : c.getProducts()) {
                                                        dc.setCommande(c);
                                                        dc.setCommandeNumber(c.getCommandeNumber());
                                                        dc.save();
                                                    }
                                                    for (DetailCommandeIngredient dci : c.getIngredients()) {
                                                        dci.setCommande(c);
                                                        dci.setCommandeNumber(c.getCommandeNumber());
                                                        dci.save();
                                                    }
                                                    for (Payment p : c.getPayments()) {
                                                        p.setCommande(c);
                                                        p.save();
                                                    }
                                                }
                                                Common.changeProgressText("Chargement des Autres données");
                                                serviceHistory.getHistory().subscribeOn(Schedulers.io())
                                                        .observeOn(AndroidSchedulers.mainThread())
                                                        .subscribe(hm -> {

                                                            TypeToken<List<HistoryModel>> htc = new TypeToken<List<HistoryModel>>() {
                                                            };
                                                            List<HistoryModel> histories = gson.fromJson(hm, htc.getType());
                                                            for (HistoryModel history : histories) {
                                                                history.toDbModel().save();
                                                            }
                                                            serviceContreBon.getContreBon().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(cb -> {

                                                                TypeToken<List<ContreBonConverter>> cbc = new TypeToken<List<ContreBonConverter>>() {
                                                                };
                                                                List<ContreBonConverter> contreBonConverters = gson.fromJson(cb, cbc.getType());
                                                                for (ContreBonConverter contreBon : contreBonConverters) {
                                                                    contreBon.toDbModel().save();
                                                                }
                                                                serviceSession.getSessions().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                                                                        ses -> {
                                                                            TypeToken<List<SessionConverter>> sesC = new TypeToken<List<SessionConverter>>() {
                                                                            };
                                                                            List<SessionConverter> sessionCs = gson.fromJson(ses, sesC.getType());
                                                                            for (SessionConverter sessionConverter : sessionCs) {
                                                                                sessionConverter.toDbModel().save();
                                                                            }
                                                                            serviceDiscount.getDiscount().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<List<DiscountConverter>>() {
                                                                                @Override
                                                                                public void onSubscribe(Disposable d) {

                                                                                }

                                                                                @Override
                                                                                public void onNext(List<DiscountConverter> discountConverters) {
                                                                                    for (DiscountConverter dc : discountConverters
                                                                                            ) {
                                                                                        Discount discount = dc.toDbModel();
                                                                                        discount.save();
                                                                                        for (Discount_Product dp :
                                                                                                discount.getDiscount_products()) {
                                                                                            dp.setDiscount_id(discount.getId());
                                                                                            dp.save();
                                                                                        }
                                                                                        for (Discount_Group dg :
                                                                                                discount.getDiscount_groups()) {
                                                                                            dg.setDiscount_id(discount.getId());
                                                                                            dg.save();

                                                                                        }
                                                                                    }
                                                                                    serviceStackTrace.getTables().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<List<LoungeTable>>() {
                                                                                        @Override
                                                                                        public void onSubscribe(Disposable d) {

                                                                                        }

                                                                                        @Override
                                                                                        public void onNext(List<LoungeTable> discountConverters) {
                                                                                            for (LoungeTable lt :
                                                                                                    discountConverters) {
                                                                                                lt.save();
                                                                                            }

                                                                                            listener.onSyncPullFinished();

                                                                                        }

                                                                                        @Override
                                                                                        public void onError(Throwable e) {

                                                                                        }

                                                                                        @Override
                                                                                        public void onComplete() {

                                                                                        }
                                                                                    });

                                                                                }

                                                                                @Override
                                                                                public void onError(Throwable e) {

                                                                                }

                                                                                @Override
                                                                                public void onComplete() {




                                                                                    }}); }); }); }); }); }); });}}


}
