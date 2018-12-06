begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|OzoneConsts
import|;
end_import

begin_comment
comment|/**  * Class representing an unique ozone object.  * */
end_comment

begin_class
DECL|class|OzoneObj
specifier|public
specifier|abstract
class|class
name|OzoneObj
implements|implements
name|IOzoneObj
block|{
DECL|field|resType
specifier|private
specifier|final
name|ResourceType
name|resType
decl_stmt|;
DECL|field|storeType
specifier|private
specifier|final
name|StoreType
name|storeType
decl_stmt|;
comment|// Full path of resource.
DECL|field|path
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
DECL|method|OzoneObj (ResourceType resType, StoreType storeType, String path)
name|OzoneObj
parameter_list|(
name|ResourceType
name|resType
parameter_list|,
name|StoreType
name|storeType
parameter_list|,
name|String
name|path
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|resType
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|storeType
argument_list|)
expr_stmt|;
name|this
operator|.
name|resType
operator|=
name|resType
expr_stmt|;
name|this
operator|.
name|storeType
operator|=
name|storeType
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
block|}
DECL|method|getResourceType ()
specifier|public
name|ResourceType
name|getResourceType
parameter_list|()
block|{
return|return
name|resType
return|;
block|}
DECL|method|getStoreType ()
specifier|public
name|StoreType
name|getStoreType
parameter_list|()
block|{
return|return
name|storeType
return|;
block|}
DECL|method|getVolumeName ()
specifier|public
specifier|abstract
name|String
name|getVolumeName
parameter_list|()
function_decl|;
DECL|method|getBucketName ()
specifier|public
specifier|abstract
name|String
name|getBucketName
parameter_list|()
function_decl|;
DECL|method|getKeyName ()
specifier|public
specifier|abstract
name|String
name|getKeyName
parameter_list|()
function_decl|;
DECL|method|getPath ()
specifier|public
name|String
name|getPath
parameter_list|()
block|{
return|return
name|path
return|;
block|}
comment|/**    * Ozone Objects supported for ACL.    */
DECL|enum|ResourceType
enum|enum
name|ResourceType
block|{
DECL|enumConstant|VOLUME
name|VOLUME
parameter_list|(
name|OzoneConsts
operator|.
name|VOLUME
parameter_list|)
operator|,
DECL|enumConstant|BUCKET
constructor|BUCKET(OzoneConsts.BUCKET
block|)
enum|,
DECL|enumConstant|KEY
name|KEY
parameter_list|(
name|OzoneConsts
operator|.
name|KEY
parameter_list|)
constructor_decl|;
comment|/**      * String value for this Enum.      */
DECL|field|value
specifier|private
specifier|final
name|String
name|value
decl_stmt|;
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|value
return|;
block|}
DECL|method|ResourceType (String resType)
name|ResourceType
parameter_list|(
name|String
name|resType
parameter_list|)
block|{
name|value
operator|=
name|resType
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|/**    * Ozone Objects supported for ACL.    */
end_comment

begin_enum
DECL|enum|StoreType
enum|enum
name|StoreType
block|{
DECL|enumConstant|OZONE
name|OZONE
parameter_list|(
name|OzoneConsts
operator|.
name|OZONE
parameter_list|)
operator|,
DECL|enumConstant|S3
constructor|S3(OzoneConsts.S3
block|)
enum|;
end_enum

begin_comment
comment|/**      * String value for this Enum.      */
end_comment

begin_decl_stmt
DECL|field|value
specifier|private
specifier|final
name|String
name|value
decl_stmt|;
end_decl_stmt

begin_function
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|value
return|;
block|}
end_function

begin_expr_stmt
DECL|method|StoreType (String objType)
name|StoreType
argument_list|(
name|String
name|objType
argument_list|)
block|{
name|value
operator|=
name|objType
block|;     }
end_expr_stmt

unit|} }
end_unit

