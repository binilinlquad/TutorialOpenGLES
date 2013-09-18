package com.gandan.opengl.practice;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.opengl.GLES20;

public class Square {

	private final String vertexShaderCode = 
			"uniform mat4 uMVPMatrix;" +
			"attribute vec4 vPosition;" +
		    "void main() {" +
			"  gl_Position = vPosition * uMVPMatrix;" +
		    "}";
	private final String fragmentShaderCode =
			"precision mediump float;" +
	        "uniform vec4 vColor;" +
		    "void main() {" +
	        "  gl_FragColor = vColor;" +
		    "}";
	
	private FloatBuffer vertexBuffer;
	private ShortBuffer drawListBuffer;
	
	// number of coordinates per vertex in this array
	static final int COORDS_PER_VERTEX = 3;
	static float squareCoords[] = { -0.5f, 0.5f, 0.0f,  // left top
									-0.5f, -0.5f, 0.0f, // left bottom
									0.5f, -0.5f, 0.0f,  // right bottom
									0.5f, 0.5f, 0.0f    // right top
									};
	private int vertexStride = COORDS_PER_VERTEX * 4;
	
	private short drawOrder[] = { 0,1,2,0,2,3 }; // order to draw vertices
	
	int mProgram;
	int mPositionHandle;
	int mColorHandle;
	int mMVPMatrixHandle;
	
	
	// Set color with red, green, blue and alpha (opacity) values
	float color[] = { 0.13671875f, 0.56953125f, 0.82265625f, 1.0f };
	
	public Square() {
		// initialize vertex byte buffer for shape coordinates
		ByteBuffer bb = ByteBuffer.allocateDirect(
				// (# of coodinate valuse * 4 bytes per float)
				squareCoords.length * 4);
		
		bb.order(ByteOrder.nativeOrder());
		vertexBuffer = bb.asFloatBuffer();
		vertexBuffer.put(squareCoords);
		vertexBuffer.position(0);
		
		// initialize byte buffer for the draw list
		ByteBuffer dlb = ByteBuffer.allocateDirect(
				// (# of coordinate values * 2 bytes per short)
				drawOrder.length * 2);
		dlb.order(ByteOrder.nativeOrder());
		drawListBuffer = dlb.asShortBuffer();
		drawListBuffer.put(drawOrder);
		drawListBuffer.position(0);
		
		// preparation for compile 
		int vertexShader = MyRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
		int fragmentShader = MyRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
		
		mProgram  = GLES20.glCreateProgram();
		GLES20.glAttachShader(mProgram, vertexShader);
		GLES20.glAttachShader(mProgram, fragmentShader);
		GLES20.glLinkProgram(mProgram);
	}

	public void draw(float[] mvpMatrix) {
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
