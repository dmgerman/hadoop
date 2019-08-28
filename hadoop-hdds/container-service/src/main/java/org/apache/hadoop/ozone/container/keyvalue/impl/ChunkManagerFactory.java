begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.keyvalue.impl
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|container
operator|.
name|keyvalue
operator|.
name|impl
package|;
end_package

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
name|ozone
operator|.
name|container
operator|.
name|keyvalue
operator|.
name|interfaces
operator|.
name|ChunkManager
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
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|HddsConfigKeys
operator|.
name|HDDS_CONTAINER_PERSISTDATA
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
name|hdds
operator|.
name|HddsConfigKeys
operator|.
name|HDDS_CONTAINER_PERSISTDATA_DEFAULT
import|;
end_import

begin_comment
comment|/**  * Select an appropriate ChunkManager implementation as per config setting.  * Ozone ChunkManager is a Singleton  */
end_comment

begin_class
DECL|class|ChunkManagerFactory
specifier|public
specifier|final
class|class
name|ChunkManagerFactory
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ChunkManagerFactory
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|instance
specifier|private
specifier|static
specifier|volatile
name|ChunkManager
name|instance
init|=
literal|null
decl_stmt|;
DECL|field|syncChunks
specifier|private
specifier|static
name|boolean
name|syncChunks
init|=
literal|false
decl_stmt|;
DECL|method|ChunkManagerFactory ()
specifier|private
name|ChunkManagerFactory
parameter_list|()
block|{   }
DECL|method|getChunkManager (Configuration config, boolean sync)
specifier|public
specifier|static
name|ChunkManager
name|getChunkManager
parameter_list|(
name|Configuration
name|config
parameter_list|,
name|boolean
name|sync
parameter_list|)
block|{
if|if
condition|(
name|instance
operator|==
literal|null
condition|)
block|{
synchronized|synchronized
init|(
name|ChunkManagerFactory
operator|.
name|class
init|)
block|{
if|if
condition|(
name|instance
operator|==
literal|null
condition|)
block|{
name|instance
operator|=
name|createChunkManager
argument_list|(
name|config
argument_list|,
name|sync
argument_list|)
expr_stmt|;
name|syncChunks
operator|=
name|sync
expr_stmt|;
block|}
block|}
block|}
name|Preconditions
operator|.
name|checkArgument
argument_list|(
operator|(
name|syncChunks
operator|==
name|sync
operator|)
argument_list|,
literal|"value of sync conflicts with previous invocation"
argument_list|)
expr_stmt|;
return|return
name|instance
return|;
block|}
DECL|method|createChunkManager (Configuration config, boolean sync)
specifier|private
specifier|static
name|ChunkManager
name|createChunkManager
parameter_list|(
name|Configuration
name|config
parameter_list|,
name|boolean
name|sync
parameter_list|)
block|{
name|ChunkManager
name|manager
init|=
literal|null
decl_stmt|;
name|boolean
name|persist
init|=
name|config
operator|.
name|getBoolean
argument_list|(
name|HDDS_CONTAINER_PERSISTDATA
argument_list|,
name|HDDS_CONTAINER_PERSISTDATA_DEFAULT
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|persist
condition|)
block|{
name|boolean
name|scrubber
init|=
name|config
operator|.
name|getBoolean
argument_list|(
name|HddsConfigKeys
operator|.
name|HDDS_CONTAINERSCRUB_ENABLED
argument_list|,
name|HddsConfigKeys
operator|.
name|HDDS_CONTAINERSCRUB_ENABLED_DEFAULT
argument_list|)
decl_stmt|;
if|if
condition|(
name|scrubber
condition|)
block|{
comment|// Data Scrubber needs to be disabled for non-persistent chunks.
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to set "
operator|+
name|HDDS_CONTAINER_PERSISTDATA
operator|+
literal|" to false."
operator|+
literal|" Please set "
operator|+
name|HddsConfigKeys
operator|.
name|HDDS_CONTAINERSCRUB_ENABLED
operator|+
literal|" also to false to enable non-persistent containers."
argument_list|)
expr_stmt|;
name|persist
operator|=
literal|true
expr_stmt|;
block|}
block|}
if|if
condition|(
name|persist
condition|)
block|{
name|manager
operator|=
operator|new
name|ChunkManagerImpl
argument_list|(
name|sync
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|HDDS_CONTAINER_PERSISTDATA
operator|+
literal|" is set to false. This should be used only for testing."
operator|+
literal|" All user data will be discarded."
argument_list|)
expr_stmt|;
name|manager
operator|=
operator|new
name|ChunkManagerDummyImpl
argument_list|(
name|sync
argument_list|)
expr_stmt|;
block|}
return|return
name|manager
return|;
block|}
block|}
end_class

end_unit

