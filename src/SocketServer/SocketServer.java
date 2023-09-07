package SocketServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

// 소켓통신용 서버 코드
public class SocketServer extends Thread {
    String name = "unknown"; // 클라이언트 이름 설정용
    static ArrayList<Socket> list = new ArrayList<Socket>(); // 유저 확인용 (Todo  이걸 어떻게 처리할지 고민) 1. 닉네임으로 저장. 2. 소켓+닉네임으로 저장
    static Socket socket = null;

    public SocketServer(Socket socket) {
        SocketServer.socket = socket; // 유저 socket을 할당
        list.add(socket); // 유저를 list에 추가
    }
    public void run() {
        // Thread 에서 start() 메소드 사용 시 자동으로 해당 메소드 시작 (Thread별로 개별적 수행)

        try {
            System.out.println("서버 : " + socket.getInetAddress()
                    + " IP의 클라이언트와 연결되었습니다");

            // InputStream - 클라이언트에서 보낸 메세지 읽기
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            // OutputStream - 서버에서 클라이언트로 메세지 보내기
            OutputStream out = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(out, true);

            // 클라이언트에게 연결되었다는 메세지 보내기
            writer.println("안녕하세요, 여기는 로비입니다. 원하시는 기능을 숫자로 입력해주세요.\n" +
                    "1. 닉네임 설정\n" +
                    "2. 접속자 리스트 출력\n" +
                    "3. 채팅 프로그램 종료");

            String readValue; // Client에서 보낸 값 저장

            boolean identify = false;

            // 클라이언트가 메세지 입력시마다 수행
            while((readValue = reader.readLine()) != null ) {
                System.out.println(readValue);
//                if(!identify) { // 연결 후 한번만 노출
//                    name = readValue; // 이름 할당
//                    identify = true;
//                    writer.println(name + "님이 접속하셨습니다.");
//                    continue;
//                }
                //TODO 빈 문자열 대응
                if(!identify && !readValue.isBlank()){ // 연결 후 한번만 노출
                    identify = true;
                    //if(readValue.is) is number-
                    //else string (/exit)//TODO 분리해서 하기
                    if(Integer.parseInt(readValue) == 1){ // 1번 : 닉네임 입력
                        //  out = list.get(i).getOutputStream();
                        //writer = new PrintWriter(out, true);

                        out = socket.getOutputStream();
                        writer = new PrintWriter(out,true);

                        writer.println("닉네임을 입력해주세요: ");
                        writer.flush();
                        // InputStream - 클라이언트에서 보낸 메세지 읽기
                        InputStream nameInput = socket.getInputStream();

                        BufferedReader nameReader = new BufferedReader(new InputStreamReader(nameInput));
                        name=nameReader.readLine();
                        writer.println("닉네임이 "+name + "으로 설정되었습니다.");
                        continue;
                    }else if(Integer.parseInt(readValue) == 2){
                        for(int i=0;i<list.size();i++){
                            writer.println("----------------------");
                            writer.print("* "+list.get(i));
                            writer.println("----------------------");
                        }
                    }else if(Integer.parseInt(readValue) == 3 || readValue.contains("/exit") ){
                        System.out.println("채팅 프로그램을 종료합니다.");
                        System.out.println("remove : "+socket.toString());
                        list.remove(socket);
                        continue;
                    }
                }



                // list 안에 클라이언트 정보가 담겨있음
                for(int i = 0; i<list.size(); i++) {
                    out = list.get(i).getOutputStream();
                    writer = new PrintWriter(out, true);
                    // 클라이언트에게 메세지 발송
                    writer.println(name + " : " + readValue);
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // 예외처리
        }
    }

    public static void main(String[] args) {
        try {
            int socketPort = 30001; // 소켓 포트 설정용
            ServerSocket serverSocket = new ServerSocket(socketPort); // 서버 소켓 만들기
            // 서버 오픈 확인용
            System.out.println("socket : " + socketPort + "으로 서버가 열렸습니다");

            // 소켓 서버가 종료될 때까지 무한루프
            while(true) {
                Socket socketUser = serverSocket.accept(); // 서버에 클라이언트 접속 시
                // Thread 안에 클라이언트 정보를 담아줌
                Thread thd = new SocketServer(socketUser);
                thd.start(); // Thread 시작
            }

        } catch (IOException e) {
            e.printStackTrace(); // 예외처리
        }

    }

}
