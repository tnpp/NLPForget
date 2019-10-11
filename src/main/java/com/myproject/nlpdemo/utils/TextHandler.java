package com.myproject.nlpdemo.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class TextHandler {
	public static final String BLANK_SPACE_REGEX = "[　\\s\\r\\n\\\\\\:\\.]+";
	public static final String[] IGNORE_WORDS = {"http", "www", "com", "net", "《", "》"};
	
	
	private final static String[] thesaurus = {"Φ	Φ	φ	Ф", "(	（	【	[	〔", ")	）	】	]	〕", ",	，", "*	×", "mm	㎡	㎜"};
	
	private static String removeIgnoreWords(String str) {
		if (str == null)
			return str;
		for (String word:IGNORE_WORDS) {
			str = StringUtils.replace(str, word, "");
		}
		return str;
	}
	
	/**
	 * 正则表达式转义
	 * @param regex 正则表达式
	 * @return　转义结果
	 */
	public static String escape(String regex) {
		if(regex == null) return regex;
		String[] fbsArr = { "\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|" };
        for(String key : fbsArr) {
            if(regex.contains(key)) {
            	regex = regex.replace(key, "\\" + key);
            }
        }
	    return regex;
	}
	
	private static String creatThesaurusRegex(List<String> words, boolean escape) {
		Collections.sort(words, new Comparator<String>() {
			@Override
			public int compare(String str1, String str2) {
				return str2.length() - str1.length();
			}
		});
		int len = words.size();
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < len; ++i) {
			if(i != (len - 1)) {
				if(escape) {
					sb.append(escape(words.get(i)));
				} else {
					sb.append(words.get(i));
				}
				sb.append("|");
			} else {
				if(escape) {
					sb.append(escape(words.get(i)));
				} else {
					sb.append(words.get(i));
				}
			}
		}
		return sb.toString();
	}
    /**
     * 全角转半角
     * @param input
     * @return
     */
	public static String toDBC(String input) {
        char c[] = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
          if (c[i] == '\u3000') {
            c[i] = ' ';
          } else if (c[i] > '\uFF00' && c[i] < '\uFF5F') {
            c[i] = (char) (c[i] - 65248);

          }
        }
        String returnString = new String(c);
        return returnString;
    }
	/**
	 * 同义词替换, 并进行转小写, 去空白, 去不可见字符, 全角转半角操作
	 * @param text
	 * @return
	 */
	public static String thesaurusMerge(String text) {
		text = removeIgnoreWords(text);
        if(text == null || text.trim().isEmpty()) return text;
        // 加载同义词字典
        Map<String, String> thes = new HashMap<String, String>();
		for(String line : thesaurus) {
			List<String> words = new ArrayList<String>(Arrays.asList(line.split(BLANK_SPACE_REGEX)));
			if(words.size() < 2) continue;
			String mainWord = words.remove(0);
			thes.put(mainWord, creatThesaurusRegex(words, true));
		}
        // 替换同义词
        if(thes == null || thes.isEmpty()) return text;
        for(Map.Entry<String, String> entry : thes.entrySet()) {
        	if(text.matches(".*(" + entry.getValue() + ").*")) {
        		text = text.replaceAll(entry.getValue(), entry.getKey());
        	} 
        }
        text = text.replaceAll(BLANK_SPACE_REGEX, "");
        text = text.replaceAll("\\p{C}", "");
        return toDBC(text).toLowerCase();
    }
	
	public static void main(String[] args) throws Exception {
		String text = " 钢筋	.水泥...《http:\\www.硼酸.com》\\φ8，:Ф16, \\:Ф123, ｄｎ５０[56×500㎡	]，\r\n\n\r（1*2㎜）, \n【33＊mm】,	\raa, ABC ＡＤＢ	ｄａｓｈ	123３４５１";
	    String text2 = thesaurusMerge(text);
		System.out.println("'" + text + "'");
		System.out.println("'" + text2 + "'");
	}

}
