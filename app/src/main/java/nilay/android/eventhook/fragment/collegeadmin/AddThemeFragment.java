package nilay.android.eventhook.fragment.collegeadmin;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import nilay.android.eventhook.AddListenerOnTextChange;
import nilay.android.eventhook.R;
import nilay.android.eventhook.fragment.registration.ClgAdminRegFragment;
import nilay.android.eventhook.model.EventTheme;
import nilay.android.eventhook.viewmodels.ClgAdminViewModel;

public class AddThemeFragment extends Fragment {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbRef = database.getReference();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View addThemeView = inflater.inflate(R.layout.fragment_add_theme, container, false);

        TextInputLayout txtThemeNameLayout = addThemeView.findViewById(R.id.txtThemeNameLayout);
        TextInputEditText txtAddTheme = addThemeView.findViewById(R.id.txtAddTheme);
        MaterialButton btnAddTheme = addThemeView.findViewById(R.id.btnAddTheme);

        List<String> themes = new ArrayList<>();

        if (getActivity() != null) {
            ClgAdminViewModel clgAdminViewModel = new ViewModelProvider(getActivity()).get(ClgAdminViewModel.class);
            clgAdminViewModel.getEventRegState().observe(getActivity(), state -> {
                if (state.equals("1")) {
                    for (String theme : themes) {
                        dbRef = database.getReference("EventTheme").child(clgAdminViewModel.getNewRegisteredEventId());
                        String themeId = dbRef.push().getKey();
                        assert themeId != null;
                        EventTheme newTheme = new EventTheme(clgAdminViewModel.getNewRegisteredEventId(), themeId, theme);
                        dbRef.child(themeId).setValue(newTheme);
                    }
                }
            });
        }

        txtAddTheme.addTextChangedListener(new AddListenerOnTextChange(getContext(), txtAddTheme, txtThemeNameLayout, "^[\\p{L} .'-]+$", "ENTER ONLY ALPHABETS"));
        btnAddTheme.setOnClickListener((View v) -> {
            String themeName = txtAddTheme.getText().toString();
            if (themeName.equals("")) {
                txtAddTheme.requestFocus();
                txtThemeNameLayout.setError("FIELD CANNOT BE EMPTY");
            } else if (!themeName.matches("^[\\p{L} .'-]+$")) {
                txtAddTheme.requestFocus();
                txtThemeNameLayout.setError("ENTER ONLY ALPHABETS");
            } else {
                themes.add(themeName);
                txtAddTheme.setText("");
                txtAddTheme.requestFocus();
            }
        });

        return addThemeView;
    }

}
