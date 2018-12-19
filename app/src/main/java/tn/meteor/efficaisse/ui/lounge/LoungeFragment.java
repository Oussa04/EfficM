package tn.meteor.efficaisse.ui.lounge;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import tn.meteor.efficaisse.R;
import tn.meteor.efficaisse.adapter.CommandeAdapter;
import tn.meteor.efficaisse.adapter.LoungeTableAdapter;
import tn.meteor.efficaisse.adapter.TicketCommandeAdapter;
import tn.meteor.efficaisse.data.repository.DetailCommandeRepository;
import tn.meteor.efficaisse.data.repository.LoungTablesRepository;
import tn.meteor.efficaisse.model.Commande;
import tn.meteor.efficaisse.model.DetailCommande;
import tn.meteor.efficaisse.model.LoungeTable;
import tn.meteor.efficaisse.ui.HolderActivity;
import tn.meteor.efficaisse.ui.base.BaseFragment;
import tn.meteor.efficaisse.ui.payement.PayementActivity;
import tn.meteor.efficaisse.ui.sale.SaleFragment;
import tn.meteor.efficaisse.utils.TicketDividerItemDecoration;


public class LoungeFragment extends BaseFragment implements TicketCommandeAdapter.TicketListener, CommandeAdapter.CommandeListener, LoungeContract.View, LoungeTableAdapter.LoungeTableListener {


    private LoungeContract.Presenter loungePresenter;
    private RecyclerView tables;
    private List<LoungeTable> loungeTableList;
    private LoungeTableAdapter loungeTableAdapter;
    private LinearLayout aa;
    private GridLayoutManager manager;
    private GridLayoutManager manager2;
    private RelativeLayout sidepanel;

    public static LoungeFragment newInstance() {
        LoungeFragment fragment = new LoungeFragment();
        return fragment;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.lounge_edit, menu);
        MenuItem item = menu.findItem(R.id.cancel);
        item.setVisible(false);


        item = menu.findItem(R.id.add_table);
        item.setVisible(true);
        if (isMerging || isChanging) {
            item = menu.findItem(R.id.cancel);
            item.setVisible(true);

            item = menu.findItem(R.id.add_table);
            item.setVisible(false);


        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {


            case R.id.add_table:

                showAddTableDialog();
                return true;
            case R.id.cancel:

                mergeId = 0;
                changeId = 0;
                isMerging = false;
                isChanging = false;

                ((HolderActivity) getActivity()).getSupportActionBar().setTitle("Tables");
                ((HolderActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
                getActivity().getWindow().setNavigationBarColor(getResources().getColor(R.color.black));
                getActivity().invalidateOptionsMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tables, container, false);
        loungePresenter = new LoungePresenter(this);
        tables = v.findViewById(R.id.tables);
        sidepanel = v.findViewById(R.id.sidepanel);
        aa = v.findViewById(R.id.aa);


        details = v.findViewById(R.id.details);

        pay = v.findViewById(R.id.pay);
        sum = v.findViewById(R.id.sum);
        merge = v.findViewById(R.id.merge);
        changeTable = v.findViewById(R.id.changeTable);


        sidepanel.setVisibility(View.GONE);
        loungeTableList = new ArrayList<>();
        aa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissSidePanel();
            }
        });
        ((HolderActivity) getActivity()).getSupportActionBar().setTitle("Tables");

        loungeTableAdapter = new LoungeTableAdapter(getActivity(), this, loungeTableList);
        tables.setAdapter(loungeTableAdapter);
        manager = new GridLayoutManager(getActivity(), 4, GridLayoutManager.VERTICAL, false);
        manager2 = new GridLayoutManager(getActivity(), 3, GridLayoutManager.VERTICAL, false);

        tables.setLayoutManager(manager);
        loungePresenter.initTables(loungeTableList);

        setHasOptionsMenu(true);
        tables.setOnTouchListener((v1, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN
                    && tables.findChildViewUnder(event.getX(), event.getY()) == null) {
                dismissSidePanel();
            }
            return false;
        });

//        h.postDelayed(new Runnable() {
//            public void run() {
//                loungeTableList.clear();
//
//                loungePresenter.initTables(loungeTableList);
//                runnable = this;
//                h.postDelayed(runnable, delay);
//            }
//        }, delay);

        return v;
    }


    @Override
    public void onResume() {
        super.onResume();

    }


    public void dismissSidePanel() {

        sidepanel.setVisibility(View.GONE);
        tables.setLayoutManager(manager);
        loungeTableAdapter.notifyDataSetChanged();
    }
    private double totalSum = 0f;
    public void showSidePanel(Commande commande) {

        sidepanel.setVisibility(View.VISIBLE);
        tables.setLayoutManager(manager2);
        loungeTableAdapter.notifyDataSetChanged();

        DetailCommandeRepository detailCommandeRepository = new DetailCommandeRepository();




        totalSum = DetailCommandeRepository.getCommandeSum(commande);

        if (commande.getCustomer() != null&& commande.getCustomer().getCustomerGroup()!=null) {
            totalSum = totalSum - totalSum * (commande.getCustomer().getCustomerGroup().getDiscount() / 100);
            sum.setText( String.format("%.3f",totalSum) + " dt. (" + detailCommandeRepository.getCommandeQuantity(commande) + " élements) - Remise -"+commande.getCustomer().getCustomerGroup().getDiscount() +"%");
        }else {

            sum.setText( String.format("%.3f",totalSum) + " dt. (" + detailCommandeRepository.getCommandeQuantity(commande) + " élements)");


        }









        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        details.setLayoutManager(mLayoutManager);
        details.setItemAnimator(new DefaultItemAnimator());
        details.addItemDecoration(new TicketDividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL, 0));
        ticketAdapter = new TicketCommandeAdapter(getActivity(), this, commande.getProducts(), false);
        details.setAdapter(ticketAdapter);

        if (commande.isStatus()) {
            pay.setVisibility(View.GONE);
        } else {
            pay.setVisibility(View.VISIBLE);
            pay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    gotoPay(commande);

                }
            });
        }

        merge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isMerging = true;
                dismissSidePanel();
                mergeId = commande.getCommandeNumber();
                ((HolderActivity) getActivity()).getSupportActionBar().setTitle("Sélectionner une table");
                ((HolderActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorGreen)));
                getActivity().invalidateOptionsMenu();

                getActivity().getWindow().setNavigationBarColor(getResources().getColor(R.color.colorGreen));
            }
        });

        changeTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isChanging = true;
                dismissSidePanel();
                changeId = commande.getCommandeNumber();
                ((HolderActivity) getActivity()).getSupportActionBar().setTitle("Sélectionner une table");
                ((HolderActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorGreen)));
                getActivity().invalidateOptionsMenu();
                getActivity().getWindow().setNavigationBarColor(getResources().getColor(R.color.colorGreen));
            }
        });


    }

    @Override
    public void onDestroy() {
//        h.removeCallbacks(runnable);


        mergeId = 0;
        changeId = 0;
        isMerging = false;
        isChanging = false;

        ((HolderActivity) getActivity()).getSupportActionBar().setTitle("Tables");
        ((HolderActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
        getActivity().getWindow().setNavigationBarColor(getResources().getColor(R.color.black));
        getActivity().invalidateOptionsMenu();
        super.onDestroy();
    }

    @Override
    public void onPause() {

        mergeId = 0;
        changeId = 0;
        isMerging = false;
        isChanging = false;

        ((HolderActivity) getActivity()).getSupportActionBar().setTitle("Tables");
        ((HolderActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
        getActivity().getWindow().setNavigationBarColor(getResources().getColor(R.color.black));
        getActivity().invalidateOptionsMenu();

//        h.removeCallbacks(runnable);
        super.onPause();
    }

    @Override
    public void updateUI() {
        loungeTableAdapter.notifyDataSetChanged();
    }


    private RecyclerView details;
    private Button pay, close, merge, changeTable;
    private TextView quantity, sum;
    private TicketCommandeAdapter ticketAdapter;


    private Boolean isMerging = false;
    private int mergeId = 0;

    private Boolean isChanging = false;
    private int changeId = 0;
    private LoungeTable loungeTableOrigin;

    private void showCommandeDetails(Commande commande) {

        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity())
                .customView(R.layout.dialog_loungetable_details, false);

        MaterialDialog dialog = builder.build();
        details = (RecyclerView) dialog.findViewById(R.id.details);

        pay = (Button) dialog.findViewById(R.id.pay);
        quantity = (TextView) dialog.findViewById(R.id.quantity);
        sum = (TextView) dialog.findViewById(R.id.sum);
        merge = (Button) dialog.findViewById(R.id.merge);
        changeTable = (Button) dialog.findViewById(R.id.changeTable);
        close = (Button) dialog.findViewById(R.id.close);

        close.setOnClickListener(view -> {
            dialog.dismiss();
        });
        DetailCommandeRepository detailCommandeRepository = new DetailCommandeRepository();
        quantity.setText("Articles (" + detailCommandeRepository.getCommandeQuantity(commande) + ")");
        sum.setText("Somme totale= " + detailCommandeRepository.getCommandeSum(commande) + " dt.");
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        details.setLayoutManager(mLayoutManager);
        details.setItemAnimator(new DefaultItemAnimator());
        details.addItemDecoration(new TicketDividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL, 0));
        ticketAdapter = new TicketCommandeAdapter(getActivity(), this, commande.getProducts(), false);
        details.setAdapter(ticketAdapter);

        if (commande.isStatus()) {
            pay.setVisibility(View.GONE);
        } else {
            pay.setVisibility(View.VISIBLE);
            pay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    gotoPay(commande);

                }
            });
        }

        merge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isMerging = true;
                dialog.dismiss();
                mergeId = commande.getCommandeNumber();
                ((HolderActivity) getActivity()).getSupportActionBar().setTitle("Sélectionner une table");
                ((HolderActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorGreen)));
                getActivity().invalidateOptionsMenu();

                getActivity().getWindow().setNavigationBarColor(getResources().getColor(R.color.colorGreen));
            }
        });

        changeTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isChanging = true;
                dialog.dismiss();
                changeId = commande.getCommandeNumber();
                ((HolderActivity) getActivity()).getSupportActionBar().setTitle("Sélectionner une table");
                ((HolderActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorGreen)));
                getActivity().invalidateOptionsMenu();
                getActivity().getWindow().setNavigationBarColor(getResources().getColor(R.color.colorGreen));
            }
        });


        dialog.show();
    }

    private void gotoPay(Commande commande) {

        if (commande.getProducts().size() > 0) {
            showLoading();
            Intent intent = new Intent(getActivity(), PayementActivity.class);
            intent.putExtra("commande", commande.getCommandeNumber());
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            hideLoading();
            startActivity(intent);
        }
    }

    private MaterialDialog addTable;
    private EditText libelle, description;
    private Button close2, add;

    public void showAddTableDialog() {


        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity())
                .customView(R.layout.dialog_add_table, false);

        MaterialDialog addTable = builder.build();
        details = (RecyclerView) addTable.findViewById(R.id.details);
        libelle = (EditText) addTable.findViewById(R.id.libelle);
        description = (EditText) addTable.findViewById(R.id.description);

        close2 = (Button) addTable.findViewById(R.id.close);
        add = (Button) addTable.findViewById(R.id.add);
        close2.setOnClickListener(view -> {
            addTable.dismiss();
        });

        add.setOnClickListener(view -> {

            LoungeTable loungeTable = new LoungeTable();
            loungeTable.setDescr(description.getText().toString());
            loungeTable.setLibelle(libelle.getText().toString());
            LoungTablesRepository loungTablesRepository = new LoungTablesRepository();
            loungTablesRepository.save(loungeTable);
            addTable.dismiss();
            loungePresenter.initTables(loungeTableList);
        });


        addTable.show();
    }

    @Override
    public void onLoungeTableClicked(LoungeTable loungeTable) {


        if (loungeTable.getId() == -1) {
            SaleFragment saleFragment = new SaleFragment();
                getFragmentManager().beginTransaction().replace(R.id.content_main, saleFragment).addToBackStack(null).commit();

        } else {


            if (isMerging) {
                LoungTablesRepository loungTablesRepository = new LoungTablesRepository();
                if (loungeTable.getCommande_number() == null) {
                    loungeTable.setCommande_number(mergeId);
                    loungTablesRepository.update(loungeTable);
                    mergeId = 0;

                    isMerging = false;

                    ((HolderActivity) getActivity()).getSupportActionBar().setTitle("Tables");
                    ((HolderActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
                    getActivity().getWindow().setNavigationBarColor(getResources().getColor(R.color.black));
                    loungeTableList.remove(loungeTable);
                    getActivity().invalidateOptionsMenu();

                    loungePresenter.initTables(loungeTableList);
                }

            } else if (isChanging) {
                LoungTablesRepository loungTablesRepository = new LoungTablesRepository();
                if (loungeTable.getCommande_number() == null) {

                    loungeTable.setCommande_number(changeId);

                    DetailCommandeRepository detailCommandeRepository = new DetailCommandeRepository();
                    loungeTableOrigin = detailCommandeRepository.find(changeId).getTableList().get(0);

                    loungeTableOrigin.setCommande_number(null);
                    loungTablesRepository.update(loungeTableOrigin);


                    loungTablesRepository.update(loungeTable);
                    changeId = 0;

                    isChanging = false;

                    ((HolderActivity) getActivity()).getSupportActionBar().setTitle("Tables");
                    ((HolderActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
                    getActivity().getWindow().setNavigationBarColor(getResources().getColor(R.color.black));
                    getActivity().invalidateOptionsMenu();


                    loungePresenter.initTables(loungeTableList);

                }

            } else {
                if (loungeTable.getCommande_number() == null) {
                    SaleFragment saleFragment = new SaleFragment();
                    Bundle bundle = new Bundle();

                    Gson gson = new Gson();
                    String json = gson.toJson(loungeTable);

                    bundle.putString("loungeTable", json);

                    saleFragment.setArguments(bundle);
                    getFragmentManager().beginTransaction().replace(R.id.content_main, saleFragment).addToBackStack(null).commit();


                } else {
//                showCommandeDetails(loungeTable.getCommande());
                    showSidePanel(loungeTable.getCommande());
                }
            }
        }
    }

    @Override
    public void onLoungeTableLongClicked(LoungeTable loungeTable) {

    }

    @Override
    public void onCommandeSelected(Commande commande) {

    }

    @Override
    public void onCommandeLongClicked(Commande commande) {

    }

    @Override
    public void onTicketSelected(DetailCommande detailCommande) {

    }

    @Override
    public void onTicketLongClickSelected(DetailCommande detailCommande) {

    }

    @Override
    public void onPlusSelected(DetailCommande detailCommande) {

    }

    @Override
    public void onMinusSelected(DetailCommande detailCommande) {

    }


    @Override
    public void onItemDeleted(DetailCommande detailCommande) {

    }
}
