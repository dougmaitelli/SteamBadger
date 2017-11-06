package com.dougmaitelli.steambadger.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dougmaitelli.steambadger.MainActivity;
import com.dougmaitelli.steambadger.R;
import com.dougmaitelli.steambadger.application.Config;
import com.dougmaitelli.steambadger.util.LoaderTask;
import com.dougmaitelli.steambadger.ws.Ws;

public class LoginActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText customUrlEdit = (EditText) findViewById(R.id.custom_url);
        final Button loginButton = (Button) findViewById(R.id.login_button);

        customUrlEdit.setOnEditorActionListener(new EditText.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginButton.performClick();
                return true;
            }

            return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String customUrl = customUrlEdit.getText().toString();

                login(customUrl);
            }
        });
    }

    private static class LoginTask extends LoaderTask<LoginActivity> {

        String customUrl;

        private LoginTask(LoginActivity ctx, String customUrl) {
            super(ctx);
            this.customUrl = customUrl;
        }

        @Override
        public void process() {
            try {
                String steamId = Ws.getSteamId(customUrl);

                Config.getInstance().setCustomUrl(customUrl);
                Config.getInstance().setSteamId(steamId);
            } catch (Exception ex) {
                Toast.makeText(getContext(), R.string.login_error, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onComplete() {
            Intent activity = new Intent(getContext(), MainActivity.class);
            getContext().startActivity(activity);
        }
    }

    private void login(final String customUrl) {
        if (customUrl.length() == 0) {
            return;
        }

        new LoginTask(this, customUrl);
    }

}

