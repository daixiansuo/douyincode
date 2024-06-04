package com.form;

import com.alibaba.fastjson.JSONObject;
import com.dtflys.forest.Forest;
import com.dtflys.forest.http.ForestCookie;
import com.formdev.flatlaf.IntelliJTheme;
import com.config.HttpConfig;
import com.entity.DouYinLoginEntity;
import com.utils.DouYinLiveUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @Author: ADMIN
 * @Date: 2022/6/17 14:57
 */
public class AppMain {
    private JPanel panel1;
    private JButton LoginButton;
    private JLabel images;
    private JButton OpenBlogButton;
    public static AtomicReference<List<ForestCookie>> cookieAtomic = new AtomicReference<>(null);
    public static DouYinLiveUtils douYinLiveUtils = new DouYinLiveUtils();
    public static String token = null;
    public static JSONObject liveInfo = null;
    public static String localVersion = "1.0.0";
    public static JFrame frame;

    static {
        IntelliJTheme.setup(AppMain.class.getResourceAsStream("/com/formdev/flatlaf/intellijthemes/themes/HiberbeeDark.theme.json"));

    }


    /**
     * 事件
     */
    public  AppMain() {
        System.out.println("AppMain");


        /**
         * 先运行一遍再打包,否则编译后的程序运行出错!
         */
        images.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                createQR();
                super.mouseClicked(e);
            }
        });
        LoginButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                if (getQRStatus()){
                    showWindow();
                }

                super.mouseClicked(e);
            }
        });
        OpenBlogButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openWeb("https://destiny.cool");
                super.mouseClicked(e);
            }
        });

        createQR();

    }


    public  void showWindow(){
      //  frame.setVisible(false);


/*
        SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run(){
                new PushCodeForm();
            }
        });
*/

        PushCodeForm pushCodeForm =  new PushCodeForm();


    }


    public static void openWeb(String url) {
        try{
            Desktop desktop = Desktop.getDesktop();
            if (Desktop.isDesktopSupported()) {
                desktop.browse(new URI(url));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.println("MAIN");
        try{
            String version =  Forest.get("http://tools.destiny.cool/app/douyin-tools-java-version.txt").executeAsString();
            if (!localVersion.equals(version)){
                JOptionPane.showMessageDialog(null, "版本不匹配!请下载最新版本", "错误", JOptionPane. ERROR_MESSAGE);
                openWeb("https://destiny.cool");
            }
        }catch (Exception e){
            JOptionPane.showMessageDialog(null, "检查版本发生错误!", "错误", JOptionPane. ERROR_MESSAGE);
            openWeb("https://destiny.cool");
        }




        ////////////应用程序初始化
        frame = new JFrame("抖音扫码登陆 v"+ localVersion);
        AppMain mainForm = new AppMain();
        frame.setContentPane(mainForm.panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //禁止调整窗口大小
        frame.setResizable(false);
        frame.setSize(600,400);
        frame.setPreferredSize(new Dimension(600,400));
        //在屏幕中间显示
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);
        //////////////业务逻辑/////////////////



    }
/**
 *创建二维码
**/
    public void createQR(){
        images.setText("创建二维码...");
        DouYinLoginEntity loginQR = douYinLiveUtils.createLoginQR(cookieAtomic, HttpConfig.AID_DOUYIN, true);
        if (null == loginQR){
            JOptionPane.showMessageDialog(null, "创建二维码失败!", "错误", JOptionPane. ERROR_MESSAGE);
        }else{

            byte[] decoded = Base64.getDecoder().decode(loginQR.getQrCode());
            ImageIcon icon = new ImageIcon(decoded);
            icon.setImage(icon.getImage().getScaledInstance(200, 200, Image.SCALE_DEFAULT));

            images.setIcon(icon);
            images.setText("");
            images.setSize(64,64);
            images.setPreferredSize(new Dimension(64,64));
           token = loginQR.getToken();

        }
    }

    /**
     * 获取扫码状态
     * @return
     */
    public boolean getQRStatus(){
        if (null == token){
            JOptionPane.showMessageDialog(null, "请先扫码登陆!", "错误", JOptionPane. ERROR_MESSAGE);
            return false;
        }

        DouYinLoginEntity liveStatus = douYinLiveUtils.liveCheckQRConnect(HttpConfig.AID_DOUYIN,token);
        if (liveStatus.getLiveStatus() == -1){
            JOptionPane.showMessageDialog(null, liveStatus.getMsg(), "错误", JOptionPane. ERROR_MESSAGE);
            return false;
        }
        //登陆成功
        if (3 == liveStatus.getLiveStatus()){
            //登陆成功回调
            douYinLiveUtils.autoCallBack(cookieAtomic,liveStatus.getRedirectUrl());
            //创建账号信息
            DouYinLoginEntity accountInfo = douYinLiveUtils.createAccountInfo(cookieAtomic, HttpConfig.AID_DOUYIN);
            //获取账号信息
            DouYinLoginEntity  userInfo =  douYinLiveUtils.getUserInfo(cookieAtomic);
            JSONObject resData = new JSONObject();
            resData.put("liveTitle",accountInfo.getLiveTitle());
            resData.put("userId",userInfo.getUserId());
            resData.put("nickname",userInfo.getNickname());
            resData.put("uniqueId",userInfo.getUniqueId());
            resData.put("avatarUrl",userInfo.getAvatarUrl());
            liveInfo = resData;
            return true;
        }else{
            String msg;
            switch(liveStatus.getLiveStatus()){
                case 1:
                    msg = "未扫码";
                    break;
                case 2:
                    msg = "已扫码未登陆";
                    break;
                case 4:
                    msg = "取消扫码";
                    break;
                case 5:
                    msg = "二维码过期";

                    break;
                default:
                    msg = liveStatus.getMsg();
            }

            JOptionPane.showMessageDialog(null, msg, "错误", JOptionPane. ERROR_MESSAGE);
            return false;
        }

    }
}
