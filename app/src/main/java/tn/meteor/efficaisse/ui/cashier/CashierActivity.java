package tn.meteor.efficaisse.ui.cashier;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.andrognito.patternlockview.PatternLockView;

import tn.meteor.efficaisse.R;
import tn.meteor.efficaisse.model.Cashier;
import tn.meteor.efficaisse.ui.base.BaseActivity;


public class CashierActivity extends BaseActivity implements CashierContract.View {


    private EditText name, username;
    private Button finish;
    private Spinner group;
    private CashierPresenter cashierPresenter;
    private Bitmap qrCodeBitmap = null;
    private Cashier cashier;

    private PatternLockView mPatternLockView;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cashier);
        cashierPresenter = new CashierPresenter(this);
        bindComponent();
        getSupportActionBar().setTitle("Ajouter un utilisateur");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setElevation(0);
        cashier = new Cashier();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        finish.setOnClickListener(view -> {


            if (TextUtils.isEmpty(name.getText().toString()) || TextUtils.isEmpty(username.getText().toString()) )
                showMessage("Veuillez vérifier les champs");
            else {


                cashier.setName(name.getText().toString());

                cashier.setUsername(username.getText().toString());


                cashierPresenter.addCashier(cashier);


            }

        });


    }


    @Override
    public void bindComponent() {


        finish = findViewById(R.id.finish);
        username = findViewById(R.id.username);
        name = findViewById(R.id.name);




    }

    @Override
    public void updateUI() {
        finish();
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

}
