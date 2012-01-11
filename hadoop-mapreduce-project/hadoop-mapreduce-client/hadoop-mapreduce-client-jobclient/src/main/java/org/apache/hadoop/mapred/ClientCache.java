begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
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
name|HashMap
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
name|commons
operator|.
name|lang
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
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|JobID
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
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|MRClientProtocol
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
name|mapreduce
operator|.
name|v2
operator|.
name|jobhistory
operator|.
name|JHAdminConfig
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
name|yarn
operator|.
name|YarnException
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
name|ipc
operator|.
name|YarnRPC
import|;
end_import

begin_class
DECL|class|ClientCache
specifier|public
class|class
name|ClientCache
block|{
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
decl_stmt|;
DECL|field|rm
specifier|private
specifier|final
name|ResourceMgrDelegate
name|rm
decl_stmt|;
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
name|ClientCache
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|cache
specifier|private
name|Map
argument_list|<
name|JobID
argument_list|,
name|ClientServiceDelegate
argument_list|>
name|cache
init|=
operator|new
name|HashMap
argument_list|<
name|JobID
argument_list|,
name|ClientServiceDelegate
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|hsProxy
specifier|private
name|MRClientProtocol
name|hsProxy
decl_stmt|;
DECL|method|ClientCache (Configuration conf, ResourceMgrDelegate rm)
specifier|public
name|ClientCache
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|ResourceMgrDelegate
name|rm
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
name|rm
operator|=
name|rm
expr_stmt|;
block|}
comment|//TODO: evict from the cache on some threshold
DECL|method|getClient (JobID jobId)
specifier|public
specifier|synchronized
name|ClientServiceDelegate
name|getClient
parameter_list|(
name|JobID
name|jobId
parameter_list|)
block|{
if|if
condition|(
name|hsProxy
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|hsProxy
operator|=
name|instantiateHistoryProxy
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
name|warn
argument_list|(
literal|"Could not connect to History server."
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|YarnException
argument_list|(
literal|"Could not connect to History server."
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
name|ClientServiceDelegate
name|client
init|=
name|cache
operator|.
name|get
argument_list|(
name|jobId
argument_list|)
decl_stmt|;
if|if
condition|(
name|client
operator|==
literal|null
condition|)
block|{
name|client
operator|=
operator|new
name|ClientServiceDelegate
argument_list|(
name|conf
argument_list|,
name|rm
argument_list|,
name|jobId
argument_list|,
name|hsProxy
argument_list|)
expr_stmt|;
name|cache
operator|.
name|put
argument_list|(
name|jobId
argument_list|,
name|client
argument_list|)
expr_stmt|;
block|}
return|return
name|client
return|;
block|}
DECL|method|getInitializedHSProxy ()
specifier|protected
specifier|synchronized
name|MRClientProtocol
name|getInitializedHSProxy
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|this
operator|.
name|hsProxy
operator|==
literal|null
condition|)
block|{
name|hsProxy
operator|=
name|instantiateHistoryProxy
argument_list|()
expr_stmt|;
block|}
return|return
name|this
operator|.
name|hsProxy
return|;
block|}
DECL|method|instantiateHistoryProxy ()
specifier|protected
name|MRClientProtocol
name|instantiateHistoryProxy
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|String
name|serviceAddr
init|=
name|conf
operator|.
name|get
argument_list|(
name|JHAdminConfig
operator|.
name|MR_HISTORY_ADDRESS
argument_list|)
decl_stmt|;
if|if
condition|(
name|StringUtils
operator|.
name|isEmpty
argument_list|(
name|serviceAddr
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Connecting to HistoryServer at: "
operator|+
name|serviceAddr
argument_list|)
expr_stmt|;
specifier|final
name|YarnRPC
name|rpc
init|=
name|YarnRPC
operator|.
name|create
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Connected to HistoryServer at: "
operator|+
name|serviceAddr
argument_list|)
expr_stmt|;
name|UserGroupInformation
name|currentUser
init|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
decl_stmt|;
return|return
name|currentUser
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedAction
argument_list|<
name|MRClientProtocol
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|MRClientProtocol
name|run
parameter_list|()
block|{
return|return
operator|(
name|MRClientProtocol
operator|)
name|rpc
operator|.
name|getProxy
argument_list|(
name|MRClientProtocol
operator|.
name|class
argument_list|,
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|serviceAddr
argument_list|)
argument_list|,
name|conf
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
block|}
end_class

end_unit

