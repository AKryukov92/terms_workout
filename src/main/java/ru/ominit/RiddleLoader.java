package ru.ominit;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import ru.ominit.model.Riddle;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

/**
 * Created by Александр on 31.03.2018.
 */
@Service
public class RiddleLoader {
    private DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

    private static String riddlesPath = "resources/riddles";
    private static String haystacksPath = "resources/haystacks";

    public Riddle loadAny() {
        File riddlesDirectory = new File(riddlesPath);
        System.out.println(riddlesDirectory.getAbsoluteFile());
        String[] filenames = riddlesDirectory.list();
        assert filenames != null;
        Random rnd = new Random();
        int nextId = rnd.nextInt(filenames.length);
        return load(filenames[nextId]);
    }

    public Riddle load(String riddleFile) {
        File riddlePath = new File(riddlesPath + "/" + riddleFile);
        try {
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(riddlePath);
            Element root = doc.getDocumentElement();
            String haystack = loadHaystack(root.getAttribute("haystack"));
            String needle = root.getAttribute("needle");
            String nextId = root.getAttribute("next");
            String minAnswer = root.getElementsByTagName("min_answer").item(0).getTextContent();
            String maxAnswer = root.getElementsByTagName("max_answer").item(0).getTextContent();
            return new Riddle(riddleFile, haystack, needle, minAnswer, maxAnswer, nextId);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            //Подозреваю, что это плохое решение, но пока не придумал как лучше
            return loadAny();
        }
    }

    public String loadHaystack(String haystackId) throws IOException {
        String haystackFilename = haystacksPath + "/" + haystackId + ".txt";
        return new String(Files.readAllBytes(Paths.get(haystackFilename)), StandardCharsets.UTF_8);
    }
}
