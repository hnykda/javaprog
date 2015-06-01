import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by dan on 5/26/15.
 */
public class Feeder {

    private HashMap<String, String> data;
    public URL url = null;
    private String maternalUrl;

    //feed.entries.forEach( (entry) -> System.out.println(entry.getLink()));




    public Feeder(URL feedUrl) throws MalformedURLException {
        this.url = feedUrl;
    }

    private ArrayList<Integer> get_entry_content_info(String url) throws
            MalformedURLException, BoilerpipeProcessingException {

        //HashMap<String, Integer> res = new HashMap<>();
        ArrayList<Integer> res = new ArrayList<>();

        String text = ArticleExtractor.INSTANCE.getText(new URL(url));

        int nWords = 0;
        String trim = text.trim();
        if (trim.isEmpty())
            nWords = 0;
        else
            nWords = trim.split("\\s+").length;

        int upperCase = 0;
        int lowerCase = 0;
        int numberCount = 0;
        int specialCharCount = 0;

        for (char c : text.toCharArray()) {
            if (Character.isUpperCase(c)){ upperCase++; }
            else if (Character.isLowerCase(c)){ lowerCase++; }
            else if (Character.isDigit(c)){ numberCount++;}
            else { specialCharCount++; }
        }


        res.add(upperCase/nWords);
        res.add(lowerCase/nWords);
        res.add(numberCount/nWords);
        res.add(specialCharCount/nWords);
        res.add(nWords);
/*
        res.put("upper", upperCase/nWords) ;
        res.put("lower", lowerCase/nWords);
        res.put("numbers", numberCount/nWords);
        res.put("special", specialCharCount/nWords);
*/
        return res;
    }

    private HashMap<String, String> get_entries_info(List<SyndEntryImpl> entries)
            throws MalformedURLException, BoilerpipeProcessingException {
        HashMap<String, String> res = new HashMap<>();

         List<String> title = new ArrayList<>();
         List<String> uri = new ArrayList<>();
         List<Date> publishedDate = new ArrayList<>();
         List<String> author = new ArrayList<>();
         List<List> categories = new ArrayList<>();
         List<String> link = new ArrayList<>();
         List<List> links = new ArrayList<>();

        // we take maximally first 25 entries
        int n = entries.size();
        if (n >= 25)
        {
            n = 25;
        }

        for (SyndEntry entry : (List<SyndEntryImpl>) entries.subList(0, n)) {
            title.add(entry.getTitle());
            uri.add(entry.getUri());
            publishedDate.add(entry.getPublishedDate());
            author.add(entry.getAuthor());
            categories.add(entry.getCategories());
            link.add(entry.getLink());
            links.add(entry.getLinks());
        }

        this.maternalUrl = alexas_guess(link.get(0));


        ArrayList<Integer> ups = new ArrayList<Integer>();
        ArrayList<Integer> downs = new ArrayList<Integer>();
        ArrayList<Integer> numbs = new ArrayList<Integer>();
        ArrayList<Integer> specs = new ArrayList<Integer>();
        ArrayList<Integer> words = new ArrayList<Integer>();

        for (String Url : uri) {
            ArrayList<Integer> entRes = this.get_entry_content_info(Url);
            ups.add(entRes.get(0));
            downs.add(entRes.get(1));
            numbs.add(entRes.get(2));
            specs.add(entRes.get(3));
            words.add(entRes.get(4));
        }

        // Add the data from the array
        ArrayList<ArrayList<Integer>> heh = new ArrayList<>();

        heh.add(downs);
        heh.add(numbs);
        heh.add(specs);
        heh.add(words);
        heh.add(ups);

        ArrayList<String> vys = new ArrayList<>();

        for (ArrayList<Integer> Cisla : heh) {
            DescriptiveStatistics stats = new DescriptiveStatistics();

            for (int ncis : Cisla) {
                stats.addValue(ncis);
            }

            vys.add(String.valueOf(stats.getMean()));
            vys.add(String.valueOf(stats.getStandardDeviation()));

        }

        res.put("downs_mean", vys.get(0));
        res.put("downs_std", vys.get(1));
        res.put("numbs_mean", vys.get(2));
        res.put("numbs_std", vys.get(3));
        res.put("spec_mean", vys.get(4));
        res.put("specs_std", vys.get(5));
        res.put("words_mean", vys.get(6));
        res.put("words_std", vys.get(7));
        res.put("ups_mean", vys.get(8));
        res.put("ups_std", vys.get(9));

        return res;
    }

    public void collect() throws IOException, FeedException {

        HashMap<String, String> res = new  HashMap<>() ;
        if (url == null) {
            throw new IllegalArgumentException("URI cannot be null");
        }
        try {
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(this.url));

            res.put("title", feed.getTitle());
            res.put("language", feed.getLanguage());
            res.put("authors", feed.getAuthor());
            res.put("feedType", feed.getFeedType());
            res.put("description",feed.getDescription());

            HashMap<String, String> entries = this.get_entries_info(feed.getEntries());

            res.putAll(entries);

        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("ERROR: " + ex.getMessage());
        }

        this.data = res;
    }

    private String alexas_guess(String urlOfFirstEntry)
    {
        //driver must be choosed on my own...
        WebDriver wdriver = new PhantomJSDriver();
        wdriver.get( "http://www.alexa.com/" );
        WebElement inputField = wdriver.findElement(By.xpath("//*[@id=\"alx-content\"]/div/div/span/form/input" ));
        inputField.clear();
        inputField.sendKeys(urlOfFirstEntry);
        inputField.submit();

        String text = wdriver.findElement(By.xpath("//*[@id=\"js-li-last\"]/span[1]/a")).getText();
        return ("http://www." + text) ;
    }

    public HashMap<String, String> getData()
    {
        return this.data;
    }

    public String getMaternalUrl()
    {
        return this.maternalUrl;
    }
/*
    public HashMap<String, String> getData()
    {

        return res;
    }
    */
}
