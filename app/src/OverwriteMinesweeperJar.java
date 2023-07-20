import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
//This class is to get compiled, and later copied out of the jar to a new directory and 
//loaded on shutdown such that it can overwrite original jar with a new one with a new scores file
//do not add any internal or anonymous classes or it will compile into more than 1 file, and that file will not be copied.
class OverwriteMinesweeperJar {
    /**
     * @param args String originalJarPath, String tempJarPath, String scoresEntryName, String thisClassName
     */
    public static void main(String[] args) {
        String originalJarPath = args[0];
        String thisDirectory = args[1];
        String scoresEntryName = args[2];
        String thisClassName = args[3];
        String scoresFileContent = null;
        File scoresFile = Path.of(thisDirectory + File.separator + Path.of(scoresEntryName).getFileName()).toFile();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Path.of(thisDirectory+File.separator+thisClassName+".class").toFile().delete();
        }));
        try(Scanner in = new Scanner(scoresFile)) {
            StringBuilder scoresFileStringBuilder = new StringBuilder();
            while (in.hasNextLine()) {
                scoresFileStringBuilder.append(in.nextLine());
                if(in.hasNextLine())scoresFileStringBuilder.append('\n');
            }
            scoresFileContent=scoresFileStringBuilder.toString();
            boolean copySucceeded = true;
            try{
                writeJarWithNewScores(originalJarPath, scoresEntryName, scoresFileContent);
            }catch(IOException e){e.printStackTrace();copySucceeded = false;}
            if(copySucceeded)scoresFile.delete();
        }catch(FileNotFoundException e){}
    }
    private static void writeJarWithNewScores(String jarFilePath, String scoresEntryName, String newScoresFileContents) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (JarInputStream jis = new JarInputStream(new FileInputStream(jarFilePath));
            JarOutputStream jos = new JarOutputStream(baos, getManifest(jarFilePath))) {
            JarEntry entry;
            while ((entry = jis.getNextJarEntry()) != null) {
                if (!newScoresFileContents.equals(null)&&entry.getName().equals(scoresEntryName)) {
                    JarEntry scoresEntry = new JarEntry(scoresEntryName);
                    jos.putNextEntry(scoresEntry);
                    byte[] scoresContentBytes = newScoresFileContents.getBytes();
                    jos.write(scoresContentBytes);
                } else {
                    jos.putNextEntry(entry);
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = jis.read(buffer)) != -1) {
                        jos.write(buffer, 0, bytesRead);
                    }
                }
                jis.closeEntry();
                jos.closeEntry();
            }
        }
        try (OutputStream outputStream = new FileOutputStream(jarFilePath)) {
            baos.writeTo(outputStream);
        }
    }
    private static Manifest getManifest(String jarFile){
        try(JarFile thisJar = new JarFile(Path.of(jarFile).toFile())){
            return thisJar.getManifest();
        }catch (IOException e){return null;}
    }
}