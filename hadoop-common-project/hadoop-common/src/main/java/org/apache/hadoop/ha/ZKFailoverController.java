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
name|security
operator|.
name|PrivilegedAction
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
name|HadoopIllegalArgumentException
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
operator|.
name|ActiveStandbyElectorCallback
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
name|HAZKUtil
operator|.
name|ZKAuthInfo
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
name|HealthMonitor
operator|.
name|State
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
name|SecurityUtil
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
name|Tool
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
name|ZooDefs
operator|.
name|Ids
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
name|ToolRunner
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

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|LimitedPrivate
argument_list|(
literal|"HDFS"
argument_list|)
DECL|class|ZKFailoverController
specifier|public
specifier|abstract
class|class
name|ZKFailoverController
implements|implements
name|Tool
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|ZKFailoverController
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// TODO: this should be namespace-scoped
DECL|field|ZK_QUORUM_KEY
specifier|public
specifier|static
specifier|final
name|String
name|ZK_QUORUM_KEY
init|=
literal|"ha.zookeeper.quorum"
decl_stmt|;
DECL|field|ZK_SESSION_TIMEOUT_KEY
specifier|private
specifier|static
specifier|final
name|String
name|ZK_SESSION_TIMEOUT_KEY
init|=
literal|"ha.zookeeper.session-timeout.ms"
decl_stmt|;
DECL|field|ZK_SESSION_TIMEOUT_DEFAULT
specifier|private
specifier|static
specifier|final
name|int
name|ZK_SESSION_TIMEOUT_DEFAULT
init|=
literal|5
operator|*
literal|1000
decl_stmt|;
DECL|field|ZK_PARENT_ZNODE_KEY
specifier|private
specifier|static
specifier|final
name|String
name|ZK_PARENT_ZNODE_KEY
init|=
literal|"ha.zookeeper.parent-znode"
decl_stmt|;
DECL|field|ZK_ACL_KEY
specifier|public
specifier|static
specifier|final
name|String
name|ZK_ACL_KEY
init|=
literal|"ha.zookeeper.acl"
decl_stmt|;
DECL|field|ZK_ACL_DEFAULT
specifier|private
specifier|static
specifier|final
name|String
name|ZK_ACL_DEFAULT
init|=
literal|"world:anyone:rwcda"
decl_stmt|;
DECL|field|ZK_AUTH_KEY
specifier|public
specifier|static
specifier|final
name|String
name|ZK_AUTH_KEY
init|=
literal|"ha.zookeeper.auth"
decl_stmt|;
DECL|field|ZK_PARENT_ZNODE_DEFAULT
specifier|static
specifier|final
name|String
name|ZK_PARENT_ZNODE_DEFAULT
init|=
literal|"/hadoop-ha"
decl_stmt|;
comment|/** Unable to format the parent znode in ZK */
DECL|field|ERR_CODE_FORMAT_DENIED
specifier|static
specifier|final
name|int
name|ERR_CODE_FORMAT_DENIED
init|=
literal|2
decl_stmt|;
comment|/** The parent znode doesn't exist in ZK */
DECL|field|ERR_CODE_NO_PARENT_ZNODE
specifier|static
specifier|final
name|int
name|ERR_CODE_NO_PARENT_ZNODE
init|=
literal|3
decl_stmt|;
comment|/** Fencing is not properly configured */
DECL|field|ERR_CODE_NO_FENCER
specifier|static
specifier|final
name|int
name|ERR_CODE_NO_FENCER
init|=
literal|4
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|healthMonitor
specifier|private
name|HealthMonitor
name|healthMonitor
decl_stmt|;
DECL|field|elector
specifier|private
name|ActiveStandbyElector
name|elector
decl_stmt|;
DECL|field|localTarget
specifier|private
name|HAServiceTarget
name|localTarget
decl_stmt|;
DECL|field|parentZnode
specifier|private
name|String
name|parentZnode
decl_stmt|;
DECL|field|lastHealthState
specifier|private
name|State
name|lastHealthState
init|=
name|State
operator|.
name|INITIALIZING
decl_stmt|;
comment|/** Set if a fatal error occurs */
DECL|field|fatalError
specifier|private
name|String
name|fatalError
init|=
literal|null
decl_stmt|;
annotation|@
name|Override
DECL|method|setConf (Configuration conf)
specifier|public
name|void
name|setConf
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|localTarget
operator|=
name|getLocalTarget
argument_list|()
expr_stmt|;
block|}
DECL|method|targetToData (HAServiceTarget target)
specifier|protected
specifier|abstract
name|byte
index|[]
name|targetToData
parameter_list|(
name|HAServiceTarget
name|target
parameter_list|)
function_decl|;
DECL|method|getLocalTarget ()
specifier|protected
specifier|abstract
name|HAServiceTarget
name|getLocalTarget
parameter_list|()
function_decl|;
DECL|method|dataToTarget (byte[] data)
specifier|protected
specifier|abstract
name|HAServiceTarget
name|dataToTarget
parameter_list|(
name|byte
index|[]
name|data
parameter_list|)
function_decl|;
DECL|method|loginAsFCUser ()
specifier|protected
specifier|abstract
name|void
name|loginAsFCUser
parameter_list|()
throws|throws
name|IOException
function_decl|;
annotation|@
name|Override
DECL|method|getConf ()
specifier|public
name|Configuration
name|getConf
parameter_list|()
block|{
return|return
name|conf
return|;
block|}
annotation|@
name|Override
DECL|method|run (final String[] args)
specifier|public
name|int
name|run
parameter_list|(
specifier|final
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|loginAsFCUser
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|SecurityUtil
operator|.
name|doAsLoginUserOrFatal
argument_list|(
operator|new
name|PrivilegedAction
argument_list|<
name|Integer
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Integer
name|run
parameter_list|()
block|{
try|try
block|{
return|return
name|doRun
argument_list|(
name|args
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|t
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|t
argument_list|)
throw|;
block|}
block|}
block|}
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|rte
parameter_list|)
block|{
throw|throw
operator|(
name|Exception
operator|)
name|rte
operator|.
name|getCause
argument_list|()
throw|;
block|}
block|}
DECL|method|doRun (String[] args)
specifier|private
name|int
name|doRun
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|HadoopIllegalArgumentException
throws|,
name|IOException
throws|,
name|InterruptedException
block|{
name|initZK
argument_list|()
expr_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|>
literal|0
condition|)
block|{
if|if
condition|(
literal|"-formatZK"
operator|.
name|equals
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|)
condition|)
block|{
name|boolean
name|force
init|=
literal|false
decl_stmt|;
name|boolean
name|interactive
init|=
literal|true
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|args
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
literal|"-force"
operator|.
name|equals
argument_list|(
name|args
index|[
name|i
index|]
argument_list|)
condition|)
block|{
name|force
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"-nonInteractive"
operator|.
name|equals
argument_list|(
name|args
index|[
name|i
index|]
argument_list|)
condition|)
block|{
name|interactive
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
name|badArg
argument_list|(
name|args
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|formatZK
argument_list|(
name|force
argument_list|,
name|interactive
argument_list|)
return|;
block|}
else|else
block|{
name|badArg
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|elector
operator|.
name|parentZNodeExists
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|fatal
argument_list|(
literal|"Unable to start failover controller. "
operator|+
literal|"Parent znode does not exist.\n"
operator|+
literal|"Run with -formatZK flag to initialize ZooKeeper."
argument_list|)
expr_stmt|;
return|return
name|ERR_CODE_NO_PARENT_ZNODE
return|;
block|}
try|try
block|{
name|localTarget
operator|.
name|checkFencingConfigured
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|BadFencingConfigurationException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|fatal
argument_list|(
literal|"Fencing is not configured for "
operator|+
name|localTarget
operator|+
literal|".\n"
operator|+
literal|"You must configure a fencing method before using automatic "
operator|+
literal|"failover."
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
name|ERR_CODE_NO_FENCER
return|;
block|}
name|initHM
argument_list|()
expr_stmt|;
try|try
block|{
name|mainLoop
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|healthMonitor
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|healthMonitor
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
return|return
literal|0
return|;
block|}
DECL|method|badArg (String arg)
specifier|private
name|void
name|badArg
parameter_list|(
name|String
name|arg
parameter_list|)
block|{
name|printUsage
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"Bad argument: "
operator|+
name|arg
argument_list|)
throw|;
block|}
DECL|method|printUsage ()
specifier|private
name|void
name|printUsage
parameter_list|()
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Usage: "
operator|+
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|" [-formatZK [-force | -nonInteractive]]"
argument_list|)
expr_stmt|;
block|}
DECL|method|formatZK (boolean force, boolean interactive)
specifier|private
name|int
name|formatZK
parameter_list|(
name|boolean
name|force
parameter_list|,
name|boolean
name|interactive
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
if|if
condition|(
name|elector
operator|.
name|parentZNodeExists
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|force
operator|&&
operator|(
operator|!
name|interactive
operator|||
operator|!
name|confirmFormat
argument_list|()
operator|)
condition|)
block|{
return|return
name|ERR_CODE_FORMAT_DENIED
return|;
block|}
try|try
block|{
name|elector
operator|.
name|clearParentZNode
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to clear zk parent znode"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
block|}
name|elector
operator|.
name|ensureParentZNode
argument_list|()
expr_stmt|;
return|return
literal|0
return|;
block|}
DECL|method|confirmFormat ()
specifier|private
name|boolean
name|confirmFormat
parameter_list|()
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"===============================================\n"
operator|+
literal|"The configured parent znode "
operator|+
name|parentZnode
operator|+
literal|" already exists.\n"
operator|+
literal|"Are you sure you want to clear all failover information from\n"
operator|+
literal|"ZooKeeper?\n"
operator|+
literal|"WARNING: Before proceeding, ensure that all HDFS services and\n"
operator|+
literal|"failover controllers are stopped!\n"
operator|+
literal|"==============================================="
argument_list|)
expr_stmt|;
try|try
block|{
return|return
name|ToolRunner
operator|.
name|confirmPrompt
argument_list|(
literal|"Proceed formatting "
operator|+
name|parentZnode
operator|+
literal|"?"
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Failed to confirm"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
comment|// ------------------------------------------
comment|// Begin actual guts of failover controller
comment|// ------------------------------------------
DECL|method|initHM ()
specifier|private
name|void
name|initHM
parameter_list|()
block|{
name|healthMonitor
operator|=
operator|new
name|HealthMonitor
argument_list|(
name|conf
argument_list|,
name|localTarget
argument_list|)
expr_stmt|;
name|healthMonitor
operator|.
name|addCallback
argument_list|(
operator|new
name|HealthCallbacks
argument_list|()
argument_list|)
expr_stmt|;
name|healthMonitor
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
DECL|method|initZK ()
specifier|private
name|void
name|initZK
parameter_list|()
throws|throws
name|HadoopIllegalArgumentException
throws|,
name|IOException
block|{
name|String
name|zkQuorum
init|=
name|conf
operator|.
name|get
argument_list|(
name|ZK_QUORUM_KEY
argument_list|)
decl_stmt|;
name|int
name|zkTimeout
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|ZK_SESSION_TIMEOUT_KEY
argument_list|,
name|ZK_SESSION_TIMEOUT_DEFAULT
argument_list|)
decl_stmt|;
name|parentZnode
operator|=
name|conf
operator|.
name|get
argument_list|(
name|ZK_PARENT_ZNODE_KEY
argument_list|,
name|ZK_PARENT_ZNODE_DEFAULT
argument_list|)
expr_stmt|;
comment|// Parse ACLs from configuration.
name|String
name|zkAclConf
init|=
name|conf
operator|.
name|get
argument_list|(
name|ZK_ACL_KEY
argument_list|,
name|ZK_ACL_DEFAULT
argument_list|)
decl_stmt|;
name|zkAclConf
operator|=
name|HAZKUtil
operator|.
name|resolveConfIndirection
argument_list|(
name|zkAclConf
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|ACL
argument_list|>
name|zkAcls
init|=
name|HAZKUtil
operator|.
name|parseACLs
argument_list|(
name|zkAclConf
argument_list|)
decl_stmt|;
if|if
condition|(
name|zkAcls
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|zkAcls
operator|=
name|Ids
operator|.
name|CREATOR_ALL_ACL
expr_stmt|;
block|}
comment|// Parse authentication from configuration.
name|String
name|zkAuthConf
init|=
name|conf
operator|.
name|get
argument_list|(
name|ZK_AUTH_KEY
argument_list|)
decl_stmt|;
name|zkAuthConf
operator|=
name|HAZKUtil
operator|.
name|resolveConfIndirection
argument_list|(
name|zkAuthConf
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|ZKAuthInfo
argument_list|>
name|zkAuths
decl_stmt|;
if|if
condition|(
name|zkAuthConf
operator|!=
literal|null
condition|)
block|{
name|zkAuths
operator|=
name|HAZKUtil
operator|.
name|parseAuth
argument_list|(
name|zkAuthConf
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|zkAuths
operator|=
name|Collections
operator|.
name|emptyList
argument_list|()
expr_stmt|;
block|}
comment|// Sanity check configuration.
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|zkQuorum
operator|!=
literal|null
argument_list|,
literal|"Missing required configuration '%s' for ZooKeeper quorum"
argument_list|,
name|ZK_QUORUM_KEY
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|zkTimeout
operator|>
literal|0
argument_list|,
literal|"Invalid ZK session timeout %s"
argument_list|,
name|zkTimeout
argument_list|)
expr_stmt|;
name|elector
operator|=
operator|new
name|ActiveStandbyElector
argument_list|(
name|zkQuorum
argument_list|,
name|zkTimeout
argument_list|,
name|parentZnode
argument_list|,
name|zkAcls
argument_list|,
name|zkAuths
argument_list|,
operator|new
name|ElectorCallbacks
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|mainLoop ()
specifier|private
specifier|synchronized
name|void
name|mainLoop
parameter_list|()
throws|throws
name|InterruptedException
block|{
while|while
condition|(
name|fatalError
operator|==
literal|null
condition|)
block|{
name|wait
argument_list|()
expr_stmt|;
block|}
assert|assert
name|fatalError
operator|!=
literal|null
assert|;
comment|// only get here on fatal
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"ZK Failover Controller failed: "
operator|+
name|fatalError
argument_list|)
throw|;
block|}
DECL|method|fatalError (String err)
specifier|private
specifier|synchronized
name|void
name|fatalError
parameter_list|(
name|String
name|err
parameter_list|)
block|{
name|LOG
operator|.
name|fatal
argument_list|(
literal|"Fatal error occurred:"
operator|+
name|err
argument_list|)
expr_stmt|;
name|fatalError
operator|=
name|err
expr_stmt|;
name|notifyAll
argument_list|()
expr_stmt|;
block|}
DECL|method|becomeActive ()
specifier|private
specifier|synchronized
name|void
name|becomeActive
parameter_list|()
throws|throws
name|ServiceFailedException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Trying to make "
operator|+
name|localTarget
operator|+
literal|" active..."
argument_list|)
expr_stmt|;
try|try
block|{
name|HAServiceProtocolHelper
operator|.
name|transitionToActive
argument_list|(
name|localTarget
operator|.
name|getProxy
argument_list|(
name|conf
argument_list|,
name|FailoverController
operator|.
name|getRpcTimeoutToNewActive
argument_list|(
name|conf
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Successfully transitioned "
operator|+
name|localTarget
operator|+
literal|" to active state"
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
name|fatal
argument_list|(
literal|"Couldn't make "
operator|+
name|localTarget
operator|+
literal|" active"
argument_list|,
name|t
argument_list|)
expr_stmt|;
if|if
condition|(
name|t
operator|instanceof
name|ServiceFailedException
condition|)
block|{
throw|throw
operator|(
name|ServiceFailedException
operator|)
name|t
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|ServiceFailedException
argument_list|(
literal|"Couldn't transition to active"
argument_list|,
name|t
argument_list|)
throw|;
block|}
comment|/* * TODO: * we need to make sure that if we get fenced and then quickly restarted, * none of these calls will retry across the restart boundary * perhaps the solution is that, whenever the nn starts, it gets a unique * ID, and when we start becoming active, we record it, and then any future * calls use the same ID */
block|}
block|}
DECL|method|becomeStandby ()
specifier|private
specifier|synchronized
name|void
name|becomeStandby
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"ZK Election indicated that "
operator|+
name|localTarget
operator|+
literal|" should become standby"
argument_list|)
expr_stmt|;
try|try
block|{
name|int
name|timeout
init|=
name|FailoverController
operator|.
name|getGracefulFenceTimeout
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|localTarget
operator|.
name|getProxy
argument_list|(
name|conf
argument_list|,
name|timeout
argument_list|)
operator|.
name|transitionToStandby
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Successfully transitioned "
operator|+
name|localTarget
operator|+
literal|" to standby state"
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
literal|"Couldn't transition "
operator|+
name|localTarget
operator|+
literal|" to standby state"
argument_list|,
name|e
argument_list|)
expr_stmt|;
comment|// TODO handle this. It's a likely case since we probably got fenced
comment|// at the same time.
block|}
block|}
comment|/**    * @return the last health state passed to the FC    * by the HealthMonitor.    */
annotation|@
name|VisibleForTesting
DECL|method|getLastHealthState ()
name|State
name|getLastHealthState
parameter_list|()
block|{
return|return
name|lastHealthState
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getElectorForTests ()
name|ActiveStandbyElector
name|getElectorForTests
parameter_list|()
block|{
return|return
name|elector
return|;
block|}
comment|/**    * Callbacks from elector    */
DECL|class|ElectorCallbacks
class|class
name|ElectorCallbacks
implements|implements
name|ActiveStandbyElectorCallback
block|{
annotation|@
name|Override
DECL|method|becomeActive ()
specifier|public
name|void
name|becomeActive
parameter_list|()
throws|throws
name|ServiceFailedException
block|{
name|ZKFailoverController
operator|.
name|this
operator|.
name|becomeActive
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|becomeStandby ()
specifier|public
name|void
name|becomeStandby
parameter_list|()
block|{
name|ZKFailoverController
operator|.
name|this
operator|.
name|becomeStandby
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|enterNeutralMode ()
specifier|public
name|void
name|enterNeutralMode
parameter_list|()
block|{     }
annotation|@
name|Override
DECL|method|notifyFatalError (String errorMessage)
specifier|public
name|void
name|notifyFatalError
parameter_list|(
name|String
name|errorMessage
parameter_list|)
block|{
name|fatalError
argument_list|(
name|errorMessage
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|fenceOldActive (byte[] data)
specifier|public
name|void
name|fenceOldActive
parameter_list|(
name|byte
index|[]
name|data
parameter_list|)
block|{
name|HAServiceTarget
name|target
init|=
name|dataToTarget
argument_list|(
name|data
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Should fence: "
operator|+
name|target
argument_list|)
expr_stmt|;
name|boolean
name|gracefulWorked
init|=
operator|new
name|FailoverController
argument_list|(
name|conf
argument_list|)
operator|.
name|tryGracefulFence
argument_list|(
name|target
argument_list|)
decl_stmt|;
if|if
condition|(
name|gracefulWorked
condition|)
block|{
comment|// It's possible that it's in standby but just about to go into active,
comment|// no? Is there some race here?
name|LOG
operator|.
name|info
argument_list|(
literal|"Successfully transitioned "
operator|+
name|target
operator|+
literal|" to standby "
operator|+
literal|"state without fencing"
argument_list|)
expr_stmt|;
return|return;
block|}
try|try
block|{
name|target
operator|.
name|checkFencingConfigured
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|BadFencingConfigurationException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Couldn't fence old active "
operator|+
name|target
argument_list|,
name|e
argument_list|)
expr_stmt|;
comment|// TODO: see below todo
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|target
operator|.
name|getFencer
argument_list|()
operator|.
name|fence
argument_list|(
name|target
argument_list|)
condition|)
block|{
comment|// TODO: this will end up in some kind of tight loop,
comment|// won't it? We need some kind of backoff
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unable to fence "
operator|+
name|target
argument_list|)
throw|;
block|}
block|}
block|}
comment|/**    * Callbacks from HealthMonitor    */
DECL|class|HealthCallbacks
class|class
name|HealthCallbacks
implements|implements
name|HealthMonitor
operator|.
name|Callback
block|{
annotation|@
name|Override
DECL|method|enteredState (HealthMonitor.State newState)
specifier|public
name|void
name|enteredState
parameter_list|(
name|HealthMonitor
operator|.
name|State
name|newState
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Local service "
operator|+
name|localTarget
operator|+
literal|" entered state: "
operator|+
name|newState
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|newState
condition|)
block|{
case|case
name|SERVICE_HEALTHY
case|:
name|LOG
operator|.
name|info
argument_list|(
literal|"Joining master election for "
operator|+
name|localTarget
argument_list|)
expr_stmt|;
name|elector
operator|.
name|joinElection
argument_list|(
name|targetToData
argument_list|(
name|localTarget
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|INITIALIZING
case|:
name|LOG
operator|.
name|info
argument_list|(
literal|"Ensuring that "
operator|+
name|localTarget
operator|+
literal|" does not "
operator|+
literal|"participate in active master election"
argument_list|)
expr_stmt|;
name|elector
operator|.
name|quitElection
argument_list|(
literal|false
argument_list|)
expr_stmt|;
break|break;
case|case
name|SERVICE_UNHEALTHY
case|:
case|case
name|SERVICE_NOT_RESPONDING
case|:
name|LOG
operator|.
name|info
argument_list|(
literal|"Quitting master election for "
operator|+
name|localTarget
operator|+
literal|" and marking that fencing is necessary"
argument_list|)
expr_stmt|;
name|elector
operator|.
name|quitElection
argument_list|(
literal|true
argument_list|)
expr_stmt|;
break|break;
case|case
name|HEALTH_MONITOR_FAILED
case|:
name|fatalError
argument_list|(
literal|"Health monitor failed!"
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unhandled state:"
operator|+
name|newState
argument_list|)
throw|;
block|}
name|lastHealthState
operator|=
name|newState
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

