package org.usfirst.frc.team4564.robot;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.ParamEnum;
import com.ctre.phoenix.motorcontrol.ControlFrame;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.SensorTerm;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

//was talonSRX
public class OCTalon extends WPI_TalonSRX{
	double lastValue;
	ControlMode lastMode;
	String name;
	boolean isError;

	public OCTalon(int deviceNumber, String talonName) {
		super(deviceNumber);
		lastMode = ControlMode.PercentOutput;
		talonName = name;
	}
	
	public OCTalon(int deviceNumber, String talonName, FeedbackDevice sensor) {
		super(deviceNumber);
		lastMode = ControlMode.PercentOutput;
		talonName = name;
		isError = false;
		//Does PIDidx need to change?
		errorCheck(super.configSelectedFeedbackSensor(sensor, 0, 10));
	}
	
	private boolean isChanged(double value, ControlMode mode) {
		if (value != lastValue || mode != lastMode) {
			//Common.debug(name+"changed = true");
			return true;
		}
		else {
			//Common.debug(name+"changed = false");
			return false;
		}
	}
	
	private void errorCheck(ErrorCode error) {
		if (error != ErrorCode.OK) {
			isError = true;
			Common.debug(name+ ": " + error.toString());
		} else {
			isError = false;
		}
	}
	
	public void handle() {
		if (isError) {
			neutralOutput();
			Common.debug(name+" in error state");
		}
	}
	
	/**
	 * 
	 * @param input - Percent from 1.0 to -1.0 to run the motor at 
	 */
	public void setPercent(double input) {
		if (isChanged(input, ControlMode.PercentOutput)) {
			lastValue = input;
			lastMode = ControlMode.PercentOutput;
			Common.debug("name"+input);
			super.set(ControlMode.PercentOutput, input);
		}
	}
	
	public void setCurrent(double input) {
		if (isChanged(input, ControlMode.Current)) {
			lastValue = input;
			lastMode = ControlMode.Current;
			super.set(ControlMode.Current, input);
		}
	}
	
	public void setVelocity(double input) {
		if (isChanged(input, ControlMode.Velocity)) {
			lastValue = input;
			lastMode = ControlMode.Velocity;
			super.set(ControlMode.Velocity, input);
		}
	}
	
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
	 * @param percent
	 */
	public void deadband(double percent) {
		errorCheck(super.configNeutralDeadband(percent, 10));
	}
	
	public void configVoltageComp(double voltage) {
		errorCheck(super.configVoltageCompSaturation(voltage, 10));
	}
	
	public void configVoltageMeasurementFilter(int samples) {
		errorCheck(super.configVoltageMeasurementFilter(samples, 10));
	}
	//No remote feed back device?
	
	public void configFeedbackCoefficient(double coefficient) {
		errorCheck(super.configSelectedFeedbackCoefficient(coefficient, 0, 10));
	}
	
	public void configSensorTerm(SensorTerm sensorTerm, FeedbackDevice device) {
		errorCheck(super.configSensorTerm(sensorTerm, device, 10));
	}
	
	//also sets
	public void configControlFramePeriod(ControlFrame frame, int periodMs) {
		errorCheck(super.setControlFramePeriod(frame, periodMs));
	}
	
	//also sets
	public void configControlFramePeriod(int frame, int periodMs) {
		errorCheck(super.setControlFramePeriod(frame, periodMs));
	}
	
	public void configP(double p) {
		//set timeout to 0 and defaults to one slot
		errorCheck(super.config_kP(0, p, 0));
	}
	
	public void configF(double f) {
		//set timeout to 0 and defaults to one slot
		errorCheck(super.config_kF(0, f, 0));
	}
	
	public void configI(double i) {
		//set timeout to 0 and defaults to one slot
		errorCheck(super.config_kP(0, i, 0));
	}
	
	public void configD(double d) {
		//set timeout to 0 and defaults to one slot
		errorCheck(super.config_kP(0, d, 0));
	}
	
	public double getParam(ParamEnum enu) {
		//ordinal appears to be 0
		return super.configGetParameter(enu, 0, 0);
	}
	
	public double getError() {
		return super.getClosedLoopError(0);
	}
	
	public double getTarget() {
		return super.getClosedLoopTarget(0);
	}
	
	public double getPosition() {
		return super.getSelectedSensorPosition(0);
	}
	
	public int getVelocity() {
		return super.getSelectedSensorVelocity(0);
	}
	
	//start on set statusframe
}
