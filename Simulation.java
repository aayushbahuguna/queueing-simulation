import java.io.*;
import java.util.*;

public class Simulation {
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

		Job current = job[0];
		current.responseTime = current.serviceTime;
		Job next = job[1];
		Job jobWhichNeverArrives = new Job(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
		int nextDepartureTime = job[0].arrivalTime + job[0].serviceTime;
		Queue waitingQueue = new Queue();

		while (true) {
			if (next.arrivalTime < nextDepartureTime) {
				// Next event is an arrival.

				if (current == null) {
					// There is no job executing currently.
					// So, arrived job will go directly to the server instead of the waiting queue.
					current = next;
					current.waitingTime = 0;
					current.responseTime = current.serviceTime;
					nextDepartureTime = next.arrivalTime + next.serviceTime;
				} else {
					// Arrived job will go to the waiting queue.
					waitingQueue.add(next);
				}
				if (next.id == N-1) {
					next = jobWhichNeverArrives;
				} else {
					next = job[next.id + 1];
				}
			} else if (next.arrivalTime > nextDepartureTime) {
				// Next event is a departure.

				if (current.id == N-1) {
					break;
				}
				if (waitingQueue.isEmpty()) {
					// Server will become idle.
					current = null;
					nextDepartureTime = Integer.MAX_VALUE;
				} else {
					// Server gets the first job from the waiting queue.
					current = waitingQueue.remove();
					current.waitingTime = nextDepartureTime - current.arrivalTime;
					current.responseTime = current.waitingTime + current.serviceTime;
					nextDepartureTime += current.serviceTime;
				}
			} else {
				// A job departs and another job arrives at the same time.

				if (waitingQueue.isEmpty()) {
					current = next;
					current.waitingTime = 0;
					current.responseTime = current.serviceTime;
					nextDepartureTime = next.arrivalTime + next.serviceTime;
				} else {
					// Server gets the first job from the waiting queue.
					current = waitingQueue.remove();
					current.waitingTime = nextDepartureTime - current.arrivalTime;
					current.responseTime = current.waitingTime + current.serviceTime;
					nextDepartureTime += current.serviceTime;
					waitingQueue.add(next);
				}
				next = job[next.id + 1];
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
