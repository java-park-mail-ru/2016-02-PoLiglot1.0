package main.cnf;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Snach on 15.04.16.
 */
public class SAXHendler extends DefaultHandler {
    final Map<Long, String> words = new HashMap<>();
    private long pos = 0;
    private boolean inElement = false;

    @Override
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        if (!qName.equals("words"))
            inElement = true;
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        inElement = false;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (inElement) {
            words.put(++pos, new String(ch, start, length));
        }
    }

    public long getPos() {
        return pos;
    }

    public Map<Long, String> getWords() {
        return words;
    }
}
