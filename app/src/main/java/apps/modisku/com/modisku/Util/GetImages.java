package apps.modisku.com.modisku.Util;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Danz on 9/8/2015.
 */
public class GetImages extends AsyncTask<Object, Object, Object> {
    private String requestUrl, imagename_;
    private ImageView view;
    private Bitmap bitmap ;
    private FileOutputStream fos;
    ProgressDialog pd;
    public GetImages(String requestUrl, ImageView view, String _imagename_) {
        this.requestUrl = requestUrl;
        this.view = view;
        this.imagename_ = _imagename_ ;

    }

    public GetImages(Context context,String requestUrl, ImageView view, String _imagename_) {
        this.requestUrl = requestUrl;
        this.view = view;
        this.imagename_ = _imagename_ ;
    }
    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();

    }

    @Override
    protected Object doInBackground(Object... objects) {
        try {
            URL url = new URL(requestUrl);
            URLConnection conn = url.openConnection();
            bitmap = BitmapFactory.decodeStream(conn.getInputStream());
            Log.d("TEST", "Name " + "Inside GetImages");
        } catch (Exception ex) {
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        if(!ImageStorage.checkifImageExists(imagename_))
        {
            view.setImageBitmap(bitmap);
            ImageStorage.saveToSdCard(bitmap, imagename_);
        }
    }
}