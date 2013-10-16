package de.feedo.android.model.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.feedo.android.R;
import de.feedo.android.model.Feed;

/**
 * Created by Jan-Henrik on 14.10.13.
 */
public class FeedAdapter extends ArrayAdapter<Feed> {

    public FeedAdapter(Context context, List<Feed> objects) {
        super(context, R.layout.list_item_drawer_feed, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_item_drawer_feed, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.list_item_drawer_feed_label);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.list_item_drawer_feed_icon);
        Feed f = this.getItem(position);

        textView.setText(f.title);

        if(f.has_unread) {
            textView.setTypeface(textView.getTypeface(), Typeface.ITALIC);
        } else {
            textView.setTypeface(textView.getTypeface(), Typeface.NORMAL);
        }

        Picasso.with(getContext()).load(f.favicon_url).fit().centerCrop().into(imageView);
        return rowView;
    }
}
