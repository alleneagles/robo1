package org.usfirst.frc.team5417.robot;

import edu.wpi.first.wpilibj.MotorSafety;

public interface IStrafeDrive extends MotorSafety, Feedable {

	public void strafeDrive(XboxController controller);
	public void drive(double f, double t, double s);
	
}
