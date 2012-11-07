package queuingSimulation;
public class Job {
	int id;
	double arrivalTime;
	double serviceTime;
	double waitingTime;
	Job next;

	Job (int id, double arrivalTime, double serviceTime) {
		this.id = id;
		this.arrivalTime = arrivalTime;
		this.serviceTime = serviceTime;
	}

	double getResponseTime() {
		return waitingTime + serviceTime;
	}
}
