package game.firstlvl;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Snach on 07.04.16.
 */
public class ShuffleWord {

    private final long id;

    @NotNull
    private final String word;

    public ShuffleWord(long id, @NotNull String word) {
        this.id = id;
        this.word = word;
    }

    public long getId() {
        return id;
    }

    @NotNull
    public String getWord() {
        return word;
    }

}
