package ua.coworking;
import ua.coworking.model.TimeInterval;
import java.nio.file.*;
import java.io.IOException;
import java.util.List;
/** Експорт лише фактично прочитаних із БД рядків. */
public final class StaticHtmlExporter {
    private String escape(String s) { return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;").replace("\"","&quot;"); }
    public void write(List<TimeInterval> items,Path path) throws IOException {
        StringBuilder h=new StringBuilder("<!doctype html><html lang='uk'><meta charset='UTF-8'><title>Коворкінг — дані SQLite</title><style>body{font:18px Arial;margin:48px;color:#172238}table{border-collapse:collapse}td,th{padding:14px;border-bottom:1px solid #bcc5d3}th{text-align:left;background:#edf2fa}</style><h1>Інтервали бронювання коворкінгу</h1><p>Практична робота 3. Дані прочитані з SQLite.</p><table><tr><th>ID</th><th>Резидент</th><th>Місце</th><th>Початок</th><th>Кінець</th></tr>");
        for(TimeInterval t:items) { h.append("<tr>");for(String v:List.of(t.id(),t.resident(),t.space(),t.start(),t.end()))h.append("<td>").append(escape(v)).append("</td>");h.append("</tr>"); }
        h.append("</table><p>Рядків: ").append(items.size()).append("</p></html>");Files.writeString(path,h.toString());
    }
}
