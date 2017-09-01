begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3a
package|;
end_package

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|s3
operator|.
name|transfer
operator|.
name|Upload
import|;
end_import

begin_comment
comment|/**  * Simple struct that contains information about a S3 upload.  */
end_comment

begin_class
DECL|class|UploadInfo
specifier|public
class|class
name|UploadInfo
block|{
DECL|field|upload
specifier|private
specifier|final
name|Upload
name|upload
decl_stmt|;
DECL|field|length
specifier|private
specifier|final
name|long
name|length
decl_stmt|;
DECL|method|UploadInfo (Upload upload, long length)
specifier|public
name|UploadInfo
parameter_list|(
name|Upload
name|upload
parameter_list|,
name|long
name|length
parameter_list|)
block|{
name|this
operator|.
name|upload
operator|=
name|upload
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
block|}
DECL|method|getUpload ()
specifier|public
name|Upload
name|getUpload
parameter_list|()
block|{
return|return
name|upload
return|;
block|}
DECL|method|getLength ()
specifier|public
name|long
name|getLength
parameter_list|()
block|{
return|return
name|length
return|;
block|}
block|}
end_class

end_unit

