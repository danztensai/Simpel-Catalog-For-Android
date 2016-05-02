package apps.modisku.com.modisku.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import apps.modisku.com.modisku.Model.Product;
import apps.modisku.com.modisku.R;
import apps.modisku.com.modisku.Util.SharedPreference;

public class ProductDetailFragment extends Fragment {

    public static final String ARG_ITEM_ID = "pdt_detail_fragment";
    private String TAG = ProductDetailFragment.class.getSimpleName();
    TextView pdtIdTxt;
    TextView pdtNameTxt;
    ImageView pdtImg;
    Activity activity;
    ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions options;
    Product product;
    private ImageLoadingListener imageListener;
    SharedPreference sharedPreference;
    Button btnOrder ;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        options = new DisplayImageOptions.Builder()
                .showImageOnFail(R.drawable.ic_launcher)
                .showStubImage(R.drawable.ic_launcher)
                .showImageForEmptyUri(R.drawable.ic_launcher).cacheInMemory()
                .cacheOnDisc().build();
        sharedPreference = new SharedPreference();
        imageListener = new ImageDisplayListener();




    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_detail, container,
                false);
        findViewById(view);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            product = bundle.getParcelable("singleProduct");
            setProductItem(product);
        }
        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialog();
            }
        });
        return view;
    }

    private void findViewById(View view) {

        pdtNameTxt = (TextView) view.findViewById(R.id.pdt_name);
        //   pdtIdTxt = (TextView) view.findViewById(R.id.product_id_text);

        pdtImg = (ImageView) view.findViewById(R.id.product_detail_img);
        btnOrder =(Button)view.findViewById(R.id.buttonOrderPromo);
    }

    private void setProductItem(Product resultProduct) {
        pdtNameTxt.setText("" + resultProduct.getDescription());
        //  pdtIdTxt.setText("Product Name :"+resultProduct.getTitle());

        imageLoader.displayImage(resultProduct.getImageURL(), pdtImg, options,
                imageListener);
    }

    private static class ImageDisplayListener extends
            SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections
                .synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view,
                                      Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);

                }
            }
        }
    }


    protected void showInputDialog() {

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View promptView = layoutInflater.inflate(R.layout.quantity_input_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProductDetailFragment.this.getActivity());
        alertDialogBuilder.setView(promptView);

        final EditText editTextQuantity = (EditText) promptView.findViewById(R.id.quantityText);



        List<Product> p = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(p);


        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String q = editTextQuantity.getText().toString();
                        product.setQuantity(Integer.parseInt(q));
                        sharedPreference.addFavorite(getActivity(), product);



                      /*//
                        Log.d("TEST", "Quantity :" + q);
                        urlSendEmail = "http://122.129.112.169/portal/danztensai/email.php?name=" + name + "&email=" + email;
                        Log.d(TAG, urlSendEmail);
                        new FetchTask().execute();
                      */

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
}