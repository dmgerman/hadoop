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

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
import|;
end_import

begin_comment
comment|/**  * This class is a common place for NN configuration.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|NNConf
specifier|final
class|class
name|NNConf
block|{
comment|/**    * Support for ACLs is controlled by a configuration flag. If the     * configuration flag is false, then the NameNode will reject all     * ACL-related operations.    */
DECL|field|aclsEnabled
specifier|private
specifier|final
name|boolean
name|aclsEnabled
decl_stmt|;
comment|/**    * Support for XAttrs is controlled by a configuration flag. If the     * configuration flag is false, then the NameNode will reject all     * XAttr-related operations.    */
DECL|field|xattrsEnabled
specifier|private
specifier|final
name|boolean
name|xattrsEnabled
decl_stmt|;
comment|/**    * Maximum size of a single name-value extended attribute.    */
DECL|field|xattrMaxSize
specifier|final
name|int
name|xattrMaxSize
decl_stmt|;
comment|/**    * Creates a new NNConf from configuration.    *    * @param conf Configuration to check    */
DECL|method|NNConf (Configuration conf)
specifier|public
name|NNConf
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|aclsEnabled
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
name|NNConf
operator|.
name|class
argument_list|)
operator|.
name|info
argument_list|(
literal|"ACLs enabled? "
operator|+
name|aclsEnabled
argument_list|)
expr_stmt|;
name|xattrsEnabled
operator|=
name|conf
operator|.
name|getBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_XATTRS_ENABLED_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_XATTRS_ENABLED_DEFAULT
argument_list|)
expr_stmt|;
name|LogFactory
operator|.
name|getLog
argument_list|(
name|NNConf
operator|.
name|class
argument_list|)
operator|.
name|info
argument_list|(
literal|"XAttrs enabled? "
operator|+
name|xattrsEnabled
argument_list|)
expr_stmt|;
name|xattrMaxSize
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_MAX_XATTR_SIZE_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_MAX_XATTR_SIZE_DEFAULT
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|xattrMaxSize
operator|>=
literal|0
argument_list|,
literal|"Cannot set a negative value for the maximum size of an xattr (%s)."
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_MAX_XATTR_SIZE_KEY
argument_list|)
expr_stmt|;
specifier|final
name|String
name|unlimited
init|=
name|xattrMaxSize
operator|==
literal|0
condition|?
literal|" (unlimited)"
else|:
literal|""
decl_stmt|;
name|LogFactory
operator|.
name|getLog
argument_list|(
name|NNConf
operator|.
name|class
argument_list|)
operator|.
name|info
argument_list|(
literal|"Maximum size of an xattr: "
operator|+
name|xattrMaxSize
operator|+
name|unlimited
argument_list|)
expr_stmt|;
block|}
comment|/**    * Checks the flag on behalf of an ACL API call.    *    * @throws AclException if ACLs are disabled    */
DECL|method|checkAclsConfigFlag ()
specifier|public
name|void
name|checkAclsConfigFlag
parameter_list|()
throws|throws
name|AclException
block|{
if|if
condition|(
operator|!
name|aclsEnabled
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
literal|"The ACL operation has been rejected.  "
operator|+
literal|"Support for ACLs has been disabled by setting %s to false."
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_ACLS_ENABLED_KEY
argument_list|)
argument_list|)
throw|;
block|}
block|}
comment|/**    * Checks the flag on behalf of an XAttr API call.    * @throws IOException if XAttrs are disabled    */
DECL|method|checkXAttrsConfigFlag ()
specifier|public
name|void
name|checkXAttrsConfigFlag
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|xattrsEnabled
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"The XAttr operation has been rejected.  "
operator|+
literal|"Support for XAttrs has been disabled by setting %s to false."
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_XATTRS_ENABLED_KEY
argument_list|)
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

