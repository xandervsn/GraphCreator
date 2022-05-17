package avs;

import java.util.*;

public class Queue {

	ArrayList<String> queue = new ArrayList<String>();

	//adds string to queue
	public void enqueue(String s) {
		queue.add(s);
	}

	//removes string from queue
	public String dequeue() {
		String s = queue.get(0);
		queue.remove(0);
		return s;
	}

	//checks if queue is empty
	public boolean isEmpty() {
		return queue.isEmpty();
	}

}	