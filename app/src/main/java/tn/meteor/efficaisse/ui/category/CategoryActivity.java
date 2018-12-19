package tn.meteor.efficaisse.ui.category;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;

import tn.meteor.efficaisse.R;
import tn.meteor.efficaisse.model.Category;
import tn.meteor.efficaisse.model.Category_Table;
import tn.meteor.efficaisse.ui.base.BaseActivity;
import tn.meteor.efficaisse.utils.CacheStore;
import tn.meteor.efficaisse.utils.SquareImageView;


public class CategoryActivity extends BaseActivity implements CategoryContract.View {


    private EditText libelle, quantity, price;
    private LinearLayout addPhotoGallery;
    private LinearLayout addPhotoCamera;
    private SquareImageView photo;

    private Boolean imageChanged = false;
    private Button finish;
    protected Uri resultUri;
    private CategoryPresenter categoryPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        categoryPresenter = new CategoryPresenter(this);
        bindComponent();
        Category category = new Category();

        addPhotoGallery.setOnClickListener(view -> CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON).setActivityTitle("Image de l'ingrédient")
                .start(CategoryActivity.this));


        finish.setOnClickListener(view -> {

            CacheStore cacheStore = CacheStore.getInstance();


            if (TextUtils.isEmpty(libelle.getText().toString())) {
                libelle.setHint("Ce champ est obligatoire");
            } else if (!imageChanged) {
                Toast.makeText(getApplicationContext(), "Veuillez ajouter une image", Toast.LENGTH_LONG).show();
            } else if (SQLite.select().from(Category.class).where(Category_Table.name.eq(libelle.getText().toString())).querySingle() != null) {
                showMessage("Cette catégorie existe déjà");
            } else {


                try {
                    cacheStore.saveCacheFile(libelle.getText().toString().trim(), MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                category.setName(libelle.getText().toString());
                category.setPhoto(libelle.getText().toString().trim());
                categoryPresenter.addCategory(category);
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
        finish = findViewById(R.id.finish);
        libelle = findViewById(R.id.libelle);


    }

    @Override
    public void updateUI() {
        finish();
    }
}
