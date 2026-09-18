package first.robot.commands;

import java.util.HashSet;
import java.util.Set;
import java.util.function.DoubleSupplier;

import org.wpilib.command3.Command;
import org.wpilib.command3.Coroutine;
import org.wpilib.command3.Mechanism;

import first.robot.mechanisms.RomiDrivetrain;

/**
 * Do Not Use
 * 
 * <p>Equivalent to the real one in {@link RomiDrivetrain#arcadeDriveCommand()}:
 * <p><pre><code>
 *  runRepeatedly(coroutine -> arcadeDrive(driveSpeedSupplier, rotationSpeedSupplier))
 *      .named("Arcade drive");
 * </code></pre>
 */
public class ArcadeDriveCommand implements Command
{
    private final Set<Mechanism> requirements = new HashSet<>();
    private final DoubleSupplier driveSpeedSupplier;
    private final DoubleSupplier rotationSpeedSupplier;
    private final RomiDrivetrain romiDrivetrain;

    public ArcadeDriveCommand(DoubleSupplier driveSpeedSupplier,
                              DoubleSupplier rotationSpeedSupplier,
                              RomiDrivetrain romiDrivetrain)
    {
        this.driveSpeedSupplier = driveSpeedSupplier;
        this.rotationSpeedSupplier = rotationSpeedSupplier;
        this.romiDrivetrain = romiDrivetrain;

        requirements.add(romiDrivetrain);
    }

    @Override
    public void run(Coroutine coroutine) {
        while(true)
        {
            romiDrivetrain.arcadeDrive(driveSpeedSupplier, rotationSpeedSupplier);
            coroutine.yield();
        }
    }

    @Override
    public String name() {
        return "Arcade Drive";
    }

    @Override
    public Set<Mechanism> requirements() {
        return requirements;
    }
}
