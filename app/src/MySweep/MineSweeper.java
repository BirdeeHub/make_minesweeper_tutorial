package MySweep;
import javax.swing.ImageIcon;
import javax.swing.UIManager;
import java.awt.EventQueue;
import java.awt.Image;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.awt.Frame;
class MineSweeper {
    private static final Path minesweeperclasspath = Path.of(System.getProperty("java.class.path"));
    private static final Path tempDirPath = Path.of(System.getProperty("java.io.tmpdir"));
    private static final String OvrightJarClassName = "OverwriteMinesweeperJar";
    private static final Path OvrightJarPath = tempDirPath.resolve(OvrightJarClassName+".class");
    private static final String scoresFileName= "MinesweeperScores.txt";
    public static final Path tempScoresPath = tempDirPath.resolve(scoresFileName);
    public static final Path scoresPathForIDE = minesweeperclasspath.resolve("MySweep").resolve("save").resolve(scoresFileName);
    public static final String scoresEntryName = "src/MySweep/save/"+scoresFileName;
    public static final Image ExplosionIcon = new ImageIcon(MineSweeper.class.getResource(((isJarFile())?"/src/MySweep/":"") + "Icons/GameOverExplosion.png")).getImage();
    public static final Image MineIcon = new ImageIcon(MineSweeper.class.getResource(((isJarFile())?"/src/MySweep/":"") + "Icons/MineSweeperIcon.png")).getImage();
    public static boolean isJarFile() {//<-- apparently .jar files have a magic number that shows if it is a jar file.
        try (FileInputStream fileInputStream = new FileInputStream(minesweeperclasspath.toFile())) {
            byte[] magicNumber = new byte[4];
            int bytesRead = fileInputStream.read(magicNumber);
            return bytesRead == 4 &&
                    (magicNumber[0] == 0x50 && magicNumber[1] == 0x4B && magicNumber[2] == 0x03 && magicNumber[3] == 0x04)
                    || (magicNumber[0] == (byte) 0x80 && magicNumber[1] == 0x75 && magicNumber[2] == 0x03 && magicNumber[3] == 0x04);
        } catch (Exception e) {return false;}
    }
    private static boolean DarkMode = true;
    public static boolean isDarkMode(){return DarkMode;}
    public static void toggleDarkMode() {
        Frame[] frames = Frame.getFrames();
        DarkMode = !DarkMode;
        for (Frame frame : frames) {
            if(frame instanceof MainGameWindow){
                ((MainGameWindow)frame).toggleDarkMode();
            }else if(frame instanceof InstructionsWindow){
                ((InstructionsWindow)frame).toggleDarkMode();
            }else if(frame instanceof OpeningWindow){
                ((OpeningWindow)frame).toggleDarkMode();
            }else if(frame instanceof ScoresWindow){
                ((ScoresWindow)frame).toggleDarkMode();
            }
        }
    }
    private static Process startJarOverwriter() throws IOException{
        ProcessBuilder OvrightJarPro = new ProcessBuilder();
        OvrightJarPro.command(Path.of(System.getProperty("java.home")).resolve("bin").resolve("java").toString());
        OvrightJarPro.command().add("-cp");
        OvrightJarPro.command().add(OvrightJarPath.getParent().toString());
        OvrightJarPro.command().add(OvrightJarClassName);
        OvrightJarPro.command().add(String.valueOf(ProcessHandle.current().pid()));
        OvrightJarPro.command().add(scoresEntryName);
        OvrightJarPro.command().add(minesweeperclasspath.toAbsolutePath().toString());//<- original jar path
        OvrightJarPro.command().add(OvrightJarPath.toString());
        OvrightJarPro.command().add(tempScoresPath.toString());//<-- scores directory
        OvrightJarPro.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        OvrightJarPro.redirectError(ProcessBuilder.Redirect.INHERIT);
        return OvrightJarPro.start();
    }
    /**
     * @param args [String "o" or "m"], int width, int height, int BombCount, int lives
     */
    public static void main(String[] args) {
        try {UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());}
        catch (Exception e) {e.printStackTrace();}
        if(isJarFile()){
            try(InputStream inputStream = ClassLoader.getSystemResourceAsStream(OvrightJarPath.getFileName().toString())){//<-- copy our program that overwrites
                OvrightJarPath.getParent().toFile().mkdirs();
                Files.copy(inputStream, OvrightJarPath);
            } catch (IOException e) {}
            Thread processMonitoringThread = new Thread(() -> {
                try {
                    Process jarOverwriter = startJarOverwriter();
                    while (true) {
                        try {// wait for the overwriter process to die in case it dies before the game does so that we can restart it.
                            int exitCode = jarOverwriter.waitFor();
                            System.out.println("Overwriter process exited with code: " + exitCode + ". Attempting restart of " + OvrightJarClassName);
                            jarOverwriter = startJarOverwriter();
                            Thread.sleep(1500);//<-- Sleep for a short interval before checking again
                        } catch (InterruptedException e) {e.printStackTrace();}
                    }
                }catch (IOException e) {e.printStackTrace();}
            });
            processMonitoringThread.start();
        }
        int width, height, bombCount, lives;
        if(args.length == 4){
            try{
                width = (int)(Integer.parseInt(args[0]));
                height = (int)(Integer.parseInt(args[1]));
                bombCount = (int)(Integer.parseInt(args[2]));
                lives = (int)(Integer.parseInt(args[3]));
                if(width > 0 && height > 0 && bombCount > 0 && lives > 0){
                    EventQueue.invokeLater(new Runnable(){public void run(){new MainGameWindow(width, height, bombCount, lives).setVisible(true);}});
                }else{
                    System.out.println("integer arguments only: width, height, BombCount, lives (where all are > 0)");
                    EventQueue.invokeLater(new Runnable(){public void run(){new OpeningWindow().setVisible(true);}});
                }
            }catch(NumberFormatException e){
                System.out.println("integer arguments only: width, height, BombCount, lives (where all are > 0)");
                EventQueue.invokeLater(new Runnable(){public void run(){new OpeningWindow().setVisible(true);}});
            }
        }else if(args.length == 5){
            if(args[0].equals("o")||args[0].equals("m")){
                try{
                    width = (int)(Integer.parseInt(args[1]));
                    height = (int)(Integer.parseInt(args[2]));
                    bombCount = (int)(Integer.parseInt(args[3]));
                    lives = (int)(Integer.parseInt(args[4]));
                    if(width > 0 && height > 0 && bombCount > 0 && lives > 0){
                        if(args[0].equals("m"))EventQueue.invokeLater(new Runnable(){public void run(){
                            new MainGameWindow(width, height, bombCount, lives).setVisible(true);}});
                        if(args[0].equals("o"))EventQueue.invokeLater(new Runnable(){public void run(){
                            new OpeningWindow(args[1],args[2],args[3],args[4]).setVisible(true);}});
                    }else{
                        System.out.println("<m or o>, width, height, BombCount, lives");
                        EventQueue.invokeLater(new Runnable(){public void run(){new OpeningWindow().setVisible(true);}});
                    }
                }catch(NumberFormatException e){
                    System.out.println("<m or o>, width, height, BombCount, lives");
                    EventQueue.invokeLater(new Runnable(){public void run(){new OpeningWindow().setVisible(true);}});
                }
            }else{
                System.out.println("<m or o>, width, height, BombCount, lives");
                EventQueue.invokeLater(new Runnable(){public void run(){new OpeningWindow().setVisible(true);}});
            }
        }else{
            EventQueue.invokeLater(new Runnable(){public void run(){new OpeningWindow().setVisible(true);}});
        }
    }
}
