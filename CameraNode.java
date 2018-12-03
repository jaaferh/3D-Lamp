import com.jogamp.opengl.*;

public class CameraNode extends SGNode {

  protected Camera camera;

  public CameraNode(String name, Camera c) {
    super(name);
    camera = c;
  }
}
