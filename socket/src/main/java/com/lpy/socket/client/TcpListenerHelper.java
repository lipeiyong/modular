package com.lpy.socket.client;


import java.util.HashSet;
import java.util.Set;

/**
 * @author lipeiyong
 * @since 2023/07/05 16:17
 */
class TcpListenerHelper<T extends TcpMessageListener> {


    private final Set<T> listeners = new HashSet<>();

    synchronized final void registerListener(T tcpListener) {
        listeners.add(tcpListener);
    }

    synchronized final void unRegisterListener(T tcpListener) {
        listeners.remove(tcpListener);
    }

    final void callOpen() {
        for (T listener : listeners) {
            if (listener instanceof TcpListener) {
                ((TcpListener) listener).onOpen();
            }
        }
    }

    final void callClose() {
        for (T listener : listeners) {
            if (listener instanceof TcpListener) {
                ((TcpListener) listener).onClose();
            }
        }
    }

    final void callError(Throwable e) {
        for (T listener : listeners) {
            if (listener instanceof TcpListener) {
                ((TcpListener) listener).onError(e);
            }
        }
    }

    final void callMessage(String msg) {
        for (T listener : listeners) {
            listener.onMessage(msg);
        }
    }
}
