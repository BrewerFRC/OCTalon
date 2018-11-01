package org.usfirst.frc.team4564.robot;

/**
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
	private double f;
	
	
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
		//Is this okay?
		setP(p);
		this.p = p;
		talon.configI(i);
		this.i = i;
		talon.configD(d);
		this.d = d;
		talon.configF(f);
		this.f = f;
		if (visible) {
			Common.dashNum(name + " P", p);
			Common.dashNum(name + " I", i);
			Common.dashNum(name + " D", d);
			Common.dashNum(name + " F", f);
		}
	}
	
	/**
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
		talon.configP(p);
		this.p = p;
		talon.configI(i);
		this.i = i;
		talon.configD(d);
		this.d = d;
		if (visible) {
			Common.dashNum(name + " P", p);
			Common.dashNum(name + " I", i);
			Common.dashNum(name + " D", d);
		}
	}
	
	//Getter Functions
	
	
	
	//Setter Functions
	/**
	 * Sets the proportional term only if stored value is not equal to new value.
	 * 
	 * @param newP The new proportional term to be set.
	 */
	public void setP(double newP) {
		if (this.p != newP) {
			p = newP;
			talon.configP(p);
		}
	}
	
	/**
	 * Sets the integral term only if stored value is not equal to new value.
	 * 
	 * @param newI The new integral term to be set.
	 */
	public void setI(double newI) {
		if (this.i != newI) {
			i = newI;
			talon.configI(i);
		}
	}
	
	/**
	 * Sets the derivative term only if stored value is not equal to new value.
	 * 
	 * @param newD The new derivative term to be set.
	 */
	public void setD(double newD) {
		if (this.d != newD) {
			d = newD;
			talon.configD(d);
		}
	}
	
	/**
	 * Sets the feed forward term only if stored value is not equal to new value.
	 * 
	 * @param newF The new feed forward term to be set.
	 */
	public void setF(double newF) {
		if (this.f != newF) {
			f = newF;
			talon.configF(f);
		}
	}
	
	
	
	//Utility Functions
	/**
	 * 
	 */
	public void update() {
		if (visible) {
			Common.dashNum(name + " target", talon.getTarget());
			Common.dashNum(name + " error", talon.getError());
			
			//update params
			double newp = Common.getNum(name+ " P");
			setP(newp);
			double newi = Common.getNum(name+ " I");
			setI(newi);
			double newd = Common.getNum(name+ " D");
			setD(newd);
			if (Double.valueOf(f) != null) {
				double newf = Common.getNum(name+ " F");
				setF(newf);
			}
		}
	}
}
