package com.expedia.homeaway.common.ui;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.expedia.homeaway.R;
import com.expedia.homeaway.common.app.HomeAwayApplication;
import com.expedia.homeaway.common.ui.injection.component.ActivityComponent;
import com.expedia.homeaway.common.ui.injection.component.DaggerActivityComponent;

import butterknife.Bind;

public abstract class BaseActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    public Toolbar toolbar;

    protected ActivityComponent component;


    protected void initToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    public ActivityComponent component() {
        if (component == null) {
            component = DaggerActivityComponent.builder()
                    .placesSearchComponent(((HomeAwayApplication) getApplication()).component())
                    .build();
        }
        return component;
    }

    protected void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View focusedView = getCurrentFocus();
        if (focusedView != null) {
            imm.toggleSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),
                    InputMethodManager.SHOW_FORCED, 0);
        }
    }

    protected void hideKeyboard() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        View focusedView = getCurrentFocus();
        if (focusedView != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
    }


    protected void displayCustomToast(String message) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast,
                (ViewGroup) findViewById(R.id.custom_toast_container));

        TextView text = layout.findViewById(R.id.text);
        text.setText(message);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.BOTTOM, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

}
