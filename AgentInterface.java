package SmarPark;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

//L’agent Interface possède un GUI qui permet la saisi de la demande des clients : Pays, ville, chambre
//(single, double), nombre de personnes date de début et date de fin de séjour
public class AgentInterface extends GuiAgent {

	private static final long serialVersionUID = 1L;
	private fenetre f;
	private List<String> infosHotel = new ArrayList<String>();

//-----------------------------------------------------------------------------------------------//
	@Override
	protected void setup() {
		f = new fenetre("");
		f.setAgentInterface(this);
	}

//-----------------------------------------------------------------------------------------------//
	@Override
	protected void onGuiEvent(GuiEvent ev) {

		switch (ev.getType()) {
		case 0:

			Map<String, Object> params = (Map<String, Object>) ev.getParameter(0);
			String pays = (String) params.get("Pays");
			String ville = (String) params.get("Ville");
			String nbrPersonne = (String) params.get("Nombre de personnes");
			String dateDebut = (String) params.get("Date de début");
			String dateFin = (String) params.get("Date de fin");
			String chambre = (String) params.get("Chambre");
			Object[] demande = { pays, ville, nbrPersonne, dateDebut, dateFin, chambre };

			addBehaviour(new CyclicBehaviour() {
				// Agent Interface : après la sais des informations, l’agent cherche un agent AR
				// et lui envoie la demande de reservation.
				@Override
				public void action() {
					DFAgentDescription participants = new DFAgentDescription();
					ServiceDescription sde = new ServiceDescription();
					sde.setType("Reservation Hotel");
					participants.addServices(sde);
					DFAgentDescription[] result = null;
					try {
						result = DFService.search(myAgent, participants);
					} catch (FIPAException e) {
						e.printStackTrace();
					}
					AID[] AgentReservation = new AID[result.length];
					for (int i = 0; i < result.length; ++i) {
						AgentReservation[i] = result[i].getName();
					}
					ACLMessage msgRequest = new ACLMessage(ACLMessage.REQUEST);
					msgRequest.addReceiver(new AID(result[0].getName().getLocalName(), AID.ISLOCALNAME));
					try {
						msgRequest.setContentObject(demande);
					} catch (IOException e) {
						e.printStackTrace();
					}
					send(msgRequest);
//**********************************************************************************************************************************//
					ACLMessage msgInform = receive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
					if (msgInform != null) {
						try {
							infosHotel = (List<String>) msgInform.getContentObject();
						} catch (UnreadableException e) {
							e.printStackTrace();
						}
						f.showmsg(" Nom d'hotel : "+infosHotel.get(2)+"\n Pays : "+infosHotel.get(0)+"\n ville : "
								+infosHotel.get(1)+"\n Prix : "+infosHotel.get(3)+"Euro", false);
						doDelete();
					}

				}
			});
		}
	}
}
