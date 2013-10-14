package de.feedo.android.model;

import android.content.Context;
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
public class FeedItemAdapter extends ArrayAdapter<FeedItem> {
    public FeedItemAdapter(Context context, FeedItem[] items) {
        super(context, R.layout.list_item_feed_item, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_item_feed_item, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.feed_item_title);

        FeedItem fi = this.getItem(position);

        textView.setText(fi.title);

        return rowView;
    }
}
