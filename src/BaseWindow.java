import org.lwjgl.*;
import org.lwjgl.opengl.*;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.input.*;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.*;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class BaseWindow implements ActionListener {
	int MAP_X;
	int MAP_Z;
	float MAP_SCALE = 3.0f; // the scale of the terrain map
	// ////Terrain Data
	float[][][] terrain; // heightfield terrain data (0-255); 256x256

	float angle = 0.0f; // camera angle
	float radians = 0.0f; // camera angle in radians

	// //// Mouse/Camera Variables
	int mouseX, mouseY; // mouse coordinates
	float cameraX, cameraY, cameraZ; // camera coordinates
	float lookX, lookY, lookZ; // camera look-at coordinates

	protected static boolean isRunning = false;

	
	private JButton start;
    private JButton credits;
    private JButton exit;
    private JButton back;
    private ImageIcon image1;
    private ImageIcon image2;
    private ImageIcon image3;
    private ImageIcon image4;
    
    JFrame frame = new JFrame("RGTI Igra");
    JFrame c = new JFrame("Credits");
    
    private void displayGUI()
    {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1024,768);

        JPanel contentPane = new JPanel();
        try
        {
            image1 = new ImageIcon(ImageIO.read(
                    new File("start.png")));
            image2 = new ImageIcon(ImageIO.read(
                    new File("credits3.png")));
            image3 = new ImageIcon(ImageIO.read(
                    new File("exit.png")));
            image4 = new ImageIcon(ImageIO.read(
                    new File("back.png")));
        }
        catch(MalformedURLException mue)
        {
            mue.printStackTrace();
        }
        catch(IOException ioe)
        {
            ioe.printStackTrace();
        }       

        start = new JButton();
        start.setIcon(image1);
        start.addActionListener(this);
        credits = new JButton();
        credits.setIcon(image2);
        credits.addActionListener(this);
        exit = new JButton();
        exit.setIcon(image3);
        exit.addActionListener(this);
        contentPane.setBackground(Color.black);
        contentPane.add(start);
        contentPane.add(credits);
        contentPane.add(exit);
        frame.setContentPane(contentPane);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    }

    public static void main(String... args)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                new BaseWindow().displayGUI();
            }
        });
    }

	@Override
	public void actionPerformed(ActionEvent event) {
		JPanel cred = new JPanel();
		
		if (event.getSource() == exit){
			System.exit(0);
		}
		if (event.getSource() == credits){
			frame.setVisible(false);
			c.setSize(1024,768);
			cred.setBackground(Color.black);
			back = new JButton();
			back.setIcon(image4);
			back.addActionListener(this);
			JLabel content = new JLabel("Authors: Matej Zrimšek, Matic Volk");
			content.setForeground(Color.RED);
			cred.add(content);
			cred.add(back);
			c.setContentPane(cred);
			c.pack();
	        c.setLocationByPlatform(true);
	        c.setVisible(true);			
		}
		if (event.getSource() == back){
			frame.setVisible(true);
			c.setVisible(false);
		}
		if (event.getSource() == start){
			frame.setVisible(false);
			(new Refactored()).main(null);
		}
	}

	/**
	 * Initializes display and enters main loop
	 */
	protected void execute() {
		try {
			initDisplay();
		} catch (LWJGLException e) {
			System.err.println("Can't open display.");
			System.exit(0);
		}

		BaseWindow.isRunning = true;
		mainLoop();
		Display.destroy();
	}

	/**
	 * Main loop: renders and processes input events
	 */
	protected void mainLoop() {
		// setup camera and lights
		setupView();

		while (BaseWindow.isRunning) {
			// reset view
			resetView();

			// let subsystem paint //sceno izris
			renderFrame();

			// process input events /V/I procesiramo
			processInput();

			// update window contents and process input messages
			Display.update();
		}
	}

	/**
	 * Initial setup of projection of the scene onto screen, lights, etc.
	 */
	protected void setupView() {

	}

	/**
	 * Resets the view of current frame
	 */
	protected void resetView() {

	}

	/**
	 * Renders current frame
	 */
	protected void renderFrame() {
		
	}

	/**
	 * Processes Keyboard and Mouse input and spawns actions
	 */
	protected void processInput() {
		if (Display.isCloseRequested()
				|| Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
			BaseWindow.isRunning = false;
		}
		
		
	}

	/**
	 * Finds best 1024x768 display mode and sets it
	 * 
	 * @throws LWJGLException
	 */
	protected void initDisplay() throws LWJGLException {
		DisplayMode bestMode = null;
		DisplayMode[] dm = Display.getAvailableDisplayModes();
		for (int nI = 0; nI < dm.length; nI++) {
			DisplayMode mode = dm[nI];
			if (mode.getWidth() == 1024 && mode.getHeight() == 768
					&& mode.getFrequency() <= 85) {
				if (bestMode == null
						|| (mode.getBitsPerPixel() >= bestMode
								.getBitsPerPixel() && mode.getFrequency() > bestMode
								.getFrequency()))
					bestMode = mode;
			}
		}

		Display.setDisplayMode(bestMode);
		// FSAA
		Display.create(new PixelFormat(8, 8, 8, 4));
		// No FSAA
		// Display.create();
		Display.setTitle(this.getClass().getName());
	}

	/**
	 * Utils for creating native buffers
	 * 
	 * @throws LWJGLException
	 */
	public static ByteBuffer allocBytes(int howmany) {
		return ByteBuffer.allocateDirect(howmany)
				.order(ByteOrder.nativeOrder());
	}

	public static IntBuffer allocInts(int howmany) {
		return ByteBuffer.allocateDirect(howmany)
				.order(ByteOrder.nativeOrder()).asIntBuffer();
	}

	public static FloatBuffer allocFloats(int howmany) {
		return ByteBuffer.allocateDirect(howmany)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();
	}

	public static ByteBuffer allocBytes(byte[] bytearray) {
		ByteBuffer bb = ByteBuffer.allocateDirect(bytearray.length * 1).order(
				ByteOrder.nativeOrder());
		bb.put(bytearray).flip();
		return bb;
	}

	public static IntBuffer allocInts(int[] intarray) {
		IntBuffer ib = ByteBuffer.allocateDirect(intarray.length * 4)
				.order(ByteOrder.nativeOrder()).asIntBuffer();
		ib.put(intarray).flip();
		return ib;
	}

	public static FloatBuffer allocFloats(float[] floatarray) {
		FloatBuffer fb = ByteBuffer.allocateDirect(floatarray.length * 4)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();
		fb.put(floatarray).flip();
		return fb;
	}
}
