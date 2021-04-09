import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

class GameDialog extends Client {
	public static int ShowDialog(Component con ,int flag, User user) throws ClassNotFoundException, IOException
	{
		return ShowDialog(con, flag,user,null);
	}
	public static int ShowDialog(Component con , int flag , User user , Object[] object) throws ClassNotFoundException, IOException
	{
		String str = "";
		switch (flag)
		{
		case ASKTAG.GOANDSTOP:
			if (user.getName() == "Com"&&user.type==1)
			{
				return user.ask(2);
			}
			if (user.getName() == "Com"&&user.type!=1)
			{
				Data read = new Data();
				read = (Data) objectInputStream.readObject();
				return read.choice;
			}
			
			int choice1 = JOptionPane.showConfirmDialog(
					con, "GO?", "Would you GO?", JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE, null);
			if(user.type!=1&&user.type!=2)
			{
				Data write1 = new Data();
				write1.choice = choice1;
				objectOutputStream.writeObject(write1);
				objectOutputStream.flush();
				read_trash();
			}
			return choice1; 
		case ASKTAG.SHOWCARD:
		{
			int size = object.length; 
			Card[] cards = new Card[size];
			ImageIcon[] imgs = new ImageIcon[size];
			for (int i = 0 ; i < size ; i++)
			{
				cards[i] = (Card)object[i];
				ImageIcon img =new ImageIcon();
				img.setImage(
					CardIamge.getImageIndex(
							cards[i].getIndex()));
				imgs[i] = img;
			}
			
			JOptionPane.showOptionDialog(con, "Let's shake", "Shake", JOptionPane.NO_OPTION,
					JOptionPane.INFORMATION_MESSAGE, null,imgs,0);
			return 0;
		}
		case ASKTAG.ENDGAME:
		
			User user1 = (User) object[0];
			User user2 = (User) object[1];
			UserState wuser  = user1.getState() ;
			UserState luser = user2.getState() ;
			final JDialog jd = new JDialog();
			jd.setModal(true);
			JPanel panel = new JPanel();
			JLabel label = new JLabel();
			JTextArea jta = new JTextArea();
			panel.setBackground(Color.white);
			jta.setFocusable(false);
			str = "\n" + user1.getName() + " WINS";

			int doublescore = 0;
			
			str +="\r\n";
			str += "\nSCORE " + wuser.SCORE ;
			str += "\n GWANG + " + wuser.GWANG_SOCRE;
			str += "\nYEOLGGOT  + " + wuser.YEOLGGOT_SCORE;
			str += "\nTTI  + " + wuser.TTI_SCORE;
			str += "\nPEE + " + wuser.PEE_SCORE;
			str += "\n";
			if (wuser.GODORI)
				str += "GODORI + 3";
			if (wuser.CHEONGDAN)
				str += "CHEONGDAN  + 3";
			if (wuser.CHODAN)
				str += "CHODAN  + 3";
			if (wuser.HONGDAN)
				str += "HONGDAN + 3";
			if (wuser.GO > 0)
			{
				
				str += "\n"+wuser.GO + " GO  ";
				if (wuser.GO > 2)
				{
					str += "X 2";
					doublescore ++;
				}
			}
			if (wuser.SHAKE > 0)
			{
				str +="\nSHAKE X 2   ";
				doublescore ++;
			}

			if (wuser.PEE_SCORE > 0 && luser.PEE < 5)
			{
				str += " PEEBAK X 2";
				doublescore ++;
			}
			if (wuser.GWANG_SOCRE > 0 && luser.GWANG == 0 )
			{
				str += " GWANGBAK X 2";
				doublescore ++;
			}
			if ( luser.GO > 0 )
			{
				str += " GOBAK X 2";
				doublescore ++;
			}
			long total = 0;

			
			total = user1.getTotalScore() * 500;
			
			str += " \r\n \n" + user1.getTotalScore() + " SCORE X 500 WON = " + total + " WON";
			label.setText(str);
			jta.setText(str);
			panel.add(jta);
			jd.pack();
			
			jd.getContentPane().add(panel);
			
			jd.addMouseListener(new MouseAdapter()
			{
				public void mouseReleased(MouseEvent evt)
				{
					jd.dispose();
				}
			});
			jd.setSize(300,300);
			Point po = con.getLocation();
			Rectangle rect = con.getBounds();
			po.translate((int)(rect.getCenterX() - jd.getBounds().getWidth() / 2), (int)(rect.getCenterY() -jd.getHeight() / 2));
			jd.setLocation(po);
			jd.setTitle("Close window when clicked");
			jd.setVisible(true);
			return -1;
		case ASKTAG.SHAKE:
			if (user.getName() == "Com"&&user.type==1)
			{
				return 0;
			}
			if (user.getName() == "Com"&&user.type!=1)
			{
				Data read = new Data();
				read = (Data) objectInputStream.readObject();
				return read.choice;
			}
			int choice2 = JOptionPane.showConfirmDialog(
					con, "SHAKE?", "Choose", JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE, null);
			if(user.type!=1&&user.type!=2)
			{
				Data write2 = new Data();
				write2.choice = choice2;
				objectOutputStream.writeObject(write2);
				objectOutputStream.flush();
				read_trash();
			}
			return choice2;
		case ASKTAG.BOMB:
		case ASKTAG.SELECTCARD:
			int size = object.length;
			if (user.getName() == "Com"&&user.type==1)
			{
				return user.ask(size);
			}
			if (user.getName() == "Com"&&user.type!=1)
			{
				Data read = new Data();
				read = (Data) objectInputStream.readObject();
				return read.choice;
			}
			Card[] cards = new Card[size];
			ImageIcon[] imgs = new ImageIcon[size];
			for (int i = 0 ; i < size ; i++)
			{
				cards[i] = (Card)object[i];
				ImageIcon img =new ImageIcon();
				img.setImage(
					CardIamge.getImageIndex(
							cards[i].getIndex()));
				imgs[i] = img;
			}
			
			int choice3 = JOptionPane.showOptionDialog(con, "Which card?", "Choose", JOptionPane.OK_OPTION,
					JOptionPane.QUESTION_MESSAGE, null,imgs,0);
			if (choice3 == -1)
				choice3 = 0;
			if(user.type!=1&&user.type!=2)
			{
				Data write3 = new Data();
				write3.choice = choice3;
				objectOutputStream.writeObject(write3);
				objectOutputStream.flush();
				read_trash();
			}
			return choice3;
		case ASKTAG.STARTGAME:
			str ="";
			String[] strs = {"START" , "QUIT"};
			int choice = JOptionPane.showOptionDialog(con, "START", "QUIT", JOptionPane.OK_OPTION,
					JOptionPane.QUESTION_MESSAGE, null,strs,"START");
			System.out.println(choice);
			if(choice==0) {
				cardset = new CardStack();
				cardset.RandCard();
			}
			return choice;
		}
		return -1;
	}
	@Override
	public void animateCard(Card card, long time, Point fromPoint, Point toPoint, int cards) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void animateCard(Card card, long time, Point fromPoint, Point toPoint) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void PopUp(String string, long time) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void Draw() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void GameOver(User firstUser) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public int ShowDialog(int showcard, User user, Card[] cards) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int ShowDialog(int shake, User user) {
		// TODO Auto-generated method stub
		return 0;
	}

	static void read_trash() {
		Data i = new Data();
        try {
			i = (Data) objectInputStream.readObject();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
	}
	
}
