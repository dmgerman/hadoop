begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.test.system
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|test
operator|.
name|system
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_comment
comment|/**  *  Its the data container which contains host names and  *  groups against each proxy user.  */
end_comment

begin_class
DECL|class|ProxyUserDefinitions
specifier|public
specifier|abstract
class|class
name|ProxyUserDefinitions
block|{
comment|/**    *  Groups and host names container    */
DECL|class|GroupsAndHost
specifier|public
class|class
name|GroupsAndHost
block|{
DECL|field|groups
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|groups
decl_stmt|;
DECL|field|hosts
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|hosts
decl_stmt|;
DECL|method|getGroups ()
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getGroups
parameter_list|()
block|{
return|return
name|groups
return|;
block|}
DECL|method|setGroups (List<String> groups)
specifier|public
name|void
name|setGroups
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|groups
parameter_list|)
block|{
name|this
operator|.
name|groups
operator|=
name|groups
expr_stmt|;
block|}
DECL|method|getHosts ()
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getHosts
parameter_list|()
block|{
return|return
name|hosts
return|;
block|}
DECL|method|setHosts (List<String> hosts)
specifier|public
name|void
name|setHosts
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|hosts
parameter_list|)
block|{
name|this
operator|.
name|hosts
operator|=
name|hosts
expr_stmt|;
block|}
block|}
DECL|field|proxyUsers
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|GroupsAndHost
argument_list|>
name|proxyUsers
decl_stmt|;
DECL|method|ProxyUserDefinitions ()
specifier|protected
name|ProxyUserDefinitions
parameter_list|()
block|{
name|proxyUsers
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|GroupsAndHost
argument_list|>
argument_list|()
expr_stmt|;
block|}
comment|/**    * Add proxy user data to a container.    * @param userName - proxy user name.    * @param definitions - groups and host names.    */
DECL|method|addProxyUser (String userName, GroupsAndHost definitions)
specifier|public
name|void
name|addProxyUser
parameter_list|(
name|String
name|userName
parameter_list|,
name|GroupsAndHost
name|definitions
parameter_list|)
block|{
name|proxyUsers
operator|.
name|put
argument_list|(
name|userName
argument_list|,
name|definitions
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get the host names and groups against given proxy user.    * @return - GroupsAndHost object.    */
DECL|method|getProxyUser (String userName)
specifier|public
name|GroupsAndHost
name|getProxyUser
parameter_list|(
name|String
name|userName
parameter_list|)
block|{
return|return
name|proxyUsers
operator|.
name|get
argument_list|(
name|userName
argument_list|)
return|;
block|}
comment|/**    * Get the Proxy users data which contains the host names    * and groups against each user.    * @return - the proxy users data as hash map.    */
DECL|method|getProxyUsers ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|GroupsAndHost
argument_list|>
name|getProxyUsers
parameter_list|()
block|{
return|return
name|proxyUsers
return|;
block|}
comment|/**    * The implementation of this method has to be provided by a child of the class    * @param filePath    * @return    * @throws IOException    */
DECL|method|writeToFile (URI filePath)
specifier|public
specifier|abstract
name|boolean
name|writeToFile
parameter_list|(
name|URI
name|filePath
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

