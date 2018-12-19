package tn.meteor.efficaisse.data;


import tn.meteor.efficaisse.utils.SyncPushListener;

/**
 * Created by SKIIN on 07/02/2018.
 */

public interface DataManager {


    void clearData();
    Long logSize();
    void synchronizationData(SyncPushListener listener);



}
