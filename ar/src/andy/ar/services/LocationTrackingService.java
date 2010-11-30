package andy.ar.services;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class LocationTrackingService implements LocationListener {

	public interface LocationHandler {

		public void onLocationChanged(Location location);

		public void onSensorDisabled(String provider);
	}

	private final LocationHandler callbackHandler;
	private final LocationManager locationSensor;
	private final int updateTime;
	private final int updateDistance;

	public LocationTrackingService(Context context, LocationHandler handler, int updateTime, int updateDistance) {
		this.callbackHandler = handler;
		this.locationSensor = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		this.updateTime = updateTime;
		this.updateDistance = updateDistance;
	}

	public void start() {
		locationSensor.requestLocationUpdates(LocationManager.GPS_PROVIDER, updateTime, updateDistance, this);
		locationSensor.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	}

	public void stop() {
		locationSensor.removeUpdates(this);
	}

	public void onLocationChanged(final Location location) {
		callbackHandler.onLocationChanged(location);
	}

	public void onProviderDisabled(String provider) {
		callbackHandler.onSensorDisabled(provider);
	}

	public void onProviderEnabled(String provider) {
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
	}
}