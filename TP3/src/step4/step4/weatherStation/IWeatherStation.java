package step4.weatherStation;

import java.rmi.Remote;
import java.rmi.RemoteException;

import step4.weatherStation.WeatherData;

public interface IWeatherStation extends Remote {
	
	int MPH=0;
	int KTS=1;
	int MPS=2;

	int getTemperature(int unit) throws RemoteException;
	int getWindSpeed() throws RemoteException;
	
	/**
	 * 
	 * @return	"N","NW","W","SW","S","SE","E","NE" 
	 * @throws RemoteException
	 */
	String getWindDirection() throws RemoteException;
	
	WeatherData getWeatherData() throws RemoteException;
	
	IWeatherStats getStatLogger() throws RemoteException;

}
