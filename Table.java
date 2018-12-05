import gmaths.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;

public class Table {

  private SGNode tableRoot;
  private TransformNode translateTable;
  private float tableLength = 25f;
  private float tableDepth = 15f;
  private float floorSize = 40f;
  private Model model;
  private Model model2;

  public Table(Model model, Model model2) {
    this.model = model;
    this.model2 = model2;
  }

  /* Returns tableRoot once the nodes and scene graph have been set up */
  public SGNode tableInit() {

    tableRoot = new NameNode("table structure");
    Mat4 m = Mat4Transform.translate(0,0,(-0.5f*floorSize) + (tableDepth*0.5f));
    translateTable = new TransformNode("translate(0,0,"+ (-0.5f*floorSize) + (tableDepth*0.5f) +")", m);

    // TABLE BODY //
    // TABLE BODY //
    // TABLE BODY //
    NameNode tableBody = new NameNode("tableBody");
    m = Mat4Transform.scale(tableLength,1,tableDepth);
    m = Mat4.multiply(Mat4Transform.translate(0,5f,0), m);
    TransformNode tableBodyTransform = new TransformNode("scale(" + tableLength + ",1," + tableDepth + ");translate(0,5,0)", m);
    ModelNode cube0NodeT = new ModelNode("cubeWood(table body)", model);

    // TABLE LEG BACK LEFT //
    // TABLE LEG BACK LEFT //
    // TABLE LEG BACK LEFT //
    NameNode tableLegBL = new NameNode("tableLegBL");
    m = Mat4Transform.scale(1f,5f,1f);
    m = Mat4.multiply(Mat4Transform.translate((0.5f*-tableLength)+0.5f,2.0f,(0.5f*-tableDepth)+0.5f), m);
    TransformNode tableLegBLTransform = new TransformNode("scale(1,5,1);translate(" + (0.5f*-tableLength)+0.5f + ",2," + (0.5f*-tableDepth)+0.5f + ")", m);
    ModelNode cube1NodeT = new ModelNode("cubeWoodLegs(table leg back left)", model2);

    // TABLE LEG BACK RIGHT //
    // TABLE LEG BACK RIGHT //
    // TABLE LEG BACK RIGHT //
    NameNode tableLegBR = new NameNode("tableLegBR");
    m = Mat4Transform.scale(1f,5f,1f);
    m = Mat4.multiply(Mat4Transform.translate((0.5f*tableLength)-0.5f,2.0f,(0.5f*-tableDepth)+0.5f), m);
    TransformNode tableLegBRTransform = new TransformNode("scale(1,5,1);translate(" + ((0.5f*tableLength)-0.5f) + ",2," + (0.5f*-tableDepth)+0.5f + ")", m);
    ModelNode cube2NodeT = new ModelNode("cubeWoodLegs(table leg back right)", model2);

    // TABLE LEG FRONT RIGHT //
    // TABLE LEG FRONT RIGHT //
    // TABLE LEG FRONT RIGHT //
    NameNode tableLegFR = new NameNode("tableLegFR");
    m = Mat4Transform.scale(1f,5f,1f);
    m = Mat4.multiply(Mat4Transform.translate((0.5f*tableLength)-0.5f,2.0f,(0.5f*tableDepth)-0.5f), m);
    TransformNode tableLegFRTransform = new TransformNode("scale(1,5,1);translate(" + ((0.5f*tableLength)-0.5f) + ",2," + ((0.5f*tableDepth)-0.5f) + ")", m);
    ModelNode cube3NodeT = new ModelNode("cubeWoodLegs(table leg front right)", model2);

    // TABLE LEG FRONT LEFT //
    // TABLE LEG FRONT LEFT //
    // TABLE LEG FRONT LEFT //
    NameNode tableLegFL = new NameNode("tableLegFL");
    m = Mat4Transform.scale(1f,5f,1f);
    m = Mat4.multiply(Mat4Transform.translate((0.5f*-tableLength)+0.5f,2.0f,(0.5f*tableDepth)-0.5f), m);
    TransformNode tableLegFLTransform = new TransformNode("scale(1,5,1);translate(" + (0.5f*-tableLength)+0.5f + ",2," + ((0.5f*tableDepth)-0.5f) + ")", m);
    ModelNode cube4NodeT = new ModelNode("cubeWoodLegs(table leg front left)", model2);


    tableRoot.addChild(translateTable);
      translateTable.addChild(tableBody);
        tableBody.addChild(tableBodyTransform);
          tableBodyTransform.addChild(cube0NodeT);
        tableBody.addChild(tableLegBL);
          tableLegBL.addChild(tableLegBLTransform);
            tableLegBLTransform.addChild(cube1NodeT);
        tableBody.addChild(tableLegBR);
          tableLegBR.addChild(tableLegBRTransform);
            tableLegBRTransform.addChild(cube2NodeT);
        tableBody.addChild(tableLegFR);
          tableLegFR.addChild(tableLegFRTransform);
            tableLegFRTransform.addChild(cube3NodeT);
        tableBody.addChild(tableLegFL);
          tableLegFL.addChild(tableLegFLTransform);
            tableLegFLTransform.addChild(cube4NodeT);



    tableRoot.update();
    return tableRoot;
  }
}
