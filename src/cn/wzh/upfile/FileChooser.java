/**
 * 这个文件夹仅仅是为了测试JFileChooser的使用情况
 * 并不涉及主程序
 *
 */

package cn.wzh.upfile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class FileChooser extends JFrame implements ActionListener {
    JButton open = null;

    public FileChooser() {
        open = new JButton("test");
        this.add(open);
        this.setBounds(400, 200, 160, 200);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        open.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        jFileChooser.showDialog(new Label(), "选择文件");
        File file = jFileChooser.getSelectedFile();
        if (file.isDirectory()) {
            System.out.println("文件夹：" + file.getAbsolutePath());
        } else if (file.isFile()) {
            System.out.println("文件：" + file.getAbsolutePath());
        }
        System.out.println(jFileChooser.getSelectedFile().getName());
    }
}
