package first.robot;

import org.wpilib.command3.Scheduler;
import org.wpilib.framework.OpModeRobot;
import org.wpilib.telemetry.Telemetry;

import first.robot.OpModes.MyAutoController;
import first.robot.commands.MyCommands;
import first.robot.controls.TriggerBindings;

public class Robot extends OpModeRobot
{
    private final Scheduler scheduler = Scheduler.getDefault();
    private final RobotContainer robotContainer = new RobotContainer(this);

    public Robot()
    {
        System.out.println("Hello World!");

        MyCommands.createMyCommands(robotContainer);
        TriggerBindings.createBindings(robotContainer);
        MyAutoController.createAutoSelector(robotContainer);
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
}
