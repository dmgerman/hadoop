begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.tools
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|tools
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
name|io
operator|.
name|PrintStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetSocketAddress
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
name|conf
operator|.
name|Configured
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
name|ipc
operator|.
name|RPC
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
name|net
operator|.
name|NetUtils
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
name|util
operator|.
name|Tool
import|;
end_import

begin_comment
comment|/**  * Base class for the HDFS and MR implementations of tools which fetch and  * display the groups that users belong to.  */
end_comment

begin_class
DECL|class|GetGroupsBase
specifier|public
specifier|abstract
class|class
name|GetGroupsBase
extends|extends
name|Configured
implements|implements
name|Tool
block|{
DECL|field|out
specifier|private
name|PrintStream
name|out
decl_stmt|;
comment|/**    * Create an instance of this tool using the given configuration.    * @param conf    */
DECL|method|GetGroupsBase (Configuration conf)
specifier|protected
name|GetGroupsBase
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|this
argument_list|(
name|conf
argument_list|,
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
block|}
comment|/**    * Used exclusively for testing.    *     * @param conf The configuration to use.    * @param out The PrintStream to write to, instead of System.out    */
DECL|method|GetGroupsBase (Configuration conf, PrintStream out)
specifier|protected
name|GetGroupsBase
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|PrintStream
name|out
parameter_list|)
block|{
name|super
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|this
operator|.
name|out
operator|=
name|out
expr_stmt|;
block|}
comment|/**    * Get the groups for the users given and print formatted output to the    * {@link PrintStream} configured earlier.    */
annotation|@
name|Override
DECL|method|run (String[] args)
specifier|public
name|int
name|run
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|args
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|args
operator|=
operator|new
name|String
index|[]
block|{
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getUserName
argument_list|()
block|}
expr_stmt|;
block|}
for|for
control|(
name|String
name|username
range|:
name|args
control|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|username
operator|+
literal|" :"
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|group
range|:
name|getUgmProtocol
argument_list|()
operator|.
name|getGroupsForUser
argument_list|(
name|username
argument_list|)
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
operator|.
name|append
argument_list|(
name|group
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|println
argument_list|(
name|sb
argument_list|)
expr_stmt|;
block|}
return|return
literal|0
return|;
block|}
comment|/**    * Must be overridden by subclasses to get the address where the    * {@link GetUserMappingsProtocol} implementation is running.    *     * @param conf The configuration to use.    * @return The address where the service is listening.    * @throws IOException    */
DECL|method|getProtocolAddress (Configuration conf)
specifier|protected
specifier|abstract
name|InetSocketAddress
name|getProtocolAddress
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get a client of the {@link GetUserMappingsProtocol}.    * @return A {@link GetUserMappingsProtocol} client proxy.    * @throws IOException    */
DECL|method|getUgmProtocol ()
specifier|protected
name|GetUserMappingsProtocol
name|getUgmProtocol
parameter_list|()
throws|throws
name|IOException
block|{
name|GetUserMappingsProtocol
name|userGroupMappingProtocol
init|=
name|RPC
operator|.
name|getProxy
argument_list|(
name|GetUserMappingsProtocol
operator|.
name|class
argument_list|,
name|GetUserMappingsProtocol
operator|.
name|versionID
argument_list|,
name|getProtocolAddress
argument_list|(
name|getConf
argument_list|()
argument_list|)
argument_list|,
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
argument_list|,
name|getConf
argument_list|()
argument_list|,
name|NetUtils
operator|.
name|getSocketFactory
argument_list|(
name|getConf
argument_list|()
argument_list|,
name|GetUserMappingsProtocol
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|userGroupMappingProtocol
return|;
block|}
block|}
end_class

end_unit

