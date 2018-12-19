package tn.meteor.efficaisse;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import net.steamcrafted.loadtoast.LoadToast;

import tn.meteor.efficaisse.data.db.DataBaseManager;
import tn.meteor.efficaisse.data.preferences.AppPreferencesHelper;
import tn.meteor.efficaisse.data.repository.CashierRepository;
import tn.meteor.efficaisse.data.repository.CredentialsRepository;
import tn.meteor.efficaisse.data.repository.SessionRepository;
import tn.meteor.efficaisse.model.Cashier;
import tn.meteor.efficaisse.ui.analysis.AnalysisFragment;
import tn.meteor.efficaisse.ui.base.BaseActivity;
import tn.meteor.efficaisse.ui.cashier.CashierActivity;
import tn.meteor.efficaisse.ui.customers.CustomersFragment;
import tn.meteor.efficaisse.ui.discounts.DiscountsFragment;
import tn.meteor.efficaisse.ui.events.EventActivity;
import tn.meteor.efficaisse.ui.items.ItemFragment;
import tn.meteor.efficaisse.ui.login.LoginActivity;
import tn.meteor.efficaisse.ui.lounge.LoungeFragment;
import tn.meteor.efficaisse.ui.orders.OrdersFragment;
import tn.meteor.efficaisse.ui.recette.RecetteFragment;
import tn.meteor.efficaisse.ui.sale.SaleFragment;
import tn.meteor.efficaisse.ui.statistics.StatisticsFragment;
import tn.meteor.efficaisse.ui.stock.StockFragment;
import tn.meteor.efficaisse.utils.Constants;
import tn.meteor.efficaisse.utils.EfficaisseApplication;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private TextView username, email;
    private AppPreferencesHelper preferences;
    private AppPreferencesHelper preferences2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);

        username = navigationView.getHeaderView(0).findViewById(R.id.usernameDrawer);
        email = navigationView.getHeaderView(0).findViewById(R.id.emailDrawer);
        preferences = new AppPreferencesHelper(getApplicationContext(), Constants.SHARED_PREFERENCES_NAME.PREFERENCES_USER);

        preferences2 = new AppPreferencesHelper(getApplicationContext(), Constants.SHARED_PREFERENCES_NAME.PREFERENCES_ACCES_TYPE);


        Cashier cashier = CashierRepository.find(preferences.getUsername());
        if (cashier != null) {
            Log.d("1122", cashier.getName());
            username.setText(cashier.getName());
            email.setText(cashier.getUsername());
        }
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

        replaceFragment(new SaleFragment());


        CredentialsRepository credentialsRepository = new CredentialsRepository();
        if (credentialsRepository.find(cashier.getUsername()) == null) {
            preferences2.setAccesType("cashier");

            navigationView.getMenu().findItem(R.id.nav_change_user).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_discounts).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_receipt).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_stats).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_analyses).setVisible(false);

        } else {
            preferences2.setAccesType("manager");
        }


        if (preferences.getStoreType()==0)
        {
            navigationView.getMenu().findItem(R.id.nav_sale).setVisible(false);

        }else if (preferences.getStoreType()==1){

            navigationView.getMenu().findItem(R.id.nav_lounge).setVisible(false);


        }else if (preferences.getStoreType()==2){

            navigationView.getMenu().findItem(R.id.nav_lounge).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_stock).setVisible(false);

        }


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_disconnect) {
            SessionRepository.close();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
            preferences2.setAccesType("");
        } else if (id == R.id.nav_sale) {
            replaceFragment(new SaleFragment());
        } else if (id == R.id.nav_articles) {
            replaceFragment(new ItemFragment());
        } else if (id == R.id.nav_stock) {
            replaceFragment(new StockFragment());
        } else if (id == R.id.nav_sale_history) {
            replaceFragment(new OrdersFragment());
        } else if (id == R.id.nav_receipt) {
            replaceFragment(new RecetteFragment());
        } else if (id == R.id.nav_stats) {
            replaceFragment(new StatisticsFragment());
        } else if (id == R.id.nav_customer_care) {
            replaceFragment(new CustomersFragment());
        } else if (id == R.id.nav_discounts) {
            replaceFragment(new DiscountsFragment());
        } else if (id == R.id.nav_change_user) {
            startActivity(new Intent(this, CashierActivity.class));
        } else if (id == R.id.nav_lounge) {
            replaceFragment(new LoungeFragment());
        } else if (id == R.id.nav_throw_error) {
            throw new RuntimeException("Efficaisse Crash Test");
        }else if (id == R.id.nav_sync){
            if (!preferences.isSyncing()) {
                EfficaisseApplication.toast = new LoadToast(this);
                EfficaisseApplication.toast.setText("Syncronisation en cours ...");
                loadToast = EfficaisseApplication.toast;
                EfficaisseApplication.toast.show();
                preferences.setSyncing(true);
                DataBaseManager dataBaseManager = DataBaseManager.getInstance();
                dataBaseManager.synchronizationData(EfficaisseApplication.getInstance());
            }
        } else if (id == R.id.nav_event){
            startActivity(new Intent(this, EventActivity.class));
        }

        if (id == R.id.nav_analyses) {
            replaceFragment(new AnalysisFragment());
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void replaceFragment(android.app.Fragment fragment) {
        getFragmentManager().beginTransaction().replace(R.id.content_main, fragment).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

}

