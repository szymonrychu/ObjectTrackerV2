package simple.as.fuck.objecttrackerv2.elements;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.SparseArray;
import android.view.View;

public class ControlsHelper{
	private Paint paint;
	private OnMultitouch listener;
	private class Pnt{
		int x;
		int y;
		int r;
		public Pnt() {}
		public Pnt(int x, int y){
			this.x=x;
			this.y=y;
		}
		public Pnt(int x, int y, int r){
			this.x=x;
			this.y=y;
			this.r=r;
		}
	}
	private Pnt base;
	private Pnt pivot = new Pnt();
	public ControlsHelper(View drawerView) {
		paint = new Paint();
		paint.setStrokeWidth(5.0f);
		listener = new OnMultitouch(drawerView);
		// TODO Auto-generated constructor stub
	}
	public void drawControls(Canvas canvas){
		SparseArray<Pointer> pointers = listener.getPoints();
		int w = canvas.getWidth();
		int h = canvas.getHeight();
		int d = canvas.getDensity();
		base = new Pnt(w/4,h/2,w/5);
		pivot = new Pnt(w/4,h/2,w/10);
		
		
		paint.setColor(Color.BLUE);
		canvas.drawCircle(w/4, h/2, w/5, paint);
		paint.setColor(Color.GREEN);
		Boolean moved = false;
		for(int c=0;c<pointers.size();c++){
			Pointer p = pointers.get(c);
			if(p.getDistance(w/4, h/2)<w/10){
				moved = true;
				
			}
		}
		if(! moved){
			canvas.drawCircle(w/4, h/2, w/10, paint);
		}
	}
}
