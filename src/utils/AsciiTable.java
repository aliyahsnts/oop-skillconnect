package utils;

import java.util.List;
import java.util.function.Function;

public final class AsciiTable {
    private AsciiTable() {} // static utility

    /* ----------------------------------------------
       One-liner: print any List<T> as a boxed table
       ---------------------------------------------- */
    public static <T> void print(List<T> rows,
                                 String[] headers,
                                 int[] widths,
                                 Function<T, String[]> extractor) {

        // top border
        System.out.println(border(widths));

        // header row
        String[] h = new String[headers.length];
        for (int i = 0; i < headers.length; i++)
            h[i] = pad(headers[i], widths[i]);
        System.out.println(row(h, widths));

        // separator
        System.out.println(border(widths));

        // data rows
        for (T t : rows) {
            String[] cells = extractor.apply(t);
            for (int i = 0; i < cells.length; i++)
                cells[i] = pad(cells[i], widths[i]);
            System.out.println(row(cells, widths));
        }

        // bottom border
        System.out.println(border(widths));
    }

    /* ---------------- helpers ---------------- */
    private static String border(int[] w) {
        StringBuilder b = new StringBuilder("+");
        for (int n : w) b.append("-".repeat(n)).append("+");
        return b.toString();
    }
    private static String row(String[] cells, int[] w) {
        StringBuilder b = new StringBuilder("|");
        for (int i = 0; i < cells.length; i++)
            b.append(cells[i]).append("|");
        return b.toString();
    }
    private static String pad(String s, int width) {
        if (s == null) s = "";
        return s.length() > width ? s.substring(0, width) 
                                  : String.format("%-" + width + "s", s);
    }
}