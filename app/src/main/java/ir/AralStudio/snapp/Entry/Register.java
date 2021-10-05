package ir.AralStudio.snapp.Entry;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.text.Bidi;

import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import ir.AralStudio.snapp.Grpc.AuthGrpcServices.AuthGrpc;
import ir.AralStudio.snapp.Grpc.AuthGrpcServices.Token;
import ir.AralStudio.snapp.Grpc.AuthGrpcServices.driver;
import ir.AralStudio.snapp.Grpc.AuthGrpcServices.user;
import ir.AralStudio.snapp.R;

public class Register extends AppCompatActivity {

    TextInputEditText edtNumber,edtName, edtFamily,edtPassWord;
    LinearLayout layoutName, layoutFamily, layoutNumber, layoutPassWord;
    Animation animation;
    Spinner dropDownPhone;
    String name, familyName, phoneNumber,passWord;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        dropDownPhone = findViewById(R.id.dropDownPhone);
        edtName = findViewById(R.id.txtName);
        edtFamily = findViewById(R.id.txtFamilyName);
        edtNumber = findViewById(R.id.txtPhoneNumber);
        edtPassWord = findViewById(R.id.txtPassWord);

        layoutName = findViewById(R.id.layoutName);
        layoutFamily = findViewById(R.id.layoutFamily);
        layoutNumber = findViewById(R.id.layoutNumber);
        layoutPassWord = findViewById(R.id.layoutPassWord);

        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.register_layout_anim);
        layoutName.startAnimation(animation);

        String[] items = new String[]{"Iran +98", "India +91", "Turkey +90"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropDownPhone.setAdapter(adapter);
    }

    public void onBtnClicked(View view) {

        if (view.getId() == R.id.btnNextName) {
            name = edtName.getText().toString();

            if (!isTextValidate(name, "نام")) {
                return;
            }

                layoutName.setVisibility(View.GONE);
                layoutFamily.setVisibility(View.VISIBLE);
                layoutFamily.startAnimation(animation);

        } else if (view.getId() == R.id.btnNext2) {
            familyName = edtFamily.getText().toString();

            if (!isTextValidate(familyName, "نام خانوادگی")) {
                return;
            }
                layoutFamily.setVisibility(View.GONE);
                layoutNumber.setVisibility(View.VISIBLE);
                layoutNumber.startAnimation(animation);

        } else if (view.getId() == R.id.btnNext3) {
            phoneNumber = edtNumber.getText().toString();
            phoneNumber.trim();

            if (!phoneNumber.isEmpty() && phoneNumber.charAt(0) == '0') {
                phoneNumber = phoneNumber.substring(1);
            }

            if (!phoneNumber.matches("[0-9]+") || phoneNumber.length() != 10) {
                Toast.makeText(this, "شماره موبایل خود را به صورت صحیح وارد کنید", Toast.LENGTH_LONG).show();
                return;
            }
            layoutNumber.setVisibility(View.GONE);
            layoutPassWord.setVisibility(View.VISIBLE);
            layoutPassWord.startAnimation(animation);
        }
        else if (view.getId() == R.id.btnRegister)
        {
            passWord = edtPassWord.getText().toString();

            if (passWord.contains(" "))
            {
                Toast.makeText(this,"رمز عبور نمیتواند دارای فاصله باشد", Toast.LENGTH_LONG).show();
                return;
            }
            else if (!passWord.matches("[a-zA-z\\W\\d]+")) {
                Toast.makeText(this,"رمز عبور نمیتواند دارای حروف فارسی باشد", Toast.LENGTH_LONG).show();
                return;
            }
            else if (passWord.length() < 8)
            {
                Toast.makeText(this,"رمز عبور باید دارای حداقل ۸ کاراکتر باشد", Toast.LENGTH_LONG).show();
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
        try {
            ManagedChannel channel = Grpc.newChannelBuilder("192.168.1.200:6167",
                    InsecureChannelCredentials.create()).build();
            AuthGrpc.AuthBlockingStub AuthenticationGrp = AuthGrpc.newBlockingStub(channel);

            driver nameFL = driver.newBuilder().setName(name).setLastname(familyName).build();
            user newPassenger = user.newBuilder().setPassword(passWord).setPhone(phoneNumber).setDriverDetails(nameFL).build();

            Token myToken = AuthenticationGrp.newAccount(newPassenger);

            SharedPreferences preferences = getSharedPreferences(getString(R.string.RegPref), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();

            editor.putString(getString(R.string.PrefName), name);
            editor.putString(getString(R.string.PrefFamily), familyName);
            editor.putString(getString(R.string.PrefPhone), phoneNumber);
            editor.putString(getString(R.string.Token), myToken.getToken());
            editor.putBoolean(getString(R.string.PrefIsReg), true);
            editor.apply();

            startActivity(new Intent(Register.this, SplashScreen.class));
            finish();
        }catch (Exception e)
        {
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Toast.makeText(this, "برای ورود به برنامه حتما نیاز به ثبت نام دارید", Toast.LENGTH_LONG).show();
    }
}