package org.usfirst.frc.team5417.robot;

import edu.wpi.first.wpilibj.DriverStation;
//import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.GenericHID.Hand;
//import edu.wpi.first.wpilibj.Joystick.AxisType;
//import edu.wpi.first.wpilibj.Joystick.ButtonType;
//import edu.wpi.first.wpilibj.Joystick.RumbleType;
import edu.wpi.first.wpilibj.communication.FRCNetworkCommunicationsLibrary;
import edu.wpi.first.wpilibj.communication.UsageReporting;
import edu.wpi.first.wpilibj.communication.FRCNetworkCommunicationsLibrary.tResourceType;

/**
 * Handle input from Xbox controllers connected to the Driver Station.
 * This class handles standard input that comes from the Driver Station. Each time a value is requested
 * the most recent value is returned. There is a single class instance for each XboxController and the mapping
 * of ports to hardware buttons depends on the code in the driver station.
 */
public class XboxController  {

    static final byte kDefaultLXAxis = 0;
    static final byte kDefaultLYAxis = 1;

    static final byte kDefaultRXAxis = 4;
    static final byte kDefaultRYAxis = 5;

    static final byte kDefaultZAxis = 2;
    static final byte kDefaultTwistAxis = 2;
    
    static final int kDefaultTriggerButton = 1;
    static final int kDefaultTopButton = 2;

    /**
     * Represents an analog axis on a XboxController.
     */
    public static class AxisType 
    {
        /**
         * The integer value representing this enumeration
         */
        public final int value;
        static final int kLX_val = 0;
        static final int kLY_val = 1;
        static final int kZ_val = 2;
//        static final int kTwist_val = 3;
        static final int kRX_val = 4;
        static final int kRY_val = 5;
        static final int kNumAxis_val = 6;
        
        /**
         * axis: x-axis
         */
        public static final AxisType kLX = new AxisType(kLX_val);
        /**
         * axis: y-axis
         */
        public static final AxisType kLY = new AxisType(kLY_val);
        /**
         * axis: z-axis
         */
        public static final AxisType kZ = new AxisType(kZ_val);
//        /**
//         * axis: twist
//         */
//        public static final AxisType kTwist = new AxisType(kTwist_val);
        /**
         * axis: x-axis
         */
        public static final AxisType kRX = new AxisType(kRX_val);
        /**
         * axis: y-axis
         */
        public static final AxisType kRY = new AxisType(kRY_val);
//        /**
//         * axis: throttle
//         */
//        public static final AxisType kThrottle = new AxisType(kThrottle_val);
        /**
         * axis: number of axis
         */
        public static final AxisType kNumAxis = new AxisType(kNumAxis_val);

        private AxisType(int value) {
            this.value = value;
        }
    }

    /**
     * Represents a digital button on the XboxController
     */
    public static class ButtonType 
    {
        /**
         * The integer value representing this enumeration
         */
        public final int value;
        static final int kTrigger_val = 0;
        static final int kTop_val = 1;
        static final int kNumButton_val = 2;
        /**
         * button: trigger
         */
        public static final ButtonType kTrigger = new ButtonType((kTrigger_val));
        /**
         * button: top button
         */
        public static final ButtonType kTop = new ButtonType(kTop_val);
        /**
         * button: num button types
         */
        public static final ButtonType kNumButton = new ButtonType((kNumButton_val));

        private ButtonType(int value) {
            this.value = value;
        }
    }
    
    
    /**
     * Represents a rumble output on the XboxController
     */
    public static class RumbleType 
    {
        /**
         * The integer value representing this enumeration
         */
        public final int value;
        static final int kLeftRumble_val = 0;
        static final int kRightRumble_val = 1;
        /**
         * Left Rumble
         */
        public static final RumbleType kLeftRumble = new RumbleType((kLeftRumble_val));
        /**
         * Right Rumble
         */
        public static final RumbleType kRightRumble = new RumbleType(kRightRumble_val);

        private RumbleType(int value) {
            this.value = value;
        }
    }
    
    private DriverStation m_ds;
    private final int m_port;
    private final byte[] m_axes;
    private final byte[] m_buttons;
    private int m_outputs;
    private short m_leftRumble;
    private short m_rightRumble;

    /**
     * Construct an instance of an XboxController.
     * The XBoxController index is the usb port on the drivers station.
     *
     * @param port The port on the driver station that the XboxController is plugged into.
     */
    public XboxController(final int port) 
    {
        this(port, AxisType.kNumAxis.value, ButtonType.kNumButton.value);

        m_axes[AxisType.kLX.value] = kDefaultLXAxis;
        m_axes[AxisType.kLY.value] = kDefaultLYAxis;
        m_axes[AxisType.kZ.value] = kDefaultZAxis;
//        m_axes[AxisType.kTwist.value] = kDefaultTwistAxis;
//        m_axes[AxisType.kThrottle.value] = kDefaultThrottleAxis;
        m_axes[AxisType.kRX.value] = kDefaultRXAxis;
        m_axes[AxisType.kRY.value] = kDefaultRYAxis;

        m_buttons[ButtonType.kTrigger.value] = kDefaultTriggerButton;
        m_buttons[ButtonType.kTop.value] = kDefaultTopButton;

        UsageReporting.report(tResourceType.kResourceType_Joystick, port);
    }

    /**
     * Protected version of the constructor to be called by sub-classes.
     *
     * This constructor allows the subclass to configure the number of constants
     * for axes and buttons.
     *
     * @param port The port on the driver station that the XboxController is plugged into.
     * @param numAxisTypes The number of axis types in the enum.
     * @param numButtonTypes The number of button types in the enum.
     */
    protected XboxController(int port, int numAxisTypes, int numButtonTypes) 
    {
        m_ds = DriverStation.getInstance();
        m_axes = new byte[numAxisTypes];
        m_buttons = new byte[numButtonTypes];
        m_port = port;
    }

    /**
     * Get the X value of the XboxController.
     * This depends on the mapping of the XboxController connected to the current port.
     *
     * @param hand Unused
     * @return The X value of the XboxController.
     */
    public double getLX(Hand hand) 
    {
        return getRawAxis(m_axes[AxisType.kLX.value]);
    }

    /**
     * Get the Y value of the XboxController.
     * This depends on the mapping of the XboxController connected to the current port.
     *
     * @param hand Unused
     * @return The Y value of the XboxController.
     */
    public double getLY(Hand hand) 
    {
        return getRawAxis(m_axes[AxisType.kLY.value]);
    }

    /**
     * Get the X value of the XboxController.
     * This depends on the mapping of the XboxController connected to the current port.
     *
     * @param hand Unused
     * @return The X value of the XboxController.
     */
    public double getRX(Hand hand) 
    {
        return getRawAxis(m_axes[AxisType.kRX.value]);
    }

    /**
     * Get the Y value of the XboxController.
     * This depends on the mapping of the XboxController connected to the current port.
     *
     * @param hand Unused
     * @return The Y value of the XboxController.
     */
    public double getRY(Hand hand) 
    {
        return getRawAxis(m_axes[AxisType.kRY.value]);
    }

    /**
     * Get the Z value of the XboxController.
     * This depends on the mapping of the XboxController connected to the current port.
     *
     * @param hand Unused
     * @return The Z value of the XboxController.
     */
    public double getZ(Hand hand) 
    {
        return getRawAxis(m_axes[AxisType.kZ.value]);
    }

//    /**
//     * Get the twist value of the current XboxController.
//     * This depends on the mapping of the XboxController connected to the current port.
//     *
//     * @return The Twist value of the XboxController.
//     */
//    public double getTwist() {
//        return getRawAxis(m_axes[AxisType.kTwist.value]);
//    }

//    /**
//     * Get the throttle value of the current XboxController.
//     * This depends on the mapping of the XboxController connected to the current port.
//     *
//     * @return The Throttle value of the XboxController.
//     */
//    public double getThrottle() {
//        return getRawAxis(m_axes[AxisType.kThrottle.value]);
//    }

    /**
     * Get the value of the axis.
     *
     * @param axis The axis to read, starting at 0.
     * @return The value of the axis.
     */
    public double getRawAxis(final int axis) {
        return m_ds.getStickAxis(m_port, axis);
    }

    /**
     * For the current XboxController, return the axis determined by the argument.
     *
     * This is for cases where the XboxController axis is returned programatically, otherwise one of the
     * previous functions would be preferable (for example getX()).
     *
     * @param axis The axis to read.
     * @return The value of the axis.
     */
    public double getAxis(final AxisType axis) {
        switch (axis.value) {
        case AxisType.kLX_val:
            return getLX(Hand.kLeft);
        case AxisType.kLY_val:
            return getLY(Hand.kLeft);
        case AxisType.kZ_val:
            return getZ(Hand.kRight);
//        case AxisType.kTwist_val:
//            return getTwist();
//        case AxisType.kThrottle_val:
//            return getThrottle();
        case AxisType.kRX_val:
            return getRX(Hand.kRight);
        case AxisType.kRY_val:
            return getRY(Hand.kRight);
        default:
            return 0.0;
        }
    }

    /**
    * For the current XboxController, return the number of axis
    */
    public int getAxisCount(){
        return m_ds.getStickAxisCount(m_port);
    }

    /**
     * Read the state of the trigger on the XboxController.
     *
     * Look up which button has been assigned to the trigger and read its state.
     *
     * @param hand This parameter is ignored for the XboxController class and is only here to complete the GenericHID interface.
     * @return The state of the trigger.
     */
    public boolean getTrigger(Hand hand) {
        return getRawButton(m_buttons[ButtonType.kTrigger.value]);
    }

    /**
     * Read the state of the top button on the XboxController.
     *
     * Look up which button has been assigned to the top and read its state.
     *
     * @param hand This parameter is ignored for the XboxController class and is only here to complete the GenericHID interface.
     * @return The state of the top button.
     */
    public boolean getTop(Hand hand) {
        return getRawButton(m_buttons[ButtonType.kTop.value]);
    }

    /**
     * This is not supported for the XboxController.
     * This method is only here to complete the GenericHID interface.
     *
     * @param hand This parameter is ignored for the XboxController class and is only here to complete the GenericHID interface.
     * @return The state of the bumper (always false)
     */
    public boolean getBumper(Hand hand) {
        return false;
    }

    /**
     * Get the button value (starting at button 1)
     *
     * The appropriate button is returned as a boolean value.
     *
     * @param button The button number to be read (starting at 1).
     * @return The state of the button.
     */
    public boolean getRawButton(final int button) {
        return m_ds.getStickButton(m_port, (byte)button);
    }

    /**
    * For the current XboxController, return the number of buttons
    */
    public int getButtonCount(){
        return m_ds.getStickButtonCount(m_port);
    }

    /**
     * Get the state of a POV on the XboxController.
     *
	 * @param pov The index of the POV to read (starting at 0)
     * @return the angle of the POV in degrees, or -1 if the POV is not pressed.
     */
    public int getPOV(int pov) {
        return m_ds.getStickPOV(m_port, pov);
    }

    /**
    * For the current XboxController, return the number of POVs
    */
    public int getPOVCount(){
        return m_ds.getStickPOVCount(m_port);
    }

    /**
     * Get buttons based on an enumerated type.
     *
     * The button type will be looked up in the list of buttons and then read.
     *
     * @param button The type of button to read.
     * @return The state of the button.
     */
    public boolean getButton(ButtonType button) {
        switch (button.value) {
        case ButtonType.kTrigger_val:
            return getTrigger(Hand.kRight);
        case ButtonType.kTop_val:
            return getTop(Hand.kRight);
        default:
            return false;
        }
    }

//    /**
//     * Get the magnitude of the direction vector formed by the XboxController's
//     * current position relative to its origin
//     *
//     * @return The magnitude of the direction vector
//     */
//    public double getMagnitude() {
//        return Math.sqrt(Math.pow(getX(), 2) + Math.pow(getY(), 2));
//    }

//    /**
//     * Get the direction of the vector formed by the XboxController and its origin
//     * in radians
//     *
//     * @return The direction of the vector in radians
//     */
//    public double getDirectionRadians() {
//        return Math.atan2(getX(), -getY());
//    }
//
//    /**
//     * Get the direction of the vector formed by the XboxController and its origin
//     * in degrees
//     *
//     * uses acos(-1) to represent Pi due to absence of readily accessable Pi
//     * constant in C++
//     *
//     * @return The direction of the vector in degrees
//     */
//    public double getDirectionDegrees() {
//        return Math.toDegrees(getDirectionRadians());
//    }

    /**
     * Get the channel currently associated with the specified axis.
     *
     * @param axis The axis to look up the channel for.
     * @return The channel fr the axis.
     */
    public int getAxisChannel(AxisType axis) {
        return m_axes[axis.value];
    }

    /**
     * Set the channel associated with a specified axis.
     *
     * @param axis The axis to set the channel for.
     * @param channel The channel to set the axis to.
     */
    public void setAxisChannel(AxisType axis, int channel) {
        m_axes[axis.value] = (byte) channel;
    }
    
    /**
     * Set the rumble output for the XboxController. The DS currently supports 2 rumble values,
     * left rumble and right rumble
     * @param type Which rumble value to set
     * @param value The normalized value (0 to 1) to set the rumble to
     */
    public void setRumble(RumbleType type, float value) {
        if (value < 0)
            value = 0;
        else if (value > 1)
            value = 1;
        if (type.value == RumbleType.kLeftRumble_val)
            m_leftRumble = (short)(value*65535);
        else
            m_rightRumble = (short)(value*65535);
        FRCNetworkCommunicationsLibrary.HALSetJoystickOutputs((byte)m_port, m_outputs, m_leftRumble, m_rightRumble);
    }

    /**
     * Set a single HID output value for the XboxController.
     * @param outputNumber The index of the output to set (1-32)
     * @param value The value to set the output to
     */
        
    public void setOutput(int outputNumber, boolean value) {
        m_outputs = (m_outputs & ~(1 << (outputNumber-1))) | ((value?1:0) << (outputNumber-1));
        FRCNetworkCommunicationsLibrary.HALSetJoystickOutputs((byte)m_port, m_outputs, m_leftRumble, m_rightRumble);
    }

    /**
     * Set all HID output values for the XboxController.
     * @param value The 32 bit output value (1 bit for each output)
     */
    public void setOutputs(int value) {
        m_outputs = value;
        FRCNetworkCommunicationsLibrary.HALSetJoystickOutputs((byte)m_port, m_outputs, m_leftRumble, m_rightRumble);
    }
}
