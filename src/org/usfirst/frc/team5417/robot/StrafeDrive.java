package org.usfirst.frc.team5417.robot;

import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.RobotDrive.MotorType;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.TalonSRX;

public class StrafeDrive {

	public class MotorParameters {
		public double f = 0;
		public double s = 0;
		public double t = 0;

		public double L = 0;
		public double C = 0;
		public double R = 0;

	}

	private boolean _shouldInvertL = false;
	private boolean _shouldInvertC = false;
	private boolean _shouldInvertR = false;

	private SpeedController _LMotor;
	private SpeedController _RMotor;
	private SpeedController _CMotor;
	
	private int _LPort = 0, _CPort = 2, _RPort = 1;
	
	private double _LFix = 1.0;
	private double _CFix = 1.0;
	private double _RFix = 1.0;
	
	public StrafeDrive() {
		// TODO: assign values
		_LMotor = new TalonSRX(_LPort);
		_RMotor = new TalonSRX(_RPort);
		_CMotor = new TalonSRX(_CPort);
	}

	public double Clamp(double lowerBound, double value, double upperBound) {
		value = Math.max(lowerBound, value);
		value = Math.min(upperBound, value);
		return value;
	}

	/**
	 * Caps the input value to (-1.0, 1.0)
	 * 
	 * @param num
	 * @return 1.0 is num > 1.0 and -1.0 if num > -1.0
	 */
	protected static double limit(double num) {
		if (num > 1.0) {
			return 1.0;
		}
		if (num < -1.0) {
			return -1.0;
		}
		return num;
	}

	private double SignificantDigits(double value, int digitCount) {
		double factor = Math.pow(10, digitCount);
		int trunc = (int) (factor * value);
		return trunc / factor;
	}

	public void strafeDrive(XboxController controller) {
		MotorParameters mp = CalcStrafeDrive(controller.getLY(Hand.kLeft),
				controller.getRX(Hand.kRight), controller.getLX(Hand.kLeft));
		_LMotor.set (mp.L);
		_RMotor.set (mp.R);
		_CMotor.set (mp.C);
		
	}

	private MotorParameters CalcStrafeDrive(double f, double t, double s) {
		MotorParameters mp = new MotorParameters();

		f = Clamp(-1.0, f, 1.0);
		s = Clamp(-1.0, s, 1.0);
		t = Clamp(-1.0, t, 1.0);

		f = SignificantDigits(f, 2);
		s = SignificantDigits(s, 2);
		t = SignificantDigits(t, 2);

		double fHalf = f * 0.5;
		double tHalf = t * 0.5;

		double numerL = fHalf * fHalf + tHalf * tHalf;
		double numerR = fHalf * fHalf - tHalf * tHalf;
		final double denom = 0.5 * 0.5 + 0.5 * 0.5;

		double L = (numerL / denom) * _LFix;
		double R = (numerR / denom) * _RFix;
		double C = s * s * _CFix;

		L = SignificantDigits(L, 2);
		R = SignificantDigits(R, 2);
		C = SignificantDigits(C, 2);

		final double deadSpaceThreshold = 0.2;

		// gives us our dead space at rest
		if (t > -deadSpaceThreshold && t < deadSpaceThreshold
				&& f > -deadSpaceThreshold && f < deadSpaceThreshold) {
			f = t = 0;
			L = R = 0;
		}

		// gives us our dead space at rest
		if (s > -deadSpaceThreshold && s < deadSpaceThreshold) {
			s = 0;
			C = 0;
		}

		// the following 'if' statements should be greater than some minimum
		// threshold because a very small negative value,
		// even when the sticks are still, could cause the signs of the motor
		// magnitudes to get messed up
		if (t <= -deadSpaceThreshold) {
			// swap L and R
			double temp = L;
			L = R;
			R = temp;
		}

		if (f <= -deadSpaceThreshold) {
			// invert the signs for L and R
			L = -L;
			R = -R;
		}

		if (s <= -deadSpaceThreshold) {
			// invert the sign for C
			C = -C;
		}
		mp.f = f;
		mp.s = s;
		mp.t = t;

		mp.L = _shouldInvertL ? - L: L;
		mp.C = _shouldInvertC ? - C: C;
		mp.R = _shouldInvertR ? - R: R;

		return mp;
		
		
	}
}