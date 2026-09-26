package first.robot.commands;

import static first.robot.Utility.checkFork;
import static org.wpilib.units.Units.Seconds;

import org.wpilib.command3.Command;
import org.wpilib.romi.RomiGyro;

import first.robot.RobotContainer;
import first.robot.mechanisms.RomiDrivetrain;
import first.robot.mechanisms.RomiLED;

/**
 * Make static command factories for commands that require more than one or less than one mechanism.
 * 
 * <p>Commands requiring one mechanism belong in the class of the Mechanism.
 */
public abstract class MyCommands
{
    private static RomiDrivetrain romiDrivetrain;
    private static RomiLED greenLED;
    private static RomiLED redLED;
    private static RomiGyro gyro;

    private MyCommands() {}

    /**
     * Required to run this method to define the mechanisms used in this class.
     * 
     * @param robotContainer
     */
    public static void createMyCommands(RobotContainer robotContainer)
    {
        romiDrivetrain = robotContainer.getRomiDrivetrain();
        greenLED = robotContainer.getGreenLED();
        redLED = robotContainer.getRedLED();
        gyro = robotContainer.getGyro();
    }

    /**
     * Turn off the red light then drive for three seconds with the green light blinking then end.
     * @return the command to do this
     */
    public static Command drive3SecondsCommand()
    {
        return
        Command
        // Not typical but lockout Mechanisms for the duration instead of just step-by-step.
        // This covers the non-command use of a Mechanism that is hidden from the Scheduler.
        // This prevents global scope default commands from running until the entire command completes.
        .requiring(
            redLED,
            greenLED,
            romiDrivetrain)
        .executing(
            coroutine ->
            {
            greenLED.setDefaultCommand(greenLED.offCommand());
            romiDrivetrain.setDefaultCommand(romiDrivetrain.stopDriveCommand());
            redLED.off();
            var result = coroutine.fork(
                greenLED.blinkCommand(),
                romiDrivetrain.arcadeDriveCommand(() -> 0.5, () -> 0.));
            checkFork(result);
            coroutine.wait(Seconds.of(3.));
            })
        .named("Drive 3 Seconds");
    }

    /**
     * Drive and Spin for several seconds with the green light blinking then end.
     * @return the command to do this
     */
    public static Command autonomousDriveAndSpinCommand()
    {
        return
        Command
        .noRequirements(
            coroutine ->
            {
            coroutine.fork(greenLED.blinkCommand());
            coroutine.await(romiDrivetrain.autonomousDriveCommand(0.5, 3.0));
            coroutine.await(romiDrivetrain.autonomousSpinCommand(0.4, 1.5));
            coroutine.await(romiDrivetrain.autonomousDriveCommand(-0.5, 2.0));
            coroutine.await(romiDrivetrain.autonomousSpinCommand(-0.4, 1.0));
            }
        )
        .named("Autonomous Drive and Spin");
    }

        /**
     * Print the gyro values.
     * @return the command to do this
    */
    public static Command printGyro()
    {
        return Command.noRequirements(coroutine -> {
            while(true)
            {
                System.out.println("gyro angle degrees " + gyro.getAngle());
                coroutine.yield();
            }
            }).named("encoders");
    }
}
