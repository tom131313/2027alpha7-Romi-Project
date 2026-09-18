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

import org.wpilib.command3.Command;
import org.wpilib.command3.Scheduler;
import org.wpilib.driverstation.RobotState;
import org.wpilib.framework.OpModeRobot;
import org.wpilib.opmode.OpMode;
import org.wpilib.telemetry.Telemetry;
import org.wpilib.tunable.Selectable;
import org.wpilib.tunable.Tunables;

import first.robot.Robot;
import first.robot.RobotContainer;
import first.robot.commands.MyCommands;

public class MyAutoController implements OpMode {

    @SuppressWarnings("unused")
    private Robot robot;
    private Scheduler scheduler = Scheduler.getDefault();
    private Command autonomousCommand;
    private static Selectable<Command> autonomousChooser;

    public MyAutoController(Robot robot)
    {
        this.robot = robot;
        System.out.println("Constructing: " + RobotState.getOpMode() + ", " + RobotState.getRobotMode());

        selector();
    }

    private void selector()
    {
        autonomousCommand =
            switch (RobotState.getOpMode())
            {
                case "Auto Spin" -> MyCommands.autonomousDriveAndSpinCommand();
                case "Auto Drive 3 Sec" -> MyCommands.drive3SecondsCommand();
                // default -> Command.noRequirements(coroutine -> {}).named("do nothing auto");
                default -> autonomousChooser.getSelected();
            };
        System.out.println("Auto Command " + autonomousCommand.name());
        Telemetry.log("Auto Command", autonomousCommand.name());
    }

    public static void createAutoSelector(RobotContainer robotContainer)
    {
        autonomousChooser = robotContainer.getAutoSelector();

        // Add commands to the autonomous command chooser
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
    public void disabledPeriodic() {}

    /** Called once when this opmode transitions to enabled. */
    public void start() {
        System.out.println("MyAutoController start");
        Telemetry.log("Mode", "MyAutoController");
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
