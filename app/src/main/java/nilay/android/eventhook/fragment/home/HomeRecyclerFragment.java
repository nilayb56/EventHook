package nilay.android.eventhook.fragment.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.android.material.textview.MaterialTextView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import nilay.android.eventhook.R;
import nilay.android.eventhook.fragment.admin.AddCollegeFragment;
import nilay.android.eventhook.fragment.collegeadmin.AddEventFragment;
import nilay.android.eventhook.fragment.log.LoginFragment;
import nilay.android.eventhook.viewmodels.HomeViewModel;

public class HomeRecyclerFragment extends Fragment {
    private final String[] data = {
            "About", "Help", "Contact Us", "View Events/Register", "Login", "Result Dashboard"
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home_recycler, container, false);
        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerHomeMenu);
        ImageView imgLogo = rootView.findViewById(R.id.imgLogo);
        setupRecyclerView(recyclerView, imgLogo);
        return rootView;

    }

    private void setupRecyclerView(RecyclerView recyclerView, ImageView imgLogo) {
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter(new SimpleStringRecyclerViewAdapter(getActivity(),
                data, recyclerView, imgLogo));
    }

    public static class SimpleStringRecyclerViewAdapter extends RecyclerView.Adapter<SimpleStringRecyclerViewAdapter.ViewHolder> {
        private String[] mValues;
        private FragmentActivity mActivity;
        private RecyclerView recyclerView;
        private ImageView imgLogo;
        private MaterialTextView prevTextView;
        private FrameLayout prevFlMenuItem;
        private Integer prevPos;
        private List<ViewHolder> holderList = new ArrayList<>();
        private HomeViewModel homeViewModel;

        public static class ViewHolder extends RecyclerView.ViewHolder {

            public final View mView;
            public final MaterialTextView mTextView;
            public final FrameLayout flMenuItem;
            public Fragment fragment = null;
            public Class fragmentClass = null;
            public int itemState = 0;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mTextView = view.findViewById(R.id.lblMenuItem);
                flMenuItem = view.findViewById(R.id.flMenuItem);
                mTextView.setId(GetUniqueID());
                flMenuItem.setId(GetUniqueID());
            }

        }

        public String getValueAt(int position) {
            return mValues[position];
        }

        public SimpleStringRecyclerViewAdapter(FragmentActivity activity, String[] items, RecyclerView recyclerView, ImageView imgLogo) {
            mActivity = activity;
            mValues = items;
            this.recyclerView = recyclerView;
            this.imgLogo = imgLogo;
            homeViewModel = new ViewModelProvider(mActivity).get(HomeViewModel.class);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.home_menu_item, parent, false);
            ViewHolder holder = new ViewHolder(view);
            holderList.add(holder);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NotNull final ViewHolder holder, final int position) {

            holder.mTextView.setText(getValueAt(position));
            holder.mTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_plus_green, 0, 0, 0);

            holder.mView.setOnClickListener((View v) -> {

                recyclerView.scrollToPosition(position);

                homeViewModel.setMenuItem(getValueAt(position));

                imgLogo.setVisibility(View.GONE);

                if (prevPos != null && position != prevPos) {
                    holderList.get(prevPos).itemState = 0;
                    prevTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_plus_green, 0, 0, 0);
                    prevTextView.setBackgroundResource(R.drawable.border);
                    prevFlMenuItem.setBackground(null);
                    prevFlMenuItem.setVisibility(View.GONE);
                    Fragment prevFragment = mActivity.getSupportFragmentManager().findFragmentByTag(getValueAt(position));
                    if (prevFragment != null)
                        mActivity.getSupportFragmentManager().beginTransaction().remove(prevFragment).commit();
                } else {
                    if (prevPos != null)
                        imgLogo.setVisibility(View.VISIBLE);
                }

                if (holderList.get(position).itemState == 0) {
                    holder.mTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_minus_red, 0, 0, 0);
                    holder.flMenuItem.setVisibility(View.VISIBLE);
                    holder.mTextView.setBackground(null);
                    holder.flMenuItem.setBackgroundResource(R.drawable.border);
                    holderList.get(position).itemState = 1;
                    fillFrameLayout(holder, position);
                } else {
                    holder.mTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_plus_green, 0, 0, 0);
                    holder.flMenuItem.setVisibility(View.GONE);
                    holder.mTextView.setBackgroundResource(R.drawable.border);
                    holder.flMenuItem.setBackground(null);
                    holderList.get(position).itemState = 0;
                }
                prevPos = position;
                prevTextView = holder.mTextView;
                prevFlMenuItem = holder.flMenuItem;
            });
        }

        private void fillFrameLayout(@NotNull ViewHolder holder, int position) {
            if (getValueAt(position).equals("Login")) {
                holder.fragmentClass = LoginFragment.class;
            } else if (getValueAt(position).equals("Result Dashboard")) {
                holder.fragmentClass = AddCollegeFragment.class;
            } else {
                holder.fragmentClass = CommonHomeMenuFragment.class;
            }
            try {
                holder.fragment = (Fragment) holder.fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (holder.fragment != null) {
                FragmentManager fragmentManager = mActivity.getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(holder.flMenuItem.getId(), holder.fragment, mValues[position]).commit();
            }
        }

        public static int GetUniqueID() {
            return (111 + (int) (Math.random() * 9999));
        }

        @Override
        public int getItemCount() {
            return mValues.length;
        }
    }
}
