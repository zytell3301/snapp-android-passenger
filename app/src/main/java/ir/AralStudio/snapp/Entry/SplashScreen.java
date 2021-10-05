package ir.AralStudio.snapp.Entry;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import ir.AralStudio.snapp.Grpc.AuthGrpcServices.Token;
import ir.AralStudio.snapp.R;
import ir.AralStudio.snapp.MapLayout.ShowMap;

public class SplashScreen extends AppCompatActivity {

public static String TOKEN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent;
                SharedPreferences preferences = getSharedPreferences(getString(R.string.RegPref), Context.MODE_PRIVATE);

                if (preferences.getBoolean(getString(R.string.PrefIsReg),false)) {

                    String name = preferences.getString(getString(R.string.PrefName), null);
                    TOKEN = preferences.getString(getString(R.string.Token),null);
                    Toast.makeText(SplashScreen.this,"سلام " + name, Toast.LENGTH_LONG).show();
                    intent = new Intent(SplashScreen.this, ShowMap.class);

                } else {
                    intent = new Intent(SplashScreen.this, Register.class);
                }

                startActivity(intent);
                finish();
            }
        }, 2000);
    }
}
