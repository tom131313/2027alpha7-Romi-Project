package first.robot.OpModes;

import org.wpilib.command3.Scheduler;
import org.wpilib.framework.OpModeRobot;
import org.wpilib.opmode.OpMode;
import org.wpilib.opmode.Utility;
import org.wpilib.telemetry.Telemetry;

import first.robot.Robot;

@Utility(name = "My Utility Controller", group = "Group 1")
public class MyUtilityController implements OpMode {
    
    @SuppressWarnings("unused")
    private final Robot robot;
    @SuppressWarnings("unused")
    private Scheduler scheduler = Scheduler.getDefault();

    public MyUtilityController(Robot robot)
    {
        this.robot = robot;
    }

    /**
   * This function is called periodically while the opmode is selected and the robot is disabled.
   * Code that should only run once when the opmode is selected should go in the opmode constructor.
   */
  public void disabledPeriodic() {}

  /** Called once when this opmode transitions to enabled. */
  public void start() {
    System.out.println("MyUtilityController start");
    Telemetry.log("Mode", "MyUtilityController");
  }

  /**
   * This function is called periodically while the opmode is enabled at the rate returned by {@link
   * OpModeRobot#getPeriod()}.
   * 
   * This method runs periodically, using the same period as the Robot instance.
   *
   * Additional periodic methods may be configured with addPeriodic(),
   * which can have periods that differ from the main Robot instance.
   */
  public void periodic() {}

  /**
   * This function is called asynchronously when the robot disables or switches opmodes while this
   * opmode is enabled. Implementations should stop blocking work promptly.
   */
  public void end() {
    System.out.println("MyUtilityController end");
  }

  /**
   * This function is called when the opmode is no longer selected on the DS or after an enabled run
   * ends. The object will not be reused after this is called.
   */
  public void close() {
    System.out.println("MyUtilityController close");
  }
}
