package tn.meteor.efficaisse.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.annimon.stream.Stream;

import net.steamcrafted.loadtoast.LoadToast;

import java.util.ArrayList;
import java.util.List;

import tn.meteor.efficaisse.R;
import tn.meteor.efficaisse.adapter.MenuItemsAdapter;
import tn.meteor.efficaisse.data.db.DataBaseManager;
import tn.meteor.efficaisse.data.preferences.AppPreferencesHelper;
import tn.meteor.efficaisse.data.repository.CashierRepository;
import tn.meteor.efficaisse.data.repository.CredentialsRepository;
import tn.meteor.efficaisse.data.repository.SessionRepository;
import tn.meteor.efficaisse.data.repository.StoreRepository;
import tn.meteor.efficaisse.model.Cashier;
import tn.meteor.efficaisse.model.Store;
import tn.meteor.efficaisse.ui.base.BaseActivity;
import tn.meteor.efficaisse.ui.cashier.CashierActivity;
import tn.meteor.efficaisse.ui.login.LoginActivity;
import tn.meteor.efficaisse.utils.Constants;
import tn.meteor.efficaisse.utils.EfficaisseApplication;
import tn.meteor.efficaisse.utils.HomeMenu;

public class Homie extends BaseActivity implements MenuItemsAdapter.MenuItemListener {
    private StaggeredGridLayoutManager _sGridLayoutManager;
    private List<HomeMenu> menuItems;

    private TextView username, email, battery;
    private AppPreferencesHelper preferences;
    private AppPreferencesHelper preferences2;
    private Cashier cashier;
    private LinearLayout aa,cashiersGroup;
    private ImageButton disconnect, sync, cashiers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_final);


        username = findViewById(R.id.name);
        email = findViewById(R.id.username);
        disconnect = findViewById(R.id.disconnect);
        cashiers = findViewById(R.id.cashiers);
        battery = findViewById(R.id.battery);
        cashiersGroup = findViewById(R.id.cashiersGroup);
        sync = findViewById(R.id.sync);
        sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                syncIt();
            }
        });
        disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){

                if (CashierRepository.canLogout()){
                SessionRepository.close();
                finish();
                startActivity(new Intent(Homie.this, LoginActivity.class));
                preferences2.setAccesType("");
                }
                else {
                    showMessage("Vous ne pouvez pas se déconnecter, commandes inpayées");
                }
//
//                    SessionRepository.close();
//                    finish();
//                    startActivity(new Intent(Homie.this, LoginActivity.class));
//                    preferences2.setAccesType("");

            }
        });
        cashiers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Homie.this, CashierActivity.class));

            }
        });
        preferences = new AppPreferencesHelper(getApplicationContext(), Constants.SHARED_PREFERENCES_NAME.PREFERENCES_USER);

        preferences2 = new AppPreferencesHelper(getApplicationContext(), Constants.SHARED_PREFERENCES_NAME.PREFERENCES_ACCES_TYPE);


        cashier = CashierRepository.find(preferences.getUsername());
        StoreRepository storeRepository = new StoreRepository();
        Store store = storeRepository.getActualStore();
        if (cashier != null) {

            username.setText(cashier.getName());
            email.setText(store.getName());
        }


        RecyclerView recyclerView = findViewById(R.id.menu);
        recyclerView.setHasFixedSize(true);

        _sGridLayoutManager = new StaggeredGridLayoutManager(4,
                StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(_sGridLayoutManager);

        List<HomeMenu> sList = getMenuItems();
        MenuItemsAdapter menuItemsAdapter = new MenuItemsAdapter(this, this, sList);
        recyclerView.setAdapter(menuItemsAdapter);

    }

    private void syncIt() {
        if (!preferences.isSyncing()) {
            EfficaisseApplication.toast = new LoadToast(this);
            EfficaisseApplication.toast.setText("Syncronisation en cours ...");
            loadToast = EfficaisseApplication.toast;
            EfficaisseApplication.toast.show();
            preferences.setSyncing(true);
            DataBaseManager dataBaseManager = DataBaseManager.getInstance();
            dataBaseManager.synchronizationData(EfficaisseApplication.getInstance());
        }
    }

    @Override
    public void onMenuItemClicked(HomeMenu homeMenu) {
        if (homeMenu.getItemText().equals(getResources().getString(R.string.nav_sale))) {
            replaceFragment(getResources().getString(R.string.nav_sale));


        } else if (homeMenu.getItemText().equals(getResources().getString(R.string.nav_lounge))) {
            replaceFragment(getResources().getString(R.string.nav_lounge));


        } else if (homeMenu.getItemText().equals(getResources().getString(R.string.nav_sale_history))) {
            replaceFragment(getResources().getString(R.string.nav_sale_history));


        } else if (homeMenu.getItemText().equals(getResources().getString(R.string.nav_articles))) {

            replaceFragment(getResources().getString(R.string.nav_articles));

        } else if (homeMenu.getItemText().equals(getResources().getString(R.string.nav_receipt))) {

            replaceFragment(getResources().getString(R.string.nav_receipt));

        } else if (homeMenu.getItemText().equals(getResources().getString(R.string.nav_customer_care))) {
            replaceFragment(getResources().getString(R.string.nav_customer_care));


        } else if (homeMenu.getItemText().equals(getResources().getString(R.string.nav_discounts))) {
            replaceFragment(getResources().getString(R.string.nav_discounts));


        } else if (homeMenu.getItemText().equals(getResources().getString(R.string.nav_stats))) {
            replaceFragment(getResources().getString(R.string.nav_stats));


        } else if (homeMenu.getItemText().equals(getResources().getString(R.string.nav_stock))) {
            replaceFragment(getResources().getString(R.string.nav_stock));


        }
    }

    @Override
    public void onMenuItemLongClicked(HomeMenu homeMenu) {

    }

    public List<HomeMenu> getMenuItems() {


        List<HomeMenu> list = new ArrayList<>();

        list.add(new HomeMenu(R.drawable.shopping, getResources().getString(R.string.nav_sale), Color.parseColor("#EF5350")));
        list.add(new HomeMenu(R.drawable.eating, getResources().getString(R.string.nav_lounge), Color.parseColor("#FFCA28")));
        list.add(new HomeMenu(R.drawable.order, getResources().getString(R.string.nav_sale_history), Color.parseColor("#5C6BC0")));
        list.add(new HomeMenu(R.drawable.laundry, getResources().getString(R.string.nav_articles), Color.parseColor("#66BB6A")));
        list.add(new HomeMenu(R.drawable.stock, getResources().getString(R.string.nav_stock), Color.parseColor("#FFCA28")));
        list.add(new HomeMenu(R.drawable.moneyhome, getResources().getString(R.string.nav_receipt), Color.parseColor("#EC407A")));
        list.add(new HomeMenu(R.drawable.people, getResources().getString(R.string.nav_customer_care), Color.parseColor("#EF5350")));
        list.add(new HomeMenu(R.drawable.tag, getResources().getString(R.string.nav_discounts), Color.parseColor("#EC407A")));
        list.add(new HomeMenu(R.drawable.planning, getResources().getString(R.string.nav_stats), Color.parseColor("#5C6BC0")));


        CredentialsRepository credentialsRepository = new CredentialsRepository();
        if (credentialsRepository.find(cashier.getUsername()) == null) {
            preferences2.setAccesType("cashier");
            cashiersGroup.setVisibility(View.GONE);

            try {
                list.remove(Stream.of(list).filter(entry -> entry.getItemText().equals(getResources().getString(R.string.nav_discounts))).findFirst().get());
                list.remove(Stream.of(list).filter(entry -> entry.getItemText().equals(getResources().getString(R.string.nav_receipt))).findFirst().get());
                list.remove(Stream.of(list).filter(entry -> entry.getItemText().equals(getResources().getString(R.string.nav_stats))).findFirst().get());
                list.remove(Stream.of(list).filter(entry -> entry.getItemText().equals(getResources().getString(R.string.nav_stats))).findFirst().get());

            } catch (Exception e) {


            }

            //                navigationView.getMenu().findItem(R.id.nav_change_user).setVisible(false);


        } else {
            preferences2.setAccesType("manager");
        }


        if (preferences.getStoreType() == 0) //lounge

        {
            try {
                list.remove(Stream.of(list).filter(entry -> entry.getItemText().equals(getResources().getString(R.string.nav_sale))).findFirst().get());

            } catch (Exception e) {


            }


        } else if (preferences.getStoreType() == 1) { //fastfood
            try {
                list.remove(Stream.of(list).filter(entry -> entry.getItemText().equals(getResources().getString(R.string.nav_lounge))).findFirst().get());


            } catch (Exception e) {


            }


        } else if (preferences.getStoreType() == 2) { //Magazin
            try {
                list.remove(Stream.of(list).filter(entry -> entry.getItemText().equals(getResources().getString(R.string.nav_lounge))).findFirst().get());
                list.remove(Stream.of(list).filter(entry -> entry.getItemText().equals(getResources().getString(R.string.nav_stock))).findFirst().get());
            } catch (Exception e) {


            }

        }


        return list;
    }

    public void replaceFragment(String fragmentName) {
        Intent mIntent = new Intent(this, HolderActivity.class);
        mIntent.putExtra("fragment", fragmentName);
        startActivity(mIntent);
    }


    @Override
    protected void onBatteryChanged(boolean isCharging, float percentage) {
        if (isCharging) {


            Math.round(percentage);
            battery.setText(Math.round(percentage*100) + "% (En charge)");
        } else {


            Math.round(percentage);
            battery.setText(Math.round(percentage*100) + "%");

        }

        if(Math.round(percentage*100)<16){

            battery.setTextColor(Color.parseColor("#FF3D00"));
        }else {

            battery.setTextColor(Color.parseColor("#757575"));
        }

        super.onBatteryChanged(isCharging, percentage);
    }
}
