package tn.meteor.efficaisse.ui.customer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.epson.epos2.Epos2Exception;
import com.epson.epos2.discovery.DeviceInfo;
import com.epson.epos2.discovery.Discovery;
import com.epson.epos2.discovery.DiscoveryListener;
import com.epson.epos2.discovery.FilterOption;
import com.epson.epos2.printer.Printer;
import com.epson.epos2.printer.PrinterStatusInfo;
import com.epson.epos2.printer.ReceiveListener;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import tn.meteor.efficaisse.R;
import tn.meteor.efficaisse.data.repository.CustomerGroupRepository;
import tn.meteor.efficaisse.data.repository.DetailCommandeRepository;
import tn.meteor.efficaisse.data.repository.StoreRepository;
import tn.meteor.efficaisse.model.Customer;
import tn.meteor.efficaisse.model.CustomerGroup;
import tn.meteor.efficaisse.model.Customer_Table;
import tn.meteor.efficaisse.model.Store;
import tn.meteor.efficaisse.ui.base.BaseActivity;
import tn.meteor.efficaisse.utils.QrCodeUtils;


public class CustomerActivity extends BaseActivity implements ReceiveListener, CustomerContract.View {


    private EditText name, email, phone;
    private Button finish;
    private Spinner group;
    private CustomerPresenter customerPresenter;
    private Bitmap qrCodeBitmap = null;
    private Customer customer;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);
        customerPresenter = new CustomerPresenter(this);
        bindComponent();

        customer = new Customer();
        CustomerGroupRepository customerGroupRepository = new CustomerGroupRepository();
        List<CustomerGroup> customerGroups = customerGroupRepository.findAll();


        ArrayAdapter<CustomerGroup> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, customerGroups);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);


        group.setAdapter(adapter);
        group.setSelection(0);

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


        finish.setOnClickListener(view -> {

            if (SQLite.select().from(Customer.class).where(Customer_Table.name.eq(name.getText().toString())).querySingle() != null) {
                showMessage("Un client avec le mème nom existe déjà");
            } else {
                if (TextUtils.isEmpty(name.getText().toString()) || TextUtils.isEmpty(phone.getText().toString()) || TextUtils.isEmpty(email.getText().toString()))
                    showMessage("Veuillez remplir tous les champs");
                else {

                    if (customer.getCustomerGroup().getName().equals(getResources().getString(R.string.sans_groupe))) {
                        customer.setCustomerGroup(null);
                    }

                    customer.setName(name.getText().toString());
                    customer.setEmail(email.getText().toString());
                    customer.setPhone(phone.getText().toString());
                    customer.setJoinDate(new Date());

                   qrCodeBitmap = QrCodeUtils.GenerateClick(customer.getCrypted());
                printJob();
                    customerPresenter.addCustomer(customer);


                }
            }
        });

  initializePrinter();


    }


    @Override
    public void bindComponent() {


        finish = findViewById(R.id.finish);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        group = findViewById(R.id.group);


    }

    @Override
    public void updateUI() {
        finish();
    }

    private ArrayList<HashMap<String, String>> mPrinterList = null;
    private FilterOption mFilterOption = null;
    private boolean printerFound = false;
    private Printer mPrinter = null;
    private String target = "";


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

    private boolean createReceiptData() {
        String method = "";
        Bitmap logoData = BitmapFactory.decodeResource(getResources(), R.drawable.logoprint);
        StringBuilder textData = new StringBuilder();


        DetailCommandeRepository detailCommandeRepository = new DetailCommandeRepository();
        if (mPrinter == null) {
            showMessage("mPrinter==null");
            return false;
        }


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
            StoreRepository storeRepository = new StoreRepository();
            Store store = storeRepository.getActualStore();
            textData.append(store.getName()+" - "+ store.getPhone());
            textData.append("\n");
            textData.append(store.getAdress());
            textData.append("\n");
            textData.append("\n");

            textData.append("Client: " + customer.getName().toString() + "\n");
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
            if (customer.getCustomerGroup() != null) {
                mPrinter.addText("Remise: -" + customer.getCustomerGroup().getDiscount() + "% \n");


                method = "addFeedLine";
                mPrinter.addTextSize(1, 1);
                textData.append("------------------------------\n");
            }
            method = "addFeedLine";
            mPrinter.addTextSize(1, 1);
            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());


            textData.append("La carte est strictement personnelle \n et individuelle et ne peut en aucun cas \n être cédée, prêtée, vendue à quiconque." + "\n");

            textData.append("\n");

            textData.append("*****\n");
            textData.append("\n");
            textData.append("En cas de perte merci d'appeler: \n");
            textData.append("\n");
            textData.append(customer.getPhone());
            textData.append("\n");

            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());
            method = "addFeedLine";
            mPrinter.addFeedLine(2);


            method = "addCut";
            mPrinter.addCut(Printer.CUT_FEED);
        } catch (Exception e) {


            Toast.makeText(this, method + "///" + e.getMessage(), Toast.LENGTH_LONG).show();
            return false;
        }


        textData = null;

        return true;

    }


    private boolean runPrintReceiptSequence() {

        if (!initializeObject()) {
            showMessage("initializzeObject");
            restartDiscovery();
            return false;
        }

        if (!createReceiptData()) {
            showMessage("createReceiptData");

            finalizeObject();

            restartDiscovery();
            return false;
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
        updateUI();
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

}
