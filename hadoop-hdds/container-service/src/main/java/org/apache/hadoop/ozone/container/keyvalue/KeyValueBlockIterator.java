begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.keyvalue
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
name|hdds
operator|.
name|conf
operator|.
name|OzoneConfiguration
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
name|OzoneConsts
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
name|common
operator|.
name|helpers
operator|.
name|BlockData
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
name|common
operator|.
name|helpers
operator|.
name|ContainerUtils
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
name|common
operator|.
name|impl
operator|.
name|ContainerData
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
name|common
operator|.
name|impl
operator|.
name|ContainerDataYaml
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
name|common
operator|.
name|interfaces
operator|.
name|BlockIterator
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
name|helpers
operator|.
name|BlockUtils
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
name|helpers
operator|.
name|KeyValueContainerLocationUtil
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
name|utils
operator|.
name|MetaStoreIterator
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
name|utils
operator|.
name|MetadataKeyFilters
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
name|utils
operator|.
name|MetadataKeyFilters
operator|.
name|KeyPrefixFilter
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
name|utils
operator|.
name|MetadataStore
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
name|utils
operator|.
name|MetadataStore
operator|.
name|KeyValue
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
name|java
operator|.
name|io
operator|.
name|File
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
name|NoSuchElementException
import|;
end_import

begin_comment
comment|/**  * Block Iterator for KeyValue Container. This block iterator returns blocks  * which match with the {@link MetadataKeyFilters.KeyPrefixFilter}. If no  * filter is specified, then default filter used is  * {@link MetadataKeyFilters#getNormalKeyFilter()}  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
DECL|class|KeyValueBlockIterator
specifier|public
class|class
name|KeyValueBlockIterator
implements|implements
name|BlockIterator
argument_list|<
name|BlockData
argument_list|>
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
name|KeyValueBlockIterator
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|blockIterator
specifier|private
name|MetaStoreIterator
argument_list|<
name|KeyValue
argument_list|>
name|blockIterator
decl_stmt|;
DECL|field|defaultBlockFilter
specifier|private
specifier|static
name|KeyPrefixFilter
name|defaultBlockFilter
init|=
name|MetadataKeyFilters
operator|.
name|getNormalKeyFilter
argument_list|()
decl_stmt|;
DECL|field|blockFilter
specifier|private
name|KeyPrefixFilter
name|blockFilter
decl_stmt|;
DECL|field|nextBlock
specifier|private
name|BlockData
name|nextBlock
decl_stmt|;
DECL|field|containerId
specifier|private
name|long
name|containerId
decl_stmt|;
comment|/**    * KeyValueBlockIterator to iterate blocks in a container.    * @param id - container id    * @param path -  container base path    * @throws IOException    */
DECL|method|KeyValueBlockIterator (long id, File path)
specifier|public
name|KeyValueBlockIterator
parameter_list|(
name|long
name|id
parameter_list|,
name|File
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|id
argument_list|,
name|path
argument_list|,
name|defaultBlockFilter
argument_list|)
expr_stmt|;
block|}
comment|/**    * KeyValueBlockIterator to iterate blocks in a container.    * @param id - container id    * @param path - container base path    * @param filter - Block filter, filter to be applied for blocks    * @throws IOException    */
DECL|method|KeyValueBlockIterator (long id, File path, KeyPrefixFilter filter)
specifier|public
name|KeyValueBlockIterator
parameter_list|(
name|long
name|id
parameter_list|,
name|File
name|path
parameter_list|,
name|KeyPrefixFilter
name|filter
parameter_list|)
throws|throws
name|IOException
block|{
name|containerId
operator|=
name|id
expr_stmt|;
name|File
name|metdataPath
init|=
operator|new
name|File
argument_list|(
name|path
argument_list|,
name|OzoneConsts
operator|.
name|METADATA
argument_list|)
decl_stmt|;
name|File
name|containerFile
init|=
name|ContainerUtils
operator|.
name|getContainerFile
argument_list|(
name|metdataPath
operator|.
name|getParentFile
argument_list|()
argument_list|)
decl_stmt|;
name|ContainerData
name|containerData
init|=
name|ContainerDataYaml
operator|.
name|readContainerFile
argument_list|(
name|containerFile
argument_list|)
decl_stmt|;
name|KeyValueContainerData
name|keyValueContainerData
init|=
operator|(
name|KeyValueContainerData
operator|)
name|containerData
decl_stmt|;
name|keyValueContainerData
operator|.
name|setDbFile
argument_list|(
name|KeyValueContainerLocationUtil
operator|.
name|getContainerDBFile
argument_list|(
name|metdataPath
argument_list|,
name|containerId
argument_list|)
argument_list|)
expr_stmt|;
name|MetadataStore
name|metadataStore
init|=
name|BlockUtils
operator|.
name|getDB
argument_list|(
name|keyValueContainerData
argument_list|,
operator|new
name|OzoneConfiguration
argument_list|()
argument_list|)
decl_stmt|;
name|blockIterator
operator|=
name|metadataStore
operator|.
name|iterator
argument_list|()
expr_stmt|;
name|blockFilter
operator|=
name|filter
expr_stmt|;
block|}
comment|/**    * This method returns blocks matching with the filter.    * @return next block or null if no more blocks    * @throws IOException    */
annotation|@
name|Override
DECL|method|nextBlock ()
specifier|public
name|BlockData
name|nextBlock
parameter_list|()
throws|throws
name|IOException
throws|,
name|NoSuchElementException
block|{
if|if
condition|(
name|nextBlock
operator|!=
literal|null
condition|)
block|{
name|BlockData
name|currentBlock
init|=
name|nextBlock
decl_stmt|;
name|nextBlock
operator|=
literal|null
expr_stmt|;
return|return
name|currentBlock
return|;
block|}
if|if
condition|(
name|hasNext
argument_list|()
condition|)
block|{
return|return
name|nextBlock
argument_list|()
return|;
block|}
throw|throw
operator|new
name|NoSuchElementException
argument_list|(
literal|"Block Iterator reached end for "
operator|+
literal|"ContainerID "
operator|+
name|containerId
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|hasNext ()
specifier|public
name|boolean
name|hasNext
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|nextBlock
operator|!=
literal|null
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|blockIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|KeyValue
name|block
init|=
name|blockIterator
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|blockFilter
operator|.
name|filterKey
argument_list|(
literal|null
argument_list|,
name|block
operator|.
name|getKey
argument_list|()
argument_list|,
literal|null
argument_list|)
condition|)
block|{
name|nextBlock
operator|=
name|BlockUtils
operator|.
name|getBlockData
argument_list|(
name|block
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|trace
argument_list|(
literal|"Block matching with filter found: blockID is : {} for "
operator|+
literal|"containerID {}"
argument_list|,
name|nextBlock
operator|.
name|getLocalID
argument_list|()
argument_list|,
name|containerId
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
name|hasNext
argument_list|()
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|seekToFirst ()
specifier|public
name|void
name|seekToFirst
parameter_list|()
block|{
name|nextBlock
operator|=
literal|null
expr_stmt|;
name|blockIterator
operator|.
name|seekToFirst
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|seekToLast ()
specifier|public
name|void
name|seekToLast
parameter_list|()
block|{
name|nextBlock
operator|=
literal|null
expr_stmt|;
name|blockIterator
operator|.
name|seekToLast
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

