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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * For sharing between the local and remote block reader implementations.  */
end_comment

begin_class
DECL|class|BlockReaderUtil
class|class
name|BlockReaderUtil
block|{
comment|/* See {@link BlockReader#readAll(byte[], int, int)} */
DECL|method|readAll (BlockReader reader, byte[] buf, int offset, int len)
specifier|public
specifier|static
name|int
name|readAll
parameter_list|(
name|BlockReader
name|reader
parameter_list|,
name|byte
index|[]
name|buf
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|n
init|=
literal|0
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
name|int
name|nread
init|=
name|reader
operator|.
name|read
argument_list|(
name|buf
argument_list|,
name|offset
operator|+
name|n
argument_list|,
name|len
operator|-
name|n
argument_list|)
decl_stmt|;
if|if
condition|(
name|nread
operator|<=
literal|0
condition|)
return|return
operator|(
name|n
operator|==
literal|0
operator|)
condition|?
name|nread
else|:
name|n
return|;
name|n
operator|+=
name|nread
expr_stmt|;
if|if
condition|(
name|n
operator|>=
name|len
condition|)
return|return
name|n
return|;
block|}
block|}
comment|/* See {@link BlockReader#readFully(byte[], int, int)} */
DECL|method|readFully (BlockReader reader, byte[] buf, int off, int len)
specifier|public
specifier|static
name|void
name|readFully
parameter_list|(
name|BlockReader
name|reader
parameter_list|,
name|byte
index|[]
name|buf
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|toRead
init|=
name|len
decl_stmt|;
while|while
condition|(
name|toRead
operator|>
literal|0
condition|)
block|{
name|int
name|ret
init|=
name|reader
operator|.
name|read
argument_list|(
name|buf
argument_list|,
name|off
argument_list|,
name|toRead
argument_list|)
decl_stmt|;
if|if
condition|(
name|ret
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Premature EOF from inputStream"
argument_list|)
throw|;
block|}
name|toRead
operator|-=
name|ret
expr_stmt|;
name|off
operator|+=
name|ret
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

