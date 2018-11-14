package org.usfirst.frc.team4564.robot;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.ParamEnum;
import com.ctre.phoenix.motorcontrol.ControlFrame;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.SensorTerm;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

//was talonSRX
/**
 * A wrapper to simplify Talon SRX's for easy use.
 * 
 * @author Brewer FIRST Robotics Team 4564
 * @author Brent Roberts
 */
public class OCTalon extends WPI_TalonSRX {
	private double lastValue;
	private ControlMode lastMode;
	private String name = "notSet";
	private boolean isError;
	private FeedbackDevice sensor;
	private int count = 0;
	private double scaler = 1;
	private double deviceNumber;
	private int timeoutMs = 0;

	/**
	 * Instantiates a talon at the device number with the device name.
	 * 
	 * 
	 * @param deviceNumber CAN ID of talon.
	 * @param talonName Desired name of talon.
	 */
	public OCTalon(int deviceNumber, String talonName) {
		super(deviceNumber);
		this.deviceNumber = deviceNumber;
		this.lastMode = ControlMode.PercentOutput;
		this.name = talonName;
		this.isError = false;
		super.setName(name);
	}

	/**
	 * Instantiates a talon with an sensor at the device number with the device
	 * name.
	 * 
	 * @param deviceNumber CAN ID of talon.
	 * @param talonName Desired name of talon.
	 * @param sensor Sensor to be created.
	 */
	public OCTalon(int deviceNumber, String talonName, FeedbackDevice sensor) {
		super(deviceNumber);
		this.deviceNumber = deviceNumber;
		this.lastMode = ControlMode.PercentOutput;
		this.name = talonName;
		super.setName(name);
		this.sensor = sensor;
		this.isError = false;
		// Does PIDidx need to change?
		errorCheck(super.configSelectedFeedbackSensor(sensor, 0, getTimeoutMs()));
		if (sensor == FeedbackDevice.CTRE_MagEncoder_Relative) {
			/* get the absolute pulse width position */
			int pulseWidth = getSensorCollection().getPulseWidthPosition();

			/*
			 * mask out the bottom 12 bits to normalize to [0,4095], or in other
			 * words, to stay within [0,360) degrees
			 */
			pulseWidth = pulseWidth & 0xFFF;
			/* save it to quadrature */
			getSensorCollection().setQuadraturePosition(0, getTimeoutMs());

		}
	}

	/**
	 * Checks if values would change a motor's target or mode.
	 * 
	 * @param value to be checked against current.
	 * @param mode to be checked against current.
	 * @return whether the values sent would change either target or mode.
	 */
	private boolean isChanged(double value, ControlMode mode) {
		if (value != lastValue || mode != lastMode) {
			// Common.debug(name+"changed = true");
			return true;
		} else {
			// Common.debug(name+"changed = false");
			return false;
		}
	}

	/**
	 * Checks if error code is abnormal and if it is debugs the code with the
	 * talon name and sets the talon to error mode.
	 * 
	 * @param error to be checked if not okay.
	 */
	public void errorCheck(ErrorCode error) {
		if (error != ErrorCode.OK) {
			isError = true;
			Common.debug(name + ": " + error.toString());
		} else {
			isError = false;
		}
	}

	/**
	 * Controls any error by setting the talon to neutral output along with
	 * debugging the talon's error state.
	 * 
	 */
	public void handle() {
		if (isError) {
			this.neutralOutput();
			Common.debug(name + " in error state");
		}
	}

	/**
	 * Returns whether the talon is error.
	 * 
	 * @return isError Whether or not the talon has errored.
	 */
	public boolean error() {
		return isError;
	}

	/**
	 * Don't use this, use the setType you want to use instead.
	 */
	@Override
	public void set(ControlMode mode, double input) {
		Common.debug(
				"The wrong Talon set method was used with on:" + name + " ControlMode:" + mode + " input:" + input);
	}

	/**
	 * Sets the motor to run at an percent
	 * 
	 * @param input Percent from 1.0 to -1.0 to run the motor at.
	 * @param input Percent from 1.0 to -1.0 to run the motor at.
	 */
	public void setPercent(double input) {
		if (isChanged(input, ControlMode.PercentOutput)) {
			lastValue = input;
			lastMode = ControlMode.PercentOutput;
			Common.debug("name" + input);
			super.set(ControlMode.PercentOutput, input);
		}
	}

	/**
	 * Sets an target for an closed loop current PID.
	 * 
	 * @param input target to be set in Milliamperes.
	 */
	public void setCurrent(double input) {
		if (isChanged(input, ControlMode.Current)) {
			lastValue = input;
			lastMode = ControlMode.Current;
			super.set(ControlMode.Current, input);
		}
	}

	/**
	 * Sets an target for an closed loop velocity PID.
	 * 
	 * @param input target to be set in native units or scaled.
	 */
	public void setVelocity(double input) {
		if (isChanged(input, ControlMode.Velocity)) {
			lastValue = input;
			lastMode = ControlMode.Velocity;
			super.set(ControlMode.Velocity, input);
		}
	}

	/**
	 * Sets an target for an closed loop position PID.
	 * 
	 * @param input Target to be set in native unites or scaled.
	 */
	public void setPosition(double input) {
		if (isChanged(input, ControlMode.Position)) {
			lastValue = input;
			lastMode = ControlMode.Position;
			super.set(ControlMode.Position, input);
		}
	}

	/**
	 * Sets an talon to follow another CAN motor controller
	 * 
	 * @param deviceNumber The Device Number of the talon to be followed.
	 */
	public void follow(double deviceNumber) {
		super.set(ControlMode.Follower, deviceNumber);
	}
	
	/**
	 * Tests last error for potential problems.
	 */
	public void update() {
		errorCheck(super.getLastError());
	}
	
	/**
	 * Sets an ramp on closed loop control.
	 * Affects all set functions except for percent and follow.
	 * 
	 * @param seconds Minimum desired time to go from neutral to full throttle. A value of '0' will disable the ramp.
	 */
	public void closedLoopRamp(double seconds) {
		errorCheck(super.configClosedloopRamp(seconds, 0));
	}

	/**
	 * Sets an ramp on open loop control.
	 * Affects percent.
	 * 
	 * @param seconds Minimum desired time to go from neutral to full throttle. A value of '0' will disable the ramp.
	 */
	public void openLoopRamp(double seconds) {
		errorCheck(super.configOpenloopRamp(seconds, 0));
	}

	/**
	 *  Sets both the peak forward and backward outputs.
	 * 
	 * @param percentForward Maximum output percentage forward from 0 to 1.
	 * @param percentBackward Maximum output percentage backward from 0 to 1.
	 */
	public void peakOutput(double percentForward, double percentBackward) {
		errorCheck(super.configPeakOutputForward(percentForward, getTimeoutMs()));
		errorCheck(super.configPeakOutputReverse(percentBackward, getTimeoutMs()));
	}

	/**
	 * Sets both minimum outputs forward and backward outputs.
	 * 
	 * @param minForward Minimum Output percentage forward from 0 to +1.
	 * @param minBackward Minimum Output percentage backward from -1 to 0.
	 */
	public void minimalOutputs(double minForward, double minBackward) {
		errorCheck(super.configNominalOutputForward(minForward, getTimeoutMs()));
		errorCheck(super.configNominalOutputReverse(minBackward, getTimeoutMs()));
	}

	/**
	 * Sets and deadband of inputs to not be set.
	 * Default of 4%
	 * 
	 * @param percent Percentage to be set as deadband.
	 */
	public void deadband(double percent) {
		errorCheck(super.configNeutralDeadband(percent, getTimeoutMs()));
	}

	/**
	 * Configures the Voltage Compensation saturation voltage.
	 * 
	 * @param voltage This is the max voltage to apply to the hbridge when voltage
	 *            compensation is enabled.  For example, if 10 (volts) is specified
	 *            and a TalonSRX is commanded to 0.5 (PercentOutput, closed-loop, etc)
	 *            then the TalonSRX will attempt to apply a duty-cycle to produce 5V.
	 */
	public void configVoltageComp(double voltage) {
		errorCheck(super.configVoltageCompSaturation(voltage, getTimeoutMs()));
	}
	
	/**
	 * Configures the number of samples to be in the rolling voltage average measurement. 
	 * 
	 * @param samples Number of samples in the rolling average of voltage
	 *            measurement.
	 */
	public void cfgVoltageMeasurementFilter(int samples) {
		errorCheck(super.configVoltageMeasurementFilter(samples, getTimeoutMs()));
	}
	
	/**
	 * Don't use this becuase it doesn't work, use cfgVoltageMeasurementFilter instead.
	 * 
	 * @return Errorcode FeatureNotSupported.
	 */
	public ErrorCode configVoltageMeasuremetFilter(int samples) {
		Common.debug("Someone is using the super configVoltageMeasurementFilter on talon:"+ name);
		Common.debug("They should really stop because this one does not work.");
		return ErrorCode.FeatureNotSupported;
	}
	
	/**
	 * Select what sensor term should be bound to switch feedback device.
	 * Sensor Sum = Sensor Sum Term 0 - Sensor Sum Term 1
	 * Sensor Difference = Sensor Diff Term 0 - Sensor Diff Term 1
	 * The four terms are specified with this routine.  Then Sensor Sum/Difference
	 * can be selected for closed-looping.
	 * 
	 * @param sensorTerm Which sensor term to bind to a feedback source.
	 */
	public void cfgSensorTerm(SensorTerm sensorTerm) {
		errorCheck(super.configSensorTerm(sensorTerm, this.sensor, getTimeoutMs()));
	}
	
	/**
	 * Don't use this because it doesn't work, use cfgSensorTerm instead.
	 * 
	 * @return Errorcode FeatureNotSupported.
	 */
	public ErrorCode configSensorTerm(SensorTerm sensorTerm, FeedbackDevice device) {
		Common.debug("Someone is using the super configVoltageMeasurementFilter on talon:"+ name);
		Common.debug("They should really stop because this one does not work.");
		return ErrorCode.FeatureNotSupported;
	}
	

	/**
	 * Sets the period of the given control frame.
	 * 
	 * @param frame Frame whose period is to be changed.
	 * @param periodMs Period in ms for the given frame.
	 */
	public void configControlFramePeriod(ControlFrame frame, int periodMs) {
		errorCheck(super.setControlFramePeriod(frame, periodMs));
	}

	/**
	 * Sets the period of the given control frame.
	 * 
	 * @param frame Frame whose period is to be changed.
	 * @param periodMs Period in ms for the given frame.
	 */
	public void configControlFramePeriod(int frame, int periodMs) {
		errorCheck(super.setControlFramePeriod(frame, periodMs));
	}

	/**
	 * Sets the proportional term for an closed loop PID.
	 * Should only be used with the OCTalonPID
	 * sets timeout to 0 and defaults to the 0 slot
	 * 
	 * @param p The proportional term to be set.
	 */
	public void configP(double p) {
		errorCheck(super.config_kP(0, p, getTimeoutMs()));
	}

	/**
	 * Sets the feed forward term for an closed loop PID.
	 * Should only be used with the OCTalonPID
	 * sets timeout to 0 and defaults to the 0 slot
	 * 
	 * @param f The feedforward term to be set.
	 */
	public void configF(double f) {
		// set timeout to 0 and defaults to one slot
		errorCheck(super.config_kF(0, f, getTimeoutMs()));
	}

	/**
	 * Sets the integral term for an closed loop PID.
	 * Should only be used with the OCTalonPID
	 * sets timeout to 0 and defaults to the 0 slot
	 * 
	 * @param i The integral term to be set.
	 */
	public void configI(double i) {
		// set timeout to 0 and defaults to one slot
		errorCheck(super.config_kP(0, i, getTimeoutMs()));
	}

	/**
	 * Sets the derivative term for an closed loop PID.
	 * Should only be used with the OCTalonPID
	 * sets timeout to 0 and defaults to the 0 slot
	 * 
	 * @param d The derivative term to be set.
	 */
	public void configD(double d) {
		// set timeout to 0 and defaults to one slot
		errorCheck(super.config_kP(0, d, getTimeoutMs()));
	}

	/**
	 * Returns an parameter on the talon.
	 * Uses an ordinal of 0 because that is what the example uses and I don't know what it is.
	 * 
	 * @param enu ParamEnum of the wanted param.
	 * @return Parameter
	 */
	public double getParam(ParamEnum enu) {
		// ordinal appears to be 0
		return super.configGetParameter(enu, 0, getTimeoutMs());
	}

	/**
	 * Returns the closed loop error of the 0 loop.
	 * 
	 * @return The closed loop error of the 0 loop.
	 */
	public double getError() {
		return super.getClosedLoopError(0);
	}

	/**
	 * Returns the closed loop target of the 0 loop.
	 * 
	 * @return The closed loop target of the 0 loop.
	 */
	public double getTarget() {
		return super.getClosedLoopTarget(0);
	}

	/**
	 * Returns the current sensor position multiplied by scaler.
	 * 
	 * @return Sensor position multiplied by scaler.
	 */
	public double getPosition() {
		double pos = super.getSelectedSensorPosition(0) * scaler;
		return pos;
	}

	/**
	 * Returns the current sensor velocity multiplied by scaler.
	 * 
	 * @return Sensor velocity multiplied by scaler.
	 */
	public double getVelocity() {
		double pos = super.getSelectedSensorVelocity(0) * scaler;
		return pos;
	}

	/**
	 * Gets the absolute position from an mag encoder.
	 * 
	 * @return Absolute position from 0 to 360.
	 */
	public double getABSPosition() {
		int pos = super.getSensorCollection().getPulseWidthPosition() & 0xFFF;
		double deg = pos * 360.0 / 4906.0;

		return deg;
	}

	// Test whether scaler is neccasary clientside
	/**
	 * Sets the scaler of all non absolute sensor readings in the class.
	 * Defaults to 1. The scaler must be within [0.000015258789, 1.000], and has
	 * a resolution of 0.0000152587890625.
	 * 
	 * @param Scaler
	 *            Scaler of all non absolute sensor readings.
	 */
	public void setSensorScaler(double scaler) {
		this.scaler = scaler;
		super.configSelectedFeedbackCoefficient(scaler, 0, getTimeoutMs());
	}

	// No remote feed back device?????????
	// Unkown error
	/*
	 * public void configFeedbackCoefficient(double coefficient) {
	 * errorCheck(super.config(coefficient, 0, 10)); }
	 */
	/**
	 * Returns the current sensor scaler. Defaults to 1.
	 * 
	 * @return Scaler of all non absolute sensor readings.
	 */
	public double getSensorScaler() {
		return this.scaler;
	}
	
	/**
	 * Returns the talon's device number.
	 * 
	 * @return The talon's device number.
	 */
	public double getDeviceNumber() {
		return this.deviceNumber;
	}

	/**
	 * Returns the talon's sensor type.
	 * 
	 * @return the talon's sensor type.
	 */
	public FeedbackDevice getSensor() {
		return this.sensor;
	}
	
	/**
	 * Returns the talon's TimeoutMs.
	 * 
	 * @return the talon's TimeoutMs.
	 */
	public int getTimeoutMs() {
		return this.timeoutMs;
	}
	
	/**
	 * Sets the timeout in Ms to be used in any functions that require it.
	 * If 0 will not check for an timeout.
	 * 
	 * @param timeoutMs The timeout in Ms 
	 */
	public void setTimeoutMs(int timeoutMs) {
		this.timeoutMs = timeoutMs;
	}
}
