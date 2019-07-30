package com.luther.code;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * All rights Reserved, Designed by Luther
 *
 * @auther: Luther
 * @createdTime: 2019/7/30 18:04
 * @version： 0.0.1
 * @copyRight: @2019
 * TODO:
 */
public class DiffJson {

    private String joaStr;
    private String jobStr;
    private File resultFile;
    private BufferedWriter bw;

    public static void diffJson(String sourceFilePath, String targetFilePath, String resultFilePath) throws IOException {
        DiffJson diffJson = new DiffJson();
        diffJson.init(sourceFilePath, targetFilePath, resultFilePath);
        diffJson.doDiff();
        diffJson.destroy();
    }

    private void init(String sourceFilePath, String targetFilePath, String resultFilePath) throws IOException {

        File sourceFile = new File(sourceFilePath);
        joaStr = parseFile2Str(sourceFile);

        File targetFile = new File(targetFilePath);
        jobStr = parseFile2Str(targetFile);

        resultFile = new File(resultFilePath);
        if (!resultFile.exists()) {
            resultFile.createNewFile();
        }
        bw = new BufferedWriter(new FileWriter(resultFile));
        bw.newLine();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        bw.append(LocalDateTime.now().format(formatter));
    }

    private void doDiff() throws IOException {
        compareJSON(joaStr, jobStr);
    }

    private void destroy() throws IOException {
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
            write(bw, MessageTemplate.DIFFTYPE.getTemplate());
        }
    }

    private void compareJSON(JSONArray source, JSONArray target, String key, String dir) throws IOException {
        for (int i = 0; i < source.size(); i++) {
            Object temp = source.get(i);
            if (i + 1 > target.size()) {
                write(bw, String.format(MessageTemplate.MISSROW.getTemplate(), dir, i, temp));
                continue;
            }
            compareJSON(source.get(i), target.get(i), key, dir + ">" + "row " + i);
        }
        if (source.size() < target.size()) {
            for (int i = source.size(); i < target.size(); i++) {
                write(bw, String.format(MessageTemplate.MOREROW.getTemplate(), dir, i, target.get(i)));

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
                write(bw, String.format(MessageTemplate.MOREKEY.getTemplate(), dir, key, target.get(targetKey)));
            }
        }

    }

    private void compareJSON(Object source, Object target, String key, String dir) throws IOException {
        if (target == null) {
            write(bw, String.format(MessageTemplate.MISSKEY.getTemplate(), dir, key));
            return;
        }
        if (source instanceof JSONObject) {
            if (!(target instanceof JSONObject)) {
                throw new IllegalArgumentException(String.format(MessageTemplate.CASTEXEOBJ.getTemplate(), dir));
            }
            compareJSON((JSONObject) source, (JSONObject) target, key, dir);
        } else if (source instanceof JSONArray) {
            if (!(target instanceof JSONArray)) {
                throw new IllegalArgumentException(String.format(MessageTemplate.CASTEXEARR.getTemplate(), dir));
            }
            compareJSON((JSONArray) source, (JSONArray) target, key, dir);
        } else {
            compareJSON(source.toString(), target.toString(), key, dir);
        }
    }

    private void compareJSON(String source, String target, String key, String dir) throws IOException {
        if (!source.equals(target)) {
            write(bw, String.format(MessageTemplate.DIFF.getTemplate(), dir, key, source, target));
        }
    }

    private void write(BufferedWriter bw, String content) throws IOException {
        bw.newLine();
        bw.append(content);
    }

    private String parseFile2Str(String filePath) throws IOException {
        return parseFile2Str(new File(filePath));
    }
    private String parseFile2Str(File file) throws IOException {
        if (!file.exists()) {
            throw new FileNotFoundException(file + "NOT FOUND");
        }
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        StringBuffer sb = new StringBuffer();
        String line = "";
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
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
        private String template;

        MessageTemplate(String template) {
            this.template = template;
        }

        public String getTemplate() {
            return template;
        }
    }
}
