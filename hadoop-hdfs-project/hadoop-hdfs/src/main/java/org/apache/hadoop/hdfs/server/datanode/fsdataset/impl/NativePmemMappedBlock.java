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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|nativeio
operator|.
name|NativeIO
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
name|IOException
import|;
end_import

begin_comment
comment|/**  * Represents an HDFS block that is mapped to persistent memory by the DataNode.  */
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
DECL|class|NativePmemMappedBlock
specifier|public
class|class
name|NativePmemMappedBlock
implements|implements
name|MappableBlock
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
name|NativePmemMappedBlock
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|pmemMappedAddress
specifier|private
name|long
name|pmemMappedAddress
init|=
operator|-
literal|1L
decl_stmt|;
DECL|field|length
specifier|private
name|long
name|length
decl_stmt|;
DECL|field|key
specifier|private
name|ExtendedBlockId
name|key
decl_stmt|;
DECL|method|NativePmemMappedBlock (long pmemMappedAddress, long length, ExtendedBlockId key)
name|NativePmemMappedBlock
parameter_list|(
name|long
name|pmemMappedAddress
parameter_list|,
name|long
name|length
parameter_list|,
name|ExtendedBlockId
name|key
parameter_list|)
block|{
assert|assert
name|length
operator|>
literal|0
assert|;
name|this
operator|.
name|pmemMappedAddress
operator|=
name|pmemMappedAddress
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
name|this
operator|.
name|key
operator|=
name|key
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getLength ()
specifier|public
name|long
name|getLength
parameter_list|()
block|{
return|return
name|length
return|;
block|}
annotation|@
name|Override
DECL|method|getAddress ()
specifier|public
name|long
name|getAddress
parameter_list|()
block|{
return|return
name|pmemMappedAddress
return|;
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
block|{
if|if
condition|(
name|pmemMappedAddress
operator|!=
operator|-
literal|1L
condition|)
block|{
name|String
name|cacheFilePath
init|=
name|PmemVolumeManager
operator|.
name|getInstance
argument_list|()
operator|.
name|getCachePath
argument_list|(
name|key
argument_list|)
decl_stmt|;
try|try
block|{
comment|// Current libpmem will report error when pmem_unmap is called with
comment|// length not aligned with page size, although the length is returned
comment|// by pmem_map_file.
name|boolean
name|success
init|=
name|NativeIO
operator|.
name|POSIX
operator|.
name|Pmem
operator|.
name|unmapBlock
argument_list|(
name|pmemMappedAddress
argument_list|,
name|length
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|success
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed to unmap the mapped file from "
operator|+
literal|"pmem address: "
operator|+
name|pmemMappedAddress
argument_list|)
throw|;
block|}
name|pmemMappedAddress
operator|=
operator|-
literal|1L
expr_stmt|;
name|FsDatasetUtil
operator|.
name|deleteMappedFile
argument_list|(
name|cacheFilePath
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Successfully uncached one replica:{} from persistent memory"
operator|+
literal|", [cached path={}, length={}]"
argument_list|,
name|key
argument_list|,
name|cacheFilePath
argument_list|,
name|length
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
literal|"IOException occurred for block {}!"
argument_list|,
name|key
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

