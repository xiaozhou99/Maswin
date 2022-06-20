package main.ning;

import main.lu.Chess;
import main.yang.GobangPanel;

import java.util.Stack;



    public class Pruning {
        private GobangPanel gobangPanel;
        public static final int BOARD_SIZE = 15;// 棋盘格数
        private static int INFINITY = 1000000;
        private static int movex = 1;
        private static int movey = 1;
        private static int level;// 深度
        private static int node = 10;// 每层结点
        private static AIGo aigo;
        public static int[][] boardData;
        public static Stack<Chess> history;
        private static int color;
        private static Chess maxchess;


        public Pruning(int[][] boardData, Stack<Chess> history, int level, int color) {
            this.boardData = boardData;
            this.history = history;
            this.color = color;
            this.level = level;
            aigo = new AIGo();
        }


        // 估值函数+搜索树
        public static Chess findTreeBestStep() {
            int i = alpha_beta(0, boardData, history, -INFINITY, INFINITY, color);
            System.out.println(i);
            return new Chess(movex, movey);
        }



        // alpha-beta剪枝搜索算法
        public static int alpha_beta(int depth, int[][] boardData, Stack<Chess> history, int alpha, int beta, int color) {
            if (depth == level) {
                Chess chess = maxchess;
                // 搜索树辅助输出
//            System.out.println("【" + ( move.x) +  (move.y) + "】," + move.getScore()+","+color);
                return maxchess.getScore();// 局面估分
            }
            // 对局面下得分最高的几个点进行拓展
            Chess[] sorted = Level.getSortedPoint(color,boardData,history);
            int score = 0;
            for (int i = 0; i < node; i++) {
                int x = sorted[i].getX();
                int y = sorted[i].getY();
                // 走这个走法
                if (boardData[x][y] == 0) {
                    boardData[x][y] = color;
                    history.push(new Chess(x, y, color));
                    maxchess = new Chess(x, y, sorted[i].getScore());
                    color = 3 - color;
                } else continue;
                score = alpha_beta(depth + 1, boardData, history, alpha, beta, color);
                //temp = new GobangPanel(gobangPanel);// 撤消这个走法
                boardData[x][y] = 0;
                history.pop();
                color = 3 - color;
                if (depth % 2 == 0) {// MAX
                    if (score > alpha) {
                        alpha = score;
                        if (depth == 0) {
                            movex = x;
                            movey = y;
                        }
                    }
                    if (alpha >= beta) {
                        score = alpha;
//                        System.out.println(" beta剪枝");
                        return score;
                    }
                } else {// MIN
                    if (score < beta) {
                        beta = score;
                    }
                    if (alpha >= beta) {
                        score = beta;
//                        System.out.println(" alpha剪枝");
                        return score;
                    }
                }

            }
            return depth % 2 == 0 ? alpha : beta;
        }


}
