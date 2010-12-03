package andy.ar.ui.views;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.location.Location;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import andy.ar.domain.Poi;

public class PoiMarker implements OnTouchListener {

	private static final Paint markerPaint = new Paint();
	static {
		markerPaint.setColor(Color.WHITE);
	}

	private static final Paint textPaint = new Paint();
	static {
		textPaint.setColor(Color.BLUE);
	}
	private static final int border = 2;
	private static final int textWidth = 100;

	final Poi poi;
	private Rect marker;
	private Float azimuth;
	private Float distance;

	public PoiMarker(final Poi poi) {
		this.poi = poi;
	}
	
	public void draw(Canvas canvas, float leftArm, float rightArm) {
		// optimization: do not continue when no viewpoint is set
		if(azimuth == null || distance == null) {
			return;
		}
		
		int leftPosition = getLeft(leftArm, rightArm, canvas.getWidth());
		int topPosition = 30;

		canvas.drawLine(leftPosition, 0, leftPosition, canvas.getHeight(), markerPaint);
		
		// if description would be out of viewport show this left aligned
		if (canvas.getWidth() - leftPosition < textWidth) {
			leftPosition -= textWidth;
		}
		marker = new Rect(leftPosition, topPosition, leftPosition + textWidth, topPosition + 30);
		canvas.drawRect(marker, markerPaint);

		// add border for internal description
		topPosition -= border;
		leftPosition -= border;
		canvas.drawText(clipTitle(poi.getName()), leftPosition + 5, topPosition + 15, textPaint);
		canvas.drawText(distance + "m", leftPosition + 5, topPosition + 25, textPaint);
	}
	
	private String clipTitle(String title) {
		if (title == null || title.length() < 15) {
			return title;
		}
		return title.substring(0, 13) + "...";
	}

	private int getLeft(float leftArm, float rightArm, int width) {
		float offset;

		if (leftArm > rightArm) {
			if (azimuth >= leftArm) {
				offset = azimuth - leftArm;
			}
			if (azimuth <= rightArm) {
				offset = 360 - leftArm + azimuth;
			} else
				offset = azimuth - leftArm;
		} else {
			offset = azimuth - leftArm;
		}

		return (int) ((offset / CameraView.X_ANGEL_WIDTH) * width);
	}

	/**
	 * Checks whether motion event touches visualized marker of interest point
	 * or not
	 */
	public boolean onTouch(View v, MotionEvent event) {
		if (marker != null && marker.contains((int) event.getX(), (int) event.getY())) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	
	public void setViewpoint(Location viewpoint) {
		azimuth = viewpoint.bearingTo(poi.getLocation());
		if (azimuth < 0) {
			azimuth += 360;
		}
		distance = viewpoint.distanceTo(poi.getLocation());
	}
}
