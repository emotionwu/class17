
import java.util.*;

//用来放置规则的条件和结论的类,是该应用程序的主要数据结构
//以线性表存储条件
//构造函数
public class Rule {
	String result;                     //结论为字符串类型

	ArrayList<String> cause;          //条件被放置在列表类中


	public Rule(ArrayList<String> cause, String result) {
		this.result = result;
		this.cause = cause;

	}

}
