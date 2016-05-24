package game.firstlvl;

import main.cnf.ReaderXMLData;
import org.junit.Before;
import org.junit.Test;


import static org.junit.Assert.*;

/**
 * Created by Snach on 21.05.16.
 */
public class ShuffleWordServiceTest {

    private ShuffleWordService shuffleWordService;

    @Before
    public void setupShuffleWordService() {

        shuffleWordService = new ShuffleWordService(ReaderXMLData.readXML(), ReaderXMLData.getMaxWordId());
    }

    @Test
    public void testGetShuffleWord() {
        for (int i = 0; i < 10; i++) {
            final ShuffleWord word = shuffleWordService.getShuffleWord();
            assertNotEquals(word.getWord(), shuffleWordService.getWordById(word.getId()));
        }
    }
}
