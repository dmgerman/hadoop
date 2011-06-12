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
name|java
operator|.
name|io
operator|.
name|DataInput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutput
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
name|io
operator|.
name|Text
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
name|Writable
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
name|WritableUtils
import|;
end_import

begin_comment
comment|/** A class to implement an array of BlockLocations  *  It provide efficient customized serialization/deserialization methods  *  in stead of using the default array (de)serialization provided by RPC  */
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
implements|implements
name|Writable
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
implements|implements
name|Writable
block|{
DECL|field|block
name|Block
name|block
decl_stmt|;
DECL|field|datanodeIDs
name|String
name|datanodeIDs
index|[]
decl_stmt|;
comment|/** default constructor */
DECL|method|BlockWithLocations ()
specifier|public
name|BlockWithLocations
parameter_list|()
block|{
name|block
operator|=
operator|new
name|Block
argument_list|()
expr_stmt|;
name|datanodeIDs
operator|=
literal|null
expr_stmt|;
block|}
comment|/** constructor */
DECL|method|BlockWithLocations (Block b, String[] datanodes)
specifier|public
name|BlockWithLocations
parameter_list|(
name|Block
name|b
parameter_list|,
name|String
index|[]
name|datanodes
parameter_list|)
block|{
name|block
operator|=
name|b
expr_stmt|;
name|datanodeIDs
operator|=
name|datanodes
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
DECL|method|getDatanodes ()
specifier|public
name|String
index|[]
name|getDatanodes
parameter_list|()
block|{
return|return
name|datanodeIDs
return|;
block|}
comment|/** deserialization method */
DECL|method|readFields (DataInput in)
specifier|public
name|void
name|readFields
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|block
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|int
name|len
init|=
name|WritableUtils
operator|.
name|readVInt
argument_list|(
name|in
argument_list|)
decl_stmt|;
comment|// variable length integer
name|datanodeIDs
operator|=
operator|new
name|String
index|[
name|len
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|len
condition|;
name|i
operator|++
control|)
block|{
name|datanodeIDs
index|[
name|i
index|]
operator|=
name|Text
operator|.
name|readString
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** serialization method */
DECL|method|write (DataOutput out)
specifier|public
name|void
name|write
parameter_list|(
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|block
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|WritableUtils
operator|.
name|writeVInt
argument_list|(
name|out
argument_list|,
name|datanodeIDs
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// variable length int
for|for
control|(
name|String
name|id
range|:
name|datanodeIDs
control|)
block|{
name|Text
operator|.
name|writeString
argument_list|(
name|out
argument_list|,
name|id
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|field|blocks
specifier|private
name|BlockWithLocations
index|[]
name|blocks
decl_stmt|;
comment|/** default constructor */
DECL|method|BlocksWithLocations ()
name|BlocksWithLocations
parameter_list|()
block|{   }
comment|/** Constructor with one parameter */
DECL|method|BlocksWithLocations ( BlockWithLocations[] blocks )
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
comment|/** serialization method */
DECL|method|write ( DataOutput out )
specifier|public
name|void
name|write
parameter_list|(
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|WritableUtils
operator|.
name|writeVInt
argument_list|(
name|out
argument_list|,
name|blocks
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|blocks
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|blocks
index|[
name|i
index|]
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** deserialization method */
DECL|method|readFields (DataInput in)
specifier|public
name|void
name|readFields
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|len
init|=
name|WritableUtils
operator|.
name|readVInt
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|blocks
operator|=
operator|new
name|BlockWithLocations
index|[
name|len
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|len
condition|;
name|i
operator|++
control|)
block|{
name|blocks
index|[
name|i
index|]
operator|=
operator|new
name|BlockWithLocations
argument_list|()
expr_stmt|;
name|blocks
index|[
name|i
index|]
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

