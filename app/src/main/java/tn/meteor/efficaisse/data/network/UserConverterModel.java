package tn.meteor.efficaisse.data.network;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.converter.DateConverter;

import java.util.Date;
import java.util.Set;

import tn.meteor.efficaisse.model.Licence;
import tn.meteor.efficaisse.model.Role;
import tn.meteor.efficaisse.model.Store;
import tn.meteor.efficaisse.model.User;
import tn.meteor.efficaisse.utils.LicenceConverter;
import tn.meteor.efficaisse.utils.RoleConverter;
import tn.meteor.efficaisse.utils.StoreConverter;

/**
 * Created by ahmed on 03/14/18.
 */

public class UserConverterModel {

    private Integer id;

    private String name;

    private String username;

    private String email;

    private StoreConverter store;


    private Set<Role> authorities;
    private  class StoreConverter{
        private Integer id;

        private String name;

        private String adress;

        private String phone;
        private StoreType type;
        private class StoreType {
           private  int type;
        }


        private Licence licence;

        private Date payDate;

        private String registerDC;
        private Store toDbModel() {
            Store store = new Store(id,name,adress,phone,licence,payDate.getTime(),registerDC);
            store.setType(type.type);
            return store;
        }

    }
    public User toDbModel(){
        User user =  new User(id,name,username,email,store.toDbModel(),authorities);

        return user;
    }
}
