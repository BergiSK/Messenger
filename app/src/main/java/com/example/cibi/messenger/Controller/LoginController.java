package com.example.cibi.messenger.Controller;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.cibi.messenger.GUI.MainScreen;
import com.example.cibi.messenger.Model.User;
import com.example.cibi.messenger.R;

// Trieda zodpovedna za vykonanie prihlasenia.
public class LoginController {

    public void login (final String name, final String password, final Activity loginActivity) {
        new AsyncLogin(loginActivity,password,name).execute();
    }

    // Prihlasovanie bezi na samostatnom vlakne.
    public class AsyncLogin extends AsyncTask<Void, String, Integer> {
        Activity loginActivity;
        String name;
        String password;
        Button loginBtn;
        RelativeLayout loadingBar;

        public AsyncLogin(Activity loginActivity, String password, String name) {
            this.loginActivity = loginActivity;
            this.password = password;
            this.name = name;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            User loggedIn;
            AISCommunicator aisCommunicator = new AISCommunicator();
            // posli request
            String cookieVal = aisCommunicator.login(password,name);

            // ak sa vratilo login cookie, si prihlaseny, cookie uloz a zapni mainscreen
            if (cookieVal != null) {
                loggedIn = new User();
                loggedIn.setLoginName(name);
                loggedIn.setCookie(cookieVal);
                loggedIn.setName("");
                loggedIn.setEmail(name +"@is.stuba.sk");
                Intent i = new Intent(loginActivity, MainScreen.class);
                i.putExtra("User",loggedIn);

                Log.d("MyLog","user cookie: " + loggedIn.getCookie());

                loginActivity.startActivity(i);
                return 0;
            }
            else {
                return 1;
            }

        }

        @Override
        public void onPreExecute() {
        }

        // Update GUI. Vykona sa ak sa prihlasenie nevykonalo uspesne.
        @Override
        public void onPostExecute(Integer logCode) {
            if (logCode == 1) {
                loginBtn = (Button) loginActivity.findViewById(R.id.loginBtn);
                loadingBar = (RelativeLayout) loginActivity.findViewById(R.id.loadingPanel);

                loadingBar.setVisibility(View.INVISIBLE);
                loginBtn.setVisibility(View.VISIBLE);
                Toast.makeText(loginActivity, R.string.wrongCredentials, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
