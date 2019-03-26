begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.s3.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|s3
operator|.
name|util
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|codec
operator|.
name|digest
operator|.
name|DigestUtils
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

begin_comment
comment|/**  * Ozone util for S3 related operations.  */
end_comment

begin_class
DECL|class|OzoneS3Util
specifier|public
specifier|final
class|class
name|OzoneS3Util
block|{
DECL|method|OzoneS3Util ()
specifier|private
name|OzoneS3Util
parameter_list|()
block|{   }
DECL|method|getVolumeName (String userName)
specifier|public
specifier|static
name|String
name|getVolumeName
parameter_list|(
name|String
name|userName
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|userName
argument_list|)
expr_stmt|;
return|return
name|DigestUtils
operator|.
name|md5Hex
argument_list|(
name|userName
argument_list|)
return|;
block|}
block|}
end_class

end_unit

