package nilay.android.eventhook.viewmodels;

import androidx.lifecycle.ViewModel;

public class DutyAssignViewModel extends ViewModel {
    private String vol_id = "";
    private String duty_name = "";
    private String duty_id = "";

    public String getVol_id() {
        return vol_id;
    }

    public void setVol_id(String vol_id) {
        this.vol_id = vol_id;
    }

    public String getDuty_name() {
        return duty_name;
    }

    public void setDuty_name(String duty_name) {
        this.duty_name = duty_name;
    }

    public String getDuty_id() {
        return duty_id;
    }

    public void setDuty_id(String duty_id) {
        this.duty_id = duty_id;
    }
}
