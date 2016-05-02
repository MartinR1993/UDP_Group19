package Server;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Server {
	public static void main(String[] args){
		DatagramSocket socket = null;
		DatagramPacket inPacket = null; 
		DatagramPacket outPacket = null; 
		byte[] inBuf;
		final int PORT = 8888;
		ArrayList<String> modtaget = new ArrayList<String>();


		try{
			socket = new DatagramSocket(PORT);
			while(true){
				//Modtager pakke fra client
				inBuf = new byte[256];
				inPacket = new DatagramPacket(inBuf, inBuf.length);
				socket.receive(inPacket);

				// tilføjer vores besked til vores liste
				String receivedData = new String(inPacket.getData(), 0, inPacket.getLength());
				modtaget.add(receivedData);

				// sender confirm tilbage
				Matcher matcher = Pattern.compile("\\d+").matcher(receivedData);
				matcher.find();
				int packetNr = Integer.valueOf(matcher.group());
				String con = packetNr+"";
				byte[] confirm = con.getBytes();
				int source_port = inPacket.getPort();
				InetAddress source_address = inPacket.getAddress();
				outPacket = new DatagramPacket(confirm,confirm.length,source_address, source_port);
				socket.send(outPacket);

				System.out.println("Modtaget: "+ receivedData);
				System.out.println("Besked: "+ receivedData.substring(con.length()+1));
			}
		}catch(IOException e){

		}
	}
}