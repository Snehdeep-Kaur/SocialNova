package com.example.socialnova;

import android.app.Activity;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyListView2 extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] Name;
    private final String[] Status;
    private final Uri[] uri;

    public MyListView2(Activity context, String[] name, String[] status, Uri[] uri) {
        super(context, R.layout.custom_list, name);
        // TODO Auto-generated constructor stub
        this.context=context;
        this.Name = name;
        this.Status = status;
        this.uri = uri;

    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.custom_list2, null,true);

        CheckedTextView nameText = (CheckedTextView) rowView.findViewById(R.id.name);
        CircleImageView profileView = (CircleImageView) rowView.findViewById(R.id.pp);
        TextView statusText = (TextView) rowView.findViewById(R.id.status);

        nameText.setChecked(false);
        //nameText.setCheckMarkDrawable(R.drawable.ic_baseline_radio_button_unchecked_24);
        nameText.setText(Name[position]);
        Picasso.get().load(uri[position]).fit().centerInside().into(profileView);
        statusText.setText(Status[position]);

        /* nameText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nameText.isChecked()){
                    nameText.setCheckMarkDrawable(R.drawable.ic_baseline_radio_button_unchecked_24);
                    nameText.setChecked(false);
                }else{
                    nameText.setCheckMarkDrawable(R.drawable.ic_baseline_check_24);
                    nameText.setChecked(true);
                }
            }
        });
        statusText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nameText.isChecked()){
                    nameText.setCheckMarkDrawable(R.drawable.ic_baseline_radio_button_unchecked_24);
                    nameText.setChecked(false);
                }else{
                    nameText.setCheckMarkDrawable(R.drawable.ic_baseline_check_24);
                    nameText.setChecked(true);
                }
            }
        });
        profileView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nameText.isChecked()){
                    nameText.setCheckMarkDrawable(R.drawable.ic_baseline_radio_button_unchecked_24);
                    nameText.setChecked(false);
                }else{
                    nameText.setCheckMarkDrawable(R.drawable.ic_baseline_check_24);
                    nameText.setChecked(true);
                }
            }
        });*/


        return rowView;

    };
}

