package step4;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import step4.weatherStation.WeatherLogger;
import step4.weatherStation.WeatherStation;

public class WeatherServer {

	public static void main(String args[]) {
		
		String name = "//localhost/step4/WeatherStation";
		WeatherStation station;
		
		try {
			
			station = new WeatherStation();
			Registry reg = LocateRegistry.createRegistry(1099);
			reg.rebind(name, station);
			new WeatherLogger(station);
			
		} catch (RemoteException ex) {
			System.out.println("Failed to create the server");
			ex.printStackTrace();
		}
	}


}
