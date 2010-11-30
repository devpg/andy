package andy.ar.domain;

import android.location.Location;

public class Poi {

	private final String name;
	private final Location location;


	public Poi(String name, Location location) {
		this.name = name;
		this.location = location;
	}

	public String getName() {
		return name;
	}

	public Location getLocation() {
		return location;
	}
}
