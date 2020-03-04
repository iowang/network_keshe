package cn.itcast.chat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.Socket;
import java.util.Properties;

//有界面所以需要继承窗体类JFrame
public class ClientChatMain extends JFrame implements ActionListener, KeyListener {
    public static void main(String[] args) {
        new ClientChatMain();
    }

    //    属性
//    文本域
    private JTextArea jta;
    //    滚动条
    private JScrollPane jsp;
    //    面板
    private JPanel jp;
    //    文本框
    private JTextField jtf;
    //    按钮
    private JButton jb;
    //    输出流
    private BufferedWriter bw;

    //    客户端IP地址
    private static String clientIp;

    private static int clientPort;

    static {
        Properties properties = new Properties();
        try {
            properties.load(new FileReader("chat.properties"));
            clientIp = properties.getProperty("clientIp");
            clientPort = Integer.parseInt(properties.getProperty("clientPort"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    构造方法

    public ClientChatMain() {
        Font font = new Font("Serif", 0, 20);
        jta = new JTextArea();
//        设置文本域不可编辑,调整字体大小
        jta.setEditable(false);
        jta.setFont(font);
//        将文本域添加在滚动条中
        jsp = new JScrollPane(jta);

        jp = new JPanel();
        jtf = new JTextField(30);
        jb = new JButton("发送");
//        将文本框和按钮添加到面板中
        jp.add(jtf);
        jp.add(jb);

//        将滚动条和面板添加到窗体
        this.add(jsp, BorderLayout.CENTER);
        this.add(jp, BorderLayout.SOUTH);
//        设置标题,大小，位置，关闭，是否可见

        this.setTitle("聊天客户端");
        this.setSize(600, 500);
        this.setLocation(600, 300);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
//        ************TCP客户端Start**************
//         给发送按钮绑定监听事件
        jb.addActionListener(this);
        jtf.addKeyListener(this);
        try {
//        创建客户端套接字(尝试链接)
            Socket socket = new Socket(clientIp, clientPort);

//        获取socket通道输入流
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//        获取socket通道输出流
            bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

//        循环读取数据，拼接到文本中
            String line = null;
            while ((line = br.readLine()) != null) {
                jta.append(line + System.lineSeparator());
            }
//        关闭socket通道
            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }


//        ************TCP客户端结束****************

    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        sendDataToSocket();
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
            sendDataToSocket();
        }
    }

    private void sendDataToSocket() {
        //        获取文本框发送的内容
        String text = jtf.getText();
//        拼接内容
        text = "客户端：" + text;
//        自己显示
        jta.append(text + System.lineSeparator());
        try {
//        发送
            bw.write(text);
            bw.newLine();
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        清空
        jtf.setText("");

    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {

    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {

    }
}
