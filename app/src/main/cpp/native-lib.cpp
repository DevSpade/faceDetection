#include <jni.h>

#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>

using namespace cv;
extern "C" {
JNIEXPORT void JNICALL
Java_com_example_dev_facedetect_MainActivity_ConvertRGBtoGray(JNIEnv *env, jobject instance,
                                                              jlong matAddrInput,
                                                              jlong matAddrResult) {
    // TODO
    Mat &matInput = *(Mat *)matAddrInput;
    Mat &matResult = *(Mat *)matAddrResult;

    cvtColor(matInput, matResult, CV_RGBA2GRAY);
    Canny(matResult,matResult,10,50,3,false);
    //cvCanny(matInput,matResult,10,50,3);
}
}