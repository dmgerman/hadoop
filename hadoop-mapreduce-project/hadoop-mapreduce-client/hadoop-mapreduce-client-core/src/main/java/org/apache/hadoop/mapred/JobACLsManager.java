begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
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
name|Map
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
name|mapreduce
operator|.
name|JobACL
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
name|mapreduce
operator|.
name|MRConfig
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
name|AccessControlException
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
name|security
operator|.
name|authorize
operator|.
name|AccessControlList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|JobACLsManager
specifier|public
class|class
name|JobACLsManager
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|JobACLsManager
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|conf
name|Configuration
name|conf
decl_stmt|;
DECL|field|adminAcl
specifier|private
specifier|final
name|AccessControlList
name|adminAcl
decl_stmt|;
DECL|method|JobACLsManager (Configuration conf)
specifier|public
name|JobACLsManager
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|adminAcl
operator|=
operator|new
name|AccessControlList
argument_list|(
name|conf
operator|.
name|get
argument_list|(
name|MRConfig
operator|.
name|MR_ADMINS
argument_list|,
literal|" "
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
block|}
DECL|method|areACLsEnabled ()
specifier|public
name|boolean
name|areACLsEnabled
parameter_list|()
block|{
return|return
name|conf
operator|.
name|getBoolean
argument_list|(
name|MRConfig
operator|.
name|MR_ACLS_ENABLED
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**    * Construct the jobACLs from the configuration so that they can be kept in    * the memory. If authorization is disabled on the JT, nothing is constructed    * and an empty map is returned.    *     * @return JobACL to AccessControlList map.    */
DECL|method|constructJobACLs (Configuration conf)
specifier|public
name|Map
argument_list|<
name|JobACL
argument_list|,
name|AccessControlList
argument_list|>
name|constructJobACLs
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|Map
argument_list|<
name|JobACL
argument_list|,
name|AccessControlList
argument_list|>
name|acls
init|=
operator|new
name|HashMap
argument_list|<
name|JobACL
argument_list|,
name|AccessControlList
argument_list|>
argument_list|()
decl_stmt|;
comment|// Don't construct anything if authorization is disabled.
if|if
condition|(
operator|!
name|areACLsEnabled
argument_list|()
condition|)
block|{
return|return
name|acls
return|;
block|}
for|for
control|(
name|JobACL
name|aclName
range|:
name|JobACL
operator|.
name|values
argument_list|()
control|)
block|{
name|String
name|aclConfigName
init|=
name|aclName
operator|.
name|getAclName
argument_list|()
decl_stmt|;
name|String
name|aclConfigured
init|=
name|conf
operator|.
name|get
argument_list|(
name|aclConfigName
argument_list|)
decl_stmt|;
if|if
condition|(
name|aclConfigured
operator|==
literal|null
condition|)
block|{
comment|// If ACLs are not configured at all, we grant no access to anyone. So
comment|// jobOwner and cluster administrator _only_ can do 'stuff'
name|aclConfigured
operator|=
literal|" "
expr_stmt|;
block|}
name|acls
operator|.
name|put
argument_list|(
name|aclName
argument_list|,
operator|new
name|AccessControlList
argument_list|(
name|aclConfigured
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|acls
return|;
block|}
comment|/**     * Is the calling user an admin for the mapreduce cluster     * i.e. member of mapreduce.cluster.administrators     * @return true, if user is an admin     */
DECL|method|isMRAdmin (UserGroupInformation callerUGI)
name|boolean
name|isMRAdmin
parameter_list|(
name|UserGroupInformation
name|callerUGI
parameter_list|)
block|{
if|if
condition|(
name|adminAcl
operator|.
name|isUserAllowed
argument_list|(
name|callerUGI
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/**    * If authorization is enabled, checks whether the user (in the callerUGI)    * is authorized to perform the operation specified by 'jobOperation' on    * the job by checking if the user is jobOwner or part of job ACL for the    * specific job operation.    *<ul>    *<li>The owner of the job can do any operation on the job</li>    *<li>For all other users/groups job-acls are checked</li>    *</ul>    * @param callerUGI    * @param jobOperation    * @param jobOwner    * @param jobACL    */
DECL|method|checkAccess (UserGroupInformation callerUGI, JobACL jobOperation, String jobOwner, AccessControlList jobACL)
specifier|public
name|boolean
name|checkAccess
parameter_list|(
name|UserGroupInformation
name|callerUGI
parameter_list|,
name|JobACL
name|jobOperation
parameter_list|,
name|String
name|jobOwner
parameter_list|,
name|AccessControlList
name|jobACL
parameter_list|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"checkAccess job acls, jobOwner: "
operator|+
name|jobOwner
operator|+
literal|" jobacl: "
operator|+
name|jobOperation
operator|.
name|toString
argument_list|()
operator|+
literal|" user: "
operator|+
name|callerUGI
operator|.
name|getShortUserName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|String
name|user
init|=
name|callerUGI
operator|.
name|getShortUserName
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|areACLsEnabled
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
comment|// Allow Job-owner for any operation on the job
if|if
condition|(
name|isMRAdmin
argument_list|(
name|callerUGI
argument_list|)
operator|||
name|user
operator|.
name|equals
argument_list|(
name|jobOwner
argument_list|)
operator|||
name|jobACL
operator|.
name|isUserAllowed
argument_list|(
name|callerUGI
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

