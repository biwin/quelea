/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.quelea.windows.timer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.quelea.data.displayable.TimerDisplayable;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Input and output a timer to a file
 * <p/>
 * @author Ben
 */
public class TimerIO {

    /**
     * Method to save the countdown timer as a file
     * <p/>
     * @param t the timer displayable to save
     * @param f the file to save the timer to
     */
    public static void timerToFile(TimerDisplayable t, File f) throws IOException {
        if (f.exists()) {

        } else {
            f.createNewFile();
        }
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), "UTF-8"))) {
            bw.write(t.getXML());
        }
    }

    /**
     * Method to load the countdown timer from a file
     * <p/>
     * @param f the file to load the timer from
     * @return the timer if the operation was successful, null otherwise.
     */
    public static TimerDisplayable timerFromFile(File f) {
        if (f.isFile()) {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
            StringBuilder contentsBuilder = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                contentsBuilder.append(line).append('\n');
            }
            String contents = contentsBuilder.toString();
            contents = contents.replace(new String(new byte[]{11}), "\n");
            contents = contents.replace(new String(new byte[]{-3}), " ");
            InputStream strInputStream = new ByteArrayInputStream(contents.getBytes("UTF-8"));
            
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(strInputStream); //Read from our "bodged" stream.
            Node node = doc.getFirstChild();
            return TimerDisplayable.parseXML(node);
            }
            catch(IOException | ParserConfigurationException | SAXException e) {
                return null;
            }
            
        } else {
            return null;
        }
    }
}
