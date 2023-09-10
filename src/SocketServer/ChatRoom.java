package SocketServer;

import jdk.net.Sockets;

import java.util.ArrayList;
import java.util.List;

public class ChatRoom {
    private int id;
    private String title;
    private List<SocketServer> chatThreadList;

    public ChatRoom(int id, String title){
        this.id = id;
        this.title = title;

        chatThreadList = new ArrayList<>();

    }

    public void broadcast(String msg){
        System.out.println("ChatRoom에서 메세지 브로드캐스트" + msg);
        for(int i=0;i<chatThreadList.size();i++){
            SocketServer chatThread = chatThreadList.get(i);
            chatThread.sendMessage(msg);
        }
    }

    // 방에 입장했을때,
    public void addChatThread(SocketServer chatThread){
        chatThreadList.add(chatThread);
        chatThread.setChatRoom(this); //
    }

    public void removeChatThread(SocketServer chatThread){
        //chatThreadList.remove(chatThread);
        // 모든 채팅 참여자 나가도록 하기
        for(SocketServer ct : chatThreadList){
            ct.setChatRoom(null);
        }
        chatThread.setChatRoom(null);
        chatThreadList.clear();
        broadcast(chatThread.getName() + "님이 퇴장하셨습니다.");
        broadcast(chatThread.getName() + "채팅방을 종료합니다.");
        chatThreadList.clear();
    }



    @Override
    public String toString() {
        return "ChatRoom{" +
                "id=" + id +
                ", title='" + title + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}