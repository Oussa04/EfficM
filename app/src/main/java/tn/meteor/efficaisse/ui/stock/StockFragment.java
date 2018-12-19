package tn.meteor.efficaisse.ui.stock;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import tn.meteor.efficaisse.R;
import tn.meteor.efficaisse.adapter.IngredientAdapter;
import tn.meteor.efficaisse.data.preferences.AppPreferencesHelper;
import tn.meteor.efficaisse.model.Ingredient;
import tn.meteor.efficaisse.ui.base.BaseFragment;
import tn.meteor.efficaisse.ui.ingredient.IngredientActivity;
import tn.meteor.efficaisse.ui.ingredientDetails.IngredientDetailsFragment;
import tn.meteor.efficaisse.utils.Constants;
import tn.meteor.efficaisse.utils.ItemOffsetDecoration;

/**
 * Created by lilk on 28/01/2018.
 */

public class StockFragment extends BaseFragment implements StockContract.View, IngredientAdapter.GridAdapterListener {


    private StockContract.Presenter stockPresenter;
    private RecyclerView ingredients;
    private List<Ingredient> ingredientList;
    private LinearLayout addIngredientBar;
    private IngredientAdapter ingredientAdapter;
    private LinearLayout empty;

    private AppPreferencesHelper preferences;

    public static StockFragment newInstance() {
        StockFragment fragment = new StockFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_stock, container, false);
        stockPresenter = new StockPresenter(this);
        ingredients = v.findViewById(R.id.ingredients);
        addIngredientBar = v.findViewById(R.id.addIngredientBar);
        empty = v.findViewById(R.id.empty);
        ingredientList = new ArrayList<>();
        preferences = new AppPreferencesHelper(getActivity(), Constants.SHARED_PREFERENCES_NAME.PREFERENCES_ACCES_TYPE);
        hideThingsForCashier();

        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(5, LinearLayoutManager.VERTICAL);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(v.getContext(), R.dimen.item_product_offset);


        ingredientAdapter = new IngredientAdapter(getActivity(), this, ingredientList);
        ingredients.addItemDecoration(itemDecoration);
        ingredients.setLayoutManager(staggeredGridLayoutManager);
        ingredients.setAdapter(ingredientAdapter);

        stockPresenter.initIngredientList(ingredientList);

        addIngredientBar.setOnClickListener(view -> {
            startActivity(new Intent(getActivity(), IngredientActivity.class));

        });
        return v;
    }

    public void hideThingsForCashier() {
        if (preferences.getAccesType().equals("cashier")) {
            addIngredientBar.setVisibility(View.GONE);

        }

    }

    @Override
    public void onIngredientSelected(Ingredient ingredient) {
        showIngredientDetails(ingredient);


    }

    @Override
    public void onIngredientLongClickSelected(Ingredient ingredient) {

    }

    @Override
    public void onResume() {
        super.onResume();
        stockPresenter.initIngredientList(ingredientList);

    }

    @Override
    public void updateUI() {
        empty.setVisibility(View.GONE);
        ingredients.setVisibility(View.VISIBLE);

        if (ingredientList.isEmpty()) {

            empty.setVisibility(View.VISIBLE);
            ingredients.setVisibility(View.GONE);
        }

        ingredientAdapter.notifyDataSetChanged();
    }

    @Override
    public void showIngredientDetails(Ingredient ingredient) {
        IngredientDetailsFragment ingredientDetailsFragment = new IngredientDetailsFragment();
        Bundle bundle = new Bundle();

        Gson gson = new Gson();
        String json = gson.toJson(ingredient);

        bundle.putString("ingredient", json);

        ingredientDetailsFragment.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.content_main, ingredientDetailsFragment).addToBackStack(null).commit();

    }
}
