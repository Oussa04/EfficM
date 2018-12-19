package tn.meteor.efficaisse.ui.discountAdd;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.leavjenn.smoothdaterangepicker.date.SmoothDateRangePickerFragment;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import tn.meteor.efficaisse.R;
import tn.meteor.efficaisse.model.Discount;
import tn.meteor.efficaisse.ui.base.BaseActivity;
import tn.meteor.efficaisse.ui.events.EventActivity;
import tn.meteor.efficaisse.utils.Constants;

/*
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog.OnDateSetListener;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog.OnTimeSetListener;
*/


public class DiscountAddActivity extends BaseActivity implements DiscountAddContract.View {


    private EditText libelle, discount;
    private Button  begin, end;
    private Button finish;
    private Button events;


    private DiscountAddPresenter discountAddPresenter;
    private Discount discountFinal;
    private TimePickerDialog tpd;
    private DatePickerDialog dpd;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_discount);
        discountAddPresenter = new DiscountAddPresenter(this);
        bindComponent();
        getSupportActionBar().setTitle("Ajouter une promotion");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setElevation(0);
        discountFinal = new Discount();

        SmoothDateRangePickerFragment smoothDateRangePickerFragment = SmoothDateRangePickerFragment.newInstance(
                new SmoothDateRangePickerFragment.OnDateRangeSetListener() {
                    @Override
                    public void onDateRangeSet(SmoothDateRangePickerFragment view,
                                               int yearStart, int monthStart,
                                               int dayStart, int yearEnd,
                                               int monthEnd, int dayEnd) {

                        monthEnd = monthEnd+1;
                        monthStart = monthStart+1;


                        dateBeginning = dayStart + "/" + monthStart + "/" + yearStart +"";
                        dateEnd = dayEnd + "/" + monthEnd + "/" + yearEnd+"";
                        begin.setText(dateBeginning);
                        end.setText(dateEnd);
                        Toast.makeText(DiscountAddActivity.this, yearEnd+" - "+yearStart, Toast.LENGTH_SHORT).show();

                    }


                });
        smoothDateRangePickerFragment.setMinDate(Calendar.getInstance());
      begin.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
                  smoothDateRangePickerFragment.show(getFragmentManager(), "smoothDateRangePicker");
          }
      });
        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                smoothDateRangePickerFragment.show(getFragmentManager(), "smoothDateRangePicker");
            }
        });

        finish.setOnClickListener(view -> {


            if (TextUtils.isEmpty(libelle.getText().toString()) || TextUtils.isEmpty(begin.getText().toString()) || TextUtils.isEmpty(discount.getText().toString())|| Float.parseFloat(discount.getText().toString())<0 || Float.parseFloat(discount.getText().toString())>100)
                showMessage("Veuillez vérifier les champs");
            else {

                DateFormat format = new SimpleDateFormat(Constants.DATE_FORMAT);

                discountFinal.setName(libelle.getText().toString());
                try {
                    discountFinal.setDatebegin(format.parse(dateBeginning));
                    discountFinal.setDateend(format.parse(dateEnd));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                discountFinal.setDiscount(Float.parseFloat(discount.getText().toString()));


                discountAddPresenter.addDiscount(discountFinal);


            }

        });
events.setVisibility(View.GONE);
        events.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DiscountAddActivity.this, EventActivity.class));
            }
        });
    }


    @Override
    public void bindComponent() {


        libelle = findViewById(R.id.libelle);
        begin = findViewById(R.id.begin);
        end = findViewById(R.id.end);
        events = findViewById(R.id.events);
        discount = findViewById(R.id.discount);
        finish = findViewById(R.id.finish);



    }

    @Override
    public void updateUI() {
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }


    private String dateBeginning;
    private String dateEnd;


}
