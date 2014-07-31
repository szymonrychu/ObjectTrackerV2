package simple.as.fuck.objecttrackerv2.OpenGLElements;
import android.content.Context;
import android.os.Bundle;
import simple.as.fuck.objecttrackerv2.R;
import simple.as.fuck.objecttrackerv2.elements.FullScreenActivity;

public class OpenGLActivity extends FullScreenActivity {
	GLRenderer renderer;
	GLTextureView glTextureView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		renderer = new GLRenderer(this);
		glTextureView = new GLTextureView(this);
		glTextureView.setEGLContextClientVersion(3);

		glTextureView.setRenderer(renderer);
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
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
