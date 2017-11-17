package com.example.dev.facedetect;

        import android.app.Activity;
        import android.os.Bundle;
        import android.os.Environment;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.SurfaceView;
        import android.view.View;
        import android.view.Window;
        import android.view.WindowManager;
        import android.widget.ImageButton;
        import android.widget.ImageView;

        import org.opencv.android.BaseLoaderCallback;
        import org.opencv.android.CameraBridgeViewBase;
        import org.opencv.android.JavaCameraView;
        import org.opencv.android.LoaderCallbackInterface;
        import org.opencv.android.OpenCVLoader;
        import org.opencv.core.Core;
        import org.opencv.core.CvType;
        import org.opencv.core.Mat;
        import org.opencv.core.MatOfInt;
        import org.opencv.core.MatOfPoint;
        import org.opencv.core.MatOfPoint2f;
        import org.opencv.core.Point;
        import org.opencv.core.Scalar;
        import org.opencv.core.Size;
        import org.opencv.imgcodecs.Imgcodecs;
        import org.opencv.imgproc.Imgproc;

        import java.io.File;
        import java.text.SimpleDateFormat;
        import java.util.ArrayList;
        import java.util.Date;
        import java.util.List;

        import android.content.Intent;
        import android.net.Uri;


/*
public class CameraActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {
    private JavaCameraView mOpenCvCamaraview;
    private Mat mIntermediateMat, mIntermediateMat2,m3;
    private List<MatOfPoint> contours ;
    private ImageView imageView;
    private ImageButton capture_button;
    static final int CAM_REQUEST=1;
    private int thresh = 50, N = 11;
    private String dir;
    private File sdRoot;
    private String fileName;
    private     List<MatOfPoint> squares = new ArrayList<MatOfPoint>();
    private Mat image;
    double a;
    double maxArea = 0;
    int maxIdx = 0;
    String path;
    private Point pt1;
    private    Point pt2;
    private   Point pt3;
    private   Point pt4;

    List<MatOfPoint> contour;



    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    //  Log.i(TAG, "opencv load success");
                    mOpenCvCamaraview.enableView();
                    break;
                }
                fdefault: {
                    super.onManagerConnected(status);
                }
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_camera);

        mOpenCvCamaraview=(JavaCameraView)findViewById(R.id.MainActivityCameraView);
        mOpenCvCamaraview.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCamaraview.setCvCameraViewListener(this);
        sdRoot = Environment.getExternalStorageDirectory();
        dir = "/DCIM/Data Collection/";
        fileName = "IMG_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()).toString() + ".jpg";

        imageView=(ImageView)findViewById(R.id.image_view);
        ImageButton capture = (ImageButton)findViewById(R.id.imagebutton);
        final Uri mImageCaptreUri = Uri.fromFile(new File(sdRoot, dir+fileName));

        //Capture Image by Button Click
        capture.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                path = mImageCaptreUri.getPath();
                Intent img_intent = new Intent(CameraActivity.this, Image_View.class);
                img_intent.putExtra("ImgURI", path);
                File mkDir = new File(sdRoot, dir);
                mkDir.mkdirs();
                File pictureFile = new File(sdRoot, dir + fileName);

                Imgproc.cvtColor(mIntermediateMat,mIntermediateMat,Imgproc.COLOR_BGR2RGB);
                Imgcodecs.imwrite("/sdcard/" + dir + fileName, mIntermediateMat);
                startActivity(img_intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    public void onResume() {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
    }
    public void onDestroy() {
        super.onDestroy();
        if(mOpenCvCamaraview!=null){
            mOpenCvCamaraview.disableView();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        Mat image= new Mat(height, width, CvType.CV_8UC4);
        Mat mIntermediateMat =new Mat();
        contour = new ArrayList<MatOfPoint>();

    }

    @Override
    public void onCameraViewStopped() {
        //    image.release();
        //    mIntermediateMat.release();

    }

    @Override
    public Mat onCameraFrame   (CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mOpenCvCamaraview.enableFpsMeter();
        mIntermediateMat=inputFrame.rgba();
        findSquares(inputFrame.gray().clone(), squares);
        Mat image = inputFrame.rgba();

        Imgproc.drawContours(image, squares, -1, new Scalar(255, 0, 0), 8);
        return image;


    }
    void findSquares( Mat image, List<MatOfPoint> squares ) {
        squares.clear();
        Mat smallerImg = new Mat(new Size(image.width() / 2, image.height() / 2), image.type());
        Mat gray0 = new Mat(image.size(), CvType.CV_8U);
        Mat gray;
        gray = image.clone();

        // down-scale and upscale the image to filter out the noise
        Imgproc.pyrDown(image, smallerImg, smallerImg.size());
        Imgproc.pyrUp(smallerImg, image, image.size());
        // find squares in every color plane of the image
        // try several threshold levels
        //Cany removed... Didn't work so well
        Imgproc.Canny(image,gray0,20,100);
        Imgproc.GaussianBlur(gray0, gray0, new Size(3, 3), 3);
        contour.clear();
        //Imgproc.threshold(gray, gray0, (l + 1) * 255 / N, 255,  Imgproc.THRESH_BINARY);
        // find contours and store them all as a list
        Imgproc.findContours(gray0, contour, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        MatOfPoint approx = new MatOfPoint();
        // test each contour
        for (int i = 0; i < contour.size(); i++) {
            // approximate contour with accuracy proportional
            // to the contour perimeter
            approx = approxPolyDP(contour.get(i), Imgproc.arcLength(new MatOfPoint2f(contour.get(i).toArray()), true) * 0.02, true);
            double Area = Math.abs(Imgproc.contourArea(approx));
            if(i == 0) maxArea = Area;

            if (approx.toArray().length == 4 && Math.abs(Imgproc.contourArea(approx)) > 2000 && Imgproc.isContourConvex(approx) && Area >= maxArea) {
                maxArea = Area;
                maxIdx = i;
                //       pt1 = (Point)v
            }
        }
        try{
            MatOfPoint app = approxPolyDP(contour.get(maxIdx), Imgproc.arcLength(new MatOfPoint2f(contour.get(maxIdx).toArray()), true) * 0.02, true);
            if(app.toArray().length == 4 && Math.abs(Imgproc.contourArea(app)) > 2000 && Imgproc.isContourConvex(app)) squares.add(app);
        }catch (IndexOutOfBoundsException e){
            e.printStackTrace();
        }
    }
    void extractChannel(Mat source, Mat out, int channelNum) {
        List<Mat> sourceChannels=new ArrayList<Mat>();
        List<Mat> outChannel=new ArrayList<Mat>();
        Core.split(source, sourceChannels);
        outChannel.add(new Mat(sourceChannels.get(0).size(),sourceChannels.get(0).type()));
        Core.mixChannels(sourceChannels, outChannel, new MatOfInt(channelNum,0));
        Core.merge(outChannel, out);
    }

    MatOfPoint approxPolyDP(MatOfPoint curve, double epsilon, boolean closed) {
        MatOfPoint2f tempMat=new MatOfPoint2f();
        Imgproc.approxPolyDP(new MatOfPoint2f(curve.toArray()), tempMat, epsilon, closed);
        return new MatOfPoint(tempMat.toArray());
    }


}*/
