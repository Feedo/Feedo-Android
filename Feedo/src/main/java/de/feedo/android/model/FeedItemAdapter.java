package de.feedo.android.model;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import de.feedo.android.R;
import de.feedo.android.util.DateParser;

/**
 * Created by Jan-Henrik on 14.10.13.
 */
public class FeedItemAdapter extends ArrayAdapter<FeedItem> {
    public FeedItemAdapter(Context context, List<FeedItem> items) {

        super(context, R.layout.list_item_feed_item, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_item_feed_item, parent, false);
        TextView titleTextView = (TextView) rowView.findViewById(R.id.feed_item_title);
        TextView summaryTextView = (TextView) rowView.findViewById(R.id.feed_item_summary);
        TextView dateTextView = (TextView) rowView.findViewById(R.id.feed_item_date);

        FeedItem fi = this.getItem(position);

        Spanned summarySpanned = Html.fromHtml(fi.summary);
        String summary = "";

        if(summarySpanned.length() > 100)
            summary = summarySpanned.subSequence(0, 100)+"...";

        titleTextView.setText(fi.title);
        summaryTextView.setText(summary.isEmpty() ? summarySpanned : summary);

        if(summary.isEmpty() && summarySpanned.toString().isEmpty())
            summaryTextView.setVisibility(View.GONE);

        Date date = null;
        try {
            date = DateParser.parse(fi.published);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(!fi.read) {
            titleTextView.setTypeface(titleTextView.getTypeface(), Typeface.ITALIC);
        } else {
            titleTextView.setTypeface(titleTextView.getTypeface(), Typeface.NORMAL);
        }

        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getContext());
        DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(getContext());
        String dateString = dateFormat.format(date) + " " + timeFormat.format(date);

        dateTextView.setText(dateString);
        return rowView;
    }
}
