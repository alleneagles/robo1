package org.usfirst.frc.team5417.robot;

public class DummyStrafeDrive implements IStrafeDrive {

	public void strafeDrive(XboxController controller) {
		
	}
	public void drive(double f, double t, double s) {
		
	}
	
	public void feedAllMotors() {
		
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
    	return false;
    }
    public String getDescription() {
    	return "Dummy Strafe Drive";
    }

}
