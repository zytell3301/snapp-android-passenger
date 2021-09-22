package ir.AralStudio.snapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.List;

public class ShowMap extends AppCompatActivity {

    private MapView map;
    private Button btnMarkerInsert;
    private IMapController mapController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        setContentView(R.layout.activity_show_map);

        Dexter.withContext(getApplicationContext()).withPermissions(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

                    }
                }).check();


        btnMarkerInsert = findViewById(R.id.btnMarkerInsert);
        map = findViewById(R.id.mapView);

        mapController = map.getController();
        mapController.setZoom((long)15);
        map.setTilesScaledToDpi(true);
        /*needs to be checked*/
        //map.getZoomController().activate();


        map.setMultiTouchControls(true);
        GeoPoint pointStart = new GeoPoint(35.7448416,51.3775099,17);
        GeoPoint pointEnd = new GeoPoint(35.7423246,51.3819979,15.88);
        mapController.setCenter(pointStart);

        btnMarkerInsert.setText("انتخاب مبدا");
        Marker markerStart = new Marker(map);
        Marker markerEnd = new Marker(map);


        btnMarkerInsert.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View view) {
                markerStart.setPosition(pointStart);
                markerStart.setTextLabelFontSize(30);
                markerStart.setIcon(AppCompatResources.getDrawable(ShowMap.this,R.mipmap.map_origin));
                map.getOverlays().add(markerStart);

                btnMarkerInsert.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        markerEnd.setPosition(pointEnd);
                        markerEnd.setTextLabelFontSize(30);
                        markerEnd.setIcon(AppCompatResources.getDrawable(ShowMap.this,R.mipmap.map_destination));
                        map.getOverlays().add(markerEnd);
                    }
                });
            //map.requestFocus();
            }
        });
        //map.requestFocus();
    }
}