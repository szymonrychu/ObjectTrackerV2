package simple.as.fuck.objecttrackerv2.OpenGLElements;

import android.content.Context;
import static android.opengl.GLES20.*;


public abstract class Shader{
	protected static final String U_MATRIX = "u_Matrix";
	protected static final String U_TEXTURE_UNIT = "u_TextureUnit";
	protected static final String U_COLOR = "u_Color";
	protected static final String A_POSITION = "a_Position";
	protected static final String A_COLOR = "a_Color";
	protected static final String A_TEXTURE_COORDINATES = "a_TextureCoordinates";
	
	protected final int program;
	
	public Shader(Context context, int vertexShaderReosurceID, int fragmentShaderResourceID){
	String vertexShaderSource = Tools.textFromRawRes(context, vertexShaderReosurceID);
	String fragmentShaderSource = Tools.textFromRawRes(context, fragmentShaderResourceID);
	this.program = Tools.buildProgram(vertexShaderSource, fragmentShaderSource);
	}
	public void useShader(){
		glUseProgram(program);
	}
			
}