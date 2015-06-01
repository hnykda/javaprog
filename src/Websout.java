/**
 * Created by dan on 5/26/15.
 */
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;

public class Websout extends Scrapper{

    public String url;

    public Websout(String _maternalURL, String _baseURL,
                String _xpathOfInputField, WebDriver driver, String jsonName)
    {
        super(_maternalURL, _baseURL,
                _xpathOfInputField,  driver, jsonName);
    }

    @Override
    boolean check_availability() throws NoSuchElementException {
        String chck = this.driver.findElement(
                By.xpath("/html/body")).getText();

        if ((chck.toLowerCase().contains("no data".toLowerCase())) || ((chck.toLowerCase().contains("not a Valid Domain".toLowerCase()))))
            return false;
        else
            return true;
    }

}
