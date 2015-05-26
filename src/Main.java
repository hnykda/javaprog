public class Main {

    public static void main(String[] args) throws Exception {
        Feeder feed = new Feeder("http://rss.cnn.com/rss/edition.rss");
        feed.collect();
        //feed.entries.forEach( (entry) -> System.out.println(entry.getLink()));

        Alexa alexa = new Alexa("http://www.ihned.cz","http://www.alexa.com",
                "//*[@id=\"alx-content\"]/div/div/span/form/input", "phantomjs");
        String res = alexa.collect();
        System.out.println(res);

    }
}
