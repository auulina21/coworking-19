package ua.coworking;
import ua.coworking.model.*;
import ua.coworking.infrastructure.CoworkingModule;
import com.google.inject.*;
import java.sql.Connection;
import java.nio.file.Path;
/** Детермінований сценарій для повторюваної перевірки всіх ролей. */
public final class Main {
    public static void main(String[] args) throws Exception {
        System.setOut(new java.io.PrintStream(System.out,true,java.nio.charset.StandardCharsets.UTF_8));
        Injector injector=Guice.createInjector(new CoworkingModule());
        try (Connection connection=injector.getInstance(Connection.class)) {
        System.out.println("=== Коворкінг: імітаційний прототип, варіант 19 ===");
        Booking booking = injector.getInstance(Booking.class);
        Resident resident=new Resident("R19", "Олена");
        SpaceManager manager=new SpaceManager("M19", "Андрій");
        Accountant accountant=new Accountant("A19", "Марія");
        System.out.println(manager.role()+": "+manager.manageLayout("4 робочі місця, переговорна A"));
        System.out.println(manager.manageLayout(""));
        System.out.println(accountant.invoice(booking));
        if(booking.getIntervals().isEmpty())System.out.println(booking.confirm());
        else System.out.println("Прочитано наявні інтервали; DRAFT збережено для перевірки дублів.");
        System.out.println(resident.book(booking,new TimeInterval("T19","Олена","Переговорна A","2026-12-01T09:00","2026-12-01T11:00")));
        System.out.println(resident.book(booking,new TimeInterval("T19","Олена","Місце 1","2026-12-01T12:00","2026-12-01T13:00")));
        System.out.println(resident.book(booking,new TimeInterval("OVERLAP","Олена","переговорна a","2026-12-01T10:00","2026-12-01T12:00")));
        System.out.println(resident.book(booking,new TimeInterval("BAD","Олена","Місце 2","2026-12-01T12:00","2026-12-01T11:00")));
        System.out.println(resident.book(booking,"Переговорна A","2026-12-01T11:00","2026-12-01T12:00"));
        System.out.println(resident.book(booking,new TimeInterval("T20","Олена","Місце 1","2026-12-01T09:00","2026-12-01T10:00")));
        System.out.println(resident.changeTariff("FLEX"));
        System.out.println(resident.changeTariff("UNKNOWN"));
        System.out.println(booking.confirm());
        System.out.println(accountant.invoice(booking));
        System.out.println(booking.add(new TimeInterval("LATE","Олена","Місце 3","2026-12-01T13:00","2026-12-01T14:00")));
        System.out.println(booking.confirm());
        System.out.println("Прийнято інтервалів: "+booking.getIntervals().size());
        System.out.println(booking.cancel());
        System.out.println(booking.cancel());
        var items=injector.getInstance(TimeIntervalStorage.class).findAll();
        new StaticHtmlExporter().write(items,Path.of("target/intervals.html"));
        System.out.println("SQLite: "+items.size()+" рядків. HTML: target/intervals.html");
        }
    }
}
