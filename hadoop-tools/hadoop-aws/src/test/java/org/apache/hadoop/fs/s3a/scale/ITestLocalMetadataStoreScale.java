begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a.scale
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
operator|.
name|scale
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
name|fs
operator|.
name|s3a
operator|.
name|s3guard
operator|.
name|LocalMetadataStore
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
name|s3a
operator|.
name|s3guard
operator|.
name|MetadataStore
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

begin_comment
comment|/**  * Scale test for LocalMetadataStore.  */
end_comment

begin_class
DECL|class|ITestLocalMetadataStoreScale
specifier|public
class|class
name|ITestLocalMetadataStoreScale
extends|extends
name|AbstractITestS3AMetadataStoreScale
block|{
annotation|@
name|Override
DECL|method|createMetadataStore ()
specifier|public
name|MetadataStore
name|createMetadataStore
parameter_list|()
throws|throws
name|IOException
block|{
name|MetadataStore
name|ms
init|=
operator|new
name|LocalMetadataStore
argument_list|()
decl_stmt|;
name|ms
operator|.
name|initialize
argument_list|(
name|getFileSystem
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|ms
return|;
block|}
block|}
end_class

end_unit

