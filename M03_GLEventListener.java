import gmaths.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;

public class M03_GLEventListener implements GLEventListener {

  private static final boolean DISPLAY_SHADERS = false;

  public M03_GLEventListener(Camera camera) {
    this.camera = camera;
    this.camera.setPosition(new Vec3(0f,17f,18f));
    this.camera.setTarget(new Vec3(0f,6f,-10f));
  }

  // ***************************************************
  /*
   * METHODS DEFINED BY GLEventListener
   */

  /* Initialisation */
  public void init(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    System.err.println("Chosen GLCapabilities: " + drawable.getChosenGLCapabilities());
    gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    gl.glClearDepth(1.0f);
    gl.glEnable(GL.GL_DEPTH_TEST);
    gl.glDepthFunc(GL.GL_LESS);
    gl.glFrontFace(GL.GL_CCW);    // default is 'CCW'
    gl.glEnable(GL.GL_CULL_FACE); // default is 'not enabled'
    gl.glCullFace(GL.GL_BACK);   // default is 'back', assuming CCW
    initialise(gl);
    startTime = getSeconds();
  }

  /* Called to indicate the drawing surface has been moved and/or resized  */
  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    GL3 gl = drawable.getGL().getGL3();
    gl.glViewport(x, y, width, height);
    float aspect = (float)width/(float)height;
    camera.setPerspectiveMatrix(Mat4Transform.perspective(45, aspect));
    spotCamera.setPerspectiveMatrix(Mat4Transform.perspective(1, 1));
  }

  /* Draw */
  public void display(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    render(gl);
  }

  /* Clean up memory, if necessary */
  public void dispose(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    light.dispose(gl);
    floor.dispose(gl);
    sphere.dispose(gl);
  }


  // ***************************************************
  /* INTERACTION
   *
   *
   */

   public void light1Off() {
     light.setBrightness(0.0f);
   }

   public void light1On() {
     light.setBrightness(0.5f);
   }

   public void light2Off() {
     light2.setBrightness(0.0f);
   }

   public void light2On() {
     light2.setBrightness(0.5f);
   }



   public void randomPose() {
     rotateAllAngleNew = rng(-30,55);
     rotateSphereBodyAngleNew = rng(-30,20);
     rotateUpperBranchAngleNew = rng(-80,0);
     rotateHeadAngleNew = rng(-50,50);
     pose = true;
     move = false;
     lampRoot.update();
   }

   public void originPose() {
     rotateAllAngleNew  = 35;
     rotateSphereBodyAngleNew = -30;
     rotateUpperBranchAngleNew = -40;
     rotateHeadAngleNew = 10;
     pose = true;
     move = false;
     lampRoot.update();
   }

   public void jumpingPose() {
     rotateAllAngleNew = 10;
     rotateSphereBodyAngleNew = -10;
     rotateUpperBranchAngleNew = -10;
     rotateHeadAngleNew = 50;
     pose = true;
     move = false;
     lampRoot.update();
   }

   public void buttonTime() {
     buttonTime = getSeconds();
   }

   public int rng(int min, int max) {
     int x = Math.round((float)(Math.random()*((max-min)+1))+min);
     return x;
   }

   public void jump() {
     int rngAngleX = rng((int)rotateFootAngleNew-45, (int)rotateFootAngleNew+45);
     int rngAngleZ = (rngAngleX - 90)*-1;

     double rngAngleXRad = Math.toRadians(rngAngleX);
     double rngAngleZRad = Math.toRadians(rngAngleZ);
     double angleCosX = Math.cos(rngAngleXRad);
     double angleCosZ = Math.cos(rngAngleZRad);

     double length = 1.0;
     lampTransXNew = (float)((angleCosX/length)*(Math.pow(length,2)));
     lampTransZNew = (float)((angleCosZ/length)*(Math.pow(length,2)));

     lampTransXNew += lampTransX;
     lampTransZNew = lampTransZ + (lampTransZNew*-1);

     rotateFootAngleNew = (float)rngAngleX;
     rotateAllAngleNew = 50;
     rotateSphereBodyAngleNew = -50;
     rotateUpperBranchAngleNew = -60;
     rotateHeadAngleNew = 70;
     lampTransY = 5.5f;

     move = true;
     pose = false;
     lampRoot.update();
   }

   public void originalPosition() {
     lampTransX = 0.5f;
     lampTransY = 5.5f;
     lampTransZ = -12.5f;
     rotateFootAngleNew = rotateFootAngle = rotateFootAngleStart;

     rotateFoot.setTransform(Mat4Transform.rotateAroundY(rotateFootAngle));
     translateLamp.setTransform(Mat4Transform.translate(lampTransX,lampTransY,lampTransZ));
     lampRoot.update();
   }

   public void turn180() {
     rotateFootAngleNew = rotateFootAngle - 180;
     lampRoot.update();
   }


  // ***************************************************
  /* THE SCENE
   * Now define all the methods to handle the scene.
   * This will be added to in later examples.
   */
  private Boolean pose = false;
  private Boolean move = false;
  private Boolean jumpV = false;

  private Camera camera, spotCamera;
  private Mat4 perspective;
  private Model floor, cubeWindow, danceFloor, sphere, sphereAfro, sphereLong;
  private Model cube, cubeBeige, cubeWood, cubeWoodLegs, cubeFloor, cubeBlackB, cubeTS, cubeDjBooth, cubeWall;
  private Light light, light2, spotLight;
  private SGNode lampRoot, glassesRoot, tableRoot, wallRoot, boothRoot, discoRoot;


  private TransformNode translateX, translateLamp;
  private TransformNode rotateFoot, rotateAll, rotateSphereBody, rotateUpperBranch, rotateHead;
  private TransformNode translateWall;
  private TransformNode translateTable;
  private TransformNode translateBooth, rotateBooth;
  private TransformNode translateDisco, rotateDisco;
  private float xPosition = 0;
  private float rotateFootAngleStart = -15, rotateFootAngle = rotateFootAngleStart;
  private float rotateAllAngleStart = 35, rotateAllAngle = rotateAllAngleStart;
  private float rotateSphereBodyStart = -30, rotateSphereBodyAngle = rotateSphereBodyStart;
  private float rotateUpperAngleStart = -40, rotateUpperBranchAngle = rotateUpperAngleStart;
  private float rotateHeadAngleStart = 10, rotateHeadAngle = rotateHeadAngleStart;
  private float rotateDiscoAngleStart = 10, rotateDiscoAngle = rotateDiscoAngleStart;
  private float rotateAllAngleNew, rotateSphereBodyAngleNew, rotateUpperBranchAngleNew, rotateHeadAngleNew;

  private float lampTransX = 0.5f;
  private float lampTransY = 5.5f;
  private float lampTransZ = -12.5f;

  private float lampTransXNew, lampTransYNew, lampTransZNew;

  private float rotateFootAngleNew = rotateFootAngleStart;



  private void initialise(GL3 gl) {
    createRandomNumbers();
    int[] textureId0 = TextureLibrary.loadTexture(gl, "textures/carpet.jpg");
    int[] textureId1 = TextureLibrary.loadTexture(gl, "textures/leather.jpg");
    int[] textureId2 = TextureLibrary.loadTexture(gl, "textures/red.jpg");
    int[] textureId3 = TextureLibrary.loadTexture(gl, "textures/disco.jpg");
    int[] textureId4 = TextureLibrary.loadTexture(gl, "textures/glitterred.jpg");
    int[] textureId5 = TextureLibrary.loadTexture(gl, "textures/beige.jpg");
    int[] textureId6 = TextureLibrary.loadTexture(gl, "textures/grayafro.jpg");
    int[] textureId7 = TextureLibrary.loadTexture(gl, "textures/wood.jpg");
    int[] textureId8 = TextureLibrary.loadTexture(gl, "textures/moon.jpg");
    int[] textureId9 = TextureLibrary.loadTexture(gl, "textures/dancefloor.jpg");
    int[] textureId10 = TextureLibrary.loadTexture(gl, "textures/container2_specular.jpg");
    int[] textureId11 = TextureLibrary.loadTexture(gl, "textures/speaker.jpg");
    int[] textureId12 = TextureLibrary.loadTexture(gl, "textures/dj_booth.jpg");
    int[] textureId13 = TextureLibrary.loadTexture(gl, "textures/wall.jpg");
    // Constant Variables

    // Lamp
    float lampScale = 0.3f;

    float footLength = 5f * lampScale;
    float footHeight = 1.4f * lampScale;
    float footDepth = 3f * lampScale;

    float branchLength = 7f * lampScale;
    float branchThick = 1f * lampScale;
    float lowBTransX = 0f * lampScale;
    float lowBTransY = 1f * lampScale;
    float lowBTransZ = 0f * lampScale;
    float upBTransX = 0f * lampScale;
    float upBTransY = 0.5f * lampScale;
    float upBTransZ = 0f * lampScale;

    float sphereScale = 1.4f * lampScale;
    float sphereTransX = -0.10f * lampScale;
    float sphereTransY = 7.50f * lampScale;
    float sphereTransZ = 0f * lampScale;

    float headLength = 4f * lampScale;
    float headHeight = 2.5f * lampScale;
    float headDepth = 2.5f * lampScale;
    float headTransX = 0.5f * lampScale;
    float headTransY = 7f * lampScale;
    float headTransZ = 0f * lampScale;

    float afroScale = 6f * lampScale;
    float afroTransX = -1f * lampScale;
    float afroTransY = 2f * lampScale;
    float afroTransZ = 0f * lampScale;

    float glassesTransX = 2f * lampScale;
    float glassesTransY = 2f * lampScale;
    float glassesTransZ = 0f * lampScale;
    float glassesScale = 0.15f;

    // Floor
    float floorSize = 40f;

    // Table
    float tableLength = 25f;
    float tableDepth = 15f;

    // DJ Booth
    float boothScale = 0.8f;
    float djHeight = 3f * boothScale;

    // Disco Ball
    float discoScale = 0.4f;
    float discoBaseHeight = 1f * discoScale;

    // Dance Floor
    float danceFloorScale = 0.3f;



    light = new Light(gl);
    light.setCamera(camera);

    light2 = new Light(gl);
    light2.setCamera(camera);

    spotCamera = new Camera(Camera.DEFAULT_POSITION, Camera.DEFAULT_TARGET, Camera.DEFAULT_UP);
    spotLight = new Light(gl);
    spotLight.setCamera(spotCamera);



    Mesh mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    Shader shader = new Shader(gl, "vs_tt_05.txt", "fs_tt_uncoloured.txt");
    Material material = new Material(new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.3f, 0.3f, 0.3f), 32.0f);
    Mat4 modelMatrix = Mat4Transform.scale(floorSize,1f,floorSize);
    floor = new Model(gl, camera, light, light2, spotLight, shader, material, modelMatrix, mesh, textureId0);



    mesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
    shader = new Shader(gl, "vs_cube_04.txt", "fs_cube_04.txt");
    material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(4,4,4), Mat4Transform.translate(0,0.5f,0));
    sphere = new Model(gl, camera, light, light2, spotLight, shader, material, modelMatrix, mesh, textureId3, textureId3);
    sphereLong = new Model(gl, camera, light, light2, spotLight, shader, material, modelMatrix, mesh, textureId2, textureId3);

    material = new Material(new Vec3(0.1f, 0.1f, 0.1f), new Vec3(0.1f, 0.1f, 0.1f), new Vec3(0.1f, 0.1f, 0.1f), 100.0f);
    sphereAfro = new Model(gl, camera, light, light2, spotLight, shader, material, modelMatrix, mesh, textureId6);


    mesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
    shader = new Shader(gl, "vs_cube_04.txt", "fs_cube_04.txt");
    material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(4,4,4), Mat4Transform.translate(0,0.5f,0));
    cubeWood = new Model(gl, camera, light, light2, spotLight, shader, material, modelMatrix, mesh, textureId7);
    cube = new Model(gl, camera, light, light2, spotLight, shader, material, modelMatrix, mesh, textureId1);
    cubeBeige = new Model(gl, camera, light, light2, spotLight, shader, material, modelMatrix, mesh, textureId5);
    cubeFloor = new Model(gl, camera, light, light2, spotLight, shader, material, modelMatrix, mesh, textureId0);
    cubeBlackB = new Model(gl, camera, light, light2, spotLight, shader, material, modelMatrix, mesh, textureId10);
    cubeWall = new Model(gl, camera, light, light2, spotLight, shader, material, modelMatrix, mesh, textureId13);
    cubeWindow = new Model(gl, camera, light, light2, spotLight, shader, material, modelMatrix, mesh, textureId8);

    mesh = new Mesh(gl, CubeTableLeg.vertices.clone(), CubeTableLeg.indices.clone());
    cubeWoodLegs = new Model(gl, camera, light, light2, spotLight, shader, material, modelMatrix, mesh, textureId7);

    mesh = new Mesh(gl, CubeSpeaker.vertices.clone(), CubeSpeaker.indices.clone());
    cubeTS = new Model(gl, camera, light, light2, spotLight, shader, material, modelMatrix, mesh, textureId11);

    mesh = new Mesh(gl, CubeBooth.vertices.clone(), CubeBooth.indices.clone());
    cubeDjBooth = new Model(gl, camera, light, light2, spotLight, shader, material, modelMatrix, mesh, textureId12);

    mesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
    Mat4 m = Mat4Transform.scale(danceFloorScale*30f,danceFloorScale*0.5f,danceFloorScale*30f);
    modelMatrix = Mat4.multiply(Mat4Transform.translate(0,5.5f,-12.5f), m);
    danceFloor = new Model(gl, camera, light, light2, spotLight, shader, material, modelMatrix, mesh, textureId9);





    // LAMP ROOT //
    // LAMP ROOT //
    // LAMP ROOT //
    lampRoot = new NameNode("lamp structure");
    translateLamp = new TransformNode("translate(0,0,0)", Mat4Transform.translate(lampTransX,lampTransY,lampTransZ));
    rotateFoot = new TransformNode("rotateAroundY("+rotateFootAngle+")", Mat4Transform.rotateAroundY(rotateFootAngle));

    // FOOT //
    // FOOT //
    // FOOT //
    NameNode lampFoot = new NameNode("lamp foot");
    m = Mat4Transform.scale(footLength,footHeight,footDepth);
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode lampFootTransform = new TransformNode("scale(3,1.4,1.4);translate(0,0.5,0)", m);
    ModelNode cube0Node = new ModelNode("Cube(foot)", cube);

    //  LOWER BRANCH //
    //  LOWER BRANCH //
    //  LOWER BRANCH //
    NameNode lowerBranch = new NameNode("lower branch");
    TransformNode translateAboveFoot = new TransformNode("translate(0,1,0)",Mat4Transform.translate(lowBTransX,lowBTransY,lowBTransZ));
    rotateAll = new TransformNode("rotateAroundZ("+rotateAllAngle+")", Mat4Transform.rotateAroundZ(rotateAllAngle));

    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.scale(branchThick,branchLength,branchThick));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode lowerBranchTransform = new TransformNode("scale(1,7,1); translate(0,0.5,0)", m);
    ModelNode cube1Node = new ModelNode("SpherLong(lower branch)", sphereLong);

    //  SPHERE BODY //
    //  SPHERE BODY //
    //  SPHERE BODY //
    TransformNode translateAboveLowBranch = new TransformNode("translate(-0.10,7.50,0)",Mat4Transform.translate(sphereTransX,sphereTransY,sphereTransZ));
    rotateSphereBody = new TransformNode("rotateAroundZ("+rotateSphereBodyAngle+")",Mat4Transform.rotateAroundZ(rotateSphereBodyAngle));

    NameNode sphereBody = new NameNode("sphere body");
    m = Mat4Transform.scale(sphereScale,sphereScale,sphereScale);
    TransformNode sphereBodyTransform = new TransformNode("scale(1.4f,1.4f,1.4f);translate(0,0.5,0)", m);
    ModelNode cube2Node = new ModelNode("Sphere(sphere body)", sphere);

    //  UPPER BRANCH //
    //  UPPER BRANCH //
    //  UPPER BRANCH //
    TransformNode translateAboveSphereBody = new TransformNode("translate(0.5,0.5,0)",Mat4Transform.translate(upBTransX,upBTransY,upBTransZ));
    rotateUpperBranch = new TransformNode("rotateAroundZ("+rotateUpperBranchAngle+")",Mat4Transform.rotateAroundZ(rotateUpperBranchAngle));

    NameNode upperBranch = new NameNode("upper branch");
    m = Mat4Transform.scale(branchThick,branchLength,branchThick);
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode upperBranchTransform = new TransformNode("scale(1,7,1); translate(0,0.5,0)", m);
    ModelNode cube3Node = new ModelNode("SphereLong(upper branch)", sphereLong);

    // HEAD //
    // HEAD //
    // HEAD //
    TransformNode translateAboveUpperBranch = new TransformNode("translate(0.5,7,0)",Mat4Transform.translate(headTransX,headTransY,headTransZ));
    rotateHead = new TransformNode("rotateAroundZ("+rotateHeadAngle+")",Mat4Transform.rotateAroundZ(rotateHeadAngle));

    NameNode lampHead = new NameNode("head");
    m = Mat4Transform.scale(headLength,headHeight,headDepth);
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode lampHeadTransform = new TransformNode("scale(4,2.5,2.5);translate(0,0.5,0)", m);
    ModelNode cube4Node = new ModelNode("Cube2(head)", cubeBeige);

    // AFRO //
    // AFRO //
    // AFRO //
    TransformNode translateAboveHead = new TransformNode("translate(0.5,7,0)",Mat4Transform.translate(afroTransX,afroTransY,afroTransZ));

    NameNode afro = new NameNode("afro");
    m = Mat4Transform.scale(afroScale,afroScale,afroScale);
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode afroTransform = new TransformNode("scale(5,5,5);translate(0,0.5,0)", m);
    ModelNode cube5Node = new ModelNode("Sphere2(5)", sphereAfro);

    // LIGHT //
    // LIGHT //
    // LIGHT //
    float lightTransX =(lampScale*3f)+(0.5f);
    float lightTransY =(lampScale*16.6f)-1+(5.5f);
    float lightTransZ =(lampScale*0f)+(-12.5f);
    TransformNode translateFrontHead = new TransformNode("translate(0.5,7,0)",Mat4Transform.translate(0.5f,0.2f,0));

    NameNode lampLight = new NameNode("lamp light");
    m = Mat4Transform.scale(0.3f,0.3f,0.5f);
    m = Mat4.multiply(m, Mat4Transform.translate(0,0,0));
    TransformNode lampLightTransform = new TransformNode("scale(5,5,5);translate(0,0.5,0)", m);
    LightNode cube6Node = new LightNode("Light(6)", spotLight);

    // CAMERA //
    // CAMERA //
    // CAMERA //
    // NameNode lampCamera = new NameNode("lamp camera");
    // CameraNode cube7Node = new CameraNode("Light(6)", spotCamera);







    //GLASSES ROOT//
    //GLASSES ROOT//
    //GLASSES ROOT//
    glassesRoot = new NameNode("Glasses structure");
    TransformNode translateGlasses = new TransformNode("translate(0.5,7,0)",Mat4Transform.translate(glassesTransX,glassesTransY,glassesTransZ));

    //LEFT LENS//
    //LEFT LENS//
    //LEFT LENS//
    NameNode leftLens = new NameNode("leftLens");
    m = Mat4Transform.scale(glassesScale*0.25f,glassesScale*3,glassesScale*3);
    m = Mat4.multiply(Mat4Transform.translate(0,0f,glassesScale*1.5f), m);
    TransformNode leftLensTransform = new TransformNode("translate(0,5,-5);scale(14,1,10)", m);
    ModelNode sphere0NodeG = new ModelNode("Spere(table body)", sphere);

    //RIGHT LENS//
    //RIGHT LENS//
    //RIGHT LENS//
    NameNode rightLens = new NameNode("rightLens");
    m = Mat4Transform.scale(glassesScale*0.25f,glassesScale*3,glassesScale*3);
    m = Mat4.multiply(Mat4Transform.translate(0,0f,glassesScale*-1.5f), m);
    TransformNode rightLensTransform = new TransformNode("translate(0,5,-5);scale(14,1,10)", m);
    ModelNode sphere1NodeG = new ModelNode("Sphere(table body)", sphere);

    //MIDDLE BRIDGE//
    //MIDDLE BRIDGE//
    //MIDDLE BRIDGE//
    NameNode middleBridge = new NameNode("middleBridge");
    m = Mat4Transform.scale(glassesScale*0.25f,glassesScale*0.5f,glassesScale*0.5f);
    m = Mat4.multiply(Mat4Transform.translate(0,0f,0), m);
    TransformNode middleBridgeTransform = new TransformNode("translate(0,5,-5);scale(14,1,10)", m);
    ModelNode cube3NodeG = new ModelNode("Cube(table body)", cubeWall);

    //LEFT ARM//
    //LEFT ARM//
    //LEFT ARM//
    NameNode leftArm = new NameNode("leftArm");
    m = Mat4Transform.scale(glassesScale*3f,glassesScale*0.5f,glassesScale*0.25f);
    m = Mat4.multiply(Mat4Transform.translate(glassesScale*-1.5f,0f,glassesScale*2.75f), m);
    TransformNode leftArmTransform = new TransformNode("translate(0,5,-5);scale(14,1,10)", m);
    ModelNode cube5NodeG = new ModelNode("Cube(table body)", cubeWall);

    //RIGHT ARM//
    //RIGHT ARM//
    //RIGHT ARM//
    NameNode rightArm = new NameNode("rightArm");
    m = Mat4Transform.scale(glassesScale*3f,glassesScale*0.5f,glassesScale*0.25f);
    m = Mat4.multiply(Mat4Transform.translate(glassesScale*-1.5f,0f,glassesScale*-2.75f), m);
    TransformNode rightArmTransform = new TransformNode("translate(0,5,-5);scale(14,1,10)", m);
    ModelNode cube6NodeG = new ModelNode("Cube(table body)", cubeWall);

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


    // lampRoot
    lampRoot.addChild(translateLamp);
      translateLamp.addChild(rotateFoot);
        rotateFoot.addChild(lampFoot);
          lampFoot.addChild(lampFootTransform);
            lampFootTransform.addChild(cube0Node);
          lampFoot.addChild(translateAboveFoot);
            translateAboveFoot.addChild(rotateAll);
              rotateAll.addChild(lowerBranch);
                lowerBranch.addChild(lowerBranchTransform);
                  lowerBranchTransform.addChild(cube1Node);
                lowerBranch.addChild(translateAboveLowBranch);
                  translateAboveLowBranch.addChild(rotateSphereBody);
                    rotateSphereBody.addChild(sphereBody);
                      sphereBody.addChild(sphereBodyTransform);
                        sphereBodyTransform.addChild(cube2Node);
                      sphereBody.addChild(translateAboveSphereBody);
                        translateAboveSphereBody.addChild(rotateUpperBranch);
                          rotateUpperBranch.addChild(upperBranch);
                            upperBranch.addChild(upperBranchTransform);
                              upperBranchTransform.addChild(cube3Node);
                            upperBranch.addChild(translateAboveUpperBranch);
                              translateAboveUpperBranch.addChild(rotateHead);
                                rotateHead.addChild(lampHead);
                                  lampHead.addChild(lampHeadTransform);
                                    lampHeadTransform.addChild(cube4Node);
                                  lampHead.addChild(translateAboveHead);
                                    translateAboveHead.addChild(afro);
                                      afro.addChild(afroTransform);
                                        afroTransform.addChild(cube5Node);
                                  lampHead.addChild(translateGlasses);
                                    translateGlasses.addChild(glassesRoot);
                                  lampHead.addChild(translateFrontHead);
                                    translateFrontHead.addChild(lampLightTransform);
                                      lampLightTransform.addChild(lampLight);
                                        lampLight.addChild(cube6Node);
                                    // translateFrontHead.addChild(lampCamera);
                                    //   lampCamera.addChild(cube7Node);
    lampRoot.update();  // IMPORTANT – must be done every time any part of the scene graph changes
    // lampRoot.print(0, false);
    // System.exit(0);












    // TABLE ROOT //
    // TABLE ROOT //
    // TABLE ROOT //
    tableRoot = new NameNode("table structure");
    translateTable = new TransformNode("translate(0,0,0)", Mat4Transform.translate(0,0,(-0.5f*floorSize) + (tableDepth*0.5f)));

    // TABLE BODY //
    // TABLE BODY //
    // TABLE BODY //
    NameNode tableBody = new NameNode("tableBody");
    m = Mat4Transform.scale(tableLength,1,tableDepth);
    m = Mat4.multiply(Mat4Transform.translate(0,5f,0), m);
    TransformNode tableBodyTransform = new TransformNode("translate(0,5,-5);scale(14,1,10)", m);
    ModelNode cube0NodeT = new ModelNode("Cube(table body)", cubeWood);

    // TABLE LEG BACK LEFT //
    // TABLE LEG BACK LEFT //
    // TABLE LEG BACK LEFT //
    NameNode tableLegBL = new NameNode("tableLegBL");
    m = Mat4Transform.scale(1f,5f,1f);
    m = Mat4.multiply(Mat4Transform.translate((0.5f*-tableLength)+0.5f,2.0f,(0.5f*-tableDepth)+0.5f), m);
    TransformNode tableLegBLTransform = new TransformNode("translate(-6.5,2.5,-9.5);scale(1,5,1)", m);
    ModelNode cube1NodeT = new ModelNode("Cube(table leg back left)", cubeWood);

    // TABLE LEG BACK RIGHT //
    // TABLE LEG BACK RIGHT //
    // TABLE LEG BACK RIGHT //
    NameNode tableLegBR = new NameNode("tableLegBR");
    m = Mat4Transform.scale(1f,5f,1f);
    m = Mat4.multiply(Mat4Transform.translate((0.5f*tableLength)-0.5f,2.0f,(0.5f*-tableDepth)+0.5f), m);
    TransformNode tableLegBRTransform = new TransformNode("scale(10,10,10);translate(0,0,5)", m);
    ModelNode cube2NodeT = new ModelNode("Cube(table leg back right)", cubeWood);

    // TABLE LEG FRONT RIGHT //
    // TABLE LEG FRONT RIGHT //
    // TABLE LEG FRONT RIGHT //
    NameNode tableLegFR = new NameNode("tableLegFR");
    m = Mat4Transform.scale(1f,5f,1f);
    m = Mat4.multiply(Mat4Transform.translate((0.5f*tableLength)-0.5f,2.0f,(0.5f*tableDepth)-0.5f), m);
    TransformNode tableLegFRTransform = new TransformNode("scale(10,10,10);translate(0,0,5)", m);
    ModelNode cube3NodeT = new ModelNode("Cube(table leg front right)", cubeWood);

    // TABLE LEG FRONT LEFT //
    // TABLE LEG FRONT LEFT //
    // TABLE LEG FRONT LEFT //
    NameNode tableLegFL = new NameNode("tableLegFL");
    m = Mat4Transform.scale(1f,5f,1f);
    m = Mat4.multiply(Mat4Transform.translate((0.5f*-tableLength)+0.5f,2.0f,(0.5f*tableDepth)-0.5f), m);
    TransformNode tableLegFLTransform = new TransformNode("scale(10,10,10);translate(0,0,5)", m);
    ModelNode cube4NodeT = new ModelNode("Cube(table leg front left)", cubeWood);


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

    // WALL ROOT //
    // WALL ROOT //
    // WALL ROOT //
    wallRoot = new NameNode("wall structure");
    translateWall = new TransformNode("translate(0,0,0)", Mat4Transform.translate(0,0,-floorSize*0.25f));

    // BOTTOM //
    // BOTTOM //
    // BOTTOM //
    NameNode bottomWall = new NameNode("bottomWall");
    m = Mat4Transform.scale(14f,6f,0f);
    m = Mat4.multiply(Mat4Transform.translate(0f,3f,-10f), m);
    TransformNode bottomWallTransform = new TransformNode("scale(10,10,10);translate(0,0,5)", m);
    ModelNode cube0NodeW = new ModelNode("Cube(bottom wall)", cubeWall);

    // LEFT //
    // LEFT //
    // LEFT //
    NameNode leftWall = new NameNode("leftWall");
    m = Mat4Transform.scale(13f,20f,0f);
    m = Mat4.multiply(Mat4Transform.translate(-floorSize/2 + 6.5f,10f,-10f), m);
    TransformNode leftWallTransform = new TransformNode("scale(10,10,10);translate(0,0,5)", m);
    ModelNode cube1NodeW = new ModelNode("Cube(left wall)", cubeWall);

    // RIGHT //
    // RIGHT //
    // RIGHT //
    NameNode rightWall = new NameNode("rightWall");
    m = Mat4Transform.scale(13f,20f,0f);
    m = Mat4.multiply(Mat4Transform.translate(floorSize/2 - 6.5f,10f,-10f), m);
    TransformNode rightWallTransform = new TransformNode("scale(10,10,10);translate(0,0,5)", m);
    ModelNode cube2NodeW = new ModelNode("Cube(right wall)", cubeWall);

    // TOP //
    // TOP //
    // TOP //
    NameNode topWall = new NameNode("topWall");
    m = Mat4Transform.scale(14f,3f,0f);
    m = Mat4.multiply(Mat4Transform.translate(0f,18.5f,-10f), m);
    TransformNode topWallTransform = new TransformNode("scale(10,10,10);translate(0,0,5)", m);
    ModelNode cube3NodeW = new ModelNode("Cube(top wall)", cubeWall);

    // CENTRAL //
    // CENTRAL //
    // CENTRAL //
    NameNode centralWall = new NameNode("centralWall");
    m = Mat4Transform.scale(14f,11f,0f);
    m = Mat4.multiply(Mat4Transform.translate(0f,11.5f,-10f), m);
    TransformNode centralWallTransform = new TransformNode("scale(10,10,10);translate(0,0,5)", m);
    ModelNode cube4NodeW = new ModelNode("Cube(central wall)", cubeWindow);

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




    // BOOTH ROOT //
    // BOOTH ROOT //
    // BOOTH ROOT //
    boothRoot = new NameNode("booth structure");
    translateBooth = new TransformNode("translate(0,0,0)", Mat4Transform.translate(10f,5.5f,-12.5f));
    rotateBooth = new TransformNode("rotateAroundY(90)", Mat4Transform.rotateAroundY(90f));

    // DJ //
    // DJ //
    // DJ //
    NameNode dj = new NameNode("dj");
    m = Mat4Transform.scale(boothScale*9f,boothScale*3f,boothScale*4f);
    m = Mat4.multiply(Mat4Transform.translate(0f,boothScale*1.5f,0f), m);
    TransformNode djTransform = new TransformNode("scale(10,10,10);translate(0,0,5)", m);
    ModelNode cube0Booth = new ModelNode("Cube(dj)", cubeDjBooth);

    // LEFT SPEAKER //
    // LEFT SPEAKER //
    // LEFT SPEAKER //
    NameNode leftSpeaker = new NameNode("leftSpeaker");
    m = Mat4Transform.scale(boothScale*3f,boothScale*7f,boothScale*4f);
    m = Mat4.multiply(Mat4Transform.translate(boothScale*-6f,boothScale*3.5f,0f), m);
    TransformNode leftSpeakerTransform = new TransformNode("scale(10,10,10);translate(0,0,5)", m);
    ModelNode cube1Booth = new ModelNode("Cube(left speaker)", cubeTS);

    // RIGHT SPEAKER //
    // RIGHT SPEAKER //
    // RIGHT SPEAKER //
    NameNode rightSpeaker = new NameNode("rightSpeaker");
    m = Mat4Transform.scale(boothScale*3f,boothScale*7f,boothScale*4f);
    m = Mat4.multiply(Mat4Transform.translate(boothScale*6f,boothScale*3.5f,0f), m);
    TransformNode rightSpeakerTransform = new TransformNode("scale(10,10,10);translate(0,0,5)", m);
    ModelNode cube2Booth = new ModelNode("Cube(right speaker)", cubeTS);

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



    // DISCO BALL ROOT //
    // DISCO BALL ROOT //
    // DISCO BALL ROOT //
    discoRoot = new NameNode("disco structure");
    translateDisco = new TransformNode("translate(0,0,0)", Mat4Transform.translate(-9f,5.5f,-14.5f));

    // DISCO BASE //
    // DISCO BASE //
    // DISCO BASE //
    NameNode discoBase = new NameNode("discoBase");
    m = Mat4Transform.scale(discoScale*6f,discoScale*1f,discoScale*3f);
    m = Mat4.multiply(Mat4Transform.translate(0f,discoScale*0.5f,0f), m);
    TransformNode discoBaseTransform = new TransformNode("scale(10,10,10);translate(0,0,5)", m);
    ModelNode cube0Disco = new ModelNode("Cube(discoBase)", cubeBlackB);

    // DISCO STAND VERTICAL //
    // DISCO STAND VERTICAL //
    // DISCO STAND VERTICAL //
    NameNode discoStandV = new NameNode("discoStandV");
    m = Mat4Transform.scale(discoScale*0.1f,discoScale*16f,discoScale*0.1f);
    m = Mat4.multiply(Mat4Transform.translate(0f,discoScale*8.5f,0f), m);
    TransformNode discoStandVTransform = new TransformNode("scale(10,10,10);translate(0,0,5)", m);
    ModelNode cube1Disco = new ModelNode("Cube(discoStandV)", cubeFloor);

    // DISCO STAND HORIZONTAL //
    // DISCO STAND HORIZONTAL //
    // DISCO STAND HORIZONTAL //
    NameNode discoStandH = new NameNode("discoStandH");
    m = Mat4Transform.scale(discoScale*5f,discoScale*0.1f,discoScale*0.1f);
    m = Mat4.multiply(Mat4Transform.translate(discoScale*2.5f,discoScale*16.5f,0f), m);
    TransformNode discoStandHTransform = new TransformNode("scale(10,10,10);translate(0,0,5)", m);
    ModelNode cube2Disco = new ModelNode("Cube(discoStandH)", cubeFloor);

    // DISCO STRING //
    // DISCO STRING //
    // DISCO STRING //
    NameNode discoString = new NameNode("discoString");
    m = Mat4Transform.scale(discoScale*0.1f,discoScale*1f,discoScale*0.1f);
    m = Mat4.multiply(Mat4Transform.translate(discoScale*5f,discoScale*16f,0f), m);
    TransformNode discoStringTransform = new TransformNode("scale(10,10,10);translate(0,0,5)", m);
    ModelNode cube3Disco = new ModelNode("Cube(discoString)", cubeFloor);

    // DISCO BALL //
    // DISCO BALL //
    // DISCO BALL //
    TransformNode translateBelowString = new TransformNode("translate(-0.10,7.50,0)",Mat4Transform.translate(discoScale*5f,discoScale*13.5f,0f));

    rotateDisco = new TransformNode("rotateAroundY("+rotateHeadAngle+")",Mat4Transform.rotateAroundY(rotateHeadAngle));
    NameNode discoBall = new NameNode("discoBall");
    m = Mat4Transform.scale(discoScale*4f,discoScale*4f,discoScale*4f);
    m = Mat4.multiply(Mat4Transform.translate(0f,0f,0f), m);
    TransformNode discoBallTransform = new TransformNode("scale(10,10,10);translate(0,0,5)", m);
    ModelNode cube4Disco = new ModelNode("Cube(discoBall)", sphere);

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






    //table
    // tableRoot.
  }

  private void render(GL3 gl) {
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

    double radA = Math.toRadians(rotateFootAngle);
    float cosA = (float)Math.cos(radA);
    float sinA = (float)Math.sin(radA);


    spotCamera.setPosition(new Vec3(lampTransX+1f - 20*cosA,lampTransY+4f,lampTransZ + 20*sinA));
    spotCamera.setTarget(new Vec3(lampTransX+4f,lampTransY, -12.5f));



    // light.setPosition(getLightPosition());  // changing light position each frame
    light.setPosition(new Vec3(15f,15f,15f));
    light.render(gl);
    light2.setPosition(new Vec3(-15f,15f,15f));
    light2.render(gl);
    spotLight.setSpotPosition(new Vec3(lampTransX+1f,lampTransY+4f, lampTransZ)); // spotlight source pos not model pos
    floor.render(gl);
    lampRoot.draw(gl);
    tableRoot.draw(gl);
    wallRoot.draw(gl);
    boothRoot.draw(gl);
    discoRoot.draw(gl);
    danceFloor.render(gl);
    updateBranches();

    if (pose) {
      updatePose();
    }

    if (move) {
      updateFootAngle();
    }

    if (jumpV) {
      jumpVertical();
      jumpHorizontal();
    }

  }

  private void updateBranches() {
    double elapsedTime = getSeconds()-startTime;
    rotateDiscoAngle = rotateDiscoAngleStart + (float)elapsedTime*50;
    rotateDisco.setTransform(Mat4Transform.rotateAroundY(rotateDiscoAngle));
    discoRoot.update();
  }

  private void updateFootAngle() {
    double elapsedTime = getSeconds()-buttonTime;
    rotateFootAngle = poseCalculation(rotateFootAngle, rotateFootAngleNew, elapsedTime);
    rotateAllAngle = poseCalculation(rotateAllAngle, rotateAllAngleNew, elapsedTime);
    rotateSphereBodyAngle = poseCalculation(rotateSphereBodyAngle, rotateSphereBodyAngleNew, elapsedTime);
    rotateUpperBranchAngle = poseCalculation(rotateUpperBranchAngle, rotateUpperBranchAngleNew, elapsedTime);
    rotateHeadAngle = poseCalculation(rotateHeadAngle, rotateHeadAngleNew, elapsedTime);
    rotateAll.setTransform(Mat4Transform.rotateAroundZ(rotateAllAngle));
    rotateSphereBody.setTransform(Mat4Transform.rotateAroundZ(rotateSphereBodyAngle));
    rotateUpperBranch.setTransform(Mat4Transform.rotateAroundZ(rotateUpperBranchAngle));
    rotateHead.setTransform(Mat4Transform.rotateAroundZ(rotateHeadAngle));
    rotateFoot.setTransform(Mat4Transform.rotateAroundY(rotateFootAngle));

    if (rotateFootAngle == rotateFootAngleNew) {
      move = false;
      jumpV = true;
      jumpTime = getSeconds();
    }
    lampRoot.update();
  }

  private void jumpVertical() {
    double elapsedTime = getSeconds()- jumpTime;

    if (lampTransY >= 5.5f) {
      jumpingPose();
      lampTransY = lampTransY + (lampTransY*(float)Math.sin(elapsedTime*10f))/50;
      System.out.println(lampTransY);
      if (lampTransY < 5.5f) {
        lampTransY = 5.49f;
        originPose();
        buttonTime = getSeconds() - 0.5;
        jumpV = false;
      }
      translateLamp.setTransform(Mat4Transform.translate(lampTransX,lampTransY,lampTransZ));
    }
    lampRoot.update();
  }

  private void jumpHorizontal() {
    double elapsedTime = getSeconds()- jumpTime;
    double speed = 0.15;
    if (lampTransX > (lampTransXNew - 0.2) && lampTransX < (lampTransXNew + 0.2)) {
      lampTransX = lampTransXNew;
    }
    else if (lampTransX < lampTransXNew) {
      lampTransX += (float)elapsedTime*speed;
    }
    else if (lampTransX > lampTransXNew) {
      lampTransX -= (float)elapsedTime*speed;
    }

    if (lampTransZ > (lampTransZNew - 0.2) && lampTransZ < (lampTransZNew + 0.2)) {
      lampTransZ = lampTransZNew;
    }
    else if (lampTransZ < lampTransZNew) {
      lampTransZ += (float)elapsedTime*speed;
    }
    else if (lampTransZ > lampTransZNew) {
      lampTransZ -= (float)elapsedTime*speed;
    }

    // Invisible walls
    if (lampTransX > 7) {
      lampTransX = 7;
      turn180();
    }
    if (lampTransX < -8 ) {
      lampTransX = -8;
      turn180();
    }
    if (lampTransZ > -6) {
      lampTransZ = -6;
      turn180();
    }
    if (lampTransZ < -18) {
      lampTransZ = -18;
      turn180();
    }

    translateLamp.setTransform(Mat4Transform.translate(lampTransX,lampTransY,lampTransZ));
    lampRoot.update();
  }

  private void updatePose() {

    double elapsedTime = getSeconds()-buttonTime;

    rotateFootAngle = poseCalculation(rotateFootAngle, rotateFootAngleNew, elapsedTime);
    rotateAllAngle = poseCalculation(rotateAllAngle, rotateAllAngleNew, elapsedTime);
    rotateSphereBodyAngle = poseCalculation(rotateSphereBodyAngle, rotateSphereBodyAngleNew, elapsedTime);
    rotateUpperBranchAngle = poseCalculation(rotateUpperBranchAngle, rotateUpperBranchAngleNew, elapsedTime);
    rotateHeadAngle = poseCalculation(rotateHeadAngle, rotateHeadAngleNew, elapsedTime);
    rotateAll.setTransform(Mat4Transform.rotateAroundZ(rotateAllAngle));
    rotateSphereBody.setTransform(Mat4Transform.rotateAroundZ(rotateSphereBodyAngle));
    rotateUpperBranch.setTransform(Mat4Transform.rotateAroundZ(rotateUpperBranchAngle));
    rotateHead.setTransform(Mat4Transform.rotateAroundZ(rotateHeadAngle));
    rotateFoot.setTransform(Mat4Transform.rotateAroundY(rotateFootAngle));

    lampRoot.update(); // IMPORTANT – the scene graph has changed
  }

  private float poseCalculation(float startAngle, float newAngle, double time) {
    if (startAngle <= newAngle-3) {
      startAngle = startAngle + (float)(time*6);
    }
    else if (startAngle >= newAngle+3) {
      startAngle = startAngle - (float)(time*6);
    }
    else if (startAngle >= (newAngle-3) && startAngle <= (newAngle+3)) {
      startAngle = newAngle;
    }
    return startAngle;
  }

  // The light's postion is continually being changed, so needs to be calculated for each frame.
  private Vec3 getLightPosition() {
    double elapsedTime = getSeconds()-startTime;
    float x = 5.0f*(float)(Math.sin(Math.toRadians(elapsedTime*50)));
    float y = 2.7f;
    float z = 5.0f*(float)(Math.cos(Math.toRadians(elapsedTime*50)));
    return new Vec3(x,y,z);
  }

  // ***************************************************
  /* TIME
   */

  private double startTime;
  private double buttonTime;
  private double jumpTime;

  private double getSeconds() {
    return System.currentTimeMillis()/1000.0;
  }

  // ***************************************************
  /* An array of random numbers
   */

  private int NUM_RANDOMS = 1000;
  private float[] randoms;

  private void createRandomNumbers() {
    randoms = new float[NUM_RANDOMS];
    for (int i=0; i<NUM_RANDOMS; ++i) {
      randoms[i] = (float)Math.random();
    }
  }

}
