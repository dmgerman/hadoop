begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.swift.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|swift
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
name|fs
operator|.
name|Path
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
name|fs
operator|.
name|swift
operator|.
name|exceptions
operator|.
name|SwiftConfigurationException
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
name|fs
operator|.
name|swift
operator|.
name|http
operator|.
name|RestClientBindings
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_comment
comment|/**  * Swift hierarchy mapping of (container, path)  */
end_comment

begin_class
DECL|class|SwiftObjectPath
specifier|public
specifier|final
class|class
name|SwiftObjectPath
block|{
DECL|field|PATH_PART_PATTERN
specifier|private
specifier|static
specifier|final
name|Pattern
name|PATH_PART_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|".*/AUTH_\\w*/"
argument_list|)
decl_stmt|;
comment|/**    * Swift container    */
DECL|field|container
specifier|private
specifier|final
name|String
name|container
decl_stmt|;
comment|/**    * swift object    */
DECL|field|object
specifier|private
specifier|final
name|String
name|object
decl_stmt|;
DECL|field|uriPath
specifier|private
specifier|final
name|String
name|uriPath
decl_stmt|;
comment|/**    * Build an instance from a (host, object) pair    *    * @param container container name    * @param object    object ref underneath the container    */
DECL|method|SwiftObjectPath (String container, String object)
specifier|public
name|SwiftObjectPath
parameter_list|(
name|String
name|container
parameter_list|,
name|String
name|object
parameter_list|)
block|{
if|if
condition|(
name|object
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"object name can't be null"
argument_list|)
throw|;
block|}
name|this
operator|.
name|container
operator|=
name|container
expr_stmt|;
name|this
operator|.
name|object
operator|=
name|URI
operator|.
name|create
argument_list|(
name|object
argument_list|)
operator|.
name|getPath
argument_list|()
expr_stmt|;
name|uriPath
operator|=
name|buildUriPath
argument_list|()
expr_stmt|;
block|}
DECL|method|getContainer ()
specifier|public
name|String
name|getContainer
parameter_list|()
block|{
return|return
name|container
return|;
block|}
DECL|method|getObject ()
specifier|public
name|String
name|getObject
parameter_list|()
block|{
return|return
name|object
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object o)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|SwiftObjectPath
operator|)
condition|)
return|return
literal|false
return|;
specifier|final
name|SwiftObjectPath
name|that
init|=
operator|(
name|SwiftObjectPath
operator|)
name|o
decl_stmt|;
return|return
name|this
operator|.
name|toUriPath
argument_list|()
operator|.
name|equals
argument_list|(
name|that
operator|.
name|toUriPath
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|result
init|=
name|container
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|object
operator|.
name|hashCode
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
DECL|method|buildUriPath ()
specifier|private
name|String
name|buildUriPath
parameter_list|()
block|{
return|return
name|SwiftUtils
operator|.
name|joinPaths
argument_list|(
name|container
argument_list|,
name|object
argument_list|)
return|;
block|}
DECL|method|toUriPath ()
specifier|public
name|String
name|toUriPath
parameter_list|()
block|{
return|return
name|uriPath
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
name|toUriPath
argument_list|()
return|;
block|}
comment|/**    * Test for the object matching a path, ignoring the container    * value.    *    * @param path path string    * @return true iff the object's name matches the path    */
DECL|method|objectMatches (String path)
specifier|public
name|boolean
name|objectMatches
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
name|object
operator|.
name|equals
argument_list|(
name|path
argument_list|)
return|;
block|}
comment|/**    * Query to see if the possibleChild object is a child path of this.    * object.    *    * The test is done by probing for the path of the this object being    * at the start of the second -with a trailing slash, and both    * containers being equal    *    * @param possibleChild possible child dir    * @return true iff the possibleChild is under this object    */
DECL|method|isEqualToOrParentOf (SwiftObjectPath possibleChild)
specifier|public
name|boolean
name|isEqualToOrParentOf
parameter_list|(
name|SwiftObjectPath
name|possibleChild
parameter_list|)
block|{
name|String
name|origPath
init|=
name|toUriPath
argument_list|()
decl_stmt|;
name|String
name|path
init|=
name|origPath
decl_stmt|;
if|if
condition|(
operator|!
name|path
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|path
operator|=
name|path
operator|+
literal|"/"
expr_stmt|;
block|}
name|String
name|childPath
init|=
name|possibleChild
operator|.
name|toUriPath
argument_list|()
decl_stmt|;
return|return
name|childPath
operator|.
name|equals
argument_list|(
name|origPath
argument_list|)
operator|||
name|childPath
operator|.
name|startsWith
argument_list|(
name|path
argument_list|)
return|;
block|}
comment|/**    * Create a path tuple of (container, path), where the container is    * chosen from the host of the URI.    *    * @param uri  uri to start from    * @param path path underneath    * @return a new instance.    * @throws SwiftConfigurationException if the URI host doesn't parse into    *                                     container.service    */
DECL|method|fromPath (URI uri, Path path)
specifier|public
specifier|static
name|SwiftObjectPath
name|fromPath
parameter_list|(
name|URI
name|uri
parameter_list|,
name|Path
name|path
parameter_list|)
throws|throws
name|SwiftConfigurationException
block|{
return|return
name|fromPath
argument_list|(
name|uri
argument_list|,
name|path
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**    * Create a path tuple of (container, path), where the container is    * chosen from the host of the URI.    * A trailing slash can be added to the path. This is the point where    * these /-es need to be appended, because when you construct a {@link Path}    * instance, {@link Path#normalizePath(String, String)} is called    * -which strips off any trailing slash.    *    * @param uri              uri to start from    * @param path             path underneath    * @param addTrailingSlash should a trailing slash be added if there isn't one.    * @return a new instance.    * @throws SwiftConfigurationException if the URI host doesn't parse into    *                                     container.service    */
DECL|method|fromPath (URI uri, Path path, boolean addTrailingSlash)
specifier|public
specifier|static
name|SwiftObjectPath
name|fromPath
parameter_list|(
name|URI
name|uri
parameter_list|,
name|Path
name|path
parameter_list|,
name|boolean
name|addTrailingSlash
parameter_list|)
throws|throws
name|SwiftConfigurationException
block|{
name|String
name|url
init|=
name|path
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
operator|.
name|replaceAll
argument_list|(
name|PATH_PART_PATTERN
operator|.
name|pattern
argument_list|()
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|//add a trailing slash if needed
if|if
condition|(
name|addTrailingSlash
operator|&&
operator|!
name|url
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|url
operator|+=
literal|"/"
expr_stmt|;
block|}
name|String
name|container
init|=
name|uri
operator|.
name|getHost
argument_list|()
decl_stmt|;
if|if
condition|(
name|container
operator|==
literal|null
condition|)
block|{
comment|//no container, not good: replace with ""
name|container
operator|=
literal|""
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|container
operator|.
name|contains
argument_list|(
literal|"."
argument_list|)
condition|)
block|{
comment|//its a container.service URI. Strip the container
name|container
operator|=
name|RestClientBindings
operator|.
name|extractContainerName
argument_list|(
name|container
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|SwiftObjectPath
argument_list|(
name|container
argument_list|,
name|url
argument_list|)
return|;
block|}
block|}
end_class

end_unit

