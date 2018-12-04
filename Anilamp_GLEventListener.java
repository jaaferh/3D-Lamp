import gmaths.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;

public class Anilamp_GLEventListener implements GLEventListener {

  private static final boolean DISPLAY_SHADERS = false;

  public Anilamp_GLEventListener(Camera camera) {
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

   /* Turns Right light off by adjusting fragment shader */
   public void light1Off() {
     light.setBrightness(0.0f);
   }

   /* Turns Right light on */
   public void light1On() {
     light.setBrightness(0.5f);
   }

   /* Turns Left light off */
   public void light2Off() {
     light2.setBrightness(0.0f);
   }

   /* Turns Left light on */
   public void light2On() {
     light2.setBrightness(0.5f);
   }

   /* Turns spotlight off */
   public void spotLightOff() {
     spotLight.setBrightness(0.0f);
     lampRoot.update();
   }

   /* Turns spotlight on */
   public void spotLightOn() {
     spotLight.setBrightness(2f);
     lampRoot.update();
   }

   /* Sets random angles for random lamp pose */
   public void randomPose() {
     rotateAllAngleNew = rng(-30,55);
     rotateSphereBodyAngleNew = rng(-30,20);
     rotateUpperBranchAngleNew = rng(-80,0);
     rotateHeadAngleNew = rng(-50,50);
     pose = true;
     move = false;
     lampRoot.update();
   }

   /* Sets angles for lamp's original pose */
   public void originPose() {
     rotateAllAngleNew  = 35;
     rotateSphereBodyAngleNew = -30;
     rotateUpperBranchAngleNew = -40;
     rotateHeadAngleNew = 10;
     pose = true;
     move = false;
     lampRoot.update();
   }

   /* Sets angles for lamp's jumping pose */
   public void jumpingPose() {
     rotateAllAngleNew = 10;
     rotateSphereBodyAngleNew = -10;
     rotateUpperBranchAngleNew = -10;
     rotateHeadAngleNew = -30;
     pose = true;
     move = false;
     lampRoot.update();
   }

   /* Random number generator */
   public int rng(int min, int max) {
     int x = Math.round((float)(Math.random()*((max-min)+1))+min);
     return x;
   }

   /* Calculates the next x and z position to jump to */
   public void jump() {
     int rngAngleX = rng((int)rotateFootAngleNew-45, (int)rotateFootAngleNew+45);
     int rngAngleZ = (rngAngleX - 90)*-1;

     double rngAngleXRad = Math.toRadians(rngAngleX);
     double rngAngleZRad = Math.toRadians(rngAngleZ);
     double angleCosX = Math.cos(rngAngleXRad);
     double angleCosZ = Math.cos(rngAngleZRad);

     double length = 1.0;
     // Vector equation calculates next position from origin 0
     lampTransXNew = (float)((angleCosX/length)*(Math.pow(length,2)));
     lampTransZNew = (float)((angleCosZ/length)*(Math.pow(length,2)));

     // Adding to existing positions
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

   /* Sets translation and rotation values for lamp's original position */
   public void originalPosition() {
     lampTransX = 0.5f;
     lampTransY = 5.5f;
     lampTransZ = -12.5f;
     rotateFootAngleNew = rotateFootAngle = rotateFootAngleStart;

     rotateFoot.setTransform(Mat4Transform.rotateAroundY(rotateFootAngle));
     translateLamp.setTransform(Mat4Transform.translate(lampTransX,lampTransY,lampTransZ));
     lampRoot.update();
   }

   /* Turn 100 degrees to the right */
   public void turn100() {
     rotateFootAngleNew = rotateFootAngle - 100;
     lampRoot.update();
   }


  // ***************************************************
  /* THE SCENE
   *
   *
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
  private float rotateFootAngleNew = rotateFootAngleStart;

  private float lampTransX = 0.5f;
  private float lampTransY = 5.5f;
  private float lampTransZ = -12.5f;
  private float lampTransXNew, lampTransYNew, lampTransZNew;


  /* Contains all scene graph initialisations and model creations */
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

    // Lamp variables
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


    // Initialising lights
    light = new Light(gl); // Right
    light.setCamera(camera);

    light2 = new Light(gl); // Left
    light2.setCamera(camera);

    spotCamera = new Camera(Camera.DEFAULT_POSITION, Camera.DEFAULT_TARGET, Camera.DEFAULT_UP);
    spotLight = new Light(gl);
    spotLight.setCamera(spotCamera); // Use spotCamera to get camera front


    // Model creation
      // floor
    Mesh mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    Shader shader = new Shader(gl, "vs_tt_05.txt", "fs_tt_uncoloured.txt");
    Material material = new Material(new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.3f, 0.3f, 0.3f), 32.0f);
    Mat4 modelMatrix = Mat4Transform.scale(40f,1f,40f);
    floor = new Model(gl, camera, light, light2, spotLight, shader, material, modelMatrix, mesh, textureId0);

      // spheres
    mesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
    shader = new Shader(gl, "vs_cube_04.txt", "fs_cube_04.txt");
    material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(4,4,4), Mat4Transform.translate(0,0.5f,0));
    sphere = new Model(gl, camera, light, light2, spotLight, shader, material, modelMatrix, mesh, textureId3, textureId3);
    sphereLong = new Model(gl, camera, light, light2, spotLight, shader, material, modelMatrix, mesh, textureId2, textureId3);

    material = new Material(new Vec3(0.1f, 0.1f, 0.1f), new Vec3(0.1f, 0.1f, 0.1f), new Vec3(0.0f, 0.0f, 0.0f), 100.0f);
    sphereAfro = new Model(gl, camera, light, light2, spotLight, shader, material, modelMatrix, mesh, textureId6);

      // cubes
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

      // dance floor
    mesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
    Mat4 m = Mat4Transform.scale(danceFloorScale*30f,danceFloorScale*0.5f,danceFloorScale*30f);
    modelMatrix = Mat4.multiply(Mat4Transform.translate(0,5.5f,-12.5f), m);
    danceFloor = new Model(gl, camera, light, light2, spotLight, shader, material, modelMatrix, mesh, textureId9);





    // LAMP ROOT //
    // LAMP ROOT //
    // LAMP ROOT //
    lampRoot = new NameNode("lamp structure");
    m = Mat4Transform.translate(lampTransX,lampTransY,lampTransZ);
    translateLamp = new TransformNode("translate(" + lampTransX + "," + lampTransY + "," + lampTransZ + ")", m);
    rotateFoot = new TransformNode("rotateAroundY("+rotateFootAngle+")", Mat4Transform.rotateAroundY(rotateFootAngle));

    // FOOT //
    // FOOT //
    // FOOT //
    NameNode lampFoot = new NameNode("lamp foot");
    m = Mat4Transform.scale(footLength,footHeight,footDepth);
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode lampFootTransform = new TransformNode("scale(" + footLength + "," + footHeight + "," + footDepth + ");translate(0,0.5,0)", m);
    ModelNode cube0Node = new ModelNode("Cube(foot)", cube);

    //  LOWER BRANCH //
    //  LOWER BRANCH //
    //  LOWER BRANCH //
    NameNode lowerBranch = new NameNode("lower branch");
    m = Mat4Transform.translate(lowBTransX,lowBTransY,lowBTransZ);
    TransformNode translateAboveFoot = new TransformNode("translate(" + lowBTransX + "," + lowBTransY + "," + lowBTransZ + ")", m);
    rotateAll = new TransformNode("rotateAroundZ("+rotateAllAngle+")", Mat4Transform.rotateAroundZ(rotateAllAngle));

    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.scale(branchThick,branchLength,branchThick));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode lowerBranchTransform = new TransformNode("scale(" + branchThick + "," + branchLength + "," + branchThick + "); translate(0,0.5,0)", m);
    ModelNode cube1Node = new ModelNode("SphereLong(lower branch)", sphereLong);

    //  SPHERE BODY //
    //  SPHERE BODY //
    //  SPHERE BODY //
    NameNode sphereBody = new NameNode("sphere body");
    m = Mat4Transform.translate(sphereTransX,sphereTransY,sphereTransZ);
    TransformNode translateAboveLowBranch = new TransformNode("translate(" + sphereTransX + "," + sphereTransY + "," + sphereTransZ + ")", m);
    rotateSphereBody = new TransformNode("rotateAroundZ("+rotateSphereBodyAngle+")",Mat4Transform.rotateAroundZ(rotateSphereBodyAngle));

    m = Mat4Transform.scale(sphereScale,sphereScale,sphereScale);
    TransformNode sphereBodyTransform = new TransformNode("scale(" + sphereScale + "," + sphereScale + "," + sphereScale + ");translate(0,0.5,0)", m);
    ModelNode cube2Node = new ModelNode("Sphere(sphere body)", sphere);

    //  UPPER BRANCH //
    //  UPPER BRANCH //
    //  UPPER BRANCH //
    NameNode upperBranch = new NameNode("upper branch");
    m = Mat4Transform.translate(upBTransX,upBTransY,upBTransZ);
    TransformNode translateAboveSphereBody = new TransformNode("translate(" + upBTransX + "," + upBTransY + "," + upBTransZ + ")", m);
    rotateUpperBranch = new TransformNode("rotateAroundZ("+rotateUpperBranchAngle+")",Mat4Transform.rotateAroundZ(rotateUpperBranchAngle));

    m = Mat4Transform.scale(branchThick,branchLength,branchThick);
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode upperBranchTransform = new TransformNode("scale(" + branchThick + "," + branchLength + "," + branchThick + "); translate(0,0.5,0)", m);
    ModelNode cube3Node = new ModelNode("SphereLong(upper branch)", sphereLong);

    // HEAD //
    // HEAD //
    // HEAD //
    NameNode lampHead = new NameNode("head");
    m = Mat4Transform.translate(headTransX,headTransY,headTransZ);
    TransformNode translateAboveUpperBranch = new TransformNode("translate(" + headTransX + "," + headTransY + "," + headTransZ + ")", m);
    rotateHead = new TransformNode("rotateAroundZ("+rotateHeadAngle+")",Mat4Transform.rotateAroundZ(rotateHeadAngle));

    m = Mat4Transform.scale(headLength,headHeight,headDepth);
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode lampHeadTransform = new TransformNode("scale(" + headLength + "," + headHeight + "," + headDepth + ");translate(0,0.5,0)", m);
    ModelNode cube4Node = new ModelNode("CubeBeige(head)", cubeBeige);

    // AFRO //
    // AFRO //
    // AFRO //
    NameNode afro = new NameNode("afro");
    m = Mat4Transform.translate(afroTransX,afroTransY,afroTransZ);
    TransformNode translateAboveHead = new TransformNode("translate(" + afroTransX + "," + afroTransY + "," + afroTransZ + ")", m);

    m = Mat4Transform.scale(afroScale,afroScale,afroScale);
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode afroTransform = new TransformNode("scale(" + afroScale + "," + afroScale + "," + afroScale + ");translate(0,0.5,0)", m);
    ModelNode cube5Node = new ModelNode("SphereAfro(Afro)", sphereAfro);

    // SPOT LIGHT ORIGIN //
    // SPOT LIGHT ORIGIN //
    // SPOT LIGHT ORIGIN //
    NameNode lampLight = new NameNode("lamp light");
    m = Mat4Transform.translate(0.5f,0.2f,0);
    TransformNode translateFrontHead = new TransformNode("translate(0.5f,0.2f,0)", m);

    m = Mat4Transform.scale(0.3f,0.3f,0.5f);
    m = Mat4.multiply(m, Mat4Transform.translate(0,0,0));
    TransformNode lampLightTransform = new TransformNode("scale(0.3f,0.3f,0.5f);translate(0,0,0)", m);
    LightNode cube6Node = new LightNode("SpotLight(Lamp Light)", spotLight);


    // LIGHT MODEL //
    // LIGHT MODEL //
    // LIGHT MODEL //
    NameNode lampLightMouth = new NameNode("lamp light");
    lightMouth = new Light(gl);
    lightMouth.setCamera(camera);

    m = Mat4Transform.scale(0.3f,0.3f,0.5f);
    m = Mat4.multiply(m, Mat4Transform.translate(0,0,0));
    TransformNode lampLightMouthTransform = new TransformNode("scale(0.3f,0.3f,0.5f);translate(0,0,0)", m);
    LightNode cube7Node = new LightNode("LightMouth(LampLightMouth)", lightMouth);




    // GLASSES ROOT //
    // GLASSES ROOT //
    // GLASSES ROOT //
    m = Mat4Transform.translate(glassesTransX,glassesTransY,glassesTransZ);
    TransformNode translateGlasses = new TransformNode("translate(" + glassesTransX + "," + glassesTransY + "," + glassesTransZ + ")", m);
    glasses = new Glasses(sphere, cubeWall); // Glasses class contains scene graph
    glassesRoot = glasses.glassesInit();


    // LAMP ROOT //
    // LAMP ROOT //
    // LAMP ROOT //
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
    lampRoot.update();
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

  }

  /* Render calls and update calls */
  private void render(GL3 gl) {
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

    // Calculates position and target of the camera in spotlight
    double radA = Math.toRadians(rotateFootAngle);
    float cosA = (float)Math.cos(radA);
    float sinA = (float)Math.sin(radA);

    spotCamera.setPosition(new Vec3(lampTransX+1f - 20*cosA,lampTransY+ 4f,lampTransZ + 20*sinA));
    spotCamera.setTarget(new Vec3(lampTransX+4f,lampTransY, -12.5f));


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
    updateDiscoRotation();

    // pose, move and jumpV Booleans determine when to call their respective functions
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

  /* Keeps the disco ball spinning */
  private void updateDiscoRotation() {
    double elapsedTime = getSeconds()-startTime;
    float rotateDiscoAngleStart = disco.getAngleStart();
    float rotateDiscoAngle = rotateDiscoAngleStart + (float)elapsedTime*50;
    disco.setRotateTransform(rotateDiscoAngle);
    discoRoot.update();
  }

  /* Rotates the entire lamp to jumping target and prepares for jump through a crouching pose */
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

    // If target angle reached
    if (rotateFootAngle == rotateFootAngleNew) {
      move = false;
      jumpV = true;
      jumpTime = getSeconds();
    }
    lampRoot.update();
  }

  /* Controls the vertical translation of the lamp using a sine wave. Also determines lamp pose in midair and when landing */
  private void jumpVertical() {
    double elapsedTime = getSeconds()- jumpTime;

    if (lampTransY >= 5.5f) {
      jumpingPose();
      lampTransY = lampTransY + (lampTransY*(float)Math.sin(elapsedTime*10f))/50;
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

  /* Controls the horizontal translation of the lamp using elapsed time. Also keeps the lamp within boundaries */
  private void jumpHorizontal() {
    double elapsedTime = getSeconds()- jumpTime;
    double speed = 0.15;
    if (lampTransX > (lampTransXNew - 0.2) && lampTransX < (lampTransXNew + 0.2)) { // allowing room for error
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
      turn100(); // turns the lamp once it reaches boundary
    }
    if (lampTransX < -8 ) {
      lampTransX = -8;
      turn100();
    }
    if (lampTransZ > -6) {
      lampTransZ = -6;
      turn100();
    }
    if (lampTransZ < -18) {
      lampTransZ = -18;
      turn100();
    }

    translateLamp.setTransform(Mat4Transform.translate(lampTransX,lampTransY,lampTransZ));
    lampRoot.update();
  }

  /* Controls the pose by giving it a smooth transition from the original angles to the new angles */
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

    lampRoot.update();
  }

  /* Makes sure that the angle changes in the above update functions change over time (smoothly) */
  private float poseCalculation(float startAngle, float newAngle, double time) {
    if (startAngle <= newAngle-3) {
      startAngle = startAngle + (float)(time*6);
    }
    else if (startAngle >= newAngle+3) {
      startAngle = startAngle - (float)(time*6);
    }
    else if (startAngle >= (newAngle-3) && startAngle <= (newAngle+3)) { // allowing room for error
      startAngle = newAngle;
    }
    return startAngle;
  }


  // ***************************************************
  /* TIME
   */

  private double startTime;
  private double buttonTime;
  private double jumpTime;

  /* Returns system time in milliseconds */
  private double getSeconds() {
    return System.currentTimeMillis()/1000.0;
  }

  /* Returns system time at the time a button is pressed, if that button calls this function */
  public void buttonTime() {
    buttonTime = getSeconds();
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
