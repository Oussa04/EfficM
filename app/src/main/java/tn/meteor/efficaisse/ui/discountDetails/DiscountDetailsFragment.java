package tn.meteor.efficaisse.ui.discountDetails;

import android.content.DialogInterface;
import android.os.Bundle;
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
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import tn.meteor.efficaisse.R;
import tn.meteor.efficaisse.adapter.CategoryAdapterEdited;
import tn.meteor.efficaisse.adapter.CustomerGroupAdapterEdited;
import tn.meteor.efficaisse.adapter.DiscountAdapter;
import tn.meteor.efficaisse.adapter.ProductAdapter;
import tn.meteor.efficaisse.data.repository.CategoryRepository;
import tn.meteor.efficaisse.data.repository.CustomerGroupRepository;
import tn.meteor.efficaisse.data.repository.DiscountRepository;
import tn.meteor.efficaisse.model.Category;
import tn.meteor.efficaisse.model.CustomerGroup;
import tn.meteor.efficaisse.model.Discount;
import tn.meteor.efficaisse.model.Discount_Group;
import tn.meteor.efficaisse.model.Discount_Product;
import tn.meteor.efficaisse.model.Product;
import tn.meteor.efficaisse.ui.base.BaseFragment;
import tn.meteor.efficaisse.utils.Constants;
import tn.meteor.efficaisse.utils.ItemOffsetDecoration;

/**
 * Created by lilk on 28/01/2018.
 */

public class DiscountDetailsFragment extends BaseFragment implements CustomerGroupAdapterEdited.CustomerGroupAdapterListener, ProductAdapter.GridAdapterListener, CategoryAdapterEdited.CategoryAdapterListener, DiscountDetailsContract.View, DiscountAdapter.DiscountListener {


    private DiscountDetailsContract.Presenter discountDetailsPresenter;

    private EditText libelle, datedeb, datefin, discount;

    private TextView addText;
    private Button products, customers;
    private LinearLayout addBar;
    private Discount discountObj;
    private RecyclerView discountItems;
    private ProductAdapter discountProductAdapter;
    private CustomerGroupAdapterEdited discountCustomerGroupAdapter;
    private Boolean isDialogShown = false;
    private int which = 0; // 0 for products /// 1 for customers

    public static DiscountDetailsFragment newInstance() {
        DiscountDetailsFragment fragment = new DiscountDetailsFragment();
        return fragment;
    }

    private List<Product> productList;
    private List<CustomerGroup> customerGroupsLists;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_discount_details, container, false);
        discountDetailsPresenter = new DiscountDetailsPresenter(this);
        discountObj = new Discount();
        String discountJson = getArguments().getString("discount");
        Gson gson = new Gson();
        discountObj = gson.fromJson(discountJson, Discount.class);


        libelle = v.findViewById(R.id.libelle);
        datedeb = v.findViewById(R.id.datedeb);
        datefin = v.findViewById(R.id.datefin);
        discount = v.findViewById(R.id.discount);
        products = v.findViewById(R.id.products);
        customers = v.findViewById(R.id.customers);
        addText = v.findViewById(R.id.addText);
        addBar = v.findViewById(R.id.addBar);
        discountItems = v.findViewById(R.id.items);
        Toast.makeText(getActivity(), "hello", Toast.LENGTH_SHORT).show();
        addBar.setVisibility(View.VISIBLE);
        products.setTextColor(getResources().getColor(R.color.colorGreen));
        customers.setTextColor(getResources().getColor(R.color.light_gray_inactive_icon));
        addText.setText("Ajouter un produit");


        productList = new ArrayList<>();
        customerGroupsLists = new ArrayList<>();


        discountProductAdapter = new ProductAdapter(getActivity(), this, productList);
        StaggeredGridLayoutManager staggeredGridLayoutManager2 = new StaggeredGridLayoutManager(7, LinearLayoutManager.VERTICAL);
        ItemOffsetDecoration itemDecoration2 = new ItemOffsetDecoration(getActivity(), R.dimen.item_category_offset);
        discountItems.addItemDecoration(itemDecoration2);
        discountItems.setLayoutManager(staggeredGridLayoutManager2);
        discountItems.setAdapter(discountProductAdapter);

        discountCustomerGroupAdapter = new CustomerGroupAdapterEdited(getActivity(), this, customerGroupsLists);
        StaggeredGridLayoutManager staggeredGridLayoutManager3 = new StaggeredGridLayoutManager(7, LinearLayoutManager.VERTICAL);
        ItemOffsetDecoration itemDecoration3 = new ItemOffsetDecoration(getActivity(), R.dimen.item_category_offset);
        discountItems.addItemDecoration(itemDecoration3);
        discountItems.setLayoutManager(staggeredGridLayoutManager3);


        which = 0;

        products.setOnClickListener(view -> {
            products.setTextColor(getResources().getColor(R.color.colorGreen));
            customers.setTextColor(getResources().getColor(R.color.light_gray_inactive_icon));
            addText.setText("Ajouter un produit");
            which = 0;
            discountItems.setAdapter(discountProductAdapter);

            getDiscountPrducts();
        });

        customers.setOnClickListener(view -> {
            customers.setTextColor(getResources().getColor(R.color.colorGreen));
            products.setTextColor(getResources().getColor(R.color.light_gray_inactive_icon));
            addText.setText("Ajouter des clients");
            which = 1;
            discountItems.setAdapter(discountCustomerGroupAdapter);

            getDiscountCustomerGroups();


        });

        getDiscountPrducts();
        libelle.setEnabled(false);
        datedeb.setEnabled(false);
        discount.setEnabled(false);
        datefin.setEnabled(false);


        libelle.setText(discountObj.getName());
        DateFormat df2 = new SimpleDateFormat(Constants.DATE_FORMAT);

        datedeb.setText(df2.format(discountObj.getDatebegin()));
        datefin.setText(df2.format(discountObj.getDateend()));

        discount.setText(discountObj.getDiscount() + "");


        addBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (which == 0) {
                    showProductsDialog();
                } else if (which == 1) {
                    showCustomerGroupDialog();

                }
            }
        });


        return v;
    }


    private RecyclerView items;
    private TextView title;
    private CategoryAdapterEdited categoryAdapter;
    private ProductAdapter productAdapter;
    private MaterialDialog dialog;

    private void showProductsDialog() {
        isDialogShown = true;

        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity())
                .customView(R.layout.dialog_discount_product_clients, false);

        dialog = builder.build();
        items = (RecyclerView) dialog.findViewById(R.id.items);
        title = (TextView) dialog.findViewById(R.id.text);
        title.setText("Sélectionner une catégorie");

        CategoryRepository categoryRepository = new CategoryRepository();

        categoryAdapter = new CategoryAdapterEdited(getActivity(), this, categoryRepository.findAll());
        StaggeredGridLayoutManager staggeredGridLayoutManager2 = new StaggeredGridLayoutManager(3, LinearLayoutManager.VERTICAL);
        ItemOffsetDecoration itemDecoration2 = new ItemOffsetDecoration(getActivity(), R.dimen.item_category_offset);
        items.addItemDecoration(itemDecoration2);
        items.setLayoutManager(staggeredGridLayoutManager2);
        items.setAdapter(categoryAdapter);


        dialog.show();
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                isDialogShown = false;

            }
        });
    }

    private CustomerGroupAdapterEdited customerGroupAdapterEdited;
    private MaterialDialog dialog2;

    private void showCustomerGroupDialog() {
        isDialogShown = true;
        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity())
                .customView(R.layout.dialog_discount_product_clients, false);

        dialog2 = builder.build();
        items = (RecyclerView) dialog2.findViewById(R.id.items);
        title = (TextView) dialog2.findViewById(R.id.text);
        title.setText("Clients");

        CustomerGroupRepository customerGroupRepository = new CustomerGroupRepository();

        customerGroupAdapterEdited = new CustomerGroupAdapterEdited(getActivity(), this, customerGroupRepository.findall());
        StaggeredGridLayoutManager staggeredGridLayoutManager2 = new StaggeredGridLayoutManager(3, LinearLayoutManager.VERTICAL);
        ItemOffsetDecoration itemDecoration2 = new ItemOffsetDecoration(getActivity(), R.dimen.item_category_offset);
        items.addItemDecoration(itemDecoration2);
        items.setLayoutManager(staggeredGridLayoutManager2);
        items.setAdapter(customerGroupAdapterEdited);

        dialog2.setCanceledOnTouchOutside(false);
        dialog2.show();
        dialog2.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                isDialogShown = false;

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void updateUI() {

    }

    @Override
    public void showDiscountDetails(Discount discount) {


    }


    @Override
    public void onCommandeSelected(Discount discount) {

    }

    @Override
    public void onCommandeLongClicked(Discount discount) {

    }

    MaterialDialog categoryOptions;
    ProductAdapter cateogryProductsAdapter;

    @Override
    public void onCategorySelected(Category category) {
        if (isDialogShown) {


            categoryOptions = new MaterialDialog.Builder(getActivity())
                    .items(R.array.discountProduct)
                    .itemsCallback((d, view1, which, text) -> {
                        if (which == 0) {

                            for (Product product :
                                    category.getProducts()) {
                                Discount_Product dg = new Discount_Product();
                                dg.setDiscount(discountObj);
                                dg.setProduct_id(product.getId());
                                discountObj.getDiscount_products().add(dg);
                            }
                            DiscountRepository discountRepository = new DiscountRepository();
                            discountRepository.update(discountObj);
                            dialog.dismiss();

                            d.dismiss();
                            getDiscountPrducts();


                        } else if (which == 1) {

                            title.setText("Sélectionner un produit");
                            cateogryProductsAdapter = new ProductAdapter(getActivity(), this, category.getProducts());
                            StaggeredGridLayoutManager staggeredGridLayoutManager2 = new StaggeredGridLayoutManager(3, LinearLayoutManager.VERTICAL);
                            ItemOffsetDecoration itemDecoration2 = new ItemOffsetDecoration(getActivity(), R.dimen.item_category_offset);
                            items.addItemDecoration(itemDecoration2);
                            items.setLayoutManager(staggeredGridLayoutManager2);
                            items.setAdapter(cateogryProductsAdapter);


                            categoryOptions.dismiss();


                        }

                    })
                    .show();
        }
    }

    @Override
    public void onCategoryLongClickSelected(Category category) {

    }

    @Override
    public void onProductSelected(Product product) {
        if (isDialogShown) {

            Discount_Product dg = new Discount_Product();
            dg.setDiscount(discountObj);
            dg.setProduct_id(product.getId());
            discountObj.getDiscount_products().add(dg);
            DiscountRepository discountRepository = new DiscountRepository();

            discountRepository.update(discountObj);
            dialog.dismiss();
            getDiscountPrducts();

            isDialogShown = false;
        }
    }

    @Override
    public void onProductLongClickSelected(Product product) {

    }

    @Override
    public void onGroupSelected(CustomerGroup customerGroup) {
        if (isDialogShown) {


            Discount_Group dg = new Discount_Group();
            dg.setDiscount(discountObj);
            dg.setGroup_name(customerGroup.getName());
            discountObj.getDiscount_groups().add(dg);
            DiscountRepository discountRepository = new DiscountRepository();

            discountRepository.update(discountObj);
            dialog2.dismiss();
            getDiscountCustomerGroups();
            isDialogShown = false;

        }
    }

    public void getDiscountPrducts() {

        if (discountObj.getProductList() != null) {
            discountItems.setVisibility(View.VISIBLE);

            productList.clear();
            productList.addAll(discountObj.getProductList());
            discountProductAdapter.notifyDataSetChanged();

        } else {

            discountItems.setVisibility(View.GONE);

        }
    }

    public void getDiscountCustomerGroups() {
        if (discountObj.getCustomerGroups() != null) {
            discountItems.setVisibility(View.VISIBLE);
            customerGroupsLists.clear();
            customerGroupsLists.addAll(discountObj.getCustomerGroups());
            discountCustomerGroupAdapter.notifyDataSetChanged();
        } else {

            discountItems.setVisibility(View.GONE);

        }
    }


    @Override
    public void onGroupLongClickSelected(CustomerGroup customerGroup) {

    }
}
