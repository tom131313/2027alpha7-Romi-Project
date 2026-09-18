// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package first.robot.commands;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.wpilib.command3.Command;
import org.wpilib.command3.Coroutine;
import org.wpilib.command3.Mechanism;

/**
 * Do Not Use (normally)
 * 
 * <p>An example command that uses an example mechanism of type RomiDrivetrain and does nothing. It
 * would interrupt a running command that uses the same mechanism at the same or lower priority.
 * 
 * <p>Usually better to use:
 * <pre><code>
 *      Command.requires(romiDrivetrain).execute(coroutine -> {}).named("Example Command");
 *    OR EVEN BETTER
 *      run(coroutine -> {}).named("Example Command");
 * </code></pre>
 * in the Mechanism's class static command factory.
 */
public class ExampleCommand implements Command {

    private final Set<Mechanism> requirements = new HashSet<>();

    /**
     * Creates a new ExampleCommand object.
     * 
     * <p>If there is only one required mechanism, then simplify the parameter list and addAll is add.
     * But then this should be in the mechanism's class unless this command has a huge run method.
     *
     * @param mechanisms The mechanisms used by this command.
     */
    public ExampleCommand(Mechanism... mechanisms) {
        requirements.addAll(Arrays.asList(mechanisms));
    }

    @Override
    public void run(Coroutine coroutine) {while(true){coroutine.yield();}}

    @Override
    public String name() {
        return "Example Command";
    }

    @Override
    public Set<Mechanism> requirements() {
        return requirements;
    }

    /**
     * Set the command priority used for scheduling and interrupting.
     *
     * <p>Optional method; this example is unnecessary as it merely returns the default
     * 
     * @return priority of the command
     */
    @Override
    public int priority() {
        return DEFAULT_PRIORITY;
    }

    /**
     * Runs when the command is canceled.
     * 
     * <p>Optional method; this example is unnecessary as it merely does nothing which is the default.
     */
    @Override
    public void onCancel(){}
}
