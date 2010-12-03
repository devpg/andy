package andy.ar.services;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.util.Log;
import andy.ar.domain.Orientation;

abstract class OrientationTracking {

	private final float smoothingFilter;
	private Orientation filteredOrientation;

	public OrientationTracking(float smoothingFilter) {
		this.smoothingFilter = smoothingFilter;
	}

	private final float smooth(float base, float value) {
		if (Math.abs(base - value) > 100) {
			if (value > 180) {
				return 360f;
			}
			return 0f;
		}
		return (float) ((value * smoothingFilter) + (base * (1.0 - smoothingFilter)));
	}

	public final Orientation onOrientationChanged(SensorEvent event) {
		Orientation sensorOrientation = handleSensorEvent(event);

		if (filteredOrientation == null) {
			filteredOrientation = new Orientation(sensorOrientation);
		} else {
			filteredOrientation.azimuth = smooth(filteredOrientation.azimuth, sensorOrientation.azimuth);
			filteredOrientation.pitch = smooth(filteredOrientation.pitch, sensorOrientation.pitch);
			filteredOrientation.roll = smooth(filteredOrientation.roll, sensorOrientation.roll);
		}
		return filteredOrientation;
	}

	protected abstract Orientation handleSensorEvent(SensorEvent event);

	public abstract int[] getRequiredSensorTypes();

}

class CombinedSensorOrientationTracking extends OrientationTracking {

	private static final int matrix_size = 16;
	private float[] rotation = new float[matrix_size];
	private float[] landscapeRotation = new float[matrix_size];
	private float[] inclination = new float[matrix_size];
	private float[] deviceOrientation = new float[3];
	private float[] magnetic;
	private float[] gravity;

	private final Orientation orientation = new Orientation();

	public CombinedSensorOrientationTracking(float smoothingFilter) {
		super(smoothingFilter);
	}

	protected Orientation handleSensorEvent(SensorEvent event) {
		Sensor sensor = event.sensor;
		int type = sensor.getType();

		switch (type) {
		case Sensor.TYPE_MAGNETIC_FIELD:
			magnetic = event.values;
			break;
		case Sensor.TYPE_ACCELEROMETER:
			gravity = event.values;
			break;
		}

		if (magnetic != null && gravity != null) {
			SensorManager.getRotationMatrix(rotation, inclination, gravity, magnetic);
			SensorManager.remapCoordinateSystem(rotation, SensorManager.AXIS_X, SensorManager.AXIS_Z, landscapeRotation);
			SensorManager.getOrientation(landscapeRotation, deviceOrientation);

			orientation.azimuth = (float) Math.toDegrees(deviceOrientation[0]);
			orientation.pitch = (float) Math.toDegrees(deviceOrientation[1]);
			orientation.roll = (float) Math.toDegrees(deviceOrientation[2]);

			Log.d("andy", "Combined sensor tracking: " + orientation.azimuth + ", " + orientation.pitch + ", " + orientation.roll);

			magnetic = null;
			gravity = null;
		}
		return orientation;
	}

	public int[] getRequiredSensorTypes() {
		return new int[] { Sensor.TYPE_MAGNETIC_FIELD, Sensor.TYPE_ACCELEROMETER };
	}
}

class SingleSensorOrientationTracking extends OrientationTracking {

	private final Orientation orientation = new Orientation();

	public SingleSensorOrientationTracking(float smoothingFilter) {
		super(smoothingFilter);
	}

	@Override
	public int[] getRequiredSensorTypes() {
		return new int[] { Sensor.TYPE_ORIENTATION };
	}

	@Override
	protected Orientation handleSensorEvent(SensorEvent event) {
		orientation.azimuth = (event.values[0] + 90) % 360;
		orientation.pitch = event.values[1];
		orientation.roll = event.values[2];

		Log.d("andy", "Single sensor tracking: " + orientation.azimuth + ", " + orientation.pitch + ", " + orientation.roll);
 
		return orientation;
	}

}