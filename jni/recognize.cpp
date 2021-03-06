#include "geometry.h"
using namespace std;
using namespace cv;

class Recognizer{
private:
	Mat cameraMat, distMat, map1, map2;
	bool processImages;
	vector<Mat> tags;
	int outline, normSize;
	double maxTagSize, minTagSize;
    int blockSize;
    double adaptThresh;
	Point2f norm2DPts[4];
	int tagSize, tagKernel;
	bool preview;
	struct PointStr{
		int x;
		int y;
	};
	int getTagId(Mat normTag){
		int result=0;
		switch(this->rotation){
		case(ROTATION_PORTRAIT):
			for(int C=0;C<tagSize-1;C++){
				for(int R=4;R>=0;R--){
					int r=R*tagKernel;
					int c=C*tagKernel;
					Mat roi = normTag(Range(r,r+tagKernel-1),Range(c,c+tagKernel-1));
					Scalar res = mean(roi);
					if((R==0 || C==0 || R==tagSize-1 || C==tagSize-1)){
						if(res.val[0]>122){
							return -1;
						}
					}else{
						if(R==1 && C==1){
							if(res.val[0]>122){
								return -1;
							}
						}else if(R==1 && C==tagSize-2){
							if(res.val[0]>122){
								return -1;
							}
						}else if(R==tagSize-2 && C==1){
							if(res.val[0]<122){
								return -1;
							}
						}else if(R==tagSize-2 && C==tagSize-2){
							if(res.val[0]>122){
								return -1;
							}
						}else{
							result=result*2;
							if(res.val[0]<122){
								result+=1;
							}
						}
					}
				}
			}
		break;
		case(ROTATION_LANDSCAPE):
			for(int R=0;R<tagSize;R++){
				for(int C=0;C<tagSize;C++){
					int r=R*tagKernel;
					int c=C*tagKernel;
					Mat roi = normTag(Range(r,r+tagKernel-1),Range(c,c+tagKernel-1));
					Scalar res = mean(roi);
					if((R==0 || C==0 || R==tagSize-1 || C==tagSize-1)){
						if(res.val[0]>122){
							return -1;
						}
					}else if(R==1 && C==1){
						if(res.val[0]<122){
							return -1;
						}
					}else if(R==1 && C==tagSize-2){
						if(res.val[0]>122){
							return -1;
						}
					}else if(R==tagSize-2 && C==1){
						if(res.val[0]>122){
							return -1;
						}
					}else if(R==tagSize-2 && C==tagSize-2){
						if(res.val[0]>122){
							return -1;
						}
					}else{
						result=result*2;
						if(res.val[0]<122){
							result+=1;
						}
					}
				}
			}
		break;
		case(ROTATION_LANDS_UPSIDE_DOWN):
			for(int R=tagSize-1;R>=0;R--){
				for(int C=tagSize-1;C>=0;C--){
					int r=R*tagKernel;
					int c=C*tagKernel;
					Mat roi = normTag(Range(r,r+tagKernel-1),Range(c,c+tagKernel-1));
					Scalar res = mean(roi);
					if((R==0 || C==0 || R==tagSize-1 || C==tagSize-1)){
						if(res.val[0]>122){
							return -1;
						}
					}else if(R==1 && C==1){
						if(res.val[0]>122){
							return -1;
						}
					}else if(R==1 && C==tagSize-2){
						if(res.val[0]>122){
							return -1;
						}
					}else if(R==tagSize-2 && C==1){
						if(res.val[0]>122){
							return -1;
						}
					}else if(R==tagSize-2 && C==tagSize-2){
						if(res.val[0]<122){
							return -1;
						}
					}else{
						result=result*2;
						if(res.val[0]<122){
							result+=1;
						}
					}
				}
			}
		break;
		}
		return result+1;
	}
public:
	Size imageSize;
	int rotation;
	Recognizer(Mat cameraMat, Mat distMat, Size imageSize){
		this->outline = (imageSize.width + imageSize.height) * 2;
		this->distMat = distMat;
		this->cameraMat = cameraMat;
		this->imageSize = imageSize;
		this->blockSize = 45;
		this->adaptThresh = 5.0;
		this->maxTagSize = 0.95;
		this->minTagSize = 0.01;
		this->normSize = 50;
		this->tagSize = 5;
		this->preview = true;
		this->rotation = 0;



		this->map1 = Mat(imageSize,CV_32FC1);
		this->map2 = Mat(imageSize,CV_32FC1);
		this->processImages = false;
		this->norm2DPts[0] = Point2f(0,0);
		this->norm2DPts[1] = Point2f(normSize-1,0);
		this->norm2DPts[2] = Point2f(normSize-1,normSize-1);
		this->norm2DPts[3] = Point2f(0,normSize-1);
		this->tagKernel = normSize / tagSize;
	}
	~Recognizer(){

	}
	void addTagToSet(Mat tag){
		this->tags.push_back(tag);
	}
	vector<Geometry::Tag> recognizeTags(Mat&mYuv){
	    Mat mGray(mYuv.rows,mYuv.cols,CV_8UC3);
	    cvtColor(mYuv,mGray,COLOR_YUV2GRAY_NV21);
	    Mat mBin;
	    adaptiveThreshold(mGray,mBin,255,CV_ADAPTIVE_THRESH_GAUSSIAN_C,CV_THRESH_BINARY_INV,blockSize,adaptThresh);
	    //dilate(mBin,mBin,Mat());
		vector<vector<Point> > contours;
	    findContours(mBin,contours, CV_RETR_EXTERNAL, CV_CHAIN_APPROX_SIMPLE);
	    unsigned int size = 0;
	    vector<vector<Point> >::iterator itVVP;
	    vector<Geometry::Tag> result;
	    for(itVVP = contours.begin(); itVVP!=contours.end(); itVVP++){
	    	double len = arcLength(*itVVP,true);
	    	if(len > minTagSize*outline && len < maxTagSize*outline){
	    		vector<Point> polygon;
	    		approxPolyDP(*itVVP,polygon,len*0.02,true);
	    		if(polygon.size()==4 && isContourConvex(Mat (polygon))){
	    			int minY, maxY, minX, maxX;
	    			minY = maxY = polygon.at(0).y;
	    			minX = maxX = polygon.at(0).x;
	    		    vector<cv::Point>::iterator itVP;
    				vector<Point2f> refinedVertices;
    				int counter;
    				double d;
    				double dmin=(4*outline);
    				int v1=-1;
	    			for(itVP = polygon.begin(); itVP!=polygon.end();itVP++,counter++){
	    				if(itVP->x>maxX){
	    					maxX = itVP->x;
	    				}
	    				if(itVP->x<minX){
	    					minX = itVP->x;
	    				}
	    				if(itVP->y>maxY){
	    					maxY = itVP->y;
	    				}
	    				if(itVP->y<minY){
	    					minY = itVP->y;
	    				}
						refinedVertices.push_back(*itVP);
						d = norm(*itVP);
						if (d<dmin) {
						dmin=d;
						v1=counter;
						}
	    			}
	    			Rect roi(minX, minY, maxX-minX+1, maxY-minY+1);
    				//cornerSubPix(mGray, refinedVertices, Size(3,3), Size(-1,-1), TermCriteria(1, 3, 1));
	    			Point2f roi2DPts[4];
					for(counter=0; counter<4;counter++){
						roi2DPts[counter] = Point2f(refinedVertices.at((4+v1-counter)%4).x - minX, refinedVertices.at((4+v1-counter)%4).y - minY);
					}
    				Mat homo(3,3,CV_32F);
    				homo = getPerspectiveTransform(roi2DPts,norm2DPts);
    				Mat subImg = mGray(cv::Range(roi.y, roi.y+roi.height), cv::Range(roi.x, roi.x+roi.width));
    				Mat normROI = Mat(normSize,normSize,CV_8UC1);
    				warpPerspective( subImg, normROI, homo, Size(normSize,normSize));
    				int id = getTagId(normROI);

    				vector<int>::iterator it;
    				if(id!=-1){
        				Geometry::Tag tag;
						tag.setPoints(refinedVertices,rotation,Size(mGray.cols,mGray.rows), len);
						tag.preview = normROI;
						tag.id = id;
						result.push_back(tag);
    				}
	    		}
	    	}
	    }
		return result;
	}
	void notifyImageSizeChanged(Size newSize,int rotation){
		double scaleW = imageSize.width / newSize.width;
		double scaleH = imageSize.height / newSize.height;
		this->processImages = false;
		this->imageSize = newSize;
		this->rotation = rotation;
		this->outline = (imageSize.width + imageSize.height) / 2;
		//this->cameraMat.at<double>(0,2,0) = this->cameraMat.at<double>(0,2,0) * scaleW;
		//this->cameraMat.at<double>(1,2,0) = this->cameraMat.at<double>(1,2,0) * scaleH;
		//this->cameraMat.at<double>(2,2,0) =  (scaleH/scaleW) * this->cameraMat.at<double>(2,2,0); // 3,3 camera matrix element is an aspect ratio

		initUndistortRectifyMap(cameraMat,distMat,Mat(),cameraMat,imageSize,CV_32FC1,map1, map2);
		convertMaps(this->map1,this->map2,this->map1,this->map2,CV_16SC2);
		this->processImages = true;
	}
	void remap(Mat &rgbFrame){
		if(processImages){
			cv::remap(rgbFrame,rgbFrame, map1, map2, CV_INTER_LINEAR);
		}
	}


};
extern "C"{
JNIEXPORT jlong JNICALL Java_simple_as_fuck_objecttrackerv2_recognizer_Recognizer_newRecognizerNtv(JNIEnv* env, jobject\
		, jlong cameraMatAddr, jlong distortionMatAddr, jint width, jint height){
    Mat& cameraMat = *(Mat*)cameraMatAddr;
    Mat& distortionMat = *(Mat*)distortionMatAddr;
    Recognizer*recognizer = new Recognizer(cameraMat,distortionMat, Size(width,height));
	return (jlong)recognizer;
}
JNIEXPORT void JNICALL Java_simple_as_fuck_objecttrackerv2_recognizer_Recognizer_delRecognizerNtv(JNIEnv* env, jobject\
		,jlong calibratorAddr){
	if(calibratorAddr != 0){
		Recognizer*recognizer = (Recognizer*)calibratorAddr;
		delete recognizer;
	}
}
JNIEXPORT jobject JNICALL Java_simple_as_fuck_objecttrackerv2_recognizer_Recognizer_remapFrameNtv(JNIEnv* env, jobject\
		,jlong recognizerAddr,jlong addrYuv, jint rotation){
    Mat& mYuv = *(Mat*)addrYuv;
	if(recognizerAddr != 0){
		Recognizer*recognizer = (Recognizer*)recognizerAddr;
		Mat mRGB(mYuv.rows,mYuv.cols,CV_8UC3);
		cvtColor(mYuv,mRGB,COLOR_YUV2RGB_NV21);
		recognizer->remap(mRGB);
		jobject result = env->NewObject(jMatCls,jMatConsID);
		Mat&cMat=*(Mat*)env->CallLongMethod(result,jMatGetNatAddr);
		rotateMat(mRGB,rotation);
		cMat = mRGB;
		return result;
	}else{
		return NULL;
	}
}
JNIEXPORT jobjectArray JNICALL Java_simple_as_fuck_objecttrackerv2_recognizer_Recognizer_findTagsNtv(JNIEnv* env, jobject\
		,jlong recognizerAddr,jlong addrYuv){
	Mat& mYuv = *(Mat*)addrYuv;
	if(recognizerAddr != 0){
		Recognizer*recognizer = (Recognizer*)recognizerAddr;
		vector<Geometry::Tag> tags = recognizer->recognizeTags(mYuv);
		if(tags.size()>0){
			jobjectArray result = env->NewObjectArray(tags.size(),jTagCls,tags.at(0).toJava(env,recognizer->rotation,recognizer->imageSize));
			for(int c=1;c<tags.size();c++){
				env->SetObjectArrayElement(result,c,tags.at(c).toJava(env,recognizer->rotation,recognizer->imageSize));
			}
			return result;
		}else{
			return NULL;
		}
	}else{
		return NULL;
	}
}
JNIEXPORT void JNICALL Java_simple_as_fuck_objecttrackerv2_recognizer_Recognizer_notifySizeChangedNtv(JNIEnv* env, jobject\
		,jlong recognizerAddr,jint width, jint height, jint rotation){
	if(recognizerAddr != 0){
		Recognizer*recognizer = (Recognizer*)recognizerAddr;
		Size size(width,height);
		recognizer->notifyImageSizeChanged(size, rotation);
	}
}


}
