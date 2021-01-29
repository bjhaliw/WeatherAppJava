package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class WeatherGridInformation {

	private String uom;
	private ArrayList<WeatherValues> values;

	/**
	 * @return the uom
	 */
	public String getUom() {
		return uom;
	}

	/**
	 * @param uom the uom to set
	 */
	public void setUom(String uom) {
		this.uom = uom;
	}

	/**
	 * @return the values
	 */
	public ArrayList<WeatherValues> getValues() {
		return values;
	}

	/**
	 * @param values the values to set
	 */
	public void setValues(ArrayList<WeatherValues> values) {
		this.values = values;
	}

	/**
	 * Takes in a list of WeatherValues and returns another list containing
	 * WeatherValues with valid times associated with exactly one hour (instead of
	 * multiple hours for valid time, i.e. PT2H or PT1D)
	 * 
	 * @param oldList
	 * @return
	 */
	public static ArrayList<WeatherValues> get24HrValues(ArrayList<WeatherValues> oldList, LocalTime currentTime,
			String timeZone) {
		ArrayList<WeatherValues> newList = new ArrayList<>();
		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

		for (WeatherValues currWV : oldList) {

			if (newList.size() >= 24) {
				break;
			}

			// Get the duration of the valid time (PT2H -> 2 hours)
			String durationString = currWV.getValidTime().substring(currWV.getValidTime().indexOf("/") + 1);
			Duration dur = Duration.parse(durationString);
			int hours = (int) dur.toHours(); // How many hours the forecast is good for

			// Getting the current value time
			String time = currWV.getValidTime().substring(0, currWV.getValidTime().indexOf("+"));
			time = time.substring(0, time.lastIndexOf(":"));
			time = time.replace("T", " ");
			LocalDateTime date = LocalDateTime.from(df.parse(time));

			while (hours > 0) {
				WeatherValues newWV = new WeatherValues();
				// System.out.println(date.toString());
				newWV.setValidTime(date.toString());
				newWV.setValue(currWV.getValue());
				newList.add(newWV);
				date = date.plusHours(1);
				hours--;
			}
		}

		return newList;
	}

}
