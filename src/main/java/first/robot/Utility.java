package first.robot;

import org.wpilib.command3.Command;
import org.wpilib.command3.Coroutine.ForkResult;
import org.wpilib.command3.Scheduler.ScheduleResult.LowerPriorityThanQueuedCommand;
import org.wpilib.command3.Scheduler.ScheduleResult.LowerPriorityThanRunningCommand;

public class Utility {
    private Utility(){}

    /**
     * Check a coroutine fork for errors.
     * Just a demo since it prints successful commands, too.
     * Will need updating with next WPILib release that supports UnSafe mechanisms.
     * 
     * <p>Also note that successful() isn’t necessary by default; if a command fails to fork, the
     * composition is interrupted unless you call 
     *<pre><code>
     *      coroutine.setCancelOnForkFailure(false)
     *</code></pre>
     *<p>otherwise the interrupted command never gets here.
     * 
     * @param result of the fork
     */
    public static void checkFork(ForkResult result)
    {
        if (result.failed())
        {
            result.getFailedCommands().forEach(failure -> {
            switch(failure) {
                case LowerPriorityThanQueuedCommand(Command cmd, Command queued) ->
                    System.out.printf("Command %s was lower priority than queued command %s\n", cmd.name(), queued.name());
                case LowerPriorityThanRunningCommand(Command cmd, Command running) ->
                    System.out.printf("Command %s was lower priority than running command %s\n", cmd.name(), running.name());
                // case RequiresUnsafeMechanisms(Command cmd, Collection<Mechanism> mechs) ->
                //     System.out.printf("The robot is disabled and command %s requires unsafe mechanisms %s\n", cmd.name(), mechs.stream().map(Mechanism::getName).collect(joining(", ")));
                default -> System.out.println("unspecified fork failure");
            }});
        }
        else if (result.successful())
        {
            result.getForkedCommands().forEach(successfulCmd -> {
                System.out.println("forked " + successfulCmd.name());
            });
        }
        else
        {
            System.out.println("Fork neither failed nor successful");
        }
    }
}
