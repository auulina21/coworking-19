package ua.coworking;
import org.junit.jupiter.api.Test;
import ua.coworking.model.*;
import ua.coworking.infrastructure.SqliteTimeIntervalStorage;
import java.sql.*;
import java.nio.file.*;
import static org.junit.jupiter.api.Assertions.*;
class HtmlExportTest {
 @Test void exportsActualReopenedRowsAndEscapesHtml() throws Exception {
  Path db=Files.createTempFile("coworking-html-", ".db");Path html=Files.createTempFile("coworking-html-", ".html");
  try {
   try(Connection c=DriverManager.getConnection("jdbc:sqlite:"+db)) {
    SqliteTimeIntervalStorage storage=new SqliteTimeIntervalStorage(c);
    storage.save(new TimeInterval("HTML","<script>&\"","Зал <A>","2026-12-01T09:00","2026-12-01T10:00"));
   }
   try(Connection c=DriverManager.getConnection("jdbc:sqlite:"+db)) {
    java.util.List<TimeInterval> rows=new SqliteTimeIntervalStorage(c).findAll();
    assertEquals(1,rows.size());new StaticHtmlExporter().write(rows,html);
    String page=Files.readString(html);assertTrue(page.contains("HTML"));
    assertTrue(page.contains("&lt;script&gt;&amp;&quot;"));assertTrue(page.contains("Зал &lt;A&gt;"));
    assertTrue(page.contains(rows.getFirst().start()));assertTrue(page.contains(rows.getFirst().end()));
    assertFalse(page.contains("<script>"));
   }
  } finally {Files.deleteIfExists(html);Files.deleteIfExists(db);}
 }
}
