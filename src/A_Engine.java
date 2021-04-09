import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import java.awt.*;

abstract class A_Engine extends Client {
	
	protected int turn = 0;
	private int GameFlag = GameState.STOP;
	private boolean controlrunning = false;
	private Board board = new Board();
	private boolean ReStart = false;
	private int seed = 0;
	
	int maxValue = 0;
	int sendIndex;
	int targetIndex = -1;
	
	private boolean ChoAlert_toggle=true;
	private boolean ChoScored_toggle=false;
	private boolean HongAlert_toggle=true;
	private boolean HongScored_toggle=false;
	private boolean CheongScored_toggle=false;
	private boolean CheongAlert_toggle=true;
	private boolean GodoriScored_toggle=false;
	private boolean GodoriAlert_toggle=true;

	public A_Engine() {}

	public void setGameState(int flag) {
		GameFlag = flag;
	}

	public int getGameState() {
		return GameFlag;
	}

	public void takePoint(Point pt) throws UnknownHostException, IOException {
		// TODO Auto-generated method stub
		if (GameState.PLAY != GameFlag)
			return;
		if (controlrunning)
			return;
		if (getP1().getTurn()) {
			int index = getP1().getPointtoIndex(pt);
			if (index != -1) {
				Control(getP1(), index);
			}
		}
	}

	private void Control(User user, int index) throws UnknownHostException, IOException {
		// TODO Auto-generated method stub
		control con = new control(user, index);

		con.start();
	}

	public void Start() {
		// cardset.RandCard();
		if (ReStart) {
			cardset.SeedCard(seed++);
			p1.Reset();
			p2.Reset();
			board.Reset();

			control con = new control(control.START);
			con.start();
			GameFlag = GameState.START;
		} else {
			p1.Reset();
			p2.Reset();
			board.Reset();

			control con = new control(control.START);
			con.start();
			GameFlag = GameState.START;
			ReStart = true;
		}
	}
	
	public void Play(String filename)
	{
		try
		{
			File file = new File(filename);
			AudioInputStream stream = AudioSystem.getAudioInputStream(file);
			Clip clip = AudioSystem.getClip();
			clip.open(stream);
			clip.start();
		} catch (Exception e) {}
	}

	/**
	 * @return the p1
	 */
	public User getP1() {
		return p1;
	}

	/**
	 * @param p1 the p1 to set
	 */
	public void setP1(User p1) {
		this.p1 = p1;
	}

	/**
	 * @return the p2
	 */
	public User getP2() {
		return p2;
	}

	/**
	 * @param p2 the p2 to set
	 */
	public void setP2(User p2) {
		this.p2 = p2;
	}

	/**
	 * @return the board
	 */
	public Board getBoard() {
		return board;
	}

	public CardStack getStack() {
		// TODO Auto-generated method stub
		return cardset;
	}

	public void UserSort() {
		// TODO Auto-generated method stub
		p1.SortCard();
		p1.setFlagList(board);
		p2.SortCard();
		p2.setFlagList(board);
	}

	public void SumScore() {
		// TODO Auto-generated method stub
		p1.calScore();
		p2.calScore();
	}

	public void Alert(User user) {
		if (user.is_ChoAlert() && ChoAlert_toggle) {
			PopUp("CHO ALERT", 1000);
			ChoAlert_toggle = false;
			ChoScored_toggle = true;
		}
		if (user.is_HongAlert() && HongAlert_toggle) {
			PopUp("HONG ALERT", 1000);
			HongAlert_toggle = false;
			HongScored_toggle = true;
		}
		if (user.is_CheongAlert() && CheongAlert_toggle) {
			PopUp("CHEONG ALERT", 1000);
			CheongAlert_toggle = false;
			CheongScored_toggle = true;
		}
		if (user.is_GodoriAlert() && GodoriAlert_toggle) {
			PopUp("GODORI AlERT", 1000);
			GodoriAlert_toggle = false;
			GodoriScored_toggle = true;
		}
	}

	public void Scored(User user) {
		if (user.is_Cho() && ChoScored_toggle) {
			Play("sounds/chodan.wav");
			PopUp("CHODAN!", 1000);
			ChoScored_toggle = false;
		}
		if (user.is_Hong() && HongScored_toggle) {
			Play("sounds/hongdan.wav");
			PopUp("HONGDAN!", 1000);
			HongScored_toggle = false;
		}
		if (user.is_Cheong() && CheongScored_toggle) {
			Play("sounds/cheongdan.wav");
			PopUp("CHEONGDAN!", 1000);
			CheongScored_toggle = false;
		}
		if (user.is_Godori() && GodoriScored_toggle) {
			Play("sounds/godori.wav");
			PopUp("GODORI!", 1000);
			GodoriScored_toggle = false;
		}
	}

	public void Cut() {
		if (getP1().hasCho() && getP2().hasCho() && ChoAlert_toggle) {
			PopUp("CHO CUT", 1000);
			ChoAlert_toggle = false;
			ChoScored_toggle = false;
		}
		if (getP1().hasHong() && getP2().hasHong() && HongAlert_toggle) {
			PopUp("HONG CUT", 1000);
			HongAlert_toggle = false;
			HongScored_toggle = false;
		}
		if (getP1().hasCheong() && getP2().hasCheong() && CheongAlert_toggle) {
			PopUp("CHEONG CUT", 1000);
			CheongAlert_toggle = false;
			CheongScored_toggle = false;
		}
		if (getP1().hasGodori() && getP2().hasGodori() && GodoriAlert_toggle) {
			PopUp("GODORI CUT", 1000);
			GodoriAlert_toggle = false;
			GodoriScored_toggle = false;
		}
	}

	public void TurnOff() throws UnknownHostException, ClassNotFoundException, IOException {
		getP1().setTurn();
		getP2().setTurn();
		if (turn == 1) {
			turn = 2;
		} else {
			turn = 1;
		}

		TurnOn();
	}

	public void TurnOn() {
		if (getP2().getTurn()) {
			if (getP2().type == 1)
				putComputer();
			else
				threadread();
		}
		Draw();
	}

	public void putComputer() {

		ComThread comthread = new ComThread();
		comthread.start();
	}

	class ComThread extends Thread {
		public void run() {
			while (getGameState() == GameState.PLAY && getP2().getTurn()) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				int i = 0;
				boolean select = false;
				int size = getP2().getSize();
				eval();
				control cont = new control(getP2(), sendIndex);
				cont.start();
				try {
					cont.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	void eval() {
		int sum = 0;
		int max = 0;
		ArrayList mine = new ArrayList();
		mine = getP2().cardlist;
		for (int i = 0; i < mine.size(); i++) {
			Card card = CardSet.getCardIndex((int) mine.get(i));
			
			if (getBoard().pan.containsKey(card.getSet())) {
				ArrayList a = new ArrayList();
				a = getBoard().pan.get(card.getSet());
				if (a != null) {
					for (int j = 0; j < a.size(); j++) {
						Card card1 = CardSet.getCardIndex((int) a.get(j));

						if (card1.getType() == 1) {
							ArrayList al = new ArrayList();
							al = (ArrayList) getP2().hasCard.get(Card.GWANG);
							if (al != null) {
								if (al.size() == 2)
									sum += 100;
							}
							sum += 100;
						} else if (card1.getType() == 2) {
							if (card1.getTypeSub() == 8) {
								ArrayList al = new ArrayList();
								al = (ArrayList) getP1().hasCard.get(Card.YEOLGGOT);
								if (al != null) {
									int numberOfGodori = 0;
									for (int k = 0; k < al.size(); k++) {
										Card card2 = CardSet.getCardIndex((int) al.get(k));
										if (card.getTypeSub() == 8)
											numberOfGodori++;
									}
									if (numberOfGodori == 2)
										sum += 200;
								}
								al = (ArrayList) getP2().hasCard.get(Card.YEOLGGOT);
								if (al != null) {
									int numberOfGodori = 0;
									for (int k = 0; k < al.size(); k++) {
										Card card2 = CardSet.getCardIndex((int) al.get(k));
										if (card.getTypeSub() == 8)
											numberOfGodori++;
									}
									if (numberOfGodori == 2)
										sum += 500;
								}
							}
						} else if (card1.getType() == 3) {
						
							int dan = card1.getTypeSub();
							ArrayList al = new ArrayList();
							al = (ArrayList) getP1().hasCard.get(Card.TTI);
							if (al != null) {
								int numberOfDan = 0;
								for (int k = 0; k < al.size(); k++) {
									Card card2 = CardSet.getCardIndex((int) al.get(k));
									if (card.getTypeSub() == dan)
										numberOfDan++;
								}
								if (numberOfDan == 2)
									sum += 100;
							}
							al = (ArrayList) getP2().hasCard.get(Card.TTI);
							if (al != null) {
								int numberOfDan = 0;
								for (int k = 0; k < al.size(); k++) {
									Card card2 = CardSet.getCardIndex((int) al.get(k));
									if (card2.getTypeSub() == dan)
										numberOfDan++;
								}
								if (numberOfDan == 2)
									sum += 200;
							}
							sum += 30;
						} else if (card1.getType() == 4) {

							if (card1.getTypeSub() == 1)
								sum += 100;
							else
								sum += 30;
						}

						if (card.getType() == 1) {
							ArrayList al = new ArrayList();
							al = (ArrayList) getP2().hasCard.get(Card.GWANG);
							if(al != null) {
								if (al.size() == 2)
								sum += 100;
							}
							sum += 100;
						} else if (card.getType() == 2) {
							if (card.getTypeSub() == 8) {
								ArrayList al = new ArrayList();
								al = (ArrayList) getP1().hasCard.get(Card.YEOLGGOT);
								if (al != null) {
									int numberOfGodori = 0;
									for (int k = 0; k < al.size(); k++) {
										Card card2 = CardSet.getCardIndex((int) al.get(k));
										if (card2.getTypeSub() == 8)
											numberOfGodori++;
									}
									if (numberOfGodori == 2)
										sum += 200;
								}
								al = (ArrayList) getP2().hasCard.get(Card.YEOLGGOT);
								if (al != null) {
									int numberOfGodori = 0;
									for (int k = 0; k < al.size(); k++) {
										Card card2 = CardSet.getCardIndex((int) al.get(k));
										if (card2.getTypeSub() == 8)
											numberOfGodori++;
									}
									if (numberOfGodori == 2)
										sum += 500;
								}
							}
						} else if (card.getType() == 3) {
							
							int dan = card.getTypeSub();
							ArrayList al = new ArrayList();
							al = (ArrayList) getP1().hasCard.get(Card.TTI);
							if (al != null) {
								int numberOfDan = 0;
								for (int k = 0; k < al.size(); k++) {
									Card card2 = CardSet.getCardIndex((int) al.get(k));
									if (card2.getTypeSub() == dan)
										numberOfDan++;
								}
								if (numberOfDan == 2)
									sum += 100;
							}
							
							al = (ArrayList) getP2().hasCard.get(Card.TTI);
							if (al != null) {
								int numberOfDan = 0;
								for (int k = 0; k < al.size(); k++) {
									Card card2 = CardSet.getCardIndex((int) al.get(k));
									if (card2.getTypeSub() == dan)
										numberOfDan++;
								}
								if (numberOfDan == 2)
									sum += 200;
							}
							sum += 30;
						} else if (card.getType() == 4) {
							
							if (card.getTypeSub() == 1)
								sum += 100;
							else
								sum += 30;
						}
						if (max <= sum) {
							max = sum;
							sendIndex = i;
							targetIndex = card1.getIndex();
						}
						sum = 0;
					}
				}
			}
		}
		if(max==0) sendIndex=mine.size()-1;
	}

	public void threadread() {
		read read = new read();
		read.start();
	}

	void read_trash() {
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

	abstract public void animateCard(Card card, long time, Point fromPoint, Point toPoint, int cards);

	abstract public void animateCard(Card card, long time, Point fromPoint, Point toPoint);

	abstract public void PopUp(String string, long time);

	abstract public void Draw();

	abstract public void GameOver(User firstUser);

	abstract public int ShowDialog(int showcard, User user, Card[] cards);

	abstract public int ShowDialog(int shake, User user);

	class control extends Thread {
		User user;
		int index;
		int flag = 0;

		public static final int TAKE_CARD = 1;
		public static final int START = 2;

		public control(User user, int index) {
			this.user = user;
			this.index = index;
			this.flag = TAKE_CARD;
			if (user.name == "User" && user.type != 2) {
				write();
			}
		}

		void write() {
			try {
				Data data = new Data();
				data.index = index;
				System.out.println(this.user.name + "output index : " + index);
				System.out.println(objectOutputStream);
				objectOutputStream.writeObject(data);
				objectOutputStream.flush();
				System.out.println(this.user.name + "output index : " + index + " check");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			read_trash();
		}

		public control(int flag) {
			this.flag = flag;
		}

		private void takecards() throws UnknownHostException, ClassNotFoundException, IOException {
			Point fromPoint = new Point();
			int result1 = 0;
			int stealcount = 0;
			int cardindex = user.getIndexCard(index);
			if (cardindex != -1) {
				Card takecard = CardSet.getCardIndex(cardindex);
				fromPoint = user.getCardDeckPoint();
				if (takecard.isFieldCard()) {
					int flag = user.getFlag(index);
					if (flag == User.BOMBABLE) {
						Play("sounds/bomb.wav");
						HashMap map = user.getCardSet(takecard);
						ArrayList al = (ArrayList) map.get("CARD");
						ArrayList Indexs = (ArrayList) map.get("INDEX");
						int size = al.size();
						Iterator it = al.iterator();
						Card tcard = null;
						index = (Integer) Indexs.get(0);
						for (int x = 0; x < size; x++) {
							cardindex = user.removeIndexCard(index);

							tcard = CardSet.getCardIndex(cardindex);

							throwPan(tcard, fromPoint);
							result1 = getBoard().push(tcard);
						}
						for (int i = 0; i < al.size() - 1; i++)
							user.push(50);
					} else {
						if (flag == User.SHAKEABLE) {
							if (ShowDialog(ASKTAG.SHAKE, user) == 0) {
								Play("sounds/shake.wav");
								user.DoShake();

								Card tcard = null;

								HashMap map = user.getCardSet(takecard);

								ArrayList al = (ArrayList) map.get("CARD");
								int size = al.size();
								Iterator it = al.iterator();
								Card[] cards = new Card[size];
								for (int x = 0; x < size; x++) {
									cardindex = (Integer) it.next();

									tcard = CardSet.getCardIndex(cardindex);
									cards[x] = tcard;
								}
								if (user.getName() == "Com") {
									ShowDialog(ASKTAG.SHOWCARD, user, cards);
								}
								int select = ShowDialog(ASKTAG.SELECTCARD, user, cards);
								takecard = cards[select];
								ArrayList Indexs = (ArrayList) map.get("INDEX");
								index = (Integer) Indexs.get(select);

								PopUp("SHAKE!", 1000);
							}
						}

						user.removeIndexCard(index);

						throwPan(takecard, fromPoint);
						result1 = getBoard().push(takecard);
					}
				} else {
					/*
					 * user -> bonus
					 * 
					 */
					if (takecard.isBonus()) {
						cardindex = user.removeIndexCard(index);

						throwhas(takecard, fromPoint);
						user.SendCard(takecard.getIndex());

						takecard = getStack().popCard();
						Point toPoint = user.getTakeCardPoint(user.getSize());
						throwUser(takecard, toPoint);
						user.push(takecard);
						UserSort();
						stealCard();
						Draw();
						return;
					}
					user.removeIndexCard(index);
				}

				/*
				 * deck -> pan
				 * 
				 */
				Card deckcard = getStack().popCard();
				fromPoint.setLocation(board.getStackPoint());

				if (deckcard.isBonus()) {
					throwhas(deckcard, fromPoint);
					user.SendCard(deckcard);
					stealcount++;

					deckcard = getStack().popCard();

					if (deckcard.isBonus()) {
						throwhas(deckcard, fromPoint);
						user.SendCard(deckcard);
						stealcount++;
						deckcard = getStack().popCard();
					}
				}
				throwPan(deckcard, fromPoint);

				String str = "";
				int result2 = getBoard().push(deckcard);
				if (takecard.getSet() == deckcard.getSet()) {
					if (result2 == 2) {
						str += "deepkiss";
						moveCard(takecard);
						stealcount++;
						Play("sounds/kiss.wav");
						PopUp("KISS!", 1000);

					}
					if (result2 == 3) {
						str += "shit";
						PopUp("SHIT!!", 1000);
					}
					if (result2 == 4) {
						str += "deepkiss";
						moveCard(takecard);
						stealcount++;
					}
				} else {
					if (result1 == 2) {
						str += "onepair";
						moveCard(takecard);
					} else if (result1 == 3) {
						str += "onepair";
						moveSelectCard(takecard);
					}

					else if (result1 == 4) {
						str += "bomb";
						moveCard(takecard);
						stealcount++;
					}
					if (result2 == 2) {
						str += "onepair";
						moveCard(deckcard);
					} else if (result2 == 3) {
						str += "onepair";
						moveSelectCard(deckcard);
					} else if (result2 == 4) {
						str += "bomb";
						moveCard(deckcard);
						stealcount++;
					}
				}
				if (str == "") {
					str = "empty";
				}
				if (getBoard().isEmpty() && getStack().getEnoughSize() != 0) {
					stealcount++;
					Play("sounds/pansseul.wav");
					PopUp("PAN SSEUL!", 1000);

				}
				for (int i = 0; i < stealcount; i++) {
					stealCard();
				}
				
				UserSort();
				SumScore();
				Draw();
				Alert(user);
				Scored(user);
				Cut();
				boolean end = false;

				if (user.isGoal()) {
					if (user.getSize() != 0) {
						if (ShowDialog(ASKTAG.GOANDSTOP, user) == 0) {
							Play("sounds/go.wav");
							user.DoGo();
							PopUp(user.getGo() + " GO", 1000);

						} else {
							Play("sounds/stop.wav");
							GameOver(user);
							return;
						}
					} else {
						GameOver(user);
						return;
					}
				}
				if (getStack().getEnoughSize() == 0) {
					GameOver(null);
					return;
				}

				if (!end) {
					System.out.println("TurnOFF");
					TurnOff();
				}
			}
		}

		private void startGame() {
			GameFlag = GameState.START;
			User FirstUser = getP1();
			User SecondUser = getP2();
			if (FirstUser.getTurn() != true) {
				User temp = FirstUser;
				FirstUser = SecondUser;
				SecondUser = temp;
			}

			Point fromPoint = new Point();
			Point toPoint = new Point();
			fromPoint.setLocation(board.getStackPoint());
			Card card = null;
			for (int r = 0; r < 2; r++) {
				for (int i = 0; i < 5; i++) {
					card = getStack().popCard();
					if (getP1().Turn == true)
						user = getP1();
					else
						user = getP2();
					user.push(card.getIndex());
				}
				toPoint = p1.getTakeCardPoint(0);

				throwUserhidden(toPoint);
				for (int i = 0; i < 5; i++) {
					if (getP2().Turn == false)
						user = getP2();
					else
						user = getP1();
					card = getStack().popCard();
					user.push(card.getIndex());
				}
				toPoint = p2.getTakeCardPoint(0);

				throwUserhidden(toPoint);
			}
			user = FirstUser;
			for (int i = 0; i < 8; i++) {

				card = getStack().popCard();

				if (card.isBonus()) {

					throwhas(card, fromPoint);

					user.SendCard(card.getIndex());
					i--;
				}

				else {
					if ((i % 4) == 0)
						throwPanfast(card, fromPoint);
					getBoard().push(card);
				}
			}
			throwPanfast(card, fromPoint);
			UserSort();
			if (getBoard().isTong()) {
				Play("sounds/chongtong.wav");
				PopUp("CHONGTONG!", 1000);
				GameOver(FirstUser);
				return;
			}
			if (FirstUser.isTong()) {
				Play("sounds/chongtong.wav");
				PopUp("CHONGTONG!", 1000);
				GameOver(FirstUser);
				return;
			}
			if (SecondUser.isTong()) {
				Play("sounds/chongtong.wav");
				PopUp("CHONGTONG!", 1000);
				GameOver(SecondUser);
				return;
			}

			GameFlag = GameState.PLAY;
			p1.start_flag = 1;
			p2.start_flag = 1;
			TurnOn();

		}

		private void stealCard() {
			Point fromPoint = new Point();
			User user2 = null;
			if (user == getP1()) {
				user2 = getP2();
				fromPoint = new Point(0, 10);
			} else if (user == getP2()) {
				user2 = getP1();
				fromPoint = new Point(0, 500);
			}
			if (user2.getsizePee() == 0)
				return;
			Point tpoint = new Point();

			tpoint.setLocation(user.getHasCardPoint(Card.PEE));
			fromPoint.translate(tpoint.x, tpoint.y);

			Card card = user2.removePee();
			throwhas(card, fromPoint);
			user.SendCard(card);
		}

		private void moveSelectCard(Card card) {
			getBoard().removeCard(card);
			int i = getBoard().getIndex(card.getSet());
			Point fromPoint = getBoard().getPoint(i);
			throwhas(card, fromPoint);
			user.SendCard(card);
			ArrayList tal = getBoard().geSetList(card);
			Card tcard = null;
			int size = tal.size();
			Iterator it = tal.iterator();
			Card[] cards = new Card[size];
			for (int x = 0; x < size; x++) {
				flag = (Integer) it.next();

				tcard = CardSet.getCardIndex(flag);
				cards[x] = tcard;
			}
			if (getP1().Turn == true) {
				int select = ShowDialog(ASKTAG.SELECTCARD, user, cards);
				tcard = cards[select];
			} else if (targetIndex != -1) {
				tcard = CardSet.getCardIndex(targetIndex);
				targetIndex = -1;
			} else if (targetIndex == -1) {
				tcard = cards[0];
			}
			card = getBoard().removeCard(tcard);
			throwhas(tcard, fromPoint);
			user.SendCard(tcard);
		}

		private void moveCard(Card card) {
			int i = getBoard().getIndex(card.getSet());
			Point fromPoint = getBoard().getPoint(i);
			ArrayList tal = getBoard().geSetList(card);
			Card tcard = null;
			int size = tal.size();
			Iterator it = tal.iterator();
			int[] indexs = new int[size];
			for (int x = 0; x < size; x++) {
				indexs[x] = (Integer) it.next();

			}
			for (int x = 0; x < size; x++) {
				tcard = CardSet.getCardIndex(indexs[x]);
				getBoard().removeCard(tcard);
				throwhas(tcard, fromPoint);
				user.SendCard(tcard);
			}
		}

		private void throwhas(Card card, Point fromPoint) {
			Point toPoint = new Point();

			toPoint = user.getHasCardPoint(card.getType());

			animateCard(card, 200, fromPoint, toPoint, 1);

		}

		private void throwPan(Card card, Point fromPoint) {

			int i = getBoard().getIndex(card.getSet());
			Point toPoint = getBoard().getPoint(i);

			animateCard(card, 200, fromPoint, toPoint);

		}

		private void throwPanfast(Card card, Point fromPoint) {

			int i = getBoard().getIndex(card.getSet());
			Point toPoint = getBoard().getPoint(i);

			animateCard(card, 300, fromPoint, toPoint, 1);

		}

		private void throwUser(Card card, Point toPoint) {
			Point fromPoint = board.getStackPoint();

			animateCard(card, 200, fromPoint, toPoint, 1);
		}

		private void throwUserhidden(Point toPoint) {
			Point fromPoint = board.getStackPoint();
			Card card = new Card(50,14,3,Card.EMPTY);
			animateCard(card, 200, fromPoint, toPoint, 1);
		}

		public void run() {
			controlrunning = true;
			switch (flag) {
			case START:
				startGame();
				break;
			case TAKE_CARD:
				try {
					takecards();
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
			controlrunning = false;
		}
	}

	class read extends Thread {
		public void run() {
			while (getGameState() == GameState.PLAY && getP2().getTurn()) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Data i = new Data();
				try {
					i = (Data) objectInputStream.readObject();
					control cont = new control(getP2(), i.index);
					System.out.println(" input index : " + i.index);
					cont.start();
					cont.join();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}