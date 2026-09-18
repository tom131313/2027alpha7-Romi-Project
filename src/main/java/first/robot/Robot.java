package first.robot;

import java.lang.invoke.MethodHandles;

import org.wpilib.command3.Scheduler;
import org.wpilib.datalog.ProtobufLogEntry;
import org.wpilib.driverstation.DriverStationDisplay;
import org.wpilib.framework.OpModeRobot;
import org.wpilib.system.DataLogManager;
import org.wpilib.telemetry.Telemetry;

public class Robot extends OpModeRobot
{
    private static final String m_fullClassName = MethodHandles.lookup().lookupClass().getCanonicalName();
    static
    {
        System.out.println("Loading: " + m_fullClassName);
    }

    private final Scheduler scheduler = Scheduler.getDefault();
    private final RobotContainer robotContainer = new RobotContainer(this);

    private final ProtobufLogEntry<Scheduler> schedulerLog =
      ProtobufLogEntry.create(DataLogManager.getLog(), "Scheduler", Scheduler.proto);

    public Robot()
    {
        System.out.println("Hello World!");

        
        // In Line mode only the lines added between successive updateLines() are written, if those
        // lines are added slower than the underlying display update time period of at least 0.23
        // seconds. Any changes made before the next underlying display update are ignored.

        // If using DriverStationDisplay to show periodically updated values, then set the updateLines()
        // update time period to equal or greater than the longest period of any periodically updated
        // values, otherwise the display blinks as some values are not displayed then displayed if
        // updated too fast. (And don't updateLines() faster (shorter period) than 0.23 seconds.)

        // "Static" values added (that aren't updated or seldomly updated) will display with an
        // updateLines() after their add (if there hasn't been a "recent" updateLines(). A second
        // updateLines(), however, will clear their display.

        // "Keyed" lines obviously update differently than sequentially added lines as keyed lines
        // overlay identical keys but sequentially added lines accumulate during the period between
        // successive updateLines().

        // If high speed display or history of values is needed, then use Telemetry or Datalog.
        // addPeriodic(() -> DriverStationDisplay.updateLines(), 2.); // not used in this project

        DriverStationDisplay.addLine("The Driver Station Display");
        DriverStationDisplay.addLine("Hello World!");
        DriverStationDisplay.addLine("Romi project can't use the real Driver Station");
        DriverStationDisplay.addLine("Set command to be run in Elastic");
        DriverStationDisplay.updateLines(); // above lines stick until another updateLines or ANSI mode then they vanish
    }

    @Override
    public void robotPeriodic()
    {
        scheduler.run();
        schedulerLog.append(scheduler); // dumps some scheduler info to NT but bug in 2027 alpha7 causes DataLog to show <invalid> instead of the good NT data that does show but passes by quickly!
    }

    @Override
    public void disabledInit()
    {
        System.out.println("Robot class Disabled Mode");
        Telemetry.log("Mode", "Robot class Disabled");
    }

    @Override
    public void disabledPeriodic() {}

    @Override
    public void disabledExit() {}

    /**
     * Getter for RobotContainer instance
     * @return robotContainer
     */
    public RobotContainer getRobotContainer()
    {
        return robotContainer;
    }
}
