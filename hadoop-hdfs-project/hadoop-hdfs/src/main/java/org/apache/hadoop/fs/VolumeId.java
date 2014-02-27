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
comment|/**  * Opaque interface that identifies a disk location. Subclasses  * should implement {@link Comparable} and override both equals and hashCode.  */
end_comment

begin_interface
annotation|@
name|InterfaceStability
operator|.
name|Unstable
annotation|@
name|InterfaceAudience
operator|.
name|Public
DECL|interface|VolumeId
specifier|public
interface|interface
name|VolumeId
extends|extends
name|Comparable
argument_list|<
name|VolumeId
argument_list|>
block|{
annotation|@
name|Override
DECL|method|compareTo (VolumeId arg0)
specifier|abstract
specifier|public
name|int
name|compareTo
parameter_list|(
name|VolumeId
name|arg0
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|hashCode ()
specifier|abstract
specifier|public
name|int
name|hashCode
parameter_list|()
function_decl|;
annotation|@
name|Override
DECL|method|equals (Object obj)
specifier|abstract
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

