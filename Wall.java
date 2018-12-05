import gmaths.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;

public class Wall {

  private SGNode wallRoot;
  private TransformNode translateWall;
  private float floorSize = 40f;
  private Model model;
  private Model model2;

  public Wall(Model model, Model model2) {
    this.model = model;
    this.model2 = model2;
  }

  /* Returns wallRoot once the nodes and scene graph have been set up */
  public SGNode wallInit() {
    wallRoot = new NameNode("wall structure");
    translateWall = new TransformNode("translate(0,0," + (-floorSize*0.25f) + ")", Mat4Transform.translate(0,0,-floorSize*0.25f));

    // BOTTOM //
    // BOTTOM //
    // BOTTOM //
    NameNode bottomWall = new NameNode("bottomWall");
    Mat4 m = Mat4Transform.scale(14f,6f,0f);
    m = Mat4.multiply(Mat4Transform.translate(0f,3f,-10f), m);
    TransformNode bottomWallTransform = new TransformNode("scale(14,6,0);translate(0,3,-10)", m);
    ModelNode cube0NodeW = new ModelNode("cubeWall(bottom wall)", model);

    // LEFT //
    // LEFT //
    // LEFT //
    NameNode leftWall = new NameNode("leftWall");
    m = Mat4Transform.scale(13f,20f,0f);
    m = Mat4.multiply(Mat4Transform.translate(-floorSize/2 + 6.5f,10f,-10f), m);
    TransformNode leftWallTransform = new TransformNode("scale(13,20,0);translate("+ (-floorSize/2 + 6.5f) +",10,-10)", m);
    ModelNode cube1NodeW = new ModelNode("cubeWall(left wall)", model);

    // RIGHT //
    // RIGHT //
    // RIGHT //
    NameNode rightWall = new NameNode("rightWall");
    m = Mat4Transform.scale(13f,20f,0f);
    m = Mat4.multiply(Mat4Transform.translate(floorSize/2 - 6.5f,10f,-10f), m);
    TransformNode rightWallTransform = new TransformNode("scale(13,20,0);translate("+ (floorSize/2 - 6.5f) +",10,-10)", m);
    ModelNode cube2NodeW = new ModelNode("cubeWall(right wall)", model);

    // TOP //
    // TOP //
    // TOP //
    NameNode topWall = new NameNode("topWall");
    m = Mat4Transform.scale(14f,3f,0f);
    m = Mat4.multiply(Mat4Transform.translate(0f,18.5f,-10f), m);
    TransformNode topWallTransform = new TransformNode("scale(14,3,10);translate(0,18.5,-10)", m);
    ModelNode cube3NodeW = new ModelNode("cubeWall(top wall)", model);

    // CENTRAL //
    // CENTRAL //
    // CENTRAL //
    NameNode centralWall = new NameNode("centralWall");
    m = Mat4Transform.scale(35f,20f,0f);
    m = Mat4.multiply(Mat4Transform.translate(0f,10f,-15f), m);
    TransformNode centralWallTransform = new TransformNode("scale(35,20,0);translate(0,10,-15)", m);
    ModelNode cube4NodeW = new ModelNode("cubeWindow(central wall)", model2);

    wallRoot.addChild(translateWall);
      translateWall.addChild(bottomWall);
        bottomWall.addChild(bottomWallTransform);
          bottomWallTransform.addChild(cube0NodeW);
      translateWall.addChild(leftWall);
        leftWall.addChild(leftWallTransform);
          leftWallTransform.addChild(cube1NodeW);
      translateWall.addChild(rightWall);
        rightWall.addChild(rightWallTransform);
          rightWallTransform.addChild(cube2NodeW);
      translateWall.addChild(topWall);
        topWall.addChild(topWallTransform);
          topWallTransform.addChild(cube3NodeW);
      translateWall.addChild(centralWall);
        centralWall.addChild(centralWallTransform);
          centralWallTransform.addChild(cube4NodeW);


    wallRoot.update();
    return wallRoot;

  }
}
