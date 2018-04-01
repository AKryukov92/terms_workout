package ru.ominit;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.stereotype.Service;
import ru.ominit.model.Sphinx;

import java.io.File;
import java.io.IOException;
import java.util.Random;

/**
 * Created by Александр on 31.03.2018.
 */
@Service
public class RiddleLoader {
    private XmlMapper mapper = new XmlMapper();

    private static String haystacksPath = "resources/haystacks";

    public String getAnySphinxId(Random rnd){
        File riddlesDirectory = new File(haystacksPath);
        String[] filenames = riddlesDirectory.list();
        assert filenames != null;
        int nextId = rnd.nextInt(filenames.length);
        return filenames[nextId].replace(".xml", "");
    }

    public Sphinx load(String sphinxFilename) throws IOException {
        File sphinxPath = new File(haystacksPath + "/" + sphinxFilename + ".xml");
        return mapper.readValue(sphinxPath, Sphinx.class);
    }
}
