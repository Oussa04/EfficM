package tn.meteor.efficaisse.utils;

import java.util.List;

import tn.meteor.efficaisse.model.ErrorSync;

/**
 * Created by ahmed on 02/15/18.
 */

public interface SyncPushListener {
    void onSyncPushFinished(List<ErrorSync> errors);
}
