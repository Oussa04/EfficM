package tn.meteor.efficaisse.ui.recette;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import tn.meteor.efficaisse.R;
import tn.meteor.efficaisse.adapter.CommandeAdapter;
import tn.meteor.efficaisse.adapter.DateCommandeAdapter;
import tn.meteor.efficaisse.adapter.TicketCommandeAdapter;
import tn.meteor.efficaisse.data.repository.DetailCommandeRepository;
import tn.meteor.efficaisse.model.Commande;
import tn.meteor.efficaisse.model.DetailCommande;
import tn.meteor.efficaisse.model.PayementType;
import tn.meteor.efficaisse.model.Payment;
import tn.meteor.efficaisse.ui.base.BaseFragment;
import tn.meteor.efficaisse.ui.payement.PayementActivity;
import tn.meteor.efficaisse.utils.DateCommande;
import tn.meteor.efficaisse.utils.ItemOffsetDecoration;
import tn.meteor.efficaisse.utils.TicketDividerItemDecoration;

/**
 * Created by lilk on 28/01/2018.
 */

public class RecetteFragment extends BaseFragment implements DateCommandeAdapter.CommandeListener, RecetteContract.View, CommandeAdapter.CommandeListener, TicketCommandeAdapter.TicketListener {

    private TextView cheque, espece, bon, sum;
    private RecetteContract.Presenter recettePresenter;
    private RecyclerView commandes, dates;
    private List<Commande> commandesList;
    private List<DateCommande> dateCommandeList;

    private LinearLayout empty, leftLayout, rightLayout;
    private CommandeAdapter commandeAdapter;
    private DateCommandeAdapter dateCommandeAdapter;

    public static RecetteFragment newInstance() {
        RecetteFragment fragment = new RecetteFragment();
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_recette, container, false);
        recettePresenter = new RecettePresenter(this);
        commandes = v.findViewById(R.id.commandes);

        dates = v.findViewById(R.id.dates);
        cheque = v.findViewById(R.id.cheque);
        espece = v.findViewById(R.id.espece);
        bon = v.findViewById(R.id.bons);
        sum = v.findViewById(R.id.sum);
        empty = v.findViewById(R.id.empty);
        leftLayout = v.findViewById(R.id.leftLayout);
        rightLayout = v.findViewById(R.id.rightLayout);
        empty.setVisibility(View.GONE);

        dateCommandeList = new ArrayList<>();
        commandesList = new ArrayList<>();


        DetailCommandeRepository detailCommandeRepository = new DetailCommandeRepository();
        try {
            dateCommandeList = detailCommandeRepository.getCommandeGroupedBySession();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        dates.setLayoutManager(mLayoutManager);
        dates.setItemAnimator(new DefaultItemAnimator());
        dates.addItemDecoration(new TicketDividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL, 0));
        dateCommandeAdapter = new DateCommandeAdapter(getActivity(), this, dateCommandeList);
        dates.setAdapter(dateCommandeAdapter);
        dateCommandeAdapter.notifyDataSetChanged();


        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, LinearLayoutManager.VERTICAL);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(v.getContext(), R.dimen.item_product_offset);
        commandeAdapter = new CommandeAdapter(getActivity(), this, commandesList);
        commandes.addItemDecoration(itemDecoration);
        commandes.setLayoutManager(staggeredGridLayoutManager);
        commandes.setAdapter(commandeAdapter);

        commandesList.clear();
        leftLayout.setVisibility(View.VISIBLE);
        rightLayout.setVisibility(View.VISIBLE);
        if (!dateCommandeList.isEmpty()) {
            commandesList.addAll(dateCommandeList.get(0).getCommandeList());
            commandeAdapter.notifyDataSetChanged();

            calculateCommandes(dateCommandeList.get(0));
        } else {
            empty.setVisibility(View.VISIBLE);
            leftLayout.setVisibility(View.GONE);
            rightLayout.setVisibility(View.GONE);
        }

        return v;
    }

    private void calculateCommandes(DateCommande dateCommande) {
        int chequeNumber = 0;
        double chequeValue = 0;
        double especeValue = 0;
        int bonsNumber = 0;
        double bonsValue = 0;

        for (Commande commande : dateCommande.getCommandeList()) {

            for (Payment payment : commande.getPayments()) {
                if (payment.getType().equals(PayementType.ESPECE.toString())) {
                    especeValue = especeValue + payment.getMontant();
                } else if (payment.getType().equals(PayementType.CHEQUE.toString())) {
                    chequeNumber = chequeNumber + 1;
                    chequeValue = chequeValue + payment.getMontant();
                } else if (payment.getType().equals(PayementType.BON.toString())) {
                    bonsNumber = bonsNumber + payment.getQuantity();
                    bonsValue = bonsValue + payment.getMontant() * payment.getQuantity();
                }

            }
        }


        cheque.setText(String.format("%.3f",chequeValue) + " dt. / " + chequeNumber);
        espece.setText(String.format("%.3f",especeValue) + " dt.");
        bon.setText(String.format("%.3f",bonsValue) + " dt. / " + bonsNumber);
        sum.setText("Total= " +  String.format("%.3f",dateCommande.getSum()) + " dt.");

    }


    @Override
    public void onResume() {
        super.onResume();


    }

    @Override
    public void updateUI() {

    }


    private RecyclerView dialog_details;
    private Button dialog_pay, dialog_close;
    private TextView dialog_quantity, sumafter, sumbefore, discount, customer;
    private TicketCommandeAdapter dialog_ticketAdapter;

    private void showCommandeDetails(Commande commande) {

        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity())
                .customView(R.layout.dialog_commande_details, false);

        MaterialDialog dialog = builder.build();
        dialog_details = (RecyclerView) dialog.findViewById(R.id.details);

        dialog_pay = (Button) dialog.findViewById(R.id.pay);
        dialog_quantity = (TextView) dialog.findViewById(R.id.quantity);
        sumafter = (TextView) dialog.findViewById(R.id.sumafter);
        sumbefore = (TextView) dialog.findViewById(R.id.sumbefore);
        discount = (TextView) dialog.findViewById(R.id.discount);
        customer = (TextView) dialog.findViewById(R.id.customer);
        dialog_close = (Button) dialog.findViewById(R.id.close);
        dialog_close.setOnClickListener(view -> {
            dialog.dismiss();
        });
        DetailCommandeRepository detailCommandeRepository = new DetailCommandeRepository();
        dialog_quantity.setText("Articles (" + detailCommandeRepository.getCommandeQuantity(commande) + ")");


        if (commande.getCustomer() != null) {
            customer.setText("Client: " + commande.getCustomer().getName());
            sumafter.setText("Somme finale: " + String.format("%.3f",DetailCommandeRepository.getCommandeSumdiscounted(commande)) + " dt.");
            sumbefore.setText("Totale: " + String.format("%.3f",DetailCommandeRepository.getCommandeSum(commande)) + " dt.");

         if ( commande.getCustomer().getCustomerGroup()!=null)
            discount.setText("Remise: " + commande.getCustomer().getCustomerGroup().getDiscount() + "%");

        } else if (DetailCommandeRepository.getCommandeSumdiscounted(commande) == DetailCommandeRepository.getCommandeSum(commande))

        {
            sumbefore.setVisibility(View.GONE);
            discount.setVisibility(View.GONE);
            sumafter.setText("Somme finale: " + String.format("%.3f",DetailCommandeRepository.getCommandeSumdiscounted(commande)) + " dt.");

        } else

        {
            sumbefore.setVisibility(View.GONE);
            discount.setVisibility(View.GONE);
            customer.setVisibility(View.GONE);
            sumafter.setText("Somme finale: " + String.format("%.3f",DetailCommandeRepository.getCommandeSum(commande) )+ " dt.");

        }

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        dialog_details.setLayoutManager(mLayoutManager);
        dialog_details.setItemAnimator(new DefaultItemAnimator());
        dialog_details.addItemDecoration(new TicketDividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL, 0));
        dialog_ticketAdapter = new TicketCommandeAdapter(getActivity(), this, commande.getProducts(), false);
        dialog_details.setAdapter(dialog_ticketAdapter);

        if (commande.isStatus()) {
            dialog_pay.setVisibility(View.GONE);
        } else {
            dialog_pay.setVisibility(View.VISIBLE);
            dialog_pay.setOnClickListener(new View.OnClickListener() {
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


    @Override
    public void onDateCommandSelected(DateCommande commande) {
        calculateCommandes(commande);
        commandesList.clear();
        commandesList.addAll(commande.getCommandeList());

        commandeAdapter.notifyDataSetChanged();
    }


}
