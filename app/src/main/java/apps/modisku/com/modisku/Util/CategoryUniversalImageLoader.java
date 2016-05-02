package apps.modisku.com.modisku.Util;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import apps.modisku.com.modisku.R;


/**
 * Created by Danial on 8/9/2015.
 */
public class CategoryUniversalImageLoader extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Notify the system to allow an options menu for this fragment.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstaceState)
    {
        View view = inflater.inflate(R.layout.fragment_layout_promo,container,false);


        return view;
    }




}
