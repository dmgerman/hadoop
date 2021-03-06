begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.service.containerlaunch
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|service
operator|.
name|containerlaunch
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
name|conf
operator|.
name|Configuration
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
name|StringUtils
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
name|yarn
operator|.
name|api
operator|.
name|ApplicationConstants
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
name|yarn
operator|.
name|conf
operator|.
name|YarnConfiguration
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
name|yarn
operator|.
name|service
operator|.
name|utils
operator|.
name|ServiceUtils
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * build a classpath -allows for entries to be injected in front of  * YARN classpath as well as behind, adds appropriate separators,   * extraction of local classpath, etc.  */
end_comment

begin_class
DECL|class|ClasspathConstructor
specifier|public
class|class
name|ClasspathConstructor
block|{
DECL|field|CLASS_PATH_SEPARATOR
specifier|public
specifier|static
specifier|final
name|String
name|CLASS_PATH_SEPARATOR
init|=
name|ApplicationConstants
operator|.
name|CLASS_PATH_SEPARATOR
decl_stmt|;
DECL|field|pathElements
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|pathElements
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|ClasspathConstructor ()
specifier|public
name|ClasspathConstructor
parameter_list|()
block|{   }
comment|/**    * Get the list of JARs from the YARN settings    * @param config configuration    */
DECL|method|yarnApplicationClasspath (Configuration config)
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|yarnApplicationClasspath
parameter_list|(
name|Configuration
name|config
parameter_list|)
block|{
name|String
index|[]
name|cp
init|=
name|config
operator|.
name|getTrimmedStrings
argument_list|(
name|YarnConfiguration
operator|.
name|YARN_APPLICATION_CLASSPATH
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_YARN_CROSS_PLATFORM_APPLICATION_CLASSPATH
argument_list|)
decl_stmt|;
return|return
name|cp
operator|!=
literal|null
condition|?
name|Arrays
operator|.
name|asList
argument_list|(
name|cp
argument_list|)
else|:
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
literal|0
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|buildClasspath
argument_list|()
return|;
block|}
DECL|method|buildClasspath ()
specifier|public
name|String
name|buildClasspath
parameter_list|()
block|{
return|return
name|ServiceUtils
operator|.
name|join
argument_list|(
name|pathElements
argument_list|,
name|CLASS_PATH_SEPARATOR
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**    * Get a copy of the path list    * @return the JARs    */
DECL|method|getPathElements ()
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getPathElements
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|pathElements
argument_list|)
return|;
block|}
comment|/**    * Append an entry    * @param path path    */
DECL|method|append (String path)
specifier|public
name|void
name|append
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|pathElements
operator|.
name|add
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
comment|/**    * Insert a path at the front of the list. This places it ahead of    * the standard YARN artifacts    * @param path path to the JAR. Absolute or relative -on the target    * system    */
DECL|method|insert (String path)
specifier|public
name|void
name|insert
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|pathElements
operator|.
name|add
argument_list|(
literal|0
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
DECL|method|appendAll (Collection<String> paths)
specifier|public
name|void
name|appendAll
parameter_list|(
name|Collection
argument_list|<
name|String
argument_list|>
name|paths
parameter_list|)
block|{
name|pathElements
operator|.
name|addAll
argument_list|(
name|paths
argument_list|)
expr_stmt|;
block|}
DECL|method|insertAll (Collection<String> paths)
specifier|public
name|void
name|insertAll
parameter_list|(
name|Collection
argument_list|<
name|String
argument_list|>
name|paths
parameter_list|)
block|{
name|pathElements
operator|.
name|addAll
argument_list|(
literal|0
argument_list|,
name|paths
argument_list|)
expr_stmt|;
block|}
DECL|method|addLibDir (String pathToLibDir)
specifier|public
name|void
name|addLibDir
parameter_list|(
name|String
name|pathToLibDir
parameter_list|)
block|{
name|append
argument_list|(
name|buildLibDir
argument_list|(
name|pathToLibDir
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|insertLibDir (String pathToLibDir)
specifier|public
name|void
name|insertLibDir
parameter_list|(
name|String
name|pathToLibDir
parameter_list|)
block|{
name|insert
argument_list|(
name|buildLibDir
argument_list|(
name|pathToLibDir
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|addClassDirectory (String pathToDir)
specifier|public
name|void
name|addClassDirectory
parameter_list|(
name|String
name|pathToDir
parameter_list|)
block|{
name|append
argument_list|(
name|appendDirectoryTerminator
argument_list|(
name|pathToDir
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|insertClassDirectory (String pathToDir)
specifier|public
name|void
name|insertClassDirectory
parameter_list|(
name|String
name|pathToDir
parameter_list|)
block|{
name|insert
argument_list|(
name|buildLibDir
argument_list|(
name|appendDirectoryTerminator
argument_list|(
name|pathToDir
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|addRemoteClasspathEnvVar ()
specifier|public
name|void
name|addRemoteClasspathEnvVar
parameter_list|()
block|{
name|append
argument_list|(
name|ApplicationConstants
operator|.
name|Environment
operator|.
name|CLASSPATH
operator|.
name|$$
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|insertRemoteClasspathEnvVar ()
specifier|public
name|void
name|insertRemoteClasspathEnvVar
parameter_list|()
block|{
name|append
argument_list|(
name|ApplicationConstants
operator|.
name|Environment
operator|.
name|CLASSPATH
operator|.
name|$$
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Build a lib dir path    * @param pathToLibDir path to the directory; may or may not end with a    * trailing space    * @return a path to a lib dir that is compatible with the java classpath    */
DECL|method|buildLibDir (String pathToLibDir)
specifier|public
name|String
name|buildLibDir
parameter_list|(
name|String
name|pathToLibDir
parameter_list|)
block|{
name|String
name|dir
init|=
name|appendDirectoryTerminator
argument_list|(
name|pathToLibDir
argument_list|)
decl_stmt|;
name|dir
operator|+=
literal|"*"
expr_stmt|;
return|return
name|dir
return|;
block|}
DECL|method|appendDirectoryTerminator (String pathToLibDir)
specifier|private
name|String
name|appendDirectoryTerminator
parameter_list|(
name|String
name|pathToLibDir
parameter_list|)
block|{
name|String
name|dir
init|=
name|pathToLibDir
operator|.
name|trim
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|dir
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|dir
operator|+=
literal|"/"
expr_stmt|;
block|}
return|return
name|dir
return|;
block|}
comment|/**    * Split a classpath. This uses the local path separator so MUST NOT    * be used to work with remote classpaths    * @param localpath local path    * @return a splite    */
DECL|method|splitClasspath (String localpath)
specifier|public
name|Collection
argument_list|<
name|String
argument_list|>
name|splitClasspath
parameter_list|(
name|String
name|localpath
parameter_list|)
block|{
name|String
name|separator
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"path.separator"
argument_list|)
decl_stmt|;
return|return
name|StringUtils
operator|.
name|getStringCollection
argument_list|(
name|localpath
argument_list|,
name|separator
argument_list|)
return|;
block|}
comment|/**    * Get the local JVM classpath split up    * @return the list of entries on the JVM classpath env var    */
DECL|method|localJVMClasspath ()
specifier|public
name|Collection
argument_list|<
name|String
argument_list|>
name|localJVMClasspath
parameter_list|()
block|{
return|return
name|splitClasspath
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.class.path"
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

