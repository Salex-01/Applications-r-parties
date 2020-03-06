package step4.weatherStation;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;


public class WeatherLogger extends UnicastRemoteObject implements IWeatherStats, Runnable {

	private static final int SIZE = 16;
	WeatherStation station;
	WeatherData datas[];
	int idx;

	Thread worker;

	public WeatherLogger(WeatherStation station) throws RemoteException {
		super();
		this.station = station;
		this.station.setLogger(this);
		datas = new WeatherData[SIZE];
		worker = new Thread(this, "logger");
		worker.start();
	}

	public WeatherData[] getStats() throws RemoteException {
		return datas;
	}

	public void run() {
		while (true) {
			try {
				datas[idx % SIZE] = station.getWeatherData();
				idx++;
				Thread.sleep(1000);
			} catch (Exception ex) {
			}
		}
	}
}
