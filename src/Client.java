import java.awt.Point;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import javax.swing.JFrame;

class Client {
	protected static User p1 ;
	protected static User p2 ;
	static CardStack cardset ;
	static Socket socket;
	static ObjectOutputStream objectOutputStream;
	static ObjectInputStream objectInputStream;
	static Scanner scan = new Scanner(System.in); 
	
    public static void main(String[] args) {
    	System.out.println("Input Battle Type (1 = with AI, 2 = with Other Player) : ");
    	int type = scan.nextInt();
    	if(type==1)
    	{
    		p1 = new User("User");
    		p1.setCardDeckPoint(540, 450);
			p1.setTakeCardPoint(580, 505);
			p1.setHasCardPoint(5, 500);
			p1.type=2;
			
			p2 = new User("Com");
			p2.setCardDeckPoint(540, 150);
			p2.setTakeCardPoint(580, 15);
			p2.setHasCardPoint(5, 5);
			p2.type=1;
			
			p1.Turn=true;
			
			cardset = new CardStack();
	    	cardset.RandCard();
	    	
			JFrame frame = new MatgoFrame();
			frame.setVisible(true);
    	}
    	else
    	{
			try {
				String ip = "115.145.242.22";
				int port = 5000;
				
				Board board = new Board();
				
				System.out.println("Server Information : " + ip + " : " + port);
				socket = new Socket(ip, port);
				Scanner scan = new Scanner(System.in);
				objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
				objectInputStream = new ObjectInputStream(socket.getInputStream());
				cardset = (CardStack) objectInputStream.readObject();

				p1 = new User("User");
				p1.setCardDeckPoint(540, 450);
				p1.setTakeCardPoint(580, 505);
				p1.setHasCardPoint(5, 500);

				p2 = new User("Com");
				p2.setCardDeckPoint(540, 150);
				p2.setTakeCardPoint(580, 15);
				p2.setHasCardPoint(5, 5);
				
				if (cardset.order != 1) {
					p1.Turn = true;
				} else {
					p2.Turn = true;
				}

				JFrame frame = new MatgoFrame();
				frame.setVisible(true);
			} catch (Exception e) {
				System.err.println("Usage: java Client <hostname> <port:4000>");
				e.printStackTrace();
			}
    	}

    }
	public void animateCard(Card card, long time, Point fromPoint, Point toPoint, int cards) {
		// TODO Auto-generated method stub
		
	}
	public void animateCard(Card card, long time, Point fromPoint, Point toPoint) {
		// TODO Auto-generated method stub
		
	}
	public void PopUp(String string, long time) {
		// TODO Auto-generated method stub
		
	}
	public void Draw() {
		// TODO Auto-generated method stub
		
	}
	public void GameOver(User firstUser) {
		// TODO Auto-generated method stub
		
	}
	public int ShowDialog(int showcard, User user, Card[] cards) {
		// TODO Auto-generated method stub
		return 0;
	}
	public int ShowDialog(int shake, User user) {
		// TODO Auto-generated method stub
		return 0;
	}
}
 
class Cardtemp extends CardSet
{
	public Cardtemp()
	{
		cardsetting();
		int len = cardsetsAl.size();
		int width = 67;
		int height = 94;
		int x = 0;
		int y = 0;
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0 ; i < len ; i++)
		{
			if (i % 12 == 0 && i !=0 )
			{
				x = 0;
				y += height;
			}
			
			Card card = cardsetsAl.get(i);
			int index = card.getIndex();
			int set = card.getLine();
			int line = card.getSet();
			int type = card.getType();
			int type2 = card.getTypeSub();
			int left = x;
			int top = y;
			
			String buff = String.format("%d/%d,%d/%d,%d,%d,%d", i+1, line, set, left, top, width, height);
			if (type2 == Card.EMPTY)
			{
				buff += String.format("/%d", type);
			}
			else
			{
				buff += String.format("/%d,%d", type, type2);
			}
			sb.append(buff + "\n");
			x = left + width;	
		}
		try
		{
			FileOutputStream fos = new FileOutputStream("go_card.data");
			OutputStreamWriter osw = new OutputStreamWriter(fos);
			osw.write(sb.toString());
			osw.flush();
			osw.close();
			fos.close();
		}
		catch (Exception e) {			
			// TODO: handle exception
		}
	}
}
