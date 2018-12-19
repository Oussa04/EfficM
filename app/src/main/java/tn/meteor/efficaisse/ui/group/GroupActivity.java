package tn.meteor.efficaisse.ui.group;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import tn.meteor.efficaisse.R;
import tn.meteor.efficaisse.model.Customer;
import tn.meteor.efficaisse.model.CustomerGroup;
import tn.meteor.efficaisse.model.Customer_Table;
import tn.meteor.efficaisse.ui.base.BaseActivity;


public class GroupActivity extends BaseActivity implements GroupContract.View {


    private EditText name, discount;

    private Button finish;


    private GroupPresenter groupPresenter;


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customergroup);
        groupPresenter = new GroupPresenter(this);
        bindComponent();

        CustomerGroup customerGroup = new CustomerGroup();


        discount.setText("0");
//        discount.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                String s = discount.getText().toString();
//
//                if (s.contains("-")) {
//                    s.replace("-", "");
//                }
//                if (s.contains("%")) {
//                    s.replace("%", "");
//                }
//
//                s = s + "%";
//                discount.setText(s);
//                s = s.substring(0, s.length() - 1);
//                if (Float.parseFloat(s) > 100) {
//
//                    discount.setText("100%");
//
//                } else if (Float.parseFloat(s) < 0) {
//
//                    discount.setText("0%");
//
//                }
//
//                if (discount.getText().toString().equals("%")) {
//
//                    discount.setText("0%");
//                }
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });


        finish.setOnClickListener(view -> {

            if (SQLite.select().from(Customer.class).where(Customer_Table.name.eq(name.getText().toString())).querySingle() != null) {
                showMessage("Un client avec le mème nom existe déjà");
            } else {
                if (TextUtils.isEmpty(name.getText().toString()) || TextUtils.isEmpty(discount.getText().toString()))
                    showMessage("Veuillez remplir tous les champs");
                else {


                    customerGroup.setName(name.getText().toString());
                    customerGroup.setDiscount(Float.parseFloat(discount.getText().toString()));


                    groupPresenter.addCustomerGroup(customerGroup);

                }
            }
        });


    }

    @Override
    public void bindComponent() {


        finish = findViewById(R.id.finish);
        name = findViewById(R.id.name);
        discount = findViewById(R.id.discount);


    }

    @Override
    public void updateUI() {
        finish();
    }
}
