import java.io.*;
import java.util.*;

public class Simulation {

	Job currentJob;
	Job nextJob;
	static final Job jobWhichNeverArrives = new Job(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
	Queue waitingQueue;

	public static void main(String[] args) throws IOException {
		Scanner s = new Scanner(new BufferedReader(new InputStreamReader(System.in)));
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(System.out));

		int N = s.nextInt();
		Job[] job = new Job[N];

		for (int i = 0; i < N; i++) {
			int arrivalTime = s.nextInt();
			int serviceTime = s.nextInt();
			job[i] = new Job(i, arrivalTime, serviceTime); 
		}

		s.close();

		waitingQueue = new Queue();
		currentJob = job[0];
		nextJob = job[1];
		currentJob.responseTime = currentJob.serviceTime;
		int currentJobDepartureTime = job[0].arrivalTime + job[0].serviceTime;

		while (true) {
			if (nextJob.arrivalTime < currentJobDepartureTime) {
				// Next event is an arrival.

				if (currentJob == null) {
					// There is no job executing currently.
					// So, arrived job will go directly to the server instead of the waiting queue.
					currentJob = nextJob;
					currentJob.waitingTime = 0;
					currentJob.responseTime = currentJob.serviceTime;
					currentJobDepartureTime = currentJob.arrivalTime + currentJob.serviceTime;
				} else {
					// Arrived job will go to the waiting queue.
					waitingQueue.add(nextJob);
				}
				if (nextJob.id == N-1) {
					nextJob = jobWhichNeverArrives;
				} else {
					nextJob = job[nextJob.id + 1];
				}
			} else if (nextJob.arrivalTime > currentJobDepartureTime) {
				// Next event is a departure.

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
					currentJob = waitingQueue.remove();
					currentJob.waitingTime = currentJobDepartureTime - currentJob.arrivalTime;
					currentJob.responseTime = currentJob.waitingTime + currentJob.serviceTime;
					currentJobDepartureTime += currentJob.serviceTime;
				}
			} else {
				// A job departs and another job arrives at the same time.

				if (waitingQueue.isEmpty()) {
					currentJob = nextJob;
					currentJob.waitingTime = 0;
					currentJob.responseTime = currentJob.serviceTime;
					currentJobDepartureTime = currentJob.arrivalTime + currentJob.serviceTime;
				} else {
					// Server gets the first job from the waiting queue.
					currentJob = waitingQueue.remove();
					currentJob.waitingTime = currentJobDepartureTime - currentJob.arrivalTime;
					currentJob.responseTime = currentJob.waitingTime + currentJob.serviceTime;
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
		for (int i = 0; i < N; i++) {
			out.write("Job ID = " + job[i].id + 
			          " Job Arrival Time = " + job[i].arrivalTime + 
					  " Job Service Time = " + job[i].serviceTime + 
					  " Job Waiting Time = " + job[i].waitingTime + 
					  " Job Response Time = " + job[i].responseTime + "\n");
		}
		out.flush();
		out.close();
	}
}

class Queue {

	Job first;
	Job last;
	int length;

	Queue() {
		first = null;
		last = null;
		length = 0;
	}

	void add(Job j) {
		if (first == null) {
			first = j;
			last = j;
		} else {
			last.next = j;
			last = j;
		}
		length++;
	}

	Job remove() {
		if (first == null) {
			throw new RuntimeException("Trying to remove from empty queue");
		}
		length--;
		Job result = first;
		first = first.next;
		return result;
	}

	boolean isEmpty() {
		return length==0;
	}

}

class Job {
	int id;
	int arrivalTime;
	int serviceTime;
	int waitingTime;
	int responseTime;
	Job next;

	Job (int id, int arrivalTime, int serviceTime) {
		this.id = id;
		this.arrivalTime = arrivalTime;
		this.serviceTime = serviceTime;
	}
}
