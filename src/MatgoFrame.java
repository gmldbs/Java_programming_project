import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;

import javax.swing.JPanel;

class Prepare extends JPanel
{
	private Image background;

	public Prepare()
	{
		background = Toolkit.getDefaultToolkit().getImage("images/background_green.png");
		setSize(400,500);
        try {
            MediaTracker tracker = new MediaTracker(this);
            tracker.addImage(background, 0);
            tracker.waitForID(0);
        } catch (Exception e) {}
		invalidate();
	}

	@Override
	protected void paintComponent(Graphics g) {
		// TODO Auto-generated method stub
		super.paintComponent(g);
		g.setColor(Color.BLACK);
		g.fillRect(0,0,getParent().getWidth(), getParent().getHeight());
		g.drawImage(background, 0,0, getParent().getWidth(), getParent().getHeight(), null);
		Resource.LoadImage(this);
	}
	
}

public class MatgoFrame extends JFrame {

	private static final long serialVersionUID = 1001629318918318000L;

	public MatgoFrame() {
		Dimension wdi = Toolkit.getDefaultToolkit().getScreenSize();
		this.setBounds((int)( wdi.getWidth() / 2 - 800 / 2), (int)(wdi.getHeight() / 2 - 700 / 2), (int)(800), (int)(700));

		Prepare pre = new Prepare();
		getContentPane().add(pre);
		JMenuBar menubar = new JMenuBar();
		JMenu menu = new JMenu("GAME");
		JMenuItem item = new JMenuItem("START");
		
		final MatgoMainPanel panel = new MatgoMainPanel();
		getContentPane().removeAll();
		getContentPane().add(panel);
		panel.setVisible(false);
		panel.setVisible(true);
		panel.Start();
		
		this.setJMenuBar(menubar);
		menubar.add(menu);
		menu.add(item);
		JMenuItem exitItem = new JMenuItem("QUIT");
		exitItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				System.exit(EXIT_ON_CLOSE);
				
			}
		});
		menu.add(exitItem);		
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	}
}

interface GoFrame
{
	public void Draw();
	public int ShowDialog(int startgame, User user) ;
	public int ShowDialog(int flag , User user , Object[] object);
}

class CardIamge 
{
	static ArrayList<Image> images = new ArrayList<Image>();
	static BufferedImage StarImage = null;
	static public Image Tag01 = null;
	static public Image[] DeckCard = new Image[2];
	
	public CardIamge(){
		LoadImage();
	}
	
	public void LoadImage()
	{
		Toolkit tk = Toolkit.getDefaultToolkit();

		DecimalFormat nf = new DecimalFormat("00");
		for (int i = 0 ; i < 12 ; i++)
		{
			for (int j = 0 ; j < 4 ; j++)
			{
				Image img = tk.getImage("images/go_img" + nf.format(i + 1) + "_" + nf.format(j + 1) + ".png");
				images.add(img);
			}
		}
		
		Tag01 = tk.getImage("images/tag_01.png");
		DeckCard[0] = tk.getImage("images/deckcard01.png");
		DeckCard[1] = tk.getImage("images/deckcard02.png");
		Image img1 = tk.getImage("images/go_img13_01.png");
		Image img2 = tk.getImage("images/go_img13_02.png");
		Image img3 = tk.getImage("images/go_img14_01.png");
		images.add(img1);
		images.add(img2);
		images.add(img3);
		
		BufferedImage StarImage = new BufferedImage(10, 10, BufferedImage.TYPE_4BYTE_ABGR);

		Graphics2D g2d = StarImage.createGraphics();
		g2d.setColor(Color.YELLOW);
		g2d.fillOval(0, 0,8, 8);
	}
	
	public static Image getImageIndex(int index)
	{
		return images.get(index);
	}
	
	public static Image getImageBehind() {
		// TODO Auto-generated method stub
		return images.get(images.size()-1);
	}
}

class GameState 
{
	public final static int READY = 0;
	public final static int START = 1;
	public final static int PLAY = 2;
	public final static int STOP = 3;
	public final static int END = 4;
}

class ASKTAG
{
	public static final int SHAKE = 1;
	public static final int BOMB = 2;
	public static final int SELECTCARD = 3;
	public static final int ENDGAME = 4;
	public static final int GOANDSTOP = 5;
	public static final int STARTGAME = 6;
	public static final int DRAW = 7;
	public static final int SHOWCARD = 8;
}

class MatgoMainPanel extends JPanel implements Runnable, MouseListener ,GoFrame
{
	private static final long serialVersionUID = 8314977873108747590L;
	
	
	BufferedImage bufferedimage = null;
	BufferedImage foregroundbuffered;
	boolean backgroundredraw = true;
	CardIamge ci = new CardIamge();
	Engine engine = new Engine(this);

	Image background = null;
	private int turn = 1;
	private boolean controlrunning = false;
	RefreshThread refrashthread = new RefreshThread();
	HashMap hascardcontainer = new HashMap();
	
	public Thread refreshThread =null;
	private BufferedImage backgroundbuffered;
	
	
	public MatgoMainPanel()
	{
		background = Toolkit.getDefaultToolkit().getImage("images/background_green.png");
		
		
		if(engine.getP1().start_flag==1) engine.getP1().setTurn();
		this.addMouseListener(this);
	}
	
	public void Popup( String str, long Flag)
	{
		PopUp pop = new PopUp(str,Flag);
		pop.start();
	}
	
	public void Start()
	{
		if (refreshThread != null)
		{
			refreshThread.stop();
			try {
				refrashthread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		refreshThread = new Thread(this);
		engine.Start();
	
		controlrunning = false;
		
		Draw();				
		
		refreshThread.start();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub			
		
		try {
			while(!Thread.currentThread().isInterrupted())
			{
				repaint();
				Thread.sleep(1000/24);
			
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}
	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}
	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}
	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}
	@Override
	public void mouseReleased(MouseEvent event) {
		// TODO Auto-generated method stub
		
		Point pt = event.getPoint();
		try {
			engine.takePoint(pt);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public User getAnotherUser(User user)
	{
		if (user == engine.getP1())
			return engine.getP2();
		else
			return engine.getP1();
	}

	public Graphics2D createGraphic()
	{
		if (bufferedimage == null)
		{
			bufferedimage = new BufferedImage(800, 640, BufferedImage.TYPE_3BYTE_BGR);
		}
		return  bufferedimage.createGraphics();
	}
	
	public Graphics2D createGraphicAnimate()
	{
		if (foregroundbuffered == null)
		{
			foregroundbuffered = new BufferedImage(200, 150, BufferedImage.TYPE_4BYTE_ABGR);
		}
		return   foregroundbuffered.createGraphics();
	}
	
	public void DrawBackGround()
	{	
		if (backgroundredraw )
		{
		if (backgroundbuffered == null)
		{
			backgroundbuffered = new BufferedImage(800,640, BufferedImage.TYPE_USHORT_555_RGB);
		}
		if (background.getProperty("WIDTH ", this) != null)
		{
			Graphics2D g2d =  backgroundbuffered.createGraphics();
			// // System.out.println("test");
			g2d.drawImage(background, 0, 0, null);
			g2d.dispose();
			backgroundredraw = true;
	
		}
		else
			backgroundredraw = false;
		}
	}
	
	public void Draw()
	{
		Graphics2D g = createGraphic();
		Draw(g);
		g.dispose();
	}
	
	public void Draw(Graphics2D g)
	{
		synchronized (bufferedimage)
		{
			g.drawImage(background, 0, 0, this);
			
			HashMap hashmap = engine.getBoard().getHash();
			Iterator it = hashmap.keySet().iterator();
			
			Rectangle rect = new Rectangle(0,0,40,60);
			int[] sets = engine.getBoard().getSet();
			
			for (int i = 0 ; i < 12 ; i++)
			{
				rect.setLocation(engine.getBoard().getPoint(i));
				if (sets[i] != -1)
				{
					ArrayList<Integer> list = (ArrayList<Integer>)hashmap.get(sets[i]);
					if (list != null)
					{
						Iterator<Integer> it2 = list.iterator();
						int j =0;
						
						while (it2.hasNext())
						{
							int index2 = it2.next();
							Image img = ci.getImageIndex(index2);
							g.drawImage(img, rect.x, rect.y, rect.width , rect.height ,null);
							j++;
							rect.translate(20, 0);
						}
					}
				}
			}
			
			DrawHasCard(g, engine.getP1(), new Point(0,500));
			DrawHasCard(g, engine.getP2(), new Point(0,10));

			DrawTakeCard(g, engine.getP2(), new Point(580,15));
			DrawTakeCard(g, engine.getP1(), new Point(580,505));			
		
			Stack stack = engine.getStack().getList();
			
			Point pt = new Point();
			pt.setLocation((Point) engine.getBoard().getStackPoint());
			Image img = null;
			
			if (!stack.isEmpty())
			{
				img = ci.DeckCard[0];
				
				g.drawImage(img, pt.x, pt.y  ,null);
				pt.x += 6*2;
			}
			hascardcontainer.put("CardDeckLastPoint", pt);
			
			DrawUser(g, engine.getP2(), 580, 160);
			DrawUser(g, engine.getP1(), 580, 395);
		}
	}
	
	public void DrawUser(Graphics2D g, User user, int x, int y)
	{
		x += 5;
		y += 5;
		int tempx = x;
		int tempy = y;
		
		UserState state = user.getState();
		String str = "";
		Font font = new Font("Times",Font.BOLD,12);
		
		g.setFont(font);
		g.drawString("SCORE: " + user.getScore(), tempx, tempy);
		g.setColor(Color.RED);
		
		User user1 = getAnotherUser(user);
		UserState user1state = user1.getState();
		str = "";
		
		if (state.PEE < 6 &&  user1state.PEE_SCORE > 0)
			str += "PEEBAK ";
		if (state.GWANG == 0 && user1state.GWANG_SOCRE > 0)
			str += "GWANGBAK  ";
		
		g.drawString(str, tempx, y +80);
		g.setColor(Color.white);
		font = new Font("Times",Font.PLAIN,12);
		g.setFont(font);
		
		tempy += 20;
		
		g.drawString(Long.toString(user.getMoney()) + " WON", tempx, tempy);
		
		if (user.getGo() != 0)
		{
			tempy += 20;
			g.drawString( user.getGo()+ "GO", tempx, tempy);
		}
		if (state.SHAKE != 0)
		{
			tempy += 20;
			g.drawString( "SHAKED", tempx, tempy);
			
		}

		tempy = y;
		tempx += 100;
		str = "GWANG  " + state.GWANG;
		g.drawString( str, tempx, tempy);
		tempy += 20;
		str = "YEOLGGOT " + state.YEOLGGOT;
		g.drawString( str, tempx, tempy);
		tempy += 20;
		str = "TTI  " + state.TTI;
		g.drawString( str, tempx, tempy);
		tempy += 20;
		str = "PEE  " + state.PEE;
		g.drawString( str, tempx, tempy);
		tempy += 20;
		str = "";
		
		if (state.GODORI)
			str += "GODORI  ";
		if (state.CHEONGDAN)
			str += "CHEONGDAN ";
		if (state.CHODAN)
			str += "CHODAN ";
		if (state.HONGDAN)
			str += "HONGDAN ";
		g.drawString( str , tempx -10 , tempy);

	}
	
	public void DrawHasCard(Graphics2D g , User user, Point point)
	{
		Rectangle rect = new Rectangle(0,0,40,60);
		HashMap p1hascard = user.getHasListAll();
		ArrayList al = null;

		al = (ArrayList)p1hascard.get(Card.GWANG);
		rect.setLocation(user.getHasCardPoint(Card.GWANG));
		
		int i = 0 ;
		
		if (al != null)
		{
			Iterator<Integer> it1 = al.listIterator();
			while(it1.hasNext())
			{
				int index = it1.next();
				Image img = ci.getImageIndex(index);
				g.drawImage(img, rect.x, rect.y, rect.width , rect.height ,null);
				rect.translate(20, 0);
				i++;
				
				if ( i % 3 ==0)
				{
					rect.setLocation(user.getHasCardPoint(Card.GWANG).x, rect.y+50);
				}
			}
		}
		
		al = (ArrayList)p1hascard.get(Card.YEOLGGOT);
		rect.setLocation(user.getHasCardPoint(Card.YEOLGGOT));
		i = 0;
		
		if (al != null)
		{
			Iterator<Integer> it1 = al.listIterator();
			while(it1.hasNext())
			{
				int index = it1.next();
				Image img = ci.getImageIndex(index);
				g.drawImage(img, rect.x, rect.y, rect.width , rect.height ,null);
				rect.translate(20, 0);
				i++;
				
				if ( i % 4 ==0)
				{
					rect.setLocation(user.getHasCardPoint(Card.YEOLGGOT).x, rect.y+50);
				}
			}
		}
		
		al = (ArrayList)p1hascard.get(Card.TTI);
		rect.setLocation((Point) user.getHasCardPoint(Card.TTI));
		i = 0;
		
		if (al != null)
		{
			Iterator<Integer> it1 = al.listIterator();
			while(it1.hasNext())
			{
				int index = it1.next();
				Image img = ci.getImageIndex(index);
				g.drawImage(img, rect.x, rect.y, rect.width , rect.height ,null);
				rect.translate(20, 0);
				i++;
				
				if ( i % 6 ==0)
				{
					rect.setLocation(user.getHasCardPoint(Card.TTI).x, rect.y+50);
				}
			}
		}
		
		al = (ArrayList)p1hascard.get(Card.PEE);
		rect.setLocation(user.getHasCardPoint(Card.PEE));
		i = 0;
		
		if (al != null)
		{
			Iterator<Integer> it1 = al.listIterator();
			while(it1.hasNext())
			{
				int index = it1.next();
				Image img = ci.getImageIndex(index);
				g.drawImage(img, rect.x, rect.y, rect.width , rect.height ,null);
				rect.translate(20, 0);
				i++;
				
				if ( i % 7 ==0)
				{
					rect.setLocation(user.getHasCardPoint(Card.PEE).x, rect.y+40);
				}
			}
		}
		
	}
	
	public void DrawTakeCard(Graphics2D g , User user, Point point)
	{	
		Color oldcolor = g.getColor();

		point.translate(1, 1);
		
		g.setStroke(new BasicStroke(5,2,1,10));
		
		Rectangle rect = new Rectangle(0,0,40,60);
		ArrayList<Integer> list = (ArrayList)user.getCardListAll();
		Iterator<Integer> it = list.iterator();
		int i = 0;
		Point pt = new Point();
		
		while (it.hasNext())
		{
			int index = it.next();
			Image img;
			if (user.getName() == "User")
				img = ci.getImageIndex(index);
			else
				img = ci.getImageBehind();

			pt.setLocation(user.getTakeCardPoint(i));
			g.drawImage(img, pt.x, pt.y ,rect.width,rect.height ,this);
			
			if (user.getName() != "Com")
			{
				if (user.isStar(i) )
				{
					g.drawImage(ci.Tag01, pt.x, pt.y, this);
				}
				else if (user.getFlag(i) == 1 )
				{
					g.setColor(Color.GRAY);
					g.fillRect(pt.x, pt.y, 10, 10);
				}
				else if (user.getFlag(i) == 2 )
				{
					g.setColor(Color.YELLOW);
					g.fillOval(pt.x, pt.y, 10, 10);
				}
				else if (user.getFlag(i) == 7 )
				{
					g.setColor(Color.red);
					g.fillOval(pt.x, pt.y, 10, 10);
				}
			}
			
			i++;
		}
		
		if (user.getTurn())
		{
			g.setColor( new Color(150,250,57,180));
			g.drawRect(point.x-5,point.y-5,200+10, 120+10);
		}
		
		else
		{
			g.setColor( new Color(100,100,100,150));
			g.fillRect(point.x-5,point.y-5,200+10, 120+10);
		}
		g.setColor(oldcolor);
	}
	
	public synchronized void paint(Graphics g1)
	{	
		super.paint(g1);
		synchronized (bufferedimage)
		{
			g1.drawImage(bufferedimage, 0, 0,getWidth(),getHeight(), this);
			
			if (foregroundbuffered != null)
			{
				Dimension di = getSize();
				int x = foregroundbuffered.getWidth();
				int y =foregroundbuffered.getHeight();
				
				g1.drawImage(foregroundbuffered, (int) (di.getWidth() / 2 - x /2), (int)(di.getHeight()/2 - y /2 ) ,this);
			}
		}
	}
	
	public void GameOver(User user) {
		// TODO Auto-generated method stub
	
		engine.setGameState(GameState.END);
		User users[] = new User[2];
		
		if (user == null)
		{
			ShowDialog(ASKTAG.DRAW, user);
		}
		else
		{
			users[0] = user;
			users[1] = getAnotherUser(user);
			
			int total = user.SumTotalScore(users[0]);
			long money = total * 500;
			if (total == 0 )
			{
				money = 7 * 500;
			}
			long getmoney = users[1].StealMoney(money);
			users[0].PutMoney(getmoney);
			
			ShowDialog(ASKTAG.ENDGAME,user,users );
		}
		Draw();
		if (ShowDialog(ASKTAG.STARTGAME,user) == 0)
		{
			Start();
		}
	}

	public int ShowDialog(int flag, User user) {
		// TODO Auto-generated method stub
		try {
			return GameDialog.ShowDialog(this, flag, user);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}


	public int ShowDialog(int flag , User user , Object[] object) {
		try {
			return GameDialog.ShowDialog(this, flag, user, object);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		return 0;
		
	}

	public void clearPopup() {
		// TODO Auto-generated method stub
		foregroundbuffered = null;
	}

	class RefreshThread extends Thread
	{
		public void run()
		{
			int i = 10;
			while(!Thread.currentThread().isInterrupted())
			{
				synchronized(bufferedimage)
				{
					Graphics2D g2 = createGraphic();
					Draw(g2);
					g2.drawRect(10+i, 10+i, 100, 100);
					g2.dispose();
				}
					repaint();
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				i++;
				
			}
		}
	}
	
	class PopUp extends Thread 
	{
		private String str ="";
		private long time = 0;
		public PopUp(String str, long time)
		{
			this.str = str;
			this.time = time;
		}
		public void run()
		{
			Graphics2D g = createGraphicAnimate();

			GradientPaint gp =  new GradientPaint(0,0,Color.YELLOW,200*.35f,50*.35f,Color.RED);
			
			Font oft = g.getFont();
			
			FontRenderContext frc = g.getFontRenderContext();
            Font f = new Font("Times",Font.BOLD ,20);
            GeneralPath  path ;
            TextLayout tl = new TextLayout(str, f, frc);
			AffineTransform Tx = AffineTransform.getScaleInstance(1, 1);
			Tx.translate(0	,tl.getBounds().getHeight() /2);
			
			Shape sh1 = tl.getOutline(Tx);
			
			path = new GeneralPath();
			path.append(Tx.createTransformedShape(sh1), false);

			g.setColor(Color.DARK_GRAY);
			g.translate(20, 50);
			g.fill(path);
			g.setPaint(gp);
			
			g.translate(-5, -5);
			g.fill(path);
			
			g.setFont(oft);
			g.dispose();
			try {
				Thread.sleep(time);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			clearPopup();
		}
	}

	class Animate extends Thread
	{
		Image image = null;
		long time = 0;
		long starttime = 0;
		Point toPoint = null;
		Point fromPoint = null;
		double stepx =0;
		double stepy = 0;
		private int flag = 0;
		public static final int ONE_CARD_DELAY  = 0;
		public static final int CARDS_DELAY  = 1;
		public static final int CARDS  = 1;
		public static final int ONE_CARD  = 2;
		Animate(Image image , long time, Point fromPoint, Point toPoint)
		{
			
			this.image = image;
			this.time = time;
			this.toPoint = toPoint;
			this.fromPoint = fromPoint;
			flag = ONE_CARD_DELAY;
			calstep(fromPoint);
		}
		Animate(Image image , long time, Point fromPoint, Point toPoint,int flag)
		{
			
			this.image = image;
			this.time = time;
			this.toPoint = toPoint;
			this.fromPoint = fromPoint;
			this.flag = flag;
			calstep(fromPoint);
		}
		public void Throwcards()
		{
			Point tempPoint = new Point(fromPoint);
			
			starttime = System.currentTimeMillis();
			
			while (starttime + time > System.currentTimeMillis())
			{
				calstep(tempPoint);
				tempPoint.translate((int)stepx, (int)stepy);
				//synchronized(bufferedimage)
				{	
					Graphics2D g2 = createGraphic();
					Draw(g2);
					g2.drawImage(image, tempPoint.x, tempPoint.y, null);
					
					g2.dispose();
				}
				try {
					Thread.sleep(30);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		public void Throwcardsdelay()
		{
			try
			{
				synchronized(bufferedimage)
				{
					Graphics2D g2 = createGraphic();

					Draw(g2);
					g2.drawImage(image, fromPoint.x, fromPoint.y , null);
					g2.dispose();
				}
			
				Thread.sleep(300);
				Throwcards();
				
				synchronized(bufferedimage)
				{	
					Graphics2D g2 = createGraphic();
					Draw(g2);
					g2.drawImage(image, toPoint.x, toPoint.y, null);
					
					g2.dispose();
				}
				
				Thread.sleep(300);
			}
			catch(InterruptedException ee)	{}
		}
		public void calstep(Point currentPoint)
		{
			long delay =  starttime + time - System.currentTimeMillis();
			delay++;
			int disx = toPoint.x - currentPoint.x;
			int disy = toPoint.y - currentPoint.y;
			stepx = disx / (time/10) ;
			stepy = disy / (time/10);
		}
		public void step(Point currentPoint)
		{

		}
		public void run()
		{
			switch (flag)
			{
			case ONE_CARD_DELAY:
				Throwcardsdelay();
				break;
			case CARDS:
				Throwcards();
				break;
			}
		}

	}

	public void animateCard(Card card, long time, Point fromPoint, Point toPoint, int flag) {
		// TODO Auto-generated method stub
		Image img = ci.getImageIndex(card.getIndex());
		Animate ani = new Animate(img , time, fromPoint, toPoint, flag);
		ani.start();
		try {
			ani.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void animateCard(Card card, long time, Point fromPoint, Point toPoint) {
		// TODO Auto-generated method stub
		Image img = ci.getImageIndex(card.getIndex());
		Animate ani = new Animate(img , time, fromPoint, toPoint);
		ani.start();
		try {
			ani.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}