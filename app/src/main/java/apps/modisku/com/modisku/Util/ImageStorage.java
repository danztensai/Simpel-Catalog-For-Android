package apps.modisku.com.modisku.Util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Danz on 9/8/2015.
 */
public class ImageStorage {


    public static String saveToSdCard(Bitmap bitmap, String filename) {

        String stored = null;

        File sdcard = Environment.getExternalStorageDirectory() ;

        File folder = new File(sdcard.getAbsoluteFile(), ".Catalog");//the dot makes this directory hidden to the user
        folder.mkdir();
        File file = new File(folder.getAbsoluteFile(), filename) ;
        if (file.exists())
            return stored ;
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
            stored = "success";
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("ImageStorage TOSD CARD", e.getMessage());
        }
        return stored;
    }

    public static File getImage(String imagename) {

        File mediaImage = null;
        try {
            Log.d("TEST GETIMAGE","ImageName : "+imagename);
            String root = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(root);
            if (!myDir.exists())
                return null;

            mediaImage = new File(myDir.getPath() + "/.Catalog/"+imagename);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.d("ImageStorage getImage", e.getMessage());
        }
        return mediaImage;
    }
    public static boolean checkifImageExists(String imagename)
    {
        Bitmap b = null ;
        File file = ImageStorage.getImage("/"+imagename);
        String path = file.getAbsolutePath();

        Log.d("TEST","PATH CHECK IMAGE : "+path);
        if (path != null) {
            Log.d("TEST", "ADA " + imagename);
            b = BitmapFactory.decodeFile(path);
        }
        if(b == null ||  b.equals(""))
        {
            Log.d("TEST","GA ADA " +imagename);
            return false ;
        }
        return true ;
    }
}