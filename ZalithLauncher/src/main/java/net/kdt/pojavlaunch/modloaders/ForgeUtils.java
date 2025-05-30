package net.kdt.pojavlaunch.modloaders;

import net.kdt.pojavlaunch.utils.DownloadUtils;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class ForgeUtils {
    private static final String FORGE_METADATA_URL = "https://maven.minecraftforge.net/net/minecraftforge/forge/maven-metadata.xml";
    private static final String FORGE_INSTALLER_URL = "https://maven.minecraftforge.net/net/minecraftforge/forge/%1$s/forge-%1$s-installer.jar";

    public static List<String> downloadForgeVersions(boolean force) throws Exception {
        SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        SAXParser saxParser = parserFactory.newSAXParser();

        return DownloadUtils.downloadStringCached(FORGE_METADATA_URL, "forge_versions", force, input -> {
            try {
                ForgeVersionListHandler handler = new ForgeVersionListHandler();
                saxParser.parse(new InputSource(new StringReader(input)), handler);
                return handler.getVersions();
                // IOException is present here StringReader throws it only if the parser called close()
                // sooner than needed, which is a parser issue and not an I/O one
            } catch (SAXException | IOException e) {
                throw new DownloadUtils.ParseException(e);
            }
        });
    }
    public static String getInstallerUrl(String version) {
        return String.format(FORGE_INSTALLER_URL, version);
    }
}
