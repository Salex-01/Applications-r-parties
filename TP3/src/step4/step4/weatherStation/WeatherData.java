package step4.weatherStation;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;

public class WeatherData implements Serializable {

	int temperature;
	int windSpeed;
	String windDirection;
	Date date;
	
	public Date getDate() {
		return date;
	}
	
	public int getTemperature(int unit) {
		return temperature;
	}
	
	public int getWindSpeed() {
		return windSpeed;
	}
	
	/**
	 * 
	 * @return	"N","NW","W","SW","S","SE","E","NE" 
	 * @throws RemoteException
	 */
	public String getWindDirection() {
		return windDirection;
	}

}
