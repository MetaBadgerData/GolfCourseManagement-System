
public class Reservations {
    private int golferId;
	private int courseId;
	private String date;
    private String time;
	private double totalCost;
	
	public Reservations(int golferId, int courseId, String date, String time, double totalCost) {
		super();
		this.golferId = golferId;
		this.courseId = courseId;
		this.date = date;
        this.time = time;
		this.totalCost = totalCost;
	}

	public double getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(double totalCost) {
		this.totalCost = totalCost;
	}
	public int getGolferId() {
		return golferId;
	}

	public void setGolferId(int golferId) {
		this.golferId = golferId;
	}

	public int getCourseId() {
		return courseId;
	}

	public void setCourseId(int courseId) {
		this.courseId = courseId;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
    
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

	@Override
	public String toString() {
		return "Reservations [golferId=" + golferId + ", courseId=" + courseId + ", date=" + date + ", time= " + time + ", totalCost=" + totalCost
				 + "]";
	}
}
