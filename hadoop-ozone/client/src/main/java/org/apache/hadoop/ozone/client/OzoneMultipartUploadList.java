begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.client
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|client
package|;
end_package

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
comment|/**  * List of in-flight MPU upoads.  */
end_comment

begin_class
DECL|class|OzoneMultipartUploadList
specifier|public
class|class
name|OzoneMultipartUploadList
block|{
DECL|field|uploads
specifier|private
name|List
argument_list|<
name|OzoneMultipartUpload
argument_list|>
name|uploads
decl_stmt|;
DECL|method|OzoneMultipartUploadList ( List<OzoneMultipartUpload> uploads)
specifier|public
name|OzoneMultipartUploadList
parameter_list|(
name|List
argument_list|<
name|OzoneMultipartUpload
argument_list|>
name|uploads
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|uploads
argument_list|)
expr_stmt|;
name|this
operator|.
name|uploads
operator|=
name|uploads
expr_stmt|;
block|}
DECL|method|getUploads ()
specifier|public
name|List
argument_list|<
name|OzoneMultipartUpload
argument_list|>
name|getUploads
parameter_list|()
block|{
return|return
name|uploads
return|;
block|}
DECL|method|setUploads ( List<OzoneMultipartUpload> uploads)
specifier|public
name|void
name|setUploads
parameter_list|(
name|List
argument_list|<
name|OzoneMultipartUpload
argument_list|>
name|uploads
parameter_list|)
block|{
name|this
operator|.
name|uploads
operator|=
name|uploads
expr_stmt|;
block|}
block|}
end_class

end_unit

