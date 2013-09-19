package com.gandan.opengl.practice;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.opengl.GLES20;

public class Circle {
	private final String vertexShaderCode = 
			"uniform mat4 uMVPMatrix;" +
			"attribute vec4 vPosition;" + 
			"void main(){"+
			"    gl_Position = vPosition* uMVPMatrix;" +
			"}";
	private final String fragmentShaderCode = 
			"precision mediump float;" +
			"uniform vec4 vColor;" +
			"void main() {" +
			"    gl_FragColor = vColor;" +
			"}";

	private int rad;
	private float radius = 0.5f;

	private float[] circleCoords;	// add one center point
	private short[] drawOrder;
	
	private FloatBuffer vertexBuffer;
	private ShortBuffer drawListBuffer;
	
	int mProgram;
	int mPositionHandle;
	int mColorHandle;
	int COORDS_PER_VERTEX = 3;
	int vertexStride = COORDS_PER_VERTEX * 4;
	int mMVPMatrixHandle;
	int mvpMatrix;
	
	// Set color with red, green, blue and alpha (opacity) values
	float color[] = { 0.63671875f, 0.36953125f, 0.32265625f, 1.0f };
	
	public Circle(int rad) {
		this.rad = rad; // angle		
		this.circleCoords = new float[rad*3+3];	// we need three element for x,y,and z. Extra three elements are needed for 0,0,0 point		
		this.drawOrder = new short[rad*3+1];	// save draworder for GL_TRIANGLES
		
		// setup vertex shader and fragment shader
		int vertexShader = MyRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
		int fragmentShader = MyRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
		
		mProgram = GLES20.glCreateProgram();
		GLES20.glAttachShader(mProgram, vertexShader);
		GLES20.glAttachShader(mProgram, fragmentShader);
		GLES20.glLinkProgram(mProgram);
		
		// naive implementation
		
		circleCoords[0] = 0.0f;
		circleCoords[1] = 0.0f;
		circleCoords[2] = 0.0f;
 		for(int t=3; t<rad*3; t+=3) { // 0-360 degree
			circleCoords[t] = (float) Math.cos((double)(t/3)*3.1415926d/180.0d) * radius;
			circleCoords[t+1]= (float)Math.sin((double)(t/3)*3.1415926d/180.0d) * radius;
			circleCoords[t+2] = 0.0f;
		}
		ByteBuffer bb = ByteBuffer.allocateDirect(
				circleCoords.length * 
				4 /* 4 bytes for float*/ 
		);
		bb.order(ByteOrder.nativeOrder());

 		
 		for( int t=0, j=1; t<rad*3; t+=3, j++){
 			drawOrder[t] = 0;
 			drawOrder[t+1] = (short)j;
 			drawOrder[t+2] = (short)((j+1) == 360?1:(j+1));
 		}
		// initialize byte buffer for the draw list
		ByteBuffer dlb = ByteBuffer.allocateDirect(
				// (# of coordinate values * 2 bytes per short)
				drawOrder.length * 2);
		dlb.order(ByteOrder.nativeOrder());
		
		vertexBuffer = bb.asFloatBuffer();
		vertexBuffer.put(circleCoords);
		vertexBuffer.position(0);
		
		drawListBuffer = dlb.asShortBuffer();
		drawListBuffer.put(drawOrder);
		drawListBuffer.position(0);
	}
	
	public void draw() {
		
		GLES20.glUseProgram(mProgram);
		
		mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
		
		GLES20.glEnableVertexAttribArray(mPositionHandle);
		
		GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, 
				GLES20.GL_FLOAT, false,
				vertexStride, vertexBuffer);
		
		mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
		
		GLES20.glUniform4fv(mColorHandle, 1, color, 0);
		
		// Draw the circle
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length, 
				GLES20.GL_UNSIGNED_SHORT, drawListBuffer);
		
		GLES20.glDisableVertexAttribArray(mPositionHandle);
	}
	
	public void draw(float[] mvpMatrix){
		GLES20.glUseProgram(mProgram);
		
		mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
		
		GLES20.glEnableVertexAttribArray(mPositionHandle);
		
		GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, 
				GLES20.GL_FLOAT, false,
				vertexStride, vertexBuffer);
		
		mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
		
		GLES20.glUniform4fv(mColorHandle, 1, color, 0);
		
		// get handle to shape's transformation matrix
		mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
		
		// Apply the projection and view tranformation
		GLES20.glUniformMatrix4fv(mMVPMatrixHandle,1, false, mvpMatrix, 0);
		
		// Draw the square
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length, 
				GLES20.GL_UNSIGNED_SHORT, drawListBuffer);
		
		GLES20.glDisableVertexAttribArray(mPositionHandle);
	}

}
