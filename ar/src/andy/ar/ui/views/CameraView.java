package andy.ar.ui.views;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

class CameraView extends SurfaceView {

	static final float X_ANGEL_WIDTH = 29;
	static final float X_HALF_ANGEL_WIDTH = X_ANGEL_WIDTH / 2;

	private Camera camera;
	private final SurfaceHolder previewHolder;

	public CameraView(Context context) {
		this(context, null);
	}

	public CameraView(final Context context, AttributeSet attributes) {
		super(context, attributes);

		previewHolder = this.getHolder();
		previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		previewHolder.addCallback(new SurfaceHolder.Callback() {
			public void surfaceCreated(SurfaceHolder holder) {
				camera = Camera.open();

				try {
					camera.setPreviewDisplay(previewHolder);
				} catch (Throwable e) {
					Log.e(CameraView.class.getName(), e.getMessage());
				}
			}

			public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
				 Parameters params = camera.getParameters();
				 params.setPreviewSize(width, height);
				 camera.setParameters(params);
				 camera.startPreview();
			}

			public void surfaceDestroyed(SurfaceHolder arg0) {
				camera.stopPreview();
				camera.release();
			}
		});
	}
}