begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.protocol.datatransfer
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
operator|.
name|datatransfer
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

begin_comment
comment|/** Operation */
end_comment

begin_enum
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|enum|Op
specifier|public
enum|enum
name|Op
block|{
DECL|enumConstant|WRITE_BLOCK
name|WRITE_BLOCK
argument_list|(
operator|(
name|byte
operator|)
literal|80
argument_list|)
block|,
DECL|enumConstant|READ_BLOCK
name|READ_BLOCK
argument_list|(
operator|(
name|byte
operator|)
literal|81
argument_list|)
block|,
DECL|enumConstant|READ_METADATA
name|READ_METADATA
argument_list|(
operator|(
name|byte
operator|)
literal|82
argument_list|)
block|,
DECL|enumConstant|REPLACE_BLOCK
name|REPLACE_BLOCK
argument_list|(
operator|(
name|byte
operator|)
literal|83
argument_list|)
block|,
DECL|enumConstant|COPY_BLOCK
name|COPY_BLOCK
argument_list|(
operator|(
name|byte
operator|)
literal|84
argument_list|)
block|,
DECL|enumConstant|BLOCK_CHECKSUM
name|BLOCK_CHECKSUM
argument_list|(
operator|(
name|byte
operator|)
literal|85
argument_list|)
block|,
DECL|enumConstant|TRANSFER_BLOCK
name|TRANSFER_BLOCK
argument_list|(
operator|(
name|byte
operator|)
literal|86
argument_list|)
block|,
DECL|enumConstant|REQUEST_SHORT_CIRCUIT_FDS
name|REQUEST_SHORT_CIRCUIT_FDS
argument_list|(
operator|(
name|byte
operator|)
literal|87
argument_list|)
block|,
DECL|enumConstant|RELEASE_SHORT_CIRCUIT_FDS
name|RELEASE_SHORT_CIRCUIT_FDS
argument_list|(
operator|(
name|byte
operator|)
literal|88
argument_list|)
block|,
DECL|enumConstant|REQUEST_SHORT_CIRCUIT_SHM
name|REQUEST_SHORT_CIRCUIT_SHM
argument_list|(
operator|(
name|byte
operator|)
literal|89
argument_list|)
block|,
DECL|enumConstant|CUSTOM
name|CUSTOM
argument_list|(
operator|(
name|byte
operator|)
literal|127
argument_list|)
block|;
comment|/** The code for this operation. */
DECL|field|code
specifier|public
specifier|final
name|byte
name|code
decl_stmt|;
DECL|method|Op (byte code)
specifier|private
name|Op
parameter_list|(
name|byte
name|code
parameter_list|)
block|{
name|this
operator|.
name|code
operator|=
name|code
expr_stmt|;
block|}
DECL|field|FIRST_CODE
specifier|private
specifier|static
specifier|final
name|int
name|FIRST_CODE
init|=
name|values
argument_list|()
index|[
literal|0
index|]
operator|.
name|code
decl_stmt|;
comment|/** Return the object represented by the code. */
DECL|method|valueOf (byte code)
specifier|private
specifier|static
name|Op
name|valueOf
parameter_list|(
name|byte
name|code
parameter_list|)
block|{
specifier|final
name|int
name|i
init|=
operator|(
name|code
operator|&
literal|0xff
operator|)
operator|-
name|FIRST_CODE
decl_stmt|;
return|return
name|i
operator|<
literal|0
operator|||
name|i
operator|>=
name|values
argument_list|()
operator|.
name|length
condition|?
literal|null
else|:
name|values
argument_list|()
index|[
name|i
index|]
return|;
block|}
comment|/** Read from in */
DECL|method|read (DataInput in)
specifier|public
specifier|static
name|Op
name|read
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|valueOf
argument_list|(
name|in
operator|.
name|readByte
argument_list|()
argument_list|)
return|;
block|}
comment|/** Write to out */
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
name|out
operator|.
name|write
argument_list|(
name|code
argument_list|)
expr_stmt|;
block|}
block|}
end_enum

end_unit

