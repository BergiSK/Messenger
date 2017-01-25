package com.example.cibi.messenger.GUI;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.example.cibi.messenger.Controller.EventHandler;
import com.example.cibi.messenger.R;

/**
 * Trieda predstavuje login screen
 */
public class LoginActivity extends AppCompatActivity {
    EventHandler eventHandler = new EventHandler();
    EditText unameField;
    EditText pswField;
    RelativeLayout loadingBar;

    private static final Logger log = Logger.getLogger(LoginActivity.class.getName());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        unameField = (EditText) findViewById(R.id.unameField);
        pswField = (EditText) findViewById(R.id.pswField);
        loadingBar = (RelativeLayout) findViewById(R.id.loadingPanel);

        final Button loginBtn = (Button) findViewById(R.id.loginBtn);

        // Po kliknuti na login
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String loginName = unameField.getText().toString();
                final String password = pswField.getText().toString();

                log.log(Level.INFO,loginName);
                log.log(Level.INFO,password);

                // zobraz loading a nechaj eventHandler postarat sa o akciu
                loginBtn.setVisibility(View.INVISIBLE);
                loadingBar.setVisibility(View.VISIBLE);

                // spracuj login
                eventHandler.login(loginName, password, LoginActivity.this);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // znova zobraz button, zrus loading bar
        Button loginBtn = (Button) findViewById(R.id.loginBtn);
        loadingBar = (RelativeLayout) findViewById(R.id.loadingPanel);

        loadingBar.setVisibility(View.INVISIBLE);
        loginBtn.setVisibility(View.VISIBLE);
    }
}
