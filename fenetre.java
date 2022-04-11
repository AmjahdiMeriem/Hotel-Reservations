package SmarPark;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import jade.gui.GuiEvent;

public class fenetre extends JFrame {

	private static final long serialVersionUID = 1L;

	AgentInterface agentInterface = new AgentInterface();
	JTextArea JTextAreaMess = new JTextArea();

	fenetre(String titre) {
		super(titre);
//-----------------------------------------------------------------------------------------------------//
		JPanel pWestCenter = new JPanel();
		GridLayout b3 = new GridLayout(10, 0);
		pWestCenter.setLayout(b3);
//-----------------------------------------------------------------------------------------------------//
		String[] tab1 = { "Pays", "Ville", "Nombre de personnes", "Date de début", "Date de fin" };
		JTextField[] ts = new JTextField[5];
		for (int i = 0; i < tab1.length; i++) {
			JLabel l = new JLabel(tab1[i]);
			pWestCenter.add(l);
			JTextField t = new JTextField();
			ts[i] = t;
			pWestCenter.add(t);
		}
		
//-----------------------------------------------------------------------------------------------------//
		JPanel panCenterSouth = new JPanel();
//-----------------------------------------------------------------------------------------------------//
		JLabel chambre = new JLabel("Chambre");
		panCenterSouth.add(chambre);
		ButtonGroup group = new ButtonGroup();
		JRadioButton j1;
		JRadioButton j2;
		j1 = new JRadioButton("Single");
		group.add(j1);
		j2 = new JRadioButton("Double");
		group.add(j2);
		panCenterSouth.add(j1);
		panCenterSouth.add(j2);
//-----------------------------------------------------------------------------------------------------//
		JPanel pWestSouth = new JPanel();
		FlowLayout f = new FlowLayout();
		pWestSouth.setLayout(f);
//-----------------------------------------------------------------------------------------------------//
		String[] tab2 = { "Rechercher", "Annuler" };
		JButton[] but = new JButton[tab2.length];
		for (int i = 0; i < tab2.length; i++) {
			but[i] = new JButton(tab2[i]);
			pWestSouth.add(but[i]);
		}
//-----------------------------------------------------------------------------------------------------//
		JPanel pWest = new JPanel();
		BorderLayout b2 = new BorderLayout();
		pWest.setLayout(b2);
		pWest.setBorder(BorderFactory.createTitledBorder("Demande"));
//-----------------------------------------------------------------------------------------------------//
		pWestCenter.add(panCenterSouth, BorderLayout.CENTER);
		pWestCenter.add(pWestSouth, BorderLayout.SOUTH);
		pWest.add(pWestCenter, BorderLayout.CENTER);
//-----------------------------------------------------------------------------------------------------//
		JPanel p = new JPanel();
		BorderLayout b1 = new BorderLayout();
		p.setLayout(b1);
		
		JTextAreaMess.setEditable(false);
		JScrollPane sc = new JScrollPane(JTextAreaMess);
		p.add(sc, BorderLayout.CENTER);
		p.add(pWest, BorderLayout.WEST);
		this.add(p);
		
		this.setPreferredSize(new Dimension(800, 700));
		this.pack();
		this.setVisible(true);
		this.setDefaultCloseOperation(fenetre.EXIT_ON_CLOSE);
//-----------------------------------------------------------------------------------------------------//
		but[0].addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				GuiEvent gev = new GuiEvent(this, 0);
				Map<String, Object> demande = new HashMap<>();
				for(int i=0;i<ts.length;i++) {
					demande.put(tab1[i], ts[i].getText());
				}
				if (j1.isSelected() == true)
					demande.put("Chambre", j1.getText());
				if (j2.isSelected() == true)
					demande.put("Chambre", j2.getText());
				gev.addParameter(demande);
				agentInterface.onGuiEvent(gev);
			}});
//-----------------------------------------------------------------------------------------------------//
			but[1].addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					for (int i = 0; i < ts.length; i++) {
						ts[i].setText("");
					}
					group.clearSelection();	
				}
		});
	}
//-----------------------------------------------------------------------------------------------------//
	public void showmsg(Object msg,boolean b) {
		if(b==true) {
			JTextAreaMess.append((String) msg);
		}else {
			JTextAreaMess.setText((String) msg);
		}
	}
//-----------------------------------------------------------------------------------------------------//
	
	public AgentInterface getAgentInterface() {
		return agentInterface;
	}

	public void setAgentInterface(AgentInterface agentInterface) {
		this.agentInterface = agentInterface;
	}
//-----------------------------------------------------------------------------------------------------//

}
