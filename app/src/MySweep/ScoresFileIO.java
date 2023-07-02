package MySweep;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

class ScoresFileIO{
    private final String scoresFromMySweep= "scores.txt";
    private final String MySweepFromClassPath = "src/MySweep/";
    private ScoreEntry[] runtimeEntries;//<-- IT NOW NEEDS TO INTERNALLY SAVE STATE BECAUSE THE NEW SAVES ARE IN THE OTHER JAR. THIS IS BAD.
    public ScoresFileIO(){}//<-- CONSTRUCTOR
    //----------------------------------WRITE------------------------------------------------------WRITE----------------------------------
    private void writeLeaderboard(ScoreEntry[] allEntries, boolean append){//writes from Score Entries to file or jar
        if(append){
            ArrayList<ScoreEntry> newRuntimeEntries = new ArrayList<>();
            newRuntimeEntries.addAll(Arrays.asList(runtimeEntries));
            for(int i = 0; i<allEntries.length; i++){
                newRuntimeEntries.add(allEntries[i]);
            }
            runtimeEntries=newRuntimeEntries.toArray(new ScoreEntry[0]);
        } else {runtimeEntries=allEntries;}
        if(MineSweeper.isJarFile()){//-------------------------------------------------------IN A JAR----------------------------
            StringBuilder jarFileScoresStringBuilder = new StringBuilder();// create string from entries
            for(int i = 0; i < runtimeEntries.length; i++){
                jarFileScoresStringBuilder.append(runtimeEntries[i].toString()).append(" ");
            }
            extractJar(MineSweeper.getClassPath().toString(), MineSweeper.getTempJarPath().toString());
            try{
            copyManifestToDirectory(MineSweeper.getClassPath().toString(),MineSweeper.getTempJarPath().toString());
            }catch(IOException e){e.printStackTrace();}
            try (FileWriter scoreWriter = new FileWriter(Paths.get(MineSweeper.getTempJarPath().toString()+"/"+MySweepFromClassPath+scoresFromMySweep).toFile())){
                scoreWriter.write(jarFileScoresStringBuilder.toString());//<-- overwrite the file with new contents.
            }catch(IOException e){
                System.out.println(e.getClass()+" @ "+Paths.get(MineSweeper.getTempJarPath().toString()+"/"+MySweepFromClassPath+scoresFromMySweep).toString());
                e.printStackTrace();
            }
            File loaderFiles = MineSweeper.getTempPath().toFile();
            loaderFiles.mkdirs();
            createJar(MineSweeper.getTempJarPath().toString(), MineSweeper.getTempPath().toString()+File.separator+"minesweeper.jar");
            try{
                removeDirectory(MineSweeper.getTempJarPath().toString());
            }catch(IOException e){e.printStackTrace();}
        }else{//---------------------------------------------------------NOT IN A JAR---------------------------
            File scoresFile = new File(getClass().getResource(scoresFromMySweep).getPath().toString());
            StringBuilder scoresFileString = new StringBuilder();// create string from entries
            if(append)scoresFileString.append(" ");
            for(int i = 0; i < allEntries.length; i++){
              scoresFileString.append(allEntries[i].toString()).append(" ");
            }
            try (FileWriter out2 = new FileWriter(scoresFile, append)) {// write string
                out2.write(scoresFileString.toString());//<-- overwrite the file with new contents.
            }catch(IOException e){System.out.println(e.getClass()+" @ "+scoresFile.toPath().toString());e.printStackTrace();}
        }
    }
    //----------------------------------------------HELPERS FOR WRITE----------------------------HELPERS FOR WRITE------------------------------------
    //-------------------------------------------extract and create jar files------------------------------------------------
    private static void extractJar(String jarFile, String outputDirectory) {//extracts jar to a specified directory minus the manifest
        try (JarInputStream jis = new JarInputStream(new FileInputStream(jarFile))) {//start our jar writer
            JarEntry entry;
            while ((entry = jis.getNextJarEntry()) != null) {//if there is stuff
                String entryName = entry.getName();

                if (entry.isDirectory()){//if its a directory, call on contents
                    File dir = new File(outputDirectory, entryName);
                    dir.mkdirs();
                } else {//otherwise, write it.
                    File file = new File(outputDirectory, entryName);
                    file.getParentFile().mkdirs();

                    try (OutputStream os = new FileOutputStream(file)) {
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = jis.read(buffer)) != -1) {
                            os.write(buffer, 0, bytesRead);
                        }
                    }
                }
                jis.closeEntry();//yay we're done.
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void copyManifestToDirectory(String jarPath, String outputDirectory) throws IOException {//copies manifest into new jar
        File thisJarFile = new File(jarPath);
        try(JarFile thisJar = new JarFile(thisJarFile)){
            Manifest manifest = thisJar.getManifest();
            if (manifest != null) {
                File destinationDir = new File(outputDirectory + File.separator + "META-INF");
                destinationDir.mkdirs();
                File manifestFile = new File(destinationDir, "MANIFEST.MF");
                Files.copy(thisJar.getInputStream(thisJar.getEntry("META-INF/MANIFEST.MF")), manifestFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }
    private static void createJar(String inputDirectory, String jarFile) {//creates the jar file to add files to
        try (JarOutputStream jos = new JarOutputStream(new FileOutputStream(jarFile))) {
            File directory = new File(inputDirectory);
            addFilesToJar(directory, directory.getAbsolutePath(), jos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void addFilesToJar(File file, String root, JarOutputStream jos) throws IOException {//adds the files
        if (file.isDirectory()) {//if it is a directory, call addfiles on the children of the directory
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    addFilesToJar(f, root, jos);
                }
            }
        } else {
            String entryName = file.getAbsolutePath().substring(root.length() + 1).replace("\\", "/");//if windows, swap \ to /
            JarEntry jarEntry = new JarEntry(entryName);//create new entry
            jos.putNextEntry(jarEntry);//add new entry
            
            try (InputStream is = new FileInputStream(file)) {// add stuff to new entry
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    jos.write(buffer, 0, bytesRead);
                }
            }
            jos.closeEntry();//close to prepare next item.
        }
    }
    //-------------------------remove a directory----------------------------------------------------
    private static void removeDirectory(String directoryPath) throws IOException {//walk through and remove directory and its contents
        Files.walkFileTree(Paths.get(directoryPath), new SimpleFileVisitor<Path>() {//This is better than recursion
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }
    //---------------------------------------------------------functions used by other windows---------------------------------------
    //-----------------------------------READ-------------------------------------READ----------------------READ--------------------
    public ScoreEntry[] readLeaderboard(){ //reads from internal file by word to Score Entries
        if(runtimeEntries==null){
            ArrayList<ScoreEntry> fileEntriesBuilder = new ArrayList<>();
            ScoreEntry[] fileEntries;
            if(MineSweeper.isJarFile()){//----------------------------------------------------------IN A JAR-------------------------------------------
                InputStream inputStream = ClassLoader.getSystemResourceAsStream(MySweepFromClassPath+scoresFromMySweep);
                InputStreamReader streamReader = new InputStreamReader(inputStream);
                BufferedReader in = new BufferedReader(streamReader);
                try{
                    for (String line; (line = in.readLine()) != null;) {
                        String[] words = line.split("\\s+");
                        for(String word : words){
                            ScoreEntry currentEntry = new ScoreEntry(word);//<-- get next word (string separated by whitespace)
                            if(currentEntry.isValid())fileEntriesBuilder.add(currentEntry);//<-- only read out valid scores
                        }
                    }
                    fileEntries = fileEntriesBuilder.toArray(new ScoreEntry[0]);
                }catch(IOException e){e.printStackTrace();fileEntries=null;}
                runtimeEntries = fileEntries;
                return fileEntries;
            }else{//------------------------------------------------------------------NOT IN A JAR------------------------------------------
                try{
                    File scoresFile = new File(getClass().getResource(scoresFromMySweep).getPath().toString());
                    try(Scanner in = new Scanner(scoresFile)) {
                        while (in.hasNext()) {
                            ScoreEntry currentEntry = new ScoreEntry(in.next());//<-- get next word (string separated by whitespace)
                            if(currentEntry.isValid())fileEntriesBuilder.add(currentEntry);//<-- only read out valid scores
                        }
                        fileEntries = fileEntriesBuilder.toArray(new ScoreEntry[0]);
                    }catch(FileNotFoundException e){
                        fileEntries=null; 
                        System.out.println(e.getClass()+" @ "+scoresFile.toPath().toString());
                        e.printStackTrace();
                    }
                    runtimeEntries = fileEntries;
                    return fileEntries;
                }catch(NullPointerException e){e.printStackTrace();return runtimeEntries;}
            }
        }else return runtimeEntries;
    }
    //--------------------------------------------Everything below here uses only ScoreEntries to do its work-----------------------------------
    //-----------------------------Everything below here uses only ScoreEntries to do its work---------------------------------------------------
    public void deleteScoreEntry(ScoreEntry thisEntry){//<-- reads score file, overwrites with the same thing but without specified entry
        ScoreEntry[] deletries = readLeaderboard();// <-- read
        ArrayList<ScoreEntry> newFileBuilder = new ArrayList<>();
        if(deletries!=null){
            int c = 0;
            while(c<deletries.length){
                if(thisEntry.isValid()){//<-- only write back valid entries
                    if(!(deletries[c].equals(thisEntry) && thisEntry.getRemainingLives()==deletries[c].getRemainingLives() && thisEntry.getTime()==deletries[c].getTime())){
                        newFileBuilder.add(deletries[c]);//only add back entries that arent the exact entry in thisEntry (equals() only finds if same board)
                    }
                }
                c++;
            }
            deletries = newFileBuilder.toArray(new ScoreEntry[0]);
            writeLeaderboard(deletries, false);// <-- overwrite with new
        }
    }
    public int updateScoreEntry(boolean won, int time, int cellsExploded, int Fieldx, int Fieldy, int bombCount, int lives){
        //Writes new scores to score file, returns highscore/new_board/normal index for assigning win/loss message
        int RemainingLives= Math.max(0, lives-cellsExploded);
        ScoreEntry thisEntry = new ScoreEntry(Fieldx,Fieldy,bombCount,lives,RemainingLives,time);
        if(thisEntry.isValid()){
            ScoreEntry[] entries = readLeaderboard();
            if(entries == null){//<-- file not found
                entries = new ScoreEntry[1];
                entries[0] = thisEntry;
                writeLeaderboard(entries, false);
                return 1;
            }else{
                if(0==entries.length){//<-- file found but empty. Writing.
                    entries = new ScoreEntry[1];
                    entries[0] = thisEntry;
                    writeLeaderboard(entries, false);
                    return 1;
                }else{//<---------------------- file found and not empty
                    int c = 0;
                    boolean highscore = false;
                    while(c<entries.length){//loop through entries in file
                        if(entries[c].isValid() && entries[c].equals(thisEntry)){//<-- board identifier matches
                            if(won && entries[c].getTime()>time){
                                entries[c]=thisEntry;//                         ^did you beat the time?
                                highscore=true;
                            }else if(won && entries[c].getRemainingLives()>RemainingLives && entries[c].getTime()==time){
                                entries[c]=thisEntry;//                         ^is it same time but more lives?
                                highscore=true;
                            }else if(won && entries[c].getRemainingLives()<1){//was the entry created by dying on a new board configuration?
                                entries[c]=thisEntry;
                                highscore=true;
                            }
                            break;
                        }
                        c++;
                    }
                    if(c==entries.length && highscore == false){//none were a match. New Board Size
                                            //   ^i dont actually need to check for high score here, but it cant hurt?
                        ScoreEntry[] newEntries = new ScoreEntry[1];
                        newEntries[0] = thisEntry;
                        writeLeaderboard(newEntries, true);
                        return 1;
                    }
                    if(highscore){//Was a high score! save edited version of file
                        writeLeaderboard(entries, false);
                        return 2;
                    }
                }
            }
        }
        return 0;//<-- return index for setting display label
    }
}