
package tn.meteor.efficaisse.data.preferences;


import android.content.Context;
import android.content.SharedPreferences;


public class AppPreferencesHelper implements PreferencesHelper {

    private final SharedPreferences mPrefs;

    private static final String ACCES_TOKEN = "PREF_KEY_ACCESS_TOKEN";
    private static final String REFRESH_TOKEN = "PREF_KEY_REFRESH_TOKEN";
    private static final String STORE_RDC = "PREF_KEY_STORE_RDC";
    private static final String USERNAME = "PREF_KEY_USERNAME";
    private static final String KEY = "PREF_KEY_KEY";
    private static final String ACCES_TYPE = "PREF_KEY_ACCES_TYPE";
    private static final String STORE_TYPE = "PREF_KEY_STORE_TYPE";
    private static final String IS_SYNCING = "PREF_KEY_SYNCING";


    public AppPreferencesHelper(Context context, String prefName) {
        mPrefs = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
    }


    @Override
    public String getAccessToken() {
        return mPrefs.getString(ACCES_TOKEN, null);
    }

    @Override
    public void setAccesToken(String accesToken) {
        mPrefs.edit().putString(ACCES_TOKEN, accesToken).commit();
    }

    @Override
    public String getRefreshToken() {
        return mPrefs.getString(REFRESH_TOKEN, null);
    }

    @Override
    public void setRefreshToken(String refreshToken) {
        mPrefs.edit().putString(REFRESH_TOKEN, refreshToken).commit();
    }

    @Override
    public String getStoreRDC() {
        return mPrefs.getString(STORE_RDC, null);
    }

    @Override
    public void setStoreRDC(String storeRDC) {
        mPrefs.edit().putString(STORE_RDC, storeRDC).commit();
    }

    @Override
    public String getUsername() {
        return mPrefs.getString(USERNAME, null);
    }

    @Override
    public void setUsername(String username) {
        mPrefs.edit().putString(USERNAME, username).commit();
    }

    @Override
    public String getCipherKey() {
        return mPrefs.getString(KEY, null);
    }

    @Override
    public void setCipherKey(String key) {
        mPrefs.edit().putString(KEY, key).commit();
    }


    @Override
    public int getStoreType() {
        return mPrefs.getInt(STORE_TYPE, -1);
    }

    @Override
    public void setStoreType(int storeType) {
        mPrefs.edit().putInt(STORE_TYPE, storeType).commit();
    }

    @Override
    public boolean isSyncing() {
        return mPrefs.getBoolean(IS_SYNCING, false);
    }

    @Override
    public void setSyncing(boolean syncing) {
        mPrefs.edit().putBoolean(IS_SYNCING, syncing).commit();
    }

    //Acces Type
    @Override
    public String getAccesType() {
        return mPrefs.getString(ACCES_TYPE, null);
    }

    @Override
    public void setAccesType(String accesType) {
        mPrefs.edit().putString(ACCES_TYPE, accesType).commit();

    }


}
