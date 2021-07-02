package com.stoopidgame;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.nio.*;
import java.util.Random;

import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.opengl.GL46.*;

public class App {

	// The window handle
	private long window;
	private int scrWidth = 800, scrHeight = 600;

	private float lmX, lmY, mXofst, mYofst;
	float yaw = 90, pitch = 0;
	private boolean mouseb = true;
	float msens = 0.1f;

	public void run() {
		System.out.println("Running with LWJGL " + Version.getVersion());

		init();
		loop();

		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);

		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}

	private void init() {
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if (!glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW");

		// Configure GLFW
		glfwDefaultWindowHints(); // optional, the current window hints are already the default
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

		String[] s = {"look at dis cube", "some fookin crap", "now with textures", "omgisdisshaders", 
			"omgisdisopengl", "LogXx? sounds stupid!", "your computer will explode in three secs (that's a joke, lad)", 
			"your ass is gonna be interrogated by my d1cc real soon", "where is the lightning you dumbass", "r6 is trash, you know?",
			"nope. nothing funny this time"
		};

		Random rand = new Random();

		// Create the window
		window = glfwCreateWindow(scrWidth, scrHeight, s[rand.nextInt(s.length)], NULL, NULL);
		if (window == NULL)
			throw new RuntimeException("Failed to create the GLFW window");

		// Setup a key callback. It will be called every time a key is pressed, repeated
		// or released.
		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
				glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
		});

		// Get the thread stack and push a new frame
		try (MemoryStack stack = stackPush()) {
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*

			// Get the window size passed to glfwCreateWindow
			glfwGetWindowSize(window, pWidth, pHeight);

			// Get the resolution of the primary monitor
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

			// Center the window
			glfwSetWindowPos(window, (vidmode.width() - pWidth.get(0)) / 2, (vidmode.height() - pHeight.get(0)) / 2);
		} // the stack frame is popped automatically

		// Make the OpenGL context current
		glfwMakeContextCurrent(window);
		// Enable v-sync
		glfwSwapInterval(1);

		glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
		glfwSetCursorPosCallback(window, (wnd, x, y) -> {
			float _x = (float) x, _y = (float) y;
			if (mouseb)
			{
				lmX = _x;
				lmY = _y;
				mouseb = false;
			}
			mXofst = _x - lmX;
			mYofst = -(_y - lmY);
			lmX = _x;
			lmY = _y;
			yaw += mXofst * msens;
			pitch += mYofst * msens;
			if (pitch > 89.0f)
        		pitch = 89.0f;
    		if (pitch < -89.0f)
        		pitch = -89.0f;

		});  

		// Make the window visible
		glfwShowWindow(window);
	}

	private void loop() {
		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();

		glfwSetFramebufferSizeCallback(window, (wnd, w, h) -> {
			glViewport(0, 0, w, h);
		});

		float vertices[] = {
			-0.5f, -0.5f, -0.5f,  0.0f, 0.0f,
			 0.5f, -0.5f, -0.5f,  1.0f, 0.0f,
			 0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
			 0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
			-0.5f,  0.5f, -0.5f,  0.0f, 1.0f,
			-0.5f, -0.5f, -0.5f,  0.0f, 0.0f,
		
			-0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
			 0.5f, -0.5f,  0.5f,  1.0f, 0.0f,
			 0.5f,  0.5f,  0.5f,  1.0f, 1.0f,
			 0.5f,  0.5f,  0.5f,  1.0f, 1.0f,
			-0.5f,  0.5f,  0.5f,  0.0f, 1.0f,
			-0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
		
			-0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
			-0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
			-0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
			-0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
			-0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
			-0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
		
			 0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
			 0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
			 0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
			 0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
			 0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
			 0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
		
			-0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
			 0.5f, -0.5f, -0.5f,  1.0f, 1.0f,
			 0.5f, -0.5f,  0.5f,  1.0f, 0.0f,
			 0.5f, -0.5f,  0.5f,  1.0f, 0.0f,
			-0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
			-0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
		
			-0.5f,  0.5f, -0.5f,  0.0f, 1.0f,
			 0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
			 0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
			 0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
			-0.5f,  0.5f,  0.5f,  0.0f, 0.0f,
			-0.5f,  0.5f, -0.5f,  0.0f, 1.0f
		};
		/*
		int indices[] = { 
				0, 1, 3, 
				1, 2, 3 
		};
		*/

		int vaoID = glGenVertexArrays();
		int vboID = glGenBuffers();
		//int eboID = glGenBuffers();
		glBindVertexArray(vaoID);
		glBindBuffer(GL_ARRAY_BUFFER, vboID);
		glBufferData(GL_ARRAY_BUFFER, vertices, GL_DYNAMIC_DRAW);
		//glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
		//glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
		// glVertexAttribPointer(0, 3, GL_FLOAT, false, 8 * 4, 0);
		// glEnableVertexAttribArray(0);
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 5 * 4, 0);
		glEnableVertexAttribArray(0);
		glVertexAttribPointer(1, 2, GL_FLOAT, false, 5 * 4, 3 * 4);
		glEnableVertexAttribArray(1);
		// glVertexAttribPointer(1, 3, GL_FLOAT, false, 8 * 4, 3 * 4);
		// glEnableVertexAttribArray(1);
		// glVertexAttribPointer(2, 2, GL_FLOAT, false, 8 * 4, 6 * 4);
		// glEnableVertexAttribArray(2);

		glBindBuffer(GL_ARRAY_BUFFER, 0);
		//glBindVertexArray(0);

		int texID = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, texID);
		try (MemoryStack stack = stackPush()) {
			IntBuffer w = stack.mallocInt(1), h = stack.mallocInt(1), c = stack.mallocInt(1);
			ByteBuffer d_texture = stbi_load("textures/dirt.png", w, h, c, 0);
			if (d_texture == null) {
				System.err.println("dirt.png load fail...\n" + stbi_failure_reason());
			} else {
				System.out.printf("Loaded dirt.png: %d %d %d\n", w.get(0), h.get(0), c.get(0));
				glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, w.get(0), h.get(0), 0, GL_RGBA, GL_UNSIGNED_BYTE, d_texture);
				glGenerateMipmap(GL_TEXTURE_2D);
			}
			stbi_image_free(d_texture);
		}
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

		Vector3f cameraPos = new Vector3f(0, 0, 0), cameraFront = new Vector3f(0, 0, 1), cameraUp = new Vector3f(0, 1, 0);
		Vector3f cubeOffset = new Vector3f(0, 0, 3);
		
		Matrix4f modelMat = new Matrix4f();
		Matrix4f viewMat = new Matrix4f();
		Matrix4f projMat = new Matrix4f().setPerspective((float)Math.toRadians(60), (float) scrWidth / scrHeight, 0.1f, 100.0f);

		Shader sh = new Shader("vertex1.vs", "fragment1.fs");
		int modelLoc = glGetUniformLocation(sh.getID(), "model");
		int viewLoc = glGetUniformLocation(sh.getID(), "view");
		int projLoc = glGetUniformLocation(sh.getID(), "projection");
		sh.use();

		try (MemoryStack stack = stackPush()) {
			FloatBuffer projFb = stack.mallocFloat(16);
			projMat.get(projFb);
			glUniformMatrix4fv(projLoc, false, projFb);
		}

		// Set the clear color
		glClearColor(0.1f, 0.2f, 0.2f, 0.0f);
		//glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
		glEnable(GL_DEPTH_TEST); 

		float deltaTime, lastFrame = (float) glfwGetTime();

		while (!glfwWindowShouldClose(window)) {
			float currentFrame = (float) glfwGetTime();
			deltaTime = currentFrame - lastFrame;
        	lastFrame = currentFrame;

			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

			try (MemoryStack stack = stackPush()) {
				IntBuffer pWidth = stack.mallocInt(1); // int*
				IntBuffer pHeight = stack.mallocInt(1); // int*
	
				glfwGetWindowSize(window, pWidth, pHeight);
				if (scrWidth != pWidth.get(0) || scrHeight != pHeight.get(0)){
					scrWidth = pWidth.get(0);
					scrHeight = pHeight.get(0);
					projMat.setPerspective((float)Math.toRadians(60), (float) scrWidth / scrHeight, 0.1f, 100.0f);
					FloatBuffer projFb = stack.mallocFloat(16);
					projMat.get(projFb);
					glUniformMatrix4fv(projLoc, false, projFb);
				}
			} 

			if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS)
				cameraPos.add(new Vector3f(cameraFront).mul(deltaTime * 5f));
			if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS)
				cameraPos.sub(new Vector3f(cameraFront).mul(deltaTime * 5f));
			Vector3f right = new Vector3f(cameraFront).cross(cameraUp).normalize();
			if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS)
				cameraPos.add(new Vector3f(right).mul(deltaTime * 5f));
			if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS)
				cameraPos.sub(new Vector3f(right).mul(deltaTime * 5f));
			
			cameraFront.x = (float) (Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
			cameraFront.y = (float) Math.sin(Math.toRadians(pitch));
			cameraFront.z = (float) (Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
			cameraFront.normalize();

			try (MemoryStack stack = stackPush()) {
				viewMat.setLookAt(cameraPos, new Vector3f(cameraPos).add(cameraFront), cameraUp);
				FloatBuffer viewFb = stack.mallocFloat(16);
				viewMat.get(viewFb);
				glUniformMatrix4fv(viewLoc, false, viewFb);
			
				modelMat.translation(cubeOffset).rotate((float) Math.sin(glfwGetTime()), 1.0f, 0.0f, 0.0f);
				FloatBuffer modelFb = stack.mallocFloat(16);
				modelMat.get(modelFb);
				glUniformMatrix4fv(modelLoc, false, modelFb);
			}
			
			//glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);
			glDrawArrays(GL_TRIANGLES, 0, 36);

			glfwSwapBuffers(window);

			glfwPollEvents();
		}

		glDeleteVertexArrays(vaoID);
		glDeleteBuffers(vboID);
		//glDeleteBuffers(eboID);
		sh.finalize();
	}

	public static void main(String[] args) {
		new App().run();
	}

}