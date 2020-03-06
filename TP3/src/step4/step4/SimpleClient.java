package step4;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import step4.weatherStation.IWeatherStation;
import step4.weatherStation.IWeatherStats;
import step4.weatherStation.WeatherData;


public class SimpleClient {

	public static void main(String args[]) {
		String name = "//localhost/step4/WeatherStation";
		new SimpleClient(name);
	}
	
	SimpleClient(String name) {
		WeatherData data;
		try {
			
			IWeatherStation station;
 			
 			Registry reg = LocateRegistry.getRegistry(1099);
 			station = (IWeatherStation) reg.lookup(name);
			
			System.out.println("station="+station.toString());
			System.out.println("-------------");
			
			data = station.getWeatherData();
			
			System.out.println("Temp="+data.getTemperature(IWeatherStation.MPS));
			System.out.println("Wind speed="+data.getWindSpeed()+" direction="+data.getWindDirection());
			System.out.println("-------logs------");
			IWeatherStats logger = station.getStatLogger();

			WeatherData datas[];
			datas = logger.getStats();
			if (datas!=null)
				for (int i=0;i<datas.length;i++) {
					data = datas[i];
					if (data==null)
						continue;
					System.out.println("date="+data.getDate());
					System.out.println("Temp="+data.getTemperature(IWeatherStation.MPS));
					System.out.println("Wind speed="+data.getWindSpeed()+" direction="+data.getWindDirection());
				}
			
		} catch (RemoteException ex) {
			System.out.println("Failed to create the server");
			ex.printStackTrace();
		} catch (NotBoundException ex) {
			System.out.println("Unknown station "+name);
		}
	}
}
