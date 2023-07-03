import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
//This class is to get compiled, and later copied out of the jar to a new directory and 
//loaded on shutdown such that it can overwrite original jar with the new one supplied by ScoresFileIO
class OverwriteJar {
    /**
     * @param args String originalJarPath, String tempJarPath, String[] full_command_plus_new_arguments
     */
        public static void main(String[] args) {
            String originalJarPath = args[0];
            String thisDirectory = args[1];
            try{Thread.sleep(300);
            }catch(InterruptedException e){}
            boolean copySucceeded = true;
            try(InputStream inputStream = new FileInputStream(Paths.get(thisDirectory+File.separator+"minesweeper.jar").toFile())){
                File destinationDir = Paths.get(originalJarPath).toFile();
                destinationDir.mkdirs();
                Files.copy(inputStream, Paths.get(originalJarPath), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {copySucceeded = false;}
            if(copySucceeded)Paths.get(thisDirectory+File.separator+"minesweeper.jar").toFile().delete();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                String pathToProgram = thisDirectory+File.separator+"OverwriteJar.class"; // Specify the path to the program's file
                File programFile = new File(pathToProgram);
                programFile.delete();
            }));
        }
}