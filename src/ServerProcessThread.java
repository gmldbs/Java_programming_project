import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.List;

public class ServerProcessThread extends Thread {
	private String nickname = null;
	private Socket socket = null;
	ObjectInputStream objectInputStream;
	ObjectOutputStream objectOutputStream;
	Board board = new Board();
	CardStack cardset = new CardStack();
	List<ObjectOutputStream> listPlayers = null;
	List<ObjectInputStream> listPlayers_in = null;
	int cando = 1;

	public ServerProcessThread(Socket socket, List<ObjectOutputStream> listPlayers,
			List<ObjectInputStream> listPlayers_in) {
		this.socket = socket;
		this.listPlayers = listPlayers;
		this.listPlayers_in = listPlayers_in;
	}

	public void run() {
		try {
			objectInputStream = new ObjectInputStream(socket.getInputStream());
			objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
			System.out.println("thread execute");
			cardset.RandCard();
			synchronized (listPlayers) {
				listPlayers.add(objectOutputStream);
			}
			synchronized (listPlayers_in) {
				listPlayers_in.add(objectInputStream);
			}
			System.out.println(listPlayers_in);
			if (listPlayers.size() == 2) {
				broadcast(cardset);
				readClient player1 = new readClient(listPlayers_in.get(0));
				readClient player2 = new readClient(listPlayers_in.get(1));
				player1.start();
				player2.start();
			}
		} catch (IOException e) {
			consoleLog(this.nickname + " has left.");
		}
	}

	private void doQuit(ObjectOutputStream writer) {
		removeWriter(writer);

		String data = this.nickname + "has left.";
		// broadcast(data);
	}

	class readClient extends Thread {
		ObjectInputStream ois;
		Data data_cli;

		readClient(ObjectInputStream ois_cli) {
			ois = ois_cli;
		}

		public void run() {
			while (true) {
				Data data_a = new Data();
				try {
					data_a = (Data) ois.readObject();
					broadcast_Data(data_a);
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	private void removeWriter(ObjectOutputStream writer) {
		synchronized (listPlayers) {
			listPlayers.remove(writer);
		}
	}

	private void showPan() throws IOException {
		broadcast(this.cardset);
		// broadcast(this.nickname + ":" + data);
	}

	private void doJoin(String nickname, ObjectOutputStream writer) {
		this.nickname = nickname;

		String data = nickname + " has joined.";

		addWriter(writer);
	}

	private void addWriter(ObjectOutputStream writer) {
		synchronized (listPlayers) {
			listPlayers.add(writer);
		}
	}

	private void broadcast(CardStack cardset) throws IOException {
		int i = 1;
		synchronized (listPlayers) {
			for (ObjectOutputStream objectOutputStream : listPlayers) {
				cardset.order = i;
				objectOutputStream.writeObject(this.cardset);
				objectOutputStream.flush();
				i++;
			}
		}
	}

	void broadcast_Data(Data data) throws IOException {
		synchronized (listPlayers) {
			for (ObjectOutputStream objectOutputStream : listPlayers) {
				objectOutputStream.writeObject(data);
				objectOutputStream.flush();
			}
		}
	}

	private void consoleLog(String log) {
		System.out.println(log);
	}

}

class Data implements Serializable {
	int index;
	int choice;
	boolean isShake = false;
}