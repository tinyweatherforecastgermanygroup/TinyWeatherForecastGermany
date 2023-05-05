package de.kaffeemitkoffein.tinyweatherforecastgermany;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;

public class WeatherDetailsListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<WeatherDetailsActivity.DetailsElement> detailsElements;
    private LayoutInflater layoutInflater;

    public WeatherDetailsListAdapter(Context context, ArrayList<WeatherDetailsActivity.DetailsElement> detailsElements){
        this.context = context;
        this.detailsElements = detailsElements;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return detailsElements.size();
    }

    @Override
    public Object getItem(int i) {
        return detailsElements.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    private static class ViewHolder{
        TextView heading;
        LinearLayout linearLayout;
        ImageView icon;
        TextView value;
        TextView label;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        WeatherDetailsActivity.DetailsElement detailsElement = detailsElements.get(position);
        boolean isNewView = true;
        ViewHolder viewHolder = new ViewHolder();
        TextView heading = null;
        LinearLayout linearLayout = null;
        ImageView icon = null;
        TextView value = null;
        TextView label = null;
        if (view==null){
            ThemePicker.SetTheme(context);
            view = layoutInflater.inflate(R.layout.detailslistitem, viewGroup, false);
        } else {
            isNewView = false;
            // recycle
            viewHolder = (ViewHolder) view.getTag();
            heading = viewHolder.heading;
            linearLayout = viewHolder.linearLayout;
            icon = viewHolder.icon;
            value = viewHolder.value;
            label = viewHolder.label;
        }
        // find views if null
        if (heading==null){
            heading = (TextView) view.findViewById(R.id.dl_heading);
        }
        if (linearLayout==null){
            linearLayout = (LinearLayout) view.findViewById(R.id.dl_linearlayout);
        }
        if (icon==null){
            icon = (ImageView) view.findViewById(R.id.dl_icon);
        }
        if (value==null){
            value = (TextView) view.findViewById(R.id.dl_value);
        }
        if (label==null){
            label = (TextView) view.findViewById(R.id.dl_label);
        }
        // populate with values
        heading.setText(detailsElement.heading);
        //icon.setImageDrawable(detailsElement.icon);
        value.setText(detailsElement.value);
        label.setText(detailsElement.label);
        // save to tag for viewHolderPattern if view was not recycled
        if (isNewView){
            viewHolder.heading = heading; viewHolder.linearLayout = linearLayout; viewHolder.icon = icon;
            viewHolder.value = value; viewHolder.label=label;
            view.setTag(viewHolder);
        }
        return view;
    }
}
