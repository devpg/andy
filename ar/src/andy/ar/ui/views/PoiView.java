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
	private float maxDistance;
	private Orientation orientation;
	private List<PoiMarking> markings = new ArrayList<PoiMarking>();
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
		
		if(orientation == null || markings == null || markings.size() == 0) {
			return;
		}
		
		float leftArm = orientation.azimuth - (CameraView.X_HALF_ANGEL_WIDTH);
		float rightArm = orientation.azimuth + (CameraView.X_HALF_ANGEL_WIDTH);
		if (leftArm < 0)
			leftArm += 360;
		if (rightArm > 360)
			rightArm -= 360;

		for (PoiMarking marking: markings) {
			//marking.draw(canvas, leftArm, rightArm, getPoiMarkingTopPosition(canvas, marking));
			marking.draw(canvas, leftArm, rightArm, 30);
		}
	}
	
	/*
	 * Calculates the position of the poi marking from the top of
	 * the canvas. The position is related to the poi distance.
	 */
	private int getPoiMarkingTopPosition(Canvas canvas, PoiMarking marking) {
		float offset = marking.getDistance() / maxDistance;
		offset = Math.min(offset, (float) 0.95);
		return (int) (offset * canvas.getHeight());
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (clickListener != null && markings != null && event.getAction() == MotionEvent.ACTION_DOWN) {
			for (int i = markings.size(); i > 0;) {
				PoiMarking marking = markings.get(--i);
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
		
		if(markings == null) {
			return;
		}
		for(PoiMarking marking : markings) {
			marking.setViewpoint(location);
		}
	}

	public void setPois(List<Poi> pois) {
		markings.clear();
		
		// determine max distance
		maxDistance = 0;
		for(Poi poi : pois) {
			PoiMarking marking = new PoiMarking(poi, viewpoint);
			markings.add(marking);
			
			if(marking.getDistance() > maxDistance) {
				maxDistance = marking.getDistance();
			}
		}
	}
}