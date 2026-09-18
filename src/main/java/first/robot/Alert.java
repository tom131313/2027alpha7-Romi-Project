package first.robot;

import java.lang.invoke.MethodHandles;

/**
 * Stub since Alert still not available WPILib 2027 alpha7 simulation
 * Alert
 */
public class Alert {
    private static final String m_fullClassName = MethodHandles.lookup().lookupClass().getCanonicalName();
    static
    {
        System.out.println("Loading: " + m_fullClassName);
    }
    
    public enum Level {LOW, MEDIUM, HIGH};

    String group = "Alert";
    String id = "";
    String text = "";
    Level level = Level.LOW;
    boolean display = false;

    Alert(String group, String id, String text, Level level) {
        this.group = group;
        this.id = id;
        this.text = text;
        this.level = level;
    }

    void set(boolean display) {
        this.display = display;
        if (display) System.out.println("[ALERT] " + group + " " + id + " " + text + " importance " + level);
    }

    void setText(String text) {
        this.text = text;
    }
}
