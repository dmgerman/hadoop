begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.scm
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|scm
package|;
end_package

begin_comment
comment|/**  * This is a class that tracks versions of SCM.  */
end_comment

begin_class
DECL|class|VersionInfo
specifier|public
specifier|final
class|class
name|VersionInfo
block|{
comment|// We will just be normal and use positive counting numbers for versions.
DECL|field|VERSION_INFOS
specifier|private
specifier|final
specifier|static
name|VersionInfo
index|[]
name|VERSION_INFOS
init|=
block|{
operator|new
name|VersionInfo
argument_list|(
literal|"First version of SCM"
argument_list|,
literal|1
argument_list|)
block|}
decl_stmt|;
DECL|field|DESCRIPTION_KEY
specifier|public
specifier|static
specifier|final
name|String
name|DESCRIPTION_KEY
init|=
literal|"Description"
decl_stmt|;
DECL|field|description
specifier|private
specifier|final
name|String
name|description
decl_stmt|;
DECL|field|version
specifier|private
specifier|final
name|int
name|version
decl_stmt|;
comment|/**    * Never created outside this class.    *    * @param description -- description    * @param version     -- version number    */
DECL|method|VersionInfo (String description, int version)
specifier|private
name|VersionInfo
parameter_list|(
name|String
name|description
parameter_list|,
name|int
name|version
parameter_list|)
block|{
name|this
operator|.
name|description
operator|=
name|description
expr_stmt|;
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
block|}
comment|/**    * Returns all versions.    *    * @return Version info array.    */
DECL|method|getAllVersions ()
specifier|public
specifier|static
name|VersionInfo
index|[]
name|getAllVersions
parameter_list|()
block|{
return|return
name|VERSION_INFOS
operator|.
name|clone
argument_list|()
return|;
block|}
comment|/**    * Returns the latest version.    *    * @return versionInfo    */
DECL|method|getLatestVersion ()
specifier|public
specifier|static
name|VersionInfo
name|getLatestVersion
parameter_list|()
block|{
return|return
name|VERSION_INFOS
index|[
name|VERSION_INFOS
operator|.
name|length
operator|-
literal|1
index|]
return|;
block|}
comment|/**    * Return description.    *    * @return String    */
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
comment|/**    * Return the version.    *    * @return int.    */
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
block|}
end_class

end_unit

