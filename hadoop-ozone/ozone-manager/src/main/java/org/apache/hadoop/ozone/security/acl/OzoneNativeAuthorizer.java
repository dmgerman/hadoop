begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.security.acl
package|package
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
name|BucketManager
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
name|KeyManager
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
name|PrefixManager
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
name|VolumeManager
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
name|util
operator|.
name|Objects
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
name|INVALID_REQUEST
import|;
end_import

begin_comment
comment|/**  * Public API for Ozone ACLs. Security providers providing support for Ozone  * ACLs should implement this.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|LimitedPrivate
argument_list|(
block|{
literal|"HDFS"
block|,
literal|"Yarn"
block|,
literal|"Ranger"
block|,
literal|"Hive"
block|,
literal|"HBase"
block|}
argument_list|)
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|OzoneNativeAuthorizer
specifier|public
class|class
name|OzoneNativeAuthorizer
implements|implements
name|IAccessAuthorizer
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
name|OzoneNativeAuthorizer
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|volumeManager
specifier|private
name|VolumeManager
name|volumeManager
decl_stmt|;
DECL|field|bucketManager
specifier|private
name|BucketManager
name|bucketManager
decl_stmt|;
DECL|field|keyManager
specifier|private
name|KeyManager
name|keyManager
decl_stmt|;
DECL|field|prefixManager
specifier|private
name|PrefixManager
name|prefixManager
decl_stmt|;
DECL|method|OzoneNativeAuthorizer ()
specifier|public
name|OzoneNativeAuthorizer
parameter_list|()
block|{   }
DECL|method|OzoneNativeAuthorizer (VolumeManager volumeManager, BucketManager bucketManager, KeyManager keyManager, PrefixManager prefixManager)
specifier|public
name|OzoneNativeAuthorizer
parameter_list|(
name|VolumeManager
name|volumeManager
parameter_list|,
name|BucketManager
name|bucketManager
parameter_list|,
name|KeyManager
name|keyManager
parameter_list|,
name|PrefixManager
name|prefixManager
parameter_list|)
block|{
name|this
operator|.
name|volumeManager
operator|=
name|volumeManager
expr_stmt|;
name|this
operator|.
name|bucketManager
operator|=
name|bucketManager
expr_stmt|;
name|this
operator|.
name|keyManager
operator|=
name|keyManager
expr_stmt|;
name|this
operator|.
name|prefixManager
operator|=
name|prefixManager
expr_stmt|;
block|}
comment|/**    * Check access for given ozoneObject.    *    * @param ozObject object for which access needs to be checked.    * @param context Context object encapsulating all user related information.    * @return true if user has access else false.    */
DECL|method|checkAccess (IOzoneObj ozObject, RequestContext context)
specifier|public
name|boolean
name|checkAccess
parameter_list|(
name|IOzoneObj
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
name|OzoneObjInfo
name|objInfo
decl_stmt|;
if|if
condition|(
name|ozObject
operator|instanceof
name|OzoneObjInfo
condition|)
block|{
name|objInfo
operator|=
operator|(
name|OzoneObjInfo
operator|)
name|ozObject
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|OMException
argument_list|(
literal|"Unexpected input received. OM native acls are "
operator|+
literal|"configured to work with OzoneObjInfo type only."
argument_list|,
name|INVALID_REQUEST
argument_list|)
throw|;
block|}
switch|switch
condition|(
name|objInfo
operator|.
name|getResourceType
argument_list|()
condition|)
block|{
case|case
name|VOLUME
case|:
name|LOG
operator|.
name|trace
argument_list|(
literal|"Checking access for volume: {}"
argument_list|,
name|objInfo
argument_list|)
expr_stmt|;
return|return
name|volumeManager
operator|.
name|checkAccess
argument_list|(
name|objInfo
argument_list|,
name|context
argument_list|)
return|;
case|case
name|BUCKET
case|:
name|LOG
operator|.
name|trace
argument_list|(
literal|"Checking access for bucket: {}"
argument_list|,
name|objInfo
argument_list|)
expr_stmt|;
return|return
operator|(
name|bucketManager
operator|.
name|checkAccess
argument_list|(
name|objInfo
argument_list|,
name|context
argument_list|)
operator|&&
name|volumeManager
operator|.
name|checkAccess
argument_list|(
name|objInfo
argument_list|,
name|context
argument_list|)
operator|)
return|;
case|case
name|KEY
case|:
name|LOG
operator|.
name|trace
argument_list|(
literal|"Checking access for Key: {}"
argument_list|,
name|objInfo
argument_list|)
expr_stmt|;
return|return
operator|(
name|keyManager
operator|.
name|checkAccess
argument_list|(
name|objInfo
argument_list|,
name|context
argument_list|)
operator|&&
name|prefixManager
operator|.
name|checkAccess
argument_list|(
name|objInfo
argument_list|,
name|context
argument_list|)
operator|&&
name|bucketManager
operator|.
name|checkAccess
argument_list|(
name|objInfo
argument_list|,
name|context
argument_list|)
operator|&&
name|volumeManager
operator|.
name|checkAccess
argument_list|(
name|objInfo
argument_list|,
name|context
argument_list|)
operator|)
return|;
case|case
name|PREFIX
case|:
name|LOG
operator|.
name|trace
argument_list|(
literal|"Checking access for Prefix: {]"
argument_list|,
name|objInfo
argument_list|)
expr_stmt|;
return|return
operator|(
name|prefixManager
operator|.
name|checkAccess
argument_list|(
name|objInfo
argument_list|,
name|context
argument_list|)
operator|&&
name|bucketManager
operator|.
name|checkAccess
argument_list|(
name|objInfo
argument_list|,
name|context
argument_list|)
operator|&&
name|volumeManager
operator|.
name|checkAccess
argument_list|(
name|objInfo
argument_list|,
name|context
argument_list|)
operator|)
return|;
default|default:
throw|throw
operator|new
name|OMException
argument_list|(
literal|"Unexpected object type:"
operator|+
name|objInfo
operator|.
name|getResourceType
argument_list|()
argument_list|,
name|INVALID_REQUEST
argument_list|)
throw|;
block|}
block|}
DECL|method|setVolumeManager (VolumeManager volumeManager)
specifier|public
name|void
name|setVolumeManager
parameter_list|(
name|VolumeManager
name|volumeManager
parameter_list|)
block|{
name|this
operator|.
name|volumeManager
operator|=
name|volumeManager
expr_stmt|;
block|}
DECL|method|setBucketManager (BucketManager bucketManager)
specifier|public
name|void
name|setBucketManager
parameter_list|(
name|BucketManager
name|bucketManager
parameter_list|)
block|{
name|this
operator|.
name|bucketManager
operator|=
name|bucketManager
expr_stmt|;
block|}
DECL|method|setKeyManager (KeyManager keyManager)
specifier|public
name|void
name|setKeyManager
parameter_list|(
name|KeyManager
name|keyManager
parameter_list|)
block|{
name|this
operator|.
name|keyManager
operator|=
name|keyManager
expr_stmt|;
block|}
DECL|method|setPrefixManager (PrefixManager prefixManager)
specifier|public
name|void
name|setPrefixManager
parameter_list|(
name|PrefixManager
name|prefixManager
parameter_list|)
block|{
name|this
operator|.
name|prefixManager
operator|=
name|prefixManager
expr_stmt|;
block|}
block|}
end_class

end_unit

