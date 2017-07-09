
import java.util.ArrayList;

import jade.content.ContentElement;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.content.onto.basic.Result;
import jade.core.AID;
import jade.core.Agent;
import jade.core.ContainerID;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.FIPANames;
import jade.domain.JADEAgentManagement.CreateAgent;
import jade.domain.JADEAgentManagement.JADEManagementOntology;
import jade.domain.JADEAgentManagement.QueryPlatformLocationsAction;
import jade.domain.introspection.AMSSubscriber;
import jade.domain.introspection.AddedContainer;
import jade.domain.introspection.Event;
import jade.domain.introspection.IntrospectionVocabulary;
import jade.domain.introspection.RemovedContainer;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import jade.util.leap.LinkedList;
import jade.util.leap.List;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

public class AgentArchitect extends Agent{
	
	
	private int numAttackers;
	private static jade.wrapper.AgentContainer mainContainer;
	private GUI frame;
	//private int agentsAttackersDone;
	private long timeStartAttack;
	private static jade.core.Runtime rt;
	private int agentsToBeCreated;
	ReceiveMessage rm = new ReceiveMessage();
	
	public static void main(String [] args){
		startRma();
	}
	private ArrayList<ContainerID> availableContainers = new ArrayList<ContainerID>();
	public void setup()
	{
		frame = new GUI(this);
		frame.setVisible(true);
		AMSSubscriber subscriber = new AMSSubscriber(){
			 @Override
		      protected void installHandlers(java.util.Map handlers){
		        EventHandler addedHandler = new EventHandler(){
		          public void handle(Event event){
		        	  System.out.println("Adding Container");
		              AddedContainer addedContainer = (AddedContainer) event;
		              availableContainers.add(addedContainer.getContainer());
		          }
		        };
		    handlers.put(IntrospectionVocabulary.ADDEDCONTAINER,addedHandler);


		        EventHandler removedHandler = new EventHandler(){
		          public void handle(Event event){
		        	  System.out.println("Removing Container");
		              RemovedContainer removedContainer = (RemovedContainer) event;
		              ArrayList<ContainerID> temp = new ArrayList<ContainerID>(availableContainers);
		              for(ContainerID container : temp){
		                  if(container.getID().equalsIgnoreCase(removedContainer.getContainer().getID()))
		                      availableContainers.remove(container);
		              }
		          }
		        };
		        handlers.put(IntrospectionVocabulary.REMOVEDCONTAINER,removedHandler);
		      }

		
		    };
		    addBehaviour(subscriber);
		    
	}
	public void attack(int numAttackers, String ip, String port, String tickerTime, String fib)
	{
		addBehaviour(rm);
		agentsToBeCreated = numAttackers;
		this.numAttackers = numAttackers;
		String args = ip+" "+port+" "+fib+" "+tickerTime;
		createAgents(args);
		
	}
	public void startAttack()
	{
		timeStartAttack = System.currentTimeMillis();
		for(int i = 0; i < numAttackers ; i++)
		{ 
			SendMessageBehaviour sm = new SendMessageBehaviour("AgentAttacker"+i+"@Platform1","attack");
			this.addBehaviour(sm);
		}
		
	}
	public void stopAttack()
	{
		for(int i = 0; i < numAttackers ; i++)
		{
			SendMessageBehaviour sm = new SendMessageBehaviour("AgentAttacker"+i+"@Platform1","STOP");
			this.addBehaviour(sm);
		}
		//deleteAgents();
		frame.attackFinished(System.currentTimeMillis() - timeStartAttack);
	}
	
	// method for getting the containerids from the ams
protected List getContainerIDs(){
		ACLMessage msg2 = new ACLMessage(ACLMessage.REQUEST);
		msg2.setProtocol(jade.domain.FIPANames.InteractionProtocol.FIPA_REQUEST);
		msg2.setOntology(JADEManagementOntology.NAME);
		msg2.setLanguage(jade.domain.FIPANames.ContentLanguage.FIPA_SL0);
		try {
		    // send a request to the AMS
		    getContentManager().fillContent(msg2, new Action(getAMS(),
	                    new QueryPlatformLocationsAction()));
		    msg2.addReceiver(getAMS());
		    send(msg2);
		    
		    // wait for the answer from the ams
		    msg2 = blockingReceive(MessageTemplate.MatchOntology(
	                    JADEManagementOntology.NAME));
		    //extract the content and cast to type Result
		    ContentElement ce = getContentManager().extractContent(msg2);
		    Result res = null;
		    if (ce instanceof Result) {
			res = (Result) ce;
		    } else{
			return null;
		    }
		    
		    // make a list of all ContainerID's given in the result
		    jade.util.leap.Iterator it = res.getItems().iterator();
		    List result = new LinkedList();
		    while (it.hasNext()) {
			result.add((ContainerID) it.next());
		    }
		    return result;
		} catch (OntologyException e) {
		    return null;
		} catch(CodecException e){
		    return null;
		}
    }
	public static void startRma()
	{
		rt = jade.core.Runtime.instance();
		Profile profile = new ProfileImpl(null, 1099, "Platform1");
		mainContainer = rt.createMainContainer(profile);
        try {
			  AgentController rma = mainContainer.createNewAgent("rma", "jade.tools.rma.rma", null);
      	  	  rma.start();
			  AgentController architect =  mainContainer.createNewAgent("AgentArchitect", AgentArchitect.class.getName(), null);
			  architect.start();		  
		} catch (StaleProxyException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public void createAgents(String args)
	{
		ContainerID myContainerID = null;
		int counterAgentsCreated = 0;
		int counterAgentsPerMachine = numAttackers/availableContainers.size();
		for(ContainerID container : availableContainers){
			myContainerID = container;
			for(;counterAgentsCreated < numAttackers ; counterAgentsCreated++)
			{
				if(counterAgentsCreated==counterAgentsPerMachine)
				{
					counterAgentsPerMachine+=numAttackers/availableContainers.size();
					break;
				}
				// create the request to the ams
				CreateAgent ca = new CreateAgent();
				ca.setAgentName("AgentAttacker"+counterAgentsCreated);
				ca.setClassName(AgentAttacker.class.getName());
				String args2 = args + " "+ counterAgentsCreated;
				ca.addArguments(args2);
				ca.setContainer(myContainerID);
				
				Action a = new Action();
				a.setActor(getAMS());
				a.setAction(ca);
				ACLMessage AMSRequest = new ACLMessage(ACLMessage.REQUEST);
				AMSRequest.setSender(getAID());
				AMSRequest.addReceiver(getAMS());
				AMSRequest.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
				AMSRequest.setOntology(JADEManagementOntology.NAME);

				AMSRequest.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);

				try {
					getContentManager().registerLanguage(SLCodec.class.newInstance());
					getContentManager().registerOntology(JADEManagementOntology.getInstance());
					getContentManager().fillContent(AMSRequest, a);
					send(AMSRequest);
				} catch (InstantiationException | IllegalAccessException | CodecException | OntologyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					Thread.sleep(1, 0);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
        }
	}
	
	public class SendMessageBehaviour extends OneShotBehaviour {
    	String message;
    	String dest;
    	String ipDest;
    	public SendMessageBehaviour(String dest, String message)
        {
        	this.message = message;
        	this.dest = dest;
        }
		
    	private static final long serialVersionUID = -6375015618906642757L;

		public void action() {
			ACLMessage msg = null;
			msg = new ACLMessage(ACLMessage.INFORM);
			AID aid = new AID(dest, AID.ISGUID);
			msg.addReceiver(aid);
            msg.setLanguage("English");
            msg.setContent(message);
            send(msg);
            //System.out.println("****I Sent Message to::> "+ dest +" *****" + "\n" +
            //      "The Content of My Message is::>" + msg.getContent());
            this.done();    
        }
    }
	public synchronized void messageReceived(String senderName, String content)
	{
		if(content.equals("created"))
		{
			
			System.out.println("Agents left to be created:" + agentsToBeCreated);
			agentsToBeCreated--;
			if(agentsToBeCreated ==0)
			{
				removeBehaviour(rm);
				frame.sendLog("Agents created");
				frame.sendLog("Attacking...");
				frame.sendLog("Dont Add Remote platforms");
				this.startAttack();
			}
		}
	}
    public class ReceiveMessage extends CyclicBehaviour {
		private static final long serialVersionUID = 2L;
		// Variable to Hold the content of the received Message
        private String Message_Content;
        private String SenderName;
        public void action() {
            ACLMessage msg = receive();
            System.out.println("VEIRIFICANDO MENSAGEM RECEBIDA");
            if(msg != null) {
                Message_Content = msg.getContent();
                SenderName = msg.getSender().getLocalName();
                
                
                /*System.out.println("\n*************************\n");
                System.out.println(" ****I Received a Message***" +"\n"+
                        "The Sender Name is::>"+ SenderName+"\n"+
                        "The Content of the Message is::> " + Message_Content + "\n");
                System.out.println("ooooooooooooooooooooooooooooooooooooooo");*/
                AgentArchitect.this.messageReceived(SenderName, Message_Content);
            }

        }
    }
}
