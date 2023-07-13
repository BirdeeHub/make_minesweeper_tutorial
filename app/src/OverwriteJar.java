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
import java.util.zip.ZipException;
//This class is to get compiled, and later copied out of the jar to a new directory and 
//loaded on shutdown such that it can overwrite original jar with the new one supplied by ScoresFileIO
class OverwriteJar {
    /**
     * @param args String originalJarPath, String tempJarPath, String scoresEntryName
     */
    public static void main(String[] args) {
        String thisDirectory = args[1];
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            String pathToProgram = thisDirectory+File.separator+"OverwriteJar.class"; // Specify the path to the program's file
            File programFile = new File(pathToProgram);
            programFile.delete();
        }));
        String originalJarPath = args[0];
        String scoresEntryName = args[2];
        String scoresFileContent = null;
        try(Scanner in = new Scanner(Path.of(thisDirectory + File.separator + Path.of(scoresEntryName).getFileName()).toFile())) {
            StringBuilder scoresFileStringBuilder = new StringBuilder();
            while (in.hasNext()) {
                scoresFileStringBuilder.append(in.next()).append(" ");
            }
            scoresFileContent=scoresFileStringBuilder.toString();
        }catch(FileNotFoundException e){}
        doOverwrite(originalJarPath, scoresEntryName, thisDirectory, scoresFileContent);
    }

    private static void doOverwrite(String jarFilePath, String scoreEntryName, String scoresFileDirectory, String newScoresFileContents){
        boolean copySucceeded = true;
        try{
            writeJarWithNewScores(getManifest(jarFilePath), jarFilePath, scoreEntryName, scoresFileDirectory, newScoresFileContents);
        }catch(IOException e){e.printStackTrace();copySucceeded = false;}
        if(copySucceeded){
            Path.of(scoresFileDirectory+File.separator+Path.of(scoreEntryName).getFileName()).toFile().delete();
        }
    }
    private static Manifest getManifest(String jarFile){
        try(JarFile thisJar = new JarFile(Path.of(jarFile).toFile())){
            return thisJar.getManifest();
        }catch (IOException e){return null;}
    }
    private static void writeJarWithNewScores(Manifest jarManifest, String jarFilePath, String scoreEntryName, String scoresFileDirectory, String newScoresFileContents) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (JarInputStream jis = new JarInputStream(new FileInputStream(jarFilePath));
            JarOutputStream jos = new JarOutputStream(baos)) {

            JarEntry entry;
            while ((entry = jis.getNextJarEntry()) != null) {
                if (!newScoresFileContents.equals(null)&&entry.getName().equals(scoreEntryName)) {
                    JarEntry scoresEntry = new JarEntry(scoreEntryName);
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

            if (jarManifest != null) {
                try{
                    JarEntry manifestEntry = new JarEntry(JarFile.MANIFEST_NAME);
                    jos.putNextEntry(manifestEntry);
                    jarManifest.write(jos);
                }catch(ZipException e){}
            }
        }

        try (OutputStream outputStream = new FileOutputStream(jarFilePath)) {
            baos.writeTo(outputStream);
        }
    }
}