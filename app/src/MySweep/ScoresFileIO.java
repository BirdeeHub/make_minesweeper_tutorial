package MySweep;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;

class ScoresFileIO{
    private final String scoresFileNameWindows = System.getProperty("user.home") + File.separator + "AppData" + File.separator + "Roaming" + File.separator + "minesweeperScores" + File.separator + "Leaderboard.txt";
    private final String scoresFileNameOther = System.getProperty("user.home") + File.separator + ".minesweeper" + File.separator + "Leaderboard.txt";
    private final String scoresFileName;
    public ScoresFileIO(){
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            scoresFileName = scoresFileNameWindows;
        } else {
            scoresFileName = scoresFileNameOther;
        }
    }
    //----------------------------------WRITE------------------------------------------------------WRITE-------
    private void writeLeaderboard(ScoreEntry[] allEntries, boolean append){//writes from Score Entries to file
        StringBuilder scoresFileString = new StringBuilder();// create string
        if(append)scoresFileString.append(" ");
        for(int i = 0; i < allEntries.length; i++){
            scoresFileString.append(allEntries[i].toString()).append(" ");
        }
        try {//-----write string-------------
            Files.createDirectories(Path.of(scoresFileName).getParent()); //<-- Create the directory.
        } catch (IOException e) {System.out.println(e.getClass()+" @ "+scoresFileName);}
        try{
            Files.createFile(Path.of(scoresFileName));//<-- Create the file if not created.
        }catch(IOException e){if(!(e instanceof FileAlreadyExistsException))System.out.println(e.getClass()+" @ "+scoresFileName);}
        try (FileWriter out2 = new FileWriter(scoresFileName, append)) {
            out2.write(scoresFileString.toString());//<-- overwrite the file with new contents.
        }catch(IOException e){System.out.println(e.getClass()+" @ "+scoresFileName);}
    }
    //-----------------------------------READ-------------------------------------READ---
    public ScoreEntry[] readLeaderboard(){ //reads from file by word to Score Entries
        ArrayList<ScoreEntry> tFile = new ArrayList<>();
        ScoreEntry[] entries;
        try(Scanner in = new Scanner(new File(scoresFileName))) {
            while (in.hasNext()) {
                ScoreEntry currentEntry = new ScoreEntry(in.next());//<-- get next word (string separated by whitespace)
                if(currentEntry.isValid())tFile.add(currentEntry);//<-- only read out valid scores
            }
            entries = tFile.toArray(new ScoreEntry[0]);
        }catch(FileNotFoundException e){
            entries=null; 
            System.out.println(e.getClass()+" @ "+scoresFileName);
        }
        return entries;
    }
    //--------------------------------------------Everything below here uses only ScoreEntries to do its work-----------------------------------
    //-----------------------------Everything below here uses only ScoreEntries to do its work---------------------------------------------------
    public void deleteScoreEntry(ScoreEntry thisEntry){//<-- reads score file, overwrites with the same thing but without specified entry
        ScoreEntry[] entries = readLeaderboard();// <-- read
        ArrayList<ScoreEntry> newFile = new ArrayList<>();
        if(entries!=null){
            int c = 0;
            while(c<entries.length){
                if(thisEntry.isValid()){//<-- only write back valid entries
                    if(!(entries[c].equals(thisEntry) && thisEntry.getRemainingLives()==entries[c].getRemainingLives() && thisEntry.getTime()==entries[c].getTime())){
                        newFile.add(entries[c]);//only add back entries that arent the exact entry in thisEntry (equals() only finds if same board)
                    }
                }
                c++;
            }
            entries = newFile.toArray(new ScoreEntry[0]);
            writeLeaderboard(entries, false);// <-- overwrite with new
        }
    }
    public int updateScoreEntry(boolean won, int time, int cellsExploded, int Fieldx, int Fieldy, int bombCount, int lives){
        //Writes new scores to score file, returns highscore/new_board/normal index for assigning win/loss message
        int RemainingLives= Math.max(0, lives-cellsExploded);
        boolean highscore = false;
        boolean newBoardSize = false;
        boolean fileFound = true;
        ScoreEntry thisEntry = new ScoreEntry(Fieldx,Fieldy,bombCount,lives,RemainingLives,time);
        if(thisEntry.isValid()){
            ScoreEntry[] entries = readLeaderboard();
            if(entries == null){//<-- file not found
                fileFound = false;
                entries = new ScoreEntry[1];
                entries[0] = thisEntry;
                writeLeaderboard(entries, false);
            }else{
                if(0==entries.length){//<-- file found but empty. Writing.
                    fileFound=false;
                    entries = new ScoreEntry[1];
                    entries[0] = thisEntry;
                    writeLeaderboard(entries, false);
                }else{//<---------------------- file found and not empty
                    int c = 0;
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
                        newBoardSize=true;//   ^i dont actually need to check for high score here, but it cant hurt?
                        ScoreEntry[] newEntries = new ScoreEntry[1];
                        newEntries[0] = thisEntry;
                        writeLeaderboard(newEntries, true);
                    }
                    if(highscore){//Was a high score! save edited version of file
                        writeLeaderboard(entries, false);
                    }
                }
            }
        }
        return (highscore)?2:((newBoardSize || !fileFound)?1:0);//<-- return index for setting display label
    }
}