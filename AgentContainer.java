package SmarPark;

import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;

public class AgentContainer {
	public static void main(String[] args) {
		try {
			Runtime rt = Runtime.instance();
			ProfileImpl pc = new ProfileImpl(false);
			pc.setParameter(ProfileImpl.MAIN_HOST, "localhost");
			jade.wrapper.AgentContainer container = rt.createAgentContainer(pc);
			AgentController agentReservation = container.createNewAgent("AgentReservation",
					AgentReservation.class.getName(), new Object[] {});
			AgentController agentHotel1 = container.createNewAgent("AgentHotel1", AgentHotel.class.getName(),
					new Object[][] { { "maroc", "casa","HotelXX" }, { "Double", 5, 50 }, { "Single", 20, 2000 } });
			AgentController agentHotel2 = container.createNewAgent("AgentHotel2", AgentHotel.class.getName(),
					new Object[][] { { "maroc", "casa","HotelYY" }, { "Double", 0, 500 }, { "Single", 0, 800 } });
			AgentController agentHotel3 = container.createNewAgent("AgentHotel3", AgentHotel.class.getName(),
					new Object[][] { { "france", "paris","HotelZZ" }, { "Double", 5, 1400 }, { "Single", 2, 1800 } });
			AgentController agentHotel4 = container.createNewAgent("AgentHotel4", AgentHotel.class.getName(),
					new Object[][] { { "maroc", "casa","HotelWW" }, { "Double", 10, 50 }, { "Single", 12, 300 } });
			AgentController agentInterface = container.createNewAgent("AgentInterface", AgentInterface.class.getName(),
					new Object[] {});
			agentReservation.start();
			agentHotel1.start();
			agentHotel2.start();
			agentHotel3.start();
			agentHotel4.start();
			agentInterface.start();
		} catch (ControllerException e) {
			e.printStackTrace();}}}
