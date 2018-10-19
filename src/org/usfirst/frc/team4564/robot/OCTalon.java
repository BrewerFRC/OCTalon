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

	/**
	 * Instantiates a talon at the device number with the device name.
	 * 
	 * 
	 * @param deviceNumber,
	 *            CAN ID of talon.
	 * @param talonName,
	 *            Desired name of talon.
	 */
	public OCTalon(int deviceNumber, String talonName) {
		super(deviceNumber);
		lastMode = ControlMode.PercentOutput;
		name = talonName;
		super.setName(name);
	}

	/**
	 * Instantiates a talon with an sensor at the device number with the device
	 * name.
	 * 
	 * @param deviceNumber
	 *            CAN ID of talon.
	 * @param talonName
	 *            Desired name of talon.
	 * @param sensor
	 *            Sensor to be created.
	 */
	public OCTalon(int deviceNumber, String talonName, FeedbackDevice sensor) {
		super(deviceNumber);
		lastMode = ControlMode.PercentOutput;
		name = talonName;
		super.setName(name);
		this.sensor = sensor;
		isError = false;
		// Does PIDidx need to change?
		errorCheck(super.configSelectedFeedbackSensor(sensor, 0, 0));
		if (sensor == FeedbackDevice.CTRE_MagEncoder_Relative) {
			/* get the absolute pulse width position */
			int pulseWidth = getSensorCollection().getPulseWidthPosition();

			/*
			 * mask out the bottom 12 bits to normalize to [0,4095], or in other
			 * words, to stay within [0,360) degrees
			 */
			pulseWidth = pulseWidth & 0xFFF;
			/* save it to quadrature */
			getSensorCollection().setQuadraturePosition(0, 0);

		}
	}

	/**
	 * Checks if values would change a motor's target or mode.
	 * 
	 * @param value
	 *            to be checked against current.
	 * @param mode
	 *            to be checked against current.
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
	 * @param error
	 *            to be check if not okay
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
	 * Don't use this, use the setType you want to use instead
	 */
	@Override
	public void set(ControlMode mode, double input) {
		Common.debug(
				"The wrong Talon set method was used with on:" + name + " ControlMode:" + mode + " input:" + input);
	}

	/**
	 * Sets the motor to run at an percent
	 * 
	 * @param input
	 *            Percent from 1.0 to -1.0 to run the motor at.
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
	 * @param input
	 *            target to be set in Milliamperes.
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
	 * @param input
	 *            target to be set in native units or scaled.
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
	 * @param input
	 *            targetto be set in native unites or scaled.
	 */
	public void setPosition(double input) {
		if (isChanged(input, ControlMode.Position)) {
			lastValue = input;
			lastMode = ControlMode.Position;
			super.set(ControlMode.Position, input);
		}
	}

	public void follow(double deviceNumber) {
		super.set(ControlMode.Follower, deviceNumber);
	}

	public void update() {
		errorCheck(super.getLastError());
	}

	public void closedLoopRamp(double seconds) {
		errorCheck(super.configClosedloopRamp(seconds, 0));
	}

	public void openLoopRamp(double seconds) {
		errorCheck(super.configOpenloopRamp(seconds, 0));
	}

	public void peakOutput(double percentForward, double percentBackward) {
		errorCheck(super.configPeakOutputForward(percentForward, 10));
		errorCheck(super.configPeakOutputReverse(percentBackward, 10));
	}

	public void minimalOutputs(double minForward, double minBackward) {
		errorCheck(super.configNominalOutputForward(minForward, 10));
		errorCheck(super.configNominalOutputReverse(minBackward, 10));
	}

	/**
	 * Default of 4%
	 * 
	 * @param percent
	 */
	public void deadband(double percent) {
		errorCheck(super.configNeutralDeadband(percent, 10));
	}

	public void configVoltageComp(double voltage) {
		errorCheck(super.configVoltageCompSaturation(voltage, 10));
	}
	
	
	public void cfgVoltageMeasurementFilter(int samples) {
		errorCheck(super.configVoltageMeasurementFilter(samples, 10));
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
	
	public void cfgSensorTerm(SensorTerm sensorTerm, FeedbackDevice device) {
		errorCheck(super.configSensorTerm(sensorTerm, device));
	}
	
	/**
	 * Don't use this becuase it doesn't work, use cfgSensorTerm instead.
	 * 
	 * @return Errorcode FeatureNotSupported.
	 */
	public ErrorCode configSensorTerm(SensorTerm sensorTerm, FeedbackDevice device) {
		Common.debug("Someone is using the super configVoltageMeasurementFilter on talon:"+ name);
		Common.debug("They should really stop because this one does not work.");
		return ErrorCode.FeatureNotSupported;
	}
	

	// also sets
	public void configControlFramePeriod(ControlFrame frame, int periodMs) {
		errorCheck(super.setControlFramePeriod(frame, periodMs));
	}

	// also sets
	public void configControlFramePeriod(int frame, int periodMs) {
		errorCheck(super.setControlFramePeriod(frame, periodMs));
	}

	public void configP(double p) {
		// set timeout to 0 and defaults to one slot
		errorCheck(super.config_kP(0, p, 0));
	}

	public void configF(double f) {
		// set timeout to 0 and defaults to one slot
		errorCheck(super.config_kF(0, f, 0));
	}

	public void configI(double i) {
		// set timeout to 0 and defaults to one slot
		errorCheck(super.config_kP(0, i, 0));
	}

	public void configD(double d) {
		// set timeout to 0 and defaults to one slot
		errorCheck(super.config_kP(0, d, 0));
	}

	public double getParam(ParamEnum enu) {
		// ordinal appears to be 0
		return super.configGetParameter(enu, 0, 0);
	}

	public double getError() {
		return super.getClosedLoopError(0);
	}

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
		super.configSelectedFeedbackCoefficient(scaler, 0, 0);
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
		return scaler;
	}

}
