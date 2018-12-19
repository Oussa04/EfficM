package tn.meteor.efficaisse.ui.customerDetails;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import at.markushi.ui.CircleButton;
import tn.meteor.efficaisse.R;
import tn.meteor.efficaisse.adapter.CommandeAdapter;
import tn.meteor.efficaisse.data.preferences.AppPreferencesHelper;
import tn.meteor.efficaisse.data.repository.CustomerGroupRepository;
import tn.meteor.efficaisse.data.repository.CustomerRepository;
import tn.meteor.efficaisse.model.Commande;
import tn.meteor.efficaisse.model.Customer;
import tn.meteor.efficaisse.model.CustomerGroup;
import tn.meteor.efficaisse.ui.base.BaseFragment;
import tn.meteor.efficaisse.utils.Constants;
import tn.meteor.efficaisse.utils.ItemOffsetDecoration;

/**
 * Created by lilk on 28/01/2018.
 */

public class CustomerDetailsFragment extends BaseFragment implements CommandeAdapter.CommandeListener {

    private EditText name, email, phone;
    private RecyclerView commandesRecyclerView;
    private CommandeAdapter commandeAdapter;
    private List<Commande> customer_orders;
    private Spinner group;


    private CircleButton edit, save, cancel;

    private Customer customer;
    private AppPreferencesHelper preferences2;


    public static CustomerDetailsFragment newInstance() {
        CustomerDetailsFragment fragment = new CustomerDetailsFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_customer_details, container, false);

        name = v.findViewById(R.id.name);
        email = v.findViewById(R.id.email);
        phone = v.findViewById(R.id.phone);
        group = v.findViewById(R.id.group);

        commandesRecyclerView = v.findViewById(R.id.commandes);
        edit = v.findViewById(R.id.editIngredient);
        save = v.findViewById(R.id.save);
        cancel = v.findViewById(R.id.cancel);

        save.setVisibility(View.GONE);
        cancel.setVisibility(View.GONE);

        group.setEnabled(false);
        group.setClickable(false);

        String customerJson = getArguments().getString("customer");
        Gson gson = new Gson();
        customer = gson.fromJson(customerJson, Customer.class);

        name.setText(customer.getName());
        email.setText(customer.getName());
        phone.setText(customer.getPhone());


        CustomerRepository repo = new CustomerRepository();
        customer_orders = new ArrayList<>();
        customer_orders.addAll(repo.getCustomersCommande(customer));
if (customer_orders.isEmpty()){
    commandesRecyclerView.setVisibility(View.GONE);

}
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, LinearLayoutManager.VERTICAL);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(v.getContext(), R.dimen.item_product_offset);
        commandeAdapter = new CommandeAdapter(getActivity(), this, customer_orders);
        commandesRecyclerView.addItemDecoration(itemDecoration);
        commandesRecyclerView.setLayoutManager(staggeredGridLayoutManager);
        commandesRecyclerView.setAdapter(commandeAdapter);


        edit.setOnClickListener(view -> {
            name.setEnabled(true);
            phone.setEnabled(true);
            email.setEnabled(true);
            group.setEnabled(false);
            group.setClickable(false);
            save.setVisibility(View.VISIBLE);
            cancel.setVisibility(View.VISIBLE);
            edit.setVisibility(View.GONE);

        });

        cancel.setOnClickListener(view -> {
            name.setEnabled(false);
            phone.setEnabled(false);
            email.setEnabled(false);
            group.setEnabled(false);
            group.setClickable(false);

            name.setText(customer.getName());
            phone.setText(customer.getPhone());
            email.setText(customer.getEmail());

            save.setVisibility(View.GONE);
            cancel.setVisibility(View.GONE);
            edit.setVisibility(View.VISIBLE);


        });

        save.setOnClickListener(view -> {
            if (!name.getText().toString().isEmpty() && !email.getText().toString().isEmpty() && !phone.getText().toString().isEmpty()) {

                name.setEnabled(false);
                phone.setEnabled(false);
                email.setEnabled(false);
                group.setEnabled(false);
                group.setClickable(false);

                save.setVisibility(View.GONE);
                cancel.setVisibility(View.GONE);
                edit.setVisibility(View.VISIBLE);

                saveCustomer();

            } else {
                showMessage("Prière de vérifier les champs, certains peuvent être vides ");

            }
        });


        CustomerGroupRepository customerGroupRepository = new CustomerGroupRepository();
        List<CustomerGroup> customerGroups = customerGroupRepository.findAll();


        ArrayAdapter<CustomerGroup> adapter =
                new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_list_item_1, customerGroups);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);


        group.setAdapter(adapter);
        group.setSelection(customerGroups.indexOf(customer.getCustomerGroup()));

        customer.setCustomerGroup(customerGroups.get(0));
        group.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                customer.setCustomerGroup((CustomerGroup) adapterView.getItemAtPosition(i)
                );
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        preferences2 = new AppPreferencesHelper(getActivity(), Constants.SHARED_PREFERENCES_NAME.PREFERENCES_ACCES_TYPE);
        hideThingsForCashier();
        return v;



    }

    public void hideThingsForCashier() {
        if (preferences2.getAccesType().equals("cashier")) {
            edit.setVisibility(View.GONE);

        }

    }

    private void saveCustomer() {


        if (customer.getCustomerGroup().getName().equals(getResources().getString(R.string.sans_groupe))) {
            customer.setCustomerGroup(null);
        }

        customer.setPhone(phone.getText().toString());
        customer.setEmail(email.getText().toString());
        customer.setName(name.getText().toString());
        CustomerRepository customerRepository = new CustomerRepository();
        customerRepository.update(customer);


    }


    @Override
    public void onCommandeSelected(Commande commande) {

    }

    @Override
    public void onCommandeLongClicked(Commande commande) {

    }
}
