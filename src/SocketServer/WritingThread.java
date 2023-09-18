package SocketServer;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Scanner;

import static Global.Global.clientUserList;

public class WritingThread extends Thread { // client로 메세지 보내는 Thread
    private   Scanner scanner = new Scanner(System.in); // 공지용 Scanner

    public WritingThread() { // 생성자

    }

    public void run() {
        try {

            while(true) { // 무한반복
                String message = scanner.nextLine();
                if(message.equals("/list")){
                    // 접속한 사람 체크

                    System.out.println("----------------------");
                    for(int i = 0; i< clientUserList.size(); i++){
                        System.out.println("* "+ clientUserList.get(i).getUserName());
                    }
                    System.out.println("----------------------");
                }else{
                    for(User user : clientUserList){
                        // OutputStream - Server에서 Client로 메세지 발송
                        // socket의 OutputStream 정보를 OutputStream out에 넣은 뒤
                        // PrintWriter에 위 OutputStream을 담아 사용
                        PrintWriter pw =  new PrintWriter(new OutputStreamWriter(user.getClient_socket().getOutputStream()));
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
