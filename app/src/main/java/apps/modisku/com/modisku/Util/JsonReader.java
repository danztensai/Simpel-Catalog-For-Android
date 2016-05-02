package apps.modisku.com.modisku.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import apps.modisku.com.modisku.Model.Product;
import apps.modisku.com.modisku.helper.TagName;

/**
 * Created by Danz on 9/8/2015.
 */

public class JsonReader {

    public static List<Product> getHome(JSONObject jsonObject)
            throws JSONException {
        List<Product> products = new ArrayList<Product>();

        JSONArray jsonArray = jsonObject.getJSONArray(TagName.TAG_PRODUCTS);
        Product product;
        for (int i = 0; i < jsonArray.length(); i++) {
            product = new Product();
            JSONObject productObj = jsonArray.getJSONObject(i);
            product.setId(productObj.getInt(TagName.KEY_ID));
            product.setTitle(productObj.getString(TagName.KEY_NAME));
            product.setImageURL(productObj.getString(TagName.KEY_IMAGE_URL));
            product.setDescription(productObj.getString("description"));

            products.add(product);
        }
        return products;
    }
}