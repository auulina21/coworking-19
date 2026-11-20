package ua.coworking;
import ua.coworking.model.Booking;
import ua.coworking.infrastructure.*;
import ua.coworking.controller.CoworkingController;
import ua.coworking.view.CoworkingWebView;
import com.google.inject.*;
import java.sql.Connection;
public final class Main {
    public static void main(String[] args) {
        System.setOut(new java.io.PrintStream(System.out,true,java.nio.charset.StandardCharsets.UTF_8));
        Injector injector=Guice.createInjector(new CoworkingModule());
        Booking model=injector.getInstance(Booking.class);
        CoworkingController controller=new CoworkingController(model,new CoworkingWebView());
        Runtime.getRuntime().addShutdownHook(new Thread(()->{try{injector.getInstance(Connection.class).close();}catch(Exception ignored){}}));
        injector.getInstance(WebServer.class).start(controller,8080);
        System.out.println("Коворкінг запущено: http://localhost:8080/ . Зупинка: Ctrl+C.");
    }
}
