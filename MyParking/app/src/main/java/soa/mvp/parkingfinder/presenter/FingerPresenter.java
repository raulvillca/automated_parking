package soa.mvp.parkingfinder.presenter;

/**
 * Created by raulvillca on 16/5/17.
 */

public interface FingerPresenter {
    interface View <T> {
        void getFingerResult(T t);
    }

    interface Model <T, O> {
        void putModel(T t);
        void getModel(O o);
    }
}
