package ru.ominit;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.ominit.model.*;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.Random;

/**
 * Created by Александр on 31.03.2018.
 */
@Service
public class RiddleLoaderService {
    private Logger logger = LoggerFactory.getLogger(RiddleLoaderService.class);
    private XmlMapper mapper = new XmlMapper();

    public RiddleLoaderService() {
        this.haystacksPath = "resources/haystacks";
        File haystacksFile = new File(this.haystacksPath);
        logger.info("Initialise riddle loader with directory: " + haystacksFile.getAbsolutePath());
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public RiddleLoaderService(String haystacksPath) {
        this.haystacksPath = haystacksPath;
        File haystacksFile = new File(this.haystacksPath);
        logger.info("Initialise riddle loader with directory: " + haystacksFile.getAbsolutePath());
    }

    private String haystacksPath;
    public static final String META_FILENAME = "meta.xml";

    public Optional<String> getAnyHaystackId(Random rnd) {
        logger.debug("Pick random haystackId");
        Optional<Theme> themeOpt = loadMeta().getRandomTheme(rnd);
        return themeOpt.flatMap(theme -> theme.getRandomHaystackId(rnd));
    }

    public Haystack load(String haystackId) throws IOException {
        logger.debug("Load haystack {}", haystackId);
        File haystackPath = new File(haystacksPath + "/" + haystackId + ".xml");
        return mapper.readValue(haystackPath, Haystack.class);
    }

    public void write(String haystackId, Haystack haystack) throws IOException {
        File output = new File(haystacksPath + "/" + haystackId + ".xml");
        if (output.exists()) {
            if (output.delete()) {
                mapper.writeValue(output, haystack);
            } else {
                throw new FailToUpdateHaystackException(haystackId);
            }
        } else {
            mapper.writeValue(output, haystack);
        }
    }

    private File getMetaPath() {
        return new File(haystacksPath + "/" + META_FILENAME);
    }

    public Meta loadMeta() {
        logger.debug("Load list of themes");
        File metaPath = getMetaPath();
        try {
            if (metaPath.exists()) {
                Meta meta = mapper.readValue(metaPath, Meta.class);
                if (meta.getThemes().isEmpty()) {
                    throw new MetaFileEmptyException(haystacksPath);
                }
                return meta;
            } else {
                throw new MetaFileMissingException(haystacksPath);
            }
        } catch (IOException e) {
            logger.error("Failed to load meta file", e);
            throw new MetaFileMissingException(haystacksPath, e);
        }
    }

    public Optional<Haystack> loadOptional(String haystackId) {
        logger.debug("Optionally load haystack {}", haystackId);
        File haystackPath = new File(haystacksPath + "/" + haystackId + ".xml");
        if (!haystackPath.exists()) {
            return Optional.empty();
        }
        try {
            return Optional.of(mapper.readValue(haystackPath, Haystack.class));
        } catch (IOException e) {
            logger.error("Failed to deserialise haystack with id '" + haystackId + "'", e);
            return Optional.empty();
        }
    }
}
