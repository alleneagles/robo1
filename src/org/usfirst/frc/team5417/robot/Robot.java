package org.usfirst.frc.team5417.robot;

//import edu.wpi.first.wpilibj.GenericHID.Hand;
//import edu.wpi.first.wpilibj.communication.UsageReporting;
//import edu.wpi.first.wpilibj.communication.FRCNetworkCommunicationsLibrary.tInstances;
//import edu.wpi.first.wpilibj.communication.FRCNetworkCommunicationsLibrary.tResourceType;
//import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.SampleRobot;
//import edu.wpi.first.wpilibj.RobotDrive;
//import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * This is a demo program showing the use of the RobotDrive class, specifically
 * it contains the code necessary to operate a robot with tank drive.
 *
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the SampleRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 *
 * WARNING: While it may look like a good choice to use for your code if you're
 * inexperienced, don't. Unless you know what you are doing, complex code will
 * be much more difficult under this system. Use IterativeRobot or Command-Based
 * instead if you're new.
 */
public class Robot extends SampleRobot {
	private CameraServer cameraServer;
	
	private int strafeControllerPort = 2; // USB Controller Port
	private XboxController strafeController;
	
	private int manipulatorControllerPort = 0; // USB Controller Port
	private XboxController manipulatorController;
	
	private IStrafeDrive strafe;
	private IManipulatorDrive manipulator;

	public Robot() {
        super();

        try
        {
	        cameraServer = CameraServer.getInstance();
	        cameraServer.setQuality(50);
	        //the camera name (ex "cam0") can be found through the roborio web interface
	        cameraServer.startAutomaticCapture("cam0");

        	SmartDashboard.putString("Camera", "Connected");
        }
        catch (Exception ex)
        {
        	SmartDashboard.putString("Camera", "Not Connected");
        }

        strafeController = new XboxController(strafeControllerPort);
        strafe = new StrafeDrive(1, 0, 2);
		//strafe = new DummyStrafeDrive();
		strafe.setExpiration(0.1);

		manipulatorController = new XboxController(manipulatorControllerPort);
		manipulator = new ManipulatorDrive(0, 1, 2, 3, 10, 11);
		//manipulator = new DummyManipulatorDrive();
		manipulator.setExpiration(0.1);

		GlobalFeeder.addFeedableMotorSafety(strafe);
		GlobalFeeder.addFeedableMotorSafety(manipulator);
	}

    /**
     * Called once each time the robot enters the autonomous state.
     */
//    public void autonomous() {
//    	strafe.setSafetyEnabled(false);
//    	manipulator.setSafetyEnabled(false);
//    	
//    	manipulator.moveToInitialPosition();
//    	
//    	// TODO: tune these parameters
//    	final double full_f = 1.0;
//    	final double full_b = -1.0;
//    	final double full_l = -1.0;
//    	final double full_r = 1.0;
//    	
//    	final double half_f = 0.5;
//    	final double half_b = -0.5;
//    	final double half_l = -0.5;
//    	final double half_r = 0.5;
//    	
//    	final double quarter_f = 0.25;
//    	final double quarter_b = -0.25;
//    	final double quarter_l = -0.25;
//    	final double quarter_r = 0.25;
//    	
//    	final double stop = 0.0;
//    	
//    	double totalDelaySeconds = .2; // TODO: tune this delay; Delay is in Milliseconds, as are all Java time Units
//    	
//    	double initialMatchTime = Timer.getMatchTime();
//    	while (isAutonomous() && isEnabled()) {
//    		strafe.drive(half_b, stop, stop);
//    		Timer.delay(500);
//    		
//    		double currentMatchTime = Timer.getMatchTime();
//    		if (Math.abs(currentMatchTime - initialMatchTime) >= totalDelaySeconds)
//    			break;
//    	}
//    	
//    	strafe.drive(stop, stop, stop); // stop // TODO - Bryan check
//    }
    	/**
    }
	 * Runs the motors with Strafe Drive steering.
	 */
	public void operatorControl() {
		strafe.setSafetyEnabled(true);
		manipulator.setSafetyEnabled(true);

		//manipulator.moveToInitialPosition();

		while (isOperatorControl() && isEnabled()) {
			strafe.strafeDrive(strafeController);
			manipulator.manipulatorControl(manipulatorController);

			
			Timer.delay(0.005); // wait for a motor update time
		}
	}

}
