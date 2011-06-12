begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|util
package|;
end_package

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

begin_comment
comment|/**   * Wrapper for byte[] to use byte[] as key in HashMap  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|ByteArray
specifier|public
class|class
name|ByteArray
block|{
DECL|field|hash
specifier|private
name|int
name|hash
init|=
literal|0
decl_stmt|;
comment|// cache the hash code
DECL|field|bytes
specifier|private
specifier|final
name|byte
index|[]
name|bytes
decl_stmt|;
DECL|method|ByteArray (byte[] bytes)
specifier|public
name|ByteArray
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|)
block|{
name|this
operator|.
name|bytes
operator|=
name|bytes
expr_stmt|;
block|}
DECL|method|getBytes ()
specifier|public
name|byte
index|[]
name|getBytes
parameter_list|()
block|{
return|return
name|bytes
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
if|if
condition|(
name|hash
operator|==
literal|0
condition|)
block|{
name|hash
operator|=
name|Arrays
operator|.
name|hashCode
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
block|}
return|return
name|hash
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object o)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|ByteArray
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|Arrays
operator|.
name|equals
argument_list|(
name|bytes
argument_list|,
operator|(
operator|(
name|ByteArray
operator|)
name|o
operator|)
operator|.
name|bytes
argument_list|)
return|;
block|}
block|}
end_class

end_unit

