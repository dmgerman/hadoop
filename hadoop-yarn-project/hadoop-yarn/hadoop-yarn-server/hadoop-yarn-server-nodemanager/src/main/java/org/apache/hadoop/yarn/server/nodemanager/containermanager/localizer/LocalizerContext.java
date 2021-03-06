begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer
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
name|nodemanager
operator|.
name|containermanager
operator|.
name|localizer
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Future
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
name|fs
operator|.
name|FileStatus
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
name|fs
operator|.
name|Path
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
name|Credentials
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
name|ContainerId
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
name|cache
operator|.
name|LoadingCache
import|;
end_import

begin_class
DECL|class|LocalizerContext
specifier|public
class|class
name|LocalizerContext
block|{
DECL|field|user
specifier|private
specifier|final
name|String
name|user
decl_stmt|;
DECL|field|containerId
specifier|private
specifier|final
name|ContainerId
name|containerId
decl_stmt|;
DECL|field|credentials
specifier|private
specifier|final
name|Credentials
name|credentials
decl_stmt|;
DECL|field|statCache
specifier|private
specifier|final
name|LoadingCache
argument_list|<
name|Path
argument_list|,
name|Future
argument_list|<
name|FileStatus
argument_list|>
argument_list|>
name|statCache
decl_stmt|;
DECL|method|LocalizerContext (String user, ContainerId containerId, Credentials credentials)
specifier|public
name|LocalizerContext
parameter_list|(
name|String
name|user
parameter_list|,
name|ContainerId
name|containerId
parameter_list|,
name|Credentials
name|credentials
parameter_list|)
block|{
name|this
argument_list|(
name|user
argument_list|,
name|containerId
argument_list|,
name|credentials
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|LocalizerContext (String user, ContainerId containerId, Credentials credentials, LoadingCache<Path,Future<FileStatus>> statCache)
specifier|public
name|LocalizerContext
parameter_list|(
name|String
name|user
parameter_list|,
name|ContainerId
name|containerId
parameter_list|,
name|Credentials
name|credentials
parameter_list|,
name|LoadingCache
argument_list|<
name|Path
argument_list|,
name|Future
argument_list|<
name|FileStatus
argument_list|>
argument_list|>
name|statCache
parameter_list|)
block|{
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
name|this
operator|.
name|containerId
operator|=
name|containerId
expr_stmt|;
name|this
operator|.
name|credentials
operator|=
name|credentials
expr_stmt|;
name|this
operator|.
name|statCache
operator|=
name|statCache
expr_stmt|;
block|}
DECL|method|getUser ()
specifier|public
name|String
name|getUser
parameter_list|()
block|{
return|return
name|user
return|;
block|}
DECL|method|getContainerId ()
specifier|public
name|ContainerId
name|getContainerId
parameter_list|()
block|{
return|return
name|containerId
return|;
block|}
DECL|method|getCredentials ()
specifier|public
name|Credentials
name|getCredentials
parameter_list|()
block|{
return|return
name|credentials
return|;
block|}
DECL|method|getStatCache ()
specifier|public
name|LoadingCache
argument_list|<
name|Path
argument_list|,
name|Future
argument_list|<
name|FileStatus
argument_list|>
argument_list|>
name|getStatCache
parameter_list|()
block|{
return|return
name|statCache
return|;
block|}
block|}
end_class

end_unit

