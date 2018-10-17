package org.usfirst.frc.team4564.robot;

import com.ctre.phoenix.ParamEnum;

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
	public OCTalonPID(OCTalon talon, String name, double p, double i, double d, double f, boolean visible) {
		this.talon = talon;
		this.visible = visible;
		this.name = name;
		talon.configP(p);
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
	
	public void update() {
		if (visible) {
			Common.dashNum(name + " target", talon.getTarget());
			Common.dashNum(name + " error", talon.getError());
			
			//update params
			double newp = Common.getNum(name+ " P");
			if (this.p != newp) {
				p = newp;
				talon.configP(p);
			}
			double newi = Common.getNum(name+ " I");
			if (this.i != newi) {
				i = newi;
				talon.configI(i);
			}
			double newd = Common.getNum(name+ " D");
			if (this.d != newd) {
				d = newd;
				talon.configD(d);
			}
			if (Double.valueOf(f) != null) {
				double newf = Common.getNum(name+ " F");
				//check for a existing f
				if (this.f != newf) {
					f = newf;
					talon.configF(f);
				}
			}
		}
	}
}
