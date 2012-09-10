/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include "exception.h"
#include "webhdfs.h"
#include "jni_helper.h"

#include <inttypes.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define EXCEPTION_INFO_LEN (sizeof(gExceptionInfo)/sizeof(gExceptionInfo[0]))

struct ExceptionInfo {
    const char * const name;
    int noPrintFlag;
    int excErrno;
};

static const struct ExceptionInfo gExceptionInfo[] = {
    {
        .name = "java/io/FileNotFoundException",
        .noPrintFlag = NOPRINT_EXC_FILE_NOT_FOUND,
        .excErrno = ENOENT,
    },
    {
        .name = "org/apache/hadoop/security/AccessControlException",
        .noPrintFlag = NOPRINT_EXC_ACCESS_CONTROL,
        .excErrno = EACCES,
    },
    {
        .name = "org/apache/hadoop/fs/UnresolvedLinkException",
        .noPrintFlag = NOPRINT_EXC_UNRESOLVED_LINK,
        .excErrno = ENOLINK,
    },
    {
        .name = "org/apache/hadoop/fs/ParentNotDirectoryException",
        .noPrintFlag = NOPRINT_EXC_PARENT_NOT_DIRECTORY,
        .excErrno = ENOTDIR,
    },
    {
        .name = "java/lang/IllegalArgumentException",
        .noPrintFlag = NOPRINT_EXC_ILLEGAL_ARGUMENT,
        .excErrno = EINVAL,
    },
    {
        .name = "java/lang/OutOfMemoryError",
        .noPrintFlag = 0,
        .excErrno = ENOMEM,
    },
    
};

int printExceptionWebV(hdfs_exception_msg *exc, int noPrintFlags, const char *fmt, va_list ap)
{
    int i, noPrint, excErrno;
    if (!exc) {
        fprintf(stderr, "printExceptionWebV: the hdfs_exception_msg is NULL\n");
        return EINTERNAL;
    }
    
    for (i = 0; i < EXCEPTION_INFO_LEN; i++) {
        if (strstr(gExceptionInfo[i].name, exc->exception)) {
            break;
        }
    }
    if (i < EXCEPTION_INFO_LEN) {
        noPrint = (gExceptionInfo[i].noPrintFlag & noPrintFlags);
        excErrno = gExceptionInfo[i].excErrno;
    } else {
        noPrint = 0;
        excErrno = EINTERNAL;
    }
    
    if (!noPrint) {
        vfprintf(stderr, fmt, ap);
        fprintf(stderr, " error:\n");
        fprintf(stderr, "Exception: %s\nJavaClassName: %s\nMessage: %s\n", exc->exception, exc->javaClassName, exc->message);
    }
    
    free(exc);
    return excErrno;
}

int printExceptionWeb(hdfs_exception_msg *exc, int noPrintFlags, const char *fmt, ...)
{
    va_list ap;
    int ret;
    
    va_start(ap, fmt);
    ret = printExceptionWebV(exc, noPrintFlags, fmt, ap);
    va_end(ap);
    return ret;
}

int printExceptionAndFreeV(JNIEnv *env, jthrowable exc, int noPrintFlags,
        const char *fmt, va_list ap)
{
    int i, noPrint, excErrno;
    char *className = NULL;
    jstring jStr = NULL;
    jvalue jVal;
    jthrowable jthr;

    jthr = classNameOfObject(exc, env, &className);
    if (jthr) {
        fprintf(stderr, "PrintExceptionAndFree: error determining class name "
            "of exception.\n");
        className = strdup("(unknown)");
        destroyLocalReference(env, jthr);
    }
    for (i = 0; i < EXCEPTION_INFO_LEN; i++) {
        if (!strcmp(gExceptionInfo[i].name, className)) {
            break;
        }
    }
    if (i < EXCEPTION_INFO_LEN) {
        noPrint = (gExceptionInfo[i].noPrintFlag & noPrintFlags);
        excErrno = gExceptionInfo[i].excErrno;
    } else {
        noPrint = 0;
        excErrno = EINTERNAL;
    }
    if (!noPrint) {
        vfprintf(stderr, fmt, ap);
        fprintf(stderr, " error:\n");

        // We don't want to  use ExceptionDescribe here, because that requires a
        // pending exception.  Instead, use ExceptionUtils.
        jthr = invokeMethod(env, &jVal, STATIC, NULL, 
            "org/apache/commons/lang/exception/ExceptionUtils",
            "getStackTrace", "(Ljava/lang/Throwable;)Ljava/lang/String;", exc);
        if (jthr) {
            fprintf(stderr, "(unable to get stack trace for %s exception: "
                    "ExceptionUtils::getStackTrace error.)\n", className);
            destroyLocalReference(env, jthr);
        } else {
            jStr = jVal.l;
            const char *stackTrace = (*env)->GetStringUTFChars(env, jStr, NULL);
            if (!stackTrace) {
                fprintf(stderr, "(unable to get stack trace for %s exception: "
                        "GetStringUTFChars error.)\n", className);
            } else {
                fprintf(stderr, "%s", stackTrace);
                (*env)->ReleaseStringUTFChars(env, jStr, stackTrace);
            }
        }
    }
    destroyLocalReference(env, jStr);
    destroyLocalReference(env, exc);
    free(className);
    return excErrno;
}

int printExceptionAndFree(JNIEnv *env, jthrowable exc, int noPrintFlags,
        const char *fmt, ...)
{
    va_list ap;
    int ret;

    va_start(ap, fmt);
    ret = printExceptionAndFreeV(env, exc, noPrintFlags, fmt, ap);
    va_end(ap);
    return ret;
}

int printPendingExceptionAndFree(JNIEnv *env, int noPrintFlags,
        const char *fmt, ...)
{
    va_list ap;
    int ret;
    jthrowable exc;

    exc = (*env)->ExceptionOccurred(env);
    if (!exc) {
        va_start(ap, fmt);
        vfprintf(stderr, fmt, ap);
        va_end(ap);
        fprintf(stderr, " error: (no exception)");
        ret = 0;
    } else {
        (*env)->ExceptionClear(env);
        va_start(ap, fmt);
        ret = printExceptionAndFreeV(env, exc, noPrintFlags, fmt, ap);
        va_end(ap);
    }
    return ret;
}

jthrowable getPendingExceptionAndClear(JNIEnv *env)
{
    jthrowable jthr = (*env)->ExceptionOccurred(env);
    if (!jthr)
        return NULL;
    (*env)->ExceptionClear(env);
    return jthr;
}

jthrowable newRuntimeError(JNIEnv *env, const char *fmt, ...)
{
    char buf[512];
    jobject out, exc;
    jstring jstr;
    va_list ap;

    va_start(ap, fmt);
    vsnprintf(buf, sizeof(buf), fmt, ap);
    va_end(ap);
    jstr = (*env)->NewStringUTF(env, buf);
    if (!jstr) {
        // We got an out of memory exception rather than a RuntimeException.
        // Too bad...
        return getPendingExceptionAndClear(env);
    }
    exc = constructNewObjectOfClass(env, &out, "RuntimeException",
        "(java/lang/String;)V", jstr);
    (*env)->DeleteLocalRef(env, jstr);
    // Again, we'll either get an out of memory exception or the
    // RuntimeException we wanted.
    return (exc) ? exc : out;
}
