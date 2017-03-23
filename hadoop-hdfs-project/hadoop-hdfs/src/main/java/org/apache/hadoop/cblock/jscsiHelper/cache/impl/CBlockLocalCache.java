begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.cblock.jscsiHelper.cache.impl
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|cblock
operator|.
name|jscsiHelper
operator|.
name|cache
operator|.
name|impl
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
name|cblock
operator|.
name|jscsiHelper
operator|.
name|CBlockTargetMetrics
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
name|cblock
operator|.
name|jscsiHelper
operator|.
name|ContainerCacheFlusher
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
name|cblock
operator|.
name|jscsiHelper
operator|.
name|cache
operator|.
name|CacheModule
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
name|cblock
operator|.
name|jscsiHelper
operator|.
name|cache
operator|.
name|LogicalBlock
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
name|scm
operator|.
name|XceiverClientManager
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
name|scm
operator|.
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|Pipeline
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
name|List
import|;
end_import

begin_comment
comment|/**  * A local cache used by the CBlock ISCSI server. This class is enabled or  * disabled via config settings.  *  * TODO : currently, this class is a just a place holder.  */
end_comment

begin_class
DECL|class|CBlockLocalCache
specifier|final
specifier|public
class|class
name|CBlockLocalCache
implements|implements
name|CacheModule
block|{
DECL|method|CBlockLocalCache ()
specifier|private
name|CBlockLocalCache
parameter_list|()
block|{   }
annotation|@
name|Override
DECL|method|get (long blockID)
specifier|public
name|LogicalBlock
name|get
parameter_list|(
name|long
name|blockID
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|put (long blockID, byte[] data)
specifier|public
name|void
name|put
parameter_list|(
name|long
name|blockID
parameter_list|,
name|byte
index|[]
name|data
parameter_list|)
throws|throws
name|IOException
block|{   }
annotation|@
name|Override
DECL|method|flush ()
specifier|public
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{    }
annotation|@
name|Override
DECL|method|start ()
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|IOException
block|{    }
annotation|@
name|Override
DECL|method|stop ()
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|IOException
block|{    }
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{    }
annotation|@
name|Override
DECL|method|isDirtyCache ()
specifier|public
name|boolean
name|isDirtyCache
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
DECL|method|newBuilder ()
specifier|public
specifier|static
name|Builder
name|newBuilder
parameter_list|()
block|{
return|return
operator|new
name|Builder
argument_list|()
return|;
block|}
comment|/**    * Builder class for CBlocklocalCache.    */
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
block|{
comment|/**      * Sets the Config to be used by this cache.      *      * @param configuration - Config      * @return Builder      */
DECL|method|setConfiguration (Configuration configuration)
specifier|public
name|Builder
name|setConfiguration
parameter_list|(
name|Configuration
name|configuration
parameter_list|)
block|{
return|return
name|this
return|;
block|}
comment|/**      * Sets the user name who is the owner of this volume.      *      * @param userName - name of the owner, please note this is not the current      * user name.      * @return - Builder      */
DECL|method|setUserName (String userName)
specifier|public
name|Builder
name|setUserName
parameter_list|(
name|String
name|userName
parameter_list|)
block|{
return|return
name|this
return|;
block|}
comment|/**      * Sets the VolumeName.      *      * @param volumeName - Name of the volume      * @return Builder      */
DECL|method|setVolumeName (String volumeName)
specifier|public
name|Builder
name|setVolumeName
parameter_list|(
name|String
name|volumeName
parameter_list|)
block|{
return|return
name|this
return|;
block|}
comment|/**      * Sets the Pipelines that form this volume.      *      * @param pipelines - list of pipelines      * @return Builder      */
DECL|method|setPipelines (List<Pipeline> pipelines)
specifier|public
name|Builder
name|setPipelines
parameter_list|(
name|List
argument_list|<
name|Pipeline
argument_list|>
name|pipelines
parameter_list|)
block|{
return|return
name|this
return|;
block|}
comment|/**      * Sets the Client Manager that manages the communication with containers.      *      * @param clientManager - clientManager.      * @return - Builder      */
DECL|method|setClientManager (XceiverClientManager clientManager)
specifier|public
name|Builder
name|setClientManager
parameter_list|(
name|XceiverClientManager
name|clientManager
parameter_list|)
block|{
return|return
name|this
return|;
block|}
comment|/**      * Sets the block size -- Typical sizes are 4KB, 8KB etc.      *      * @param blockSize - BlockSize.      * @return - Builder      */
DECL|method|setBlockSize (int blockSize)
specifier|public
name|Builder
name|setBlockSize
parameter_list|(
name|int
name|blockSize
parameter_list|)
block|{
return|return
name|this
return|;
block|}
comment|/**      * Sets the volumeSize.      *      * @param volumeSize - VolumeSize      * @return - Builder      */
DECL|method|setVolumeSize (long volumeSize)
specifier|public
name|Builder
name|setVolumeSize
parameter_list|(
name|long
name|volumeSize
parameter_list|)
block|{
return|return
name|this
return|;
block|}
comment|/**      * Set flusher.      * @param flusher - cache Flusher      * @return Builder.      */
DECL|method|setFlusher (ContainerCacheFlusher flusher)
specifier|public
name|Builder
name|setFlusher
parameter_list|(
name|ContainerCacheFlusher
name|flusher
parameter_list|)
block|{
return|return
name|this
return|;
block|}
comment|/**      * Sets the cblock Metrics.      *      * @param targetMetrics - CBlock Target Metrics      * @return - Builder      */
DECL|method|setCBlockTargetMetrics (CBlockTargetMetrics targetMetrics)
specifier|public
name|Builder
name|setCBlockTargetMetrics
parameter_list|(
name|CBlockTargetMetrics
name|targetMetrics
parameter_list|)
block|{
return|return
name|this
return|;
block|}
DECL|method|build ()
specifier|public
name|CBlockLocalCache
name|build
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|CBlockLocalCache
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

