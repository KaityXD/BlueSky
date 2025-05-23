package com.movtery.zalithlauncher.utils.stringutils;

import static android.content.Context.CLIPBOARD_SERVICE;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.Base64;
import android.widget.Toast;

import com.movtery.zalithlauncher.R;
import com.movtery.zalithlauncher.task.TaskExecutors;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.StringJoiner;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    public static String insertSpace(Object prefixString, Object... suffixString) {
        return insertSpace(prefixString == null ? null : prefixString.toString(),
                Arrays.stream(suffixString).map(Object::toString).toArray(String[]::new));
    }

    /**
     * 在字符串之间插入空格
     *
     * @param prefixString 第一个字符串
     * @param suffixString 之后的多个字符串
     * @return 返回插入好空格的字符串 "string1 string2 string3"
     */
    public static String insertSpace(String prefixString, String... suffixString) {
        return insertString(" ", prefixString, suffixString);
    }

    public static String insertNewline(Object prefixString, Object... suffixString) {
        return insertNewline(prefixString == null ? null : prefixString.toString(),
                Arrays.stream(suffixString).map(Object::toString).toArray(String[]::new));
    }

    /**
     * 在字符串之间插入换行符
     *
     * @param prefixString 第一个字符串
     * @param suffixString 之后的多个字符串
     * @return 返回插入好换行符的字符串
     */
    public static String insertNewline(String prefixString, String... suffixString) {
        return insertString("\r\n", prefixString, suffixString);
    }

    public static String insertString(String stringToInsert, String prefixString, String... suffixString) {
        StringJoiner stringJoiner = new StringJoiner(stringToInsert);
        if (prefixString != null) {
            stringJoiner.add(prefixString);
        }
        for (String string : suffixString) {
            stringJoiner.add(string);
        }

        return stringJoiner.toString();
    }

    public static String shiftString(String input, ShiftDirection direction, int shiftCount) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        //确保位移个数在字符串长度范围内
        int length = input.length();
        shiftCount = shiftCount % length;
        if (shiftCount == 0) {
            return input;
        }

        switch (direction) {
            case LEFT:
                return input.substring(shiftCount) + input.substring(0, shiftCount);
            case RIGHT:
                return input.substring(length - shiftCount) + input.substring(0, length - shiftCount);
            default:
                throw new IllegalArgumentException("Invalid shift direction: " + direction);
        }
    }

    /**
     * @return 检查字符串是否为null，如果是那么则返回""，如果不是，则返回字符串本身
     */
    public static String getStringNotNull(String string) {
        if (string == null) return "";
        else return string;
    }

    /**
     * 检查一段字符串内是否含有中文字符（中文标点）
     * @param str 检查的字符
     * @return 是否带有中文
     */
    public static boolean containsChinese(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }

        Pattern pattern = Pattern.compile("[一-龥|！，。（）《》“”？：；【】]");
        Matcher matcher = pattern.matcher(str);
        return matcher.find();
    }

    public static String formattingTime(String time) {
        int T = time.indexOf('T');
        int Z = time.indexOf('Z');
        if (T == -1 || Z == -1) return time;
        return StringUtils.insertSpace(time.substring(0, T), time.substring(T + 1, Z));
    }

    public static String formatDate(Date date, Locale locale, TimeZone timeZone) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", locale);
        formatter.setTimeZone(timeZone);
        return formatter.format(date);
    }

    public static String markdownToHtml(String markdown) {
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(document);
    }

    public static void copyText(String label, String text, Context context) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
        clipboardManager.setPrimaryClip(ClipData.newPlainText(label, text));
        TaskExecutors.runInUIThread(() -> Toast.makeText(context, context.getString(R.string.generic_copied), Toast.LENGTH_SHORT).show());
    }

    public static String decodeBase64(String rawValue) {
        byte[] decodedBytes = Base64.decode(rawValue, Base64.DEFAULT);
        return new String(decodedBytes, StandardCharsets.UTF_8);
    }
}
