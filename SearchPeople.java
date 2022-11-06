/**
 * 
 */

import java.util.*;

//该类是控制类,负责除主窗口类以外的类的生成
public class SearchPeople {
	private Rule rule;

	private ProduceRule produceRule;

	private Reasoning reasoning;

	private ArrayList<Rule> rulesList;

	private List<ArrayList> sumList;

	private ArrayList<ArrayList<String>> conditList;

	private ArrayList<String> resultList;

	public SearchPeople(ArrayList<String> userDemandList) {
		rulesList = new ArrayList<Rule>();

		reasoning = new Reasoning(userDemandList);

		produceRule = new ProduceRule();

		List readFile = produceRule.readFile();

		sumList = readFile;

		if (sumList.size() == 2) {
			conditList = sumList.get(0);

			resultList = sumList.get(1);
			
			/*生成规则,并放置在rulesList列表中*/
			for (int i = 0; i < conditList.size(); i++) {
				ArrayList<String> tempList = new ArrayList<String>();

				for (String temp : conditList.get(i)) {
					tempList.add(temp);

				}
				String tempString = new String(resultList.get(i));

				rulesList.add(new Rule(tempList, tempString));
			}

		}

	}

	/*根据用户选择的模式:简单或详细模式,调用不同的推理方法,
	 * 默认条件下(条件为真),是调用详细模式*/
	public String search(boolean odd) {
		if(odd){
		return reasoning.find(rulesList);             //详细推理方法
		}
		 return reasoning.findCause(rulesList);      //简单推理方法
		
	}

	/*以下两个方法体实现获得到推理步骤*/
	public List<ArrayList> getInfCause() {
		return reasoning.getInfCause();

	}

	public List<String> getInfResult() {
		return reasoning.getInfResult();

	}

}
