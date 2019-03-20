begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.container.replication
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|scm
operator|.
name|container
operator|.
name|replication
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|ObjectName
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
import|;
end_import

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
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
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
name|atomic
operator|.
name|AtomicBoolean
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
name|hdds
operator|.
name|HddsConfigKeys
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
name|metrics2
operator|.
name|util
operator|.
name|MBeans
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
name|annotations
operator|.
name|VisibleForTesting
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
name|utils
operator|.
name|Scheduler
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

begin_comment
comment|/**  * Event listener to track the current state of replication.  */
end_comment

begin_class
DECL|class|ReplicationActivityStatus
specifier|public
class|class
name|ReplicationActivityStatus
implements|implements
name|ReplicationActivityStatusMXBean
implements|,
name|Closeable
block|{
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
name|ReplicationActivityStatus
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|scheduler
specifier|private
name|Scheduler
name|scheduler
decl_stmt|;
DECL|field|replicationEnabled
specifier|private
name|AtomicBoolean
name|replicationEnabled
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
DECL|field|jmxObjectName
specifier|private
name|ObjectName
name|jmxObjectName
decl_stmt|;
DECL|method|ReplicationActivityStatus (Scheduler scheduler)
specifier|public
name|ReplicationActivityStatus
parameter_list|(
name|Scheduler
name|scheduler
parameter_list|)
block|{
name|this
operator|.
name|scheduler
operator|=
name|scheduler
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|isReplicationEnabled ()
specifier|public
name|boolean
name|isReplicationEnabled
parameter_list|()
block|{
return|return
name|replicationEnabled
operator|.
name|get
argument_list|()
return|;
block|}
annotation|@
name|VisibleForTesting
annotation|@
name|Override
DECL|method|setReplicationEnabled (boolean enabled)
specifier|public
name|void
name|setReplicationEnabled
parameter_list|(
name|boolean
name|enabled
parameter_list|)
block|{
name|replicationEnabled
operator|.
name|set
argument_list|(
name|enabled
argument_list|)
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|enableReplication ()
specifier|public
name|void
name|enableReplication
parameter_list|()
block|{
name|replicationEnabled
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|start ()
specifier|public
name|void
name|start
parameter_list|()
block|{
try|try
block|{
name|this
operator|.
name|jmxObjectName
operator|=
name|MBeans
operator|.
name|register
argument_list|(
literal|"StorageContainerManager"
argument_list|,
literal|"ReplicationActivityStatus"
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"JMX bean for ReplicationActivityStatus can't be registered"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|this
operator|.
name|jmxObjectName
operator|!=
literal|null
condition|)
block|{
name|MBeans
operator|.
name|unregister
argument_list|(
name|jmxObjectName
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Waits for    * {@link HddsConfigKeys#HDDS_SCM_WAIT_TIME_AFTER_CHILL_MODE_EXIT} and set    * replicationEnabled to start replication monitor thread.    */
DECL|method|fireReplicationStart (boolean chillModeStatus, long waitTime)
specifier|public
name|void
name|fireReplicationStart
parameter_list|(
name|boolean
name|chillModeStatus
parameter_list|,
name|long
name|waitTime
parameter_list|)
block|{
if|if
condition|(
operator|!
name|chillModeStatus
condition|)
block|{
name|scheduler
operator|.
name|schedule
argument_list|(
parameter_list|()
lambda|->
block|{
name|setReplicationEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Replication Timer sleep for {} ms completed. Enable "
operator|+
literal|"Replication"
argument_list|,
name|waitTime
argument_list|)
expr_stmt|;
block|}
argument_list|,
name|waitTime
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

