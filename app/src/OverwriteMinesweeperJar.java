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
    /**@param args long parentProcessId, String scoresEntryName, String originalJarPath, String scoresFile, String thisFile
     * 
     * This class is to get compiled, and later copied out of the jar to a new directory and loaded
     * loaded on startup, and it will then overwrite the old jar with the new jar when the old jar exits.
     * do not add any internal classes or it will compile into more than 1 file, and the extra files will not be copied.
     * 
     * If you open 1 jar many times, there will be no issue, as it will fail to overwrite, and thus not delete anything, and will do the overwrite when the last game open exits
     * 
     * If you open 2 different versions of the jar, 
     *  - the first jar to be closed will recieve new scores from both files, and the original scores from the first jar that was opened.
     *  - the second jar to be closed will keep its original scores, but it will not recieve any new scores.
     *  - If you didnt update the scores file, either by new highscore, or delete, nothing will happen.
     */
    public static void main(String[] args) {
        long parentProcessId = Long.parseLong(args[0]);
        String scoresEntryName = args[1];
        String originalJarPath = args[2];
        File thisFile = new File(args[3]);
        File scoresFile = new File(args[4]);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            thisFile.delete();//<-- will fail if still running
        }));
        Thread processMonitoringThread = new Thread(() -> {//<-- lambda functions will not create a new file, because they are not anonymous classes, but rather, anonymous functions.
            while (true) {
                try {// Check if the parent process is alive
                    if (!ProcessHandle.of(parentProcessId).isPresent()) {//<-- if not
                        boolean copySucceeded = false;
                        try(Scanner in = new Scanner(scoresFile)){//<-- if no scores file, this will throw an Exception
                            StringBuilder scoresFileStringBuilder = new StringBuilder();
                            while(in.hasNextLine())scoresFileStringBuilder.append(in.nextLine()+((in.hasNextLine())?'\n':""));
                            try{
                                writeJarWithNewScores(originalJarPath, scoresEntryName, scoresFileStringBuilder.toString());//<-- will fail if still running
                                copySucceeded = true;
                            }catch(IOException e){
                                System.out.println(e.getStackTrace().toString()+"\nUnable to overwrite "+originalJarPath+" with new scores!\nPlease move your game folder to a regular, user-writeable directory then open and close it to complete save.\n(because this game does not have admin privileges)\nIf you still have another version of minesweeper running, you can ignore this message.");
                            }
                        }catch(FileNotFoundException e){}
                        if(copySucceeded)scoresFile.delete();//<-- need to delete after closing Scanner otherwise it wont delete
                        break;
                    }
                    Thread.sleep(1500);//<-- Sleep for a short interval before checking again
                } catch (InterruptedException e) {e.printStackTrace();}
            }
        });
        processMonitoringThread.start();
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