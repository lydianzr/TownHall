package com.example.modulehajar;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.List;

public class CustomAdapter extends BaseAdapter {
    private Context context;
    private List<String> contacts; // List of contacts in "Name - Phone" format

    public CustomAdapter(Context context, List<String> contacts) {
        this.context = context;
        this.contacts = contacts;
    }

    @Override
    public int getCount() {
        return contacts.size();
    }

    @Override
    public Object getItem(int position) {
        return contacts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.contact_item_view, parent, false);
        }

        TextView contactText = convertView.findViewById(R.id.TVContact);
        ImageButton callButton = convertView.findViewById(R.id.imageButton);

        String contact = contacts.get(position);
        String[] parts = contact.split(" - ");
        String name = parts[0];
        String phone = parts.length > 1 ? parts[1] : "";

        contactText.setText(name);

        // Set Call Button functionality
//        callButton.setOnClickListener(v -> {
//            Intent callIntent = new Intent(Intent.ACTION_DIAL);
//            callIntent.setData(Uri.parse("tel:" + phone));
//            context.startActivity(callIntent);
//        });

        callButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + phone));
                context.startActivity(callIntent);
            } else {
                // Request permission if not granted
                Toast.makeText(context, "Call permission is not granted", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CALL_PHONE}, 1);
            }
        });

        return convertView;
    }
}


