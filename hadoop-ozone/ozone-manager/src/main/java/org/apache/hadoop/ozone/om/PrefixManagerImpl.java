begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.om
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|om
package|;
end_package

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
name|Strings
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
name|ozone
operator|.
name|OzoneAcl
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
name|ozone
operator|.
name|om
operator|.
name|exceptions
operator|.
name|OMException
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
name|ozone
operator|.
name|om
operator|.
name|helpers
operator|.
name|OmBucketInfo
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
name|ozone
operator|.
name|om
operator|.
name|helpers
operator|.
name|OmPrefixInfo
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
name|ozone
operator|.
name|om
operator|.
name|helpers
operator|.
name|OzoneAclUtil
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
name|ozone
operator|.
name|security
operator|.
name|acl
operator|.
name|OzoneObj
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
name|ozone
operator|.
name|security
operator|.
name|acl
operator|.
name|RequestContext
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
name|ozone
operator|.
name|util
operator|.
name|RadixNode
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
name|ozone
operator|.
name|util
operator|.
name|RadixTree
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
name|utils
operator|.
name|db
operator|.
name|Table
operator|.
name|KeyValue
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
name|utils
operator|.
name|db
operator|.
name|TableIterator
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
name|util
operator|.
name|ArrayList
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
name|Objects
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
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
name|ozone
operator|.
name|om
operator|.
name|exceptions
operator|.
name|OMException
operator|.
name|ResultCodes
operator|.
name|BUCKET_NOT_FOUND
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
name|ozone
operator|.
name|om
operator|.
name|exceptions
operator|.
name|OMException
operator|.
name|ResultCodes
operator|.
name|PREFIX_NOT_FOUND
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
name|ozone
operator|.
name|om
operator|.
name|exceptions
operator|.
name|OMException
operator|.
name|ResultCodes
operator|.
name|VOLUME_NOT_FOUND
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
name|ozone
operator|.
name|om
operator|.
name|lock
operator|.
name|OzoneManagerLock
operator|.
name|Resource
operator|.
name|PREFIX_LOCK
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
name|ozone
operator|.
name|security
operator|.
name|acl
operator|.
name|OzoneObj
operator|.
name|ResourceType
operator|.
name|PREFIX
import|;
end_import

begin_comment
comment|/**  * Implementation of PrefixManager.  */
end_comment

begin_class
DECL|class|PrefixManagerImpl
specifier|public
class|class
name|PrefixManagerImpl
implements|implements
name|PrefixManager
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|PrefixManagerImpl
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|EMPTY_ACL_LIST
specifier|private
specifier|static
specifier|final
name|List
argument_list|<
name|OzoneAcl
argument_list|>
name|EMPTY_ACL_LIST
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|metadataManager
specifier|private
specifier|final
name|OMMetadataManager
name|metadataManager
decl_stmt|;
comment|// In-memory prefix tree to optimize ACL evaluation
DECL|field|prefixTree
specifier|private
name|RadixTree
argument_list|<
name|OmPrefixInfo
argument_list|>
name|prefixTree
decl_stmt|;
comment|// TODO: This isRatisEnabled check will be removed as part of HDDS-1909,
comment|//  where we integrate both HA and Non-HA code.
DECL|field|isRatisEnabled
specifier|private
name|boolean
name|isRatisEnabled
decl_stmt|;
DECL|method|PrefixManagerImpl (OMMetadataManager metadataManager, boolean isRatisEnabled)
specifier|public
name|PrefixManagerImpl
parameter_list|(
name|OMMetadataManager
name|metadataManager
parameter_list|,
name|boolean
name|isRatisEnabled
parameter_list|)
block|{
name|this
operator|.
name|isRatisEnabled
operator|=
name|isRatisEnabled
expr_stmt|;
name|this
operator|.
name|metadataManager
operator|=
name|metadataManager
expr_stmt|;
name|loadPrefixTree
argument_list|()
expr_stmt|;
block|}
DECL|method|loadPrefixTree ()
specifier|private
name|void
name|loadPrefixTree
parameter_list|()
block|{
name|prefixTree
operator|=
operator|new
name|RadixTree
argument_list|<>
argument_list|()
expr_stmt|;
try|try
init|(
name|TableIterator
argument_list|<
name|String
argument_list|,
name|?
extends|extends
name|KeyValue
argument_list|<
name|String
argument_list|,
name|OmPrefixInfo
argument_list|>
argument_list|>
name|iterator
init|=
name|getMetadataManager
argument_list|()
operator|.
name|getPrefixTable
argument_list|()
operator|.
name|iterator
argument_list|()
init|)
block|{
name|iterator
operator|.
name|seekToFirst
argument_list|()
expr_stmt|;
while|while
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|KeyValue
argument_list|<
name|String
argument_list|,
name|OmPrefixInfo
argument_list|>
name|kv
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|prefixTree
operator|.
name|insert
argument_list|(
name|kv
operator|.
name|getKey
argument_list|()
argument_list|,
name|kv
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Fail to load prefix tree"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getMetadataManager ()
specifier|public
name|OMMetadataManager
name|getMetadataManager
parameter_list|()
block|{
return|return
name|metadataManager
return|;
block|}
comment|/**    * Add acl for Ozone object. Return true if acl is added successfully else    * false.    *    * @param obj Ozone object for which acl should be added.    * @param acl ozone acl top be added.    * @throws IOException if there is error.    */
annotation|@
name|Override
DECL|method|addAcl (OzoneObj obj, OzoneAcl acl)
specifier|public
name|boolean
name|addAcl
parameter_list|(
name|OzoneObj
name|obj
parameter_list|,
name|OzoneAcl
name|acl
parameter_list|)
throws|throws
name|IOException
block|{
name|validateOzoneObj
argument_list|(
name|obj
argument_list|)
expr_stmt|;
name|String
name|prefixPath
init|=
name|obj
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|metadataManager
operator|.
name|getLock
argument_list|()
operator|.
name|acquireLock
argument_list|(
name|PREFIX_LOCK
argument_list|,
name|prefixPath
argument_list|)
expr_stmt|;
try|try
block|{
name|OmPrefixInfo
name|prefixInfo
init|=
name|metadataManager
operator|.
name|getPrefixTable
argument_list|()
operator|.
name|get
argument_list|(
name|prefixPath
argument_list|)
decl_stmt|;
name|OMPrefixAclOpResult
name|omPrefixAclOpResult
init|=
name|addAcl
argument_list|(
name|obj
argument_list|,
name|acl
argument_list|,
name|prefixInfo
argument_list|)
decl_stmt|;
return|return
name|omPrefixAclOpResult
operator|.
name|isOperationsResult
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|ex
operator|instanceof
name|OMException
operator|)
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Add acl operation failed for prefix path:{} acl:{}"
argument_list|,
name|prefixPath
argument_list|,
name|acl
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
throw|throw
name|ex
throw|;
block|}
finally|finally
block|{
name|metadataManager
operator|.
name|getLock
argument_list|()
operator|.
name|releaseLock
argument_list|(
name|PREFIX_LOCK
argument_list|,
name|prefixPath
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Remove acl for Ozone object. Return true if acl is removed successfully    * else false.    *    * @param obj Ozone object.    * @param acl Ozone acl to be removed.    * @throws IOException if there is error.    */
annotation|@
name|Override
DECL|method|removeAcl (OzoneObj obj, OzoneAcl acl)
specifier|public
name|boolean
name|removeAcl
parameter_list|(
name|OzoneObj
name|obj
parameter_list|,
name|OzoneAcl
name|acl
parameter_list|)
throws|throws
name|IOException
block|{
name|validateOzoneObj
argument_list|(
name|obj
argument_list|)
expr_stmt|;
name|String
name|prefixPath
init|=
name|obj
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|metadataManager
operator|.
name|getLock
argument_list|()
operator|.
name|acquireLock
argument_list|(
name|PREFIX_LOCK
argument_list|,
name|prefixPath
argument_list|)
expr_stmt|;
try|try
block|{
name|OmPrefixInfo
name|prefixInfo
init|=
name|metadataManager
operator|.
name|getPrefixTable
argument_list|()
operator|.
name|get
argument_list|(
name|prefixPath
argument_list|)
decl_stmt|;
name|OMPrefixAclOpResult
name|omPrefixAclOpResult
init|=
name|removeAcl
argument_list|(
name|obj
argument_list|,
name|acl
argument_list|,
name|prefixInfo
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|omPrefixAclOpResult
operator|.
name|isOperationsResult
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"acl {} does not exist for prefix path {} "
argument_list|,
name|acl
argument_list|,
name|prefixPath
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
return|return
name|omPrefixAclOpResult
operator|.
name|isOperationsResult
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|ex
operator|instanceof
name|OMException
operator|)
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Remove prefix acl operation failed for prefix path:{}"
operator|+
literal|" acl:{}"
argument_list|,
name|prefixPath
argument_list|,
name|acl
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
throw|throw
name|ex
throw|;
block|}
finally|finally
block|{
name|metadataManager
operator|.
name|getLock
argument_list|()
operator|.
name|releaseLock
argument_list|(
name|PREFIX_LOCK
argument_list|,
name|prefixPath
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Acls to be set for given Ozone object. This operations reset ACL for given    * object to list of ACLs provided in argument.    *    * @param obj Ozone object.    * @param acls List of acls.    * @throws IOException if there is error.    */
annotation|@
name|Override
DECL|method|setAcl (OzoneObj obj, List<OzoneAcl> acls)
specifier|public
name|boolean
name|setAcl
parameter_list|(
name|OzoneObj
name|obj
parameter_list|,
name|List
argument_list|<
name|OzoneAcl
argument_list|>
name|acls
parameter_list|)
throws|throws
name|IOException
block|{
name|validateOzoneObj
argument_list|(
name|obj
argument_list|)
expr_stmt|;
name|String
name|prefixPath
init|=
name|obj
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|metadataManager
operator|.
name|getLock
argument_list|()
operator|.
name|acquireLock
argument_list|(
name|PREFIX_LOCK
argument_list|,
name|prefixPath
argument_list|)
expr_stmt|;
try|try
block|{
name|OmPrefixInfo
name|prefixInfo
init|=
name|metadataManager
operator|.
name|getPrefixTable
argument_list|()
operator|.
name|get
argument_list|(
name|prefixPath
argument_list|)
decl_stmt|;
name|OMPrefixAclOpResult
name|omPrefixAclOpResult
init|=
name|setAcl
argument_list|(
name|obj
argument_list|,
name|acls
argument_list|,
name|prefixInfo
argument_list|)
decl_stmt|;
return|return
name|omPrefixAclOpResult
operator|.
name|isOperationsResult
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|ex
operator|instanceof
name|OMException
operator|)
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Set prefix acl operation failed for prefix path:{} acls:{}"
argument_list|,
name|prefixPath
argument_list|,
name|acls
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
throw|throw
name|ex
throw|;
block|}
finally|finally
block|{
name|metadataManager
operator|.
name|getLock
argument_list|()
operator|.
name|releaseLock
argument_list|(
name|PREFIX_LOCK
argument_list|,
name|prefixPath
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Returns list of ACLs for given Ozone object.    *    * @param obj Ozone object.    * @throws IOException if there is error.    */
annotation|@
name|Override
DECL|method|getAcl (OzoneObj obj)
specifier|public
name|List
argument_list|<
name|OzoneAcl
argument_list|>
name|getAcl
parameter_list|(
name|OzoneObj
name|obj
parameter_list|)
throws|throws
name|IOException
block|{
name|validateOzoneObj
argument_list|(
name|obj
argument_list|)
expr_stmt|;
name|String
name|prefixPath
init|=
name|obj
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|metadataManager
operator|.
name|getLock
argument_list|()
operator|.
name|acquireLock
argument_list|(
name|PREFIX_LOCK
argument_list|,
name|prefixPath
argument_list|)
expr_stmt|;
try|try
block|{
name|String
name|longestPrefix
init|=
name|prefixTree
operator|.
name|getLongestPrefix
argument_list|(
name|prefixPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|prefixPath
operator|.
name|equals
argument_list|(
name|longestPrefix
argument_list|)
condition|)
block|{
name|RadixNode
argument_list|<
name|OmPrefixInfo
argument_list|>
name|lastNode
init|=
name|prefixTree
operator|.
name|getLastNodeInPrefixPath
argument_list|(
name|prefixPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|lastNode
operator|!=
literal|null
operator|&&
name|lastNode
operator|.
name|getValue
argument_list|()
operator|!=
literal|null
condition|)
block|{
return|return
name|lastNode
operator|.
name|getValue
argument_list|()
operator|.
name|getAcls
argument_list|()
return|;
block|}
block|}
block|}
finally|finally
block|{
name|metadataManager
operator|.
name|getLock
argument_list|()
operator|.
name|releaseLock
argument_list|(
name|PREFIX_LOCK
argument_list|,
name|prefixPath
argument_list|)
expr_stmt|;
block|}
return|return
name|EMPTY_ACL_LIST
return|;
block|}
comment|/**    * Check access for given ozoneObject.    *    * @param ozObject object for which access needs to be checked.    * @param context Context object encapsulating all user related information.    * @return true if user has access else false.    */
annotation|@
name|Override
DECL|method|checkAccess (OzoneObj ozObject, RequestContext context)
specifier|public
name|boolean
name|checkAccess
parameter_list|(
name|OzoneObj
name|ozObject
parameter_list|,
name|RequestContext
name|context
parameter_list|)
throws|throws
name|OMException
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|ozObject
argument_list|)
expr_stmt|;
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|String
name|prefixPath
init|=
name|ozObject
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|metadataManager
operator|.
name|getLock
argument_list|()
operator|.
name|acquireLock
argument_list|(
name|PREFIX_LOCK
argument_list|,
name|prefixPath
argument_list|)
expr_stmt|;
try|try
block|{
name|String
name|longestPrefix
init|=
name|prefixTree
operator|.
name|getLongestPrefix
argument_list|(
name|prefixPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|prefixPath
operator|.
name|equals
argument_list|(
name|longestPrefix
argument_list|)
condition|)
block|{
name|RadixNode
argument_list|<
name|OmPrefixInfo
argument_list|>
name|lastNode
init|=
name|prefixTree
operator|.
name|getLastNodeInPrefixPath
argument_list|(
name|prefixPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|lastNode
operator|!=
literal|null
operator|&&
name|lastNode
operator|.
name|getValue
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|boolean
name|hasAccess
init|=
name|OzoneAclUtil
operator|.
name|checkAclRights
argument_list|(
name|lastNode
operator|.
name|getValue
argument_list|()
operator|.
name|getAcls
argument_list|()
argument_list|,
name|context
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"user:{} has access rights for ozObj:{} ::{} "
argument_list|,
name|context
operator|.
name|getClientUgi
argument_list|()
argument_list|,
name|ozObject
argument_list|,
name|hasAccess
argument_list|)
expr_stmt|;
return|return
name|hasAccess
return|;
block|}
else|else
block|{
return|return
literal|true
return|;
block|}
block|}
else|else
block|{
return|return
literal|true
return|;
block|}
block|}
finally|finally
block|{
name|metadataManager
operator|.
name|getLock
argument_list|()
operator|.
name|releaseLock
argument_list|(
name|PREFIX_LOCK
argument_list|,
name|prefixPath
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getLongestPrefixPath (String path)
specifier|public
name|List
argument_list|<
name|OmPrefixInfo
argument_list|>
name|getLongestPrefixPath
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|String
name|prefixPath
init|=
name|prefixTree
operator|.
name|getLongestPrefix
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|metadataManager
operator|.
name|getLock
argument_list|()
operator|.
name|acquireLock
argument_list|(
name|PREFIX_LOCK
argument_list|,
name|prefixPath
argument_list|)
expr_stmt|;
try|try
block|{
return|return
name|getLongestPrefixPathHelper
argument_list|(
name|prefixPath
argument_list|)
return|;
block|}
finally|finally
block|{
name|metadataManager
operator|.
name|getLock
argument_list|()
operator|.
name|releaseLock
argument_list|(
name|PREFIX_LOCK
argument_list|,
name|prefixPath
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Get longest prefix path assuming caller take prefix lock.    * @param prefixPath    * @return list of prefix info.    */
DECL|method|getLongestPrefixPathHelper (String prefixPath)
specifier|private
name|List
argument_list|<
name|OmPrefixInfo
argument_list|>
name|getLongestPrefixPathHelper
parameter_list|(
name|String
name|prefixPath
parameter_list|)
block|{
return|return
name|prefixTree
operator|.
name|getLongestPrefixPath
argument_list|(
name|prefixPath
argument_list|)
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|c
lambda|->
name|c
operator|.
name|getValue
argument_list|()
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Helper method to validate ozone object.    * @param obj    * */
DECL|method|validateOzoneObj (OzoneObj obj)
specifier|public
name|void
name|validateOzoneObj
parameter_list|(
name|OzoneObj
name|obj
parameter_list|)
throws|throws
name|OMException
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|obj
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|obj
operator|.
name|getResourceType
argument_list|()
operator|.
name|equals
argument_list|(
name|PREFIX
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unexpected argument passed to "
operator|+
literal|"PrefixManager. OzoneObj type:"
operator|+
name|obj
operator|.
name|getResourceType
argument_list|()
argument_list|)
throw|;
block|}
name|String
name|volume
init|=
name|obj
operator|.
name|getVolumeName
argument_list|()
decl_stmt|;
name|String
name|bucket
init|=
name|obj
operator|.
name|getBucketName
argument_list|()
decl_stmt|;
name|String
name|prefixName
init|=
name|obj
operator|.
name|getPrefixName
argument_list|()
decl_stmt|;
if|if
condition|(
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|volume
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|OMException
argument_list|(
literal|"Volume name is required."
argument_list|,
name|VOLUME_NOT_FOUND
argument_list|)
throw|;
block|}
if|if
condition|(
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|bucket
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|OMException
argument_list|(
literal|"Bucket name is required."
argument_list|,
name|BUCKET_NOT_FOUND
argument_list|)
throw|;
block|}
if|if
condition|(
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|prefixName
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|OMException
argument_list|(
literal|"Prefix name is required."
argument_list|,
name|PREFIX_NOT_FOUND
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|prefixName
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|OMException
argument_list|(
literal|"Invalid prefix name: "
operator|+
name|prefixName
argument_list|,
name|PREFIX_NOT_FOUND
argument_list|)
throw|;
block|}
block|}
DECL|method|addAcl (OzoneObj ozoneObj, OzoneAcl ozoneAcl, OmPrefixInfo prefixInfo)
specifier|public
name|OMPrefixAclOpResult
name|addAcl
parameter_list|(
name|OzoneObj
name|ozoneObj
parameter_list|,
name|OzoneAcl
name|ozoneAcl
parameter_list|,
name|OmPrefixInfo
name|prefixInfo
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|prefixInfo
operator|==
literal|null
condition|)
block|{
name|prefixInfo
operator|=
operator|new
name|OmPrefixInfo
operator|.
name|Builder
argument_list|()
operator|.
name|setName
argument_list|(
name|ozoneObj
operator|.
name|getPath
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
name|boolean
name|changed
init|=
name|prefixInfo
operator|.
name|addAcl
argument_list|(
name|ozoneAcl
argument_list|)
decl_stmt|;
if|if
condition|(
name|changed
condition|)
block|{
comment|// update the in-memory prefix tree
name|prefixTree
operator|.
name|insert
argument_list|(
name|ozoneObj
operator|.
name|getPath
argument_list|()
argument_list|,
name|prefixInfo
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|isRatisEnabled
condition|)
block|{
name|metadataManager
operator|.
name|getPrefixTable
argument_list|()
operator|.
name|put
argument_list|(
name|ozoneObj
operator|.
name|getPath
argument_list|()
argument_list|,
name|prefixInfo
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|OMPrefixAclOpResult
argument_list|(
name|prefixInfo
argument_list|,
name|changed
argument_list|)
return|;
block|}
DECL|method|removeAcl (OzoneObj ozoneObj, OzoneAcl ozoneAcl, OmPrefixInfo prefixInfo)
specifier|public
name|OMPrefixAclOpResult
name|removeAcl
parameter_list|(
name|OzoneObj
name|ozoneObj
parameter_list|,
name|OzoneAcl
name|ozoneAcl
parameter_list|,
name|OmPrefixInfo
name|prefixInfo
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|removed
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|prefixInfo
operator|!=
literal|null
condition|)
block|{
name|removed
operator|=
name|prefixInfo
operator|.
name|removeAcl
argument_list|(
name|ozoneAcl
argument_list|)
expr_stmt|;
block|}
comment|// Nothing is matching to remove.
if|if
condition|(
name|removed
condition|)
block|{
comment|// Update in-memory prefix tree.
if|if
condition|(
name|prefixInfo
operator|.
name|getAcls
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|prefixTree
operator|.
name|removePrefixPath
argument_list|(
name|ozoneObj
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|isRatisEnabled
condition|)
block|{
name|metadataManager
operator|.
name|getPrefixTable
argument_list|()
operator|.
name|delete
argument_list|(
name|ozoneObj
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|prefixTree
operator|.
name|insert
argument_list|(
name|ozoneObj
operator|.
name|getPath
argument_list|()
argument_list|,
name|prefixInfo
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|isRatisEnabled
condition|)
block|{
name|metadataManager
operator|.
name|getPrefixTable
argument_list|()
operator|.
name|put
argument_list|(
name|ozoneObj
operator|.
name|getPath
argument_list|()
argument_list|,
name|prefixInfo
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
operator|new
name|OMPrefixAclOpResult
argument_list|(
name|prefixInfo
argument_list|,
name|removed
argument_list|)
return|;
block|}
DECL|method|setAcl (OzoneObj ozoneObj, List<OzoneAcl> ozoneAcls, OmPrefixInfo prefixInfo)
specifier|public
name|OMPrefixAclOpResult
name|setAcl
parameter_list|(
name|OzoneObj
name|ozoneObj
parameter_list|,
name|List
argument_list|<
name|OzoneAcl
argument_list|>
name|ozoneAcls
parameter_list|,
name|OmPrefixInfo
name|prefixInfo
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|prefixInfo
operator|==
literal|null
condition|)
block|{
name|prefixInfo
operator|=
operator|new
name|OmPrefixInfo
operator|.
name|Builder
argument_list|()
operator|.
name|setName
argument_list|(
name|ozoneObj
operator|.
name|getPath
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
name|boolean
name|changed
init|=
name|prefixInfo
operator|.
name|setAcls
argument_list|(
name|ozoneAcls
argument_list|)
decl_stmt|;
if|if
condition|(
name|changed
condition|)
block|{
name|List
argument_list|<
name|OzoneAcl
argument_list|>
name|aclsToBeSet
init|=
name|prefixInfo
operator|.
name|getAcls
argument_list|()
decl_stmt|;
comment|// Inherit DEFAULT acls from prefix.
name|boolean
name|prefixParentFound
init|=
literal|false
decl_stmt|;
name|List
argument_list|<
name|OmPrefixInfo
argument_list|>
name|prefixList
init|=
name|getLongestPrefixPathHelper
argument_list|(
name|prefixTree
operator|.
name|getLongestPrefix
argument_list|(
name|ozoneObj
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|prefixList
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
comment|// Add all acls from direct parent to key.
name|OmPrefixInfo
name|parentPrefixInfo
init|=
name|prefixList
operator|.
name|get
argument_list|(
name|prefixList
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|parentPrefixInfo
operator|!=
literal|null
condition|)
block|{
name|prefixParentFound
operator|=
name|OzoneAclUtil
operator|.
name|inheritDefaultAcls
argument_list|(
name|aclsToBeSet
argument_list|,
name|parentPrefixInfo
operator|.
name|getAcls
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// If no parent prefix is found inherit DEFAULT acls from bucket.
if|if
condition|(
operator|!
name|prefixParentFound
condition|)
block|{
name|String
name|bucketKey
init|=
name|metadataManager
operator|.
name|getBucketKey
argument_list|(
name|ozoneObj
operator|.
name|getVolumeName
argument_list|()
argument_list|,
name|ozoneObj
operator|.
name|getBucketName
argument_list|()
argument_list|)
decl_stmt|;
name|OmBucketInfo
name|bucketInfo
init|=
name|metadataManager
operator|.
name|getBucketTable
argument_list|()
operator|.
name|get
argument_list|(
name|bucketKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|bucketInfo
operator|!=
literal|null
condition|)
block|{
name|OzoneAclUtil
operator|.
name|inheritDefaultAcls
argument_list|(
name|aclsToBeSet
argument_list|,
name|bucketInfo
operator|.
name|getAcls
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|prefixTree
operator|.
name|insert
argument_list|(
name|ozoneObj
operator|.
name|getPath
argument_list|()
argument_list|,
name|prefixInfo
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|isRatisEnabled
condition|)
block|{
name|metadataManager
operator|.
name|getPrefixTable
argument_list|()
operator|.
name|put
argument_list|(
name|ozoneObj
operator|.
name|getPath
argument_list|()
argument_list|,
name|prefixInfo
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|OMPrefixAclOpResult
argument_list|(
name|prefixInfo
argument_list|,
name|changed
argument_list|)
return|;
block|}
comment|/**    * Result of the prefix acl operation.    */
DECL|class|OMPrefixAclOpResult
specifier|public
specifier|static
class|class
name|OMPrefixAclOpResult
block|{
DECL|field|omPrefixInfo
specifier|private
name|OmPrefixInfo
name|omPrefixInfo
decl_stmt|;
DECL|field|operationsResult
specifier|private
name|boolean
name|operationsResult
decl_stmt|;
DECL|method|OMPrefixAclOpResult (OmPrefixInfo omPrefixInfo, boolean operationsResult)
specifier|public
name|OMPrefixAclOpResult
parameter_list|(
name|OmPrefixInfo
name|omPrefixInfo
parameter_list|,
name|boolean
name|operationsResult
parameter_list|)
block|{
name|this
operator|.
name|omPrefixInfo
operator|=
name|omPrefixInfo
expr_stmt|;
name|this
operator|.
name|operationsResult
operator|=
name|operationsResult
expr_stmt|;
block|}
DECL|method|getOmPrefixInfo ()
specifier|public
name|OmPrefixInfo
name|getOmPrefixInfo
parameter_list|()
block|{
return|return
name|omPrefixInfo
return|;
block|}
DECL|method|isOperationsResult ()
specifier|public
name|boolean
name|isOperationsResult
parameter_list|()
block|{
return|return
name|operationsResult
return|;
block|}
block|}
block|}
end_class

end_unit

