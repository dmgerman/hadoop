begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|InvalidProtocolBufferException
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
name|InterfaceStability
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
name|ha
operator|.
name|ActiveStandbyElector
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
name|ServiceFailedException
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
name|service
operator|.
name|AbstractService
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
name|StringUtils
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
name|ZKUtil
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
name|HAUtil
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
name|event
operator|.
name|Dispatcher
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
name|YarnRuntimeException
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
name|proto
operator|.
name|YarnServerResourceManagerServiceProtos
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|KeeperException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|data
operator|.
name|ACL
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
name|Collections
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

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|EmbeddedElectorService
specifier|public
class|class
name|EmbeddedElectorService
extends|extends
name|AbstractService
implements|implements
name|ActiveStandbyElector
operator|.
name|ActiveStandbyElectorCallback
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
name|EmbeddedElectorService
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|req
specifier|private
specifier|static
specifier|final
name|HAServiceProtocol
operator|.
name|StateChangeRequestInfo
name|req
init|=
operator|new
name|HAServiceProtocol
operator|.
name|StateChangeRequestInfo
argument_list|(
name|HAServiceProtocol
operator|.
name|RequestSource
operator|.
name|REQUEST_BY_ZKFC
argument_list|)
decl_stmt|;
DECL|field|rmContext
specifier|private
name|RMContext
name|rmContext
decl_stmt|;
DECL|field|localActiveNodeInfo
specifier|private
name|byte
index|[]
name|localActiveNodeInfo
decl_stmt|;
DECL|field|elector
specifier|private
name|ActiveStandbyElector
name|elector
decl_stmt|;
DECL|method|EmbeddedElectorService (RMContext rmContext)
name|EmbeddedElectorService
parameter_list|(
name|RMContext
name|rmContext
parameter_list|)
block|{
name|super
argument_list|(
name|EmbeddedElectorService
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|rmContext
operator|=
name|rmContext
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceInit (Configuration conf)
specifier|protected
specifier|synchronized
name|void
name|serviceInit
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
name|conf
operator|=
name|conf
operator|instanceof
name|YarnConfiguration
condition|?
name|conf
else|:
operator|new
name|YarnConfiguration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|String
name|zkQuorum
init|=
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|RM_ZK_ADDRESS
argument_list|)
decl_stmt|;
if|if
condition|(
name|zkQuorum
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
literal|"Embedded automatic failover "
operator|+
literal|"is enabled, but "
operator|+
name|YarnConfiguration
operator|.
name|RM_ZK_ADDRESS
operator|+
literal|" is not set"
argument_list|)
throw|;
block|}
name|String
name|rmId
init|=
name|HAUtil
operator|.
name|getRMHAId
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|String
name|clusterId
init|=
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|RM_CLUSTER_ID
argument_list|)
decl_stmt|;
if|if
condition|(
name|clusterId
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
name|YarnConfiguration
operator|.
name|RM_CLUSTER_ID
operator|+
literal|" is not specified!"
argument_list|)
throw|;
block|}
name|localActiveNodeInfo
operator|=
name|createActiveNodeInfo
argument_list|(
name|clusterId
argument_list|,
name|rmId
argument_list|)
expr_stmt|;
name|String
name|zkBasePath
init|=
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|AUTO_FAILOVER_ZK_BASE_PATH
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_AUTO_FAILOVER_ZK_BASE_PATH
argument_list|)
decl_stmt|;
name|String
name|electionZNode
init|=
name|zkBasePath
operator|+
literal|"/"
operator|+
name|clusterId
decl_stmt|;
name|long
name|zkSessionTimeout
init|=
name|conf
operator|.
name|getLong
argument_list|(
name|YarnConfiguration
operator|.
name|RM_ZK_TIMEOUT_MS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_ZK_TIMEOUT_MS
argument_list|)
decl_stmt|;
name|String
name|zkAclConf
init|=
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|RM_ZK_ACL
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_ZK_ACL
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|ACL
argument_list|>
name|zkAcls
decl_stmt|;
try|try
block|{
name|zkAcls
operator|=
name|ZKUtil
operator|.
name|parseACLs
argument_list|(
name|ZKUtil
operator|.
name|resolveConfIndirection
argument_list|(
name|zkAclConf
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ZKUtil
operator|.
name|BadAclFormatException
name|bafe
parameter_list|)
block|{
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
name|YarnConfiguration
operator|.
name|RM_ZK_ACL
operator|+
literal|"has ill-formatted ACLs"
argument_list|)
throw|;
block|}
comment|// TODO (YARN-1528): ZKAuthInfo to be set for rm-store and elector
name|List
argument_list|<
name|ZKUtil
operator|.
name|ZKAuthInfo
argument_list|>
name|zkAuths
init|=
name|Collections
operator|.
name|emptyList
argument_list|()
decl_stmt|;
name|elector
operator|=
operator|new
name|ActiveStandbyElector
argument_list|(
name|zkQuorum
argument_list|,
operator|(
name|int
operator|)
name|zkSessionTimeout
argument_list|,
name|electionZNode
argument_list|,
name|zkAcls
argument_list|,
name|zkAuths
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|elector
operator|.
name|ensureParentZNode
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|isParentZnodeSafe
argument_list|(
name|clusterId
argument_list|)
condition|)
block|{
name|notifyFatalError
argument_list|(
name|electionZNode
operator|+
literal|" znode has invalid data! "
operator|+
literal|"Might need formatting!"
argument_list|)
expr_stmt|;
block|}
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
specifier|synchronized
name|void
name|serviceStart
parameter_list|()
throws|throws
name|Exception
block|{
name|elector
operator|.
name|joinElection
argument_list|(
name|localActiveNodeInfo
argument_list|)
expr_stmt|;
name|super
operator|.
name|serviceStart
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceStop ()
specifier|protected
specifier|synchronized
name|void
name|serviceStop
parameter_list|()
throws|throws
name|Exception
block|{
name|elector
operator|.
name|quitElection
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|elector
operator|.
name|terminateConnection
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
DECL|method|becomeActive ()
specifier|public
specifier|synchronized
name|void
name|becomeActive
parameter_list|()
throws|throws
name|ServiceFailedException
block|{
try|try
block|{
name|rmContext
operator|.
name|getRMAdminService
argument_list|()
operator|.
name|transitionToActive
argument_list|(
name|req
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServiceFailedException
argument_list|(
literal|"RM could not transition to Active"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|becomeStandby ()
specifier|public
specifier|synchronized
name|void
name|becomeStandby
parameter_list|()
block|{
try|try
block|{
name|rmContext
operator|.
name|getRMAdminService
argument_list|()
operator|.
name|transitionToStandby
argument_list|(
name|req
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"RM could not transition to Standby"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|enterNeutralMode ()
specifier|public
name|void
name|enterNeutralMode
parameter_list|()
block|{
comment|/**      * Possibly due to transient connection issues. Do nothing.      * TODO: Might want to keep track of how long in this state and transition      * to standby.      */
block|}
annotation|@
name|SuppressWarnings
argument_list|(
name|value
operator|=
literal|"unchecked"
argument_list|)
annotation|@
name|Override
DECL|method|notifyFatalError (String errorMessage)
specifier|public
specifier|synchronized
name|void
name|notifyFatalError
parameter_list|(
name|String
name|errorMessage
parameter_list|)
block|{
name|rmContext
operator|.
name|getDispatcher
argument_list|()
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|RMFatalEvent
argument_list|(
name|RMFatalEventType
operator|.
name|EMBEDDED_ELECTOR_FAILED
argument_list|,
name|errorMessage
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|fenceOldActive (byte[] oldActiveData)
specifier|public
specifier|synchronized
name|void
name|fenceOldActive
parameter_list|(
name|byte
index|[]
name|oldActiveData
parameter_list|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Request to fence old active being ignored, "
operator|+
literal|"as embedded leader election doesn't support fencing"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|createActiveNodeInfo (String clusterId, String rmId)
specifier|private
specifier|static
name|byte
index|[]
name|createActiveNodeInfo
parameter_list|(
name|String
name|clusterId
parameter_list|,
name|String
name|rmId
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|YarnServerResourceManagerServiceProtos
operator|.
name|ActiveRMInfoProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setClusterId
argument_list|(
name|clusterId
argument_list|)
operator|.
name|setRmId
argument_list|(
name|rmId
argument_list|)
operator|.
name|build
argument_list|()
operator|.
name|toByteArray
argument_list|()
return|;
block|}
DECL|method|isParentZnodeSafe (String clusterId)
specifier|private
specifier|synchronized
name|boolean
name|isParentZnodeSafe
parameter_list|(
name|String
name|clusterId
parameter_list|)
throws|throws
name|InterruptedException
throws|,
name|IOException
throws|,
name|KeeperException
block|{
name|byte
index|[]
name|data
decl_stmt|;
try|try
block|{
name|data
operator|=
name|elector
operator|.
name|getActiveData
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ActiveStandbyElector
operator|.
name|ActiveNotFoundException
name|e
parameter_list|)
block|{
comment|// no active found, parent znode is safe
return|return
literal|true
return|;
block|}
name|YarnServerResourceManagerServiceProtos
operator|.
name|ActiveRMInfoProto
name|proto
decl_stmt|;
try|try
block|{
name|proto
operator|=
name|YarnServerResourceManagerServiceProtos
operator|.
name|ActiveRMInfoProto
operator|.
name|parseFrom
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidProtocolBufferException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Invalid data in ZK: "
operator|+
name|StringUtils
operator|.
name|byteToHexString
argument_list|(
name|data
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
comment|// Check if the passed proto corresponds to an RM in the same cluster
if|if
condition|(
operator|!
name|proto
operator|.
name|getClusterId
argument_list|()
operator|.
name|equals
argument_list|(
name|clusterId
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Mismatched cluster! The other RM seems "
operator|+
literal|"to be from a different cluster. Current cluster = "
operator|+
name|clusterId
operator|+
literal|"Other RM's cluster = "
operator|+
name|proto
operator|.
name|getClusterId
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

