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