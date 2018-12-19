package tn.meteor.efficaisse.ui.orders;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.List;

import tn.meteor.efficaisse.R;
import tn.meteor.efficaisse.adapter.CommandeAdapter;
import tn.meteor.efficaisse.adapter.TicketCommandeAdapter;
import tn.meteor.efficaisse.data.repository.DetailCommandeRepository;
import tn.meteor.efficaisse.model.Commande;
import tn.meteor.efficaisse.model.DetailCommande;
import tn.meteor.efficaisse.ui.base.BaseFragment;
import tn.meteor.efficaisse.ui.payement.PayementActivity;
import tn.meteor.efficaisse.utils.ItemOffsetDecoration;
import tn.meteor.efficaisse.utils.TicketDividerItemDecoration;

/**
 * Created by lilk on 28/01/2018.
 */

public class OrdersFragment extends BaseFragment implements OrdersContract.View, CommandeAdapter.CommandeListener, TicketCommandeAdapter.TicketListener {


    private OrdersContract.Presenter orderPresenter;
    private RecyclerView paid, notPaid;
    private List<Commande> paidList, notPaidList,  paidListOrigin, notPaidListOrigin;
    private EditText search;
    private CommandeAdapter commandeAdapter, commandeAdapter2;
    private LinearLayout notPaidLayout, paidLayout, empty;

    public static OrdersFragment newInstance() {
        OrdersFragment fragment = new OrdersFragment();
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_orders, container, false);
        orderPresenter = new OrdersPresenter(this);
        paid = v.findViewById(R.id.paid);
        notPaid = v.findViewById(R.id.notPaid);
        notPaidLayout = v.findViewById(R.id.notPaidLayout);
        paidLayout = v.findViewById(R.id.paidLayout);
        empty = v.findViewById(R.id.empty);
        search = v.findViewById(R.id.search);

      getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        search.addTextChangedListener(new TextWatcher() {

            // the user's changes are saved here
            public void onTextChanged(CharSequence c, int start, int before, int count) {

                paidList.clear();
                paidList.addAll(paidListOrigin);
                commandeAdapter.notifyDataSetChanged();
                notPaidList.clear();
                notPaidList.addAll(notPaidListOrigin);
                commandeAdapter2.notifyDataSetChanged();
                if (!c.toString().isEmpty()) {
                    if (paidList.size() > 0) {
                        List<Commande> listPaid = Stream.of(paidList).filter(i -> i.getCommandeNumber() == Integer.parseInt(c.toString())).toList();
                        paidList.clear();
                        paidList.addAll(listPaid);
                        commandeAdapter.notifyDataSetChanged();
                    }
                    if (notPaidList.size() > 0) {
                        List<Commande> listNotPaid = Stream.of(notPaidList).filter(i -> i.getCommandeNumber() == Integer.parseInt(c.toString())).toList();
                        notPaidList.clear();
                        notPaidList.addAll(listNotPaid);
                        commandeAdapter2.notifyDataSetChanged();
                    }
                }


            }

            public void beforeTextChanged(CharSequence c, int start, int count, int after) {
                // this space intentionally left blank
            }

            public void afterTextChanged(Editable c) {
                // this one too
            }
        });
        paidList = new ArrayList<>();
        notPaidList = new ArrayList<>();
        paidListOrigin = new ArrayList<>();
        notPaidListOrigin = new ArrayList<>();


        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(v.getContext(), R.dimen.item_product_offset);
        commandeAdapter = new CommandeAdapter(getActivity(), this, paidList);
        paid.addItemDecoration(itemDecoration);
        paid.setLayoutManager(staggeredGridLayoutManager);
        paid.setAdapter(commandeAdapter);


        StaggeredGridLayoutManager staggeredGridLayoutManager1 = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        ItemOffsetDecoration itemDecoration1 = new ItemOffsetDecoration(v.getContext(), R.dimen.item_product_offset);
        commandeAdapter2 = new CommandeAdapter(getActivity(), this, notPaidList);
        notPaid.addItemDecoration(itemDecoration1);
        notPaid.setLayoutManager(staggeredGridLayoutManager1);
        notPaid.setAdapter(commandeAdapter2);

        orderPresenter.initPaidList(paidList);
        orderPresenter.initNotPaidList(notPaidList);


        return v;
    }


    @Override
    public void onResume() {
        super.onResume();
        loadData();

    }

    @Override
    public void updateUI() {
        loadData();
    }


    public void loadData() {

        notPaid.setVisibility(View.VISIBLE);
        notPaidLayout.setVisibility(View.VISIBLE);
        paid.setVisibility(View.VISIBLE);
        paidLayout.setVisibility(View.VISIBLE);
        commandeAdapter.notifyDataSetChanged();
        commandeAdapter2.notifyDataSetChanged();

        empty.setVisibility(View.GONE);
        if (paidList.isEmpty() && notPaidList.isEmpty()) {
            empty.setVisibility(View.VISIBLE);
            paidLayout.setVisibility(View.GONE);
            notPaidLayout.setVisibility(View.GONE);
        }

        if (paidList.isEmpty()) {
            paid.setVisibility(View.GONE);
            StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(4, LinearLayoutManager.VERTICAL);

            notPaid.setLayoutManager(staggeredGridLayoutManager);

            paidLayout.setVisibility(View.GONE);
        } else {
            StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
            notPaid.setLayoutManager(staggeredGridLayoutManager);
        }
        if (notPaidList.isEmpty()) {
            notPaid.setVisibility(View.GONE);
            StaggeredGridLayoutManager staggeredGridLayoutManager1 = new StaggeredGridLayoutManager(4, LinearLayoutManager.VERTICAL);

            paid.setLayoutManager(staggeredGridLayoutManager1);

            notPaidLayout.setVisibility(View.GONE);

        } else {

            StaggeredGridLayoutManager staggeredGridLayoutManager1 = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
            paid.setLayoutManager(staggeredGridLayoutManager1);
        }

        paidListOrigin.clear();
        paidListOrigin.addAll(paidList);
        notPaidListOrigin.clear();
     notPaidListOrigin.addAll(notPaidList);
    }

    private RecyclerView details;
    private Button pay, close;
    private TextView quantity, sumafter, sumbefore, discount, customer;
    private TicketCommandeAdapter ticketAdapter;

    private void showCommandeDetails(Commande commande) {

        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity())
                .customView(R.layout.dialog_commande_details, false);

        MaterialDialog dialog = builder.build();
        details = (RecyclerView) dialog.findViewById(R.id.details);

        pay = (Button) dialog.findViewById(R.id.pay);
        quantity = (TextView) dialog.findViewById(R.id.quantity);
        sumafter = (TextView) dialog.findViewById(R.id.sumafter);
        sumbefore = (TextView) dialog.findViewById(R.id.sumbefore);
        discount = (TextView) dialog.findViewById(R.id.discount);
        customer = (TextView) dialog.findViewById(R.id.customer);
        close = (Button) dialog.findViewById(R.id.close);
        close.setOnClickListener(view -> {
            dialog.dismiss();
        });
        DetailCommandeRepository detailCommandeRepository = new DetailCommandeRepository();
        quantity.setText("Articles (" + detailCommandeRepository.getCommandeQuantity(commande) + ")");


        if (commande.getCustomer() != null) {
            customer.setText("Client: " + commande.getCustomer().getName());
            sumafter.setText("Somme finale: " + String.format("%.3f",DetailCommandeRepository.getCommandeSumdiscounted(commande) )+ " dt.");
            sumbefore.setText("Totale: " + String.format("%.3f",DetailCommandeRepository.getCommandeSum(commande) )+ " dt.");


            if ( commande.getCustomer().getCustomerGroup()!=null)
            discount.setText("Remise: " + String.format("%.3f",commande.getCustomer().getCustomerGroup().getDiscount()) + "%");

        } else if (DetailCommandeRepository.getCommandeSumdiscounted(commande) == DetailCommandeRepository.getCommandeSum(commande))

        {
            sumbefore.setVisibility(View.GONE);
            discount.setVisibility(View.GONE);

            sumafter.setText("Somme finale: " + String.format("%.3f",DetailCommandeRepository.getCommandeSumdiscounted(commande)) + " dt.");

        }
        if (commande.getCustomer() != null) {

            customer.setText("Client: "+commande.getCustomer().getName());
        }else {

            customer.setVisibility(View.GONE);
        }
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        details.setLayoutManager(mLayoutManager);
        details.setItemAnimator(new

                DefaultItemAnimator());
        details.addItemDecoration(new

                TicketDividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL, 0));
        ticketAdapter = new

                TicketCommandeAdapter(getActivity(), this, commande.getProducts(), false);
        details.setAdapter(ticketAdapter);

        if (commande.isStatus())

        {
            pay.setVisibility(View.GONE);
        } else

        {
            pay.setVisibility(View.VISIBLE);
            pay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    gotoPay(commande);

                }
            });
        }
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


    @Override
    public void onCommandeSelected(Commande commande) {
        showCommandeDetails(commande);

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
