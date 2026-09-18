package first.robot.mechanisms;

import static org.wpilib.units.Units.Seconds;

import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

import org.wpilib.command3.Command;
import org.wpilib.command3.Coroutine.ForkResult;
import org.wpilib.command3.Mechanism;
import org.wpilib.drive.DifferentialDrive;
import org.wpilib.hardware.rotation.Encoder;
import org.wpilib.romi.RomiMotor;

import first.robot.controls.TriggerBindings;

/**
 * Romi drivetrain actions.
 */
public class RomiDrivetrain implements Mechanism
{
    // The Romi has onboard encoders that are hardcoded
    // to use DIO pins 4/5 and 6/7 for the left and right
    private static final Encoder leftEncoder = new Encoder(4, 5);
    private static final Encoder rightEncoder = new Encoder(6, 7);

    private final RomiMotor leftMotor = new RomiMotor(0);
    private final RomiMotor rightMotor = new RomiMotor(1);
    private final DifferentialDrive differentialDrive = new DifferentialDrive(leftMotor, rightMotor);
    
    private static final double COUNTS_PER_REVOLUTION = 1440.0;
    private static final double WHEEL_DIAMETER_INCH = 2.75591; // 70 mm

    public record WheelEncoders(double left, double right){}
    public static final Supplier<WheelEncoders> encoders = () ->
        {return new WheelEncoders(leftEncoder.getDistance(), rightEncoder.getDistance());};

    public RomiDrivetrain()
    {
        rightMotor.setInverted(true);
        differentialDrive.setSafetyEnabled(true);
        configDefaultCommand();
        // Use inches as unit for encoder distances
        leftEncoder.setDistancePerPulse((Math.PI * WHEEL_DIAMETER_INCH) / COUNTS_PER_REVOLUTION);
        rightEncoder.setDistancePerPulse((Math.PI * WHEEL_DIAMETER_INCH) / COUNTS_PER_REVOLUTION);
        leftEncoder.reset();
        rightEncoder.reset();
    }

    /**
     * Print the drive train encoder values.
     * @return the command to do this
    */
    public static Command printWheelEncoders()
    {
        return Command.noRequirements(coroutine -> {
            while(true)
            {
                System.out.println("wheels distance inched " + encoders.get().left() + " " + encoders.get().right());
                coroutine.yield();
            }
            }).named("encoders");
    }

    /**
     * Arcade drive - set speed and rotation once
     * Not a Command.
     * @param driveSpeed
     * @param rotationSpeed
     */
    public void arcadeDrive(double driveSpeed, double rotationSpeed)
    {
        differentialDrive.arcadeDrive(driveSpeed, rotationSpeed);
    }

    /**
     * Arcade drive - set speed and rotation once from a supplier
     * Not a Command.
     * @param driveSpeedSupplier
     * @param rotationSpeedSupplier
     */
    public void arcadeDrive(DoubleSupplier driveSpeedSupplier, DoubleSupplier rotationSpeedSupplier)
    {
        arcadeDrive(driveSpeedSupplier.getAsDouble(), rotationSpeedSupplier.getAsDouble());
    }

    /**
     * Stop the drive train once
     * Not a Command.
     */
    public void stopDrive()
    {
        differentialDrive.stopMotor();
    }

    /**
     * Stop the motors once and the command ends immediately. The default command then will run.
     * @return
     */
    public Command stopDriveCommand()
    {
        return run(coroutine -> stopDrive()).named("Stop Drive");
    }

    /**
     * Arcade Drive Command runs forever setting the supplied speed until the Command is canceled
     * 
     * @param driveSpeedSupplier
     * @param rotationSpeedSupplier
     * @return drive command
     */
    public Command arcadeDriveCommand(DoubleSupplier driveSpeedSupplier, DoubleSupplier rotationSpeedSupplier)
    {
        return runRepeatedly(() -> arcadeDrive(driveSpeedSupplier, rotationSpeedSupplier))
                .named("Arcade drive");
    }

    /**
     * Drives without rotation for specified seconds then ends
     * 
     * @param driveSpeed
     * @param driveTimeSeconds
     * @return drive command
     */
    public Command autonomousDriveCommand(double driveSpeed, double driveTimeSeconds)
    {
        return
        run(coroutine ->
            {
                ForkResult result;
                result = coroutine.await(arcadeDriveCommand(() -> driveSpeed, () -> 0.0)
                    .withTimeout(Seconds.of(driveTimeSeconds)));
                System.out.println("Drive result successful " + result.successful());
                
                result = coroutine.await(stopDriveCommand());
                System.out.println("Drive result successful " + result.successful());
            }
        )
        .named("Autonomous Drive");
    }

    /**
     * Spins at given speed for given seconds then ends.
     * 
     * @param spinSpeed
     * @param spinTimeSeconds
     * @return the command
     */
    public Command autonomousSpinCommand(double spinSpeed, double spinTimeSeconds)
    {
        return
        run(coroutine ->
            {
                ForkResult result;
                result = coroutine.await(arcadeDriveCommand( () -> 0.0, () -> spinSpeed )
                    .withTimeout(Seconds.of(spinTimeSeconds)));
                System.out.println("Spin result successful " + result.successful());

                result = coroutine.await(stopDriveCommand());
                System.out.println("Spin result successful " + result.successful());

            }
        )
        .named("Autonomous Spin");
    }

    /**
     * Drives once and only when backwards speed is supplied (no rotation allowed).
     * For internal use with the command.
     *  
     * @param driveSpeedSupplier
     * @return the runnable
     */
    private Runnable onlyDriveBackwardRunnable(DoubleSupplier driveSpeedSupplier)
    {
        return
        () ->
            {
            arcadeDrive(
                    () -> Math.clamp(driveSpeedSupplier.getAsDouble(), -1.0, 0.0),
                    () -> 0.0
            );
            };
    }

    /**
     * Drives forever and moves only when backwards speed is supplied (no rotation allowed).
     * 
     * @param driveSpeedSupplier
     * @return the command
     */
    public Command onlyDriveBackwardCommand(DoubleSupplier driveSpeedSupplier)
    {
        return runRepeatedly(onlyDriveBackwardRunnable(driveSpeedSupplier))
                .named("Drive Backward");
    }

    /**
     * The usual usage of the drivetrain is to drive via the driver controller
     * so setup to allow that assuming the driver controller axes are so configured.
     * 
     * <p>Default commands should have the lowest priority of any command that requires its
     * mechanism; the default priority of 0 is totally valid as long as you don’t have any other
     * commands with negative priority values. Ideally we’d use an unsigned int to represent
     * priorities, but Java only has signed integer types [Sam Carlberg]
     */
    private void configDefaultCommand()
    {
        setDefaultCommand(
            run(
                coroutine ->
                {
                    while(true)
                    {
                        arcadeDrive(TriggerBindings.leftYAxisSupplier, TriggerBindings.leftXAxisSupplier);
                        coroutine.yield();
                    }
                }
            )
            .named("romiDrivetrain Default")
        );
    }
}
