package ua.coworking.infrastructure;
import ua.coworking.controller.CoworkingController;
import ua.coworking.view.WebResponse;
import io.javalin.Javalin;
import io.javalin.http.*;
import io.javalin.json.JavalinJackson;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
/** Єдине місце залежності від Javalin і JSON-бібліотеки. */
public final class JavalinWebServer implements WebServer {
    public record Input(String id,String resident,String space,String start,String end) {}
    private void send(Context ctx,WebResponse response) {ctx.status(response.status()).json(response.body());}
    @Override public void start(CoworkingController controller,int port) {
        ObjectMapper mapper=new ObjectMapper().enable(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_TRAILING_TOKENS);
        Javalin app=Javalin.create(config->{
            config.staticFiles.add("/public");config.jsonMapper(new JavalinJackson(mapper,false));
            config.showJavalinBanner=false;
        });
        app.get("/api/coworking",ctx->send(ctx,controller.read()));
        app.post("/api/coworking",ctx->{Input v=mapper.readValue(ctx.body(),Input.class);send(ctx,v==null?controller.invalid():controller.create(v.id(),v.resident(),v.space(),v.start(),v.end()));});
        app.put("/api/coworking/{id}",ctx->{Input v=mapper.readValue(ctx.body(),Input.class);send(ctx,v==null?controller.invalid():controller.update(ctx.pathParam("id"),v.id(),v.resident(),v.space(),v.start(),v.end()));});
        app.delete("/api/coworking/{id}",ctx->send(ctx,controller.delete(ctx.pathParam("id"))));
        app.exception(BadRequestResponse.class,(ex,ctx)->send(ctx,controller.invalid()));
        app.exception(JsonProcessingException.class,(ex,ctx)->send(ctx,controller.invalid()));
        app.exception(Exception.class,(ex,ctx)->{ex.printStackTrace();send(ctx,controller.error());});
        Runtime.getRuntime().addShutdownHook(new Thread(app::stop));
        try {app.start("127.0.0.1",port);}
        catch(RuntimeException ex) {app.stop();throw ex;}
    }
}
