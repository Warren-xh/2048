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
    private static final int jframeWidth = 405;//���ڿ��
    private static final int jframeHeight = 530;
    private static int score = 0;

    Font topicFont = new Font("΢���ź�", Font.BOLD, 50);//��������
    Font scoreFont = new Font("΢���ź�", Font.BOLD, 28);//�÷�����
    Font explainFont = new Font("����", Font.PLAIN,20);//��ʾ����

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
        //1����������
        jframeMain = new JFrame("HipHop 2048");
        jframeMain.setSize(jframeWidth, jframeHeight);
        jframeMain.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframeMain.setLocationRelativeTo(null);//������ʾλ�þ���
        jframeMain.setResizable(false);
        jframeMain.setLayout(null);//���þ��Բ��֣��Ա���������setBounds����λ��

        jlblTitle = new JLabel("2048", JLabel.CENTER);
        jlblTitle.setFont(topicFont);
        jlblTitle.setForeground(Color.BLACK);
        jlblTitle.setBounds(50, 0, 150, 60);
        jframeMain.add(jlblTitle);


        //2����ܴ��ڴ�ã����������濪ʼ�������
        //�������弰����ɫ��λ��
        jlblScoreName = new JLabel("Point", JLabel.CENTER);
        jlblScoreName.setFont(scoreFont);
        jlblScoreName.setForeground(Color.WHITE);
        jlblScoreName.setOpaque(true);
        jlblScoreName.setBackground(Color.GRAY);
        jlblScoreName.setBounds(250, 0, 120, 30);
        jframeMain.add(jlblScoreName);

        //3���÷���(�÷���+����)
        jlblScore = new JLabel("0", JLabel.CENTER);
        jlblScore.setFont(scoreFont);
        jlblScore.setForeground(Color.WHITE);
        jlblScore.setOpaque(true);
        jlblScore.setBackground(Color.GRAY);
        jlblScore.setBounds(250, 30, 120, 30);
        jframeMain.add(jlblScore);

        //4����ʾ˵����
        jlblTip = new JLabel("�� �� �� �� to move, esc to restart  ",
                JLabel.CENTER);
        jlblTip.setFont(explainFont);
        jlblTip.setForeground(Color.DARK_GRAY);
        jlblTip.setBounds(0, 60, 400, 40);
        jframeMain.add(jlblTip);

        //5������Ϸ�����
        gameBoard = new GameBoard();
        gameBoard.setBounds(0, 100, 400, 400);
        gameBoard.setBackground(Color.GRAY);
        gameBoard.setFocusable(true);//���㼴��ǰ���ڲ����������Ҳ�����ƶ�������
        gameBoard.setLayout(new FlowLayout());
        jframeMain.add(gameBoard);
    }

    // ��Ϸ���
    class GameBoard extends JPanel implements KeyListener {
        private static final int CHECK_GAP = 10;//����֮��ļ�϶
        private static final int CHECK_SIZE = 85;//�����С
        private static final int CHECK_ARC = 20;//���񻡶�

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
                    initGame();//���¿�ʼ��Ϸ����ʼ����Ϸ��
                    break;
                case KeyEvent.VK_LEFT:
                    moveLeft();
                    createCheck();//����һ�η�������һ����������
                    judgeGameOver();//�������ж��Ƿ�GameOver�������и��Ӿ���������GameOver
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
                    break;//��������û�з�Ӧ
            }
            repaint();//ˢ�£����Զ�����paint()���������»����ƶ����ͼ
        }


        private void createCheck() {
            List<Check> list = getEmptyChecks();

            if (!list.isEmpty() && isadd) {
                Random random = new Random();
                int index = random.nextInt(list.size());
                Check check = list.get(index);
                // 2, 4���ָ���3:1
                int randomValue = random.nextInt(4);
                check.value = ( randomValue % 3 == 0 || randomValue % 3 == 1) ? 2 : 4;//ֻ��[0,4)�е�2��������4
                isadd = false;
            }
        }

        // ��ȡ�հ׷���
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
        //�Ƿ�ȫ������ռ����ȫ��ռ����GameOver
        private boolean judgeGameOver() {
            jlblScore.setText(score + "");

            if (!getEmptyChecks().isEmpty()) {
                return false;
            }

            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    //�ж��Ƿ���ڿɺϲ��ķ���
                    if (checks[i][j].value == checks[i][j + 1].value
                            || checks[i][j].value == checks[i + 1][j].value) {
                        return false;
                    }
                }
            }

            return true;
        }

        private void moveLeft() {
            //�ҵ�һ���ǿո��Ӻ�checks[i][j].value > 0���ɷ�Ϊ�����������
            for (int i = 0; i < 4; i++) {
                for (int j = 1, index = 0; j < 4; j++) {
                    if (checks[i][j].value > 0) {
                        //��һ�������checks[i][j]���ǵ�1�У���checks[i][index]������ȣ���ϲ�����2���ҵ÷�����
                        if (checks[i][j].value == checks[i][index].value) {
                            score += checks[i][index].value *= 2;
                            checks[i][j].value = 0;
                            isadd = true;
                        } else if (checks[i][index].value == 0) {
                            //�ڶ��֣���checks[i][index]Ϊ�ո��ӣ�checks[i][j]��ֱ���Ƶ������checks[i][index]
                            checks[i][index].value = checks[i][j].value;
                            checks[i][j].value = 0;
                            isadd = true;
                        } else if (checks[i][++index].value == 0) {
                            //�����֣���checks[i][index]��Ϊ�ո��ӣ���������Ҳ����ȣ������Ա�Ϊ�ո��ӣ����Ƶ����Ա�
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
                g.setColor(new Color(64, 64, 64, 100));//RGBA���һ��A������Ϊ͸����
                g.fillRect(0, 0, getWidth(), getHeight());//�����Σ���Ϸ��壩��������ɫ�����ȥ
                g.setColor(Color.WHITE);
                g.setFont(topicFont);
                FontMetrics fms = getFontMetrics(topicFont);//FontMetrics���������������Paint���ڲ��࣬ͨ��getFontMetrics()�����ɻ�ȡ�����������
                String value = "Game Over!";
                g.drawString(value, (getWidth()-fms.stringWidth(value)) / 2, getHeight() / 2);//���������ʾ
            }
        }

        // ���Ʒ���
        // Graphics2D ����Graphics ���࣬ӵ��ǿ��Ķ�άͼ�δ�������
        private void drawCheck(Graphics g, int i, int j) {
            Graphics2D gg = (Graphics2D) g;
            //���������ǿ����ģʽ��������Ż��������־�ݣ����������˳��
            gg.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            gg.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
            //��ȡ����
            Check check = checks[i][j];
            //��ͬ�������ñ���ɫ
            gg.setColor(check.getBackground());
            // ����Բ��
            gg.fillRoundRect(CHECK_GAP + (CHECK_GAP + CHECK_SIZE) * j,
                    CHECK_GAP + (CHECK_GAP + CHECK_SIZE) * i,
                    CHECK_SIZE, CHECK_SIZE, CHECK_ARC, CHECK_ARC);
            //�������弰����ɫ
            gg.setColor(check.getForeground());
            gg.setFont(check.getCheckFont());

            // ���ֲ������������ֽ��л���
            FontMetrics fms = getFontMetrics(check.getCheckFont());
            String value = String.valueOf(check.value);
            //ʹ�ô�ͼ�������ĵĵ�ǰ��ɫ������ָ���������������ı���
            //getAscent()��FontMetrics�е�һ��������
            //getDescent() Ϊ����
            gg.drawString(value,
                    CHECK_GAP + (CHECK_GAP + CHECK_SIZE) * j +
                            (CHECK_SIZE - fms.stringWidth(value)) / 2,
                    CHECK_GAP + (CHECK_GAP + CHECK_SIZE) * i +
                            (CHECK_SIZE - fms.getAscent() - fms.getDescent()) / 2
                            + fms.getAscent());//�����־�����ʾ
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