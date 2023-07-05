package com.lpy.socket.server;



import java.util.HashSet;
import java.util.Set;

/**
 * @author lipeiyong
 * @since 2023/07/05 16:17
 */
class TcpServerListenerHelper<T extends TcpServerMessageListener> {

    private final Set<T> listeners = new HashSet<>();

    synchronized final void registerListener(T tcpListener) {
        listeners.add(tcpListener);
    }

    synchronized final void unRegisterListener(T tcpListener) {
        listeners.remove(tcpListener);
    }

    final void callOpen() {
        for (T listener : listeners) {
            if (listener instanceof TcpServerListener) {
                ((TcpServerListener) listener).onOpen();
            }
        }
    }

    final void callClose() {
        for (T listener : listeners) {
            if (listener instanceof TcpServerListener) {
                ((TcpServerListener) listener).onClose();
            }
        }
    }

    final void callError(Throwable e) {
        for (T listener : listeners) {
            if (listener instanceof TcpServerListener) {
                ((TcpServerListener) listener).onError(e);
            }
        }
    }

    final void callClientMessage(Client client, String msg) {
        for (T listener : listeners) {
            listener.onClientMessage(client, msg);
        }
    }

    final void callClientError(String sn, Throwable t) {
        for (T listener : listeners) {
            listener.onClientError(sn, t);
        }
    }
}
