package simple.as.fuck.objecttrackerv2.recognizer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import simple.as.fuck.objecttrackerv2.R;
import simple.as.fuck.objecttrackerv2.R.id;
import simple.as.fuck.objecttrackerv2.R.layout;
import simple.as.fuck.objecttrackerv2.R.menu;
import simple.as.fuck.objecttrackerv2.elements.CameraDrawerPreview;
import simple.as.fuck.objecttrackerv2.elements.CameraDrawerPreview.CameraProcessingCallback;
import simple.as.fuck.objecttrackerv2.elements.CameraDrawerPreview.CameraSetupCallback;
import simple.as.fuck.objecttrackerv2.elements.Constants;
import simple.as.fuck.objecttrackerv2.elements.FullScreenActivity;
import simple.as.fuck.objecttrackerv2.elements.OfflineDataHelper;
import simple.as.fuck.objecttrackerv2.elements.Pointer;
import simple.as.fuck.objecttrackerv2.elements.ResolutionDialog;
import simple.as.fuck.objecttrackerv2.geomerty.Tag;
import simple.as.fuck.objecttrackerv2.natUtils.Misc;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.RotateAnimation;
import android.widget.Button;

public class RecognizeActivity extends FullScreenActivity implements CameraSetupCallback, CameraProcessingCallback{
	private final static String TAG=RecognizeActivity.class.getSimpleName();
	private CameraDrawerPreview preview = null;
	private Camera.Parameters params = null;
	private Recognizer recognizer;
	private OfflineDataHelper helper;
	private Bitmap tmp;
	private int rotation=0;
	private int camWidth, camHeight;
	private Mat cameraMatrix, distortionMatrix;
	private Boolean showPreview = false;
	private Tag[] tags;
	private Paint paint;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_recognize);
        preview = (CameraDrawerPreview) findViewById(R.id.recognize_preview);
        preview.setCameraSetupCallback(this);
        preview.setCameraProcessingCallback(this);
		helper = new OfflineDataHelper(this);
		Button screenshotButton = (Button) findViewById(R.id.screenshot_button);
		screenshotButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Bitmap bmp = preview.getScreenShot();
				SimpleDateFormat s = new SimpleDateFormat("ddMMyyyyhhmmss",Locale.getDefault());
				String date = s.format(new Date());
				helper.saveScreenshot(bmp, "screenshot-"+date+".png");
			}
		});
		cameraMatrix = helper.loadCameraMatrix();
		distortionMatrix = helper.loadDistortionMatrix();
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(Color.GREEN);
		paint.setStrokeWidth(7.0f);
		paint.setTextSize(25.0f);
		super.onCreate(savedInstanceState);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.recognizer, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.recognize_action_resolution) {
			params = preview.getCameraParameters();
			ResolutionDialog resolutionDialog = new ResolutionDialog(params.getSupportedPreviewSizes()) {
				@Override
				public void onListItemClick(DialogInterface dialog, Camera.Size size) {
					params.setPreviewSize(size.width, size.height);
					preview.reloadCameraSetup(params);
					recognizer.notifySizeChanged(size, rotation);
					helper.setResolution(size);
				}
			};
			resolutionDialog.show(getFragmentManager(), "resolutions");
			
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	protected void onSystemBarsVisible() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onSystemBarsHided() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void processImage(Mat yuvFrame, Thread thiz) {
		// TODO Auto-generated method stub
		if(showPreview){
			tmp = recognizer.remapFrame(yuvFrame);
		}
		tags = recognizer.findTags(yuvFrame, rotation);
		preview.requestRefresh();
	}
	@Override
	public void drawOnCamera(Canvas canvas, double scaleX, double scaleY) {
		if(showPreview){
			canvas.drawBitmap(tmp,0, 0, new Paint());
		}
		if(tags!=null){
			int x=0;
			int y=0;
			for(Tag tag : tags){
				int X=0;
				int Y=0;
				for(int c=0;c<4;c++){
					canvas.drawLine(tag.points[c].x, tag.points[c].y, tag.points[(c+1)%4].x, tag.points[(c+1)%4].y, paint);
					X+=tag.points[c].x;
					Y+=tag.points[c].y;
				}
				canvas.drawText(""+tag.id, X/4, Y/4, paint);
				canvas.drawBitmap(Misc.mat2Bitmap(tag.preview), x, y , paint);
				x+=tag.preview.cols();
			}
		}
	}
	@Override
	public void setCameraParameters(Parameters params, int width, int height, int rotation) {
		Log.v(TAG,"params.getPreviewSize()=w:"+params.getPreviewSize().width+":h:"+params.getPreviewSize().height);
		recognizer.notifySizeChanged(params.getPreviewSize(), rotation);
	}
	@Override
	public void setCameraInitialParameters(Parameters params, int width, int height, int rotation) {
		camWidth = helper.getResolutionWidth(params.getPreviewSize().width);
		camHeight = helper.getResolutionHeight(params.getPreviewSize().height);
		params.setPreviewSize(camWidth, camHeight);
		recognizer = new Recognizer(cameraMatrix,distortionMatrix, width, height);
		recognizer.notifySizeChanged(params.getPreviewSize(), rotation);
		
	}
	@Override
	public void getPointers(SparseArray<Pointer> pointers) {
		// TODO Auto-generated method stub
		
	}
	
}
