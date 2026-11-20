package ua.coworking.controller;
import ua.coworking.model.*;
import ua.coworking.view.*;
import java.util.Objects;
/** Контролер компонують вручну з уже підготовленою моделлю Main. */
public final class CoworkingController {
    private final Booking model;
    private final CoworkingWebView view;
    public CoworkingController(Booking model,CoworkingWebView view) {this.model=Objects.requireNonNull(model);this.view=Objects.requireNonNull(view);}
    public WebResponse read() {return view.read(model.read());}
    public WebResponse create(String id,String resident,String space,String start,String end) {
        return view.result(model.create(new TimeInterval(id,resident,space,start,end)),true);
    }
    public WebResponse update(String id,String bodyId,String resident,String space,String start,String end) {
        return view.result(model.update(id,new TimeInterval(bodyId,resident,space,start,end)),false);
    }
    public WebResponse delete(String id) {return view.result(model.delete(id),false);}
    public WebResponse invalid() {return view.invalid();}
    public WebResponse error() {return view.error();}
}
