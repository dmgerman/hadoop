begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.protocol
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

begin_comment
comment|/**  * Maintains an array of blocks and their corresponding storage IDs.  */
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
DECL|class|BlocksWithLocations
specifier|public
class|class
name|BlocksWithLocations
block|{
comment|/**    * A class to keep track of a block and its locations    */
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|BlockWithLocations
specifier|public
specifier|static
class|class
name|BlockWithLocations
block|{
DECL|field|block
name|Block
name|block
decl_stmt|;
DECL|field|storageIDs
name|String
name|storageIDs
index|[]
decl_stmt|;
comment|/** constructor */
DECL|method|BlockWithLocations (Block block, String[] storageIDs)
specifier|public
name|BlockWithLocations
parameter_list|(
name|Block
name|block
parameter_list|,
name|String
index|[]
name|storageIDs
parameter_list|)
block|{
name|this
operator|.
name|block
operator|=
name|block
expr_stmt|;
name|this
operator|.
name|storageIDs
operator|=
name|storageIDs
expr_stmt|;
block|}
comment|/** get the block */
DECL|method|getBlock ()
specifier|public
name|Block
name|getBlock
parameter_list|()
block|{
return|return
name|block
return|;
block|}
comment|/** get the block's locations */
DECL|method|getStorageIDs ()
specifier|public
name|String
index|[]
name|getStorageIDs
parameter_list|()
block|{
return|return
name|storageIDs
return|;
block|}
block|}
DECL|field|blocks
specifier|private
name|BlockWithLocations
index|[]
name|blocks
decl_stmt|;
comment|/** Constructor with one parameter */
DECL|method|BlocksWithLocations (BlockWithLocations[] blocks)
specifier|public
name|BlocksWithLocations
parameter_list|(
name|BlockWithLocations
index|[]
name|blocks
parameter_list|)
block|{
name|this
operator|.
name|blocks
operator|=
name|blocks
expr_stmt|;
block|}
comment|/** getter */
DECL|method|getBlocks ()
specifier|public
name|BlockWithLocations
index|[]
name|getBlocks
parameter_list|()
block|{
return|return
name|blocks
return|;
block|}
block|}
end_class

end_unit

