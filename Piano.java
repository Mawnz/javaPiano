import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import javax.accessibility.AccessibleContext;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Synthesizer;
import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class Piano extends JFrame implements KeyListener {
	Box oktav = Box.createHorizontalBox();
	MidiChannel channel;
	Synthesizer synthesizer;
	HashMap<String, String> melodies;
	HashMap<Integer, String> tones;
	List<String[]> pressedKeys;
	JLabel song;
	JLabel oct;
	int[] keyThings;
	int tempNote;
	private int multiplier;
	private boolean keyPressed;
	private boolean mouseDown;
	private List<key> allKeys;
	Color tempColor;
	key tempKey;
	int tTone;
	private List<key> keysDown;

	public Piano() {
		keysDown = new ArrayList<key>();
		tTone = 0;
		keyPressed = false;
		mouseDown = false;
		multiplier = 0;
		this.addKeyListener(this);
		keyThings = new int[14];
		pressedKeys = new ArrayList<String[]>();
		melodies = new HashMap<String, String>();
		tones = new HashMap<Integer, String>();
		song = new JLabel("Försök att spela!");
		oct = new JLabel("Oktavhöjning för keyboard: 1");
		allKeys = new ArrayList<key>();

		JPanel info = new JPanel();
		info.setLayout(new GridBagLayout());

		initSound();
		initKeys();
		initHash();

		initHashTones();
		getContentPane().setLayout(new FlowLayout());
		oktav.setOpaque(true);

		// octave 0
		oktav.add(bredbox(48, 47, 100));
		oktav.add(svartbox(49, 100, 48, 50, 100, 100));
		oktav.add(smalbox(50, 100));
		oktav.add(svartbox(51, 100, 50, 52, 100, 100));
		oktav.add(bredbox(53, 52, 100));
		oktav.add(svartbox(54, 100, 53, 55, 100, 100));
		oktav.add(smalbox(55, 100));
		oktav.add(svartbox(56, 100, 55, 57, 100, 100));
		oktav.add(smalbox(57, 100));
		oktav.add(svartbox(58, 100, 57, 59, 100, 100));
		// octave 1
		oktav.add(bredbox(60, 59, 100));
		oktav.add(svartbox(61, 100, 60, 62, 100, 100));
		oktav.add(smalbox(62, 100));
		oktav.add(svartbox(63, 100, 62, 64, 100, 100));
		oktav.add(bredbox(65, 64, 100));
		oktav.add(svartbox(66, 100, 65, 67, 100, 100));
		oktav.add(smalbox(67, 100));
		oktav.add(svartbox(68, 100, 67, 69, 100, 100));
		oktav.add(smalbox(69, 100));
		oktav.add(svartbox(70, 100, 69, 71, 100, 100));

		// octave 2
		oktav.add(bredbox(72, 71, 100));
		oktav.add(svartbox(73, 100, 72, 74, 100, 100));
		oktav.add(smalbox(74, 100));
		oktav.add(svartbox(75, 100, 74, 76, 100, 100));
		oktav.add(bredbox(77, 76, 100));
		oktav.add(svartbox(78, 100, 77, 79, 100, 100));
		oktav.add(smalbox(79, 100));
		oktav.add(svartbox(80, 100, 79, 81, 100, 100));
		oktav.add(smalbox(81, 100));
		oktav.add(svartbox(82, 100, 81, 83, 100, 100));
		// high c
		oktav.add(bredbox(84, 83, 100));

		oktav.setBackground(Color.black);

		// oktav.setPreferredSize(new Dimension(200,200));
		JScrollPane window = new JScrollPane(oktav);

		getContentPane().add(window);
		// grid
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.NORTH;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.ipady = 85;
		info.add(song, constraints);

		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.anchor = GridBagConstraints.SOUTH;
		constraints.ipady = 85;
		info.add(oct, constraints);

		getContentPane().add(info);
		getContentPane().setBackground(Color.LIGHT_GRAY);
		pack();
		// setSize(330, 240);
		// getContentPane().setBackground(Color.blue);
		// setBackground(Color.white);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	public Box bredbox(int tone, int tone2, int vel) {
		Box box = Box.createHorizontalBox();
		box.add(bredvit(tone2, vel));
		box.add(Box.createHorizontalStrut(2));
		box.add(bredvit(tone, vel));
		return box;
	}

	public Box smalbox(int tone, int vel) {
		Box box = Box.createHorizontalBox();
		box.add(smalvit(tone, vel));
		return box;
	}

	public JPanel bredvit(int tone, int vel) {
		key bred = new key(tone, vel);
		// add key to arraylist as well
		allKeys.add(bred);

		bred.setBackground(Color.white);
		bred.setPreferredSize(new Dimension(23, 200));
		return bred;
	}

	public JPanel smalvit(int tone, int vel) {
		key smal = new key(tone, vel);
		// add key to arraylist as well
		allKeys.add(smal);

		smal.setBackground(Color.white);
		smal.setPreferredSize(new Dimension(11, 200));
		return smal;
	}

	public Box svartbox(int stone, int svel, int tone1, int tone2, int vel1,
			int vel2) {
		Box box = Box.createVerticalBox();
		box.add(svarttangent(stone, svel));
		box.add(vithalvor(tone1, tone2, vel1, vel2));
		return box;
	}

	public Box vithalvor(int tone1, int tone2, int vel1, int vel2) {
		Box box = Box.createHorizontalBox();
		box.add(vithalva(tone1, vel1));
		box.add(Box.createHorizontalStrut(2));
		box.add(vithalva(tone2, vel2));
		return box;
	}

	public JPanel vithalva(int tone, int vel) {
		key halva = new key(tone, vel);
		// add key to arraylist as well
		allKeys.add(halva);

		halva.setBackground(Color.white);
		halva.setPreferredSize(new Dimension(14, 80));
		return halva;
	}

	public JPanel svarttangent(int tone, int vel) {
		key tang = new key(tone, vel);
		// add key to arraylist as well
		allKeys.add(tang);

		tang.setBackground(Color.black);
		tang.setPreferredSize(new Dimension(30, 120));
		return tang;
	}

	public void initSound() {

		try {
			synthesizer = MidiSystem.getSynthesizer();
			synthesizer.open();
			channel = synthesizer.getChannels()[0];
		} catch (Exception e) {
			System.out.println("e");
		}
	}

	void initHash() {
		try {
			File mello = new File("melodies.txt");
			Scanner sc = new Scanner(mello);
			String name = "";
			String melody = "";
			int row = 0;
			while (sc.hasNext()) {
				if (row == 0) {
					melody = sc.nextLine();
					row++;
				} else if (row == 1) {
					name = sc.nextLine();
					melodies.put(melody, name);
					row = 0;
				}
			}

			sc.close();
		} catch (FileNotFoundException e) {
			System.out.println("Hittade inte filen");
		}
	}

	void initHashTones() {
		tones.put(47, "B");
		tones.put(48, "C");
		tones.put(49, "C#");
		tones.put(50, "D");
		tones.put(51, "D#");
		tones.put(52, "E");
		tones.put(53, "F");
		tones.put(54, "F#");
		tones.put(55, "G");
		tones.put(56, "G#");
		tones.put(57, "A");
		tones.put(58, "A#");
		tones.put(59, "B");
		tones.put(60, "C");
		tones.put(61, "C#");
		tones.put(62, "D");
		tones.put(63, "D#");
		tones.put(64, "E");
		tones.put(65, "F");
		tones.put(66, "F#");
		tones.put(67, "G");
		tones.put(68, "G#");
		tones.put(69, "A");
		tones.put(70, "A#");
		tones.put(71, "B");
		tones.put(72, "C");
		tones.put(73, "C#");
		tones.put(74, "D");
		tones.put(75, "D#");
		tones.put(76, "E");
		tones.put(77, "F");
		tones.put(78, "F#");
		tones.put(79, "G");
		tones.put(80, "G#");
		tones.put(81, "A");
		tones.put(82, "A#");
		tones.put(83, "B");
		tones.put(84, "C");
	}

	void initKeys() {
		keyThings[0] = KeyEvent.VK_A;
		keyThings[1] = KeyEvent.VK_S;
		keyThings[2] = KeyEvent.VK_E;
		keyThings[3] = KeyEvent.VK_D;
		keyThings[4] = KeyEvent.VK_R;
		keyThings[5] = KeyEvent.VK_F;
		keyThings[6] = KeyEvent.VK_G;
		keyThings[7] = KeyEvent.VK_Y;
		keyThings[8] = KeyEvent.VK_H;
		keyThings[9] = KeyEvent.VK_U;
		keyThings[10] = KeyEvent.VK_J;
		keyThings[11] = KeyEvent.VK_I;
		keyThings[12] = KeyEvent.VK_K;
		keyThings[13] = KeyEvent.VK_L;

	}

	public static void main(String[] args) {
		new Piano();
	}

	private class key extends JPanel implements MouseListener {
		int tone, velocity;
		String[] note = new String[2];
		AccessibleContext ac = getAccessibleContext();

		key(int t, int v) {
			ac.setAccessibleName(Integer.toString(t));
			tone = t;
			velocity = v;
			if (tones.get(t).length() == 2) {
				note[0] = tones.get(t).charAt(0) + "#";
				note[1] = tones.get(t + 1).charAt(0) + "b";
			} else {
				note[0] = tones.get(t);
			}
			this.addMouseListener(this);
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mousePressed(MouseEvent e) {
			mouseDown = true;
			tTone = tone;
			channel.noteOn(tone, velocity);
			pressedKeys.add(note);
			tempKey = (key) e.getSource();
			tempColor = tempKey.getBackground();
			for (int i = 0; i < allKeys.size(); i++) {
				if (allKeys.get(i).tone == Integer.parseInt(tempKey
						.getAccessibleContext().getAccessibleName())) {
					allKeys.get(i).setBackground(Color.RED);
				}
			}

		}

		@Override
		public void mouseReleased(MouseEvent e) {
			mouseDown = false;
			for (int i = 0; i < allKeys.size(); i++) {
				if (allKeys.get(i).tone == Integer.parseInt(tempKey
						.getAccessibleContext().getAccessibleName())) {
					allKeys.get(i).setBackground(tempColor);

					;
				}
			}

			channel.noteOff(tone);
			channel.noteOff(tTone);
			String major = "";
			String minor = "";

			for (String[] i : pressedKeys) {
				if (i[1] != null) {
					major += i[0];
					minor += i[1];
				} else {
					major += i[0];
					minor += i[0];
				}
			}
			isInMelodies(major, minor);

		}

		@Override
		public void mouseEntered(MouseEvent e) {
			if (mouseDown && tTone != tone) {
				channel.noteOff(tTone);
				if (tempKey.getBackground() == Color.red) {
					for (int i = 0; i < allKeys.size(); i++) {
						if (allKeys.get(i).tone == Integer.parseInt(tempKey
								.getAccessibleContext().getAccessibleName())) {
							allKeys.get(i).setBackground(tempColor);
						}
					}
				}
				tTone = tone;
				channel.noteOn(tone, velocity);
				pressedKeys.add(note);
				tempKey = (key) e.getSource();
				tempColor = tempKey.getBackground();

				for (int i = 0; i < allKeys.size(); i++) {
					if (allKeys.get(i).tone == Integer.parseInt(tempKey
							.getAccessibleContext().getAccessibleName())) {
						allKeys.get(i).setBackground(Color.RED);
					}
				}
				String major = "";
				String minor = "";

				for (String[] i : pressedKeys) {
					if (i[1] != null) {
						major += i[0];
						minor += i[1];
					} else {
						major += i[0];
						minor += i[0];
					}
				}
				isInMelodies(major, minor);
			} else if (tTone != tone) {
				channel.noteOff(tTone);
			}
		}

		@Override
		public void mouseExited(MouseEvent e) {

		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		
		if (!keyPressed) {
			keyPressed = true;
			if (e.getKeyCode() == KeyEvent.VK_UP
					&& Integer.parseInt(String
							.valueOf(oct.getText().charAt(27))) < 4) {
				multiplier += 12;
				oct.setText("Oktavhöjning för keyboard: "
						+ Integer.toString(Integer.parseInt(String.valueOf(oct
								.getText().charAt(27))) + 1));
			} else if (e.getKeyCode() == KeyEvent.VK_DOWN
					&& Integer.parseInt(String
							.valueOf(oct.getText().charAt(27))) != 0) {
				multiplier -= 12;
				oct.setText("Oktavhöjning för keyboard: "
						+ Integer.toString(Integer.parseInt(String.valueOf(oct
								.getText().charAt(27))) - 1));

			} else {
				String[] note = new String[2];

				for (int i = 0; i < keyThings.length; i++) {
					if (keyThings[i] == e.getKeyCode()) {
						tempNote = i + 47 + multiplier;
						// errorHandling
						if (tempNote < 85 && tempNote > 46) {
							for (int x = 0; x < allKeys.size(); x++) {
								if (allKeys.get(x).tone == tempNote) {
									tempKey = allKeys.get(x);
									break;
								}
							}


							tempColor = tempKey.getBackground();
							for (int o = 0; o < allKeys.size(); o++) {
								if (allKeys.get(o).tone == Integer
										.parseInt(tempKey
												.getAccessibleContext()
												.getAccessibleName())) {
									allKeys.get(o).setBackground(Color.RED);
								}
							}

							if (tones.get(tempNote).length() == 2) {
								note[0] = String.valueOf(tones.get(tempNote)
										.charAt(0)) + "#";
								note[1] = String.valueOf(tones
										.get(tempNote + 1).charAt(0)) + "b";
							} else {
								note[0] = String.valueOf(tones.get(tempNote)
										.charAt(0));
							}

							channel.noteOn(tempNote, 100);
							pressedKeys.add(note);
							break;
						}
					}
				}
			}
		}

	}

	@Override
	public void keyReleased(KeyEvent e) {

		for (int i = 0; i < allKeys.size(); i++) {
			if (allKeys.get(i).tone == Integer.parseInt(tempKey
					.getAccessibleContext().getAccessibleName())) {
				allKeys.get(i).setBackground(tempColor);

				;
			}
		}

		/*
		
		for (int p = 0; p < keyThings.length; p++) {
			if (keyThings[p] == e.getKeyCode()) {
				int retNote = p + multiplier + 47;
				for (int x = 0; x < allKeys.size(); x++) {
					if (allKeys.get(x).tone == retNote) {
						tempKey = allKeys.get(x);
						break;
					}
					
				}
				
			}
		}*/
		channel.noteOff(tempKey.tone);
		//keysDown.remove(tempKey);
		
		
		String major = "";
		String minor = "";

		
		keyPressed = false;

		for (String[] i : pressedKeys) {
			if (i[1] != null) {
				major += i[0];
				minor += i[1];
			} else {
				major += i[0];
				minor += i[0];
			}
		}

		isInMelodies(major, minor);

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	void isInMelodies(String maj, String min) {

		for (String h : melodies.keySet()) {
			if (maj.contains(h)) {
				song.setText("Du spelade " + melodies.get(h));
				pack();
				pressedKeys.clear();

				if (melodies.get(h).equals("Saria's Song")
						|| melodies.get(h).equals("Song Of Healing")
						|| melodies.get(h).equals("Song Of Storms")) {
					secret();
				} else {
					lame();
				}
			} else if (min.contains(h)) {
				song.setText("Du spelade " + melodies.get(h));
				pack();
				pressedKeys.clear();
				lame();
			}
		}
	}

	void secret() {
		try {
			Thread.sleep(150);

			channel.noteOn(79, 100);
			Thread.sleep(150);
			channel.noteOff(79);

			channel.noteOn(78, 80);
			Thread.sleep(150);
			channel.noteOff(78);

			channel.noteOn(75, 70);
			Thread.sleep(150);
			channel.noteOff(75);

			channel.noteOn(69, 70);
			Thread.sleep(150);
			channel.noteOff(69);

			channel.noteOn(68, 60);
			Thread.sleep(150);
			channel.noteOff(68);

			channel.noteOn(76, 70);
			Thread.sleep(150);
			channel.noteOff(76);

			channel.noteOn(80, 80);
			Thread.sleep(150);
			channel.noteOff(80);

			channel.noteOn(84, 90);
			Thread.sleep(150);
			channel.noteOff(84);
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
	}

	void lame() {
		try {
			Thread.sleep(150);

			channel.noteOn(72, 100);
			Thread.sleep(70);
			channel.noteOff(72);

			channel.noteOn(84, 70);
			Thread.sleep(150);
			channel.noteOff(84);

		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
	}
}
