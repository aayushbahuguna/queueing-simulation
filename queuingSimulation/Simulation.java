package queuingSimulation;
import java.io.*;
import java.util.*;

public class Simulation {
	static final Job jobWhichNeverArrives = new Job(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
	public static void main(String[] args) throws IOException{
		Scanner in = new Scanner(new BufferedReader(new InputStreamReader(System.in)));
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(System.out));

		int N = in.nextInt();
		Job[] job = new Job[N];
		
		double lambda = in.nextDouble();
		double mu = in.nextDouble();
		Random rnd = new Random();
		double arrivalTime = nextRandomExponential(rnd, lambda);
		double serviceTime = nextRandomExponential(rnd, mu);

		job[0] = new Job(0, arrivalTime, serviceTime);
		for (int i = 1; i < N; i++) {
			/*
			 *  Inter arrival times are exponentially distributed.
			 * Service time is exponentially distributed.
			 * We want to generate random numbers which follow exponential distribution.
			 * The default random number generated follow uniform distribution.
			 * We need to convert that distribution into exponential distribution.
			 */
			arrivalTime = nextRandomExponential(rnd, lambda) + job[i-1].arrivalTime;
			serviceTime = nextRandomExponential(rnd, mu);
			job[i] = new Job(i, arrivalTime, serviceTime); 
		}
		in.close();
		
		Queue<Job> waitingQueue = new LinkedList<Job>();
		
		ArrayList<Double> queueLengthY = new ArrayList<Double>();
		ArrayList<Double> queueLengthX = new ArrayList<Double>();
		
		queueLengthY.add(0.0);
		queueLengthX.add(0.0);
		double epsilon = 0.0001;
		Job currentJob = job[0];
		Job nextJob = job[1];
		double currentJobDepartureTime = job[0].arrivalTime + job[0].serviceTime;
		double QiTi = 0;
		double lastUpdate = 0;
		
		while (true) {
			if (nextJob.arrivalTime < currentJobDepartureTime) {
				// Next event is an arrival.
				
				QiTi += (waitingQueue.size() * (nextJob.arrivalTime - lastUpdate));
				lastUpdate = nextJob.arrivalTime;

				if (currentJob == null) {
					// There is no job executing currently.
					// So, arrived job will go directly to the server instead of the waiting queue.
					currentJob = nextJob;
					currentJob.waitingTime = 0;
					currentJobDepartureTime = currentJob.arrivalTime + currentJob.serviceTime;
				} else {
					// Arrived job will go to the waiting queue.
					queueLengthY.add((double)waitingQueue.size());
					queueLengthX.add(nextJob.arrivalTime - epsilon);
					waitingQueue.add(nextJob);
					queueLengthY.add((double)waitingQueue.size());
					queueLengthX.add(nextJob.arrivalTime);
				}
				if (nextJob.id == N-1) {
					nextJob = jobWhichNeverArrives;
				} else {
					nextJob = job[nextJob.id + 1];
				}
			} else if (nextJob.arrivalTime > currentJobDepartureTime) {
				// Next event is a departure.
				
				QiTi += (waitingQueue.size() * (currentJobDepartureTime - lastUpdate));
				lastUpdate = currentJobDepartureTime;

				if (currentJob.id == N-1) {
					// Last job has departed.
					break;
				}
				if (waitingQueue.isEmpty()) {
					// Server will become idle.
					currentJob = null;
					currentJobDepartureTime = Integer.MAX_VALUE;
				} else {
					// Server gets the first job from the waiting queue.
					queueLengthY.add((double)waitingQueue.size());
					queueLengthX.add(currentJobDepartureTime - epsilon);
					currentJob = waitingQueue.remove();
					queueLengthY.add((double)waitingQueue.size());
					queueLengthX.add(currentJobDepartureTime);
					
					currentJob.waitingTime = currentJobDepartureTime - currentJob.arrivalTime;
					currentJobDepartureTime += currentJob.serviceTime;
				}
			} else {
				// A job departs and another job arrives at the same time.

				if (waitingQueue.isEmpty()) {
					currentJob = nextJob;
					currentJob.waitingTime = 0;
					currentJobDepartureTime = currentJob.arrivalTime + currentJob.serviceTime;
				} else {
					// Server gets the first job from the waiting queue.
					currentJob = waitingQueue.remove();
					currentJob.waitingTime = currentJobDepartureTime - currentJob.arrivalTime;
					currentJobDepartureTime += currentJob.serviceTime;
					waitingQueue.add(nextJob);
				}
				if (nextJob.id == N-1) {
					nextJob = jobWhichNeverArrives;
				} else {
					nextJob = job[nextJob.id + 1];
				}
			}
		}
		double[] X = new double[queueLengthX.size()];
		double[] Y = new double[queueLengthX.size()];
		for(int i = 0; i < X.length; i++){
			X[i] = queueLengthX.get(i);
			Y[i] = queueLengthY.get(i);
		}
		LineChart.createChart(X, Y, "Queue Length vs Time", "Time", "QueueLength");
		
		/*for (int i = 0; i < N; i++) {
			out.write("Job ID = " + job[i].id + 
			          " Arrival Time = " + String.format("%.3f", job[i].arrivalTime) + 
					  " Service Time = " + String.format("%.3f", job[i].serviceTime) + 
					  " Waiting Time = " + String.format("%.3f", job[i].waitingTime) + 
					  " Response Time = " + String.format("%.3f", job[i].getResponseTime()) + "\n");
		}*/
		out.write("Average Queue length = " + QiTi/currentJobDepartureTime +
				  " Throughput = " + N/currentJobDepartureTime +  "\n");
		out.flush();
		out.close();
	}
	
	static double nextRandomExponential(Random rnd, double l) {
		double expX = -1 * Math.log(1 - rnd.nextDouble()) / l;
		return expX;
	}

}
