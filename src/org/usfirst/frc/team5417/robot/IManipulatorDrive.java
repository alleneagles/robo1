package org.usfirst.frc.team5417.robot;

import edu.wpi.first.wpilibj.MotorSafety;

public interface IManipulatorDrive extends MotorSafety, Feedable {

	public void manipulatorControl(XboxController manipulatorController);
	public void moveToInitialPosition();
}
