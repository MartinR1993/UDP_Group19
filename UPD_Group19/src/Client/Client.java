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
	static ArrayList<String> messageList  = new ArrayList<String>();
	static final int PORT = 8888;

	public static void main(String[] args) throws IOException, InterruptedException {

		Thread Send = new Thread(new Runnable() {
			public void run() {

				DatagramPacket outPacket = null;
				byte[] outBuf;
				int sendCounter = new Random().nextInt(1000)+1;
				String msg;

				while (true) {
					try {
						InetAddress address = InetAddress.getByName("localhost");
						socket = new DatagramSocket();

						Scanner scan = new Scanner(System.in);
						msg = scan.nextLine();
						
						String packet = sendCounter+"-" + msg;
						messageList.add(packet);

						//Laver besked til bytes og sender
						outBuf = packet.getBytes();
						outPacket = new DatagramPacket(outBuf, outBuf.length,address, PORT);

						System.out.println("Sender: " + msg);
						System.out.println("Nummer sendt(message): " +sendCounter );
						socket.send(outPacket);

						sendCounter++;					

						System.out.println("Pakker uden kvittering for modtagelse før: " + messageList.size());


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

						for (int i = 0; i < messageList.size(); i++) {
							String findString = messageList.get(i);
							String[] splittedString = findString.split("-");


							if (splittedString[0].equals(data)){
								messageList.remove(i);
							}
						}
						System.out.println("Nummer modtaget: "+data);

						System.out.println("Pakker uden kvittering for modtagelse: " + messageList.size());

					} catch (IOException | NullPointerException e) {
					}
				}
			}
		});

		Send.start();
		receive.start();		
		
		while(!messageList.isEmpty()){
			DatagramPacket hej = null;
			for (int i = 0; i < messageList.size(); i++) {
				messageList.get(i);
				byte[] Boh = messageList.get(i).getBytes();
				InetAddress address = InetAddress.getByName("localhost");
				hej = new DatagramPacket(Boh, Boh.length,address, PORT);
				
				socket.send(hej);
			}
		}
	}

}