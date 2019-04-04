begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  *<p>http://www.apache.org/licenses/LICENSE-2.0  *<p>  *<p>Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.chillmode
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
name|chillmode
package|;
end_package

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
name|hdds
operator|.
name|scm
operator|.
name|block
operator|.
name|BlockManager
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
name|scm
operator|.
name|container
operator|.
name|ReplicationManager
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
name|scm
operator|.
name|server
operator|.
name|SCMClientProtocolServer
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
name|scm
operator|.
name|chillmode
operator|.
name|SCMChillModeManager
operator|.
name|ChillModeStatus
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
name|server
operator|.
name|events
operator|.
name|EventHandler
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
name|server
operator|.
name|events
operator|.
name|EventPublisher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
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

begin_comment
comment|/**  * Class to handle the activities needed to be performed after exiting chill  * mode.  */
end_comment

begin_class
DECL|class|ChillModeHandler
specifier|public
class|class
name|ChillModeHandler
implements|implements
name|EventHandler
argument_list|<
name|ChillModeStatus
argument_list|>
block|{
DECL|field|scmClientProtocolServer
specifier|private
specifier|final
name|SCMClientProtocolServer
name|scmClientProtocolServer
decl_stmt|;
DECL|field|scmBlockManager
specifier|private
specifier|final
name|BlockManager
name|scmBlockManager
decl_stmt|;
DECL|field|waitTime
specifier|private
specifier|final
name|long
name|waitTime
decl_stmt|;
DECL|field|isInChillMode
specifier|private
specifier|final
name|AtomicBoolean
name|isInChillMode
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|true
argument_list|)
decl_stmt|;
DECL|field|replicationManager
specifier|private
specifier|final
name|ReplicationManager
name|replicationManager
decl_stmt|;
comment|/**    * ChillModeHandler, to handle the logic once we exit chill mode.    * @param configuration    * @param clientProtocolServer    * @param blockManager    * @param replicationManager    */
DECL|method|ChillModeHandler (Configuration configuration, SCMClientProtocolServer clientProtocolServer, BlockManager blockManager, ReplicationManager replicationManager)
specifier|public
name|ChillModeHandler
parameter_list|(
name|Configuration
name|configuration
parameter_list|,
name|SCMClientProtocolServer
name|clientProtocolServer
parameter_list|,
name|BlockManager
name|blockManager
parameter_list|,
name|ReplicationManager
name|replicationManager
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|configuration
argument_list|,
literal|"Configuration cannot be null"
argument_list|)
expr_stmt|;
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|clientProtocolServer
argument_list|,
literal|"SCMClientProtocolServer "
operator|+
literal|"object cannot be null"
argument_list|)
expr_stmt|;
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|blockManager
argument_list|,
literal|"BlockManager object cannot be null"
argument_list|)
expr_stmt|;
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|replicationManager
argument_list|,
literal|"ReplicationManager "
operator|+
literal|"object cannot be null"
argument_list|)
expr_stmt|;
name|this
operator|.
name|waitTime
operator|=
name|configuration
operator|.
name|getTimeDuration
argument_list|(
name|HddsConfigKeys
operator|.
name|HDDS_SCM_WAIT_TIME_AFTER_CHILL_MODE_EXIT
argument_list|,
name|HddsConfigKeys
operator|.
name|HDDS_SCM_WAIT_TIME_AFTER_CHILL_MODE_EXIT_DEFAULT
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|this
operator|.
name|scmClientProtocolServer
operator|=
name|clientProtocolServer
expr_stmt|;
name|this
operator|.
name|scmBlockManager
operator|=
name|blockManager
expr_stmt|;
name|this
operator|.
name|replicationManager
operator|=
name|replicationManager
expr_stmt|;
specifier|final
name|boolean
name|chillModeEnabled
init|=
name|configuration
operator|.
name|getBoolean
argument_list|(
name|HddsConfigKeys
operator|.
name|HDDS_SCM_CHILLMODE_ENABLED
argument_list|,
name|HddsConfigKeys
operator|.
name|HDDS_SCM_CHILLMODE_ENABLED_DEFAULT
argument_list|)
decl_stmt|;
name|isInChillMode
operator|.
name|set
argument_list|(
name|chillModeEnabled
argument_list|)
expr_stmt|;
block|}
comment|/**    * Set ChillMode status based on    * {@link org.apache.hadoop.hdds.scm.events.SCMEvents#CHILL_MODE_STATUS}.    *    * Inform BlockManager, ScmClientProtocolServer and replicationAcitivity    * status about chillMode status.    *    * @param chillModeStatus    * @param publisher    */
annotation|@
name|Override
DECL|method|onMessage (ChillModeStatus chillModeStatus, EventPublisher publisher)
specifier|public
name|void
name|onMessage
parameter_list|(
name|ChillModeStatus
name|chillModeStatus
parameter_list|,
name|EventPublisher
name|publisher
parameter_list|)
block|{
try|try
block|{
name|isInChillMode
operator|.
name|set
argument_list|(
name|chillModeStatus
operator|.
name|getChillModeStatus
argument_list|()
argument_list|)
expr_stmt|;
name|scmClientProtocolServer
operator|.
name|setChillModeStatus
argument_list|(
name|isInChillMode
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|scmBlockManager
operator|.
name|setChillModeStatus
argument_list|(
name|isInChillMode
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|waitTime
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
block|}
finally|finally
block|{
name|replicationManager
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getChillModeStatus ()
specifier|public
name|boolean
name|getChillModeStatus
parameter_list|()
block|{
return|return
name|isInChillMode
operator|.
name|get
argument_list|()
return|;
block|}
block|}
end_class

end_unit

