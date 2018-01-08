package com.gionee.client.business.push;

import java.util.HashSet;

public class ReceiverNotifier {
    private static ReceiverNotifier sSelf = null;
    private HashSet<RidObserver> mObservers = new HashSet<RidObserver>(1);

    public static ReceiverNotifier getInstance() {
        if (sSelf == null) {
            sSelf = new ReceiverNotifier();
        }

        return sSelf;
    }

    public void notifyRidGot(String rid) {
        for (RidObserver observer : mObservers) {
            observer.ridGot(rid);
        }
    }

    public void notifyRidCanceled(String rid) {
        for (RidObserver observer : mObservers) {
            observer.ridCanceled(rid);
        }
    }

    public void notifyRidError(String error) {
        for (RidObserver observer : mObservers) {
            observer.ridError(error);
        }
    }

    public void notifyMessage(String message) {
        for (RidObserver observer : mObservers) {
            observer.messageReceived(message);
        }
    }

    public void registerRidObserver(RidObserver observer) {
        mObservers.add(observer);
    }

    public void unRegisterRidObserver(RidObserver observer) {
        mObservers.remove(observer);
    }

    public interface RidObserver {
        void ridGot(String rid);

        void ridCanceled(String rid);

        void ridError(String error);

        void messageReceived(String message);
    }

}
