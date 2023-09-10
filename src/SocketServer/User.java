package SocketServer;

import java.net.Socket;
import java.util.ArrayList;

// 소켓통신용 서버 코드
public class User {

    private Socket client_sokeet; // 유저 소켓
    private String userName; // 닉네임

    private SocketServer myChatThread; // 내가 속한 채팅 쓰레드
    private ArrayList<SocketServer> chatThreadList; // 내가 속한 채팅 쓰레드 리스트
    public Socket getClient_sokeet() {
        return client_sokeet;
    }

    public void setClient_sokeet(Socket client_sokeet) {
        this.client_sokeet = client_sokeet;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public User(Socket client_socket, String userName){
        this.client_sokeet = client_socket;
        this.userName = userName;
    }

    public SocketServer getMyChatThread() {
        return myChatThread;
    }

    public void setMyChatThread(SocketServer myChatThread) {
        this.myChatThread = myChatThread;
    }


}
