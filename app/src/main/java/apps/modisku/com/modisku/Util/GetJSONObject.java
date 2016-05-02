package apps.modisku.com.modisku.Util;

import android.os.Build;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Danz on 9/8/2015.
 */
public class GetJSONObject {

    public static JSONObject getJSONObject(String url) throws IOException,
            JSONException {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = null;
        // Use HttpURLConnection
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO) {
            jsonObject = jsonParser.getJSONHttpURLConnection(url);
        } else {
            // use HttpClient
            jsonObject = jsonParser.getJSONHttpClient(url);
        }

        Log.i("ImageSlider", jsonObject.toString());
        return jsonObject;
    }
}