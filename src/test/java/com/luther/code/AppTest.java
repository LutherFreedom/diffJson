package com.luther.code;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Unit test for simple App.
 */
public class AppTest {

    String joaStr;
    String jobStr;
    File resultFile;
    BufferedWriter bw;


    @Before
    public void initJSON() throws IOException {
        String pre = "D://workspace//diffJSON//src//main//source//";
        resultFile = new File(pre + "result.txt");
        bw = new BufferedWriter(new FileWriter(resultFile));
        File filea = new File(pre + "a.json");
        FileReader fr = new FileReader(filea);
        BufferedReader br = new BufferedReader(fr);
        StringBuffer sb = new StringBuffer();
        String line = "";
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        joaStr = sb.toString();
        File fileb = new File(pre + "b.json");
        FileReader frb = new FileReader(fileb);
        BufferedReader brb = new BufferedReader(frb);
        StringBuffer sbb = new StringBuffer();
        String lineb = "";
        while ((lineb = brb.readLine()) != null) {
            sbb.append(lineb);
        }
        jobStr = sbb.toString();
    }

    @Test
    public void test() throws IOException {
        compareJSON(joaStr, jobStr);
    }

    @After
    public void destroy() throws IOException {
        bw.flush();
        bw.close();
    }

    private void compareJSON(String source, String target) throws IOException {
        JSON.isValidArray(source);
        if (JSONArray.isValidArray(source) && JSONArray.isValidArray(target)) {
            compareJSON(JSONArray.parseArray(source), JSONArray.parseArray(target), "", "");
        } else if (JSONObject.isValidObject(source) && JSONObject.isValidObject(target)) {
            compareJSON(JSONObject.parseObject(source), JSONObject.parseObject(target), "", "");
        } else {
            write(bw, MessageTemplate.DIFFTYPE.getTempldate());
        }
    }

    private void compareJSON(JSONArray source, JSONArray target, String key, String dir) throws IOException {
        for (int i = 0; i < source.size(); i++) {
            Object temp = source.get(i);
            if (i + 1 > target.size()) {
                write(bw, String.format(MessageTemplate.MISSROW.getTempldate(), dir, i, temp));
                continue;
            }
            compareJSON(source.get(i), target.get(i), key, dir + ">" + "row " + i);
        }
        if (source.size() < target.size()) {
            for (int i = source.size(); i < target.size(); i++) {
                write(bw, String.format(MessageTemplate.MOREROW.getTempldate(), dir, i, target.get(i)));

            }
        }
    }

    private void compareJSON(JSONObject source, JSONObject target, String key, String dir) throws IOException {
        Iterator<String> keys = source.keySet().iterator();
        Set<String> targetSet = new HashSet<>(target.keySet());
        while (keys.hasNext()) {
            key = keys.next();
            targetSet.remove(key);
            compareJSON(source.get(key), target.get(key), key, dir + ">" + key);
        }

        if (targetSet.size() > 0) {
            Iterator<String> targetKeys = source.keySet().iterator();
            while (targetKeys.hasNext()) {
                String targetKey = targetKeys.next();
                write(bw, String.format(MessageTemplate.MOREKEY.getTempldate(), dir, key, target.get(targetKey)));
            }
        }

    }

    private void compareJSON(Object source, Object target, String key, String dir) throws IOException {
        if (target == null) {
            write(bw, String.format(MessageTemplate.MISSKEY.getTempldate(), dir, key));
            return;
        }
        if (source instanceof JSONObject) {
            if (!(target instanceof JSONObject)) {
                throw new IllegalArgumentException(String.format(MessageTemplate.CASTEXEOBJ.getTempldate(), dir));
            }
            compareJSON((JSONObject) source, (JSONObject) target, key, dir);
        } else if (source instanceof JSONArray) {
            if (!(target instanceof JSONArray)) {
                throw new IllegalArgumentException(String.format(MessageTemplate.CASTEXEARR.getTempldate(), dir));
            }
            compareJSON((JSONArray) source, (JSONArray) target, key, dir);
        } else {
            compareJSON(source.toString(), target.toString(), key, dir);
        }
    }

    private void compareJSON(String source, String target, String key, String dir) throws IOException {
        if (!source.equals(target)) {
            write(bw, String.format(MessageTemplate.DIFF.getTempldate(), dir, key, source, target));
        }
    }

    private void write(BufferedWriter bw, String content) throws IOException {
        bw.newLine();
        bw.write(content);
    }

    public enum MessageTemplate {
        DIFFTYPE("source and target are not same type"),
        MOREKEY("DIR %s target more one key %s, value is %s"),
        MOREROW("DIR %s target more one row %s, data is %s"),
        MISSKEY("DIR %s target miss key %s"),
        MISSROW("DIR %s target miss row %s, data is %s"),
        DIFF("DIR %s KEY %s is different; source is %s, target is %s"),
        CASTEXEOBJ("DIR %s target can not cast JSONObject"),
        CASTEXEARR("DIR %s target can not cast JSONArray");
        private String templdate;

        MessageTemplate(String templdate) {
            this.templdate = templdate;
        }

        public String getTempldate() {
            return templdate;
        }
    }
}
