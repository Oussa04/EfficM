package tn.meteor.efficaisse;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.epson.epos2.Epos2Exception;
import com.epson.epos2.discovery.DeviceInfo;
import com.epson.epos2.discovery.Discovery;
import com.epson.epos2.discovery.DiscoveryListener;
import com.epson.epos2.discovery.FilterOption;
import com.epson.epos2.printer.Printer;
import com.epson.epos2.printer.PrinterStatusInfo;
import com.epson.epos2.printer.ReceiveListener;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import tn.meteor.efficaisse.data.repository.DetailCommandeRepository;
import tn.meteor.efficaisse.model.Commande;
import tn.meteor.efficaisse.model.DetailCommande;
import tn.meteor.efficaisse.ui.base.BaseActivity;

public class PayActivity extends BaseActivity implements ReceiveListener {

    private ArrayList<HashMap<String, String>> mPrinterList = null;
    private FilterOption mFilterOption = null;
    private Button print;
    private Button newSale;
    private TextView sum;
    private boolean printerFound = false;
    private LinearLayout searching;
    private Printer mPrinter = null;
    private String target = "";
    private BigDecimal result;
    private int idCommande;
    private Commande commande;
    private double totalSum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_effipay);
        searching = (LinearLayout) findViewById(R.id.searching);
        print = (Button) findViewById(R.id.print);
        sum = (TextView) findViewById(R.id.sum);
        newSale = (Button) findViewById(R.id.newSale);


        mPrinterList = new ArrayList<HashMap<String, String>>();
        float total = 0;


        try {

            Intent intent = getIntent();
            idCommande = intent.getIntExtra("commande", -1);

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (idCommande > -1) {
            DetailCommandeRepository detailCommandeRepository = new DetailCommandeRepository();
            commande = detailCommandeRepository.find(idCommande);
            totalSum = detailCommandeRepository.getCommandeSum(commande);
        }


        print.setEnabled(false);
        print.setText("Recherche d'imprimantes...");
        mFilterOption = new FilterOption();
        mFilterOption.setDeviceType(Discovery.TYPE_PRINTER);
        mFilterOption.setEpsonFilter(Discovery.FILTER_NAME);

        try {
            Discovery.start(this, mFilterOption, mDiscoveryListener);
        } catch (Epos2Exception e) {
            e.printStackTrace();
        }

        print.setOnClickListener(view -> printTicket());


        BroadcastReceiver receiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getExtras().getBoolean("connected")) {
                    //start doing something for state - connected
                } else {
                    restartDiscovery();
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction("android.hardware.usb.action.USB_STATE");

        registerReceiver(receiver, filter);
        newSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoading();

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("reset", true);
                hideLoading();
                startActivity(intent);
            }
        });

    }

    private void printTicket() {

        if (!runPrintReceiptSequence()) {
            print.setEnabled(true);
            print.setText("Imprimer Ticket");

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

//        while (true) {
//            try {
//                Discovery.stop();
//                break;
//            } catch (Epos2Exception e) {
//                if (e.getErrorStatus() != Epos2Exception.ERR_PROCESSING) {
//                    break;
//                }
//            }
//        }
//
//        mFilterOption = null;
    }

    private void restartDiscovery() {
        print.setEnabled(false);
        print.setText("Recherche d'imprimantes...");
        searching.setVisibility(View.VISIBLE);
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
                    print.setEnabled(true);
                    print.setText("Imprimer Ticket");
                    searching.setVisibility(View.INVISIBLE);
                    target = deviceInfo.getTarget();
                    Toast.makeText(PayActivity.this, deviceInfo.getDeviceName() + " Trouvée", Toast.LENGTH_SHORT).show();
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

        try {
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
            textData.append("Efficaisse - ESPRIT Tunisia");
            textData.append("\n");
            textData.append("\n");
            textData.append("N° " + commande.getCommandeNumber());
            textData.append("\n");
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Log.d("reciept", "date: " + dateFormat.format(commande.getDate()));

            textData.append(dateFormat.format(commande.getDate()) + "\n");
            textData.append("------------------------------\n");
            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            for (DetailCommande a : commande.getProducts()) {
                if (a.getProductName() == null && a.getProduct() != null) {
                    textData.append(a.getQuantity() + "x    " + a.getProduct().getName() + "    " + a.getProduct().getPrice() + "\n");
                    textData.append("           " + a.getProduct().getPrice() * a.getQuantity() + "\n");
                } else {
                    textData.append(a.getQuantity() + "x    " + a.getProductName() + "    " + a.getPrice() + "\n");
                    textData.append("           " + a.getPrice() + "\n");
                }
            }

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
            mPrinter.addText("TOTAL    " + detailCommandeRepository.getCommandeSum(commande) + " dt \n");
            method = "addTextSize";
//            mPrinter.addTextSize(1, 1);
            method = "addFeedLine";
            mPrinter.addFeedLine(1);
            mPrinter.addTextSize(1, 1);
            textData.append("------------------------------\n");
            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());
            textData.append(detailCommandeRepository.getCommandeQuantity(commande) + " éléments");

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

        textData = null;

        return true;
    }


    private boolean runPrintReceiptSequence() {
        print.setEnabled(false);
        print.setText("Impression...");
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
            return false;
        }

        if (!connectPrinter(target)) {
            return false;
        }

        PrinterStatusInfo status = mPrinter.getStatus();


        if (!isPrintable(status)) {
            try {
                mPrinter.disconnect();
            } catch (Exception ex) {
                // Do nothing
            }
            return false;
        }

        try {
            mPrinter.sendData(Printer.PARAM_DEFAULT);
            print.setEnabled(true);
            print.setText("Imprimer Ticket");
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
            ;//print available
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
