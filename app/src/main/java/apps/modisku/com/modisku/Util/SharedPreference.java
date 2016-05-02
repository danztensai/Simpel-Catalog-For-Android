package apps.modisku.com.modisku.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import apps.modisku.com.modisku.Model.Product;

/**
 * Created by Danz on 9/8/2015.
 */
public class SharedPreference {

    public static final String PREFS_NAME = "PRODUCT_APP";
    public static final String FAVORITES = "Product_Favorite";

    public SharedPreference() {
        super();
    }

    // This four methods are used for maintaining favorites.
    public void saveFavorites(Context context, List<Product> favorites) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        editor = settings.edit();

        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(favorites);

        editor.putString(FAVORITES, jsonFavorites);

        editor.commit();
    }
    public void remove(Context context) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        editor = settings.edit();
        editor.remove(FAVORITES);

        editor.commit();
    }
    public void addFavorite(Context context, Product product) {
        List<Product> favorites = getFavorites(context);
        if (favorites == null)
            favorites = new ArrayList<Product>();
        favorites.add(product);
        saveFavorites(context, favorites);
    }

    public void removeFavorite(Context context, Product product) {
        ArrayList<Product> favorites = getFavorites(context);
        Log.d("TEST SHAREPREF", "Size Fav RemoveFav" + favorites.size());
        Log.d("TEST SHAREPREF","Size Fav RemoveFav"+favorites.toString());
        if (favorites != null) {
            favorites.remove(product);
            Log.d("TEST SHAREPREF", "Inside If, Size Fav RemoveFav" + favorites.size());
            saveFavorites(context, favorites);
        }
    }



    public ArrayList<Product> getFavorites(Context context) {
        SharedPreferences settings;
        List<Product> favorites;

        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);

        if (settings.contains(FAVORITES)) {
            String jsonFavorites = settings.getString(FAVORITES, null);
            Gson gson = new Gson();
            Product[] favoriteItems = gson.fromJson(jsonFavorites,
                    Product[].class);

            favorites = Arrays.asList(favoriteItems);
            favorites = new ArrayList<Product>(favorites);
        } else
            return null;

        return (ArrayList<Product>) favorites;
    }
}
