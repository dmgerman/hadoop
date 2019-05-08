begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode.fsdataset.impl
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
name|datanode
operator|.
name|fsdataset
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
name|ExtendedBlockId
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
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
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|FileChannel
import|;
end_import

begin_comment
comment|/**  * Maps block to DataNode cache region.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|MappableBlockLoader
specifier|public
specifier|abstract
class|class
name|MappableBlockLoader
block|{
comment|/**    * Initialize a specific MappableBlockLoader.    */
DECL|method|initialize (FsDatasetCache cacheManager)
specifier|abstract
name|void
name|initialize
parameter_list|(
name|FsDatasetCache
name|cacheManager
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Load the block.    *    * Map the block, and then verify its checksum.    *    * @param length         The current length of the block.    * @param blockIn        The block input stream. Should be positioned at the    *                       start. The caller must close this.    * @param metaIn         The meta file input stream. Should be positioned at    *                       the start. The caller must close this.    * @param blockFileName  The block file name, for logging purposes.    * @param key            The extended block ID.    *    * @throws IOException   If mapping block to cache region fails or checksum    *                       fails.    *    * @return               The Mappable block.    */
DECL|method|load (long length, FileInputStream blockIn, FileInputStream metaIn, String blockFileName, ExtendedBlockId key)
specifier|abstract
name|MappableBlock
name|load
parameter_list|(
name|long
name|length
parameter_list|,
name|FileInputStream
name|blockIn
parameter_list|,
name|FileInputStream
name|metaIn
parameter_list|,
name|String
name|blockFileName
parameter_list|,
name|ExtendedBlockId
name|key
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Try to reserve some given bytes.    *    * @param key           The ExtendedBlockId for a block.    *    * @param bytesCount    The number of bytes to add.    *    * @return              The new number of usedBytes if we succeeded;    *                      -1 if we failed.    */
DECL|method|reserve (ExtendedBlockId key, long bytesCount)
specifier|abstract
name|long
name|reserve
parameter_list|(
name|ExtendedBlockId
name|key
parameter_list|,
name|long
name|bytesCount
parameter_list|)
function_decl|;
comment|/**    * Release some bytes that we're using.    *    * @param key           The ExtendedBlockId for a block.    *    * @param bytesCount    The number of bytes to release.    *    * @return              The new number of usedBytes.    */
DECL|method|release (ExtendedBlockId key, long bytesCount)
specifier|abstract
name|long
name|release
parameter_list|(
name|ExtendedBlockId
name|key
parameter_list|,
name|long
name|bytesCount
parameter_list|)
function_decl|;
comment|/**    * Get the approximate amount of cache space used.    */
DECL|method|getCacheUsed ()
specifier|abstract
name|long
name|getCacheUsed
parameter_list|()
function_decl|;
comment|/**    * Get the maximum amount of cache bytes.    */
DECL|method|getCacheCapacity ()
specifier|abstract
name|long
name|getCacheCapacity
parameter_list|()
function_decl|;
comment|/**    * Check whether the cache is non-volatile.    */
DECL|method|isTransientCache ()
specifier|abstract
name|boolean
name|isTransientCache
parameter_list|()
function_decl|;
comment|/**    * Clean up cache, can be used during DataNode shutdown.    */
DECL|method|shutdown ()
name|void
name|shutdown
parameter_list|()
block|{
comment|// Do nothing.
block|}
comment|/**    * Reads bytes into a buffer until EOF or the buffer's limit is reached.    */
DECL|method|fillBuffer (FileChannel channel, ByteBuffer buf)
specifier|protected
name|int
name|fillBuffer
parameter_list|(
name|FileChannel
name|channel
parameter_list|,
name|ByteBuffer
name|buf
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|bytesRead
init|=
name|channel
operator|.
name|read
argument_list|(
name|buf
argument_list|)
decl_stmt|;
if|if
condition|(
name|bytesRead
operator|<
literal|0
condition|)
block|{
comment|//EOF
return|return
name|bytesRead
return|;
block|}
while|while
condition|(
name|buf
operator|.
name|remaining
argument_list|()
operator|>
literal|0
condition|)
block|{
name|int
name|n
init|=
name|channel
operator|.
name|read
argument_list|(
name|buf
argument_list|)
decl_stmt|;
if|if
condition|(
name|n
operator|<
literal|0
condition|)
block|{
comment|//EOF
return|return
name|bytesRead
return|;
block|}
name|bytesRead
operator|+=
name|n
expr_stmt|;
block|}
return|return
name|bytesRead
return|;
block|}
block|}
end_class

end_unit

