package game.firstlvl;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.jetbrains.annotations.Nullable;
/**
 * Created by Snach on 02.04.16.
 */
public class ShuffleWordService {

    private  Map<Long, String> words = new HashMap<>();
    private final long maxWordId;



    public ShuffleWordService(@Nullable Map<Long, String> map, long maxId){
        this.words = map;
        this.maxWordId = maxId;
    }

    public ShuffleWord getShuffleWord(){

        final long randId = 1 + (long)(Math.random() * ((maxWordId - 1) + 1));
        final String word = words.get(randId);
        String shuffleWord;
        do {
            shuffleWord = shuffleString(word);
        } while (word.equals(shuffleWord));

        return new ShuffleWord(randId,shuffleWord);
    }

    public String shuffleString(String word){
        final char [] wordInChar = word.toCharArray ();
        final Random rnd = ThreadLocalRandom.current();
        for (int i = wordInChar.length - 1; i > 0; i--) {
            final int index = rnd.nextInt(i + 1);
            final char a = wordInChar[index];
            wordInChar[index] = wordInChar[i];
            wordInChar[i] = a;
        }

        return new String(wordInChar);
    }

    public ShuffleWord getWordById(long id) {
        return new ShuffleWord(id,words.get(id));
    }
}
