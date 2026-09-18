package first.robot.mechanisms;

import org.wpilib.command3.Command;
import org.wpilib.command3.Mechanism;
import org.wpilib.hardware.discrete.DigitalOutput;
import org.wpilib.system.Timer;

/**
 * Control the Romi LEDs
 * <p>Instantiate this class for each LED to be controlled
 */
public class RomiLED implements Mechanism
{
    private final DigitalOutput led;
    private final Timer timer = new Timer();

    /**
     * Specify the LED to be controlled
     * @param port
     */
    public RomiLED(int port)
    {
        led = new DigitalOutput(port);
        timer.reset();
        timer.start();

        setDefaultCommand(runRepeatedly(this::off).named("LED port " + port + " default"));
    }

    /**
     * Set LED on once
     */
    public void on()
    {
        led.set(true);
    }

    /**
     * Set LED off once
     */
    public void off()
    {
        led.set(false);
    }

    /**
     * Set LED on forever until canceled then turn off
     * @return the command
     */
    public Command onCommand()
    {
        return
            runRepeatedly(this::on).whenCanceled(this::off).named("Turn On LED");
    }

    /**
     * Turn off LED once then end immediately
     * @return
     */
    public Command offCommand()
    {
        return
            run(coroutine -> off()).named("Turn Off LED");
    }

    /**
     * Blink the LED
     * 
     * <p>Note - on the competition robot there is a better way to blink using the LEDPattern.
     * 
     * <p>A slightly more Java-like way to construct the command from these two methods is shown
     * below in comments.
     */
    private void blink()
    {
        double currentTime = timer.get();
        double decimalPart = Math.abs( currentTime - (int) currentTime );
        if(decimalPart < 0.5)
            on();
        else
            off();
    }
    
    /**
     * Blink the LED forever until the Command is canceled then turn off
     * @return blink command
     */
    public Command blinkCommand()
    {
        return
            runRepeatedly(this::blink).whenCanceled(this::off).named("Blink LED");
    }   
    
    /* slightly more Java-like */
    // public Command blink()
    // {
    //     return
    //         run(coroutine ->
    //             {
    //                 while(true)
    //                 {
    //                     double currentTime = timer.get();
    //                     double decimalPart = Math.abs( currentTime - (int) currentTime );
    //                     if(decimalPart < 0.5)
    //                         on();
    //                     else
    //                         off();
    //                     coroutine.yield();
    //                 }
    //             }
    //         )
    //         .whenCanceled(this::off)
    //         .named("Blink LED");
    // }
}
