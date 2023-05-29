package jp.jaxa.iss.kibo.rpc.PENUTSOR;

import android.graphics.Bitmap;
import android.util.Log;
import jp.jaxa.iss.kibo.rpc.api.KiboRpcService;
import java.util.List;
import java.util.Arrays;
import gov.nasa.arc.astrobee.types.Point;
import gov.nasa.arc.astrobee.types.Quaternion;
import org.opencv.core.Mat;
import org.opencv.objdetect.QRCodeDetector;

public class YourService extends KiboRpcService {
    private int imageID = 1;
    private String textQRCode;
    private int live=0;
    private boolean loop =true;
    private Integer [] startclose ={2,1,5,6,4,7,3};// number 7 คือ QRCODE
    private Integer [] target1close ={6,2,7,5,4,3};
    private Integer [] target2close ={5,6,1,7,4,3};
    private Integer [] target3close ={4,5,6,7,2,1};
    private Integer [] target4close ={3,5,2,6,1,7};
    private Integer [] target5close ={6,4,2,7,1,3};
    private Integer [] target6close ={1,5,2,4,3};
    private Integer [] qrCodeclose ={6,1,5,2,3,4};
    @Override
    protected void runPlan1(){
        api.startMission();
        while (loop){
            List<Long> timeRemaining = api.getTimeRemaining();
            if (timeRemaining.get(1) < 40000) {
                break;
            }else{
                List<Integer> activeTargets = api.getActiveTargets();
                switch(live){
                    case 0:
                        onStart(getCloserTarget(activeTargets,Arrays.asList(startclose)));
                        break;
                    case 1:
                        onTarget1(getCloserTarget(activeTargets,Arrays.asList(target1close)));
                        loop=false;
                        break;
                    case 2:break;
                    case 3:break;
                    case 4:break;
                    case 5:break;
                    case 6:break;
                    case 7:break;
                    case 8:break;
                    default:break;
                }
            }
        }
        
        qrCodePoint();
        api.reportMissionCompletion(reportQRCode());
    }
    @Override
    protected void runPlan2(){
       // write your plan 2 here
    }

    @Override
    protected void runPlan3(){
        // write your plan 3 here
    }

    private void centerPoint(){
        moveTo(10.7955d-0.1d,-8.0635d,5.1305d+0.15d,0f, 0f,-0.707f, 0.707f, false);
        saveMatNavCam();
    }

    private void targetPoint1(){
        //y==-10.0d+0.003d 
        //optimize  y==-9.92284d+0.14d ระยะทางสั้นขึ้น
        moveTo(11.2746d-0.0651d-0.002d, -9.92284d+0.14d, 5.3625d+0.1111d, 0f, 0f, -0.707f, 0.707f,false);
        destroyTarget(1);
    }

    private void targetPoint2(){
        //z==4.32d+0.04d
        //optimize  y==-4.48d+0.04d ระยะทางสั้นขึ้น
        moveTo(10.612d-0.1302d-0.028d, -9.085172d-0.0572d-0.054d, 4.48d+0.04d, 0.5f ,0.5f ,-0.5f ,0.5f,false);
        destroyTarget(2);
    }

    private void targetPoint3(){
        //z==4.32d+0.04d
        //optimize  y==-4.48d+0.04d ระยะทางสั้นขึ้น
        moveTo(10.71d+0.00413d, -7.7d-0.0572d-0.0107d, 4.32d+0.04d, 0f, 0.707f, 0f, 0.707f, false);
        destroyTarget(3);
    }

    private void targetPoint4(){
        //x==10.3d+0.04d จะเร็วและสั้นก็ต่อเมื่อ start
        //x==10.51d ของตำแหน่งอื่นๆไม่ใช่ start จะเร็ว
        moveTo(10.51d,-6.7185d+0.0572d+0.049d,5.1804d+(0.1111d/2d)-0.028d,0f,0f,-1f,0f, false);
        destroyTarget(4);
    }

    private void targetPoint5(){
        moveTo(11.114d-0.1302d+0.065d,-7.9756d+0.0572d,5.57d-0.04d,-0.5f,-0.5f,-0.5f,0.5f, false);
        destroyTarget(5);
    }

    private void targetPoint6(){
        moveTo(11.55d-0.06d ,-8.9929d-0.0572d+0.006d ,4.7818d+0.1111d+0.05d,0f ,0f ,0f ,1f, false);
        destroyTarget(6);
    }

    private void qrCodePoint(){
        moveTo(11.381944d+0.1177d, -8.566172d-0.0422d, 4.33d+0.04d, 0.5f ,0.5f ,-0.5f ,0.5f, false);
        readQRCode();
    }
    private void goalPoint(){
        moveTo(11.143d, -6.7607d, 4.9654d, 0f, 0f, -0.707f, 0.707f, false);
    }
    private void destroyTarget(int targetId){
        api.laserControl(true);
        api.takeTargetSnapshot(targetId);
    }

    private void doLaserTakePhoto(int target_id,boolean takeImage){
        api.laserControl(true);
        if(takeImage==true){
            try {
                Thread.sleep(2000);
                saveMatNavCam("laser");
                Thread.sleep(1000);
            } catch (InterruptedException a) {
                a.printStackTrace();
            }
        }
        api.takeTargetSnapshot(target_id);
    }
    
    private void readQRCode(){
        Mat image = new Mat();
        api.flashlightControlFront(0.05f);
        try{
            Thread.sleep(2000);
            image = api.getMatNavCam();
        }catch (InterruptedException a){
            a.printStackTrace();
        }
        api.flashlightControlFront(0.0f);
        QRCodeDetector qrCodeDetector = new QRCodeDetector();
        setTextQRCode(qrCodeDetector.detectAndDecode(image));
    }

    private String reportQRCode(){
        switch (getTextQRCode()){
            case "JEM":return "STAY_AT_JEM";
            case "COLUMBUS":return "GO_TO_COLUMBUS";
            case "RACK1":return "CHECK_RACK_1";
            case "ASTROBEE":return "I_AM_HERE";
            case "INTBALL":return "LOOKING_FORWARD_TO_SEE_YOU";
            case "BLANK":return "NO_PROBLEM";
            default:return null;
        }
    }

    private void saveBitmapNavCam(){
        Bitmap image = api.getBitmapNavCam();
        String imageName = "BitmapNavCam-" + getImageID() + ".jpg";
        api.saveBitmapImage(image, imageName);
        setImageID(getImageID() + 1);
    }

    private void saveBitmapDockCam(){
        Bitmap image = api.getBitmapDockCam();
        String imageName = "BitmapDockCam-" + getImageID() + ".jpg";
        api.saveBitmapImage(image, imageName);
        setImageID(getImageID() + 1);
    }

    private void saveMatNavCam(){
        Mat image = api.getMatNavCam();
        String imageName = "MatNavCam-" + getImageID() + ".jpg";
        api.saveMatImage(image, imageName);
        setImageID(getImageID() + 1);
    }
    private void saveMatNavCam(String a){
        Mat image = api.getMatNavCam();
        String imageName = a + getImageID() + ".jpg";
        api.saveMatImage(image, imageName);
        setImageID(getImageID() + 1);
    }

    private void saveMatDockCam(){
        Mat image = api.getMatDockCam();
        String imageName = "MatDockCam-" + getImageID() + ".jpg";
        api.saveMatImage(image, imageName);
        setImageID(getImageID() + 1);
    }

    private void moveTo(double pointX, double pointY, double pointZ, float quaternionX, float quaternionY, float quaternionZ, float quaternionW, boolean printRobotPosition){
        Point goalPoint = new Point(pointX, pointY, pointZ);
        Quaternion orientation = new Quaternion(quaternionX, quaternionY, quaternionZ, quaternionW);
        api.moveTo(goalPoint, orientation, printRobotPosition);
    }

    private void relativeMoveTo(double pointX, double pointY, double pointZ, float quaternionX, float quaternionY, float quaternionZ, float quaternionW, boolean printRobotPosition){
        Point goalPoint = new Point(pointX, pointY, pointZ);
        Quaternion orientation = new Quaternion(quaternionX, quaternionY, quaternionZ, quaternionW);
        api.relativeMoveTo(goalPoint, orientation, printRobotPosition);
    }

    private void showMessage(String message){
        Log.i("ShowMessage", message);
    }

    public int getImageID() {
        return imageID;
    }

    public void setImageID(int imageID) {
        this.imageID = imageID;
    }

    public String getTextQRCode() {
        return textQRCode;
    }

    public void setTextQRCode(String textQRCode) {
        this.textQRCode = textQRCode;
    }

    private Integer getCloserTarget(List <Integer> activeTargets,List<Integer> choiceTarget){
        int size = activeTargets.size();
        if(size>1){
            Integer min = 9;
            Integer choose = null;
            activeTargets.add(7);// number 7 คือ QRCODE
            for(Integer d:activeTargets) {
               Integer now= choiceTarget.indexOf(d);
               if(now<=min){
                   min=now;
                   choose=d;
               }
            }
            return choose;
        }
        return activeTargets.get(0);
    }
    private void onStart(Integer d){
        switch(d){
            case 1:startToTarget1();break;
            case 2:startToTarget2();break;
            case 3:startToTarget3();break;
            case 4:startToTarget4();break;
            case 5:startToTarget5();break;
            case 6:startToTarget6();break;
            case 7:startToQRCode();break;
        }
    }
    private void startToTarget1(){moveTo(11.2746d, -9.92284d+0.3d, 4.8385d+0.1d, 0f, 0f, -0.707f, 0.707f, false);targetPoint1();}
    private void startToTarget2(){targetPoint2();}
    private void startToTarget3(){moveTo(10.71d, -8.2826d+0.1d, 4.6725d+0.3d, 0 ,0.707f ,0f ,0.707f, false);targetPoint3();}
    private void startToTarget4(){moveTo(10.42d, -8.2826d+0.1d, 5.1804d, 0 ,0f ,-1f ,0f, false);moveTo(10.42d,-6.7185d+0.0572d+0.049d,5.1804d+(0.1111d/2d)-0.028d,0f,0f,-1f,0f, false);doLaserTakePhoto(4,true);}
    private void startToTarget5(){targetPoint5();}
    private void startToTarget6(){targetPoint6();}
    private void startToQRCode(){moveTo(10.9628+0.06d, -9.0734d, 4.48d, 0 ,0.707f ,0f ,0.707f, false);qrCodePoint();}

    private void onTarget1(Integer select){
        switch(select){
            case 2:target1ToTarget2();break;
            case 3:target1ToTarget3();break;
            case 4:target1ToTarget4();break;
            case 5:target1ToTarget5();break;
            case 6:target1ToTarget6();break;
            case 7:target1ToQRCode();break;
            case 8:target1ToGoal();break;
        }
    }
    private void target1ToTarget2(){targetPoint2();}
    private void target1ToTarget3(){centerPoint();targetPoint3();}
    private void target1ToTarget4(){targetPoint4();}
    private void target1ToTarget5(){targetPoint5();}
    private void target1ToTarget6(){targetPoint6();}
    private void target1ToQRCode(){qrCodePoint();}
    private void target1ToGoal(){moveTo(11.143d, -8.0635d, 5.1055-0.14d, 0f, 0f, -0.707f, 0.707f, false);goalPoint();}


}
    