package interfaces;

import org.jsoup.nodes.Document;
import java.io.IOException;
import java.util.List;


// Each site implementation handles its own parsing logic.

public interface NovelSite {
    // Returns a unique key identifying this site (e.g., "novelbin")
    String getSiteKey();

    // Returns the base URL used as the referrer header for HTTP requests
    String getReferrer();

    // Extracts the novel slug from the URL for use as the EPUB filename
    String extractSlug(String novelUrl);

    // Parses the chapter title from a fetched document
    String parseTitle(Document doc, int chapterNumber);

    // Parses the chapter body content from a fetched document
    String parseContent(Document doc, String chapterUrl);

    // Fetches and returns all chapter URLs from the novel's main page
    List<String> getChapterUrls(String novelUrl) throws IOException;
}