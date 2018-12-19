package tn.meteor.efficaisse.ui.payement;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.annimon.stream.Stream;
import com.dlazaro66.qrcodereaderview.QRCodeReaderView;
import com.epson.epos2.Epos2Exception;
import com.epson.epos2.discovery.DeviceInfo;
import com.epson.epos2.discovery.Discovery;
import com.epson.epos2.discovery.DiscoveryListener;
import com.epson.epos2.discovery.FilterOption;
import com.epson.epos2.printer.Printer;
import com.epson.epos2.printer.PrinterStatusInfo;
import com.epson.epos2.printer.ReceiveListener;
import com.google.gson.Gson;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import at.markushi.ui.CircleButton;
import tn.meteor.efficaisse.R;
import tn.meteor.efficaisse.adapter.PayementAdapter;
import tn.meteor.efficaisse.data.repository.ContreBonRepository;
import tn.meteor.efficaisse.data.repository.DetailCommandeRepository;
import tn.meteor.efficaisse.data.repository.PaymentRepository;
import tn.meteor.efficaisse.data.repository.StoreRepository;
import tn.meteor.efficaisse.model.Commande;
import tn.meteor.efficaisse.model.ContreBon;
import tn.meteor.efficaisse.model.DetailCommande;
import tn.meteor.efficaisse.model.PayementType;
import tn.meteor.efficaisse.model.Payment;
import tn.meteor.efficaisse.model.Store;
import tn.meteor.efficaisse.ui.HolderActivity;
import tn.meteor.efficaisse.ui.base.BaseActivity;
import tn.meteor.efficaisse.utils.QrCodeUtils;
import tn.meteor.efficaisse.utils.TicketDividerItemDecoration;

public class PayementActivity extends BaseActivity implements QRCodeReaderView.OnQRCodeReadListener, PayementContract.View, PayementAdapter.PayementListener, ReceiveListener {

    private CircleButton cash, bon, cheque, contreBon, back, quick;
    private Button one, two, three, four, five, six, seven, eight, nine, zero, doubleZero, matta, erase;
    private TextView total, rendre, paid;
    private LinearLayout quantityLayout;
    private ImageButton minus, plus;
    private double totalInt, rendreInt, paidInt;
    private EditText value, quantity, chequeDesc;
    private CircleButton add;
    private Button finish;
    private RecyclerView recyclerView;
    private PayementAdapter payementAdapter;
    private List<Payment> payements;
    private List<DetailCommande> ticketList;
    private List<ContreBon> contreBons;
    private int type = 0;  //0:cash 1:cheque 2:bon  3:contre-bon
    private int idCommande = 0;
    private Commande commande;
    private boolean especExisxts = false;
    private boolean contreBonExists = false;
    private boolean bonExists = false;
    private boolean valueExists;


    private ArrayList<HashMap<String, String>> mPrinterList = null;
    private FilterOption mFilterOption = null;
    private boolean printerFound = false;
    private Printer mPrinter = null;
    private String target = "";
    private BigDecimal result;
    private float àSum;
    private boolean isThereContreBon = false;
    private ContreBon contreBonObject;
    private Bitmap qrCodeBitmap = null;

    private LinearLayout details;
    private QRCodeReaderView qrCodeReaderView;
    private Button close;
    private ImageButton switchCamera;
    private MaterialDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payement);
      initializePrinter();
        bindComponents();
        initializeComponents();


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setElevation(0);

        commande = new Commande();
        contreBons = new ArrayList<>();
        double totalSum = 0;

        try {

            Intent intent = getIntent();
            idCommande = intent.getIntExtra("commande", -1);
            showMessage(idCommande);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (idCommande > -1) {
            List<DetailCommande> ticketListe;
            DetailCommandeRepository detailCommandeRepository = new DetailCommandeRepository();
            commande = detailCommandeRepository.find(idCommande);
            ticketList.clear();
            ticketList.addAll(commande.getProducts());
            totalSum = detailCommandeRepository.getCommandeSum(commande);

            if (commande.getCustomer() != null && commande.getCustomer().getCustomerGroup()!=null) {
                totalSum = totalSum - totalSum * (commande.getCustomer().getCustomerGroup().getDiscount() / 100);

            }


        }

        totalInt = totalSum;
        rendreInt = 0;
        paidInt = 0;
        total.setText("A payer: " +  String.format("%.3f",totalInt )+ " dt");
        updateUI();

        Commande finalCommande = commande;

        finish.setOnClickListener(view -> {

            verifyContreBonStatus();//will create a contrebon  if needed
            printJob();
            PaymentRepository paymentRepository = new PaymentRepository();
            paymentRepository.save(payements, finalCommande, contreBons);
            Gson gson1 = new Gson();
            String json = gson1.toJson(paymentRepository.findPaymentsByCommande(finalCommande));
            showLoading();
//            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            intent.putExtra("reset", true);
//            startActivity(intent);
            HolderActivity.activityHolder.finish();
            finish();
        });


    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void verifyContreBonStatus() {

        for (Payment payment : payements) {
            if (payment.getType().equals("ESPECE")) {
                isThereContreBon = true;
            } else if (payment.getType().equals("CHEQUE")) {
                isThereContreBon = true;

            }
        }


        if (!isThereContreBon) {
            if (paidInt - totalInt > 0) {
                contreBonObject = new ContreBon(paidInt - totalInt);
                contreBonObject.setDateSortie(new Date());
                qrCodeBitmap = QrCodeUtils.GenerateClick(contreBonObject.getCrypted());
                ContreBonRepository.save(contreBonObject);

            }
        }
    }

    private void initializePrinter() {

        mPrinterList = new ArrayList<HashMap<String, String>>();

        mFilterOption = new FilterOption();
        mFilterOption.setDeviceType(Discovery.TYPE_PRINTER);
        mFilterOption.setEpsonFilter(Discovery.FILTER_NAME);

        try {
            Discovery.start(this, mFilterOption, mDiscoveryListener);
        } catch (Epos2Exception e) {
            e.printStackTrace();
        }
        restartDiscovery();

    }

    private void bindComponents() {
        cash = findViewById(R.id.cash);
        bon = findViewById(R.id.bon);
        cheque = findViewById(R.id.cheque);
        contreBon = findViewById(R.id.contreBon);

        total = findViewById(R.id.total);
        rendre = findViewById(R.id.rendre);
        paid = findViewById(R.id.paid);


        quantity = findViewById(R.id.quantity);
        chequeDesc = findViewById(R.id.description);
        value = findViewById(R.id.value);
        add = findViewById(R.id.add);
        finish = findViewById(R.id.checkout);
        quantityLayout = findViewById(R.id.quantityLayout);
        minus = findViewById(R.id.minus);
        plus = findViewById(R.id.plus);
        details = findViewById(R.id.details);


        details.setVisibility(View.VISIBLE);


        one = findViewById(R.id.one);
        two = findViewById(R.id.two);
        three = findViewById(R.id.three);
        four = findViewById(R.id.four);
        five = findViewById(R.id.five);

        six = findViewById(R.id.six);
        seven = findViewById(R.id.seven);
        eight = findViewById(R.id.eight);
        nine = findViewById(R.id.nine);
        zero = findViewById(R.id.zero);
        doubleZero = findViewById(R.id.doubleZero);
        quick = findViewById(R.id.quick);
        matta = findViewById(R.id.matta);
        erase = findViewById(R.id.erase);
        back = findViewById(R.id.back);

        recyclerView = (RecyclerView) findViewById(R.id.paymentList);


    }

    private void initializeComponents() {


        value.setVisibility(View.VISIBLE);
        chequeDesc.setVisibility(View.GONE);
        quantity.setInputType(InputType.TYPE_NULL);
        value.setInputType(InputType.TYPE_NULL);
        quantityLayout.setVisibility(View.GONE);
        payements = new ArrayList<>();
        ticketList = new ArrayList<>();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new TicketDividerItemDecoration(this, DividerItemDecoration.VERTICAL, 0));
        payementAdapter = new PayementAdapter(this, this, payements);
        recyclerView.setAdapter(payementAdapter);

        cash.setOnClickListener(view -> {

            type = 0;
            value.setVisibility(View.VISIBLE);
            quantityLayout.setVisibility(View.GONE);
            chequeDesc.setVisibility(View.GONE);

            bon.setColor(Color.LTGRAY);
            cheque.setColor(Color.LTGRAY);
            contreBon.setColor(Color.LTGRAY);
            cash.setColor(getResources().getColor(R.color.colorAccent));
            value.setHint("Valeur");


            value.setText("");
            quantity.setText("");
            chequeDesc.setText("");

        });

        bon.setColor(Color.LTGRAY);
        cheque.setColor(Color.LTGRAY);

        contreBon.setColor(Color.LTGRAY);

        cheque.setOnClickListener(view -> {

            type = 1;
            value.setVisibility(View.VISIBLE);
            quantityLayout.setVisibility(View.GONE);
            chequeDesc.setVisibility(View.VISIBLE);

            bon.setColor(Color.LTGRAY);
            cash.setColor(Color.LTGRAY);
            contreBon.setColor(Color.LTGRAY);
            cheque.setColor(getResources().getColor(R.color.colorAccent));
            value.setHint("Valeur");


            value.setText("");
            quantity.setText("");
            chequeDesc.setText("");
        });


        bon.setOnClickListener(view -> {
            value.setHint("Valeur");

            type = 2;
            value.setVisibility(View.VISIBLE);
            quantityLayout.setVisibility(View.VISIBLE);
            chequeDesc.setVisibility(View.GONE);
            value.setText("");
            quantity.setText("");
            chequeDesc.setText("");
            cheque.setColor(Color.LTGRAY);
            cash.setColor(Color.LTGRAY);
            contreBon.setColor(Color.LTGRAY);
            bon.setColor(getResources().getColor(R.color.colorAccent));
            quantity.setText("1");

        });


        MaterialDialog.Builder builder = new MaterialDialog.Builder(this)
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
                type = 0;
                value.setVisibility(View.VISIBLE);
                quantityLayout.setVisibility(View.GONE);
                chequeDesc.setVisibility(View.GONE);

                bon.setColor(Color.LTGRAY);
                cheque.setColor(Color.LTGRAY);
                contreBon.setColor(Color.LTGRAY);
                cash.setColor(getResources().getColor(R.color.colorAccent));
                value.setHint("Valeur");


                value.setText("");
                quantity.setText("");
                chequeDesc.setText("");
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


        contreBon.setOnClickListener(view -> {


            dialog.show();
            qrCodeReaderView.startCamera();

            type = 3;
            value.setVisibility(View.GONE);

            quantityLayout.setVisibility(View.GONE);
            chequeDesc.setVisibility(View.GONE);
            value.setText("");
            quantity.setText("");
            chequeDesc.setText("");
            cheque.setColor(Color.LTGRAY);
            cash.setColor(Color.LTGRAY);
            bon.setColor(Color.LTGRAY);
            contreBon.setColor(getResources().getColor(R.color.colorAccent));


        });


        quick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (totalInt - paidInt > 0)

                    value.setText(String.format("%.3f",totalInt - paidInt));
            }
        });
        one.setOnClickListener(view -> value.setText(value.getText().toString() + "1"));
        two.setOnClickListener(view -> value.setText(value.getText().toString() + "2"));
        three.setOnClickListener(view -> value.setText(value.getText().toString() + "3"));
        four.setOnClickListener(view -> value.setText(value.getText().toString() + "4"));
        five.setOnClickListener(view -> value.setText(value.getText().toString() + "5"));
        six.setOnClickListener(view -> value.setText(value.getText().toString() + "6"));
        seven.setOnClickListener(view -> value.setText(value.getText().toString() + "7"));
        eight.setOnClickListener(view -> value.setText(value.getText().toString() + "8"));
        nine.setOnClickListener(view -> value.setText(value.getText().toString() + "9"));
        zero.setOnClickListener(view -> value.setText(value.getText().toString() + "0"));
        doubleZero.setOnClickListener(view -> value.setText(value.getText().toString() + "00"));
        matta.setOnClickListener(view -> {
            if (!value.getText().toString().contains("."))
                value.setText(value.getText().toString() + ".");
        });
        erase.setOnClickListener(view -> {
            if (!value.getText().toString().contains("."))
                value.setText("");
        });
        back.setOnClickListener(view -> {
            if (!value.getText().toString().isEmpty())
                value.setText(value.getText().toString().substring(0, value.getText().toString().length() - 1));
        });

        quantity.setText("1");
        plus.setOnClickListener(view -> {
            quantity.setText(Integer.parseInt(quantity.getText().toString()) + 1 + "");
        });
        minus.setOnClickListener(view -> {
            if (Integer.parseInt(quantity.getText().toString()) > 1)
                quantity.setText(Integer.parseInt(quantity.getText().toString()) - 1 + "");
        });

        add.setOnClickListener(view -> {

            switch (type) {

                case 0: {//cash

                    Payment payement = new Payment();
                    payement.setQuantity(1);
                    payement.setCommande(null);
                    payement.setCommentaire("");
                    payement.setMontant(0);
                    payement.setType(PayementType.ESPECE.toString());
                    if (TextUtils.isEmpty(value.getText().toString())) {

                    } else {
                        payement.setMontant(payement.getMontant() + Double.parseDouble(value.getText().toString().replace(",",".")));
                        if (especExisxts) {
                            payement = Stream.of(payements).filter(i -> i.getType().equals(PayementType.ESPECE.toString())).single();
                            payement.setMontant(payement.getMontant() +Double.parseDouble(value.getText().toString().replace(",",".")));
                        } else {
                            payements.add(payement);
                            especExisxts = true;
                        }
                        payementAdapter.notifyDataSetChanged();
                        updateUI();
                    }
                    break;

                }
                case 1: { //cheque
                    Payment payement = new Payment();
                    if (TextUtils.isEmpty(value.getText().toString())) {

                    } else if (TextUtils.isEmpty(chequeDesc.getText().toString())) {
                        chequeDesc.setError("Ce champ est obligatoire");
                    } else {
                        payement.setMontant(Double.parseDouble(value.getText().toString().replace(",",".")));
                        payement.setType(PayementType.CHEQUE.toString());
                        payement.setQuantity(1);
                        payement.setCommande(null);
                        payement.setCommentaire(chequeDesc.getText().toString());
                        payements.add(payement);
                        payementAdapter.notifyDataSetChanged();
                        updateUI();
                    }
                    break;
                }
                case 2: { //bon
                    Payment payement = new Payment();


                    if (TextUtils.isEmpty(value.getText().toString())) {

                    } else if (TextUtils.isEmpty(quantity.getText().toString())) {

                    } else {
                        payement.setType(PayementType.BON.toString());
                        payement.setCommentaire("");
                        payement.setCommande(null);


                        if (bonExists) {
                            List<Payment> paymentBon = Stream.of(payements).filter(i -> i.getType().equals(PayementType.BON.toString())).toList();
                            Payment finalPayement = payement;

                            for (Payment a : paymentBon) {
                                if (a.getMontant() == Double.parseDouble(value.getText().toString().replace(",","."))) {
                                    valueExists = true;
                                    if (!quantity.getText().toString().isEmpty()) {
                                        a.setQuantity(a.getQuantity() + Integer.parseInt(quantity.getText().toString()));
                                    } else {
                                        a.setQuantity(a.getQuantity() + 1);
                                    }
                                    break;
                                }
                            }
                            if (!valueExists) {
                                payement.setMontant(Double.parseDouble(value.getText().toString().replace(",",".")));
                                if (!quantity.getText().toString().isEmpty()) {
                                    payement.setQuantity(Integer.parseInt(quantity.getText().toString()));
                                } else {
                                    payement.setQuantity(1);
                                }
                                payements.add(payement);
                            }
                            bonExists = true;
                            valueExists = false;
                        } else {
                            payement.setMontant(Double.parseDouble(value.getText().toString().replace(",",".")));
                            if (!quantity.getText().toString().isEmpty()) {
                                payement.setQuantity(Integer.parseInt(quantity.getText().toString()));
                            } else {
                                payement.setQuantity(1);
                            }
                            bonExists = true;
                            payements.add(payement);
                        }


                        payementAdapter.notifyDataSetChanged();

                        updateUI();
                    }


                    break;
                }
                case 3: { //contre-bon
                    if (TextUtils.isEmpty(chequeDesc.getText().toString())) {


                    } else {

                        ContreBon ctrvbon = new ContreBon();
                        ctrvbon.setCrypted(chequeDesc.getText().toString());
                        ContreBonRepository contreBonRepository = new ContreBonRepository();
                        ctrvbon = contreBonRepository.findByCode(ctrvbon.getCode());
                        if (ctrvbon.getDateSortie() == null) {
                            showMessage("Code invalide");
                        } else {

                            Calendar myCal = Calendar.getInstance();
                            myCal.setTime(ctrvbon.getDateSortie());
                            myCal.add(Calendar.MONTH, +1);
                            Date now = new Date();
                            if (now.after(myCal.getTime())) {
                                showMessage("Date de validité dépassé");
                            } else {
                                Payment payement = new Payment();
                                payement.setType(PayementType.CONTREBON.toString());
                                payement.setCommentaire("");
                                payement.setQuantity(1);
                                payement.setCommande(null);
                                if (contreBonExists) {
                                    payement = Stream.of(payements).filter(i -> i.getType().equals(PayementType.CONTREBON.toString())).single();
                                    payement.setMontant(payement.getMontant() + ctrvbon.getMontant());
                                } else {
                                    payement.setMontant(ctrvbon.getMontant());
                                    payements.add(payement);
                                    contreBonExists = true;
                                }
                                ctrvbon.setDatePay(now);
                                ctrvbon.setPayed(true);
                                payementAdapter.notifyDataSetChanged();
                                updateUI();

                            }

                        }


                    }
//re
//
//                    } else {
//                    Payment payement = new Payment();
//                    value.setHint("Valeur");
//                    if (TextUtils.isEmpty(value.getText().toString())) {
//re
//
//                    } else {
//                        payement.setType(PayementType.CONTREBON.toString());
//                        payement.setCommentaire("");
//                        payement.setQuantity(1);
//                        payement.setCommande(null);
//                        if (contreBonExists) {
//                            payement = Stream.of(payements).filter(i -> i.getType().equals(PayementType.CONTREBON.toString())).single();
//                            payement.setMontant(payement.getMontant() + Float.parseFloat(value.getText().toString()));
//                        } else {
//                            payement.setMontant(Float.parseFloat(value.getText().toString()));
//                            payements.add(payement);
//                            contreBonExists = true;
//                        }
//                        payementAdapter.notifyDataSetChanged();
//                        updateUI();
//                    }
                    break;
                }
            }
        });

    }


    @Override
    public void updateUI() {
        paidInt = 0;
        rendreInt = 0;
        paid.setVisibility(View.GONE);
        rendre.setVisibility(View.GONE);
        finish.setEnabled(false);
        for (Payment payment : payements) {

            paidInt = paidInt + payment.getQuantity() * payment.getMontant();

        }
        rendreInt = totalInt - paidInt;

        if (rendreInt > 0) {

            rendre.setText("Reste à payer: " + String.format("%.3f",rendreInt) + " dt");
            rendre.setVisibility(View.VISIBLE);
        } else {
            rendreInt = Math.abs(rendreInt);

            rendre.setText("A rendre: " + String.format("%.3f",rendreInt) + " dt");

            rendre.setVisibility(View.VISIBLE);


        }
        if (paidInt > 0) {

            paid.setText("Payé: " + String.format("%.3f",paidInt) + " dt");

            paid.setVisibility(View.VISIBLE);

        }
        if (Double.parseDouble(String.format("%.3f",paidInt).replace(",","."))>= Double.parseDouble(String.format("%.3f",totalInt).replace(",","."))) {

            finish.setEnabled(true);
        }
    }

    @Override
    public void onPayementItemSelected(Payment payement) {

    }

    @Override
    public void onPayementItemLongClickSelected(Payment payement) {

    }

    public boolean existsContreBon() {

        for (Payment payment :
                payements) {
            if (payment.getType().equals(PayementType.ESPECE.toString()))
                return true;
        }
        return false;
    }

    @Override
    public void onDeleteSelected(Payment payement) {

        Toast.makeText(this, payement.getType(), Toast.LENGTH_SHORT).show();
        payements.remove(payements.indexOf(payement));
        if (payement.getType().equals(PayementType.ESPECE.toString())) {
            especExisxts = false;
        } else if (payement.getType().equals(PayementType.CONTREBON.toString())) {
            contreBonExists = existsContreBon();
            contreBons.remove(payement.getContreBon());
        } else if (payement.getType().equals(PayementType.BON.toString())) {
            bonExists = false;
        }
        payementAdapter.notifyDataSetChanged();
        updateUI();
    }


    private void printJob() {

        if (!runPrintReceiptSequence()) {
            showMessage("Error Printjob");
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        while (true) {
            try {
                Discovery.stop();
                break;
            } catch (Epos2Exception e) {
                if (e.getErrorStatus() != Epos2Exception.ERR_PROCESSING) {
                    break;
                }
            }
        }

        mFilterOption = null;
    }

    private void restartDiscovery() {
        showMessage("Recherche d'imprimantes ...");
        while (true) {
            try {
                Discovery.stop();
                break;
            } catch (Epos2Exception e) {
                if (e.getErrorStatus() != Epos2Exception.ERR_PROCESSING) {
                    return;
                }
            }
        }
        mPrinterList.clear();
        try {
            Discovery.start(this, mFilterOption, mDiscoveryListener);
        } catch (Exception e) {
        }
    }

    private DiscoveryListener mDiscoveryListener = new DiscoveryListener() {
        @Override
        public void onDiscovery(final DeviceInfo deviceInfo) {
            runOnUiThread(new Runnable() {
                @Override
                public synchronized void run() {
                    HashMap<String, String> item = new HashMap<String, String>();
                    item.put("PrinterName", deviceInfo.getDeviceName());
                    item.put("Target", deviceInfo.getTarget());
                    mPrinterList.add(item);
                    connectPrinter(deviceInfo.getTarget());
                    printerFound = true;

                    target = deviceInfo.getTarget();
                    showMessage(deviceInfo.getDeviceName() + " Trouvée");

                }
            });
        }


    };

    private boolean createReceiptData(String type) {
        String method = "";
        Bitmap logoData = BitmapFactory.decodeResource(getResources(), R.drawable.logoprint);
        StringBuilder textData = new StringBuilder();


        DetailCommandeRepository detailCommandeRepository = new DetailCommandeRepository();
        if (mPrinter == null) {
            showMessage("mPrinter==null");
            return false;
        }
        if (type.equals("TICKET")) {
            try {
                method = "";
                textData = new StringBuilder();
                method = "addTextAlign";
                mPrinter.addTextAlign(Printer.ALIGN_CENTER);

                method = "addImage";
                mPrinter.addImage(logoData, 0, 0,
                        logoData.getWidth(),
                        logoData.getHeight(),
                        Printer.COLOR_1,
                        Printer.MODE_MONO,
                        Printer.HALFTONE_DITHER,
                        Printer.PARAM_DEFAULT,
                        Printer.COMPRESS_AUTO);
                textData.append("\n");
                method = "addFeedLine";
                mPrinter.addFeedLine(1);
                StoreRepository storeRepository = new StoreRepository();
                Store store = storeRepository.getActualStore();
                textData.append(store.getName()+" - "+ store.getPhone());
                textData.append("\n");
                textData.append(store.getAdress());
                textData.append("\n");
                textData.append("\n");
                textData.append("N° " + commande.getCommandeNumber());
                textData.append("\n");
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Log.d("reciept", "date: " + dateFormat.format(commande.getDate()));

                textData.append(dateFormat.format(commande.getDate()) + "\n");

                if (commande.getCustomer() != null ) {
                    textData.append("\n");
                    textData.append("\n");

                    textData.append("Client: " + commande.getCustomer().getName() + "\n");

                }

                textData.append("\n");


                textData.append("------------------------------\n");
                method = "addText";
                mPrinter.addText(textData.toString());
                textData.delete(0, textData.length());

                for (DetailCommande a : commande.getProducts()) {
                    if (a.getProductName() == null && a.getProduct() != null) {
                        textData.append(a.getQuantity() + "x    " + a.getProduct().getName() + "    " + String.format("%.3f",a.getProduct().getPrice()) + "\n");
                        textData.append("           " + String.format("%.3f",a.getProduct().getPrice() * a.getQuantity()) + "\n");
                    } else {
                        textData.append(a.getQuantity() + "x    " + a.getProductName() + "    " + String.format("%.3f",a.getPrice()) + "\n");
                        textData.append("           " + String.format("%.3f",a.getPrice() * a.getQuantity()) + "\n");
                    }
                }

                textData.append("------------------------------\n");
                method = "addText";
                mPrinter.addText(textData.toString());
                textData.delete(0, textData.length());

                if (commande.getCustomer() != null) {
                    if (commande.getCustomer().getCustomerGroup() != null) {
                        mPrinter.addText("Remise:    -" + commande.getCustomer().getCustomerGroup().getDiscount() + "%\n");
                    }

                }

                method = "addText";
                mPrinter.addText(textData.toString());
                textData.delete(0, textData.length());

                method = "addTextSize";
                mPrinter.addTextSize(2, 2);
                method = "addText";
                mPrinter.addText("TOTAL    " + String.format("%.3f",totalInt )+ " dt \n");
                method = "addTextSize";
//            mPrinter.addTextSize(1, 1);
                method = "addFeedLine";
                mPrinter.addFeedLine(1);
                mPrinter.addTextSize(1, 1);
                textData.append("------------------------------\n");
                method = "addText";
                mPrinter.addText(textData.toString());
                textData.delete(0, textData.length());

                textData.append("Merci pour votre visite.\n");
                method = "addText";
                mPrinter.addText(textData.toString());
                textData.delete(0, textData.length());
                method = "addFeedLine";
                mPrinter.addFeedLine(2);


                method = "addCut";
                mPrinter.addCut(Printer.CUT_FEED);

            } catch (Exception e) {
                return false;
            }

        } else if (type.equals("CONTREBON")) {

            final int pageAreaHeight = 500;
            final int pageAreaWidth = 500;
            final int fontAHeight = 24;
            final int fontAWidth = 12;

            method = "";
            textData = new StringBuilder();
            try {

                method = "";
                textData = new StringBuilder();
                method = "addTextAlign";
                mPrinter.addTextAlign(Printer.ALIGN_CENTER);

                method = "addImage";
                mPrinter.addImage(qrCodeBitmap, 0, 0, qrCodeBitmap.getWidth(), qrCodeBitmap.getHeight(), Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT, 3, Printer.PARAM_DEFAULT);

                textData.append("\n");

                method = "addFeedLine";
                mPrinter.addFeedLine(1);
                textData.append("Efficaisse - ESPRIT Tunisia");
                textData.append("\n");
                textData.append("\n");
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                Log.d("reciept", "date: " + dateFormat.format(commande.getDate()));

                textData.append(dateFormat.format(contreBonObject.getDateSortie()) + "\n");
                textData.append("------------------------------\n");
                method = "addText";
                mPrinter.addText(textData.toString());
                textData.delete(0, textData.length());


                method = "addText";
                mPrinter.addText(textData.toString());
                textData.delete(0, textData.length());

                method = "addTextSize";
                mPrinter.addTextSize(2, 2);
                method = "addText";
                mPrinter.addText("Valeur    " + String.format("%.3f",(paidInt - totalInt)) + " dt \n");
                method = "addTextSize";
//            mPrinter.addTextSize(1, 1);
                method = "addFeedLine";
                mPrinter.addFeedLine(1);
                mPrinter.addTextSize(1, 1);
                textData.append("------------------------------\n");
                method = "addText";
                mPrinter.addText(textData.toString());
                textData.delete(0, textData.length());

                Calendar myCal = Calendar.getInstance();
                myCal.setTime(contreBonObject.getDateSortie());
                myCal.add(Calendar.MONTH, +1);
                textData.append("Valable jusqu'à\n");
                textData.append(dateFormat.format(myCal.getTime()) + "\n");


                textData.append("------------------------------\n");
                method = "addText";
                mPrinter.addText(textData.toString());
                textData.delete(0, textData.length());
                method = "addFeedLine";
                mPrinter.addFeedLine(2);


                method = "addCut";
                mPrinter.addCut(Printer.CUT_FEED);
            } catch (Exception e) {
                return false;
            }

        }
        textData = null;
//        Calendar myCal = Calendar.getInstance();
//        myCal.setTime(commande.getDate());
//        myCal.add(Calendar.MONTH, +1);
//        textData.append(dateFormat.format(myCal.getTime()) + "\n");

        return true;

    }


    private boolean runPrintReceiptSequence() {

        if (!initializeObject()) {
            showMessage("initializzeObject");
            restartDiscovery();
            return false;
        }

        if (!createReceiptData("TICKET")) {
            showMessage("createReceiptData");

            finalizeObject();

            restartDiscovery();
            return false;
        }
        if (!isThereContreBon) {
            if (!createReceiptData("CONTREBON")) {
                showMessage("createQRRecipt");

                finalizeObject();

                restartDiscovery();
                return false;
            }
        }

        if (!printData()) {
            showMessage("printData");

            finalizeObject();
            restartDiscovery();
            return false;
        }


        return true;
    }

    private boolean initializeObject() {
        try {
            mPrinter = new Printer(Printer.TM_T20, Printer.MODEL_ANK, this);
        } catch (Exception e) {
            return false;
        }

        mPrinter.setReceiveEventListener(this);

        return true;
    }

    private boolean connectPrinter(String target) {
        boolean isBeginTransaction = false;

        if (mPrinter == null) {
            return false;
        }

        try {
            mPrinter.connect(target, Printer.PARAM_DEFAULT);
        } catch (Exception e) {
            return false;
        }

        try {
            mPrinter.beginTransaction();
            isBeginTransaction = true;
        } catch (Exception e) {
        }

        if (isBeginTransaction == false) {
            try {
                mPrinter.disconnect();
            } catch (Epos2Exception e) {
                // Do nothing
                return false;
            }
        }

        return true;
    }

    private boolean printData() {
        if (mPrinter == null) {
            showMessage("mPrinter");

            return false;
        }

        if (!connectPrinter(target)) {
            showMessage("connectPrinter");
            return false;
        }

        PrinterStatusInfo status = mPrinter.getStatus();


        if (!isPrintable(status)) {
            showMessage("isPrintable");
            try {
                mPrinter.disconnect();
            } catch (Exception ex) {
                // Do nothing
            }
            return false;
        }

        try {
            mPrinter.sendData(Printer.PARAM_DEFAULT);

        } catch (Exception e) {
            try {
                mPrinter.disconnect();
            } catch (Exception ex) {
                // Do nothing
            }
            return false;
        }

        return true;
    }

    private void disconnectPrinter() {
        if (mPrinter == null) {
            return;
        }

        try {
            mPrinter.endTransaction();
        } catch (final Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public synchronized void run() {
                }
            });
        }

        try {
            mPrinter.disconnect();
        } catch (final Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public synchronized void run() {

                }
            });
        }

        finalizeObject();
    }

    private void finalizeObject() {
        if (mPrinter == null) {
            return;
        }

        mPrinter.clearCommandBuffer();

        mPrinter.setReceiveEventListener(null);

        mPrinter = null;
    }

    private boolean isPrintable(PrinterStatusInfo status) {
        if (status == null) {
            return false;
        }

        if (status.getConnection() == Printer.FALSE) {
            return false;
        } else if (status.getOnline() == Printer.FALSE) {
            return false;
        } else {
            showMessage("Printer Mawjouda");
        }

        return true;
    }


    @Override
    public void onPtrReceive(final Printer printerObj, final int code, final PrinterStatusInfo status, final String printJobId) {
        runOnUiThread(new Runnable() {
            @Override
            public synchronized void run() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        disconnectPrinter();
                    }
                }).start();
            }
        });
    }


    @Override
    public void onQRCodeRead(String text, PointF[] points) {
        ContreBon contreBonBon = new ContreBon(text);
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

        if (contreBonBon.getCode() != null) {
            final MediaPlayer mp = MediaPlayer.create(this, R.raw.bititit);
            mp.start();
            contreBonBon = ContreBonRepository.findByCode(contreBonBon.getCode());
            Calendar myCal = Calendar.getInstance();
            myCal.setTime(contreBonBon.getDateSortie());
            myCal.add(Calendar.MONTH, +1);
            dateFormat.format(myCal.getTime());
            if (contreBonBon != null && !contreBons.contains(contreBonBon)) {
                if (!contreBonBon.isPayed() && myCal.getTime().after(new Date())) {

                    Payment payement = new Payment();
                    payement.setMontant(contreBonBon.getMontant());
                    payement.setType(PayementType.CONTREBON.toString());
                    payement.setQuantity(1);
                    payement.setCommande(null);
                    payement.setCommentaire("");
                    payement.setContreBon(contreBonBon);
                    contreBonBon.setDatePay(new Date());
                    contreBonBon.setPayed(true);
                    contreBons.add(contreBonBon);
                    payements.add(payement);
                    payementAdapter.notifyDataSetChanged();
                    updateUI();
                    qrCodeReaderView.stopCamera();
                    dialog.dismiss();

                    type = 0;
                    value.setVisibility(View.VISIBLE);
                    quantityLayout.setVisibility(View.GONE);
                    chequeDesc.setVisibility(View.GONE);

                    bon.setColor(Color.LTGRAY);
                    cheque.setColor(Color.LTGRAY);
                    contreBon.setColor(Color.LTGRAY);
                    cash.setColor(getResources().getColor(R.color.colorAccent));
                    value.setHint("Valeur");


                    value.setText("");
                    quantity.setText("");
                    chequeDesc.setText("");


                } else {
                    showMessage("Contre bon invalide");
                }
            }
        }

    }


}
