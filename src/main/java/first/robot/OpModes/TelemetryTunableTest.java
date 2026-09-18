package first.robot.OpModes;

import java.lang.invoke.MethodHandles;
import java.util.function.Supplier;

import org.wpilib.math.controller.PIDController;
import org.wpilib.opmode.OpMode;
import org.wpilib.opmode.Utility;
import org.wpilib.telemetry.TelemetryLoggable;
import org.wpilib.telemetry.TelemetryTable;
import org.wpilib.tunable.ComplexTunable;
import org.wpilib.tunable.TunableConfig;
import org.wpilib.tunable.TunableOption;
import org.wpilib.tunable.TunableTable;
import org.wpilib.tunable.Tunables;

/**
 *  ComplexTunable test and telemetry test and run as an OpMode
 */

@Utility(name = "Telemetry and Tunable Test")
public class TelemetryTunableTest implements  OpMode, /* activate OpMode */
                                              TelemetryLoggable, /* activate logTo */
                                              ComplexTunable /* activate publishTunable */
{
    private static final String m_fullClassName = MethodHandles.lookup().lookupClass().getCanonicalName();
    static
    {
        System.out.println("Loading: " + m_fullClassName);
    }

    PIDController pid; // for the demo
    
    public TelemetryTunableTest()
    {      
      Tunables.publish("For Demonstrating Tunable", this); // subtable name; tuning inputs from dashboard

      pid = new PIDController(0, 0, 0);
      Tunables.publish("For Tuning PID Controller", pid); // subtable name; tuning inputs from dashboard
    }

    private String HiHoMessage = "HiHo Test";

    @Override
    public void logTo(TelemetryTable table)
    {
        table.log("HiHo", "It's here");
    }

    /**
     * set the .type entry value to document the data
     */
    @Override
    public String getTelemetryType() {
      return HiHoMessage;
    }
    
    public String getMessage() {
      return HiHoMessage;
    }

    public void setMessage(String message) {
        if (HiHoMessage != message) {
          setChildTunableChanged("HiHo Message"); // table entry name
          HiHoMessage = message;
          System.out.println(getMessage()); // print changed tuned variable          
        }
    }

    double testDouble = 123.;

    public double getTestDouble() {
      return testDouble;
    }

    public void setTestDouble(double value) {
      if (testDouble != value) {
        setChildTunableChanged("Test Double");
        testDouble = value;
        System.out.println(getTestDouble()); // print changed tuned variable          
      }
    }
    
    @Override
    public void publishTunable(TunableTable table) {
      var getOnChange = TunableConfig.of(TunableOption.GET_ON_CHANGE);

      table.publishValue("HiHo Message", // table entry name
                        (Supplier<String>)()->HiHoMessage,
                        this::setMessage,
                        String.class,
                        getOnChange);

      table.publishDouble("test double", // table entry name
                        this::getTestDouble,
                        this::setTestDouble,
                        getOnChange);                    
    }

    /**
     * set the .type entry value to document the data
     */
    @Override
    public String getTunableType() {
      return "Test Two Types";
    }
}
