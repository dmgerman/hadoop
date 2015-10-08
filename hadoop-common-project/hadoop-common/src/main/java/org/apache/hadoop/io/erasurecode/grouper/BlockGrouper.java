begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.io.erasurecode.grouper
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|erasurecode
operator|.
name|grouper
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
name|io
operator|.
name|erasurecode
operator|.
name|ECBlock
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
name|erasurecode
operator|.
name|ECBlockGroup
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
name|erasurecode
operator|.
name|ECSchema
import|;
end_import

begin_comment
comment|/**  * As part of a codec, to handle how to form a block group for encoding  * and provide instructions on how to recover erased blocks from a block group  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|BlockGrouper
specifier|public
class|class
name|BlockGrouper
block|{
DECL|field|schema
specifier|private
name|ECSchema
name|schema
decl_stmt|;
comment|/**    * Set EC schema.    * @param schema    */
DECL|method|setSchema (ECSchema schema)
specifier|public
name|void
name|setSchema
parameter_list|(
name|ECSchema
name|schema
parameter_list|)
block|{
name|this
operator|.
name|schema
operator|=
name|schema
expr_stmt|;
block|}
comment|/**    * Get EC schema.    * @return    */
DECL|method|getSchema ()
specifier|protected
name|ECSchema
name|getSchema
parameter_list|()
block|{
return|return
name|schema
return|;
block|}
comment|/**    * Get required data blocks count in a BlockGroup.    * @return count of required data blocks    */
DECL|method|getRequiredNumDataBlocks ()
specifier|public
name|int
name|getRequiredNumDataBlocks
parameter_list|()
block|{
return|return
name|schema
operator|.
name|getNumDataUnits
argument_list|()
return|;
block|}
comment|/**    * Get required parity blocks count in a BlockGroup.    * @return count of required parity blocks    */
DECL|method|getRequiredNumParityBlocks ()
specifier|public
name|int
name|getRequiredNumParityBlocks
parameter_list|()
block|{
return|return
name|schema
operator|.
name|getNumParityUnits
argument_list|()
return|;
block|}
comment|/**    * Calculating and organizing BlockGroup, to be called by ECManager    * @param dataBlocks Data blocks to compute parity blocks against    * @param parityBlocks To be computed parity blocks    * @return    */
DECL|method|makeBlockGroup (ECBlock[] dataBlocks, ECBlock[] parityBlocks)
specifier|public
name|ECBlockGroup
name|makeBlockGroup
parameter_list|(
name|ECBlock
index|[]
name|dataBlocks
parameter_list|,
name|ECBlock
index|[]
name|parityBlocks
parameter_list|)
block|{
name|ECBlockGroup
name|blockGroup
init|=
operator|new
name|ECBlockGroup
argument_list|(
name|dataBlocks
argument_list|,
name|parityBlocks
argument_list|)
decl_stmt|;
return|return
name|blockGroup
return|;
block|}
comment|/**    * Given a BlockGroup, tell if any of the missing blocks can be recovered,    * to be called by ECManager    * @param blockGroup a blockGroup that may contain erased blocks but not sure    *                   recoverable or not    * @return true if any erased block recoverable, false otherwise    */
DECL|method|anyRecoverable (ECBlockGroup blockGroup)
specifier|public
name|boolean
name|anyRecoverable
parameter_list|(
name|ECBlockGroup
name|blockGroup
parameter_list|)
block|{
name|int
name|erasedCount
init|=
name|blockGroup
operator|.
name|getErasedCount
argument_list|()
decl_stmt|;
return|return
name|erasedCount
operator|>
literal|0
operator|&&
name|erasedCount
operator|<=
name|getRequiredNumParityBlocks
argument_list|()
return|;
block|}
block|}
end_class

end_unit

