begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.protocol
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocol
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

begin_comment
comment|/**  * A block and the full path information to the block data file and  * the metadata file stored on the local file system.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|BlockLocalPathInfo
specifier|public
class|class
name|BlockLocalPathInfo
block|{
DECL|field|block
specifier|private
specifier|final
name|ExtendedBlock
name|block
decl_stmt|;
DECL|field|localBlockPath
specifier|private
name|String
name|localBlockPath
init|=
literal|""
decl_stmt|;
comment|// local file storing the data
DECL|field|localMetaPath
specifier|private
name|String
name|localMetaPath
init|=
literal|""
decl_stmt|;
comment|// local file storing the checksum
comment|/**    * Constructs BlockLocalPathInfo.    * @param b The block corresponding to this lock path info.     * @param file Block data file.    * @param metafile Metadata file for the block.    */
DECL|method|BlockLocalPathInfo (ExtendedBlock b, String file, String metafile)
specifier|public
name|BlockLocalPathInfo
parameter_list|(
name|ExtendedBlock
name|b
parameter_list|,
name|String
name|file
parameter_list|,
name|String
name|metafile
parameter_list|)
block|{
name|block
operator|=
name|b
expr_stmt|;
name|localBlockPath
operator|=
name|file
expr_stmt|;
name|localMetaPath
operator|=
name|metafile
expr_stmt|;
block|}
comment|/**    * Get the Block data file.    * @return Block data file.    */
DECL|method|getBlockPath ()
specifier|public
name|String
name|getBlockPath
parameter_list|()
block|{
return|return
name|localBlockPath
return|;
block|}
comment|/**    * @return the Block    */
DECL|method|getBlock ()
specifier|public
name|ExtendedBlock
name|getBlock
parameter_list|()
block|{
return|return
name|block
return|;
block|}
comment|/**    * Get the Block metadata file.    * @return Block metadata file.    */
DECL|method|getMetaPath ()
specifier|public
name|String
name|getMetaPath
parameter_list|()
block|{
return|return
name|localMetaPath
return|;
block|}
comment|/**    * Get number of bytes in the block.    * @return Number of bytes in the block.    */
DECL|method|getNumBytes ()
specifier|public
name|long
name|getNumBytes
parameter_list|()
block|{
return|return
name|block
operator|.
name|getNumBytes
argument_list|()
return|;
block|}
block|}
end_class

end_unit

