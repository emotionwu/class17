package maze51;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import javax.swing.*;
import javax.swing.event.*;
// 该程序解决并可视化了机器人运动设计的问题
/*
Maze ：一个实现 BFS , DFS 和 A ＊算法的迷宫应用程序
迷宫 IDE : NetBeans
语言： JAVA Windows 应用程序一个实现 BFS , DFS 和 A ＊算法的迷宫应用程序算法应用
DFS , BFS 和 A ＊该程序解决并可视化了机器人运动计划（ robot motion planning ）的问题，
实现了算法 DFS , BFS 和 A ＊以及贪婪搜索算法（作为 A ＊的特例）的变体。算法 A ＊优于其他两个。
用户可以通过指定所需的行数和列数来更改网格中的单元格数。用户可以按照自己的计划頃加任意数量的障碍物。
用设计程序绘制曲线。通过单击消除单个障碍。
可以通过拖动鼠标来更改机器人和／或目标的位置。从“逐步”搜索跳到“移动”搜索，反之亦然即使搜索正在进行，也按相应的按钮。
即使搜索正在进行中，只要将滑块“ Speed ”放置在新的所需位置，然后按“ Move ”按钮，也可以更改可搜索搜索的速度。
应用程序认为机器人本身具有一定的体积。
 */
public class Maze51
{
    /** 程序的主要形式
     *
     */
    public static JFrame mazeFrame;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        int width  = 693;
        int height = 545;
        mazeFrame = new JFrame("Maze 5.0");
        mazeFrame.setContentPane(new MazePanel(width,height));
        mazeFrame.pack();
        mazeFrame.setResizable(false);

        // 将表单放在屏幕的中心
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double screenWidth = screenSize.getWidth();
        double ScreenHeight = screenSize.getHeight();
        int x = ((int)screenWidth-width)/2;
        int y = ((int)ScreenHeight-height)/2;

        mazeFrame.setLocation(x,y);
        mazeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mazeFrame.setVisible(true);
    } // end main()

    /**
     * 此类确定主窗体的内容
     * 并包含程序的所有功能。
     */
    public static class MazePanel extends JPanel
    {

        /**
         **********************************************************
         *         迷宫面板中的嵌套类
         **********************************************************
         */

        /**
         * 表示网格单元的辅助类
         */
        private class Cell
        {
            /** 单元格的行号（第 0 行是顶部）*/
            int row;
            /** 单元格的列号（第 0 列为左侧）*/
            int col;
            /** 算法 A* 的 g 函数值（从一个节点移动到另一个节点的成本）*/
            double g;
            /** 算法 A* 的 h 函数值（到目的地的距离*/
            double h;
            /** 算法 A* 的函数 f 的值 （g + h）*/
            double f;

            /** 每个状态对应于一个单元格
             每种情况都有一个前身
             存储在此变量中*/
            Cell prev;

            /** Cell Constructor*/
            public Cell(int row, int col)
            {
                this.row = row;
                this.col = col;
            }
        } // 辅助类单元的结束

        /**
         * 指定对单元格进行排序的辅助类（方法）
         *           基于他们的字段 f
         */
        private class CellComparatorByF implements Comparator<Cell>
        {
            @Override
            public int compare(Cell cell1, Cell cell2)
            {
                return (int)(cell1.f-cell2.f);
            }
        } // 比较器

        /**
         * 在“绘画”时处理鼠标移动的类
         *    障碍物或移动机器人和或目标。
         */
        private class MouseHandler implements MouseListener, MouseMotionListener
        {
            /**变量：当前行、当前列和当前值，即按按钮获得的值（坐标将包含的值）*/
            private int cur_row, cur_col, cur_val;
            @Override
            public void mousePressed(MouseEvent evt) //触摸按钮
            {
                //通过鼠标获取坐标
                int row = (evt.getY() - 10) / squareSize;
                int col = (evt.getX() - 10) / squareSize;
                if (row >= 0 && row < rows && col >= 0 && col < columns) //如果它在迷宫的边界内
                {
                    //εαν το realtime είναι ίσο με το real_time τότε γράψε true αλλιώς δεν βρέθηκε και η αναζήτηση έφτασε στο τέλος
                    boolean real_time = realTime ? true : !found && !searching;
                    if (real_time)
                    {
                        if (realTime) //ενεργοποιείται μόνο όταν πατήσουμε το κουμπί "Σε πραγματικό χρόνο"
                        {
                            searching = true;
                            fillGrid();
                        }
                        cur_row = row;
                        cur_col = col;
                        cur_val = grid[row][col]; //坐标将具有的值
                        //通过按如果盒子是空的（白色）来做（黑色）（有障碍物）
                        if (cur_val == EMPTY)
                        {
                            grid[row][col] = OBST;
                        }
                        //如果盒子已满（黑色），请按键（白色）（无障碍物）
                        if (cur_val == OBST)
                        {
                            grid[row][col] = EMPTY;
                        }
                    }
                }
                if (realTime)
                {
                    timer.setDelay(0);
                    timer.start();
                } else
                {
                    repaint();
                }
            }

            @Override
            public void mouseDragged(MouseEvent evt) //拖动鼠标时
            {
                /**通过鼠标获取坐标值*/
                int row = (evt.getY() - 10) / squareSize;
                int col = (evt.getX() - 10) / squareSize;
                if (row >= 0 && row < rows && col >= 0 && col < columns)
                {
                    if (realTime ? true : !found && !searching)
                    {
                        if (realTime)
                        {
                            searching = true;
                            fillGrid();
                        }
                        //如果位于迷宫的边缘，并且当前值是初始状态或最终状态
                        if ((row*columns+col != cur_row*columns+cur_col) && (cur_val == ROBOT || cur_val == TARGET))
                        {
                            /** new_val = 通过拖动鼠标获得的单元格（坐标）的值*/
                            int new_val = grid[row][col];
                            //如果值>0
                            if (new_val == EMPTY)
                            {
                                grid[row][col] = cur_val; //将旧值设置为新坐标（该值已从鼠标按下方法中获取）
                                if (cur_val == ROBOT) {  //如果该值是机器人即2的值...
                                    robotStart.row = row; //机器人启动是一个 Cell 类型的对象，我们在行变量中存储一个值
                                    robotStart.col = col;//机器人启动是一个 Cell 类型的对象，我们在 col 变量中存储一个值
                                } else
                                {
                                    targetPos.row = row;
                                    targetPos.col = col;
                                }
                                grid[cur_row][cur_col] = new_val; //将以前的坐标放在新的变量中
                                cur_row = row;
                                cur_col = col;
                                if (cur_val == ROBOT)
                                {
                                    robotStart.row = cur_row;
                                    robotStart.col = cur_col;
                                } else
                                {
                                    targetPos.row = cur_row;
                                    targetPos.col = cur_col;
                                }
                                cur_val = grid[row][col];
                            }
                        } else if (grid[row][col] != ROBOT && grid[row][col] != TARGET) //如果单元格既不是初始状态也不是最终状态
                        {
                            grid[row][col] = OBST;   //将单元格设置为障碍物
                        }
                    }
                }
                if (realTime)
                {
                    timer.setDelay(0);
                    timer.start();
                } else
                {
                    repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent evt) { }
            @Override
            public void mouseEntered(MouseEvent evt) { }
            @Override
            public void mouseExited(MouseEvent evt) { }
            @Override
            public void mouseMoved(MouseEvent evt) { }
            @Override
            public void mouseClicked(MouseEvent evt) { }

        }

        /**
         * 当用户按下按钮时，它会执行相应的功能
         */
        private class ActionHandler implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent evt) {
                String cmd = evt.getActionCommand();
                if (cmd.equals("Καθάρισμα")) {
                    fillGrid();
                    realTime = false;
                    realTimeButton.setEnabled(true);
                    realTimeButton.setForeground(Color.black);
                    stepButton.setEnabled(true);
                    animationButton.setEnabled(true);
                    slider.setEnabled(true);
                    dfs.setEnabled(true);
                    bfs.setEnabled(true);
                    aStar.setEnabled(true);
                    diagonal.setEnabled(true);
                    drawArrows.setEnabled(true);
                    drawNumbers.setEnabled(true);
                } else if (cmd.equals("实时") && !realTime) {
                    realTime = true;  //只有在这里实时是真的
                    searching = true;
                    realTimeButton.setForeground(Color.red);
                    stepButton.setEnabled(false);
                    animationButton.setEnabled(false);
                    slider.setEnabled(false);
                    dfs.setEnabled(false);
                    bfs.setEnabled(false);
                    aStar.setEnabled(false);
                    diagonal.setEnabled(false);
                    drawArrows.setEnabled(false);
                    drawNumbers.setEnabled(false);
                    timer.setDelay(0);
                    timer.start();
                } else if (cmd.equals("循序渐进") && !found && !endOfSearch) {
                    realTime = false;
                    searching = true;
                    message.setText(msgSelectStepByStepEtc);
                    realTimeButton.setEnabled(false);
                    dfs.setEnabled(false);
                    bfs.setEnabled(false);
                    aStar.setEnabled(false);
                    diagonal.setEnabled(false);
                    drawArrows.setEnabled(false);
                    drawNumbers.setEnabled(false);
                    timer.stop();
                    // 在这里，我们决定是否可以继续
                    // “分步”搜索与否
                    // 对于算法，DFS，BFS，A *
                    // 在这里，我们有他们的第二步：
                    // 2. 如果打开 = []，则完成。没有解决办法。
                    checkTermination();  //检查是否已找到解决方案
                    repaint();
                } else if (cmd.equals("运动") && !endOfSearch) {
                    realTime = false;
                    searching = true;
                    message.setText(msgSelectStepByStepEtc);
                    realTimeButton.setEnabled(false);
                    dfs.setEnabled(false);
                    bfs.setEnabled(false);
                    aStar.setEnabled(false);
                    diagonal.setEnabled(false);
                    drawArrows.setEnabled(false);
                    drawNumbers.setEnabled(false);
                    timer.setDelay(delay);
                    timer.start();
                } else if (cmd.equals("关于迷宫")) {
                    AboutBox aboutBox = new AboutBox(mazeFrame,true);
                    aboutBox.setVisible(true);
                }
            }
        } // 操作手柄辅助类的结束

        /**
         * 负责动画的类
         */
        private class RepaintAction implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent evt) {
                // 在这里，我们决定是否可以继续
                // 按“运动”搜索。
                // 对于算法 DFS、BFS 和 A* 的情况
                // 在这里，我们有他们的第二步：
                // 2. 如果打开 = []，则完成。没有解决办法。
                checkTermination();
                if (found) {
                    timer.stop();
                }
                if (!realTime) {
                    repaint();
                }
            }
        } // 辅助类重绘结束

        /**检查是否已找到解决方案  */
        public void checkTermination()
        {
            if (openSet.isEmpty() ) //如果列表中没有打开状态（即，如果它是空的）
            { //未找到解决方案
                endOfSearch = true;  //结束搜索
                grid[robotStart.row][robotStart.col]=ROBOT;
                message.setText(msgNoSolution);
                stepButton.setEnabled(false);
                animationButton.setEnabled(false);
                slider.setEnabled(false);
                repaint();
            } else //如果列表已满
            {
                expandNode();
                if (found) //检查是否找到了解决方案（如果找到...）
                {
                    endOfSearch = true; //结束搜索）
                    plotRoute();        //计算路线
                    stepButton.setEnabled(false);
                    animationButton.setEnabled(false);
                    slider.setEnabled(false);
                    repaint();
                }
            }
        }

        /**
         * 关于框创建的类
         */
        private class AboutBox extends JDialog{

            public AboutBox(Frame parent, boolean modal){
                super(parent, modal);
                // 关于框放置在屏幕的中心
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                double screenWidth = screenSize.getWidth();
                double ScreenHeight = screenSize.getHeight();
                int width = 350;
                int height = 190;
                int x = ((int)screenWidth-width)/2;
                int y = ((int)ScreenHeight-height)/2;
                setSize(width,height);
                setLocation(x, y);

                setResizable(false);
                setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

                JLabel title = new JLabel("Maze", JLabel.CENTER);
                title.setFont(new Font("Helvetica",Font.PLAIN,24));
                title.setForeground(new java.awt.Color(102, 153, 255));

                JLabel version = new JLabel("版本: 5.0", JLabel.CENTER);
                version.setFont(new Font("Helvetica",Font.BOLD,14));

                JLabel programmer = new JLabel("计算机科学与技术：吴晓菲", JLabel.CENTER);
                programmer.setFont(new Font("Helvetica",Font.PLAIN,16));

                JLabel programmer2 = new JLabel("指导老师：陈亮亮", JLabel.CENTER);
                programmer2.setFont(new Font("Helvetica",Font.PLAIN,16));

                JLabel lesson = new JLabel("人工智能实验：搜索策略算法", JLabel.CENTER);
                lesson.setFont(new Font("Helvetica",Font.BOLD,16));

                JLabel dummy = new JLabel("");

                add(title);
                add(version);
                add(programmer);
                add(programmer2);
                add(lesson);
                add(dummy);

                title.      setBounds(5,  0, 330, 30);
                version.    setBounds(5, 30, 330, 20);
                programmer. setBounds(5, 55, 330, 20);
                programmer2.setBounds(5, 80, 330, 20);
                lesson.     setBounds(5,105, 330, 20);
                dummy.      setBounds(5,155, 330, 20);
            }


        } // 关于箱子辅助类费

        /**
         * 创建一个随机的，完美的（没有圆圈）迷宫
         */
        private class MyMaze
        {
            /**迷宫（迷宫）的尺寸 */
            private int dimensionX, dimensionY;
            /**网格的尺寸 */
            private int gridDimensionX, gridDimensionY;
            /**外部网格（网格) */
            private char[][] mazeGrid;
            /**二维 （2d） 细胞基质（细胞） */
            private Cell[][] cells;
            /**随机对象 */
            private Random random = new Random();

            /**初始化时 x 和 y 相同 */
            public MyMaze(int aDimension) {
                // Αρχικοποίηση
                this(aDimension, aDimension);
            }
            /**Constructor */
            public MyMaze(int xDimension, int yDimension)
            {
                dimensionX = xDimension;
                dimensionY = yDimension;
                gridDimensionX = xDimension * 2 + 1;
                gridDimensionY = yDimension * 2 + 1;
                mazeGrid = new char[gridDimensionX][gridDimensionY];
                init();
                generateMaze();
            }

            private void init()
            {
                /**创建单元格 */
                cells = new Cell[dimensionX][dimensionY];
                for (int x = 0; x < dimensionX; x++) {
                    for (int y = 0; y < dimensionY; y++) {
                        /**创建单元格（参照 Cell constructor)  */
                        cells[x][y] = new Cell(x, y, false);
                    }
                }
            }

            /** 表示单元格的内部类 */
            private class Cell
            {
                /**坐标 */
                int x, y;
                /** 存储每个单元格中的数据的单元格类型列表*/
                ArrayList<Cell> neighbors = new ArrayList<>();
                /** 设置迷宫的墙不可改变*/
                boolean wall = true;
                /** 如果为真，它仍然可以继续使用*/
                boolean open = true;
                /** construct Cell  x, y */
                Cell(int x, int y)
                {
                    this(x, y, true);
                }
                Cell(int x, int y, boolean isWall)
                {
                    this.x = x;
                    this.y = y;
                    this.wall = isWall;
                }
                /**将旁边的元素添加到此像元，并将此像元添加为另一个像元的旁边*/
                void addNeighbor(Cell other) {
                    if (!this.neighbors.contains(other)) { // αποφυγή διπλότυπων στη λίστα μας (εαν δεν περιέχει την τιμή)
                        this.neighbors.add(other);          //βάλτο μες τη λίστα
                    }
                    if (!other.neighbors.contains(this)) { // αποφυγή διπλότυπων
                        other.neighbors.add(this);
                    }
                }
                /** 在方法中使用 updateGrid()
                 检查单元格是否低于相邻单元格
                 * 它检查邻居列表是否包含以下项目
                 */
                boolean isCellBelowNeighbor() {
                    return this.neighbors.contains(new Cell(this.x, this.y + 1));
                }
                /** 在方法中使用 updateGrid()
                 检查单元格是否与相邻单元格相邻（右侧）
                 * 也就是说，它检查相邻列表是否包含相邻项
                 */
                boolean isCellRightNeighbor() {
                    return this.neighbors.contains(new Cell(this.x + 1, this.y));
                }
                /* Cell*/
                @Override
                public boolean equals(Object other)
                {
                    if (!(other instanceof Cell)) return false;//如果项目不是 Cell
                    Cell otherCell = (Cell) other;
                    return (this.x == otherCell.x && this.y == otherCell.y); //并返回新坐标
                }

                //必须以等于
                @Override
                public int hashCode()
                {
                    // 代码的随机混合;该方法设计为独一无二，hash链表
                    return this.x + this.y * 256;
                }

            }
            /** 从左上角创建迷宫*/
            private void generateMaze()
            {
                generateMaze(0, 0);
            }
            /** 从坐标 x， y 创建迷宫*/
            private void generateMaze(int x, int y)
            {
                generateMaze(getCell(x, y)); // 创建 Cell
            }
            /**通过cell创建（随机）迷宫*/
            private void generateMaze(Cell startAt) //startAt 是一个类似单元格的对象，因此它将采用值（参数）x，y-
            {
                // 如果原始帖子是空缺的，不要从此处创建
                if (startAt == null) return;
                startAt.open = false; //然后它指示单元格已关闭以进行创建
                /**创建单元格列表*/
                ArrayList<Cell> cellsList = new ArrayList<>();
                cellsList.add(startAt);//将原始坐标放在列表中

                while (!cellsList.isEmpty()) //初始列表已满...
                {
                    /**用于未来值存储的单元格对象*/
                    Cell cell;
                    /*这是为了减少而不是完全消除数字
                     */

                    if (random.nextInt(10)==0) //如果要创建的随机数为0，则创建最多10个随机数
                        //从列表中删除位于随机位置的对象->该位置由列表的大小确定，并输入在单元格中删除的对象的值
                        cell = cellsList.remove(random.nextInt(cellsList.size()));
                    else cell = cellsList.remove(cellsList.size() - 1); //从列表中删除其最后一项并指定给单元格对象
                    //要收集的列表
                    ArrayList<Cell> neighbors = new ArrayList<>();
                    //可能相邻的细胞
                    Cell[] potentialNeighbors = new Cell[] //Cell（表格）类型的新表格
                            {
                                    getCell(cell.x + 1, cell.y), //向下（此表中的位置0
                                    getCell(cell.x, cell.y + 1), //右侧（此表中的位置1）
                                    getCell(cell.x - 1, cell.y), //左侧（此表中的位置2）
                                    getCell(cell.x, cell.y - 1) //顶部（此表中的位置3）
                            };
                    for (Cell other : potentialNeighbors) //把周围的区域（没有障碍物）放到一个新的列表中
                    {
                        //如果外面有墙或没有打开，请通过
                        if (other==null || other.wall || !other.open) continue;//离开这一部分，进入下一个元素
                        neighbors.add(other); //将可用单元添加到列表中，墙或封闭单元除外
                    }
                    if (neighbors.isEmpty()) continue; //离开放大镜的这一部分，去下一个。返回到while
                    // 从列表中选取一个随机单元格并指定给所选对象（随机对象可以是：上、下、右、左）
                    Cell selected = neighbors.get(random.nextInt(neighbors.size()));
                    //将选定的单元格添加为相邻单元格
                    selected.open = false; //然后宣布这个细胞关闭生产
                    cell.addNeighbor(selected);  //使用该方法将所选单元格添加到列表中（只需使用单元格对象调用该方法）
                    cellsList.add(cell); //将坐标（对象）放在列表中，第一次的坐标将是1个节点的原始交点
                    cellsList.add(selected);  //这些坐标是原始节点周围的随机相邻坐标
                }
                updateGrid();
            }
            /** 它是否用于从x坐标y中获取单元格（节点）？超出边界时返回null*/
            public Cell getCell(int x, int y) {
                try {
                    return cells[x][y];
                } catch (ArrayIndexOutOfBoundsException e) { // catch εκτός ορίου
                    return null;
                }
            }
            // 绘制迷宫
            public void updateGrid()
            {
                char backChar = ' ', wallChar = 'X', cellChar = ' ';
                //背景填充
                for (int x = 0; x < gridDimensionX; x ++)
                {
                    for (int y = 0; y < gridDimensionY; y ++)
                    {
                        mazeGrid[x][y] = backChar;
                    }
                }
                // 建造围墙
                for (int x = 0; x < gridDimensionX; x ++)
                {
                    for (int y = 0; y < gridDimensionY; y ++)
                    {
                        if (x % 2 == 0 || y % 2 == 0)
                            mazeGrid[x][y] = wallChar;
                    }
                }
                // 作出陈述
                for (int x = 0; x < dimensionX; x++)
                {
                    for (int y = 0; y < dimensionY; y++)
                    {
                        Cell current = getCell(x, y);
                        int gridX = x * 2 + 1, gridY = y * 2 + 1;
                        mazeGrid[gridX][gridY] = cellChar;
                        if (current.isCellBelowNeighbor())
                        {
                            mazeGrid[gridX][gridY + 1] = cellChar;
                        }
                        if (current.isCellRightNeighbor())
                        {
                            mazeGrid[gridX + 1][gridY] = cellChar;
                        }
                    }
                }

                //新建迷宫网格
                searching = false;
                endOfSearch = false;
                fillGrid();
                // …并将障碍物的位置复制到其中
                // 由迷宫构建算法创建
                for (int x = 0; x < gridDimensionX; x++)
                {
                    for (int y = 0; y < gridDimensionY; y++)
                    {
                        if (mazeGrid[x][y] == wallChar && grid[x][y] != ROBOT && grid[x][y] != TARGET)
                        {
                            grid[x][y] = OBST;
                        }
                    }
                }
            }
        } // MyMaze辅助类结束

        /**
         **********************************************************
         *      MazePanel类常量
         **********************************************************
         */

        /**κενό κελί */
        private final static int EMPTY    = 0;
        /**κελί με εμπόδιο */
        private final static int OBST     = 1;
        /**η θέση του ρομπότ(αρχική θέση) */
        private final static int  ROBOT    = 2;
        /**η θέση του στόχου (τελική θέση) */
        private final static int  TARGET   = 3;
        /**κελιά του μετώπου αναζήτησης (ΑΝΟΙΚΤΈΣ καταστάσεις)(μπλέ) */
        private final static int   FRONTIER = 4;
        /**(Σιάν)κελιά κλειστών καταστάσεων (οι περιοχές που έχουν εξερευνηθεί και έχουν μπει στην ΚΛΕΙΣΤΗ λίστα) */
        private final static int   CLOSED   = 5;
        /**κελιά που σχηματίζουν τη διαδρομή ρομπότ-στόχος (η τελική διαδρομή)(Υπολογίζεται από την τελευταία θέση(στόχος) και πρός τα πίσω βρίσκοντας την πιο σύντομη διαδρομή των κλειστών καταστάσεων) */
        private final static int   ROUTE    = 6;

        public static int num_DFS = -1;

        /** Μηνύματα προς τον χρήστη*/
        private final static String
                msgDrawAndSelect =
                "“绘制”障碍物并选择“逐步”或“移动”",
                msgSelectStepByStepEtc =
                        "选择“分步”或“移动”或“清除”",
                msgNoSolution =
                        "没有通往目标的路径 !!!";

        /**
         **********************************************************
         *          Μεταβλητές της κλάσης MazePanel
         **********************************************************
         */

        /** (βέλη) Spinners για την είσοδο του αριθμού των γραμμών και των στηλών*/
        JSpinner rowsSpinner, columnsSpinner;

        /** Ο αρχικός αριθμός των γραμμών του πλέγματος(grid)*/
        int rows    = 5;
        /** Ο αρχικός αριθμός των στηλών του πλέγματος(grid)*/
        int   columns = 5;
        /** Οι διαστάσεις του εκάστοτε κελιού(Cell) σε pixels, εξαρτάται πάντα από τον αριθμό των γραμμών*/
        int squareSize = 500/rows;
        /** Το μέγεθος της μύτης του βέλους που δείχνει το προκάτοχο κελί*/
        int arrowSize = squareSize/2;
        /** το σύνολο ανοικτών καταστάσεων */
        ArrayList<Cell> openSet   = new ArrayList();
        /**το σύνολο κλειστών καταστάσεων */
        ArrayList<Cell> closedSet = new ArrayList();
        /**η αρχική θέση του ρομπότ */
        Cell robotStart;
        /**η θέση του στόχου */
        Cell targetPos;
        /**μήνυμα προς τον χρήστη */
        JLabel message;
        /**Τα Βασικά κουμπιά */
        JButton resetButton, mazeButton, clearButton, realTimeButton, stepButton, animationButton;
        /**Η λίστα που θα μας χρειαστεί για να ζωγραφήσουμε τους αριθμούς μέσα στα κελιά
         Λίστα με τις συντεταγμένες*/
        ArrayList<Cell> array = new ArrayList<>();

        /**τα κουμπιά για την επιλογή του αλγόριθμου */
        JRadioButton dfs, bfs, aStar;

        /**ο slider για την ρύθμιση της ταχύτητας του animation */
        JSlider slider;
        /**Επιτρέπονται διαγώνιες κινήσεις; */
        JCheckBox diagonal;
        /**Σχεδίαση βελών προς προκατόχους */
        JCheckBox drawArrows;
        /**Σχεδίαση αριθμών*/
        JCheckBox drawNumbers;
        /** Το πλέγμα(grid)*/
        int[][] grid;
        /** Η λύση εμφανίζεται αμέσως*/
        boolean realTime;
        /** flag ότι βρέθηκε ο στόχος*/
        boolean found;
        /** flag ότι η αναζήτηση είναι σε εξέλιξη*/
        boolean searching;
        /** flag ότι η αναζήτηση έφθασε στο τέρμα*/
        boolean endOfSearch;
        /** ο χρόνος της καθυστέρησης σε msec του animation*/
        int delay;
        /** ο αριθμός των κόμβων που έχουν αναπτυχθεί*/
        int expanded;

        /** το αντικείμενο που ελέγχει το animation*/
        RepaintAction action = new RepaintAction();

        /** ο Timer που ρυθμίζει την ταχύτητα εκτέλεσης του animation*/
        Timer timer;

        /**
         * Ο δημιουργός του panel
         * @param width το πλάτος του panel.
         * @param height το ύψος panel.
         */
        public MazePanel(int width, int height)
        {

            setLayout(null);

            MouseHandler listener = new MouseHandler();
            addMouseListener(listener);
            addMouseMotionListener(listener);

            setBorder(BorderFactory.createMatteBorder(2,2,2,2,Color.blue));

            setPreferredSize( new Dimension(width,height) );

            grid = new int[rows][columns];

            // Δημιουργούμε τα περιεχόμενα του panel

            message = new JLabel(msgDrawAndSelect, JLabel.CENTER);
            message.setForeground(Color.blue);
            message.setFont(new Font("Helvetica",Font.PLAIN,16));

            JLabel rowsLbl = new JLabel("列数 （5-83):", JLabel.RIGHT);
            rowsLbl.setFont(new Font("Helvetica",Font.PLAIN,13));

            SpinnerModel rowModel = new SpinnerNumberModel(5, //αρχική τιμή
                    5,  //min
                    83, //max
                    1); //step
            rowsSpinner = new JSpinner(rowModel);

            JLabel columnsLbl = new JLabel("列数 （5-83):", JLabel.RIGHT);
            columnsLbl.setFont(new Font("Helvetica",Font.PLAIN,13));

            SpinnerModel colModel = new SpinnerNumberModel(5, //αρχική τιμή
                    5,  //min
                    83, //max
                    1); //step
            columnsSpinner = new JSpinner(colModel);

            resetButton = new JButton("新建网格");
            resetButton.addActionListener(new ActionHandler());
            resetButton.setBackground(Color.lightGray);
            resetButton.setToolTipText
                    ("根据给定的尺寸清理和重新设计网格");
            resetButton.addActionListener(this::resetButtonActionPerformed);

            mazeButton = new JButton("迷宫");
            mazeButton.addActionListener(new ActionHandler());
            mazeButton.setBackground(Color.lightGray);
            mazeButton.setToolTipText
                    ("创建随机迷宫");
            mazeButton.addActionListener(this::mazeButtonActionPerformed);

            clearButton = new JButton("清除");
            clearButton.addActionListener(new ActionHandler());
            clearButton.setBackground(Color.lightGray);
            clearButton.setToolTipText
                    ("第一次点击：搜索清理，第二次点击：清除障碍物");

            realTimeButton = new JButton("实时");
            realTimeButton.addActionListener(new ActionHandler());
            realTimeButton.setBackground(Color.lightGray);
            realTimeButton.setToolTipText
                    ("当搜索过程中，障碍物，机器人和目标的位置可能会发生变化");

            stepButton = new JButton("循序渐进");
            stepButton.addActionListener(new ActionHandler());
            stepButton.setBackground(Color.lightGray);
            stepButton.setToolTipText
                    ("每次点击都会逐步完成搜索");

            animationButton = new JButton("运动");
            animationButton.addActionListener(new ActionHandler());
            animationButton.setBackground(Color.lightGray);
            animationButton.setToolTipText
                    ("搜索自动完成");

            JLabel velocity = new JLabel("速度", JLabel.CENTER);
            velocity.setFont(new Font("Helvetica",Font.PLAIN,10));

            slider = new JSlider(0,1000,500); //αρχική τιμή καθυστέρησης 500 msec
            slider.setToolTipText
                    ("调整每个步骤中的延迟（0 到 1 秒）");

            delay = 1000-slider.getValue();
            slider.addChangeListener((ChangeEvent evt) -> {
                JSlider source = (JSlider)evt.getSource();
                if (!source.getValueIsAdjusting()) {
                    delay = 1000-source.getValue();
                }
            });

            // ButtonGroup που συγχρονίζει τα πέντε RadioButtons
            // που επιλέγουν τον αλγόριθμο, έτσι ώστε ένα μόνο από
            // αυτά να μπορεί να επιλεγεί ανά πάσα στιγμή
            ButtonGroup algoGroup = new ButtonGroup();

            dfs = new JRadioButton("DFS");
            dfs.setToolTipText("深度搜索算法");
            algoGroup.add(dfs);
            dfs.addActionListener(new ActionHandler());

            bfs = new JRadioButton("BFS");
            bfs.setToolTipText("宽度搜索算法");
            algoGroup.add(bfs);
            bfs.addActionListener(new ActionHandler());

            aStar = new JRadioButton("A*");
            aStar.setToolTipText("搜索算法 Α*");
            algoGroup.add(aStar);
            aStar.addActionListener(new ActionHandler());

            JPanel algoPanel = new JPanel();
            algoPanel.setBorder(javax.swing.BorderFactory.
                    createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(),
                            "算法", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                            javax.swing.border.TitledBorder.TOP, new java.awt.Font("Helvetica", 0, 14)));

            dfs.setSelected(true);  // Αρχικά ο DFS έχει επιλεγεί

            diagonal = new
                    JCheckBox("对角线运动");
            diagonal.setToolTipText("允许对角线移动");

            drawArrows = new
                    JCheckBox("指向前置任务的箭头");
            drawArrows.setToolTipText("绘制指向前置任务状态的箭头");

            drawNumbers = new
                    JCheckBox("平方编号");
            drawNumbers.setToolTipText("代理一次检查的正方形的顺序");

            JLabel robot = new JLabel("机器人", JLabel.CENTER);
            robot.setForeground(Color.green);
            robot.setFont(new Font("Helvetica",Font.PLAIN,14));

            JLabel target = new JLabel("目标", JLabel.CENTER);
            target.setForeground(Color.red);
            target.setFont(new Font("Helvetica",Font.PLAIN,14));

            JLabel frontier = new JLabel("前额", JLabel.CENTER);
            frontier.setForeground(Color.blue);
            frontier.setFont(new Font("Helvetica",Font.PLAIN,14));

            JLabel closed = new JLabel("闭", JLabel.CENTER);
            closed.setForeground(Color.CYAN);
            closed.setFont(new Font("Helvetica",Font.PLAIN,14));

            JButton aboutButton = new JButton("关于迷宫");
            aboutButton.addActionListener(new ActionHandler());
            aboutButton.setBackground(Color.lightGray);

            // προσθέτουμε τα περιεχόμενα στο panel
            add(message);
            add(rowsLbl);
            add(rowsSpinner);
            add(columnsLbl);
            add(columnsSpinner);
            add(resetButton);
            add(mazeButton);
            add(clearButton);
            add(realTimeButton);
            add(stepButton);
            add(animationButton);
            add(velocity);
            add(slider);
            add(dfs);
            add(bfs);
            add(aStar);
            add(algoPanel);
            add(diagonal);
            add(drawArrows);
            add(drawNumbers);
            add(robot);
            add(target);
            add(frontier);
            add(closed);
            add(aboutButton);

            // ρυθμίζουμε τα μεγέθη και τις θέσεις τους
            message.setBounds(0, 515, 500, 23);
            rowsLbl.setBounds(520, 5, 130, 25);
            rowsSpinner.setBounds(655, 5, 35, 25);
            columnsLbl.setBounds(520, 35, 130, 25);
            columnsSpinner.setBounds(655, 35, 35, 25);
            resetButton.setBounds(520, 65, 170, 25);
            mazeButton.setBounds(520, 95, 170, 25);
            clearButton.setBounds(520, 125, 170, 25);
            realTimeButton.setBounds(520, 155, 170, 25);
            stepButton.setBounds(520, 185, 170, 25);
            animationButton.setBounds(520, 215, 170, 25);
            velocity.setBounds(520, 245, 170, 10);
            slider.setBounds(520, 255, 170, 25);
            dfs.setBounds(530, 300, 70, 25);
            bfs.setBounds(600, 300, 70, 25);
            aStar.setBounds(530, 325, 70, 25);
            algoPanel.setLocation(520,280);
            algoPanel.setSize(170, 80);
            diagonal.setBounds(520, 365, 170, 25);
            drawArrows.setBounds(520, 390, 170, 25);
            drawNumbers.setBounds(520, 415, 170, 25);
            robot.setBounds(520, 450, 80, 25);
            target.setBounds(605, 450, 80, 25);
            frontier.setBounds(520, 470, 80, 25);
            closed.setBounds(605, 470, 80, 25);
            aboutButton.setBounds(520, 510, 170, 25);

            // δημιουργούμε τον timer
            timer = new Timer(delay, action);

            // δίνουμε στα κελιά του πλέγματος αρχικές τιμές
            // εδώ γίνεται και το πρώτο βήμα των αλγόριθμων
            fillGrid1();

        } // τέλος του MazePanel




        /**
         * Λειτουργία που εκτελείται αν ο χρήστης πιέσει το κουμπί "Νέο Πλέγμα"
         */
        private void resetButtonActionPerformed(java.awt.event.ActionEvent evt) {
            realTime = false;
            realTimeButton.setEnabled(true);
            realTimeButton.setForeground(Color.black);
            stepButton.setEnabled(true);
            animationButton.setEnabled(true);
            slider.setEnabled(true);
            initializeGrid(false);
        } // τέλος λειτουργίας resetButtonActionPerformed()

        /**
         * Λειτουργία που εκτελείται αν ο χρήστης πιέσει το κουμπί "Λαβύρινθος"
         */
        private void mazeButtonActionPerformed(java.awt.event.ActionEvent evt) {
            realTime = false;
            realTimeButton.setEnabled(true);
            realTimeButton.setForeground(Color.black);
            stepButton.setEnabled(true);
            animationButton.setEnabled(true);
            slider.setEnabled(true);
            initializeGrid(true);
        } // τελος λειτουργίας mazeButtonActionPerformed()

        /**
         * Δημιουργεί ένα νέο καθαρό πλέγμα ή ένα νέο λαβύρινθο
         */
        private void initializeGrid(Boolean makeMaze)
        {
            rows    = (int)(rowsSpinner.getValue()); //παίρνει την αρχική τιμη των γραμμών που έχει βάλει ο χρήστης στο textbox και την αποδιδει στη rows
            columns = (int)(columnsSpinner.getValue());
            squareSize = 500/(rows > columns ? rows : columns);
            arrowSize = squareSize/2;
            //Ο λαβύρινθος πρέπει να έχει έναν περιττό αριθμό γραμμών και στηλών (στην περίπτωση που θα πατήσουμε το κουμπί "Δημιουργια λαβυρινθου"
            if (makeMaze && rows % 2 == 0) {
                rows -= 1;
            }
            if (makeMaze && columns % 2 == 0) {
                columns -= 1;
            }
            grid = new int[rows][columns];
            robotStart = new Cell(rows - 2,0);        //η αρχική θέση του ρομπότ
            targetPos = new Cell(rows - 2,columns-2);
            dfs.setEnabled(true);
            dfs.setSelected(true);
            bfs.setEnabled(true);
            aStar.setEnabled(true);
            diagonal.setSelected(false);
            diagonal.setEnabled(true);
            drawArrows.setSelected(false);
            drawArrows.setEnabled(true);
            drawNumbers.setSelected(false);
            drawNumbers.setEnabled(true);
            slider.setValue(500);
            if (makeMaze) {
                MyMaze maze = new MyMaze(rows/2,columns/2); // εαν εχει παρει τιμή αληθείας δημιουργεί εναν τυχαίο λαβύρυνθο
            } else {
                fillGrid(); // αλλιως δημιουργεί ένα κενό πλέγμα (με άσπρα κελιά)
            }
        } // τέλος λειτουργίας initializeGrid()

        /**
         * Επεκτείνει ένα κόμβο και δημιουργεί τους διαδόχους του
         */
        private void expandNode(){            //<<<<<<<<<<<<<<<<<<<<<<--------------------------------------------πηγαίνει στον επόμενο κόμβο
            Cell current;
            if (dfs.isSelected() || bfs.isSelected()) {
                // Εδώ έχουμε το 3ο βήμα των αλγόριθμων DFS και BFS
                // 3. Αφαίρεσε την πρώτη κατάσταση Si, από τις ΑΝΟΙΚΤΕΣ .... (Η πρώτη(αρχική) κατάσταση θα είναι πάντα η θέση του ΡΟΜΠΟΤ)
                current = openSet.remove(0);      //<<<<<<<<<<<---------------------------------στοίβα
            } else //εαν ο Α* έχει επιλεγεί
            {
                // Εδώ έχουμε το 3ο βήμα του αλγορίθμου Α*
                // 3. Αφαίρεσε την κατάσταση Si, από την λίστα ΑΝΟΙΚΤΕΣ,
                //    για την οποία f(Si) <= f(Sj) για όλες τις άλλες
                //    ανοικτές καταστάσεις Sj ...
                // (ταξινομούμε πρώτα τη λίστα ΑΝΟΙΚΤΕΣ κατά αύξουσα σειρά ως προς f)
                Collections.sort(openSet, new CellComparatorByF());//ταξινομεί την openset με βάση το f(την απόσταση δηλ)
                current = openSet.remove(0);
            }
            //... και πρόσθεσέ την πρώτη κατάσταση στις ΚΛΕΙΣΤΕΣ.
            closedSet.add(0,current);

            array.add(current);// <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<--------------------ΤΗΝ ΧΡΕΙΑΖΟΜΑΙ ΓΙΑ ΝΑ ΖΩΓΡΑΦΙΣΩ ΤΑ ΝΟΥΜΕΡΑ

            // Ενημέρωσε το χρώμα του κελιού
            grid[current.row][current.col] = CLOSED;
            // Αν ο επιλεγμένος κόμβος είναι ο στόχος ...
            if (current.row == targetPos.row && current.col == targetPos.col)
            {
                // ... τότε τερμάτισε κλπ
                Cell last = targetPos;
                last.prev = current.prev;
                closedSet.add(last);  //βάζει τον τελικό στόχο μέσα στην κλειστή λίστα για μετέπειτα επεξεργασία
                found = true;
                return;
            }
            // Καταμετρούμε τους κόμβους που έχουμε αναπτύξει.
            expanded++;
            // Εδώ έχουμε το 4ο βήμα των αλγόριθμων
            // 4. Δημιούργησε τις διαδόχους της Si, με βάση τις ενέργειες που μπορούν
            //    να εφαρμοστούν στην Si.
            //    Η κάθε διάδοχος έχει ένα δείκτη προς την Si, ως την προκάτοχό της.
            //    Στην περίπτωση των αλγόριθμων DFS και BFS οι διάδοχοι δεν πρέπει
            //    να ανήκουν ούτε στις ΑΝΟΙΚΤΕΣ ούτε στις ΚΛΕΙΣΤΕΣ.
            ArrayList<Cell> succesors;
            //αποδίδει τα αποτελέσματα της createSuccesors στην λίστα succesors(Τα αποτελέσματα είναι μια λίστα με όνομα temp), τα οποία στοιχεία της τα βάζει με ανάποδη σειρα στους succesors
            succesors = createSuccesors(current);   //<<<<<<<<<<<<<<<<<<-----------------------------βρες τα γειτονικά κελια(δημιουργια διαδόχων)
            // Εδώ έχουμε το 5ο βήμα των αλγόριθμων
            // 5. Για κάθε διάδοχο της Si, ...
            succesors.stream().forEach((cell) -> {  //<<<<<--για κάθε γειτονικό κελί...
                // ... αν τρέχουμε τον DFS ...
                if (dfs.isSelected()) {
                    //  ... πρόσθεσε τον διάδοχο στην αρχή της λίστας ΑΝΟΙΚΤΕΣ
                    openSet.add(0, cell); //<<<<<<<<<----------------------------------------------------------------στοίβα (DFS)
                    // Ενημέρωσε το χρώμα του κελιού
                    grid[cell.row][cell.col] = FRONTIER;
                    //  ... αν τρέχουμε τον ΒFS ...
                } else if (bfs.isSelected()){
                    //  ... πρόσθεσε τον διάδοχο στο τέλος της λίστας ΑΝΟΙΚΤΕΣ
                    openSet.add(cell);  //<<<<<<<<<----------------------------------------------------------------ουρα   (BFS)
                    //  Ενημέρωσε το χρώμα του κελιού
                    grid[cell.row][cell.col] = FRONTIER;
                    //  ... αν τρέχουμε τον αλγόριθμο Α* (Βήμα 5 αλγόριθμου Α*) ...
                } else if (aStar.isSelected())
                {
                    // ... υπολόγισε την τιμή f(Sj)...
                    int dxg = current.col-cell.col;//διαφορά(απόσταση) της τρέχων θέσης από την γειτονική
                    int dyg = current.row-cell.row;
                    int dxh = targetPos.col-cell.col; //διαφορά(απόσταση) της τελικής θέσης από την γειτονική
                    int dyh = targetPos.row-cell.row;
                    if (diagonal.isSelected())
                    {

                        if(Math.abs(dxg) == 1 && Math.abs(dyg) == 1) //εαν η απόσταση των x και y είναι 1(δηλαδή όταν έχουμε διαγώνιο κελι...)
                        {
                            cell.g = current.g +(int)((double)2*Math.sqrt(dxg*dxg + dyg*dyg)); //το κόστος μετάβασης θα είναι 2
                            //cell.h = (int)((double)Math.sqrt(dxh*dxh + dyh*dyh)); //απόσταση  μεχρι το τελος
                        }
                        else //εαν η απόσταση χ ή η απόσταση y δεν είναι 1 (εαν δεν έχουμε δηλαδή διαγώνιο κελί)
                        {
                            cell.g = current.g + (Math.abs(dxg)+Math.abs(dyg)); //το κόστος μετάβασης θα είναι 1
                            //cell.h = Math.abs(dxh)+Math.abs(dyh);
                        }
                        cell.h = Math.abs(dxh)+Math.abs(dyh);


                        // Με διαγώνιες κινήσεις υπολογίζουμε
                        // το 2-πλάσιο των ευκλείδιων αποστάσεων

                        //Το g του γειτονικού κόμβου είναι το g του τρέχων + το g της διαγωνιου
                        //cell.g = current.g+(int)((double)2*Math.sqrt(dxg*dxg + dyg*dyg));

                        //η απόσταση του γειτονικού κελιού μέχρι τον τερματισμό(cell.h)
                        //cell.h = (int)((double)2*Math.sqrt(dxh*dxh + dyh*dyh));


                    } else   // Χωρίς διαγώνιες κινήσεις υπολογίζουμε
                    {
                        // τις αποστάσεις Manhattan
                        cell.g = current.g+Math.abs(dxg)+Math.abs(dyg);

                        //η απόσταση του γειτονικού κελιού μέχρι τον τερματισμό(cell.h)
                        cell.h = Math.abs(dxh)+Math.abs(dyh);
                    }
                    cell.f = cell.g+cell.h; //το χρησιμοποιούμε για να επιλέξουμε τον συντομότερο κόμβο κατά την αναζήτηση
                    // ... αν η Sj(το γειτονικό κελί) δεν ανήκει ούτε στις ΑΝΟΙΚΤΕΣ ούτε στις ΚΛΕΙΣΤΕΣ ...
                    int openIndex   = isInList(openSet,cell);
                    int closedIndex = isInList(closedSet,cell);
                    if (openIndex == -1 && closedIndex == -1)
                    {
                        // ... τότε πρόσθεσε την Sj στις ΑΝΟΙΚΤΕΣ ...
                        // ... με τιμή αξιολόγησης f(Sj)
                        openSet.add(cell);
                        // Ενημέρωσε το χρώμα του κελιού
                        grid[cell.row][cell.col] = FRONTIER;
                        // Αλλιώς ...
                    } else //Εαν το γειτονικό κελί υπάρχει η στις ανοιχτές η και στις κλειστές
                    {
                        // ... αν ανήκει στις ΑΝΟΙΚΤΕΣ, τότε ...
                        if (openIndex > -1){
                            // ... σύγκρινε την νέα τιμή αξιολόγισής της με την παλαιά.
                            // Αν παλαιά <= νέα ...
                            if (openSet.get(openIndex).f <= cell.f) {  //πρέπει να παίρνουμε πάντα την μικρότερη τιμή
                                // ... απόβαλε το νέο κόμβο με την κατάσταση Sj
                                // (δηλαδή μην κάνεις τίποτε για αυτόν τον κόμβο).
                                // Διαφορετικά, ...
                            } else {
                                // ... αφαίρεσε το στοιχείο (Sj,παλαιά) από τη λίστα
                                // στην οποία ανήκει ...
                                openSet.remove(openIndex);
                                // ... και πρόσθεσε το στοιχείο (Sj,νέα) στις ΑΝΟΙΚΤΕΣ
                                openSet.add(cell);
                                // Ενημέρωσε το χρώμα του κελιού
                                grid[cell.row][cell.col] = FRONTIER;
                            }
                            // ... αν ανήκε στις ΚΛΕΙΣΤΕΣ, τότε ...
                        } else {
                            // ... σύγκρινε την νέα τιμή αξιολόγισής της με την παλαιά.
                            // Αν παλαιά <= νέα ...
                            if (closedSet.get(closedIndex).f <= cell.f) {
                                // ... απόβαλε το νέο κόμβο με την κατάσταση Sj
                                // (δηλαδή μην κάνεις τίποτε για αυτόν τον κόμβο).
                                // Διαφορετικά, ...
                            } else {
                                // ... αφαίρεσε το στοιχείο (Sj,παλαιά) από τη λίστα
                                // στην οποία ανήκει ...
                                closedSet.remove(closedIndex);
                                // ... και πρόσθεσε το στοιχείο (Sj,νέα) στις ΑΝΟΙΚΤΕΣ
                                openSet.add(cell);
                                // Ενημέρωσε το χρώμα του κελιού
                                grid[cell.row][cell.col] = FRONTIER;
                            }
                        }
                    }
                }
            });

        } //Τέλος της λειτουργίας expandNode()

        /**
         * Δημιουργεί τους διαδόχους μιας κατάστασης/κελιού
         *
         * @param current το κελί του οποίου ζητούμε τους διαδόχους
         * @return οι διάδοχοι του κελιού με μορφή λίστας
         */
        private ArrayList<Cell> createSuccesors(Cell current){ //<<<<<<<<<<<<<<-----------------------βρες τα γειττονικα κελια
            int r = current.row;
            int c = current.col;
            // Δημιουργούμε μια κενή λίστα για τους διαδόχους του τρέχοντος κελιού.
            ArrayList<Cell> temp = new ArrayList<>();
            // Με διαγώνιες κινήσεις η προτεραιότητα είναι:
            // 1:Πάνω 2:Πάνω-δεξιά 3:Δεξιά 4:Κάτω-δεξιά
            // 5:Κάτω 6:Κάτω-αριστερά 7:Αριστερά 8:Πάνω-αριστερά

            // Χωρίς διαγώνιες κινήσεις η προτεραιότητα είναι:
            // 1:Πάνω 2:Δεξιά 3:Κάτω 4:Αριστερά

            // Αν δεν βρισκόμαστε στο πάνω όριο του πλέγματος (εαν δεν βρισκόμαστε δηλ στην γραμμή 0)
            // και το πάνω κελί δεν είναι εμπόδιο ...
            if (r > 0 && grid[r-1][c] != OBST &&
                    // ... και (στην περίπτωση μόνο που δεν εκτελούμε τον Α* )
                    // δεν ανήκει ήδη ούτε στις ΑΝΟΙΚΤΕΣ ούτε στις ΚΛΕΙΣΤΕΣ ...
                    ((aStar.isSelected()) ? true :
                            isInList(openSet,new Cell(r-1,c)) == -1 && //εαν το κελί δεν ανήκει στην ανοιχτη λίστα
                                    isInList(closedSet,new Cell(r-1,c)) == -1)) { //και εαν το κελί δεν ανήκει στην κλειστή λίστα
                Cell cell = new Cell(r-1,c);  //δημιούργησε ένα κελί (cell)  και πέρνα του τις συντεταγμενες(του πάνω κόμβου)

                // ... ενημέρωσε τον δείκτη του πάνω κελιού να δείχνει το τρέχον ...
                cell.prev = current; //στο αντικειμενο(μεταβλητη) prev αποθηκευουμε την τρέχων κατάσταση <<<<<<<<<<<<<<<<<<<<-------------αποθηκεύουμε το τρέχον κελί στη μεταβλητή prev
                // ... και πρόσθεσε το πάνω κελί στους διαδόχους του τρέχοντος(δηλαδή το πάνω κελί θα είναι διάδοχος του τρέχων κελιου).
                temp.add(cell); //<<<<<<<<<<<<<<<<<<<<<<<<----------------------------------------Αποθηκεύουμε το πάνω κελι

            }
            if (diagonal.isSelected())
            {
                // Αν δεν βρισκόμαστε ούτε στο πάνω ούτε στο δεξιό όριο του πλέγματος
                // και το πάνω-δεξί κελί δεν είναι εμπόδιο ...
                if (r > 0 && c < columns-1 && grid[r-1][c+1] != OBST &&
                        // ... και ένα από τα πάνω ή δεξιό κελιά δεν είναι εμπόδια ...
                        // (επειδή δεν είναι λογικό να επιτρέψουμε να περάσει
                        //  το ρομπότ από μία σχισμή)
                        (grid[r-1][c] != OBST || grid[r][c+1] != OBST) &&
                        // ... και (στην περίπτωση μόνο που δεν εκτελούμε τον Α*)
                        // δεν ανήκει ήδη ούτε στις ΑΝΟΙΚΤΕΣ ούτε στις ΚΛΕΙΣΤΕΣ ...
                        ((aStar.isSelected()) ? true :
                                isInList(openSet,new Cell(r-1,c+1)) == -1 &&
                                        isInList(closedSet,new Cell(r-1,c+1)) == -1)) {
                    Cell cell = new Cell(r-1,c+1);

                    // ... ενημέρωσε τον δείκτη του πάνω-δεξιού κελιού να δείχνει το τρέχον ...
                    cell.prev = current;
                    // ... και πρόσθεσε το πάνω-δεξί κελί στους διαδόχους του τρέχοντος.
                    temp.add(cell);

                }
            }
            // Αν δεν βρισκόμαστε στο δεξί όριο του πλέγματος
            // και το δεξί κελί δεν είναι εμπόδιο ...
            if (c < columns-1 && grid[r][c+1] != OBST &&
                    // ... και (στην περίπτωση μόνο που δεν εκτελούμε τον Α* )
                    // δεν ανήκει ήδη ούτε στις ΑΝΟΙΚΤΕΣ ούτε στις ΚΛΕΙΣΤΕΣ ...
                    ((aStar.isSelected())? true :
                            isInList(openSet,new Cell(r,c+1)) == -1 &&
                                    isInList(closedSet,new Cell(r,c+1)) == -1)) {
                Cell cell = new Cell(r,c+1);

                // ... ενημέρωσε τον δείκτη του δεξιού κελιού να δείχνει το τρέχον ...
                cell.prev = current;
                // ... και πρόσθεσε το δεξί κελί στους διαδόχους του τρέχοντος.
                temp.add(cell);

            }
            if (diagonal.isSelected())
            {
                // Αν δεν βρισκόμαστε ούτε στο κάτω ούτε στο δεξιό όριο του πλέγματος
                // και το κάτω-δεξί κελί δεν είναι εμπόδιο ...
                if (r < rows-1 && c < columns-1 && grid[r+1][c+1] != OBST &&
                        // ... και ένα από τα κάτω ή δεξιό κελιά δεν είναι εμπόδια ...
                        (grid[r+1][c] != OBST || grid[r][c+1] != OBST) &&
                        // ... και (στην περίπτωση μόνο που δεν εκτελούμε τον Α*)
                        // δεν ανήκει ήδη ούτε στις ΑΝΟΙΚΤΕΣ ούτε στις ΚΛΕΙΣΤΕΣ ...
                        ((aStar.isSelected()) ? true :
                                isInList(openSet,new Cell(r+1,c+1)) == -1 &&
                                        isInList(closedSet,new Cell(r+1,c+1)) == -1)) {
                    Cell cell = new Cell(r+1,c+1);

                    // ... ενημέρωσε τον δείκτη του κάτω-δεξιού κελιού να δείχνει το τρέχον ...
                    cell.prev = current;
                    // ... και πρόσθεσε το κάτω-δεξί κελί στους διαδόχους του τρέχοντος.
                    temp.add(cell);

                }
            }
            // Αν δεν βρισκόμαστε στο κάτω όριο του πλέγματος
            // και το κάτω κελί δεν είναι εμπόδιο ...
            if (r < rows-1 && grid[r+1][c] != OBST &&
                    // ... και (στην περίπτωση μόνο που δεν εκτελούμε τον Α*)
                    // δεν ανήκει ήδη ούτε στις ΑΝΟΙΚΤΕΣ ούτε στις ΚΛΕΙΣΤΕΣ ...
                    ((aStar.isSelected()) ? true :
                            isInList(openSet,new Cell(r+1,c)) == -1 &&
                                    isInList(closedSet,new Cell(r+1,c)) == -1)) {
                Cell cell = new Cell(r+1,c);

                // ... ενημέρωσε τον δείκτη του κάτω κελιού να δείχνει το τρέχον ...
                cell.prev = current;
                // ... και πρόσθεσε το κάτω κελί στους διαδόχους του τρέχοντος.
                temp.add(cell);

            }
            if (diagonal.isSelected())
            {
                // Αν δεν βρισκόμαστε ούτε στο κάτω ούτε στο αριστερό όριο του πλέγματος
                // και το κάτω-αριστερό κελί δεν είναι εμπόδιο ...
                if (r < rows-1 && c > 0 && grid[r+1][c-1] != OBST &&
                        // ... και ένα από τα κάτω ή αριστερό κελιά δεν είναι εμπόδια ...
                        (grid[r+1][c] != OBST || grid[r][c-1] != OBST) &&
                        // ... και (στην περίπτωση μόνο που δεν εκτελούμε τον Α* )
                        // δεν ανήκει ήδη ούτε στις ΑΝΟΙΚΤΕΣ ούτε στις ΚΛΕΙΣΤΕΣ ...
                        ((aStar.isSelected()) ? true :
                                isInList(openSet,new Cell(r+1,c-1)) == -1 &&
                                        isInList(closedSet,new Cell(r+1,c-1)) == -1)) {
                    Cell cell = new Cell(r+1,c-1);

                    // ... ενημέρωσε τον δείκτη του κάτω-αριστερού κελιού να δείχνει το τρέχον ...
                    cell.prev = current;
                    // ... και πρόσθεσε το κάτω-αριστερό κελί στους διαδόχους του τρέχοντος.
                    temp.add(cell);

                }
            }
            // Αν δεν βρισκόμαστε στο αριστερό όριο του πλέγματος
            // και το αριστερό κελί δεν είναι εμπόδιο ...
            if (c > 0 && grid[r][c-1] != OBST &&
                    // ... και (στην περίπτωση μόνο που δεν εκτελούμε τον Α* )
                    // δεν ανήκει ήδη ούτε στις ΑΝΟΙΚΤΕΣ ούτε στις ΚΛΕΙΣΤΕΣ ...
                    ((aStar.isSelected()) ? true :
                            isInList(openSet,new Cell(r,c-1)) == -1 &&
                                    isInList(closedSet,new Cell(r,c-1)) == -1)) {
                Cell cell = new Cell(r,c-1);

                // ... ενημέρωσε τον δείκτη του αριστερού κελιού να δείχνει το τρέχον ...
                cell.prev = current;
                // ... και πρόσθεσε το αριστερό κελί στους διαδόχους του τρέχοντος.
                temp.add(cell);

            }
            if (diagonal.isSelected())
            {
                // Αν δεν βρισκόμαστε ούτε στο πάνω ούτε στο αριστερό όριο του πλέγματος
                // και το πάνω-αριστερό κελί δεν είναι εμπόδιο ...
                if (r > 0 && c > 0 && grid[r-1][c-1] != OBST &&
                        // ... και ένα από τα πάνω ή αριστερό κελιά δεν είναι εμπόδια ...
                        (grid[r-1][c] != OBST || grid[r][c-1] != OBST) &&
                        // ... και (στην περίπτωση μόνο που δεν εκτελούμε τον Α*)
                        // δεν ανήκει ήδη ούτε στις ΑΝΟΙΚΤΕΣ ούτε στις ΚΛΕΙΣΤΕΣ ...
                        ((aStar.isSelected()) ? true :
                                isInList(openSet,new Cell(r-1,c-1)) == -1 &&
                                        isInList(closedSet,new Cell(r-1,c-1)) == -1)) {
                    Cell cell = new Cell(r-1,c-1);

                    // ... ενημέρωσε τον δείκτη του πάνω-αριστερού κελιού να δείχνει το τρέχον ...
                    cell.prev = current;
                    // ... και πρόσθεσε το πάνω-αριστερό κελί στους διαδόχους του τρέχοντος.
                    temp.add(cell);

                }
            }
            // Επειδή στον αλγόριθμο DFS τα κελιά προστίθενται ένα-ένα στην
            // αρχή της λίστας ΑΝΟΙΚΤΕΣ, αντιστρέφουμε την σειρά των διαδόχων
            // που σχηματίστηκε, ώστε ο διάδοχος που αντιστοιχεί στην υψηλότερη
            // προτεραιότητα, να βρεθεί πρώτος στην λίστα.
            // Για τον, A*  δεν υπάρχει ζήτημα, γιατί η λίστα
            // ταξινομείται ως προς f ή dist πριν την εξαγωγή του πρώτου στοιχείου της.

            /*if (dfs.isSelected()){
                Collections.reverse(temp);
            }*/
            return temp;
        } // Τέλος της λειτουργίας createSuccesors()

        /**
         * Επιστρέφει τον δείκτη του κελιού current στη λίστα list
         *
         * @param list η λίστα μέσα στην οποία αναζητάμε
         * @param current το κελί που αναζητάμε
         * @return ο δείκτης το κελιού μέσα στη λίστα
         * αν το κελί δεν βρεθεί επιστρέφει -1
         */
        private int isInList(ArrayList<Cell> list, Cell current){
            int index = -1;
            for (int i = 0 ; i < list.size(); i++) {
                if (current.row == list.get(i).row && current.col == list.get(i).col) {
                    index = i;
                    break;
                }
            }
            return index;
        } // Τέλος της λειτουργίας isInList()

        /**
         * Επιστρέφει το προκάτοχο κελί του κελιού current της λίστας list
         *
         * @param list η λίστα μέσα στην οποία αναζητάμε
         * @param current το κελί που αναζητάμε
         * @return το κελί που αντιστοιχεί στον προκάτοχο του current
         */
        private Cell findPrev(ArrayList<Cell> list, Cell current){
            int index = isInList(list, current);
            return list.get(index).prev;
        } // Τέλος της λειτουργίας findPrev()


        /**
         * Υπολογίζει την διαδρομή από τον στόχο προς την αρχική θέση
         * του ρομπότ και μετρά τα αντίστοιχα βήματα
         * και την απόσταση που διανύθηκε.
         */
        private void plotRoute(){ //υπολογίζει από το τέλος
            searching = false;
            endOfSearch = true;
            int steps = 0;
            double distance = 0;
            int index = isInList(closedSet,targetPos); //ψάξε μέσα στην κλειστη λίστα για να δεις εαν υπάρχει ο τελικός προορισμός(targetPos)
            Cell cur = closedSet.get(index); //πάρε το targetPos(τις συντεταγμένες του τελικού προορισμού)
            grid[cur.row][cur.col]= TARGET;
            do {
                steps++;
                if (diagonal.isSelected())
                {
                    int dx = cur.col-cur.prev.col; //διαφορά τελικού στόχου απο τον προηγούμενο
                    int dy = cur.row-cur.prev.row;
                    //distance += Math.sqrt(dx*dx + dy*dy); //εαν κάνει διαγώνια κίνηση να υπολογίσεις την υποτείνουσα(στο δικό μας παράδειγμα θέλουμε σταθερο αριθμο το 2)
                    if(Math.abs(dx) == 1 && Math.abs(dy) == 1) //εαν η απόσταση των x και y είναι 1(δηλαδή όταν έχουμε διαγώνιο κελι...)
                    {
                        //distance += 2; //το κόστος μετάβασης θα είναι 2
                        distance += (int)((double)2*Math.sqrt(dx*dx + dy*dy));
                    }
                    else //εαν η απόσταση χ ή η απόσταση y δεν είναι 1 (εαν δεν έχουμε δηλαδή διαγώνιο κελί)
                    {
                        distance+= (Math.abs(dx)+Math.abs(dy)); //το κόστος μετάβασης θα είναι 1
                    }
                } else //εαν δεν θέλουμε διαγώνιο κίνηση
                {
                    distance++;  //η μετακινηση για το κάθε κελι θα είναι πάντα +1
                }
                cur = cur.prev;  //στο αντικείμενο prev έχουν αποθηκευτεί ΟΛΕΣ οι συντεταγμένες των προηγουμενων κόμβων(ΟΧΙ ομως αυτες που καταλήγαν σε αδιέξοδο)
                grid[cur.row][cur.col] = ROUTE;


            } while (!(cur.row == robotStart.row && cur.col == robotStart.col));
            grid[robotStart.row][robotStart.col]=ROBOT;
            String msg;
            msg = String.format("探索的节点: %d, 步骤: %d, 过渡成本: %.2f",
                    expanded,steps,distance);
            message.setText(msg);

        } // Τέλος της λειτουργίας plotRoute()

        /**
         * Δίνει αρχικές τιμές στα κελιά του πλέγματος
         * Με το πρώτο κλικ στο κουμπί 'Καθάρισμα' μηδενίζει τα στοιχεία
         * της τυχόν αναζήτησης που είχε εκτελεστεί (Μέτωπο, Κλειστές, Διαδρομή)
         * και αφήνει ανέπαφα τα εμπόδια και τις θέσεις ρομπότ και στόχου
         * προκειμένου να είναι δυνατή η εκτέλεση άλλου αλγόριθμου
         * με τα ίδια δεδομένα.
         * Με το δεύτερο κλικ αφαιρεί και τα εμπόδια.
         */
        private void fillGrid() {
            if (searching || endOfSearch) //όταν η αναζήτηση είναι ακόμα σε εξέληξη η έχουμε φτάσει στο τέλος με(με τα κελιά χρωματιστά)
            {
                for (int r = 0; r < rows; r++)
                {
                    for (int c = 0; c < columns; c++) {
                        if (grid[r][c] == FRONTIER || grid[r][c] == CLOSED || grid[r][c] == ROUTE) {
                            grid[r][c] = EMPTY;
                        }
                        if (grid[r][c] == ROBOT){ //εαν οι συντεταγμενες του κελιού ειναι αυτες του ΡΟΜΠΟΤ
                            robotStart = new Cell(r,c); //δημιούργησε εναν καινούριο Constructor με αυτες τις συντεταγμένες και βάλτες στο robotstart
                        }
                        if (grid[r][c] == TARGET){
                            targetPos = new Cell(r,c);
                        }
                    }
                }

                searching = false;
            } else //όταν δεν υπάρχει αναζήτηση
            {
                for (int r = 0; r < rows; r++)
                {
                    for (int c = 0; c < columns; c++)
                    {
                        grid[r][c] = EMPTY;

                        if (grid[r][c] == ROBOT){
                            robotStart = new Cell(r,c);
                        }
                        if (grid[r][c] == TARGET){
                            targetPos = new Cell(r,c);
                        }
                    }
                }


            }
            if (aStar.isSelected())
            {
                robotStart.g = 0;  //αρχικοποιούμε τις μεταβλητές
                robotStart.h = 0;
                robotStart.f = 0;
            }
            expanded = 0;
            found = false;
            searching = false;
            endOfSearch = false;

            // Το πρώτο βήμα των αλγόριθμων BFS και DFS γίνεται εδώ
            // 1. ΑΝΟΙΚΤΕΣ:= [So], ΚΛΕΙΣΤΕΣ:= []
            openSet.removeAll(openSet);
            openSet.add(robotStart); //βάλε στις ανοιχτες καταστάσεις την θέση του ΡΟΜΠΟΤ
            closedSet.removeAll(closedSet);
            array.removeAll(array);

            grid[targetPos.row][targetPos.col] = TARGET;
            grid[robotStart.row][robotStart.col] = ROBOT;
            message.setText(msgDrawAndSelect);
            timer.stop();
            repaint();

        } // Τέλος της λειτουργίας fillGrid()

        /**Extra μεθοδος - με το που ξεκινάει το πρόγραμμα να ανοίγει ο λαβύρινθος που έχουμε κατασκεύασει εμείς για τις ανάγκες της εργασίας */
        private void fillGrid1() {
            if (searching || endOfSearch) //όταν η αναζήτηση είναι ακόμα σε εξέληξη η έχουμε φτάσει στο τέλος με(με τα κελιά χρωματιστά)
            {
                for (int r = 0; r < rows; r++)
                {
                    for (int c = 0; c < columns; c++) {
                        if (grid[r][c] == FRONTIER || grid[r][c] == CLOSED || grid[r][c] == ROUTE) {
                            grid[r][c] = EMPTY;
                        }
                        if (grid[r][c] == ROBOT){
                            robotStart = new Cell(r,c);
                        }
                        if (grid[r][c] == TARGET){
                            targetPos = new Cell(r,c);
                        }
                    }
                }

                searching = false;
            } else //όταν δεν υπάρχει αναζήτηση
            {
                for (int r = 0; r < rows; r++)
                {
                    for (int c = 0; c < columns; c++)
                    {
                        grid[r][c] = EMPTY;
                    }
                }
                robotStart = new Cell(rows - 2,0);   //η αρχική θέση του ρομπότ
                targetPos = new Cell(rows - 2,columns-2);   //η αρχική θέση του προορισμού

                //δημιουργούμε μαύρα κελιά(εμπόδια) σύμφωνα με την εργασία του Παναγιωτόπουλου
                grid[0][columns -2] = OBST;
                grid[rows - 1][1] = OBST;
                grid[rows - 1][2] = OBST;
                grid[2][1] = OBST;
                grid[2][2] = OBST;
                grid[3][2] = OBST;

            }
            if (aStar.isSelected()){
                robotStart.g = 0;  //αρχικοποιύμε τις μεταβλητές
                robotStart.h = 0;
                robotStart.f = 0;
            }
            expanded = 0;
            found = false;
            searching = false;
            endOfSearch = false;

            // Το πρώτο βήμα των υπόλοιπων αλγόριθμων γίνεται εδώ
            // 1. ΑΝΟΙΚΤΕΣ:= [So], ΚΛΕΙΣΤΕΣ:= []
            openSet.removeAll(openSet);
            openSet.add(robotStart);
            closedSet.removeAll(closedSet);
            array.removeAll(array);

            grid[targetPos.row][targetPos.col] = TARGET;
            grid[robotStart.row][robotStart.col] = ROBOT;
            message.setText(msgDrawAndSelect);
            timer.stop();
            repaint();

        } // Τέλος της λειτουργίας fillGrid()


        /**
         * Ζωγραφίζει το πλέγμα
         */
        @Override
        public void paintComponent(Graphics g)
        {

            super.paintComponent(g);  // Γεμίζει το background χρώμα.
            num_DFS = 0;

            g.setColor(Color.DARK_GRAY); //το χρωματικό πλαίσιο των ορθογωνίων
            g.fillRect(10, 10, columns*squareSize+1, rows*squareSize+1);

            for (int r = 0; r < rows; r++)
            {
                for (int c = 0; c < columns; c++)
                {
                    switch (grid[r][c]) {
                        case EMPTY:
                            g.setColor(Color.WHITE);
                            break;
                        case ROBOT:
                            g.setColor(Color.GREEN);
                            break;
                        case TARGET:
                            g.setColor(Color.RED);
                            break;
                        case OBST:
                            g.setColor(Color.BLACK);
                            break;
                        case FRONTIER:
                            g.setColor(Color.BLUE);
                            break;
                        case CLOSED:
                            g.setColor(Color.CYAN);
                            break;
                        case ROUTE:
                            g.setColor(Color.YELLOW);
                            break;
                        default:
                            break;
                    }
                    g.fillRect(11 + c*squareSize, 11 + r*squareSize, squareSize - 1, squareSize - 1); //Γέμισμα των ορθογωνίων με χρώμα (αποσταση απο το χ, αποσταση απο ψ, μηκος πλατος τετραγωνου)
                }
            }

            if (drawArrows.isSelected())
            {
                // Ζωγραφίζουμε όλα τα βέλη από κάθε ανοικτή ή κλειστή κατάσταση
                // προς την προκάτοχό της.
                for (int r = 0; r < rows; r++) {
                    for (int c = 0; c < columns; c++) {
                        // Αν το τρέχον κελί είναι ο στόχος και έχει βρεθεί λύση
                        // ή είναι κελί της διαδρομής προς στο στόχο
                        // ή είναι ανοικτή κατάσταση,
                        // ή κλειστή αλλά όχι η αρχική θέση του ρομπότ
                        if ((grid[r][c] == TARGET && found)  || grid[r][c] == ROUTE  ||
                                grid[r][c] == FRONTIER || (grid[r][c] == CLOSED &&
                                !(r == robotStart.row && c == robotStart.col))){
                            // Η ουρά του βέλους είναι το τρέχον κελί, ενώ
                            // η κορυφή του βέλους είναι το προκάτοχο κελί.
                            Cell head;
                            if (grid[r][c] == FRONTIER)
                            {
                                head = findPrev(openSet,new Cell(r,c));

                            } else
                            {
                                head = findPrev(closedSet,new Cell(r,c));
                            }
                            // Οι συντεταγμένες του κέντρου του τρέχοντος κελιού
                            int tailX = 11+c*squareSize+squareSize/2;
                            int tailY = 11+r*squareSize+squareSize/2;
                            // Οι συντεταγμένες του κέντρου του προκάτοχου κελιού
                            int headX = 11+head.col*squareSize+squareSize/2;
                            int headY = 11+head.row*squareSize+squareSize/2;
                            // Αν το τρέχον κελί είναι ο στόχος
                            // ή είναι κελί της διαδρομής προς το στόχο ...
                            if (grid[r][c] == TARGET  || grid[r][c] == ROUTE){
                                // ... σχεδίασε ένα μπλέ βέλος προς την κατεύθυνση του στόχου.
                                g.setColor(Color.BLUE);
                                drawArrow(g,tailX,tailY,headX,headY);
                                // Αλλιώς ...
                            } else {
                                // ... σχεδίασε ένα μαύρο βέλος προς το προκάτοχο κελί.
                                g.setColor(Color.BLACK);
                                drawArrow(g,headX,headY,tailX,tailY);
                            }
                        }
                    }
                }
            }

            if(drawNumbers.isSelected())
            {
                num_DFS = -1;
                for(Cell index : array)
                {
                    num_DFS++;
                    g.setColor(Color.blue);
                    int fontSize = 1* squareSize/4;
                    g.setFont(new Font("Arial", Font.PLAIN, fontSize));
                    g.drawString(String.valueOf(num_DFS), 11 + index.col*squareSize+squareSize/2, 11 + index.row*squareSize+squareSize/2);
                }
            }

        } // Τέλος της λειτουργίας paintComponent()

        /**
         * Ζωγραφίζει ένα βέλος από το σημείο (x2,y2) προς το σημείο (x1,y1)
         */
        private void drawArrow(Graphics g1, int x1, int y1, int x2, int y2) {
            Graphics2D g = (Graphics2D) g1.create();

            double dx = x2 - x1, dy = y2 - y1;
            double angle = Math.atan2(dy, dx);
            int len = (int) Math.sqrt(dx*dx + dy*dy);
            AffineTransform at = AffineTransform.getTranslateInstance(x1, y1);
            at.concatenate(AffineTransform.getRotateInstance(angle));
            g.transform(at);

            // Εμείς ζωγραφίζουμε ένα οριζόντιο βέλος μήκους len
            // που καταλήγει στο σημείο (0,0) με τις δύο αιχμές μήκους arrowSize
            // να σχηματίζουν γωνίες 20 μοιρών με τον άξονα του βέλους ...
            g.drawLine(0, 0, len, 0);
            g.drawLine(0, 0, (int)(arrowSize*Math.sin(70*Math.PI/180)) , (int)(arrowSize*Math.cos(70*Math.PI/180)));
            g.drawLine(0, 0, (int)(arrowSize*Math.sin(70*Math.PI/180)) , -(int)(arrowSize*Math.cos(70*Math.PI/180)));
            // ... και η κλάση AffineTransform αναλαμβάνει τα υπόλοιπα !!!!!!
            // Πώς να μην θαυμάσει κανείς αυτήν την Java !!!!
        } // Τέλος της λειτουργίας drawArrow()

    } // Τέλος της εμφωλευμένης κλάσης MazePanel

} // Τέλος της κλάσης Maze