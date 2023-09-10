package SocketClient;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class WritingThread extends Thread { // 서버로 메세지 보내는 Thread
    Socket socket = null;
    private BufferedReader br;
    private PrintWriter pw;
    Scanner scanner = new Scanner(System.in); // 채팅용 Scanner

    public WritingThread(Socket socket) throws IOException { // 생성자
        // 받아온 Socket Parameter를 해당 클래스 Socket에 넣기
        this.socket = socket;
        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter pw =  new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.br = br;
        this.pw = pw;
    }

    public void run() {
        try {
            // OutputStream - Server에서 Client로 메세지 발송
            // socket의 OutputStream 정보를 OutputStream out에 넣은 뒤
            // PrintWriter에 위 OutputStream을 담아 사용
            pw =  new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

            while(true) { // 무한반복
                String message = scanner.nextLine();
                pw.println(message); // 입력한 메세지 발송
                pw.flush();
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
