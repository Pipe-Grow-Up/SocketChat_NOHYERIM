package SocketClient;

import java.io.IOException;
import java.net.Socket;

// 소켓통신용 클라이언트 부분
public class SocketClient {

    public static void main(String[] args) {
        try {
            Socket socket = null;
            // 소켓 서버에 접속
            socket = new Socket("127.0.0.1", 30000);
            System.out.println("서버에 접속 성공!"); // 접속 확인용


            ListeningThread t1 = new ListeningThread(socket); // 서버에서 보낸 메세지 읽는 Thread
            WritingThread t2 = new WritingThread(socket);     // 서버로 메세지 보내는 Thread

            t1.start(); // ListeningThread Start
            t2.start(); // WritingThread Start

        } catch (IOException e) {
            e.printStackTrace(); // 예외처리
        }
    }
}