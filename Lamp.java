import gmaths.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;

public class Lamp {
  private Camera camera;
  private Mat4 perspective;
  private Model floor, window, sphere, sphere2, sphereLong, cube, cube2, cube3;
  private Light light, light2;
  private SGNode lampRoot, tableRoot, wallRoot;


  private TransformNode translateX, translateLamp, translateTable, translateWall, rotateAll;
  private TransformNode rotateSec, rotateSphereBody, rotateUpperBranch, rotateHead;
  private float xPosition = 0;
  private float rotateAllAngleStart = 25, rotateAllAngle = rotateAllAngleStart;
  private float rotateSphereBodyStart = 60, rotateSphereBodyAngle = rotateSphereBodyStart;
  private float rotateUpperAngleStart = -120, rotateUpperBranchAngle = rotateUpperAngleStart;
  private float rotateHeadAngleStart = 10, rotateHeadAngle = rotateHeadAngleStart;

  public void lampInit() {

  }
}
