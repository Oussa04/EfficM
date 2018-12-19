package tn.meteor.efficaisse.ui.offlineLogin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;

import java.util.ArrayList;
import java.util.List;

import tn.meteor.efficaisse.R;
import tn.meteor.efficaisse.adapter.UserAdapter;
import tn.meteor.efficaisse.data.preferences.AppPreferencesHelper;
import tn.meteor.efficaisse.data.preferences.PreferencesHelper;
import tn.meteor.efficaisse.data.repository.CashierRepository;
import tn.meteor.efficaisse.data.repository.SessionRepository;
import tn.meteor.efficaisse.data.repository.UserRepository;
import tn.meteor.efficaisse.model.Cashier;
import tn.meteor.efficaisse.model.Session;
import tn.meteor.efficaisse.ui.Homie;
import tn.meteor.efficaisse.ui.base.BaseActivity;
import tn.meteor.efficaisse.utils.Constants;
import tn.meteor.efficaisse.utils.EfficaisseApplication;
import tn.meteor.efficaisse.utils.ItemOffsetDecoration;


public class OfflineLoginActivity extends BaseActivity implements UserAdapter.GridAdapterListener, OfflineLoginContract.View {


    private EditText name, email;
    private OfflineLoginPresenter cashierPresenter;
    private TextView texty;
    private PatternLockView mPatternLockView;
    private List<Cashier> userList;
    private UserAdapter usersAdapter;
    private RecyclerView users;
    private MaterialDialog dialog;
    private Button confirmer;
    private Cashier selectedUser;
    private UserRepository userRepo;
    private PreferencesHelper prefs;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_login);
        cashierPresenter = new OfflineLoginPresenter(this);
        bindComponent();


        getSupportActionBar().setTitle("Choisir un utilisateur");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setElevation(0);

        userList = new ArrayList<>();
        userList.addAll(CashierRepository.findAll());

        prefs = new AppPreferencesHelper(EfficaisseApplication.getInstance().getApplicationContext(), Constants.SHARED_PREFERENCES_NAME.PREFERENCES_USER);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(6, LinearLayoutManager.VERTICAL);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(this, R.dimen.item_product_offset);

        usersAdapter = new UserAdapter(this, this, userList);
        users.addItemDecoration(itemDecoration);
        users.setLayoutManager(staggeredGridLayoutManager);
        users.setAdapter(usersAdapter);


        MaterialDialog.Builder builder = new MaterialDialog.Builder(this)
                .customView(R.layout.dialog_pattern, false);

        dialog = builder.build();

        mPatternLockView = (PatternLockView) dialog.findViewById(R.id.pattern_lock);
        texty = (TextView) dialog.findViewById(R.id.texty);
        confirmer = (Button) dialog.findViewById(R.id.confirmer);


        mPatternLockView.addPatternLockListener(mPatternLockViewListener);


    }


    @Override
    public void bindComponent() {


        email = findViewById(R.id.email);
        name = findViewById(R.id.name);
        users = findViewById(R.id.users);


    }

    @Override
    public void updateUI(Cashier c) {
        Session session = new Session();
        session.setUser(c);
        SessionRepository.open(session);
        ActivityCompat.finishAffinity(this);
        finish();

        Intent a= new Intent(this, Homie.class);

        startActivity(a);

    }


    private String firstValue = "";
    private String secondValue;
    private PatternLockViewListener mPatternLockViewListener = new PatternLockViewListener() {
        @Override
        public void onStarted() {
            Log.d(getClass().getName(), "Pattern drawing started");
        }

        @Override
        public void onProgress(List<PatternLockView.Dot> progressPattern) {
            Log.d(getClass().getName(), "Pattern progress: " +
                    PatternLockUtils.patternToString(mPatternLockView, progressPattern));
        }

        @Override
        public void onComplete(List<PatternLockView.Dot> pattern) {
            Log.d(getClass().getName(), "Pattern complete: " +
                    PatternLockUtils.patternToString(mPatternLockView, pattern));
            if (PatternLockUtils.patternToString(mPatternLockView, pattern).length() > 2) {
                if (selectedUser.getPattern() != null) {
                    selectedUser.setSchema(Long.valueOf(PatternLockUtils.patternToString(mPatternLockView, pattern)));
                    if (selectedUser.isValid()) {

                        mPatternLockView.setViewMode(PatternLockView.PatternViewMode.CORRECT);
                        prefs.setUsername(selectedUser.getUsername());
                        updateUI(selectedUser);

                    } else {

                        mPatternLockView.setViewMode(PatternLockView.PatternViewMode.WRONG);

                    }

                } else {

                    if (firstValue.equals("")) {
                        firstValue = PatternLockUtils.patternToString(mPatternLockView, pattern);
                        mPatternLockView.clearPattern();

                        texty.setText("Veuillez redissiner le schéma");
                        confirmer.setVisibility(View.VISIBLE);
                        confirmer.setText("Terminer");
                    } else {

                        if (PatternLockUtils.patternToString(mPatternLockView, pattern).equals(firstValue)) {

                            mPatternLockView.setViewMode(PatternLockView.PatternViewMode.CORRECT); // HAMAX
                            confirmer.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    selectedUser.setSchema(Long.parseLong(firstValue));
                                    CashierRepository.update(selectedUser);
                                    prefs.setUsername(selectedUser.getUsername());

                                    updateUI(selectedUser);
                                }
                            });

                        } else {

                            mPatternLockView.setViewMode(PatternLockView.PatternViewMode.WRONG);

                        }

                    }


                }
            } else {

                mPatternLockView.setViewMode(PatternLockView.PatternViewMode.WRONG);
                Toast.makeText(OfflineLoginActivity.this, "Schéma trés court", Toast.LENGTH_SHORT).show();

            }
        }

        @Override
        public void onCleared() {
            Log.d(getClass().getName(), "Pattern has been cleared");
        }
    };

    @Override
    public void onUserSelected(Cashier user) {
        mPatternLockView.clearPattern();
        dialog.show();
        firstValue = "";
        if (user.getPattern() == null) {


            texty.setText("Veuillez créer un nouveau schéma");
            confirmer.setVisibility(View.GONE);


        } else {
            texty.setText("Introduire le schéma");

            confirmer.setVisibility(View.GONE);

        }
        selectedUser = user;
    }

    @Override
    public void onUserLongClicked(Cashier user) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
