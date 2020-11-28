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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    TextView user_id, user_email, user_point, textView3;
    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    DatabaseReference mUserIdRef, mUserEmailRef, mUserPointRef, mUserProgressRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        final FirebaseUser currentUser = mAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        mUserIdRef = mDatabase.getReference("/users/" + currentUser.getUid() + "/userId");
        mUserEmailRef = mDatabase.getReference("/users/" + currentUser.getUid() + "/email");
        mUserPointRef = mDatabase.getReference("/users/" + currentUser.getUid() + "/point");
        mUserProgressRef = mDatabase.getReference("/users/" + currentUser.getUid() + "/story_progress/0");

        user_id = view.findViewById(R.id.user_id);
        user_email = view.findViewById(R.id.user_email);
        user_point = view.findViewById(R.id.user_point);
        textView3 = view.findViewById(R.id.textView3);
        final ProgressBar collection_pb = view.findViewById(R.id.collection_pb);

        mUserIdRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
                user_id.setText(value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mUserEmailRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
                user_email.setText(value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mUserPointRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer value = snapshot.getValue(Integer.class);
                user_point.setText(Integer.toString(value));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mUserPointRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                ArrayList<Integer> progressList = snapshot.getValue(ArrayList.class);
//                Integer value = progressList.size();
//                textView3.setText(Integer.toString(value));
//                textView3.setText(progressList.toString());
                Integer value = snapshot.getValue(Integer.class);
                textView3.setText(Integer.toString(value));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return view;
    }

    private void setProgress(ProgressBar pb, int total, int progress){
        pb.setMax(total);
        pb.setProgress(progress);
    }
}