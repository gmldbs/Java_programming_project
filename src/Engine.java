import java.awt.Component;
import java.awt.Point;

public class Engine extends A_Engine {
	MatgoMainPanel com;
	
	public Engine(Component com)
	{
		this.com = (MatgoMainPanel) com;
		System.out.println(this.com);
		
	}

	public void PopUp(String string, long time) {
		// TODO Auto-generated method stub
		com.Popup(string, time);
	}

	public void Draw() {
		// TODO Auto-generated method stub
		com.Draw();
	}

	public void GameOver(User firstUser) {
		// TODO Auto-generated method stub
		com.GameOver(firstUser);
	}

	public int ShowDialog(int showcard, User user, Card[] cards) {
		// TODO Auto-generated method stub
		
		return com.ShowDialog(showcard, user, cards);
	}

	public int ShowDialog(int shake, User user) {
		// TODO Auto-generated method stub
		return com.ShowDialog(shake, user);
	}

	public void animateCard(Card card, long time, Point fromPoint, Point toPoint, int flag) {
		// TODO Auto-generated method stub
		com.animateCard(card, time, fromPoint, toPoint, flag);
	}

	public void animateCard(Card card,long time, Point fromPoint, Point toPoint) {
		// TODO Auto-generated method stub
		com.animateCard(card, time, fromPoint, toPoint);
	}

	public void Control(User user, int index) {
		// TODO Auto-generated method stub
	}
}