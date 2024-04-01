package gui;

// 자바 swing 라이브러리는 그래픽으로 화면 표시

import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

// JFrame은 윈도우 창 => 상속하여 커스텀 객체 만들기이다
public class MainFrame extends JFrame {

    public JTextField address;
    public JLabel resAddress, resX, resY, jibunAddress, imageLabel;

    public MainFrame(String title) {
        super(title); // 윈도우 창의 제목
        JPanel pan = new JPanel();
        JLabel addressLbl = new JLabel("주소 입력 ");
        address = new JTextField(50); // 주소를 입력 받을 창 (50칸)
        JButton btn = new JButton("클릭");
        pan.add(addressLbl);
        pan.add(address);
        pan.add(btn);

        // 버튼 클릭 시 이벤트
        btn.addActionListener(e -> {
            try {
                new NaverMap(this);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            } catch (ParseException ex) {
                throw new RuntimeException(ex);
            }
        });

        // 가운데 이미지 라벨
        imageLabel = new JLabel("지도 보기");

        // 아래의 정보 표시 패널
        JPanel pan1 = new JPanel();
        pan1.setLayout(new GridLayout(4, 1));
        resAddress = new JLabel("도로명");
        jibunAddress = new JLabel("지번 주소");
        resX = new JLabel("경도");
        resY = new JLabel("위도");

        pan1.add(resAddress);
        pan1.add(jibunAddress);
        pan1.add(resX);
        pan1.add(resY);

        // 레이아웃 설정
        setLayout(new BorderLayout());
        add(BorderLayout.NORTH, pan);
        add(BorderLayout.CENTER, imageLabel);
        add(BorderLayout.SOUTH, pan1);

        setSize(730, 660); // 창의 크기
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 창을 닫을때 프로그램도 종료됨
        setVisible(true); // 화면에 표시
    }
}
