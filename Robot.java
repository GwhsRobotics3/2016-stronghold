package org.usfirst.frc.team5507.robot;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Timer;


/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {


	// RobotDrive myRobot;
	Joystick stick;
	// int autoLoopCounter;


	// camera stuff
	SmartDashboard x = new SmartDashboard();
	CameraServer server;


	// final int frontLeftChannel= 2;
	// final int rearLeftChannel= 3;
	final int leftChannel = 0;
	final int rightChannel = 1;


	private SpeedController motor1;
	private SpeedController motor2;


	// private SpeedController frontLeft= new Talon(frontLeftChannel);
	private SpeedController right = new Talon(rightChannel);
	private SpeedController left = new Talon(leftChannel);
	// private SpeedController rearRight= new Talon(rearRightChannel);


	private final double INTAKE_SPEED = 0.75;
	private final double SHOOT_SPEED = 1;


	private double robotSpeed = 1;
	// timer
	Timer autoTimer = new Timer();
	int autoStrat;
	int drivemode = 4;// 0 for left stick turning, 4 for right


	// constants for timer to travel certain* amounts of distance
	// acquired through extensive testing *TO BE DONE
	final double INCH = .25 / 12;
	final int FOOT = 12;
	final double FIFTEEN_DEGREES = .077;
	final int FORWARD = 1;
	final int CLOCKWISE = 2;
	final int BACKWARD = 3;
	final int COUNTER_CLOCKWISE = 4; // GachiGASM
	final int SHOOT = 6;
	final int INTAKE = 5;
	final int MOAT = 9 * FOOT;


	public void robotInit() {
		// myRobot = new RobotDrive(0,1);
		stick = new Joystick(0);


		// camera
		server = CameraServer.getInstance();
		server.setQuality(1000);
		server.setSize(900);
		server.startAutomaticCapture("cam0");// try to use cam name(String).


		// INTAKE motors
		motor1 = new Spark(2);// initialize the motor as a Spark on channel 0
		motor2 = new Spark(3);
	}


	// drive method, y is vertical(FORWARD) , z is rotation(turning)
	public void drive(double y, double z) {


		/*
		 * frontLeft.set((y+z)); frontRight.set((-y+z)); rearLeft.set((y + z));
		 * rearRight.set((-y+z));
		 */
		double left1 = 0;
		double right1 = 0;
		if (y > 0.0) {
			if (z > 0.0) 
			{


				left1 = y - z;
				right1 = Math.max(y, z);


			} else {


				left1 = Math.max(y, -z);
				right1 = y + z;
			}
		} else {
			if (z > 0.0) {


				left1 = -Math.max(-y, z);
				right1 = y + z;
			} else {


				left1 = y - z;
				right1 = -Math.max(-y, -z);
			}
		}
		// Make sure it does not power more than 1
		left1 = left1 * robotSpeed;
		right1 = right1 * robotSpeed;


		left.set(left1);// invert left motor forward(0.9,-0.9) backward (-0.9,0.9)
		right.set(-right1 * 0.9);
	}


	public void autonomousInit() {
		autoTimer.reset();
		autoStrat = 7;
	}


	public void autonomousPeriodic() {
		switch (autoStrat) {
		case 1: // go straight 11.4ft *to test*
			basicMovements(FORWARD, 9 * FOOT);
			autoStrat = 0;
			break;


		case 2: // CLOCKWISE test
			basicMovements(CLOCKWISE, 12);
			autoStrat = 0;
			break;
		case 3: // COUNTER_CLOCKWISE test
			basicMovements(COUNTER_CLOCKWISE, 1);
			autoStrat = 0;
			break;
		case 4: // BACKWARDs test
			basicMovements(BACKWARD, 1);
			autoStrat = 0;
		case 5: // turn left 92,go straight 8.2ft, turn left 92, go straight
				// 7.2, turn right 88,
			// go stright 8.2 ft/ turn right 87, go straight 13.25 feet. turn
			// right 63, go straight 7ft
			basicMovements(FORWARD, 11);
			basicMovements(COUNTER_CLOCKWISE, 6);
			basicMovements(FORWARD, 8);
			basicMovements(COUNTER_CLOCKWISE, 6);
			basicMovements(FORWARD, 7);
			basicMovements(CLOCKWISE, 6);
			basicMovements(FORWARD, 8);
			basicMovements(CLOCKWISE, 6);
			basicMovements(FORWARD, 13);
			basicMovements(CLOCKWISE, 4);
			basicMovements(FORWARD, 7);
			autoStrat = 0;
			break;
		case 6:
			basicMovements(FORWARD, MOAT + 5 * FOOT);
			basicMovements(CLOCKWISE, 12);
			basicMovements(FORWARD, MOAT + 2 * FOOT);
			basicMovements(CLOCKWISE, 12);
			basicMovements(FORWARD, MOAT + 2 * FOOT);
			autoStrat = 0;
			break;
		case 7:
			basicMovements(FORWARD, MOAT + 5 * FOOT);
			basicMovements(INTAKE, 1);
			basicMovements(CLOCKWISE, 12);
			basicMovements(FORWARD, MOAT + 2 * FOOT);
			basicMovements(INTAKE, 1);
			basicMovements(CLOCKWISE, 12);
			basicMovements(FORWARD, MOAT + 2 * FOOT);
			basicMovements(INTAKE, 1);
			autoStrat = 0;
			break;


		}
	}


	private void basicMovements(int strats, int repeat) {
		switch (strats) {
		case 1: // FORWARD
			for (int i = 0; i < repeat; i++) {
				autoTimer.start();
				while (autoTimer.get() <= INCH) {
					drive(1, 0);
				}
				drive(0, 0);
				autoTimer.stop();
				autoTimer.reset();
			}
			break;


		case 2: // rotate CLOCKWISE
			for (int i = 0; i < repeat; i++) {
				autoTimer.start();
				while (autoTimer.get() <= FIFTEEN_DEGREES) {
					drive(0, 1);
				}
				drive(0, 0);
				autoTimer.stop();
				autoTimer.reset();
			}
			break;


		case 3: // BACKWARDs
			for (int i = 0; i < repeat; i++) {
				autoTimer.start();
				while (autoTimer.get() <= INCH) {
					drive(-1, 0);
				}
				drive(0, 0);
				autoTimer.stop();
				autoTimer.reset();
			}
			break;


		case 4: // rotate counter CLOCKWISE
			for (int i = 0; i < repeat; i++) {
				autoTimer.start();
				while (autoTimer.get() <= FIFTEEN_DEGREES) {
					drive(0, -1);
				}
				drive(0, 0);
				autoTimer.stop();
				autoTimer.reset();
			}
			break;


		case 5: // SHOOT
			autoTimer.start();
			while (autoTimer.get() <= repeat) {
				motor1.set(-SHOOT_SPEED);
				motor2.set(SHOOT_SPEED);
			}
			motor1.set(0);
			motor2.set(0);
			autoTimer.stop();
			autoTimer.reset();
			break;


		case 6: // INTAKE
			autoTimer.start();
			while (autoTimer.get() * 1.0 <= repeat / 2.0) {
				motor1.set(INTAKE_SPEED);
				motor2.set(-INTAKE_SPEED);
			}
			motor1.set(0);
			motor2.set(0);
			autoTimer.stop();
			autoTimer.reset();
			break;
		}
	}


	public void teleopInit() {


	}


	public void teleopPeriodic() {
		// SHOOT
		if (stick.getRawAxis(3) > 0) {// Set the motor's output.
										// This takes a number from -1 (100%
										// speed in reverse) to +1 (100% speed
										// going FORWARD)
			motor1.set(SHOOT_SPEED);
			motor2.set(-SHOOT_SPEED);
		}
		// INTAKE
		else if (stick.getRawAxis(2) > 0) // left trigger
		{
			motor1.set(-INTAKE_SPEED);
			motor2.set(INTAKE_SPEED);
		} else if (stick.getRawButton(5)) { // left bumper
			if (drivemode == 0)
				drivemode = 4; // right stick turn
			else
				drivemode = 0; // left stick turn
		} else {
			motor1.set(0);
			motor2.set(0);
			drive(0, 0);


		}
		drive(-stick.getRawAxis(1), -stick.getRawAxis(drivemode));
	}


	public void testPeriodic() {
		LiveWindow.run();
	}


}




