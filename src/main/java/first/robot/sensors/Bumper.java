package first.robot.sensors;

import java.util.function.BooleanSupplier;

import org.wpilib.hardware.discrete.DigitalInput;

/**
 * Manage a Digital Input
 * 
 * <p>Named for the bumper switch on the front of the Romi
 */
public class Bumper 
{
    private final DigitalInput bumper;

    public Bumper(int port)
    {
        bumper = new DigitalInput(port);
    }


    private boolean isPressed()
    {
        // Romi normally pulls high with open circuit so negate so low is false-open; high is true-closed
        return !bumper.get();
    }

    /**
     * Supplier version of the digital input
     * @return value that is supplied
     */
    public BooleanSupplier isPressedSupplier()
    {
        return this::isPressed;
    }
}
