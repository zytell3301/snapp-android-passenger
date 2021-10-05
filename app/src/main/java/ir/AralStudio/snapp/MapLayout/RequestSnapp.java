package ir.AralStudio.snapp.MapLayout;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import io.grpc.Metadata;
import io.grpc.stub.MetadataUtils;
import ir.AralStudio.snapp.Entry.SplashScreen;
import ir.AralStudio.snapp.Grpc.TravelersService.TravelersServiceGrpc;
import ir.AralStudio.snapp.Grpc.TravelersService.direction;
import ir.AralStudio.snapp.Grpc.TravelersService.driver;
import ir.AralStudio.snapp.Grpc.TravelersService.location;
import ir.AralStudio.snapp.R;

public class RequestSnapp extends AppCompatActivity {

    Button btnCancelRequest;
    ManagedChannel channel;
    TravelersServiceGrpc.TravelersServiceBlockingStub TravelersService;
    Metadata header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_snapp);
        btnCancelRequest = findViewById(R.id.btnCancelRequest);

        header = new Metadata();
        Metadata.Key<String> key =
                Metadata.Key.of("token", Metadata.ASCII_STRING_MARSHALLER);
        header.put(key, SplashScreen.TOKEN);

        channel = Grpc.newChannelBuilder("192.168.1.200:6166", InsecureChannelCredentials.create()).build();
        TravelersService = TravelersServiceGrpc.newBlockingStub(channel);
        try {
            TravelersService = MetadataUtils.attachHeaders(TravelersService, header);
            driver driver = TravelersService.requestDriver(direction.newBuilder().
                    setOrigin(location.newBuilder().setX(ShowMap.startPoint.getLongitude()).setY(ShowMap.startPoint.getLatitude()
                    ).build()).setDestination(location.newBuilder().setX(ShowMap.endPoint.getLongitude()).setY(ShowMap.startPoint.getLatitude()
            ).build()).build());

            ShowMap.IsAccepted = true;
            ShowMap.DriverName = driver.getName();
            ShowMap.DriverPlate = driver.getVehicleNo();

            finish();

        }catch (Exception e)
        {
            Log.i("Driver", e.getMessage());
        }


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