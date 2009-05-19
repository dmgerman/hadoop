begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|io
operator|.
name|Writable
import|;
end_import

begin_comment
comment|/** An abstract class representing file checksums for files. */
end_comment

begin_class
DECL|class|FileChecksum
specifier|public
specifier|abstract
class|class
name|FileChecksum
implements|implements
name|Writable
block|{
comment|/** The checksum algorithm name */
DECL|method|getAlgorithmName ()
specifier|public
specifier|abstract
name|String
name|getAlgorithmName
parameter_list|()
function_decl|;
comment|/** The length of the checksum in bytes */
DECL|method|getLength ()
specifier|public
specifier|abstract
name|int
name|getLength
parameter_list|()
function_decl|;
comment|/** The value of the checksum in bytes */
DECL|method|getBytes ()
specifier|public
specifier|abstract
name|byte
index|[]
name|getBytes
parameter_list|()
function_decl|;
comment|/** Return true if both the algorithms and the values are the same. */
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
name|other
operator|==
name|this
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|other
operator|==
literal|null
operator|||
operator|!
operator|(
name|other
operator|instanceof
name|FileChecksum
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
specifier|final
name|FileChecksum
name|that
init|=
operator|(
name|FileChecksum
operator|)
name|other
decl_stmt|;
return|return
name|this
operator|.
name|getAlgorithmName
argument_list|()
operator|.
name|equals
argument_list|(
name|that
operator|.
name|getAlgorithmName
argument_list|()
argument_list|)
operator|&&
name|Arrays
operator|.
name|equals
argument_list|(
name|this
operator|.
name|getBytes
argument_list|()
argument_list|,
name|that
operator|.
name|getBytes
argument_list|()
argument_list|)
return|;
block|}
comment|/** {@inheritDoc} */
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|getAlgorithmName
argument_list|()
operator|.
name|hashCode
argument_list|()
operator|^
name|Arrays
operator|.
name|hashCode
argument_list|(
name|getBytes
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

