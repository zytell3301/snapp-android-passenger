package ir.AralStudio.snapp.MapLayout;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import ir.AralStudio.snapp.R;

public class RequestSnapp extends AppCompatActivity {

    Button btnCancelRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_snapp);
        btnCancelRequest = findViewById(R.id.btnCancelRequest);


        btnCancelRequest.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(RequestSnapp.this, "سفر لغو شد", Toast.LENGTH_LONG).show();
                finish();
                return false;
            }
        });

        btnCancelRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(RequestSnapp.this, "برای لغو سفر کلید را نگه دارید", Toast.LENGTH_LONG).show();
            }
        });


    }

    @Override
    public void onBackPressed() {
    }
}