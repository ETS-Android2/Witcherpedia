package com.example.drhappy.witcherpedia;

import android.app.Activity;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

class CustomArrayAdapter extends ArrayAdapter<String> {
	ArrayList<String> labels;
	private ArrayList<String> icons;
	
	CustomArrayAdapter(Activity context, ArrayList<String> labels, ArrayList<String> icons) {
		super(context, R.layout.list_row_structure, R.id.list_row_label, labels);
		
		this.labels = labels;
		this.icons = icons;
	}
	
	static class ViewHolder{
		TextView label;
		ImageView icon;
		//int position;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		
		//User user = getItem(position);
		
		// Check if an existing view is being reused, otherwise inflate the view
		ViewHolder viewHolder; // view lookup cache stored in tag
		if (convertView == null) {
			// If there's no view to re-use, inflate a brand new view for row
			viewHolder = new ViewHolder();
			LayoutInflater inflater = LayoutInflater.from(getContext());
			
			convertView = inflater.inflate(R.layout.list_row_structure, parent, false);
			viewHolder.label = convertView.findViewById(R.id.list_row_label);
			viewHolder.icon = convertView.findViewById(R.id.list_row_icon);
			// Cache the viewHolder object inside the fresh view
			convertView.setTag(viewHolder);
		} else {
			// View is being recycled, retrieve the viewHolder object from tag
			viewHolder = (ViewHolder) convertView.getTag();
		}
		// Populate the data from the data object via the viewHolder object
		// into the template view.
		Spanned item;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			item = Html.fromHtml(getItem(position), Html.FROM_HTML_MODE_COMPACT);
		}
		else {
			item = Html.fromHtml(getItem(position));
		}
		viewHolder.label.setText(item);
		
		int resourceId = getContext().getResources().getIdentifier(icons.get(position), "drawable", getContext().getPackageName());
		viewHolder.icon.setImageResource(resourceId);
		//viewHolder.icon.setImageResource(map.get(getItem(position)));
		
		// Return the completed view to render on screen
		return convertView;
	}
	
}