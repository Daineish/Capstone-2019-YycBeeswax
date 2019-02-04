
import java.io.*;
import java.net.*;

class TestServer
{
   public static void main(String args[]) throws Exception
      {
	   	int localPort = 12975;
	   	DatagramSocket serverSocket = new DatagramSocket(null);
	    InetSocketAddress serverAddress = new InetSocketAddress("216.219.115.12", localPort);
	    serverSocket.bind(serverAddress);
         byte[] receiveData = new byte[1024];
         int tick = 0;
         System.out.println("loop starts");
         while(true)
         {
	          DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
	          serverSocket.receive(receivePacket);
	          String sentence = new String( receivePacket.getData());
	          System.out.println("RECEIVED: " + sentence);
	          InetAddress IPAddress = receivePacket.getAddress();
	          int port = receivePacket.getPort();
	          System.out.println("FROM IP: " + IPAddress + "with port " + port);
	          tick++;
	          if (tick >=10){
	        	  serverSocket.close();
	        	  System.out.println("session closed!");
	          }
	          System.out.println("completed loop" + tick + "times!");
         }
	          
      }
}