begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ha
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ha
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
name|InetSocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|SocketFactory
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
name|conf
operator|.
name|Configuration
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|CommonConfigurationKeys
operator|.
name|*
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
name|ha
operator|.
name|HAServiceProtocol
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
name|ha
operator|.
name|HAServiceProtocol
operator|.
name|HAServiceState
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
name|ha
operator|.
name|HealthCheckFailedException
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
name|ha
operator|.
name|protocolPB
operator|.
name|HAServiceProtocolClientSideTranslatorPB
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
name|ipc
operator|.
name|RPC
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
name|net
operator|.
name|NetUtils
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
name|util
operator|.
name|Daemon
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
import|;
end_import

begin_comment
comment|/**  * This class is a daemon which runs in a loop, periodically heartbeating  * with an HA service. It is responsible for keeping track of that service's  * health and exposing callbacks to the failover controller when the health  * status changes.  *   * Classes which need callbacks should implement the {@link Callback}  * interface.  */
end_comment

begin_class
DECL|class|HealthMonitor
class|class
name|HealthMonitor
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
name|HealthMonitor
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|daemon
specifier|private
name|Daemon
name|daemon
decl_stmt|;
DECL|field|connectRetryInterval
specifier|private
name|long
name|connectRetryInterval
decl_stmt|;
DECL|field|checkIntervalMillis
specifier|private
name|long
name|checkIntervalMillis
decl_stmt|;
DECL|field|sleepAfterDisconnectMillis
specifier|private
name|long
name|sleepAfterDisconnectMillis
decl_stmt|;
DECL|field|rpcTimeout
specifier|private
name|int
name|rpcTimeout
decl_stmt|;
DECL|field|shouldRun
specifier|private
specifier|volatile
name|boolean
name|shouldRun
init|=
literal|true
decl_stmt|;
comment|/** The connected proxy */
DECL|field|proxy
specifier|private
name|HAServiceProtocol
name|proxy
decl_stmt|;
comment|/** The address running the HA Service */
DECL|field|addrToMonitor
specifier|private
specifier|final
name|InetSocketAddress
name|addrToMonitor
decl_stmt|;
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
decl_stmt|;
DECL|field|state
specifier|private
name|State
name|state
init|=
name|State
operator|.
name|INITIALIZING
decl_stmt|;
comment|/**    * Listeners for state changes    */
DECL|field|callbacks
specifier|private
name|List
argument_list|<
name|Callback
argument_list|>
name|callbacks
init|=
name|Collections
operator|.
name|synchronizedList
argument_list|(
operator|new
name|LinkedList
argument_list|<
name|Callback
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|lastServiceState
specifier|private
name|HAServiceStatus
name|lastServiceState
init|=
operator|new
name|HAServiceStatus
argument_list|(
name|HAServiceState
operator|.
name|INITIALIZING
argument_list|)
decl_stmt|;
DECL|enum|State
enum|enum
name|State
block|{
comment|/**      * The health monitor is still starting up.      */
DECL|enumConstant|INITIALIZING
name|INITIALIZING
block|,
comment|/**      * The service is not responding to health check RPCs.      */
DECL|enumConstant|SERVICE_NOT_RESPONDING
name|SERVICE_NOT_RESPONDING
block|,
comment|/**      * The service is connected and healthy.      */
DECL|enumConstant|SERVICE_HEALTHY
name|SERVICE_HEALTHY
block|,
comment|/**      * The service is running but unhealthy.      */
DECL|enumConstant|SERVICE_UNHEALTHY
name|SERVICE_UNHEALTHY
block|,
comment|/**      * The health monitor itself failed unrecoverably and can      * no longer provide accurate information.      */
DECL|enumConstant|HEALTH_MONITOR_FAILED
name|HEALTH_MONITOR_FAILED
block|;   }
DECL|method|HealthMonitor (Configuration conf, InetSocketAddress addrToMonitor)
name|HealthMonitor
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|InetSocketAddress
name|addrToMonitor
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|this
operator|.
name|addrToMonitor
operator|=
name|addrToMonitor
expr_stmt|;
name|this
operator|.
name|sleepAfterDisconnectMillis
operator|=
name|conf
operator|.
name|getLong
argument_list|(
name|HA_HM_SLEEP_AFTER_DISCONNECT_KEY
argument_list|,
name|HA_HM_SLEEP_AFTER_DISCONNECT_DEFAULT
argument_list|)
expr_stmt|;
name|this
operator|.
name|checkIntervalMillis
operator|=
name|conf
operator|.
name|getLong
argument_list|(
name|HA_HM_CHECK_INTERVAL_KEY
argument_list|,
name|HA_HM_CHECK_INTERVAL_DEFAULT
argument_list|)
expr_stmt|;
name|this
operator|.
name|connectRetryInterval
operator|=
name|conf
operator|.
name|getLong
argument_list|(
name|HA_HM_CONNECT_RETRY_INTERVAL_KEY
argument_list|,
name|HA_HM_CONNECT_RETRY_INTERVAL_DEFAULT
argument_list|)
expr_stmt|;
name|this
operator|.
name|rpcTimeout
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|HA_HM_RPC_TIMEOUT_KEY
argument_list|,
name|HA_HM_RPC_TIMEOUT_DEFAULT
argument_list|)
expr_stmt|;
name|this
operator|.
name|daemon
operator|=
operator|new
name|MonitorDaemon
argument_list|()
expr_stmt|;
block|}
DECL|method|addCallback (Callback cb)
specifier|public
name|void
name|addCallback
parameter_list|(
name|Callback
name|cb
parameter_list|)
block|{
name|this
operator|.
name|callbacks
operator|.
name|add
argument_list|(
name|cb
argument_list|)
expr_stmt|;
block|}
DECL|method|removeCallback (Callback cb)
specifier|public
name|void
name|removeCallback
parameter_list|(
name|Callback
name|cb
parameter_list|)
block|{
name|callbacks
operator|.
name|remove
argument_list|(
name|cb
argument_list|)
expr_stmt|;
block|}
DECL|method|shutdown ()
specifier|public
name|void
name|shutdown
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Stopping HealthMonitor thread"
argument_list|)
expr_stmt|;
name|shouldRun
operator|=
literal|false
expr_stmt|;
name|daemon
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
comment|/**    * @return the current proxy object to the underlying service.    * Note that this may return null in the case that the service    * is not responding. Also note that, even if the last indicated    * state is healthy, the service may have gone down in the meantime.    */
DECL|method|getProxy ()
specifier|public
specifier|synchronized
name|HAServiceProtocol
name|getProxy
parameter_list|()
block|{
return|return
name|proxy
return|;
block|}
DECL|method|loopUntilConnected ()
specifier|private
name|void
name|loopUntilConnected
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|tryConnect
argument_list|()
expr_stmt|;
while|while
condition|(
name|proxy
operator|==
literal|null
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|connectRetryInterval
argument_list|)
expr_stmt|;
name|tryConnect
argument_list|()
expr_stmt|;
block|}
assert|assert
name|proxy
operator|!=
literal|null
assert|;
block|}
DECL|method|tryConnect ()
specifier|private
name|void
name|tryConnect
parameter_list|()
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
name|proxy
operator|==
literal|null
argument_list|)
expr_stmt|;
try|try
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
name|proxy
operator|=
name|createProxy
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Could not connect to local service at "
operator|+
name|addrToMonitor
operator|+
literal|": "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|proxy
operator|=
literal|null
expr_stmt|;
name|enterState
argument_list|(
name|State
operator|.
name|SERVICE_NOT_RESPONDING
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Connect to the service to be monitored. Stubbed out for easier testing.    */
DECL|method|createProxy ()
specifier|protected
name|HAServiceProtocol
name|createProxy
parameter_list|()
throws|throws
name|IOException
block|{
name|SocketFactory
name|socketFactory
init|=
name|NetUtils
operator|.
name|getDefaultSocketFactory
argument_list|(
name|conf
argument_list|)
decl_stmt|;
return|return
operator|new
name|HAServiceProtocolClientSideTranslatorPB
argument_list|(
name|addrToMonitor
argument_list|,
name|conf
argument_list|,
name|socketFactory
argument_list|,
name|rpcTimeout
argument_list|)
return|;
block|}
DECL|method|doHealthChecks ()
specifier|private
name|void
name|doHealthChecks
parameter_list|()
throws|throws
name|InterruptedException
block|{
while|while
condition|(
name|shouldRun
condition|)
block|{
name|HAServiceStatus
name|status
init|=
literal|null
decl_stmt|;
name|boolean
name|healthy
init|=
literal|false
decl_stmt|;
try|try
block|{
name|status
operator|=
name|proxy
operator|.
name|getServiceStatus
argument_list|()
expr_stmt|;
name|proxy
operator|.
name|monitorHealth
argument_list|()
expr_stmt|;
name|healthy
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|HealthCheckFailedException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Service health check failed: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|enterState
argument_list|(
name|State
operator|.
name|SERVICE_UNHEALTHY
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Transport-level exception trying to monitor health of "
operator|+
name|addrToMonitor
operator|+
literal|": "
operator|+
name|t
operator|.
name|getLocalizedMessage
argument_list|()
argument_list|)
expr_stmt|;
name|RPC
operator|.
name|stopProxy
argument_list|(
name|proxy
argument_list|)
expr_stmt|;
name|proxy
operator|=
literal|null
expr_stmt|;
name|enterState
argument_list|(
name|State
operator|.
name|SERVICE_NOT_RESPONDING
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|sleepAfterDisconnectMillis
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|status
operator|!=
literal|null
condition|)
block|{
name|setLastServiceStatus
argument_list|(
name|status
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|healthy
condition|)
block|{
name|enterState
argument_list|(
name|State
operator|.
name|SERVICE_HEALTHY
argument_list|)
expr_stmt|;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
name|checkIntervalMillis
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|setLastServiceStatus (HAServiceStatus status)
specifier|private
specifier|synchronized
name|void
name|setLastServiceStatus
parameter_list|(
name|HAServiceStatus
name|status
parameter_list|)
block|{
name|this
operator|.
name|lastServiceState
operator|=
name|status
expr_stmt|;
block|}
DECL|method|enterState (State newState)
specifier|private
specifier|synchronized
name|void
name|enterState
parameter_list|(
name|State
name|newState
parameter_list|)
block|{
if|if
condition|(
name|newState
operator|!=
name|state
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Entering state "
operator|+
name|newState
argument_list|)
expr_stmt|;
name|state
operator|=
name|newState
expr_stmt|;
synchronized|synchronized
init|(
name|callbacks
init|)
block|{
for|for
control|(
name|Callback
name|cb
range|:
name|callbacks
control|)
block|{
name|cb
operator|.
name|enteredState
argument_list|(
name|newState
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|getHealthState ()
specifier|synchronized
name|State
name|getHealthState
parameter_list|()
block|{
return|return
name|state
return|;
block|}
DECL|method|getLastServiceStatus ()
specifier|synchronized
name|HAServiceStatus
name|getLastServiceStatus
parameter_list|()
block|{
return|return
name|lastServiceState
return|;
block|}
DECL|method|isAlive ()
name|boolean
name|isAlive
parameter_list|()
block|{
return|return
name|daemon
operator|.
name|isAlive
argument_list|()
return|;
block|}
DECL|method|join ()
name|void
name|join
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|daemon
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
DECL|method|start ()
name|void
name|start
parameter_list|()
block|{
name|daemon
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
DECL|class|MonitorDaemon
specifier|private
class|class
name|MonitorDaemon
extends|extends
name|Daemon
block|{
DECL|method|MonitorDaemon ()
specifier|private
name|MonitorDaemon
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
name|setName
argument_list|(
literal|"Health Monitor for "
operator|+
name|addrToMonitor
argument_list|)
expr_stmt|;
name|setUncaughtExceptionHandler
argument_list|(
operator|new
name|UncaughtExceptionHandler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|uncaughtException
parameter_list|(
name|Thread
name|t
parameter_list|,
name|Throwable
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|fatal
argument_list|(
literal|"Health monitor failed"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|enterState
argument_list|(
name|HealthMonitor
operator|.
name|State
operator|.
name|HEALTH_MONITOR_FAILED
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
while|while
condition|(
name|shouldRun
condition|)
block|{
try|try
block|{
name|loopUntilConnected
argument_list|()
expr_stmt|;
name|doHealthChecks
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
operator|!
name|shouldRun
argument_list|,
literal|"Interrupted but still supposed to run"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/**    * Callback interface for state change events.    *     * This interface is called from a single thread which also performs    * the health monitoring. If the callback processing takes a long time,    * no further health checks will be made during this period, nor will    * other registered callbacks be called.    *     * If the callback itself throws an unchecked exception, no other    * callbacks following it will be called, and the health monitor    * will terminate, entering HEALTH_MONITOR_FAILED state.    */
DECL|interface|Callback
specifier|static
interface|interface
name|Callback
block|{
DECL|method|enteredState (State newState)
name|void
name|enteredState
parameter_list|(
name|State
name|newState
parameter_list|)
function_decl|;
block|}
comment|/**    * Simple main() for testing.    */
DECL|method|main (String[] args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|InterruptedException
block|{
if|if
condition|(
name|args
operator|.
name|length
operator|!=
literal|1
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Usage: "
operator|+
name|HealthMonitor
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"<addr to monitor>"
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|String
name|target
init|=
name|args
index|[
literal|0
index|]
decl_stmt|;
name|InetSocketAddress
name|addr
init|=
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|target
argument_list|)
decl_stmt|;
name|HealthMonitor
name|hm
init|=
operator|new
name|HealthMonitor
argument_list|(
name|conf
argument_list|,
name|addr
argument_list|)
decl_stmt|;
name|hm
operator|.
name|start
argument_list|()
expr_stmt|;
name|hm
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

