package lab3;

import java.awt.Color;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import java.util.List;
import java.util.Scanner;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.cloudwatch.model.Datapoint;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsRequest;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsResult;
import com.amazonaws.services.cloudwatch.model.Metric;
import com.amazonaws.services.ec2.AmazonEC2Client;

// Extends from applicationFram Jfreechart 
public class CWT extends ApplicationFrame {

	public CWT(final String title) {

		super(title);

		AWSCredentials credentials = null;
		AmazonCloudWatchClient cloudWatchClient = null;
		try {
			credentials = new ProfileCredentialsProvider("default").getCredentials();
			// cloud watch credentials to send
			cloudWatchClient = new AmazonCloudWatchClient(credentials);
		} catch (Exception e) {
			throw new AmazonClientException("Cannot load the credentials from the credential profiles file. "
					+ "Please make sure that your credentials file is at the correct "
					+ "location (C:\\Users\\Felipe\\.aws\\credentials), and is in valid format.", e);
		}
		// create scanner to set region
		Scanner scan = new Scanner(System.in);
		AmazonEC2Client ec2Client = new AmazonEC2Client(credentials);
		// use metods from Ec2 to get regions and status
		Region region = Ec2.RegionMenu();
		ec2Client.setRegion(region);
		Ec2.getInstanceStatus("zoulipe", ec2Client);
		System.out.println("Enter ID of instance to investigate");
		String instanceID = scan.next();
		// select the instance we want to watch
		cloudWatchClient.setRegion(region);
		System.out
				.println("Enter the metric you want to monitor: \n (Enter ListAll if you do not know the metrics) \n");
		// input metric that we want to watch
		String metricoption = scan.next();
		// in case we dont know the metric we display all the list we the if
		if (metricoption.equalsIgnoreCase("ListAll")) {
			List<Metric> metrics = cloudWatchClient.listMetrics().getMetrics();
			System.out.println("Here is a list of all possible metrics");
			// for to display all the metrics available in cloud watch
			for (Metric metric : metrics) {
				System.out.println(metric.getMetricName());
			}
			System.out.println("Enter the metric you want to use:");
			// input metric that we want to watch
			metricoption = scan.next();
		}
		// if we want more metrics to display we split with a coma
		String[] metrics = metricoption.split(",");
		for (String metric : metrics) {
			// we use the methods from Ec2 defined to get and display metrics
			GetMetricStatisticsRequest statrequest = Ec2.request(instanceID, metric);
			GetMetricStatisticsResult statresult = Ec2.result(cloudWatchClient, statrequest);
			Ec2.toStdOut(statresult, instanceID);
			// with the data from the methods we create a dataset to display in chart and use the methdos
			final XYDataset dataset = createDataset(statresult);
			final JFreeChart chart = createChart(dataset, statresult);
			final ChartPanel chartPanel = new ChartPanel(chart);
			chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
			// diplay the panel
			setContentPane(chartPanel);
		}

	}

	// create data set with metrc values
	private XYDataset createDataset(GetMetricStatisticsResult result) {

		final XYSeries series1 = new XYSeries(result.getLabel() + " data");
		// create the datapoints to put in the chart
		for (final Datapoint dataPoint : result.getDatapoints()) {
			series1.add(dataPoint.getTimestamp().getHours(), dataPoint.getAverage());
		}
		final XYSeriesCollection dataset = new XYSeriesCollection();
		// add the datapoiint to the series
		dataset.addSeries(series1);
		return dataset;

	}

// method to create the chart
	private JFreeChart createChart(final XYDataset dataset, GetMetricStatisticsResult result) {

		// create the chart...
		final JFreeChart chart = ChartFactory.createXYLineChart("Cloudwatch for " + result.getLabel(), // chart
				// title
				"Time in Hours", // x axis label
				result.getLabel() + " in " + result.getDatapoints().get(0).getUnit(), // y
																						// axis
																						// label
				dataset, // data
				PlotOrientation.VERTICAL, true, // include legend
				true, // tooltips
				false // urls
		);

		// NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
		chart.setBackgroundPaint(Color.white);

		// final StandardLegend legend = (StandardLegend) chart.getLegend();
		// legend.setDisplaySeriesShapes(true);

		// get a reference to the plot for further customisation...
		final XYPlot plot = chart.getXYPlot();
		plot.setBackgroundPaint(Color.lightGray);
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);

		final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		renderer.setSeriesLinesVisible(0, false);
		renderer.setSeriesShapesVisible(1, false);
		plot.setRenderer(renderer);

		// change the auto tick unit selection to integer units only...
		final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		// OPTIONAL CUSTOMISATION COMPLETED.

		return chart;

	}

	public static void main(final String[] args) {

		final CWT demo = new CWT("Metric");
		demo.pack();
		RefineryUtilities.centerFrameOnScreen(demo);
		demo.setVisible(true);

	}

}