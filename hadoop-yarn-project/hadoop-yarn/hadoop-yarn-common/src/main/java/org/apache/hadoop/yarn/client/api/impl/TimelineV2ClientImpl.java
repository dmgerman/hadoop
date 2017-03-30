begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.client.api.impl
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|client
operator|.
name|api
operator|.
name|impl
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
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
name|BlockingQueue
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
name|Callable
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
name|ExecutionException
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
name|ExecutorService
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
name|Executors
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
name|FutureTask
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
name|LinkedBlockingQueue
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
name|TimeUnit
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|MediaType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|MultivaluedMap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|classification
operator|.
name|InterfaceAudience
operator|.
name|Private
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
name|conf
operator|.
name|Configuration
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
name|security
operator|.
name|UserGroupInformation
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
name|security
operator|.
name|token
operator|.
name|delegation
operator|.
name|web
operator|.
name|DelegationTokenAuthenticatedURL
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ApplicationId
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|timelineservice
operator|.
name|TimelineEntities
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|timelineservice
operator|.
name|TimelineEntity
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
name|yarn
operator|.
name|client
operator|.
name|api
operator|.
name|TimelineV2Client
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
name|yarn
operator|.
name|conf
operator|.
name|YarnConfiguration
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
name|yarn
operator|.
name|exceptions
operator|.
name|YarnException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|api
operator|.
name|client
operator|.
name|ClientResponse
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|core
operator|.
name|util
operator|.
name|MultivaluedMapImpl
import|;
end_import

begin_comment
comment|/**  * Implementation of timeline v2 client interface.  *  */
end_comment

begin_class
DECL|class|TimelineV2ClientImpl
specifier|public
class|class
name|TimelineV2ClientImpl
extends|extends
name|TimelineV2Client
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TimelineV2ClientImpl
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|RESOURCE_URI_STR_V2
specifier|private
specifier|static
specifier|final
name|String
name|RESOURCE_URI_STR_V2
init|=
literal|"/ws/v2/timeline/"
decl_stmt|;
DECL|field|entityDispatcher
specifier|private
name|TimelineEntityDispatcher
name|entityDispatcher
decl_stmt|;
DECL|field|timelineServiceAddress
specifier|private
specifier|volatile
name|String
name|timelineServiceAddress
decl_stmt|;
comment|// Retry parameters for identifying new timeline service
comment|// TODO consider to merge with connection retry
DECL|field|maxServiceRetries
specifier|private
name|int
name|maxServiceRetries
decl_stmt|;
DECL|field|serviceRetryInterval
specifier|private
name|long
name|serviceRetryInterval
decl_stmt|;
DECL|field|connector
specifier|private
name|TimelineConnector
name|connector
decl_stmt|;
DECL|field|contextAppId
specifier|private
name|ApplicationId
name|contextAppId
decl_stmt|;
DECL|method|TimelineV2ClientImpl (ApplicationId appId)
specifier|public
name|TimelineV2ClientImpl
parameter_list|(
name|ApplicationId
name|appId
parameter_list|)
block|{
name|super
argument_list|(
name|TimelineV2ClientImpl
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|contextAppId
operator|=
name|appId
expr_stmt|;
block|}
DECL|method|getContextAppId ()
specifier|public
name|ApplicationId
name|getContextAppId
parameter_list|()
block|{
return|return
name|contextAppId
return|;
block|}
DECL|method|serviceInit (Configuration conf)
specifier|protected
name|void
name|serviceInit
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
name|YarnConfiguration
operator|.
name|timelineServiceEnabled
argument_list|(
name|conf
argument_list|)
operator|||
operator|(
name|int
operator|)
name|YarnConfiguration
operator|.
name|getTimelineServiceVersion
argument_list|(
name|conf
argument_list|)
operator|!=
literal|2
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Timeline V2 client is not properly configured. "
operator|+
literal|"Either timeline service is not enabled or version is not set to"
operator|+
literal|" 2"
argument_list|)
throw|;
block|}
name|UserGroupInformation
name|ugi
init|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
decl_stmt|;
name|UserGroupInformation
name|realUgi
init|=
name|ugi
operator|.
name|getRealUser
argument_list|()
decl_stmt|;
name|String
name|doAsUser
init|=
literal|null
decl_stmt|;
name|UserGroupInformation
name|authUgi
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|realUgi
operator|!=
literal|null
condition|)
block|{
name|authUgi
operator|=
name|realUgi
expr_stmt|;
name|doAsUser
operator|=
name|ugi
operator|.
name|getShortUserName
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|authUgi
operator|=
name|ugi
expr_stmt|;
name|doAsUser
operator|=
literal|null
expr_stmt|;
block|}
comment|// TODO need to add/cleanup filter retry later for ATSV2. similar to V1
name|DelegationTokenAuthenticatedURL
operator|.
name|Token
name|token
init|=
operator|new
name|DelegationTokenAuthenticatedURL
operator|.
name|Token
argument_list|()
decl_stmt|;
name|connector
operator|=
operator|new
name|TimelineConnector
argument_list|(
literal|false
argument_list|,
name|authUgi
argument_list|,
name|doAsUser
argument_list|,
name|token
argument_list|)
expr_stmt|;
name|addIfService
argument_list|(
name|connector
argument_list|)
expr_stmt|;
comment|// new version need to auto discovery (with retry till ATS v2 address is
comment|// got).
name|maxServiceRetries
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_CLIENT_MAX_RETRIES
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_TIMELINE_SERVICE_CLIENT_MAX_RETRIES
argument_list|)
expr_stmt|;
name|serviceRetryInterval
operator|=
name|conf
operator|.
name|getLong
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_CLIENT_RETRY_INTERVAL_MS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_TIMELINE_SERVICE_CLIENT_RETRY_INTERVAL_MS
argument_list|)
expr_stmt|;
name|entityDispatcher
operator|=
operator|new
name|TimelineEntityDispatcher
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|super
operator|.
name|serviceInit
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceStart ()
specifier|protected
name|void
name|serviceStart
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|serviceStart
argument_list|()
expr_stmt|;
name|entityDispatcher
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceStop ()
specifier|protected
name|void
name|serviceStop
parameter_list|()
throws|throws
name|Exception
block|{
name|entityDispatcher
operator|.
name|stop
argument_list|()
expr_stmt|;
name|super
operator|.
name|serviceStop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|putEntities (TimelineEntity... entities)
specifier|public
name|void
name|putEntities
parameter_list|(
name|TimelineEntity
modifier|...
name|entities
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
block|{
name|entityDispatcher
operator|.
name|dispatchEntities
argument_list|(
literal|true
argument_list|,
name|entities
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|putEntitiesAsync (TimelineEntity... entities)
specifier|public
name|void
name|putEntitiesAsync
parameter_list|(
name|TimelineEntity
modifier|...
name|entities
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
block|{
name|entityDispatcher
operator|.
name|dispatchEntities
argument_list|(
literal|false
argument_list|,
name|entities
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setTimelineServiceAddress (String address)
specifier|public
name|void
name|setTimelineServiceAddress
parameter_list|(
name|String
name|address
parameter_list|)
block|{
name|this
operator|.
name|timelineServiceAddress
operator|=
name|address
expr_stmt|;
block|}
annotation|@
name|Private
DECL|method|putObjects (String path, MultivaluedMap<String, String> params, Object obj)
specifier|protected
name|void
name|putObjects
parameter_list|(
name|String
name|path
parameter_list|,
name|MultivaluedMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
parameter_list|,
name|Object
name|obj
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
block|{
name|int
name|retries
init|=
name|verifyRestEndPointAvailable
argument_list|()
decl_stmt|;
comment|// timelineServiceAddress could be stale, add retry logic here.
name|boolean
name|needRetry
init|=
literal|true
decl_stmt|;
while|while
condition|(
name|needRetry
condition|)
block|{
try|try
block|{
name|URI
name|uri
init|=
name|TimelineConnector
operator|.
name|constructResURI
argument_list|(
name|getConfig
argument_list|()
argument_list|,
name|timelineServiceAddress
argument_list|,
name|RESOURCE_URI_STR_V2
argument_list|)
decl_stmt|;
name|putObjects
argument_list|(
name|uri
argument_list|,
name|path
argument_list|,
name|params
argument_list|,
name|obj
argument_list|)
expr_stmt|;
name|needRetry
operator|=
literal|false
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// handle exception for timelineServiceAddress being updated.
name|checkRetryWithSleep
argument_list|(
name|retries
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|retries
operator|--
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Check if reaching to maximum of retries.    *    * @param retries    * @param e    */
DECL|method|checkRetryWithSleep (int retries, IOException e)
specifier|private
name|void
name|checkRetryWithSleep
parameter_list|(
name|int
name|retries
parameter_list|,
name|IOException
name|e
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
if|if
condition|(
name|retries
operator|>
literal|0
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|this
operator|.
name|serviceRetryInterval
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ex
parameter_list|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|YarnException
argument_list|(
literal|"Interrupted while retrying to connect to ATS"
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|StringBuilder
name|msg
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"TimelineClient has reached to max retry times : "
argument_list|)
decl_stmt|;
name|msg
operator|.
name|append
argument_list|(
name|this
operator|.
name|maxServiceRetries
argument_list|)
expr_stmt|;
name|msg
operator|.
name|append
argument_list|(
literal|" for service address: "
argument_list|)
expr_stmt|;
name|msg
operator|.
name|append
argument_list|(
name|timelineServiceAddress
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|msg
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
name|msg
operator|.
name|toString
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|putObjects (URI base, String path, MultivaluedMap<String, String> params, Object obj)
specifier|protected
name|void
name|putObjects
parameter_list|(
name|URI
name|base
parameter_list|,
name|String
name|path
parameter_list|,
name|MultivaluedMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
parameter_list|,
name|Object
name|obj
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
block|{
name|ClientResponse
name|resp
decl_stmt|;
try|try
block|{
name|resp
operator|=
name|connector
operator|.
name|getClient
argument_list|()
operator|.
name|resource
argument_list|(
name|base
argument_list|)
operator|.
name|path
argument_list|(
name|path
argument_list|)
operator|.
name|queryParams
argument_list|(
name|params
argument_list|)
operator|.
name|accept
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|)
operator|.
name|type
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|)
operator|.
name|put
argument_list|(
name|ClientResponse
operator|.
name|class
argument_list|,
name|obj
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|re
parameter_list|)
block|{
comment|// runtime exception is expected if the client cannot connect the server
name|String
name|msg
init|=
literal|"Failed to get the response from the timeline server."
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|msg
argument_list|,
name|re
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
name|re
argument_list|)
throw|;
block|}
if|if
condition|(
name|resp
operator|==
literal|null
operator|||
name|resp
operator|.
name|getStatusInfo
argument_list|()
operator|.
name|getStatusCode
argument_list|()
operator|!=
name|ClientResponse
operator|.
name|Status
operator|.
name|OK
operator|.
name|getStatusCode
argument_list|()
condition|)
block|{
name|String
name|msg
init|=
literal|"Response from the timeline server is "
operator|+
operator|(
operator|(
name|resp
operator|==
literal|null
operator|)
condition|?
literal|"null"
else|:
literal|"not successful,"
operator|+
literal|" HTTP error code: "
operator|+
name|resp
operator|.
name|getStatus
argument_list|()
operator|+
literal|", Server response:\n"
operator|+
name|resp
operator|.
name|getEntity
argument_list|(
name|String
operator|.
name|class
argument_list|)
operator|)
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|msg
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|YarnException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
block|}
DECL|method|verifyRestEndPointAvailable ()
specifier|private
name|int
name|verifyRestEndPointAvailable
parameter_list|()
throws|throws
name|YarnException
block|{
comment|// timelineServiceAddress could haven't be initialized yet
comment|// or stale (only for new timeline service)
name|int
name|retries
init|=
name|pollTimelineServiceAddress
argument_list|(
name|this
operator|.
name|maxServiceRetries
argument_list|)
decl_stmt|;
if|if
condition|(
name|timelineServiceAddress
operator|==
literal|null
condition|)
block|{
name|String
name|errMessage
init|=
literal|"TimelineClient has reached to max retry times : "
operator|+
name|this
operator|.
name|maxServiceRetries
operator|+
literal|", but failed to fetch timeline service address. Please verify"
operator|+
literal|" Timeline Auxiliary Service is configured in all the NMs"
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|errMessage
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|YarnException
argument_list|(
name|errMessage
argument_list|)
throw|;
block|}
return|return
name|retries
return|;
block|}
comment|/**    * Poll TimelineServiceAddress for maximum of retries times if it is null.    *    * @param retries    * @return the left retry times    * @throws IOException    */
DECL|method|pollTimelineServiceAddress (int retries)
specifier|private
name|int
name|pollTimelineServiceAddress
parameter_list|(
name|int
name|retries
parameter_list|)
throws|throws
name|YarnException
block|{
while|while
condition|(
name|timelineServiceAddress
operator|==
literal|null
operator|&&
name|retries
operator|>
literal|0
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|this
operator|.
name|serviceRetryInterval
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|YarnException
argument_list|(
literal|"Interrupted while trying to connect ATS"
argument_list|)
throw|;
block|}
name|retries
operator|--
expr_stmt|;
block|}
return|return
name|retries
return|;
block|}
DECL|class|EntitiesHolder
specifier|private
specifier|final
class|class
name|EntitiesHolder
extends|extends
name|FutureTask
argument_list|<
name|Void
argument_list|>
block|{
DECL|field|entities
specifier|private
specifier|final
name|TimelineEntities
name|entities
decl_stmt|;
DECL|field|isSync
specifier|private
specifier|final
name|boolean
name|isSync
decl_stmt|;
DECL|method|EntitiesHolder (final TimelineEntities entities, final boolean isSync)
name|EntitiesHolder
parameter_list|(
specifier|final
name|TimelineEntities
name|entities
parameter_list|,
specifier|final
name|boolean
name|isSync
parameter_list|)
block|{
name|super
argument_list|(
operator|new
name|Callable
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
comment|// publishEntities()
specifier|public
name|Void
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|MultivaluedMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
init|=
operator|new
name|MultivaluedMapImpl
argument_list|()
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"appid"
argument_list|,
name|getContextAppId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"async"
argument_list|,
name|Boolean
operator|.
name|toString
argument_list|(
operator|!
name|isSync
argument_list|)
argument_list|)
expr_stmt|;
name|putObjects
argument_list|(
literal|"entities"
argument_list|,
name|params
argument_list|,
name|entities
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|this
operator|.
name|entities
operator|=
name|entities
expr_stmt|;
name|this
operator|.
name|isSync
operator|=
name|isSync
expr_stmt|;
block|}
DECL|method|isSync ()
specifier|public
name|boolean
name|isSync
parameter_list|()
block|{
return|return
name|isSync
return|;
block|}
DECL|method|getEntities ()
specifier|public
name|TimelineEntities
name|getEntities
parameter_list|()
block|{
return|return
name|entities
return|;
block|}
block|}
comment|/**    * This class is responsible for collecting the timeline entities and    * publishing them in async.    */
DECL|class|TimelineEntityDispatcher
specifier|private
class|class
name|TimelineEntityDispatcher
block|{
comment|/**      * Time period for which the timelineclient will wait for draining after      * stop.      */
DECL|field|drainTimeoutPeriod
specifier|private
specifier|final
name|long
name|drainTimeoutPeriod
decl_stmt|;
DECL|field|numberOfAsyncsToMerge
specifier|private
name|int
name|numberOfAsyncsToMerge
decl_stmt|;
DECL|field|timelineEntityQueue
specifier|private
specifier|final
name|BlockingQueue
argument_list|<
name|EntitiesHolder
argument_list|>
name|timelineEntityQueue
decl_stmt|;
DECL|field|executor
specifier|private
name|ExecutorService
name|executor
decl_stmt|;
DECL|method|TimelineEntityDispatcher (Configuration conf)
name|TimelineEntityDispatcher
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|timelineEntityQueue
operator|=
operator|new
name|LinkedBlockingQueue
argument_list|<
name|EntitiesHolder
argument_list|>
argument_list|()
expr_stmt|;
name|numberOfAsyncsToMerge
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|YarnConfiguration
operator|.
name|NUMBER_OF_ASYNC_ENTITIES_TO_MERGE
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_NUMBER_OF_ASYNC_ENTITIES_TO_MERGE
argument_list|)
expr_stmt|;
name|drainTimeoutPeriod
operator|=
name|conf
operator|.
name|getLong
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_V2_CLIENT_DRAIN_TIME_MILLIS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_TIMELINE_V2_CLIENT_DRAIN_TIME_MILLIS
argument_list|)
expr_stmt|;
block|}
DECL|method|createRunnable ()
name|Runnable
name|createRunnable
parameter_list|()
block|{
return|return
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|EntitiesHolder
name|entitiesHolder
decl_stmt|;
while|while
condition|(
operator|!
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|isInterrupted
argument_list|()
condition|)
block|{
comment|// Merge all the async calls and make one push, but if its sync
comment|// call push immediately
try|try
block|{
name|entitiesHolder
operator|=
name|timelineEntityQueue
operator|.
name|take
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Timeline dispatcher thread was interrupted "
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|entitiesHolder
operator|!=
literal|null
condition|)
block|{
name|publishWithoutBlockingOnQueue
argument_list|(
name|entitiesHolder
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|timelineEntityQueue
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Yet to publish "
operator|+
name|timelineEntityQueue
operator|.
name|size
argument_list|()
operator|+
literal|" timelineEntities, draining them now. "
argument_list|)
expr_stmt|;
block|}
comment|// Try to drain the remaining entities to be published @ the max for
comment|// 2 seconds
name|long
name|timeTillweDrain
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
name|drainTimeoutPeriod
decl_stmt|;
while|while
condition|(
operator|!
name|timelineEntityQueue
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|publishWithoutBlockingOnQueue
argument_list|(
name|timelineEntityQueue
operator|.
name|poll
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|>
name|timeTillweDrain
condition|)
block|{
comment|// time elapsed stop publishing further....
if|if
condition|(
operator|!
name|timelineEntityQueue
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Time to drain elapsed! Remaining "
operator|+
name|timelineEntityQueue
operator|.
name|size
argument_list|()
operator|+
literal|"timelineEntities will not"
operator|+
literal|" be published"
argument_list|)
expr_stmt|;
comment|// if some entities were not drained then we need interrupt
comment|// the threads which had put sync EntityHolders to the queue.
name|EntitiesHolder
name|nextEntityInTheQueue
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|nextEntityInTheQueue
operator|=
name|timelineEntityQueue
operator|.
name|poll
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|nextEntityInTheQueue
operator|.
name|cancel
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
break|break;
block|}
block|}
block|}
block|}
comment|/**          * Publishes the given EntitiesHolder and return immediately if sync          * call, else tries to fetch the EntitiesHolder from the queue in non          * blocking fashion and collate the Entities if possible before          * publishing through REST.          *          * @param entitiesHolder          */
specifier|private
name|void
name|publishWithoutBlockingOnQueue
parameter_list|(
name|EntitiesHolder
name|entitiesHolder
parameter_list|)
block|{
if|if
condition|(
name|entitiesHolder
operator|.
name|isSync
argument_list|()
condition|)
block|{
name|entitiesHolder
operator|.
name|run
argument_list|()
expr_stmt|;
return|return;
block|}
name|int
name|count
init|=
literal|1
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
comment|// loop till we find a sync put Entities or there is nothing
comment|// to take
name|EntitiesHolder
name|nextEntityInTheQueue
init|=
name|timelineEntityQueue
operator|.
name|poll
argument_list|()
decl_stmt|;
if|if
condition|(
name|nextEntityInTheQueue
operator|==
literal|null
condition|)
block|{
comment|// Nothing in the queue just publish and get back to the
comment|// blocked wait state
name|entitiesHolder
operator|.
name|run
argument_list|()
expr_stmt|;
break|break;
block|}
elseif|else
if|if
condition|(
name|nextEntityInTheQueue
operator|.
name|isSync
argument_list|()
condition|)
block|{
comment|// flush all the prev async entities first
name|entitiesHolder
operator|.
name|run
argument_list|()
expr_stmt|;
comment|// and then flush the sync entity
name|nextEntityInTheQueue
operator|.
name|run
argument_list|()
expr_stmt|;
break|break;
block|}
else|else
block|{
comment|// append all async entities together and then flush
name|entitiesHolder
operator|.
name|getEntities
argument_list|()
operator|.
name|addEntities
argument_list|(
name|nextEntityInTheQueue
operator|.
name|getEntities
argument_list|()
operator|.
name|getEntities
argument_list|()
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
if|if
condition|(
name|count
operator|==
name|numberOfAsyncsToMerge
condition|)
block|{
comment|// Flush the entities if the number of the async
comment|// putEntites merged reaches the desired limit. To avoid
comment|// collecting multiple entities and delaying for a long
comment|// time.
name|entitiesHolder
operator|.
name|run
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
block|}
return|;
block|}
DECL|method|dispatchEntities (boolean sync, TimelineEntity[] entitiesTobePublished)
specifier|public
name|void
name|dispatchEntities
parameter_list|(
name|boolean
name|sync
parameter_list|,
name|TimelineEntity
index|[]
name|entitiesTobePublished
parameter_list|)
throws|throws
name|YarnException
block|{
if|if
condition|(
name|executor
operator|.
name|isShutdown
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|YarnException
argument_list|(
literal|"Timeline client is in the process of stopping,"
operator|+
literal|" not accepting any more TimelineEntities"
argument_list|)
throw|;
block|}
comment|// wrap all TimelineEntity into TimelineEntities object
name|TimelineEntities
name|entities
init|=
operator|new
name|TimelineEntities
argument_list|()
decl_stmt|;
for|for
control|(
name|TimelineEntity
name|entity
range|:
name|entitiesTobePublished
control|)
block|{
name|entities
operator|.
name|addEntity
argument_list|(
name|entity
argument_list|)
expr_stmt|;
block|}
comment|// created a holder and place it in queue
name|EntitiesHolder
name|entitiesHolder
init|=
operator|new
name|EntitiesHolder
argument_list|(
name|entities
argument_list|,
name|sync
argument_list|)
decl_stmt|;
try|try
block|{
name|timelineEntityQueue
operator|.
name|put
argument_list|(
name|entitiesHolder
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|YarnException
argument_list|(
literal|"Failed while adding entity to the queue for publishing"
argument_list|,
name|e
argument_list|)
throw|;
block|}
if|if
condition|(
name|sync
condition|)
block|{
comment|// In sync call we need to wait till its published and if any error then
comment|// throw it back
try|try
block|{
name|entitiesHolder
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|YarnException
argument_list|(
literal|"Failed while publishing entity"
argument_list|,
name|e
operator|.
name|getCause
argument_list|()
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|YarnException
argument_list|(
literal|"Interrupted while publishing entity"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|start ()
specifier|public
name|void
name|start
parameter_list|()
block|{
name|executor
operator|=
name|Executors
operator|.
name|newSingleThreadExecutor
argument_list|()
expr_stmt|;
name|executor
operator|.
name|execute
argument_list|(
name|createRunnable
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|stop ()
specifier|public
name|void
name|stop
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Stopping TimelineClient."
argument_list|)
expr_stmt|;
name|executor
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
try|try
block|{
name|executor
operator|.
name|awaitTermination
argument_list|(
name|drainTimeoutPeriod
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

