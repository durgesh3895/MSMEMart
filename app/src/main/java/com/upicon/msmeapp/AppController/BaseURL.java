 package com.upicon.msmeapp.AppController;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

 public class BaseURL {

     public static final String TOKEN= "08ee418395cb6e2c8df58eb949feaadc";

     public static String BASE_PATH = "http://durgesh3895.000webhostapp.com/";
     //public static String BASE_PATH = "http://192.168.43.96/";

     private static final String BASE_FOLDER= BASE_PATH + "msme_mart/";
     public static final String IMAGE_PATH= BASE_FOLDER + "images/products/";

     public static final String USER_LOGIN= BASE_FOLDER+ "UserLogin.php";
     public static final String USERS_LIST= BASE_FOLDER+ "UserList.php";
     public static final String REGISTER_USER= BASE_FOLDER+ "UserRegistration.php";
     public static final String UPDATE_USER= BASE_FOLDER+ "UpdateUser.php";
     public static final String PRODUCT_LIST= BASE_FOLDER+ "ProductList.php";
     public static final String ADD_UNIT= BASE_FOLDER+ "AddUnit.php";
     public static final String CHANGE_PASSWORD= BASE_FOLDER+ "ChangePassword.php";
     public static final String USER_COUNT= BASE_FOLDER+ "UserCount.php";
     public static final String ADD_TO_CART= BASE_FOLDER+ "AddToCart.php";
     public static final String GET_CART= BASE_FOLDER+ "GetCart.php";
     public static final String REMOVE_CART= BASE_FOLDER+ "RemoveCart.php";





    public static boolean isOnline(Context applicationContext) {

        ConnectivityManager cm = (ConnectivityManager) applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();

    }


}
