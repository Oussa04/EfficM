package tn.meteor.efficaisse.ui.items;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import at.markushi.ui.CircleButton;
import tn.meteor.efficaisse.R;
import tn.meteor.efficaisse.adapter.CategoryAdapter;
import tn.meteor.efficaisse.adapter.ProductAdapter;
import tn.meteor.efficaisse.data.preferences.AppPreferencesHelper;
import tn.meteor.efficaisse.data.repository.CategoryRepository;
import tn.meteor.efficaisse.model.Category;
import tn.meteor.efficaisse.model.Product;
import tn.meteor.efficaisse.ui.base.BaseFragment;
import tn.meteor.efficaisse.ui.category.CategoryActivity;
import tn.meteor.efficaisse.ui.product.ProductActivity;
import tn.meteor.efficaisse.ui.productDetails.ProductDetailsFragment;
import tn.meteor.efficaisse.utils.CacheStore;
import tn.meteor.efficaisse.utils.Constants;
import tn.meteor.efficaisse.utils.ItemOffsetDecoration;
import tn.meteor.efficaisse.utils.SquareImageView;


public class ItemFragment extends BaseFragment implements ItemContract.View, ProductAdapter.GridAdapterListener, CategoryAdapter.CategoryAdapterListener {


    private ItemContract.Presenter itemPresenter;
    private RecyclerView products;
    private RecyclerView categories;
    private LinearLayout addProductBar, addCategoryBar;
    private List<Product> productsList;
    private List<Category> categoryList;
    private ProductAdapter productsAdapter;
    private CategoryAdapter categoryAdapter;
    private Category selectedCategory;
    private Button editCategory;

    private CategoryRepository categoryRepository = new CategoryRepository();
    private Uri resultUri;
    private Bitmap imageUpdatedBitmap;
    private boolean imageUpdated;
    private AppPreferencesHelper preferences2;

    public static ItemFragment newInstance() {
        ItemFragment fragment = new ItemFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_items, container, false);

        itemPresenter = new ItemPresenter(this);
        products = v.findViewById(R.id.products);
        categories = v.findViewById(R.id.categories);
        addProductBar = v.findViewById(R.id.addProductBar);
        addCategoryBar = v.findViewById(R.id.addCategorieBar);
        editCategory = v.findViewById(R.id.editGroup);
        productsList = new ArrayList<>();
        categoryList = new ArrayList<>();
        editCategory.setVisibility(View.GONE);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(4, LinearLayoutManager.VERTICAL);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(v.getContext(), R.dimen.item_product_offset);

        productsAdapter = new ProductAdapter(getActivity(), this, productsList);
        products.addItemDecoration(itemDecoration);
        products.setLayoutManager(staggeredGridLayoutManager);
        products.setAdapter(productsAdapter);


        categoryAdapter = new CategoryAdapter(getActivity(), this, categoryList);
        StaggeredGridLayoutManager staggeredGridLayoutManager2 = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        ItemOffsetDecoration itemDecoration2 = new ItemOffsetDecoration(v.getContext(), R.dimen.item_category_offset);
        categories.addItemDecoration(itemDecoration2);
        categories.setLayoutManager(staggeredGridLayoutManager2);
        categories.setAdapter(categoryAdapter);


        addProductBar.setOnClickListener(view -> startActivity(new Intent(getActivity(), ProductActivity.class)));
        addCategoryBar.setOnClickListener(view -> startActivity(new Intent(getActivity(), CategoryActivity.class)));
        itemPresenter.initProductList(productsList);
        itemPresenter.getFavoriteProducts(productsList);
        editCategory.setOnClickListener(view -> {
            showCategoryDetails(selectedCategory);
        });


        preferences2 = new AppPreferencesHelper(getActivity(), Constants.SHARED_PREFERENCES_NAME.PREFERENCES_ACCES_TYPE);


        return v;
    }

    public void hideThingsForCashier() {
        if (preferences2.getAccesType().equals("cashier")) {
            addProductBar.setVisibility(View.GONE);
            addCategoryBar.setVisibility(View.GONE);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void updateProducts() {
        productsAdapter.notifyDataSetChanged();

    }

    @Override
    public void updateCategories() {
        addProductBar.setVisibility(View.VISIBLE);
        categoryAdapter.notifyDataSetChanged();
        if (categoryList.size() == 1) {
            addProductBar.setVisibility(View.INVISIBLE);

        }
        hideThingsForCashier();

    }

    @Override
    public void showProductDetail(Product product) {

        ProductDetailsFragment productDetailsFragment = new ProductDetailsFragment();
        Bundle bundle = new Bundle();

        Gson gson = new Gson();
        String json = gson.toJson(product);

        bundle.putString("product", json);

        productDetailsFragment.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.content_main, productDetailsFragment).addToBackStack(null).commit();

    }


    @Override
    public void onProductSelected(Product product) {
        showProductDetail(product);
    }

    @Override
    public void onProductLongClickSelected(Product product) {
    }


    @Override
    public void onCategorySelected(Category category) {
        if (category.getName().equals("Favoris")) {

            itemPresenter.getFavoriteProducts(productsList);
            editCategory.setVisibility(View.GONE);

        } else {
            itemPresenter.getProductByCategory(category, productsList);
            if (!preferences2.getAccesType().equals("cashier")) {
                editCategory.setVisibility(View.VISIBLE);
            }


            selectedCategory = category;
        }

    }

    private EditText libelle;
    private Button delete, cancel, edit, save;
    private CircleButton photo;
    private SquareImageView thumbnail;

    private void showCategoryDetails(Category category) {

        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity())
                .customView(R.layout.dialog_category_details, false);

        MaterialDialog dialog = builder.build();
        libelle = (EditText) dialog.findViewById(R.id.libelle);
        delete = (Button) dialog.findViewById(R.id.delete);
        cancel = (Button) dialog.findViewById(R.id.cancel);
        edit = (Button) dialog.findViewById(R.id.edit);
        photo = (CircleButton) dialog.findViewById(R.id.photo);
        thumbnail = (SquareImageView) dialog.findViewById(R.id.thumbnail);

        save = (Button) dialog.findViewById(R.id.save);
        libelle.setText(category.getName().toString());

        libelle.setEnabled(false);
        CacheStore cacheStore = CacheStore.getInstance();
        try {
            Picasso.with(getActivity())
                    .load(cacheStore.getFileUri(category.getPhoto()))
                    .into(thumbnail);

        } catch (Exception e) {

        }
        dialog.show();


        cancel.setVisibility(View.VISIBLE);
        edit.setVisibility(View.VISIBLE);

        delete.setVisibility(View.GONE);
        photo.setVisibility(View.GONE);
        save.setVisibility(View.GONE);

        edit.setOnClickListener(view -> {
            edit.setVisibility(View.GONE);

            delete.setVisibility(View.VISIBLE);
            photo.setVisibility(View.VISIBLE);
            save.setVisibility(View.VISIBLE);

        });

        cancel.setOnClickListener(view -> {
            edit.setVisibility(View.VISIBLE);
            delete.setVisibility(View.GONE);
            photo.setVisibility(View.GONE);
            save.setVisibility(View.GONE);

            dialog.dismiss();

        });

        delete.setOnClickListener(view -> {
            if (!category.getProducts().isEmpty()) {
                showMessage("Cette catégorie ne peut pas être supprimée car elle possède un ou plusieurs produits.");
            } else {
                CategoryRepository categoryRepository = new CategoryRepository();
                categoryRepository.delete(category);
                categoryList.remove(category);
                dialog.dismiss();
                categoryAdapter.notifyDataSetChanged();

            }
        });
        save.setOnClickListener(view -> {
            if (libelle.getText().toString().isEmpty()) {
                showMessage("Vérifier le nom de la catégorie");
            } else {
                category.setName(libelle.getText().toString());
                dialog.dismiss();
                libelle.setEnabled(false);
                if (imageUpdated) {
                    cacheStore.deleteCacheFile(category.getPhoto());
                    cacheStore.saveCacheFile(category.getPhoto(), imageUpdatedBitmap);
                }

                categoryRepository.update(category, imageUpdated);
                categoryAdapter.notifyDataSetChanged();
            }

        });
        photo.setOnClickListener(view -> CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON).setActivityTitle("Image de la catégorie")
                .start(getActivity(), this)
        );
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
    public void onCategoryLongClickSelected(Category category) {

    }

    @Override
    public void onResume() {
        super.onResume();
        itemPresenter.initProductList(productsList);
        itemPresenter.initCategoriesList(categoryList);
    }
}

