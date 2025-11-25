package code.uz.smartnotesbackned.util;

public class PageUtil {
    public static int getPage(int page) {
        return page <= 0 ? 1 : page - 1;
    }
}
