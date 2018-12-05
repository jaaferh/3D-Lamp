import gmaths.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;

public class Glasses {

  private SGNode glassesRoot;
  private float lampScale = 0.3f;
  private float glassesTransX = 2f * lampScale;
  private float glassesTransY = 2f * lampScale;
  private float glassesTransZ = 0f * lampScale;
  private float glassesScale = 0.15f;
  private Model model;
  private Model model2;

  public Glasses(Model model, Model model2) {
    this.model = model;
    this.model2 = model2;
  }

  /* Returns glassesRoot once the nodes and scene graph have been set up*/
  public SGNode glassesInit() {
    glassesRoot = new NameNode("Glasses structure");

    //LEFT LENS//
    //LEFT LENS//
    //LEFT LENS//
    NameNode leftLens = new NameNode("leftLens");
    Mat4 m = Mat4Transform.scale(glassesScale*0.25f,glassesScale*3,glassesScale*3);
    m = Mat4.multiply(Mat4Transform.translate(0f,0f,glassesScale*1.5f), m);
    TransformNode leftLensTransform = new TransformNode("scale(" + glassesScale*0.25f + "," + glassesScale*3 + "," + glassesScale*3 + ")" +
                                                         ";translate(0,0," + glassesScale*1.5f + ")", m);
    ModelNode sphere0NodeG = new ModelNode("Sphere(table body)", model);

    //RIGHT LENS//
    //RIGHT LENS//
    //RIGHT LENS//
    NameNode rightLens = new NameNode("rightLens");
    m = Mat4Transform.scale(glassesScale*0.25f,glassesScale*3,glassesScale*3);
    m = Mat4.multiply(Mat4Transform.translate(0,0f,glassesScale*-1.5f), m);
    TransformNode rightLensTransform = new TransformNode("scale(" + glassesScale*0.25f + "," + glassesScale*3 + "," + glassesScale*3 + ")" +
                                                         ";translate(0,0," + glassesScale*-1.5f + ")", m);
    ModelNode sphere1NodeG = new ModelNode("Sphere(table body)", model);

    //MIDDLE BRIDGE//
    //MIDDLE BRIDGE//
    //MIDDLE BRIDGE//
    NameNode middleBridge = new NameNode("middleBridge");
    m = Mat4Transform.scale(glassesScale*0.25f,glassesScale*0.5f,glassesScale*0.5f);
    m = Mat4.multiply(Mat4Transform.translate(0,0,0), m);
    TransformNode middleBridgeTransform = new TransformNode("scale(" + glassesScale*0.25f + "," + glassesScale*0.5f + "," + glassesScale*0.5f + ")" +
                                                         ";translate(0,0,0)", m);
    ModelNode cube3NodeG = new ModelNode("CubeWall(table body)", model2);

    //LEFT ARM//
    //LEFT ARM//
    //LEFT ARM//
    NameNode leftArm = new NameNode("leftArm");
    m = Mat4Transform.scale(glassesScale*3f,glassesScale*0.5f,glassesScale*0.25f);
    m = Mat4.multiply(Mat4Transform.translate(glassesScale*-1.5f,0f,glassesScale*2.75f), m);
    TransformNode leftArmTransform = new TransformNode("scale(" + glassesScale*3f + "," + glassesScale*0.5f + "," + glassesScale*0.25f + ")" +
                                                         ";translate(" + glassesScale*-1.5f + ",0," + glassesScale*2.75f + ")", m);
    ModelNode cube5NodeG = new ModelNode("CubeWall(table body)", model2);

    //RIGHT ARM//
    //RIGHT ARM//
    //RIGHT ARM//
    NameNode rightArm = new NameNode("rightArm");
    m = Mat4Transform.scale(glassesScale*3f,glassesScale*0.5f,glassesScale*0.25f);
    m = Mat4.multiply(Mat4Transform.translate(glassesScale*-1.5f,0f,glassesScale*-2.75f), m);
    TransformNode rightArmTransform = new TransformNode("scale(" + glassesScale*3f + "," + glassesScale*0.5f + "," + glassesScale*0.25f + ")" +
                                                         ";translate(" + glassesScale*-1.5f + ",0," + glassesScale*-2.75f + ")", m);
    ModelNode cube6NodeG = new ModelNode("CubeWall(table body)", model2);

    // glassesRoot
    glassesRoot.addChild(middleBridge);
      middleBridge.addChild(middleBridgeTransform);
        middleBridgeTransform.addChild(cube3NodeG);
      middleBridge.addChild(leftLens);
        leftLens.addChild(leftLensTransform);
          leftLensTransform.addChild(sphere0NodeG);
        leftLens.addChild(leftArm);
          leftArm.addChild(leftArmTransform);
            leftArmTransform.addChild(cube5NodeG);
      middleBridge.addChild(rightLens);
        rightLens.addChild(rightLensTransform);
          rightLensTransform.addChild(sphere1NodeG);
        rightLens.addChild(rightArm);
          rightArm.addChild(rightArmTransform);
            rightArmTransform.addChild(cube6NodeG);

    glassesRoot.update();
    return glassesRoot;
  }
}
