begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.pipeline
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
name|pipeline
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
name|protocol
operator|.
name|DatanodeDetails
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
name|ScmConfigKeys
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
name|client
operator|.
name|HddsClientUtils
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
name|ratis
operator|.
name|RatisHelper
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|client
operator|.
name|RaftClient
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|grpc
operator|.
name|GrpcTlsConfig
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|protocol
operator|.
name|RaftGroup
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|protocol
operator|.
name|RaftGroupId
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|protocol
operator|.
name|RaftPeer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|retry
operator|.
name|RetryPolicy
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|rpc
operator|.
name|SupportedRpcType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|util
operator|.
name|TimeDuration
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
comment|/**  * Utility class for Ratis pipelines. Contains methods to create and destroy  * ratis pipelines.  */
end_comment

begin_class
DECL|class|RatisPipelineUtils
specifier|public
specifier|final
class|class
name|RatisPipelineUtils
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
name|RatisPipelineUtils
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|RatisPipelineUtils ()
specifier|private
name|RatisPipelineUtils
parameter_list|()
block|{   }
comment|/**    * Removes pipeline from SCM. Sends ratis command to destroy pipeline on all    * the datanodes.    *    * @param pipeline        - Pipeline to be destroyed    * @param ozoneConf       - Ozone configuration    * @param grpcTlsConfig    * @throws IOException    */
DECL|method|destroyPipeline (Pipeline pipeline, Configuration ozoneConf, GrpcTlsConfig grpcTlsConfig)
specifier|static
name|void
name|destroyPipeline
parameter_list|(
name|Pipeline
name|pipeline
parameter_list|,
name|Configuration
name|ozoneConf
parameter_list|,
name|GrpcTlsConfig
name|grpcTlsConfig
parameter_list|)
block|{
specifier|final
name|RaftGroup
name|group
init|=
name|RatisHelper
operator|.
name|newRaftGroup
argument_list|(
name|pipeline
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"destroying pipeline:{} with {}"
argument_list|,
name|pipeline
operator|.
name|getId
argument_list|()
argument_list|,
name|group
argument_list|)
expr_stmt|;
for|for
control|(
name|DatanodeDetails
name|dn
range|:
name|pipeline
operator|.
name|getNodes
argument_list|()
control|)
block|{
try|try
block|{
name|destroyPipeline
argument_list|(
name|dn
argument_list|,
name|pipeline
operator|.
name|getId
argument_list|()
argument_list|,
name|ozoneConf
argument_list|,
name|grpcTlsConfig
argument_list|)
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
literal|"Pipeline destroy failed for pipeline={} dn={}"
argument_list|,
name|pipeline
operator|.
name|getId
argument_list|()
argument_list|,
name|dn
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Sends ratis command to destroy pipeline on the given datanode.    *    * @param dn         - Datanode on which pipeline needs to be destroyed    * @param pipelineID - ID of pipeline to be destroyed    * @param ozoneConf  - Ozone configuration    * @param grpcTlsConfig - grpc tls configuration    * @throws IOException    */
DECL|method|destroyPipeline (DatanodeDetails dn, PipelineID pipelineID, Configuration ozoneConf, GrpcTlsConfig grpcTlsConfig)
specifier|static
name|void
name|destroyPipeline
parameter_list|(
name|DatanodeDetails
name|dn
parameter_list|,
name|PipelineID
name|pipelineID
parameter_list|,
name|Configuration
name|ozoneConf
parameter_list|,
name|GrpcTlsConfig
name|grpcTlsConfig
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|rpcType
init|=
name|ozoneConf
operator|.
name|get
argument_list|(
name|ScmConfigKeys
operator|.
name|DFS_CONTAINER_RATIS_RPC_TYPE_KEY
argument_list|,
name|ScmConfigKeys
operator|.
name|DFS_CONTAINER_RATIS_RPC_TYPE_DEFAULT
argument_list|)
decl_stmt|;
specifier|final
name|RetryPolicy
name|retryPolicy
init|=
name|RatisHelper
operator|.
name|createRetryPolicy
argument_list|(
name|ozoneConf
argument_list|)
decl_stmt|;
specifier|final
name|RaftPeer
name|p
init|=
name|RatisHelper
operator|.
name|toRaftPeer
argument_list|(
name|dn
argument_list|)
decl_stmt|;
specifier|final
name|int
name|maxOutstandingRequests
init|=
name|HddsClientUtils
operator|.
name|getMaxOutstandingRequests
argument_list|(
name|ozoneConf
argument_list|)
decl_stmt|;
specifier|final
name|TimeDuration
name|requestTimeout
init|=
name|RatisHelper
operator|.
name|getClientRequestTimeout
argument_list|(
name|ozoneConf
argument_list|)
decl_stmt|;
try|try
init|(
name|RaftClient
name|client
init|=
name|RatisHelper
operator|.
name|newRaftClient
argument_list|(
name|SupportedRpcType
operator|.
name|valueOfIgnoreCase
argument_list|(
name|rpcType
argument_list|)
argument_list|,
name|p
argument_list|,
name|retryPolicy
argument_list|,
name|maxOutstandingRequests
argument_list|,
name|grpcTlsConfig
argument_list|,
name|requestTimeout
argument_list|)
init|)
block|{
name|client
operator|.
name|groupRemove
argument_list|(
name|RaftGroupId
operator|.
name|valueOf
argument_list|(
name|pipelineID
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|,
literal|true
argument_list|,
name|p
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

