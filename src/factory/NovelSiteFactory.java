package factory;

import interfaces.NovelSite;
import sites.RoyalRoadSite;


// Factory that returns the correct NovelSite implementation based on the URL.
// Implementing a Factory design pattern.

public class NovelSiteFactory {

    // Returns the appropriate NovelSite implementation for the given URL.

    public static NovelSite getSiteByUrl(String url) {
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("URL cannot be null or empty.");
        }
        if (url.contains("royalroad")) {
            return new RoyalRoadSite();
        }
        
        // Add more sites here as needed

        //Throws an exception if the site is not supported
        throw new IllegalArgumentException("Unsupported site: " + url);
    }
}