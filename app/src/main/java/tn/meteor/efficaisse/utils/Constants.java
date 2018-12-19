package tn.meteor.efficaisse.utils;

public class Constants {


    public static class HTTP {

        public static final String BASE_URL = "http://172.16.212.202:8080/";
        private static final String OAUTH =  "oauth";
        private static final String SYNC ="sync";
        public static final String ACCESS_TOKEN = OAUTH +"/token";
        private static final String CASHIER = "cashier";


        public static final String CURRENT_USER = CASHIER+"/user/get";
        public static final String CURRENT_KEY = CASHIER +"/key/show";
        public static final String ADD_CASHIER = "manager/cashier/add";
        public static final String CHANGE_PATTERN = SYNC + "/user/pattern";
        public static final String GET_USERS= CASHIER + "/user/getAll";

        public static final String IMAGE_URL_CAT = BASE_URL+"images/categories/";
        public static final String IMAGE_URL_ING = BASE_URL+"images/ingredients/";
        public static final String IMAGE_URL_PRO = BASE_URL+"images/products/";


        public static final String GET_PRODUCTS = CASHIER + "/product/show";
        public static final String ADD_PRODUCT = SYNC + "/product/add";
        public static final String UPDATE_PRODUCT = SYNC + "/product/update";
        public static final String DELETE_PRODUCT = SYNC + "/product/delete/{id}";


        public static final String ADD_PRODUCT_INGREDIENT=SYNC+"/product/ingredient/add";
        public static final String DELETE_PRODUCT_INGREDIENT=SYNC+"/product/ingredient/delete";
        public static final String UPDATE_PRODUCT_INGREDIENT=SYNC+"/product/ingredient/update";

        public static final String GET_INGREDIENTS = CASHIER+"/ingredient/show";
        public static final String ADD_INGREDIENT = SYNC+"/ingredient/add";
        public static final String DELETE_INGREDIENT = SYNC+"/ingredient/delete/{id}";
        public static final String UPDATE_INGREDIENT = SYNC+"/ingredient/update";

        public static final String SHOW_CATEGORY = CASHIER+"/category/show";
        public static final String ADD_CATEGORY = SYNC+"/category/add";
        public static final String UPDATE_CATEGORY = SYNC+"/category/update/{name}";
        public static final String DELETE_CATEGORY = SYNC+"/category/delete/{name}";

        public static final String ADD_COMMANDE = SYNC+"/commande/add";
        public static final String GET_COMMANDE = CASHIER+"/commande/show";
        public static final String ADD_PAYMENT = SYNC+"/payment/assign";

        public static final String ADD_HISTORY = SYNC +"/history/add";
        public static final String GET_HISTORY = CASHIER + "/history/show";


        public static final String ADD_CONTREBON = SYNC +"/contreBon/add";
        public static final String GET_CONTREBON = CASHIER + "/contreBon/show";


        public static final String ADD_CUSTOMER_GROUP = SYNC + "/customer/addGroup";
        public static final String MODIFY_CUSTOMER_GROUP = SYNC + "/customer/modifyGroup";
        public static final String DELETE_CUSTOMER_GROUP = SYNC + "/customer/deleteGroup";
        public static final String ADD_CUSTOMER = SYNC + "/customer/add";
        public static final String UPDATE_CUSTOMER = SYNC + "/customer/update";
        public static final String GET_CUSTOMER_GROUPS = CASHIER + "/customerGroup/get";
        public static final String GET_CUSTOMERS = CASHIER + "/customer/get";


        public static final String ADD_SESSION = SYNC +"/session/add";
        public static final String GET_SESSION = CASHIER + "/session/get";

        public static final String GET_WORLDCUP_FiXTURES = "http://api.football-data.org/v1/competitions/467/fixtures";
        public static final String GET_CHAMPIONSLEAGUE_FiXTURES = "http://api.football-data.org/v1/competitions/464/fixtures";




        public static final String ADD_DISCOUNT = SYNC + "/discount/add";
        public static final String UPDATE_DISCOUNT = SYNC  + "/discount/update";
        public static final String GET_DISCOUNT = CASHIER + "/discount/get";




        public static final String  ADD_STACKTRACE = SYNC + "/stackTrace/add";
        public static final String ADD_TABLE = SYNC + "/table/add";
        public static final String GET_TABLE = CASHIER + "/table/get";
    }



    public static class SHARED_PREFERENCES_NAME {

        public static final String PREFERENCES_USER = "users";
        public static final String PREFERENCES_ACCES_TYPE = "access";

    }

    public static final String TIMESTAMP_FORMAT = "dd/MM/yyyy HH:mm:ss";
    public static final String DATE_FORMAT = "dd/MM/yyyy";


}
