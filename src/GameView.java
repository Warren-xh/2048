import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class GameView{
    private static final int jframeWidth = 405;//窗口宽高
    private static final int jframeHeight = 530;
    private static int score = 0;

    Font topicFont = new Font("微软雅黑", Font.BOLD, 50);//主题字体
    Font scoreFont = new Font("微软雅黑", Font.BOLD, 28);//得分字体
    Font explainFont = new Font("宋体", Font.PLAIN,20);//提示字体

    private JFrame jframeMain;
    private JLabel jlblTitle;
    private JLabel jlblScoreName;
    private JLabel jlblScore;
    private JLabel jlblTip;
    private GameBoard gameBoard;

    public GameView() {
        init();
    }

    public void init() {
        //1、创建窗口
        jframeMain = new JFrame("HipHop 2048");
        jframeMain.setSize(jframeWidth, jframeHeight);
        jframeMain.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframeMain.setLocationRelativeTo(null);//窗口显示位置居中
        jframeMain.setResizable(false);
        jframeMain.setLayout(null);//设置绝对布局，以便后面可以用setBounds设置位置

        jlblTitle = new JLabel("2048", JLabel.CENTER);
        jlblTitle.setFont(topicFont);
        jlblTitle.setForeground(Color.BLACK);
        jlblTitle.setBounds(50, 0, 150, 60);
        jframeMain.add(jlblTitle);


        //2、框架窗口搭建好，则需向里面开始添加内容
        //设置字体及其颜色、位置
        jlblScoreName = new JLabel("Point", JLabel.CENTER);
        jlblScoreName.setFont(scoreFont);
        jlblScoreName.setForeground(Color.WHITE);
        jlblScoreName.setOpaque(true);
        jlblScoreName.setBackground(Color.GRAY);
        jlblScoreName.setBounds(250, 0, 120, 30);
        jframeMain.add(jlblScoreName);

        //3、得分区(得分名+分数)
        jlblScore = new JLabel("0", JLabel.CENTER);
        jlblScore.setFont(scoreFont);
        jlblScore.setForeground(Color.WHITE);
        jlblScore.setOpaque(true);
        jlblScore.setBackground(Color.GRAY);
        jlblScore.setBounds(250, 30, 120, 30);
        jframeMain.add(jlblScore);

        //4、提示说明区
        jlblTip = new JLabel("↑ ↓ ← → to move, esc to restart  ",
                JLabel.CENTER);
        jlblTip.setFont(explainFont);
        jlblTip.setForeground(Color.DARK_GRAY);
        jlblTip.setBounds(0, 60, 400, 40);
        jframeMain.add(jlblTip);

        //5、主游戏面板区
        gameBoard = new GameBoard();
        gameBoard.setBounds(0, 100, 400, 400);
        gameBoard.setBackground(Color.GRAY);
        gameBoard.setFocusable(true);//焦点即当前正在操作的组件，也就是移动的数字
        gameBoard.setLayout(new FlowLayout());
        jframeMain.add(gameBoard);
    }

    // 游戏面板
    class GameBoard extends JPanel implements KeyListener {
        private static final int CHECK_GAP = 10;//方格之间的间隙
        private static final int CHECK_SIZE = 85;//方格大小
        private static final int CHECK_ARC = 20;//方格弧度

        private Check[][] checks = new Check[4][4];
        private boolean isadd = true;

        public GameBoard() {
            initGame();
            addKeyListener(this);
        }

        private void initGame() {
            score = 0;
            for (int indexRow = 0; indexRow < 4; indexRow++) {
                for (int indexCol = 0; indexCol < 4; indexCol++) {
                    checks[indexRow][indexCol] = new Check();
                }
            }

            isadd = true;
            createCheck();
            isadd = true;
            createCheck();
        }

        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_ESCAPE:
                    initGame();//重新开始游戏（初始化游戏）
                    break;
                case KeyEvent.VK_LEFT:
                    moveLeft();
                    createCheck();//调用一次方法创建一个方格数字
                    judgeGameOver();//创建后判断是否GameOver，若所有格子均满即跳出GameOver
                    break;
                case KeyEvent.VK_RIGHT:
                    moveRight();
                    createCheck();
                    judgeGameOver();
                    break;
                case KeyEvent.VK_UP:
                    moveUp();
                    createCheck();
                    judgeGameOver();
                    break;
                case KeyEvent.VK_DOWN:
                    moveDown();
                    createCheck();
                    judgeGameOver();
                    break;
                default:
                    break;//按其他键没有反应
            }
            repaint();//刷新，会自动调用paint()方法，重新绘制移动后的图
        }


        private void createCheck() {
            List<Check> list = getEmptyChecks();

            if (!list.isEmpty() && isadd) {
                Random random = new Random();
                int index = random.nextInt(list.size());
                Check check = list.get(index);
                // 2, 4出现概率3:1
                int randomValue = random.nextInt(4);
                check.value = ( randomValue % 3 == 0 || randomValue % 3 == 1) ? 2 : 4;//只有[0,4)中的2才能生成4
                isadd = false;
            }
        }

        // 获取空白方格
        private List<Check> getEmptyChecks() {
            List<Check> checkList = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    if (checks[i][j].value == 0) {
                        checkList.add(checks[i][j]);
                    }
                }
            }
            return checkList;
        }
        //是否全部格子占满，全部占满则GameOver
        private boolean judgeGameOver() {
            jlblScore.setText(score + "");

            if (!getEmptyChecks().isEmpty()) {
                return false;
            }

            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    //判断是否存在可合并的方格
                    if (checks[i][j].value == checks[i][j + 1].value
                            || checks[i][j].value == checks[i + 1][j].value) {
                        return false;
                    }
                }
            }

            return true;
        }

        private void moveLeft() {
            //找到一个非空格子后checks[i][j].value > 0，可分为三种情况处理
            for (int i = 0; i < 4; i++) {
                for (int j = 1, index = 0; j < 4; j++) {
                    if (checks[i][j].value > 0) {
                        //第一种情况：checks[i][j]（非第1列）与checks[i][index]的数相等，则合并乘以2，且得分增加
                        if (checks[i][j].value == checks[i][index].value) {
                            score += checks[i][index].value *= 2;
                            checks[i][j].value = 0;
                            isadd = true;
                        } else if (checks[i][index].value == 0) {
                            //第二种：若checks[i][index]为空格子，checks[i][j]就直接移到最左边checks[i][index]
                            checks[i][index].value = checks[i][j].value;
                            checks[i][j].value = 0;
                            isadd = true;
                        } else if (checks[i][++index].value == 0) {
                            //第三种：若checks[i][index]不为空格子，并且数字也不相等，若其旁边为空格子，则移到其旁边
                            checks[i][index].value = checks[i][j].value;
                            checks[i][j].value = 0;
                            isadd = true;
                        }
                    }
                }
            }
        }

        private void moveRight() {
            for (int i = 0; i < 4; i++) {
                for (int j = 2, index = 3; j >= 0; j--) {
                    if (checks[i][j].value > 0) {
                        if (checks[i][j].value == checks[i][index].value) {
                            score += checks[i][index].value *= 2;
                            checks[i][j].value = 0;
                            isadd = true;
                        } else if (checks[i][index].value == 0) {
                            checks[i][index].value = checks[i][j].value;
                            checks[i][j].value = 0;
                            isadd = true;
                        } else if (checks[i][--index].value == 0) {
                            checks[i][index].value = checks[i][j].value;
                            checks[i][j].value = 0;
                            isadd = true;
                        }
                    }
                }
            }
        }

        private void moveUp() {
            for (int i = 0; i < 4; i++) {
                for (int j = 1, index = 0; j < 4; j++) {
                    if (checks[j][i].value > 0) {
                        if (checks[j][i].value == checks[index][i].value) {
                            score += checks[index][i].value *= 2;
                            checks[j][i].value = 0;
                            isadd = true;
                        } else if (checks[index][i].value == 0) {
                            checks[index][i].value = checks[j][i].value;
                            checks[j][i].value = 0;
                            isadd = true;
                        } else if (checks[++index][i].value == 0){
                            checks[index][i].value = checks[j][i].value;
                            checks[j][i].value = 0;
                            isadd = true;
                        }
                    }
                }
            }
        }

        private void moveDown() {
            for (int i = 0; i < 4; i++) {
                for (int j = 2, index = 3; j >= 0; j--) {
                    if (checks[j][i].value > 0) {
                        if (checks[j][i].value == checks[index][i].value) {
                            score += checks[index][i].value *= 2;
                            checks[j][i].value = 0;
                            isadd = true;
                        } else if (checks[index][i].value == 0) {
                            checks[index][i].value = checks[j][i].value;
                            checks[j][i].value = 0;
                            isadd = true;
                        } else if (checks[--index][i].value == 0) {
                            checks[index][i].value = checks[j][i].value;
                            checks[j][i].value = 0;
                            isadd = true;
                        }
                    }
                }
            }
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    drawCheck(g, i, j);
                }
            }

            // GameOver
            if (judgeGameOver()) {
                g.setColor(new Color(64, 64, 64, 100));//RGBA最后一个A可以视为透明度
                g.fillRect(0, 0, getWidth(), getHeight());//填充矩形（游戏面板），将暗黑色填充上去
                g.setColor(Color.WHITE);
                g.setFont(topicFont);
                FontMetrics fms = getFontMetrics(topicFont);//FontMetrics字体测量，该类是Paint的内部类，通过getFontMetrics()方法可获取字体相关属性
                String value = "Game Over!";
                g.drawString(value, (getWidth()-fms.stringWidth(value)) / 2, getHeight() / 2);//字体居中显示
            }
        }

        // 绘制方格
        // Graphics2D 类是Graphics 子类，拥有强大的二维图形处理能力
        private void drawCheck(Graphics g, int i, int j) {
            Graphics2D gg = (Graphics2D) g;
            //下面两句是抗锯齿模式，计算和优化消除文字锯齿，字体更清晰顺滑
            gg.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            gg.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
            //获取方格
            Check check = checks[i][j];
            //不同数字设置背景色
            gg.setColor(check.getBackground());
            // 绘制圆角
            gg.fillRoundRect(CHECK_GAP + (CHECK_GAP + CHECK_SIZE) * j,
                    CHECK_GAP + (CHECK_GAP + CHECK_SIZE) * i,
                    CHECK_SIZE, CHECK_SIZE, CHECK_ARC, CHECK_ARC);
            //绘制字体及其颜色
            gg.setColor(check.getForeground());
            gg.setFont(check.getCheckFont());

            // 文字测量，并对文字进行绘制
            FontMetrics fms = getFontMetrics(check.getCheckFont());
            String value = String.valueOf(check.value);
            //使用此图形上下文的当前颜色绘制由指定迭代器给定的文本。
            //getAscent()是FontMetrics中的一个方法，
            //getDescent() 为降部
            gg.drawString(value,
                    CHECK_GAP + (CHECK_GAP + CHECK_SIZE) * j +
                            (CHECK_SIZE - fms.stringWidth(value)) / 2,
                    CHECK_GAP + (CHECK_GAP + CHECK_SIZE) * i +
                            (CHECK_SIZE - fms.getAscent() - fms.getDescent()) / 2
                            + fms.getAscent());//让数字居中显示
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }

        @Override
        public void keyTyped(KeyEvent e) {
        }

    }

    public void showView() {
        jframeMain.setVisible(true);
    }

}