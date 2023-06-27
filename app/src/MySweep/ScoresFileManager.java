package MySweep;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;

class ScoresFileManager{
    private final String scoresFileNameWindows = System.getProperty("user.home") + File.separator + "AppData" + File.separator + "Roaming" + File.separator + "minesweeperScores" + File.separator + "Leaderboard.txt";
    private final String scoresFileNameOther = System.getProperty("user.home") + File.separator + ".minesweeper" + File.separator + "Leaderboard.txt";
    private final String scoresFileName;
    public ScoresFileManager(){
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            scoresFileName = scoresFileNameWindows;
        } else {
            scoresFileName = scoresFileNameOther;
        }
    }
    public String[] readLeaderboard(){ //reads leaderboard to string array for processing
        String[] entryWords = null;
        try(Scanner in = new Scanner(new File(scoresFileName))) {
            StringBuilder tFile = new StringBuilder();
            while (in.hasNext()) {
                tFile.append(in.next()).append(" ");
            }
            entryWords = tFile.toString().split("\\s+");
        }catch(FileNotFoundException e){e.printStackTrace();}
        return entryWords;
    }
    public void deleteScoreEntry(String BoardEntry){//<-- reads score file, overwrites with the same thing but without specified entry
        String[] entryWords = readLeaderboard();
        StringBuilder newFile = new StringBuilder();
        if(entryWords!=null && (1<=entryWords.length && !entryWords[0].isEmpty())){
            int c = 0;
            String[] currentSavedScore;
            while(c<entryWords.length){
                currentSavedScore=entryWords[c].split("-");
                if(!currentSavedScore[0].equals(BoardEntry)){
                    newFile.append(entryWords[c]).append(" ");
                }
                c++;
            }
            try (FileWriter out2 = new FileWriter(scoresFileName)) {
                out2.write(newFile.toString());
            }catch(IOException e){e.printStackTrace();}
        }
    }
    public int addScoreEntry(boolean won, String timeString, int cellsExploded, int Fieldx, int Fieldy, int bombCount, int lives){
        //Writes new scores to score file, returns highscore/new board/normal index for assigning win/loss message
        String RemainingLives= String.valueOf(Math.max(0, lives-cellsExploded));
        boolean highscore = false;
        boolean newBoardSize = false;
        String answersString = String.valueOf(Fieldx) + ":" + String.valueOf(Fieldy) + ":" + String.valueOf(bombCount) + ":" + String.valueOf(lives);
        String thisScore = (answersString +"-"+ RemainingLives +"-"+ timeString);  //format:"x:y:bombCount:lives-RemainingLives-time"
        boolean fileFound = true;
        if(Fieldx*Fieldy>bombCount&&bombCount>0&&lives>0&&Fieldx>0&&Fieldy>0){
            String[] entryWords = readLeaderboard();
            if(entryWords == null){//<-- file not found
                fileFound = false;
                try {
                    Files.createDirectories(Path.of(scoresFileName).getParent()); //<-- Create the directory
                    Files.writeString(Path.of(scoresFileName), thisScore + " ", StandardOpenOption.CREATE); //<-- Write the file
                } catch (IOException ex) {ex.printStackTrace();}
            }else{
                if(1>=entryWords.length && entryWords[0].isEmpty()){//<-- file found but empty. Writing.
                    try (FileWriter out1 = new FileWriter(scoresFileName)) {
                        fileFound=false;
                        out1.write(thisScore + " ");
                    } catch (IOException ex) {ex.printStackTrace();}
                }else{//file found and not empty
                    int c = 0;
                    String[] currentSavedScore;
                    while(c<entryWords.length){//loop through entries in file
                        currentSavedScore=entryWords[c].split("-");
                        if(currentSavedScore[0].equals(answersString)){//<-- board identifier matches
                            if(won && (Integer.parseInt(currentSavedScore[2])>Integer.parseInt(timeString))){
                                entryWords[c]=thisScore;//                         ^did you beat the time?
                                highscore=true;
                            }else if(won && (Integer.parseInt(currentSavedScore[1])>Integer.parseInt(RemainingLives)) && (Integer.parseInt(currentSavedScore[2])==Integer.parseInt(timeString))){
                                entryWords[c]=thisScore;//                         ^is it same time but more lives?
                                highscore=true;
                            }else if(won && (Integer.parseInt(currentSavedScore[1])<1)){//was the entry created by dying on a new board configuration?
                                entryWords[c]=thisScore;
                                highscore=true;
                            }
                            break;
                        }
                        c++;
                    }
                    if(c==entryWords.length){//none were a match. New Board Size
                        newBoardSize=true;
                        String[] xUy = new String[entryWords.length+1];
                        int i=0;
                        while(i<entryWords.length){
                            xUy[i]=entryWords[i];
                            i++;
                        }
                        xUy[i]=thisScore;
                        entryWords=xUy;
                    }
                }
                if(highscore || newBoardSize){//save edited version of file
                    StringBuilder fileOutString = new StringBuilder();
                    for(int i=0; i<entryWords.length; i++){
                        fileOutString.append(entryWords[i]).append(" ");
                    }
                    try (FileWriter out2 = new FileWriter(scoresFileName)) {
                        out2.write(fileOutString.toString());
                    }catch(IOException e){e.printStackTrace();}
                }
            }
        }
        return (highscore)?2:((newBoardSize || !fileFound)?1:0);//<-- return index for setting display label
    }
}