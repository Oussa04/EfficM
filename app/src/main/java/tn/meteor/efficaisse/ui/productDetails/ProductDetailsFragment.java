package tn.meteor.efficaisse.ui.productDetails;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.annimon.stream.Stream;
import com.arlib.floatingsearchview.FloatingSearchView;
import com.google.gson.Gson;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import at.markushi.ui.CircleButton;
import tn.meteor.efficaisse.R;
import tn.meteor.efficaisse.adapter.HistoryAdapter;
import tn.meteor.efficaisse.adapter.IngredientAdapter;
import tn.meteor.efficaisse.adapter.ProductIngredientAdapter;
import tn.meteor.efficaisse.data.preferences.AppPreferencesHelper;
import tn.meteor.efficaisse.data.repository.HistoryRepository;
import tn.meteor.efficaisse.data.repository.IngredientRepository;
import tn.meteor.efficaisse.data.repository.ProductIngredientRepository;
import tn.meteor.efficaisse.model.History;
import tn.meteor.efficaisse.model.Ingredient;
import tn.meteor.efficaisse.model.Product;
import tn.meteor.efficaisse.model.Product_Ingredient;
import tn.meteor.efficaisse.model.Product_Table;
import tn.meteor.efficaisse.ui.base.BaseFragment;
import tn.meteor.efficaisse.utils.CacheStore;
import tn.meteor.efficaisse.utils.Constants;
import tn.meteor.efficaisse.utils.ItemOffsetDecoration;
import tn.meteor.efficaisse.utils.SquareImageView;
import tn.meteor.efficaisse.utils.TicketDividerItemDecoration;

/**
 * Created by lilk on 28/01/2018.
 */

public class ProductDetailsFragment extends BaseFragment implements IngredientAdapter.GridAdapterListener, ProductIngredientAdapter.GridAdapterListener, HistoryAdapter.HistoryListener {

    private EditText cost, libelle, price, quantity, quantityAvailble;
    private LinearLayout sidePanel;
    private TextView unit, costText, quantityText;
    private Switch favoris;
    private CircleButton edit;
    private CircleButton cancel;
    private CircleButton delete;
    private CircleButton save;
    private Button close;
    private Button add;
    private ProductDetailsPresenter productDetailsPresenter;
    private LinearLayout addIngredientBar;
    private RecyclerView sideRecyclerView, productIngredients;
    private IngredientAdapter ingredientAdapter;
    private HistoryAdapter historyAdapter;
    private ProductIngredientAdapter productIngredientAdapter;
    private List<Ingredient> ingredientList;
    private List<Product_Ingredient> product_ingredient_relationList;
    private List<History> historyList;
    private Ingredient selectedIngredient;
    private Product product;
    private ProductIngredientRepository repository;
    private SquareImageView thumbnail;
    private FloatingSearchView search;
    private CircleButton photo;
    private boolean imageUpdated = false;
    private Bitmap imageUpdatedBitmap;
    private TreeMap<Ingredient, Float> tmap;
    private Uri resultUri;
    private AppPreferencesHelper preferences2;


    public static ProductDetailsFragment newInstance() {
        ProductDetailsFragment fragment = new ProductDetailsFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_product_details, container, false);
        preferences2 = new AppPreferencesHelper(getActivity(), Constants.SHARED_PREFERENCES_NAME.PREFERENCES_ACCES_TYPE);

        libelle = v.findViewById(R.id.libelle);
        cost = v.findViewById(R.id.cost);
        price = v.findViewById(R.id.price);
        thumbnail = v.findViewById(R.id.thumbnail);
        sideRecyclerView = v.findViewById(R.id.sideRecyclerView);
        productIngredients = v.findViewById(R.id.productIngredients);
        search = v.findViewById(R.id.search);
        quantityText = v.findViewById(R.id.quantityText);
        costText = v.findViewById(R.id.costText);
        edit = v.findViewById(R.id.editProduct);
        photo = v.findViewById(R.id.photo);
        cancel = v.findViewById(R.id.cancel);
        delete = v.findViewById(R.id.deleteProduct);
        quantityAvailble = v.findViewById(R.id.quantityAvailble);
        save = v.findViewById(R.id.save);
        favoris = v.findViewById(R.id.favoris);
        sidePanel = v.findViewById(R.id.allingredientsPanel);
        save.setVisibility(View.GONE);
        cancel.setVisibility(View.GONE);
        photo.setVisibility(View.GONE);
        delete.setVisibility(View.GONE);

        edit.setOnClickListener(view -> {
            libelle.setEnabled(true);
            price.setEnabled(true);
            cost.setEnabled(true);
            quantityAvailble.setEnabled(true);
            save.setVisibility(View.VISIBLE);
            photo.setVisibility(View.VISIBLE);
            cancel.setVisibility(View.VISIBLE);
            delete.setVisibility(View.VISIBLE);
            edit.setVisibility(View.GONE);
            hideThingsForCashier();

        });

        cancel.setOnClickListener(view -> {
            libelle.setEnabled(false);
            price.setEnabled(false);
            cost.setEnabled(false);
            quantityAvailble.setEnabled(false);

            libelle.setText(product.getName());
            cost.setText(product.getCost() + "");
            price.setText(product.getPrice() + "");

            save.setVisibility(View.GONE);
            cancel.setVisibility(View.GONE);
            photo.setVisibility(View.GONE);

            delete.setVisibility(View.GONE);
            edit.setVisibility(View.VISIBLE);
        });


        save.setOnClickListener(view -> {


            if (product.getCost() == null && product.getStockQuantity() == null) {
                if (TextUtils.isEmpty(price.getText().toString())) {
                    price.setError("Champ obligatoire");
                } else if (TextUtils.isEmpty(libelle.getText().toString())) {
                    libelle.setError("Champ obligatoire");
                }
            } else {
                if (TextUtils.isEmpty(cost.getText().toString())) {
                    cost.setError("Champ obligatoire");
                } else if (TextUtils.isEmpty(quantityAvailble.getText().toString())) {
                    quantityAvailble.setError("Champ obligatoire");
                } else if (TextUtils.isEmpty(price.getText().toString())) {
                    price.setError("Champ obligatoire");
                } else if (TextUtils.isEmpty(libelle.getText().toString())) {
                    libelle.setError("Champ obligatoire");
                }

            }


            if (!libelle.getText().toString().equals(product.getName())) {
                if (SQLite.select().from(Product.class).where(Product_Table.name.eq(libelle.getText().toString())).querySingle() == null) {
                    saveProduct();
                } else {
                    showMessage("Un produit avec le mème nom existe déjà");
                }
            } else {
                saveProduct();
            }
        });


        delete.setOnClickListener(view -> {
            ProductIngredientRepository productIngredientRepository = new ProductIngredientRepository();
            productIngredientRepository.delete(product);
            getFragmentManager().popBackStackImmediate();


        });


        IngredientRepository ingredientRepository = new IngredientRepository();


        photo.setOnClickListener(view -> CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON).setActivityTitle("Image de produit")
                .start(getActivity(), this)
        );
        String productJson = getArguments().getString("product");
        Gson gson = new Gson();
        product = gson.fromJson(productJson, Product.class);
        Log.i("pro", product.getId() + "");
        libelle.setText(product.getName());
        cost.setText(product.getCost() + "");
        price.setText(product.getPrice() + "");
        quantityAvailble.setText(product.getStockQuantity() + "");
        favoris.setChecked(product.isFavoris());
        favoris.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (!product.isFavoris() && isChecked) {
                product.setFavoris(true);
                ProductIngredientRepository productIngredientRepository = new ProductIngredientRepository();
                productIngredientRepository.updateProduct(product, false);

            } else {
                product.setFavoris(false);
                ProductIngredientRepository productIngredientRepository = new ProductIngredientRepository();
                productIngredientRepository.updateProduct(product, false);
            }
        });

        sidePanel.setVisibility(View.VISIBLE);
        productIngredients.setVisibility(View.GONE);
        quantityAvailble.setVisibility(View.VISIBLE);
        cost.setVisibility(View.VISIBLE);
        quantityText.setVisibility(View.VISIBLE);
        costText.setVisibility(View.VISIBLE);
        if (product.getCost() == null && product.getStockQuantity() == null) {
            productIngredients.setVisibility(View.VISIBLE);
            quantityAvailble.setVisibility(View.GONE);
            cost.setVisibility(View.GONE);
            quantityText.setVisibility(View.GONE);
            costText.setVisibility(View.GONE);
        }
        CacheStore cacheStore = CacheStore.getInstance();
        try {
            Picasso.with(getActivity())
                    .load(cacheStore.getFileUri(product.getPhoto()))
                    .into(thumbnail);

        } catch (Exception e) {

        }
        ingredientList = new ArrayList<>();
        historyList = new ArrayList<>();
        product_ingredient_relationList = new ArrayList<>();


        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(v.getContext(), R.dimen.item_product_offset);
        if (product.getCost() == null && product.getStockQuantity() == null) {
            ingredientAdapter = new IngredientAdapter(getActivity(), this, ingredientList);
            sideRecyclerView.setAdapter(ingredientAdapter);
            search.setOnQueryChangeListener((oldQuery, newQuery) -> {

                List<Ingredient> listToShow = Stream.of(initialIngredientsList).filter(i -> i.getName().toLowerCase().contains(newQuery.toLowerCase())).toList();
                ingredientList.clear();
                ingredientList.addAll(listToShow);
                ingredientAdapter.notifyDataSetChanged();
            });
            sideRecyclerView.addItemDecoration(itemDecoration);
            sideRecyclerView.setLayoutManager(staggeredGridLayoutManager);
            sideRecyclerView.setAdapter(ingredientAdapter);

        } else {
            historyAdapter = new HistoryAdapter(getActivity(), this, historyList, "Piece");
            sideRecyclerView.setAdapter(historyAdapter);
            search.setOnQueryChangeListener((oldQuery, newQuery) -> {

                List<History> listToShow = Stream.of(initialHistoriesList).filter(i -> i.getUsername().toLowerCase().contains(newQuery.toLowerCase())).toList();
                historyList.clear();
                historyList.addAll(listToShow);
                historyAdapter.notifyDataSetChanged();


            });
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            sideRecyclerView.setLayoutManager(mLayoutManager);
            sideRecyclerView.setItemAnimator(new DefaultItemAnimator());
            sideRecyclerView.addItemDecoration(new TicketDividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL, 0));
        }


        StaggeredGridLayoutManager staggeredGridLayoutManager2 = new StaggeredGridLayoutManager(3, LinearLayoutManager.VERTICAL);
        ItemOffsetDecoration itemDecoration2 = new ItemOffsetDecoration(v.getContext(), R.dimen.item_product_offset);

        productIngredientAdapter = new ProductIngredientAdapter(preferences2.getAccesType().equals("cashier"),getActivity(), this, product_ingredient_relationList, true);
        productIngredients.addItemDecoration(itemDecoration2);
        productIngredients.setLayoutManager(staggeredGridLayoutManager2);
        productIngredients.setAdapter(productIngredientAdapter);
        imageUpdated = false;

        loadData();

        hideThingsForCashier();
        return v;


    }

    public void hideThingsForCashier() {

        if (preferences2.getAccesType().equals("cashier")) {

            if (!(product.getCost() != null && product.getStockQuantity() != null)) {
                edit.setVisibility(View.GONE);


            } else {


                cost.setVisibility(View.GONE);
                libelle.setEnabled(false);
                price.setEnabled(false);
                photo.setVisibility(View.GONE);
                delete.setVisibility(View.GONE);
            }
        }

    }

    public void saveProduct() {
        ProductIngredientRepository productIngredientRepository = new ProductIngredientRepository();
        if (product.getCost() != null && product.getStockQuantity() != null)
            if (product.getStockQuantity() != Float.parseFloat(quantityAvailble.getText().toString())) {

                History history = new History();
                history.setDate(new Date());
                history.setName(product.getName());
                history.setQuantity(Float.parseFloat(quantityAvailble.getText().toString()) - product.getStockQuantity());
                history.setType("Product");
                HistoryRepository historyRepository = new HistoryRepository();
                historyRepository.save(history);
                loadHistory();
            }
        if (imageUpdated) {

            CacheStore cacheStore = CacheStore.getInstance();
            cacheStore.deleteCacheFile(product.getPhoto());
            cacheStore.saveCacheFile(product.getPhoto(), imageUpdatedBitmap);
        }

        product.setPrice(Float.parseFloat(price.getText().toString()));
        product.setName(libelle.getText().toString());
        if (product.getCost() != null && product.getStockQuantity() != null) {
            product.setCost(Double.parseDouble(cost.getText().toString()));
            product.setStockQuantity(Double.parseDouble(quantityAvailble.getText().toString()));
        }


        productIngredientRepository.updateProduct(product, imageUpdated);
        afterSaveButtonsState();


    }

    private void showQuantityDialog(Ingredient ingredient) {

        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity())
                .customView(R.layout.dialog_ingredient_quantity, false);

        MaterialDialog dialog = builder.build();
        quantity = (EditText) dialog.findViewById(R.id.quantity);
        unit = (TextView) dialog.findViewById(R.id.unit);
        close = (Button) dialog.findViewById(R.id.close);
        add = (Button) dialog.findViewById(R.id.add);
        unit.setText(ingredient.getUnit().toString());
        close.setOnClickListener(view -> {
            dialog.dismiss();
        });
        add.setOnClickListener(view -> {

            ProductIngredientRepository productIngredientRepository = new ProductIngredientRepository();
            if (TextUtils.isEmpty(quantity.getText().toString())) {
                quantity.setError("Champ obligatoire");
            } else {
                productIngredientRepository.save(product, ingredient, Float.parseFloat(quantity.getText().toString()));
                loadData();
                dialog.dismiss();
            }

        });
        dialog.show();
    }

    private void afterSaveButtonsState() {
        libelle.setEnabled(false);
        price.setEnabled(false);
        cost.setEnabled(false);
        quantityAvailble.setEnabled(false);

        save.setVisibility(View.GONE);
        photo.setVisibility(View.GONE);

        cancel.setVisibility(View.GONE);
        delete.setVisibility(View.GONE);
        edit.setVisibility(View.VISIBLE);
    }

    private void showQuantityEditDialog(Product_Ingredient productIngredient) {

        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity())
                .customView(R.layout.dialog_ingredient_quantity, false);

        MaterialDialog dialog = builder.build();
        quantity = (EditText) dialog.findViewById(R.id.quantity);
        quantity.setText(productIngredient.getQuantity() + "");
        unit = (TextView) dialog.findViewById(R.id.unit);
        close = (Button) dialog.findViewById(R.id.close);
        add = (Button) dialog.findViewById(R.id.add);
        unit.setText(productIngredient.getIngredient().getUnit().toString());
        close.setOnClickListener(view -> {
            dialog.dismiss();


        });
        add.setOnClickListener(view -> {
            if (quantity.getText().toString().isEmpty() || Float.parseFloat(quantity.getText().toString()) <= 0) {
                showMessage("Vérifier la quantité");
            } else {
                productIngredient.setQuantity(Float.parseFloat(quantity.getText().toString()));
                ProductIngredientRepository productIngredientRepository = new ProductIngredientRepository();
                productIngredientRepository.updateProductIngreient(productIngredient);
                dialog.dismiss();
                loadData();


            }
        });
        dialog.show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("photo", "onActivityResult: ");

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK) {
                resultUri = result.getUri();
                try {
                    imageUpdatedBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), resultUri);
                    Picasso.with(getActivity())
                            .load(resultUri).into(thumbnail);
                    imageUpdated = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }

    @Override
    public void onIngredientSelected(Ingredient ingredient) {
        Toast.makeText(getActivity(), ingredient.getName(), Toast.LENGTH_SHORT).show();

        showQuantityDialog(ingredient);

    }

    @Override
    public void onIngredientLongClickSelected(Ingredient ingredient) {

    }


    @Override
    public void onProductIngredientRemoveClicked(Product_Ingredient productIngredientRelation) {
        ProductIngredientRepository productIngredientRepository = new ProductIngredientRepository();
        productIngredientRepository.deleteProductIngredient(productIngredientRelation);
        loadData();
    }

    @Override
    public void onProductIngredientEditClicked(Product_Ingredient productIngredientRelation) {
        showQuantityEditDialog(productIngredientRelation);
    }

    private List<Ingredient> initialIngredientsList;

    private List<History> initialHistoriesList;

    public void loadData() {
        ProductIngredientRepository productIngredientRepository = new ProductIngredientRepository();
        product = productIngredientRepository.find(product.getId());
        if (product.getCost() == null && product.getStockQuantity() == null) {
            loadIngredients();
        } else {
            loadHistory();
        }

    }

    public void loadHistory() {
        HistoryRepository repository = new HistoryRepository();
        historyList.clear();
        historyList.addAll(repository.find(product));
        Collections.reverse(historyList);
        initialHistoriesList = new ArrayList<>();
        initialHistoriesList.addAll(historyList);
        search.clearQuery();
        search.clearSearchFocus();
        historyAdapter.notifyDataSetChanged();
    }

    public void loadIngredients() {
        IngredientRepository repo = new IngredientRepository();
        ingredientList.clear();
        ingredientList.addAll(repo.findAll());

        product_ingredient_relationList.clear();
        product_ingredient_relationList.addAll(product.getIngredients());

        for (Product_Ingredient productIngredient : product_ingredient_relationList) {

            if (ingredientList.indexOf(productIngredient.getIngredient()) > -1) {
                ingredientList.remove(productIngredient.getIngredient());
            }

        }
        initialIngredientsList = new ArrayList<>();
        initialIngredientsList.addAll(ingredientList);

        search.clearQuery();
        search.clearSearchFocus();
        productIngredientAdapter.notifyDataSetChanged();
        ingredientAdapter.notifyDataSetChanged();
    }

    @Override
    public void onHistorySelected(History history) {

    }
}
