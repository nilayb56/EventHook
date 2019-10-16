package nilay.android.eventhook.fragment.student;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import nilay.android.eventhook.R;
import nilay.android.eventhook.home.EventGridItemDecoration;
import nilay.android.eventhook.model.EventStudentSubmission;

public class StudentVotesFragment extends Fragment {

    private RecyclerView recyclerView;
    private StudentVotesRecyclerViewAdapter adapter;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbRef = database.getReference();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View studentVoteView = inflater.inflate(R.layout.fragment_student_votes, container, false);

        recyclerView = studentVoteView.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1, RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        int largePadding = getResources().getDimensionPixelSize(R.dimen.event_grid_spacing);
        int smallPadding = getResources().getDimensionPixelSize(R.dimen.event_grid_spacing_small);
        recyclerView.addItemDecoration(new EventGridItemDecoration(largePadding, smallPadding));

        dbRef = database.getReference("EventStudentSubmission");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<EventStudentSubmission> studentSubmissions = new ArrayList<>();
                for(DataSnapshot contentSnapShot : dataSnapshot.getChildren()) {
                    studentSubmissions.add(contentSnapShot.getValue(EventStudentSubmission.class));
                }
                adapter = new StudentVotesRecyclerViewAdapter(getActivity(),studentSubmissions);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return studentVoteView;
    }

}
