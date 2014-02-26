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
name|java
operator|.
name|util
operator|.
name|List
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Joiner
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
name|base
operator|.
name|Preconditions
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
name|primitives
operator|.
name|Longs
import|;
end_import

begin_comment
comment|/**  * Augments an array of blocks on a datanode with additional information about  * where the block is stored.  */
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
DECL|class|HdfsBlocksMetadata
specifier|public
class|class
name|HdfsBlocksMetadata
block|{
comment|/** The block pool that was queried */
DECL|field|blockPoolId
specifier|private
specifier|final
name|String
name|blockPoolId
decl_stmt|;
comment|/**    * List of blocks    */
DECL|field|blockIds
specifier|private
specifier|final
name|long
index|[]
name|blockIds
decl_stmt|;
comment|/**    * List of volumes    */
DECL|field|volumeIds
specifier|private
specifier|final
name|List
argument_list|<
name|byte
index|[]
argument_list|>
name|volumeIds
decl_stmt|;
comment|/**    * List of indexes into<code>volumeIds</code>, one per block in    *<code>blocks</code>. A value of Integer.MAX_VALUE indicates that the    * block was not found.    */
DECL|field|volumeIndexes
specifier|private
specifier|final
name|List
argument_list|<
name|Integer
argument_list|>
name|volumeIndexes
decl_stmt|;
comment|/**    * Constructs HdfsBlocksMetadata.    *     * @param blockIds    *          List of blocks described    * @param volumeIds    *          List of potential volume identifiers, specifying volumes where     *          blocks may be stored    * @param volumeIndexes    *          Indexes into the list of volume identifiers, one per block    */
DECL|method|HdfsBlocksMetadata (String blockPoolId, long[] blockIds, List<byte[]> volumeIds, List<Integer> volumeIndexes)
specifier|public
name|HdfsBlocksMetadata
parameter_list|(
name|String
name|blockPoolId
parameter_list|,
name|long
index|[]
name|blockIds
parameter_list|,
name|List
argument_list|<
name|byte
index|[]
argument_list|>
name|volumeIds
parameter_list|,
name|List
argument_list|<
name|Integer
argument_list|>
name|volumeIndexes
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|blockIds
operator|.
name|length
operator|==
name|volumeIndexes
operator|.
name|size
argument_list|()
argument_list|,
literal|"Argument lengths should match"
argument_list|)
expr_stmt|;
name|this
operator|.
name|blockPoolId
operator|=
name|blockPoolId
expr_stmt|;
name|this
operator|.
name|blockIds
operator|=
name|blockIds
expr_stmt|;
name|this
operator|.
name|volumeIds
operator|=
name|volumeIds
expr_stmt|;
name|this
operator|.
name|volumeIndexes
operator|=
name|volumeIndexes
expr_stmt|;
block|}
comment|/**    * Get the array of blocks.    *     * @return array of blocks    */
DECL|method|getBlockIds ()
specifier|public
name|long
index|[]
name|getBlockIds
parameter_list|()
block|{
return|return
name|blockIds
return|;
block|}
comment|/**    * Get the list of volume identifiers in raw byte form.    *     * @return list of ids    */
DECL|method|getVolumeIds ()
specifier|public
name|List
argument_list|<
name|byte
index|[]
argument_list|>
name|getVolumeIds
parameter_list|()
block|{
return|return
name|volumeIds
return|;
block|}
comment|/**    * Get a list of indexes into the array of {@link VolumeId}s, one per block.    *     * @return list of indexes    */
DECL|method|getVolumeIndexes ()
specifier|public
name|List
argument_list|<
name|Integer
argument_list|>
name|getVolumeIndexes
parameter_list|()
block|{
return|return
name|volumeIndexes
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Metadata for "
operator|+
name|blockIds
operator|.
name|length
operator|+
literal|" blocks in "
operator|+
name|blockPoolId
operator|+
literal|": "
operator|+
name|Joiner
operator|.
name|on
argument_list|(
literal|","
argument_list|)
operator|.
name|join
argument_list|(
name|Longs
operator|.
name|asList
argument_list|(
name|blockIds
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

