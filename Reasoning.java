//处理数组包
import java.util.*;

/*该类实现了推理过程*/
public class Reasoning {
	//用户输入的条件内容放在一个数组当中，与所有条件的数组进行字符串匹配
	private ArrayList<String> userDemandList = new ArrayList<String>();

	private ArrayList<ArrayList> infCause;

	private ArrayList<String> infResult;

	private Rule rule;

	// 用户希望得到结果,是下面其中的某个人物
	String[] str = new String[] { "该人物是鲁班", "该人物是妲己", "该人物是瑶", "该人物是蔡文姬",
			"该人物是孙策", "该人物是百里守约", "该人物是程咬金","该人物是米莱狄" ,"该人物是安琪拉","该人物是兰陵王","该人物是猪八戒"};

	List<String> strList;
	
//构造函数
	public Reasoning(ArrayList<String> userDemandList) {
		this.userDemandList = userDemandList;
		strList = Arrays.asList(str);

		infCause = new ArrayList<ArrayList>();

		infResult = new ArrayList<String>();
	}
/*该方法通过比较用户输入和规则列表中条件部分,判定用户输入是否包含了某条规则的条件部分,
 * 若不是,就比较下条规则的条件,不停地循环,直至最后一条规则.
 * 若是,判定该条规则的结论是否为最终结论,若是,则返回结论!否则,判断该条规则的结论是否应放置在用户输入列表中.
 * */
	public String findCause(ArrayList<Rule> rulesList) {
		/*判定用户输入是否为空,以及是否矛盾*/
		if (userDemandList != null && isContradict(userDemandList)) {
			for (int i = 0; i < rulesList.size(); i++) {

				List<String> causeList = new ArrayList<String>(
						rulesList.get(i).cause.size());

				for (String tempString : rulesList.get(i).cause) {
					causeList.add(tempString.trim());
				}
			/*	trim() 函数移除字符串两侧的空白字符或其他预定义字符.
			  该功能除去字符串开头和末尾的空格或其他字符。
			  函数执行成功时返回删除了string字符串首部和尾部空格的字符串，
			  用以准确得到条件数组
			 */
				String result = new String(rulesList.get(i).result.trim());
				if (userDemandList.containsAll(causeList))

					if (strList.contains(result)) {

						return result;
					}

					else {
						if (!userDemandList.contains(result)) {
							userDemandList.add(result);
							if (!isContradict(userDemandList))
								return null;
							i = 0;
						} else
							continue;

					}

			}
		}
		return null;

	}

	//用于判断用户输入是否包含矛盾的条件
	public boolean isContradict(ArrayList<String> userDemandList) {
		if (userDemandList.contains("该人物去上路")
				&& userDemandList.contains("该人物去下路"))
			return false;
		if (userDemandList.contains("该人物辅助别人") && userDemandList.contains("该人物会打怪兽"))
			return false;
		if (userDemandList.contains("该人物是辅助")
				&& userDemandList.contains("该人物去上路"))
			return false;

		return true;
	}

	//该方法与findcase一致
	public String find(ArrayList<Rule> rulesList) {
		ArrayList tempList = new ArrayList();

		while(this.userDemandList != null) {
			Iterator var3 = this.userDemandList.iterator();

			while(var3.hasNext()) {
				String str = (String)var3.next();
				tempList.add(str);
			}

			this.findcause(rulesList);
			if (!this.findcause(rulesList)) {
				if (this.userDemandList.size() == 1) {
					return (String)this.userDemandList.get(0);
				}
				break;
			}
		}

		return null;
	}

	/*实现了字符串的匹配和删除条件*/
	private boolean findcause(ArrayList<Rule> rulesList) {
		boolean odd = false;
		for (int i = 0; i < rulesList.size(); i++) {

			ArrayList<String> causeList = new ArrayList<String>(rulesList
					.get(i).cause.size());

			for (String tempString : rulesList.get(i).cause) {
				causeList.add(tempString.trim());
			}

			String result = new String(rulesList.get(i).result.trim());

			if (userDemandList.containsAll(causeList)) {

				infCause.add(causeList);

				infResult.add(result);

				userDemandList.removeAll(causeList);
				odd = true;
				if (!userDemandList.contains(result)) {
					userDemandList.add(result);
				}
			}
		}
		return odd;
	}

	/*以下两个方法体实现获得到推理步骤*/
	public List<ArrayList> getInfCause() {
		return infCause;

	}

	public List<String> getInfResult() {
		return infResult;

	}

}
