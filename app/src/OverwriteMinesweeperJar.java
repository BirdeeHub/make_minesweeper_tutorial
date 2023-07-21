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
class OverwriteMinesweeperJar {
    /**@param args String originalJarPath, String tempJarPath, String scoresEntryName, String thisClassName
     * This class is to get compiled, and later copied out of the jar to a new directory and 
     * loaded on shutdown such that it can overwrite original jar with a new one with a new scores file
     * do not add any internal or anonymous classes or it will compile into more than 1 file, and that file will not be copied.
     */
    public static void main(String[] args) {
        String originalJarPath = args[0];
        String scoresDirectory = args[1];
        String scoresEntryName = args[2];
        String thisClassName = args[3];
        File scoresFile = Path.of(scoresDirectory + File.separator + Path.of(scoresEntryName).getFileName()).toFile();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Path.of(System.getProperty("java.class.path")+File.separator+thisClassName+".class").toFile().delete();
        }));
        boolean copySucceeded = false;
        try(Scanner in = new Scanner(scoresFile)) {
            String scoresFileContent = null;
            StringBuilder scoresFileStringBuilder = new StringBuilder();
            while (in.hasNextLine()) {
                scoresFileStringBuilder.append(in.nextLine());
                if(in.hasNextLine())scoresFileStringBuilder.append('\n');
            }
            scoresFileContent=scoresFileStringBuilder.toString();
            try{
                writeJarWithNewScores(originalJarPath, scoresEntryName, scoresFileContent);
                copySucceeded = true;
            }catch(IOException e){e.printStackTrace();}
        }catch(FileNotFoundException e){e.printStackTrace();}
        if(copySucceeded)scoresFile.delete();//<-- need to delete after closing Scanner otherwise it wont delete
    }
    private static void writeJarWithNewScores(String jarFilePath, String scoresEntryName, String newScoresFileContents) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (JarFile thisJar = new JarFile(Path.of(jarFilePath).toFile());
            JarInputStream jis = new JarInputStream(new FileInputStream(jarFilePath));
            JarOutputStream jos = new JarOutputStream(baos, thisJar.getManifest())) {
            JarEntry entry;
            while ((entry = jis.getNextJarEntry()) != null) {
                if (!newScoresFileContents.equals(null)&&entry.getName().equals(scoresEntryName)) {
                    JarEntry scoresEntry = new JarEntry(scoresEntryName);
                    jos.putNextEntry(scoresEntry);
                    jos.write(newScoresFileContents.getBytes());
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
}