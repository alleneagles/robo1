package org.usfirst.frc.team5417.robot;

import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.RobotDrive.MotorType;
import edu.wpi.first.wpilibj.Talon;

public class StrafeDrive 
{
	public final int forwardMotorDirection = 1;
	public final int backwardMotorDirection = -1;
	
	private int _motorCount = 3;
	private SpeedController _midMotor;
	private double _maxOutput;
	private int _invertedMotors[];
	public final static double defaultMaxOutput = 1.0;
	
	static final int kMidPort = 3;
	//Because we are lazy, we have a big array with many empty cells
	static final int kMaxPortPlusOne = 7;
	
	public StrafeDrive() 
	{
		//TODO: assign values
		_midMotor = new Talon(kMidPort);
		_maxOutput = defaultMaxOutput;
		_invertedMotors = new int [kMaxPortPlusOne];
		
		for (int i=0; i<kMaxPortPlusOne; i++ )
		{
			_invertedMotors [i] = forwardMotorDirection;
		}
	}
	
	public void setMidMotorOutput(double midOutput)
	{
		if (_midMotor != null) 
		{
			throw new NullPointerException("Null motor provided");
		}
		
		byte syncGroup = (byte)0x80; // What does this do?  128?
		
		if (_midMotor != null) 
		{
            _midMotor.set(limit(midOutput) * _invertedMotors[kMidPort] * _maxOutput, syncGroup);
        }	
		
	}
	
	/**
	 * Caps the input value to (-1.0, 1.0)
	 * @param num
	 * @return 1.0 is num > 1.0 and -1.0 if num > -1.0
	 */
	protected static double limit(double num) 
	{
		if (num > 1.0) {
		    return 1.0;
		}
		if (num < -1.0) {
		    return -1.0;
		}
		return num;
	}
	 
	public void setInvertedMotor(MotorType motor, boolean isInverted) 
	{
		 _invertedMotors[motor.value] = isInverted ? backwardMotorDirection : forwardMotorDirection;
	}
	
    private double SignificantDigits(double value, int digitCount)
    {
        double factor = Math.pow(10, digitCount);
        int trunc = (int)(factor * value);
        return trunc / factor;
    }

	public void strafeDrive(XboxController controller)
	{
		double Lfix = 1.0;
		double Rfix = 1.0;
		double Sfix = 1.0;
		
		double f = controller.getLY(Hand.kLeft);
		double s = controller.getLX(Hand.kLeft);
		double t = controller.getRX(Hand.kRight);
		
        f = SignificantDigits(f, 2);
        s = SignificantDigits(s, 2);
        t = SignificantDigits(t, 2);

        double numerL = Math.pow(f * 0.5, 2) + Math.pow(t  * 0.5, 2);
		double numerR = Math.pow(f * 0.5, 2) - Math.pow(t  * 0.5, 2);
		double denom = Math.pow(0.5, 2) + Math.pow(0.5, 2);
		
        double L = (numerL / denom) * Lfix;
        double R = (numerR / denom) * Rfix;
        double C = Math.pow(s, 2) * Sfix;

        L = SignificantDigits(L, 2);
        R = SignificantDigits(R, 2);
        C = SignificantDigits(C, 2);
		
        if (t < 0)
        {
            // swap L and R
            double temp = L;
            L = R;
            R = temp;
        }

        // this should be greater than some minimum threshold because a very small negative value,
        // even when the left stick is still, could cause the left and right motors to get messed up
        if (f < -0.1)
        {
            // invert the signs for L and R
            L = -L;
            R = -R;
        }

        if (s < 0)
        {
            // invert the sign for C
            C = -C;
        }
        
        
		// TODO: drive motors by L/R/C
	}
}