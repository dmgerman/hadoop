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
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|IOUtils
import|;
end_import

begin_comment
comment|/**  * This class returns build information about Hadoop components.  */
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
DECL|class|VersionInfo
specifier|public
class|class
name|VersionInfo
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|VersionInfo
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|info
specifier|private
name|Properties
name|info
decl_stmt|;
DECL|method|VersionInfo (String component)
specifier|protected
name|VersionInfo
parameter_list|(
name|String
name|component
parameter_list|)
block|{
name|info
operator|=
operator|new
name|Properties
argument_list|()
expr_stmt|;
name|String
name|versionInfoFile
init|=
name|component
operator|+
literal|"-version-info.properties"
decl_stmt|;
name|InputStream
name|is
init|=
literal|null
decl_stmt|;
try|try
block|{
name|is
operator|=
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getContextClassLoader
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
name|versionInfoFile
argument_list|)
expr_stmt|;
if|if
condition|(
name|is
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Resource not found"
argument_list|)
throw|;
block|}
name|info
operator|.
name|load
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|LogFactory
operator|.
name|getLog
argument_list|(
name|getClass
argument_list|()
argument_list|)
operator|.
name|warn
argument_list|(
literal|"Could not read '"
operator|+
name|versionInfoFile
operator|+
literal|"', "
operator|+
name|ex
operator|.
name|toString
argument_list|()
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|_getVersion ()
specifier|protected
name|String
name|_getVersion
parameter_list|()
block|{
return|return
name|info
operator|.
name|getProperty
argument_list|(
literal|"version"
argument_list|,
literal|"Unknown"
argument_list|)
return|;
block|}
DECL|method|_getRevision ()
specifier|protected
name|String
name|_getRevision
parameter_list|()
block|{
return|return
name|info
operator|.
name|getProperty
argument_list|(
literal|"revision"
argument_list|,
literal|"Unknown"
argument_list|)
return|;
block|}
DECL|method|_getBranch ()
specifier|protected
name|String
name|_getBranch
parameter_list|()
block|{
return|return
name|info
operator|.
name|getProperty
argument_list|(
literal|"branch"
argument_list|,
literal|"Unknown"
argument_list|)
return|;
block|}
DECL|method|_getDate ()
specifier|protected
name|String
name|_getDate
parameter_list|()
block|{
return|return
name|info
operator|.
name|getProperty
argument_list|(
literal|"date"
argument_list|,
literal|"Unknown"
argument_list|)
return|;
block|}
DECL|method|_getUser ()
specifier|protected
name|String
name|_getUser
parameter_list|()
block|{
return|return
name|info
operator|.
name|getProperty
argument_list|(
literal|"user"
argument_list|,
literal|"Unknown"
argument_list|)
return|;
block|}
DECL|method|_getUrl ()
specifier|protected
name|String
name|_getUrl
parameter_list|()
block|{
return|return
name|info
operator|.
name|getProperty
argument_list|(
literal|"url"
argument_list|,
literal|"Unknown"
argument_list|)
return|;
block|}
DECL|method|_getSrcChecksum ()
specifier|protected
name|String
name|_getSrcChecksum
parameter_list|()
block|{
return|return
name|info
operator|.
name|getProperty
argument_list|(
literal|"srcChecksum"
argument_list|,
literal|"Unknown"
argument_list|)
return|;
block|}
DECL|method|_getBuildVersion ()
specifier|protected
name|String
name|_getBuildVersion
parameter_list|()
block|{
return|return
name|getVersion
argument_list|()
operator|+
literal|" from "
operator|+
name|_getRevision
argument_list|()
operator|+
literal|" by "
operator|+
name|_getUser
argument_list|()
operator|+
literal|" source checksum "
operator|+
name|_getSrcChecksum
argument_list|()
return|;
block|}
DECL|method|_getProtocVersion ()
specifier|protected
name|String
name|_getProtocVersion
parameter_list|()
block|{
return|return
name|info
operator|.
name|getProperty
argument_list|(
literal|"protocVersion"
argument_list|,
literal|"Unknown"
argument_list|)
return|;
block|}
DECL|field|COMMON_VERSION_INFO
specifier|private
specifier|static
name|VersionInfo
name|COMMON_VERSION_INFO
init|=
operator|new
name|VersionInfo
argument_list|(
literal|"common"
argument_list|)
decl_stmt|;
comment|/**    * Get the Hadoop version.    * @return the Hadoop version string, eg. "0.6.3-dev"    */
DECL|method|getVersion ()
specifier|public
specifier|static
name|String
name|getVersion
parameter_list|()
block|{
return|return
name|COMMON_VERSION_INFO
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
name|COMMON_VERSION_INFO
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
name|COMMON_VERSION_INFO
operator|.
name|_getBranch
argument_list|()
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
name|COMMON_VERSION_INFO
operator|.
name|_getDate
argument_list|()
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
name|COMMON_VERSION_INFO
operator|.
name|_getUser
argument_list|()
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
name|COMMON_VERSION_INFO
operator|.
name|_getUrl
argument_list|()
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
name|COMMON_VERSION_INFO
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
name|COMMON_VERSION_INFO
operator|.
name|_getBuildVersion
argument_list|()
return|;
block|}
comment|/**    * Returns the protoc version used for the build.    */
DECL|method|getProtocVersion ()
specifier|public
specifier|static
name|String
name|getProtocVersion
parameter_list|()
block|{
return|return
name|COMMON_VERSION_INFO
operator|.
name|_getProtocVersion
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
literal|"Source code repository "
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
literal|"Compiled with protoc "
operator|+
name|getProtocVersion
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
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"This command was run using "
operator|+
name|ClassUtil
operator|.
name|findContainingJar
argument_list|(
name|VersionInfo
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

