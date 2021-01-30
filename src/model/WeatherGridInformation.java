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
	public static ArrayList<WeatherValues> get24HrValues(ArrayList<WeatherValues> oldList, LocalTime currentTime) {
		ArrayList<WeatherValues> newList = new ArrayList<>();
		DateTimeFormatter df = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

		/**
		 * Convert all durations to individual times
		 */
		for (WeatherValues currWV : oldList) {
			// Get the duration of the valid time (PT2H -> 2 hours)
			String durationString = currWV.getValidTime().substring(currWV.getValidTime().indexOf("/") + 1);
			Duration dur = Duration.parse(durationString);
			int hours = (int) dur.toHours(); // How many hours the forecast is good for

			String time = currWV.getValidTime().substring(0, currWV.getValidTime().indexOf("/"));
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

		ArrayList<WeatherValues> temp = (ArrayList<WeatherValues>) newList.clone();
		df = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
		for (WeatherValues currWV : temp) {
			// Getting the current value time
			LocalDateTime date = LocalDateTime.from(df.parse(currWV.getValidTime()));
			if (date.toLocalTime().compareTo(currentTime) < 0) {
				if (date.getHour() != currentTime.getHour()) {
					newList.remove(currWV);
				}
			} else {
				break;
			}
		}

		ArrayList<WeatherValues> outputList = new ArrayList<>();
		for (int i = 0; i <= 24; i++) {
			outputList.add(newList.get(i));
		}

		return outputList;
	}

}
