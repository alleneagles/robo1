package org.usfirst.frc.team5417.robot;

public class DummyManipulatorDrive implements IManipulatorDrive {

	public void manipulatorControl(XboxController manipulatorController) {
		
	}

	public void moveToInitialPosition()
	{
		
	}
	
	public void setExpiration(double timeout) {
		
	}

	public double getExpiration() {
		return 1;
	}

	public boolean isAlive() {
		return true;
	}

	public void stopMotor() {
		
	}

	public void setSafetyEnabled(boolean enabled) {
		
	}

	public boolean isSafetyEnabled() {
		return true;
	}

	public String getDescription() {
		return "Dummy Manipulator Drive";
	}

	public void feedAllMotors() {
		
	}
	
}
