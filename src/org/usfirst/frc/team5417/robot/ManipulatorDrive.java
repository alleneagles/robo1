package org.usfirst.frc.team5417.robot;

import org.usfirst.frc.team5417.robot.StrafeDrive.MotorParameters;
import edu.wpi.first.wpilibj.MotorSafety;
import edu.wpi.first.wpilibj.MotorSafetyHelper;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.GenericHID.Hand;

public class ManipulatorDrive implements MotorSafety {

	public class MotorParameters {
		public double x, y;
		public double A, B;
	}

	private DigitalInput _TopSwitch;
	private DigitalInput _BottomSwitch;
	private DigitalInput _OpenSwitch;
	private DigitalInput _CloseSwitch;

	protected MotorSafetyHelper m_safetyHelper;
	
	private CANTalon _aMotor;
	private CANTalon _bMotor;

	public ManipulatorDrive(int TopSwitchChannel, int BottomSwitchChannel,
			int OpenSwitchChannel, int CloseSwitchChannel, int aDeviceNumber,
			int bDeviceNumber) {

		_TopSwitch = new DigitalInput(TopSwitchChannel);
		_BottomSwitch = new DigitalInput(BottomSwitchChannel);
		_OpenSwitch = new DigitalInput(OpenSwitchChannel);
		_CloseSwitch = new DigitalInput(CloseSwitchChannel);

		_aMotor = new CANTalon(aDeviceNumber);
		_bMotor = new CANTalon(bDeviceNumber);
	}

	public void manipulatorControl(XboxController controller) {
		// FREE MOTION
		MotorParameters mp = CalcManipulatorDrive(controller.getLX(Hand.kLeft),
				controller.getRY(Hand.kRight));
		_aMotor.set(mp.A);
		_bMotor.set(mp.B);
		
		if (m_safetyHelper != null) m_safetyHelper.feed();
	}

	public MotorParameters CalcManipulatorDrive(double x, double y) {

		//
		// first, limit x and y if we are at the extremes of our permissible movement
		//
		boolean atTop = (_TopSwitch.get());
		boolean atBottom = (_BottomSwitch.get());
		boolean atOpen = (_OpenSwitch.get());
		boolean atClose = (_CloseSwitch.get());

		final double deadSpaceThreshold = 0.2 ;
		
		if (atTop) {
			y = 0;
		}

		if (atBottom) {
			y = 0;
		}

		if (atOpen) {
			x = 0;
		}

		if (atClose) {
			x = 0;
		}

		//
		// now, calculate motor movements
		//
		double A = 0, B = 0;

		//if joysticks in middle do nothing
		if (x >= -deadSpaceThreshold && x <= deadSpaceThreshold)
		{
			x = 0;
		}
		
		//if joysticks in middle do nothing
		if (y >= -deadSpaceThreshold && y <= deadSpaceThreshold){
			
			y = 0;
		}
		
		final double maxMotorSpeed = 1.0;
		
		//
		// diagonals
		//	
		if (x < 0 && y < 0) {
			// Move down/left
			A = -maxMotorSpeed;
			B = 0.0;
		} else if (x > 0 && y < 0) {
			// Move down/right
			A = maxMotorSpeed;
			B = 0.0;
		} else if (x < 0 && y > 0) {
			// Move up/left
			A = 0.0;
			B = -maxMotorSpeed;
		} else if (x > 0 && y > 0) {
			// Move up/right
			A = 0.0;
			B = maxMotorSpeed;
		}
		//
		// cardinal directions
		//
		else if (x < 0) {
			// move left
			A = maxMotorSpeed;
			B = maxMotorSpeed;
		} else if (x > 0) {
			// move right
			A = -maxMotorSpeed;
			B = -maxMotorSpeed;
		} else if (y > 0) {
			// move up
			A = maxMotorSpeed;
			B = -maxMotorSpeed;
		} else if (y < 0) {
			// move down
			A = -maxMotorSpeed;
			B = maxMotorSpeed;
		}

		MotorParameters m = new MotorParameters();
		m.x = x;
		m.y = y;
		m.A = A;
		m.B = B;
		return m;
	}

	public void setExpiration(double timeout) {
		m_safetyHelper.setExpiration(timeout);
	}

	public double getExpiration() {
		return m_safetyHelper.getExpiration();
	}

	public boolean isAlive() {
		return m_safetyHelper.isAlive();
	}

	public void stopMotor() {
		_aMotor.set(0.0);
		_bMotor.set(0.0);
		if (m_safetyHelper != null) m_safetyHelper.feed();
	}

	public void setSafetyEnabled(boolean enabled) {
		m_safetyHelper.setSafetyEnabled(enabled);
	}

	public boolean isSafetyEnabled() {
		return m_safetyHelper.isSafetyEnabled();
	}

	public String getDescription() {
		return "Manipulation Drive";
	}

}
