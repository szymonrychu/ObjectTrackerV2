#ifndef GEOMETRY_H
#define GEOMETRY_H

#include "helper.h"

static const int ROTATION_LANDSCAPE=1;
static const int ROTATION_PORTRAIT=0;
static const int ROTATION_PORTR_UPSIDE_DOWN=2;
static const int ROTATION_LANDS_UPSIDE_DOWN=3;


class Geometry{
private:
public:
	class Point{
	public:
		Point(){
			this->x = 0;
			this->y = 0;
		}
		int x;
		int y;
		jobject toJava(JNIEnv* env, int rotation, Size matSize){
			jfieldID xID = env->GetFieldID(jPointCls,"x","I");
			jfieldID yID = env->GetFieldID(jPointCls,"y","I");
			jobject result = env->NewObject(jPointCls,jPointConsID);
			switch(rotation){
			case(ROTATION_LANDSCAPE):
				env->SetIntField(result,xID,x);
				env->SetIntField(result,yID,y);
			break;
			case(ROTATION_PORTRAIT):
				env->SetIntField(result,xID,matSize.height - y);
				env->SetIntField(result,yID,x);
			break;
			case(ROTATION_LANDS_UPSIDE_DOWN):
				env->SetIntField(result,xID,matSize.width - x);
				env->SetIntField(result,yID,matSize.height - y);
			break;
			case(ROTATION_PORTR_UPSIDE_DOWN):
				env->SetIntField(result,xID,y);
				env->SetIntField(result,yID,matSize.width - x);
			break;
			}
			return result;
		}
	};
	class Tag{
	public:
		int id;
		Mat preview;
		Mat homo;
		vector<Geometry::Point> points;
		void setPoints(vector<Point2f> points,int rotation, Size matSize){
			vector<Point2f>::iterator it;
			for(it=points.begin();it!=points.end();it++){
				Geometry::Point point;
				point.x = it->x;
				point.y = it->y;
				this->points.push_back(point);
			}
		}
		jobject toJava(JNIEnv*env, int rotation, Size matSize){
			jfieldID idID = env->GetFieldID(jTagCls,"id","I");
			jfieldID homoID = env->GetFieldID(jTagCls,"homo","Lorg/opencv/core/Mat;");
			jfieldID previewID = env->GetFieldID(jTagCls,"preview","Lorg/opencv/core/Mat;");
			jfieldID pointsID = env->GetFieldID(jTagCls,"points","[Lsimple/as/fuck/objecttrackerv2/geomerty/Point;");

			jobject result = env->NewObject(jTagCls,jTagConsID);

			env->SetIntField(result,idID,this->id);
			env->SetObjectField(result,homoID,mat2JMat(env,this->homo));
			env->SetObjectField(result,previewID,mat2JMat(env,this->preview));
			jobjectArray pointsJArray = env->NewObjectArray(this->points.size(),jPointCls,points.at(0).toJava(env,rotation,matSize));
			for(int c=1;c<this->points.size();c++){
				env->SetObjectArrayElement(pointsJArray,c,points.at(c).toJava(env,rotation,matSize));
			}
			jobject pointsJ = reinterpret_cast<jobject>(pointsJArray);
			env->SetObjectField(result,pointsID,pointsJ);
			env->DeleteLocalRef(pointsJ);
			return result;
		}
	};


};
#endif
