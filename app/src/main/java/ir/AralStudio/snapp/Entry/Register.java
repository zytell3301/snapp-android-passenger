package ir.AralStudio.snapp.Entry;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.text.Bidi;

import ir.AralStudio.snapp.R;
import retrofit2.http.GET;

public class Register extends AppCompatActivity {

    EditText edtName, edtFamily, edtNumber;
    LinearLayout layoutName, layoutFamily, layoutNumber;
    Animation animation;

    String name, familyName, phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtName = findViewById(R.id.txtName);
        edtFamily = findViewById(R.id.txtFamilyName);
        edtNumber = findViewById(R.id.txtPhoneNumber);
        layoutName = findViewById(R.id.layoutName);
        layoutFamily = findViewById(R.id.layoutFamily);
        layoutNumber = findViewById(R.id.layoutNumber);

        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.register_layout_anim);
        layoutName.startAnimation(animation);
    }

    public void onBtnClicked(View view) {

        if (view.getId() == R.id.btnNextName) {
            name = edtName.getText().toString();

            if (isTextValidate(name, "نام")) {
                layoutName.setVisibility(View.GONE);
                layoutFamily.setVisibility(View.VISIBLE);
                layoutFamily.startAnimation(animation);
            } else {
                return;
            }
        } else if (view.getId() == R.id.btnNext2) {
            familyName = edtFamily.getText().toString();

            if (isTextValidate(familyName, "نام خانوادگی")) {

                layoutFamily.setVisibility(View.GONE);
                layoutNumber.setVisibility(View.VISIBLE);
                layoutNumber.startAnimation(animation);
            } else {
                return;
            }

        } else if (view.getId() == R.id.btnRegister) {
            phoneNumber = edtNumber.getText().toString();

            if (!phoneNumber.matches("[0-9]+")) {
                Toast.makeText(this, "شماره موبایل خود را به صورت صحیح وارد کنید", Toast.LENGTH_LONG).show();
                return;
            }

            onBtnRegisterClicked();
        }

        InputMethodManager methodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        methodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }

    private boolean isTextValidate(String text, String appendText) {

        text.trim();

        if (!text.matches(".*\\d.*")) {
            if (!new Bidi(text, 0).isLeftToRight()) {

                if (!text.matches(".*\\W.*") || text.contains(" ")) {
                    return true;
                } else {
                    Toast.makeText(this, appendText + " نباید دارای علامت و کاراکتر باشد", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, appendText + " باید فارسی باشد", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, appendText + " نباید دارای عدد باشد", Toast.LENGTH_LONG).show();
        }
        return false;
    }


    private void onBtnRegisterClicked() {

        SharedPreferences preferences = getSharedPreferences(getString(R.string.RegPref), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(getString(R.string.PrefName), name);
        editor.putString(getString(R.string.PrefFamily), familyName);
        editor.putString(getString(R.string.PrefPhone), phoneNumber);
        editor.putBoolean(getString(R.string.PrefIsReg), true);
        editor.apply();

        startActivity(new Intent(Register.this, SplashScreen.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Toast.makeText(this, "برای ورود به برنامه حتما نیاز به ثبت نام دارید", Toast.LENGTH_LONG).show();
    }
}