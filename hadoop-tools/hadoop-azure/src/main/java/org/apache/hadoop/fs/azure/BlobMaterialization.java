begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.azure
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|azure
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

begin_comment
comment|/**  * Indicates whether there are actual blobs indicating the existence of  * directories or whether we're inferring their existence from them having files  * in there.  */
end_comment

begin_enum
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|enum|BlobMaterialization
enum|enum
name|BlobMaterialization
block|{
comment|/**    * Indicates a directory that isn't backed by an actual blob, but its    * existence is implied by the fact that there are files in there. For    * example, if the blob /a/b exists then it implies the existence of the /a    * directory if there's no /a blob indicating it.    */
DECL|enumConstant|Implicit
name|Implicit
block|,
comment|/**    * Indicates that the directory is backed by an actual blob that has the    * isFolder metadata on it.    */
DECL|enumConstant|Explicit
name|Explicit
block|, }
end_enum

end_unit

