

import java.util.*;
import java.io.*;
//字符串匹配包
//默认正则表达式匹配
import java.util.regex.*;

//该类实现了从Rule.txt文件中读取规则
public class ProduceRule {
	/*List是一个接口，而ArrayList是List接口的一个实现类。
	ArrayList类继承并实现了List接口。
	 */
	private ArrayList<String> resultList;

	private ArrayList<ArrayList> conditList;

	private List<ArrayList> sumList;

	Scanner s;

	public ProduceRule() {
		resultList = new ArrayList<String>();                 //该列表用于放置规则的结论部分
		conditList = new ArrayList<ArrayList>();              //该列表用于放置规则的条件部分
		sumList = new ArrayList<ArrayList>();                 //该列表用来放置resultList和conditList
	}

	//通过Scanner类实现了读取特定格式的文件
	public List readFile() {
		String fileName = "Rule.txt";
		File file = new File(fileName);
		try {
			s = new Scanner(new FileInputStream(file));
			String wholeExp = "^IF\\s*(.+)\\s*THEN\\s*(.+)";
			String partExp = "\\s*AND\\s*";

			//'^'和'$'分别匹配一行的开始和结束，提取出条件和结论
			Pattern wholePat = Pattern.compile(wholeExp, Pattern.MULTILINE);
			Pattern partPat = Pattern.compile(partExp, Pattern.MULTILINE);

			//进行一行一行的扫描
			//如果此扫描器的输入中还有另一行，则java.util.Scanner类的hasNextLine()方法将返回true。
			while (s.hasNextLine()) {
				s.nextLine();
				String line = s.findInLine(wholePat);
				if (line != null) {
					MatchResult result = s.match();
					parseLine(result.group(1), partPat);
					resultList.add(result.group(2));
				} else {
					System.out.println("errror format");
				}

			}
			sumList.add(conditList);
			sumList.add(resultList);
			return sumList;

			//捕捉没有具体未知的文件
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			s.close();
		}

		return null;

	}

	//ParseLine 方法将通知 Document 对象，作为参数传递给它的字符串进行分析。
	private void parseLine(String str, Pattern pat) {
		Scanner scanner = new Scanner(str).useDelimiter(pat);
		ArrayList<String> cells = new ArrayList<String>();
		while (scanner.hasNext()) {
			cells.add(scanner.next());
		}
		conditList.add(cells);
	}

}
