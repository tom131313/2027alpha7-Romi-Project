/**
 * This class runs the Autonomous Commands.
 * The operator chooses a command by one of two methods:
 * 
 *      choose a command based on the Autonomous OpMode selected
 *  OR
 *      choose a command from the Elastic Dashboard Autonomous selector
 * 
 * This class has both methods in it for illustrative purposes only. Pick a method to code and use
 * and don't confuse things by having both possibilities at once in the code.
 */
package first.robot.OpModes;

import java.lang.invoke.MethodHandles;

import org.wpilib.command3.Command;
import org.wpilib.command3.Scheduler;
import org.wpilib.driverstation.RobotState;
import org.wpilib.framework.OpModeRobot;
import org.wpilib.opmode.OpMode;
import org.wpilib.telemetry.Telemetry;
import org.wpilib.tunable.Selectable;
import org.wpilib.tunable.Tunables;

import first.robot.Robot;
import first.robot.commands.MyCommands;

/**
 * Determines which Autonomous command to use
 */
public class MyAutoController implements OpMode {
    private static final String m_fullClassName = MethodHandles.lookup().lookupClass().getCanonicalName();
    static
    {
        System.out.println("Loading: " + m_fullClassName);
    }

    @SuppressWarnings("unused")
    private Robot robot;
    private Scheduler scheduler = Scheduler.getDefault();
    private Command autonomousCommand;
    private final Selectable<Command> autonomousChooser = new Selectable<>();
    boolean useAutoSelector;

    public MyAutoController(Robot robot)
    {
        this.robot = robot;
        System.out.println("Constructing: " + RobotState.getOpMode() + ", " + RobotState.getRobotMode());
        selector();
        if (useAutoSelector) {
            createAutoSelector();
        }
    }

    private void selector()
    {
        useAutoSelector = false;
        autonomousCommand =
            switch (RobotState.getOpMode())
            {
                case "Auto Spin" -> MyCommands.autonomousDriveAndSpinCommand();
                case "Auto Drive 3 Sec" -> MyCommands.drive3SecondsCommand();
                default -> {
                            useAutoSelector = true;
                            yield null;
                           }
            };
    }

    /**
     * Create the auto command selector that can be used on Elastic dashboard
     * <p>This is one choice of where auto command selection is made. The other choice is pick the
     * desired OpMode that runs the auto commandsv3.
     * <p>It's a demo! Typically pick one way or the other and not both to implement.
     * 
     * Note that Elastic manages the selection well. If you want to enter a selection using other
     * tools such as OutlineViewer or the Simulation Network Tables View then the selection is made
     * on the Retained Values area only: Tunables Autonomous selected tune. BUT that entry doesn't
     * exist until Elastic publishes it. So use Elastic! (Maybe Glass?)
     * 
     * V
     * @param robotContainer
     */
    public void createAutoSelector()
    {
        // Add commands to the autonomous command chooser
        Tunables.remove("Autonomous"); // clean up NT path; doesn't change the selected value though
                                    // if previously set - must use Elastic to change the selection
        autonomousChooser.add("Auto Spin", MyCommands.autonomousDriveAndSpinCommand());
        autonomousChooser.add("Auto Drive 3 Sec", MyCommands.drive3SecondsCommand());
        autonomousChooser.addDefault("Do Nothing", Command.noRequirements(coroutine -> {}).named("do nothing auto"));
        // Put the chooser on the dashboard
        Tunables.publish("Autonomous", autonomousChooser);
    }

    /**
    * This function is called periodically while the opmode is selected and the robot is disabled.
    * Code that should only run once when the opmode is selected should go in the opmode constructor.
    */
    public void disabledPeriodic() {
        if (useAutoSelector) autonomousCommand = autonomousChooser.getSelected();
        System.out.println("Auto Command " + autonomousCommand.name());
        Telemetry.log("Auto Command", autonomousCommand.name());
    }

    /** Called once when this opmode transitions to enabled. */
    public void start() {
        System.out.println("MyAutoController start");
        Telemetry.log("Mode", "MyAutoController");
        System.out.println("Auto Command " + autonomousCommand.name());
        Telemetry.log("Auto Command", autonomousCommand.name());

        scheduler.schedule(autonomousCommand);
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
        System.out.println("MyAutoController end");
    }

    /**
    * This function is called when the opmode is no longer selected on the DS or after an enabled run
    * ends. The object will not be reused after this is called.
    */
    public void close() {
        scheduler.cancel(autonomousCommand);
        System.out.println("MyAutoController close");
    }
}
