package tn.meteor.efficaisse.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import tn.meteor.efficaisse.R;
import tn.meteor.efficaisse.ui.base.BaseActivity;
import tn.meteor.efficaisse.ui.customers.CustomersFragment;
import tn.meteor.efficaisse.ui.discounts.DiscountsFragment;
import tn.meteor.efficaisse.ui.items.ItemFragment;
import tn.meteor.efficaisse.ui.lounge.LoungeFragment;
import tn.meteor.efficaisse.ui.orders.OrdersFragment;
import tn.meteor.efficaisse.ui.recette.RecetteFragment;
import tn.meteor.efficaisse.ui.sale.SaleFragment;
import tn.meteor.efficaisse.ui.statistics.StatisticsFragment;
import tn.meteor.efficaisse.ui.stock.StockFragment;

public class HolderActivity extends BaseActivity {
    public static Activity activityHolder;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_holder);
        activityHolder=this;




        getSupportActionBar().setHomeAsUpIndicator(R.drawable.backtomain);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setElevation(0);


        String fragmentName= getIntent().getStringExtra("fragment");
        if (fragmentName.equals(getResources().getString(R.string.nav_sale))) {
            getFragmentManager().beginTransaction().replace(R.id.content_main, new SaleFragment()).commit();
            getSupportActionBar().setTitle(getResources().getString(R.string.nav_sale));


        } else if (fragmentName.equals(getResources().getString(R.string.nav_lounge))) {
            getFragmentManager().beginTransaction().replace(R.id.content_main, new LoungeFragment()).commit();
            getSupportActionBar().setTitle(getResources().getString(R.string.nav_lounge));


        } else if (fragmentName.equals(getResources().getString(R.string.nav_sale_history))) {
            getFragmentManager().beginTransaction().replace(R.id.content_main, new OrdersFragment()).commit();
            getSupportActionBar().setTitle(getResources().getString(R.string.nav_sale_history));


        } else if (fragmentName.equals(getResources().getString(R.string.nav_articles))) {

            getFragmentManager().beginTransaction().replace(R.id.content_main, new ItemFragment()).commit();
            getSupportActionBar().setTitle(getResources().getString(R.string.nav_articles));

        } else if (fragmentName.equals(getResources().getString(R.string.nav_receipt))) {

            getFragmentManager().beginTransaction().replace(R.id.content_main, new RecetteFragment()).commit();
            getSupportActionBar().setTitle(getResources().getString(R.string.nav_receipt));

        } else if (fragmentName.equals(getResources().getString(R.string.nav_customer_care))) {
            getFragmentManager().beginTransaction().replace(R.id.content_main, new CustomersFragment()).commit();
            getSupportActionBar().setTitle(getResources().getString(R.string.nav_customer_care));


        } else if (fragmentName.equals(getResources().getString(R.string.nav_discounts))) {
            getFragmentManager().beginTransaction().replace(R.id.content_main, new DiscountsFragment()).commit();
            getSupportActionBar().setTitle(getResources().getString(R.string.nav_discounts));


        } else if (fragmentName.equals(getResources().getString(R.string.nav_stats))) {
            getFragmentManager().beginTransaction().replace(R.id.content_main, new StatisticsFragment()).commit();
            getSupportActionBar().setTitle(getResources().getString(R.string.nav_stats));


        }else if (fragmentName.equals(getResources().getString(R.string.nav_stock))) {
            getFragmentManager().beginTransaction().replace(R.id.content_main, new StockFragment()).commit();

            getSupportActionBar().setTitle(getResources().getString(R.string.nav_stock));

        }
    }


    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }



}
