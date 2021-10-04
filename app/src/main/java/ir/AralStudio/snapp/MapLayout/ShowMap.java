package ir.AralStudio.snapp.MapLayout;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import ir.AralStudio.snapp.Grpc.GrpcServices.GetNearbyDriversResponse;
import ir.AralStudio.snapp.Grpc.GrpcServices.TravelersServiceGrpc;
import ir.AralStudio.snapp.Grpc.GrpcServices.location;
import ir.AralStudio.snapp.R;

public class ShowMap extends AppCompatActivity {

    private MapView map;
    private IMapController mapController;
    GeoPoint startPoint, endPoint;
    Marker startMarker,endMarker;
    Marker driversLocation[];
    Timer timer;
    ManagedChannel channel;
    TravelersServiceGrpc.TravelersServiceBlockingStub TravelersService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        channel = Grpc.newChannelBuilder("192.168.1.200:6166", InsecureChannelCredentials.create()).build();
        TravelersService = TravelersServiceGrpc.newBlockingStub(channel);


        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        setContentView(R.layout.activity_show_map);

        Dexter.withContext(getApplicationContext()).withPermissions(Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

                    }
                }).check();

        map = findViewById(R.id.mapView);
        mapController = map.getController();
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);
        mapController.setZoom((long) 15);
        map.setTilesScaledToDpi(true);
        map.setMultiTouchControls(true);

        startMarker = new Marker(map);
        startMarker.setAnchor(0.5f,0.5f);
        endMarker = new Marker(map);
        endMarker.setAnchor(0.5f,0.5f);

        GeoPoint centerPoint = new GeoPoint(35.7448416, 51.3775099, 17);
        mapController.animateTo(centerPoint);
    }

    public void onCommitBtnClicked(View view) {

        GeoPoint tmpPoint = new GeoPoint(map.getMapCenter());
        Button tmpBtn = (Button) view;

        if (tmpBtn.getTag().equals("startPoint")) {
            startPoint = tmpPoint;
            startMarker.setPosition(tmpPoint);
            startMarker.setIcon(getDrawable(R.mipmap.map_origin));
            mapController.animateTo(new GeoPoint(startPoint.getLatitude() - 0.002,
                    startPoint.getLongitude() + 0.002,startPoint.getAltitude()));
            map.getOverlays().add(startMarker);

            findViewById(R.id.imgFakeMark).setBackground(getDrawable(R.mipmap.map_destination));
            tmpBtn.setText("انتخاب مقصد");
            tmpBtn.setTag("endPoint");
        } else {
            if (!startPoint.equals(tmpPoint)) {
                endPoint = tmpPoint;
                endMarker.setPosition(tmpPoint);
                endMarker.setIcon(getDrawable(R.mipmap.map_destination));
                map.getOverlays().add(endMarker);

                findViewById(R.id.imgFakeMark).setVisibility(View.GONE);
                tmpBtn.setVisibility(View.GONE);
                findViewById(R.id.LayoutRequest).setVisibility(View.VISIBLE);

                TextView txtShowPrice = findViewById(R.id.txtShowPrice);
                txtShowPrice.setText("هزینه سفر : " + priceCalculator() + " ریال");

            } else {
                Toast.makeText(this, "نقطه شروع و پایان نمیتوانند یکسان باشند", Toast.LENGTH_LONG).show();
                return;
            }
        }
    }

    private long priceCalculator() {
        double lat = Math.abs(endPoint.getLatitude() - startPoint.getLatitude());
        double lon = Math.abs(endPoint.getLongitude() - startPoint.getLongitude());
        long price = (long) (Math.pow(Math.pow(lat,2) + Math.pow(lon,2),0.5) * 350000);

         if (price < 2000)
         {
             price = 2000;
         }

        if((price / 100) % 10 >= 5)
        {
            price = ((price / 100) + 1) * 100;
        }
        else if (price % 1000 == 0){
            //price will not change
        }else {
            price = (((price / 1000) * 10) + 5) * 100;
        }
      return (price * 10);
    }

    public void onRequestBtnClicked(View view)
    {
        Intent intent = new Intent(this,RequestSnapp.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    location location = ir.AralStudio.snapp.Grpc.GrpcServices.location.newBuilder().setX(1).setY(1).build();
                    GetNearbyDriversResponse Test = TravelersService.getNearbyDrivers(location);

                    for (int count = 0; count < Test.getUserCount(); count++) {

                        //String name = Test.getUser(count).getDriverDetails().getName();
                    }
                } catch (Exception e) {
                    Toast.makeText(ShowMap.this, "ارتباط با سرور برقرار نشد...", Toast.LENGTH_LONG).show();
                }
            }
        }, 0, 5000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        timer.cancel();
    }
}