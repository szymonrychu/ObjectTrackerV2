package simple.as.fuck.objecttrackerv2.OpenGLElements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.content.res.Resources;

import static android.opengl.GLES20.*;

public class Tools {
	public static final int BYTES_PER_FLOAT = 4;
	public static String textFromRawRes(Context context, int resID){
		StringBuilder body = new StringBuilder();
		try {
			InputStream input = context.getResources().openRawResource(resID);
			InputStreamReader reader = new InputStreamReader(input);
			BufferedReader buffReader = new BufferedReader(reader);
			String line;
			while((line = buffReader.readLine()) != null){
				body.append(line);
				body.append("\n");
			}
		}catch(IOException e){
			throw new RuntimeException("Could not find resource: "+resID+"!",e);
		}catch(Resources.NotFoundException nfe){
			throw new RuntimeException("Resource not found: "+resID+"!",nfe);
		}
		return body.toString();
	}
	public static int compileShader(int type, String shaderCode){
		final int shader = glCreateShader(type);
		if(shader == 0){
			return 0;
		}
		glShaderSource(shader, shaderCode);
		glCompileShader(shader);
		final int[] compileStatus = new int[1];
		glGetShaderiv(shader, GL_COMPILE_STATUS, compileStatus, 0 );
		if(compileStatus[0] == 0){
			glDeleteShader(shader);
			return 0;
		}
		return shader;
	}
	public static int linkProgram(int glVertexShader, int glFragmentShader){
		final int program = glCreateProgram();
		if(program == 0){
			return 0;
		}
		glAttachShader(program, glVertexShader);
		glAttachShader(program, glFragmentShader);
		glLinkProgram(program);
		final int[] linkStatus = new int[1];
		glGetProgramiv(program, GL_LINK_STATUS, linkStatus, 0);
		if(linkStatus[0] == 0){
			glDeleteProgram(program);
			return 0;
		}
		return program;
	}
	public static int buildProgram(String vertexShaderSource,String fragmentShaderSource){

		int vertexShader = compileShader(GL_VERTEX_SHADER, vertexShaderSource);
		int fragmentShader = compileShader(GL_FRAGMENT_SHADER, fragmentShaderSource);
		int program = linkProgram(vertexShader, fragmentShader);
		//validateProgram(program);
		return program;

	}
	public static boolean validateProgram(int glProgramId){
		glValidateProgram(glProgramId);
		final int[] validateStatus = new int[1];
		glGetProgramiv(glProgramId, GL_VALIDATE_STATUS, validateStatus, 0);
		return validateStatus[0] != 0;
	}
	public static void perspectiveM(float[] m,float yFovInDegrees, float aspect, float n, float f){
		final float angleInRadians= (float) Math.toRadians(yFovInDegrees);
		final float A = (float) (1.0 / Math.tan(angleInRadians/2.0));
		for(int x=0;x<16;x++){
			m[x]=0f;
		}
		m[0]=A/aspect;
		m[5]=A;
		m[10]=-((f+n)/(f-n));
		m[11] = -1f;
		m[14]=-((2f*f*n)/(f-n));
	}
}
