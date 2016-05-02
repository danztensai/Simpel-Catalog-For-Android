package apps.modisku.com.modisku.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.utils.L;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import apps.modisku.com.modisku.Model.Category;
import apps.modisku.com.modisku.R;
import apps.modisku.com.modisku.Util.Constant;
import apps.modisku.com.modisku.Util.DateUtils;
import apps.modisku.com.modisku.Util.UserEmailFetcher;
import apps.modisku.com.modisku.app.AppConfig;
import apps.modisku.com.modisku.app.ModiskuApps;
import apps.modisku.com.modisku.fragment.HomeFragment;
import apps.modisku.com.modisku.fragment.ProductDetailFragment;

public class CategoryActivity extends AppCompatActivity {

    
    private static final String TEST_FILE_NAME = "Universal Image Loader @#&=+-_.,!()~'%20.png";
    private static String TAG = CategoryActivity.class.getSimpleName();
    public static final int INDEX = 1;
    ImageAdapter adapter;
    GridView grid;
    public static ProgressBar spinner;
    private String urlCategory = AppConfig.HOME_URL+"ci/index.php/main/getJsonCategory?offset=";
    private List<Category> categoryList;

    private Fragment contentFragment;
    HomeFragment homeFragment;
    ProductDetailFragment pdtDetailFragment;

    private void copyTestImageToSdCard(
            final File testImageOnSdCard) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream is = getAssets().open(TEST_FILE_NAME);
                    FileOutputStream fos = new FileOutputStream(testImageOnSdCard);
                    byte[] buffer = new byte[8192];
                    int read;
                    try {
                        while ((read = is.read(buffer)) != -1) {
                            fos.write(buffer, 0, read);
                        }
                    } finally {
                        fos.flush();
                        fos.close();
                        is.close();
                    }
                } catch (IOException e) {
                    L.w("Can't copy test image onto SD card");
                }
            }
        }).start();
    }
    public void switchContent(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        while (fragmentManager.popBackStackImmediate())
            ;

        if (fragment != null) {
            FragmentTransaction transaction = fragmentManager
                    .beginTransaction();
            transaction.replace(R.id.promofragment, fragment, tag);
            // Only ProductDetailFragment is added to the back stack.
            if (!(fragment instanceof HomeFragment)) {
                transaction.addToBackStack(tag);
            }
            transaction.commit();
            contentFragment = fragment;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);


        FragmentManager fragmentManager = getSupportFragmentManager();

//
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("content")) {
                String content = savedInstanceState.getString("content");
                if (content.equals(ProductDetailFragment.ARG_ITEM_ID)) {
                    if (fragmentManager
                            .findFragmentByTag(ProductDetailFragment.ARG_ITEM_ID) != null) {
                        contentFragment = fragmentManager
                                .findFragmentByTag(ProductDetailFragment.ARG_ITEM_ID);
                    }
                }
            }
            if (fragmentManager.findFragmentByTag(HomeFragment.ARG_ITEM_ID) != null) {
                homeFragment = (HomeFragment) fragmentManager
                        .findFragmentByTag(HomeFragment.ARG_ITEM_ID);
                contentFragment = homeFragment;
            }
        } else {
            homeFragment = new HomeFragment();
            switchContent(homeFragment, HomeFragment.ARG_ITEM_ID);
        }
//

        spinner = (ProgressBar)findViewById(R.id.progressBarCategoryLoading);
        categoryList = new ArrayList<>();


        File testImageOnSdCard = new File("/mnt/sdcard", TEST_FILE_NAME);
        fetchCatalog();
        if (!testImageOnSdCard.exists()) {
            copyTestImageToSdCard(testImageOnSdCard);
        }

        grid = (GridView) findViewById(R.id.grid);
        grid.setBackgroundColor(Color.WHITE);
        grid.setVerticalSpacing(5);
        grid.setHorizontalSpacing(5);
        adapter = new ImageAdapter(this,categoryList);
        grid.setAdapter(adapter);

        String userEmail = UserEmailFetcher.getEmail(getApplicationContext());


        //       Toast.makeText(getApplicationContext(), "User Email"+userEmail,Toast.LENGTH_SHORT).show();

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ImageView imageView = (ImageView) view.findViewById(R.id.image);
                String tagView = (String) imageView.getTag();
             //   Toast.makeText(getApplicationContext(), tagView, Toast.LENGTH_SHORT).show();


                Intent categoryListIntent = new Intent(getApplicationContext(), BrandActivity.class);
                categoryListIntent.putExtra("category_id", tagView);

                startActivity(categoryListIntent);
            }
        });




    }


    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            super.onBackPressed();
        } else if (contentFragment instanceof HomeFragment
                || fm.getBackStackEntryCount() == 0) {
            finish();
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (contentFragment instanceof HomeFragment) {
            outState.putString("content", HomeFragment.ARG_ITEM_ID);
        } else {
            outState.putString("content", ProductDetailFragment.ARG_ITEM_ID);
        }
        super.onSaveInstanceState(outState);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_category, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (item.getItemId())
        {
            case R.id.action_Refresh:
                // Single menu item is selected do something
                // Ex: launching new activity/screen or show alert message
                Toast.makeText(CategoryActivity.this, "Trying To Refresh", Toast.LENGTH_SHORT).show();
                adapter.clearData();
                adapter.notifyDataSetChanged();
              fetchCatalog();
                return true;
            case R.id.menu_shopping_cart:
                Intent shoppingCartIntent = new Intent(getApplicationContext(), ShoppingCart.class);
                startActivity(shoppingCartIntent);
                return true;

            case R.id.menu_request_item:
                Intent requestItem = new Intent(getApplicationContext(), OrderByRequest.class);
                startActivity(requestItem);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }




    private static class ImageAdapter extends BaseAdapter {



        private LayoutInflater inflater;
        private List<Category>categoryList;

        private DisplayImageOptions options;

        ImageAdapter(Context context,List<Category> cl) {
            inflater = LayoutInflater.from(context);

            options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.ic_stub)
                    .showImageForEmptyUri(R.drawable.ic_empty)
                    .showImageOnFail(R.drawable.ic_error)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .build();
            this.categoryList = cl;
        }

        @Override
        public int getCount() {
            return categoryList.size();
        }

        @Override
        public Object getItem(int position) {
            return categoryList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            View view = convertView;
            if (view == null) {
                view = inflater.inflate(R.layout.item_grid_image, parent, false);
                holder = new ViewHolder();
                assert view != null;
                holder.imageView = (ImageView) view.findViewById(R.id.image);
                holder.progressBar = (ProgressBar) view.findViewById(R.id.progress);
                holder.productTitle = (TextView)view.findViewById(R.id.productTitle);
                holder.iconNew = (ImageView)view.findViewById(R.id.iconNew);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            holder.imageView.setTag(String.valueOf(categoryList.get(position).id));
            holder.productTitle.setText(categoryList.get(position).name);

            Calendar now = Calendar.getInstance();
            now.setTime(new Date());
            Calendar lastUpdate = Calendar.getInstance();
            lastUpdate.setTime(categoryList.get(position).getLastUpdate());
            long intervalDay = DateUtils.getElapseday(lastUpdate,now);
            Log.d(TAG,"Interval Daynya "+intervalDay);
            Log.d(TAG, "Expired Day " + categoryList.get(position).getExpiredDay());

            if(intervalDay>categoryList.get(position).getExpiredDay())
            {
                holder.iconNew.setVisibility(View.INVISIBLE);
            }else
            {
                holder.iconNew.setVisibility(View.VISIBLE);
            }

            ImageLoader.getInstance()
                    .displayImage(categoryList.get(position).imageURL, holder.imageView, options, new SimpleImageLoadingListener() {
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

            return view;
        }

        public void clearData() {
            // clear the data
            categoryList.clear();
        }
    }

    static class ViewHolder {
        ImageView imageView;
        ProgressBar progressBar;
        TextView productTitle;
        ImageView iconNew ;
    }
    private void fetchCatalog()
    {
        //  swipeRefreshLayout.setRefreshing(true);
        if(spinner.getVisibility()==View.VISIBLE)
        {

        }else
        {
            spinner.setVisibility(View.VISIBLE);
        }
        String url = urlCategory;
        JsonArrayRequest req = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());

                        if (response.length() > 0) {

                            // looping through json and adding to movies list
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject categoryObject = response.getJSONObject(i);

                                    int id = categoryObject.getInt("id");
                                    String name = categoryObject.getString("name");
                                    String urlImage = categoryObject.getString("imageURL");
                                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:s");
                                    try {
                                        Date date = formatter.parse(categoryObject.getString("lastUpdate"));
                                        Category m = new Category(id,name,urlImage,date,categoryObject.getInt("notifDay"));
                                        categoryList.add(0, m);

                                    } catch (ParseException e) {
                                        Log.d(TAG,e.getMessage());
                                    }



                                    // updating offset value to highest value

                                } catch (JSONException e) {
                                    Log.e(TAG, "JSON Parsing error: " + e.getMessage());
                                }
                            }
                            spinner.setVisibility(View.GONE);
                            adapter.notifyDataSetChanged();

                        }

                        // stopping swipe refresh
                        //        swipeRefreshLayout.setRefreshing(false);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Server Error: " + error.getMessage());
                //  fetchCatalog();
                Toast.makeText(getApplicationContext(), "Connection Problem Please Refresh List", Toast.LENGTH_SHORT).show();

                // stopping swipe refresh
                //swipeRefreshLayout.setRefreshing(false);
            }
        });

        // Adding request to request queue
        Log.e(TAG, req.toString());
        ModiskuApps.getInstance().addToRequestQueue(req);
    }
}
