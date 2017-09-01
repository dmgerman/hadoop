begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a.s3guard
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
name|s3guard
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
name|FileSystem
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
comment|/**  * Test specification for MetadataStore contract tests. Supplies configuration  * and MetadataStore instance.  */
end_comment

begin_class
DECL|class|AbstractMSContract
specifier|public
specifier|abstract
class|class
name|AbstractMSContract
block|{
DECL|method|getFileSystem ()
specifier|public
specifier|abstract
name|FileSystem
name|getFileSystem
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|getMetadataStore ()
specifier|public
specifier|abstract
name|MetadataStore
name|getMetadataStore
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

