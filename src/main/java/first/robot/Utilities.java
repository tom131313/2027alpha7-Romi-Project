package first.robot;

import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.stream.Collectors;

import org.wpilib.command3.Command;
import org.wpilib.command3.Coroutine.ForkResult;
import org.wpilib.command3.Mechanism;
import org.wpilib.command3.Scheduler.ScheduleResult.AlreadyRunning;
import org.wpilib.command3.Scheduler.ScheduleResult.LowerPriorityThanQueuedCommand;
import org.wpilib.command3.Scheduler.ScheduleResult.LowerPriorityThanRunningCommand;
import org.wpilib.command3.Scheduler.ScheduleResult.RequiresUnsafeMechanisms;
import org.wpilib.command3.Scheduler.ScheduleResult.Success;

public class Utilities {
    private static final String m_fullClassName = MethodHandles.lookup().lookupClass().getCanonicalName();
    static
    {
        System.out.println("Loading: " + m_fullClassName);
    }
    private Utilities(){}

    /**
     * Check a coroutine fork for errors.
     * Just a demo since it prints successful commands, too.
     * <p>
     * Also note that successful() isn’t necessary by default; if a command fails to fork, the
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
                    System.out.printf("Command %s was lower priority than queued command %s\n",
                     cmd.name(),
                     queued.name());
                case LowerPriorityThanRunningCommand(Command cmd, Command running) ->
                    System.out.printf("Command %s was lower priority than running command %s\n",
                     cmd.name(),
                     running.name());
                case RequiresUnsafeMechanisms(Command cmd, Collection<Mechanism> unsafeMechanisms) ->
                    System.out.printf("The robot is disabled and command %s requires unsafe mechanisms %s\n",
                     cmd.name(),
                     unsafeMechanisms.stream().map(Mechanism::getName).collect(Collectors.joining(", ")));
            }});
        }
        else if (result.successful())
        {
            result.getForkedCommands().forEach(successful -> {
            switch(successful) {
                case Success(Command cmd, int runId) -> 
                    System.out.printf("Command %s successfully forked with id %d\n",
                     cmd.name(),
                     runId);
                case AlreadyRunning(Command cmd, int runId) -> {
                    // format before printing to minimize chance of DS warnings printed within this message
                    var errorMessage = String.format("Cannot fork Command %s since it's already running with id %d\n",
                     cmd.name(),
                     runId);
                    System.out.print(errorMessage);}
            }});
        }
        else
        {
            System.out.println("Fork neither failed nor successful");
        }
    }
}
