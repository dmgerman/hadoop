begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
package|;
end_package

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
comment|/**  * Byte array backed part handle.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|BBPartHandle
specifier|public
specifier|final
class|class
name|BBPartHandle
implements|implements
name|PartHandle
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|0x23ce3eb1
decl_stmt|;
DECL|field|bytes
specifier|private
specifier|final
name|byte
index|[]
name|bytes
decl_stmt|;
DECL|method|BBPartHandle (ByteBuffer byteBuffer)
specifier|private
name|BBPartHandle
parameter_list|(
name|ByteBuffer
name|byteBuffer
parameter_list|)
block|{
name|this
operator|.
name|bytes
operator|=
name|byteBuffer
operator|.
name|array
argument_list|()
expr_stmt|;
block|}
DECL|method|from (ByteBuffer byteBuffer)
specifier|public
specifier|static
name|PartHandle
name|from
parameter_list|(
name|ByteBuffer
name|byteBuffer
parameter_list|)
block|{
return|return
operator|new
name|BBPartHandle
argument_list|(
name|byteBuffer
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|bytes ()
specifier|public
name|ByteBuffer
name|bytes
parameter_list|()
block|{
return|return
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|bytes
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|hashCode
argument_list|(
name|bytes
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object other)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|other
operator|instanceof
name|PartHandle
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|PartHandle
name|o
init|=
operator|(
name|PartHandle
operator|)
name|other
decl_stmt|;
return|return
name|bytes
argument_list|()
operator|.
name|equals
argument_list|(
name|o
operator|.
name|bytes
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

