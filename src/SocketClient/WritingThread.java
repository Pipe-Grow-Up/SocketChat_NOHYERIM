package SocketClient;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class WritingThread extends Thread { // 서버로 메세지 보내는 Thread
    Socket socket = null;
    Scanner scanner = new Scanner(System.in); // 채팅용 Scanner

    public WritingThread(Socket socket) { // 생성자
        // 받아온 Socket Parameter를 해당 클래스 Socket에 넣기
        this.socket = socket;
    }

    public void run() {
        try {
            // OutputStream - 클라이언트에서 Server로 메세지 발송
            // socket의 OutputStream 정보를 OutputStream out에 넣은 뒤
            OutputStream out = socket.getOutputStream();
            // PrintWriter에 위 OutputStream을 담아 사용
            PrintWriter writer = new PrintWriter(out, true);

            while(true) { // 무한반복
                String message = scanner.nextLine();
                writer.println(message); // 입력한 메세지 발송
                System.out.println(message);
                if(message.equals("3") || message.contains("/exit")){ // 종료이면 종료
                    System.out.println("채팅 프로그램을 종료합니다..");
                    break;
                }
            }
            System.exit(0);

        } catch (Exception e) {
            e.printStackTrace(); // 예외처리
        }


    }


}
