package tn.meteor.efficaisse.ui.product;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.List;

import tn.meteor.efficaisse.R;
import tn.meteor.efficaisse.data.preferences.AppPreferencesHelper;
import tn.meteor.efficaisse.data.repository.CategoryRepository;
import tn.meteor.efficaisse.model.Category;
import tn.meteor.efficaisse.model.Product;
import tn.meteor.efficaisse.model.Product_Table;
import tn.meteor.efficaisse.ui.base.BaseActivity;
import tn.meteor.efficaisse.utils.CacheStore;
import tn.meteor.efficaisse.utils.Constants;
import tn.meteor.efficaisse.utils.SquareImageView;


public class ProductActivity extends BaseActivity implements ProductContract.View {


    private EditText libelle, cost, price, quantity;
    private LinearLayout addPhotoGallery;
    private LinearLayout addPhotoCamera;
    private SquareImageView photo;
    private Switch favoris;
    private Boolean setFavoris = false;
    private Button finish;
    private Button toMarchandise;
    private Button toProduit;
    private Spinner category;
    protected Uri resultUri;
    private ProductPresenter productPresenter;
    private Boolean isProduit = false;
    private Boolean imageChanged = false;
    private AppPreferencesHelper preferences;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        productPresenter = new ProductPresenter(this);
        bindComponent();
        preferences = new AppPreferencesHelper(getApplicationContext(), Constants.SHARED_PREFERENCES_NAME.PREFERENCES_USER);

        Product product = new Product();
        CategoryRepository categoryRepository = new CategoryRepository();
        List<Category> categories = categoryRepository.findAll();
      getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


        ArrayAdapter<Category> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);


        category.setAdapter(adapter);
        category.setSelection(0);
        product.setCategory(categories.get(0));
        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                product.setCategory((Category) adapterView.getItemAtPosition(i)
                );
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        addPhotoGallery.setOnClickListener(view -> CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON).setActivityTitle("Image de produit")
                .start(ProductActivity.this));
        favoris.setChecked(false);
        favoris.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (!setFavoris && isChecked) {
                setFavoris = true;

            } else {
                setFavoris = false;

            }
        });


        finish.setOnClickListener(view -> {

            if (SQLite.select().from(Product.class).where(Product_Table.name.eq(libelle.getText().toString())).querySingle() != null) {
                showMessage("Un produit avec le mème nom existe déjà");
            } else if (isProduit) {
                if (TextUtils.isEmpty(libelle.getText().toString()) || TextUtils.isEmpty(price.getText().toString()))
                    showMessage("Veuillez remplir tous les champs");
                else {
                    if (!imageChanged) {
                        Toast.makeText(getApplicationContext(), "Veuillez ajouter une image", Toast.LENGTH_SHORT).show();
                    } else {
                        CacheStore cacheStore = CacheStore.getInstance();
                        try {
                            cacheStore.saveCacheFile(libelle.getText().toString().trim(), MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        product.setName(libelle.getText().toString());
                        product.setPrice(Float.parseFloat(price.getText().toString()));
                        product.setFavoris(setFavoris);
                        product.setPhoto(libelle.getText().toString().trim());

                        product.setCost(null);
                        product.setStockQuantity(null);


                        productPresenter.addProduct(product);
                    }
                }
            } else if (!isProduit) {
                if (TextUtils.isEmpty(libelle.getText().toString()) || TextUtils.isEmpty(price.getText().toString()) || TextUtils.isEmpty(cost.getText().toString()) || TextUtils.isEmpty(quantity.getText().toString()))
                    showMessage("Veuillez remplir tous les champs");
                else {
                    if (!imageChanged) {
                        Toast.makeText(getApplicationContext(), "Veuillez ajouter une image", Toast.LENGTH_SHORT).show();
                    } else {
                        CacheStore cacheStore = CacheStore.getInstance();
                        try {
                            cacheStore.saveCacheFile(libelle.getText().toString().trim(), MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        product.setName(libelle.getText().toString());
                        product.setPrice(Double.parseDouble(price.getText().toString()));
                        product.setFavoris(setFavoris);
                        product.setPhoto(libelle.getText().toString().trim());

                        product.setCost(Double.parseDouble(cost.getText().toString()));
                        product.setStockQuantity(Double.parseDouble(quantity.getText().toString()));

                        productPresenter.addProduct(product);
                    }
                }
            }
        });


        toMarchandise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toMarchandise.setTextColor(getResources().getColor(R.color.colorAccent));
                toProduit.setTextColor(Color.LTGRAY);
                isProduit = false;
                quantity.setVisibility(View.VISIBLE);
                cost.setVisibility(View.VISIBLE);

            }
        });

        toProduit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toProduit.setTextColor(getResources().getColor(R.color.colorAccent));
                toMarchandise.setTextColor(Color.LTGRAY);
                isProduit = true;

                quantity.setVisibility(View.GONE);
                cost.setVisibility(View.GONE);
            }
        });

        quantity.setVisibility(View.VISIBLE);
        cost.setVisibility(View.VISIBLE);
        if (preferences.getStoreType() == 2) {

            toProduit.setVisibility(View.GONE);
            toMarchandise.setVisibility(View.GONE);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
                    imageChanged = true;
                    Picasso.with(this)
                            .load(resultUri).into(photo);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }

    @Override
    public void bindComponent() {

        addPhotoCamera = findViewById(R.id.addPhotoCamera);
        addPhotoGallery = findViewById(R.id.addPhotoGallery);
        photo = findViewById(R.id.thumbnail);
        favoris = findViewById(R.id.favorite);
        finish = findViewById(R.id.finish);
        libelle = findViewById(R.id.libelle);
        cost = findViewById(R.id.cost);
        price = findViewById(R.id.price);
        category = findViewById(R.id.category);
        toMarchandise = findViewById(R.id.toMarchandise);
        toProduit = findViewById(R.id.toProduit);
        quantity = findViewById(R.id.quantity);


    }

    @Override
    public void updateUI() {
        finish();
    }
}
