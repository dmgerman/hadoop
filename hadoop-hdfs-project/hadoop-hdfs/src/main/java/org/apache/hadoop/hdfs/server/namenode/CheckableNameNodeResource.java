begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|namenode
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

begin_comment
comment|/**  * Implementers of this class represent a NN resource whose availability can be  * checked. A resource can be either "required" or "redundant". All required  * resources must be available for the NN to continue operating. The NN will  * continue to operate as long as *any* redundant resource is available.  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|interface|CheckableNameNodeResource
interface|interface
name|CheckableNameNodeResource
block|{
comment|/**    * Is this resource currently available.     *     * @return true if and only if the resource in question is available.      */
DECL|method|isResourceAvailable ()
specifier|public
name|boolean
name|isResourceAvailable
parameter_list|()
function_decl|;
comment|/**    * Is this resource required.    *     * @return true if and only if the resource in question is required for NN operation.    */
DECL|method|isRequired ()
specifier|public
name|boolean
name|isRequired
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

