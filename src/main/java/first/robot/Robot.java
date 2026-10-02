package first.robot;

import java.lang.invoke.MethodHandles;

import org.wpilib.command3.Scheduler;
import org.wpilib.driverstation.DriverStationDisplay;
import org.wpilib.framework.OpModeRobot;
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
