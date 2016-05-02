package Client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Client {
	static DatagramSocket socket = null;
	static ArrayList<Integer> packetNrList  = new ArrayList<Integer>();
	public static void main(String[] args) throws IOException {

		Thread Send = new Thread(new Runnable() {
			public void run() {

				DatagramPacket outPacket = null;
				byte[] outBuf;
				final int PORT = 8888;
				ArrayList<String> messageList  = new ArrayList<String>();
				int sendCounter = new Random().nextInt(1000)+1;
				String msg;

				while (true) {
					try {
						InetAddress address = InetAddress.getByName("localhost");
						socket = new DatagramSocket();



						System.out.println("skriv tekst");
						Scanner scan = new Scanner(System.in);
						msg = scan.nextLine();

						String packet = sendCounter+"." + msg;
						messageList.add(packet);
						packetNrList.add(sendCounter);

						//Laver besked til bytes og sender
						outBuf = packet.getBytes();
						outPacket = new DatagramPacket(outBuf, outBuf.length,address, PORT);
				
						System.out.println("sender: " + msg);
						System.out.println("nummer sendt(message): " +sendCounter );
						socket.send(outPacket);

						sendCounter++;					
					
						System.out.println(packetNrList.size()+"");
						
					} catch (IOException e) {
					}			
				}

			}
		});

		
		Thread receive = new Thread(new  Runnable() {
			public void run() {

				DatagramPacket inPacket = null;
				byte[] inBuf;

				while (true) {

					try {

						inBuf = new byte[256];
						inPacket = new DatagramPacket(inBuf, inBuf.length);

						socket.receive(inPacket);

						String data = new String(inPacket.getData(), 0, inPacket.getLength());

						int receivedNumber = Integer.parseInt(data);
						packetNrList.remove(Integer.valueOf(receivedNumber));

						System.out.println("nummer modtaget: "+data);	

					} catch (IOException | NullPointerException e) {
					}
				}
			}
		});

		Send.start();
		receive.start();

	}

}
