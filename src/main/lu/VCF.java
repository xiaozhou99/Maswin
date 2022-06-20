package main.lu;

import main.yang.GobangPanel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class VCF {
    private final ArrayList<ArrayList<Chess>> chess_wall;  //模拟边界，chess_wall[0]表示边界为黑，1为白
    private ArrayList<ArrayList<Chess>> changlian;
    private ArrayList<ArrayList<Chess>> lian5;  //一些棋形
    private ArrayList<ArrayList<Chess>> huo4;
    private ArrayList<ArrayList<Chess>> mian4;
    private ArrayList<ArrayList<Chess>> huo3;
    private ArrayList<ArrayList<Chess>> mian3;
    private ArrayList<ArrayList<Chess>> huo2;
    private ArrayList<Chess> chessman; //落子的历史记录
    private ArrayList<Chess> chess_solve;  //解的落子走法
    private Chess laststep;
    private int count_vcf;   //VCF遍历的深度

    public VCF() {
        // chess_wall初始化
        ArrayList<Chess> chess_wall0 = new ArrayList<>();
        ArrayList<Chess> chess_wall1 = new ArrayList<>();
        chess_wall = new ArrayList<>();
        for (int i = 0; i < 17; i += 16) {
            for (int j = 0; j < 17; j++) {
                chess_wall0.add(new Chess(i,j,1));
                chess_wall0.add(new Chess(j,i,1));
                chess_wall1.add(new Chess(i,j,2));
                chess_wall1.add(new Chess(j,i,2));
            }
        }
        chess_wall.add(chess_wall0);
        chess_wall.add(chess_wall1);


        // 棋形初始化
        changlian = new ArrayList<>();
        lian5 = new ArrayList<>();
        huo4 = new ArrayList<>();
        mian4 = new ArrayList<>();
        huo3 = new ArrayList<>();
        mian3 = new ArrayList<>();
        huo2 = new ArrayList<>();


        chessman = new ArrayList<>();

        chess_solve = new ArrayList<>();  // 解初始化
        count_vcf = 0;  //VCF深度初始化
    }

    // 找模式
    public void find_pattern() {
        long startTime = System.currentTimeMillis();
        // 清空
        changlian = new ArrayList<>();
        lian5 = new ArrayList<>();
        huo4 = new ArrayList<>();
        mian4 = new ArrayList<>();
        huo3 = new ArrayList<>();
        mian3 = new ArrayList<>();
        huo2 = new ArrayList<>();

        for (Chess cm : chessman) {
            int x = cm.getX(), y = cm.getY(), c = cm.getColor();
            ArrayList<Chess> chessman_c = new ArrayList<>();
            chessman_c.addAll(chessman);
            chessman_c.addAll(chess_wall.get(2 - c));

            int[][] unit = {{1, 0}, {1, 1}, {0, 1}, {-1, 1}};
            for(int index = 0; index < 4; index ++) {
                int[] i = unit[index];
                // 长连
                ArrayList<Chess> list_y = new ArrayList<>(Arrays.asList(
                        new Chess(x, y, c),
                        new Chess(x + i[0], y + i[1], c),
                        new Chess(x + 2 * i[0], y + 2 * i[1], c),
                        new Chess(x + 3 * i[0], y + 3 * i[1], c),
                        new Chess(x + 4 * i[0], y + 4 * i[1], c),
                        new Chess(x + 5 * i[0], y + 5 * i[1], c)));
                ArrayList<Chess> list_n;

                if (isContainsAll(chessman_c,list_y)) {
                    changlian.add(list_y);
                }

                // 连五
                list_y = new ArrayList<>(Arrays.asList(
                        new Chess(x, y, c),
                        new Chess(x + i[0], y + i[1], c),
                        new Chess(x + 2 * i[0], y + 2 * i[1], c),
                        new Chess(x + 3 * i[0], y + 3 * i[1], c),
                        new Chess(x + 4 * i[0], y + 4 * i[1], c)));
                list_n = new ArrayList<>();

                if (c == 1) {
                    list_n.add(new Chess(x - i[0], y - i[1], c));
                    list_n.add(new Chess(x + 5 * i[0], y + 5 * i[1], c));
                }
                if (isContainsAll(chessman_c,list_y) && isNotIntersection(chessman_c , list_n)) {
                    lian5.add(list_y);
                }

                //活四
                list_y = new ArrayList<>(Arrays.asList(
                        new Chess(x, y, c),
                        new Chess(x + i[0], y + i[1], c),
                        new Chess(x + 2 * i[0], y + 2 * i[1], c),
                        new Chess(x + 3 * i[0], y + 3 * i[1], c)));
                list_n = new ArrayList<>(Arrays.asList(
                        new Chess(x - i[0], y - i[1], c),
                        new Chess(x + 4 * i[0], y + 4 * i[1], c),
                        new Chess(x - i[0], y - i[1], 3 - c),
                        new Chess(x + 4 * i[0], y + 4 * i[1], 3 - c)));
                if (c == 1) {
                    list_n.add(new Chess(x - 2 * i[0], y - 2 * i[1], c));
                    list_n.add(new Chess(x + 5 * i[0], y + 5 * i[1], c));
                }
                if (isContainsAll(chessman_c,list_y) && isNotIntersection(chessman_c , list_n)) {
                    ArrayList<Chess> temp = new ArrayList<>(list_y);
                    temp.add(list_n.get(0));
                    temp.add(list_n.get(1));
                    huo4.add(temp);
                }

                //眠四 XX XX  二二四
                list_y = new ArrayList<>(Arrays.asList(
                        new Chess(x, y, c),
                        new Chess(x + i[0], y + i[1], c),
                        new Chess(x + 3 * i[0], y + 3 * i[1], c),
                        new Chess(x + 4 * i[0], y + 4 * i[1], c)));
                list_n = new ArrayList<>(Arrays.asList(
                        new Chess(x + 2 * i[0], y + 2 * i[1], c),
                        new Chess(x + 2 * i[0], y + 2 * i[1], 3 - c)));
                if (c == 1) {
                    list_n.add(new Chess(x + 5 * i[0], y + 5 * i[1], c));
                    list_n.add(new Chess(x - i[0], y - i[1], c));
                }
                if (isContainsAll(chessman_c,list_y) && isNotIntersection(chessman_c , list_n)) {
                    ArrayList<Chess> temp = new ArrayList<>(list_y);
                    temp.add(list_n.get(0));
                    mian4.add(temp);
                }

                //活三  XXX 连三
                list_y = new ArrayList<>(Arrays.asList(
                        new Chess(x, y, c),
                        new Chess(x + i[0], y + i[1], c),
                        new Chess(x + 2 * i[0], y + 2 * i[1], c)));
                list_n = new ArrayList<>(Arrays.asList(
                        new Chess(x - i[0], y - i[1], c),
                        new Chess(x + 3 * i[0], y + 3 * i[1], c),
                        new Chess(x - i[0], y - i[1], 3 - c),
                        new Chess(x + 3 * i[0], y + 3 * i[1], 3 - c)));
                if (c == 1) {
                    list_n.add(new Chess(x - 2 * i[0], y - 2 * i[1], c));
                    list_n.add(new Chess(x + 4 * i[0], y + 4 * i[1], c));
                }

                if (isContainsAll(chessman_c,list_y) && isNotIntersection(chessman_c , list_n)) {
                   ArrayList<Chess> list_2d = new ArrayList<>();
                   list_2d.add(new Chess(x - 2 * i[0], y - 2 * i[1], 3 - c));
                   list_2d.add(new Chess(x + 4 * i[0], y + 4 * i[1], 3 - c));
                   ArrayList<Chess> temp = new ArrayList<>(list_y);
                   if (isContainsAll(chessman_c,list_2d)) {
                       temp.add(list_n.get(0));
                       temp.add(list_n.get(1));
                       mian3.add(temp);
                   } else if (isContains(chessman_c,list_2d.get(0))) {  //修改
                       temp.add(list_n.get(1));
                       temp.add(list_2d.get(1));
                       temp.add(list_n.get(0));
                       huo3.add(temp);
                   } else if (isContains(chessman_c,list_2d.get(1))) {
                       temp.add(list_n.get(0));
                       temp.add(list_2d.get(0));
                       temp.add(list_n.get(1));
                       huo3.add(temp);
                   } else {
                       temp.add(list_n.get(0));
                       temp.add(list_n.get(1));
                       temp.add(list_2d.get(0));
                       temp.add(list_2d.get(1));
                       huo3.add(temp);
                   }
                }

                //眠三 X X X
                list_y = new ArrayList<>(Arrays.asList(
                        new Chess(x, y, c),
                        new Chess(x + 2 * i[0], y + 2 * i[1], c),
                        new Chess(x + 4 * i[0], y + 4 * i[1], c)));
                list_n = new ArrayList<>(Arrays.asList(
                        new Chess(x + i[0], y + i[1], c),
                        new Chess(x + 3 * i[0], y + 3 * i[1], c),
                        new Chess(x + i[0], y + i[1], 3 - c),
                        new Chess(x + 3 * i[0], y + 3 * i[1], 3 - c)));
                if (c == 1) {
                    list_n.add(new Chess(x - i[0], y - i[1], c));
                    list_n.add(new Chess(x + 5 * i[0], y + 5 * i[1], c));
                }
                if (isContainsAll(chessman_c,list_y) && isNotIntersection(chessman_c , list_n)) {
                    ArrayList<Chess> temp = new ArrayList<>(list_y);
                    temp.add(list_n.get(0));
                    temp.add(list_n.get(1));
                    mian3.add(temp);
                }

                //活二 XX
                /*list_y = new ArrayList<>(Arrays.asList(
                        new Chess(x, y, c),
                        new Chess(x + i[0], y + i[1], c)));
                list_n = new ArrayList<>(Arrays.asList(
                        new Chess(x - i[0], y - i[1], c),
                        new Chess(x - 2 * i[0], y - 2 * i[1], c),
                        new Chess(x - 3 * i[0], y - 3 * i[1], c),
                        new Chess(x + 2 * i[0], y + 2 * i[1], c),
                        new Chess(x + 3 * i[0], y + 3 * i[1], c),
                        new Chess(x + 4 * i[0], y + 4 * i[1], c),
                        new Chess(x - i[0], y - i[1], 3 - c),
                        new Chess(x + 2 * i[0], y + 2 * i[1], 3 - c)));
                if (c == 1) {
                    list_n.add(new Chess(x - 4 * i[0], y - 4 * i[1], c));
                    list_n.add(new Chess(x + 5 * i[0], y + 5 * i[1], c));
                }

                if (isContainsAll(chessman_c,list_y) && isNotIntersection(chessman_c , list_n)) {
                   int count_left = 0;
                   ArrayList<Chess> points_left = new ArrayList<>();
                   for(int j = -1; j > -4; j--) {
                       Chess p = new Chess(x + j * i[0], y + j * i[1], 3 - c);
                       if (!(isContains(chessman_c,p))) {
                           count_left ++;
                           points_left.add(p);
                       } else {
                           break;
                       }
                   }

                    int count_right = 0;
                    ArrayList<Chess> points_right = new ArrayList<>();
                    for(int j = 2; j < 5; j++) {
                        Chess p = new Chess(x + j * i[0], y + j * i[1], 3 - c);
                        if (!(isContains(chessman_c,p))) {
                            count_right ++;
                            points_right.add(p);
                        } else {
                            break;
                        }
                    }

                    if (count_left + count_right > 3) {
                        ArrayList<Chess> temp = new ArrayList<>(list_y);
                        for(int k = 0; k < points_left.size() - 1; k++)
                            temp.add(new Chess(points_left.get(k).x, points_left.get(k).y, c));
                        for(int k = 0; k < points_right.size() - 1; k++)
                            temp.add(new Chess(points_right.get(k).x, points_right.get(k).y, c));
                        huo2.add(temp);
                    }
                }

                //跳二  X X
                list_y = new ArrayList<>(Arrays.asList(
                        new Chess(x, y, c),
                        new Chess(x + 2 * i[0], y + 2 * i[1], c)));
                list_n = new ArrayList<>(Arrays.asList(
                        new Chess(x + i[0], y + i[1], c),
                        new Chess(x - i[0], y - i[1], c),
                        new Chess(x - 2 * i[0], y - 2 * i[1], c),
                        new Chess(x + 3 * i[0], y + 3 * i[1], c),
                        new Chess(x + 4 * i[0], y + 4 * i[1], c),
                        new Chess(x + i[0], y + i[1], 3 - c),
                        new Chess(x - i[0], y - i[1], 3 - c),
                        new Chess(x + 3 * i[0], y + 3 * i[1], 3 - c)));
                if (c == 1) {
                    list_n.add(new Chess(x - 3 * i[0], y - 3 * i[1], c));
                    list_n.add(new Chess(x + 5 * i[0], y + 5 * i[1], c));
                }
                if (isContainsAll(chessman_c,list_y) && isNotIntersection(chessman_c , list_n)) {
                    int count_left = 0;
                    ArrayList<Chess> points_left = new ArrayList<>();
                    for(int j = -1; j > -3; j--) {
                        Chess p = new Chess(x + j * i[0], y + j * i[1], 3 - c);
                        if (!(isContains(chessman_c,p))) {
                            count_left ++;
                            points_left.add(p);
                        } else {
                            break;
                        }
                    }

                    int count_right = 0;
                    ArrayList<Chess> points_right = new ArrayList<>();
                    for(int j = 3; j < 5; j++) {
                        Chess p = new Chess(x + j * i[0], y + j * i[1], 3 - c);
                        if (!(isContains(chessman_c, p))) {
                            count_right ++;
                            points_right.add(p);
                        } else {
                            break;
                        }
                    }

                    if (count_left + count_right > 2) {
                        ArrayList<Chess> temp = new ArrayList<>(list_y);
                        temp.add(new Chess(x + i[0], y + i[1], c));
                        for(int k = 0; k < points_left.size() - 1; k++)
                            temp.add(new Chess(points_left.get(k).x, points_left.get(k).y, c));
                        for(int k = 0; k < points_right.size() - 1; k++)
                            temp.add(new Chess(points_right.get(k).x, points_right.get(k).y, c));
                        huo2.add(temp);
                    }
                }

                //X  X
                list_y = new ArrayList<>(Arrays.asList(
                        new Chess(x, y, c),
                        new Chess(x + 3 * i[0], y + 3 * i[1], c)));
                list_n = new ArrayList<>(Arrays.asList(
                        new Chess(x + i[0], y + i[1], c),
                        new Chess(x + 2 * i[0], y + 2 * i[1], c),
                        new Chess(x - i[0], y - i[1], c),
                        new Chess(x + 4 * i[0], y + 4 * i[1], c),
                        new Chess(x + i[0], y + i[1], 3 - c),
                        new Chess(x + 2 * i[0], y + 2 * i[1], 3 - c),
                        new Chess(x - i[0], y - i[1], 3 - c),
                        new Chess(x + 4 * i[0], y + 4 * i[1], 3 - c)));
                if (c == 1) {
                    list_n.add(new Chess(x - 2 * i[0], y - 2 * i[1], c));
                    list_n.add(new Chess(x + 5 * i[0], y + 5 * i[1], c));
                }
                if (isContainsAll(chessman_c,list_y) && isNotIntersection(chessman_c , list_n)) {
                    ArrayList<Chess> temp = new ArrayList<>(list_y);
                    temp.add(list_n.get(0));
                    temp.add(list_n.get(1));
                    huo2.add(temp);
                }*/
            }

            int[][] unit2 = {{1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}, {1, -1}};
            for(int index = 0; index < 8 ; index ++) {
                int[] i = unit2[index];

                //OXXXX
                ArrayList<Chess> list_y = new ArrayList<>(Arrays.asList(
                        new Chess(x - i[0], y - i[1], 3 - c),
                        new Chess(x, y, c),
                        new Chess(x + i[0], y + i[1], c),
                        new Chess(x + 2 * i[0], y + 2 * i[1], c),
                        new Chess(x + 3 * i[0], y + 3 * i[1], c)));
                ArrayList<Chess> list_n = new ArrayList<>(Arrays.asList(
                        new Chess(x + 4 * i[0], y + 4 * i[1], c),
                        new Chess(x + 4 * i[0], y + 4 * i[1], 3 - c)));

                if (c == 1) {
                    list_n.add(new Chess(x + 5 * i[0], y + 5 * i[1], c));
                }
                if (isContainsAll(chessman_c,list_y) && isNotIntersection(chessman_c , list_n)) {
                    ArrayList<Chess> temp = new ArrayList<>();
                    for (int k = 1; k < list_y.size(); k++) {
                        temp.add(list_y.get(k));
                    }
                    temp.add(list_n.get(0));
                    mian4.add(temp);
                }

                //XXX X
                list_y = new ArrayList<>(Arrays.asList(
                        new Chess(x, y, c),
                        new Chess(x + i[0], y + i[1], c),
                        new Chess(x + 2 * i[0], y + 2 * i[1], c),
                        new Chess(x + 4 * i[0], y + 4 * i[1], c)));
                list_n = new ArrayList<>(Arrays.asList(
                        new Chess(x + 3 * i[0], y + 3 * i[1], c),
                        new Chess(x + 3 * i[0], y + 3 * i[1], 3 - c)));

                if (c == 1) {
                    list_n.add(new Chess(x + 5 * i[0], y + 5 * i[1], c));
                    list_n.add(new Chess(x - i[0], y - i[1], c));
                }
                if (isContainsAll(chessman_c,list_y) && isNotIntersection(chessman_c , list_n)) {
                    ArrayList<Chess> temp = new ArrayList<>(list_y);
                    temp.add(list_n.get(0));
                    mian4.add(temp);
                }

                // XX X
                list_y = new ArrayList<>(Arrays.asList(
                        new Chess(x, y, c),
                        new Chess(x + i[0], y + i[1], c),
                        new Chess(x + 3 * i[0], y + 3 * i[1], c)));
                list_n = new ArrayList<>(Arrays.asList(
                        new Chess(x + 2 * i[0], y + 2 * i[1], c),
                        new Chess(x - i[0], y - i[1], c),
                        new Chess(x + 4 * i[0], y + 4 * i[1], c),
                        new Chess(x - i[0], y - i[1], 3 - c),
                        new Chess(x + 2 * i[0], y + 2 * i[1], 3 - c),
                        new Chess(x + 4 * i[0], y + 4 * i[1], 3 - c)));

                if (c == 1) {
                    list_n.add(new Chess(x - 2 * i[0], y - 2 * i[1], c));
                    list_n.add(new Chess(x + 5 * i[0], y + 5 * i[1], c));
                }
                if (isContainsAll(chessman_c,list_y) && isNotIntersection(chessman_c , list_n)) {
                    ArrayList<Chess> temp = new ArrayList<>(list_y);
                    temp.add(list_n.get(0));
                    temp.add(list_n.get(1));  //修改
                    temp.add(list_n.get(2));
                    huo3.add(temp);
                }

                // 眠三 OXXX
                list_y = new ArrayList<>(Arrays.asList(
                        new Chess(x - i[0], y - i[1], 3 - c),
                        new Chess(x, y, c),
                        new Chess(x + i[0], y + i[1], c),
                        new Chess(x + 2 * i[0], y + 2 * i[1], c)));
                list_n = new ArrayList<>(Arrays.asList(
                        new Chess(x + 3 * i[0], y + 3 * i[1], c),
                        new Chess(x + 4 * i[0], y + 4 * i[1], c),
                        new Chess(x + 3 * i[0], y + 3 * i[1], 3 - c),
                        new Chess(x + 4 * i[0], y + 4 * i[1], 3 - c)));

                if (c == 1) {
                    list_n.add(new Chess(x + 5 * i[0], y + 5 * i[1], c));
                }
                if (isContainsAll(chessman_c,list_y) && isNotIntersection(chessman_c , list_n)) {
                    ArrayList<Chess> temp = new ArrayList<>();
                    for (int k = 1; k < list_y.size(); k++)
                        temp.add(list_y.get(k));
                    temp.add(list_n.get(0));
                    temp.add(list_n.get(1));
                    mian3.add(temp);
                }

                // 眠三 OXX X
                list_y = new ArrayList<>(Arrays.asList(
                        new Chess(x - i[0], y - i[1], 3 - c),
                        new Chess(x, y, c),
                        new Chess(x + i[0], y + i[1], c),
                        new Chess(x + 3 * i[0], y + 3 * i[1], c)));
                list_n = new ArrayList<>(Arrays.asList(
                        new Chess(x + 2 * i[0], y + 2 * i[1], c),
                        new Chess(x + 4 * i[0], y + 4 * i[1], c),
                        new Chess(x + 2 * i[0], y + 2 * i[1], 3 - c),
                        new Chess(x + 4 * i[0], y + 4 * i[1], 3 - c)));

                if (c == 1) {
                    list_n.add(new Chess(x + 5 * i[0], y + 5 * i[1], c));
                }
                if (isContainsAll(chessman_c,list_y) && isNotIntersection(chessman_c , list_n)) {
                    ArrayList<Chess> temp = new ArrayList<>();
                    for (int k = 1; k < list_y.size(); k++)
                        temp.add(list_y.get(k));
                    temp.add(list_n.get(0));
                    temp.add(list_n.get(1));
                    mian3.add(temp);
                }

                // 眠三 OX XX
                list_y = new ArrayList<>(Arrays.asList(
                        new Chess(x - i[0], y - i[1], 3 - c),
                        new Chess(x, y, c),
                        new Chess(x + 2 * i[0], y + 2 * i[1], c),
                        new Chess(x + 3 * i[0], y + 3 * i[1], c)));
                list_n = new ArrayList<>(Arrays.asList(
                        new Chess(x + i[0], y + i[1], c),
                        new Chess(x + 4 * i[0], y + 4 * i[1], c),
                        new Chess(x + i[0], y + i[1], 3 - c),
                        new Chess(x + 4 * i[0], y + 4 * i[1], 3 - c)));

                if (c == 1) {
                    list_n.add(new Chess(x + 5 * i[0], y + 5 * i[1], c));
                }
                if (isContainsAll(chessman_c,list_y) && isNotIntersection(chessman_c , list_n)) {
                    ArrayList<Chess> temp = new ArrayList<>();
                    for (int k = 1; k < list_y.size(); k++)
                        temp.add(list_y.get(k));
                    temp.add(list_n.get(0));
                    temp.add(list_n.get(1));
                    mian3.add(temp);
                }

                // 眠三 XX  X
                list_y = new ArrayList<>(Arrays.asList(
                        new Chess(x, y, c),
                        new Chess(x + i[0], y + i[1], c),
                        new Chess(x + 4 * i[0], y + 4 * i[1], c)));
                list_n = new ArrayList<>(Arrays.asList(
                        new Chess(x + 2 * i[0], y + 2 * i[1], c),
                        new Chess(x + 3 * i[0], y + 3 * i[1], c),
                        new Chess(x + 2 * i[0], y + 2 * i[1], 3 - c),
                        new Chess(x + 3 * i[0], y + 3 * i[1], 3 - c)));

                if (c == 1) {
                    list_n.add(new Chess(x - i[0], y - i[1], c));
                    list_n.add(new Chess(x + 5 * i[0], y + 5 * i[1], c));
                }
                if (isContainsAll(chessman_c,list_y) && isNotIntersection(chessman_c , list_n)) {
                    ArrayList<Chess> temp = new ArrayList<>(list_y);
                    temp.add(list_n.get(0));
                    temp.add(list_n.get(1));
                    mian3.add(temp);
                }

                //增加，修改一种特殊的眠三  XX_XXX__
                if(c == 1) {
                    list_y = new ArrayList<>(Arrays.asList(
                            new Chess(x, y, c),
                            new Chess(x + i[0], y + i[1], c),
                            new Chess(x + 2 * i[0], y + 2 * i[1], c),
                            new Chess(x - 2 * i[0], y - 2 * i[1], c),
                            new Chess(x - 3 * i[0], y - 3 * i[1], c)));
                    list_n = new ArrayList<>(Arrays.asList(
                            new Chess(x - i[0], y - i[1], c),
                            new Chess(x + 3 * i[0], y + 3 * i[1], c),
                            new Chess(x + 4 * i[0], y + 4 * i[1], c),
                            new Chess(x + 5 * i[0], y + 5 * i[1], c),
                            new Chess(x - i[0], y - i[1], 3 - c),
                            new Chess(x + 3 * i[0], y + 3 * i[1], 3 - c),
                            new Chess(x + 4 * i[0], y + 4 * i[1], 3 - c)));
                    if (isContainsAll(chessman_c,list_y) && isNotIntersection(chessman_c , list_n)) {
                        ArrayList<Chess> temp = new ArrayList<>();
                        for (int k = 0; k < 3; k++)
                            temp.add(list_y.get(k));
                        temp.add(list_n.get(1));
                        temp.add(list_n.get(2));
                        mian3.add(temp);
                    }
                }

                // X XX X
                list_y = new ArrayList<>(Arrays.asList(
                        new Chess(x - 2 * i[0], y - 2 * i[1], c),
                        new Chess(x, y, c),
                        new Chess(x + i[0], y + i[1], c),
                        new Chess(x + 3 * i[0], y + 3 * i[1], c)));
                list_n = new ArrayList<>(Arrays.asList(
                        new Chess(x + 2 * i[0], y + 2 * i[1], c),
                        new Chess(x + 4 * i[0], y + 4 * i[1], c),
                        new Chess(x - i[0], y - i[1], c),
                        new Chess(x + 5 * i[0], y + 5 * i[1], c),
                        new Chess(x + 2 * i[0], y + 2 * i[1], 3 - c),
                        new Chess(x + 4 * i[0], y + 4 * i[1], 3 - c)));

                if (isContainsAll(chessman_c,list_y) && isNotIntersection(chessman_c , list_n)) {
                    ArrayList<Chess> temp = new ArrayList<>();
                    for (int k = 1; k < list_y.size(); k++)
                        temp.add(list_y.get(k));
                    temp.add(list_n.get(0));
                    temp.add(list_n.get(1));
                    mian3.add(temp);
                }

                // X X XX
                list_y = new ArrayList<>(Arrays.asList(
                        new Chess(x - 2 * i[0], y - 2 * i[1], c),
                        new Chess(x, y, c),
                        new Chess(x + 2 * i[0], y + 2 * i[1], c),
                        new Chess(x + 3 * i[0], y + 3 * i[1], c)));
                list_n = new ArrayList<>(Arrays.asList(
                        new Chess(x + i[0], y + i[1], c),
                        new Chess(x + 4 * i[0], y + 4 * i[1], c),
                        new Chess(x - i[0], y - i[1], c),
                        new Chess(x + 5 * i[0], y + 5 * i[1], c),
                        new Chess(x + i[0], y + i[1], 3 - c),
                        new Chess(x + 4 * i[0], y + 4 * i[1], 3 - c)));

                if (isContainsAll(chessman_c,list_y) && isNotIntersection(chessman_c , list_n)) {
                    ArrayList<Chess> temp = new ArrayList<>();
                    for (int k = 1; k < list_y.size(); k++)
                        temp.add(list_y.get(k));
                    temp.add(list_n.get(0));
                    temp.add(list_n.get(1));
                    mian3.add(temp);
                }

            }

        }
        long endTime = System.currentTimeMillis();
//        System.out.println("找棋形函数运行时间：" + (endTime - startTime) + "ms");
        // 输出各模式
       /* System.out.println("-----------------");
        System.out.print("L5:");
        printPattern(lian5);
        System.out.print("H4:");
        printPattern(huo4);
        System.out.print("M4:");
        printPattern(mian4);
        System.out.print("H3:");
        printPattern(huo3);
        System.out.print("M3:");
        printPattern(mian3);
        System.out.print("H2:");
        printPattern(huo2);*/
    }

    //输出
    public void printPattern(ArrayList<ArrayList<Chess>> list) {
        for (ArrayList<Chess> l : list){
            System.out.print("[");
            for(Chess c : l){
                System.out.print("(" + c.x + "," + c.y + "," + c.color + "),");
            }
            System.out.print("],");
        }
        System.out.println();
    }

    // 判断list2是否是list1的子集
    public boolean isContainsAll(ArrayList<Chess> list1, ArrayList<Chess> list2) {
        int sum = 0, i = 0;
        boolean result = true;
        for (Chess c1 : list2) {
            i++;
            for (Chess c2 : list1){
                if (c2.x == c1.x && c2.y == c1.y && c2.color == c1.color) {
                    sum ++;
                    break;
                }
            }
            if (sum != i){
                result = false;
                break;
            }
        }
        return result;
    }

    //判断list中是否包含chess对象
    public boolean isContains(ArrayList<Chess> list, Chess chess) {
        boolean result = false;
        for (Chess c : list) {
            if (c.x == chess.x && c.y == chess.y && c.color == chess.color) {
                result = true;
                break;
            }
        }
        return result;
    }

    // 判断两个集合是否没有交集
    public boolean isNotIntersection(ArrayList<Chess> list1, ArrayList<Chess> list2) {
        int length1 = list1.size(), length2 = list2.size();
        boolean result = false;
        if (length1 <= length2) {
            for (Chess chess : list1) {
                if (isContains(list2, chess)){
                    result = true;  //有交集
                    break;
                }
            }
        } else {
            for (Chess chess : list2) {
                if (isContains(list1, chess)){
                    result = true;
                    break;
                }
            }
        }
        return (!result);
    }

    // VCF
    public boolean vcf(int c) {
//        System.out.println("递归深度：" + count_vcf);
        /*System.out.print("chessman:");
        for (Chess chess :chessman){
            System.out.print("("+ chess.x + "," + chess.y + "," + chess.color +") ");
        }
        System.out.println();*/
//        if (count_vcf == 100000)   //递归深度到达100000时，终止递归
//            return false;
        count_vcf ++;
        find_pattern();
//        System.out.println("laststep:"+laststep.getX()+" "+laststep.getY()+" "+laststep.getColor());
        if (c == 2) {
            for(ArrayList<Chess> cl : changlian) {  //长连
                if (cl.get(0).color == 1) {
                    chess_solve.addAll(chessman);
                    System.out.println("VCF结果：solved，黑棋长连禁手判输.");
                    return true;
                }
            }
            // 四四禁手
            int four_count = 0;
            for (ArrayList<Chess> h4: huo4) {
                if (h4.get(0).color == 1) {
                    if(h4.subList(0,4).contains(laststep)) {
                        four_count ++;
                    }
                }
            }
            for (ArrayList<Chess> m4: mian4) {
                if (m4.get(0).color == 1) {
                    if(m4.subList(0,4).contains(laststep)) {
                        four_count ++;
                    }
                }
            }
            if(four_count >= 2) {
                chess_solve.addAll(chessman);
                System.out.println("VCF结果：solved，黑棋四四禁手判输.");
                return true;
            }

            //三三禁手
            int h3_count = 0;
            for (ArrayList<Chess> h3: huo3) {
                if (h3.get(0).color == 1) {
                    if(h3.subList(0,3).contains(laststep)) {
                        h3_count ++;
                    }
                }
            }
            if(h3_count >= 2) {
                chess_solve.addAll(chessman);
                System.out.println("VCF结果：solved，黑棋三三禁手判输.");
                return true;
            }
        }

        // 活四
        for(ArrayList<Chess> h4 : huo4) {  //活四
            if (h4.get(0).color == 3 - c){
//                System.out.println("VCF结果：no solution.");
                return false;
            }
            else if (h4.get(0).color == c) {
                System.out.println("VCF结果：solved.");
                chess_solve.addAll(chessman);
                return true;
            }
        }
        //眠四
        for(ArrayList<Chess> m4 : mian4) {
            if (m4.get(0).color == 3 - c) {  //对方眠四
                ArrayList<Chess> cv = new ArrayList<>();   //可使我方眠三和活三的成四点
                List<List<Chess>> selfmian3 = new ArrayList<>();   //我方眠三
                List<List<Chess>> selfhuo3 = new ArrayList<>();  //我方活三
                // 眠三
                for (ArrayList<Chess> list : mian3) {  //筛选我方眠三
                    if (list.get(0).color == c) {
                        selfmian3.add(list);
                    }
                }
                for (List<Chess> list : selfmian3) {    //获取最后两位solution点
                    int length = list.size();
                    cv.addAll(list.subList(length - 2,length));
                }
                //活三
                for (ArrayList<Chess> list : huo3) {  //筛选我方活三
                    if (list.get(0).color == c) {
                        selfhuo3.add(list);
                    }
                }
                for (List<Chess> list : selfhuo3) {
                    int length = list.size();
//                    cv.addAll(list.subList(length - 2,length));
                    cv.addAll(list.subList(length - 3,length));   //修改
                }

                //判断是否可以在堵对方的眠四的同时使我方的三成四
                int tx = m4.get(m4.size() - 1).x, ty = m4.get(m4.size() - 1).y;  //tx、ty为堵对方眠四成五点
                Chess tempChess = new Chess(tx, ty, c);
                boolean flag = false;
                for(Chess chess : cv) {
                    if (chess.x == tx && chess.y == ty){
                        flag = true;
                        break;
                    }
                }
                if (!flag) {   //如果堵住对方的同时没有形成我方的四，则返回false，因为走棋过程中冲四才算VCF，堵该点没有达到冲四的目的
                    return false;
                }


                chessman.add(tempChess);
                laststep = tempChess;
                boolean result = vcf(c);
                chessman.remove(tempChess);
                return result;
            }
            if (m4.get(0).color == c) {  //我方有眠四
                int tx = m4.get(m4.size() - 1).x, ty = m4.get(m4.size() - 1).y;
                Chess tempChess = new Chess(tx, ty, 3 - c);
                chessman.add(tempChess);  //模拟对方堵我方眠四，进入下一次递归
                laststep = tempChess;
                boolean result = vcf(c);
                chessman.remove(tempChess);
                return result;
            }
        }
        // 活三
        for(ArrayList<Chess> h3 : huo3) {
            if (h3.get(0).color == c) {
                System.out.println("VCF结果：solved.");
                chess_solve.addAll(chessman);
                return true;
            }
        }
        // 我方眠三
        List<List<Chess>> selfmian3 = new ArrayList<>();

        int size = 0;
        for (ArrayList<Chess> list : mian3) {  //筛选我方眠三
            if (list.get(0).color == c) {       //修改：只加入两个
                selfmian3.add(list);
                size ++;
            }
            if (size == 2)
                break;
        }

        for (List<Chess> list : selfmian3) {   //模拟我方活三形成冲四，再模拟对方堵我方冲四
            int length = list.size();
            list = list.subList(length - 2,length);
            int tx1 = list.get(0).x, ty1 = list.get(0).y;  //眠三的两个成四点
            int tx2 = list.get(1).x, ty2 = list.get(1).y;
            Chess tempChess1 = new Chess(tx1, ty1, c);
            Chess tempChess2 = new Chess(tx2, ty2, 3 - c);
            chessman.add(tempChess1);
            chessman.add(tempChess2);
            laststep = tempChess2;
            boolean result = vcf(c);
            chessman.remove(tempChess1);
            chessman.remove(tempChess2);
            if (result) {
                return true;
            }

            tempChess1 = new Chess(tx2, ty2, c);   //以不同的落子次序模拟冲四
            tempChess2 = new Chess(tx1, ty1, 3 - c);
            chessman.add(tempChess1);
            chessman.add(tempChess2);
            laststep = tempChess2;
            result = vcf(c);
            chessman.remove(tempChess1);
            chessman.remove(tempChess2);
            if (result) {
                return true;
            }
        }
        return false; //待确定
    }

    //传入的参数表示寻找VCF的下棋方 1:黑 2:白，返回的是除去历史记录需新下的子
    public ArrayList<Chess> find_solution(int c) {
        Stack<Chess> history = GobangPanel.history;
//        System.out.println(history);
        System.out.println("VCF查找中...");
        chessman.clear();
        chess_solve.clear();
        for (int i = 0; i < history.size(); i++) {  //chessman初始化，即拷贝一份落子记录数据
            chessman.add(new Chess(history.get(i).x,(16 - history.get(i).y),history.get(i).color));
        }

        count_vcf = 0;
        laststep = chessman.get(chessman.size() - 1);

        ArrayList<Chess> result = new ArrayList<>();
        long startTime = System.currentTimeMillis();
        if (vcf(c)) {
            long endTime = System.currentTimeMillis();
            System.out.println("运行总时间：" + (double)((endTime - startTime) / 1000) + "s");
            System.out.println("递归总次数：" + count_vcf);
            for (int i = history.size(); i < chess_solve.size(); i++) {
                result.add(new Chess(chess_solve.get(i).x, (16 - chess_solve.get(i).y), chess_solve.get(i).color));
            }
            System.out.println("VCF解：");
            for (Chess chess : result)
                System.out.println(chess.x + " " + chess.y + " " + chess.color);
//            System.out.println("historySize：" + history.size());
//            System.out.println("chess_solveSize：" + chess_solve.size());
//            System.out.println("解Size：" + result.size());
        } else {
            System.out.println("VCF结果：no solution");
        }

        return result;
    }

    public static void main(String[] args) {
        VCF vcf = new VCF();
        vcf.find_solution(1);
    }

}
