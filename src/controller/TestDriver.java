package controller;

import model.Weather;

public class TestDriver {

	public static void main(String[] args) {
		Weather weather = new Weather("21122");
		
		System.out.println(weather.getCurrentTemp());
		System.out.println(weather.getCurrentCondition());
	}
}
