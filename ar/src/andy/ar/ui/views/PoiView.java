package andy.ar.ui.views;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import andy.ar.domain.Orientation;
import andy.ar.domain.Poi;

public final class PoiView extends FrameLayout implements OnTouchListener {

	public interface ClickListener {

		public void onClick(Poi interestPoint);
	}
	
	private static final Paint textPaint = new Paint();
	static {
		textPaint.setColor(Color.WHITE);
	}
	
	private ClickListener clickListener;
	private Orientation orientation;
	private List<PoiMarker> markers = new ArrayList<PoiMarker>();
	private Location viewpoint;

	public PoiView(Context context) {
		this(context, null);
	}
	
	public PoiView(Context context, AttributeSet attributes) {
		super(context, attributes);
		setOnTouchListener(this);
	}
	
	public void setOnInterestPointClickListener(ClickListener clickListener) {
		this.clickListener = clickListener;		
	}
	
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		
		if(orientation == null || markers == null || markers.size() == 0) {
			return;
		}
		
		float leftArm = orientation.azimuth - (CameraView.X_HALF_ANGEL_WIDTH);
		float rightArm = orientation.azimuth + (CameraView.X_HALF_ANGEL_WIDTH);
		if (leftArm < 0)
			leftArm += 360;
		if (rightArm > 360)
			rightArm -= 360;

		for (PoiMarker marking: markers) {
			marking.draw(canvas, leftArm, rightArm);
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (clickListener != null && markers != null && event.getAction() == MotionEvent.ACTION_DOWN) {
			for (int i = markers.size(); i > 0;) {
				PoiMarker marking = markers.get(--i);
				if (marking.onTouch(v, event)) {
					clickListener.onClick(marking.poi);
					return Boolean.TRUE;
				}
			}
		}
		return Boolean.FALSE;
	}

	public void setOrientation(Orientation orientation) {
		this.orientation = orientation;
		postInvalidate();
	}
	
	public void setViewpoint(Location location) {
		viewpoint = location;
		setViewpointInternal();
	}
	
	public void setPoiMarkers(List<PoiMarker> pois) {
		markers = pois;
		setViewpointInternal();
	}
	
	private void setViewpointInternal() {
		if(markers == null) {
			return;
		}
		for(PoiMarker marking : markers) {
			marking.setViewpoint(viewpoint);
		}
	}

	
}