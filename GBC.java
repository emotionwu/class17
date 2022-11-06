
import java.awt.*;
import java.awt.GridBagConstraints;

//继承awt中包，组件控制
//该类用于控制组件
public class GBC extends GridBagConstraints {
	//构造函数
	public GBC(int gridx,int gridy){
		this.gridx=gridx;
		this.gridy=gridy;
	}

//gridwidth横占的单元格个数，gridheight列占的单元格格式
	public GBC(int gridx,int gridy,int gridWidth,int gridHeight){
		  this.gridx = gridx;
	      this.gridy = gridy;
	      this.gridwidth = gridwidth; 
	      this.gridheight = gridheight; 
		
	}

//将组件放在某个位置
	public GBC setAnchor(int anchor) 
	   { 
	      this.anchor = anchor; 
	      return this;
	   }

	   //填充空间
	public GBC setFill(int fill) 
	   { 
	      this.fill = fill; 
	      return this;
	   }

	   //设置窗口的大小
	 public GBC setWeight(double weightx, double weighty) 
	   { 
	      this.weightx = weightx; 
	      this.weighty = weighty; 
	      return this;
	   }

	   //组件彼此之间的间距
	 public GBC setInsets(int distance) 
	   { 
	      this.insets = new Insets(distance, distance, distance, distance);
	      return this;
	   }

	 public GBC setInsets(int top, int left, int bottom, int right) 
	   { 
	      this.insets = new Insets(top, left, bottom, right);
	      return this;
	   }

	   //组件内部填充空间时，即给组件的最小添加多少宽度
	 public GBC setIpad(int ipadx, int ipady) 
	   { 
	      this.ipadx = ipadx; 
	      this.ipady = ipady; 
	      return this;
	   }

}
