begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred.gridmix
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|gridmix
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
name|net
operator|.
name|URI
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
name|conf
operator|.
name|Configuration
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
name|security
operator|.
name|UserGroupInformation
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
comment|/**  * Maps users in the trace to a set of valid target users on the test cluster.  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|interface|UserResolver
specifier|public
interface|interface
name|UserResolver
block|{
comment|/**    * Configure the user map given the URI and configuration. The resolver's    * contract will define how the resource will be interpreted, but the default    * will typically interpret the URI as a {@link org.apache.hadoop.fs.Path}    * listing target users.    * This method should be called only if {@link #needsTargetUsersList()}    * returns true.    * @param userdesc URI from which user information may be loaded per the    * subclass contract.    * @param conf The tool configuration.    * @return true if the resource provided was used in building the list of    * target users    */
DECL|method|setTargetUsers (URI userdesc, Configuration conf)
specifier|public
name|boolean
name|setTargetUsers
parameter_list|(
name|URI
name|userdesc
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Map the given UGI to another per the subclass contract.    * @param ugi User information from the trace.    */
DECL|method|getTargetUgi (UserGroupInformation ugi)
specifier|public
name|UserGroupInformation
name|getTargetUgi
parameter_list|(
name|UserGroupInformation
name|ugi
parameter_list|)
function_decl|;
comment|/**    * Indicates whether this user resolver needs a list of target users to be    * provided.    *    * @return true if a list of target users is to be provided for this    * user resolver    */
DECL|method|needsTargetUsersList ()
specifier|public
name|boolean
name|needsTargetUsersList
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

