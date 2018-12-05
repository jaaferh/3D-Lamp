import gmaths.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;

public class DjBooth {

  private SGNode boothRoot;
  private TransformNode translateBooth, rotateBooth;
  private float boothScale = 0.8f;
  private float djHeight = 3f * boothScale;
  private Model model;
  private Model model2;

  public DjBooth(Model model, Model model2) {
    this.model = model;
    this.model2 = model2;
  }

  /* Returns boothRoot once the nodes and scene graph have been set up */
  public SGNode djBoothInit() {
    boothRoot = new NameNode("booth structure");
    translateBooth = new TransformNode("translate(10f,5.5f,-12.5f)", Mat4Transform.translate(10f,5.5f,-12.5f));
    rotateBooth = new TransformNode("rotateAroundY(90)", Mat4Transform.rotateAroundY(90f));

    // DJ //
    // DJ //
    // DJ //
    NameNode dj = new NameNode("dj");
    Mat4 m = Mat4Transform.scale(boothScale*9f,boothScale*3f,boothScale*4f);
    m = Mat4.multiply(Mat4Transform.translate(0f,boothScale*1.5f,0f), m);
    TransformNode djTransform = new TransformNode("scale(" + boothScale*9f + "," + boothScale*3f + "," + boothScale*4f + ")" +
                                                         ";translate(0," + boothScale*1.5f + ", 0)", m);
    ModelNode cube0Booth = new ModelNode("cubeDjBooth(dj)", model);

    // LEFT SPEAKER //
    // LEFT SPEAKER //
    // LEFT SPEAKER //
    NameNode leftSpeaker = new NameNode("leftSpeaker");
    m = Mat4Transform.scale(boothScale*3f,boothScale*7f,boothScale*4f);
    m = Mat4.multiply(Mat4Transform.translate(boothScale*-6f,boothScale*3.5f,0f), m);
    TransformNode leftSpeakerTransform = new TransformNode("scale(" + boothScale*3f + "," + boothScale*7f + "," + boothScale*7f + ")" +
                                                         ";translate(" + boothScale*-6f + "," + boothScale*3.5f + ", 0)", m);
    ModelNode cube1Booth = new ModelNode("cubeTS(left speaker)", model2);

    // RIGHT SPEAKER //
    // RIGHT SPEAKER //
    // RIGHT SPEAKER //
    NameNode rightSpeaker = new NameNode("rightSpeaker");
    m = Mat4Transform.scale(boothScale*3f,boothScale*7f,boothScale*4f);
    m = Mat4.multiply(Mat4Transform.translate(boothScale*6f,boothScale*3.5f,0f), m);
    TransformNode rightSpeakerTransform = new TransformNode("scale(" + boothScale*3f + "," + boothScale*7f + "," + boothScale*4f + ")" +
                                                         ";translate(" + boothScale*6f + "," + boothScale*3.5f + ", 0)", m);
    ModelNode cube2Booth = new ModelNode("cubeTS(right speaker)", model2);

    boothRoot.addChild(translateBooth);
      translateBooth.addChild(rotateBooth);
        rotateBooth.addChild(dj);
          dj.addChild(djTransform);
            djTransform.addChild(cube0Booth);
          dj.addChild(leftSpeaker);
            leftSpeaker.addChild(leftSpeakerTransform);
              leftSpeakerTransform.addChild(cube1Booth);
          dj.addChild(rightSpeaker);
            rightSpeaker.addChild(rightSpeakerTransform);
              rightSpeakerTransform.addChild(cube2Booth);


    boothRoot.update();
    return boothRoot;
  }
}
