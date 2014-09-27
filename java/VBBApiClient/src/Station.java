
public class Station implements Comparable{
	
	private String stationName; // Station Name
	private String id;			// Station id accordingly VBB Api
	private String ledId;		// Led id associated with the station
	private String line;		// Line the station belong to
	private String color;		// RGB color of the station, printed on the real map
	
	
	public Station(String name, String id, String ledId, String line, String color) {
		this.stationName = name;
		this.id = id;
		this.ledId = ledId;
		this.line = line;
		this.color = color;
		
	}
	public String getStationName() {
		return stationName;
	}
	public String getId() {
		return id;
	}
	public String getLedId() {
		return ledId;
	}
	public String getLine() {
		return line;
	}
	public String getColor() {
		return color;
	}
	
	@Override
	public String toString() {
		return "Station [stationName=" + stationName + ", id=" + id
				+ ", ledId=" + ledId + ", line=" + line + ", color=" + color
				+ "]";
	}
	
	
	public int compareTo(Object o) {
		if(o != null && o instanceof Station){
			if(this.ledId.equals(((Station)o).getLedId()))
				return 0;
			else if(Integer.valueOf(this.ledId) > Integer.valueOf(((Station)o).getLedId()))
				return 1;
			else
				return -1;
		}
		return -1;
	}
	

}
