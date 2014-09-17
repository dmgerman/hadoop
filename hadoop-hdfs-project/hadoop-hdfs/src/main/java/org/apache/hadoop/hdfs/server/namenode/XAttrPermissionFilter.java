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
name|util
operator|.
name|List
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
name|fs
operator|.
name|XAttr
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
name|XAttrHelper
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
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

begin_import
import|import static
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
name|common
operator|.
name|HdfsServerConstants
operator|.
name|SECURITY_XATTR_UNREADABLE_BY_SUPERUSER
import|;
end_import

begin_comment
comment|/**  * There are four types of extended attributes<XAttr> defined by the  * following namespaces:  *<br>  * USER - extended user attributes: these can be assigned to files and  * directories to store arbitrary additional information. The access  * permissions for user attributes are defined by the file permission  * bits. For sticky directories, only the owner and privileged user can   * write attributes.  *<br>  * TRUSTED - trusted extended attributes: these are visible/accessible  * only to/by the super user.  *<br>  * SECURITY - extended security attributes: these are used by the HDFS  * core for security purposes and are not available through admin/user  * API.  *<br>  * SYSTEM - extended system attributes: these are used by the HDFS  * core and are not available through admin/user API.  *<br>  * RAW - extended system attributes: these are used for internal system  *   attributes that sometimes need to be exposed. Like SYSTEM namespace  *   attributes they are not visible to the user except when getXAttr/getXAttrs  *   is called on a file or directory in the /.reserved/raw HDFS directory  *   hierarchy. These attributes can only be accessed by the superuser.  *</br>  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|XAttrPermissionFilter
specifier|public
class|class
name|XAttrPermissionFilter
block|{
DECL|method|checkPermissionForApi (FSPermissionChecker pc, XAttr xAttr, boolean isRawPath)
specifier|static
name|void
name|checkPermissionForApi
parameter_list|(
name|FSPermissionChecker
name|pc
parameter_list|,
name|XAttr
name|xAttr
parameter_list|,
name|boolean
name|isRawPath
parameter_list|)
throws|throws
name|AccessControlException
block|{
specifier|final
name|boolean
name|isSuperUser
init|=
name|pc
operator|.
name|isSuperUser
argument_list|()
decl_stmt|;
if|if
condition|(
name|xAttr
operator|.
name|getNameSpace
argument_list|()
operator|==
name|XAttr
operator|.
name|NameSpace
operator|.
name|USER
operator|||
operator|(
name|xAttr
operator|.
name|getNameSpace
argument_list|()
operator|==
name|XAttr
operator|.
name|NameSpace
operator|.
name|TRUSTED
operator|&&
name|isSuperUser
operator|)
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|xAttr
operator|.
name|getNameSpace
argument_list|()
operator|==
name|XAttr
operator|.
name|NameSpace
operator|.
name|RAW
operator|&&
name|isRawPath
operator|&&
name|isSuperUser
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|XAttrHelper
operator|.
name|getPrefixName
argument_list|(
name|xAttr
argument_list|)
operator|.
name|equals
argument_list|(
name|SECURITY_XATTR_UNREADABLE_BY_SUPERUSER
argument_list|)
condition|)
block|{
if|if
condition|(
name|xAttr
operator|.
name|getValue
argument_list|()
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|AccessControlException
argument_list|(
literal|"Attempt to set a value for '"
operator|+
name|SECURITY_XATTR_UNREADABLE_BY_SUPERUSER
operator|+
literal|"'. Values are not allowed for this xattr."
argument_list|)
throw|;
block|}
return|return;
block|}
throw|throw
operator|new
name|AccessControlException
argument_list|(
literal|"User doesn't have permission for xattr: "
operator|+
name|XAttrHelper
operator|.
name|getPrefixName
argument_list|(
name|xAttr
argument_list|)
argument_list|)
throw|;
block|}
DECL|method|checkPermissionForApi (FSPermissionChecker pc, List<XAttr> xAttrs, boolean isRawPath)
specifier|static
name|void
name|checkPermissionForApi
parameter_list|(
name|FSPermissionChecker
name|pc
parameter_list|,
name|List
argument_list|<
name|XAttr
argument_list|>
name|xAttrs
parameter_list|,
name|boolean
name|isRawPath
parameter_list|)
throws|throws
name|AccessControlException
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|xAttrs
operator|!=
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
name|xAttrs
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
for|for
control|(
name|XAttr
name|xAttr
range|:
name|xAttrs
control|)
block|{
name|checkPermissionForApi
argument_list|(
name|pc
argument_list|,
name|xAttr
argument_list|,
name|isRawPath
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|filterXAttrsForApi (FSPermissionChecker pc, List<XAttr> xAttrs, boolean isRawPath)
specifier|static
name|List
argument_list|<
name|XAttr
argument_list|>
name|filterXAttrsForApi
parameter_list|(
name|FSPermissionChecker
name|pc
parameter_list|,
name|List
argument_list|<
name|XAttr
argument_list|>
name|xAttrs
parameter_list|,
name|boolean
name|isRawPath
parameter_list|)
block|{
assert|assert
name|xAttrs
operator|!=
literal|null
operator|:
literal|"xAttrs can not be null"
assert|;
if|if
condition|(
name|xAttrs
operator|==
literal|null
operator|||
name|xAttrs
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|xAttrs
return|;
block|}
name|List
argument_list|<
name|XAttr
argument_list|>
name|filteredXAttrs
init|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
name|xAttrs
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|isSuperUser
init|=
name|pc
operator|.
name|isSuperUser
argument_list|()
decl_stmt|;
for|for
control|(
name|XAttr
name|xAttr
range|:
name|xAttrs
control|)
block|{
if|if
condition|(
name|xAttr
operator|.
name|getNameSpace
argument_list|()
operator|==
name|XAttr
operator|.
name|NameSpace
operator|.
name|USER
condition|)
block|{
name|filteredXAttrs
operator|.
name|add
argument_list|(
name|xAttr
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|xAttr
operator|.
name|getNameSpace
argument_list|()
operator|==
name|XAttr
operator|.
name|NameSpace
operator|.
name|TRUSTED
operator|&&
name|isSuperUser
condition|)
block|{
name|filteredXAttrs
operator|.
name|add
argument_list|(
name|xAttr
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|xAttr
operator|.
name|getNameSpace
argument_list|()
operator|==
name|XAttr
operator|.
name|NameSpace
operator|.
name|RAW
operator|&&
name|isSuperUser
operator|&&
name|isRawPath
condition|)
block|{
name|filteredXAttrs
operator|.
name|add
argument_list|(
name|xAttr
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|XAttrHelper
operator|.
name|getPrefixName
argument_list|(
name|xAttr
argument_list|)
operator|.
name|equals
argument_list|(
name|SECURITY_XATTR_UNREADABLE_BY_SUPERUSER
argument_list|)
condition|)
block|{
name|filteredXAttrs
operator|.
name|add
argument_list|(
name|xAttr
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|filteredXAttrs
return|;
block|}
block|}
end_class

end_unit

