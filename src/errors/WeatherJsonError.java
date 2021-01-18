package errors;


@SuppressWarnings("serial")
public class WeatherJsonError extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1133396552376338297L;

	public WeatherJsonError(String string) {
		super(string);
	}
	

}
