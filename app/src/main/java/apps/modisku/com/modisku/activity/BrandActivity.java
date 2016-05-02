package apps.modisku.com.modisku.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
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
import java.util.Date;
import java.util.List;
import java.util.Locale;

import apps.modisku.com.modisku.Model.Category;
import apps.modisku.com.modisku.Model.Product;
import apps.modisku.com.modisku.R;
import apps.modisku.com.modisku.adapter.BrandListImageAdapter;
import apps.modisku.com.modisku.app.AppConfig;
import apps.modisku.com.modisku.app.ModiskuApps;

public class BrandActivity extends AppCompatActivity {

    private static final String TEST_FILE_NAME = "Universal Image Loader @#&=+-_.,!()~'%20.png";
    public static ProgressBar spinner;
    EditText editsearch;
    Button btnGoToShoppingCart;
    private String TAG = BrandActivity.class.getSimpleName();
    private String URLJSON = AppConfig.HOME_URL+"ci/index.php/main/getJsonBrandByCategoryid?category_id=";
    //private ListView listView;
    private GridView gridView;
    private BrandListImageAdapter adapter;
    private List<Product> productList;
    // initially offset will be 0, later will be updated while parsing the json
    private int offSet = 0;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brand);
        btnGoToShoppingCart = (Button)findViewById(R.id.btnGoToShoppingCart);
        File testImageOnSdCard = new File("/mnt/sdcard", TEST_FILE_NAME);
        gridView = (GridView)findViewById(R.id.gridViewListProduct);

        gridView.setVerticalSpacing(5);
        gridView.setHorizontalSpacing(5);
        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        productList = new ArrayList<>();

        final String category_id = getIntent().getExtras().getString("category_id");
        Log.d(TAG, "Category ID " + category_id);
        URLJSON = URLJSON + category_id;

        fetchProduct();
        if (!testImageOnSdCard.exists()) {
            copyTestImageToSdCard(testImageOnSdCard);
        }
        adapter = new BrandListImageAdapter(this, productList);
        gridView.setAdapter(adapter);

        editsearch = (EditText)findViewById(R.id.search);

        editsearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                String text = editsearch.getText().toString().toLowerCase(Locale.getDefault());
                Log.d(TAG,"Text To Search : "+text);
                adapter.filter(text);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                // TODO Auto-generated method stub
            }
        });


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageView imageView = (ImageView) view.findViewById(R.id.productImage);
                String tagView = (String) imageView.getTag();
                //Toast.makeText(getApplicationContext(), "Brand ID :" + tagView + "| CategoryID : " + category_id, Toast.LENGTH_SHORT).show();
                Intent productListIntent = new Intent(getApplicationContext(), ProductList.class);
                productListIntent.putExtra("category_id", category_id);
                productListIntent.putExtra("brand_id", tagView);

                startActivity(productListIntent);
            }
        });

        btnGoToShoppingCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shoppingCartIntent = new Intent(getApplicationContext(), ShoppingCart.class);
                startActivity(shoppingCartIntent);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_brand, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_menu_refreshBrand) {
            adapter.clearData();
            adapter.notifyDataSetChanged();

            fetchProduct();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void fetchProduct() {

        // showing refresh animation before making http call
        //  swipeRefreshLayout.setRefreshing(true);

        if(spinner.getVisibility()==View.VISIBLE)
        {

        }else
        {
            spinner.setVisibility(View.VISIBLE);
        }
        // appending offset to url
        String url = URLJSON;

        // Volley's json array request object
        JsonArrayRequest req = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());

                        if (response.length() > 0) {

                            // looping through json and adding to movies list
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject brandObj = response.getJSONObject(i);

                                    int rank = brandObj.getInt("idBrand");
                                    String title = brandObj.getString("brand_name");
                                    String imageURL = brandObj.getString("imageURL");
                                    int notifDay = brandObj.getInt("notifDay");



                                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:s");
                                    try {
                                        Date date = formatter.parse(brandObj.getString("lastUpdate"));
                                        Product m = new Product(rank,title,imageURL,date,notifDay);
                                    //    categoryList.add(0, m);
                                        adapter.add(m);
                                    } catch (ParseException e) {
                                        Log.d(TAG,e.getMessage());
                                    }


                                    // productList.add(0, m);


                                    // updating offset value to highest value
                                    if (rank >= offSet)
                                        offSet = rank;

                                } catch (JSONException e) {
                                    Log.e(TAG, "JSON Parsing error: " + e.getMessage());
                                }
                            }

                            //   adapter.notifyDataSetChanged();
                        }

                        // stopping swipe refresh
                        // swipeRefreshLayout.setRefreshing(false);
                        spinner.setVisibility(View.GONE);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Server Error: " + error.getMessage());
                //fetchProduct();
                Toast.makeText(getApplicationContext(), "Connection Problem Please Refresh List", Toast.LENGTH_LONG).show();

                // stopping swipe refresh
                //    swipeRefreshLayout.setRefreshing(false);
                spinner.setVisibility(View.GONE);
            }
        });

        // Adding request to request queue
        Log.e(TAG, req.toString());
        ModiskuApps.getInstance().addToRequestQueue(req);
    }
}
