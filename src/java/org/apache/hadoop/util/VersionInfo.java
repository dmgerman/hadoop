begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
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
name|hadoop
operator|.
name|HadoopVersionAnnotation
import|;
end_import

begin_comment
comment|/**  * This class finds the package info for Hadoop and the HadoopVersionAnnotation  * information.  */
end_comment

begin_class
DECL|class|VersionInfo
specifier|public
class|class
name|VersionInfo
block|{
DECL|field|myPackage
specifier|private
specifier|static
name|Package
name|myPackage
decl_stmt|;
DECL|field|version
specifier|private
specifier|static
name|HadoopVersionAnnotation
name|version
decl_stmt|;
static|static
block|{
name|myPackage
operator|=
name|HadoopVersionAnnotation
operator|.
name|class
operator|.
name|getPackage
argument_list|()
expr_stmt|;
name|version
operator|=
name|myPackage
operator|.
name|getAnnotation
argument_list|(
name|HadoopVersionAnnotation
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get the meta-data for the Hadoop package.    * @return    */
DECL|method|getPackage ()
specifier|static
name|Package
name|getPackage
parameter_list|()
block|{
return|return
name|myPackage
return|;
block|}
comment|/**    * Get the Hadoop version.    * @return the Hadoop version string, eg. "0.6.3-dev"    */
DECL|method|getVersion ()
specifier|public
specifier|static
name|String
name|getVersion
parameter_list|()
block|{
return|return
name|version
operator|!=
literal|null
condition|?
name|version
operator|.
name|version
argument_list|()
else|:
literal|"Unknown"
return|;
block|}
comment|/**    * Get the subversion revision number for the root directory    * @return the revision number, eg. "451451"    */
DECL|method|getRevision ()
specifier|public
specifier|static
name|String
name|getRevision
parameter_list|()
block|{
return|return
name|version
operator|!=
literal|null
condition|?
name|version
operator|.
name|revision
argument_list|()
else|:
literal|"Unknown"
return|;
block|}
comment|/**    * Get the branch on which this originated.    * @return The branch name, e.g. "trunk" or "branches/branch-0.20"    */
DECL|method|getBranch ()
specifier|public
specifier|static
name|String
name|getBranch
parameter_list|()
block|{
return|return
name|version
operator|!=
literal|null
condition|?
name|version
operator|.
name|branch
argument_list|()
else|:
literal|"Unknown"
return|;
block|}
comment|/**    * The date that Hadoop was compiled.    * @return the compilation date in unix date format    */
DECL|method|getDate ()
specifier|public
specifier|static
name|String
name|getDate
parameter_list|()
block|{
return|return
name|version
operator|!=
literal|null
condition|?
name|version
operator|.
name|date
argument_list|()
else|:
literal|"Unknown"
return|;
block|}
comment|/**    * The user that compiled Hadoop.    * @return the username of the user    */
DECL|method|getUser ()
specifier|public
specifier|static
name|String
name|getUser
parameter_list|()
block|{
return|return
name|version
operator|!=
literal|null
condition|?
name|version
operator|.
name|user
argument_list|()
else|:
literal|"Unknown"
return|;
block|}
comment|/**    * Get the subversion URL for the root Hadoop directory.    */
DECL|method|getUrl ()
specifier|public
specifier|static
name|String
name|getUrl
parameter_list|()
block|{
return|return
name|version
operator|!=
literal|null
condition|?
name|version
operator|.
name|url
argument_list|()
else|:
literal|"Unknown"
return|;
block|}
comment|/**    * Get the checksum of the source files from which Hadoop was    * built.    **/
DECL|method|getSrcChecksum ()
specifier|public
specifier|static
name|String
name|getSrcChecksum
parameter_list|()
block|{
return|return
name|version
operator|!=
literal|null
condition|?
name|version
operator|.
name|srcChecksum
argument_list|()
else|:
literal|"Unknown"
return|;
block|}
comment|/**    * Returns the buildVersion which includes version,     * revision, user and date.     */
DECL|method|getBuildVersion ()
specifier|public
specifier|static
name|String
name|getBuildVersion
parameter_list|()
block|{
return|return
name|VersionInfo
operator|.
name|getVersion
argument_list|()
operator|+
literal|" from "
operator|+
name|VersionInfo
operator|.
name|getRevision
argument_list|()
operator|+
literal|" by "
operator|+
name|VersionInfo
operator|.
name|getUser
argument_list|()
operator|+
literal|" source checksum "
operator|+
name|VersionInfo
operator|.
name|getSrcChecksum
argument_list|()
return|;
block|}
DECL|method|main (String[] args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Hadoop "
operator|+
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Subversion "
operator|+
name|getUrl
argument_list|()
operator|+
literal|" -r "
operator|+
name|getRevision
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Compiled by "
operator|+
name|getUser
argument_list|()
operator|+
literal|" on "
operator|+
name|getDate
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"From source with checksum "
operator|+
name|getSrcChecksum
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

