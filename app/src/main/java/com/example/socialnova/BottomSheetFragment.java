package com.example.socialnova;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import de.hdodenhof.circleimageview.CircleImageView;


public class BottomSheetFragment extends BottomSheetDialogFragment {
    CircleImageView chat,group;
    String phoneno;
    public BottomSheetFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottom_sheet, container, false);
        group = view.findViewById(R.id.groupNew);
        chat = view.findViewById(R.id.chatNew);
        Bundle args = getArguments();
        try {
            phoneno = args.getString("UID");
        } catch (NullPointerException e) {
        }

        group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(),CreateGroupChat.class);
                i.putExtra("uid",phoneno);
                startActivity(i);

            }
        });

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ContactsContract.Intents.Insert.ACTION);
                i.setType(ContactsContract.RawContacts.CONTENT_TYPE);
                startActivity(i);
            }
        });
        return view;
    }
}