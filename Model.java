/* This class has been used from Dr Steve Maddock's tutorials */
/* Some functions may have been added or adjusted */

import gmaths.*;
import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;

public class Model {

  private Mesh mesh;
  private int[] textureId1;
  private int[] textureId2;
  private Material material;
  private Shader shader;
  private Mat4 modelMatrix;
  private Camera camera;
  private Light light;
  private Light light2;
  private Light spotLight;
  private double startTime;

  public Model(GL3 gl, Camera camera, Light light, Light light2, Light spotLight, Shader shader, Material material, Mat4 modelMatrix, Mesh mesh, int[] textureId1, int[] textureId2) {
    this.mesh = mesh;
    this.material = material;
    this.modelMatrix = modelMatrix;
    this.shader = shader;
    this.camera = camera;
    this.light = light;
    this.light2 = light2;
    this.spotLight = spotLight;
    this.textureId1 = textureId1;
    this.textureId2 = textureId2;
  }

  public Model(GL3 gl, Camera camera, Light light, Light light2, Light spotLight, Shader shader, Material material, Mat4 modelMatrix, Mesh mesh, int[] textureId1) {
    this(gl, camera, light, light2, spotLight, shader, material, modelMatrix, mesh, textureId1, null);
  }

  public Model(GL3 gl, Camera camera, Light light, Light light2, Light spotLight, Shader shader, Material material, Mat4 modelMatrix, Mesh mesh) {
    this(gl, camera, light, light2, spotLight, shader, material, modelMatrix, mesh, null, null);
  }

  public void setModelMatrix(Mat4 m) {
    modelMatrix = m;
  }

  public void setCamera(Camera camera) {
    this.camera = camera;
  }

  public void setLight(Light light) {
    this.light = light;
  }

  public void setLight2(Light light2) {
    this.light2 = light2;
  }

  public void setspotLight(Light spotLight) {
    this.spotLight = spotLight;
  }

  private double getSeconds() {
    return System.currentTimeMillis()/1000.0;
  }

  public void render(GL3 gl, Mat4 modelMatrix) {
    Mat4 mvpMatrix = Mat4.multiply(camera.getPerspectiveMatrix(), Mat4.multiply(camera.getViewMatrix(), modelMatrix));
    shader.use(gl);
    shader.setFloatArray(gl, "model", modelMatrix.toFloatArrayForGLSL());
    shader.setFloatArray(gl, "mvpMatrix", mvpMatrix.toFloatArrayForGLSL());

    shader.setVec3(gl, "viewPos", camera.getPosition());

    shader.setVec3(gl, "light.position", light.getPosition());
    shader.setVec3(gl, "light.ambient", light.getMaterial().getAmbient());
    shader.setVec3(gl, "light.diffuse", light.getMaterial().getDiffuse());
    shader.setVec3(gl, "light.specular", light.getMaterial().getSpecular());
    shader.setFloat(gl, "light.brightness", light.getBrightness());

    shader.setVec3(gl, "light2.position", light2.getPosition());
    shader.setFloat(gl, "light2.brightness", light2.getBrightness());

    shader.setVec3(gl, "spotLight.position", spotLight.getSpotPosition());
    shader.setVec3(gl, "spotLight.direction", spotLight.getCamera().getFront());
    shader.setFloat(gl, "spotLight.cutOff", (float)(Math.cos(Math.toRadians(10f))));
    shader.setFloat(gl, "spotLight.outerCutOff", (float)(Math.cos(Math.toRadians(11f))));
    shader.setFloat(gl, "spotLight.brightness", spotLight.getBrightness());



    shader.setVec3(gl, "material.ambient", material.getAmbient());
    shader.setVec3(gl, "material.diffuse", material.getDiffuse());
    shader.setVec3(gl, "material.specular", material.getSpecular());
    shader.setFloat(gl, "material.shininess", material.getShininess());


    // Moving textures
    double elapsedTime = getSeconds() - startTime;

    double t = elapsedTime*0.05;  // *0.1 slows it down a bit
    float offsetX = (float)(t - Math.floor(t));
    float offsetY = 0.0f;
    shader.setFloat(gl, "offset", offsetX, offsetY);

    shader.setInt(gl, "second_moving_texture", 0);
    shader.setInt(gl, "second_moving_texture", 1);


    if (textureId1!=null) {
      shader.setInt(gl, "first_texture", 0);  // be careful to match these with GL_TEXTURE0 and GL_TEXTURE1
      gl.glActiveTexture(GL.GL_TEXTURE0);
      gl.glBindTexture(GL.GL_TEXTURE_2D, textureId1[0]);
    }
    if (textureId2!=null) {
      shader.setInt(gl, "second_texture", 1);
      gl.glActiveTexture(GL.GL_TEXTURE1);
      gl.glBindTexture(GL.GL_TEXTURE_2D, textureId2[0]);
    }
    mesh.render(gl);
  }

  public void render(GL3 gl) {
    render(gl, modelMatrix);
  }

  public void dispose(GL3 gl) {
    mesh.dispose(gl);
    if (textureId1!=null) gl.glDeleteBuffers(1, textureId1, 0);
    if (textureId2!=null) gl.glDeleteBuffers(1, textureId2, 0);
  }

}
