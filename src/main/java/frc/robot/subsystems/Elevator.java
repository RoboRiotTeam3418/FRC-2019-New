package frc.robot.subsystems;

import frc.robot.Setup;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


/*------------------------------------------------- Elevator -----
  |  Name: Elevator.java
  |
  |  Purpose:  This subsystem controls the Elevator motors of the robot.
  |
  |			   The Elevator has a limit swtich on the botton and a prox at the top.
  | 		   When these are trigged it stops the Elevator from moving in the direction that is
  |			   triggered.  
  |
  |            The subsystem also has the height of the elevator using a LIDAR Lite 3 Laser. 
  |			   On the robot there is a laser mounted to the elevator that moves up and down 
  |			   with the elevator. 
  |
  |  Varibles:
  | 
  |  Elevator Postitions: These heights are compared to to the laser for auto height adjustment.
  |						  There is also a slowdown point that will slowdown the elevator when it 
  |						  reaches that point. This keeps the tension on the rope and protects the robot.
  |                   
  |
  |  Methods: 
  |
  |   setElevatorSpeed: This sets the speed of the spool motors with a given speed.
  |						This also has limits built into it to prevent the elevator from destroying itself.
  |						 
  |   ElevatorDistanceCalulator: This returns the distance to travel given the parameters. 
  |
  |   setElevatorPosition: This uses the ElevatorDistanceCalulator to determine how far to move and moves the elevator to taht location. 
  |							There are low and high distances used to speed up the postition settings by allowing slight error.
  |							
  |   stop: This stops the Subsystem
  |
  |  Returns:
  |
  |  This returns the Elevator Laser Distance to the Smartdashboard.
  |  
  |  
  *-------------------------------------------------------------------*/

public class Elevator extends Subsystem {

	static Elevator mInstance = new Elevator();
	public DigitalInput mElevatorTopProxHardware;
	public DigitalInput mElevatorBottomProxHardware;
	LIDAR mElevatorLaser;
	Setup mSetup = new Setup();

    //Elevator Positions 
    double elevatorHatchHighPosition = 60;
	double elevatorHatchMiddlePosition = 40;
	double elevatorHatchLowPosition = 10;

	double elevatorCargoHighPosition = 0;
	double elevatorCargoMiddlePosition = 65;
	double elevatorCargoLowPosition = 65;

	double ElevatorSlowdownPoint = 15;

	
	TalonSRX mSpool = new TalonSRX(Setup.kSpoolId);
	VictorSPX mSpool1 = new VictorSPX(Setup.kSpoolId1);
    
    public Elevator() 
{
    mElevatorBottomProxHardware = new DigitalInput(Setup.kElevatorBottomProx);
	mElevatorTopProxHardware = new DigitalInput(Setup.kElevatorTopProx);
	mElevatorLaser = new LIDAR(new DigitalInput(Setup.kElevatorLaser));

	System.out.println("Elevator Done Initializing.");
}

    public static Elevator getInstance() {
        return mInstance;
    }

    public void setElevatorSpeedAnalog(double speed)

	 {
		 if((mElevatorBottomProxHardware.get() == false) && (mElevatorTopProxHardware.get() == true))
		 {
			
			if((mElevatorLaser.getDistance()<=ElevatorSlowdownPoint) && (mSetup.getSecondaryElevatorAnalog() < 0 ))
			{
				mSpool.set(ControlMode.PercentOutput,speed*.25);
				mSpool1.set(ControlMode.PercentOutput,speed*.25);
			}
			else
			{
				mSpool.set(ControlMode.PercentOutput,speed);
				mSpool1.set(ControlMode.PercentOutput,speed);
			}
		 }
		 else if((mElevatorBottomProxHardware.get() == true) && (mSetup.getSecondaryElevatorAnalog() > 0))
		 {
			mSpool.set(ControlMode.PercentOutput,speed);
			mSpool1.set(ControlMode.PercentOutput,speed);
		 }
		 else if((mElevatorTopProxHardware.get() == false) && (mSetup.getSecondaryElevatorAnalog() < 0))
		 {
			mSpool.set(ControlMode.PercentOutput,speed);
			mSpool1.set(ControlMode.PercentOutput,speed);
		 }
		 else 
		 {
			mSpool.set(ControlMode.PercentOutput,0);
			mSpool1.set(ControlMode.PercentOutput,0);
		 }
     	
	 }
	
	 public void setElevatorSpeed(double speed)

	 {

		 if((mElevatorBottomProxHardware.get() == false) && (mElevatorTopProxHardware.get() == true))
		 {
			
			if((mElevatorLaser.getDistance()<=ElevatorSlowdownPoint) && (speed < 0 ))
			{
				mSpool.set(ControlMode.PercentOutput,speed*.25);
				mSpool1.set(ControlMode.PercentOutput,speed*.25);
			}
			else
			{
				mSpool.set(ControlMode.PercentOutput,speed);
				mSpool1.set(ControlMode.PercentOutput,speed);
			}
		 }
		 else if((mElevatorBottomProxHardware.get() == true) && (speed > 0))
		 {
			mSpool.set(ControlMode.PercentOutput,speed);
			mSpool1.set(ControlMode.PercentOutput,speed);
		 }
		 else if((mElevatorTopProxHardware.get() == false) && (speed < 0))
		 {
			mSpool.set(ControlMode.PercentOutput,speed);
			mSpool1.set(ControlMode.PercentOutput,speed);
		 }
		 else 
		 {
			mSpool.set(ControlMode.PercentOutput,0);
			mSpool1.set(ControlMode.PercentOutput,0);
		 }
     	
	 }
	 
	//This will calulate the distance needed to travel making the Set Position Smaller
	 public double ElevatorDistanceCalulator(String type, String postition)
	 {
		if (postition == "HIGH")
		{
			if (type == "HATCH")
			{
				return elevatorHatchHighPosition;
			}
			if (type == "CARGO")
			{
				return elevatorCargoHighPosition;
			}

		}
		else if (postition == "MIDDLE")
		{
			if (type == "HATCH")
			{
				return elevatorHatchMiddlePosition;
			}
			if (type == "CARGO")
			{
				return elevatorCargoMiddlePosition;
			}
		}
		else if (postition == "LOW")
		{
			if (type == "HATCH")
			{
				return elevatorHatchLowPosition;
			}
			if (type == "CARGO")
			{
				return elevatorCargoLowPosition;
			}
		}
		
		return 0;
		
	 }

//This Varible is used in the Auto Actions To Stop it from moving
public boolean FinishedMoving;

public double ElevatorDistanceToTravel = 0;
public double ElevatorDistanceToTravelHigh = 0;
public double ElevatorDistanceToTravelLow = 0;
String printtype = "";
String printposition= "";
public boolean heartbeat = false;
public int heartbeatcount = 0;

    public void setElevatorPosition(String type, String position)
	{
		double ElevatorDistance = mElevatorLaser.getDistance();
		ElevatorDistanceToTravel = ElevatorDistanceCalulator(type, position);
		ElevatorDistanceToTravelLow = ElevatorDistanceToTravel - 2;
		ElevatorDistanceToTravelHigh = ElevatorDistanceToTravel + 2;  
		printtype = type;
		printposition = position;
		FinishedMoving = false;
		heartbeatcount+=1;
		if (heartbeatcount>100)
		{
			heartbeat = !heartbeat;
			heartbeatcount=0;
		}
		

		if ((ElevatorDistance <= ElevatorDistanceToTravelHigh) && (ElevatorDistance >= ElevatorDistanceToTravelLow))
		{
			setElevatorSpeed(0);
			FinishedMoving = true;


		}
		else if (ElevatorDistance < ElevatorDistanceToTravel)
		{
			//<Move Up
			setElevatorSpeed(1);
		}

		else if (ElevatorDistance > ElevatorDistanceToTravel)
		{
			//Move Down
			setElevatorSpeed(-1);
		}
		else
		{
			setElevatorSpeed(0);
			FinishedMoving = true;
		}

	}

    @Override
	public void stop()
	{
		setElevatorSpeed(0);
		System.out.println("Stopping Elevator");

    }
    

	@Override
	public void updateSubsystem(){
		
		//Update Laser
		double ElevatorDistance = mElevatorLaser.getDistance();

		if  (mSetup.AutoRunning == true)
		{
			setElevatorPosition(printtype, printposition);
		}
		
		outputToSmartDashboard();
	}
	
	@Override
	public void outputToSmartDashboard() {
		
		SmartDashboard.putNumber("Elevator Laser Distance", mElevatorLaser.getDistance());
		// SmartDashboard.putNumber("Elevator Laser Distance To Travel", ElevatorDistanceToTravel);
		// SmartDashboard.putNumber("Elevator Laser Distance To Travel High", ElevatorDistanceToTravelHigh);
		// SmartDashboard.putNumber("Elevator Laser Distance To Travel Low", ElevatorDistanceToTravelLow);
		
		// SmartDashboard.putString("Elevator Type", printtype);
		// SmartDashboard.putString("Elevator Position", printposition);

		// SmartDashboard.putNumber("Elevator Laser Distance", mElevatorLaser.getDistance());
		// SmartDashboard.putNumber("Elevator Command Speed", mSpool.getMotorOutputPercent());
		// SmartDashboard.putBoolean("Finished Moving", FinishedMoving);
		// SmartDashboard.putBoolean("Elevator Heartbeat", heartbeat);
		// SmartDashboard.putNumber("Heartbeat Count", heartbeatcount);

	}

}