begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.lib.join
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|lib
operator|.
name|join
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutputStream
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
name|io
operator|.
name|Writable
import|;
end_import

begin_comment
comment|/**  * This class provides an implementation of ResetableIterator. This  * implementation uses a byte array to store elements added to it.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|class|StreamBackedIterator
specifier|public
class|class
name|StreamBackedIterator
parameter_list|<
name|X
extends|extends
name|Writable
parameter_list|>
implements|implements
name|ResetableIterator
argument_list|<
name|X
argument_list|>
block|{
DECL|class|ReplayableByteInputStream
specifier|private
specifier|static
class|class
name|ReplayableByteInputStream
extends|extends
name|ByteArrayInputStream
block|{
DECL|method|ReplayableByteInputStream (byte[] arr)
specifier|public
name|ReplayableByteInputStream
parameter_list|(
name|byte
index|[]
name|arr
parameter_list|)
block|{
name|super
argument_list|(
name|arr
argument_list|)
expr_stmt|;
block|}
DECL|method|resetStream ()
specifier|public
name|void
name|resetStream
parameter_list|()
block|{
name|mark
operator|=
literal|0
expr_stmt|;
name|reset
argument_list|()
expr_stmt|;
block|}
block|}
DECL|field|outbuf
specifier|private
name|ByteArrayOutputStream
name|outbuf
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
DECL|field|outfbuf
specifier|private
name|DataOutputStream
name|outfbuf
init|=
operator|new
name|DataOutputStream
argument_list|(
name|outbuf
argument_list|)
decl_stmt|;
DECL|field|inbuf
specifier|private
name|ReplayableByteInputStream
name|inbuf
decl_stmt|;
DECL|field|infbuf
specifier|private
name|DataInputStream
name|infbuf
decl_stmt|;
DECL|method|StreamBackedIterator ()
specifier|public
name|StreamBackedIterator
parameter_list|()
block|{ }
DECL|method|hasNext ()
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|infbuf
operator|!=
literal|null
operator|&&
name|inbuf
operator|.
name|available
argument_list|()
operator|>
literal|0
return|;
block|}
DECL|method|next (X val)
specifier|public
name|boolean
name|next
parameter_list|(
name|X
name|val
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|hasNext
argument_list|()
condition|)
block|{
name|inbuf
operator|.
name|mark
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|val
operator|.
name|readFields
argument_list|(
name|infbuf
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
DECL|method|replay (X val)
specifier|public
name|boolean
name|replay
parameter_list|(
name|X
name|val
parameter_list|)
throws|throws
name|IOException
block|{
name|inbuf
operator|.
name|reset
argument_list|()
expr_stmt|;
if|if
condition|(
literal|0
operator|==
name|inbuf
operator|.
name|available
argument_list|()
condition|)
return|return
literal|false
return|;
name|val
operator|.
name|readFields
argument_list|(
name|infbuf
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
DECL|method|reset ()
specifier|public
name|void
name|reset
parameter_list|()
block|{
if|if
condition|(
literal|null
operator|!=
name|outfbuf
condition|)
block|{
name|inbuf
operator|=
operator|new
name|ReplayableByteInputStream
argument_list|(
name|outbuf
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
name|infbuf
operator|=
operator|new
name|DataInputStream
argument_list|(
name|inbuf
argument_list|)
expr_stmt|;
name|outfbuf
operator|=
literal|null
expr_stmt|;
block|}
name|inbuf
operator|.
name|resetStream
argument_list|()
expr_stmt|;
block|}
DECL|method|add (X item)
specifier|public
name|void
name|add
parameter_list|(
name|X
name|item
parameter_list|)
throws|throws
name|IOException
block|{
name|item
operator|.
name|write
argument_list|(
name|outfbuf
argument_list|)
expr_stmt|;
block|}
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
literal|null
operator|!=
name|infbuf
condition|)
name|infbuf
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
literal|null
operator|!=
name|outfbuf
condition|)
name|outfbuf
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|clear ()
specifier|public
name|void
name|clear
parameter_list|()
block|{
if|if
condition|(
literal|null
operator|!=
name|inbuf
condition|)
name|inbuf
operator|.
name|resetStream
argument_list|()
expr_stmt|;
name|outbuf
operator|.
name|reset
argument_list|()
expr_stmt|;
name|outfbuf
operator|=
operator|new
name|DataOutputStream
argument_list|(
name|outbuf
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

