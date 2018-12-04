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

   public void spotLightOff() {
     spotLight.setBrightness(0.0f);
     lampRoot.update();
   }

   public void spotLightOn() {
     spotLight.setBrightness(2f);
     lampRoot.update();
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
     rotateHeadAngleNew = -30; // 50
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
     // Squatting pose
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
  private Light light, light2, lightMouth, spotLight;
  private SGNode lampRoot, glassesRoot, tableRoot, wallRoot, boothRoot, discoRoot;
  private DiscoBall disco;
  private Table table;
  private Wall wall;
  private DjBooth booth;
  private Glasses glasses;


  private TransformNode translateLamp;
  private TransformNode rotateFoot, rotateAll, rotateSphereBody, rotateUpperBranch, rotateHead;
  private float rotateFootAngleStart = -15, rotateFootAngle = rotateFootAngleStart;
  private float rotateAllAngleStart = 35, rotateAllAngle = rotateAllAngleStart;
  private float rotateSphereBodyStart = -30, rotateSphereBodyAngle = rotateSphereBodyStart;
  private float rotateUpperAngleStart = -40, rotateUpperBranchAngle = rotateUpperAngleStart;
  private float rotateHeadAngleStart = 10, rotateHeadAngle = rotateHeadAngleStart;
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
    int[] textureId8 = TextureLibrary.loadTexture(gl, "textures/moon2.jpg");
    int[] textureId9 = TextureLibrary.loadTexture(gl, "textures/dancefloor.jpg");
    int[] textureId10 = TextureLibrary.loadTexture(gl, "textures/container2_specular.jpg");
    int[] textureId11 = TextureLibrary.loadTexture(gl, "textures/speaker.jpg");
    int[] textureId12 = TextureLibrary.loadTexture(gl, "textures/dj_booth.jpg");
    int[] textureId13 = TextureLibrary.loadTexture(gl, "textures/wall.jpg");
    int[] textureId14 = TextureLibrary.loadTexture(gl, "textures/cloudy2.jpg");
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
    Mat4 modelMatrix = Mat4Transform.scale(40f,1f,40f);
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
    material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(4,4,4), Mat4Transform.translate(0,0.5f,0));
    shader = new Shader(gl, "vs_cube_moving.txt", "fs_cube_moving.txt");
    cubeWindow = new Model(gl, camera, light, light2, spotLight, shader, material, modelMatrix, mesh, textureId8, textureId14);

    shader = new Shader(gl, "vs_cube_04.txt", "fs_cube_04.txt");
    cubeWood = new Model(gl, camera, light, light2, spotLight, shader, material, modelMatrix, mesh, textureId7);
    cube = new Model(gl, camera, light, light2, spotLight, shader, material, modelMatrix, mesh, textureId1);
    cubeBeige = new Model(gl, camera, light, light2, spotLight, shader, material, modelMatrix, mesh, textureId5);
    cubeFloor = new Model(gl, camera, light, light2, spotLight, shader, material, modelMatrix, mesh, textureId0);
    cubeBlackB = new Model(gl, camera, light, light2, spotLight, shader, material, modelMatrix, mesh, textureId10);
    cubeWall = new Model(gl, camera, light, light2, spotLight, shader, material, modelMatrix, mesh, textureId13);


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
    TransformNode translateFrontHead = new TransformNode("translate(0.5,7,0)",Mat4Transform.translate(0.5f,0.2f,0));

    NameNode lampLight = new NameNode("lamp light");
    m = Mat4Transform.scale(0.3f,0.3f,0.5f);
    m = Mat4.multiply(m, Mat4Transform.translate(0,0,0));
    TransformNode lampLightTransform = new TransformNode("scale(5,5,5);translate(0,0.5,0)", m);
    LightNode cube6Node = new LightNode("Light(6)", spotLight);



    lightMouth = new Light(gl);
    lightMouth.setCamera(camera);
    NameNode lampLightMouth = new NameNode("lamp light");
    m = Mat4Transform.scale(0.3f,0.3f,0.5f);
    m = Mat4.multiply(m, Mat4Transform.translate(0,0,0));
    TransformNode lampLightMouthTransform = new TransformNode("scale(5,5,5);translate(0,0.5,0)", m);
    LightNode cube7Node = new LightNode("Light(6)", lightMouth);

    // CAMERA //
    // CAMERA //
    // CAMERA //
    // NameNode lampCamera = new NameNode("lamp camera");
    // CameraNode cube7Node = new CameraNode("Light(6)", spotCamera);







    //GLASSES ROOT//
    //GLASSES ROOT//
    //GLASSES ROOT//
    TransformNode translateGlasses = new TransformNode("translate(0.5,7,0)",Mat4Transform.translate(glassesTransX,glassesTransY,glassesTransZ));
    glasses = new Glasses(sphere, cubeWall);
    glassesRoot = glasses.glassesInit();


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
                                    translateFrontHead.addChild(lampLightMouthTransform);
                                      lampLightMouthTransform.addChild(lampLightMouth);
                                        lampLightMouth.addChild(cube7Node);
    lampRoot.update();  // IMPORTANT – must be done every time any part of the scene graph changes
    // lampRoot.print(0, false);
    // System.exit(0);








    // TABLE ROOT //
    // TABLE ROOT //
    // TABLE ROOT //
    table = new Table(cubeWood, cubeWoodLegs);
    tableRoot = table.tableInit();


    // WALL ROOT //
    // WALL ROOT //
    // WALL ROOT //
    wall = new Wall(cubeWall, cubeWindow);
    wallRoot = wall.wallInit();

    // BOOTH ROOT //
    // BOOTH ROOT //
    // BOOTH ROOT //
    booth = new DjBooth(cubeDjBooth, cubeTS);
    boothRoot = booth.djBoothInit();


    // DISCO BALL ROOT //
    // DISCO BALL ROOT //
    // DISCO BALL ROOT //
    disco = new DiscoBall(cubeBlackB, cubeFloor, sphere);
    discoRoot = disco.discoInit();





    //table
    // tableRoot.
  }

  private void render(GL3 gl) {
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

    double radA = Math.toRadians(rotateFootAngle);
    float cosA = (float)Math.cos(radA);
    float sinA = (float)Math.sin(radA);

    double rad1 = Math.toRadians(rotateHeadAngle);
    double rad2 = Math.toRadians(rotateAllAngle);
    double rad3 = Math.toRadians(rotateSphereBodyAngle);
    double rad4 = Math.toRadians(rotateUpperBranchAngle);
    float cos1 = (float)Math.cos(rad1);
    float cos2 = (float)Math.cos(rad2);
    float cos3 = (float)Math.cos(rad3);
    float cos4 = (float)Math.cos(rad4);

    float cosTotal = (cos1 * 5) + (cos2 * 5) + (cos3 * 5) + (cos4 * 5);


    spotCamera.setPosition(new Vec3(lampTransX+1f - 20*cosA,lampTransY+ 4f,lampTransZ + 20*sinA));
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
    float rotateDiscoAngle;
    float rotateDiscoAngleStart = disco.getAngleStart();
    rotateDiscoAngle = rotateDiscoAngleStart + (float)elapsedTime*50;
    disco.setRotateTransform(rotateDiscoAngle);
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
      // spotLightOff();
      lampTransY = lampTransY + (lampTransY*(float)Math.sin(elapsedTime*10f))/50;
      System.out.println(lampTransY);
      if (lampTransY < 5.5f) {
        lampTransY = 5.49f;
        // spotLightOn();
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
