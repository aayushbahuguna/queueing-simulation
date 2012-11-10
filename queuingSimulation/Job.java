package queuingSimulation;
enum status{
	SERVED, UNSERVED;		
}
public class Job {
	status jobStatus;
	int id;
	double arrivalTime;
	double serviceTime;
	double waitingTime;
	Job next;

	Job (int id, double arrivalTime, double serviceTime) {
		jobStatus = status.UNSERVED;
		this.id = id;
		this.arrivalTime = arrivalTime;
		this.serviceTime = serviceTime;
	}

	double getResponseTime() {
		return waitingTime + serviceTime;
	}
}
