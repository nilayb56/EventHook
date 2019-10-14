package nilay.android.eventhook.fragment.student;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import nilay.android.eventhook.R;
import nilay.android.eventhook.model.Event;
import nilay.android.eventhook.model.UserParticipation;
import nilay.android.eventhook.viewmodels.StudentViewModel;

public class UploadWorkFragment extends Fragment {

    private StudentViewModel studentViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload_work, container, false);

        if(getActivity()!=null)
            studentViewModel = new ViewModelProvider(getActivity()).get(StudentViewModel.class);

        RecyclerView recyclerEventWork = view.findViewById(R.id.recyclerEventWork);
        recyclerEventWork.setLayoutManager(new LinearLayoutManager(recyclerEventWork.getContext()));
        recyclerEventWork.setAdapter(new UploadWorkRecyclerViewAdapter(getActivity(), recyclerEventWork,
                studentViewModel.getParticipations(), studentViewModel.getParticipatedEvents()));

        return view;
    }

    public static class UploadWorkRecyclerViewAdapter extends RecyclerView.Adapter<UploadWorkRecyclerViewAdapter.ViewHolder> {

        private FragmentActivity mActivity;
        private RecyclerView recyclerView;
        private List<UserParticipation> participations;
        private List<Event> participatedEvents;
        private StudentViewModel studentViewModel;

        public UploadWorkRecyclerViewAdapter(FragmentActivity mActivity, RecyclerView recyclerView, List<UserParticipation> participations, List<Event> participatedEvents) {
            this.mActivity = mActivity;
            this.recyclerView = recyclerView;
            this.participations = participations;
            this.participatedEvents = participatedEvents;
            studentViewModel = new ViewModelProvider(mActivity).get(StudentViewModel.class);
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }
    }

}
