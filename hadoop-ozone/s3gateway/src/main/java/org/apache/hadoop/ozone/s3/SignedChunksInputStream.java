begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  *  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.s3
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|s3
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_comment
comment|/**  * Input stream implementation to read body with chunked signatures.  *<p>  * see: https://docs.aws.amazon.com/AmazonS3/latest/API/sigv4-streaming.html  */
end_comment

begin_class
DECL|class|SignedChunksInputStream
specifier|public
class|class
name|SignedChunksInputStream
extends|extends
name|InputStream
block|{
DECL|field|signatureLinePattern
specifier|private
name|Pattern
name|signatureLinePattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"([0-9A-Fa-f]+);chunk-signature=.*"
argument_list|)
decl_stmt|;
DECL|field|originalStream
specifier|private
name|InputStream
name|originalStream
decl_stmt|;
comment|/**    * Numer of following databits. If zero, the signature line should be parsed.    */
DECL|field|remainingData
specifier|private
name|int
name|remainingData
init|=
literal|0
decl_stmt|;
DECL|method|SignedChunksInputStream (InputStream inputStream)
specifier|public
name|SignedChunksInputStream
parameter_list|(
name|InputStream
name|inputStream
parameter_list|)
block|{
name|originalStream
operator|=
name|inputStream
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|read ()
specifier|public
name|int
name|read
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|remainingData
operator|>
literal|0
condition|)
block|{
name|int
name|curr
init|=
name|originalStream
operator|.
name|read
argument_list|()
decl_stmt|;
name|remainingData
operator|--
expr_stmt|;
if|if
condition|(
name|remainingData
operator|==
literal|0
condition|)
block|{
comment|//read the "\r\n" at the end of the data section
name|originalStream
operator|.
name|read
argument_list|()
expr_stmt|;
name|originalStream
operator|.
name|read
argument_list|()
expr_stmt|;
block|}
return|return
name|curr
return|;
block|}
else|else
block|{
name|remainingData
operator|=
name|readHeader
argument_list|()
expr_stmt|;
if|if
condition|(
name|remainingData
operator|==
operator|-
literal|1
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
return|return
name|read
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|read (byte[] b, int off, int len)
specifier|public
name|int
name|read
parameter_list|(
name|byte
index|[]
name|b
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
if|if
condition|(
name|b
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|()
throw|;
block|}
elseif|else
if|if
condition|(
name|off
operator|<
literal|0
operator|||
name|len
argument_list|<
literal|0
operator|||
name|len
argument_list|>
name|b
operator|.
name|length
operator|-
name|off
condition|)
block|{
throw|throw
operator|new
name|IndexOutOfBoundsException
argument_list|()
throw|;
block|}
elseif|else
if|if
condition|(
name|len
operator|==
literal|0
condition|)
block|{
return|return
literal|0
return|;
block|}
name|int
name|currentOff
init|=
name|off
decl_stmt|;
name|int
name|currentLen
init|=
name|len
decl_stmt|;
name|int
name|totalReadBytes
init|=
literal|0
decl_stmt|;
name|int
name|realReadLen
init|=
literal|0
decl_stmt|;
name|int
name|maxReadLen
init|=
literal|0
decl_stmt|;
do|do
block|{
if|if
condition|(
name|remainingData
operator|>
literal|0
condition|)
block|{
name|maxReadLen
operator|=
name|Math
operator|.
name|min
argument_list|(
name|remainingData
argument_list|,
name|currentLen
argument_list|)
expr_stmt|;
name|realReadLen
operator|=
name|originalStream
operator|.
name|read
argument_list|(
name|b
argument_list|,
name|currentOff
argument_list|,
name|maxReadLen
argument_list|)
expr_stmt|;
if|if
condition|(
name|realReadLen
operator|==
operator|-
literal|1
condition|)
block|{
break|break;
block|}
name|currentOff
operator|+=
name|realReadLen
expr_stmt|;
name|currentLen
operator|-=
name|realReadLen
expr_stmt|;
name|totalReadBytes
operator|+=
name|realReadLen
expr_stmt|;
name|remainingData
operator|-=
name|realReadLen
expr_stmt|;
if|if
condition|(
name|remainingData
operator|==
literal|0
condition|)
block|{
comment|//read the "\r\n" at the end of the data section
name|originalStream
operator|.
name|read
argument_list|()
expr_stmt|;
name|originalStream
operator|.
name|read
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
name|remainingData
operator|=
name|readHeader
argument_list|()
expr_stmt|;
if|if
condition|(
name|remainingData
operator|==
operator|-
literal|1
condition|)
block|{
break|break;
block|}
block|}
block|}
do|while
condition|(
name|currentLen
operator|>
literal|0
condition|)
do|;
return|return
name|totalReadBytes
operator|>
literal|0
condition|?
name|totalReadBytes
else|:
operator|-
literal|1
return|;
block|}
DECL|method|readHeader ()
specifier|private
name|int
name|readHeader
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|prev
init|=
operator|-
literal|1
decl_stmt|;
name|int
name|curr
init|=
literal|0
decl_stmt|;
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
comment|//read everything until the next \r\n
while|while
condition|(
operator|!
name|eol
argument_list|(
name|prev
argument_list|,
name|curr
argument_list|)
operator|&&
name|curr
operator|!=
operator|-
literal|1
condition|)
block|{
name|int
name|next
init|=
name|originalStream
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|next
operator|!=
operator|-
literal|1
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
operator|(
name|char
operator|)
name|next
argument_list|)
expr_stmt|;
block|}
name|prev
operator|=
name|curr
expr_stmt|;
name|curr
operator|=
name|next
expr_stmt|;
block|}
name|String
name|signatureLine
init|=
name|buf
operator|.
name|toString
argument_list|()
operator|.
name|trim
argument_list|()
decl_stmt|;
if|if
condition|(
name|signatureLine
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
comment|//parse the data length.
name|Matcher
name|matcher
init|=
name|signatureLinePattern
operator|.
name|matcher
argument_list|(
name|signatureLine
argument_list|)
decl_stmt|;
if|if
condition|(
name|matcher
operator|.
name|matches
argument_list|()
condition|)
block|{
return|return
name|Integer
operator|.
name|parseInt
argument_list|(
name|matcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|16
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Invalid signature line: "
operator|+
name|signatureLine
argument_list|)
throw|;
block|}
block|}
DECL|method|eol (int prev, int curr)
specifier|private
name|boolean
name|eol
parameter_list|(
name|int
name|prev
parameter_list|,
name|int
name|curr
parameter_list|)
block|{
return|return
name|prev
operator|==
literal|13
operator|&&
name|curr
operator|==
literal|10
return|;
block|}
block|}
end_class

end_unit

