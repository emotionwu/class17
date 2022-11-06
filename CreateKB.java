
//导入绘画包
import java.awt.*;
import java.awt.event.*;

//图形可视化包
import javax.swing.*;

//能得到所有类
import java.io.*;
//读系统文件
import java.util.Scanner;

/*该类实现了用户输入接口并显示用户输入,并且将按照特定格式将用户输入存入Rule.txt文件中*/
//添加知识库到指定文件中
public class CreateKB {
	private static final int WIDTH = 500;

	private static final int HEIGHT = 600;

	private JDialog dialog;

	//面板容器类，承载下面控件
	private JPanel inPanel, showPanel, buttonPanel, panel;

	//文本框控件，输入单行
	private JTextField resultField;

	//输出结果框，允许多行输入
	private JTextArea causeArea, showArea;

	//按钮
	private JButton certain, exit;

	public CreateKB(JFrame owner) {
		dialog = new JDialog(owner, "创建知识库");
		//布局管理器
		dialog.setLayout(null);

		//设置结论的边界和大小
		resultField = new JTextField();
		resultField.setPreferredSize(new Dimension(210, 60));
		resultField.setBounds(0, 420, 210, 60);

		//提示标签
		JLabel fieldLabel = new JLabel("请输入结论");
		fieldLabel.setLabelFor(resultField);
		fieldLabel.setBounds(0, 360, 210, 60);

		//条件框设置
		causeArea = new JTextArea();
		causeArea.setPreferredSize(new Dimension(210, 300));
		//可换行
		causeArea.setLineWrap(true);
		causeArea.setBounds(0, 60, 210, 300);

		//条件提示标签
		JLabel areaLabel = new JLabel("请输入条件");
		areaLabel.setLabelFor(causeArea);
		areaLabel.setBounds(0, 0, 210, 60);

		showArea = new JTextArea();
		//控件不可被编辑
		showArea.setEditable(false);
		showArea.setLineWrap(true);
		showArea.setPreferredSize(new Dimension(210, 420));
		showArea.setBounds(290, 60, 210, 420);

		JLabel showAreaLabel = new JLabel("结果显示");
		showAreaLabel.setLabelFor(showArea);
		showAreaLabel.setBounds(290, 0, 210, 60);

		certain = new JButton(new InputAction());
		certain.setBounds(210, 210, 80, 60);
		exit = new JButton(new ExitAction());
		exit.setBounds(210, 510, 80, 60);

		dialog.add(resultField);
		dialog.add(fieldLabel);
		dialog.add(causeArea);
		dialog.add(areaLabel);
		dialog.add(showArea);
		dialog.add(showAreaLabel);
		dialog.add(certain);
		dialog.add(exit);
		dialog.setSize(WIDTH, HEIGHT);
		dialog.setVisible(true);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

	}

	private class InputAction extends AbstractAction {
		ImageIcon icon;

		public InputAction() {
			icon = new ImageIcon(".\\image\\input.gif");
			if (icon.getIconWidth() < 80)
				//图片缩放
				icon = new ImageIcon(icon.getImage().getScaledInstance(20, -1,
						Image.SCALE_DEFAULT));
			putValue(Action.SMALL_ICON, icon);

		}

		public void actionPerformed(ActionEvent e) {

			String str = causeArea.getText();
			String s = resultField.getText();
			if (s.length()==0||str.length()==0) {
				JOptionPane.showConfirmDialog(null, "请输入完整的条件与结论", "信息提示",
						JOptionPane.INFORMATION_MESSAGE);

			}
			else{
			showArea.setText("" + "IF" + " " + str + "\n" + "THEN" + " " + s);
			Scanner scanner=new Scanner(str);
			String tempString=new String();
			while(scanner.hasNextLine()){
				tempString +=scanner.nextLine();
				if(scanner.hasNextLine())
					tempString +=" "+"AND"+" ";
			}
			String string=new String();
			string="IF" + " " + tempString +" "+ "THEN" + " " + s;
			File file=new File(".\\Rule.txt");
			 try{
			 	    //缓冲
			    	 BufferedWriter out=new BufferedWriter(new FileWriter(file,true));
				     out.write(string);
				     out.newLine();
				     out.close();
			       
			 }catch(IOException ioe){
				ioe.printStackTrace();
			}
			
			}

		}
		
	}

	private class ExitAction extends AbstractAction {
		public ExitAction() {
			putValue(Action.NAME, "EXIT");
		}

		public void actionPerformed(ActionEvent e) {
			if (dialog != null)
				dialog.dispose();
		}

	}

}
