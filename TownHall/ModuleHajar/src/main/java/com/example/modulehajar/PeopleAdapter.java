package com.example.modulehajar;

import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class PeopleAdapter extends ArrayAdapter<PeopleForLocation> {

    private Context context;
    private List<PeopleForLocation> people;

    public PeopleAdapter(Context context, List<PeopleForLocation> people) {
        super(context, R.layout.people_item_view, people);
        this.context = context;
        this.people = people;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.people_item_view, parent, false);
        }

        // Get the current PeopleForLocation
        PeopleForLocation currentPeopleForLocation = people.get(position);

        // Bind data to the views
        ImageView profileImage = convertView.findViewById(R.id.profileImage);
        TextView PeopleForLocationName = convertView.findViewById(R.id.personName);

        profileImage.setImageResource(currentPeopleForLocation.getImageResId()); // Set profile image
        PeopleForLocationName.setText(currentPeopleForLocation.getName()); // Set name

        return convertView;
    }
}

