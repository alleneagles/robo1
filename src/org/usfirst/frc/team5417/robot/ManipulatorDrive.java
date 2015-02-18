package org.usfirst.frc.team5417.robot;

//import org.usfirst.frc.team5417.robot.StrafeDrive.MotorParameters;
//import org.usfirst.frc.team5417.robot.XboxController.ButtonType;

import edu.wpi.first.wpilibj.CANTalon.ControlMode;
import edu.wpi.first.wpilibj.MotorSafety;
import edu.wpi.first.wpilibj.MotorSafetyHelper;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Timer;

public class ManipulatorDrive implements IManipulatorDrive, MotorSafety, Feedable {

	public class MotorParameters {
		public double x, y;
		public double R, L;
	}

	private DigitalInput _TopSwitch;
	private DigitalInput _BottomSwitch;
	private DigitalInput _OpenSwitch;
	private DigitalInput _CloseSwitch;
	
	/* Range [0.0-1.0] where 0.0 will stop the motor and 1.0 will allow the motor to run at full. */
	private final double _LFix = 1.0;
	private final double _RFix = 1.0;
	
	/* Threshold value to use for when joystick is at center */
	private final double _deadSpaceThreshold = 0.2 ;
	
	private final double _maxMotorSpeed = 1.0;

    public static final double kDefaultExpirationTime = 0.1;
	protected MotorSafetyHelper m_safetyHelper;
	
	private CANTalon _leftMotor;
	private CANTalon _rightMotor;

	/**
	 * @param TopSwitchChannel
	 * @param BottomSwitchChannel
	 * @param OpenSwitchChannel
	 * @param CloseSwitchChannel
	 * @param leftDeviceNumber
	 * @param bDeviceNumber
	 * */
	public ManipulatorDrive(int TopSwitchChannel, int BottomSwitchChannel,
			int OpenSwitchChannel, int CloseSwitchChannel, int leftDeviceNumber,
			int bDeviceNumber) {

		_TopSwitch = new DigitalInput(TopSwitchChannel);
		_BottomSwitch = new DigitalInput(BottomSwitchChannel);
		_OpenSwitch = new DigitalInput(OpenSwitchChannel);
		_CloseSwitch = new DigitalInput(CloseSwitchChannel);

		_leftMotor = new CANTalon(leftDeviceNumber);
		_rightMotor = new CANTalon(bDeviceNumber);
		
		setupMotorSafety();
	}
	
	/**
	 * Moves the Manipulator to it's initial position by moving to atBottom and atOpen
	 */
	public void moveToInitialPosition()
	{
		boolean atBottom = _BottomSwitch.get();
		boolean atOpen = _OpenSwitch.get();
		
		// move to open position
		while (atBottom == false || atOpen == false)
		{
			GlobalFeeder.feedAllMotors();
			
			// TODO: move in the correct direction by giving fake x and y parameters
			moveInDirection(1.0, 1.0);
			Timer.delay(0.05);

			atBottom = _BottomSwitch.get();
			atOpen = _OpenSwitch.get();
		}
	}

	public void changeControlModeForBothMotors(ControlMode controlMode)
	{
		_leftMotor.changeControlMode(controlMode);
		_rightMotor.changeControlMode(controlMode);		
	}
	
	public void moveInDirection(double rightX, double leftY)
	{
		this.changeControlModeForBothMotors(ControlMode.PercentVbus);
		
		MotorParameters mp = CalcManipulatorDrive(rightX, leftY);
		
		_leftMotor.enableBrakeMode(false);
		_rightMotor.enableBrakeMode(false);

		UpdateSmartDashboard(mp);
		_leftMotor.set(mp.L);
		_rightMotor.set(mp.R);
		
	}
	
	public void UpdateSmartDashboard(MotorParameters mp)
	{
		SmartDashboard.putNumber("Manip 'x'", mp.x);
		SmartDashboard.putNumber("Manip 'y'", mp.y);
		SmartDashboard.putNumber("Manip 'A'", mp.R);
		SmartDashboard.putNumber("Manip 'B'", mp.L);
	}
	
	public void UpdateSmartDashboardWithSwitchValues()
	{
		SmartDashboard.putBoolean("@ Top", _TopSwitch.get());
		SmartDashboard.putBoolean("@ Bottom", _BottomSwitch.get());
		SmartDashboard.putBoolean("@ Open", _TopSwitch.get());
		SmartDashboard.putBoolean("@ Closed", _CloseSwitch.get());		
	}
	
	/**
	 * Free Motion H-bot Gantry
	 * @param controller
	 */
	public void manipulatorControl(XboxController controller) 
	{
		GlobalFeeder.feedAllMotors();

		double rightX = controller.getRX(Hand.kLeft);
		double leftY = controller.getLY(Hand.kRight);
		
		SmartDashboard.putNumber("Manip 'LeftX'", rightX);
		SmartDashboard.putNumber("Manip 'RightY'", leftY);
		
		UpdateSmartDashboardWithSwitchValues();
		
		boolean didPressResetPositionButton = false;
		
		boolean stillWaiting = false;
		boolean isMoveToPositionCommandPending = false; // TODO: do this for real
		
		// did we recently issue a move to an absolute position command for each motor?
		if (isMoveToPositionCommandPending)
		{
			double A_pos = 100; // TODO: get the position
			double B_pos = 100; // TODO: get the position
			
			// ignore all input until we reach that position
			if (A_pos == _leftMotor.get() && B_pos == _rightMotor.get())
			{
				isMoveToPositionCommandPending = false;
				stillWaiting = false;
			}
			else
			{
				stillWaiting = true;
			}
		}
		
		if (false == stillWaiting)
		{
			//
			// Move H-Bot Gantry Free Style
			//
			if (Math.abs(rightX) > _deadSpaceThreshold || Math.abs(leftY) > _deadSpaceThreshold )
			{
				SmartDashboard.putString("Manip Mode", "Free Style");

				moveInDirection(rightX, leftY);
			}
			//
			// A button has been pressed, move manipulator quickly to initial position
			//
			else if (didPressResetPositionButton)
			{
				SmartDashboard.putString("Manip Mode", "Move to Initial");

				moveToInitialPosition();
			}
			//
			// Dpad Preset move
			//
	//		else if ()
	//		{
	//			
	//		}
			//
			// Bumpers and Triggers?  or other joystick to move motors at a throttled down speed 
			//
	//		else if ()
	//		{
	//			
	//		}
			else
			{
				SmartDashboard.putString("Manip Mode", "Full Stop");

				this.changeControlModeForBothMotors(ControlMode.Position);

				_leftMotor.set(0);
				_rightMotor.set(0);
				_leftMotor.enableBrakeMode(true);
				_rightMotor.enableBrakeMode(true);
			}
		}
	}

	/**
	 * Given inputX and inputY from the joystick, calculate motor movements.
	 * @param inputX
	 * @param inputY
	 * @return Calculated MotorParameters
	 */
	public MotorParameters CalcManipulatorDrive(double inputX, double inputY) 
	{
		//
		// use this line to use the limit switches
		//
		MotorParameters mp = limitAtExtremes(inputX, inputY * -1.0);
		
		//
		// use these 3 lines to disregard the limit switches
		//
//		MotorParameters mp = new MotorParameters();
//		mp.x = inputX;
//		mp.y = inputY * -1.0;

		
		
		//
		// now, calculate motor movements
		// Motor A is ??
		// Motor B is ??
		//
		double R = 0, L = 0;

		//if joystick is in middle, do nothing
		if (Math.abs(mp.x) <= _deadSpaceThreshold)
		{
			mp.x = 0;
		}
		
		//if joystick is in middle, do nothing
		if (Math.abs(mp.y) <= _deadSpaceThreshold)
		{	
			mp.y = 0;
		}
		
		//
		// diagonals
		//	
		if (mp.x < 0 && mp.y < 0)
		{
			// Move down/left
			R = 0.0;
			L = mp.y * mp.y * _maxMotorSpeed;
		}
		else if (mp.x > 0 && mp.y < 0)
		{
			// Move down/right
			R = mp.y * mp.y * (-_maxMotorSpeed);
			L = 0.0;
		}
		else if (mp.x < 0 && mp.y > 0)
		{
			// Move up/left
			R = 0.0;
			L = mp.y * mp.y * (-_maxMotorSpeed);
		}
		else if (mp.x > 0 && mp.y > 0)
		{
			// Move up/right
			R = mp.y * mp.y * _maxMotorSpeed;
			L = 0.0;
		}
		//
		// cardinal directions
		//
		else if (mp.x < 0)
		{
			// move right
			R = mp.x * mp.x * (-_maxMotorSpeed);
			L = mp.x * mp.x * (-_maxMotorSpeed);
		}
		else if (mp.x > 0)
		{
			// move left
			R = mp.x * mp.x * _maxMotorSpeed;
			L = mp.x * mp.x * _maxMotorSpeed;
		}
		else if (mp.y > 0)
		{
			// move up
			R = mp.y * mp.y * _maxMotorSpeed;
			L = mp.y * mp.y * (-_maxMotorSpeed);
		}
		else if (mp.y < 0)
		{
			// move down
			R = mp.y * mp.y * (-_maxMotorSpeed);
			L = mp.y * mp.y * _maxMotorSpeed;
		}

		mp.R = R * _RFix;
		mp.L = L * _LFix;
		
		return mp;
	}
	
	/**
	 * Limit x and y if we are at the extremes of our permissible movement based off 
	 * of the Limit Switches for _TopSwitch, _BottomSwitch, _OpenSwitch, _CloseSwitch.
	 * @param x
	 * @param y
	 * @return adjusted x and y.
	 */
	private MotorParameters limitAtExtremes(double x, double y)
	{
		MotorParameters mp = new MotorParameters();
		mp.x = x;
		mp.y = y;

//		boolean atTop = (_TopSwitch.get());
//		boolean atBottom = (_BottomSwitch.get());
//		boolean atOpen = (_OpenSwitch.get());
//		boolean atClose = (_CloseSwitch.get());

		// TODO: hook up all the switches
		boolean atTop = false;
		boolean atBottom = false;
		boolean atOpen = false;
		boolean atClose = (_CloseSwitch.get());
		
		if (atTop && y > _deadSpaceThreshold) {
			mp.y = 0;
		}

		if (atBottom && y < _deadSpaceThreshold) {
			mp.y = 0;
		}

		if (atOpen && x < _deadSpaceThreshold) {
			mp.x = 0;
		}

		if (atClose && x > _deadSpaceThreshold) {
			mp.x = 0;
		}
		
		return mp;
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
		_leftMotor.set(0.0);
		_rightMotor.set(0.0);
		GlobalFeeder.feedAllMotors();
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

    private void setupMotorSafety() {
        m_safetyHelper = new MotorSafetyHelper(this);
        m_safetyHelper.setExpiration(kDefaultExpirationTime);
        m_safetyHelper.setSafetyEnabled(true);
    }
    
    public void feedAllMotors()
    {
    	if (m_safetyHelper != null)
    	{
    		m_safetyHelper.feed();
    	}
    }
}
