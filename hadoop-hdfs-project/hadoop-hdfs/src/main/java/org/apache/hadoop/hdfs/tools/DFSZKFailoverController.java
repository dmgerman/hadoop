begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.tools
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|tools
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_KEYTAB_FILE_KEY
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
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_KERBEROS_PRINCIPAL_KEY
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
name|util
operator|.
name|ExitUtil
operator|.
name|terminate
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
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
name|net
operator|.
name|HttpURLConnection
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
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|HAServiceTarget
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
name|ZKFailoverController
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
name|hdfs
operator|.
name|DFSConfigKeys
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
name|hdfs
operator|.
name|DFSUtil
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
name|hdfs
operator|.
name|DFSUtilClient
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
name|hdfs
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
name|hdfs
operator|.
name|HDFSPolicyProvider
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
name|hdfs
operator|.
name|HdfsConfiguration
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
name|hdfs
operator|.
name|server
operator|.
name|namenode
operator|.
name|NameNode
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
name|hdfs
operator|.
name|server
operator|.
name|namenode
operator|.
name|ha
operator|.
name|proto
operator|.
name|HAZKInfoProtos
operator|.
name|ActiveNodeInfo
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
name|io
operator|.
name|IOUtils
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
name|Server
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
name|AccessControlException
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
name|authorize
operator|.
name|AccessControlList
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
name|authorize
operator|.
name|PolicyProvider
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
name|GenericOptionsParser
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
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|InvalidProtocolBufferException
import|;
end_import

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|DFSZKFailoverController
specifier|public
class|class
name|DFSZKFailoverController
extends|extends
name|ZKFailoverController
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
name|DFSZKFailoverController
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|adminAcl
specifier|private
specifier|final
name|AccessControlList
name|adminAcl
decl_stmt|;
comment|/* the same as superclass's localTarget, but with the more specfic NN type */
DECL|field|localNNTarget
specifier|private
specifier|final
name|NNHAServiceTarget
name|localNNTarget
decl_stmt|;
comment|// This is used only for unit tests
DECL|field|isThreadDumpCaptured
specifier|private
name|boolean
name|isThreadDumpCaptured
init|=
literal|false
decl_stmt|;
annotation|@
name|Override
DECL|method|dataToTarget (byte[] data)
specifier|protected
name|HAServiceTarget
name|dataToTarget
parameter_list|(
name|byte
index|[]
name|data
parameter_list|)
block|{
name|ActiveNodeInfo
name|proto
decl_stmt|;
try|try
block|{
name|proto
operator|=
name|ActiveNodeInfo
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
throw|throw
operator|new
name|RuntimeException
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
throw|;
block|}
name|NNHAServiceTarget
name|ret
init|=
operator|new
name|NNHAServiceTarget
argument_list|(
name|conf
argument_list|,
name|proto
operator|.
name|getNameserviceId
argument_list|()
argument_list|,
name|proto
operator|.
name|getNamenodeId
argument_list|()
argument_list|)
decl_stmt|;
name|InetSocketAddress
name|addressFromProtobuf
init|=
operator|new
name|InetSocketAddress
argument_list|(
name|proto
operator|.
name|getHostname
argument_list|()
argument_list|,
name|proto
operator|.
name|getPort
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|addressFromProtobuf
operator|.
name|equals
argument_list|(
name|ret
operator|.
name|getAddress
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Mismatched address stored in ZK for "
operator|+
name|ret
operator|+
literal|": Stored protobuf was "
operator|+
name|proto
operator|+
literal|", address from our own "
operator|+
literal|"configuration for this NameNode was "
operator|+
name|ret
operator|.
name|getAddress
argument_list|()
argument_list|)
throw|;
block|}
name|ret
operator|.
name|setZkfcPort
argument_list|(
name|proto
operator|.
name|getZkfcPort
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|ret
return|;
block|}
annotation|@
name|Override
DECL|method|targetToData (HAServiceTarget target)
specifier|protected
name|byte
index|[]
name|targetToData
parameter_list|(
name|HAServiceTarget
name|target
parameter_list|)
block|{
name|InetSocketAddress
name|addr
init|=
name|target
operator|.
name|getAddress
argument_list|()
decl_stmt|;
return|return
name|ActiveNodeInfo
operator|.
name|newBuilder
argument_list|()
operator|.
name|setHostname
argument_list|(
name|addr
operator|.
name|getHostName
argument_list|()
argument_list|)
operator|.
name|setPort
argument_list|(
name|addr
operator|.
name|getPort
argument_list|()
argument_list|)
operator|.
name|setZkfcPort
argument_list|(
name|target
operator|.
name|getZKFCAddress
argument_list|()
operator|.
name|getPort
argument_list|()
argument_list|)
operator|.
name|setNameserviceId
argument_list|(
name|localNNTarget
operator|.
name|getNameServiceId
argument_list|()
argument_list|)
operator|.
name|setNamenodeId
argument_list|(
name|localNNTarget
operator|.
name|getNameNodeId
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
operator|.
name|toByteArray
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getRpcAddressToBindTo ()
specifier|protected
name|InetSocketAddress
name|getRpcAddressToBindTo
parameter_list|()
block|{
name|int
name|zkfcPort
init|=
name|getZkfcPort
argument_list|(
name|conf
argument_list|)
decl_stmt|;
return|return
operator|new
name|InetSocketAddress
argument_list|(
name|localTarget
operator|.
name|getAddress
argument_list|()
operator|.
name|getAddress
argument_list|()
argument_list|,
name|zkfcPort
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getPolicyProvider ()
specifier|protected
name|PolicyProvider
name|getPolicyProvider
parameter_list|()
block|{
return|return
operator|new
name|HDFSPolicyProvider
argument_list|()
return|;
block|}
DECL|method|getZkfcPort (Configuration conf)
specifier|static
name|int
name|getZkfcPort
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
return|return
name|conf
operator|.
name|getInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HA_ZKFC_PORT_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_HA_ZKFC_PORT_DEFAULT
argument_list|)
return|;
block|}
DECL|method|create (Configuration conf)
specifier|public
specifier|static
name|DFSZKFailoverController
name|create
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|Configuration
name|localNNConf
init|=
name|DFSHAAdmin
operator|.
name|addSecurityConfiguration
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|String
name|nsId
init|=
name|DFSUtil
operator|.
name|getNamenodeNameServiceId
argument_list|(
name|conf
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|HAUtil
operator|.
name|isHAEnabled
argument_list|(
name|localNNConf
argument_list|,
name|nsId
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"HA is not enabled for this namenode."
argument_list|)
throw|;
block|}
name|String
name|nnId
init|=
name|HAUtil
operator|.
name|getNameNodeId
argument_list|(
name|localNNConf
argument_list|,
name|nsId
argument_list|)
decl_stmt|;
if|if
condition|(
name|nnId
operator|==
literal|null
condition|)
block|{
name|String
name|msg
init|=
literal|"Could not get the namenode ID of this node. "
operator|+
literal|"You may run zkfc on the node other than namenode."
decl_stmt|;
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
name|NameNode
operator|.
name|initializeGenericKeys
argument_list|(
name|localNNConf
argument_list|,
name|nsId
argument_list|,
name|nnId
argument_list|)
expr_stmt|;
name|DFSUtil
operator|.
name|setGenericConf
argument_list|(
name|localNNConf
argument_list|,
name|nsId
argument_list|,
name|nnId
argument_list|,
name|ZKFC_CONF_KEYS
argument_list|)
expr_stmt|;
name|NNHAServiceTarget
name|localTarget
init|=
operator|new
name|NNHAServiceTarget
argument_list|(
name|localNNConf
argument_list|,
name|nsId
argument_list|,
name|nnId
argument_list|)
decl_stmt|;
return|return
operator|new
name|DFSZKFailoverController
argument_list|(
name|localNNConf
argument_list|,
name|localTarget
argument_list|)
return|;
block|}
DECL|method|DFSZKFailoverController (Configuration conf, NNHAServiceTarget localTarget)
specifier|private
name|DFSZKFailoverController
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|NNHAServiceTarget
name|localTarget
parameter_list|)
block|{
name|super
argument_list|(
name|conf
argument_list|,
name|localTarget
argument_list|)
expr_stmt|;
name|this
operator|.
name|localNNTarget
operator|=
name|localTarget
expr_stmt|;
comment|// Setup ACLs
name|adminAcl
operator|=
operator|new
name|AccessControlList
argument_list|(
name|conf
operator|.
name|get
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_ADMIN
argument_list|,
literal|" "
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Failover controller configured for NameNode "
operator|+
name|localTarget
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|initRPC ()
specifier|protected
name|void
name|initRPC
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|initRPC
argument_list|()
expr_stmt|;
name|localNNTarget
operator|.
name|setZkfcPort
argument_list|(
name|rpcServer
operator|.
name|getAddress
argument_list|()
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|loginAsFCUser ()
specifier|public
name|void
name|loginAsFCUser
parameter_list|()
throws|throws
name|IOException
block|{
name|InetSocketAddress
name|socAddr
init|=
name|DFSUtilClient
operator|.
name|getNNAddress
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|SecurityUtil
operator|.
name|login
argument_list|(
name|conf
argument_list|,
name|DFS_NAMENODE_KEYTAB_FILE_KEY
argument_list|,
name|DFS_NAMENODE_KERBEROS_PRINCIPAL_KEY
argument_list|,
name|socAddr
operator|.
name|getHostName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getScopeInsideParentNode ()
specifier|protected
name|String
name|getScopeInsideParentNode
parameter_list|()
block|{
return|return
name|localNNTarget
operator|.
name|getNameServiceId
argument_list|()
return|;
block|}
DECL|method|main (String args[])
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
name|args
index|[]
parameter_list|)
throws|throws
name|Exception
block|{
name|StringUtils
operator|.
name|startupShutdownMessage
argument_list|(
name|DFSZKFailoverController
operator|.
name|class
argument_list|,
name|args
argument_list|,
name|LOG
argument_list|)
expr_stmt|;
if|if
condition|(
name|DFSUtil
operator|.
name|parseHelpArgument
argument_list|(
name|args
argument_list|,
name|ZKFailoverController
operator|.
name|USAGE
argument_list|,
name|System
operator|.
name|out
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|System
operator|.
name|exit
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
name|GenericOptionsParser
name|parser
init|=
operator|new
name|GenericOptionsParser
argument_list|(
operator|new
name|HdfsConfiguration
argument_list|()
argument_list|,
name|args
argument_list|)
decl_stmt|;
try|try
block|{
name|DFSZKFailoverController
name|zkfc
init|=
name|DFSZKFailoverController
operator|.
name|create
argument_list|(
name|parser
operator|.
name|getConfiguration
argument_list|()
argument_list|)
decl_stmt|;
name|System
operator|.
name|exit
argument_list|(
name|zkfc
operator|.
name|run
argument_list|(
name|parser
operator|.
name|getRemainingArgs
argument_list|()
argument_list|)
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
name|error
argument_list|(
literal|"DFSZKFailOverController exiting due to earlier exception "
operator|+
name|t
argument_list|)
expr_stmt|;
name|terminate
argument_list|(
literal|1
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|checkRpcAdminAccess ()
specifier|protected
name|void
name|checkRpcAdminAccess
parameter_list|()
throws|throws
name|IOException
throws|,
name|AccessControlException
block|{
name|UserGroupInformation
name|ugi
init|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
decl_stmt|;
name|UserGroupInformation
name|zkfcUgi
init|=
name|UserGroupInformation
operator|.
name|getLoginUser
argument_list|()
decl_stmt|;
if|if
condition|(
name|adminAcl
operator|.
name|isUserAllowed
argument_list|(
name|ugi
argument_list|)
operator|||
name|ugi
operator|.
name|getShortUserName
argument_list|()
operator|.
name|equals
argument_list|(
name|zkfcUgi
operator|.
name|getShortUserName
argument_list|()
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Allowed RPC access from "
operator|+
name|ugi
operator|+
literal|" at "
operator|+
name|Server
operator|.
name|getRemoteAddress
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
name|String
name|msg
init|=
literal|"Disallowed RPC access from "
operator|+
name|ugi
operator|+
literal|" at "
operator|+
name|Server
operator|.
name|getRemoteAddress
argument_list|()
operator|+
literal|". Not listed in "
operator|+
name|DFSConfigKeys
operator|.
name|DFS_ADMIN
decl_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
name|msg
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|AccessControlException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
comment|/**    * capture local NN's thread dump and write it to ZKFC's log.    */
DECL|method|getLocalNNThreadDump ()
specifier|private
name|void
name|getLocalNNThreadDump
parameter_list|()
block|{
name|isThreadDumpCaptured
operator|=
literal|false
expr_stmt|;
comment|// We use the same timeout value for both connection establishment
comment|// timeout and read timeout.
name|int
name|httpTimeOut
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HA_ZKFC_NN_HTTP_TIMEOUT_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_HA_ZKFC_NN_HTTP_TIMEOUT_KEY_DEFAULT
argument_list|)
decl_stmt|;
if|if
condition|(
name|httpTimeOut
operator|==
literal|0
condition|)
block|{
comment|// If timeout value is set to zero, the feature is turned off.
return|return;
block|}
try|try
block|{
name|String
name|stacksUrl
init|=
name|DFSUtil
operator|.
name|getInfoServer
argument_list|(
name|localNNTarget
operator|.
name|getAddress
argument_list|()
argument_list|,
name|conf
argument_list|,
name|DFSUtil
operator|.
name|getHttpClientScheme
argument_list|(
name|conf
argument_list|)
argument_list|)
operator|+
literal|"/stacks"
decl_stmt|;
name|URL
name|url
init|=
operator|new
name|URL
argument_list|(
name|stacksUrl
argument_list|)
decl_stmt|;
name|HttpURLConnection
name|conn
init|=
operator|(
name|HttpURLConnection
operator|)
name|url
operator|.
name|openConnection
argument_list|()
decl_stmt|;
name|conn
operator|.
name|setReadTimeout
argument_list|(
name|httpTimeOut
argument_list|)
expr_stmt|;
name|conn
operator|.
name|setConnectTimeout
argument_list|(
name|httpTimeOut
argument_list|)
expr_stmt|;
name|conn
operator|.
name|connect
argument_list|()
expr_stmt|;
name|ByteArrayOutputStream
name|out
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|IOUtils
operator|.
name|copyBytes
argument_list|(
name|conn
operator|.
name|getInputStream
argument_list|()
argument_list|,
name|out
argument_list|,
literal|4096
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|StringBuilder
name|localNNThreadDumpContent
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"-- Local NN thread dump -- \n"
argument_list|)
decl_stmt|;
name|localNNThreadDumpContent
operator|.
name|append
argument_list|(
name|out
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n -- Local NN thread dump -- "
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"{}"
argument_list|,
name|localNNThreadDumpContent
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|isThreadDumpCaptured
operator|=
literal|true
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
name|warn
argument_list|(
literal|"Can't get local NN thread dump due to "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|setLastHealthState (HealthMonitor.State newState)
specifier|protected
specifier|synchronized
name|void
name|setLastHealthState
parameter_list|(
name|HealthMonitor
operator|.
name|State
name|newState
parameter_list|)
block|{
name|super
operator|.
name|setLastHealthState
argument_list|(
name|newState
argument_list|)
expr_stmt|;
comment|// Capture local NN thread dump when the target NN health state changes.
if|if
condition|(
name|getLastHealthState
argument_list|()
operator|==
name|HealthMonitor
operator|.
name|State
operator|.
name|SERVICE_NOT_RESPONDING
operator|||
name|getLastHealthState
argument_list|()
operator|==
name|HealthMonitor
operator|.
name|State
operator|.
name|SERVICE_UNHEALTHY
condition|)
block|{
name|getLocalNNThreadDump
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|VisibleForTesting
DECL|method|isThreadDumpCaptured ()
name|boolean
name|isThreadDumpCaptured
parameter_list|()
block|{
return|return
name|isThreadDumpCaptured
return|;
block|}
annotation|@
name|Override
DECL|method|getAllOtherNodes ()
specifier|public
name|List
argument_list|<
name|HAServiceTarget
argument_list|>
name|getAllOtherNodes
parameter_list|()
block|{
name|String
name|nsId
init|=
name|DFSUtil
operator|.
name|getNamenodeNameServiceId
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|otherNn
init|=
name|HAUtil
operator|.
name|getNameNodeIdOfOtherNodes
argument_list|(
name|conf
argument_list|,
name|nsId
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|HAServiceTarget
argument_list|>
name|targets
init|=
operator|new
name|ArrayList
argument_list|<
name|HAServiceTarget
argument_list|>
argument_list|(
name|otherNn
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|nnId
range|:
name|otherNn
control|)
block|{
name|targets
operator|.
name|add
argument_list|(
operator|new
name|NNHAServiceTarget
argument_list|(
name|conf
argument_list|,
name|nsId
argument_list|,
name|nnId
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|targets
return|;
block|}
block|}
end_class

end_unit

