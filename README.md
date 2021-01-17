# WeatherAppJava
 Weather application using NWS information.
  
 APIs used:
   - MapQuest Geocoding: Obtaining Latitude and Longitude for locations
   - National Weather Service: Obtaining forecast information for locations
   
 External JARs used:
   - Jsoup: Webscrapping NWS website and parsing MapQuest XML file
   - Gson: Reading and convering NWS forecast information into Java object
   
# How it works
  The user inputs a location into the Location TextField and presses the Go button. The requested location is then passed through the MapQuest API to obtain Lat/Long coordinates. Once the Lat/Long coordinates have been obtained, they are used in the NWS API to obtain forecasted weather data for the requested location. This data is then transformed into a Java object named WeatherPeriod by using Gson.
  
  Once the work with the API is completed, the program used Jsoup to scrape the NWS website with the requested location to obtain the current weather information (temperature, humidity, wind speed, etc.). This information is stored in a Java object named Weather. 
  
  After the Java objects have been created, the program then updates the JavaFX nodes in the MainGUI class with the updated weather details. The background image of the program will change depending on the weather condition (cloudy, sunny, etc.). This is done by entering the required folder containing the pictures and then selecting a random file.
