package SocketServer;

import java.net.Socket;

// 소켓통신용 서버 코드
public class User {

    private Socket client_socket; // 유저 소켓
    private String userName ; // 닉네임

//    private SocketServer myChatThread; // 내가 속한 채팅 쓰레드
//    private ArrayList<SocketServer> chatThreadList; // 내가 속한 채팅 쓰레드 리스트

    public User(Socket client_socket, String userName){
        this.client_socket = client_socket;
        this.userName = userName;
    }

    public Socket getClient_socket() {
        return client_socket;
    }

    public void setClient_socket(Socket client_socket) {
        this.client_socket = client_socket;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

//    public SocketServer getMyChatThread() {
//        return myChatThread;
//    }
//
//    public void setMyChatThread(SocketServer myChatThread) {
//        this.myChatThread = myChatThread;
//    }


}
