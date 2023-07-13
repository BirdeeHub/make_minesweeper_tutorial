package MySweep;
import javax.swing.ImageIcon;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import java.awt.EventQueue;
import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.lang.management.ManagementFactory;
import java.util.List;
class MineSweeper {
    public static final Path tempPath = Path.of(System.getProperty("java.io.tmpdir"));
    public static final Path minesweeperclasspath = Path.of(System.getProperty("java.class.path"));
    public static final String ostype = (System.getProperty("os.name").toLowerCase().contains("win"))?"win":"bash";
    public static final String scoresFileName= "MinesweeperScores.txt";
    public static final String scoresFromClassPath = "src/MySweep/"+scoresFileName;
    public static final Image ExplosionIcon = new ImageIcon(MineSweeper.class.getResource(((isJarFile())?"/src/MySweep/":"") + "Icons/GameOverExplosion.png")).getImage();
    public static final Image MineIcon = new ImageIcon(MineSweeper.class.getResource(((isJarFile())?"/src/MySweep/":"") + "Icons/MineSweeperIcon.png")).getImage();
    public static boolean isJarFile() {//<-- apparently .jar files have a magic number that shows if it is a jar file.
        try (FileInputStream fileInputStream = new FileInputStream(minesweeperclasspath.toFile())) {
            byte[] magicNumber = new byte[4];
            int bytesRead = fileInputStream.read(magicNumber);
            return bytesRead == 4 &&
                    (magicNumber[0] == 0x50 && magicNumber[1] == 0x4B && magicNumber[2] == 0x03 && magicNumber[3] == 0x04)
                    || (magicNumber[0] == (byte) 0x80 && magicNumber[1] == 0x75 && magicNumber[2] == 0x03 && magicNumber[3] == 0x04);
        } catch (Exception e) {
            return false;
        }
    }
    /**
     * @param args int width, int height, int BombCount, int lives
     */
    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {//<-- this is where we call our overwrite so that next time we open, we have a new jar.
            if(isJarFile()){
                try(InputStream inputStream = ClassLoader.getSystemResourceAsStream("OverwriteJar.class")){//<-- copy our program that overwrites
                    File destinationDir = tempPath.toFile();
                    destinationDir.mkdirs();
                    Files.copy(inputStream, Path.of(tempPath.toString()+File.separator+"OverwriteJar.class"), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {System.out.println("Unable to copy updater. Scores will not be saved.");e.printStackTrace();}
            	try {//<-- try to call it.
                    ProcessBuilder OvrightJarPro = new ProcessBuilder();
                    OvrightJarPro.command().add(((!ostype.equals("win"))?"":"\"")+Path.of(System.getProperty("java.home")).resolve(Path.of("bin","java")).toString()+((!ostype.equals("win"))?"":"\""));
                    OvrightJarPro.command().addAll(ManagementFactory.getRuntimeMXBean().getInputArguments());
                    OvrightJarPro.command().add("-cp");
                    OvrightJarPro.command().add(((!ostype.equals("win"))?"":"\"")+tempPath.toString()+((!ostype.equals("win"))?"":"\""));
                    OvrightJarPro.command().add("OverwriteJar");
                    OvrightJarPro.command().add(((!ostype.equals("win"))?"":"\"")+minesweeperclasspath.toAbsolutePath().toString()+((!ostype.equals("win"))?"":"\""));
                    OvrightJarPro.command().add(((!ostype.equals("win"))?"":"\"")+tempPath.toString()+((!ostype.equals("win"))?"":"\""));
                    OvrightJarPro.command().add(scoresFromClassPath);
                    List<String> command = OvrightJarPro.command();
                    String OvrightJarCommand = String.join(" ", command);
             	    Runtime.getRuntime().exec(OvrightJarCommand);
           	    } catch (IOException e) {e.printStackTrace();}
            }
        }));
        try {
            // Set cross-platform Java L&F (also called "Metal")
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        }
        catch (UnsupportedLookAndFeelException e) {}
        catch (ClassNotFoundException e) {}
        catch (InstantiationException e) {}
        catch (IllegalAccessException e) {}

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
                            new OpeningWindow(Integer.toString(width),Integer.toString(height), Integer.toString(bombCount), Integer.toString(lives)).setVisible(true);}});
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
