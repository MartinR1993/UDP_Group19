package Server;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UDPServer {
	
	public static void main(String[] args) throws Exception
	{
		DatagramSocket serverSocket = null;
		DatagramPacket receivePacket = null; 
		DatagramPacket outPacket = null; 
		byte[] receiveData;
		ArrayList<String> sentenceList = new ArrayList<String>();


		try{
			serverSocket = new DatagramSocket(9876);
			while(true){
				//Modtager pakke fra client
				receiveData = new byte[1024];
				receivePacket = new DatagramPacket(receiveData, receiveData.length);
				serverSocket.receive(receivePacket);

				// tilføjer vores besked til vores liste
				String sentence = new String(receivePacket.getData(), 0, receivePacket.getLength());
				sentenceList.add(sentence);

				// sender ACK
				Matcher matcher = Pattern.compile("\\d+").matcher(sentence);
				matcher.find();
				int packetNr = Integer.valueOf(matcher.group());
				String con = packetNr+"";
				byte[] confirm = con.getBytes();
				int port = receivePacket.getPort();
				InetAddress IPAddress = receivePacket.getAddress();
				outPacket = new DatagramPacket(confirm,confirm.length,IPAddress, port);
				serverSocket.send(outPacket);

				System.out.println("RECEIVED: "+ sentence);
				System.out.println("MESSAGE: "+ sentence.substring(con.length()+1));
			}
		}catch(IOException e){
			System.out.println(e);
		}
	}
}