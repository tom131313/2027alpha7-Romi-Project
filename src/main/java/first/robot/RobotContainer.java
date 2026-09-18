package first.robot;

import static first.robot.Config.CommandLoggingSettings.logsSelector;

import java.lang.invoke.MethodHandles;

import org.wpilib.command3.button.CommandXboxController;
import org.wpilib.hardware.hal.RobotMode;
import org.wpilib.romi.RomiGyro;

import first.robot.OpModes.MyAutoController;
import first.robot.commands.MyCommands;
import first.robot.controls.TriggerBindings;
import first.robot.mechanisms.RomiDrivetrain;
import first.robot.mechanisms.RomiLED;
import first.robot.sensors.Bumper;

public final class RobotContainer 
{   private static final String m_fullClassName = MethodHandles.lookup().lookupClass().getCanonicalName();
    static
    {
        System.out.println("Loading: " + m_fullClassName);
    }

    private final Robot robot;
    @SuppressWarnings("unused")
    private CommandSchedulerLog schedulerLog = new CommandSchedulerLog(logsSelector);

    // Sensors
    private final CommandXboxController xbox = new CommandXboxController(0);
    private final Bumper frontBumper = new Bumper(8); // Romi DIO EXT 0 robot port 8
    private final RomiGyro gyro = new RomiGyro();

    // Mechanisms
    private final RomiDrivetrain romiDrivetrain;
    // Romi digital ports can be configured I or O; this assumes they are O which are LEDs
    private final RomiLED greenLED = new RomiLED(1);
    private final RomiLED redLED = new RomiLED(2);

    RobotContainer(Robot robot)
    {
        this.robot = robot;
        
        romiDrivetrain = new RomiDrivetrain();
        createMyOpModes();

        MyCommands.createMyCommands(this);
        TriggerBindings.createBindings(this);
    }

    private void createMyOpModes()
    {
        /* These Autonomous OpModes determine where to get the Autonomous command  */
        // If one class will have more than one OpMode assigned to it, the OpModes must be assigned
        // programmatically. The @Autonomous annotation used at the beginning of a class cannot be
        // repeated (it wasn't given the @Repeatable annotation to allow repeats).
        robot.addOpMode(RobotMode.AUTONOMOUS, "Auto Spin", "Group 1", () -> new MyAutoController(robot));
        robot.addOpMode(RobotMode.AUTONOMOUS, "Auto Drive 3 Sec", "Group 1", () -> new MyAutoController(robot));
        robot.addOpMode(RobotMode.AUTONOMOUS, "Use Elastic Dashboard Selector", () -> new MyAutoController(robot));
        robot.publishOpModes();
        /*
        It’s worth noting you can manually register opmodes, you don’t need to make a bunch of separate
        class definitions (eg you can write a loop that goes and registers them with a factory function
        that returns a class instance).

        The opmodes list in the DS is not a flat list; it’s organized into a tree view (using / separators)
        and can be color coded as well.
        */
    }

    /**
     * Getters for stuff
     */

    public RomiDrivetrain getRomiDrivetrain()
    {
        return romiDrivetrain;
    }

    public RomiLED getGreenLED()
    {
        return greenLED;
    }

    public RomiLED getRedLED()
    {
        return redLED;
    }

    public Bumper getFrontBumper()
    {
        return frontBumper;
    }

    public CommandXboxController getXbox()
    {
        return xbox;
    }

    public RomiGyro getGyro()
    {
        return gyro;
    }
}
