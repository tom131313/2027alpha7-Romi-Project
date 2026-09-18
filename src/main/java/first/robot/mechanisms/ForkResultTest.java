package first.robot.mechanisms;

import static first.robot.Utilities.checkFork;
import static org.wpilib.units.Units.Seconds;

import java.lang.invoke.MethodHandles;
import java.util.function.Consumer;

import org.wpilib.command3.Command;
import org.wpilib.command3.Coroutine;
import org.wpilib.command3.Mechanism;
import org.wpilib.command3.Scheduler;
import org.wpilib.networktables.NetworkTableInstance;
import org.wpilib.opmode.OpMode;
import org.wpilib.opmode.Teleop;
import org.wpilib.telemetry.Telemetry;
import org.wpilib.tunable.Selectable;
import org.wpilib.tunable.Tunables;

/**
 * Open Elastic and select the desired test command to run.
 * That value is retained in NT even if Elastic closes but reopen Elastic and can change the selection.
 */
@Teleop
public class ForkResultTest implements OpMode {
    private static final String m_fullClassName = MethodHandles.lookup().lookupClass().getCanonicalName();
    static
    {
        System.out.println("Loading: " + m_fullClassName);
    }
    NetworkTableInstance nt = NetworkTableInstance.getDefault();
    private Scheduler scheduler = Scheduler.getDefault();

    public A a;
    public B b;
    public C c;

    private int count;
    Command testCommand;
    private static final Selectable<Command> testCommandChooser = new Selectable<>();
    private final Command saveADelayedForLater;
    
    public ForkResultTest()
    {
        System.out.println("Constructing ForkResultTest with command chooser Selectable");

        a = new A();
        b = new B();
        c = new C();
        
        // Add commands to the command chooser

        Tunables.remove("Test Command"); // clean up NT path; doesn't change the selected value though
                                    // if previously set - must use Elastic to change the selection
        testCommandChooser.add("composite1", composite1());
        testCommandChooser.add("composite2", composite2());
        testCommandChooser.add("composite3", composite3());
        testCommandChooser.add("composite4", composite4());
        saveADelayedForLater = a.ADelayed();
        testCommandChooser.add("A delayed", saveADelayedForLater);
        testCommandChooser.addDefault("Do Nothing", Command.noRequirements(coroutine -> {}).named("do nothing auto"));


        // Put the chooser on the dashboard
        System.out.println("Publish Test Command Selectable result " +
            Tunables.publish("Test Command", testCommandChooser));
    }

    public class A implements Mechanism
    {
        public A()
        {
            setDefaultCommand(run(coroutine ->
                {
                    int counter = 0;
                    while (true) {
                        if (counter++%200 == 0) System.out.println("A default " + counter); 
                        coroutine.yield();                    
                    }
                }
            ).named("A default"));
        }

        public Command Acommand()
        {
            return 
            run(
                coroutine ->
                {
                    System.out.println("executing A command");
                    coroutine.wait(Seconds.of(2.));
                    System.out.println("completed A command");
                }
            ).named("Acommand");
        }

        public Command ADelayed()
        {
            return
            run(coroutine -> coroutine.wait(Seconds.of(3.))).named("A Delayed");
        }
    }

    class B implements Mechanism
    {
        public B()
        {
            setDefaultCommand(run(coroutine ->
                {
                    int counter = 0;
                    while (true) {
                        if (counter++%100 == 0) System.out.println("B default " + counter);
                        coroutine.yield();                    
                    }
                }
            ).named("B default"));
        }

        public Command Bcommand()
        {
            return 
            run(
                coroutine ->
                {
                    System.out.println("executing B command");
                    coroutine.wait(Seconds.of(10.));
                    System.out.println("completed B command");
                }
            ).named("Bcommand");
        }
    }

    class C implements Mechanism
    {
        public C()
        {
            setDefaultCommand(run(coroutine ->
                {
                    int counter = 0;
                    while (true) {
                        if (counter++%100 == 0) System.out.println("C default " + counter);
                        coroutine.yield();                     
                    }
                }
            ).named("C default"));
        }

        public Command Ccommand()
        {
            return 
            run(
                coroutine ->
                {
                    System.out.println("executing C command");
                    coroutine.wait(Seconds.of(16.));
                    System.out.println("completed C command");
                }
            ).named("Ccommand");
        }
    }
    
    public Command composite1()
    {
        return 
        Command.requiring(a, b, c).executing(
            coro1).named("composite 1");     
    }

    public Command composite2()
    {
        return 
        Command.noRequirements(
            coro1).named("composite 2");     
    }

    public Command composite3()
    {
        return
        // Default command per mechanism runs when that individual mechanism is done running.
        // The whole composite is interrupted if another command with any conflicting requirement
        // of any of the individually forked commands that is running at the time of the conflicting requirement.
        // If a forked command in the composite is no longer running then there is no conflict with
        // another command starting that has the same requirements as a completed command in the composite.
        // The composite continues to run.
        Command.noRequirements(coro2).named("composite 3");
    }

    public Command composite4()
    {
        return
        // All mechanisms locked until composite 4 ends then all the default commands run.
        // The whole composite is interrupted if another command with any conflicting requirement runs.
        Command.requiring(a, b, c).executing(coro2).named("composite 4");
    }

    Consumer<Coroutine> coro1 = coroutine ->
        {
            int counter = 0;
            while(true)
            {
                if(counter++%100 == 0) System.out.println(Scheduler.getDefault().currentCommand().name() + " " + counter);
                coroutine.yield();
            }
        };
      
    /*
    fork returns a ForkResult object, which lets you await completion without needing to track
    the forked command objects.
    */
    Consumer<Coroutine> coro2 = coroutine ->
        {
            coroutine.setCancelOnForkFailure(false); // handle errors ourself in checkFork
            // BAD CODE BLOCK; if coroutine.setCancelOnForkFailure(true);, then successful is implied
            // and failure gives some unreachable code and this is improper use of command factories
            // System.out.println("fork A " + (coroutine.fork(a.Acommand()).successful()? "successful":"failed"));
            // System.out.println("fork B " + (coroutine.fork(b.Bcommand()).successful()? "successful":"failed"));
            // System.out.println("fork C " + (coroutine.fork(c.Ccommand()).successful()? "successful":"failed"));
            // coroutine.awaitAll(a.Acommand(), b.Bcommand(), c.Ccommand());

            // GOOD CODE BLOCK
            // var tempA = a.Acommand();
            // var tempB = b.Bcommand();
            // var tempC = c.Ccommand();
            // System.out.println("fork A " + (coroutine.fork(tempA).successful()? "successful":"failed"));
            // System.out.println("fork B " + (coroutine.fork(tempB).successful()? "successful":"failed"));
            // System.out.println("fork C " + (coroutine.fork(tempC).successful()? "successful":"failed"));
            // do more stuff
            // coroutine.awaitAll(tempA, tempB, tempC);

            // BETTER CODE BLOCK FOR EFFICIENCY AND FOOL-PROOFING
            // fork a group of commands based on what needs to be awaited at once
            var result = coroutine.fork(a.Acommand(), b.Bcommand(), c.Ccommand()); // can await these 3 so can fork together
            checkFork(result); // error checking is nice to do if not using the default coroutine.setCancelOnForkFailure(true)
            // want to do more stuff here so we had to pick this fork/await combo instead of just
            //  coroutine.awaitAll(a.Acommand(), b.Bcommand(), c.Ccommand());
            result.awaitCompletion(); // can await all 3 commands together here so can fork all 3 together above
        };

    public void disabledPeriodic() {
        count++;
        testCommand = testCommandChooser.getSelected();
        if (count%100 == 0) {
            System.out.println("Test Command to be run " + testCommand.name());
            Telemetry.log("Test Command to be run", testCommand.name());
        }
    }
    
    public void start()
    {
        count = 0;
        // To test interaction of multiple commands schedule more than one here.
        // Forget the Command chooser since that only does one at a time.
        // Need new feature to select any combo and not just one command.
        scheduler.schedule(testCommand);
    }

    public void periodic()
    {
        count++;
        // If ADelayed scheduled immediately after composite3 there is a race and ADelayed started
        // first and was canceled by composite3.
        // If ADelayed scheduled 1 second after composite3 then ADelayed needing A interrupts and
        // cancels the entire composite3 since it needs A among the A, B, C.
        if (count == 50 && testCommand == saveADelayedForLater) scheduler.schedule(a.ADelayed());
        if (count%100 == 0) System.out.println(scheduler.getRunningCommands());
    }

    public void close()
    {}
}

/*
I did a quick skim of the commands and data logging pages. Putting aside that they’re very clearly LLM-generated, the information presented isn’t totally correct; the broad strokes are believable but the details are subtly wrong.

The data logging page is focused entirely on manual logging to NetworkTables with DataLog capture, ignoring both Epilogue and Telemetry, both of which are much easier to use than manual NT setup (though Telemetry has only been merged recently, so the authors may have only been working off of alpha 6 and not considered in-flight features).

The commands pages are focused on v3 (which I like), but has a few issues.

Compositions are presented in the v2 style, which has limitations on ownership. This is usually a good starting point because it’s safer by default, but there’s no mention of the coroutine-based alternatives that are more powerful. For example:

Command liftThenSpin =
    Command.sequence(
            // A step: it ends, so the sequence moves on.
            robot.arm.runFast().withTimeout(Seconds.of(1.0)),
            // A hold: the last member, so the group is a hold too.
            robot.flywheel.runFast())
        .named("Lift Then Spin (hold)");
This command sequence would own the arm and flywheel for its entire runtime. In the first step, the flywheel is uncommanded: owned by the sequence command, but not directly used. This means the flywheel could lose energy while the arm moves due to its motor being stopped. In the second step, the arm is uncommanded and will likely sag under gravity while the flywheel is used. In v3 it would be addressed by using coroutines and command-scoped default commands, but the site makes no mention of this approach:

Command liftThenSpin =
  Command.noRequirements(coroutine -> {
    robot.flywheel.setDefaultCommand(robot.flywheel.maintainSpeed());
    robot.arm.setDefaultCommand(robot.arm.holdPosition());

    coroutine.await(robot.arm.runFast().withTimeout(Seconds.of(1.0));
    coroutine.await(robot.flywheel.runFast());
  }).named("Lift Then Spin (hold)");
On the “Coroutines” page, timeouts are done in the v2 style (waiting on a Command) instead of using the native Coroutine.waitUntil function (this was also released in alpha 7, so maybe it wasn’t in the authors’ training data):

// Always time out a wait in an auto, or a stuck arm freezes the whole match.
coroutine.await(
    Command.waitUntil(robot.arm::isAtTarget)
        .named("wait for the arm")
        .withTimeout(Seconds.of(3.0))); // time your own arm
Should be this instead:

WaitResult result = coroutine.waitUntil(robot.arm::isAtTarget, Seconds.of(3.0));
if (result.timedOut()) {
   // arm didn't reach the target in time, exit early or take an alternative fallback path
  return;
}

*/