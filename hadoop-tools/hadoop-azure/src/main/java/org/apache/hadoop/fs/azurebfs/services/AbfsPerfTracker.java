begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.azurebfs.services
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|azurebfs
operator|.
name|services
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|UnknownHostException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|time
operator|.
name|Duration
import|;
end_import

begin_import
import|import
name|java
operator|.
name|time
operator|.
name|Instant
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentLinkedQueue
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|azurebfs
operator|.
name|constants
operator|.
name|AbfsHttpConstants
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|azurebfs
operator|.
name|AbfsConfiguration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|azurebfs
operator|.
name|contracts
operator|.
name|services
operator|.
name|AbfsPerfLoggable
import|;
end_import

begin_comment
comment|/**  * {@code AbfsPerfTracker} keeps track of service latencies observed by {@code AbfsClient}. Every request hands over  * its perf-related information as a {@code AbfsPerfInfo} object (contains success/failure, latency etc) to the  * {@code AbfsPerfTracker}'s queue. When a request is made, we check {@code AbfsPerfTracker} to see if there are  * any latency numbers to be reported. If there are any, the stats are added to an HTTP header  * ({@code x-ms-abfs-client-latency}) on the next request.  *  * A typical perf log line appears like:  *  * h=KARMA t=2019-10-25T20:21:14.518Z a=abfstest01.dfs.core.windows.net  * c=abfs-testcontainer-84828169-6488-4a62-a875-1e674275a29f cr=delete ce=deletePath r=Succeeded l=32 ls=32 lc=1 s=200  * e= ci=95121dae-70a8-4187-b067-614091034558 ri=97effdcf-201f-0097-2d71-8bae00000000 ct=0 st=0 rt=0 bs=0 br=0 m=DELETE  * u=https%3A%2F%2Fabfstest01.dfs.core.windows.net%2Fabfs-testcontainer%2Ftest%3Ftimeout%3D90%26recursive%3Dtrue  *  * The fields have the following definitions:  *  * h: host name  * t: time when this request was logged  * a: Azure storage account name  * c: container name  * cr: name of the caller method  * ce: name of the callee method  * r: result (Succeeded/Failed)  * l: latency (time spent in callee)  * ls: latency sum (aggregate time spent in caller; logged when there are multiple callees;  *     logged with the last callee)  * lc: latency count (number of callees; logged when there are multiple callees;  *     logged with the last callee)  * s: HTTP Status code  * e: Error code  * ci: client request ID  * ri: server request ID  * ct: connection time in milliseconds  * st: sending time in milliseconds  * rt: receiving time in milliseconds  * bs: bytes sent  * br: bytes received  * m: HTTP method (GET, PUT etc)  * u: Encoded HTTP URL  *  */
end_comment

begin_class
DECL|class|AbfsPerfTracker
specifier|public
specifier|final
class|class
name|AbfsPerfTracker
block|{
comment|// the logger.
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|AbfsPerfTracker
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// the field names of perf log lines.
DECL|field|HOST_NAME_KEY
specifier|private
specifier|static
specifier|final
name|String
name|HOST_NAME_KEY
init|=
literal|"h"
decl_stmt|;
DECL|field|TIMESTAMP_KEY
specifier|private
specifier|static
specifier|final
name|String
name|TIMESTAMP_KEY
init|=
literal|"t"
decl_stmt|;
DECL|field|STORAGE_ACCOUNT_NAME_KEY
specifier|private
specifier|static
specifier|final
name|String
name|STORAGE_ACCOUNT_NAME_KEY
init|=
literal|"a"
decl_stmt|;
DECL|field|CONTAINER_NAME_KEY
specifier|private
specifier|static
specifier|final
name|String
name|CONTAINER_NAME_KEY
init|=
literal|"c"
decl_stmt|;
DECL|field|CALLER_METHOD_NAME_KEY
specifier|private
specifier|static
specifier|final
name|String
name|CALLER_METHOD_NAME_KEY
init|=
literal|"cr"
decl_stmt|;
DECL|field|CALLEE_METHOD_NAME_KEY
specifier|private
specifier|static
specifier|final
name|String
name|CALLEE_METHOD_NAME_KEY
init|=
literal|"ce"
decl_stmt|;
DECL|field|RESULT_KEY
specifier|private
specifier|static
specifier|final
name|String
name|RESULT_KEY
init|=
literal|"r"
decl_stmt|;
DECL|field|LATENCY_KEY
specifier|private
specifier|static
specifier|final
name|String
name|LATENCY_KEY
init|=
literal|"l"
decl_stmt|;
DECL|field|LATENCY_SUM_KEY
specifier|private
specifier|static
specifier|final
name|String
name|LATENCY_SUM_KEY
init|=
literal|"ls"
decl_stmt|;
DECL|field|LATENCY_COUNT_KEY
specifier|private
specifier|static
specifier|final
name|String
name|LATENCY_COUNT_KEY
init|=
literal|"lc"
decl_stmt|;
DECL|field|HTTP_STATUS_CODE_KEY
specifier|private
specifier|static
specifier|final
name|String
name|HTTP_STATUS_CODE_KEY
init|=
literal|"s"
decl_stmt|;
DECL|field|ERROR_CODE_KEY
specifier|private
specifier|static
specifier|final
name|String
name|ERROR_CODE_KEY
init|=
literal|"e"
decl_stmt|;
DECL|field|CLIENT_REQUEST_ID_KEY
specifier|private
specifier|static
specifier|final
name|String
name|CLIENT_REQUEST_ID_KEY
init|=
literal|"ci"
decl_stmt|;
DECL|field|SERVER_REQUEST_ID_KEY
specifier|private
specifier|static
specifier|final
name|String
name|SERVER_REQUEST_ID_KEY
init|=
literal|"ri"
decl_stmt|;
DECL|field|CONNECTION_TIME_KEY
specifier|private
specifier|static
specifier|final
name|String
name|CONNECTION_TIME_KEY
init|=
literal|"ct"
decl_stmt|;
DECL|field|SENDING_TIME_KEY
specifier|private
specifier|static
specifier|final
name|String
name|SENDING_TIME_KEY
init|=
literal|"st"
decl_stmt|;
DECL|field|RECEIVING_TIME_KEY
specifier|private
specifier|static
specifier|final
name|String
name|RECEIVING_TIME_KEY
init|=
literal|"rt"
decl_stmt|;
DECL|field|BYTES_SENT_KEY
specifier|private
specifier|static
specifier|final
name|String
name|BYTES_SENT_KEY
init|=
literal|"bs"
decl_stmt|;
DECL|field|BYTES_RECEIVED_KEY
specifier|private
specifier|static
specifier|final
name|String
name|BYTES_RECEIVED_KEY
init|=
literal|"br"
decl_stmt|;
DECL|field|HTTP_METHOD_KEY
specifier|private
specifier|static
specifier|final
name|String
name|HTTP_METHOD_KEY
init|=
literal|"m"
decl_stmt|;
DECL|field|HTTP_URL_KEY
specifier|private
specifier|static
specifier|final
name|String
name|HTTP_URL_KEY
init|=
literal|"u"
decl_stmt|;
DECL|field|STRING_PLACEHOLDER
specifier|private
specifier|static
specifier|final
name|String
name|STRING_PLACEHOLDER
init|=
literal|"%s"
decl_stmt|;
comment|// the queue to hold latency information.
DECL|field|queue
specifier|private
specifier|final
name|ConcurrentLinkedQueue
argument_list|<
name|String
argument_list|>
name|queue
init|=
operator|new
name|ConcurrentLinkedQueue
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
comment|// whether the latency tracker has been enabled.
DECL|field|enabled
specifier|private
name|boolean
name|enabled
init|=
literal|false
decl_stmt|;
comment|// the host name.
DECL|field|hostName
specifier|private
name|String
name|hostName
decl_stmt|;
comment|// singleton latency reporting format.
DECL|field|singletonLatencyReportingFormat
specifier|private
name|String
name|singletonLatencyReportingFormat
decl_stmt|;
comment|// aggregate latency reporting format.
DECL|field|aggregateLatencyReportingFormat
specifier|private
name|String
name|aggregateLatencyReportingFormat
decl_stmt|;
DECL|method|AbfsPerfTracker (String filesystemName, String accountName, AbfsConfiguration configuration)
specifier|public
name|AbfsPerfTracker
parameter_list|(
name|String
name|filesystemName
parameter_list|,
name|String
name|accountName
parameter_list|,
name|AbfsConfiguration
name|configuration
parameter_list|)
block|{
name|this
argument_list|(
name|filesystemName
argument_list|,
name|accountName
argument_list|,
name|configuration
operator|.
name|shouldTrackLatency
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|AbfsPerfTracker (String filesystemName, String accountName, boolean enabled)
specifier|protected
name|AbfsPerfTracker
parameter_list|(
name|String
name|filesystemName
parameter_list|,
name|String
name|accountName
parameter_list|,
name|boolean
name|enabled
parameter_list|)
block|{
name|this
operator|.
name|enabled
operator|=
name|enabled
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"AbfsPerfTracker configuration: {}"
argument_list|,
name|enabled
argument_list|)
expr_stmt|;
if|if
condition|(
name|enabled
condition|)
block|{
try|try
block|{
name|hostName
operator|=
name|InetAddress
operator|.
name|getLocalHost
argument_list|()
operator|.
name|getHostName
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnknownHostException
name|e
parameter_list|)
block|{
name|hostName
operator|=
literal|"UnknownHost"
expr_stmt|;
block|}
name|String
name|commonReportingFormat
init|=
operator|new
name|StringBuilder
argument_list|()
operator|.
name|append
argument_list|(
name|HOST_NAME_KEY
argument_list|)
operator|.
name|append
argument_list|(
name|AbfsHttpConstants
operator|.
name|EQUAL
argument_list|)
operator|.
name|append
argument_list|(
name|hostName
argument_list|)
operator|.
name|append
argument_list|(
name|AbfsHttpConstants
operator|.
name|SINGLE_WHITE_SPACE
argument_list|)
operator|.
name|append
argument_list|(
name|TIMESTAMP_KEY
argument_list|)
operator|.
name|append
argument_list|(
name|AbfsHttpConstants
operator|.
name|EQUAL
argument_list|)
operator|.
name|append
argument_list|(
name|STRING_PLACEHOLDER
argument_list|)
operator|.
name|append
argument_list|(
name|AbfsHttpConstants
operator|.
name|SINGLE_WHITE_SPACE
argument_list|)
operator|.
name|append
argument_list|(
name|STORAGE_ACCOUNT_NAME_KEY
argument_list|)
operator|.
name|append
argument_list|(
name|AbfsHttpConstants
operator|.
name|EQUAL
argument_list|)
operator|.
name|append
argument_list|(
name|accountName
argument_list|)
operator|.
name|append
argument_list|(
name|AbfsHttpConstants
operator|.
name|SINGLE_WHITE_SPACE
argument_list|)
operator|.
name|append
argument_list|(
name|CONTAINER_NAME_KEY
argument_list|)
operator|.
name|append
argument_list|(
name|AbfsHttpConstants
operator|.
name|EQUAL
argument_list|)
operator|.
name|append
argument_list|(
name|filesystemName
argument_list|)
operator|.
name|append
argument_list|(
name|AbfsHttpConstants
operator|.
name|SINGLE_WHITE_SPACE
argument_list|)
operator|.
name|append
argument_list|(
name|CALLER_METHOD_NAME_KEY
argument_list|)
operator|.
name|append
argument_list|(
name|AbfsHttpConstants
operator|.
name|EQUAL
argument_list|)
operator|.
name|append
argument_list|(
name|STRING_PLACEHOLDER
argument_list|)
operator|.
name|append
argument_list|(
name|AbfsHttpConstants
operator|.
name|SINGLE_WHITE_SPACE
argument_list|)
operator|.
name|append
argument_list|(
name|CALLEE_METHOD_NAME_KEY
argument_list|)
operator|.
name|append
argument_list|(
name|AbfsHttpConstants
operator|.
name|EQUAL
argument_list|)
operator|.
name|append
argument_list|(
name|STRING_PLACEHOLDER
argument_list|)
operator|.
name|append
argument_list|(
name|AbfsHttpConstants
operator|.
name|SINGLE_WHITE_SPACE
argument_list|)
operator|.
name|append
argument_list|(
name|RESULT_KEY
argument_list|)
operator|.
name|append
argument_list|(
name|AbfsHttpConstants
operator|.
name|EQUAL
argument_list|)
operator|.
name|append
argument_list|(
name|STRING_PLACEHOLDER
argument_list|)
operator|.
name|append
argument_list|(
name|AbfsHttpConstants
operator|.
name|SINGLE_WHITE_SPACE
argument_list|)
operator|.
name|append
argument_list|(
name|LATENCY_KEY
argument_list|)
operator|.
name|append
argument_list|(
name|AbfsHttpConstants
operator|.
name|EQUAL
argument_list|)
operator|.
name|append
argument_list|(
name|STRING_PLACEHOLDER
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
comment|/**         * Example singleton log (no ls or lc field)         * h=KARMA t=2019-10-25T20:21:14.518Z a=abfstest01.dfs.core.windows.net         * c=abfs-testcontainer-84828169-6488-4a62-a875-1e674275a29f cr=delete ce=deletePath r=Succeeded l=32 s=200         * e= ci=95121dae-70a8-4187-b067-614091034558 ri=97effdcf-201f-0097-2d71-8bae00000000 ct=0 st=0 rt=0 bs=0 br=0 m=DELETE         * u=https%3A%2F%2Fabfstest01.dfs.core.windows.net%2Fabfs-testcontainer%2Ftest%3Ftimeout%3D90%26recursive%3Dtrue       */
name|singletonLatencyReportingFormat
operator|=
operator|new
name|StringBuilder
argument_list|()
operator|.
name|append
argument_list|(
name|commonReportingFormat
argument_list|)
operator|.
name|append
argument_list|(
name|STRING_PLACEHOLDER
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
comment|/**        * Example aggregate log        * h=KARMA t=2019-10-25T20:21:14.518Z a=abfstest01.dfs.core.windows.net        * c=abfs-testcontainer-84828169-6488-4a62-a875-1e674275a29f cr=delete ce=deletePath r=Succeeded l=32 ls=32 lc=1 s=200        * e= ci=95121dae-70a8-4187-b067-614091034558 ri=97effdcf-201f-0097-2d71-8bae00000000 ct=0 st=0 rt=0 bs=0 br=0 m=DELETE        * u=https%3A%2F%2Fabfstest01.dfs.core.windows.net%2Fabfs-testcontainer%2Ftest%3Ftimeout%3D90%26recursive%3Dtrue        */
name|aggregateLatencyReportingFormat
operator|=
operator|new
name|StringBuilder
argument_list|()
operator|.
name|append
argument_list|(
name|commonReportingFormat
argument_list|)
operator|.
name|append
argument_list|(
name|AbfsHttpConstants
operator|.
name|SINGLE_WHITE_SPACE
argument_list|)
operator|.
name|append
argument_list|(
name|LATENCY_SUM_KEY
argument_list|)
operator|.
name|append
argument_list|(
name|AbfsHttpConstants
operator|.
name|EQUAL
argument_list|)
operator|.
name|append
argument_list|(
name|STRING_PLACEHOLDER
argument_list|)
operator|.
name|append
argument_list|(
name|AbfsHttpConstants
operator|.
name|SINGLE_WHITE_SPACE
argument_list|)
operator|.
name|append
argument_list|(
name|LATENCY_COUNT_KEY
argument_list|)
operator|.
name|append
argument_list|(
name|AbfsHttpConstants
operator|.
name|EQUAL
argument_list|)
operator|.
name|append
argument_list|(
name|STRING_PLACEHOLDER
argument_list|)
operator|.
name|append
argument_list|(
name|STRING_PLACEHOLDER
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|trackInfo (AbfsPerfInfo perfInfo)
specifier|public
name|void
name|trackInfo
parameter_list|(
name|AbfsPerfInfo
name|perfInfo
parameter_list|)
block|{
if|if
condition|(
operator|!
name|enabled
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|isValidInstant
argument_list|(
name|perfInfo
operator|.
name|getAggregateStart
argument_list|()
argument_list|)
operator|&&
name|perfInfo
operator|.
name|getAggregateCount
argument_list|()
operator|>
literal|0
condition|)
block|{
name|recordClientLatency
argument_list|(
name|perfInfo
operator|.
name|getTrackingStart
argument_list|()
argument_list|,
name|perfInfo
operator|.
name|getTrackingEnd
argument_list|()
argument_list|,
name|perfInfo
operator|.
name|getCallerName
argument_list|()
argument_list|,
name|perfInfo
operator|.
name|getCalleeName
argument_list|()
argument_list|,
name|perfInfo
operator|.
name|getSuccess
argument_list|()
argument_list|,
name|perfInfo
operator|.
name|getAggregateStart
argument_list|()
argument_list|,
name|perfInfo
operator|.
name|getAggregateCount
argument_list|()
argument_list|,
name|perfInfo
operator|.
name|getResult
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|recordClientLatency
argument_list|(
name|perfInfo
operator|.
name|getTrackingStart
argument_list|()
argument_list|,
name|perfInfo
operator|.
name|getTrackingEnd
argument_list|()
argument_list|,
name|perfInfo
operator|.
name|getCallerName
argument_list|()
argument_list|,
name|perfInfo
operator|.
name|getCalleeName
argument_list|()
argument_list|,
name|perfInfo
operator|.
name|getSuccess
argument_list|()
argument_list|,
name|perfInfo
operator|.
name|getResult
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getLatencyInstant ()
specifier|public
name|Instant
name|getLatencyInstant
parameter_list|()
block|{
if|if
condition|(
operator|!
name|enabled
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|Instant
operator|.
name|now
argument_list|()
return|;
block|}
DECL|method|recordClientLatency ( Instant operationStart, Instant operationStop, String callerName, String calleeName, boolean success, AbfsPerfLoggable res)
specifier|private
name|void
name|recordClientLatency
parameter_list|(
name|Instant
name|operationStart
parameter_list|,
name|Instant
name|operationStop
parameter_list|,
name|String
name|callerName
parameter_list|,
name|String
name|calleeName
parameter_list|,
name|boolean
name|success
parameter_list|,
name|AbfsPerfLoggable
name|res
parameter_list|)
block|{
name|Instant
name|trackerStart
init|=
name|Instant
operator|.
name|now
argument_list|()
decl_stmt|;
name|long
name|latency
init|=
name|isValidInstant
argument_list|(
name|operationStart
argument_list|)
operator|&&
name|isValidInstant
argument_list|(
name|operationStop
argument_list|)
condition|?
name|Duration
operator|.
name|between
argument_list|(
name|operationStart
argument_list|,
name|operationStop
argument_list|)
operator|.
name|toMillis
argument_list|()
else|:
operator|-
literal|1
decl_stmt|;
name|String
name|latencyDetails
init|=
name|String
operator|.
name|format
argument_list|(
name|singletonLatencyReportingFormat
argument_list|,
name|Instant
operator|.
name|now
argument_list|()
argument_list|,
name|callerName
argument_list|,
name|calleeName
argument_list|,
name|success
condition|?
literal|"Succeeded"
else|:
literal|"Failed"
argument_list|,
name|latency
argument_list|,
name|res
operator|==
literal|null
condition|?
literal|""
else|:
operator|(
literal|" "
operator|+
name|res
operator|.
name|getLogString
argument_list|()
operator|)
argument_list|)
decl_stmt|;
name|this
operator|.
name|offerToQueue
argument_list|(
name|trackerStart
argument_list|,
name|latencyDetails
argument_list|)
expr_stmt|;
block|}
DECL|method|recordClientLatency ( Instant operationStart, Instant operationStop, String callerName, String calleeName, boolean success, Instant aggregateStart, long aggregateCount, AbfsPerfLoggable res)
specifier|private
name|void
name|recordClientLatency
parameter_list|(
name|Instant
name|operationStart
parameter_list|,
name|Instant
name|operationStop
parameter_list|,
name|String
name|callerName
parameter_list|,
name|String
name|calleeName
parameter_list|,
name|boolean
name|success
parameter_list|,
name|Instant
name|aggregateStart
parameter_list|,
name|long
name|aggregateCount
parameter_list|,
name|AbfsPerfLoggable
name|res
parameter_list|)
block|{
name|Instant
name|trackerStart
init|=
name|Instant
operator|.
name|now
argument_list|()
decl_stmt|;
name|long
name|latency
init|=
name|isValidInstant
argument_list|(
name|operationStart
argument_list|)
operator|&&
name|isValidInstant
argument_list|(
name|operationStop
argument_list|)
condition|?
name|Duration
operator|.
name|between
argument_list|(
name|operationStart
argument_list|,
name|operationStop
argument_list|)
operator|.
name|toMillis
argument_list|()
else|:
operator|-
literal|1
decl_stmt|;
name|long
name|aggregateLatency
init|=
name|isValidInstant
argument_list|(
name|aggregateStart
argument_list|)
operator|&&
name|isValidInstant
argument_list|(
name|operationStop
argument_list|)
condition|?
name|Duration
operator|.
name|between
argument_list|(
name|aggregateStart
argument_list|,
name|operationStop
argument_list|)
operator|.
name|toMillis
argument_list|()
else|:
operator|-
literal|1
decl_stmt|;
name|String
name|latencyDetails
init|=
name|String
operator|.
name|format
argument_list|(
name|aggregateLatencyReportingFormat
argument_list|,
name|Instant
operator|.
name|now
argument_list|()
argument_list|,
name|callerName
argument_list|,
name|calleeName
argument_list|,
name|success
condition|?
literal|"Succeeded"
else|:
literal|"Failed"
argument_list|,
name|latency
argument_list|,
name|aggregateLatency
argument_list|,
name|aggregateCount
argument_list|,
name|res
operator|==
literal|null
condition|?
literal|""
else|:
operator|(
literal|" "
operator|+
name|res
operator|.
name|getLogString
argument_list|()
operator|)
argument_list|)
decl_stmt|;
name|offerToQueue
argument_list|(
name|trackerStart
argument_list|,
name|latencyDetails
argument_list|)
expr_stmt|;
block|}
DECL|method|getClientLatency ()
specifier|public
name|String
name|getClientLatency
parameter_list|()
block|{
if|if
condition|(
operator|!
name|enabled
condition|)
block|{
return|return
literal|null
return|;
block|}
name|Instant
name|trackerStart
init|=
name|Instant
operator|.
name|now
argument_list|()
decl_stmt|;
name|String
name|latencyDetails
init|=
name|queue
operator|.
name|poll
argument_list|()
decl_stmt|;
comment|// non-blocking pop
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|Instant
name|stop
init|=
name|Instant
operator|.
name|now
argument_list|()
decl_stmt|;
name|long
name|elapsed
init|=
name|Duration
operator|.
name|between
argument_list|(
name|trackerStart
argument_list|,
name|stop
argument_list|)
operator|.
name|toMillis
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Dequeued latency info [{} ms]: {}"
argument_list|,
name|elapsed
argument_list|,
name|latencyDetails
argument_list|)
expr_stmt|;
block|}
return|return
name|latencyDetails
return|;
block|}
DECL|method|offerToQueue (Instant trackerStart, String latencyDetails)
specifier|private
name|void
name|offerToQueue
parameter_list|(
name|Instant
name|trackerStart
parameter_list|,
name|String
name|latencyDetails
parameter_list|)
block|{
name|queue
operator|.
name|offer
argument_list|(
name|latencyDetails
argument_list|)
expr_stmt|;
comment|// non-blocking append
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|Instant
name|trackerStop
init|=
name|Instant
operator|.
name|now
argument_list|()
decl_stmt|;
name|long
name|elapsed
init|=
name|Duration
operator|.
name|between
argument_list|(
name|trackerStart
argument_list|,
name|trackerStop
argument_list|)
operator|.
name|toMillis
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Queued latency info [{} ms]: {}"
argument_list|,
name|elapsed
argument_list|,
name|latencyDetails
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|isValidInstant (Instant testInstant)
specifier|private
name|boolean
name|isValidInstant
parameter_list|(
name|Instant
name|testInstant
parameter_list|)
block|{
return|return
name|testInstant
operator|!=
literal|null
operator|&&
name|testInstant
operator|!=
name|Instant
operator|.
name|MIN
operator|&&
name|testInstant
operator|!=
name|Instant
operator|.
name|MAX
return|;
block|}
block|}
end_class

end_unit

