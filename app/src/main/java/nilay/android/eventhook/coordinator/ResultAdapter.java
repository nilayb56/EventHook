package nilay.android.eventhook.coordinator;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import nilay.android.eventhook.R;
import nilay.android.eventhook.model.EventResult;
import nilay.android.eventhook.model.GroupMaster;
import nilay.android.eventhook.model.UserParticipation;
import nilay.android.eventhook.viewmodels.CoordinatorViewModel;

import static nilay.android.eventhook.fragment.volunteer.VolEventResultFragment.getRankList;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ResultViewHolder> {

    private FragmentActivity activity;
    private List<EventResult> resultList;
    private List<String> ranks = new ArrayList<>();
    private List<UserParticipation> winnerUser = new ArrayList<>();
    private List<GroupMaster> winnerGroup = new ArrayList<>();
    private int index = 0;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbRef = database.getReference();

    public ResultAdapter(FragmentActivity activity, List<EventResult> resultList) {
        this.activity = activity;
        this.resultList = resultList;
    }

    @NonNull
    @Override
    public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.result_list_layout, parent, false);
        return new ResultAdapter.ResultViewHolder(view);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull ResultViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        CoordinatorViewModel coordViewModel = new ViewModelProvider(activity).get(CoordinatorViewModel.class);
        if (coordViewModel.isUpdateResult()) {
            holder.spinnerRank.setEnabled(true);
            holder.spinnerWinner.setEnabled(true);
            holder.btnUpdate.setVisibility(View.VISIBLE);
        } else {
            holder.spinnerRank.setEnabled(false);
            holder.spinnerWinner.setEnabled(false);
            holder.btnUpdate.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ResultViewHolder holder, int holderPosition) {

        CoordinatorViewModel coordViewModel = new ViewModelProvider(activity).get(CoordinatorViewModel.class);
        EventResult result = resultList.get(holderPosition);
        holder.spinnerRank.setAdapter(new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_dropdown_item, getRankList()));
        getIndex(result.getRank());

        holder.spinnerRank.setSelection(index);
        if (coordViewModel.isUpdateResult()) {
            holder.spinnerRank.setEnabled(true);
            holder.spinnerWinner.setEnabled(true);
            holder.btnUpdate.setVisibility(View.VISIBLE);
        } else {
            holder.spinnerRank.setEnabled(false);
            holder.spinnerWinner.setEnabled(false);
            holder.btnUpdate.setVisibility(View.GONE);
        }
        if (coordViewModel.isEventIsGroup())
            loadWinnerGroupSpinner(coordViewModel.getEventid(), holder.spinnerWinner, result);
        else
            loadWinnerSpinner(coordViewModel.getEventid(), holder.spinnerWinner, result);

        holder.spinnerRank.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String rank = (String) parent.getSelectedItem();
                switch (rank) {
                    case "First":
                        ranks.add(holderPosition,"1");
                        break;
                    case "Second":
                        ranks.add(holderPosition,"2");
                        break;
                    case "Third":
                        ranks.add(holderPosition,"3");
                        break;

                    default:
                        ranks.add(holderPosition,"0");
                        break;
                }
                if (position == 0)
                    Toast.makeText(activity, "Please Select a Valid Rank!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        holder.spinnerWinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (coordViewModel.isEventIsGroup()) {
                    winnerGroup.add(holderPosition,(GroupMaster) parent.getSelectedItem());
                } else {
                    winnerUser.add(holderPosition,(UserParticipation) parent.getSelectedItem());
                }
                if (position == 0)
                    Toast.makeText(activity, "Please Select a Valid Winner!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        holder.btnUpdate.setOnClickListener((View v) -> {

            if (ranks.get(holderPosition).equals("0")) {
                holder.spinnerRank.requestFocus();
                TextView errorText = (TextView) holder.spinnerRank.getSelectedView();
                errorText.setError("");
                errorText.setTextColor(Color.RED);
                errorText.setText("SELECT A VALID RANK");
            } else {

                if (coordViewModel.isEventIsGroup()) {

                    if (winnerGroup.get(holderPosition).getGroup_id().equals("0")) {
                        holder.spinnerWinner.requestFocus();
                        TextView errorText = (TextView) holder.spinnerWinner.getSelectedView();
                        errorText.setError("");
                        errorText.setTextColor(Color.RED);
                        errorText.setText("SELECT A VALID WINNER");

                    } else {
                        if (ranks.get(holderPosition).equals(result.getRank()) && winnerGroup.get(holderPosition).getGroup_id().equals(result.getWinner_id())) {
                            Toast.makeText(activity, "No Change Detected!", Toast.LENGTH_SHORT).show();
                        } else {
                            dbRef = database.getReference("EventResult").child(coordViewModel.getEventid());
                            dbRef.child(result.getWinner_id()).removeValue();
                            EventResult newResult = new EventResult(winnerGroup.get(holderPosition).getGroup_id(), winnerGroup.get(holderPosition).getGroup_name(), ranks.get(holderPosition));
                            dbRef.child(winnerGroup.get(holderPosition).getGroup_id()).setValue(newResult);
                        }
                    }

                } else {

                    if (winnerUser.get(holderPosition).getUser_id().equals("0")) {
                        holder.spinnerWinner.requestFocus();
                        TextView errorText = (TextView) holder.spinnerWinner.getSelectedView();
                        errorText.setError("");
                        errorText.setTextColor(Color.RED);
                        errorText.setText("SELECT A VALID WINNER");

                    } else {
                        if (ranks.get(holderPosition).equals(result.getRank()) && winnerUser.get(holderPosition).getUser_id().equals(result.getWinner_id())) {
                            Toast.makeText(activity, "No Change Detected!", Toast.LENGTH_SHORT).show();
                        } else {
                            dbRef = database.getReference("EventResult").child(coordViewModel.getEventid());
                            dbRef.child(result.getWinner_id()).removeValue();
                            EventResult newResult = new EventResult(winnerUser.get(holderPosition).getUser_id(), winnerUser.get(holderPosition).getUser_name(), ranks.get(holderPosition));
                            dbRef.child(winnerUser.get(holderPosition).getUser_id()).setValue(newResult);
                        }
                    }

                }

            }

        });

    }

    public void clear() {
        int size = resultList.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                resultList.remove(0);
            }

            notifyItemRangeRemoved(0, size);
        }
    }

    private void getIndex(String rank) {
        switch (rank) {
            case "1":
                index = getRankList().indexOf("First");
                break;
            case "2":
                index = getRankList().indexOf("Second");
                break;
            case "3":
                index = getRankList().indexOf("Third");
                break;

            default:
                index = getRankList().indexOf("Select Rank");
                break;
        }
    }

    private void loadWinnerGroupSpinner(String eventid, Spinner spinnerWinner, EventResult result) {
        List<GroupMaster> groups = new ArrayList<>();
        groups.add(new GroupMaster("0", "Select Winner Group"));
        dbRef = database.getReference("GroupMaster");
        Query query = dbRef.orderByChild("event_id").equalTo(eventid);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 1;
                for (DataSnapshot childDataSnapShot : dataSnapshot.getChildren()) {
                    GroupMaster group = childDataSnapShot.getValue(GroupMaster.class);
                    if (group != null) {
                        groups.add(group);
                        if (group.getGroup_id().equals(result.getWinner_id()))
                            index = i;
                        else
                            i++;
                    }
                }
                spinnerWinner.setAdapter(new ArrayAdapter<GroupMaster>(activity, android.R.layout.simple_spinner_dropdown_item, groups));
                spinnerWinner.setSelection(index);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadWinnerSpinner(String eventid, Spinner spinnerWinner, EventResult result) {
        String winnerId = result.getWinner_id();
        List<UserParticipation> contestants = new ArrayList<>();
        contestants.add(new UserParticipation("0", "Select Winner Contestant"));
        dbRef = database.getReference("UserParticipation").child(eventid);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 1;
                for (DataSnapshot childDataSnapShot : dataSnapshot.getChildren()) {
                    UserParticipation userParticipation = childDataSnapShot.getValue(UserParticipation.class);
                    if (userParticipation != null && userParticipation.getEvent_attendance().equals("1")) {
                        contestants.add(childDataSnapShot.getValue(UserParticipation.class));
                        if (userParticipation.getUser_id().equals(winnerId))
                            index = i;
                        else
                            i++;
                    }
                }
                spinnerWinner.setAdapter(new ArrayAdapter<UserParticipation>(activity, android.R.layout.simple_spinner_dropdown_item, contestants));
                spinnerWinner.setSelection(index);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return resultList.size();
    }

    class ResultViewHolder extends RecyclerView.ViewHolder {

        Spinner spinnerRank, spinnerWinner;
        Button btnUpdate;

        ResultViewHolder(@NonNull View itemView) {
            super(itemView);
            spinnerRank = itemView.findViewById(R.id.spinnerRank);
            spinnerWinner = itemView.findViewById(R.id.spinnerWinner);
            btnUpdate = itemView.findViewById(R.id.btnUpdate);
        }
    }
}
