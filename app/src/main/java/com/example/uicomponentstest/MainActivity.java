package com.example.uicomponentstest;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.uicomponents.AdvancedSearchBar;

import java.util.ArrayList;

public class MainActivity extends Activity {

    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_main);
//        SearchBar sb = (SearchBar)findViewById(R.id.searchbar);
//        sb.setOnMenuClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(mContext,"menu",Toast.LENGTH_SHORT).show();
//            }
//        });
//        sb.setOnSearchClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(mContext,"search",Toast.LENGTH_SHORT).show();
//            }
//        });
//        sb.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                    Toast.makeText(mContext,"search "+v.getText(),Toast.LENGTH_SHORT).show();
//                    return true;
//                }
//                return false;
//            }
//        });


        final AdvancedSearchBar asb = (AdvancedSearchBar) findViewById(R.id.advsearchbar);
        asb.setOnMenuClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,"Menu clicked",Toast.LENGTH_SHORT).show();
            }
        });
        asb.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,"Voice search clicked",Toast.LENGTH_SHORT).show();
            }
        });
        AdvancedSearchBar.SearchAdapter adapter = new AdvancedSearchBar.SearchAdapter(){
            private final ArrayList<String> data = new ArrayList<>();

            @Override
            public void setSearchString(CharSequence searchString) {
                data.clear();
                if (searchString.length() != 0){
                    int num = searchString.length();
                    for (int i = 0; i < num; i++){
                        data.add(searchString.toString() + i);
                    }
                }
                notifyDataSetChanged();
            }

            @Override
            public int getCount() {
                return data.size();
            }

            @Override
            public Object getItem(int position) {
                return data.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = convertView;
                if (view == null) {
                    view = ((LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.listitem, parent, false);
                }
                ((TextView)view.findViewById(R.id.label)).setText(data.get(position));
                final int pos = position;
//                view.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Toast.makeText(mContext, "Clicked entry number "+pos, Toast.LENGTH_SHORT).show();
//                    }
//                });
                return view;
            }
        };
        asb.setSearchAdapter(adapter);
        asb.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(mContext, "Item click at position " + position, Toast.LENGTH_SHORT).show();
            }
        });
        asb.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(mContext, "Item long click at position " + position, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

}

