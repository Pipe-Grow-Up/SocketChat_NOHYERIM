package SocketServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

public class SocketServer extends Thread {

    /* 닉네임 지정하지 않을 시엔 : guest+id (id: auto increment)*/
    private static int GUEST_ID = 1; // 닉네임 지정 하지 않은 guest들을 위한 id
    private String guestNickName = "guest"; // 직네임 지정 하지 않은 사람들을 위한 이름

    static ArrayList<User> userList = new ArrayList<User>(); // 유저 확인용 리스트
    static Socket socket = null;

    private boolean isJoinChatRoom;
    private BufferedReader br;
    private PrintWriter pw;
    ChatRoomService chatRoomService;
    private ChatRoom chatRoom;
    public SocketServer(Socket socket, ChatRoomService chatRoomService) throws Exception {
        SocketServer.socket = socket; // 유저 socket을 할당
        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter pw =  new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.br = br;
        this.pw = pw;
        isJoinChatRoom = false;
        this.chatRoomService = chatRoomService;

    }
    public void sendMessage(String msg){
        System.out.println("sendMessage : " + msg);
        pw.println(msg);
        pw.flush();
    }

    public boolean isOnlyOneName(String userInputName){
        /// 유일한 닉네임 인지 확인 -> 있으면 false return

        for(User user : userList){
            if(userInputName.equals(user.getUserName())){
                return false;
            }
        }
        return true;
    }

    public void run() {
        // ChatThread는 사용자가 보낸 메세지를 읽어들여서,
        // 접속된 모든 클라이언트에게 메세지를 보낸다.
        // 나를 제외한 모든 사용자에게 "00님이 연결되었습니다"..
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

            // 쓰레드별로 유저생성
            User thisUser = new User(socket,"(me)"); // 초기 이름을 ME로 지정
            userList.add(thisUser);

            // 클라이언트에게 연결되었다는 메세지 보내기
            writer.println("안녕하세요, 여기는 로비입니다. 원하시는 기능을 숫자로 입력해주세요.\n" +
                    "1. 닉네임 설정\n" +
                    "2. 접속자 리스트 출력\n" +
                    "3. 채팅 프로그램 종료. 또는 /exit입력 시 언제든지 종료 가능합니다.");

            String readValue; // Client에서 보낸 값 저장


            // 클라이언트가 메세지 입력시마다 수행
            while((readValue = reader.readLine()) != null ) {
                System.out.println(readValue);

                if(!readValue.isBlank()){ // 연결 후 한번만 노출
                    //if(readValue.is) is number- // TODO 숫자가 아닌경우 대응
                    if(Integer.parseInt(readValue) == 1){ // 1번 : 닉네임 입력
                        out = socket.getOutputStream();
                        writer = new PrintWriter(out,true);

                        writer.println("닉네임을 입력해주세요: ");
                        writer.flush();
                        // InputStream - 클라이언트에서 보낸 메세지 읽기
                        InputStream nameInput = socket.getInputStream();

                        BufferedReader nameReader = new BufferedReader(new InputStreamReader(nameInput));
                        String userInputName = nameReader.readLine();
                        System.out.println("log : "+userInputName);


                        while (!isOnlyOneName(userInputName) || userInputName.contains("_")) {
                            // 이름을 입력받은 후 while문에서 유일한 이름인지 검사한다.
                            // 이름이 중복되지 않으면 while문을 종료하고 이름을 지정한다.
                            // "_" 가 들어가 있는 닉네임은 만들 수 없음. 이름 지정 안했을 때의 구분 규칙.
                            writer.println("닉네임이 중복되었습니다. 다시입력하세요");
                            userInputName = nameReader.readLine();
                        }

                        thisUser.setUserName(userInputName);
                        writer.println("닉네임이 " + thisUser.getUserName() + "으로 설정되었습니다.");



                        continue;

                    }else if(Integer.parseInt(readValue) == 2){ // 2번 : 접속자 리스트 출력
                        writer.println("----------------------");
                        for(int i=0;i<userList.size();i++){
                            writer.println("* "+userList.get(i).getUserName());
                        }
                        writer.println("----------------------");
                    }else if(Integer.parseInt(readValue) == 3 || readValue.contains("/exit") ){ // 채팅 프로그램 종료
                        System.out.println("채팅 프로그램을 종료합니다.");
                        System.out.println("remove : "+socket.toString());
                        for(User user : userList){
                            if(user.getClient_socket().equals(socket)){
                                userList.remove(user);
                            }
                        }
                        continue;
                    }
                }

                if(thisUser.getUserName().equals("(me)")){ // 이름 지정 안했으면
                    thisUser.setUserName(guestNickName+"_"+GUEST_ID);
                    GUEST_ID++;
                }
                if(readValue.contains("/exit")){ // 채팅 프로그램 종료
                    System.out.println("채팅 프로그램을 종료합니다.");
                    System.out.println("remove : "+socket.toString());
                    for(User user : userList){
                        if(user.getClient_socket().equals(socket)){
                            userList.remove(user);
                        }
                    }
                    continue;
                }else if(readValue.contains("/makechat")){ // 방 생성 요청
                      //  String title = readValue.split(" ")[1]+", "+thisUser.getUserName();
                        ChatRoom chatRoom = chatRoomService.createChatRoom(thisUser.getUserName()+"의 채팅방");
                        this.chatRoom = chatRoom;
                        this.chatRoom.addChatThread(this);
                }else if(readValue.contains("/quit")){ // 방에서 빠져나가기
                    this.chatRoom.removeChatThread(this);
                }

                else if(readValue.contains("/list") ){ // 리스트 확인
                    Iterator<ChatRoom> chatRoomIterator= chatRoomService.getChatRoomIterator();
                    while(chatRoomIterator.hasNext()){
                        ChatRoom cr = chatRoomIterator.next();
                        String status = chatRoomService.isFullRoom(cr.getId()) ? "입장불가" : "입장가능" ;
                        pw.println(cr.getId() + " - " + cr.getTitle()+" || "+status);
                        pw.flush();
                    }
                }
                else if(readValue.contains("/join")){ // 방 입장
                    try {
                        int roomId = Integer.parseInt(readValue.substring(6));
                        if(chatRoomService.isFullRoom(roomId)){ // 방이 다 찼으면 안내메시지 출력
                            pw.println("정원이 찬 방입니다.");
                        }else{
                            pw.println("채팅방에 입장합니다.");
                            chatRoomService.join(roomId, this);
                        }

                    } catch(Exception ex){
                        pw.println("방 번호가 잘못 되었습니다.");
                        pw.flush();
                    }
                } else if(this.chatRoom != null){
                    System.out.println("속한 방에 브로드캐스트 합니다."+ readValue);
                    chatRoom.chatRoomBroadcastMsg(thisUser.getUserName()+" : "+readValue);

                }
                else{
                    for (int i = 0; i < userList.size(); i++) {
                    out = userList.get(i).getClient_socket().getOutputStream();
                    writer = new PrintWriter(out, true);
                    // 클라이언트에게 메세지 발송
                    writer.println(thisUser.getUserName() + " : " + readValue);
                }
                }
//
//                for (int i = 0; i < list.size(); i++) {
//                    out = list.get(i).getClient_sokeet().getOutputStream();
//                    writer = new PrintWriter(out, true);
//                    // 클라이언트에게 메세지 발송
//                    writer.println(thisUser.getUserName() + " : " + readValue);
//                }
               // writeAll(readValue);

            }
        } catch (Exception e) {
            e.printStackTrace(); // 예외처리
        }
    }

    public void setChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }

    public static void main(String[] args) {
        try {
            int socketPort = 30000; // 소켓 포트 설정용
            ServerSocket serverSocket = new ServerSocket(socketPort); // 서버 소켓 만들기
            // 서버 오픈 확인용
            System.out.println("socket : " + socketPort + "으로 서버가 열렸습니다");

            ChatRoomService chatRoomService = new ChatRoomService();

            // 소켓 서버가 종료될 때까지 무한루프
            while(true) {
                Socket socketUser = serverSocket.accept(); // 서버에 클라이언트 접속 시
                // Thread 안에 클라이언트 정보를 담아줌
                Thread thd = new SocketServer(socketUser,chatRoomService);
                WritingThread writingThread = new WritingThread(userList); // 공지용 thread
                thd.start(); // Thread 시작
                writingThread.start(); // thread 시작
            }

        } catch (IOException e) {
            e.printStackTrace(); // 예외처리
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
