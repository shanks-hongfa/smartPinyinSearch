package shanks.yanjun.com.smartpinyinsearch.lib;

import android.support.v4.util.LruCache;
import android.util.Log;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * 智能搜索/超级搜索 支持拼音前缀和中文混合搜索
 * 
 * @author hongfa.yy
 * @date 2012-8-19下午9:29:19
 */
public class SmartSearchUtil {
	static HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
	static {
		format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		format.setVCharType(HanyuPinyinVCharType.WITH_V);
	}
    private static boolean regexCacheMode=true;
    public static void setRegexCacheMode(boolean open){
        regexCacheMode=open;
    }

	public static boolean match(String regex, String name) {

        try {
            regex = toLower(regex);
            name = toLower(name);

            if(!regexCacheMode)
                return search(regex, name, 0, regex);

            LruCache<String,Byte> map = resultMap.get(name);

            if (map == null) {
                map = new LruCache<String, Byte>(10);
                resultMap.put(name,map);
            }

           if(map.get(regex)==null){
               if(search(regex, name, 0, regex)){
                   map.put(regex, Byte.MIN_VALUE);//代表true
               }else{
                   map.put(regex, Byte.MAX_VALUE);//代表false
               }
            }

            if (map.get(regex) == Byte.MIN_VALUE)
                return true;
            else
                return false;
        } catch (BadHanyuPinyinOutputFormatCombination e) {
//            if (e != null)
//                Log.e("SmartSearchUtil", "SmartSearchUtil call match() is failed" + e.getMessage());
//            else
//                Log.e("SmartSearchUtil", "SmartSearchUtil call match() is failed");
        } catch (Exception e) {
//            if (e != null){
//                e.printStackTrace();
//                Log.e("SmartSearchUtil", "SmartSearchUtil call match() is failed" );
//            }
//            else
//                Log.e("SmartSearchUtil", "SmartSearchUtil call match() is failed");
        }
        return false;
    }
     private static String toLower(String str){
    	 StringBuilder builder=new StringBuilder();
    	 for(int i=0;i<str.length();i++){
    		 builder.append(Character.toLowerCase(str.charAt(i)));
    	 }
    	 return builder.toString();
     }
    private static Map<Character,String[]> pinyinMap=new HashMap<Character, String[]>();
    private static Map<String,LruCache<String,Byte>> resultMap=new HashMap<String, LruCache<String,Byte>>();
	private static boolean search(String r, String name, int index, String regex)
			throws Exception {
		if (r.length() == 0)
			return true;
		if (index > name.length() - 1)
			return false;
//        //匹配表达式的长度
		char ch = name.charAt(index);
        //是汉字   regex是字母
		if (isHanzi(ch)&&isLetter(r.charAt(0))&&(find(ch)!=null)) {
            //ch是汉字
				 String[] pinyins =find(ch);
                 List<String> pinyinSet=new ArrayList<String>();
            //多音字毕竟少数
                if(pinyins.length==1){
                    pinyinSet.add(pinyins[0]);
                }else {
                    for (String pinyin : pinyins)
                        if(!pinyinSet.contains(pinyin))
                            pinyinSet.add(pinyin);// 这里可以做优化

                }

				for (String pinyin : pinyinSet) {
                    int pl=pinyin.length();
                    int rl=r.length();
					int length = pl > rl ? rl: pl;

					for (int i = 0; i < length; i++) {
						if (pinyin.charAt(i) == r.charAt(i)) {
							// /r 结束了 说明完成任务
							if ((i + 1) > rl - 1) {
                              //  Log.i("find",ch+"--"+r.charAt(i));
								return true;
							}
							if (search(r.substring(i + 1), name, index + 1,
									regex)){
                                return true;
                            }
						} else{
                       //    Log.i("SmartSearchUtil","我被 break ----");
                            break;
                        }

					}

				}
				// ///////////////////////
				if (!r.equals(regex)&&index - 1 >= 0 ) {
					char c = name.charAt(index - 1);
					if (Character.isLetter(c)) {
						return search(regex, name, index, regex);
					}
				}
				return search(regex, name, index + 1, regex);
//			}
		} else {
			// not hanzi
			if (ch == r.charAt(0)) {
				if (r.length() == 1)
					return true;
				return search(r.substring(1), name, index + 1, regex);
			} else {
				return search(regex, name, index + 1, regex);
			}
		}

	}

	private static boolean isHanzi(char ch) {
		if (ch >= 0x4e00 && ch <= 0x9fa5)
			return true;
		else
			return false;
	}
    private static boolean isLetter(char ch) {
        if (ch >= 'a' && ch <= 'z')
            return true;
        else
            return false;
    }
    private static String[] find(char ch){
        if(pinyinMap.get(ch)!=null)
            return pinyinMap.get(ch);
        try {
            String[] result= PinyinHelper.toHanyuPinyinStringArray(ch, format);
            pinyinMap.put(ch,result);
            return result;
        } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
            badHanyuPinyinOutputFormatCombination.printStackTrace();
            return new String[]{};
        }
    }

}
