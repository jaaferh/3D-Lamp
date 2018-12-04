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



  public SGNode tableInit() {

    tableRoot = new NameNode("table structure");
    translateTable = new TransformNode("translate(0,0,0)", Mat4Transform.translate(0,0,(-0.5f*floorSize) + (tableDepth*0.5f)));

    // TABLE BODY //
    // TABLE BODY //
    // TABLE BODY //
    NameNode tableBody = new NameNode("tableBody");
    Mat4 m = Mat4Transform.scale(tableLength,1,tableDepth);
    m = Mat4.multiply(Mat4Transform.translate(0,5f,0), m);
    TransformNode tableBodyTransform = new TransformNode("translate(0,5,-5);scale(14,1,10)", m);
    ModelNode cube0NodeT = new ModelNode("Cube(table body)", model);

    // TABLE LEG BACK LEFT //
    // TABLE LEG BACK LEFT //
    // TABLE LEG BACK LEFT //
    NameNode tableLegBL = new NameNode("tableLegBL");
    m = Mat4Transform.scale(1f,5f,1f);
    m = Mat4.multiply(Mat4Transform.translate((0.5f*-tableLength)+0.5f,2.0f,(0.5f*-tableDepth)+0.5f), m);
    TransformNode tableLegBLTransform = new TransformNode("translate(-6.5,2.5,-9.5);scale(1,5,1)", m);
    ModelNode cube1NodeT = new ModelNode("Cube(table leg back left)", model2);

    // TABLE LEG BACK RIGHT //
    // TABLE LEG BACK RIGHT //
    // TABLE LEG BACK RIGHT //
    NameNode tableLegBR = new NameNode("tableLegBR");
    m = Mat4Transform.scale(1f,5f,1f);
    m = Mat4.multiply(Mat4Transform.translate((0.5f*tableLength)-0.5f,2.0f,(0.5f*-tableDepth)+0.5f), m);
    TransformNode tableLegBRTransform = new TransformNode("scale(10,10,10);translate(0,0,5)", m);
    ModelNode cube2NodeT = new ModelNode("Cube(table leg back right)", model2);

    // TABLE LEG FRONT RIGHT //
    // TABLE LEG FRONT RIGHT //
    // TABLE LEG FRONT RIGHT //
    NameNode tableLegFR = new NameNode("tableLegFR");
    m = Mat4Transform.scale(1f,5f,1f);
    m = Mat4.multiply(Mat4Transform.translate((0.5f*tableLength)-0.5f,2.0f,(0.5f*tableDepth)-0.5f), m);
    TransformNode tableLegFRTransform = new TransformNode("scale(10,10,10);translate(0,0,5)", m);
    ModelNode cube3NodeT = new ModelNode("Cube(table leg front right)", model2);

    // TABLE LEG FRONT LEFT //
    // TABLE LEG FRONT LEFT //
    // TABLE LEG FRONT LEFT //
    NameNode tableLegFL = new NameNode("tableLegFL");
    m = Mat4Transform.scale(1f,5f,1f);
    m = Mat4.multiply(Mat4Transform.translate((0.5f*-tableLength)+0.5f,2.0f,(0.5f*tableDepth)-0.5f), m);
    TransformNode tableLegFLTransform = new TransformNode("scale(10,10,10);translate(0,0,5)", m);
    ModelNode cube4NodeT = new ModelNode("Cube(table leg front left)", model2);


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
