package frc.robot.subsystems;
import frc.robot.Setup;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Relay;

/*------------------------------------------------- Intake -----
  |  Name: Intake.java
  |
  |  Purpose:  This subsystem controls both intaking and outtaking gamepieces along with 
  | 		   controling the rotary arm of the intake.
  |             
  |
  |  Varibles:
  | 
  |  Intake States ENUM: For the rotary arm, cargo intake, and hatch intake there are enum states.  
  |                      When the state is set the Intake will preform the actions associated with it.
  |						 These states are controled by the secondary driver through robot.java.
  |						
  |  Methods: 
  |
  |   intakeCargo/Hatch: Intakeing the cargo makes the intake motors turn in.
  |						 Intaking Hatches enables Vacuum 	
  |          
  |   outtakeCargo/Hatch: Outtaking the cargo makes teh intake motors turn out.
  |						  Outtaking Hatches enables a relay to realse the hatch form the vacuum.
  |						  (Note: There are seprate controls to stop the vacuum and to set the relay)
  |         
  |   Stop: This stops all subsystem motors
  |         
  |
  |  Returns:
  |
  |  This returns the Intake State ENUMs to the Smartdashboard.
  |  
  *-------------------------------------------------------------------*/

public class Intake extends Subsystem
{

    static Intake mInstance = new Intake();

    public static Intake getInstance() {
    	return mInstance;
    }

	Setup mSetup;
	

	private VictorSPX mIntake;
	private TalonSRX mIntakeRotary;
	private VictorSPX mMrHuck;
	private VictorSPX mMrHuckJr;

	public DigitalInput mIntakeCargoLimit;
	public DigitalInput mIntakeHatchLimit;
	
	public Relay mstopSuckingRelay;

public Intake() {
		
		mSetup = new Setup();

		mIntake= new VictorSPX(Setup.kIntakeId);
		mIntake.setInverted(false);

		mMrHuck = new VictorSPX(Setup.kMrHuckId);
		mIntake.setInverted(false);

		mMrHuckJr = new VictorSPX(Setup.kMrHuckJrId);
		mIntake.setInverted(false);

		mIntake.set(ControlMode.PercentOutput, 0);
		mIntakeRotary = new TalonSRX(Setup.kIntakeRotaryId);

		mIntakeCargoLimit = new DigitalInput(Setup.kIntakeCargoLimit);
		mIntakeHatchLimit = new DigitalInput(Setup.kIntakeHatchLimit);

		mstopSuckingRelay = new Relay(0);
		mIntakeHatchState=IntakeHatchState.SUCK;
		
		System.out.println("Intake Done Initializing.");
    }
//-------------------------------------------------------------------------------Intake Cargo----------------------------------------------------------------------------------//

    public enum IntakeCargoState {
    	INTAKE,
    	OUTTAKE,
    	STOP;
    }

	 private IntakeCargoState mIntakeCargoState=IntakeCargoState.STOP;

	 public IntakeCargoState getIntakeCargoState() {
		return mIntakeCargoState;
	}
	 
	public void intakeCargo(){
		mIntakeCargoState = IntakeCargoState.INTAKE;
	}

	public void outtakeCargo(){
		mIntakeCargoState = IntakeCargoState.OUTTAKE;
	}

	public void stopCargoIntakeMotor(){
		mIntakeCargoState = IntakeCargoState.STOP;
	}

	public void setCargoIntakeSpeed(double speed) {
		mIntake.set(ControlMode.PercentOutput, speed);

	}

	//-------------------------------------------------------------------------------Intake Hatch----------------------------------------------------------------------------------//

	public enum IntakeHatchState {
    	SUCK,
    	STOP;
    }

	 public IntakeHatchState mIntakeHatchState=IntakeHatchState.STOP;
	
	public void IntakeHatch(){
		mIntakeHatchState = IntakeHatchState.SUCK;
	}

	public void stopSucking(){
		mIntakeHatchState = IntakeHatchState.STOP;
	}

	public void setIntakeHatchSpeed(double speed) {
		mMrHuck.set(ControlMode.PercentOutput, speed*.2);
		mMrHuckJr.set(ControlMode.PercentOutput, speed);
	}

	public IntakeHatchState getIntakeHatchState() {
		return mIntakeHatchState;
	}

//-------------------------------------------------------------------------------Rotary Intake Control ----------------------------------------------------------------------------------//

public enum IntakeRotaryState {
	CARGOStart,
	CARGOStop,
	HATCHStart,
	HATCHStop;
}

 private IntakeRotaryState mIntakeRotaryState=IntakeRotaryState.HATCHStart;

 public IntakeRotaryState getIntakeRotaryState() {
	return mIntakeRotaryState;
}

 public void SetIntakeRotaryCargoState(){
	 if (!(mIntakeRotaryState==IntakeRotaryState.CARGOStart || mIntakeRotaryState==IntakeRotaryState.CARGOStop)){
		mIntakeRotaryState = IntakeRotaryState.CARGOStart;
	 }
}

public void SetIntakeRotaryHatchState(){
	if (!(mIntakeRotaryState==IntakeRotaryState.HATCHStart || mIntakeRotaryState==IntakeRotaryState.HATCHStop)){
		mIntakeRotaryState = IntakeRotaryState.HATCHStart;
		//System.out.println("Go To Hatch");
	}
}

public void SetIntakeRotaryCargo(){
	
		//Move To Cargo Limit
		if (mIntakeCargoLimit.get() == true)
		{
			mIntakeRotary.set(ControlMode.PercentOutput, 1);
		}
		else
		{
		//Brake
		mIntakeRotaryState=IntakeRotaryState.CARGOStop;
		mIntakeRotary.set(ControlMode.PercentOutput, 0);
		}
			
}

public void SetIntakeRotaryHatch(){

	//Checks if it is right state
	if (mIntakeHatchLimit.get() == true)
	{	
		mIntakeRotary.set(ControlMode.PercentOutput, -1);
		//System.out.println("Going to Hatch");
	}
	else
	{
		//Brake
		mIntakeRotaryState=IntakeRotaryState.HATCHStop;
		//System.out.println("At Hatch");
		mIntakeRotary.set(ControlMode.PercentOutput, 0);
	}

}

public void setIntakeRotarySpeed()
{
    if ((mIntakeCargoLimit.get()) && (mSetup.getSecondaryIntakeRotaryAnalog() < -.2))
    {
        if (mIntakeCargoLimit.get()) 
        {
            mIntakeRotary.set(ControlMode.PercentOutput, mSetup.getSecondaryIntakeRotaryAnalog() * -1);

        }
        else 
        {
            mIntakeRotary.set(ControlMode.PercentOutput, 0);
            mIntakeRotaryState = IntakeRotaryState.CARGOStop;

        }

    }
    else if ((mIntakeHatchLimit.get()) && (mSetup.getSecondaryIntakeRotaryAnalog() > .2))
    {
        if (mIntakeHatchLimit.get()) 
        {
            mIntakeRotary.set(ControlMode.PercentOutput, mSetup.getSecondaryIntakeRotaryAnalog() * -1);

        }
        else 
        {
            mIntakeRotary.set(ControlMode.PercentOutput, 0);
            mIntakeRotaryState = IntakeRotaryState.HATCHStop;

		}
		
	}
	else {
		mIntakeRotary.set(ControlMode.PercentOutput, 0);
	}
}


//--------------------------------------------------------------------------------Important Robot Stuff ----------------------------------------------------------------------------------//
		
	@Override
	public void updateSubsystem()
	{

		if(mSetup.getVaccuumReleaseButton()){
            mstopSuckingRelay.set(Relay.Value.kForward);
            mIntakeHatchState = IntakeHatchState.STOP;
        }
        else{
            mstopSuckingRelay.set(Relay.Value.kOff);
        }

		switch(mIntakeCargoState) {

		case INTAKE:
			setCargoIntakeSpeed(-1);
			break;
		case OUTTAKE:
			setCargoIntakeSpeed(1);
			break;
		case STOP:
			setCargoIntakeSpeed(0);
			break;
		default:
			mIntakeCargoState = IntakeCargoState.STOP;
			break;
		}

		switch(mIntakeHatchState) {

			case SUCK:
				//mstopSuckingRelay.set(Relay.Value.kOff);
				setIntakeHatchSpeed(1.0);
				break;
			case STOP:
				//mstopSuckingRelay.set(Relay.Value.kForward);
				setIntakeHatchSpeed(0);
				break;
			default:
				mIntakeHatchState = IntakeHatchState.STOP;
				break;
			}

			switch(mIntakeRotaryState) {

			case CARGOStart:
				SetIntakeRotaryCargo();
				break;
			case HATCHStart:
				SetIntakeRotaryHatch();
				break;
			default:
			//mIntakeRotary.set(ControlMode.PercentOutput, 0);
			setIntakeRotarySpeed();
				break;
			}
			
		outputToSmartDashboard();
	}


	@Override
	public void outputToSmartDashboard() {
		SmartDashboard.putString("Intake_Hatch_State", mIntakeHatchState.toString());
		SmartDashboard.putString("Intake_Cargo_State", mIntakeCargoState.toString());
	}

	@Override
	public void stop(){
		mIntakeRotaryState = IntakeRotaryState.HATCHStop;
		//System.out.println("Stopping Intake");
	}

}