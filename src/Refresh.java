public class Refresh {
  public static void refreshTerminal() {
      try {
          // Sleep 3 seconds
          Thread.sleep(3000);

          // Clear terminal (works in most terminals)
          if (System.getProperty("os.name").contains("Windows")) {
              new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
          } else {
              // Linux/Mac
              System.out.print("\033[H\033[2J");
              System.out.flush();
          }
      } catch (Exception e) {
          System.out.println("Error refreshing terminal.");
      }
  }
}

