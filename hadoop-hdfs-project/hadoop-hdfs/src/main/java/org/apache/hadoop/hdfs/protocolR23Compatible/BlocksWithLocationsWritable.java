begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.protocolR23Compatible
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocolR23Compatible
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
name|server
operator|.
name|protocol
operator|.
name|BlocksWithLocations
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
name|protocol
operator|.
name|BlocksWithLocations
operator|.
name|BlockWithLocations
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
DECL|class|BlocksWithLocationsWritable
specifier|public
class|class
name|BlocksWithLocationsWritable
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
DECL|class|BlockWithLocationsWritable
specifier|public
specifier|static
class|class
name|BlockWithLocationsWritable
implements|implements
name|Writable
block|{
DECL|field|block
specifier|private
name|BlockWritable
name|block
decl_stmt|;
DECL|field|datanodeIDs
specifier|private
name|String
name|datanodeIDs
index|[]
decl_stmt|;
comment|/** default constructor */
DECL|method|BlockWithLocationsWritable ()
specifier|public
name|BlockWithLocationsWritable
parameter_list|()
block|{
name|block
operator|=
operator|new
name|BlockWritable
argument_list|()
expr_stmt|;
name|datanodeIDs
operator|=
literal|null
expr_stmt|;
block|}
comment|/** constructor */
DECL|method|BlockWithLocationsWritable (BlockWritable b, String[] datanodes)
specifier|public
name|BlockWithLocationsWritable
parameter_list|(
name|BlockWritable
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
name|BlockWithLocationsWritable
index|[]
name|blocks
decl_stmt|;
comment|/** default constructor */
DECL|method|BlocksWithLocationsWritable ()
name|BlocksWithLocationsWritable
parameter_list|()
block|{   }
comment|/** Constructor with one parameter */
DECL|method|BlocksWithLocationsWritable ( BlockWithLocationsWritable[] blocks )
specifier|public
name|BlocksWithLocationsWritable
parameter_list|(
name|BlockWithLocationsWritable
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
name|BlockWithLocationsWritable
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
name|BlockWithLocationsWritable
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
DECL|method|convert (BlocksWithLocations locs)
specifier|public
specifier|static
name|BlocksWithLocationsWritable
name|convert
parameter_list|(
name|BlocksWithLocations
name|locs
parameter_list|)
block|{
name|BlockWithLocations
index|[]
name|blocks
init|=
name|locs
operator|.
name|getBlocks
argument_list|()
decl_stmt|;
name|BlockWithLocationsWritable
index|[]
name|blocksWritable
init|=
operator|new
name|BlockWithLocationsWritable
index|[
name|blocks
operator|.
name|length
index|]
decl_stmt|;
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
name|blocksWritable
index|[
name|i
index|]
operator|=
operator|new
name|BlockWithLocationsWritable
argument_list|(
name|BlockWritable
operator|.
name|convert
argument_list|(
name|blocks
index|[
name|i
index|]
operator|.
name|getBlock
argument_list|()
argument_list|)
argument_list|,
name|blocks
index|[
name|i
index|]
operator|.
name|getDatanodes
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|BlocksWithLocationsWritable
argument_list|(
name|blocksWritable
argument_list|)
return|;
block|}
DECL|method|convert ()
specifier|public
name|BlocksWithLocations
name|convert
parameter_list|()
block|{
name|BlockWithLocations
index|[]
name|locs
init|=
operator|new
name|BlockWithLocations
index|[
name|blocks
operator|.
name|length
index|]
decl_stmt|;
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
name|locs
index|[
name|i
index|]
operator|=
operator|new
name|BlockWithLocations
argument_list|(
name|blocks
index|[
name|i
index|]
operator|.
name|block
operator|.
name|convert
argument_list|()
argument_list|,
name|blocks
index|[
name|i
index|]
operator|.
name|datanodeIDs
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|BlocksWithLocations
argument_list|(
name|locs
argument_list|)
return|;
block|}
block|}
end_class

end_unit

