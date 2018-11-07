package org.usfirst.frc.team4564.robot;

import com.ctre.phoenix.ParamEnum;

/**
 * 
 * 
 * 
 * @author Brewer FIRST Robotics Team 4564
 * @author Brent Roberts
 */
public class OCTalonPID {
	//PID is linked to motor inversion as far as I can tell
	
	private OCTalon talon;
	private boolean visible;
	private String name;
	private double p;
	private double i;
	private double d;
	private double f = 0.0;
	
	
	//Talk about types of pid?
	/**
	 * 
	 * @param talon
	 * @param name
	 * @param p
	 * @param i
	 * @param d
	 * @param f
	 * @param visible
	 */
	public OCTalonPID(OCTalon talon, String name, double p, double i, double d, double f, boolean visible) {
		this.talon = talon;
		this.visible = visible;
		this.name = name;
		this.p = this.talon.getParam(ParamEnum.eProfileParamSlot_P);
		this.setP(p);
		this.i = this.talon.getParam(ParamEnum.eProfileParamSlot_I);
		this.setI(i);
		this.d = this.talon.getParam(ParamEnum.eProfileParamSlot_D);
		this.setD(d);
		this.f = this.talon.getParam(ParamEnum.eProfileParamSlot_F);
		this.setF(f);
		if (visible) {
			Common.dashNum(getName() + " P", getP());
			Common.dashNum(getName() + " I", getI());
			Common.dashNum(getName() + " D", getD());
			Common.dashNum(getName() + " F", getF());
		}
	}
	
	/**
	 * 
	 * Sets the F to 0.0.
	 * 
	 * @param talon
	 * @param name
	 * @param p
	 * @param i
	 * @param d
	 * @param visible
	 */
	public OCTalonPID(OCTalon talon, String name, double p, double i, double d, boolean visible) {
		this.talon = talon;
		this.visible = visible;
		this.name = name;
		this.p = this.talon.getParam(ParamEnum.eProfileParamSlot_P);
		this.setP(p);
		this.i = this.talon.getParam(ParamEnum.eProfileParamSlot_I);
		this.setI(i);
		this.d = this.talon.getParam(ParamEnum.eProfileParamSlot_D);
		this.setD(d);
		this.setF(this.f);
		if (visible) {
			Common.dashNum(getName() + " P", getP());
			Common.dashNum(getName() + " I", getI());
			Common.dashNum(getName() + " D", getD());
		}
	}
	
	//Getter Functions
	/**
	 * The error of the PID.
	 * 
	 * @return The PID error.
	 */
	public double getError() {
		return talon.getError();
	}
	
	/**
	 * The name of the PID.
	 * 
	 * @return name of PID.
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Gets the P value
	 * 
	 * @return The current P value.
	 */
	public double getP() {
		return this.p;
	}
	
	/**
	 * Gets the I value
	 * 
	 * @return The current I value.
	 */
	public double getI() {
		return this.i;
	}
	
	/**
	 * Gets the D value
	 * 
	 * @return The current D value.
	 */
	public double getD() {
		return this.d;
	}
	
	/**
	 * Gets the F value
	 * 
	 * @return The current F value. 0.0 is the default.
	 */
	public double getF() {
		return this.f;
	}
	
	//Setter Functions
	/**
	 * Sets the proportional term only if stored value is not equal to new value.
	 * 
	 * @param newP The new proportional term to be set.
	 */
	public void setP(double newP) {
		if (this.getP() != newP) {
			this.p = newP;
			talon.configP(p);
		}
	}
	
	/**
	 * Sets the integral term only if stored value is not equal to new value.
	 * 
	 * @param newI The new integral term to be set.
	 */
	public void setI(double newI) {
		if (this.getI() != newI) {
			this.i = newI;
			talon.configI(i);
		}
	}
	
	/**
	 * Sets the derivative term only if stored value is not equal to new value.
	 * 
	 * @param newD The new derivative term to be set.
	 */
	public void setD(double newD) {
		if (this.getD() != newD) {
			this.d = newD;
			talon.configD(d);
		}
	}
	
	/**
	 * Sets the feed forward term only if stored value is not equal to new value.
	 * 
	 * @param newF The new feed forward term to be set.
	 */
	public void setF(double newF) {
		if (this.getF() != newF) {
			this.f = newF;
			talon.configF(f);
		}
	}
	
	
	
	//Utility Functions
	/**
	 * 
	 */
	public void update() {
		if (visible) {
			Common.dashNum(getName() + " target", talon.getTarget());
			Common.dashNum(getName() + " error", talon.getError());
			
			//update params
			double newp = Common.getNum(getName()+ " P");
			setP(newp);
			double newi = Common.getNum(getName()+ " I");
			setI(newi);
			double newd = Common.getNum(getName()+ " D");
			setD(newd);
			if (Double.valueOf(getF()) != null) {
				double newf = Common.getNum(getName()+ " F");
				setF(newf);
			}
		}
	}
	
	public void postCoeffcients() {
		Common.dashNum(getName() + " P", getP());
		Common.dashNum(getName() + " I", getI());
		Common.dashNum(getName() + " D", getD());
		if (Double.valueOf(getF()) != 0.0) {
			Common.dashNum(getName() + " F", getF());
		}
	}
}
