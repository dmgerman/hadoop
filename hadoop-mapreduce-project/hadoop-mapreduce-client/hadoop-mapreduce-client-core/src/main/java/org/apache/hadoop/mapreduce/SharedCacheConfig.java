begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
operator|.
name|Private
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
operator|.
name|Unstable
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
name|yarn
operator|.
name|conf
operator|.
name|YarnConfiguration
import|;
end_import

begin_comment
comment|/**  * A class for parsing configuration parameters associated with the shared  * cache.  */
end_comment

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|SharedCacheConfig
specifier|public
class|class
name|SharedCacheConfig
block|{
DECL|field|LOG
specifier|protected
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|SharedCacheConfig
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|sharedCacheFilesEnabled
specifier|private
name|boolean
name|sharedCacheFilesEnabled
init|=
literal|false
decl_stmt|;
DECL|field|sharedCacheLibjarsEnabled
specifier|private
name|boolean
name|sharedCacheLibjarsEnabled
init|=
literal|false
decl_stmt|;
DECL|field|sharedCacheArchivesEnabled
specifier|private
name|boolean
name|sharedCacheArchivesEnabled
init|=
literal|false
decl_stmt|;
DECL|field|sharedCacheJobjarEnabled
specifier|private
name|boolean
name|sharedCacheJobjarEnabled
init|=
literal|false
decl_stmt|;
DECL|method|init (Configuration conf)
specifier|public
name|void
name|init
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
if|if
condition|(
operator|!
name|MRConfig
operator|.
name|YARN_FRAMEWORK_NAME
operator|.
name|equals
argument_list|(
name|conf
operator|.
name|get
argument_list|(
name|MRConfig
operator|.
name|FRAMEWORK_NAME
argument_list|)
argument_list|)
condition|)
block|{
comment|// Shared cache is only valid if the job runs on yarn
return|return;
block|}
if|if
condition|(
operator|!
name|conf
operator|.
name|getBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|SHARED_CACHE_ENABLED
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_SHARED_CACHE_ENABLED
argument_list|)
condition|)
block|{
return|return;
block|}
name|Collection
argument_list|<
name|String
argument_list|>
name|configs
init|=
name|StringUtils
operator|.
name|getTrimmedStringCollection
argument_list|(
name|conf
operator|.
name|get
argument_list|(
name|MRJobConfig
operator|.
name|SHARED_CACHE_MODE
argument_list|,
name|MRJobConfig
operator|.
name|SHARED_CACHE_MODE_DEFAULT
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|configs
operator|.
name|contains
argument_list|(
literal|"files"
argument_list|)
condition|)
block|{
name|this
operator|.
name|sharedCacheFilesEnabled
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|configs
operator|.
name|contains
argument_list|(
literal|"libjars"
argument_list|)
condition|)
block|{
name|this
operator|.
name|sharedCacheLibjarsEnabled
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|configs
operator|.
name|contains
argument_list|(
literal|"archives"
argument_list|)
condition|)
block|{
name|this
operator|.
name|sharedCacheArchivesEnabled
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|configs
operator|.
name|contains
argument_list|(
literal|"jobjar"
argument_list|)
condition|)
block|{
name|this
operator|.
name|sharedCacheJobjarEnabled
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|configs
operator|.
name|contains
argument_list|(
literal|"enabled"
argument_list|)
condition|)
block|{
name|this
operator|.
name|sharedCacheFilesEnabled
operator|=
literal|true
expr_stmt|;
name|this
operator|.
name|sharedCacheLibjarsEnabled
operator|=
literal|true
expr_stmt|;
name|this
operator|.
name|sharedCacheArchivesEnabled
operator|=
literal|true
expr_stmt|;
name|this
operator|.
name|sharedCacheJobjarEnabled
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|configs
operator|.
name|contains
argument_list|(
literal|"disabled"
argument_list|)
condition|)
block|{
name|this
operator|.
name|sharedCacheFilesEnabled
operator|=
literal|false
expr_stmt|;
name|this
operator|.
name|sharedCacheLibjarsEnabled
operator|=
literal|false
expr_stmt|;
name|this
operator|.
name|sharedCacheArchivesEnabled
operator|=
literal|false
expr_stmt|;
name|this
operator|.
name|sharedCacheJobjarEnabled
operator|=
literal|false
expr_stmt|;
block|}
block|}
DECL|method|isSharedCacheFilesEnabled ()
specifier|public
name|boolean
name|isSharedCacheFilesEnabled
parameter_list|()
block|{
return|return
name|sharedCacheFilesEnabled
return|;
block|}
DECL|method|isSharedCacheLibjarsEnabled ()
specifier|public
name|boolean
name|isSharedCacheLibjarsEnabled
parameter_list|()
block|{
return|return
name|sharedCacheLibjarsEnabled
return|;
block|}
DECL|method|isSharedCacheArchivesEnabled ()
specifier|public
name|boolean
name|isSharedCacheArchivesEnabled
parameter_list|()
block|{
return|return
name|sharedCacheArchivesEnabled
return|;
block|}
DECL|method|isSharedCacheJobjarEnabled ()
specifier|public
name|boolean
name|isSharedCacheJobjarEnabled
parameter_list|()
block|{
return|return
name|sharedCacheJobjarEnabled
return|;
block|}
DECL|method|isSharedCacheEnabled ()
specifier|public
name|boolean
name|isSharedCacheEnabled
parameter_list|()
block|{
return|return
operator|(
name|sharedCacheFilesEnabled
operator|||
name|sharedCacheLibjarsEnabled
operator|||
name|sharedCacheArchivesEnabled
operator|||
name|sharedCacheJobjarEnabled
operator|)
return|;
block|}
block|}
end_class

end_unit

