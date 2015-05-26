import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by dan on 5/26/15.
 */
public class Feeder {

    public URL url = null;

    public List<SyndEntryImpl> entries;

    //feed.entries.forEach( (entry) -> System.out.println(entry.getLink()));

/*    public List<String> title = new ArrayList<>();
    public List<String> uri = new ArrayList<>();
    public List<Date> updatedDate = new ArrayList<>();
    public List<String> author = new ArrayList<>();
    public List<List> categories = new ArrayList<>();
    public List<String> link = new ArrayList<>();
    public List<List> links = new ArrayList<>();*/


    public Feeder(String url) throws MalformedURLException {
        URL feedUrl = new URL(url);
        this.url = feedUrl;
    }

    public void collect() throws IOException, FeedException {
        if (url == null) {
            throw new IllegalArgumentException("URI cannot be null");
        }
        try {
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(this.url));

            this.entries = feed.getEntries();

/*            for (SyndEntry entry : (List<SyndEntryImpl>) feed.getEntries()) {
                this.title.add(entry.getTitle());
                this.uri.add(entry.getUri());
                this.updatedDate.add(entry.getUpdatedDate());
                this.author.add(entry.getAuthor());
                this.categories.add(entry.getCategories());
                this.link.add(entry.getLink());
                this.links.add(entry.getLinks());
            }*/
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("ERROR: " + ex.getMessage());
        }
    }
}
