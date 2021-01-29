package errors;

public class WeatherJsonError extends Exception {

	private static final long serialVersionUID = 1133396552376338297L;

	/**
	 * An error relating to reading the NWS Json file
	 * 
	 * @param string - The error message
	 */
	public WeatherJsonError(String string) {
		super(string);
	}
}
