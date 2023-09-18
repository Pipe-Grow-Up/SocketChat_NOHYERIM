package SocketServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;

import static Global.Global.clientUserList;

public class SocketServer extends Thread {

    /* 닉네임 지정하지 않을 시엔 : guest+id (id: auto increment)*/
    private static int GUEST_ID = 1; // 닉네임 지정 하지 않은 guest들을 위한 id
    private String mGuestNickName = "guest"; // 직네임 지정 하지 않은 사람들을 위한 이름
    static Socket mSocket = null;

    private boolean isJoinChatRoom;
    private BufferedReader br;
    private PrintWriter pw;
    ChatRoomService chatRoomService;
    private ChatRoom chatRoom;

    public SocketServer(Socket mSocket, ChatRoomService chatRoomService) throws Exception {
        SocketServer.mSocket = mSocket; // 유저 socket을 할당
        BufferedReader br = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(mSocket.getOutputStream()));
        this.br = br;
        this.pw = pw;
        isJoinChatRoom = false;
        this.chatRoomService = chatRoomService;
        // 23/09/18 - incava
        clientUserList.add(new User(mSocket, mGuestNickName));

    }

    void reviseNickName(Socket socket,String nickName){
        // 닉네임 수정
        for(User user : clientUserList){
            // 유저를 모두 찾아, 유저의 닉네임을 변경, 없으면 바꿔질 일이 없음.
            if(user.getClient_socket().equals(socket)){
                user.setUserName(nickName);
                return;
            }
        }
    }

    void removeClientUser(Socket socket){
        // 클라이언트 유저 정보 삭제
        for(User user : clientUserList){
            // 유저를 모두 찾아, 유저의 정보를 삭제, 없으면 삭제할 일이 없음.
            if(user.getClient_socket().equals(socket)){
                // 원하는 유저 정보를 찾아 삭제
                clientUserList.remove(user);
                return;
            }
        }
    }

    public void sendMessage(String msg) {
        System.out.println("sendMessage : " + msg);
        pw.println(msg);
        pw.flush();
    }

    public boolean isOnlyOneName(String userInputName) {
        /// 유일한 닉네임 인지 확인 -> 있으면 false return

        for (User user : clientUserList) {
            if (userInputName.equals(user.getUserName())) {
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
            System.out.println("서버 : " + mSocket.getInetAddress()
                    + " IP의 클라이언트와 연결되었습니다");

            // InputStream - 클라이언트에서 보낸 메세지 읽기
            InputStream input = mSocket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            // OutputStream - 서버에서 클라이언트로 메세지 보내기
            OutputStream out = mSocket.getOutputStream();
            PrintWriter writer = new PrintWriter(out, true);

            // 쓰레드별로 유저생성
            User thisUser = new User(mSocket, "(me)"); // 초기 이름을 ME로 지정
            clientUserList.add(thisUser);

            // 클라이언트에게 연결되었다는 메세지 보내기
            writer.println("안녕하세요, 여기는 로비입니다. 원하시는 기능을 숫자로 입력해주세요.\n" +
                    "1. 닉네임 설정\n" +
                    "2. 접속자 리스트 출력\n" +
                    "3. 채팅 프로그램 종료. 또는 /exit입력 시 언제든지 종료 가능합니다.");

            String readValue; // Client에서 보낸 값 저장


            // 클라이언트가 메세지 입력시마다 수행
            while ((readValue = reader.readLine()) != null) {
                System.out.println(readValue);
                if (!isJoinChatRoom || !readValue.isBlank()) { // 연결 후 한번만 노출
                    //if(readValue.is) is number- // TODO 숫자가 아닌경우 대응
                    if (Integer.parseInt(readValue) == 1) { // 1번 : 닉네임 입력
                        out = mSocket.getOutputStream();
                        writer = new PrintWriter(out, true);

                        writer.println("닉네임을 입력해주세요: ");
                        writer.flush();
                        // InputStream - 클라이언트에서 보낸 메세지 읽기
                        InputStream nameInput = mSocket.getInputStream();

                        BufferedReader nameReader = new BufferedReader(new InputStreamReader(nameInput));
                        String userInputName = nameReader.readLine();
                        System.out.println("log : " + userInputName);


                        while (!isOnlyOneName(userInputName) || userInputName.contains("_")) {
                            // 이름을 입력받은 후 while문에서 유일한 이름인지 검사한다.
                            // 이름이 중복되지 않으면 while문을 종료하고 이름을 지정한다.
                            // "_" 가 들어가 있는 닉네임은 만들 수 없음. 이름 지정 안했을 때의 구분 규칙.
                            writer.println("닉네임이 중복되었습니다. 다시입력하세요");
                            userInputName = nameReader.readLine();
                        }

                        thisUser.setUserName(userInputName);
                        reviseNickName(mSocket,userInputName);
                        writer.println("닉네임이 " + thisUser.getUserName() + "으로 설정되었습니다.");
                        continue;

                    } else if (Integer.parseInt(readValue) == 2) { // 2번 : 접속자 리스트 출력
                        writer.println("----------------------");
                        for (int i = 0; i < clientUserList.size(); i++) {
                            writer.println("* " + clientUserList.get(i).getUserName());
                        }
                        writer.println("----------------------");
                    } else if (Integer.parseInt(readValue) == 3 || readValue.contains("/exit")) { // 채팅 프로그램 종료
                        System.out.println("채팅 프로그램을 종료합니다.");
                        System.out.println("remove : " + mSocket.toString());
                        for (User user : clientUserList) {
                            if (user.getClient_socket().equals(mSocket)) {
                                clientUserList.remove(user);
                            }
                        }
                        continue;
                    }
                }

                if (thisUser.getUserName().equals("(me)")) { // 이름 지정 안했으면
                    thisUser.setUserName(mGuestNickName + "_" + GUEST_ID);
                    GUEST_ID++;
                }
                if (readValue.contains("/exit")) { // 채팅 프로그램 종료
                    System.out.println("채팅 프로그램을 종료합니다.");
                    System.out.println("remove : " + mSocket.toString());
                    for (User user : clientUserList) {
                        if (user.getClient_socket().equals(mSocket)) {
                            clientUserList.remove(user);
                        }
                    }
                    removeClientUser(mSocket);
                } else if (readValue.contains("/makechat")) { // 방 생성 요청
                    //  String title = readValue.split(" ")[1]+", "+thisUser.getUserName();
                    ChatRoom chatRoom = chatRoomService.createChatRoom(thisUser.getUserName() + "의 채팅방");
                    this.chatRoom = chatRoom;
                    this.chatRoom.addChatThread(this);
                } else if (readValue.contains("/quit")) { // 방에서 빠져나가기
                    this.chatRoom.removeChatThread(this);
                } else if (readValue.contains("/list")) { // 리스트 확인
                    Iterator<ChatRoom> chatRoomIterator = chatRoomService.getChatRoomIterator();
                    while (chatRoomIterator.hasNext()) {
                        ChatRoom cr = chatRoomIterator.next();
                        String status = chatRoomService.isFullRoom(cr.getId()) ? "입장불가" : "입장가능";
                        pw.println(cr.getId() + " - " + cr.getTitle() + " || " + status);
                        pw.flush();
                    }
                } else if (readValue.contains("/join")) { // 방 입장
                    try {
                        int roomId = Integer.parseInt(readValue.substring(6));
                        if (chatRoomService.isFullRoom(roomId)) { // 방이 다 찼으면 안내메시지 출력
                            pw.println("정원이 찬 방입니다.");
                        } else {
                            pw.println("채팅방에 입장합니다.");
                            chatRoomService.join(roomId, this);
                        }

                    } catch (Exception ex) {
                        pw.println("방 번호가 잘못 되었습니다.");
                        pw.flush();
                    }
                } else if (this.chatRoom != null) {
                    System.out.println("속한 방에 브로드캐스트 합니다." + readValue);
                    chatRoom.chatRoomBroadcastMsg(thisUser.getUserName() + " : " + readValue);
                } else {
                    for (int i = 0; i < clientUserList.size(); i++) {
                        out = clientUserList.get(i).getClient_socket().getOutputStream();
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
            // 공지용 Thread가 여기 있게 되면, 쓰레드기 때문에 실행 되더라도 밑의 while문도 같이 실행이 가능하며, 한개의 공지를 통해 여러개로 뿌릴 수 있습니다.
            WritingThread writingThread = new WritingThread(); // 공지용 thread
            writingThread.start(); // thread 시작
            // 소켓 서버가 종료될 때까지 무한루프
            while (true) {
                Socket socketUser = serverSocket.accept(); // 서버에 클라이언트 접속 시
                // Thread 안에 클라이언트 정보를 담아줌
                Thread thd = new SocketServer(socketUser, chatRoomService);
                // 공지용 thread가 여기에 있으면, 공지가 서버와 연결된 소켓 통로 만큼 생깁니다. 그로 인해 여러번 엔터를 눌러야 나가는 형식이 되었던 겁니다.
//                WritingThread writingThread = new WritingThread(userList); // 공지용 thread
//                writingThread.start(); // thread 시작
                thd.start(); // Thread 시작
            }

        } catch (IOException e) {
            e.printStackTrace(); // 예외처리
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
