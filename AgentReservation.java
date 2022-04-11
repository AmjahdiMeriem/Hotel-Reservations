package SmarPark;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public class AgentReservation extends Agent {
//------------------------------------------------------------------------------------------------------------------//
	private static final long serialVersionUID = 1L;
	private Object[] demande;
	private List<Integer> prix = new ArrayList<Integer>();
	private List<Integer> nbr = new ArrayList<Integer>();
	private int compteur = 0;
	List<String> infosHotel = new ArrayList<String>();

	@Override
	protected void setup() {
//------------------------------------------------------------------------------------------------------------------//
		// Les agents de Réservation s'enregistre auprès de l'agent SMA et publie leur
		// service auprès de l'agent
		// DF sous le nom « Reservation Hotel ».
		try {

			DFAgentDescription Afd = new DFAgentDescription();
			Afd.setName(getAID());

			ServiceDescription sd = new ServiceDescription();
			sd.setType("Reservation Hotel");
			sd.setName("Reservation Hotel");
			Afd.addServices(sd);

			DFService.register(this, Afd);

		} catch (FIPAException e) {
			e.printStackTrace();
		}
//------------------------------------------------------------------------------------------------------------------//
		// S'il reçoit plusieurs offre il choisi la meilleure offre de point de vue prix
		// puis selon le
		// nombre de chambres libres si même prix.
		ParallelBehaviour paralleleBehaviour = new ParallelBehaviour();
		addBehaviour(paralleleBehaviour);

//-------------------------------------------------------------------------------------------------------------------//		
		paralleleBehaviour.addSubBehaviour(new CyclicBehaviour() {

			@Override
			public void action() {
				ACLMessage msgRequest = receive(MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
				if (msgRequest != null) {
					try {
						demande = (Object[]) msgRequest.getContentObject();
					} catch (UnreadableException e) {
						e.printStackTrace();
					}

					msgRequest = null;
//------------------------------------------------------------------------------------------------------------------//
					// il demande la la liste des AH et leur envoie un message CFP. dans
					// tickerBehaviour

					DFAgentDescription participants = new DFAgentDescription();
					ServiceDescription sde = new ServiceDescription();
					sde.setType("Hotel");
					participants.addServices(sde);
					DFAgentDescription[] result = null;
					try {
						result = DFService.search(myAgent, participants);
					} catch (FIPAException e) {
						e.printStackTrace();
					}
					AID[] AgentHotel = new AID[result.length];
					for (int i = 0; i < result.length; ++i) {
						AgentHotel[i] = result[i].getName();
					}

					ACLMessage msgCFP = new ACLMessage(ACLMessage.CFP);

					try {
						msgCFP.setContentObject(demande);
					} catch (IOException e) {
						e.printStackTrace();
					}
					for (int i = 0; i < result.length; i++) {
						msgCFP.addReceiver(new AID(result[i].getName().getLocalName(), AID.ISLOCALNAME));
						send(msgCFP);

					}

//--------------------------------------------------------------------------------------------------------------------------//

				} else {
					block();
				}

			}
		});

		paralleleBehaviour.addSubBehaviour(new CyclicBehaviour() {
			List<Object> addrsHotel = new ArrayList<Object>();

			@Override
			public void action() {
				ACLMessage msgPropose = receive(MessageTemplate.MatchPerformative(ACLMessage.PROPOSE));
				if (msgPropose != null) {
					List<Integer> offres = new ArrayList<Integer>();
					for (int i = 0; i < addrsHotel.size(); i++) {
						if (msgPropose.getSender().equals(addrsHotel.get(i))) {
							compteur = 1;
						}
					}
					if (compteur != 1) {
						addrsHotel.add(msgPropose.getSender());
						try {
							offres = (List<Integer>) msgPropose.getContentObject();
							prix.add(offres.get(0));
							nbr.add(offres.get(1));
						} catch (UnreadableException e) {
							e.printStackTrace();
						}
					} else {
						int prixMin = prix.get(0);
						Object ag = addrsHotel.get(0);
						for (int i = 1; i < prix.size(); i++) {

							if (prix.get(i) < prixMin) {
								prixMin = prix.get(i);
								ag = addrsHotel.get(i);
							}
						}
						int[] indice =new int[prix.size()];
						for (int i = 0; i < prix.size(); i++) {
							if(prix.get(i)==prixMin) {
								indice[i]=i;
							}
						}
						if(indice.length>0) {
						int nbrMax=nbr.get(indice[0]);
						for(int i=0;i<indice.length;i++) {
							if(nbr.get(indice[i])>nbrMax) {
								nbrMax=nbr.get(indice[i]);
								ag = addrsHotel.get(indice[i]);
							}
						}}
						System.out.println(prixMin);
						ACLMessage msgAccept = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
						ACLMessage msgRefuse = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
						msgAccept.addReceiver(new AID(((AID) ag).getLocalName(), AID.ISLOCALNAME));
						send(msgAccept);

						for (int i = 0; i < addrsHotel.size(); i++) {
							if (addrsHotel.get(i) != ag) {
								msgAccept.addReceiver(
										new AID(((AID) addrsHotel.get(i)).getLocalName(), AID.ISLOCALNAME));
								send(msgRefuse);
							}
						}

					}

					ACLMessage msgConfirm = receive(MessageTemplate.MatchPerformative(ACLMessage.CONFIRM));
					if (msgConfirm != null) {
						try {
							infosHotel = (List<String>) msgConfirm.getContentObject();
						} catch (UnreadableException e) {
							e.printStackTrace();
						}
						System.out.println(infosHotel.get(0));

						ACLMessage msgInform = new ACLMessage(ACLMessage.INFORM);

						try {
							msgInform.setContentObject((Serializable) infosHotel);
						} catch (IOException e) {
							e.printStackTrace();
						}
						msgInform.addReceiver(new AID("AgentInterface", AID.ISLOCALNAME));
						send(msgInform);
						doDelete();

					}

//--------------------------------------------------------------------------------------------------------------------------//

				} else {
					block();
				}

			}
		});

	}
}
