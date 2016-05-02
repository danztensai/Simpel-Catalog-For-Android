package apps.modisku.com.modisku.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import apps.modisku.com.modisku.Model.Product;
import apps.modisku.com.modisku.R;
import apps.modisku.com.modisku.Util.DateUtils;

/**
 * Created by Danz on 9/8/2015.
 */
public class ProductListImageAdapter extends BaseAdapter{
    private Activity activity;
    LayoutInflater inflater;
    Context mContext;
    private List<Product> productList;
    private ArrayList<Product> arraylist;
    private String TAG = ProductListImageAdapter.class.getSimpleName();
    private Bitmap b;
    private DisplayImageOptions options;

    //private String[] bgColors;

    public ProductListImageAdapter(Context context, List<Product> productList) {
        //this.activity = activity;
        mContext = context;
        this.productList = productList;
        inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<Product>();
        this.arraylist.addAll(productList);
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_stub)
                .showImageForEmptyUri(R.drawable.ic_empty)
                .showImageOnFail(R.drawable.ic_error)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        //  bgColors = activity.getApplicationContext().getResources().getStringArray(R.array.movie_serial_bg);
    }

    public class ViewHolder{
        ImageView productImage;
        ProgressBar progressBar;
        TextView productTitle;
        ImageView iconNew;
    }

    @Override
    public int getCount() {
        return productList.size();
    }

    @Override
    public Object getItem(int location) {
        return productList.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if(convertView==null)
        {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.list_image_product_,null);
            holder.productImage = (ImageView) convertView.findViewById(R.id.productImage);
            holder.progressBar = (ProgressBar) convertView.findViewById(R.id.progress);
            holder.productTitle =(TextView)convertView.findViewById(R.id.productTitleImage);
            holder.iconNew = (ImageView)convertView.findViewById(R.id.iconNewProduct);
            convertView.setTag(holder);
        }else
        {
            holder
                    = (ViewHolder) convertView.getTag();
        }

        String imageName = productList.get(position).getTitle();
        String imageUrl = productList.get(position).getImageURL();
        String url = imageUrl;
        String fileExtenstion = MimeTypeMap.getFileExtensionFromUrl(url);
        String name = URLUtil.guessFileName(url, null, fileExtenstion);
        Log.d("TEST", "Image Name " + imageName);
        Log.d("TEST","Name "+name);
        Log.d("TEST", "fileExtenstion Name " + fileExtenstion);
        holder.productImage.setTag(String.valueOf(productList.get(position).getId()));
        holder.productTitle.setText(productList.get(position).getTitle());
        Calendar now = Calendar.getInstance();
        now.setTime(new Date());
        Calendar lastUpdate = Calendar.getInstance();
        lastUpdate.setTime(productList.get(position).getLastUpdate());
        long intervalDay = DateUtils.getElapseday(lastUpdate, now);
        Log.d(TAG, "Interval Daynya " + intervalDay);
        Log.d(TAG, "Expired Day " + productList.get(position).getExpiredDay());
        int notifDay = productList.get(position).getExpiredDay();
        if(intervalDay>notifDay)
        {
            holder.iconNew.setVisibility(View.GONE);
        }
        ImageLoader.getInstance()
                .displayImage(productList.get(position).imageURL, holder.productImage, options, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        holder.progressBar.setProgress(0);
                        holder.progressBar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        holder.progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        holder.progressBar.setVisibility(View.GONE);
                    }
                }, new ImageLoadingProgressListener() {
                    @Override
                    public void onProgressUpdate(String imageUri, View view, int current, int total) {
                        holder.progressBar.setProgress(Math.round(100.0f * current / total));
                    }
                });
 /*
        if(ImageStorage.checkifImageExists(name))
        {
            Log.d("TEST",imageName);
            File file = ImageStorage.getImage(name);
            String path = file.getAbsolutePath();


            if(path != null)
            {
                Log.d("TEST","PATH "+path);
                b = BitmapFactory.decodeFile(path);
                holder.productImage.setImageBitmap(b);
            }
        }else
        {
            new GetImages(imageUrl, holder.productImage,name).execute();
        }
*/
        return convertView;
    }

    public void add(Product p) {
        productList.add(0, p);
        arraylist.add(0, p);
        notifyDataSetChanged();
    }
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        Log.d(TAG,"START");
        Log.d(TAG,"Size ProductList "+productList.size());
        Log.d(TAG,"Size ArrayList "+arraylist.size());
        Log.d(TAG, charText);

        productList.clear();
        if (charText.length() == 0) {
            productList.addAll(arraylist);
        } else {
            for (Product wp : arraylist) {
                if (wp.getTitle().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    Log.d(TAG,"Title : "+wp.getTitle());
                    Log.d(TAG,"ImageLink : "+wp.getImageURL());

                    productList.add(wp);
                }
            }
        }
        Log.d(TAG, "END");
        notifyDataSetChanged();
    }
    public void clearData() {
        // clear the data
        productList.clear();
    }

}
