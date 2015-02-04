package org.usfirst.frc.team5417.robot;

import java.util.ArrayList;

public class GlobalFeeder
{
	static private ArrayList<Feedable> _feedableMotorSafetyImpls;

	static public void addFeedableMotorSafety(Feedable motorController)
	{
		_feedableMotorSafetyImpls.add(motorController);
	}
	
	static public void feedAllMotors()
	{
		for (Feedable f : _feedableMotorSafetyImpls)
		{
			f.feedAllMotors();
		}
	}
}
