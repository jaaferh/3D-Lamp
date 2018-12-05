import gmaths.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;

public class DiscoBall {

  private SGNode discoRoot;
  private TransformNode translateDisco, rotateDisco;
  private float rotateDiscoAngleStart = 10, rotateDiscoAngle = rotateDiscoAngleStart;
  private float discoScale = 0.4f;
  private float discoBaseHeight = 1f * discoScale;
  private Model model;
  private Model model2;
  private Model model3;

  public DiscoBall(Model model, Model model2, Model model3) {
    this.model = model;
    this.model2 = model2;
    this.model3 = model3;
  }

  public float getAngleStart() {
    return rotateDiscoAngleStart;
  }

  public float getAngle() {
    return rotateDiscoAngle;
  }

  public void setAngle(float a) {
    rotateDiscoAngle = a;
  }

  public void setRotateTransform(float a) {
    rotateDisco.setTransform(Mat4Transform.rotateAroundY(a));
  }

  /* Returns discoRoot once the nodes and scene graph have been set up*/
  public SGNode discoInit() {
    discoRoot = new NameNode("disco structure");
    translateDisco = new TransformNode("translate(-9f,5.5f,-14.5f)", Mat4Transform.translate(-9f,5.5f,-14.5f));

    // DISCO BASE //
    // DISCO BASE //
    // DISCO BASE //
    NameNode discoBase = new NameNode("discoBase");
    Mat4 m = Mat4Transform.scale(discoScale*6f,discoScale*1f,discoScale*3f);
    m = Mat4.multiply(Mat4Transform.translate(0f,discoScale*0.5f,0f), m);
    TransformNode discoBaseTransform = new TransformNode("scale(" + discoScale*6f + "," + discoScale*1f + "," + discoScale*3f + ")" +
                                                         ";translate(0," + discoScale*0.5f + ", 0)", m);
    ModelNode cube0Disco = new ModelNode("cubeBlackB(discoBase)", model);

    // DISCO STAND VERTICAL //
    // DISCO STAND VERTICAL //
    // DISCO STAND VERTICAL //
    NameNode discoStandV = new NameNode("discoStandV");
    m = Mat4Transform.scale(discoScale*0.1f,discoScale*16f,discoScale*0.1f);
    m = Mat4.multiply(Mat4Transform.translate(0f,discoScale*8.5f,0f), m);
    TransformNode discoStandVTransform = new TransformNode("scale(" + discoScale*0.1f + "," + discoScale*16f + "," + discoScale*0.1f + ")" +
                                                         ";translate(0," + discoScale*8.5f + ", 0)", m);
    ModelNode cube1Disco = new ModelNode("cubeFloor(discoStandV)", model2);

    // DISCO STAND HORIZONTAL //
    // DISCO STAND HORIZONTAL //
    // DISCO STAND HORIZONTAL //
    NameNode discoStandH = new NameNode("discoStandH");
    m = Mat4Transform.scale(discoScale*5f,discoScale*0.1f,discoScale*0.1f);
    m = Mat4.multiply(Mat4Transform.translate(discoScale*2.5f,discoScale*16.5f,0f), m);
    TransformNode discoStandHTransform = new TransformNode("scale(" + discoScale*5f + "," + discoScale*0.1f + "," + discoScale*0.1f + ")" +
                                                         ";translate(" + discoScale*2.5f + "," + discoScale*16.5f + ", 0f)", m);
    ModelNode cube2Disco = new ModelNode("cubeFloor(discoStandH)", model2);

    // DISCO STRING //
    // DISCO STRING //
    // DISCO STRING //
    NameNode discoString = new NameNode("discoString");
    m = Mat4Transform.scale(discoScale*0.1f,discoScale*1f,discoScale*0.1f);
    m = Mat4.multiply(Mat4Transform.translate(discoScale*5f,discoScale*16f,0f), m);
    TransformNode discoStringTransform = new TransformNode("scale(" + discoScale*0.1f + "," + discoScale*1f + "," + discoScale*0.1f + ")" +
                                                         ";translate(" + discoScale*5f + "," + discoScale*16f + ", 0)", m);
    ModelNode cube3Disco = new ModelNode("cubeFloor(discoString)", model2);

    // DISCO BALL //
    // DISCO BALL //
    // DISCO BALL //
    NameNode discoBall = new NameNode("discoBall");
    m = Mat4Transform.translate(discoScale*5f,discoScale*13.5f,0f);
    TransformNode translateBelowString = new TransformNode("translate(" + discoScale*5f + "," + discoScale*13.5f + ", 0)", m);

    rotateDisco = new TransformNode("rotateAroundY("+rotateDiscoAngle+")",Mat4Transform.rotateAroundY(rotateDiscoAngle));
    m = Mat4Transform.scale(discoScale*4f,discoScale*4f,discoScale*4f);
    m = Mat4.multiply(Mat4Transform.translate(0f,0f,0f), m);
    TransformNode discoBallTransform = new TransformNode("scale(" + discoScale*4f + "," + discoScale*4f + "," + discoScale*4f + ");translate(0,0,0)", m);
    ModelNode cube4Disco = new ModelNode("sphere(discoBall)", model3);

    discoRoot.addChild(translateDisco);
      translateDisco.addChild(discoBase);
        discoBase.addChild(discoBaseTransform);
          discoBaseTransform.addChild(cube0Disco);
        discoBase.addChild(discoStandV);
          discoStandV.addChild(discoStandVTransform);
            discoStandVTransform.addChild(cube1Disco);
          discoStandV.addChild(discoStandH);
            discoStandH.addChild(discoStandHTransform);
              discoStandHTransform.addChild(cube2Disco);
            discoStandH.addChild(discoString);
              discoString.addChild(discoStringTransform);
                discoStringTransform.addChild(cube3Disco);
              discoString.addChild(translateBelowString);
                translateBelowString.addChild(rotateDisco);
                  rotateDisco.addChild(discoBall);
                    discoBall.addChild(discoBallTransform);
                      discoBallTransform.addChild(cube4Disco);

    discoRoot.update();
    return discoRoot;

  }
}
