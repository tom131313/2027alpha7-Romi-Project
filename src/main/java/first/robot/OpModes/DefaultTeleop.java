// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package first.robot.OpModes;

import org.wpilib.driverstation.DriverStationDisplay;
import org.wpilib.driverstation.DriverStationDisplay.Mode;
import org.wpilib.opmode.PeriodicOpMode;
import org.wpilib.opmode.Teleop;
import org.wpilib.system.Timer;

@Teleop(name = "Default Teleop")
public class DefaultTeleop extends PeriodicOpMode {
  private static final int[] COLORS = {
    196, 202, 208, 214, 220, 226, 118, 46, 48, 51,
    45, 39, 33, 27, 21, 57, 93, 129, 165, 201
  };

  private final Timer timer = new Timer();
  private int seconds;

  /** Called once when the robot is enabled. */
  @Override
  public void start() {
    DriverStationDisplay.setMode(Mode.RawAnsi);
    timer.restart();
    seconds = 0;

    // DriverStationDisplay.writeRawAnsi(
    //     "\033[2J\033[H"
    //         + "DriverStationDisplay ANSI mode\n"
    //         + "This header and footer stay on screen.\n"
    //         + "Seconds elapsed:      \n"
    //         + "Only the number above is rewritten.");

    new AnsiArt();
    
    updateSecondsDisplay();
  }

  /** Called periodically while the robot is enabled. */
  @Override
  public void periodic() {
    if (timer.advanceIfElapsed(1.0)) {
      seconds++;
      updateSecondsDisplay();
    }
  }

  private void updateSecondsDisplay() {
    int color = COLORS[seconds % COLORS.length];
    DriverStationDisplay.writeRawAnsi(
        String.format("\033[3;18H\033[38;5;%dm%5d\033[0m", color, seconds));
  }
// }


public class AnsiArt {
    static final String ESC = "\u001B[";
    static final String RESET = ESC + "0m";

    // 256-color background and foreground helpers
    static String bg(int c) { return ESC + "48;5;" + c + "m"; }
    static String fg(int c) { return ESC + "38;5;" + c + "m"; }

    // public static void main(String[] args)
    public AnsiArt()
     {
        // Title in bold, colored text
         DriverStationDisplay.writeRawAnsi(ESC + "1m" + fg(213) + "  ANSI ART  " + RESET + "\n");
         DriverStationDisplay.writeRawAnsi("\n");

        // Rainbow banner: one colored row per band
        int[] rainbow = {196, 208, 226, 46, 21, 93};
        for (int color : rainbow) {
             DriverStationDisplay.writeRawAnsi(bg(color) + " ".repeat(40) + RESET + "\n");
        }
        System.out.println();

        // Sunny scene drawn cell by cell (2 spaces per cell for squarer pixels)
        int width = 20, height = 12;
        int sunX = 14, sunY = 3, sunR = 3;

        for (int y = 0; y < height; y++) {
            StringBuilder row = new StringBuilder();
            for (int x = 0; x < width; x++) {
                int color;
                int dx = x - sunX, dy = y - sunY;
                if (y >= 9) {
                    color = 34;                          // grass
                } else if (dx * dx + dy * dy <= sunR * sunR) {
                    color = 226;                         // sun
                } else {
                    color = 117 - (y / 3);               // sky, slight gradient
                }
                row.append(bg(color)).append("  ");
            }
            row.append(RESET);
             DriverStationDisplay.writeRawAnsi(row.toString() + "\n");
        }

         DriverStationDisplay.writeRawAnsi(RESET + "\n");
    }
}


}
/*
public class AnsiArt {
    static final String ESC = "\u001B[";
    static final String RESET = ESC + "0m";

    // 256-color background and foreground helpers
    static String bg(int c) { return ESC + "48;5;" + c + "m"; }
    static String fg(int c) { return ESC + "38;5;" + c + "m"; }

    public static void main(String[] args) {
        // Title in bold, colored text
        System.out.println(ESC + "1m" + fg(213) + "  ANSI ART  " + RESET);
        System.out.println();

        // Rainbow banner: one colored row per band
        int[] rainbow = {196, 208, 226, 46, 21, 93};
        for (int color : rainbow) {
            System.out.println(bg(color) + " ".repeat(40) + RESET);
        }
        System.out.println();

        // Sunny scene drawn cell by cell (2 spaces per cell for squarer pixels)
        int width = 20, height = 12;
        int sunX = 14, sunY = 3, sunR = 3;

        for (int y = 0; y < height; y++) {
            StringBuilder row = new StringBuilder();
            for (int x = 0; x < width; x++) {
                int color;
                int dx = x - sunX, dy = y - sunY;
                if (y >= 9) {
                    color = 34;                          // grass
                } else if (dx * dx + dy * dy <= sunR * sunR) {
                    color = 226;                         // sun
                } else {
                    color = 117 - (y / 3);               // sky, slight gradient
                }
                row.append(bg(color)).append("  ");
            }
            row.append(RESET);
            System.out.println(row);
        }

        System.out.println(RESET);
    }
}
*/