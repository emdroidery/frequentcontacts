/*
 * Copyright (C) 2010 The Phone Pony Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.phonepony.frequentcontacts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 *
 */
class ContactsListAdapter extends ArrayAdapter<CallCounts> {
    private LayoutInflater mInflater;

    public ContactsListAdapter(Context context) {
        super(context, R.layout.contact_link, R.id.appwidget_text);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * Make a SpeechView to hold each row.
     *
     * @see android.widget.ListAdapter#getView(int, android.view.View,
     *      android.view.ViewGroup)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return createViewFromResource(position, convertView, parent, R.layout.contact_cell);
    }

    private View createViewFromResource(int position, View convertView, ViewGroup parent, int resource) {
        TextView text;
        ImageView image;
        ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(resource, parent, false);
            holder = new ViewHolder();
            holder.text = (TextView) convertView.findViewById(R.id.appwidget_text);
            holder.icon = (ImageView) convertView.findViewById(R.id.appwidget_image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        CallCounts item = getItem(position);
        holder.text.setText(item.getDisplay());
        holder.icon.setImageBitmap(item.getBitmap());

        return convertView;
    }

    static class ViewHolder {
        TextView text;
        ImageView icon;
    }

}
