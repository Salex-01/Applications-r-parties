package step4.weatherStation;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IWeatherStats extends Remote {
	
	WeatherData[] getStats() throws RemoteException;
}
