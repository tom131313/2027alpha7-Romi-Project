package first.robot;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean
 * constants. These values are not expected to change (often). This class should not be used for any
 * other purpose. All constants should be declared globally (i.e. public static). Do not put
 * anything functional in this class.
 * 
 * <p>Maybe Alerts should be put in this class.
 * 
 * <p>
 * Variables that are considered user settable to configure a particular use should be set in the
 * {@link Config} interface.
 * <p>
 * It is advised to statically import this class and the inner classes wherever the constants are
 * needed, to reduce verbosity.
 * <p>
 * There are a few user settable constants scattered around this project. These likely will not be
 * changed and aren't in {@link Config} either. The values are usually an integral part of the
 * structure of the example and changing the "constant" would mess up the logic which was not
 * dynamically determined.
 */
public final class Constants {
  
    /**
     * ALERTS
     */
    public static final class Alerts {
        
        public static final Alert COMMAND_LOGGING = new Alert("Log Commands", "1", "No Command Logging Selected", Alert.Level.LOW);
        public static final Alert MOUNTED_ERROR = new Alert("Log Commands", "2", "", Alert.Level.HIGH);
    }
}
