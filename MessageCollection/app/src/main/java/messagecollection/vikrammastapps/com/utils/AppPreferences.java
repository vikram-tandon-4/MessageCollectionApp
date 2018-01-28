package messagecollection.vikrammastapps.com.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AppPreferences {

    private static String FAVORITE_LIST = "favoriteList";
    private static String FIRST_TIME = "firstTime";



    /*
    *
    * Saving and Retrieving FavoriteList
    * */

    public static void setFavoriteList(Context context, ArrayList<String> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        SharedPreferences _sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = _sharedPreferences.edit();
        editor.putString(FAVORITE_LIST, json);
        editor.commit();
    }

    public static ArrayList<String> getFavoriteList(Context context) {
        Gson gson = new Gson();
        ArrayList<String> favoriteList;
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String jsonPreferences = sharedPref.getString(FAVORITE_LIST, "");

        Type type = new TypeToken<List<String>>() {
        }.getType();
        favoriteList = gson.fromJson(jsonPreferences, type);

        return favoriteList;
    }

    /*
*
* To save data for the first time
*
*
* */
    public static boolean isFirstTime(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(FIRST_TIME, true);
    }

    public static void setFirstTime(Context context, boolean gender) {
        SharedPreferences _sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = _sharedPreferences.edit();
        editor.putBoolean(FIRST_TIME, gender);
        editor.commit();
    }
}
