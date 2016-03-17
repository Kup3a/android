package ru.kazakov.kazakov_tack_1;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

import ru.kazakov.kazakov_tack_1.clearJava.MyCounter;

public class ActivityTwo extends AppCompatActivity {

    private ListView listView;

    LayoutInflater mInflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_two);

        mInflater = getLayoutInflater();

        listView = (ListView)findViewById(R.id.listView);

        listView.setAdapter(new MyAdapter());
    }


    private class MyAdapter extends BaseAdapter {
        private int[] data = new int[1000000];


        @Override
        public int getCount() {
            return data.length;
        }

        @Override
        public Object getItem(int position) {
            return data[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_for_list, null);
                holder = new ViewHolder();
                holder.textView = (TextView) convertView.findViewById(R.id.txt_in_item);
                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.textView.setText(MyCounter.convert(position));
            if (position % 2 == 0) {
                convertView.setBackgroundColor(Color.GRAY);
            } else {
                convertView.setBackgroundColor(Color.DKGRAY);
            }

            return convertView;
        }
    }

    static class ViewHolder {
        private TextView textView;
    }

}



