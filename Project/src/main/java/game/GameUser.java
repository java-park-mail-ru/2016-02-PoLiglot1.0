package game;

import game.firstlvl.ShuffleWord;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Snach on 19.04.16.
 */
public class GameUser {
    private static final int POINT = 10;
    @NotNull
    private final String myName;
    @NotNull
    private String enemyName;
    private int myScore = 0;
    private int enemyScore = 0;
    private final Map<Long,String> usedWords= new HashMap<>();

    public GameUser(@NotNull String myName, @NotNull String enemyName) {

        this.myName = myName;
        this.enemyName = enemyName;
    }

    @NotNull
    public String getMyName() {
        return myName;
    }
    @NotNull
    public String getEnemyName() {
        return enemyName;
    }

    public int getMyScore() {
        return myScore;
    }

    @SuppressWarnings("unused")
    public int getEnemyScore() {
        return enemyScore;
    }

    public void incrementMyScore() {
        myScore += POINT;
    }

    public void incrementEnemyScore() {
        enemyScore += POINT;
    }

    @SuppressWarnings("unused")
    public void setEnemyName(@NotNull String enemyName) {
        this.enemyName = enemyName;
    }

    public boolean isWordUsed(Long wordId){
        return usedWords.containsKey(wordId);
    }

    public void addUsedWord(ShuffleWord word){
        usedWords.put(word.getId(),word.getWord());
    }
}
