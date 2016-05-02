package apps.modisku.com.modisku.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import apps.modisku.com.modisku.Model.Product;
import apps.modisku.com.modisku.R;
import apps.modisku.com.modisku.Util.GetImages;
import apps.modisku.com.modisku.Util.ImageStorage;

/**
 * Created by Danial on 8/4/2015.
 */
public class ShoppingCartAdapter extends BaseAdapter {

    Context mContext;
    LayoutInflater inflater;
    private List<Product> productList = null;
    Bitmap b;
    private boolean mShowCheckbox;
    public class ViewHolder {
        TextView title;

        ImageView productImage;
        CheckBox checkBox;
        TextView quantity;
    }

    public ShoppingCartAdapter(Context context, List<Product> productList, boolean showCheckBox)
    {
        mContext = context;
        this.productList = productList;
        this.mShowCheckbox = showCheckBox;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return productList.size();
    }

    @Override
    public Product getItem(int position) {
        return productList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if(convertView == null)
        {

            convertView = inflater.inflate(R.layout.listview_shopping_cart,null);
            holder = new ViewHolder();
            holder.title = (TextView)convertView.findViewById(R.id.productTitleShoppingCart);
            holder.productImage = (ImageView)convertView.findViewById(R.id.imageListShoppingCart);
            holder.checkBox = (CheckBox)convertView.findViewById(R.id.checkBoxItem);
            holder.quantity = (TextView)convertView.findViewById(R.id.quantityListView);
            convertView.setTag(holder);

        }else
        {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.title.setText(productList.get(position).getTitle());
        holder.quantity.setText("Quantity : "+productList.get(position).getQuantity());
        String imageUrl = productList.get(position).getImageURL();
        String url = imageUrl;
        String fileExtenstion = MimeTypeMap.getFileExtensionFromUrl(url);
        String name = URLUtil.guessFileName(url, null, fileExtenstion);
        holder.productImage.setTag(String.valueOf(productList.get(position).getId()));

        if(ImageStorage.checkifImageExists(name))
        {

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

            Product curProduct = productList.get(position);
        if(!mShowCheckbox) {
            holder.checkBox.setVisibility(View.GONE);
        } else {
            if(curProduct.selected == true)
                holder.checkBox.setChecked(true);
            else
                holder.checkBox.setChecked(false);
        }
        return convertView;
    }
}
