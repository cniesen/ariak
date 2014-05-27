package com.niesens.ariak;

import android.widget.ImageView;
import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class AppAdapter extends ArrayAdapter<String> {

    // declaring our ArrayList of items
    private ArrayList<String> objects;

    /*
    * here we must override the constructor for ArrayAdapter
    * the only variable we care about now is ArrayList<String> objects,
    * because it is the list of objects we want to display.
    */
    public AppAdapter(Context context, int textViewResourceId, ArrayList<String> objects) {
        super(context, textViewResourceId, objects);
        this.objects = objects;
    }

    /*
     * we are overriding the getView method here - this is what defines how each
     * list item will look.
     */
    public View getView(int position, View convertView, ViewGroup parent){

        // assign the view we are converting to a local variable
        View view = convertView;

        // first check to see if the view is null. if so, we have to inflate it.
        // to inflate it basically means to render, or show, the view.
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.listitem_app, null);
        }

		/*
		 * Recall that the variable position is sent in as an argument to this method.
		 * The variable simply refers to the position of the current object in the list. (The ArrayAdapter
		 * iterates through the list we sent it)
		 * 
		 * Therefore, i refers to the current String object.
		 */
        String appPackageName = objects.get(position);

        if (appPackageName != null) {

            // This is how you obtain a reference to the TextViews.
            // These TextViews are created in the XML files we defined.

            ImageView appIcon = (ImageView) view.findViewById(R.id.listitem_app_icon);
            appIcon.setImageDrawable(AndroidProcessUtils.getIconFromPackageName(appPackageName, getContext()));
            TextView appName = (TextView) view.findViewById(R.id.listitem_app_name);
            appName.setText(AndroidProcessUtils.getNameFromPackageName(appPackageName, getContext()));
        }

        // the view must be returned to our activity
        return view;

    }

}