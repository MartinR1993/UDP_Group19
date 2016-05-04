package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Random;

public class UDPClient {
	public static void main(String[] args) throws IOException, InterruptedException {
		DatagramSocket clientSocket = new DatagramSocket();
		ArrayList<String> messageList  = new ArrayList<String>();

		// denne tråd står for oprettelse og afsendelse af pakker
		Thread sendThread = new Thread(new Runnable() {
			public void run() {				

				DatagramPacket sendPacket = null;
				byte[] sendData;
				int packetID = new Random().nextInt(1000)+1;
				String sentence;
				boolean createPack = true;

				// hvis der ikke er igang med at blive afsendt noget kan vi tilføje beskeder som skal sendes.
				if (messageList.isEmpty()) {

					while (createPack) {
						BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
						try {
							sentence = inFromUser.readLine();
							String packet = packetID+"-" + sentence;

							messageList.add(packet);

							// det sidste brugeren skal skrive er slut, denne besked vil dog ikke blive sendt
							if (sentence.equals("slut")) {
								messageList.remove(messageList.size()-1);
								createPack = false;
							}

						} catch (IOException e) {
							System.out.println(e);
						}
						packetID++;
					}
				}


				// prøver at sende alle vores pakker 1 gang.
				System.out.println("Pakker uden kvittering for modtagelse før: " + messageList.size());
				while (!messageList.isEmpty()) {
					for (int i = 0; i < messageList.size(); i++) {
						try {
							InetAddress address = InetAddress.getByName("localhost");

							//Laver besked til bytes og sender
							sendData = messageList.get(i).getBytes();
							sendPacket = new DatagramPacket(sendData, sendData.length,address, 9876);


							System.out.println("Sender: " + messageList.get(i));
							clientSocket.send(sendPacket);

							packetID++;					

						} catch (IOException e) {
							System.out.println(e);
						}		
					}

					// venter på ACK et par sekunder før den igen prøver at sende de pakker hvor ACK ikke er modtaget
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						System.out.println(e);
					}
				}
			}


		});

		// denne tråd står for modtagelse af pakker
		Thread receiveThread = new Thread(new  Runnable() {
			public void run() {

				DatagramPacket receivePacket = null;
				byte[] receiveData;

				while (true) {

					try {

						receiveData = new byte[1024];
						receivePacket = new DatagramPacket(receiveData, receiveData.length);

						clientSocket.receive(receivePacket);

						String data = new String(receivePacket.getData(), 0, receivePacket.getLength());

						// fjerner de pakker som vi har fået kvittering for
						for (int i = 0; i < messageList.size(); i++) {
							String findString = messageList.get(i);
							String[] splittedString = findString.split("-");

							if (splittedString[0].equals(data)){
								messageList.remove(i);
							}

						}
						System.out.println("Nummer modtaget: "+data);

						System.out.println("Pakker uden kvittering for modtagelse: " + messageList.size());

					} catch (IOException e) {
						System.out.println(e);
					}
				}
			}
		});

		sendThread.start();
		receiveThread.start();		
		
		if (!sendThread.isAlive() && !receiveThread.isAlive()) {
			clientSocket.close();
		}
		
		
	}

}