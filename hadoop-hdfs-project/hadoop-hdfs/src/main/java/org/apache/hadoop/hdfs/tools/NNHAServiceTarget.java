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
name|Map
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
name|BadFencingConfigurationException
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
name|NodeFencer
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
name|net
operator|.
name|NetUtils
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
comment|/**  * One of the NN NameNodes acting as the target of an administrative command  * (e.g. failover).  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|NNHAServiceTarget
specifier|public
class|class
name|NNHAServiceTarget
extends|extends
name|HAServiceTarget
block|{
comment|// Keys added to the fencing script environment
DECL|field|NAMESERVICE_ID_KEY
specifier|private
specifier|static
specifier|final
name|String
name|NAMESERVICE_ID_KEY
init|=
literal|"nameserviceid"
decl_stmt|;
DECL|field|NAMENODE_ID_KEY
specifier|private
specifier|static
specifier|final
name|String
name|NAMENODE_ID_KEY
init|=
literal|"namenodeid"
decl_stmt|;
DECL|field|addr
specifier|private
specifier|final
name|InetSocketAddress
name|addr
decl_stmt|;
DECL|field|fencer
specifier|private
name|NodeFencer
name|fencer
decl_stmt|;
DECL|field|fenceConfigError
specifier|private
name|BadFencingConfigurationException
name|fenceConfigError
decl_stmt|;
DECL|field|nnId
specifier|private
specifier|final
name|String
name|nnId
decl_stmt|;
DECL|field|nsId
specifier|private
specifier|final
name|String
name|nsId
decl_stmt|;
DECL|field|autoFailoverEnabled
specifier|private
specifier|final
name|boolean
name|autoFailoverEnabled
decl_stmt|;
DECL|method|NNHAServiceTarget (Configuration conf, String nsId, String nnId)
specifier|public
name|NNHAServiceTarget
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|nsId
parameter_list|,
name|String
name|nnId
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|nnId
argument_list|)
expr_stmt|;
if|if
condition|(
name|nsId
operator|==
literal|null
condition|)
block|{
name|nsId
operator|=
name|DFSUtil
operator|.
name|getOnlyNameServiceIdOrNull
argument_list|(
name|conf
argument_list|)
expr_stmt|;
if|if
condition|(
name|nsId
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unable to determine the nameservice id."
argument_list|)
throw|;
block|}
block|}
assert|assert
name|nsId
operator|!=
literal|null
assert|;
comment|// Make a copy of the conf, and override configs based on the
comment|// target node -- not the node we happen to be running on.
name|HdfsConfiguration
name|targetConf
init|=
operator|new
name|HdfsConfiguration
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|NameNode
operator|.
name|initializeGenericKeys
argument_list|(
name|targetConf
argument_list|,
name|nsId
argument_list|,
name|nnId
argument_list|)
expr_stmt|;
name|String
name|serviceAddr
init|=
name|DFSUtil
operator|.
name|getNamenodeServiceAddr
argument_list|(
name|targetConf
argument_list|,
name|nsId
argument_list|,
name|nnId
argument_list|)
decl_stmt|;
if|if
condition|(
name|serviceAddr
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unable to determine service address for namenode '"
operator|+
name|nnId
operator|+
literal|"'"
argument_list|)
throw|;
block|}
name|this
operator|.
name|addr
operator|=
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|serviceAddr
argument_list|,
name|NameNode
operator|.
name|DEFAULT_PORT
argument_list|)
expr_stmt|;
try|try
block|{
name|this
operator|.
name|fencer
operator|=
name|NodeFencer
operator|.
name|create
argument_list|(
name|targetConf
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_HA_FENCE_METHODS_KEY
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|BadFencingConfigurationException
name|e
parameter_list|)
block|{
name|this
operator|.
name|fenceConfigError
operator|=
name|e
expr_stmt|;
block|}
name|this
operator|.
name|nnId
operator|=
name|nnId
expr_stmt|;
name|this
operator|.
name|nsId
operator|=
name|nsId
expr_stmt|;
name|this
operator|.
name|autoFailoverEnabled
operator|=
name|targetConf
operator|.
name|getBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HA_AUTO_FAILOVER_ENABLED_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_HA_AUTO_FAILOVER_ENABLED_DEFAULT
argument_list|)
expr_stmt|;
block|}
comment|/**    * @return the NN's IPC address.    */
annotation|@
name|Override
DECL|method|getAddress ()
specifier|public
name|InetSocketAddress
name|getAddress
parameter_list|()
block|{
return|return
name|addr
return|;
block|}
annotation|@
name|Override
DECL|method|checkFencingConfigured ()
specifier|public
name|void
name|checkFencingConfigured
parameter_list|()
throws|throws
name|BadFencingConfigurationException
block|{
if|if
condition|(
name|fenceConfigError
operator|!=
literal|null
condition|)
block|{
throw|throw
name|fenceConfigError
throw|;
block|}
if|if
condition|(
name|fencer
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|BadFencingConfigurationException
argument_list|(
literal|"No fencer configured for "
operator|+
name|this
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getFencer ()
specifier|public
name|NodeFencer
name|getFencer
parameter_list|()
block|{
return|return
name|fencer
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"NameNode at "
operator|+
name|addr
return|;
block|}
DECL|method|getNameServiceId ()
specifier|public
name|String
name|getNameServiceId
parameter_list|()
block|{
return|return
name|this
operator|.
name|nsId
return|;
block|}
DECL|method|getNameNodeId ()
specifier|public
name|String
name|getNameNodeId
parameter_list|()
block|{
return|return
name|this
operator|.
name|nnId
return|;
block|}
annotation|@
name|Override
DECL|method|addFencingParameters (Map<String, String> ret)
specifier|protected
name|void
name|addFencingParameters
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|ret
parameter_list|)
block|{
name|super
operator|.
name|addFencingParameters
argument_list|(
name|ret
argument_list|)
expr_stmt|;
name|ret
operator|.
name|put
argument_list|(
name|NAMESERVICE_ID_KEY
argument_list|,
name|getNameServiceId
argument_list|()
argument_list|)
expr_stmt|;
name|ret
operator|.
name|put
argument_list|(
name|NAMENODE_ID_KEY
argument_list|,
name|getNameNodeId
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|isAutoFailoverEnabled ()
specifier|public
name|boolean
name|isAutoFailoverEnabled
parameter_list|()
block|{
return|return
name|autoFailoverEnabled
return|;
block|}
block|}
end_class

end_unit

