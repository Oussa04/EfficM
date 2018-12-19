package tn.meteor.efficaisse.ui.sale;

import android.content.Intent;
import android.graphics.PointF;
import android.media.MediaPlayer;
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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dlazaro66.qrcodereaderview.QRCodeReaderView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import tn.meteor.efficaisse.R;
import tn.meteor.efficaisse.adapter.CategoryAdapter;
import tn.meteor.efficaisse.adapter.ProductAdapter;
import tn.meteor.efficaisse.adapter.TicketAdapter;
import tn.meteor.efficaisse.data.repository.CustomerRepository;
import tn.meteor.efficaisse.data.repository.DetailCommandeRepository;
import tn.meteor.efficaisse.data.repository.LoungTablesRepository;
import tn.meteor.efficaisse.model.Category;
import tn.meteor.efficaisse.model.Commande;
import tn.meteor.efficaisse.model.Customer;
import tn.meteor.efficaisse.model.DetailCommande;
import tn.meteor.efficaisse.model.LoungeTable;
import tn.meteor.efficaisse.model.Product;
import tn.meteor.efficaisse.ui.base.BaseFragment;
import tn.meteor.efficaisse.ui.payement.PayementActivity;
import tn.meteor.efficaisse.utils.ItemOffsetDecoration;
import tn.meteor.efficaisse.utils.TicketDividerItemDecoration;


public class SaleFragment extends BaseFragment implements QRCodeReaderView.OnQRCodeReadListener, SaleContract.View, ProductAdapter.GridAdapterListener, CategoryAdapter.CategoryAdapterListener, TicketAdapter.TicketListener {
    private ImageButton allproducts;
    private ImageButton favoriteproducts;
    private ImageButton discounts;
    private ImageButton emptyTicket;
    private ImageButton scan;
    private ImageButton removeCustomer;
    private RecyclerView products;
    private RecyclerView categories;
    private RecyclerView ticket;
    private ProductAdapter productsAdapter;
    private CategoryAdapter categoryAdapter;
    private TicketAdapter commandeAdapter;
    private Button checkout;
    private ImageButton save;
    private Customer customer;
    private Button log;
    private TextView ticketCount, customerDiscount, customerName;

    //_________________________________

    private SaleContract.Presenter salePresenter;
    private List<Product> productsList;
    private List<Category> categoryList;
    private Customer actualCustomer;


    private List<DetailCommande> detailCommandeList;


    private LinearLayout empty, productsLayout, clientinfo;
    private RelativeLayout ticketLayout;


    private double ticketSum = 0f;
    private int ticketCountProducts = 0;


    public static SaleFragment newInstance() {
        SaleFragment fragment = new SaleFragment();
        return fragment;
    }

    private View v;
    private boolean front = true;
    private MaterialDialog dialog;
    private LoungeTable loungeTable;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_sale, container, false);

        bindComponents();
        productsList = new ArrayList<>();
        categoryList = new ArrayList<>();
        detailCommandeList = new ArrayList<>();


        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(4, LinearLayoutManager.VERTICAL);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(v.getContext(), R.dimen.item_product_offset);

        productsAdapter = new ProductAdapter(getActivity(), this, productsList);
        products.addItemDecoration(itemDecoration);
        products.setLayoutManager(staggeredGridLayoutManager);
        products.setAdapter(productsAdapter);



        categoryAdapter = new CategoryAdapter(getActivity(), this, categoryList);
        StaggeredGridLayoutManager staggeredGridLayoutManager2 = new StaggeredGridLayoutManager(5, LinearLayoutManager.VERTICAL);
        ItemOffsetDecoration itemDecoration2 = new ItemOffsetDecoration(v.getContext(), R.dimen.item_category_offset);
        categories.addItemDecoration(itemDecoration2);
        categories.setLayoutManager(staggeredGridLayoutManager2);
        categories.setAdapter(categoryAdapter);


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        ticket.setLayoutManager(mLayoutManager);
        ticket.setItemAnimator(new DefaultItemAnimator());
        ticket.addItemDecoration(new TicketDividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL, 0));
        commandeAdapter = new TicketAdapter(getActivity(), this, detailCommandeList, true);
        ticket.setAdapter(commandeAdapter);


        salePresenter.getFavoriteProducts(productsList);
        salePresenter.initCategoriesList(categoryList);
        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity())
                .customView(R.layout.dialog_qr_code, false);

        dialog = builder.build();

        qrCodeReaderView = (QRCodeReaderView) dialog.findViewById(R.id.qrdecoderview);
        close = (Button) dialog.findViewById(R.id.close);
        switchCamera = (ImageButton) dialog.findViewById(R.id.switchCamera);
        qrCodeReaderView.setOnQRCodeReadListener(this);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                qrCodeReaderView.stopCamera();
                dialog.dismiss();
            }
        });

        switchCamera.setVisibility(View.GONE);
        // Use this function to enable/disable decoding
        qrCodeReaderView.setQRDecodingEnabled(true);

        // Use this function to change the autofocus interval (default is 5 secs)
//        qrCodeReaderView.setAutofocusInterval(2000L);

        // Use this function to enable/disable Torch
//        qrCodeReaderView.setTorchEnabled(true);

        // Use this function to set front camera preview
//        qrCodeReaderView.setFrontCamera();

        // Use this function to set back camera preview
        qrCodeReaderView.setFrontCamera();

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.show();
                qrCodeReaderView.startCamera();
                front = false;
            }
        });


        emptyTicket.setOnClickListener(view -> {
            emptyTicket();
        });


        checkout.setOnClickListener(view -> {


                    gotoPay();

                }
        );
        save.setOnClickListener(view -> {

            if (productsList.size() > 0) {
                saveCommande();
                emptyTicket();


                showMessage("Commande Sauvegardée");
            }

        });

        removeCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customerName.setText("-");
                customerDiscount.setText("-%");
                customer=null;
                clientinfo.setVisibility(View.GONE);
                commandeAdapter.setCustomer(null);
                updateCommandeUI();
            }
        });


        Bundle bundle = getArguments();
        if (bundle != null) {
            loungeTable = new LoungeTable();
            String productJson = bundle.getString("loungeTable");
            Gson gson = new Gson();
            loungeTable = gson.fromJson(productJson, LoungeTable.class);
            checkout.setVisibility(View.GONE);

        }

        return v;
    }

    private void bindComponents() {
        allproducts = v.findViewById(R.id.allproducts);
        favoriteproducts = v.findViewById(R.id.favoriteproducts);
        discounts = v.findViewById(R.id.discount);
        emptyTicket = v.findViewById(R.id.emptyTicket);
        salePresenter = new SalePresenter(this);
        products = v.findViewById(R.id.products);
        categories = v.findViewById(R.id.categories);
        ticket = v.findViewById(R.id.ticket);
        checkout = v.findViewById(R.id.checkout);
        save = v.findViewById(R.id.save);
        clientinfo = v.findViewById(R.id.clientinfo);
        scan = v.findViewById(R.id.scan);
        customerDiscount = v.findViewById(R.id.customerDiscount);
        customerName = v.findViewById(R.id.customerName);
        clientinfo.setVisibility(View.GONE);
        checkout.setText("Vide");
        ticketCount = v.findViewById(R.id.ticketCount);
        removeCustomer = v.findViewById(R.id.removeCustomer);

        empty = v.findViewById(R.id.empty);
        ticketLayout = v.findViewById(R.id.ticketLayout);
        productsLayout = v.findViewById(R.id.productsLayout);


    }

    private Commande commande;

    private Commande saveCommande() {

        commande = new Commande();
        commande.setStatus(false);
        commande.setDate(new Date());
        if(customer != null)
        commande.setCustomerCode(customer.getCode());
        commande.setProducts(detailCommandeList);
        DetailCommandeRepository detailCommandeRepository = new DetailCommandeRepository();
        detailCommandeRepository.save(commande,customer);
        if (loungeTable != null) {

            loungeTable.setCommande_number(commande.getCommandeNumber());
            LoungTablesRepository loungTablesRepository = new LoungTablesRepository();
            loungTablesRepository.update(loungeTable);
        }
        Toast.makeText(getActivity(), commande.getCommandeNumber() + "", Toast.LENGTH_SHORT).show();
        return commande;
    }

    private void gotoPay() {

        Commande commande = saveCommande();
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
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public void updateProducts() {
        productsAdapter.notifyDataSetChanged();

    }

    @Override
    public void updateCategories() {
        empty.setVisibility(View.GONE);
        if (categoryList.size() < 2) {
            empty.setVisibility(View.VISIBLE);
            productsLayout.setVisibility(View.GONE);
            ticketLayout.setVisibility(View.GONE);

        }


        categoryAdapter.notifyDataSetChanged();
    }


    @Override
    public void updateCommandeUI() {

        if (customer != null && customer.getCustomerGroup() != null) {
            this.ticketSum = salePresenter.calculateSum(detailCommandeList) - salePresenter.calculateSum(detailCommandeList) * (customer.getCustomerGroup().getDiscount() / 100);

        } else {

            this.ticketSum = salePresenter.calculateSum(detailCommandeList);
        }

        this.ticketCountProducts = salePresenter.countItems(detailCommandeList);

        ticketCount.setText("Articles (" + this.ticketCountProducts + ")");
        if (this.ticketSum > 0) {
            checkout.setText(String.format("%.3f",this.ticketSum) + " dt");
        } else {
            checkout.setText("Vide");

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        emptyTicket();
    }

    @Override
    public void onProductSelected(Product product) {

        salePresenter.addProductToTicket(product, detailCommandeList);
//          boolean insuffisante = false;
//        if (product.getStockQuantity() == null || product.getCost() == null) {
//
//            if (!product.getIngredients().isEmpty()) {
//
//                for (Product_Ingredient product_ingredient : product.getIngredients()) {
//                    if (product_ingredient.getIngredient().getStockQuantity() < product_ingredient.getQuantity()) {
//                        showMessage("Quantité de " + product_ingredient.getProduct().getName() + " insuffisante.");
//                        insuffisante = true;
//                    }
//                }
//                if (!insuffisante) {
//                    salePresenter.addProductToTicket(product, detailCommandeList);
//
//                }
//
//            } else {
//                salePresenter.addProductToTicket(product, detailCommandeList);
//
//            }
//        } else {
//            if (product.getStockQuantity() < 1) {
//                showMessage("Quantité dans le stock de " + product.getName() + " insuffisante.");
//            } else {
//                salePresenter.addProductToTicket(product, detailCommandeList);
//            }
//
//        }

    }

    @Override
    public void onProductLongClickSelected(Product product) {

    }


    @Override
    public void updateCommande() {
        commandeAdapter.notifyDataSetChanged();
    }

    public void emptyTicket() {

        salePresenter.emptyTicket(detailCommandeList);

        updateCommandeUI();
    }

    @Override
    public void onCategorySelected(Category category) {
        if (category.getName().equals("Favoris")) {
            salePresenter.getFavoriteProducts(productsList);
        } else {
            salePresenter.getProductByCategory(category, productsList);
        }
    }

    @Override
    public void onCategoryLongClickSelected(Category category) {

    }

    @Override
    public void onTicketSelected(DetailCommande detailCommande) {

    }

    @Override
    public void onTicketLongClickSelected(DetailCommande detailCommande) {

    }

    @Override
    public void onPlusSelected(DetailCommande detailCommande) {
        salePresenter.increseItemQuantity(detailCommande, detailCommandeList);
    }

    @Override
    public void onMinusSelected(DetailCommande detailCommande) {
        salePresenter.decreseItemQuantity(detailCommande, detailCommandeList);
    }

    @Override
    public void onItemDeleted(DetailCommande detailCommande) {
        salePresenter.removeItem(detailCommande, detailCommandeList);

    }

    private QRCodeReaderView qrCodeReaderView;
    private Button close;
    private ImageButton switchCamera;


    @Override
    public void onQRCodeRead(String text, PointF[] points) {
        CustomerRepository customerRepository = new CustomerRepository();
         customer = new Customer(text);
        customer = customerRepository.find(customer);

        if (customer == null) {
            Toast.makeText(getActivity(), "Cette carte n'appartient à aucun de vos clients", Toast.LENGTH_SHORT).show();
        } else {
            final MediaPlayer mp = MediaPlayer.create(this.getActivity(), R.raw.bititit);
            mp.start();

            dialog.dismiss();
            clientinfo.setVisibility(View.VISIBLE);
            customerName.setText(customer.getName());
            salePresenter.setCustomer(customer);
            commandeAdapter.setCustomer(customer);
            if (customer.getCustomerGroup() != null) {
                customerDiscount.setText("-" + String.format("%.2f",customer.getCustomerGroup().getDiscount()) + "%");
            } else {

                customerDiscount.setVisibility(View.GONE);
            }
            updateCommandeUI();

        }
    }


}

