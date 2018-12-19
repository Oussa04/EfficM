package tn.meteor.efficaisse.data.preferences;


public interface PreferencesHelper {

    String getAccessToken();

    void setAccesToken(String accesToken);

    String getRefreshToken();

    void setRefreshToken(String refreshToken);

    String getStoreRDC();

    void setStoreRDC(String storeRDC);

    String getUsername();

    void setUsername(String username);

    String getCipherKey();

    void setCipherKey(String key);

    String getAccesType();

    void setAccesType(String accesType);

    int getStoreType();

    void setStoreType(int storeType);
    boolean isSyncing ();
    void setSyncing(boolean syncing);


}
