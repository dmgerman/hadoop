begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
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
name|ClassUtil
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
name|ThreadUtil
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
name|utils
operator|.
name|HddsVersionInfo
import|;
end_import

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

begin_comment
comment|/**  * This class returns build information about Hadoop components.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|class|OzoneVersionInfo
specifier|public
class|class
name|OzoneVersionInfo
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
name|OzoneVersionInfo
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|info
specifier|private
name|Properties
name|info
decl_stmt|;
DECL|method|OzoneVersionInfo (String component)
specifier|protected
name|OzoneVersionInfo
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
name|ThreadUtil
operator|.
name|getResourceAsStream
argument_list|(
name|OzoneVersionInfo
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
argument_list|,
name|versionInfoFile
argument_list|)
expr_stmt|;
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
name|LoggerFactory
operator|.
name|getLogger
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
DECL|method|_getRelease ()
specifier|protected
name|String
name|_getRelease
parameter_list|()
block|{
return|return
name|info
operator|.
name|getProperty
argument_list|(
literal|"release"
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
name|_getVersion
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
DECL|field|OZONE_VERSION_INFO
specifier|private
specifier|static
name|OzoneVersionInfo
name|OZONE_VERSION_INFO
init|=
operator|new
name|OzoneVersionInfo
argument_list|(
literal|"ozone"
argument_list|)
decl_stmt|;
comment|/**    * Get the Ozone version.    * @return the Ozone version string, eg. "0.6.3-dev"    */
DECL|method|getVersion ()
specifier|public
specifier|static
name|String
name|getVersion
parameter_list|()
block|{
return|return
name|OZONE_VERSION_INFO
operator|.
name|_getVersion
argument_list|()
return|;
block|}
comment|/**    * Get the Ozone release name.    * @return the Ozone release string, eg. "Acadia"    */
DECL|method|getRelease ()
specifier|public
specifier|static
name|String
name|getRelease
parameter_list|()
block|{
return|return
name|OZONE_VERSION_INFO
operator|.
name|_getRelease
argument_list|()
return|;
block|}
comment|/**    * Get the Git commit hash of the repository when compiled.    * @return the commit hash, eg. "18f64065d5db6208daf50b02c1b5ed4ee3ce547a"    */
DECL|method|getRevision ()
specifier|public
specifier|static
name|String
name|getRevision
parameter_list|()
block|{
return|return
name|OZONE_VERSION_INFO
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
name|OZONE_VERSION_INFO
operator|.
name|_getBranch
argument_list|()
return|;
block|}
comment|/**    * The date that Ozone was compiled.    * @return the compilation date in unix date format    */
DECL|method|getDate ()
specifier|public
specifier|static
name|String
name|getDate
parameter_list|()
block|{
return|return
name|OZONE_VERSION_INFO
operator|.
name|_getDate
argument_list|()
return|;
block|}
comment|/**    * The user that compiled Ozone.    * @return the username of the user    */
DECL|method|getUser ()
specifier|public
specifier|static
name|String
name|getUser
parameter_list|()
block|{
return|return
name|OZONE_VERSION_INFO
operator|.
name|_getUser
argument_list|()
return|;
block|}
comment|/**    * Get the URL for the Ozone repository.    * @return the URL of the Ozone repository    */
DECL|method|getUrl ()
specifier|public
specifier|static
name|String
name|getUrl
parameter_list|()
block|{
return|return
name|OZONE_VERSION_INFO
operator|.
name|_getUrl
argument_list|()
return|;
block|}
comment|/**    * Get the checksum of the source files from which Ozone was built.    * @return the checksum of the source files    */
DECL|method|getSrcChecksum ()
specifier|public
specifier|static
name|String
name|getSrcChecksum
parameter_list|()
block|{
return|return
name|OZONE_VERSION_INFO
operator|.
name|_getSrcChecksum
argument_list|()
return|;
block|}
comment|/**    * Returns the buildVersion which includes version,    * revision, user and date.    * @return the buildVersion    */
DECL|method|getBuildVersion ()
specifier|public
specifier|static
name|String
name|getBuildVersion
parameter_list|()
block|{
return|return
name|OZONE_VERSION_INFO
operator|.
name|_getBuildVersion
argument_list|()
return|;
block|}
comment|/**    * Returns the protoc version used for the build.    * @return the protoc version    */
DECL|method|getProtocVersion ()
specifier|public
specifier|static
name|String
name|getProtocVersion
parameter_list|()
block|{
return|return
name|OZONE_VERSION_INFO
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
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"                  //////////////                 \n"
operator|+
literal|"               ////////////////////              \n"
operator|+
literal|"            ////////     ////////////////        \n"
operator|+
literal|"           //////      ////////////////          \n"
operator|+
literal|"          /////      ////////////////  /         \n"
operator|+
literal|"         /////            ////////   ///         \n"
operator|+
literal|"         ////           ////////    /////        \n"
operator|+
literal|"        /////         ////////////////           \n"
operator|+
literal|"        /////       ////////////////   //        \n"
operator|+
literal|"         ////     ///////////////   /////        \n"
operator|+
literal|"         /////  ///////////////     ////         \n"
operator|+
literal|"          /////       //////      /////          \n"
operator|+
literal|"           //////   //////       /////           \n"
operator|+
literal|"             ///////////     ////////            \n"
operator|+
literal|"               //////  ////////////              \n"
operator|+
literal|"               ///   //////////                  \n"
operator|+
literal|"              /    "
operator|+
name|getVersion
argument_list|()
operator|+
literal|"("
operator|+
name|getRelease
argument_list|()
operator|+
literal|")\n"
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
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"This command was run using "
operator|+
name|ClassUtil
operator|.
name|findContainingJar
argument_list|(
name|OzoneVersionInfo
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|HddsVersionInfo
operator|.
name|main
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

