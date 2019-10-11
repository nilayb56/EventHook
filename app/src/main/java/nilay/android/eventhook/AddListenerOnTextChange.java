package nilay.android.eventhook;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class AddListenerOnTextChange implements TextWatcher {

    private Context context;
    private TextInputEditText inputEditText;
    private TextInputLayout inputLayout;
    private String regex = "";
    private String errorText = "";

    public AddListenerOnTextChange(Context context, TextInputEditText inputEditText, TextInputLayout inputLayout) {
        this.context = context;
        this.inputEditText = inputEditText;
        this.inputLayout = inputLayout;
    }

    public AddListenerOnTextChange(Context context, TextInputEditText inputEditText, TextInputLayout inputLayout, String regex, String errorText) {
        this.context = context;
        this.inputEditText = inputEditText;
        this.inputLayout = inputLayout;
        this.regex = regex;
        this.errorText = errorText;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!isFieldNull(inputEditText.getText()))
            inputLayout.setError("FIELD CANNOT BE EMPTY");
        else if (!regex.equals("") && !isFieldValid(inputEditText.getText(), regex))
            inputLayout.setError(errorText);
        else {
            inputLayout.setError(null);
            inputLayout.setErrorEnabled(false);
        }

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private boolean isFieldNull(Editable text) {
        return text != null && !text.toString().equals("");
    }

    private boolean isFieldValid(Editable text, String regex) {
        return text != null && text.toString().matches(regex);
    }
}
