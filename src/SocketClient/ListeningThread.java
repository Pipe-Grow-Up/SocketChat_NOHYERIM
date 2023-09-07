package SocketClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;


public class ListeningThread extends Thread { // 서버에서 보낸 메세지 읽는 Thread
    Socket socket = null;
    InputStream input;

    public ListeningThread(Socket socket) { // 생성자
        this.socket = socket; // 받아온 Socket Parameter를 해당 클래스 Socket에 넣기
        try{
            // InputStream - Server에서 보낸 메세지를 클라이언트로 가져옴
            // socket의 InputStream 정보를 InputStream in에 넣은 뒤
             input = socket.getInputStream();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void run() {
        try {

            // BufferedReader에 위 InputStream을 담아 사용
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            while(input != null) { // 무한반복
                try{
                    String serverMessage = reader.readLine();
                    System.out.println("server: "+serverMessage);
                }catch (Exception e){
                    e.printStackTrace();
                    break;
                }

                }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}