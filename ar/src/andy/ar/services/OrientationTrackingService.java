package andy.ar.services;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import andy.ar.domain.Orientation;

public class OrientationTrackingService implements SensorEventListener {

	public interface OrientationHandler {

		public void onOrientationChanged(Orientation direction);

		public void onTrackingUnavailable();
	}

	private OrientationTracking trackingHandler;
	private final OrientationHandler callbackHandler;

	private final SensorManager sensorManager;
	private List<Sensor> sensors;

	public OrientationTrackingService(Context context, OrientationHandler handler, float smoothingFilter) {
		this.callbackHandler = handler;
		this.sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

		Log.d("andy", "Initialize tracking");
		if (!initTracking(new CombinedSensorOrientationTracking(smoothingFilter))) {
			Log.d("andy", "Sensors for combined orientation tracking unavailable");

			if (!initTracking(new SingleSensorOrientationTracking(smoothingFilter))) {
				Log.d("andy", "Sensor for single orientation tracking unavailable");
				callbackHandler.onTrackingUnavailable();
			}
		}
	}

	private final boolean initTracking(OrientationTracking trackingHandler) {
		int[] requiredSensors = trackingHandler.getRequiredSensorTypes();
		List<Sensor> sensors = getSensors(requiredSensors);

		if (sensors.size() == requiredSensors.length) {
			this.trackingHandler = trackingHandler;
			this.sensors = sensors;
			return true;
		}
		return false;
	}

	private final List<Sensor> getSensors(int[] sensorTypes) {
		List<Sensor> sensors = new ArrayList<Sensor>();
		for (Sensor sensor : sensorManager.getSensorList(Sensor.TYPE_ALL)) {
			for (int sensorType : sensorTypes) {
				if (sensorType == sensor.getType()) {
					sensors.add(sensor);
					break;
				}
			}
		}
		return sensors;
	}

	public void start() {
		for (Sensor sensor : sensors) {
			sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
		}
	}

	public void stop() {
		for (Sensor sensor : sensors) {
			sensorManager.unregisterListener(this, sensor);
		}
	}

	public void onAccuracyChanged(Sensor arg0, int arg1) {
	}

	public void onSensorChanged(SensorEvent event) {
		callbackHandler.onOrientationChanged(trackingHandler.onOrientationChanged(event));
	}
}