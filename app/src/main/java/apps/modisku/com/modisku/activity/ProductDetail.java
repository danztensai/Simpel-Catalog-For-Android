package apps.modisku.com.modisku.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import apps.modisku.com.modisku.Model.Product;
import apps.modisku.com.modisku.R;
import apps.modisku.com.modisku.Util.GetImages;
import apps.modisku.com.modisku.Util.ImageStorage;
import apps.modisku.com.modisku.Util.SQLiteHandler;
import apps.modisku.com.modisku.Util.SharedPreference;
import apps.modisku.com.modisku.app.AppConfig;
import apps.modisku.com.modisku.app.ModiskuApps;

public class ProductDetail extends AppCompatActivity {

    private String TAG = ProductDetail.class.getSimpleName();
    TextView title, name, description, resultText,priceText;
    ImageView imageProduct;
    Button btnOrder,buttonShopCart;
    final Context context = this;
    SharedPreference sharedPreference;
    private String urlSendEmail = "";
    Product productDetails ;
    ProgressBar progressBar;
    Bitmap b;
    private DisplayImageOptions options;
    SQLiteHandler sql;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        sql = new SQLiteHandler(getBaseContext());
        title = (TextView) findViewById(R.id.textViewTitleProduk);
        description = (TextView) findViewById(R.id.productDescription);
        btnOrder = (Button) findViewById(R.id.orderBtn);
        buttonShopCart = (Button) findViewById(R.id.buttonShopCart);
        progressBar = (ProgressBar)findViewById(R.id.progressBarImageProduct);
        priceText =(TextView)findViewById(R.id.textViewPrice);
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_stub)
                .showImageForEmptyUri(R.drawable.ic_empty)
                .showImageOnFail(R.drawable.ic_error)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        final String product_id = getIntent().getExtras().getString("product_id");
        productDetails =new Product();
        sharedPreference = new SharedPreference();
        //  imageProduct =  (ImageView)findViewById(R.id.imageView);
        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialog();

            }
        });
        buttonShopCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shoppingCartIntent = new Intent(getApplicationContext(), ShoppingCart.class);
                startActivity(shoppingCartIntent);
            }
        });

        HashMap<String,String> user = sql.getUserDetails();

        String url = AppConfig.HOME_URL+"ci/index.php/main/getJsonProductByProductid?product_id=" + product_id +"&email="+user.get("email").toString();

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
                                    JSONObject categoryObject = response.getJSONObject(i);

                                    int id = categoryObject.getInt("rank");
                                    String titleText = categoryObject.getString("title");
                                    String descriptionText = categoryObject.getString("description");
                                    String urlImage = categoryObject.getString("imageURL");
                                    double price = categoryObject.getDouble("price");
                                    productDetails.setId(id);
                                    productDetails.setTitle(titleText);
                                    productDetails.setDescription(descriptionText);
                                    productDetails.setImageURL(urlImage);
                                    productDetails.setPrice(price);

                                    imageProduct = (ImageView) findViewById(R.id.imageView);
                                    title.setText(titleText);
                                    description.setText(descriptionText);

                                    Locale swedish = new Locale("ID", "ID");
                                    NumberFormat swedishFormat = NumberFormat.getCurrencyInstance(swedish);
                                    System.out.println("Swedish: " + swedishFormat.format(price));
                                    priceText.setText(swedishFormat.format(price));
                                    String fileExtenstion = MimeTypeMap.getFileExtensionFromUrl(urlImage);
                                    String name = URLUtil.guessFileName(urlImage, null, fileExtenstion);
                                    Log.d("TEST", "Name " + name);
                                    Log.d("TEST", "fileExtenstion Name " + fileExtenstion);
                                    ImageLoader.getInstance()
                                            .displayImage(urlImage, imageProduct, options, new SimpleImageLoadingListener() {
                                                @Override
                                                public void onLoadingStarted(String imageUri, View view) {
                                                    progressBar.setProgress(0);
                                                    progressBar.setVisibility(View.VISIBLE);
                                                }

                                                @Override
                                                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                                                  progressBar.setVisibility(View.GONE);
                                                }

                                                @Override
                                                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                                   progressBar.setVisibility(View.GONE);
                                                }
                                            }, new ImageLoadingProgressListener() {
                                                @Override
                                                public void onProgressUpdate(String imageUri, View view, int current, int total) {
                                                    progressBar.setProgress(Math.round(100.0f * current / total));
                                                }
                                            });
                                  /*  if (ImageStorage.checkifImageExists(name)) {
                                        Log.d("TEST", name);
                                        File file = ImageStorage.getImage(name);
                                        String path = file.getAbsolutePath();


                                        if (path != null) {
                                            Log.d("TEST", "PATH " + path);
                                            b = BitmapFactory.decodeFile(path);
                                            imageProduct.setImageBitmap(b);
                                        }
                                    } else {
                                        new GetImages(urlImage, imageProduct, name).execute();
                                    }
                                */

                                } catch (JSONException e) {
                                    Log.e(TAG, "JSON Parsing error: " + e.getMessage());
                                }
                            }

                            // adapter.notifyDataSetChanged();
                        }

                        // stopping swipe refresh
                        //        swipeRefreshLayout.setRefreshing(false);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Server Error: " + error.getMessage());

                Toast.makeText(getApplicationContext(), "Connection Problem Please Refresh List", Toast.LENGTH_LONG).show();

                // stopping swipe refresh
                //swipeRefreshLayout.setRefreshing(false);
            }
        });

        // Adding request to request queue
        Log.e(TAG, req.toString());
        ModiskuApps.getInstance().addToRequestQueue(req);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_product_detail, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    protected void showInputDialog() {

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(ProductDetail.this);
        View promptView = layoutInflater.inflate(R.layout.quantity_input_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProductDetail.this);
        alertDialogBuilder.setView(promptView);

        final EditText editTextQuantity = (EditText) promptView.findViewById(R.id.quantityText);



        List<Product> p = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(p);


        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String q = editTextQuantity.getText().toString();
                        if(!q.isEmpty()||!q.equalsIgnoreCase("")) {


                        productDetails.setQuantity(Integer.parseInt(q));
                        sharedPreference.addFavorite(getApplicationContext(),productDetails);

                        Log.d("TEST",sharedPreference.getFavorites(getApplicationContext()).toString());

                      /*//
                        Log.d("TEST", "Quantity :" + q);
                        urlSendEmail = "http://122.129.112.169/portal/danztensai/email.php?name=" + name + "&email=" + email;
                        Log.d(TAG, urlSendEmail);
                        new FetchTask().execute();
                      */
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                        builder1.setMessage("Jumlah Sudah Di Input\nSilahkan Pilih Kembali atau\nCheckout Untuk Selesai");
                        builder1.setCancelable(true);
                        builder1.setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        Intent i = new Intent(getApplicationContext(),
                                                CategoryActivity.class);
                                        startActivity(i);
                                        finish();
                                    }
                                });
                        AlertDialog alert11 = builder1.create();
                        alert11.show();
                      }else
                        {
                            Toast.makeText(getBaseContext(), "Please Input Your Quantity", Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
        final Button positiveButton = alert.getButton(AlertDialog.BUTTON_POSITIVE);
        LinearLayout.LayoutParams positiveButtonLL = (LinearLayout.LayoutParams) positiveButton.getLayoutParams();
        positiveButtonLL.gravity = Gravity.CENTER;
        positiveButton.setLayoutParams(positiveButtonLL);

    }

}
