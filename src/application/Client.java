package application;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by ehay@naver.com on 2018-08-22
 * Blog : http://ehay.tistory.com
 * Github : http://github.com/ehayand
 */

public class Client {

    Socket socket;

    public Client(Socket socket) {
        this.socket = socket;
        receive();
    }

    public void receive() {
        Runnable thread = new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        InputStream in = socket.getInputStream();
                        byte[] buffer = new byte[512];
                        int length = in.read(buffer);
                        while (length == -1) throw new IOException();
                        System.out.println("[메세지 수신 성공] "
                                + socket.getRemoteSocketAddress()
                                + ": " + Thread.currentThread().getName());
                        String message = new String(buffer, 0, length, "UTF-8");
                        for (Client client : Controller.clients) {
                            client.send(message);
                        }
                    }
                } catch (Exception e) {
                    try {
                        System.out.println("[메세지 수신 오류] "
                                + socket.getRemoteSocketAddress()
                                + ": " + Thread.currentThread().getName());

                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
            }
        };
        Controller.threadPool.submit(thread);
    }

    public void send(String message) {
        Runnable thread = new Runnable() {
            @Override
            public void run() {
                try {
                    OutputStream out = socket.getOutputStream();
                    byte[] buffer = message.getBytes("UTF-8");
                    out.write(buffer);
                    out.flush();
                } catch (Exception e) {
                    try {
                        System.out.println("[메세지 송신 오류] "
                                + socket.getRemoteSocketAddress()
                                + ": " + Thread.currentThread().getName());
                        Controller.clients.remove(Client.this);
                        socket.close();
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
            }
        };

        Controller.threadPool.submit(thread);
    }

}
