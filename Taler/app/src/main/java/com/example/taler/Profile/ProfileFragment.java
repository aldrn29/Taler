package com.example.taler.Profile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.taler.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

    TextView user_id, user_email;
    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    DatabaseReference mUserRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        final FirebaseUser currentUser = mAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        mUserRef = mDatabase.getReference("/users/" + currentUser.getUid());

        user_id = view.findViewById(R.id.user_id);
        user_email = view.findViewById(R.id.user_email);
        final ProgressBar collection_pb = view.findViewById(R.id.collection_pb);

        mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                String userId = user.userId;
                String userEmail = user.email;
                int userPoint = user.counter_story;

                user_id.setText(userId);
                user_email.setText(userEmail);
                setProgress(collection_pb, 4, userPoint);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return view;
    }

    private void setProgress(ProgressBar pb, int total, int progress) {
        pb.setMax(total);
        pb.setProgress(progress);
    }
}