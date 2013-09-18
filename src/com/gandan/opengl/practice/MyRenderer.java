package com.gandan.opengl.practice;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.EGLConfig;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

public class MyRenderer implements GLSurfaceView.Renderer{
	public volatile float mAngle;
	private float[] mProjMatrix = new float[16];
	private float[] mVMatrix = new float[16];
	private float[] mRotationMatrix = new float[16];
	private float[] mMVPMatrix = new float[16];
	Triangle mTriangle;
	Square mSquare;
	Circle mCircle;
	
	@Override
	public void onDrawFrame(GL10 unused){
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
		
		// Set the camera position (View matrix)
		Matrix. setLookAtM(mVMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
		
		// Calculate the projection and View transformation
		Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mVMatrix, 0);
		
		// Create a rotation transformation for the triangle
		//long time = SystemClock.uptimeMillis() % 4000L;
		//float angle = 0.090f * ((int) time);
		Matrix.setRotateM(mRotationMatrix, 0, mAngle, 0, 0, -1.0f);
		
		// Combine the rotation matrix with the projection and camera view
		Matrix.multiplyMM(mMVPMatrix, 0, mRotationMatrix, 0, mMVPMatrix, 0);
		
		// draw square
		mSquare.draw(mMVPMatrix);
		
		// draw triangle
		mTriangle.draw(mMVPMatrix);
		
		// draw circle
		mCircle.draw(mMVPMatrix);
	}
	
	@Override
	public void onSurfaceChanged(GL10 unused, int width, int height){
		GLES20.glViewport(0, 0, width, height);
		
		float ratio = (float) width / height;
		
		// this projetion matrix is applied to object coordinates
		// in the onDrawFrame() method
		Matrix.frustumM(mProjMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
	}

	@Override
	public void onSurfaceCreated(GL10 gl,
			javax.microedition.khronos.egl.EGLConfig config) {
		GLES20.glClearColor(1.0f, 0.5f, 0.5f, 1.0f);
		
		// initialize a triangle
		mTriangle = new Triangle();
		
		// initialize a quare
		mSquare = new Square();
		
		// initialize a circle
		mCircle = new Circle();
	}
	
	
	
	// Shaders contain OpenGL Shading Language (GLSL) code that must be 
	// compiled prior to using it in the OpenGL ES environment
	public static int loadShader(int type, String shaderCode) {
		
		//create a vertex shader type (GLES20.GL_VERTEX_SHADER)
		// or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
		int shader =  GLES20.glCreateShader(type);
		
		// add the source code to the shader and compile it
		GLES20.glShaderSource(shader,shaderCode);
		GLES20.glCompileShader(shader);
		
		return shader;
	}

}
