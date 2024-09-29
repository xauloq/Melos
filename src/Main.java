import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import javax.sound.sampled.*;

public class Main {
    public static void main(String[] args) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        Scanner scanner = new Scanner(System.in);
        // Get the music file name from the user
        String music = Lobby(scanner);
        // Play the selected music
        playMusic(music, scanner);
    }

    private static void playMusic(String zene, Scanner scanner) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        File file = new File("music/" + zene);
        AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
        Clip clip = AudioSystem.getClip();
        clip.open(audioStream);

        String response = "";
        while (!response.equals("Q")) {
            clearConsole();
            // Display the available commands to the user
            System.out.println("Melos - Music Player");
            System.out.println("Made by xauloq");
            System.out.println("-----------------------------------------");
            System.out.println("P = play, S = stop, R = reset, Q = quit");
            System.out.println("< back | forward >");
            System.out.println("L = lobby");
            System.out.println("T = display time");
            System.out.println("-----------------------------------------");
            System.out.println("Enter your choice: ");
            response = scanner.next().toUpperCase();

            // Handle user commands
            switch (response) {
                case "P": clip.start(); break;
                case "S": clip.stop(); break;
                case "R": clip.setMicrosecondPosition(0); break;
                case "Q": clip.close(); break;
                case "L":
                    clip.close();
                    // Return to the lobby to select a new music file
                    zene = Lobby(scanner);
                    playMusic(zene, scanner);
                    return;
                case "T": displayTime(clip); break;
                default:
                    if (response.matches("<+")) {
                        long skipAmount = response.length() * -10000000L;
                        clip.setMicrosecondPosition(Math.max(0, clip.getMicrosecondPosition() + skipAmount));
                    } else if (response.matches(">+")) {
                        long skipAmount = response.length() * 10000000L;
                        clip.setMicrosecondPosition(Math.min(clip.getMicrosecondLength(), clip.getMicrosecondPosition() + skipAmount));
                    } else {
                        System.out.println("Not a valid response");
                    }
            }
        }
        System.out.println("Bye!");
    }

    private static void displayTime(Clip clip) {
        long currentMicroseconds = clip.getMicrosecondPosition();
        long totalMicroseconds = clip.getMicrosecondLength();
        System.out.printf("Current time: %d seconds\n", currentMicroseconds / 1_000_000);
        System.out.printf("Total duration: %d seconds\n", totalMicroseconds / 1_000_000);
    }

    private static String Lobby(Scanner scanner) {
        // List available music files in the music directory
        File musicDir = new File("music");
        File[] musicFiles = musicDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".wav"));

        if (musicFiles != null && musicFiles.length > 0) {
            System.out.println("Available music files:");
            for (File file : musicFiles) {
                System.out.println(file.getName());
            }
        } else {
            System.out.println("No music files found.");
        }

        // Ask the user to select a music file
        System.out.println("Which music would you like to listen to?");
        return scanner.next();
    }

    private static void clearConsole() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (IOException | InterruptedException ex) {
            System.out.println("Error clearing console: " + ex.getMessage());
        }
    }
}