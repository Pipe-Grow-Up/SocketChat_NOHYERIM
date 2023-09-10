package SocketServer;

import java.io.BufferedReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class WritingThread extends Thread { // client로 메세지 보내는 Thread
    static ArrayList<User> userList = new ArrayList<User>(); // 유저 확인용 리스트
    private   Scanner scanner = new Scanner(System.in); // 공지용 Scanner

    public WritingThread(ArrayList<User> userList) { // 생성자
        // 받아온 Socket Parameter를 해당 클래스 Socket에 넣기
        WritingThread.userList = userList;
    }

    public void run() {
        try {

            while(true) { // 무한반복
                String message = scanner.nextLine();
                if(message.equals("/list")){
                    // 접속한 사람 체크

                    System.out.println("----------------------");
                    for(int i=0;i<userList.size();i++){
                        System.out.println("* "+userList.get(i).getUserName());
                    }
                    System.out.println("----------------------");
                }else{
                    for(User user : userList){
                        // OutputStream - Server에서 Client로 메세지 발송
                        // socket의 OutputStream 정보를 OutputStream out에 넣은 뒤
                        // PrintWriter에 위 OutputStream을 담아 사용
                        PrintWriter pw =  new PrintWriter(new OutputStreamWriter(user.getClient_sokeet().getOutputStream()));
                        pw.println("서버 공지: "+message); // 입력한 메세지 발송
                        pw.flush();
                        System.out.println(message);
                    }
                }


            }

        } catch (Exception e) {
            e.printStackTrace(); // 예외처리
        }


    }


}
