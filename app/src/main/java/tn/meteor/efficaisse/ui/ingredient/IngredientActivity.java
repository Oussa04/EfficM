package tn.meteor.efficaisse.ui.ingredient;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jaredrummler.materialspinner.MaterialSpinner;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;

import tn.meteor.efficaisse.R;
import tn.meteor.efficaisse.data.db.LogSystem;
import tn.meteor.efficaisse.model.Ingredient;
import tn.meteor.efficaisse.model.Ingredient_Table;
import tn.meteor.efficaisse.model.Unit;
import tn.meteor.efficaisse.ui.base.BaseActivity;
import tn.meteor.efficaisse.utils.CacheStore;
import tn.meteor.efficaisse.utils.SquareImageView;


public class IngredientActivity extends BaseActivity implements IngredientContract.View {


    private EditText libelle, quantity, price;
    private LinearLayout addPhotoGallery;
    private LinearLayout addPhotoCamera;
    private SquareImageView photo;
    private Boolean imageChanged=false;
    private Button finish;
    protected Uri resultUri;
    private IngredientPresenter ingredientPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient);
        ingredientPresenter = new IngredientPresenter(this);
        bindComponent();
        Ingredient ingredient = new Ingredient();
        ingredient.setUnit(Unit.L);

        MaterialSpinner spinner = findViewById(R.id.unity);
        spinner.setAdapter(new ArrayAdapter<Unit>(this, android.R.layout.simple_spinner_item, Unit.values()));
        spinner.setOnItemSelectedListener((view, position, id, item) -> {
            ingredient.setUnit(Unit.valueOf(item.toString()));
            Toast.makeText(IngredientActivity.this, ingredient.getUnit().toString(), Toast.LENGTH_SHORT).show();
        });
        addPhotoGallery.setOnClickListener(view -> CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON).setActivityTitle("Image de l'ingrédient")
                .start(IngredientActivity.this));


        finish.setOnClickListener(view -> {

            if (!imageChanged){
                Toast.makeText(getApplicationContext(),"Veuillez ajouter une image",Toast.LENGTH_SHORT).show();
            } else if(TextUtils.isEmpty(libelle.getText().toString()) || TextUtils.isEmpty(price.getText().toString())
                    || TextUtils.isEmpty(quantity.getText().toString())){
                Toast.makeText(getApplicationContext(),"Veuillez remplir tous les champs",Toast.LENGTH_SHORT).show();
            } else if(SQLite.select().from(Ingredient.class).where(Ingredient_Table.name.eq(libelle.getText().toString())).querySingle()!=null){
                showMessage("Cet ingrédient existe déjà");
            }

            else{
                CacheStore cacheStore = CacheStore.getInstance();
                try {
                    cacheStore.saveCacheFile(libelle.getText().toString().trim(), MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ingredient.setName(libelle.getText().toString());
                ingredient.setPrice(Float.parseFloat(price.getText().toString()));
                ingredient.setStockQuantity(Float.parseFloat(quantity.getText().toString()));
                ingredient.setPhoto(libelle.getText().toString().trim());

                ingredientPresenter.addIngredient(ingredient);
            }
        });
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
                    imageChanged=true;
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
        finish = findViewById(R.id.finish);
        libelle = findViewById(R.id.libelle);
        quantity = findViewById(R.id.stockQuantity);
        price = findViewById(R.id.price);


    }

    @Override
    public void updateUI() {
        finish();
    }
}
