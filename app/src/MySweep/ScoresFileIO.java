package MySweep;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.util.Arrays;

class ScoresFileIO{
    //----------------------------------WRITE------------------------------------------------------WRITE----------------------------------
    private static void writeLeaderboard(ScoreEntry[] allEntries){//writes from Score Entries to file or jar
        StringBuilder scoresFileString = new StringBuilder();// StringBuilder to store and create string from entries
        if(MineSweeper.isJarFile()){//-------------------------------------------------------IN A JAR----------------------------
            for(int i = 0; i < allEntries.length; i++){//<-- for all the entries
                scoresFileString.append(allEntries[i].toString()).append(" ");//<-- string builders have a good append function. arrays dont.
            }
            try {
                Files.createDirectories(MineSweeper.tempPath); //<-- Create the directory.
            } catch (IOException e) {e.printStackTrace();}
            try{
                Files.createFile(MineSweeper.tempPath.resolve(MineSweeper.scoresFileName));//<-- Create the file if not created.
            }catch(IOException e){
                if(!(e instanceof FileAlreadyExistsException))e.printStackTrace();
            }
            try (FileWriter out2 = new FileWriter(MineSweeper.tempPath.resolve(MineSweeper.scoresFileName).toFile())) {//<-- filewriters can overwrite or append a string to a file
                out2.write(scoresFileString.toString());//<-- overwrite the file with new contents, or append as specified.
            }catch(IOException e){e.printStackTrace();}
        }else{//------------------------------------this exists for IDEs------------NOT IN A JAR---------------------------
            for(int i = 0; i < allEntries.length; i++){
                scoresFileString.append(allEntries[i].toString()).append(" ");
            }
            try (FileWriter out2 = new FileWriter(MineSweeper.class.getResource(MineSweeper.scoresPathForIDE).getFile())) {// write string
                out2.write(scoresFileString.toString());//<-- overwrite the file with new contents.
            }catch(IOException e){e.printStackTrace();}
        }
    }
    //----------------------------functions used by other windows---------------------------------------functions used by other windows-------------
    //---------------------------------------------------------------functions used by other windows------------------------------------------------
    //-----------------------------------READ-------------------------------------READ----------------------READ---------READ-----------------------
    public static ScoreEntry[] readLeaderboard(){ //reads from internal file by word to Score Entries
        ArrayList<ScoreEntry> fileEntriesBuilder = new ArrayList<>();
        ScoreEntry[] fileEntries=null;
        if(MineSweeper.isJarFile()){//----------------------------------------------------------IN A JAR--------------------------------------------
            if(MineSweeper.tempPath.resolve(MineSweeper.scoresFileName).toFile().exists()){
                try{
                    try(Scanner in = new Scanner(MineSweeper.tempPath.resolve(MineSweeper.scoresFileName).toFile())) {
                        while (in.hasNext()) {
                            ScoreEntry currentEntry = new ScoreEntry(in.next());//<-- get next word (string separated by whitespace)
                            if(currentEntry.isValid())fileEntriesBuilder.add(currentEntry);//<-- only read out valid scores
                        }
                        fileEntries = fileEntriesBuilder.toArray(new ScoreEntry[0]);
                    }catch(FileNotFoundException e){e.printStackTrace();}
                }catch(NullPointerException e){e.printStackTrace();}
            }else{
                BufferedReader in = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream(MineSweeper.scoresEntryName)));
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
        }else{//-------------------------------------------this exists for IDEs-------NOT IN A JAR------------------------------------------
            try(Scanner in = new Scanner(MineSweeper.class.getResource(MineSweeper.scoresPathForIDE).getFile())) {
                while (in.hasNext()) {
                    ScoreEntry currentEntry = new ScoreEntry(in.next());//<-- get next word (string separated by whitespace)
                    if(currentEntry.isValid())fileEntriesBuilder.add(currentEntry);//<-- only read out valid scores
                }
                fileEntries = fileEntriesBuilder.toArray(new ScoreEntry[0]);
            }catch(NullPointerException e){e.printStackTrace();}
        }
        return fileEntries;
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