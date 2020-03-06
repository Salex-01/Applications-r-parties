package step4.weatherStation;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
import java.util.Random;

public class WeatherStation extends UnicastRemoteObject implements
		IWeatherStation {

	Random gen;
	String dirs[] = { "N", "NW", "W", "SW", "S", "SE", "E", "NE" };

	public WeatherStation() throws RemoteException {
		super();
		gen = new Random();
	}

	public int getTemperature(int unit) throws RemoteException {
		return gen.nextInt() % 20;
	}

	public int getWindSpeed() throws RemoteException {
		return gen.nextInt() % 10;
	}

	public String getWindDirection() throws RemoteException {
		int idx = gen.nextInt() % 8;
		if (idx<0) idx = -idx;
		return dirs[idx];
	}

	public WeatherData getWeatherData() throws RemoteException {
		WeatherData data = new WeatherData();
		data.temperature = getTemperature(MPS);
		data.windSpeed = getWindSpeed();
		data.windDirection = getWindDirection();
		data.date = new Date(System.currentTimeMillis());
		return data;
	}

	public IWeatherStats getStatLogger() throws RemoteException {
		return logger;
	}
	
	IWeatherStats logger;
	public void setLogger(IWeatherStats logger) {
		this.logger = logger;
	}
}
