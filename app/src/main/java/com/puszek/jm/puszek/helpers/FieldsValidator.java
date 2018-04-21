package com.puszek.jm.puszek.helpers;

import android.content.Context;
import android.widget.EditText;
import android.widget.TextClock;
import android.widget.TextView;

import com.puszek.jm.puszek.R;

import org.w3c.dom.Text;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by joannamaciak on 29/03/2018.
 */

public class FieldsValidator {
    private Context context;

    public FieldsValidator(Context c) {
        this.context = c;
    }

    public boolean isValidField(EditText field) {
        String log = field.getText().toString().trim();
        if (log.equals("")) {
            field.setError(context.getString(R.string.required));
            return false;
        } else return true;
    }

    public boolean isValidPassword(EditText p1, EditText p2) {
        if (p1.getText().toString().trim().length() < 6) {
            p1.setError(context.getString(R.string.min_signs_value));
            return false;
        } else if (!Objects.equals(p1.getText().toString(), p2.getText().toString())) {
            p1.setError(context.getString(R.string.identity_required));
            p2.setText("");
            return false;
        } else return true;
    }

    public boolean isValidHouseNumber(EditText houseNumber) {
        if (!isValidField(houseNumber)) return false;
        else {
            String ADDRESS_PATTERN = "^[0-9-\\+]*$";
            Pattern pattern = Pattern.compile(ADDRESS_PATTERN);

            if (!pattern.matcher(houseNumber.getText().toString()).matches()) {
                houseNumber.setError(context.getString(R.string.incorrect_format));
                return false;
            } else return true;
        }

    }

    public boolean isValidEmail(EditText email) {
        if (!isValidField(email)) return false;
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
                email.setError(context.getString(R.string.incorrect_email));
                return false;
            } else return true;
        }

}
