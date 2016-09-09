package org.sanpei.myapplication;

import org.sanpei.myapplication.R;
import org.sanpei.myapplication.ListItem;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by sanpei on 2016/09/09.
 */
public class ImageArrayAdapter extends ArrayAdapter<ListItem>{

    private int resourceId;
    private List<ListItem> items;
    private LayoutInflater inflater;

    public ImageArrayAdapter(Context context, int resourceId, List<ListItem> items) {
        super(context, resourceId, items);

        this.resourceId = resourceId;
        this.items = items;
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView != null) {
            view = convertView;
        } else {
            view = this.inflater.inflate(this.resourceId, null);
        }

        ListItem item = this.items.get(position);

        // テキストをセット
        TextView appInfoText = (TextView)view.findViewById(R.id.item_text);
        appInfoText.setText(item.getText());

        // アイコンをセット
        ImageView appInfoImage = (ImageView)view.findViewById(R.id.item_image);
        appInfoImage.setImageResource(item.getImageId());

        view.setBackgroundColor(item.getBackgroundColor());

        return view;
    }
}
