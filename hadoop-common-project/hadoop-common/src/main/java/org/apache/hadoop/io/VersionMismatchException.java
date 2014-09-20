begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.io
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
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
comment|/** Thrown by {@link VersionedWritable#readFields(DataInput)} when the  * version of an object being read does not match the current implementation  * version as returned by {@link VersionedWritable#getVersion()}. */
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
DECL|class|VersionMismatchException
specifier|public
class|class
name|VersionMismatchException
extends|extends
name|IOException
block|{
DECL|field|expectedVersion
specifier|private
name|byte
name|expectedVersion
decl_stmt|;
DECL|field|foundVersion
specifier|private
name|byte
name|foundVersion
decl_stmt|;
DECL|method|VersionMismatchException (byte expectedVersionIn, byte foundVersionIn)
specifier|public
name|VersionMismatchException
parameter_list|(
name|byte
name|expectedVersionIn
parameter_list|,
name|byte
name|foundVersionIn
parameter_list|)
block|{
name|expectedVersion
operator|=
name|expectedVersionIn
expr_stmt|;
name|foundVersion
operator|=
name|foundVersionIn
expr_stmt|;
block|}
comment|/** Returns a string representation of this object. */
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"A record version mismatch occurred. Expecting v"
operator|+
name|expectedVersion
operator|+
literal|", found v"
operator|+
name|foundVersion
return|;
block|}
block|}
end_class

end_unit

