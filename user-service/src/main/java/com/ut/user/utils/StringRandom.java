package com.ut.user.utils;

import java.util.Random;

/**
 * Created by shindo on 2019/1/13.
 */
public class StringRandom {
    //生成随机数字和字母,
    public static String getStringRandom(int length) {

        String val = "";
        Random random = new Random();
        //length为几位密码
        for(int i = 0; i < length; i++) {
            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
            //输出字母还是数字
            if( "char".equalsIgnoreCase(charOrNum) ) {
                //输出是大写字母还是小写字母
                int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;
                val += (char)(random.nextInt(26) + temp);
            } else if( "num".equalsIgnoreCase(charOrNum) ) {
                val += String.valueOf(random.nextInt(10));
            }
        }
        return val;
    }

    //随机不重复的6-8位
    public static int card(){
        int[] array = {0,1,2,3,4,5,6,7,8,9};
        Random rand = new Random();
        for (int i = 10; i > 1; i--) {
            int index = rand.nextInt(i);
            int tmp = array[index];
            array[index] = array[i - 1];
            array[i - 1] = tmp;
        }
        int result = 0;
        for(int i = 0; i < 6; i++){
            result = result * 10 + array[i];
        }
        return result;
    }

    public static String getRandom(){
//         48-57 65-90 97-122
        StringBuffer id=new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < 8; i++) {
            char s = 0;
            int j=random.nextInt(3) % 4;
            switch (j) {
                case 0:
                    //随机生成数字
                    s = (char) (random.nextInt(57) % (57 - 48 + 1) + 48);
                    break;
                case 1:
                    //随机生成大写字母
                    s = (char) (random.nextInt(90) % (90 - 65 + 1) + 65);
                    break;
                case 2:
                    //随机生成小写字母
                    s = (char) (random.nextInt(122) % (122 - 97 + 1) + 97);
                    break;
            }
            id.append(s);
        }
        return id.toString();
    }

}
