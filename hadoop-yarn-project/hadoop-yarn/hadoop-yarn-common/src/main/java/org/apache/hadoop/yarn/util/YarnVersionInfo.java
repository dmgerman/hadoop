begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|util
package|;
end_package

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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
operator|.
name|VersionInfo
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
name|classification
operator|.
name|InterfaceAudience
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
name|classification
operator|.
name|InterfaceStability
import|;
end_import

begin_comment
comment|/**  * This class finds the package info for Yarn.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|YarnVersionInfo
specifier|public
class|class
name|YarnVersionInfo
extends|extends
name|VersionInfo
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|YarnVersionInfo
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|YARN_VERSION_INFO
specifier|private
specifier|static
name|YarnVersionInfo
name|YARN_VERSION_INFO
init|=
operator|new
name|YarnVersionInfo
argument_list|()
decl_stmt|;
DECL|method|YarnVersionInfo ()
specifier|protected
name|YarnVersionInfo
parameter_list|()
block|{
name|super
argument_list|(
literal|"yarn"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get the YARN version.    * @return the YARN version string, eg. "0.6.3-dev"    */
DECL|method|getVersion ()
specifier|public
specifier|static
name|String
name|getVersion
parameter_list|()
block|{
return|return
name|YARN_VERSION_INFO
operator|.
name|_getVersion
argument_list|()
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
name|YARN_VERSION_INFO
operator|.
name|_getRevision
argument_list|()
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
name|YARN_VERSION_INFO
operator|.
name|_getBranch
argument_list|()
return|;
block|}
comment|/**    * The date that YARN was compiled.    * @return the compilation date in unix date format    */
DECL|method|getDate ()
specifier|public
specifier|static
name|String
name|getDate
parameter_list|()
block|{
return|return
name|YARN_VERSION_INFO
operator|.
name|_getDate
argument_list|()
return|;
block|}
comment|/**    * The user that compiled Yarn.    * @return the username of the user    */
DECL|method|getUser ()
specifier|public
specifier|static
name|String
name|getUser
parameter_list|()
block|{
return|return
name|YARN_VERSION_INFO
operator|.
name|_getUser
argument_list|()
return|;
block|}
comment|/**    * Get the subversion URL for the root YARN directory.    */
DECL|method|getUrl ()
specifier|public
specifier|static
name|String
name|getUrl
parameter_list|()
block|{
return|return
name|YARN_VERSION_INFO
operator|.
name|_getUrl
argument_list|()
return|;
block|}
comment|/**    * Get the checksum of the source files from which YARN was    * built.    **/
DECL|method|getSrcChecksum ()
specifier|public
specifier|static
name|String
name|getSrcChecksum
parameter_list|()
block|{
return|return
name|YARN_VERSION_INFO
operator|.
name|_getSrcChecksum
argument_list|()
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
name|YARN_VERSION_INFO
operator|.
name|_getBuildVersion
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
name|LOG
operator|.
name|debug
argument_list|(
literal|"version: "
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
literal|"YARN "
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

