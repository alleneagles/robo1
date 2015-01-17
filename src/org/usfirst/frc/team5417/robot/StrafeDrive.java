package org.usfirst.frc.team5417.robot;

import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.RobotDrive.MotorType;
import edu.wpi.first.wpilibj.Talon;

public class StrafeDrive 
{
	private int forwardMotorDirection = 1;
	private int backwardMotorDirection = -1;
	
	private int _motorCount = 3;
	private SpeedController _midMotor;
	private double _maxOutput;
	private int _invertedMotors[];
	public final static double defaultMaxOutput = 1.0;
	
	static final int kMidPort = 3;

	public StrafeDrive() 
	{
		//TODO: assign values
		_midMotor = new Talon(kMidPort);
		_maxOutput = defaultMaxOutput;	
	}
	
	public void setMidMotorOutput(double midOutput) 
	{
		if (_midMotor != null) 
		{
			throw new NullPointerException("Null motor provided");
		}
		
		byte syncGroup = (byte)0x80;
		
		if (_midMotor != null) 
		{
            _midMotor.set(limit(midOutput) * _invertedMotors[kMidPort] * _maxOutput, syncGroup);
		}
	}
	
	protected static double limit(double num) 
	{
		if (num > 1.0) 
		{
			return 1.0;
		}
		if (num < -1.0) 
		{
			return -1.0;
		}
		return num;
	}
	 
	public void setInvertedMotor(MotorType motor, boolean isInverted) 
	{
		_invertedMotors[motor.value] = isInverted ? -1 : 1;
	}
}