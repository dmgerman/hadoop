begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.audit
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|audit
package|;
end_package

begin_comment
comment|/**  * Enum to define Audit Action types for OzoneManager.  */
end_comment

begin_enum
DECL|enum|OMAction
specifier|public
enum|enum
name|OMAction
implements|implements
name|AuditAction
block|{
comment|// WRITE Actions
DECL|enumConstant|ALLOCATE_BLOCK
name|ALLOCATE_BLOCK
block|,
DECL|enumConstant|ADD_ALLOCATE_BLOCK
name|ADD_ALLOCATE_BLOCK
block|,
DECL|enumConstant|ALLOCATE_KEY
name|ALLOCATE_KEY
block|,
DECL|enumConstant|APPLY_ALLOCATE_KEY
name|APPLY_ALLOCATE_KEY
block|,
DECL|enumConstant|COMMIT_KEY
name|COMMIT_KEY
block|,
DECL|enumConstant|CREATE_VOLUME
name|CREATE_VOLUME
block|,
DECL|enumConstant|CREATE_BUCKET
name|CREATE_BUCKET
block|,
DECL|enumConstant|CREATE_KEY
name|CREATE_KEY
block|,
DECL|enumConstant|DELETE_VOLUME
name|DELETE_VOLUME
block|,
DECL|enumConstant|DELETE_BUCKET
name|DELETE_BUCKET
block|,
DECL|enumConstant|DELETE_KEY
name|DELETE_KEY
block|,
DECL|enumConstant|RENAME_KEY
name|RENAME_KEY
block|,
DECL|enumConstant|SET_OWNER
name|SET_OWNER
block|,
DECL|enumConstant|SET_QUOTA
name|SET_QUOTA
block|,
DECL|enumConstant|UPDATE_VOLUME
name|UPDATE_VOLUME
block|,
DECL|enumConstant|UPDATE_BUCKET
name|UPDATE_BUCKET
block|,
DECL|enumConstant|UPDATE_KEY
name|UPDATE_KEY
block|,
comment|// READ Actions
DECL|enumConstant|CHECK_VOLUME_ACCESS
name|CHECK_VOLUME_ACCESS
block|,
DECL|enumConstant|LIST_BUCKETS
name|LIST_BUCKETS
block|,
DECL|enumConstant|LIST_VOLUMES
name|LIST_VOLUMES
block|,
DECL|enumConstant|LIST_KEYS
name|LIST_KEYS
block|,
DECL|enumConstant|READ_VOLUME
name|READ_VOLUME
block|,
DECL|enumConstant|READ_BUCKET
name|READ_BUCKET
block|,
DECL|enumConstant|READ_KEY
name|READ_KEY
block|,
DECL|enumConstant|LIST_S3BUCKETS
name|LIST_S3BUCKETS
block|,
DECL|enumConstant|INITIATE_MULTIPART_UPLOAD
name|INITIATE_MULTIPART_UPLOAD
block|,
DECL|enumConstant|COMMIT_MULTIPART_UPLOAD_PARTKEY
name|COMMIT_MULTIPART_UPLOAD_PARTKEY
block|,
DECL|enumConstant|COMPLETE_MULTIPART_UPLOAD
name|COMPLETE_MULTIPART_UPLOAD
block|,
DECL|enumConstant|LIST_MULTIPART_UPLOAD_PARTS
name|LIST_MULTIPART_UPLOAD_PARTS
block|,
comment|//FS Actions
DECL|enumConstant|GET_FILE_STATUS
name|GET_FILE_STATUS
block|,
DECL|enumConstant|CREATE_DIRECTORY
name|CREATE_DIRECTORY
block|,
DECL|enumConstant|CREATE_FILE
name|CREATE_FILE
block|,
DECL|enumConstant|LOOKUP_FILE
name|LOOKUP_FILE
block|,
DECL|enumConstant|LIST_STATUS
name|LIST_STATUS
block|;
annotation|@
name|Override
DECL|method|getAction ()
specifier|public
name|String
name|getAction
parameter_list|()
block|{
return|return
name|this
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_enum

end_unit

