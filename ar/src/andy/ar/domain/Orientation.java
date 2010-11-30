package andy.ar.domain;

public final class Orientation {

	public float azimuth = 0;
	public float pitch = 0;
	public float roll = 0;

	public Orientation() {
	}

	public Orientation(Orientation orientation) {
		azimuth = orientation.azimuth;
		pitch = orientation.pitch;
		roll = orientation.roll;
	}
}