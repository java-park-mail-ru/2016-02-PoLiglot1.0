package main.cnf;

import org.jetbrains.annotations.Nullable;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.util.Map;

/**
 * Created by Snach on 21.05.16.
 */
public class ReaderXMLData {

    private static final String WORDS_XML_FILE = "configuration/data/words.xml";

    private static long maxWordId;

    @Nullable
    public static Map<Long, String> readXML() {
        try {
            final SAXParserFactory factory = SAXParserFactory.newInstance();
            final SAXParser saxParser = factory.newSAXParser();

            final SAXHendler handler = new SAXHendler();
            saxParser.parse(WORDS_XML_FILE, handler);
            maxWordId = handler.getPos();
            return handler.getWords();

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static long getMaxWordId() {
        return maxWordId;
    }
}
