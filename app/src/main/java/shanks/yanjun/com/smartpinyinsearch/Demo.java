package shanks.yanjun.com.smartpinyinsearch;

import shanks.yanjun.com.smartpinyinsearch.lib.SmartSearchUtil;

/**
 * Created by shanksYao on 11/18/14.
 * 这是一个SmartSearchUtil的demo
 */
public class Demo {

    public static void main(String[] args) {
        String name="香克斯-红发";
        SmartSearchUtil.setRegexCacheMode(false);
        //可以选择true 也可以选择false,默认为true，表示每次都会缓存查询的结果，提高第二次的查询效率。
        //推荐使用true
        assertEquals(true, SmartSearchUtil.match("xks", name));
        assertEquals(true, SmartSearchUtil.match("hf", name));
        assertEquals(true, SmartSearchUtil.match("-红f", name));
        assertEquals(true, SmartSearchUtil.match("-红fa", name));
        assertEquals(true, SmartSearchUtil.match("hongfa", name));
        assertEquals(true, SmartSearchUtil.match("hofa", name));
    }

    public static void assertEquals(boolean b,boolean b$){
        if(b==b$){
            System.out.println("-------"+true+"-------");
        }else{
            System.out.println("-------"+false+"-------");
        }

    }

}
