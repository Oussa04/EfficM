package tn.meteor.efficaisse.ui.ingredientDetails;

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
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

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

import at.markushi.ui.CircleButton;
import tn.meteor.efficaisse.R;
import tn.meteor.efficaisse.adapter.HistoryAdapter;
import tn.meteor.efficaisse.adapter.ProductIngredientAdapter;
import tn.meteor.efficaisse.data.preferences.AppPreferencesHelper;
import tn.meteor.efficaisse.data.repository.HistoryRepository;
import tn.meteor.efficaisse.data.repository.IngredientRepository;
import tn.meteor.efficaisse.data.repository.ProductIngredientRepository;
import tn.meteor.efficaisse.model.History;
import tn.meteor.efficaisse.model.Ingredient;
import tn.meteor.efficaisse.model.Ingredient_Table;
import tn.meteor.efficaisse.model.Prediction;
import tn.meteor.efficaisse.model.Product_Ingredient;
import tn.meteor.efficaisse.ui.base.BaseFragment;
import tn.meteor.efficaisse.utils.CacheStore;
import tn.meteor.efficaisse.utils.Constants;
import tn.meteor.efficaisse.utils.ItemOffsetDecoration;
import tn.meteor.efficaisse.utils.SquareImageView;
import tn.meteor.efficaisse.utils.TicketDividerItemDecoration;

/**
 * Created by lilk on 28/01/2018.
 */

public class IngredientDetailsFragment extends BaseFragment implements IngredientDetailsContract.View, ProductIngredientAdapter.GridAdapterListener, HistoryAdapter.HistoryListener {

    private EditText libelle, price, quantity;
    private TextView unit,prevision;
    private RecyclerView productsUsingIngredient, sideRecyclerView;
    private ProductIngredientAdapter productAdapter;
    private List<Product_Ingredient> products_using_ingredient;
    private List<History> historyList;
    private List<History> initialHistoriesList;
    private FloatingSearchView search;
    private HistoryAdapter historyAdapter;
    private CircleButton photo, edit, delete, save, cancel;

    private Ingredient ingredient;
    private SquareImageView thumbnail;
    private Uri resultUri;
    private Bitmap imageUpdatedBitmap;
    private boolean imageUpdated;
    private AppPreferencesHelper preferences2;
private IngredientDetailsPresenter ingredientDetailsPresenter;
    public static IngredientDetailsFragment newInstance() {
        IngredientDetailsFragment fragment = new IngredientDetailsFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_ingredient_details, container, false);
        preferences2 = new AppPreferencesHelper(getActivity(), Constants.SHARED_PREFERENCES_NAME.PREFERENCES_ACCES_TYPE);

        libelle = v.findViewById(R.id.libelle);
        quantity = v.findViewById(R.id.quantity);
        price = v.findViewById(R.id.price);
        unit = v.findViewById(R.id.unit);
        thumbnail = v.findViewById(R.id.thumbnail);
        productsUsingIngredient = v.findViewById(R.id.productIngredients);
        edit = v.findViewById(R.id.editIngredient);
        delete = v.findViewById(R.id.deleteIngredient);
        save = v.findViewById(R.id.save);
        cancel = v.findViewById(R.id.cancel);
        photo = v.findViewById(R.id.photo);
        sideRecyclerView = v.findViewById(R.id.sideRecyclerView);
        prevision= v.findViewById(R.id.prevision);
        save.setVisibility(View.GONE);
        cancel.setVisibility(View.GONE);
        delete.setVisibility(View.GONE);
        photo.setVisibility(View.GONE);

        search = v.findViewById(R.id.search);
        historyList = new ArrayList<>();

        ingredientDetailsPresenter=new IngredientDetailsPresenter(this);
        String ingredientJson = getArguments().getString("ingredient");
        Gson gson = new Gson();
        ingredient = gson.fromJson(ingredientJson, Ingredient.class);
        unit.setText(ingredient.getUnit().toString());
        libelle.setText(ingredient.getName());
        quantity.setText(ingredient.getStockQuantity() + "");
        price.setText(ingredient.getPrice() + "");
        CacheStore cacheStore = CacheStore.getInstance();
        try {
            Picasso.with(getActivity())
                    .load(cacheStore.getFileUri(ingredient.getPhoto()))
                    .into(thumbnail);
        } catch (Exception e) {

        }

        ProductIngredientRepository repo = new ProductIngredientRepository();
        products_using_ingredient = new ArrayList<>();
        products_using_ingredient.addAll(repo.findProductsWithIngredient(ingredient));

        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, LinearLayoutManager.VERTICAL);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(v.getContext(), R.dimen.item_product_offset);

        productAdapter = new ProductIngredientAdapter(preferences2.getAccesType().equals("cashier"),getActivity(), this, products_using_ingredient, false);
        productsUsingIngredient.addItemDecoration(itemDecoration);
        productsUsingIngredient.setLayoutManager(staggeredGridLayoutManager);
        productsUsingIngredient.setAdapter(productAdapter);


        edit.setOnClickListener(view -> {
            libelle.setEnabled(true);
            quantity.setEnabled(true);
            price.setEnabled(true);
            save.setVisibility(View.VISIBLE);
            photo.setVisibility(View.VISIBLE);
            cancel.setVisibility(View.VISIBLE);
            delete.setVisibility(View.VISIBLE);
            edit.setVisibility(View.GONE);
            hideThingsForCashier();

        });

        cancel.setOnClickListener(view -> {
            libelle.setEnabled(false);
            quantity.setEnabled(false);
            price.setEnabled(false);

            libelle.setText(ingredient.getName());
            quantity.setText(ingredient.getStockQuantity() + "");
            price.setText(ingredient.getPrice() + "");

            save.setVisibility(View.GONE);
            cancel.setVisibility(View.GONE);
            delete.setVisibility(View.GONE);
            edit.setVisibility(View.VISIBLE);
            photo.setVisibility(View.GONE);


        });

        save.setOnClickListener(view -> {
            if (!quantity.getText().toString().isEmpty() && !price.getText().toString().isEmpty() && !libelle.getText().toString().isEmpty()) {
                if (!libelle.getText().toString().equals(ingredient.getName())) {
                    if (SQLite.select().from(Ingredient.class).where(Ingredient_Table.name.eq(libelle.getText().toString())).querySingle() == null) {
                        saveIngredient();
                    } else {
                        showMessage("Un ingrédient avec le mème nom existe déja");
                    }
                } else {
                    saveIngredient();

                }


            } else {
                showMessage("Prière de vérifier les champs, certains peuvent être vides ");

            }


        });


        delete.setOnClickListener(view ->

        {
            if (products_using_ingredient.isEmpty()) {
                IngredientRepository ingredientRepository = new IngredientRepository();
                ingredientRepository.delete(ingredient);
                getFragmentManager().popBackStackImmediate();

            } else {
                showMessage("Certaines produits utilisent cet ingrédient, prière de les modifier");


            }
        });


        photo.setOnClickListener(view -> CropImage.activity()
                .

                        setGuidelines(CropImageView.Guidelines.ON).

                        setActivityTitle("Image de l'ingrédient")
                .

                        start(getActivity(), this)
        );

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
        historyAdapter = new HistoryAdapter(getActivity(), this, historyList, ingredient.getUnit().toString());
        sideRecyclerView.setAdapter(historyAdapter);
        loadHistory();



        Prediction prediction = ingredientDetailsPresenter.getPredictionByIngredient(ingredient);
        if (prediction.getCount() > 2) {

            String message = "Il est recommendé d'avoir <b><font color='#39c870'>" + prediction.getPrediction() + " "+prediction.getIngredient().getUnit() + " de "+prediction.getIngredient().getName()+
                    "</font></b> pour demain d'après <b><font color='#39c870'>"+prediction.getCriteria()+"</font></b>.";
            prevision.setText(Html.fromHtml(message));




        } else {
            String message = "Il n'y a pas suffisamment de données pour donner une recommendation pour <b><font color='#39c870'>" +prediction.getIngredient().getName()+
                    "</font></b>.";
            prevision.setText(Html.fromHtml(message));


        }

        return v;


    }

    public void hideThingsForCashier() {
        if (preferences2.getAccesType().equals("cashier")) {
            libelle.setEnabled(false);
            price.setEnabled(false);
            photo.setVisibility(View.GONE);
            delete.setVisibility(View.GONE);
        }

    }


    public void saveIngredient() {
        if (ingredient.getStockQuantity() != Float.parseFloat(quantity.getText().toString())) {
            History history = new History();
            history.setDate(new Date());
            history.setName(ingredient.getName());
            history.setQuantity(Float.parseFloat(quantity.getText().toString()) - ingredient.getStockQuantity());
            history.setType("Ingredient");
            HistoryRepository historyRepository = new HistoryRepository();
            historyRepository.save(history);
        }
        ingredient.setStockQuantity(Float.parseFloat(quantity.getText().toString()));
        ingredient.setPrice(Float.parseFloat(price.getText().toString()));
        ingredient.setName(libelle.getText().toString());

        if (imageUpdated) {
            CacheStore cacheStore = CacheStore.getInstance();

            cacheStore.deleteCacheFile(ingredient.getPhoto());
            cacheStore.saveCacheFile(ingredient.getPhoto(), imageUpdatedBitmap);
        }

        IngredientRepository ingredientRepository = new IngredientRepository();
        ingredientRepository.update(ingredient, imageUpdated);

        libelle.setEnabled(false);
        quantity.setEnabled(false);
        price.setEnabled(false);
        save.setVisibility(View.GONE);
        cancel.setVisibility(View.GONE);
        photo.setVisibility(View.GONE);
        delete.setVisibility(View.GONE);
        edit.setVisibility(View.VISIBLE);
        hideThingsForCashier();
        loadHistory();
    }

    public void loadHistory() {
        HistoryRepository repository = new HistoryRepository();
        historyList.clear();
        historyList.addAll(repository.find(ingredient));
        Collections.reverse(historyList);
        initialHistoriesList = new ArrayList<>();
        initialHistoriesList.addAll(historyList);
        search.clearQuery();
        search.clearSearchFocus();
        historyAdapter.notifyDataSetChanged();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
    public void onProductIngredientRemoveClicked(Product_Ingredient productIngredientRelation) {

    }

    @Override
    public void onProductIngredientEditClicked(Product_Ingredient productIngredientRelation) {

    }

    @Override
    public void onHistorySelected(History history) {

    }
}
