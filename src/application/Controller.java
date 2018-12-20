package application;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by ehay@naver.com on 2018-12-15
 * Blog : http://ehay.tistory.com
 * Github : http://github.com/ehayand
 */

public class Controller {
    public static ExecutorService threadPool;
    public static Vector<Client> clients = new Vector<>();
    public static StringBuilder serverLog = new StringBuilder();
    public static final int LOG_MAX_SIZE = 100;

    ServerSocket serverSocket;

    public void startServer(String IP, int port) {
        try {
            FileWriter fw = new FileWriter("C:\\IOTest\\network.txt");
            BufferedWriter bw = new BufferedWriter(fw);

            bw.write(serverLog.toString());
            bw.flush();
            bw.close();
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(IP, port));
        } catch (Exception e) {
            e.printStackTrace();
            if (!serverSocket.isClosed()) {
                stopServer();
            }
            return;
        }

        Runnable thread = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Socket socket = serverSocket.accept();
                        clients.add(new Client(socket));
                        System.out.println("[클라이언트 접속] "
                                + socket.getRemoteSocketAddress()
                                + ": " + Thread.currentThread().getName());
                    } catch (Exception e) {
                        if (!serverSocket.isClosed()) {
                            stopServer();
                        }
                        break;
                    }
                }
            }
        };

        threadPool = Executors.newCachedThreadPool();
        threadPool.submit(thread);
    }

    public void stopServer() {
        try {
            Iterator<Client> iterator = clients.iterator();
            while (iterator.hasNext()) {
                Client client = iterator.next();
                client.socket.close();
                iterator.remove();
            }

            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }

            if (threadPool != null && !threadPool.isShutdown()) {
                threadPool.shutdown();
            }

            FileWriter fw = new FileWriter("C:\\IOTest\\network.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);

            bw.write(serverLog.toString());
            bw.flush();
            bw.close();
            fw.close();

            System.out.println("[서버 종료] : 메세지 로그 저장");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void logService(String message, String socketData) {
        Runnable logThread = new Runnable() {
            @Override
            public void run() {
                synchronized (serverLog) {

                    serverLog.append("{ { ")
                            .append(socketData)
                            .append(" }, ")
                            .append(message)
                            .append(" }, ");

                    if (serverLog.length() > LOG_MAX_SIZE) {
                        try {
                            FileWriter fw = new FileWriter("C:\\IOTest\\network.txt", true);
                            BufferedWriter bw = new BufferedWriter(fw);

                            bw.write(serverLog.toString());
                            bw.flush();
                            bw.close();
                            fw.close();

                            System.out.println("[로그 사이즈 초과] : 메세지 로그 저장 및 초기화");

                            serverLog = new StringBuilder();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };

        threadPool.submit(logThread);
    }
}
