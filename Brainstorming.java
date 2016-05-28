 import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Event;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

@SuppressWarnings("serial")
public class Brainstorming extends JFrame implements ActionListener {
	static Brainstorming w = new Brainstorming();
	final String version = "Ver 2.1.4";
	int diffX, diffY;
	int allNum = 0;
	int row = 0, col = 0;
	JCheckBox cb;
	JCheckBoxMenuItem menuRevisible;
	JLayeredPane layerPane;
	ArrayList<Idea> arrayIdeas = new ArrayList<Idea>();
	ArrayList<Idea> arrayRemove = new ArrayList<Idea>();

	enum Alignment {
		GLOUP, NUMBER
	}

	enum DicLocation {
		X, Y
	}

	public static void main(String[] args) {
		w.setVisible(true);
	}

	public Brainstorming() {
		cb = new JCheckBox("再表示しない（変更可）");
		layerPane = new JLayeredPane();
		layerPane.setLayout(new FlowLayout());
		setContentPane(layerPane);
		setLayout(null);
		setTitle("BrainStorming");
		setSize(925, 660);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		InitMenuBar();
		// http://www.tohoho-web.com/java/other.htm#TextEditor
		// look&feelの設定
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			SwingUtilities.updateComponentTreeUI(this);
		} catch (Exception e) {
		}
		newIdea();
	}

	void newIdea() {
		arrayIdeas.add(new Idea());
		layerPane.add(arrayIdeas.get(allNum), 0);
		arrayIdeas.get(allNum).ta.requestFocus();
		allNum++;
	}

	void InitMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		// ------------------------------------------------//
		// http://www.tohoho-web.com/java/other.htm#TextEditor
		// メニューの作成
		JMenu menuFile = new JMenu("ファイル(F)");
		menuFile.setMnemonic('F');
		menuBar.add(menuFile);

		JMenuItem menuNew = new JMenuItem("新しいウィンドウ(N)");
		menuNew.setMnemonic('N');
		menuNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Event.CTRL_MASK | Event.SHIFT_MASK));
		menuNew.setActionCommand("New");
		menuNew.addActionListener(this);
		menuFile.add(menuNew);

		JMenu menuSave = new JMenu("保存(S)");
		menuSave.setMnemonic('S');
		menuFile.add(menuSave);

		JMenuItem menuTextSave = new JMenuItem("テキスト(.txt)...");
		menuTextSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK));
		menuTextSave.setActionCommand("textSave");
		menuTextSave.addActionListener(this);
		menuSave.add(menuTextSave);

		JMenuItem menuCsvSave = new JMenuItem("CSV(.csv)...");
		menuCsvSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK | Event.SHIFT_MASK));
		menuCsvSave.setActionCommand("csvSave");
		menuCsvSave.addActionListener(this);
		menuSave.add(menuCsvSave);

		menuFile.addSeparator();

		JMenuItem menuExit = new JMenuItem("終了(Q)");
		menuExit.setMnemonic('Q');
		menuExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, Event.CTRL_MASK));
		menuExit.setActionCommand("Exit");
		menuExit.addActionListener(this);
		menuFile.add(menuExit);

		// ------------------------------------------------//
		JMenu menuEdit = new JMenu("編集(E)");
		menuEdit.setMnemonic('E');
		menuBar.add(menuEdit);

		JMenuItem menuAdd = new JMenuItem("新規カードを追加(N)");
		menuAdd.setMnemonic('N');
		menuAdd.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Event.CTRL_MASK));
		menuAdd.setActionCommand("Add");
		menuAdd.addActionListener(this);
		menuEdit.add(menuAdd);

		JMenuItem menuBack = new JMenuItem("削除したカードを再表示(B)");
		menuBack.setMnemonic('B');
		menuBack.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, Event.CTRL_MASK));
		menuBack.setActionCommand("Back");
		menuBack.addActionListener(this);
		menuEdit.add(menuBack);

		JMenuItem menuClear = new JMenuItem("全て削除してリセット(R)");
		menuClear.setMnemonic('R');
		menuClear.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, Event.CTRL_MASK));
		menuClear.setActionCommand("Clear");
		menuClear.addActionListener(this);
		menuEdit.add(menuClear);

		menuEdit.addSeparator();

		JMenu menuAlignment = new JMenu("整列(A)");
		menuAlignment.setMnemonic('A');
		menuEdit.add(menuAlignment);

		JMenuItem menuAlignmentNumber = new JMenuItem("追加順");
		menuAlignmentNumber.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, Event.CTRL_MASK | Event.SHIFT_MASK));
		menuAlignmentNumber.setActionCommand("AlignmentNumber");
		menuAlignmentNumber.addActionListener(this);
		menuAlignment.add(menuAlignmentNumber);

		JMenuItem menuAlignmentGloup = new JMenuItem("グループ順");
		menuAlignmentGloup.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, Event.CTRL_MASK | Event.SHIFT_MASK));
		menuAlignmentGloup.setActionCommand("AlignmentGloup");
		menuAlignmentGloup.addActionListener(this);
		menuAlignment.add(menuAlignmentGloup);

		// ------------------------------------------------//
		JMenu menuSetting = new JMenu("設定(P)");
		menuSetting.setMnemonic('P');
		menuBar.add(menuSetting);

		menuRevisible = new JCheckBoxMenuItem("削除確認を再表示しない(R)", true);
		menuRevisible.setMnemonic('R');
		menuRevisible.setActionCommand("Revisible");
		menuRevisible.addActionListener(this);
		menuRevisible.setSelected(false);
		menuSetting.add(menuRevisible);

		JMenu menuHelp = new JMenu("ヘルプ(H)");
		menuHelp.setMnemonic('H');
		menuBar.add(menuHelp);

		JMenuItem menuHowto = new JMenuItem("このアプリケーションについて(T)");
		menuHowto.setMnemonic('T');
		menuHowto.setActionCommand("Howto");
		menuHowto.addActionListener(this);
		menuHelp.add(menuHowto);

		JMenuItem menuVersion = new JMenuItem("バージョン情報(V)");
		menuVersion.setMnemonic('V');
		menuVersion.setActionCommand("Version");
		menuVersion.addActionListener(this);
		menuHelp.add(menuVersion);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		String cmd = arg0.getActionCommand();
		if (cmd.equals("Add")) {
			newIdea();
		} else if (cmd.equals("textSave")) {
			saveData(".txt");
		} else if (cmd.equals("csvSave")) {
			saveData(".csv");
		} else if (cmd.equals("Exit")) {
			exit();
		} else if (cmd.equals("Revisible")) {
			reVisible();
		} else if (cmd.equals("Clear")) {
			allClear();
		} else if (cmd.equals("AlignmentGloup")) {
			alignmentCard(Alignment.GLOUP);
		} else if (cmd.equals("AlignmentNumber")) {
			alignmentCard(Alignment.NUMBER);
		} else if (cmd.equals("New")) {
			newProject();
		} else if (cmd.equals("Back")) {
			Back();
		} else if (cmd.equals("Version")) {
			Version();
		} else if (cmd.equals("Howto")) {
			Howto();
		}
	}

	void Howto() {
		JTextArea text = new JTextArea();
		text.setEditable(false);
		text.setBackground(null);
		text.setText("ブレインストーミングとは、集団でアイデアを出し合うことによって相互交錯の連鎖反応や発想の誘発を期待する技法である。" + "\n" + "データをカードに記述し、カードをグループごとにまとめて、図解し、論文等にまとめていく。\n\n" + "【4つの原則】\n" + "　判断・結論を出さない\n" + "　粗野な考えを歓迎する\n" + "　質より量を重視する\n" + "　アイデアを結合し発展させる\n\n" + "【流れ】\n" + "　事前に議題を通達しておき、各自アイデアを浮かべておく。\n" + "　ブレインストーミングを始めると、それぞれの案をとにかくたくさん挙げて記録し発言に対しての新たな案も考える。\n" + "　ある程度の数が溜まったら、似た案に対してプルダウンリストから同じ色を振り分ける。\n" + "　データとして保存する。");
		JOptionPane.showMessageDialog(this, text, "このアプリケーションについて", JOptionPane.DEFAULT_OPTION);
	}

	void exit() {
		int ans = JOptionPane.showConfirmDialog(this, "終了しますか?", "確認", JOptionPane.YES_NO_OPTION);
		if (ans == JOptionPane.YES_OPTION) {
			dispose();
		}
	}

	void reVisible() {
		if (cb.isSelected()) {
			cb.setSelected(false);
		} else {
			cb.setSelected(true);
		}
	}

	void allClear() {
		int ans = JOptionPane.showConfirmDialog(this, "リセットしますか?", "確認", JOptionPane.YES_NO_OPTION);
		if (ans == JOptionPane.YES_OPTION) {
			for (int i = 0; i < arrayIdeas.size(); i++) {
				arrayIdeas.get(i).setVisible(false);
			}
			arrayIdeas.clear();
			arrayRemove.clear();
			allNum = 0;
			row = col = 0;
			newIdea();
		}
	}

	void alignmentCard(Alignment number) {
		row = col = 0;
		boolean change = false;
		if (number == Alignment.GLOUP) {
			for (int num = 1; num <= 7; num++) {
				for (int i = 0; i < arrayIdeas.size(); i++) {
					Idea idea = arrayIdeas.get(i);
					if (idea.box.getSelectedIndex() == num && idea.isVisible()) {
						setIdea(idea);
						change = true;
					}
				}
			}

			if (change && row != 0) {
				col++;
				row = 0;
			}

			for (int i = 0; i < arrayIdeas.size(); i++) {
				Idea idea = arrayIdeas.get(i);
				if (idea.box.getSelectedIndex() == 0 && idea.isVisible()) {
					setIdea(idea);
				}
			}
		} else if (number == Alignment.NUMBER) {
			for (int i = 0; i < arrayIdeas.size(); i++) {
				Idea idea = arrayIdeas.get(i);
				if (idea.isVisible()) {
					setIdea(idea);
				}
			}
		}
	}

	void setIdea(Idea o) {
		o.setLocation(dicLocation(DicLocation.X), dicLocation(DicLocation.Y));
	}

	void newProject() {
		Brainstorming bs = new Brainstorming();
		bs.setVisible(true);
		bs.setLocation(getLocation().x + 20, getLocation().y + 20);
	}

	void Back() {
		if (!arrayRemove.isEmpty()) {
			Idea idea = arrayRemove.get(arrayRemove.size() - 1);
			setIdea(idea);
			idea.setVisible(true);
			arrayRemove.remove(arrayRemove.size() - 1);
		}
	}

	void Version() {
		JLabel ver = new JLabel(version);
		ver.setHorizontalAlignment(JLabel.CENTER);
		ver.setFont(new Font("Dialog", 0, 15));
		JOptionPane.showMessageDialog(this, ver, "バージョン情報", JOptionPane.DEFAULT_OPTION);
	}

	int dicLocation(DicLocation x) {
		int locale = 0;
		int setRow = (getSize().height - 55) / 115;
		if (row >= setRow) {
			col++;
			row = 0;
		}
		if (x == DicLocation.X) {
			locale = col * 301 + 30;
		} else if (x == DicLocation.Y) {
			locale = row * 115 + 30;
			row++;
		}
		return locale;
	}

	void saveData(String style) {
		JFileChooser chooser = new JFileChooser();
		int ans = chooser.showSaveDialog(this);
		if (ans == JFileChooser.APPROVE_OPTION) {
			try {
				// CSVでの書き出し
				// http://java-reference.sakuraweb.com/java_file_csv_write.html
				// http://tohokuaiki.hateblo.jp/entry/20140205/1391589045
				String filename = chooser.getSelectedFile() + style;
				PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "SJIS")));
				pw.println("番号,内容,グループ,メモ,追加日時");
				for (int i = 0; i < arrayIdeas.size(); i++) {
					Idea idea = arrayIdeas.get(i);
					if (idea.isVisible())
						pw.println(idea.num + "," + idea.ta.getText() + "," + idea.box.getSelectedIndex() + "," + idea.memo.getText() + "," + idea.addDate);
				}
				pw.close();
			} catch (IOException e) {
				JOptionPane.showConfirmDialog(this, e.getMessage(), "警告", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	// ///////////////////////////////////////////////////////////////////
	class Idea extends Panel {
		JPanel p1 = new JPanel();
		JLabel lb = new JLabel();
		JButton btn = new JButton("×");
		JTextArea ta = new JTextArea();
		JTextArea memo = new JTextArea();
		JScrollPane sp = new JScrollPane(ta);
		JComboBox<String> box;
		int num = allNum + 1;
		String addDate;

		@SuppressWarnings({ "deprecation", "unchecked" })
		public Idea() {
			setBounds(dicLocation(DicLocation.X), dicLocation(DicLocation.Y), 231, 75);
			setLayout(null);
			addMouseMotionListener(new MyMouseListener_Move_());
			addMouseListener(new MyMouseListener_Move_());
			addMouseListener(new MyMouseListener_front_(this));

			Date date = new Date(Calendar.getInstance().getTimeInMillis());
			addDate = new SimpleDateFormat("MM/dd HH:mm").format(date);

			p1.setLayout(null);
			p1.setBackground(Color.GRAY);
			p1.setBounds(0, 0, 231, 20);

			lb = new JLabel();
			lb.setCursor(new Cursor(MOVE_CURSOR));
			lb.setFocusable(true);
			lb.setBounds(1, 1, 46, 19);
			lb.setText("" + num);
			lb.setVerticalAlignment(JLabel.CENTER);
			lb.setHorizontalAlignment(JLabel.CENTER);
			lb.setForeground(Color.WHITE);
			p1.add(lb);

			memo.setBounds(48, 1, 69, 19);
			memo.addMouseListener(new MyMouseListener_front_(this));
			memo.setToolTipText("メモ");
			memo.setFont(new Font("MS UI Gothic", Font.PLAIN, 12));
			p1.add(memo);

			String[] data = { ". . .", "Red", "Orange", "Yellow", "Green", "Blue", "Indigo", "Purple" };
			box = new JComboBox<String>(data);
			box.setToolTipText("グループ");
			box.setForeground(Color.WHITE);
			box.setBackground(Color.GRAY);
			box.setBounds(118, 1, 65, 19);
			box.addMouseListener(new MyMouseListener_front_(this));
			box.setRenderer(new MyRenderer());
			box.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Color color = decColor(box.getSelectedIndex());

					p1.setBackground(color);
					ta.setCaretColor(color);
					box.setBackground(color);
					sp.setBorder(BorderFactory.createLineBorder(color));
				}
			});
			p1.add(box);

			btn.setToolTipText("消去");
			btn.setOpaque(true);
			btn.setForeground(Color.RED);
			btn.setBounds(184, 1, 46, 19);
			btn.setVerticalAlignment(JButton.CENTER);
			btn.setHorizontalAlignment(JButton.CENTER);
			btn.addMouseListener(new MyMouseListener_front_(this));
			btn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (cb.isSelected()) {
						removeThisCard();
					} else {
						// ダイアログにチェックボックス
						// http://www.coderanch.com/t/415941/GUI/java/Add-Checkbox-JOptionPane
						Object[] msgContent = { "カードを削除しますか？", cb };
						int ans = JOptionPane.showConfirmDialog(w, msgContent, "確認", JOptionPane.YES_NO_OPTION);
						if (ans == JOptionPane.YES_OPTION) {
							removeThisCard();
						}
						if (cb.isSelected()) {
							menuRevisible.setSelected(true);
						}
					}
				}
			});
			p1.add(btn);

			ta.setLineWrap(true);
			ta.setToolTipText("追加日時 " + addDate);
			ta.setCaretColor(Color.GRAY);
			ta.addMouseListener(new MyMouseListener_front_(this));
			ta.setFont(new Font("Dialog", Font.PLAIN, 13));

			sp.setBounds(0, 20, 231, 55);

			add(p1, BorderLayout.NORTH);
			add(sp, BorderLayout.CENTER);
		}

		void removeThisCard() {
			setVisible(false);
			arrayRemove.add(this);
		}

		// ---------------------------------------------------------//
		Color decColor(int num) {
			Color color = null;
			switch (num) {
			case 0:
				color = Color.GRAY;
				break;
			case 1:
				color = new Color(230, 0, 18);
				break;
			case 2:
				color = new Color(255, 152, 0);
				break;
			case 3:
				color = new Color(195, 190, 0);
				break;
			case 4:
				color = new Color(0, 153, 50);
				break;
			case 5:
				color = new Color(0, 104, 183);
				break;
			case 6:
				color = new Color(29, 32, 136);
				break;
			case 7:
				color = new Color(146, 7, 131);
				break;
			}

			return color;
		}

		// ========================================
		// プルダウンリストの色
		// http://www.coderanch.com/t/415941/GUI/java/Add-Checkbox-JOptionPane
		@SuppressWarnings("rawtypes")
		class MyRenderer extends JLabel implements ListCellRenderer {
			MyRenderer() {
				setOpaque(true);
			}

			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				setText(value.toString());

				if (isSelected) {
					setForeground(Color.BLACK);
					setBackground(decColor(index));
				} else {
					setForeground(Color.WHITE);
					setBackground(decColor(index));
				}

				return this;
			}
		}

		// ========================================
		// レイヤーペインで上に移動
		// http://www.technotype.net/tutorial/tutorial.php?fileId={Swing}&sectionId={swing-jlayeredpane-about}
		class MyMouseListener_front_ extends MouseAdapter {
			Idea action;

			public MyMouseListener_front_(Idea idea) {
				action = idea;
			}

			@Override
			public void mousePressed(MouseEvent e) {
				layerPane.moveToFront(action);
			}
		}
	}

	// /////////////////////////////////////////////////////////////////////////

	class MyMouseListener_Move_ extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent e) {
			diffX = e.getXOnScreen() - e.getComponent().getX();
			diffY = e.getYOnScreen() - e.getComponent().getY();
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			int x = e.getXOnScreen() - diffX;
			int y = e.getYOnScreen() - diffY;
			if (x < 0) {
				x = 0;
			} else if (x + 247 > getSize().getWidth()) {
				x = (int) (getSize().getWidth() - 247);
			}
			if (y < 0) {
				y = 0;
			} else if (y + 135 > getSize().getHeight()) {
				y = (int) (getSize().getHeight() - 135);
			}
			e.getComponent().setLocation(x, y);
		}
	}
}