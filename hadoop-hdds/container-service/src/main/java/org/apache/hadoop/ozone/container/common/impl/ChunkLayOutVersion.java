begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.common.impl
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|impl
package|;
end_package

begin_comment
comment|/**  * Defines layout versions for the Chunks.  */
end_comment

begin_class
DECL|class|ChunkLayOutVersion
specifier|public
specifier|final
class|class
name|ChunkLayOutVersion
block|{
DECL|field|CHUNK_LAYOUT_VERSION_INFOS
specifier|private
specifier|final
specifier|static
name|ChunkLayOutVersion
index|[]
name|CHUNK_LAYOUT_VERSION_INFOS
init|=
block|{
operator|new
name|ChunkLayOutVersion
argument_list|(
literal|1
argument_list|,
literal|"Data without checksums."
argument_list|)
block|}
decl_stmt|;
DECL|field|version
specifier|private
name|int
name|version
decl_stmt|;
DECL|field|description
specifier|private
name|String
name|description
decl_stmt|;
comment|/**    * Never created outside this class.    *    * @param description -- description    * @param version     -- version number    */
DECL|method|ChunkLayOutVersion (int version, String description)
specifier|private
name|ChunkLayOutVersion
parameter_list|(
name|int
name|version
parameter_list|,
name|String
name|description
parameter_list|)
block|{
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
name|this
operator|.
name|description
operator|=
name|description
expr_stmt|;
block|}
comment|/**    * Returns all versions.    *    * @return Version info array.    */
DECL|method|getAllVersions ()
specifier|public
specifier|static
name|ChunkLayOutVersion
index|[]
name|getAllVersions
parameter_list|()
block|{
return|return
name|CHUNK_LAYOUT_VERSION_INFOS
operator|.
name|clone
argument_list|()
return|;
block|}
comment|/**    * Returns the latest version.    *    * @return versionInfo    */
DECL|method|getLatestVersion ()
specifier|public
specifier|static
name|ChunkLayOutVersion
name|getLatestVersion
parameter_list|()
block|{
return|return
name|CHUNK_LAYOUT_VERSION_INFOS
index|[
name|CHUNK_LAYOUT_VERSION_INFOS
operator|.
name|length
operator|-
literal|1
index|]
return|;
block|}
comment|/**    * Return version.    *    * @return int    */
DECL|method|getVersion ()
specifier|public
name|int
name|getVersion
parameter_list|()
block|{
return|return
name|version
return|;
block|}
comment|/**    * Returns description.    * @return String    */
DECL|method|getDescription ()
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
name|description
return|;
block|}
block|}
end_class

end_unit

