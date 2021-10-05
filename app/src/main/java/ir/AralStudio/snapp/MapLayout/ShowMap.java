package ir.AralStudio.snapp.MapLayout;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.jar.Attributes;

import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import io.grpc.Metadata;
import io.grpc.stub.MetadataUtils;
import ir.AralStudio.snapp.Entry.SplashScreen;
import ir.AralStudio.snapp.Grpc.TravelersService.GetNearbyDriversResponse;
import ir.AralStudio.snapp.Grpc.TravelersService.TravelersServiceGrpc;
import ir.AralStudio.snapp.Grpc.TravelersService.driver;
import ir.AralStudio.snapp.Grpc.TravelersService.location;
import ir.AralStudio.snapp.R;

public class ShowMap extends AppCompatActivity {

    public static boolean IsAccepted = false;
    public static String DriverName = "";
    public static String DriverPlate = "";

    private MapView map;
    private IMapController mapController;
    public static GeoPoint startPoint, endPoint;
    public static String PriceShow;

    Marker startMarker, endMarker;
    List<Marker> driversLocation;
    Timer timer;
    ManagedChannel channel;
    TravelersServiceGrpc.TravelersServiceBlockingStub TravelersService;
    Metadata header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        header = new Metadata();
        Metadata.Key<String> key =
                Metadata.Key.of("token", Metadata.ASCII_STRING_MARSHALLER);
        header.put(key, SplashScreen.TOKEN);

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
        startMarker.setAnchor(0.5f, 0.5f);
        endMarker = new Marker(map);
        endMarker.setAnchor(0.5f, 0.5f);

        GeoPoint centerPoint = new GeoPoint(35.7448416, 51.3775099);
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
                    startPoint.getLongitude() + 0.002));
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

                PriceShow = String.valueOf(priceCalculator());

                txtShowPrice.setText("هزینه سفر : " + PriceShow + " ریال");

            } else {
                Toast.makeText(this, "نقطه شروع و پایان نمیتوانند یکسان باشند", Toast.LENGTH_LONG).show();
                return;
            }
        }
    }

    private long priceCalculator() {
        double lat = Math.abs(endPoint.getLatitude() - startPoint.getLatitude());
        double lon = Math.abs(endPoint.getLongitude() - startPoint.getLongitude());
        long price = (long) (Math.pow(Math.pow(lat, 2) + Math.pow(lon, 2), 0.5) * 350000);

        if (price < 2000) {
            price = 2000;
        }

        if ((price / 100) % 10 >= 5) {
            price = ((price / 100) + 1) * 100;
        } else if (price % 1000 == 0) {
            //price will not change
        } else {
            price = (((price / 1000) * 10) + 5) * 100;
        }
        return (price * 10);
    }

    public void onRequestBtnClicked(View view) {
        Intent intent = new Intent(this, RequestSnapp.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (IsAccepted == true) {
            TextView _txtPlate = findViewById(R.id.txtPlate);
            ImageView _imgDriver = findViewById(R.id.imgDriver);
            TextView _txtDriverName = findViewById(R.id.txtDriverName);
            TextView _txtPriceShow2 = findViewById(R.id.txtShowPrice2);
            LinearLayout _layout_driver = findViewById(R.id.LayoutDriverDetail);

            _txtPlate.setText("پلاک : " + DriverPlate);
            _txtDriverName.setText("نام راننده : " + DriverName);
            _txtPriceShow2.setText("هزینه سفر : " + PriceShow + " ریال");
            _imgDriver.setBackground(getDrawable(R.mipmap.avatar_place_holder));
            _layout_driver.setVisibility(View.VISIBLE);
            findViewById(R.id.LayoutRequest).setVisibility(View.GONE);

            IsAccepted = false;
        }

        timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    location location = ir.AralStudio.snapp.Grpc.TravelersService.location.newBuilder().setX(51.3775099).setY(35.7448416).build();
                    TravelersService = MetadataUtils.attachHeaders(TravelersService, header);
                    GetNearbyDriversResponse drivers = TravelersService.getNearbyDrivers(location);

                    for (int count = 0; count < drivers.getDriverCount(); count++) {

                        double lat, lon;
                        lon = drivers.getDriver(count).getLocation().getX();
                        lat = drivers.getDriver(count).getLocation().getY();

                        Marker tempMark = new Marker(map);
                        tempMark.setPosition(new GeoPoint(lat, lon));
                        tempMark.setAnchor(0.5f, 0.5f);
                        tempMark.setIcon(getDrawable(R.mipmap.driver_moppet));

                        map.getOverlays().add(tempMark);
                    }
                } catch (Exception e) {
                    //Toast.makeText(ShowMap.this, "ارتباط با سرور برقرار نشد...", Toast.LENGTH_LONG).show();
                }
            }
        }, 1000, 5000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        timer.cancel();
    }
}