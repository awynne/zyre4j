

import org.zeromq.czmq.Zmsg;
import org.zeromq.zyre.Zyre;
import static java.lang.System.*;

import java.util.Scanner;

public class CtrlcTest {

	private static Zyre zyre = new Zyre("fred");

	public static void main(String[] args) throws Exception {
		zyre.start();
		out.println("started");
		
		MyThread mt = new MyThread();
		mt.start();
		mt.join();

		Thread.sleep(100);
		zyre.close();
	}
	
	public static class MyThread extends Thread {
		
		public void run() {
			Scanner scanner = new Scanner(in);
			while(true) {
				out.print("type \"exit\" to terminate: ");
				String line = scanner.nextLine();
				if (line.equals("exit")) {
					zyre.stop();
					break;
				}
			}
		}
		
	}

}
