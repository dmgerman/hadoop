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
name|Constants
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
name|fs
operator|.
name|s3a
operator|.
name|Constants
operator|.
name|FAST_UPLOAD_BYTEBUFFER
import|;
end_import

begin_comment
comment|/**  * Use {@link Constants#FAST_UPLOAD_BYTEBUFFER} for buffering.  */
end_comment

begin_class
DECL|class|ITestS3AHugeFilesByteBufferBlocks
specifier|public
class|class
name|ITestS3AHugeFilesByteBufferBlocks
extends|extends
name|AbstractSTestS3AHugeFiles
block|{
DECL|method|getBlockOutputBufferName ()
specifier|protected
name|String
name|getBlockOutputBufferName
parameter_list|()
block|{
return|return
name|FAST_UPLOAD_BYTEBUFFER
return|;
block|}
block|}
end_class

end_unit

