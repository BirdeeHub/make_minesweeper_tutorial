package MySweep;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import java.awt.EventQueue;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
//import java.lang.reflect.Field;
//import java.lang.reflect.Method;
//import java.net.URL;
//import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.lang.management.ManagementFactory;
import java.util.List;
class MineSweeper {
    private static Path tempPath = Paths.get(System.getProperty("java.io.tmpdir"));
    private static Path tempJarPath = Paths.get(System.getProperty("java.io.tmpdir"), "TempMSJarIn");
    private static Path minesweeperclasspath = Paths.get(System.getProperty("java.class.path"));
    private static ScoresFileIO scoresFileIO = new ScoresFileIO();
    //---public static methods-------------------------------------------------
    public static ScoresFileIO getIOmanager(){return scoresFileIO;}
    public static Path getTempJarPath(){return tempJarPath;}
    public static Path getClassPath(){return minesweeperclasspath;}
    public static Path getTempPath(){return tempPath;}
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
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if(isJarFile()){
                try(InputStream inputStream = ClassLoader.getSystemResourceAsStream("OverwriteJar.class")){
                    File destinationDir = tempPath.toFile();
                    destinationDir.mkdirs();
                    Files.copy(inputStream, Paths.get(tempPath.toString()+File.separator+"OverwriteJar.class"), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {e.printStackTrace();}
            	try {
                    ProcessBuilder OvrightJarPro = new ProcessBuilder();
                    OvrightJarPro.command().add("\""+Paths.get(System.getProperty("java.home")).resolve(Paths.get("bin","java")).toString()+"\"");
                    OvrightJarPro.command().addAll(ManagementFactory.getRuntimeMXBean().getInputArguments());
                    OvrightJarPro.command().add("-cp");
                    OvrightJarPro.command().add("\""+tempPath.toString()+"\"");
                    OvrightJarPro.command().add("OverwriteJar");
                    OvrightJarPro.command().add("\""+minesweeperclasspath.toAbsolutePath().toString()+"\"");
                    OvrightJarPro.command().add("\""+tempPath.toString()+"\"");
                    List<String> command = OvrightJarPro.command();
                    String OvrightJarCommand = String.join(" ", command);
             	    Runtime.getRuntime().exec(OvrightJarCommand);
           	} catch (IOException e) {e.printStackTrace();}
            }
        }));
        /*Runtime.getRuntime().addShutdownHook(new Thread(() -> {//<-- this version is an experiment that hasnt paid off yet.
            if(isJarFile()){
                try(InputStream inputStream = ClassLoader.getSystemResourceAsStream("OverwriteJar.class")){
                    File destinationDir = MineSweeper.getTempPath().toFile();
                    destinationDir.mkdirs();
                    Files.copy(inputStream, Paths.get(MineSweeper.getTempPath().toString()+File.separator+"OverwriteJar.class"), StandardCopyOption.REPLACE_EXISTING);
                    String additionalClasspath = tempPath.toString()+File.separator;
                    String[] entries = additionalClasspath.split(System.getProperty("path.separator"));
                    URL[] urls = new URL[entries.length];
                    for (int i = 0; i < entries.length; i++) {
                        urls[i] = new URL("file://" + entries[i]);
                    }
                    URLClassLoader additionalClassLoader = new URLClassLoader(urls);
                    try{
                        Class<?> systemClassLoaderClass = ClassLoader.getSystemClassLoader().getClass();
                        Field parentField = systemClassLoaderClass.getDeclaredField("parent");
                        parentField.setAccessible(true);
                        parentField.set(ClassLoader.getSystemClassLoader(), additionalClassLoader);
                    }catch(Exception e){e.printStackTrace();}
                } catch (IOException e) {e.printStackTrace();}
                try{
                    String [] newArgs = new String[2];
                    newArgs[0]="\""+minesweeperclasspath.toAbsolutePath().toString()+"\"";
                    newArgs[1]="\""+tempPath.toString()+"\"";
                    ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
                    Class<?> newAppClass = systemClassLoader.loadClass("OverwriteJar");
                    Method mainMethod = newAppClass.getMethod("main", String[].class);
                    mainMethod.invoke(null, (Object) newArgs);
                }catch (Exception e){e.printStackTrace();}
            }
        }));*/
        try {
            // Set cross-platform Java L&F (also called "Metal")
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        }
        catch (UnsupportedLookAndFeelException e) {
        }
        catch (ClassNotFoundException e) {
        }
        catch (InstantiationException e) {
        }
        catch (IllegalAccessException e) {
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
