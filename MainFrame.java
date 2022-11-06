
import javax.swing.*;
import java.text.*;
import java.util.Hashtable;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
//接口表示匹配操作的结果，输出最后扫描结果
import java.util.regex.MatchResult;
//图形化界面的字体设置包
import java.awt.font.*;

/*主窗口,生成菜单栏和工具栏,接受用户的输入并显示结果*/
public class MainFrame extends JFrame {

	private static final int WIDTH = 600;
    private static final int HEIGHT = 600;
    private static final int ICONWIDTH=30;
	
	private boolean modeOdd;

    AbstractAction saveAction;
    AbstractAction createKBAction;
    HelpAction helpAction;

	private JFileChooser chooser;

	private JLabel displayName, displayIcon, displayLabel;
    private JTextArea input;
    //描绘
    private JTextArea depict;
    private JButton certain;
    private JPanel panel;
    private JRadioButtonMenuItem brief,detailedly;
    

	public MainFrame() {
		modeOdd=true;

		createKBAction=new CreateKBAction();
		helpAction = new HelpAction();

		createMenu();
		//Jframe不可直接添加组件，需要容器装载
		Container cp = getContentPane();
		displayName = new JLabel("请输入条件:");
		displayName.setBounds(50, 0, 200, 150);

		input = new JTextArea();
		input.setBounds(50, 120, 200, 250);
		input.setBackground(new Color(120, 246, 250));
		input.setLineWrap(true);

		certain = new JButton("确定");
		certain.setBounds(100, 420, 80, 40);
		//响应图形界面用户点击按钮
		certain.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ArrayList<String> userDemandList = new ArrayList<String>();
				String str = input.getText();
				Scanner s = new Scanner(str);
				while (s.hasNext()) {
					userDemandList.add(s.next());
				}
				SearchPeople searchPeople = new SearchPeople(userDemandList);
				String animalName = searchPeople.search(modeOdd);
				if (animalName != null) {
					depict.setText(animalName + "\n");
					depict.append("推理过程如下:"+"\n");

					List<ArrayList> infCause = searchPeople.getInfCause();
					List<String> infResult = searchPeople.getInfResult();
                    int i=0;
					for (ArrayList<String> causeList:infCause) {
						//调用函数，输出结果
						for (String cause : causeList)
							depict.append(cause + "\n");
						depict.append("推出:");
						depict.append(infResult.get(i) + "\n");
						i++;
					}

					setIcon(animalName);

				} else {
					JOptionPane.showMessageDialog(MainFrame.this, "请输入更多有效信息",
							"信息提示", JOptionPane.INFORMATION_MESSAGE);
				}

			}

		});

		displayLabel = new JLabel("推理显示");
		displayLabel.setBounds(300, 0, 150, 150);

		// 如果查找出,则显示动物的图片
		displayIcon = new JLabel();
		displayIcon.setBounds(300, 120, 200, 150);
		displayIcon.setIcon(new ImageIcon(".\\image\\鲁班.gif"));

		depict = new JTextArea();
		depict.setEditable(false);
		depict.setLineWrap(true);
		depict.setBackground(new Color(139, 255, 118));

		JScrollPane pane = new JScrollPane(depict);
		pane.setBounds(300, 270, 200, 200);

		panel = new JPanel();
		panel.setLayout(null);
		panel.add(displayName);
		panel.add(input);
		panel.add(certain);
		panel.add(displayLabel);
		panel.add(displayIcon);
		panel.add(pane);
		cp.add(panel, BorderLayout.CENTER);
		cp.add(createToolBar(), BorderLayout.NORTH);

	}

	public void createMenu() {
		JMenu fileMenu = new JMenu("退出模式");
		fileMenu.setMnemonic('F');
		JMenuItem newItem = new JMenuItem("New", 'N');
		newItem.setEnabled(false);
		fileMenu.add(newItem);

		fileMenu.addSeparator();

		JMenuItem save = new JMenuItem(saveAction);
		save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				InputEvent.CTRL_MASK));
		fileMenu.add(save);

		JMenuItem exit = new JMenuItem("Exit");
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);

			}
		});
		fileMenu.add(exit);
		
		JMenu editMenu=new JMenu("编辑");
		//如果焦点被包含在此按钮祖先窗口中的某个地方将激活此按钮
		//当鼠标光点在按钮附近时，激活
		editMenu.setMnemonic('E');
		JMenuItem createKBItem=new JMenuItem(createKBAction);
		//快捷键
		createKBItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B,InputEvent.CTRL_MASK));
		editMenu.add(createKBItem);
		
		
		JMenu modeMenu=new JMenu("模式");
		modeMenu.setMnemonic('M');
		JMenu modeStyle=new JMenu("选择模式");
		modeStyle.setMnemonic('S');
		brief=new JRadioButtonMenuItem(new ModeAction("简单"));
		detailedly=new JRadioButtonMenuItem(new ModeAction("详细"));
		detailedly.setSelected(true);
		ButtonGroup group=new ButtonGroup();
		group.add(brief);
		group.add(detailedly);
		modeStyle.add(brief);
		modeStyle.add(detailedly);
		modeMenu.add(modeStyle);
		
		JMenu helpMenu = new JMenu("帮助");
		helpMenu.setMnemonic('H');
		JMenuItem help = new JMenuItem(helpAction);
		help.setAccelerator(KeyStroke.getKeyStroke("F1"));
		helpMenu.add(help);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		menuBar.add(modeMenu);
		menuBar.add(helpMenu);

	}

	//按钮响应
	public JToolBar createToolBar() {
		JToolBar toolBar = new JToolBar();
	//	toolBar.add(openAction);
		toolBar.add(saveAction);
		toolBar.add(createKBAction);
		return toolBar;
	}
//创建知识库按钮响应
	private class CreateKBAction extends AbstractAction{
		Icon icon;
		public CreateKBAction(){
			icon=getIcon(".\\image\\创建知识库.gif");
			putValue(Action.NAME,"创建知识库");
			putValue(Action.SMALL_ICON,icon);
			putValue(Action.SHORT_DESCRIPTION,"创建知识库");
		}
		public void actionPerformed(ActionEvent e){
			new CreateKB(MainFrame.this);
		}
	}
	
	private class ModeAction extends AbstractAction{
		public ModeAction(String name){
			putValue(Action.NAME,name);
			
		}
		public void actionPerformed(ActionEvent e){
			if(e.getSource() instanceof JRadioButtonMenuItem){
				JRadioButtonMenuItem radioButton=(JRadioButtonMenuItem)e.getSource();
				if(radioButton==brief)
					modeOdd=false;
				else
					modeOdd=true;
			}
				
			
		}
	}

	//给出一段文字，提示输入格式
	private class HelpAction extends AbstractAction {
		final Hashtable<TextAttribute, Object> map;

		AttributedString str;

		LineBreakMeasurer lineMeasurer;

		int paragraphStart, paragraphEnd;

		public HelpAction() {
			putValue(Action.NAME, "help");
			putValue(Action.SHORT_DESCRIPTION, "帮助");

			map = new Hashtable<TextAttribute, Object>();

			map.put(TextAttribute.FAMILY, "Serief");
			map.put(TextAttribute.SIZE, new Float(18.0));
			map.put(TextAttribute.FOREGROUND, Color.YELLOW);

			str = new AttributedString("在输入判定条件时,请遵循一下条件:"+"\n"
					+ "输入的条件不应带有英文字母,比如:IF,AND " 
					+ "在输完一个条件后,要回车转下行再输入另一条件 "+"\n"
					+ "输入的条件要与Rule.txt文件中条件相同 " +"\n"
					+ "如这种格式:" + "该人物去下路 "+"\n"
					+ "腿短 " +"\n"
					+ "会放大炮"+"\n"
					+ " 就能够得出该人物是鲁班和推出该游戏人物的步骤", map);

		}

		public void actionPerformed(ActionEvent e) {
			JFrame frame = new JFrame("help");

			frame.add(new JPanel() {
				public void paintComponent(Graphics g) {
					super.paintComponent(g);

					Graphics2D g2 = (Graphics2D) g;
					g2.setPaint(new GradientPaint(0, 0, new Color(150, 22, 0, 255),
							getWidth(), getHeight(), new Color(100, 100, 255)));
					
					g2.fillRect(0, 0, getWidth(), getHeight());
					if (lineMeasurer == null) {
						AttributedCharacterIterator paragraph = str
								.getIterator();
						
						paragraphStart = paragraph.getBeginIndex();
						paragraphEnd = paragraph.getEndIndex();
						FontRenderContext frc = g2.getFontRenderContext();
						lineMeasurer = new LineBreakMeasurer(paragraph, frc);
					}
					float breakWidth = (float) getWidth();
					float drawPosY = 50;
					lineMeasurer.setPosition(paragraphStart);
					while (lineMeasurer.getPosition() < paragraphEnd) {
						TextLayout layout = lineMeasurer.nextLayout(breakWidth);
						float drawPosX = layout.isLeftToRight() ? 0
								: breakWidth - layout.getAdvance();
						
						drawPosY += layout.getAscent();
						layout.draw(g2, drawPosX, drawPosY);
						drawPosY += layout.getDescent() + layout.getLeading();
					}

					g2.setColor(Color.WHITE);

				}

			});
			
			frame.setSize(MainFrame.this.getWidth(), MainFrame.this
							.getHeight());
			
			frame.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {

				}
			});
			
			frame.setVisible(true);
		}

	}
	
	private Icon getIcon(String address){
		ImageIcon icon=new ImageIcon(address);
		if(icon.getIconWidth()>ICONWIDTH){
			icon=new ImageIcon(icon.getImage().getScaledInstance(ICONWIDTH, -1, Image.SCALE_DEFAULT));
		}
		return icon;
	}

	private void setIcon(String string) {
		String patString = "(.+)是(.+)";
		List<String> list = parseLine(string, patString);
		String animalName = list.get(1).trim();
		Map<String, Icon> iconMap = loadIcon();
		Icon showIcon = iconMap.get(animalName);
		displayIcon.setIcon(showIcon);

	}

	private Map loadIcon() {
		Map<String, Icon> iconMap = new HashMap<String, Icon>();

		ImageIcon DajiIcon = new ImageIcon(".\\image\\妲己.gif");
		ImageIcon SunceIcon = new ImageIcon(".\\image\\孙策.gif");
		ImageIcon YaoIcon = new ImageIcon(".\\image\\瑶.gif");
		ImageIcon CaiwenjiIcon = new ImageIcon(".\\image\\蔡文姬.jpg");
		ImageIcon BailiIcon = new ImageIcon(".\\image\\百里守约.gif");
		ImageIcon ChengyaojinIcon = new ImageIcon(".\\image\\程咬金.gif");
		ImageIcon MilaidiIcon = new ImageIcon(".\\image\\米莱狄.gif");
		ImageIcon AnqilaIcon = new ImageIcon(".\\image\\安琪拉.gif");
		ImageIcon LanlinwangIcon = new ImageIcon(".\\image\\兰陵王.gif");
		ImageIcon ZhubajieIcon = new ImageIcon(".\\image\\猪八戒.gif");
		ImageIcon LubanIcon = new ImageIcon(".\\image\\鲁班.gif");


		iconMap.put(new String("妲己"), DajiIcon);
		iconMap.put(new String("孙策"), SunceIcon);
		iconMap.put(new String("瑶"), YaoIcon);
		iconMap.put(new String("蔡文姬"), CaiwenjiIcon);
		iconMap.put(new String("百里守约"), BailiIcon);
		iconMap.put(new String("程咬金"), ChengyaojinIcon);
		iconMap.put(new String("米莱狄"), MilaidiIcon);
		iconMap.put(new String("安琪拉"), AnqilaIcon);
		iconMap.put(new String("兰陵王"), LanlinwangIcon);
		iconMap.put(new String("猪八戒"), ZhubajieIcon);
		iconMap.put(new String("鲁班"), LubanIcon);



		return iconMap;
	}
//字符出啊不能匹配
	private List parseLine(String str, String patString) {
		List<String> stringList = new ArrayList<String>();
		Scanner scanner = new Scanner(str);
		scanner.findInLine(patString);
		MatchResult result = scanner.match();
		for (int i = 1; i <= result.groupCount(); i++) {
			stringList.add(result.group(i));

		}
		return stringList;

	}

	public static void main(String[] args) {
		// TODO 自动生成方法存根
		JFrame frame = new MainFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(WIDTH, HEIGHT);
		frame.setVisible(true);

	}

}
