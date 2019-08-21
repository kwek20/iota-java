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

void set_bool_field(JNIEnv *env, jobject javaObj, char *package, char *field, bool value){
	jclass ent_clazz = (*env)->FindClass(env, package);
	if (!ent_clazz) {
		return;
	}
	
	jfieldID fid = (*env)->GetFieldID(env, ent_clazz, field, "Z");
	if (!fid) {
		return;
	}
	
	// Set the bool field
	(*env)->SetBooleanField(env, javaObj, fid, value);
	if ((*env)->ExceptionCheck(env) != JNI_FALSE) {
		(*env)->ExceptionDescribe(env);
	    (*env)->ExceptionClear(env);
	    return;
	}
}

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

void call_string_setter(JNIEnv *env, jobject javaObj, char *package, char *method, signed char *text){
  	jclass ent_clazz = (*env)->FindClass(env, package);
	if (!ent_clazz) return;
	
	jmethodID mid = (*env)->GetMethodID(env, ent_clazz, method, "([java/lang/String;)V");
	if (!mid) return;
	
	jstring jstr = (*env)->NewStringUTF(env, text);
	if (!jstr || (*env)->ExceptionCheck(env) != JNI_FALSE) {
	    (*env)->ExceptionDescribe(env);
	    (*env)->ExceptionClear(env);
	    return;
	}
	
	(*env)->CallVoidMethod(env, javaObj, mid, jstr);
}

void set_string_field(JNIEnv *env, jobject javaObj, char *package, char *field, signed char *text){
  	jclass ent_clazz = (*env)->FindClass(env, package);
	if (!ent_clazz) return;
	
	jfieldID fid = (*env)->GetFieldID(env, ent_clazz, field, "Ljava/lang/String;");
	if (!fid) return;
	
	jstring jstr = (*env)->NewStringUTF(env, text);
	if (!jstr || (*env)->ExceptionCheck(env) != JNI_FALSE) {
	    (*env)->ExceptionDescribe(env);
	    (*env)->ExceptionClear(env);
	    return;
	}

	// Set the String field
	(*env)->SetObjectField(env, javaObj, fid, jstr);
}

void call_byte_setter(JNIEnv *env, jobject javaObj, char *package, char *method, signed byte *bytes){
	jclass ent_clazz = (*env)->FindClass(env, package);
	if (!ent_clazz) return;
	
	jmethodID mid = (*env)->GetMethodID(env, ent_clazz, method, "([B)V");
	if (!mid) return;
	
	(*env)->CallVoidMethod(env, javaObj, mid, bytes);
}

bundle_transactions_t *bundleFromJavaBundle(jobject javaBundle){
	bundle_transactions_t *bundle = NULL;

	bundle_transactions_new(&bundle);
	
	return bundle;
} 

void fillJavaBundleFromC(jobject javaBundle, bundle_transactions_t *bundle){
	
}

/*********************
****** END UTIL ******
**********************/

/*
 * Class:     org_iota_jota_c_MamC
 * Method:    mam_api_init
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jlong JNICALL Java_org_iota_jota_c_MamC_mam_1api_1init(JNIEnv *env, jclass clazz, jstring seed){
  	jboolean seed_isCopy;
  	tryte_t *seed_Str = (tryte_t *) (*env)->GetStringUTFChars(env, seed, &seed_isCopy);
  	
  	retcode_t code = mam_api_init(&api, seed_Str);
  	if (seed_isCopy == JNI_TRUE) {
	    env->ReleaseStringUTFChars(seed, seed_Str);
	}
	return code;
}

/*
 * Class:     org_iota_jota_c_MamC
 * Method:    mam_api_destroy
 * Signature: ()I
 */
JNIEXPORT jlong JNICALL Java_org_iota_jota_c_MamC_mam_1api_1destroy(JNIEnv *env, jclass clazz){
  	return mam_api_destroy(&api);
}

/*
 * Method:    mam_api_add_trusted_channel_pk
 */
JNIEXPORT jlong JNICALL Java_org_iota_jota_c_MamC_mam_1api_1add_1trusted_1channel_pk(JNIEnv *env, jclass clazz, jstring pk){
  	//TODO
  	const char *inCStr = (*env)->GetStringUTFChars(env, pk, NULL);
  	tryte_t *trytes = malloc(sizeof(tryte_t) * sizeof(inCStr));
  	
  	size_t code = mam_api_add_trusted_channel_pk(&api, trytes);
  	
  	return code;
}

/*
 * Method:    mam_api_add_trusted_endpoint_pk
 */
JNIEXPORT jlong JNICALL Java_org_iota_jota_c_MamC_mam_1api_1add_1trusted_1endpoint_1pk(JNIEnv *env, jclass clazz, jstring pk){
  	//TODO
  	const char *inCStr = (*env)->GetStringUTFChars(env, pk, NULL);
  	tryte_t *trytes = malloc(sizeof(tryte_t) * sizeof(inCStr));
  	
  	size_t code = mam_api_add_trusted_endpoint_pk(&api, trytes);
  	return code;
}

/*
 * Method:    mam_api_add_ntru_sk
 */
JNIEXPORT jlong JNICALL Java_org_iota_jota_c_MamC_mam_1api_1add_1ntru_1sk(JNIEnv *env, jclass clazz, jclass ntru_sk){
  	//TODO
  	size_t code = mam_api_add_ntru_sk(&api, ntru_sk);
  	return code;
}

/*
 * Method:    mam_api_add_ntru_pk
 */
JNIEXPORT jlong JNICALL Java_org_iota_jota_c_MamC_mam_1api_1add_1ntru_1pk(JNIEnv *env, jclass clazz, jclass ntru_pk){
	//TODO
  	size_t code = mam_api_add_ntru_pk(&api, ntru_pk);
  	return code;
}

/*
 * Method:    mam_api_add_psk
 */
JNIEXPORT jlong JNICALL Java_org_iota_jota_c_MamC_mam_1api_1add_1psk(JNIEnv *env, jclass clazz, jclass psk){
  	//TODO
  	const char *inCStr = (*env)->GetStringUTFChars(env, psk, NULL);
  	
  	size_t code = mam_api_add_psk(&api, psk);
  	return code;
}

/*
 * Method:    mam_api_channel_create
 */
JNIEXPORT jlong JNICALL Java_org_iota_jota_c_MamC_mam_1api_1channel_1create
  (JNIEnv *env, jclass clazz, jobject returnObject, jlong height){
  	tryte_t channel_id[MAM_CHANNEL_ID_TRYTE_SIZE];
  	
  	retcode_t code = mam_api_channel_create(&api, height, channel_id);
  	set_string_field(env, returnObject, "org/iota/jota/c/dto/MamCreateChannelResponse", "channel_id", &channel_id);
	return code;
}

/*
 * Method:    mam_api_channel_remaining_sks
 */
JNIEXPORT jlong JNICALL Java_org_iota_jota_c_MamC_mam_1api_1channel_1remaining_1sks(JNIEnv *env, jclass clazz, jstring endpointId){
  	jboolean endpoint_id_isCopy;
  	const char *endpoint_id = (*env)->GetStringUTFChars(env, endpointId, &endpoint_id_isCopy);
  	retcode_t ret = mam_api_channel_remaining_sks(&api, endpoint_id);
  	
  	if (endpoint_id_isCopy == JNI_TRUE) {
	    env->ReleaseStringUTFChars(endpointId, endpoint_id);
	}
  	return ret;
}

/*
 * Method:    mam_api_endpoint_create
 */
JNIEXPORT jlong JNICALL Java_org_iota_jota_c_MamC_mam_1api_1endpoint_1create
  (JNIEnv *env, jclass clazz, jobject returnObject, jlong height, jstring channelId){
  	jboolean channel_id_isCopy;
  	const tryte_t *channel_id = (tryte_t *) (*env)->GetStringUTFChars(env, channelId, &channel_id_isCopy);
  	
	// Out
  	tryte_t endpoint_id[MAM_ENDPOINT_ID_TRYTE_SIZE];
  	
	retcode_t code = mam_api_endpoint_create(&api, height, channel_id, endpoint_id);
	set_string_field(env, returnObject, "org/iota/jota/c/dto/MamCreateEndpointResponse", "endpoint_id", &endpoint_id);
	if (channel_id_isCopy == JNI_TRUE) {
	    env->ReleaseStringUTFChars(channelId, channel_id);
	}
	return code;
}
  
/*
 * Method:    mam_api_endpoint_remaining_sks
 */
JNIEXPORT jlong JNICALL Java_org_iota_jota_c_MamC_mam_1api_1endpoint_1remaining_1sks(JNIEnv *env, jclass clazz, jstring channelId, jstring endpointId){
  	jboolean channel_id_isCopy;
  	const char *channel_id = (*env)->GetStringUTFChars(env, channelId, &channel_id_isCopy);
  	
  	jboolean endpoint_id_isCopy;
  	const char *endpoint_id = (*env)->GetStringUTFChars(env, endpointId, &endpoint_id_isCopy);
  	
  	retcode_t ret = mam_api_endpoint_remaining_sks(&api, channel_id, endpoint_id);
  	if (channel_id_isCopy == JNI_TRUE) {
	    env->ReleaseStringUTFChars(channelId, channel_id);
	}
	if (endpoint_id_isCopy == JNI_TRUE) {
	    env->ReleaseStringUTFChars(endpointId, endpoint_id);
	}
	return ret;
}

/*
 * Method:    mam_api_write_tag
 */
JNIEXPORT jlong JNICALL Java_org_iota_jota_c_MamC_mam_1api_1write_1tag(JNIEnv *env, jclass clazz, jobject returnObject, trit_t const *const msg_id, trint18_t const ord){
	trit_t tag[NUM_TRITS_TAG];
	mam_api_write_tag(tag, msg_id, ord);
	
  	call_byte_setter(env, returnObject, "org/iota/jota/c/dto/MamWriteTagResponse", "setByteTag", tag);
	return 0;
}

/*
 * Method:    mam_api_bundle_write_header_on_channel
 */
JNIEXPORT jlong JNICALL Java_org_iota_jota_c_MamC_mam_1api_1bundle_1write_1header_1on_1channel
  (JNIEnv *env, jclass clazz, jobject returnObject, jstring ch_id, jobjectArray psks, jobjectArray ntru_pks, jobject javaBundle){
  	
  	jboolean channel_id_isCopy;
  	const tryte_t *channel_id = (tryte_t *) (*env)->GetStringUTFChars(env, ch_id, &channel_id_isCopy);
  	
  	bundle_transactions_t * bundle = bundleFromJavaBundle(javaBundle);
	
	trit_t message_id[MAM_MSG_ID_SIZE];
	
	retcode_t ret = mam_api_bundle_write_header_on_channel(&api, channel_id, NULL, NULL, bundle, message_id);
	call_byte_setter(env, returnObject, "org/iota/jota/c/dto/MamResponseBundleMessage", "setMessageId", message_id);
	
	if (channel_id_isCopy == JNI_TRUE) {
	    env->ReleaseStringUTFChars(ch_id, channel_id);
	}
	return ret;
}


/*
 * Method:    mam_api_bundle_write_header_on_endpoint
 */
JNIEXPORT jlong JNICALL Java_org_iota_jota_c_MamC_mam_1api_1bundle_1write_1header_1on_1endpoint
  (JNIEnv *env, jclass clazz, jobject returnObject, jstring ch_id, jstring ep_id, jobjectArray psks, jobjectArray ntru_pks, jobject obj, jbyteArray byteArray, jobject javaBundle){
  	jboolean channel_id_isCopy;
  	const tryte_t *channel_id = (tryte_t *) (*env)->GetStringUTFChars(env, ch_id, &channel_id_isCopy);
  	
  	jboolean endpoint_id_isCopy;
  	const tryte_t *endpoint_id = (tryte_t *) (*env)->GetStringUTFChars(env, ep_id, &endpoint_id_isCopy);
  	
  	bundle_transactions_t * bundle = bundleFromJavaBundle(javaBundle);
	trit_t message_id[MAM_MSG_ID_SIZE];
	
	retcode_t code = mam_api_bundle_write_header_on_endpoint(&api, channel_id, endpoint_id, NULL, NULL, bundle, message_id);
	call_byte_setter(env, returnObject, "org/iota/jota/c/dto/MamResponseBundleMessage", "setMessageId", message_id);
	
	if (channel_id_isCopy == JNI_TRUE) {
	    env->ReleaseStringUTFChars(ch_id, channel_id);
	}
	if (endpoint_id_isCopy == JNI_TRUE) {
	    env->ReleaseStringUTFChars(ep_id, endpoint_id);
	}
	return code;
}

/*
 * Method:    mam_api_bundle_announce_channel
 */
JNIEXPORT jlong JNICALL Java_org_iota_jota_c_MamC_mam_1api_1bundle_1announce_1channel
	(JNIEnv *env, jclass clazz, jobject returnObject, jstring ch_id, jstring new_ch_id, jobject psks, jobject ntru_pks, jobject javaBundle){
	
  	jboolean channel_id_isCopy;
  	const tryte_t *channel_id = (tryte_t *) (*env)->GetStringUTFChars(env, ch_id, &channel_id_isCopy);
  	
  	jboolean new_channel_id_isCopy;
  	const tryte_t *new_channel_id = (tryte_t *) (*env)->GetStringUTFChars(env, new_ch_id, &new_channel_id_isCopy);
  	
  	bundle_transactions_t * bundle = bundleFromJavaBundle(javaBundle);
  	
	trit_t message_id[MAM_MSG_ID_SIZE];
	
	retcode_t code = mam_api_bundle_announce_channel(&api, channel_id, new_channel_id, NULL, NULL, bundle, message_id);
	call_byte_setter(env, returnObject, "org/iota/jota/c/dto/MamResponseBundleMessage", "setMessageId", message_id);
	
	if (channel_id_isCopy == JNI_TRUE) {
	    env->ReleaseStringUTFChars(ch_id, channel_id);
	}
	if (new_channel_id_isCopy == JNI_TRUE) {
	    env->ReleaseStringUTFChars(new_ch_id, new_channel_id);
	}
	return code;
}

/*
 * Method:    mam_api_bundle_announce_endpoint
 */
JNIEXPORT jlong JNICALL Java_org_iota_jota_c_MamC_mam_1api_1bundle_1announce_1endpoint
	(JNIEnv *env, jclass clazz, jobject returnObject, jstring ch_id, jstring new_ep_id, jobject psks, jobject ntru_pks, jobject javaBundle){
	
  	jboolean channel_id_isCopy;
  	const tryte_t *channel_id = (tryte_t *) (*env)->GetStringUTFChars(env, ch_id, &channel_id_isCopy);
  	
  	jboolean new_endpoint_id_isCopy;
  	const tryte_t *new_endpoint_id = (tryte_t *) (*env)->GetStringUTFChars(env, new_ep_id, &new_endpoint_id_isCopy);
  	
  	bundle_transactions_t * bundle = bundleFromJavaBundle(javaBundle);
  	
	trit_t message_id[MAM_MSG_ID_SIZE];
	
	retcode_t code = mam_api_bundle_announce_endpoint(&api, channel_id, new_endpoint_id, NULL, NULL, bundle, message_id);
	call_byte_setter(env, returnObject, "org/iota/jota/c/dto/MamResponseBundleMessage", "setMessageId", message_id);
	
	if (channel_id_isCopy == JNI_TRUE) {
	    env->ReleaseStringUTFChars(ch_id, channel_id);
	}
	if (new_endpoint_id_isCopy == JNI_TRUE) {
	    env->ReleaseStringUTFChars(new_ep_id, new_endpoint_id);
	}
	return code;
}

/*
 * Method:    mam_api_bundle_write_packet
 */
JNIEXPORT jlong JNICALL Java_org_iota_jota_c_MamC_mam_1api_1bundle_1write_1packet
  (JNIEnv *env, jclass clazz, jobject returnObject, jintArray msg_id, jstring payload, jlong payload_size, jint checksum_int, jboolean is_last, jobject javaBundle){
  	mam_msg_checksum_t checksum = (mam_msg_checksum_t) checksum_int;
  	
  	jboolean write_payload_isCopy;
  	const tryte_t *write_payload = (tryte_t *) (*env)->GetStringUTFChars(env, payload, &write_payload_isCopy);
  	
  	jsize len = (*env)->GetArrayLength(env, msg_id);
	trit_t * tr_buf_msg_id = malloc(sizeof(trit_t)*len);
    (*env)->GetIntArrayRegion(env, msg_id, 0, len, tr_buf_msg_id);
    
  	bundle_transactions_t * bundle = bundleFromJavaBundle(javaBundle);
  	
  	retcode_t code = mam_api_bundle_write_packet(&api, tr_buf_msg_id, write_payload, (long) payload_size, checksum, (bool) is_last, bundle);
  	
	free(tr_buf_msg_id);
  	
	if (write_payload_isCopy == JNI_TRUE) {
	    env->ReleaseStringUTFChars(payload, write_payload);
	}
	return code;
}
  
/*
 * Method:    mam_api_bundle_read
 */
JNIEXPORT jlong JNICALL Java_org_iota_jota_c_MamC_mam_1api_1bundle_1read
  (JNIEnv *env, jclass clazz, jobject returnObject, jobject javaBundle){
  
  	bundle_transactions_t * bundle = bundleFromJavaBundle(javaBundle)
  
  	tryte_t *payload = NULL;
	size_t payload_size = 0;
	bool is_last_packet = false;
	
	retcode_t code = mam_api_bundle_read(&api, bundle, &payload, &payload_size, &is_last_packet);
	
  	set_long_field(env, returnObject, "org/iota/jota/c/dto/MamReadBundleResponse", "payloadSize", payload_size);
	call_string_setter(env, returnObject, "org/iota/jota/c/dto/MamReadBundleResponse", "setPayload", (char *)payload);
	set_bool_field(env, returnObject, "org/iota/jota/c/dto/MamReadBundleResponse", "isLast", is_last_packet);
	free(payload);
  	return code;
}


/*
 * Method:    mam_api_serialized_size
 */
JNIEXPORT jlong JNICALL Java_org_iota_jota_c_MamC_mam_1api_1serialized_1size
  (JNIEnv *env, jclass clazz, jobject returnObject){
  	size_t size =  mam_api_serialized_size(&api);
  	set_long_field(env, returnObject, "org/iota/jota/c/dto/ReturnSerialisedSize", "size", size);
  	return 0;
}

/*
 * Method:    mam_api_serialize
 */
JNIEXPORT jlong JNICALL Java_org_iota_jota_c_MamC_mam_1api_1serialize
  (JNIEnv *env, jclass clazz, jobject returnObject, jstring encryptionKey, jlong keySize){
  	size_t serialized_size = mam_api_serialized_size(&api);
	trit_t *buffer = malloc(serialized_size * sizeof(trit_t));
	
	jboolean key_isCopy;
   	const tryte_t *key = (tryte_t *) (*env)->GetStringUTFChars(env, encryptionKey, &key_isCopy);
	mam_api_serialize(&api, buffer, key, keySize);
	
	call_byte_setter(env, returnObject, "org/iota/jota/c/dto/MamReturnSerialised", "setSerialisedTrits", buffer);
	
	if (key_isCopy == JNI_TRUE) {
	    env->ReleaseStringUTFChars(encryptionKey, key);
	}
	return 0;
  }

/*
 * Method:    mam_api_deserialize
 */
JNIEXPORT jlong JNICALL Java_org_iota_jota_c_MamC_mam_1api_1deserialize
  (JNIEnv *env, jclass clazz, jintArray buffer, jlong serialized_size, jstring encryptionKey, jlong keySize){
  	
  	
  	jsize len = (*env)->GetArrayLength(env, buffer);
	trit_t * tr_buf = malloc(sizeof(trit_t)*len);
  	
    (*env)->GetIntArrayRegion(env, buffer, 0, len, tr_buf);
    
    jboolean key_isCopy;
  	const tryte_t *key = (tryte_t *) (*env)->GetStringUTFChars(env, encryptionKey, &key_isCopy);
  	
  	retcode_t ret = mam_api_deserialize(tr_buf, serialized_size, &api, key, keySize);
	free(tr_buf);
	
	if (key_isCopy == JNI_TRUE) {
	    env->ReleaseStringUTFChars(encryptionKey, key);
	}
	return ret;
  }

/*
 * Method:    mam_api_save
 */
JNIEXPORT jlong JNICALL Java_org_iota_jota_c_MamC_mam_1api_1save
  (JNIEnv *env, jclass clazz, jstring fileName, jstring encryptionKey, jlong keySize){
  	
    jboolean key_isCopy;
    const tryte_t *key = (tryte_t *) (*env)->GetStringUTFChars(env, encryptionKey, &key_isCopy);
  	
    jboolean file_name_isCopy;
  	const char *file_name = (*env)->GetStringUTFChars(env, fileName, &file_name_isCopy);
  	
  	retcode_t ret = mam_api_save(&api, file_name, key, keySize);
  	
  	if (key_isCopy == JNI_TRUE) {
	    env->ReleaseStringUTFChars(encryptionKey, key);
	}
	if (file_name_isCopy == JNI_TRUE) {
	    env->ReleaseStringUTFChars(fileName, file_name);
	}
  	return ret;
  }

/*
 * Method:    mam_api_load
 */
JNIEXPORT jlong JNICALL Java_org_iota_jota_c_MamC_mam_1api_1load
  (JNIEnv *env, jclass clazz, jstring fileName, jstring encryptionKey, jlong keySize){
    jboolean key_isCopy;
    const tryte_t *key = (tryte_t *) (*env)->GetStringUTFChars(env, encryptionKey, &key_isCopy);
  	
    jboolean file_name_isCopy;
    const char *file_name = (*env)->GetStringUTFChars(env, fileName, &file_name_isCopy);
  	
  	retcode_t ret = mam_api_load(file_name, &api, key, keySize);
  	if (key_isCopy == JNI_TRUE) {
	    env->ReleaseStringUTFChars(encryptionKey, key);
	}
	if (file_name_isCopy == JNI_TRUE) {
	    env->ReleaseStringUTFChars(fileName, file_name);
	}
  	return ret;
  }



#ifdef __cplusplus
}
#endif
#endif
