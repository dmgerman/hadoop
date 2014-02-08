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
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|hdfs
operator|.
name|DFSConfigKeys
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
name|hdfs
operator|.
name|protocol
operator|.
name|AclException
import|;
end_import

begin_comment
comment|/**  * Support for ACLs is controlled by a configuration flag.  If the configuration  * flag is false, then the NameNode will reject all ACL-related operations and  * refuse to load an fsimage or edit log containing ACLs.  */
end_comment

begin_class
DECL|class|AclConfigFlag
specifier|final
class|class
name|AclConfigFlag
block|{
DECL|field|enabled
specifier|private
specifier|final
name|boolean
name|enabled
decl_stmt|;
comment|/**    * Creates a new AclConfigFlag from configuration.    *    * @param conf Configuration to check    */
DECL|method|AclConfigFlag (Configuration conf)
specifier|public
name|AclConfigFlag
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|enabled
operator|=
name|conf
operator|.
name|getBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_ACLS_ENABLED_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_ACLS_ENABLED_DEFAULT
argument_list|)
expr_stmt|;
name|LogFactory
operator|.
name|getLog
argument_list|(
name|AclConfigFlag
operator|.
name|class
argument_list|)
operator|.
name|info
argument_list|(
literal|"ACLs enabled? "
operator|+
name|enabled
argument_list|)
expr_stmt|;
block|}
comment|/**    * Checks the flag on behalf of an ACL API call.    *    * @throws AclException if ACLs are disabled    */
DECL|method|checkForApiCall ()
specifier|public
name|void
name|checkForApiCall
parameter_list|()
throws|throws
name|AclException
block|{
name|check
argument_list|(
literal|"The ACL operation has been rejected."
argument_list|)
expr_stmt|;
block|}
comment|/**    * Checks the flag on behalf of edit log loading.    *    * @throws AclException if ACLs are disabled    */
DECL|method|checkForEditLog ()
specifier|public
name|void
name|checkForEditLog
parameter_list|()
throws|throws
name|AclException
block|{
name|check
argument_list|(
literal|"Cannot load edit log containing an ACL."
argument_list|)
expr_stmt|;
block|}
comment|/**    * Checks the flag on behalf of fsimage loading.    *    * @throws AclException if ACLs are disabled    */
DECL|method|checkForFsImage ()
specifier|public
name|void
name|checkForFsImage
parameter_list|()
throws|throws
name|AclException
block|{
name|check
argument_list|(
literal|"Cannot load fsimage containing an ACL."
argument_list|)
expr_stmt|;
block|}
comment|/**    * Common check method.    *    * @throws AclException if ACLs are disabled    */
DECL|method|check (String reason)
specifier|private
name|void
name|check
parameter_list|(
name|String
name|reason
parameter_list|)
throws|throws
name|AclException
block|{
if|if
condition|(
operator|!
name|enabled
condition|)
block|{
throw|throw
operator|new
name|AclException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%s  Support for ACLs has been disabled by setting %s to false."
argument_list|,
name|reason
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_ACLS_ENABLED_KEY
argument_list|)
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

