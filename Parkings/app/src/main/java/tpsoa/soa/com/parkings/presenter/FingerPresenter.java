package tpsoa.soa.com.parkings.presenter;

public interface FingerPresenter {
    interface View <T> {
        void getFingerResult(T t);
    }

    interface Model <T, O> {
        void putModel(T t);
        void getModel(O o);
    }
}
