package simple.as.fuck.objecttrackerv2.OpenGLElements;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import simple.as.fuck.objecttrackerv2.R;
import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;
import static android.opengl.GLES20.*;
import static android.opengl.GLUtils.*;
import static android.opengl.Matrix.*;



public abstract class GLRenderer implements GLTextureView.Renderer{
	private Context context;
	private TextureShader texShader;
	private ColorShader colShader;
	private float[] projectionMatrix = new float[16];
	private float[] viewMatrix = new float[16];
	private float[] viewProjectionMatrix = new float[16];
	/////////////////////////////////////////////////////////
	public class TextureShader extends Shader{
		//Uniform locations
		private final int uMatrixLocation;
		private final int uTextureUnitLocation;

		//Attribute locations
		private final int aPositionLocation;
		private final int aTextureCoordinatesLocation;
		public TextureShader() {
			super(context, R.raw.texture_vertex_shader,R.raw.texture_fragment_shader);

			uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
			uTextureUnitLocation = glGetUniformLocation(program, U_TEXTURE_UNIT);

			aPositionLocation = glGetAttribLocation(program, A_POSITION);
			aTextureCoordinatesLocation = glGetAttribLocation(program, A_TEXTURE_COORDINATES);
		}
		public void setUniforms(float[] matrix, int textureId){
			//pass the matrix to shader program
			glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
			//set active texture as unit 0
			glActiveTexture(GL_TEXTURE0);
			//bind texture to unit 0
			glBindTexture(GL_TEXTURE_2D, textureId);
			//inform sampler to use texture in shader from unit 0
			glUniform1i(uTextureUnitLocation, 0);
		}
		public int getPositionAttributeLocation(){
			return aPositionLocation;
		}
		public int getTextureCoordinateAttributeLocation(){
			return aTextureCoordinatesLocation;
		}
	}
	public class ColorShader extends Shader{
		//Uniform locations
		private final int uMatrixLocation;
		private final int uColorLocation;

		//Attribute locations
		private final int aPositionLocation;

		public ColorShader() {
			super(context, R.raw.color_vertex_shader,R.raw.color_fragment_shader);

			uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
			uColorLocation = glGetUniformLocation(program, U_COLOR);

			aPositionLocation = glGetAttribLocation(program, A_POSITION);
		}
		public void setUniforms(float[] matrix, float r, float g, float b){
			//pass the matrix to shader program
			glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
			glUniform4f(uColorLocation, r, g, b, 1f);

		}
		public int getPositionAttributeLocation(){
			return aPositionLocation;
		}
	}
	/////////////////////////////////////////////////////////
	/**
	 * 
	 */
	public abstract void initObjects();
	/**
	 * 
	 */
	public abstract void drawObjects(TextureShader texProgram, ColorShader colProgram);
	////////////////////////////////////////////////////////
	
	public GLRenderer(Context context){
		this.context = context;
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		texShader = new TextureShader();
		colShader = new ColorShader();
		initObjects();
		
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		glViewport(0, 0, width, height);
		final float aspectRatio = width > height ? 
				(float) width / (float) height : 
				(float) height / (float) width;
		if(width > height){
			orthoM(projectionMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f);
		}else{
			orthoM(projectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, -1f, 1f); 
		}
		setLookAtM(viewMatrix,0, 0f, 1.2f, 2.2f, 0f, 0f, 0f, 0f, 1f, 0f);
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		glClear(GL_COLOR_BUFFER_BIT);
		multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
		drawObjects(texShader, colShader);
	}

}
