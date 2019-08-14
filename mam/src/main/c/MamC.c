#include <jni.h>

#include <mam/api/api.h>


#ifndef _Included_org_iota_jota_c_MamC
#define _Included_org_iota_jota_c_MamC
#ifdef __cplusplus
extern "C" {
#endif
#undef org_iota_jota_c_MamC_MAM_SPONGE_KEY_SIZE
#define org_iota_jota_c_MamC_MAM_SPONGE_KEY_SIZE 243L
#undef org_iota_jota_c_MamC_MAM_PSK_ID_SIZE
#define org_iota_jota_c_MamC_MAM_PSK_ID_SIZE 81L
#undef org_iota_jota_c_MamC_MAM_PSK_KEY_SIZE
#define org_iota_jota_c_MamC_MAM_PSK_KEY_SIZE 243L
#undef org_iota_jota_c_MamC_MAM_NTRU_ID_SIZE
#define org_iota_jota_c_MamC_MAM_NTRU_ID_SIZE 81L
#undef org_iota_jota_c_MamC_MAM_NTRU_PK_SIZE
#define org_iota_jota_c_MamC_MAM_NTRU_PK_SIZE 9216L
#undef org_iota_jota_c_MamC_MAM_NTRU_SK_SIZE
#define org_iota_jota_c_MamC_MAM_NTRU_SK_SIZE 1024L
#undef org_iota_jota_c_MamC_MAM_NTRU_KEY_SIZE
#define org_iota_jota_c_MamC_MAM_NTRU_KEY_SIZE 243L
#undef org_iota_jota_c_MamC_MAM_NTRU_EKEY_SIZE
#define org_iota_jota_c_MamC_MAM_NTRU_EKEY_SIZE 9216L
/*
 * Class:     org_iota_jota_c_MamC
 * Method:    mam_api_serialized_size
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_org_iota_jota_c_MamC_mam_1api_1serialized_1size
  (JNIEnv *env, jclass clazz){
  	return 5;
  }

/*
 * Class:     org_iota_jota_c_MamC
 * Method:    mam_api_serialize
 * Signature: ([BLjava/lang/String;J)V
 */
JNIEXPORT void JNICALL Java_org_iota_jota_c_MamC_mam_1api_1serialize
  (JNIEnv *env, jclass clazz, jbyteArray byteArray, jstring text, jlong longValue){

  }

/*
 * Class:     org_iota_jota_c_MamC
 * Method:    mam_api_deserialize
 * Signature: ([BJLjava/lang/String;J)I
 */
JNIEXPORT jint JNICALL Java_org_iota_jota_c_MamC_mam_1api_1deserialize
  (JNIEnv *env, jclass clazz, jbyteArray byteArray, jlong longValue1, jstring text, jlong longValue2){
  	return 5;
  }

/*
 * Class:     org_iota_jota_c_MamC
 * Method:    mam_api_save
 * Signature: (Ljava/lang/String;Ljava/lang/String;J)I
 */
JNIEXPORT jint JNICALL Java_org_iota_jota_c_MamC_mam_1api_1save
  (JNIEnv *env, jclass clazz, jstring text1, jstring text2, jlong longValue){
  	return 5;
  }

/*
 * Class:     org_iota_jota_c_MamC
 * Method:    mam_api_load
 * Signature: (Ljava/lang/String;Ljava/lang/String;J)I
 */
JNIEXPORT jint JNICALL Java_org_iota_jota_c_MamC_mam_1api_1load
  (JNIEnv *env, jclass clazz, jstring text1, jstring text2, jlong longValue){
  	return 5;
  }

/*
 * Class:     org_iota_jota_c_MamC
 * Method:    mam_api_init
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_org_iota_jota_c_MamC_mam_1api_1init
  (JNIEnv *env, jclass clazz, jstring text){
  	return 5;
  }

/*
 * Class:     org_iota_jota_c_MamC
 * Method:    mam_api_channel_create
 * Signature: (JLjava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_org_iota_jota_c_MamC_mam_1api_1channel_1create
  (JNIEnv *env, jclass clazz, jlong longValue, jstring text){
  	return 5;
  }

/*
 * Class:     org_iota_jota_c_MamC
 * Method:    mam_api_endpoint_create
 * Signature: (JLjava/lang/String;Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_org_iota_jota_c_MamC_mam_1api_1endpoint_1create
  (JNIEnv *env, jclass clazz, jlong longValue, jstring text1, jstring text2){
  	return 5;
  }

/*
 * Class:     org_iota_jota_c_MamC
 * Method:    mam_api_bundle_write_header_on_channel
 * Signature: (Ljava/lang/String;[Lorg/iota/jota/c/MamC/mam_psk_t_set_entry_t;[Lorg/iota/jota/c/MamC/mam_ntru_pk_t_set_entry_t;Lorg/iota/jota/model/Bundle;[B)I
 */
JNIEXPORT jint JNICALL Java_org_iota_jota_c_MamC_mam_1api_1bundle_1write_1header_1on_1channel
  (JNIEnv *env, jclass clazz, jstring text, jobject javaObjArray1, jobject javaObjArray2, jobject obj, jbyteArray byteArray){
  	return 5;
  }

/*
 * Class:     org_iota_jota_c_MamC
 * Method:    mam_api_bundle_write_header_on_endpoint
 * Signature: (Ljava/lang/String;Ljava/lang/String;[Lorg/iota/jota/c/MamC/mam_psk_t_set_entry_t;[Lorg/iota/jota/c/MamC/mam_ntru_pk_t_set_entry_t;Lorg/iota/jota/model/Bundle;[B)I
 */
JNIEXPORT jint JNICALL Java_org_iota_jota_c_MamC_mam_1api_1bundle_1write_1header_1on_1endpoint
  (JNIEnv *env, jclass clazz, jstring text1, jstring text2, jobject javaObjArray1, jobject javaObjArray2, jobject obj, jbyteArray byteArray){
  	return 5;
  }

/*
 * Class:     org_iota_jota_c_MamC
 * Method:    mam_api_bundle_read
 * Signature: (Lorg/iota/jota/model/Bundle;[Ljava/lang/String;JZ)I
 */
JNIEXPORT jint JNICALL Java_org_iota_jota_c_MamC_mam_1api_1bundle_1read
  (JNIEnv *env, jclass clazz, jobject obj, jobject javaObjArray, jlong longValue, jboolean bool){
  	return 5;
  }

/*
 * Class:     org_iota_jota_c_MamC
 * Method:    mam_api_bundle_write_packet
 * Signature: ([BLjava/lang/String;JLjava/lang/String;ZLorg/iota/jota/model/Bundle;)I
 */
JNIEXPORT jint JNICALL Java_org_iota_jota_c_MamC_mam_1api_1bundle_1write_1packet
  (JNIEnv *env, jclass clazz, jbyteArray byteArray, jstring text1, jlong longValue, jstring text2, jboolean bool, jobject obj){
  	return 5;
  }

#ifdef __cplusplus
}
#endif
#endif
