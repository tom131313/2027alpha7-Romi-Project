package first.robot;


import static first.robot.Config.CommandLoggingSettings.logsSelector;

import org.wpilib.command3.Command;
import org.wpilib.command3.Scheduler;
import org.wpilib.command3.SchedulerEvent;
import org.wpilib.command3.button.CommandXboxController;
import org.wpilib.hardware.hal.RobotMode;
import org.wpilib.romi.RomiGyro;
import org.wpilib.tunable.Selectable;

import first.robot.CommandSchedulerLog.LogsSelector;
import first.robot.Constants.Alerts;
import first.robot.OpModes.MyAutoController;
import first.robot.mechanisms.RomiDrivetrain;
import first.robot.mechanisms.RomiLED;
import first.robot.sensors.Bumper;

public class RobotContainer 
{
    private final Robot robot;
    private CommandSchedulerLog schedulerLog;

    // Sensors
    private final CommandXboxController xbox = new CommandXboxController(0);
    private final Bumper frontBumper = new Bumper(8); // Romi DIO EXT 0 robot port 8
    private final RomiGyro gyro = new RomiGyro();
    private final Selectable<Command> autonomousChooser = new Selectable<>();

    // Mechanisms
    private final RomiDrivetrain romiDrivetrain;
    // Romi digital ports can be configured I or O; this assumes they are O which are LEDs
    private final RomiLED greenLED = new RomiLED(1);
    private final RomiLED redLED = new RomiLED(2);

    RobotContainer(Robot robot)
    {
        this.robot = robot;
        configureCommandLogs();
        
        romiDrivetrain = new RomiDrivetrain();
        createMyOpModes();
    }

    private void createMyOpModes()
    {
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
    
    public Selectable<Command> getAutoSelector()
    {
        return autonomousChooser;
    }

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

  /**
   * Configure Command logging to Console/Terminal or DataLog
   */
  private void configureCommandLogs()
  {
      if (logsSelector.contains(LogsSelector.useConsole) ||
          logsSelector.contains(LogsSelector.useDataLog)) {
        schedulerLog = new CommandSchedulerLog(logsSelector);
        Scheduler.getDefault().addEventListener( // Can (optionally) generate a lot of output
            event -> {
              // examples of debug logging and suppressing huge excess output so the good stuff is easier to find
              // System.out.println("[SchedulerEvent] " + event); // trivial logging to console but it works and is complete but many Mounted and Yielded
              // if (event.toString().contains("Achieve Hue Display")) return;
              switch (event) { // a smarter formatting to hold down excess output
                  case SchedulerEvent.Scheduled(var cmd, var time) -> schedulerLog.logCommandScheduled(cmd, time);
                  case SchedulerEvent.Mounted(var cmd, var time) -> schedulerLog.logCommandMounted(cmd, time);
                  case SchedulerEvent.Yielded(var cmd, var time) -> schedulerLog.logCommandYielded(cmd, time);
                  case SchedulerEvent.Completed(var cmd, var time) -> schedulerLog.logCommandCompleted(cmd, time);
                  case SchedulerEvent.CompletedWithError(var cmd, var exception, var time) -> schedulerLog.logCommandCompletedWithError(cmd, exception, time);
                  case SchedulerEvent.Canceled(var cmd, var time) -> schedulerLog.logCommandCanceled(cmd, time);
                  case SchedulerEvent.Interrupted(var cmd, var byCmd, var time) -> schedulerLog.logCommandInterrupted(cmd, byCmd, time);
              }
            }
          );
      }
      else {
         Alerts.COMMAND_LOGGING.set(true);
      }
  }
}
