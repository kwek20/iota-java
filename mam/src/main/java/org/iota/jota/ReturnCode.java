package org.iota.jota;

public enum ReturnCode {
    OK, // 0
    BUFFER_TOO_SMALL,
    INVALID_ARGUMENT,
    INVALID_VALUE,
    NEGATIVE_VALUE,  
    INTERNAL_ERROR,
    NOT_IMPLEMENTED,
    PB3_EOF,
    PB3_BAD_ONEOF,
    PB3_BAD_OPTIONAL,
    PB3_BAD_REPEATED,
    PB3_BAD_MAC,
    PB3_BAD_SIG,
    PB3_BAD_EKEY,
    TRITS_SIZE_T_NOT_SUPPORTED,
    CHANNEL_NOT_FOUND,
    ENDPOINT_NOT_FOUND,
    VERSION_NOT_SUPPORTED,
    CHANNEL_NOT_TRUSTED,
    ENDPOINT_NOT_TRUSTED,
    KEYLOAD_IRRELEVANT,
    KEYLOAD_OVERLOADED,
    BUNDLE_NOT_EMPTY,
    BUNDLE_DOES_NOT_CONTAIN_HEADER,
    RECV_CTX_NOT_FOUND,
    SEND_CTX_NOT_FOUND,
    MESSAGE_NOT_FOUND,
    BAD_PACKET_ORD,
    MSS_EXHAUSTED,
    NTRU_POLY_FAILED,
    API_FAILED_CREATE_ENDPOINT,
    API_FAILED_CREATE_CHANNEL,
    MSS_NOT_FOUND, 
    MISSING //NO code yet, indicated as -1
}