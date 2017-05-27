begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.azure
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|azure
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
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|util
operator|.
name|StringUtils
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
name|Path
import|;
end_import

begin_comment
comment|/**  * A mock wasb authorizer implementation.  */
end_comment

begin_class
DECL|class|MockWasbAuthorizerImpl
specifier|public
class|class
name|MockWasbAuthorizerImpl
implements|implements
name|WasbAuthorizerInterface
block|{
DECL|field|authRules
specifier|private
name|Map
argument_list|<
name|AuthorizationComponent
argument_list|,
name|Boolean
argument_list|>
name|authRules
decl_stmt|;
DECL|field|performOwnerMatch
specifier|private
name|boolean
name|performOwnerMatch
decl_stmt|;
comment|// The full qualified URL to the root directory
DECL|field|qualifiedPrefixUrl
specifier|private
name|String
name|qualifiedPrefixUrl
decl_stmt|;
DECL|method|MockWasbAuthorizerImpl (NativeAzureFileSystem fs)
specifier|public
name|MockWasbAuthorizerImpl
parameter_list|(
name|NativeAzureFileSystem
name|fs
parameter_list|)
block|{
name|qualifiedPrefixUrl
operator|=
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|)
operator|.
name|makeQualified
argument_list|(
name|fs
operator|.
name|getUri
argument_list|()
argument_list|,
name|fs
operator|.
name|getWorkingDirectory
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
operator|.
name|replaceAll
argument_list|(
literal|"/$"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|init (Configuration conf)
specifier|public
name|void
name|init
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|init
argument_list|(
name|conf
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/*   authorization matches owner with currentUserShortName while evaluating auth rules   if currentUserShortName is set to a string that is not empty   */
DECL|method|init (Configuration conf, boolean matchOwner)
specifier|public
name|void
name|init
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|boolean
name|matchOwner
parameter_list|)
block|{
name|authRules
operator|=
operator|new
name|HashMap
argument_list|<
name|AuthorizationComponent
argument_list|,
name|Boolean
argument_list|>
argument_list|()
expr_stmt|;
name|this
operator|.
name|performOwnerMatch
operator|=
name|matchOwner
expr_stmt|;
block|}
DECL|method|addAuthRule (String wasbAbsolutePath, String accessType, boolean access)
specifier|public
name|void
name|addAuthRule
parameter_list|(
name|String
name|wasbAbsolutePath
parameter_list|,
name|String
name|accessType
parameter_list|,
name|boolean
name|access
parameter_list|)
block|{
name|wasbAbsolutePath
operator|=
name|qualifiedPrefixUrl
operator|+
name|wasbAbsolutePath
expr_stmt|;
name|AuthorizationComponent
name|component
init|=
name|wasbAbsolutePath
operator|.
name|endsWith
argument_list|(
literal|"*"
argument_list|)
condition|?
operator|new
name|AuthorizationComponent
argument_list|(
literal|"^"
operator|+
name|wasbAbsolutePath
operator|.
name|replace
argument_list|(
literal|"*"
argument_list|,
literal|".*"
argument_list|)
argument_list|,
name|accessType
argument_list|)
else|:
operator|new
name|AuthorizationComponent
argument_list|(
name|wasbAbsolutePath
argument_list|,
name|accessType
argument_list|)
decl_stmt|;
name|this
operator|.
name|authRules
operator|.
name|put
argument_list|(
name|component
argument_list|,
name|access
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|authorize (String wasbAbsolutePath, String accessType, String owner)
specifier|public
name|boolean
name|authorize
parameter_list|(
name|String
name|wasbAbsolutePath
parameter_list|,
name|String
name|accessType
parameter_list|,
name|String
name|owner
parameter_list|)
throws|throws
name|WasbAuthorizationException
block|{
if|if
condition|(
name|wasbAbsolutePath
operator|.
name|endsWith
argument_list|(
name|NativeAzureFileSystem
operator|.
name|FolderRenamePending
operator|.
name|SUFFIX
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
name|String
name|currentUserShortName
init|=
literal|""
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|performOwnerMatch
condition|)
block|{
try|try
block|{
name|UserGroupInformation
name|ugi
init|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
decl_stmt|;
name|currentUserShortName
operator|=
name|ugi
operator|.
name|getShortUserName
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|//no op
block|}
block|}
comment|// In case of root("/"), owner match does not happen because owner is returned as empty string.
comment|// we try to force owner match just for purpose of tests to make sure all operations work seemlessly with owner.
if|if
condition|(
name|this
operator|.
name|performOwnerMatch
operator|&&
name|StringUtils
operator|.
name|equalsIgnoreCase
argument_list|(
name|wasbAbsolutePath
argument_list|,
name|qualifiedPrefixUrl
operator|+
literal|"/"
argument_list|)
condition|)
block|{
name|owner
operator|=
name|currentUserShortName
expr_stmt|;
block|}
name|boolean
name|shouldEvaluateOwnerAccess
init|=
name|owner
operator|!=
literal|null
operator|&&
operator|!
name|owner
operator|.
name|isEmpty
argument_list|()
operator|&&
name|this
operator|.
name|performOwnerMatch
decl_stmt|;
name|boolean
name|isOwnerMatch
init|=
name|StringUtils
operator|.
name|equalsIgnoreCase
argument_list|(
name|currentUserShortName
argument_list|,
name|owner
argument_list|)
decl_stmt|;
name|AuthorizationComponent
name|component
init|=
operator|new
name|AuthorizationComponent
argument_list|(
name|wasbAbsolutePath
argument_list|,
name|accessType
argument_list|)
decl_stmt|;
if|if
condition|(
name|authRules
operator|.
name|containsKey
argument_list|(
name|component
argument_list|)
condition|)
block|{
return|return
name|shouldEvaluateOwnerAccess
condition|?
name|isOwnerMatch
operator|&&
name|authRules
operator|.
name|get
argument_list|(
name|component
argument_list|)
else|:
name|authRules
operator|.
name|get
argument_list|(
name|component
argument_list|)
return|;
block|}
else|else
block|{
comment|// Regex-pattern match if we don't have a straight match
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|AuthorizationComponent
argument_list|,
name|Boolean
argument_list|>
name|entry
range|:
name|authRules
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|AuthorizationComponent
name|key
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|String
name|keyPath
init|=
name|key
operator|.
name|getWasbAbsolutePath
argument_list|()
decl_stmt|;
name|String
name|keyAccess
init|=
name|key
operator|.
name|getAccessType
argument_list|()
decl_stmt|;
if|if
condition|(
name|keyPath
operator|.
name|endsWith
argument_list|(
literal|"*"
argument_list|)
operator|&&
name|Pattern
operator|.
name|matches
argument_list|(
name|keyPath
argument_list|,
name|wasbAbsolutePath
argument_list|)
operator|&&
name|keyAccess
operator|.
name|equals
argument_list|(
name|accessType
argument_list|)
condition|)
block|{
return|return
name|shouldEvaluateOwnerAccess
condition|?
name|isOwnerMatch
operator|&&
name|entry
operator|.
name|getValue
argument_list|()
else|:
name|entry
operator|.
name|getValue
argument_list|()
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
block|}
DECL|method|deleteAllAuthRules ()
specifier|public
name|void
name|deleteAllAuthRules
parameter_list|()
block|{
name|authRules
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
end_class

begin_class
DECL|class|AuthorizationComponent
class|class
name|AuthorizationComponent
block|{
DECL|field|wasbAbsolutePath
specifier|private
name|String
name|wasbAbsolutePath
decl_stmt|;
DECL|field|accessType
specifier|private
name|String
name|accessType
decl_stmt|;
DECL|method|AuthorizationComponent (String wasbAbsolutePath, String accessType)
specifier|public
name|AuthorizationComponent
parameter_list|(
name|String
name|wasbAbsolutePath
parameter_list|,
name|String
name|accessType
parameter_list|)
block|{
name|this
operator|.
name|wasbAbsolutePath
operator|=
name|wasbAbsolutePath
expr_stmt|;
name|this
operator|.
name|accessType
operator|=
name|accessType
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|this
operator|.
name|wasbAbsolutePath
operator|.
name|hashCode
argument_list|()
operator|^
name|this
operator|.
name|accessType
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object obj)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|==
name|this
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|obj
operator|==
literal|null
operator|||
operator|!
operator|(
name|obj
operator|instanceof
name|AuthorizationComponent
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
operator|(
operator|(
name|AuthorizationComponent
operator|)
name|obj
operator|)
operator|.
name|getWasbAbsolutePath
argument_list|()
operator|.
name|equals
argument_list|(
name|this
operator|.
name|wasbAbsolutePath
argument_list|)
operator|&&
operator|(
operator|(
name|AuthorizationComponent
operator|)
name|obj
operator|)
operator|.
name|getAccessType
argument_list|()
operator|.
name|equals
argument_list|(
name|this
operator|.
name|accessType
argument_list|)
return|;
block|}
DECL|method|getWasbAbsolutePath ()
specifier|public
name|String
name|getWasbAbsolutePath
parameter_list|()
block|{
return|return
name|this
operator|.
name|wasbAbsolutePath
return|;
block|}
DECL|method|getAccessType ()
specifier|public
name|String
name|getAccessType
parameter_list|()
block|{
return|return
name|accessType
return|;
block|}
block|}
end_class

end_unit

