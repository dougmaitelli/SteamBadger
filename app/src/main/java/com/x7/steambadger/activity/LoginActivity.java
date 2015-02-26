package com.x7.steambadger.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.x7.steambadger.MainActivity;
import com.x7.steambadger.R;
import com.x7.steambadger.application.Config;
import com.x7.steambadger.util.LoaderTask;
import com.x7.steambadger.ws.Util;

public class LoginActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText customUrlEdit = (EditText) findViewById(R.id.custom_url);

        Button loginButton = (Button) findViewById(R.id.login_button);

        loginButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String customUrl = customUrlEdit.getText().toString();

                login(customUrl);
            }
        });
    }

    private void login(final String customUrl) {
        if (customUrl.length() == 0) {
            return;
        }

        new LoaderTask<LoginActivity>(this) {

            @Override
            public void process() {
                try {
                    String steamId = Util.getSteamId(customUrl);

                    Config.getInstance().setCustomUrl(customUrl);
                    Config.getInstance().setSteamId(steamId);
                } catch (Exception ex) {
                    System.out.println(ex);
                }
            }

            @Override
            public void onComplete() {
                Intent activity = new Intent(getContext(), MainActivity.class);
                startActivity(activity);
            }
        };
    }

}

