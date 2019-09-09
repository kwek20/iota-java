#include <jni.h>
#include <stdlib.h>

#include <mam/api/api.h>
#include <mam/api/api.h>

#ifndef _Included_org_iota_jota_c_MamC
#define _Included_org_iota_jota_c_MamC
#ifdef __cplusplus
extern "C" {
#endif

#undef TX_SIZE_TRITS
#define TX_SIZE_TRITS 8019

#undef TX_SIZE_TRYTES
#define TX_SIZE_TRYTES TX_SIZE_TRITS / 3

#undef FLEX_TRITS_FOR_TRITS_TX
#define FLEX_TRITS_FOR_TRITS_TX NUM_FLEX_TRITS_FOR_TRITS(TX_SIZE_TRITS)

mam_api_t api;

void print(char *text){
	printf("%s", text);
}

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

void call_byte_setter(JNIEnv *env, jobject javaObj, char *package, char *method, signed char *bytes){
	jclass ent_clazz = (*env)->FindClass(env, package);
	if (!ent_clazz) return;
	
	jmethodID mid = (*env)->GetMethodID(env, ent_clazz, method, "([B)V");
	if (!mid) return;
	
	size_t n = strlen(bytes);
	jbyteArray byte_array = (*env)->NewByteArray(env, n);
    (*env)->SetByteArrayRegion(env, byte_array, 0, n, (jbyte*)bytes);
    
	(*env)->CallVoidMethod(env, javaObj, mid, byte_array);
}

bundle_transactions_t *bundleFromJavaBundle(JNIEnv *env, jobject javaBundle){
	bundle_transactions_t *bundle = NULL;

	bundle_transactions_new(&bundle);
	
	jclass bundleClass = (*env)->FindClass(env, "org/iota/jota/model/Bundle");
	jmethodID getTransactions = (*env)->GetMethodID(env, bundleClass , "getTransactions", "()Ljava/util/List;");
	jobject txList = (*env)->CallObjectMethod(env, javaBundle, getTransactions );
	
	// If theres no list, this is a new bundle
	if (txList == NULL) {
		return bundle;
	}

	jclass transactionClass = (*env)->FindClass(env, "org/iota/jota/model/Transaction");
    jclass listClass = (*env)->FindClass(env, "java/util/List");    
    
	jmethodID toTrytes = (*env)->GetMethodID(env, transactionClass , "toTrytes", "()Ljava/lang/String;");
	jmethodID getMethodID = (*env)->GetMethodID(env, listClass, "get", "(I)Ljava/lang/Object;");
	jmethodID sizeMethodID = (*env)->GetMethodID(env, listClass, "size", "()I");
	
    int listItemsCount = (int)(*env)->CallIntMethod(env, txList, sizeMethodID );
	for( int i=0; i<listItemsCount; i++ ){
        jobject javaTx = (*env)->CallObjectMethod(env, txList, getMethodID, i);
		jstring txTrytes = (*env)->CallObjectMethod(env, javaTx, toTrytes);
		
        jboolean tx_isCopy;
        tryte_t *c_tx_trytes = (tryte_t *) (*env)->GetStringUTFChars(env, txTrytes, &tx_isCopy);
		size_t trytes_len = strlen(c_tx_trytes);
		
        flex_trit_t *flex_trits = malloc(FLEX_TRITS_FOR_TRITS_TX * sizeof(flex_trit_t));
        
        flex_trits_from_trytes(flex_trits, FLEX_TRITS_FOR_TRITS_TX, c_tx_trytes, trytes_len, trytes_len);
        
        iota_transaction_t *c_transaction = transaction_deserialize(flex_trits, false);
        bundle_transactions_add(bundle, c_transaction);
        
        if (tx_isCopy == JNI_TRUE) {
		    (*env)->ReleaseStringUTFChars(env, c_tx_trytes, txTrytes);
		}
    }
    
    if ((*env)->ExceptionCheck(env) != JNI_FALSE) {
	    (*env)->ExceptionDescribe(env);
	    (*env)->ExceptionClear(env);
	    return NULL;
	}
	
	return bundle;	
} 

void fillJavaBundleFromC(JNIEnv *env, jobject javaBundle, bundle_transactions_t *bundle){
	jclass transactionClass = (*env)->FindClass(env, "org/iota/jota/model/Transaction");
	jclass bundleClass = (*env)->FindClass(env, "org/iota/jota/model/Bundle");
	
	jmethodID addTransaction = (*env)->GetMethodID(env, bundleClass , "addTransaction", "(Lorg/iota/jota/model/Transaction;)V");
	jmethodID getTransactions = (*env)->GetMethodID(env, bundleClass , "getTransactions", "()Ljava/util/List;");
	jmethodID transactionConstructorTrytes = (*env)->GetMethodID(env, transactionClass , "<init>", "(Ljava/lang/String;)V");
	
	jobject txList = (*env)->CallObjectMethod(env, javaBundle, getTransactions );

	if (txList != NULL) {
		// clean bundle if not null
    	jclass listClass = (*env)->FindClass(env, "java/util/List"); 
		jmethodID cleanMethodId = (*env)->GetMethodID(env, listClass, "clear", "()V"); 
		(*env)->CallVoidMethod(env, txList, cleanMethodId );
	} // If its null, calling add will create the list

	size_t bundleTxCount = bundle_transactions_size(bundle);
	for( size_t i=0; i<bundleTxCount; i++ ){
		// convert to char *
		iota_transaction_t *transaction = bundle_at(bundle, i);
		
        flex_trit_t *flex_trits = malloc(FLEX_TRITS_FOR_TRITS_TX * sizeof(flex_trit_t));
		size_t ret_code = transaction_serialize_on_flex_trits(transaction, flex_trits);
		
		tryte_t *trytes = malloc(TX_SIZE_TRYTES * sizeof(tryte_t) + 1);	
		trytes[TX_SIZE_TRYTES * sizeof(tryte_t)] = 0;
		
		flex_trits_to_trytes(trytes, TX_SIZE_TRYTES, flex_trits, FLEX_TRITS_FOR_TRITS_TX, FLEX_TRITS_FOR_TRITS_TX);
		
		// add to bundle 
		jstring tryteText = (*env)->NewStringUTF(env, (char *)trytes);
 		jobject txObject = (*env)->NewObject(env, transactionClass, transactionConstructorTrytes, tryteText);
 		(*env)->CallVoidMethod(env, javaBundle, addTransaction, txObject);
	}
	
	if ((*env)->ExceptionCheck(env) != JNI_FALSE) {
	    (*env)->ExceptionDescribe(env);
	    (*env)->ExceptionClear(env);
	    return;
	}
}

/*********************
****** END UTIL ******
**********************/

/*
 * Method:    mam_api_init
 */
JNIEXPORT jlong JNICALL Java_org_iota_jota_c_MamC_mam_1api_1init(JNIEnv *env, jclass clazz, jstring seed){
  	jboolean seed_isCopy;
  	tryte_t *seed_Str = (tryte_t *) (*env)->GetStringUTFChars(env, seed, &seed_isCopy);
  	
  	retcode_t code = mam_api_init(&api, seed_Str);
  	if (seed_isCopy == JNI_TRUE) {
	    (*env)->ReleaseStringUTFChars(env, seed, seed_Str);
	}
	return code;
}

/*
 * Method:    mam_api_destroy
 */
JNIEXPORT jlong JNICALL Java_org_iota_jota_c_MamC_mam_1api_1destroy(JNIEnv *env, jclass clazz){
  	return mam_api_destroy(&api);
}

/*
 * Method:    mam_api_add_trusted_channel_pk
 */
JNIEXPORT jlong JNICALL Java_org_iota_jota_c_MamC_mam_1api_1add_1trusted_1channel_pk(JNIEnv *env, jclass clazz, jstring pk){
  	jboolean pk_isCopy;
  	const char *inCStr = (*env)->GetStringUTFChars(env, pk, &pk_isCopy);
  	tryte_t *trytes = malloc(sizeof(tryte_t) * sizeof(inCStr));
  	
  	size_t code = mam_api_add_trusted_channel_pk(&api, trytes);
  	if (pk_isCopy == JNI_TRUE) {
	    (*env)->ReleaseStringUTFChars(env, pk, inCStr);
	}
  	return code;
}

/*
 * Method:    mam_api_add_trusted_endpoint_pk
 */
JNIEXPORT jlong JNICALL Java_org_iota_jota_c_MamC_mam_1api_1add_1trusted_1endpoint_1pk(JNIEnv *env, jclass clazz, jstring pk){
  	jboolean pk_isCopy;
  	const char *inCStr = (*env)->GetStringUTFChars(env, pk, &pk_isCopy);
  	tryte_t *trytes = malloc(sizeof(tryte_t) * sizeof(inCStr));
  	
  	size_t code = mam_api_add_trusted_endpoint_pk(&api, trytes);
  	if (pk_isCopy == JNI_TRUE) {
	    (*env)->ReleaseStringUTFChars(env, pk, inCStr);
	}
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
  	tryte_t channel_id[MAM_CHANNEL_ID_TRYTE_SIZE + 1] = "";
  	retcode_t code = mam_api_channel_create(&api, height, channel_id);
  	set_string_field(env, returnObject, "org/iota/jota/dto/MamCreateChannelResponse", "channel_id", &channel_id);
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
	    (*env)->ReleaseStringUTFChars(env, endpointId, endpoint_id);
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
  	tryte_t endpoint_id[MAM_ENDPOINT_ID_TRYTE_SIZE+1] = "";
  	
	retcode_t code = mam_api_endpoint_create(&api, height, channel_id, endpoint_id);
	set_string_field(env, returnObject, "org/iota/jota/dto/MamCreateEndpointResponse", "endpoint_id", &endpoint_id);
	if (channel_id_isCopy == JNI_TRUE) {
	    (*env)->ReleaseStringUTFChars(env, channelId, channel_id);
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
	    (*env)->ReleaseStringUTFChars(env, channelId, channel_id);
	}
	if (endpoint_id_isCopy == JNI_TRUE) {
	    (*env)->ReleaseStringUTFChars(env, endpointId, endpoint_id);
	}
	return ret;
}

/*
 * Method:    mam_api_write_tag
 */
JNIEXPORT jlong JNICALL Java_org_iota_jota_c_MamC_mam_1api_1write_1tag(JNIEnv *env, jclass clazz, jobject returnObject, trit_t const *const msg_id, trint18_t const ord){
	trit_t tag[NUM_TRITS_TAG + 1];
	tag[NUM_TRITS_TAG] = 0;
	mam_api_write_tag(tag, msg_id, ord);
	for (int i = 0; i < sizeof(tag) / sizeof(trit_t); ++i){
		printf("%u", (unsigned int)tag[i]);
	}
	printf("\n");
  	call_byte_setter(env, returnObject, "org/iota/jota/dto/MamWriteTagResponse", "setByteTag", tag);
	return 0;
}

/*
 * Method:    mam_api_bundle_write_header_on_channel
 */
JNIEXPORT jlong JNICALL Java_org_iota_jota_c_MamC_mam_1api_1bundle_1write_1header_1on_1channel
  (JNIEnv *env, jclass clazz, jobject returnObject, jstring ch_id, jobjectArray psks, jobjectArray ntru_pks, jobject javaBundle){
  	
  	jboolean channel_id_isCopy;
  	const tryte_t *channel_id = (tryte_t *) (*env)->GetStringUTFChars(env, ch_id, &channel_id_isCopy);
  	
  	bundle_transactions_t * bundle = bundleFromJavaBundle(env, javaBundle);
	
	trit_t message_id[MAM_MSG_ID_SIZE];
	
	retcode_t ret = mam_api_bundle_write_header_on_channel(&api, channel_id, NULL, NULL, bundle, message_id);
	call_byte_setter(env, returnObject, "org/iota/jota/dto/MamResponseBundleMessage", "setMessageId", message_id);
	fillJavaBundleFromC(env, javaBundle, bundle);
	if (channel_id_isCopy == JNI_TRUE) {
	    (*env)->ReleaseStringUTFChars(env, ch_id, channel_id);
	}
	return ret;
}


/*
 * Method:    mam_api_bundle_write_header_on_endpoint
 */
JNIEXPORT jlong JNICALL Java_org_iota_jota_c_MamC_mam_1api_1bundle_1write_1header_1on_1endpoint
  (JNIEnv *env, jclass clazz, jobject returnObject, jstring ch_id, jstring ep_id, jobjectArray psks, jobjectArray ntru_pks, jobject javaBundle){
  	jboolean channel_id_isCopy;
  	const tryte_t *channel_id = (tryte_t *) (*env)->GetStringUTFChars(env, ch_id, &channel_id_isCopy);
  	
  	jboolean endpoint_id_isCopy;
  	const tryte_t *endpoint_id = (tryte_t *) (*env)->GetStringUTFChars(env, ep_id, &endpoint_id_isCopy);
  	
  	bundle_transactions_t * bundle = bundleFromJavaBundle(env, javaBundle);
	trit_t message_id[MAM_MSG_ID_SIZE];
	retcode_t code = mam_api_bundle_write_header_on_endpoint(&api, channel_id, endpoint_id, NULL, NULL, bundle, message_id);
	
	call_byte_setter(env, returnObject, "org/iota/jota/dto/MamResponseBundleMessage", "setMessageId", message_id);
	fillJavaBundleFromC(env, javaBundle, bundle);
	
	if (channel_id_isCopy == JNI_TRUE) {
	    (*env)->ReleaseStringUTFChars(env, ch_id, channel_id);
	}
	if (endpoint_id_isCopy == JNI_TRUE) {
	    (*env)->ReleaseStringUTFChars(env, ep_id, endpoint_id);
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
  	
  	bundle_transactions_t * bundle = bundleFromJavaBundle(env, javaBundle);
  	
	trit_t message_id[MAM_MSG_ID_SIZE];
	
	retcode_t code = mam_api_bundle_announce_channel(&api, channel_id, new_channel_id, NULL, NULL, bundle, message_id);
	call_byte_setter(env, returnObject, "org/iota/jota/dto/MamResponseBundleMessage", "setMessageId", message_id);
	fillJavaBundleFromC(env, javaBundle, bundle);
	if (channel_id_isCopy == JNI_TRUE) {
	    (*env)->ReleaseStringUTFChars(env, ch_id, channel_id);
	}
	if (new_channel_id_isCopy == JNI_TRUE) {
	    (*env)->ReleaseStringUTFChars(env, new_ch_id, new_channel_id);
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
  	
  	bundle_transactions_t * bundle = bundleFromJavaBundle(env, javaBundle);
  	
	trit_t message_id[MAM_MSG_ID_SIZE];
	
	retcode_t code = mam_api_bundle_announce_endpoint(&api, channel_id, new_endpoint_id, NULL, NULL, bundle, message_id);
	call_byte_setter(env, returnObject, "org/iota/jota/dto/MamResponseBundleMessage", "setMessageId", message_id);
	fillJavaBundleFromC(env, javaBundle, bundle);
	if (channel_id_isCopy == JNI_TRUE) {
	    (*env)->ReleaseStringUTFChars(env, ch_id, channel_id);
	}
	if (new_endpoint_id_isCopy == JNI_TRUE) {
	    (*env)->ReleaseStringUTFChars(env, new_ep_id, new_endpoint_id);
	}
	return code;
}

/*
 * Method:    mam_api_bundle_write_packet
 */
JNIEXPORT jlong JNICALL Java_org_iota_jota_c_MamC_mam_1api_1bundle_1write_1packet
  (JNIEnv *env, jclass clazz, jobject returnObject, jcharArray msg_id, jstring payload, jlong payload_size, jint checksum_int, jboolean is_last, jobject javaBundle){
  	mam_msg_checksum_t checksum = (mam_msg_checksum_t) checksum_int;
  	
  	jboolean write_payload_isCopy;
  	const tryte_t *write_payload = (tryte_t *) (*env)->GetStringUTFChars(env, payload, &write_payload_isCopy);
  	
  	jsize len = (*env)->GetArrayLength(env, msg_id);
	trit_t * tr_buf_msg_id = malloc(sizeof(trit_t) * len);
    (*env)->GetCharArrayRegion(env, msg_id, 0, len, tr_buf_msg_id);
    
  	bundle_transactions_t * bundle = bundleFromJavaBundle(env, javaBundle);
  	
  	retcode_t code = mam_api_bundle_write_packet(&api, tr_buf_msg_id, write_payload, (long) payload_size, checksum, (bool) is_last, bundle);
  	fillJavaBundleFromC(env, javaBundle, bundle);
	free(tr_buf_msg_id);
  	
	if (write_payload_isCopy == JNI_TRUE) {
	    (*env)->ReleaseStringUTFChars(env, payload, write_payload);
	}
	return code;
}
  
/*
 * Method:    mam_api_bundle_read
 */
JNIEXPORT jlong JNICALL Java_org_iota_jota_c_MamC_mam_1api_1bundle_1read(JNIEnv *env, jclass clazz, jobject returnObject, jobject javaBundle){
  
  	bundle_transactions_t * bundle = bundleFromJavaBundle(env, javaBundle);
  
  	tryte_t *read_payload = NULL;
	size_t payload_size = 0;
	bool is_last_packet = false;
	
	retcode_t code = mam_api_bundle_read(&api, bundle, &read_payload, &payload_size, &is_last_packet);
	
  	set_long_field(env, returnObject, "org/iota/jota/dto/MamReadBundleResponse", "payloadSize", payload_size);
	call_string_setter(env, returnObject, "org/iota/jota/dto/MamReadBundleResponse", "setPayload", (char *)read_payload);
	set_bool_field(env, returnObject, "org/iota/jota/dto/MamReadBundleResponse", "isLast", is_last_packet);
	free(read_payload);
  	return code;
}


/*
 * Method:    mam_api_serialized_size
 */
JNIEXPORT jlong JNICALL Java_org_iota_jota_c_MamC_mam_1api_1serialized_1size(JNIEnv *env, jclass clazz, jobject returnObject){
  	size_t size =  mam_api_serialized_size(&api);
  	set_long_field(env, returnObject, "org/iota/jota/dto/MamReturnSerialisedSize", "size", size);
  	return 0;
}

/*
 * Method:    mam_api_serialize
 */
JNIEXPORT jlong JNICALL Java_org_iota_jota_c_MamC_mam_1api_1serialize(JNIEnv *env, jclass clazz, jobject returnObject, jstring encryptionKey, jlong keySize){
  	size_t serialized_size = mam_api_serialized_size(&api);
	trit_t *buffer = malloc(serialized_size * sizeof(trit_t));
	
	jboolean key_isCopy;
	const tryte_t *key;
  	if (encryptionKey != NULL) {
  		key = (tryte_t *) (*env)->GetStringUTFChars(env, encryptionKey, &key_isCopy);
	}
	mam_api_serialize(&api, buffer, key, keySize);
	
	call_byte_setter(env, returnObject, "org/iota/jota/dto/MamReturnSerialised", "setSerialisedTrits", buffer);
	
	if (key_isCopy == JNI_TRUE) {
	    (*env)->ReleaseStringUTFChars(env, encryptionKey, key);
	}
	return 0;
}

/*
 * Method:    mam_api_deserialize
 */
JNIEXPORT jlong JNICALL Java_org_iota_jota_c_MamC_mam_1api_1deserialize(JNIEnv *env, jclass clazz, jintArray buffer, jlong serialized_size, jstring encryptionKey, jlong keySize){
  	
  	
  	jsize len = (*env)->GetArrayLength(env, buffer);
	trit_t * tr_buf = malloc(sizeof(trit_t)*len);
  	
    (*env)->GetIntArrayRegion(env, buffer, 0, len, tr_buf);
    
    jboolean key_isCopy;
  	const tryte_t *key;
  	if (encryptionKey != NULL) {
  		key = (tryte_t *) (*env)->GetStringUTFChars(env, encryptionKey, &key_isCopy);
	}
  	
  	retcode_t ret = mam_api_deserialize(tr_buf, serialized_size, &api, key, keySize);
	free(tr_buf);
	
	if (key_isCopy == JNI_TRUE) {
	    (*env)->ReleaseStringUTFChars(env, encryptionKey, key);
	}
	return ret;
}

/*
 * Method:    mam_api_save
 */
JNIEXPORT jlong JNICALL Java_org_iota_jota_c_MamC_mam_1api_1save(JNIEnv *env, jclass clazz, jstring fileName, jstring encryptionKey, jlong keySize){
  	
    jboolean key_isCopy;
    const tryte_t *key;
  	if (encryptionKey != NULL) {
  		key = (tryte_t *) (*env)->GetStringUTFChars(env, encryptionKey, &key_isCopy);
	}
  	
    jboolean file_name_isCopy;
  	const char *file_name = (*env)->GetStringUTFChars(env, fileName, &file_name_isCopy);
  	
  	retcode_t ret = mam_api_save(&api, file_name, key, keySize);
  	
  	if (key_isCopy == JNI_TRUE) {
	    (*env)->ReleaseStringUTFChars(env, encryptionKey, key);
	}
	if (file_name_isCopy == JNI_TRUE) {
	    (*env)->ReleaseStringUTFChars(env, fileName, file_name);
	}
  	return ret;
}

/*
 * Method:    mam_api_load
 */
JNIEXPORT jlong JNICALL Java_org_iota_jota_c_MamC_mam_1api_1load(JNIEnv *env, jclass clazz, jstring fileName, jstring encryptionKey, jlong keySize){
    jboolean key_isCopy;const tryte_t *key;
  	if (encryptionKey != NULL) {
  		key = (tryte_t *) (*env)->GetStringUTFChars(env, encryptionKey, &key_isCopy);
	}
  	
    jboolean file_name_isCopy;
    const char *file_name = (*env)->GetStringUTFChars(env, fileName, &file_name_isCopy);
  	
  	retcode_t ret = mam_api_load(file_name, &api, key, keySize);
  	if (key_isCopy == JNI_TRUE) {
	    (*env)->ReleaseStringUTFChars(env, encryptionKey, key);
	}
	if (file_name_isCopy == JNI_TRUE) {
	    (*env)->ReleaseStringUTFChars(env, fileName, file_name);
	}
  	return ret;
S}



#ifdef __cplusplus
}
#endif
#endif
