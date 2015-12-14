
import org.zeromq.czmq.Zmsg;
import org.zeromq.zyre.Zyre;
import static java.lang.System.*;

import java.util.Scanner;

public class CtrlcTest {

	private static Zyre zyre = new Zyre("fred");

	public static void main(String[] args) throws Exception {
		Runtime.getRuntime().addShutdownHook(new ShutdownThrd());
		zyre.start();
		out.println("started");
		
		//MyThread mt = new MyThread();
		//mt.start();
		//mt.join();

		Thread.sleep(100000);
		zyre.close();
	}
	
	public static class MyThread extends Thread {
		
		public void run() {
			Scanner scanner = new Scanner(in);
			try {
			while(true) {
				out.print("type \"exit\" to terminate: ");
				Thread.sleep(60000);
				String line = scanner.nextLine();
				if (line != null && line.equals("exit")) {
					zyre.stop();
					break;
				}
			}
			}
			catch(Exception e) {
				out.println("hey");
				e.printStackTrace();
			}
		}
		
	}
	
	public static class ShutdownThrd extends Thread {
		public void run() {
			out.println("Shutting down...");
			//zyre.stop();
		}
	}

}
