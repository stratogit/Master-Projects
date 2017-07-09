

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

public class AgentAttacker extends Agent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String ip;
	private int port;
	private int myNum;
	private int tickerTime;
	private String fib;
	private ReceivedMessage rm;

	public void setup()
	{
		Object [] o = getArguments();
		String []args = ((String)o[0]).split(" "); 
		this.ip = args[0];//ip;
		this.port = Integer.parseInt(args[1]);//port;
		this.fib = args[2];//port;
		this.tickerTime = Integer.parseInt(args[3]);
		myNum  = Integer.parseInt(args[4]);//agentNumber;
		rm = new ReceivedMessage();
		addBehaviour(rm);
		System.out.println("AgentAttacker"+ myNum +" Created!!!");
		SendMessageBehaviour sm = new SendMessageBehaviour("AgentArchitect@Platform1","created");
		addBehaviour(sm);
	}

	public class ReceivedMessage extends CyclicBehaviour {

		private static final long serialVersionUID = 2L;

		public void action() {
			this.block(100);
			ACLMessage msg = receive();
			if(msg!=null)
			{
				System.out.println("Agent: "+myNum + "received message CONTENT:" +msg.getContent());
				if(msg.getContent().equals("STOP"))
				{
					this.done();
					AgentAttacker.this.doDelete();
				}
				
				AgentAttacker.this.addBehaviour(new TickerBehaviour(AgentAttacker.this, tickerTime) {
					protected void onTick() {
						//System.out.println("AgentAttacker"+ myNum +" Attacking!!!");
						Socket clientSocket = null;
						DataInputStream inFromServer;
						DataOutputStream outFromServer;
						try {
							clientSocket = new Socket(ip, port);
						} catch (UnknownHostException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						try {
							outFromServer = new DataOutputStream(clientSocket.getOutputStream());
							inFromServer = new DataInputStream(clientSocket.getInputStream());
							outFromServer.writeUTF(fib);
							String res = inFromServer.readUTF();
							System.out.println("AgentAttacker"+ myNum +". Result from Fib:" + res);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						try {
							clientSocket.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				} );
			}
		}
		
	}
	
	public class SendMessageBehaviour extends OneShotBehaviour {
    	String message;
    	String dest;
    	public SendMessageBehaviour(String dest, String message)
        {
        	this.message = message;
        	this.dest = dest;
        }
		
    	private static final long serialVersionUID = -6375015618906642757L;

		public void action() {
			ACLMessage msg = null;
			msg = new ACLMessage(ACLMessage.INFORM);
			msg.addReceiver(new AID(dest, AID.ISGUID));          
            msg.setLanguage("English");
            msg.setContent(message);
            send(msg);
            //System.out.println("****I Sent Message to::> "+ dest +" *****" + "\n" +
            //        "The Content of My Message is::>" + msg.getContent());
            this.done();    
        }
    }
}
