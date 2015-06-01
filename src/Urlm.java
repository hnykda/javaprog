/**
 * Created by dan on 5/26/15.
 */
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;

public class Urlm extends Scrapper{

    public String url;

    public Urlm(String _maternalURL, String _baseURL,
                 String _xpathOfInputField, WebDriver driver, String jsonName)
    {
        super(_maternalURL, _baseURL,
                _xpathOfInputField,  driver, jsonName);
    }

    @Override
    boolean check_availability() throws NoSuchElementException {
        String chck = this.driver.findElement(
                By.xpath("/html/body/div/div[2]/div/div/div/div/h3")).getText();

        if ((chck.toLowerCase().contains("We don't have enough data to rank this website.".toLowerCase())))
            return false;
        else
            return true;
    }

}
