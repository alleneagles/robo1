package org.usfirst.frc.team5417.robot;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;

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
//	XboxController strafeController;
	XboxController manipulatorController;
	//StrafeDrive strafe;
	ManipulatorDrive manipulator;

	public Robot() {
//		strafeController = new XboxController(0);
		//strafe = new StrafeDrive(1, 0, 2);
		//strafe.setExpiration(0.1);

		manipulatorController = new XboxController(0);
		manipulator = new ManipulatorDrive(0, 1, 2, 3, 10, 11);
		manipulator.setExpiration(0.1);

		//GlobalFeeder.addFeedableMotorSafety(strafe);
		GlobalFeeder.addFeedableMotorSafety(manipulator);
	}

	/**
	 * Runs the motors with Strafe Drive steering.
	 */
	public void operatorControl() {
		//strafe.setSafetyEnabled(true);
		manipulator.setSafetyEnabled(true);

		//manipulator.moveToInitialPosition();

		while (isOperatorControl() && isEnabled()) {
//			strafe.strafeDrive(strafeController);
			manipulator.manipulatorControl(manipulatorController);

			Timer.delay(0.005); // wait for a motor update time
		}
	}
}
