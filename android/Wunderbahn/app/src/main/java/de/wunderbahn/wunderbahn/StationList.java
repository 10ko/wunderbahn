package de.wunderbahn.wunderbahn;

public enum StationList {
	STATION_01("Potsdamer Platz Bhf", "009100020", "1", "u2", "000000"),
	STATION_02("Mohrenstr.", "009100010", "2", "u2", "000000"),
	STATION_03("Stadtmitte", "009100011", "3", "u2", "000000"),
	STATION_04("U Hausvogteiplatz", "009100012", "4", "u2", "000000"),
	STATION_05("U Spittelmarkt", "009100013", "5", "u2", "000000"),
	STATION_06("Märkisches Museum", "009100014", "6", "u2", "000000"),
	STATION_07("Klosterstr.", "009100015", "7", "u2", "000000"),
	STATION_08("Alexanderplatz Bhf", "009100003", "8", "u2", "000000"),
	STATION_09("U Rosa-Luxemburg-Platz", "009100016", "9", "u2", "000000"),
	STATION_10("U Senefelderplatz", "009110005", "10", "u2", "000000"),
	STATION_11("Eberswalder Str.", "009110006", "11", "u2", "000000"),
	STATION_12("Schönhauser Allee", "009110001", "12", "u2", "000000"),
	STATION_13("Hermannstr.", "009079221", "1", "u8", "000000"),
	STATION_14("U Leinestr.", "009079201", "2", "u8", "000000"),
	STATION_15("U Boddinstr.", "009079202", "3", "u8", "000000"),
	STATION_16("Hermannplatz", "009078101", "4", "u8", "000000"),
	STATION_17("U Schönleinstr.", "009016201", "5", "u8", "000000"),
	STATION_18("U Kottbusser Tor", "009013102", "6", "u8", "000000"),
	STATION_19("U Moritzplatz", "009013101", "7", "u8", "000000"),
	STATION_20("Heinrich-Heine-Str.", "009100008", "8", "u8", "000000"),
	STATION_21("S+U Jannowitzbrücke", "009100004", "9", "u8", "000000"),
	STATION_22("Alexanderplatz Bhf", "009100003", "10", "u8", "000000"),
	STATION_23("Weinmeisterstr.", "009100051", "11", "u8", "000000"),
	STATION_24("U Rosenthaler Platz", "009100023", "12", "u8", "000000");
	
	private final String name;
	private final String id;
	private final String ledId;
	private final String line;
	private final String color;
	
	
	private StationList(String name, String id, String ledId, String line, String color){
		this.name = name;
		this.id = id;
		this.ledId = ledId;
		this.line = line;
		this.color = color;
	}


	public String getName() {
		return name;
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
	
	public String getColor(){
		return color;
	}
	
}
