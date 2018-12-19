package tn.meteor.efficaisse.ui.customers;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import at.markushi.ui.CircleButton;
import tn.meteor.efficaisse.R;
import tn.meteor.efficaisse.adapter.CustomerAdapter;
import tn.meteor.efficaisse.adapter.CustomerGroupAdapter;
import tn.meteor.efficaisse.data.preferences.AppPreferencesHelper;
import tn.meteor.efficaisse.model.Customer;
import tn.meteor.efficaisse.model.CustomerGroup;
import tn.meteor.efficaisse.ui.base.BaseFragment;
import tn.meteor.efficaisse.ui.customer.CustomerActivity;
import tn.meteor.efficaisse.ui.customerDetails.CustomerDetailsFragment;
import tn.meteor.efficaisse.ui.group.GroupActivity;
import tn.meteor.efficaisse.utils.Constants;
import tn.meteor.efficaisse.utils.ItemOffsetDecoration;
import tn.meteor.efficaisse.utils.SquareImageView;


public class CustomersFragment extends BaseFragment implements CustomersContract.View, CustomerAdapter.CustomerAdpaterListener, CustomerGroupAdapter.CustomerGroupAdapterListener {


    private CustomersContract.Presenter itemPresenter;
    private RecyclerView customers;
    private RecyclerView groups;
    private LinearLayout addCustomerBar, addGroupBar;
    private List<Customer> customerList;
    private List<CustomerGroup> customerGroups;


    private CustomerAdapter customerAdapter;
    private CustomerGroupAdapter customerGroupAdapter;
    private CustomerGroup selectedGroup;
    private Button editGroup;

    private Uri resultUri;
    private Bitmap imageUpdatedBitmap;
    private boolean imageUpdated;
    private AppPreferencesHelper preferences2;

    public static CustomersFragment newInstance() {
        CustomersFragment fragment = new CustomersFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_customers, container, false);

        itemPresenter = new CustomersPresenter(this);
        customers = v.findViewById(R.id.customers);
        groups = v.findViewById(R.id.groups);
        addCustomerBar = v.findViewById(R.id.addCustomerBar);
        addGroupBar = v.findViewById(R.id.addGroupBar);
        editGroup = v.findViewById(R.id.editGroup);
        customerList = new ArrayList<>();
        customerGroups = new ArrayList<>();
        editGroup.setVisibility(View.GONE);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(4, LinearLayoutManager.VERTICAL);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(v.getContext(), R.dimen.item_product_offset);

        customerAdapter = new CustomerAdapter(getActivity(), this, customerList);
        customers.addItemDecoration(itemDecoration);
        customers.setLayoutManager(staggeredGridLayoutManager);
        customers.setAdapter(customerAdapter);


        customerGroupAdapter = new CustomerGroupAdapter(getActivity(), this, customerGroups);
        StaggeredGridLayoutManager staggeredGridLayoutManager2 = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        ItemOffsetDecoration itemDecoration2 = new ItemOffsetDecoration(v.getContext(), R.dimen.item_category_offset);
        groups.addItemDecoration(itemDecoration2);
        groups.setLayoutManager(staggeredGridLayoutManager2);
        groups.setAdapter(customerGroupAdapter);


        addCustomerBar.setOnClickListener(view -> startActivity(new Intent(getActivity(), CustomerActivity.class)));
        addGroupBar.setOnClickListener(view -> startActivity(new Intent(getActivity(), GroupActivity.class)));
        itemPresenter.initCustomersList(customerList);
        itemPresenter.getCustomersWithoutGroup(customerList);
        editGroup.setOnClickListener(view -> {
            showCategoryDetails(selectedGroup);
        });
        preferences2 = new AppPreferencesHelper(getActivity(), Constants.SHARED_PREFERENCES_NAME.PREFERENCES_ACCES_TYPE);
        hideThingsForCashier();
        return v;



    }

    public void hideThingsForCashier() {
        if (preferences2.getAccesType().equals("cashier")) {
            addCustomerBar.setVisibility(View.GONE);
            addGroupBar.setVisibility(View.GONE);
            editGroup.setVisibility(View.GONE);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void updateCustomers() {
        customerAdapter.notifyDataSetChanged();

    }

    @Override
    public void updateCustomerGroups() {
        addCustomerBar.setVisibility(View.VISIBLE);
        customerGroupAdapter.notifyDataSetChanged();
        if (customerGroups.size() == 0) {
            addCustomerBar.setVisibility(View.INVISIBLE);

        }
        hideThingsForCashier();


    }

    @Override
    public void showCustomer(Customer customer) {

        CustomerDetailsFragment customerDetailsFragment = new CustomerDetailsFragment();
        Bundle bundle = new Bundle();

        Gson gson = new Gson();
        String json = gson.toJson(customer);

        bundle.putString("customer", json);

        customerDetailsFragment.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.content_main, customerDetailsFragment).addToBackStack(null).commit();

    }


    private EditText libelle;
    private Button delete, cancel, edit, save;
    private CircleButton photo;
    private SquareImageView thumbnail;

    private void showCategoryDetails(CustomerGroup customerGroup) {

        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity())
                .customView(R.layout.dialog_category_details, false);

        MaterialDialog dialog = builder.build();
        libelle = (EditText) dialog.findViewById(R.id.libelle);
        delete = (Button) dialog.findViewById(R.id.delete);
        cancel = (Button) dialog.findViewById(R.id.cancel);
        edit = (Button) dialog.findViewById(R.id.edit);
        photo = (CircleButton) dialog.findViewById(R.id.photo);
        thumbnail = (SquareImageView) dialog.findViewById(R.id.thumbnail);

        save = (Button) dialog.findViewById(R.id.save);
        libelle.setText(customerGroup.getName().toString());

        libelle.setEnabled(false);


        dialog.show();


        cancel.setVisibility(View.VISIBLE);
        edit.setVisibility(View.VISIBLE);

        delete.setVisibility(View.GONE);
        photo.setVisibility(View.GONE);
        save.setVisibility(View.GONE);

        edit.setOnClickListener(view -> {
            edit.setVisibility(View.GONE);

            delete.setVisibility(View.VISIBLE);
            photo.setVisibility(View.VISIBLE);
            save.setVisibility(View.VISIBLE);

        });

        cancel.setOnClickListener(view -> {
            edit.setVisibility(View.VISIBLE);
            delete.setVisibility(View.GONE);
            photo.setVisibility(View.GONE);
            save.setVisibility(View.GONE);

            dialog.dismiss();

        });

//        delete.setOnClickListener(view -> {
//            if (!c.getProducts().isEmpty()) {
//                showMessage("Cette catégorie ne peut pas être supprimée car elle possède un ou plusieurs produits.");
//            } else {
//                CategoryRepository categoryRepository = new CategoryRepository();
//                categoryRepository.delete(category);
//                customerGroups.remove(category);
//                dialog.dismiss();
//                customerGroupAdapter.notifyDataSetChanged();
//
//            }
//        });
//        save.setOnClickListener(view -> {
//            if (libelle.getText().toString().isEmpty()) {
//                showMessage("Vérifier le nom de la catégorie");
//            } else {
//                category.setName(libelle.getText().toString());
//                dialog.dismiss();
//                libelle.setEnabled(false);
//                if (imageUpdated) {
//                    cacheStore.deleteCacheFile(category.getPhoto());
//                    cacheStore.saveCacheFile(category.getPhoto(), imageUpdatedBitmap);
//                }
//
//                categoryRepository.update(category, imageUpdated);
//                customerGroupAdapter.notifyDataSetChanged();
//            }
//
//        });
        photo.setOnClickListener(view -> CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON).setActivityTitle("Image de la catégorie")
                .start(getActivity(), this)
        );
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("photo", "onActivityResult: ");

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK) {
                resultUri = result.getUri();
                try {
                    imageUpdatedBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), resultUri);
                    Picasso.with(getActivity())
                            .load(resultUri).into(thumbnail);
                    imageUpdated = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }


    @Override
    public void onResume() {
        super.onResume();
        itemPresenter.initCustomersList(customerList);
        itemPresenter.initCustomerGroupsList(customerGroups);
    }

    @Override
    public void onCustomerSelected(Customer customer) {
        showCustomer(customer);
    }

    @Override
    public void onCustomerLongClick(Customer customer) {

    }

    @Override
    public void onGroupSelected(CustomerGroup customerGroup) {
        if (customerGroup.getName().equals(getResources().getString(R.string.sans_groupe))) {

            itemPresenter.getCustomersWithoutGroup(customerList);
            editGroup.setVisibility(View.GONE);

        } else {
            itemPresenter.getCustomersByGroup(customerGroup, customerList);
            editGroup.setVisibility(View.VISIBLE);
            selectedGroup = customerGroup;
        }
        hideThingsForCashier();

    }

    @Override
    public void onGroupLongClickSelected(CustomerGroup customerGroup) {

    }


}

