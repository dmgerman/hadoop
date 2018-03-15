begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.federation.resolver.order
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
name|federation
operator|.
name|resolver
operator|.
name|order
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
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|resolver
operator|.
name|PathLocation
import|;
end_import

begin_comment
comment|/**  * Policy that decides which should be the first location accessed given  * multiple destinations.  */
end_comment

begin_interface
DECL|interface|OrderedResolver
specifier|public
interface|interface
name|OrderedResolver
block|{
comment|/**    * Get the first namespace based on this resolver approach.    *    * @param path Path to check.    * @param loc Federated location with multiple destinations.    * @return First namespace out of the locations.    */
DECL|method|getFirstNamespace (String path, PathLocation loc)
name|String
name|getFirstNamespace
parameter_list|(
name|String
name|path
parameter_list|,
name|PathLocation
name|loc
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

