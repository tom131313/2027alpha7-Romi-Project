package first.robot;

import java.util.EnumSet;

import first.robot.CommandSchedulerLog.LogsSelector;

/**
 * This defines the user settable configuration
 * <p>
 * Add settings to the appropriate interface to limit visibility of those parameters
 * <p>
 * If a parameter is shared by more than one interface, then define a parameter in common in the
 * high level Config interface and parameters that reference that common parameter to all the
 * appropriate interfaces. (That does reveal that parameter, though, in {@link Config})
 * <p>
 * It is advised to statically import this class (or one of its inner classes) wherever the
 * variables are needed, to reduce verbosity.
 */
public interface Config {

    /**
     * Choices Of Destinations of Command Logging
     */    
    public interface CommandLoggingSettings {

        // public static EnumSet<LogsSelector> logsSelector =
        //     EnumSet.allOf(LogsSelector.class);
        
        // OR PICK WHICH INDIVIDUALS TO USE comment out the unwanted ones
        public static EnumSet<LogsSelector> logsSelector =
            EnumSet.of(
                //   LogsSelector.useConsole,
                  LogsSelector.useDataLog
            );

        // OR PICK NONE
        // public static EnumSet<LogsSelector> logsSelector = EnumSet.noneOf(LogsSelector.class);
    }
}
