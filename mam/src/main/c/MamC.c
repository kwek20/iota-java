#include <jni.h>
#include <stdlib.h>

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

mam_api_t api;

void set_long_field(JNIEnv *env, jobject javaObj, char *package, char *field, size_t value){
  	jclass ent_clazz = (*env)->FindClass(env, package);
	if (!ent_clazz) {
		return;
	}
	
	jfieldID fid = (*env)->GetFieldID(env, ent_clazz, field, "J");
	if (!fid) {
		return;
	}
	
	// Set the long field
	(*env)->SetLongField(env, javaObj, fid, value);
	if ((*env)->ExceptionCheck(env) != JNI_FALSE) {
		(*env)->ExceptionDescribe(env);
	    (*env)->ExceptionClear(env);
	    return;
	}
}
  
void set_string_field(JNIEnv *env, jobject javaObj, char *package, char *field, char *text){
  	jclass ent_clazz = (*env)->FindClass(env, package);
	if (!ent_clazz) return;
	
	jfieldID fid = (*env)->GetFieldID(env, ent_clazz, field, "Ljava/lang/String;");
	if (!fid) return;
	
	jstring jstr = (*env)->NewStringUTF(env, text);
	if (!jstr || (*env)->ExceptionCheck(env) != JNI_FALSE) {
	    (*env)->ExceptionClear(env);
	    return;
	}

	// Set the String field
	(*env)->SetObjectField(env, javaObj, fid, jstr);
}

/*
 * Class:     org_iota_jota_c_MamC
 * Method:    mam_api_serialized_size
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_org_iota_jota_c_MamC_mam_1api_1serialized_1size
  (JNIEnv *env, jclass clazz, jobject returnObject){
  	size_t size =  mam_api_serialized_size(&api);
  	set_long_field(env, returnObject, "org/iota/jota/c/dto/ReturnSerialsedSize", "size", size);
  	return 0;
  }

/*
 * Class:     org_iota_jota_c_MamC
 * Method:    mam_api_serialize
 * Signature: ([BLjava/lang/String;J)V
 */
JNIEXPORT jlong JNICALL Java_org_iota_jota_c_MamC_mam_1api_1serialize
  (JNIEnv *env, jclass clazz, jbyteArray byteArray, jstring encryptionKey, jlong keySize){
  	size_t serialized_size = mam_api_serialized_size(&api);
	trit_t *buffer = malloc(serialized_size * sizeof(trit_t));

   	const tryte_t *key = (tryte_t *) (*env)->GetStringUTFChars(env, encryptionKey, NULL);
	mam_api_serialize(&api, buffer, key, keySize);
	return  6;
  }

/*
 * Class:     org_iota_jota_c_MamC
 * Method:    mam_api_deserialize
 * Signature: ([BJLjava/lang/String;J)I
 */
JNIEXPORT jint JNICALL Java_org_iota_jota_c_MamC_mam_1api_1deserialize
  (JNIEnv *env, jclass clazz, jbyteArray buffer, jlong serialized_size, jstring encryptionKey, jlong keySize){
  	const tryte_t *key = (tryte_t *) (*env)->GetStringUTFChars(env, encryptionKey, NULL);
  	
  	retcode_t ret = mam_api_deserialize(buffer, serialized_size, &api, key, keySize);
	free(buffer);
	return ret;
  }

/*
 * Class:     org_iota_jota_c_MamC
 * Method:    mam_api_save
 * Signature: (Ljava/lang/String;Ljava/lang/String;J)I
 */
JNIEXPORT jint JNICALL Java_org_iota_jota_c_MamC_mam_1api_1save
  (JNIEnv *env, jclass clazz, jstring fileName, jstring encryptionKey, jlong keySize){
  	const tryte_t *key = (tryte_t *) (*env)->GetStringUTFChars(env, encryptionKey, NULL);
  	const char *buffer = (*env)->GetStringUTFChars(env, fileName, NULL);
  	
  	return mam_api_save(&api, buffer, key, keySize);
  }

/*
 * Class:     org_iota_jota_c_MamC
 * Method:    mam_api_load
 * Signature: (Ljava/lang/String;Ljava/lang/String;J)I
 */
JNIEXPORT jint JNICALL Java_org_iota_jota_c_MamC_mam_1api_1load
  (JNIEnv *env, jclass clazz, jstring fileName, jstring encryptionKey, jlong keySize){
  	const tryte_t *key = (tryte_t *) (*env)->GetStringUTFChars(env, encryptionKey, NULL);
  	const char *buffer = (*env)->GetStringUTFChars(env, fileName, NULL);
  	
  	return mam_api_load(buffer, &api, key, keySize);
  }

/*
 * Class:     org_iota_jota_c_MamC
 * Method:    mam_api_init
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_org_iota_jota_c_MamC_mam_1api_1init(JNIEnv *env, jclass clazz, jstring seed){
  	const char *inCStr = (*env)->GetStringUTFChars(env, seed, NULL);
  	tryte_t *trytes = malloc(sizeof(tryte_t) * sizeof(inCStr));
  	
  	return mam_api_init(&api, trytes);
  }

/*
 * Class:     org_iota_jota_c_MamC
 * Method:    mam_api_channel_create
 * Signature: (JLjava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_org_iota_jota_c_MamC_mam_1api_1channel_1create
  (JNIEnv *env, jclass clazz, jlong height, jstring channelId){
  	tryte_t channel_id[MAM_CHANNEL_ID_TRYTE_SIZE];
  	
  	return mam_api_channel_create(&api, height, channel_id);
  }

/*
 * Class:     org_iota_jota_c_MamC
 * Method:    mam_api_endpoint_create
 * Signature: (JLjava/lang/String;Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_org_iota_jota_c_MamC_mam_1api_1endpoint_1create
  (JNIEnv *env, jclass clazz, jlong height, jstring channelId, jstring endpointId){
  	const tryte_t *channel_id = (tryte_t *) (*env)->GetStringUTFChars(env, channelId, NULL);
  	
	// Out
  	tryte_t endpoint_id[MAM_ENDPOINT_ID_TRYTE_SIZE];
  	
	return mam_api_endpoint_create(&api, height, channel_id, endpoint_id);
  }

/*
 * Class:     org_iota_jota_c_MamC
 * Method:    mam_api_bundle_write_header_on_channel
 * Signature: (Ljava/lang/String;[Lorg/iota/jota/c/MamC/mam_psk_t_set_entry_t;[Lorg/iota/jota/c/MamC/mam_ntru_pk_t_set_entry_t;Lorg/iota/jota/model/Bundle;[B)I
 */
JNIEXPORT jint JNICALL Java_org_iota_jota_c_MamC_mam_1api_1bundle_1write_1header_1on_1channel
  (JNIEnv *env, jclass clazz, jstring channelId, jobject psks, jobject ntru_pks, jobject bundle, jbyteArray msgId){
  	
  	/*const tryte_t *channel_id = (tryte_t *) (*env)->GetStringUTFChars(env, channelId, NULL);
  	
  	bundle_transactions_t *bundle = NULL;
	trit_t message_id[MAM_MSG_ID_SIZE];
	
	bundle_transactions_new(&bundle);
	
	return mam_api_bundle_write_header_on_channel(&api, channel_id, NULL, NULL, bundle, message_id);*/
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
  (JNIEnv *env, jclass clazz, jobject obj, jobject javaObjArray, jlong longValue, jboolean boolValue){
  	return 5;
  }

/*
 * Class:     org_iota_jota_c_MamC
 * Method:    mam_api_bundle_write_packet
 * Signature: ([BLjava/lang/String;JLjava/lang/String;ZLorg/iota/jota/model/Bundle;)I
 */
JNIEXPORT jint JNICALL Java_org_iota_jota_c_MamC_mam_1api_1bundle_1write_1packet
  (JNIEnv *env, jclass clazz, jbyteArray byteArray, jstring text1, jlong longValue, jstring text2, jboolean boolValue, jobject obj){
  	return 5;
  }

#ifdef __cplusplus
}
#endif
#endif
