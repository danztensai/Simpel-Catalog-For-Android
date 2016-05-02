package apps.modisku.com.modisku.activity;

import android.content.Intent;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import apps.modisku.com.modisku.Model.Product;
import apps.modisku.com.modisku.R;
import apps.modisku.com.modisku.Util.SQLiteHandler;
import apps.modisku.com.modisku.adapter.ProductListImageAdapter;
import apps.modisku.com.modisku.app.AppConfig;
import apps.modisku.com.modisku.app.ModiskuApps;

public class ProductList extends AppCompatActivity {

    private String TAG = ProductList.class.getSimpleName();

    private String URLJSON = AppConfig.HOME_URL+"ci/index.php/main/getProductByBrandCategory";

    private GridView gridView;
    private ProductListImageAdapter adapter;
    private List<Product> productList;
    private ProgressBar spinner;
    EditText editsearch;
    Button btnGoToShoppingCart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);


        gridView = (GridView)findViewById(R.id.gridViewListProduct);
        btnGoToShoppingCart = (Button)findViewById(R.id.btnGoToShoppingCart);
        //  listView = (ListView) findViewById(R.id.listView);
        //swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        //Log.d(TAG,"Product List : "+productList.size());
        productList = new ArrayList<>();

        final String category_id = getIntent().getExtras().getString("category_id");
        final String brand_id = getIntent().getExtras().getString("brand_id");
        URLJSON =URLJSON+"?category_id="+category_id+"&brand_id="+brand_id;

        Log.d(TAG, "Category ID " + category_id);
        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        fetchProduct();
        adapter = new ProductListImageAdapter(this, productList);
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
              //  Toast.makeText(getApplicationContext(), "Brand ID :" + tagView + "| CategoryID : " + category_id, Toast.LENGTH_SHORT).show();
                Intent productDetailIntent = new Intent(getApplicationContext(), ProductDetail.class);
                productDetailIntent.putExtra("product_id", tagView);


                startActivity(productDetailIntent);
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
        getMenuInflater().inflate(R.menu.menu_product_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
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

        // appending offset to url

        if(spinner.getVisibility()==View.VISIBLE)
        {

        }else
        {
            spinner.setVisibility(View.VISIBLE);
        }

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
                                    JSONObject objProductList = response.getJSONObject(i);

                                    int rank = objProductList.getInt("rank");
                                    String title = objProductList.getString("title");
                                    String imageURL = objProductList.getString("imageURL");
                                    int notifDay = objProductList.getInt("notifDay");
                                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:s");
                                    try {
                                        Date date = formatter.parse(objProductList.getString("lastUpdate"));
                                        Product m = new Product(rank,title,imageURL,date,notifDay);
                                        //    categoryList.add(0, m);
                                        adapter.add(m);
                                    } catch (ParseException e) {
                                        Log.d(TAG,e.getMessage());
                                    }


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
