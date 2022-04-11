package SmarPark;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public class AgentHotel extends Agent {

	private static final long serialVersionUID = 1L;

	@Override
	protected void setup() {
//-------------------------------------------------------------------------------------------------------------------//
		// Les agents de Hotel s'enregistrent auprès de l'agent SMA et publie leur
		// service auprès de l'agent DF sous le nom « Hotel »
		try {

			DFAgentDescription Afd = new DFAgentDescription();
			Afd.setName(getAID());

			ServiceDescription sd = new ServiceDescription();
			sd.setType("Hotel");
			sd.setName("Hotel");
			Afd.addServices(sd);

			DFService.register(this, Afd);
			System.out.println(getLocalName() + " Enregistrement dans l'annuaire DF");

		} catch (FIPAException e) {
			e.printStackTrace();
		}
//-------------------------------------------------------------------------------------------------------------------//
		Map<String, Integer> nbr = new HashMap<String, Integer>();
		Map<String, Integer> prix = new HashMap<String, Integer>();
		List<String> infosHotel = new ArrayList<String>();
		try {
			Object[][] args = (Object[][]) getArguments();

			if (args != null) {

				nbr.put((String) args[1][0], (Integer) args[1][1]);
				nbr.put((String) args[2][0], (Integer) args[2][1]);

				prix.put((String) args[1][0], (Integer) args[1][2]);
				prix.put((String) args[2][0], (Integer) args[2][2]);

				infosHotel.add((String) args[0][0]);
				infosHotel.add((String) args[0][1]);
				infosHotel.add((String) args[0][2]);
			} else {
				System.out.println("pas d'arguments ?");
				doDelete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
//-------------------------------------------------------------------------------------------------------------------//
		// Je peut publier ces services dans OnshotBehaviour ????
//-------------------------------------------------------------------------------------------------------------------//
		// Agent Hotel, attend la demande de l’AR et envoi le prix de la chambre, nombre
		// de chambres libres
		// similaires (si non nul), la disponibilité dans un message PROPOSE
		addBehaviour(new CyclicBehaviour() {

			@Override
			public void action() {
				ACLMessage msgCFP = receive(MessageTemplate.MatchPerformative(ACLMessage.CFP));
				Object[] demande = null;
				if (msgCFP != null) {
					try {
						demande = (Object[]) msgCFP.getContentObject();
					} catch (UnreadableException e) {
						e.printStackTrace();
					}
					if (demande[0].equals(infosHotel.get(0)) && demande[1].equals(infosHotel.get(1))) {

						if (nbr.get(demande[5]) != 0) {

							List<Integer> list2 = new ArrayList<Integer>();
							list2.add(prix.get(demande[5]));
							list2.add(nbr.get(demande[5]));
							infosHotel.add(prix.get(demande[5]).toString());
							int[] list = { prix.get(demande[5]), nbr.get(demande[5]) };
							ACLMessage reply = msgCFP.createReply();
							reply.setPerformative(ACLMessage.PROPOSE);
							try {
								reply.setContentObject((Serializable) list2);
							} catch (IOException e) {
								e.printStackTrace();
							}
							myAgent.send(reply);
						} else {

							doDelete();

						}
						ACLMessage msgAccept = receive(MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL));
						if (msgAccept != null) {
							nbr.put((String) demande[5], nbr.get(demande[5]) - 1);
							ACLMessage reply = msgAccept.createReply();
							reply.setPerformative(ACLMessage.CONFIRM);
							try {
								reply.setContentObject((Serializable) infosHotel);
							} catch (IOException e) {
								e.printStackTrace();
							}

							myAgent.send(reply);
							doDelete();
						}
						ACLMessage msgRefuse = receive(MessageTemplate.MatchPerformative(ACLMessage.REJECT_PROPOSAL));
						if (msgRefuse != null) {
							doDelete();
						}

					} else {
						block();
					}

				}
			}

		});
	}

	// si je supprime pas les services les autres agents vont trouvé des services
	// d'un agent qui n'existe pas.
	@Override
	protected void takeDown() {
		try {
			DFService.deregister(this);
		} catch (FIPAException e) {
			e.printStackTrace();
		}
	}

}
