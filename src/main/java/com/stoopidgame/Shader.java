package com.stoopidgame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;

import org.joml.Vector3f;
import org.lwjgl.system.*;

import static org.lwjgl.opengl.GL46.*;
import static org.lwjgl.system.MemoryStack.*;

public class Shader {
    private int id;

    public int getID() {
        return id;
    }

    public Shader(String vertexShName, String fragmentShName) {
        super();
        StringBuilder vstr = new StringBuilder();
        try (BufferedReader vrdr = new BufferedReader(
                new InputStreamReader(getClass().getResourceAsStream("/shaders/" + vertexShName)))) {
            String line;
            while ((line = vrdr.readLine()) != null) {
                vstr.append(line + '\n');
            }
        } catch (IOException e) {
            System.err.println("Failed to load vertex shader file...");
            e.printStackTrace();
        }
        int vshader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vshader, vstr);
        glCompileShader(vshader);
        int stat1 = glGetShaderi(vshader, GL_COMPILE_STATUS);
        if (stat1 == 0)
            System.err.println("Something's wrong with vshader!\n" + glGetShaderInfoLog(vshader));

        StringBuilder fstr = new StringBuilder();
        try (BufferedReader frdr = new BufferedReader(
                new InputStreamReader(getClass().getResourceAsStream("/shaders/" + fragmentShName)))) {
            String line;
            while ((line = frdr.readLine()) != null) {
                fstr.append(line + '\n');
            }
        } catch (IOException e) {
            System.err.println("Failed to load fragment shader file...");
            e.printStackTrace();
        }
        int fshader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fshader, fstr);
        glCompileShader(fshader);
        int stat2 = glGetShaderi(fshader, GL_COMPILE_STATUS);
        if (stat2 == 0)
            System.err.println("Something's wrong with fshader!\n" + glGetShaderInfoLog(fshader));

        int shaderProg = glCreateProgram();
        glAttachShader(shaderProg, vshader);
        glAttachShader(shaderProg, fshader);
        glLinkProgram(shaderProg);
        int stat3 = glGetProgrami(shaderProg, GL_LINK_STATUS);
        if (stat3 == 0)
            System.err.println("Something's wrong with shaderProg!\n" + glGetProgramInfoLog(shaderProg));
        glDeleteShader(vshader);
        glDeleteShader(fshader);
        id = shaderProg;
    }

    public void use() {
        glUseProgram(id);
    }

    public void setBool(String name, boolean value) {
        glUniform1i(glGetUniformLocation(id, name), value ? 1 : 0);
    }

    public void setInt(String name, int value) {
        glUniform1i(glGetUniformLocation(id, name), value);
    }

    public void setFloat(String name, float value) {
        glUniform1f(glGetUniformLocation(id, name), value);
    }

    public void setMat4(String name, Vector3f vec) {
        try (MemoryStack stack = stackPush())
        {
            FloatBuffer bf = stack.mallocFloat(3);
            vec.get(bf);
            glUniform3fv(glGetUniformLocation(id, name), bf);
        }
    }

    public void setMat4(String name, boolean trans, float[] value) {
        glUniformMatrix4fv(glGetUniformLocation(id, name), trans, value);
    }

    protected void finalize() {
        glDeleteProgram(id);
    }
}
