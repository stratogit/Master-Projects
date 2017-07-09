package lab3;

import java.awt.Canvas;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.RegionUtils;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.cloudwatch.model.Datapoint;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsRequest;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsResult;
import com.amazonaws.services.cloudwatch.model.Metric;
import com.amazonaws.services.ec2.AmazonEC2AsyncClient;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.CreateSecurityGroupRequest;
import com.amazonaws.services.ec2.model.CreateSecurityGroupResult;
import com.amazonaws.services.ec2.model.DescribeImagesRequest;
import com.amazonaws.services.ec2.model.DescribeImagesResult;
import com.amazonaws.services.ec2.model.DescribeInstanceStatusRequest;
import com.amazonaws.services.ec2.model.DescribeInstanceStatusResult;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Filter;
import com.amazonaws.services.ec2.model.Image;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceStatus;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesResult;

public class Ec2 {

	public static void main(String[] args) throws IOException {
		// aws credential
		AWSCredentials credentials = null;
		// the request to send to cloudwatch
		AmazonCloudWatchClient cloudWatchClient = null;
		try {
			// credentials to get
			credentials = new ProfileCredentialsProvider("default").getCredentials();
			// cloud watch credentials to send
			cloudWatchClient = new AmazonCloudWatchClient(credentials);
		} catch (Exception e) {
			throw new AmazonClientException("Cannot load the credentials from the credential profiles file. "
					+ "Please make sure that your credentials file is at the correct "
					+ "location (C:\\Users\\Felipe\\.aws\\credentials), and is in valid format.", e);
		}
		// creating amazon client to interact with aws intances
		AmazonEC2Client ec2Client = new AmazonEC2Client(credentials);
		System.out.println("===========================================");
		System.out.println("Welcome to EC2 interactive Programm	");
		System.out.println("===========================================\n");

		// scanner for keyboard input
		Scanner scan = new Scanner(System.in);

		// Program Menu
		loop: for (;;) {
			System.out.println(
					" \n1. List Region Names \n2. Run Instances \n3. Retreive Status \n4. Stop Instances  \n5.Exit ");
			// scan input
			int choice = scan.nextInt();
			// Variables for accessing different amazon regions

			Region region;

			try {

				switch (choice) {
				
				// Creating buckets in different locations
				case 1:
					// list the regions available
					ListRegions();
					break;
				
					// lISTING BUCKETS FROM AWS
				case 2:
					// call the method where we select region
					region = RegionMenu();
					// call method were we set the client region
					ec2Client.setRegion(region);
					// run the new instance with the recion created
					RunInstancesRequest runInstanceRequest = new RunInstancesRequest();
					// type of instance created is micro with ami ubuntu and
					// security key previously created
					runInstanceRequest.withImageId("ami-5ec1673e").withInstanceType("t2.micro").withMinCount(1)
							.withMaxCount(1).withKeyName("zoulipe").withSecurityGroups("ZoulipeSecurityGroup");
					// we run the instance created
					RunInstancesResult runInstancesResult = ec2Client.runInstances(runInstanceRequest);
					System.out.println("Instance Created");
					break;

				// Uploading file to bucket
				case 3:
					// call method to display the region menu
					region = RegionMenu();
					// call method were we set the client region
					ec2Client.setRegion(region);
					// call method to get the status from the instances
					getInstanceStatus("zoulipe", ec2Client);
					break;

				// Delete file from bucket
				case 4:
					region = RegionMenu();
					// call method to display the region menu
					ec2Client.setRegion(region);
					// call method were we set the client region
					getInstanceStatus("zoulipe", ec2Client);
					System.out.println("Enter ID of instance to stop");
					// select the instance ID from the instance we want to stop
					String instanceID = scan.next();
					// create object to stop instance
					StopInstancesRequest request = new StopInstancesRequest();
					// asociate id with request
					request.withInstanceIds(instanceID);
					//  send the request to stop the instance with ID given
					StopInstancesResult result = ec2Client.stopInstances(request);
					System.out.println("Instance stopped");
					break;
		
				case 5:
					break loop;
				default:
					// invalid selection
					System.out.println("\n Invalid selection , choose again \n \n ");
					continue loop;
				}

			} catch (AmazonServiceException ase) {
				System.out.println("Caught an AmazonServiceException, which means your request made it "
						+ "to Amazon EC2, but was rejected with an error response for some reason.");
				System.out.println("Error Message:    " + ase.getMessage());
				System.out.println("HTTP Status Code: " + ase.getStatusCode());
				System.out.println("AWS Error Code:   " + ase.getErrorCode());
				System.out.println("Error Type:       " + ase.getErrorType());
				System.out.println("Request ID:       " + ase.getRequestId());
			} catch (AmazonClientException ace) {
				System.out.println("Caught an AmazonClientException, which means the client encountered "
						+ "a serious internal problem while trying to communicate with EC2, "
						+ "such as not being able to access the network.");
				System.out.println("Error Message: " + ace.getMessage());
			}

		}
		System.out.println("\n System Terminated........");
		//close scan variable
		scan.close();
	}

	
	// method to list the regions
	public static void ListRegions() {
		int i = 0;
		// create a for to list all regions
		for (Region c : RegionUtils.getRegions()) {
			i++;
			// declare endpoint for service ec2
			String endpoint = c.getServiceEndpoint("ec2");
			//print region with endpoint
			System.out.println(i + ". Region:" + c + "\t Endpoint:" + endpoint);
		}
	}
	
	// method to get the region selected
	public static Region GetRegion(int index) {
		List<Region> regions = (List<Region>) RegionUtils.getRegions();
		return regions.get(index - 1);
	}

	//  with this method we display all the regions using methods listregion and getregion
	public static Region RegionMenu() {
		Scanner scan = new Scanner(System.in);
		System.out.println("Here are all available regions:");
		// we list regions with method
		ListRegions();
		System.out.println("Choose your region");
		int index = scan.nextInt();
		// we select the region with the method getRegion
		Region region = GetRegion(index);
		return region;
	}

	// method to create security group
	public static CreateSecurityGroupRequest createSG(String groupName) {
		// declare new security group
		CreateSecurityGroupRequest csgr = new CreateSecurityGroupRequest();
		// create security group with the selecerd ID
		csgr.withGroupName("ZoulipeSecurityGroup").withDescription("Zoulipe Security Group");
		return csgr;
	}

	// method to get the instance status
	public static void getInstanceStatus(String keyName, AmazonEC2Client ec2Client) {
		// create request variable to describe instances
		DescribeInstancesResult request = ec2Client.describeInstances();
		// create a list were instances are reserved
		List<Reservation> instances = request.getReservations();
		// for to display all instances
		for (Reservation instance : instances) {
			List<Instance> info = instance.getInstances();
			Instance current = info.get(0);
			System.out.println("Instance Key Name:" + current.getKeyName() + "\t Instamce ID:" + current.getInstanceId()
					+ "\t" + current.getState().getName());
		}
	}

	// method to obtain metrics for clod watch
	public static GetMetricStatisticsRequest request(final String instanceId, String metricoption) {
		final long twentyFourHrs = 1000 * 60 * 60 * 24;
		final int oneHour = 60 * 60;
		//generate result for one hour period in 24 hours
		return new GetMetricStatisticsRequest().withStartTime(new Date(new Date().getTime() - twentyFourHrs))
				.withNamespace("AWS/EC2").withPeriod(oneHour)
				.withDimensions(new Dimension().withName("InstanceId").withValue(instanceId))
				.withMetricName(metricoption).withStatistics("Average", "Maximum").withEndTime(new Date());
	}

	// method to get statistics results from client
	public static GetMetricStatisticsResult result(final AmazonCloudWatchClient client,
			final GetMetricStatisticsRequest request) {
		return client.getMetricStatistics(request);
	}

	public static void toStdOut(final GetMetricStatisticsResult result, String instanceId) {
		System.out.println(result); 
		for (final Datapoint dataPoint : result.getDatapoints()) {
			System.out.printf("%s instance's average " + result.getLabel() + " utilization : %s%n", instanceId,
					dataPoint.getAverage());
			System.out.printf("%s instance's max " + result.getLabel() + " utilization : %s%n", instanceId,
					dataPoint.getMaximum());
		}
	}
}
