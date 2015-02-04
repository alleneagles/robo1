package org.usfirst.frc.team5417.robot;

import java.util.ArrayList;

public class GlobalFeeder
{
	static private ArrayList<Feedable> _feedableMotorSafetyImpls = new ArrayList<Feedable>();

	static public void addFeedableMotorSafety(Feedable motorController)
	{
		if (motorController != null)
		{
			_feedableMotorSafetyImpls.add(motorController);
		}
	}
	
	static public void feedAllMotors()
	{
		for (Feedable f : _feedableMotorSafetyImpls)
		{
			if (f != null)
			{
				f.feedAllMotors();
			}
		}
	}
}
