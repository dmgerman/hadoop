begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.aliasmap
package|package
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
name|aliasmap
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
name|classification
operator|.
name|InterfaceStability
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
name|protocol
operator|.
name|Block
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
name|protocol
operator|.
name|ProvidedStorageLocation
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
name|common
operator|.
name|FileRegion
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Optional
import|;
end_import

begin_comment
comment|/**  * Protocol used by clients to read/write data about aliases of  * provided blocks for an in-memory implementation of the  * {@link org.apache.hadoop.hdfs.server.common.blockaliasmap.BlockAliasMap}.  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|interface|InMemoryAliasMapProtocol
specifier|public
interface|interface
name|InMemoryAliasMapProtocol
block|{
comment|/**    * The result of a read from the in-memory aliasmap. It contains the    * a list of FileRegions that are returned, along with the next block    * from which the read operation must continue.    */
DECL|class|IterationResult
class|class
name|IterationResult
block|{
DECL|field|batch
specifier|private
specifier|final
name|List
argument_list|<
name|FileRegion
argument_list|>
name|batch
decl_stmt|;
DECL|field|nextMarker
specifier|private
specifier|final
name|Optional
argument_list|<
name|Block
argument_list|>
name|nextMarker
decl_stmt|;
DECL|method|IterationResult (List<FileRegion> batch, Optional<Block> nextMarker)
specifier|public
name|IterationResult
parameter_list|(
name|List
argument_list|<
name|FileRegion
argument_list|>
name|batch
parameter_list|,
name|Optional
argument_list|<
name|Block
argument_list|>
name|nextMarker
parameter_list|)
block|{
name|this
operator|.
name|batch
operator|=
name|batch
expr_stmt|;
name|this
operator|.
name|nextMarker
operator|=
name|nextMarker
expr_stmt|;
block|}
DECL|method|getFileRegions ()
specifier|public
name|List
argument_list|<
name|FileRegion
argument_list|>
name|getFileRegions
parameter_list|()
block|{
return|return
name|batch
return|;
block|}
DECL|method|getNextBlock ()
specifier|public
name|Optional
argument_list|<
name|Block
argument_list|>
name|getNextBlock
parameter_list|()
block|{
return|return
name|nextMarker
return|;
block|}
block|}
comment|/**    * List the next batch of {@link FileRegion}s in the alias map starting from    * the given {@code marker}. To retrieve all {@link FileRegion}s stored in the    * alias map, multiple calls to this function might be required.    * @param marker the next block to get fileregions from.    * @return the {@link IterationResult} with a set of    * FileRegions and the next marker.    * @throws IOException    */
DECL|method|list (Optional<Block> marker)
name|InMemoryAliasMap
operator|.
name|IterationResult
name|list
parameter_list|(
name|Optional
argument_list|<
name|Block
argument_list|>
name|marker
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Gets the {@link ProvidedStorageLocation} associated with the    * specified block.    * @param block the block to lookup    * @return the associated {@link ProvidedStorageLocation}.    * @throws IOException    */
annotation|@
name|Nonnull
DECL|method|read (@onnull Block block)
name|Optional
argument_list|<
name|ProvidedStorageLocation
argument_list|>
name|read
parameter_list|(
annotation|@
name|Nonnull
name|Block
name|block
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Stores the block and it's associated {@link ProvidedStorageLocation}    * in the alias map.    * @param block    * @param providedStorageLocation    * @throws IOException    */
DECL|method|write (@onnull Block block, @Nonnull ProvidedStorageLocation providedStorageLocation)
name|void
name|write
parameter_list|(
annotation|@
name|Nonnull
name|Block
name|block
parameter_list|,
annotation|@
name|Nonnull
name|ProvidedStorageLocation
name|providedStorageLocation
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

