begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
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
name|fs
operator|.
name|FSDataInputStream
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
name|HdfsConstants
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
name|Random
import|;
end_import

begin_class
DECL|class|StripedFileTestUtil
specifier|public
class|class
name|StripedFileTestUtil
block|{
DECL|field|dataBlocks
specifier|static
name|int
name|dataBlocks
init|=
name|HdfsConstants
operator|.
name|NUM_DATA_BLOCKS
decl_stmt|;
DECL|field|parityBlocks
specifier|static
name|int
name|parityBlocks
init|=
name|HdfsConstants
operator|.
name|NUM_PARITY_BLOCKS
decl_stmt|;
DECL|field|cellSize
specifier|static
specifier|final
name|int
name|cellSize
init|=
name|HdfsConstants
operator|.
name|BLOCK_STRIPED_CELL_SIZE
decl_stmt|;
DECL|field|stripesPerBlock
specifier|static
specifier|final
name|int
name|stripesPerBlock
init|=
literal|4
decl_stmt|;
DECL|field|blockSize
specifier|static
specifier|final
name|int
name|blockSize
init|=
name|cellSize
operator|*
name|stripesPerBlock
decl_stmt|;
DECL|field|numDNs
specifier|static
specifier|final
name|int
name|numDNs
init|=
name|dataBlocks
operator|+
name|parityBlocks
operator|+
literal|2
decl_stmt|;
DECL|field|random
specifier|static
specifier|final
name|Random
name|random
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
DECL|method|generateBytes (int cnt)
specifier|static
name|byte
index|[]
name|generateBytes
parameter_list|(
name|int
name|cnt
parameter_list|)
block|{
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
name|cnt
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
name|cnt
condition|;
name|i
operator|++
control|)
block|{
name|bytes
index|[
name|i
index|]
operator|=
name|getByte
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
return|return
name|bytes
return|;
block|}
DECL|method|readAll (FSDataInputStream in, byte[] buf)
specifier|static
name|int
name|readAll
parameter_list|(
name|FSDataInputStream
name|in
parameter_list|,
name|byte
index|[]
name|buf
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|readLen
init|=
literal|0
decl_stmt|;
name|int
name|ret
decl_stmt|;
while|while
condition|(
operator|(
name|ret
operator|=
name|in
operator|.
name|read
argument_list|(
name|buf
argument_list|,
name|readLen
argument_list|,
name|buf
operator|.
name|length
operator|-
name|readLen
argument_list|)
operator|)
operator|>=
literal|0
operator|&&
name|readLen
operator|<=
name|buf
operator|.
name|length
condition|)
block|{
name|readLen
operator|+=
name|ret
expr_stmt|;
block|}
return|return
name|readLen
return|;
block|}
DECL|method|getByte (long pos)
specifier|static
name|byte
name|getByte
parameter_list|(
name|long
name|pos
parameter_list|)
block|{
specifier|final
name|int
name|mod
init|=
literal|29
decl_stmt|;
return|return
call|(
name|byte
call|)
argument_list|(
name|pos
operator|%
name|mod
operator|+
literal|1
argument_list|)
return|;
block|}
block|}
end_class

end_unit

