package nilay.android.eventhook.registration;

import android.util.SparseArray;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import nilay.android.eventhook.fragment.registration.AddMoreElementFragment;
import nilay.android.eventhook.fragment.registration.GroupMemberFragment;
import nilay.android.eventhook.fragment.registration.GroupRegistrationFragment;

public class RegistrationAdapter extends FragmentStatePagerAdapter {

    private final SparseArray<Fragment> mFragmentList = new SparseArray<>();
    private final SparseArray<String> mFragmentTitleList = new SparseArray<>();

    private ViewPager viewPager;
    private Fragment mPrimaryItem;

    private int addMemberIndex;
    private boolean onlyMembers = false;

    public boolean isOnlyMembers() {
        return onlyMembers;
    }

    public void setOnlyMembers(boolean onlyMembers) {
        this.onlyMembers = onlyMembers;
    }

    public int getAddMemberIndex() {
        return addMemberIndex;
    }

    public void setAddMemberIndex(int addMemberIndex) {
        this.addMemberIndex = addMemberIndex;
    }

    public RegistrationAdapter(@NonNull FragmentManager fm, ViewPager viewPager) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mFragmentList.clear();
        mFragmentTitleList.clear();
        this.viewPager = viewPager;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (isOnlyMembers()) {
            return new GroupMemberFragment();
        } else {
            if (position == 0) {
                return new GroupRegistrationFragment();
            } else if (position == getAddMemberIndex()) {
                return new AddMoreElementFragment();
            } else {
                return new GroupMemberFragment();
            }
        }
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFragment(Fragment fragment, String title, int index) {
        mFragmentList.put(index, fragment);
        mFragmentTitleList.put(index, title);
    }

    public void setViewPagerPosition(int index) {
        viewPager.setCurrentItem(index);
    }

    public void removeFragment(Fragment fragment, int position) {
        mFragmentList.remove(position);
        mFragmentTitleList.remove(position);
        setAddMemberIndex(getCount());
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }
}
