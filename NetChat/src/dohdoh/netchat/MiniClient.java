package dohdoh.netchat;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JOptionPane;

//이 프로그램은 소켓통신의 기본 기능 작동여부 확인을 위해 만든 코드로서 NetChat에서의 기본 기능만을 제공함
//서버에서는 클라이언트를 접속하게 하고 문자열을 받아 이를 출력하는 역할을 함
//실제적인 코드는 Chat으로 시작하는 파일에 있음

public class MiniClient {
	Socket sock;
	ObjectOutputStream writer;		// 서버로 송신하기 위한 스트림

	public static void main (String[] args) {
		new MiniClient().go();
	}

	private void go () {
		try {
			sock = new Socket("127.0.0.1", 5000);	// 채팅을 위한 소켓 포트 5000 사용
			writer = new ObjectOutputStream(sock.getOutputStream());

			for (int i=0; i<10; i++) {
				writer.writeObject("Hello From Client");
				writer.flush();
				
				Thread.sleep(500);
			}
			
		} catch(Exception ex) {
			System.out.println("C : 클라이언트  연결 중 이상발생");	// 상태를 보기위한 출력  메시지
			ex.printStackTrace();
		} finally {
			try {
				writer.close();
				sock.close();
			} catch (Exception ex) {
				System.out.println("C : 연결 닫기 전에 문제 발생");	// 상태를 보기위한 출력  메시지
				ex.printStackTrace();				
			}
		}
	}
}
