begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.io.compress
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|compress
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
comment|/**  * A fake decompressor, just like FakeCompressor  * Its input and output is the same.  */
end_comment

begin_class
DECL|class|FakeDecompressor
class|class
name|FakeDecompressor
implements|implements
name|Decompressor
block|{
DECL|field|finish
specifier|private
name|boolean
name|finish
decl_stmt|;
DECL|field|finished
specifier|private
name|boolean
name|finished
decl_stmt|;
DECL|field|nread
specifier|private
name|int
name|nread
decl_stmt|;
DECL|field|nwrite
specifier|private
name|int
name|nwrite
decl_stmt|;
DECL|field|userBuf
specifier|private
name|byte
index|[]
name|userBuf
decl_stmt|;
DECL|field|userBufOff
specifier|private
name|int
name|userBufOff
decl_stmt|;
DECL|field|userBufLen
specifier|private
name|int
name|userBufLen
decl_stmt|;
annotation|@
name|Override
DECL|method|decompress (byte[] b, int off, int len)
specifier|public
name|int
name|decompress
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
name|int
name|n
init|=
name|Math
operator|.
name|min
argument_list|(
name|len
argument_list|,
name|userBufLen
argument_list|)
decl_stmt|;
if|if
condition|(
name|userBuf
operator|!=
literal|null
operator|&&
name|b
operator|!=
literal|null
condition|)
name|System
operator|.
name|arraycopy
argument_list|(
name|userBuf
argument_list|,
name|userBufOff
argument_list|,
name|b
argument_list|,
name|off
argument_list|,
name|n
argument_list|)
expr_stmt|;
name|userBufOff
operator|+=
name|n
expr_stmt|;
name|userBufLen
operator|-=
name|n
expr_stmt|;
name|nwrite
operator|+=
name|n
expr_stmt|;
if|if
condition|(
name|finish
operator|&&
name|userBufLen
operator|<=
literal|0
condition|)
name|finished
operator|=
literal|true
expr_stmt|;
return|return
name|n
return|;
block|}
annotation|@
name|Override
DECL|method|end ()
specifier|public
name|void
name|end
parameter_list|()
block|{
comment|// nop
block|}
annotation|@
name|Override
DECL|method|finished ()
specifier|public
name|boolean
name|finished
parameter_list|()
block|{
return|return
name|finished
return|;
block|}
DECL|method|getBytesRead ()
specifier|public
name|long
name|getBytesRead
parameter_list|()
block|{
return|return
name|nread
return|;
block|}
DECL|method|getBytesWritten ()
specifier|public
name|long
name|getBytesWritten
parameter_list|()
block|{
return|return
name|nwrite
return|;
block|}
annotation|@
name|Override
DECL|method|needsDictionary ()
specifier|public
name|boolean
name|needsDictionary
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|needsInput ()
specifier|public
name|boolean
name|needsInput
parameter_list|()
block|{
return|return
name|userBufLen
operator|<=
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|reset ()
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|finish
operator|=
literal|false
expr_stmt|;
name|finished
operator|=
literal|false
expr_stmt|;
name|nread
operator|=
literal|0
expr_stmt|;
name|nwrite
operator|=
literal|0
expr_stmt|;
name|userBuf
operator|=
literal|null
expr_stmt|;
name|userBufOff
operator|=
literal|0
expr_stmt|;
name|userBufLen
operator|=
literal|0
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setDictionary (byte[] b, int off, int len)
specifier|public
name|void
name|setDictionary
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
block|{
comment|// nop
block|}
annotation|@
name|Override
DECL|method|setInput (byte[] b, int off, int len)
specifier|public
name|void
name|setInput
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
block|{
name|nread
operator|+=
name|len
expr_stmt|;
name|userBuf
operator|=
name|b
expr_stmt|;
name|userBufOff
operator|=
name|off
expr_stmt|;
name|userBufLen
operator|=
name|len
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getRemaining ()
specifier|public
name|int
name|getRemaining
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
block|}
end_class

end_unit

