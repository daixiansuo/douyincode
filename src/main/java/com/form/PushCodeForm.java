package com.form;

import com.formdev.flatlaf.IntelliJTheme;
import com.config.HttpConfig;
import com.entity.DouYinLoginEntity;
import com.utils.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.List;

/**
 * @Author: ADMIN
 * @Date: 2022/6/17 15:27
 */
public class PushCodeForm extends JFrame {


    public JButton GetPushCodeButton;
    public JPanel panel1;
    public JTextField DTextField;
    public JTextField textField1;
    public JTextField textField2;
    public JButton getDeviceIdButton;
    public JButton deviceIdButton;


    static {
        IntelliJTheme.setup(AppMain.class.getResourceAsStream("/com/formdev/flatlaf/intellijthemes/themes/HiberbeeDark.theme.json"));

    }


    public PushCodeForm() {

        //JFrame frame = new JFrame("获取推流");
        this.setTitle("获取推流");
       // PushCodeForm form =  new PushCodeForm();
        this.setContentPane(this.panel1);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //禁止调整窗口大小
        this.setResizable(false);
        this.setSize(600,400);
        this.setPreferredSize(new Dimension(600,400));
        //在屏幕中间显示
        this.setLocationRelativeTo(null);
        this.pack();

        this.setVisible(true);



        GetPushCodeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                getPushCode();
                super.mouseClicked(e);
            }
        });
        getDeviceIdButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String did = getDeviceId();
                if (null != did){
                    DTextField.setText(did);
                }
                super.mouseClicked(e);
            }
        });
        deviceIdButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(null, "手机:进入设置>往下翻>找到版本号( 抖音 version 21.0.0 ) 疯狂点击,出现DeviceId:后面的数字就是");
                super.mouseClicked(e);
            }
        });
    }


    public static void main1(String[] args) {
        JFrame frame = new JFrame("获取推流");
        PushCodeForm form =  new PushCodeForm();
        frame.setContentPane(form.panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //禁止调整窗口大小
        frame.setResizable(false);
        frame.setSize(600,400);
        frame.setPreferredSize(new Dimension(600,400));
        //在屏幕中间显示
        frame.setLocationRelativeTo(null);
        frame.pack();

        frame.setVisible(true);
    }

    public void getPushCode(){
        if (null == DTextField.getText() || DTextField.getText().length() <=5){
            JOptionPane.showMessageDialog(null, "请输入deviceId", "输入deviceId", JOptionPane. ERROR_MESSAGE);
            return;
        }
        DouYinLoginEntity liveContinue =  AppMain.douYinLiveUtils.liveContinue(AppMain.cookieAtomic, HttpConfig.AID_DOUYIN,DTextField.getText().trim());
        if (null != liveContinue && 0 ==  liveContinue.getLiveStatus()){
            String[] strings = liveContinue.getPushUrl().split("/stream-");
            String pushUrl = null;
            String pushKey = null;
            for (int i = 0; i < strings.length; i++) {
                if (i == 0){
                    pushUrl = strings[i]+"/";
                }else if (i == 1){
                    pushKey = "stream-"+ strings[i];
                }else{
                    pushKey = pushKey + strings[i];
                }
            }
            textField1.setText(pushUrl);
            textField2.setText(pushKey);
        }else{
            assert liveContinue != null;
            String errMsg = "错误:"+ liveContinue.getMsg();
            JOptionPane.showMessageDialog(null, errMsg, "错误", JOptionPane. ERROR_MESSAGE);
        }

    }


    public String getDeviceId(){
       String currentUserName = System.getProperty("user.name");
       String path = "C:\\Users\\"+currentUserName+"\\AppData\\Roaming\\webcast_mate\\logs";
        List<File> allFile = FileUtils.getAllFile(path);
        if (null == allFile || allFile.size() <= 0){
            JOptionPane.showMessageDialog(null, "未能成功获取!建议使用伴侣开播关播一次再尝试!", "错误", JOptionPane. ERROR_MESSAGE);
            return null;
        }

        for (int i = 0; i < allFile.size(); i++) {
            String name = allFile.get(i).getName();
            String extension = name.substring(name.lastIndexOf("."));
           try{
               if (".txt".equals(extension)){
                   InputStreamReader reader = new InputStreamReader(new FileInputStream(allFile.get(i)));
                   BufferedReader br = new BufferedReader(reader);
                   String line = "";
                   String did = null;
                   while (line != null) {
                       line =  br.readLine();
                       if (line.contains("did:")){
                           did = line.replace("did:","").trim();
                           break;
                       }
                       if (line.contains("did[")){
                           did = line.replace("did[","").replace("]","").trim();
                           break;
                       }

                   }
                   reader.close();
                   br.close();
                   return did;
               }

           }catch (Exception e){
               e.printStackTrace();
               JOptionPane.showMessageDialog(null, "未能成功获取!建议使用伴侣开播关播一次再尝试!" +e.getMessage(), "错误", JOptionPane. ERROR_MESSAGE);
           }
        }

        return null;
    }
}
