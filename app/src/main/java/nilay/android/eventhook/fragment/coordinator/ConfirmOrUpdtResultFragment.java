package nilay.android.eventhook.fragment.coordinator;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import nilay.android.eventhook.R;
import nilay.android.eventhook.coordinator.ResultAdapter;
import nilay.android.eventhook.model.EventResult;
import nilay.android.eventhook.viewmodels.CoordinatorViewModel;

public class ConfirmOrUpdtResultFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private CardView cardResults;
    private LinearLayout linearConfirm;
    private ImageView imgConfirm, imgUpdate;
    private TextView lblEvent;
    private RecyclerView recyclerResult;
    private Button btnConfirm;

    private CoordinatorViewModel coordViewModel;
    private ResultAdapter resultAdapter;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dbRef = database.getReference();

    public ConfirmOrUpdtResultFragment() {
    }

    // TODO: Rename and change types and number of parameters
    public static ConfirmOrUpdtResultFragment newInstance(String param1, String param2) {
        ConfirmOrUpdtResultFragment fragment = new ConfirmOrUpdtResultFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_confirm_or_updt_result, container, false);

        Animation animfadein = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        view.startAnimation(animfadein);

        cardResults = view.findViewById(R.id.cardResults);
        linearConfirm = view.findViewById(R.id.linearConfirm);
        imgConfirm = view.findViewById(R.id.imgConfirm);
        imgUpdate = view.findViewById(R.id.imgUpdate);
        lblEvent = view.findViewById(R.id.lblEvent);
        btnConfirm = view.findViewById(R.id.btnConfirm);
        recyclerResult = view.findViewById(R.id.recyclerResult);
        recyclerResult.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerResult.setItemAnimator(new DefaultItemAnimator());

        if (getActivity() != null) {
            coordViewModel = new ViewModelProvider(getActivity()).get(CoordinatorViewModel.class);
            lblEvent.setText(coordViewModel.getEventname());
        }

        imgConfirm.setOnClickListener((View v) -> {
            cardResults.setVisibility(View.GONE);
            linearConfirm.setVisibility(View.VISIBLE);
            coordViewModel.setUpdateResult(false);
            loadRecyclerView();
        });

        imgUpdate.setOnClickListener((View v) -> {
            cardResults.setVisibility(View.GONE);
            linearConfirm.setVisibility(View.VISIBLE);
            coordViewModel.setUpdateResult(true);
            loadRecyclerView();
        });

        btnConfirm.setOnClickListener((View v) -> {
            dbRef = database.getReference("EventResult").child(coordViewModel.getEventid());
            dbRef.child("result_confirmed").setValue("1");
        });

        return view;
    }

    private void loadRecyclerView() {
        List<EventResult> results = new ArrayList<>();
        dbRef = database.getReference("EventResult").child(coordViewModel.getEventid());
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot resultSnapShot : dataSnapshot.getChildren()) {
                        if (!Objects.requireNonNull(resultSnapShot.getKey()).equals("result_confirmed")) {
                            EventResult result = resultSnapShot.getValue(EventResult.class);
                            if (result != null)
                                results.add(result);
                        }
                    }
                    if(getActivity()!=null) {
                        if (resultAdapter != null && resultAdapter.getItemCount() != 0) {
                            resultAdapter.clear();
                            resultAdapter = new ResultAdapter(getActivity(), results);
                            recyclerResult.setAdapter(resultAdapter);
                        } else {
                            resultAdapter = new ResultAdapter(getActivity(), results);
                            recyclerResult.setAdapter(resultAdapter);
                        }
                        Animation animfadein = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
                        recyclerResult.startAnimation(animfadein);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
