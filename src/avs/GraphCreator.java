package avs;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.Color;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;

import javax.swing.*;
import javax.swing.JFrame; 
import javax.swing.JButton;
import javax.swing.JTextField;

import java.util.ArrayList;

public class GraphCreator implements ActionListener, MouseListener {

	//Creation of frame, buttons, containers, etc.
	JFrame frame = new JFrame();
	GraphPanel panel = new GraphPanel();
	JButton nodeB = new JButton("Node");
	JButton edgeB = new JButton("Edge");
	JTextField labelsTF = new JTextField("A");
	JTextField firstNode = new JTextField("First");
	JTextField secondNode = new JTextField("Second");
	JButton connectedB = new JButton("Check Connection");
	Container west = new Container();
	Container east = new Container();
	Container south = new Container();
	JTextField salesmanTF = new JTextField("A");
	JButton salesmanB = new JButton("Traveling Salesman");
	final int NODE_CREATE = 0;
	final int EDGE_FIRST = 1;
	final int EDGE_SECOND = 2;
	int state = NODE_CREATE;
	Node first = null;
	ArrayList<ArrayList<Node>> traveling = new ArrayList<ArrayList<Node>>();

	public GraphCreator() {
		//sets up & inits all parts of the jframe
		frame.setSize(800,600);
		frame.setLayout(new BorderLayout());
		frame.add(panel, BorderLayout.CENTER);
		west.setLayout(new GridLayout(3,1));
		west.add(nodeB);
		nodeB.addActionListener(this);
		nodeB.setBackground(Color.GREEN);
		west.add(edgeB);
		edgeB.addActionListener(this);
		edgeB.setBackground(Color.LIGHT_GRAY);
		west.add(labelsTF);
		frame.add(west, BorderLayout.WEST);
		east.setLayout(new GridLayout(3,1));
		east.add(firstNode);
		east.add(secondNode);
		east.add(connectedB);
		connectedB.addActionListener(this);
		frame.add(east, BorderLayout.EAST);
		panel.addMouseListener(this);
		south.setLayout(new GridLayout(1,2));
		south.add(salesmanTF);
		south.add(salesmanB);
		salesmanB.addActionListener(this);
		frame.add(south, BorderLayout.SOUTH);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		new GraphCreator();
	}

	public void mouseReleased(MouseEvent e) {
		//creates node
		if (state == NODE_CREATE) {
			panel.addNode(e.getX(), e.getY(), labelsTF.getText());
		}
		//creates edge
		else if (state == EDGE_FIRST) {
			Node n = panel.getNode(e.getX(), e.getY());
			if (n != null) {
				first = n;
				state = EDGE_SECOND;
				//add highlighting
				n.setHighlighted(true);
			}
		}
		else if (state == EDGE_SECOND) {
			Node n = panel.getNode(e.getX(), e.getY());
			if (n != null && first.equals(n) == false) {
				//remove highlighting
				String s = labelsTF.getText();
				boolean valid = true;
				for (int a = 0; a < s.length(); a++) {
					if (Character.isDigit(s.charAt(a)) == false) {
						valid = false;
					}
				}
				if (valid == true) {
					first.setHighlighted(false);
					panel.addEdge(first, n, labelsTF.getText());
					first = null;
					state = EDGE_FIRST;
				}
				else {
					JOptionPane.showMessageDialog(frame, "Can only have digits in edge labels.");
				}
			}
		}
		frame.repaint();
	}

	public void actionPerformed(ActionEvent e) {
		//when a node action is made
		if (e.getSource().equals(nodeB)) {
			nodeB.setBackground(Color.GREEN);
			edgeB.setBackground(Color.LIGHT_GRAY);
			state = NODE_CREATE;
		}
		//when a edge action is made
		if (e.getSource().equals(edgeB)) {
			edgeB.setBackground(Color.GREEN);
			nodeB.setBackground(Color.LIGHT_GRAY);
			state = EDGE_FIRST;
			panel.stopHighlighting();
			 frame.repaint();
		}
		//when a connected button action is made
		if (e.getSource().equals(connectedB)) {
			if (panel.nodeExists(firstNode.getText()) == false) {
				JOptionPane.showMessageDialog(frame, "First node is not in your graph.");
			}
			else if (panel.nodeExists(secondNode.getText()) == false) {
				JOptionPane.showMessageDialog(frame, "Second node is not in your graph.");
			}
			else {
				//uses queue() to see if the nodes are connected
				Queue queue = new Queue();
				ArrayList<String> connectedList = new ArrayList<String>();
				connectedList.add(panel.getNode(firstNode.getText()).getLabel());
				ArrayList<String> edges = panel.getConnectedLabels(firstNode.getText());
				for (int a = 0; a < edges.size(); a++) {
					queue.enqueue(edges.get(a));
				}
				while (queue.isEmpty() == false) {
					String currentNode = queue.dequeue();
					if (connectedList.contains(currentNode) == false) {
							connectedList.add(currentNode);
					}
					edges = panel.getConnectedLabels(currentNode);
					for (int a = 0; a < edges.size(); a++) {
						if (connectedList.contains(edges.get(a)) == false) {
							queue.enqueue(edges.get(a));
						}
					}
				}
				//pop up windows
				if (connectedList.contains(secondNode.getText())) {
					JOptionPane.showMessageDialog(frame, "These nodes are connected.");
				}
				else {
					JOptionPane.showMessageDialog(frame, "These nodes are not connected.");
				}
			}
		}
		
		//traveling salesman
		if (e.getSource().equals(salesmanB)) {
			if (panel.getNode(salesmanTF.getText()) != null) {
				ArrayList<Node> path = new ArrayList<Node>();
				path.add(panel.getNode(salesmanTF.getText()));
				salesman(panel.getNode(salesmanTF.getText()), new ArrayList<Node>(), 0);
				if (traveling.size() == 0){
					JOptionPane.showMessageDialog(frame, "No valid path found.");
				}
				else {
					int cost = Integer.MAX_VALUE;
					int index = -1;
					
					for(int i = 0; i < traveling.size(); i++){
						ArrayList<Node> currentPath = traveling.get(i);
						int pathCost = Integer.parseInt(currentPath.get(currentPath.size() - 1).getLabel());
						//eliminates the infinitely* long
						if(pathCost < cost){
							cost = pathCost;
							index = i;
						}
					}
					
					String result = "";
					//formats & writes answer panel
					for (int i = 0; i < traveling.get(index).size() - 1; i++) {
							Node n = traveling.get(index).get(i);
							result += " to " + n.getLabel();
						}
					
					result += "\nCost: " + traveling.get(index).get(traveling.get(index).size()-1).getLabel();
					JOptionPane.showMessageDialog(panel, result);
				}
			}
			else {
				JOptionPane.showMessageDialog(frame, "Please enter a valid starting node.");
			}
		}
	}

	//traveling salesman (on method call)
	public void salesman(Node n, ArrayList<Node> path, int total) {
		if (path.size() == panel.nodeList.size()) {
			ArrayList<Node> alt = new ArrayList<Node>();
			
			for (Node node: path) {
				alt.add(node);
			}
			
			alt.add(new Node(0, 0, total + ""));
			traveling.add(alt);
			path.remove(path.size() - 1);
			return;
			//removes bad routes
		}
		else {
			for (int a = 0; a < panel.edgeList.size(); a++) {
				Edge e = panel.edgeList.get(a);
				
				if (e.getEnd(n) != null) {
					if (path.contains(e.getEnd(n)) == false) {
						path.add(e.getEnd(n));
						salesman(e.getEnd(n), path, total + Integer.parseInt(e.getLabel()));
						//loops
					}
				}
			}
		}
	}

	//following methods auto generated to ignore irrelevant mouse events
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
	}
	
}
/*
Adjacency Matrix
		A	 B	 C
A	 1	 1	 1
B	 1	 1	 0
C	 1	 0	 1
*/