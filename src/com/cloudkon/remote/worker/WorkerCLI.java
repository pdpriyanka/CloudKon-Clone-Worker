package com.cloudkon.remote.worker;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class WorkerCLI {
	private String[] args;
	private static final Logger workerCLIlog = Logger.getLogger(WorkerCLI.class.getName());
	private Options options;

	public WorkerCLI() {
		super();
	}

	/**
	 * In the constructor command line options are created.
	 * 
	 * @param args
	 */
	public WorkerCLI(String[] args) {
		options = new Options();
		this.args = args;

		options.addOption("s", "qname", true, "name of SQS queue and name of the DynamoDB instance");

		options.addOption("t", "numberofWorkers", true, "Specify the number of workers");

	}

	/**
	 * This method will parse the command line options and validate it
	 */
	public void parse() {
		CommandLine commandLine = null;
		boolean missing = false;
		try {
			CommandLineParser parser = new DefaultParser();
			commandLine = parser.parse(options, args);

			// validate command line options
			if (commandLine.hasOption("s")) {
				workerCLIlog.log(Level.INFO, "Command line argument -s=" + commandLine.getOptionValue("s"));
			} else {
				workerCLIlog.log(Level.SEVERE, "missing s option from command line");
				missing = true;
			}

			if (commandLine.hasOption("t")) {
				workerCLIlog.log(Level.INFO, "Command line argument -t=" + commandLine.getOptionValue("t"));
			} else {
				workerCLIlog.log(Level.SEVERE, "missing t option from command line");
				missing = true;
			}

			// if any option is missing then show help

			if (missing) {
				help();
			}

		} catch (ParseException e) {
			System.out.println("Invalid command line option: " + e.getMessage());
		}
	}

	/**
	 * This method will show the help for command line
	 */
	public void help() {
		HelpFormatter helpFormatter = new HelpFormatter();
		helpFormatter.printHelp("Worker", options);
		System.exit(0);
	}

}
