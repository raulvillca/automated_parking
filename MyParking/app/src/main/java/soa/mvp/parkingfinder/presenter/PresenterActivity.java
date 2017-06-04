package soa.mvp.parkingfinder.presenter;

/**
 * Created by raulvillca on 16/5/17.
 */

public interface PresenterActivity {
    interface View {
        void start();
        void request();
        void response();
    }

    interface Model {
        void request();
        void response();
        void customResponse();
    }
}
