package apps.modisku.com.modisku.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import apps.modisku.com.modisku.Model.Product;
import apps.modisku.com.modisku.R;
import apps.modisku.com.modisku.Util.SQLiteHandler;
import apps.modisku.com.modisku.Util.SharedPreference;
import apps.modisku.com.modisku.adapter.ShoppingCartAdapter;
import apps.modisku.com.modisku.app.AppConfig;

public class ShoppingCart extends AppCompatActivity {

    private ListView listView;
    private ShoppingCartAdapter adapter;
    private List<Product> productList;
    SharedPreference sharedPreference;
    SQLiteHandler sql;
    Button btnRemove,btnCheckout;
    private ProgressDialog pDialog;
    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        sharedPreference = new SharedPreference();
        sql = new SQLiteHandler(getBaseContext());
        productList=sharedPreference.getFavorites(getApplicationContext());
        if(productList != null) {
            for (int i = 0; i < productList.size(); i++) {
                productList.get(i).selected = false;
            }
        }else
        {
            productList = new ArrayList<>();
        }
        listView = (ListView)findViewById(R.id.ListViewCatalog);
        Log.d("TESTShoppingCart", productList.toString());
        btnRemove = (Button)findViewById(R.id.btnDeleteCart);
        btnCheckout = (Button)findViewById(R.id.buttonCheckOut);

        adapter = new ShoppingCartAdapter(getApplicationContext(),productList,true);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Product selectedProduct = productList.get(position);
                if (selectedProduct.selected == true) {
                    selectedProduct.selected = false;
                } else {
                    selectedProduct.selected = true;
                }
                adapter.notifyDataSetInvalidated();
            }
        });

        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // Loop through and remove all the products that are selected
                // Loop backwards so that the remove works correctly
                for(int i=productList.size()-1; i>=0; i--) {

                    Log.d("TEST SIZE SharedPref ", " Index i "+i+" Sizenya : "+sharedPreference.getFavorites(getApplicationContext()).size());

                    if(productList.get(i).selected) {
                        sharedPreference.removeFavorite(getApplicationContext(),productList.get(i));
                        productList.remove(i);



                    }
                }

                adapter.notifyDataSetChanged();

            }
        });

        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showInputDialog();

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_shopping_cart, menu);
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


    public static String POST(String url,String jsonCart){
        InputStream inputStream = null;
        String result = "";
        try {

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);

            String json = jsonCart;



            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);

            // 6. set httpPost Entity
            httpPost.setEntity(se);

            // 7. Set some headers to inform server about the type of the content
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            // 8. Execute POST request to the given URL
            HttpResponse
                    httpResponse = httpclient.execute(httpPost);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        // 11. return result
        return result;
    }

    protected void showInputDialog() {

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(ShoppingCart.this);
        View promptView = layoutInflater.inflate(R.layout.checkout_shoppingcart, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ShoppingCart.this);
        alertDialogBuilder.setView(promptView);

        //  final EditText emailText = (EditText) promptView.findViewById(R.id.txtEmail);
        //final EditText nameText = (EditText) promptView.findViewById(R.id.txtName);




        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  String e = emailText.getText().toString();
                        //String n = nameText.getText().toString();
                        HashMap<String,String> user = sql.getUserDetails();
                        Log.d("SQLUSERDETAIL ",user.toString());
                        new HttpAsyncTask().execute(AppConfig.HOME_URL+"services/checkout.php?name=" + user.get("name").toString() + "&email=" + user.get("email").toString());

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
    }


    private class HttpAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            pDialog = new ProgressDialog(ShoppingCart.this);
            pDialog.setMessage("Sending Your Order...");
            showDialog();
        }

        @Override
        protected String doInBackground(String... urls) {


            List<Product> productListFromPref = sharedPreference.getFavorites(getApplicationContext());
            Gson gson = new Gson();
            String jsonCart = gson.toJson(productListFromPref);
            String result =POST(urls[0],jsonCart);
            Log.e("TEST RESULTCHECKOUT ",urls[0]+"  | "+result);
            return result;
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), "Order Sudah Dikirim, Terima Kasih Telah Memesan",Toast.LENGTH_LONG).show();
            sharedPreference.remove(getApplicationContext());
            hideDialog();
            Intent categoryList = new Intent(getApplicationContext(), CategoryActivity.class);
            startActivity(categoryList);
            finish();
        }
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }
}
