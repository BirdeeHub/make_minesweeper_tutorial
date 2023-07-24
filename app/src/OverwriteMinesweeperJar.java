import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Scanner;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
class OverwriteMinesweeperJar {
    /**@param args String scoresEntryName, String originalJarPath, String scoresFile, String thisFile
     * This class is to get compiled, and later copied out of the jar to a new directory and 
     * loaded on shutdown such that it can overwrite original jar with a new one with a new scores file
     * do not add any internal or anonymous classes or it will compile into more than 1 file, and the extra files will not be copied.
     */
    public static void main(String[] args) {
        String scoresEntryName = args[0];
        String originalJarPath = args[1];
        File thisFile = new File(args[2]);
        File scoresFile = new File(args[3]);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println((thisFile.delete())?thisFile.toString()+" failed to delete!\n":"");//<-- .delete() deletes the file, and then returns a boolean based on success.
        }));
        boolean copySucceeded = false;
        try(Scanner in = new Scanner(scoresFile)){
            StringBuilder scoresFileStringBuilder = new StringBuilder();
            while(in.hasNextLine())scoresFileStringBuilder.append(in.nextLine()+((in.hasNextLine())?'\n':""));
            try{
                writeJarWithNewScores(originalJarPath, scoresEntryName, scoresFileStringBuilder.toString());
                copySucceeded = true;
            }catch(IOException e){System.out.println(e.getStackTrace().toString()+"\nUnable to overwrite "+originalJarPath+" with new scores!\nPlease move your game folder to a regular, user-writeable directory then open and close it to complete save.\n(because this game does not have admin privileges)");}
        }catch(FileNotFoundException e){}
        if(copySucceeded)scoresFile.delete();//<-- need to delete after closing Scanner otherwise it wont delete
    }
    private static void writeJarWithNewScores(String jarFilePath, String scoresEntryName, String newScoresFileContents) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (JarInputStream jis = new JarInputStream(new FileInputStream(jarFilePath));
            JarOutputStream jos = new JarOutputStream(baos, jis.getManifest())){
            JarEntry entry;
            while((entry = jis.getNextJarEntry()) != null){
                if(!newScoresFileContents.equals(null)&&entry.getName().equals(scoresEntryName)){
                    jos.putNextEntry(new JarEntry(scoresEntryName));
                    jos.write(newScoresFileContents.getBytes());
                }else{
                    jos.putNextEntry(entry);
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = jis.read(buffer)) != -1){
                        jos.write(buffer, 0, bytesRead);
                    }
                }
                jis.closeEntry();
                jos.closeEntry();
            }
        }
        try (OutputStream outputStream = new FileOutputStream(jarFilePath)){baos.writeTo(outputStream);}
    }
}