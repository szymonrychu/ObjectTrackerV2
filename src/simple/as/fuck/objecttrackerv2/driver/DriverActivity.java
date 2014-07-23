package simple.as.fuck.objecttrackerv2.driver;

import java.util.ArrayList;
import java.util.List;

import simple.as.fuck.objecttrackerv2.R;
import simple.as.fuck.objecttrackerv2.R.id;
import simple.as.fuck.objecttrackerv2.R.layout;
import simple.as.fuck.objecttrackerv2.R.menu;
import simple.as.fuck.objecttrackerv2.elements.ControlsHelper;
import simple.as.fuck.objecttrackerv2.elements.FullScreenActivity;
import simple.as.fuck.objecttrackerv2.elements.OnMultitouch;
import simple.as.fuck.objecttrackerv2.elements.Pointer;
import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnGenericMotionListener;
import android.view.View.OnTouchListener;

public class DriverActivity extends FullScreenActivity implements Callback, Runnable{

	private SurfaceView driverView;
	private SurfaceHolder holder;
	private Boolean draw = false;
	private Thread refreshThread;
	private int time;
	private int refreshMs;
	private Paint paint;
	private ControlsHelper controlsHelper;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_driver);
		driverView = (SurfaceView) findViewById(R.id.driver_driverView);
		holder = driverView.getHolder();
		holder.addCallback(this);
		refreshThread = new Thread(this);
		refreshMs = 10;
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStrokeWidth(100.0f);
		controlsHelper = new ControlsHelper(driverView);
	}
	void drawControls(Canvas canvas){
		
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.driver, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		draw = true;
		if(refreshThread.isAlive()){
			try {
				refreshThread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		refreshThread = new Thread(this);
		refreshThread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		draw = false;
		try {
			refreshThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void redraw(Canvas canvas){
		controlsHelper.drawControls(canvas);
		/*
		SparseArray<Pointer> pointers = listener.getPoints();
		for(int c=0;c<pointers.size();c++){
			Pointer p = pointers.valueAt(c);
			float color[] = {(360*(float)p.id/5)%360, 1.0f, 1.0f};
			paint.setColor(Color.HSVToColor(color));
			canvas.drawPoint(p.x, p.y, paint);
		}*/
	}
	@Override
	public void run() {
		while(draw){
			Canvas canvas=holder.lockCanvas();
			if(canvas!=null)
					synchronized(canvas){
					canvas.drawColor(Color.WHITE, Mode.CLEAR);
					redraw(canvas);
					holder.unlockCanvasAndPost(canvas);
			}
			try {
				Thread.sleep(refreshMs);
			} catch (InterruptedException e) {}
		}
	}


	@Override
	protected void onSystemBarsVisible() {
		// TODO Auto-generated method stub
		
	}


	@Override
	protected void onSystemBarsHided() {
		// TODO Auto-generated method stub
		
	}
}
