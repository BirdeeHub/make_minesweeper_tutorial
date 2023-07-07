package MySweep;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
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
    private static final String scoresFileName= "scores.txt";
    private static final String scoresFromClassPath = "src/MySweep/"+scoresFileName;
    private static final String jarName = (MineSweeper.isJarFile())?MineSweeper.getClassPath().getFileName().toString():"";
    public ScoresFileIO(){}//<-- CONSTRUCTOR
    //----------------------------------WRITE------------------------------------------------------WRITE----------------------------------
    private static void writeLeaderboard(ScoreEntry[] allEntries){//writes from Score Entries to file or jar
        if(MineSweeper.isJarFile()){//-------------------------------------------------------IN A JAR----------------------------
            StringBuilder jarFileScoresStringBuilder = new StringBuilder();// create string from entries
            for(int i = 0; i < allEntries.length; i++){
                jarFileScoresStringBuilder.append(allEntries[i].toString()).append(" ");
            }
            extractJar(MineSweeper.getClassPath().toString(), MineSweeper.getTempJarPath().toString());
            try{
                copyManifestToDirectory(MineSweeper.getClassPath().toString(),MineSweeper.getTempJarPath().toString());
            }catch(IOException e){e.printStackTrace();}
            try (FileWriter scoreWriter = new FileWriter(Paths.get(MineSweeper.getTempJarPath().toString()+"/"+scoresFromClassPath).toFile())){
                scoreWriter.write(jarFileScoresStringBuilder.toString());//<-- overwrite the file with new contents.
            }catch(IOException e){
                System.out.println(e.getClass()+" @ "+Paths.get(MineSweeper.getTempJarPath().toString()+"/"+scoresFromClassPath).toString());
                e.printStackTrace();
            }
            File loaderFiles = MineSweeper.getTempPath().toFile();
            loaderFiles.mkdirs(); 
            createJar(MineSweeper.getTempJarPath().toString(), MineSweeper.getTempPath().toString()+File.separator+jarName);
            try{
                removeDirectory(MineSweeper.getTempJarPath().toString());
            }catch(IOException e){e.printStackTrace();}
        }else{//------------------------------------this exists for IDEs------------NOT IN A JAR---------------------------
            File scoresFile = new File(MineSweeper.class.getResource(scoresFileName).getPath().toString());
            StringBuilder scoresFileString = new StringBuilder();// create string from entries
            for(int i = 0; i < allEntries.length; i++){
              scoresFileString.append(allEntries[i].toString()).append(" ");
            }
            try (FileWriter out2 = new FileWriter(scoresFile)) {// write string
                out2.write(scoresFileString.toString());//<-- overwrite the file with new contents.
            }catch(IOException e){System.out.println(e.getClass()+" @ "+scoresFile.toPath().toString());e.printStackTrace();}
        }
    }
    //--------------------JAR STUFF------------HELPERS FOR WRITE----------------------------HELPERS FOR WRITE------------------------------------
    //--------------------JAR STUFF--------------extract and create jar files------------------------------------------------
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
        } catch (IOException e) {e.printStackTrace();}
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
    }//----------------------------------Helper for READ-------------------------Helper for READ---------------------------------------------------
    private static String extractAJarElementToString(String jarFile, String elementName) {
        try (JarInputStream jis = new JarInputStream(new FileInputStream(jarFile))) {
            JarEntry entry;
            while ((entry = jis.getNextJarEntry()) != null) {
                String entryName = entry.getName();
                if (!entry.isDirectory() && entryName.equals(elementName)) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = jis.read(buffer)) != -1) {//same as extract but to a ByteArrayOutputStream and a string instead of file
                        baos.write(buffer, 0, bytesRead);
                    }
                    baos.close();
                    return baos.toString(); // Convert the byte array to a string
                }
                jis.closeEntry();
            }
        } catch (IOException e) {e.printStackTrace();}
        return null; // If the element is not found or an error occurs
    }
    //----------------------------functions used by other windows---------------------------------------functions used by other windows-------------
    //---------------------------------------------------------------functions used by other windows---------------------------------------
    //-----------------------------------READ-------------------------------------READ----------------------READ---------READ--------------------
    public static ScoreEntry[] readLeaderboard(){ //reads from internal file by word to Score Entries
        ArrayList<ScoreEntry> fileEntriesBuilder = new ArrayList<>();
        ScoreEntry[] fileEntries=null;
        if(MineSweeper.isJarFile()){//----------------------------------------------------------IN A JAR-------------------------------------------
            if(Path.of(MineSweeper.getTempPath().toString()+File.separator+jarName).toFile().exists()){
                String fileContents = extractAJarElementToString(MineSweeper.getTempPath().toString()+File.separator+jarName, scoresFromClassPath);
                if(fileContents!=null){
                    String[] words = fileContents.split("\\s+");
                    for(String word : words){
                        ScoreEntry currentEntry = new ScoreEntry(word);
                        if(currentEntry.isValid())fileEntriesBuilder.add(currentEntry);//<-- only read out valid scores
                    }
                    fileEntries = fileEntriesBuilder.toArray(new ScoreEntry[0]);
                }
            }else{
                InputStream inputStream = ClassLoader.getSystemResourceAsStream(scoresFromClassPath);
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
                }catch(IOException e){e.printStackTrace();}
            }
            return fileEntries;
        }else{//-------------------------------------------this exists for IDEs-------NOT IN A JAR------------------------------------------
            try{
                File scoresFile = new File(MineSweeper.class.getResource(scoresFileName).getPath().toString());
                try(Scanner in = new Scanner(scoresFile)) {
                    while (in.hasNext()) {
                        ScoreEntry currentEntry = new ScoreEntry(in.next());//<-- get next word (string separated by whitespace)
                        if(currentEntry.isValid())fileEntriesBuilder.add(currentEntry);//<-- only read out valid scores
                    }
                    fileEntries = fileEntriesBuilder.toArray(new ScoreEntry[0]);
                }catch(FileNotFoundException e){
                    System.out.println(e.getClass()+" @ "+scoresFile.toPath().toString());
                    e.printStackTrace();
                }
            }catch(NullPointerException e){e.printStackTrace();}
            return fileEntries;
        }
    }
    //--------------------------------------------Everything below here uses only ScoreEntries to do its work-----------------------------------
    //-----------------------------Everything below here uses only ScoreEntries to do its work---------------------------------------------------
    public static void deleteScoreEntry(ScoreEntry thisEntry){//<-- reads score file, overwrites with the same thing but without specified entry
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
            writeLeaderboard(deletries);// <-- overwrite with new
        }
    }
    public static int updateScoreEntry(boolean won, long time, int cellsExploded, int Fieldx, int Fieldy, int bombCount, int lives){
        //Writes new scores to score file, returns highscore/new_board/normal index for assigning win/loss message
        int RemainingLives= Math.max(0, lives-cellsExploded);
        ScoreEntry thisEntry = new ScoreEntry(Fieldx,Fieldy,bombCount,lives,RemainingLives,time);
        if(thisEntry.isValid()){
            ScoreEntry[] entries = readLeaderboard();
            if(entries == null){//<-- file not found
                entries = new ScoreEntry[1];
                entries[0] = thisEntry;
                writeLeaderboard(entries);
                return 1;
            }else{
                if(0==entries.length){//<-- file found but empty. Writing.
                    entries = new ScoreEntry[1];
                    entries[0] = thisEntry;
                    writeLeaderboard(entries);
                    return 1;
                }else{//<---------------------- file found and not empty
                    boolean thisScoreFound=false;
                    boolean isHighscore=false;
                    for(int c = 0;c<entries.length;c++){//loop through entries in file
                        if(entries[c].isValid() && entries[c].equals(thisEntry)){//<-- board identifier matches
                            thisScoreFound=true;
                            if(won && entries[c].getTime()>time){
                                entries[c]=thisEntry;//                         ^did you beat the time?
                                isHighscore=true;
                            }else if(won && entries[c].getRemainingLives()>RemainingLives && entries[c].getTime()==time){
                                entries[c]=thisEntry;//                         ^is it same time but more lives?
                                isHighscore=true;
                            }else if(won && entries[c].getRemainingLives()<1){//was the entry created by dying on a new board configuration?
                                entries[c]=thisEntry;
                                isHighscore=true;
                            }
                        }
                    }
                    if(!thisScoreFound){//none were a match. New Board Size
                        ArrayList<ScoreEntry> newEntriesBuilder = new ArrayList<>();
                        newEntriesBuilder.addAll(Arrays.asList(entries));
                        newEntriesBuilder.add(thisEntry);
                        entries=newEntriesBuilder.toArray(new ScoreEntry[0]);
                        writeLeaderboard(entries);
                        return 1;
                    }
                    if(isHighscore){//Was a high score! save edited version of file
                        writeLeaderboard(entries);
                        return 2;
                    }
                }
            }
        }
        return 0;//<-- return index for setting display label
    }
}