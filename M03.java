import gmaths.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;
import java.io.*;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioSystem;

public class M03 extends JFrame implements ActionListener {

  private static final int WIDTH = 1024;
  private static final int HEIGHT = 600;
  private static final Dimension dimension = new Dimension(WIDTH, HEIGHT);
  private GLCanvas canvas;
  private M03_GLEventListener glEventListener;
  private final FPSAnimator animator;
  private Camera camera;
  private AudioInputStream audioInputStream;
  private Clip clip;


  public static void main(String[] args) {
    M03 b1 = new M03("M03");
    b1.getContentPane().setPreferredSize(dimension);
    b1.pack();
    b1.setVisible(true);
  }

  public M03(String textForTitleBar) {
    super(textForTitleBar);
    GLCapabilities glcapabilities = new GLCapabilities(GLProfile.get(GLProfile.GL3));
    canvas = new GLCanvas(glcapabilities);
    camera = new Camera(Camera.DEFAULT_POSITION, Camera.DEFAULT_TARGET, Camera.DEFAULT_UP);
    glEventListener = new M03_GLEventListener(camera);
    canvas.addGLEventListener(glEventListener);
    canvas.addMouseMotionListener(new MyMouseInput(camera));
    canvas.addKeyListener(new MyKeyboardInput(camera));
    getContentPane().add(canvas, BorderLayout.CENTER);


    // private File wavFile = new File("C:\\Users\\Jaafer\\Desktop\\UNI\\3D\\Asign\\7_2_scene_graph\\sine.wav");
    // private AudioClip sound;
    //
    // try{sound = Applet.newAudioClip(wavFile.toURL());}
    // catch(Exception e){e.printStackTrace();}

    JMenuBar menuBar=new JMenuBar();
    this.setJMenuBar(menuBar);
      JMenu fileMenu = new JMenu("File");
        JMenuItem quitItem = new JMenuItem("Quit");
        quitItem.addActionListener(this);
        fileMenu.add(quitItem);
    menuBar.add(fileMenu);

    JPanel p = new JPanel();
      JButton b = new JButton("Camera X");
      b.addActionListener(this);
      p.add(b);
      b = new JButton("Camera Z");
      b.addActionListener(this);
      p.add(b);
      b = new JButton("Left Light Off");
      b.addActionListener(this);
      p.add(b);
      b = new JButton("Left Light On");
      b.addActionListener(this);
      p.add(b);
      b = new JButton("Right Light Off");
      b.addActionListener(this);
      p.add(b);
      b = new JButton("Right Light On");
      b.addActionListener(this);
      p.add(b);
      b = new JButton("Random Pose");
      b.addActionListener(this);
      p.add(b);
      b = new JButton("Original Pose");
      b.addActionListener(this);
      p.add(b);
      b = new JButton("Jump");
      b.addActionListener(this);
      p.add(b);
      b = new JButton("Original Position");
      b.addActionListener(this);
      p.add(b);
      b = new JButton("Disco Time!");
      b.addActionListener(this);
      p.add(b);
      b = new JButton("Kill the Beat");
      b.addActionListener(this);
      p.add(b);
    this.add(p, BorderLayout.SOUTH);

    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        animator.stop();
        remove(canvas);
        dispose();
        System.exit(0);
      }
    });
    animator = new FPSAnimator(canvas, 60);
    animator.start();
  }

  public void playSound(String soundName) {
    try {
      audioInputStream = AudioSystem.getAudioInputStream(new File(soundName).getAbsoluteFile());
      clip = AudioSystem.getClip();
      clip.open(audioInputStream);
      clip.start();
    }
    catch(Exception ex) {
     System.out.println("Error with playing sound.");
     ex.printStackTrace();
    }
  }

  public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand().equalsIgnoreCase("Camera X")) {
      camera.setCamera(Camera.CameraType.X);
      canvas.requestFocusInWindow();
    }
    else if (e.getActionCommand().equalsIgnoreCase("Camera Z")) {
      camera.setCamera(Camera.CameraType.Z);
      canvas.requestFocusInWindow();
    }
    else if (e.getActionCommand().equalsIgnoreCase("Left Light Off")) {
      glEventListener.light1Off();
    }
    else if (e.getActionCommand().equalsIgnoreCase("Left Light On")) {
      glEventListener.light1On();
    }
    else if (e.getActionCommand().equalsIgnoreCase("Right Light Off")) {
      glEventListener.light2Off();
    }
    else if (e.getActionCommand().equalsIgnoreCase("Right Light On")) {
      glEventListener.light2On();
    }
    else if (e.getActionCommand().equalsIgnoreCase("Random Pose")) {
      glEventListener.buttonTime();
      glEventListener.randomPose();
    }
    else if (e.getActionCommand().equalsIgnoreCase("Original Pose")) {
      glEventListener.buttonTime();
      glEventListener.originPose();
    }
    else if (e.getActionCommand().equalsIgnoreCase("Jump")) {
      glEventListener.buttonTime();
      glEventListener.jump();
    }
    else if (e.getActionCommand().equalsIgnoreCase("Original Position")) {
      glEventListener.originalPosition();
    }
    else if (e.getActionCommand().equalsIgnoreCase("Disco Time!")) {
      switch(glEventListener.rng(1,5)) {
        case 1 :
          playSound("audio\\flying_to_space.wav");
          break;
        case 2 :
          playSound("audio\\delegation_you_and_i.wav");
          break;
        case 3 :
          playSound("audio\\makoto_lazy_night.wav");
          break;
        case 4 :
          playSound("audio\\rose_royce_car_wash.wav");
          break;
        case 5 :
          playSound("audio\\whispers_and_the_beat_goes_on.wav");
          break;
      }
    }
    else if (e.getActionCommand().equalsIgnoreCase("Kill the Beat")) {
      clip.stop();
      clip.setFramePosition(0);
    }
    else if(e.getActionCommand().equalsIgnoreCase("quit"))
      System.exit(0);
  }

}

class MyKeyboardInput extends KeyAdapter  {
  private Camera camera;

  public MyKeyboardInput(Camera camera) {
    this.camera = camera;
  }

  public void keyPressed(KeyEvent e) {
    Camera.Movement m = Camera.Movement.NO_MOVEMENT;
    switch (e.getKeyCode()) {
      case KeyEvent.VK_LEFT:  m = Camera.Movement.LEFT;  break;
      case KeyEvent.VK_RIGHT: m = Camera.Movement.RIGHT; break;
      case KeyEvent.VK_UP:    m = Camera.Movement.UP;    break;
      case KeyEvent.VK_DOWN:  m = Camera.Movement.DOWN;  break;
      case KeyEvent.VK_A:  m = Camera.Movement.FORWARD;  break;
      case KeyEvent.VK_Z:  m = Camera.Movement.BACK;  break;
    }
    camera.keyboardInput(m);
  }
}

class MyMouseInput extends MouseMotionAdapter {
  private Point lastpoint;
  private Camera camera;

  public MyMouseInput(Camera camera) {
    this.camera = camera;
  }

    /**
   * mouse is used to control camera position
   *
   * @param e  instance of MouseEvent
   */
  public void mouseDragged(MouseEvent e) {
    Point ms = e.getPoint();
    float sensitivity = 0.001f;
    float dx=(float) (ms.x-lastpoint.x)*sensitivity;
    float dy=(float) (ms.y-lastpoint.y)*sensitivity;
    //System.out.println("dy,dy: "+dx+","+dy);
    if (e.getModifiers()==MouseEvent.BUTTON1_MASK)
      camera.updateYawPitch(dx, -dy);
    lastpoint = ms;
  }

  /**
   * mouse is used to control camera position
   *
   * @param e  instance of MouseEvent
   */
  public void mouseMoved(MouseEvent e) {
    lastpoint = e.getPoint();
  }
}
