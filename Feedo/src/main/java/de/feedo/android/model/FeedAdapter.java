package de.feedo.android.model;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import de.feedo.android.R;

/**
 * Created by Jan-Henrik on 14.10.13.
 */
public class FeedAdapter extends ArrayAdapter<Feed> {
    private final Context context;
    private final Feed[] values;

    public FeedAdapter(Context context, Feed[] objects) {
        super(context, R.layout.list_item_drawer_feed, objects);
        this.context = context;
        this.values = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_item_drawer_feed, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.label);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        textView.setText(values[position].title);

        Picasso.with(context).load(values[position].faviconUrl).into(imageView);
        Log.i("feedo", values[position].title);
        return rowView;
    }
}
