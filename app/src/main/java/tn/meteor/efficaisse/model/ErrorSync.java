package tn.meteor.efficaisse.model;

/**
 * Created by ahmed on 02/12/18.
 */

public class ErrorSync {
    private Sync sync;
    private String errorMessage;

    public Sync getSync() {
        return sync;
    }

    public void setSync(Sync sync) {
        this.sync = sync;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public ErrorSync(Sync sync, String errorMessage) {
        this.sync = sync;
        this.errorMessage = errorMessage;
    }

    public ErrorSync() {
    }
}
