package tn.meteor.efficaisse.model;

/**
 * Created by SKIIN on 22/01/2018.
 */

public class Role {


    private String authority;

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }




    public Role(String authority) {
        this.authority = authority;
    }
}
