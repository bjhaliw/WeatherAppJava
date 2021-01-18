package model;

import java.util.ArrayList;

public class WeatherGridInformation {

	ForecastedTemp forecastedTemp;
	ForecastedDewpoint forecastedDewpoint;
	DailyTemperatureHigh dailyTemperatureHigh;
	DailyTemperatureLow dailyTemperatureLow;
	ForecastedHumidity forecastedHumidity;
	ForecastedApparentTemp forecastedApparentTemp;
	ForecastedHeatIndex forecastedHeatIndex;
	ForecastedWindChill forecastedWindChill;
	ForecastedWindDirection forecastedWindDirection;
	ForecastedWindSpeed forecastedWindSpeed;
	ForecastedWindGust forecastedWindGust;
	ForecastedPreciptiationChance forecastedPreciptiationChance;
	
	public WeatherGridInformation() {
		this.forecastedTemp = new ForecastedTemp();
		this.forecastedDewpoint = new ForecastedDewpoint();
		this.dailyTemperatureHigh = new DailyTemperatureHigh();
		this.dailyTemperatureLow = new DailyTemperatureLow();
		this.forecastedHumidity = new ForecastedHumidity();
		this.forecastedApparentTemp = new ForecastedApparentTemp();
		this.forecastedHeatIndex = new ForecastedHeatIndex();
		this.forecastedWindChill = new ForecastedWindChill();
		this.forecastedWindDirection = new ForecastedWindDirection();
		this.forecastedWindSpeed = new ForecastedWindSpeed() ;
		this.forecastedWindGust = new ForecastedWindGust();
		this.forecastedPreciptiationChance = new ForecastedPreciptiationChance();
	}
	
	public static class ForecastedTemp {
		public String uom;
		public ArrayList<WeatherValues> values;
	}
	
	public static class ForecastedDewpoint {
		public String uom;
		public ArrayList<WeatherValues> values;
	}
	
	public static class DailyTemperatureHigh {
		public String uom;
		public ArrayList<WeatherValues> values;
	}
	
	public static class DailyTemperatureLow {
		public String uom;
		public ArrayList<WeatherValues> values;
	}
	
	public static class ForecastedHumidity {
		public String uom;
		public ArrayList<WeatherValues> values;
	}
	
	public static class ForecastedApparentTemp {
		public String uom;
		public ArrayList<WeatherValues> values;
	}
	
	public static class ForecastedHeatIndex {
		public String uom;
		public ArrayList<WeatherValues> values;
	}
	
	public static class ForecastedWindChill {
		public String uom;
		public ArrayList<WeatherValues> values;
	}
	
	public static class ForecastedWindDirection {
		public String uom;
		public ArrayList<WeatherValues> values;
	}
	
	public static class ForecastedWindSpeed {
		public String uom;
		public ArrayList<WeatherValues> values;
	}
	
	public static class ForecastedWindGust {
		public String uom;
		public ArrayList<WeatherValues> values;
	}
	
	public static class ForecastedPreciptiationChance {
		public String uom;
		public ArrayList<WeatherValues> values;
	}

	/**
	 * @return the forecastedTemp
	 */
	public ForecastedTemp getForecastedTemp() {
		return forecastedTemp;
	}

	/**
	 * @return the forecastedDewpoint
	 */
	public ForecastedDewpoint getForecastedDewpoint() {
		return forecastedDewpoint;
	}

	/**
	 * @return the dailyTemperatureHigh
	 */
	public DailyTemperatureHigh getDailyTemperatureHigh() {
		return dailyTemperatureHigh;
	}

	/**
	 * @return the dailyTemperatureLow
	 */
	public DailyTemperatureLow getDailyTemperatureLow() {
		return dailyTemperatureLow;
	}

	/**
	 * @return the forecastedHumidity
	 */
	public ForecastedHumidity getForecastedHumidity() {
		return forecastedHumidity;
	}

	/**
	 * @return the forecastedApparentTemp
	 */
	public ForecastedApparentTemp getForecastedApparentTemp() {
		return forecastedApparentTemp;
	}

	/**
	 * @return the forecastedHeatIndex
	 */
	public ForecastedHeatIndex getForecastedHeatIndex() {
		return forecastedHeatIndex;
	}

	/**
	 * @return the forecastedWindChill
	 */
	public ForecastedWindChill getForecastedWindChill() {
		return forecastedWindChill;
	}

	/**
	 * @return the forecastedWindDirection
	 */
	public ForecastedWindDirection getForecastedWindDirection() {
		return forecastedWindDirection;
	}

	/**
	 * @return the forecastedWindSpeed
	 */
	public ForecastedWindSpeed getForecastedWindSpeed() {
		return forecastedWindSpeed;
	}

	/**
	 * @return the forecastedWindGust
	 */
	public ForecastedWindGust getForecastedWindGust() {
		return forecastedWindGust;
	}

	/**
	 * @return the forecastedPreciptiationChance
	 */
	public ForecastedPreciptiationChance getForecastedPreciptiationChance() {
		return forecastedPreciptiationChance;
	}	
	
	
	
}
