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
comment|/**  *<p>  * Holds information on a directory listing for a {@link NativeFileSystemStore}.  * This includes the {@link FileMetadata files} and directories (their names)  * contained in a directory.  *</p>  *<p>  * This listing may be returned in chunks, so a<code>priorLastKey</code> is  * provided so that the next chunk may be requested.  *</p>  *   * @see NativeFileSystemStore#list(String, int, String)  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|PartialListing
class|class
name|PartialListing
block|{
DECL|field|priorLastKey
specifier|private
specifier|final
name|String
name|priorLastKey
decl_stmt|;
DECL|field|files
specifier|private
specifier|final
name|FileMetadata
index|[]
name|files
decl_stmt|;
DECL|field|commonPrefixes
specifier|private
specifier|final
name|String
index|[]
name|commonPrefixes
decl_stmt|;
DECL|method|PartialListing (String priorLastKey, FileMetadata[] files, String[] commonPrefixes)
specifier|public
name|PartialListing
parameter_list|(
name|String
name|priorLastKey
parameter_list|,
name|FileMetadata
index|[]
name|files
parameter_list|,
name|String
index|[]
name|commonPrefixes
parameter_list|)
block|{
name|this
operator|.
name|priorLastKey
operator|=
name|priorLastKey
expr_stmt|;
name|this
operator|.
name|files
operator|=
name|files
expr_stmt|;
name|this
operator|.
name|commonPrefixes
operator|=
name|commonPrefixes
expr_stmt|;
block|}
DECL|method|getFiles ()
specifier|public
name|FileMetadata
index|[]
name|getFiles
parameter_list|()
block|{
return|return
name|files
return|;
block|}
DECL|method|getCommonPrefixes ()
specifier|public
name|String
index|[]
name|getCommonPrefixes
parameter_list|()
block|{
return|return
name|commonPrefixes
return|;
block|}
DECL|method|getPriorLastKey ()
specifier|public
name|String
name|getPriorLastKey
parameter_list|()
block|{
return|return
name|priorLastKey
return|;
block|}
block|}
end_class

end_unit

