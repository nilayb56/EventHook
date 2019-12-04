package nilay.android.eventhook.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AppAdminViewModel extends ViewModel {

    private String clgAdminRoleId = "";
    private MutableLiveData<String> stateChange;

    public MutableLiveData<String> getStateChange() {
        if(stateChange==null){
            MutableLiveData<String> state = new MutableLiveData<>();
            state.setValue("0");
            this.stateChange = state;
        }
        return stateChange;
    }

    public String getClgAdminRoleId() {
        return clgAdminRoleId;
    }

    public void setClgAdminRoleId(String clgAdminRoleId) {
        this.clgAdminRoleId = clgAdminRoleId;
    }
}
