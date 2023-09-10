package SocketServer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// ChatRoom을 관리하는 클래스

public class ChatRoomService {

    private static int GEN_ID = 1; // 채팅방 id 생성
    private List<ChatRoom> chatRoomList;

    // 생성자
    public ChatRoomService(){
        this.chatRoomList = new ArrayList<>();
    }


    public ChatRoom createChatRoom(String title){
        System.out.println("방 생성을 합니다. : " + title);
        ChatRoom chatRoom = new ChatRoom(GEN_ID, title);
        GEN_ID++;
        chatRoomList.add(chatRoom);
        return chatRoom;
    }

    public Iterator<ChatRoom> getChatRoomIterator(){
        return chatRoomList.iterator();
    }

    public boolean isFullRoom(int id){
        // 방이 다 찼는지 확인 -> 채팅방 인원이 2명이면 -> true 반환
        for (ChatRoom chatRoom : chatRoomList) {
            if (chatRoom.getId() == id) {
                if (chatRoom.getChatThreadListSize() < 2) {
                    return false; // 아직 방이 다 차지 않음
                }else{
                    break;
                }
            }
        }
        return true;
    }

    public void join(int id, SocketServer chatThread) {
        for(int i=0;i<chatRoomList.size();i++){
            ChatRoom chatRoom = chatRoomList.get(i);
            if(chatRoom.getId() == id ){
                chatRoom.addChatThread(chatThread);
                break;
            }
        }
    }
}