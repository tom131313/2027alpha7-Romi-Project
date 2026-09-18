package first.robot.controls;

import java.util.function.DoubleSupplier;

import org.wpilib.command3.Trigger;
import org.wpilib.command3.button.CommandXboxController;

import first.robot.RobotContainer;
import first.robot.mechanisms.RomiDrivetrain;
import first.robot.mechanisms.RomiLED;
import first.robot.sensors.Bumper;

/**
 * Create global scope triggers.
 * 
 * <p>Command scope triggers may also be created within a command.
 */
public final class TriggerBindings 
{
    private static CommandXboxController xbox;
    private static RomiDrivetrain romiDrivetrain;
    private static RomiLED greenLED;
    private static RomiLED redLED;
    private static Bumper frontBumper;
    public static DoubleSupplier leftYAxisSupplier;
    public static DoubleSupplier leftXAxisSupplier;


    public static void createBindings(RobotContainer robotContainer)
    {
        // define the needed sensors
        xbox = robotContainer.getXbox();
        frontBumper = robotContainer.getFrontBumper();

        // define the needed mechanisms
        romiDrivetrain = robotContainer.getRomiDrivetrain();
        greenLED = robotContainer.getGreenLED();
        redLED = robotContainer.getRedLED();

        // create the needed trigger bindings
        // sensor events trigger mechanisms' actions
        configSuppliers();
        configAButton();
        configBButton();
        configXYButtons();
        configFrontBumper();

    }

    public static void configSuppliers()
    {
        leftYAxisSupplier = () -> -xbox.getLeftY();
        leftXAxisSupplier = () -> -xbox.getLeftX();
    }

    private static void configAButton()
    {
        Trigger aButtonTrigger = xbox.a();
        aButtonTrigger
            .whileTrue(greenLED.onCommand());
    }

    private static void configBButton()
    {
        Trigger bButtonTrigger = xbox.b();
        bButtonTrigger
            .onTrue(redLED.onCommand())
            .onFalse(redLED.offCommand());
    }

    private static void configXYButtons()
    {
        Trigger xButtonTrigger = xbox.x();
        Trigger yButtonTrigger = xbox.y();

        xButtonTrigger.or(yButtonTrigger)
            .onTrue( romiDrivetrain.arcadeDriveCommand( () -> 0.5, () -> 0.0 ) )
            .onFalse( romiDrivetrain.stopDriveCommand() );
        }

    private static void configFrontBumper()
    {
        Trigger frontBumperTrigger = new Trigger(frontBumper.isPressedSupplier());
        frontBumperTrigger
            .whileTrue(romiDrivetrain.onlyDriveBackwardCommand(leftYAxisSupplier));
    }
}
