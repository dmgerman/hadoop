begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|avro
operator|.
name|reflect
operator|.
name|Stringable
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
name|conf
operator|.
name|Configuration
import|;
end_import

begin_comment
comment|/** Names a file or directory in a {@link FileSystem}.  * Path strings use slash as the directory separator.  A path string is  * absolute if it begins with a slash.  */
end_comment

begin_class
annotation|@
name|Stringable
DECL|class|Path
specifier|public
class|class
name|Path
implements|implements
name|Comparable
block|{
comment|/** The directory separator, a slash. */
DECL|field|SEPARATOR
specifier|public
specifier|static
specifier|final
name|String
name|SEPARATOR
init|=
literal|"/"
decl_stmt|;
DECL|field|SEPARATOR_CHAR
specifier|public
specifier|static
specifier|final
name|char
name|SEPARATOR_CHAR
init|=
literal|'/'
decl_stmt|;
DECL|field|CUR_DIR
specifier|public
specifier|static
specifier|final
name|String
name|CUR_DIR
init|=
literal|"."
decl_stmt|;
DECL|field|WINDOWS
specifier|static
specifier|final
name|boolean
name|WINDOWS
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"os.name"
argument_list|)
operator|.
name|startsWith
argument_list|(
literal|"Windows"
argument_list|)
decl_stmt|;
DECL|field|uri
specifier|private
name|URI
name|uri
decl_stmt|;
comment|// a hierarchical uri
comment|/** Resolve a child path against a parent path. */
DECL|method|Path (String parent, String child)
specifier|public
name|Path
parameter_list|(
name|String
name|parent
parameter_list|,
name|String
name|child
parameter_list|)
block|{
name|this
argument_list|(
operator|new
name|Path
argument_list|(
name|parent
argument_list|)
argument_list|,
operator|new
name|Path
argument_list|(
name|child
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Resolve a child path against a parent path. */
DECL|method|Path (Path parent, String child)
specifier|public
name|Path
parameter_list|(
name|Path
name|parent
parameter_list|,
name|String
name|child
parameter_list|)
block|{
name|this
argument_list|(
name|parent
argument_list|,
operator|new
name|Path
argument_list|(
name|child
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Resolve a child path against a parent path. */
DECL|method|Path (String parent, Path child)
specifier|public
name|Path
parameter_list|(
name|String
name|parent
parameter_list|,
name|Path
name|child
parameter_list|)
block|{
name|this
argument_list|(
operator|new
name|Path
argument_list|(
name|parent
argument_list|)
argument_list|,
name|child
argument_list|)
expr_stmt|;
block|}
comment|/** Resolve a child path against a parent path. */
DECL|method|Path (Path parent, Path child)
specifier|public
name|Path
parameter_list|(
name|Path
name|parent
parameter_list|,
name|Path
name|child
parameter_list|)
block|{
comment|// Add a slash to parent's path so resolution is compatible with URI's
name|URI
name|parentUri
init|=
name|parent
operator|.
name|uri
decl_stmt|;
name|String
name|parentPath
init|=
name|parentUri
operator|.
name|getPath
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|parentPath
operator|.
name|equals
argument_list|(
literal|"/"
argument_list|)
operator|||
name|parentPath
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
operator|)
condition|)
try|try
block|{
name|parentUri
operator|=
operator|new
name|URI
argument_list|(
name|parentUri
operator|.
name|getScheme
argument_list|()
argument_list|,
name|parentUri
operator|.
name|getAuthority
argument_list|()
argument_list|,
name|parentUri
operator|.
name|getPath
argument_list|()
operator|+
literal|"/"
argument_list|,
literal|null
argument_list|,
name|parentUri
operator|.
name|getFragment
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|URI
name|resolved
init|=
name|parentUri
operator|.
name|resolve
argument_list|(
name|child
operator|.
name|uri
argument_list|)
decl_stmt|;
name|initialize
argument_list|(
name|resolved
operator|.
name|getScheme
argument_list|()
argument_list|,
name|resolved
operator|.
name|getAuthority
argument_list|()
argument_list|,
name|normalizePath
argument_list|(
name|resolved
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|,
name|resolved
operator|.
name|getFragment
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|checkPathArg ( String path )
specifier|private
name|void
name|checkPathArg
parameter_list|(
name|String
name|path
parameter_list|)
block|{
comment|// disallow construction of a Path from an empty string
if|if
condition|(
name|path
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Can not create a Path from a null string"
argument_list|)
throw|;
block|}
if|if
condition|(
name|path
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Can not create a Path from an empty string"
argument_list|)
throw|;
block|}
block|}
comment|/** Construct a path from a String.  Path strings are URIs, but with    * unescaped elements and some additional normalization. */
DECL|method|Path (String pathString)
specifier|public
name|Path
parameter_list|(
name|String
name|pathString
parameter_list|)
block|{
name|checkPathArg
argument_list|(
name|pathString
argument_list|)
expr_stmt|;
comment|// We can't use 'new URI(String)' directly, since it assumes things are
comment|// escaped, which we don't require of Paths.
comment|// add a slash in front of paths with Windows drive letters
if|if
condition|(
name|hasWindowsDrive
argument_list|(
name|pathString
argument_list|,
literal|false
argument_list|)
condition|)
name|pathString
operator|=
literal|"/"
operator|+
name|pathString
expr_stmt|;
comment|// parse uri components
name|String
name|scheme
init|=
literal|null
decl_stmt|;
name|String
name|authority
init|=
literal|null
decl_stmt|;
name|int
name|start
init|=
literal|0
decl_stmt|;
comment|// parse uri scheme, if any
name|int
name|colon
init|=
name|pathString
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
decl_stmt|;
name|int
name|slash
init|=
name|pathString
operator|.
name|indexOf
argument_list|(
literal|'/'
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|colon
operator|!=
operator|-
literal|1
operator|)
operator|&&
operator|(
operator|(
name|slash
operator|==
operator|-
literal|1
operator|)
operator|||
operator|(
name|colon
operator|<
name|slash
operator|)
operator|)
condition|)
block|{
comment|// has a scheme
name|scheme
operator|=
name|pathString
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|colon
argument_list|)
expr_stmt|;
name|start
operator|=
name|colon
operator|+
literal|1
expr_stmt|;
block|}
comment|// parse uri authority, if any
if|if
condition|(
name|pathString
operator|.
name|startsWith
argument_list|(
literal|"//"
argument_list|,
name|start
argument_list|)
operator|&&
operator|(
name|pathString
operator|.
name|length
argument_list|()
operator|-
name|start
operator|>
literal|2
operator|)
condition|)
block|{
comment|// has authority
name|int
name|nextSlash
init|=
name|pathString
operator|.
name|indexOf
argument_list|(
literal|'/'
argument_list|,
name|start
operator|+
literal|2
argument_list|)
decl_stmt|;
name|int
name|authEnd
init|=
name|nextSlash
operator|>
literal|0
condition|?
name|nextSlash
else|:
name|pathString
operator|.
name|length
argument_list|()
decl_stmt|;
name|authority
operator|=
name|pathString
operator|.
name|substring
argument_list|(
name|start
operator|+
literal|2
argument_list|,
name|authEnd
argument_list|)
expr_stmt|;
name|start
operator|=
name|authEnd
expr_stmt|;
block|}
comment|// uri path is the rest of the string -- query& fragment not supported
name|String
name|path
init|=
name|pathString
operator|.
name|substring
argument_list|(
name|start
argument_list|,
name|pathString
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|initialize
argument_list|(
name|scheme
argument_list|,
name|authority
argument_list|,
name|path
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * Construct a path from a URI    */
DECL|method|Path (URI aUri)
specifier|public
name|Path
parameter_list|(
name|URI
name|aUri
parameter_list|)
block|{
name|uri
operator|=
name|aUri
expr_stmt|;
block|}
comment|/** Construct a Path from components. */
DECL|method|Path (String scheme, String authority, String path)
specifier|public
name|Path
parameter_list|(
name|String
name|scheme
parameter_list|,
name|String
name|authority
parameter_list|,
name|String
name|path
parameter_list|)
block|{
name|checkPathArg
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|initialize
argument_list|(
name|scheme
argument_list|,
name|authority
argument_list|,
name|path
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|initialize (String scheme, String authority, String path, String fragment)
specifier|private
name|void
name|initialize
parameter_list|(
name|String
name|scheme
parameter_list|,
name|String
name|authority
parameter_list|,
name|String
name|path
parameter_list|,
name|String
name|fragment
parameter_list|)
block|{
try|try
block|{
name|this
operator|.
name|uri
operator|=
operator|new
name|URI
argument_list|(
name|scheme
argument_list|,
name|authority
argument_list|,
name|normalizePath
argument_list|(
name|path
argument_list|)
argument_list|,
literal|null
argument_list|,
name|fragment
argument_list|)
operator|.
name|normalize
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|normalizePath (String path)
specifier|private
name|String
name|normalizePath
parameter_list|(
name|String
name|path
parameter_list|)
block|{
comment|// remove double slashes& backslashes
name|path
operator|=
name|path
operator|.
name|replace
argument_list|(
literal|"//"
argument_list|,
literal|"/"
argument_list|)
expr_stmt|;
name|path
operator|=
name|path
operator|.
name|replace
argument_list|(
literal|"\\"
argument_list|,
literal|"/"
argument_list|)
expr_stmt|;
comment|// trim trailing slash from non-root path (ignoring windows drive)
name|int
name|minLength
init|=
name|hasWindowsDrive
argument_list|(
name|path
argument_list|,
literal|true
argument_list|)
condition|?
literal|4
else|:
literal|1
decl_stmt|;
if|if
condition|(
name|path
operator|.
name|length
argument_list|()
operator|>
name|minLength
operator|&&
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
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|path
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
return|return
name|path
return|;
block|}
DECL|method|hasWindowsDrive (String path, boolean slashed)
specifier|private
name|boolean
name|hasWindowsDrive
parameter_list|(
name|String
name|path
parameter_list|,
name|boolean
name|slashed
parameter_list|)
block|{
if|if
condition|(
operator|!
name|WINDOWS
condition|)
return|return
literal|false
return|;
name|int
name|start
init|=
name|slashed
condition|?
literal|1
else|:
literal|0
decl_stmt|;
return|return
name|path
operator|.
name|length
argument_list|()
operator|>=
name|start
operator|+
literal|2
operator|&&
operator|(
name|slashed
condition|?
name|path
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'/'
else|:
literal|true
operator|)
operator|&&
name|path
operator|.
name|charAt
argument_list|(
name|start
operator|+
literal|1
argument_list|)
operator|==
literal|':'
operator|&&
operator|(
operator|(
name|path
operator|.
name|charAt
argument_list|(
name|start
argument_list|)
operator|>=
literal|'A'
operator|&&
name|path
operator|.
name|charAt
argument_list|(
name|start
argument_list|)
operator|<=
literal|'Z'
operator|)
operator|||
operator|(
name|path
operator|.
name|charAt
argument_list|(
name|start
argument_list|)
operator|>=
literal|'a'
operator|&&
name|path
operator|.
name|charAt
argument_list|(
name|start
argument_list|)
operator|<=
literal|'z'
operator|)
operator|)
return|;
block|}
comment|/** Convert this to a URI. */
DECL|method|toUri ()
specifier|public
name|URI
name|toUri
parameter_list|()
block|{
return|return
name|uri
return|;
block|}
comment|/** Return the FileSystem that owns this Path. */
DECL|method|getFileSystem (Configuration conf)
specifier|public
name|FileSystem
name|getFileSystem
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|FileSystem
operator|.
name|get
argument_list|(
name|this
operator|.
name|toUri
argument_list|()
argument_list|,
name|conf
argument_list|)
return|;
block|}
comment|/**    *  True if the path component (i.e. directory) of this URI is absolute.    */
DECL|method|isUriPathAbsolute ()
specifier|public
name|boolean
name|isUriPathAbsolute
parameter_list|()
block|{
name|int
name|start
init|=
name|hasWindowsDrive
argument_list|(
name|uri
operator|.
name|getPath
argument_list|()
argument_list|,
literal|true
argument_list|)
condition|?
literal|3
else|:
literal|0
decl_stmt|;
return|return
name|uri
operator|.
name|getPath
argument_list|()
operator|.
name|startsWith
argument_list|(
name|SEPARATOR
argument_list|,
name|start
argument_list|)
return|;
block|}
comment|/** True if the path component of this URI is absolute. */
comment|/**    * There is some ambiguity here. An absolute path is a slash    * relative name without a scheme or an authority.    * So either this method was incorrectly named or its    * implementation is incorrect.    */
DECL|method|isAbsolute ()
specifier|public
name|boolean
name|isAbsolute
parameter_list|()
block|{
return|return
name|isUriPathAbsolute
argument_list|()
return|;
block|}
comment|/** Returns the final component of this path.*/
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
name|String
name|path
init|=
name|uri
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|int
name|slash
init|=
name|path
operator|.
name|lastIndexOf
argument_list|(
name|SEPARATOR
argument_list|)
decl_stmt|;
return|return
name|path
operator|.
name|substring
argument_list|(
name|slash
operator|+
literal|1
argument_list|)
return|;
block|}
comment|/** Returns the parent of a path or null if at root. */
DECL|method|getParent ()
specifier|public
name|Path
name|getParent
parameter_list|()
block|{
name|String
name|path
init|=
name|uri
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|int
name|lastSlash
init|=
name|path
operator|.
name|lastIndexOf
argument_list|(
literal|'/'
argument_list|)
decl_stmt|;
name|int
name|start
init|=
name|hasWindowsDrive
argument_list|(
name|path
argument_list|,
literal|true
argument_list|)
condition|?
literal|3
else|:
literal|0
decl_stmt|;
if|if
condition|(
operator|(
name|path
operator|.
name|length
argument_list|()
operator|==
name|start
operator|)
operator|||
comment|// empty path
operator|(
name|lastSlash
operator|==
name|start
operator|&&
name|path
operator|.
name|length
argument_list|()
operator|==
name|start
operator|+
literal|1
operator|)
condition|)
block|{
comment|// at root
return|return
literal|null
return|;
block|}
name|String
name|parent
decl_stmt|;
if|if
condition|(
name|lastSlash
operator|==
operator|-
literal|1
condition|)
block|{
name|parent
operator|=
name|CUR_DIR
expr_stmt|;
block|}
else|else
block|{
name|int
name|end
init|=
name|hasWindowsDrive
argument_list|(
name|path
argument_list|,
literal|true
argument_list|)
condition|?
literal|3
else|:
literal|0
decl_stmt|;
name|parent
operator|=
name|path
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|lastSlash
operator|==
name|end
condition|?
name|end
operator|+
literal|1
else|:
name|lastSlash
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|Path
argument_list|(
name|uri
operator|.
name|getScheme
argument_list|()
argument_list|,
name|uri
operator|.
name|getAuthority
argument_list|()
argument_list|,
name|parent
argument_list|)
return|;
block|}
comment|/** Adds a suffix to the final name in the path.*/
DECL|method|suffix (String suffix)
specifier|public
name|Path
name|suffix
parameter_list|(
name|String
name|suffix
parameter_list|)
block|{
return|return
operator|new
name|Path
argument_list|(
name|getParent
argument_list|()
argument_list|,
name|getName
argument_list|()
operator|+
name|suffix
argument_list|)
return|;
block|}
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
comment|// we can't use uri.toString(), which escapes everything, because we want
comment|// illegal characters unescaped in the string, for glob processing, etc.
name|StringBuffer
name|buffer
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
if|if
condition|(
name|uri
operator|.
name|getScheme
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
name|uri
operator|.
name|getScheme
argument_list|()
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|":"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|uri
operator|.
name|getAuthority
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|"//"
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|uri
operator|.
name|getAuthority
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|uri
operator|.
name|getPath
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|String
name|path
init|=
name|uri
operator|.
name|getPath
argument_list|()
decl_stmt|;
if|if
condition|(
name|path
operator|.
name|indexOf
argument_list|(
literal|'/'
argument_list|)
operator|==
literal|0
operator|&&
name|hasWindowsDrive
argument_list|(
name|path
argument_list|,
literal|true
argument_list|)
operator|&&
comment|// has windows drive
name|uri
operator|.
name|getScheme
argument_list|()
operator|==
literal|null
operator|&&
comment|// but no scheme
name|uri
operator|.
name|getAuthority
argument_list|()
operator|==
literal|null
condition|)
comment|// or authority
name|path
operator|=
name|path
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// remove slash before drive
name|buffer
operator|.
name|append
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|uri
operator|.
name|getFragment
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|"#"
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|uri
operator|.
name|getFragment
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
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
operator|!
operator|(
name|o
operator|instanceof
name|Path
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|Path
name|that
init|=
operator|(
name|Path
operator|)
name|o
decl_stmt|;
return|return
name|this
operator|.
name|uri
operator|.
name|equals
argument_list|(
name|that
operator|.
name|uri
argument_list|)
return|;
block|}
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|uri
operator|.
name|hashCode
argument_list|()
return|;
block|}
DECL|method|compareTo (Object o)
specifier|public
name|int
name|compareTo
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
name|Path
name|that
init|=
operator|(
name|Path
operator|)
name|o
decl_stmt|;
return|return
name|this
operator|.
name|uri
operator|.
name|compareTo
argument_list|(
name|that
operator|.
name|uri
argument_list|)
return|;
block|}
comment|/** Return the number of elements in this path. */
DECL|method|depth ()
specifier|public
name|int
name|depth
parameter_list|()
block|{
name|String
name|path
init|=
name|uri
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|int
name|depth
init|=
literal|0
decl_stmt|;
name|int
name|slash
init|=
name|path
operator|.
name|length
argument_list|()
operator|==
literal|1
operator|&&
name|path
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'/'
condition|?
operator|-
literal|1
else|:
literal|0
decl_stmt|;
while|while
condition|(
name|slash
operator|!=
operator|-
literal|1
condition|)
block|{
name|depth
operator|++
expr_stmt|;
name|slash
operator|=
name|path
operator|.
name|indexOf
argument_list|(
name|SEPARATOR
argument_list|,
name|slash
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
return|return
name|depth
return|;
block|}
comment|/**    *  Returns a qualified path object.    *      *  Deprecated - use {@link #makeQualified(URI, Path)}    */
annotation|@
name|Deprecated
DECL|method|makeQualified (FileSystem fs)
specifier|public
name|Path
name|makeQualified
parameter_list|(
name|FileSystem
name|fs
parameter_list|)
block|{
return|return
name|makeQualified
argument_list|(
name|fs
operator|.
name|getUri
argument_list|()
argument_list|,
name|fs
operator|.
name|getWorkingDirectory
argument_list|()
argument_list|)
return|;
block|}
comment|/** Returns a qualified path object. */
DECL|method|makeQualified (URI defaultUri, Path workingDir )
specifier|public
name|Path
name|makeQualified
parameter_list|(
name|URI
name|defaultUri
parameter_list|,
name|Path
name|workingDir
parameter_list|)
block|{
name|Path
name|path
init|=
name|this
decl_stmt|;
if|if
condition|(
operator|!
name|isAbsolute
argument_list|()
condition|)
block|{
name|path
operator|=
operator|new
name|Path
argument_list|(
name|workingDir
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
name|URI
name|pathUri
init|=
name|path
operator|.
name|toUri
argument_list|()
decl_stmt|;
name|String
name|scheme
init|=
name|pathUri
operator|.
name|getScheme
argument_list|()
decl_stmt|;
name|String
name|authority
init|=
name|pathUri
operator|.
name|getAuthority
argument_list|()
decl_stmt|;
name|String
name|fragment
init|=
name|pathUri
operator|.
name|getFragment
argument_list|()
decl_stmt|;
if|if
condition|(
name|scheme
operator|!=
literal|null
operator|&&
operator|(
name|authority
operator|!=
literal|null
operator|||
name|defaultUri
operator|.
name|getAuthority
argument_list|()
operator|==
literal|null
operator|)
condition|)
return|return
name|path
return|;
if|if
condition|(
name|scheme
operator|==
literal|null
condition|)
block|{
name|scheme
operator|=
name|defaultUri
operator|.
name|getScheme
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|authority
operator|==
literal|null
condition|)
block|{
name|authority
operator|=
name|defaultUri
operator|.
name|getAuthority
argument_list|()
expr_stmt|;
if|if
condition|(
name|authority
operator|==
literal|null
condition|)
block|{
name|authority
operator|=
literal|""
expr_stmt|;
block|}
block|}
name|URI
name|newUri
init|=
literal|null
decl_stmt|;
try|try
block|{
name|newUri
operator|=
operator|new
name|URI
argument_list|(
name|scheme
argument_list|,
name|authority
argument_list|,
name|normalizePath
argument_list|(
name|pathUri
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|,
literal|null
argument_list|,
name|fragment
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|e
argument_list|)
throw|;
block|}
return|return
operator|new
name|Path
argument_list|(
name|newUri
argument_list|)
return|;
block|}
block|}
end_class

end_unit

