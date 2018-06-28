begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
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
name|conf
operator|.
name|Configuration
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
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ServiceLoader
import|;
end_import

begin_comment
comment|/**  * {@link ServiceLoader}-driven uploader API for storage services supporting  * multipart uploads.  */
end_comment

begin_class
DECL|class|MultipartUploaderFactory
specifier|public
specifier|abstract
class|class
name|MultipartUploaderFactory
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MultipartUploaderFactory
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Multipart Uploaders listed as services.    */
DECL|field|serviceLoader
specifier|private
specifier|static
name|ServiceLoader
argument_list|<
name|MultipartUploaderFactory
argument_list|>
name|serviceLoader
init|=
name|ServiceLoader
operator|.
name|load
argument_list|(
name|MultipartUploaderFactory
operator|.
name|class
argument_list|,
name|MultipartUploaderFactory
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
argument_list|)
decl_stmt|;
comment|// Iterate through the serviceLoader to avoid lazy loading.
comment|// Lazy loading would require synchronization in concurrent use cases.
static|static
block|{
name|Iterator
argument_list|<
name|MultipartUploaderFactory
argument_list|>
name|iterServices
init|=
name|serviceLoader
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iterServices
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|iterServices
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|get (FileSystem fs, Configuration conf)
specifier|public
specifier|static
name|MultipartUploader
name|get
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|MultipartUploader
name|mpu
init|=
literal|null
decl_stmt|;
for|for
control|(
name|MultipartUploaderFactory
name|factory
range|:
name|serviceLoader
control|)
block|{
name|mpu
operator|=
name|factory
operator|.
name|createMultipartUploader
argument_list|(
name|fs
argument_list|,
name|conf
argument_list|)
expr_stmt|;
if|if
condition|(
name|mpu
operator|!=
literal|null
condition|)
block|{
break|break;
block|}
block|}
return|return
name|mpu
return|;
block|}
DECL|method|createMultipartUploader (FileSystem fs, Configuration conf)
specifier|protected
specifier|abstract
name|MultipartUploader
name|createMultipartUploader
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

