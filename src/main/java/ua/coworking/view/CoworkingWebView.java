package ua.coworking.view;
import ua.coworking.model.*;
import java.util.*;
/** Переклад доменних результатів на транспортні коди. Бізнес-правил тут немає. */
public final class CoworkingWebView {
    public WebResponse read(List<TimeInterval> items) {return new WebResponse(200,Map.of("items",items));}
    public WebResponse result(OperationResult result,boolean created) {
        int status=switch(result.kind()) {
            case SUCCESS -> created?201:200;
            case INVALID -> 400;
            case NOT_FOUND -> 404;
            case FORBIDDEN -> 409;
            case ERROR -> 500;
        };
        return new WebResponse(status,Map.of("message",result.message()));
    }
    public WebResponse invalid() {return result(new OperationResult(ResultKind.INVALID,"Некоректний JSON-запит."),false);}
    public WebResponse error() {return result(new OperationResult(ResultKind.ERROR,"Помилка обробки запиту. Спробуйте ще раз."),false);}
}
