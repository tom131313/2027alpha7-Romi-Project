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
        DriverStationDisplay.addLine("The Driver Station Display");
        DriverStationDisplay.addLine("Hello World!");
        DriverStationDisplay.updateLines();
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
