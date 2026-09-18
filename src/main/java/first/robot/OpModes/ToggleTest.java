package first.robot.OpModes;

import static org.wpilib.units.Units.Seconds;

import java.lang.invoke.MethodHandles;

import org.wpilib.command3.Command;
import org.wpilib.command3.Mechanism;
import org.wpilib.command3.Scheduler;
import org.wpilib.command3.Trigger;
import org.wpilib.framework.OpModeRobot;
import org.wpilib.opmode.OpMode;
import org.wpilib.opmode.Utility;
import org.wpilib.telemetry.Telemetry;

import first.robot.Robot;

@Utility(name = "Toggle Test")
public class ToggleTest implements OpMode {
    private static final String m_fullClassName = MethodHandles.lookup().lookupClass().getCanonicalName();
    static
    {
        System.out.println("Loading: " + m_fullClassName);
    }
    
    @SuppressWarnings("unused")
    private final Robot robot;
    Scheduler scheduler = Scheduler.getDefault();

    public ToggleTest(Robot robot)
    {
      this.robot = robot;

      Trigger LBTrigger = robot.getRobotContainer().getXbox().leftBumper();

      LBTrigger.toggleOnTrue(
        Command
        .noRequirements(coroutine ->
            {
              class A implements Mechanism {
                A()
                {
                  setDefaultCommand(run(coroutine ->
                    {
                        int counter = 0;
                        while (true) {
                            if (counter++%50 == 0) System.out.println("A default " + counter); 
                            coroutine.yield();                    
                        }
                    }
                  ).named("A [default]"));
                } // end of class A constructor method

                Command Acommand =
                  run(coroutine ->
                      {
                          System.out.println("executing A command for 3 seconds");
                          coroutine.wait(Seconds.of(3.));
                          System.out.println("completed A command");
                      }).named("Acommand");
              } // end of class A

              A a = new A();
              coroutine.fork(a.Acommand);
              coroutine.await(a.Acommand);
              coroutine.wait(Seconds.of(6.));
              System.out.println("testing with Acommand completed and locally scoped default command ends");
            }
          )
        .whenCanceled(()->System.out.println("Acommand canceled"))
        .named("testing")
      ); // toggle on true
    }

    /**
   * This function is called periodically while the opmode is selected and the robot is disabled.
   * Code that should only run once when the opmode is selected should go in the opmode constructor.
   */
  public void disabledPeriodic() {}

  /** Called once when this opmode transitions to enabled. */
  public void start() {
    System.out.println("ToggleTest start");
    Telemetry.log("Mode", "ToggleTest");
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
  public void periodic() {System.out.println("running " + scheduler.getRunningCommands());}

  /**
   * This function is called asynchronously when the robot disables or switches opmodes while this
   * opmode is enabled. Implementations should stop blocking work promptly.
   */
  public void end() {
    System.out.println("ToggleTest end");
  }

  /**
   * This function is called when the opmode is no longer selected on the DS or after an enabled run
   * ends. The object will not be reused after this is called.
   */
  public void close() {
    System.out.println("ToggleTest close");
  }
}
