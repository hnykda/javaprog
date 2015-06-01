import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.syndication.io.FeedException;
import org.apache.commons.cli.*;

public class Cli {
    private static final Logger log = Logger.getLogger(Cli.class.getName());
    private String[] args = null;
    private Options options = new Options();

    public Cli(String[] args) {

        this.args = args;

        options.addOption("h", "help", false, "show help.");
        options.addOption("f", "feedURL", true, "URL of feed to get info about");
        options.addOption("b", "browser", true, "What browser should be used." +
                "One of: {phantomjs, phantomjsTOR, firefox, chromium (even for chrome), firefoxTOR}. Default: phantomjs");
        options.addOption("d", "database", false, "If data should be pushed to database on localhost:27017");

        options.addOption("o", "out", true, "Specify if data should be saved to given file");
    }

    public void parse() {
        CommandLineParser parser = new BasicParser();

        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);



            boolean datab;
            if (cmd.hasOption("d"))
            {
                datab = true;
            }
            else
            {
                datab = false;
            }

            String brows;
            if (!cmd.hasOption("b")) {
                brows = "phantomjs";
            }
            else {
                brows = cmd.getOptionValue("b");
            }

            String outFile;
            if (!cmd.hasOption("o")) {
                outFile = null;
            }
            else {
                outFile = cmd.getOptionValue("o");
            }
            System.out.println(cmd.hasOption("b") + cmd.getOptionValue("b") + " " + brows);


            if (cmd.hasOption("h"))
                help();

            if (cmd.hasOption("f")) {

                Collector col = new Collector(cmd.getOptionValue("f"), brows, datab, outFile);
                col.collect_all();
                System.out.println(col.getData());
            }

            } catch (FeedException e1) {
            e1.printStackTrace();
        } catch (ParseException e1) {
            e1.printStackTrace();
            log.log(Level.SEVERE, "Failed to parse comand line properties", e1);
            help();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void help() {
        // This prints out some help
        HelpFormatter formater = new HelpFormatter();

        formater.printHelp("Main", options);
        System.exit(0);
    }
}