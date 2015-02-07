package org.usfirst.frc.team5417.robot;

import org.usfirst.frc.team5417.robot.StrafeDrive.MotorParameters;
import org.usfirst.frc.team5417.robot.XboxController.ButtonType;

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
		public double A, B;
	}

	private DigitalInput _TopSwitch;
	private DigitalInput _BottomSwitch;
	private DigitalInput _OpenSwitch;
	private DigitalInput _CloseSwitch;
	
	/* Range [0.0-1.0] where 0.0 will stop the motor and 1.0 will allow the motor to run at full. */
	private final double _LFix = 0.3;
	private final double _RFix = 0.3;
	
	/* Threshold value to use for when joystick is at center */
	private final double _deadSpaceThreshold = 0.2 ;
	
	private final double _maxMotorSpeed = 1.0;

    public static final double kDefaultExpirationTime = 0.1;
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
		_aMotor.changeControlMode(controlMode);
		_bMotor.changeControlMode(controlMode);		
	}
	
	public void moveInDirection(double leftX, double rightY)
	{
		this.changeControlModeForBothMotors(ControlMode.PercentVbus);
		
		MotorParameters mp = CalcManipulatorDrive(leftX, rightY);
		
		_aMotor.enableBrakeMode(false);
		_bMotor.enableBrakeMode(false);

		UpdateSmartDashboard(mp);
		_aMotor.set(mp.A);
		_bMotor.set(mp.B);
		
	}
	
	public void UpdateSmartDashboard(MotorParameters mp)
	{
		SmartDashboard.putNumber("Manip 'x'", mp.x);
		SmartDashboard.putNumber("Manip 'y'", mp.y);
		SmartDashboard.putNumber("Manip 'A'", mp.A);
		SmartDashboard.putNumber("Manip 'B'", mp.B);
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
		
		boolean didPressResetPositionButton = false;
		
		boolean stillWaiting = false;
		boolean isMoveToPositionCommandPending = false; // TODO: do this for real
		
		// did we recently issue a move to an absolute position command for each motor?
		if (isMoveToPositionCommandPending)
		{
			double A_pos = 100; // TODO: get the position
			double B_pos = 100; // TODO: get the position
			
			// ignore all input until we reach that position
			if (A_pos == _aMotor.get() && B_pos == _bMotor.get())
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

				_aMotor.set(0);
				_bMotor.set(0);
				_aMotor.enableBrakeMode(true);
				_bMotor.enableBrakeMode(true);
			}
		}
	}

	/**
	 * Given x and y from the joystick, calculate motor movements.
	 * @param x
	 * @param y
	 * @return 
	 */
	public MotorParameters CalcManipulatorDrive(double x, double y) 
	{
		//MotorParameters mp = limitAtExtremes(x, y);
		MotorParameters mp = new MotorParameters();
		mp.x = x;
		mp.y = y;

		//
		// now, calculate motor movements
		// Motor A is ??
		// Motor B is ??
		//
		double A = 0, B = 0;

		//if joystick is in middle, do nothing
		if (Math.abs(x) <= _deadSpaceThreshold)
		{
			x = 0;
		}
		
		//if joystick is in middle, do nothing
		if (Math.abs(y) <= _deadSpaceThreshold)
		{	
			y = 0;
		}
		
		//
		// diagonals
		//	
		if (x < 0 && y < 0)
		{
			// Move down/left
			A = x * y * (-_maxMotorSpeed);
			B = 0.0;
		}
		else if (x > 0 && y < 0)
		{
			// Move down/right
			A = x * y * _maxMotorSpeed;
			B = 0.0;
		}
		else if (x < 0 && y > 0)
		{
			// Move up/left
			A = 0.0;
			B = x * y * (-_maxMotorSpeed);
		}
		else if (x > 0 && y > 0)
		{
			// Move up/right
			A = 0.0;
			B = x * y * _maxMotorSpeed;
		}
		//
		// cardinal directions
		//
		else if (x < 0)
		{
			// move left
			A = x * x * _maxMotorSpeed;
			B = x * x * _maxMotorSpeed;
		}
		else if (x > 0)
		{
			// move right
			A = x * x * (-_maxMotorSpeed);
			B = x * x * (-_maxMotorSpeed);
		}
		else if (y > 0)
		{
			// move up
			A = y * y * _maxMotorSpeed;
			B = y * y * (-_maxMotorSpeed);
		}
		else if (y < 0)
		{
			// move down
			A = y * y * (-_maxMotorSpeed);
			B = y * y * _maxMotorSpeed;
		}

		mp.x = x;
		mp.y = y;
		mp.A = A * _RFix; // TODO - which one is left and which one is right?
		mp.B = B * _LFix;
		
		return mp;
	}
	
	/**
	 * Limit x and y if we are at the extremes of our permissible movement.
	 * @param x
	 * @param y
	 * @return adjusted x and y.
	 */
	private MotorParameters limitAtExtremes(double x, double y)
	{
		boolean atTop = (_TopSwitch.get());
		boolean atBottom = (_BottomSwitch.get());
		boolean atOpen = (_OpenSwitch.get());
		boolean atClose = (_CloseSwitch.get());
		
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
		
		MotorParameters mp = new MotorParameters();
		mp.x = x;
		mp.y = y;
		
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
		_aMotor.set(0.0);
		_bMotor.set(0.0);
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
