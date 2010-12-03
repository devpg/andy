package andy.ar.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;
import andy.ar.R;
import andy.ar.domain.Orientation;
import andy.ar.domain.Poi;
import andy.ar.services.LocationTrackingService;
import andy.ar.services.OrientationTrackingService;
import andy.ar.services.LocationTrackingService.LocationHandler;
import andy.ar.services.OrientationTrackingService.OrientationHandler;
import andy.ar.ui.views.PoiMarker;
import andy.ar.ui.views.PoiView;

public abstract class PoiActivity extends Activity implements OrientationHandler, LocationHandler {

	private PoiView view;
	private OrientationTrackingService orientationService;
	private LocationTrackingService locationService;
	
	private final int updateTime;
	private final int updateDistance;
	private final float smoothingFilter;

	public PoiActivity(int updateTime, int updateDistance, float smoothingFilter) {
		this.updateTime = updateTime;
		this.updateDistance = updateDistance;
		this.smoothingFilter = smoothingFilter;
	}
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.poi_activity);

		view = (PoiView) findViewById(R.id.poiView);
		view.setOnInterestPointClickListener(new PoiView.ClickListener() {

			public void onClick(Poi poi) {
				Toast.makeText(PoiActivity.this, poi.getName(), 3000);
			}
		});
		orientationService = new OrientationTrackingService(this, this, smoothingFilter);
		locationService = new LocationTrackingService(this, this, updateTime, updateDistance);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
		orientationService.stop();
		locationService.stop();
	}

	@Override
	protected void onResume() {
		super.onResume();
		locationService.start();
	}

	public void onOrientationChanged(Orientation orientation) {
		view.setOrientation(orientation);
	}

	public void onSensorDisabled(String provider) {
		Toast.makeText(this, "GPS unavailable", 3000);
	}

	@Override
	public void onLocationChanged(Location location) {
		// optimization: do not start orientation tracking until location is detected 
		if(!orientationService.isStarted()) {
			orientationService.start();
		}
		view.setViewpoint(location);
	}

	@Override
	public void onTrackingUnavailable() {
		Toast.makeText(this, "Orientation tracking unavailable", 3000);
	}
	
	protected void setPois(List<Poi> pois) {
		List<PoiMarker> markers = new ArrayList<PoiMarker>();
		for(Poi poi : pois) {
			markers.add(createPoiMarker(poi));
		}
		view.setPoiMarkers(markers);
	}
	
	protected PoiMarker createPoiMarker(Poi poi) {
		return new PoiMarker(poi);
	}
	
}