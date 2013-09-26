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
name|io
operator|.
name|FileNotFoundException
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
name|List
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

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|Globber
class|class
name|Globber
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|Globber
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|fs
specifier|private
specifier|final
name|FileSystem
name|fs
decl_stmt|;
DECL|field|fc
specifier|private
specifier|final
name|FileContext
name|fc
decl_stmt|;
DECL|field|pathPattern
specifier|private
specifier|final
name|Path
name|pathPattern
decl_stmt|;
DECL|field|filter
specifier|private
specifier|final
name|PathFilter
name|filter
decl_stmt|;
DECL|method|Globber (FileSystem fs, Path pathPattern, PathFilter filter)
specifier|public
name|Globber
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|pathPattern
parameter_list|,
name|PathFilter
name|filter
parameter_list|)
block|{
name|this
operator|.
name|fs
operator|=
name|fs
expr_stmt|;
name|this
operator|.
name|fc
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|pathPattern
operator|=
name|pathPattern
expr_stmt|;
name|this
operator|.
name|filter
operator|=
name|filter
expr_stmt|;
block|}
DECL|method|Globber (FileContext fc, Path pathPattern, PathFilter filter)
specifier|public
name|Globber
parameter_list|(
name|FileContext
name|fc
parameter_list|,
name|Path
name|pathPattern
parameter_list|,
name|PathFilter
name|filter
parameter_list|)
block|{
name|this
operator|.
name|fs
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|fc
operator|=
name|fc
expr_stmt|;
name|this
operator|.
name|pathPattern
operator|=
name|pathPattern
expr_stmt|;
name|this
operator|.
name|filter
operator|=
name|filter
expr_stmt|;
block|}
DECL|method|getFileStatus (Path path)
specifier|private
name|FileStatus
name|getFileStatus
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
if|if
condition|(
name|fs
operator|!=
literal|null
condition|)
block|{
return|return
name|fs
operator|.
name|getFileStatus
argument_list|(
name|path
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|fc
operator|.
name|getFileStatus
argument_list|(
name|path
argument_list|)
return|;
block|}
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
DECL|method|listStatus (Path path)
specifier|private
name|FileStatus
index|[]
name|listStatus
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
if|if
condition|(
name|fs
operator|!=
literal|null
condition|)
block|{
return|return
name|fs
operator|.
name|listStatus
argument_list|(
name|path
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|fc
operator|.
name|util
argument_list|()
operator|.
name|listStatus
argument_list|(
name|path
argument_list|)
return|;
block|}
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{
return|return
operator|new
name|FileStatus
index|[
literal|0
index|]
return|;
block|}
block|}
DECL|method|fixRelativePart (Path path)
specifier|private
name|Path
name|fixRelativePart
parameter_list|(
name|Path
name|path
parameter_list|)
block|{
if|if
condition|(
name|fs
operator|!=
literal|null
condition|)
block|{
return|return
name|fs
operator|.
name|fixRelativePart
argument_list|(
name|path
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|fc
operator|.
name|fixRelativePart
argument_list|(
name|path
argument_list|)
return|;
block|}
block|}
comment|/**    * Convert a path component that contains backslash ecape sequences to a    * literal string.  This is necessary when you want to explicitly refer to a    * path that contains globber metacharacters.    */
DECL|method|unescapePathComponent (String name)
specifier|private
specifier|static
name|String
name|unescapePathComponent
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|name
operator|.
name|replaceAll
argument_list|(
literal|"\\\\(.)"
argument_list|,
literal|"$1"
argument_list|)
return|;
block|}
comment|/**    * Translate an absolute path into a list of path components.    * We merge double slashes into a single slash here.    * POSIX root path, i.e. '/', does not get an entry in the list.    */
DECL|method|getPathComponents (String path)
specifier|private
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|getPathComponents
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|ArrayList
argument_list|<
name|String
argument_list|>
name|ret
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|component
range|:
name|path
operator|.
name|split
argument_list|(
name|Path
operator|.
name|SEPARATOR
argument_list|)
control|)
block|{
if|if
condition|(
operator|!
name|component
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|ret
operator|.
name|add
argument_list|(
name|component
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|ret
return|;
block|}
DECL|method|schemeFromPath (Path path)
specifier|private
name|String
name|schemeFromPath
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|scheme
init|=
name|path
operator|.
name|toUri
argument_list|()
operator|.
name|getScheme
argument_list|()
decl_stmt|;
if|if
condition|(
name|scheme
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|fs
operator|!=
literal|null
condition|)
block|{
name|scheme
operator|=
name|fs
operator|.
name|getUri
argument_list|()
operator|.
name|getScheme
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|scheme
operator|=
name|fc
operator|.
name|getDefaultFileSystem
argument_list|()
operator|.
name|getUri
argument_list|()
operator|.
name|getScheme
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|scheme
return|;
block|}
DECL|method|authorityFromPath (Path path)
specifier|private
name|String
name|authorityFromPath
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|authority
init|=
name|path
operator|.
name|toUri
argument_list|()
operator|.
name|getAuthority
argument_list|()
decl_stmt|;
if|if
condition|(
name|authority
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|fs
operator|!=
literal|null
condition|)
block|{
name|authority
operator|=
name|fs
operator|.
name|getUri
argument_list|()
operator|.
name|getAuthority
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|authority
operator|=
name|fc
operator|.
name|getDefaultFileSystem
argument_list|()
operator|.
name|getUri
argument_list|()
operator|.
name|getAuthority
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|authority
return|;
block|}
DECL|method|glob ()
specifier|public
name|FileStatus
index|[]
name|glob
parameter_list|()
throws|throws
name|IOException
block|{
comment|// First we get the scheme and authority of the pattern that was passed
comment|// in.
name|String
name|scheme
init|=
name|schemeFromPath
argument_list|(
name|pathPattern
argument_list|)
decl_stmt|;
name|String
name|authority
init|=
name|authorityFromPath
argument_list|(
name|pathPattern
argument_list|)
decl_stmt|;
comment|// Next we strip off everything except the pathname itself, and expand all
comment|// globs.  Expansion is a process which turns "grouping" clauses,
comment|// expressed as brackets, into separate path patterns.
name|String
name|pathPatternString
init|=
name|pathPattern
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|flattenedPatterns
init|=
name|GlobExpander
operator|.
name|expand
argument_list|(
name|pathPatternString
argument_list|)
decl_stmt|;
comment|// Now loop over all flattened patterns.  In every case, we'll be trying to
comment|// match them to entries in the filesystem.
name|ArrayList
argument_list|<
name|FileStatus
argument_list|>
name|results
init|=
operator|new
name|ArrayList
argument_list|<
name|FileStatus
argument_list|>
argument_list|(
name|flattenedPatterns
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|boolean
name|sawWildcard
init|=
literal|false
decl_stmt|;
for|for
control|(
name|String
name|flatPattern
range|:
name|flattenedPatterns
control|)
block|{
comment|// Get the absolute path for this flattened pattern.  We couldn't do
comment|// this prior to flattening because of patterns like {/,a}, where which
comment|// path you go down influences how the path must be made absolute.
name|Path
name|absPattern
init|=
name|fixRelativePart
argument_list|(
operator|new
name|Path
argument_list|(
name|flatPattern
operator|.
name|isEmpty
argument_list|()
condition|?
name|Path
operator|.
name|CUR_DIR
else|:
name|flatPattern
argument_list|)
argument_list|)
decl_stmt|;
comment|// Now we break the flattened, absolute pattern into path components.
comment|// For example, /a/*/c would be broken into the list [a, *, c]
name|List
argument_list|<
name|String
argument_list|>
name|components
init|=
name|getPathComponents
argument_list|(
name|absPattern
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
comment|// Starting out at the root of the filesystem, we try to match
comment|// filesystem entries against pattern components.
name|ArrayList
argument_list|<
name|FileStatus
argument_list|>
name|candidates
init|=
operator|new
name|ArrayList
argument_list|<
name|FileStatus
argument_list|>
argument_list|(
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|Path
operator|.
name|WINDOWS
operator|&&
operator|!
name|components
operator|.
name|isEmpty
argument_list|()
operator|&&
name|Path
operator|.
name|isWindowsAbsolutePath
argument_list|(
name|absPattern
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|,
literal|true
argument_list|)
condition|)
block|{
comment|// On Windows the path could begin with a drive letter, e.g. /E:/foo.
comment|// We will skip matching the drive letter and start from listing the
comment|// root of the filesystem on that drive.
name|String
name|driveLetter
init|=
name|components
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|candidates
operator|.
name|add
argument_list|(
operator|new
name|FileStatus
argument_list|(
literal|0
argument_list|,
literal|true
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
operator|new
name|Path
argument_list|(
name|scheme
argument_list|,
name|authority
argument_list|,
name|Path
operator|.
name|SEPARATOR
operator|+
name|driveLetter
operator|+
name|Path
operator|.
name|SEPARATOR
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|candidates
operator|.
name|add
argument_list|(
operator|new
name|FileStatus
argument_list|(
literal|0
argument_list|,
literal|true
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
operator|new
name|Path
argument_list|(
name|scheme
argument_list|,
name|authority
argument_list|,
name|Path
operator|.
name|SEPARATOR
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|componentIdx
init|=
literal|0
init|;
name|componentIdx
operator|<
name|components
operator|.
name|size
argument_list|()
condition|;
name|componentIdx
operator|++
control|)
block|{
name|ArrayList
argument_list|<
name|FileStatus
argument_list|>
name|newCandidates
init|=
operator|new
name|ArrayList
argument_list|<
name|FileStatus
argument_list|>
argument_list|(
name|candidates
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|GlobFilter
name|globFilter
init|=
operator|new
name|GlobFilter
argument_list|(
name|components
operator|.
name|get
argument_list|(
name|componentIdx
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|component
init|=
name|unescapePathComponent
argument_list|(
name|components
operator|.
name|get
argument_list|(
name|componentIdx
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|globFilter
operator|.
name|hasPattern
argument_list|()
condition|)
block|{
name|sawWildcard
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|candidates
operator|.
name|isEmpty
argument_list|()
operator|&&
name|sawWildcard
condition|)
block|{
comment|// Optimization: if there are no more candidates left, stop examining
comment|// the path components.  We can only do this if we've already seen
comment|// a wildcard component-- otherwise, we still need to visit all path
comment|// components in case one of them is a wildcard.
break|break;
block|}
if|if
condition|(
operator|(
name|componentIdx
operator|<
name|components
operator|.
name|size
argument_list|()
operator|-
literal|1
operator|)
operator|&&
operator|(
operator|!
name|globFilter
operator|.
name|hasPattern
argument_list|()
operator|)
condition|)
block|{
comment|// Optimization: if this is not the terminal path component, and we
comment|// are not matching against a glob, assume that it exists.  If it
comment|// doesn't exist, we'll find out later when resolving a later glob
comment|// or the terminal path component.
for|for
control|(
name|FileStatus
name|candidate
range|:
name|candidates
control|)
block|{
name|candidate
operator|.
name|setPath
argument_list|(
operator|new
name|Path
argument_list|(
name|candidate
operator|.
name|getPath
argument_list|()
argument_list|,
name|component
argument_list|)
argument_list|)
expr_stmt|;
block|}
continue|continue;
block|}
for|for
control|(
name|FileStatus
name|candidate
range|:
name|candidates
control|)
block|{
if|if
condition|(
name|globFilter
operator|.
name|hasPattern
argument_list|()
condition|)
block|{
name|FileStatus
index|[]
name|children
init|=
name|listStatus
argument_list|(
name|candidate
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|children
operator|.
name|length
operator|==
literal|1
condition|)
block|{
comment|// If we get back only one result, this could be either a listing
comment|// of a directory with one entry, or it could reflect the fact
comment|// that what we listed resolved to a file.
comment|//
comment|// Unfortunately, we can't just compare the returned paths to
comment|// figure this out.  Consider the case where you have /a/b, where
comment|// b is a symlink to "..".  In that case, listing /a/b will give
comment|// back "/a/b" again.  If we just went by returned pathname, we'd
comment|// incorrectly conclude that /a/b was a file and should not match
comment|// /a/*/*.  So we use getFileStatus of the path we just listed to
comment|// disambiguate.
if|if
condition|(
operator|!
name|getFileStatus
argument_list|(
name|candidate
operator|.
name|getPath
argument_list|()
argument_list|)
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
continue|continue;
block|}
block|}
for|for
control|(
name|FileStatus
name|child
range|:
name|children
control|)
block|{
comment|// Set the child path based on the parent path.
name|child
operator|.
name|setPath
argument_list|(
operator|new
name|Path
argument_list|(
name|candidate
operator|.
name|getPath
argument_list|()
argument_list|,
name|child
operator|.
name|getPath
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|globFilter
operator|.
name|accept
argument_list|(
name|child
operator|.
name|getPath
argument_list|()
argument_list|)
condition|)
block|{
name|newCandidates
operator|.
name|add
argument_list|(
name|child
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
comment|// When dealing with non-glob components, use getFileStatus
comment|// instead of listStatus.  This is an optimization, but it also
comment|// is necessary for correctness in HDFS, since there are some
comment|// special HDFS directories like .reserved and .snapshot that are
comment|// not visible to listStatus, but which do exist.  (See HADOOP-9877)
name|FileStatus
name|childStatus
init|=
name|getFileStatus
argument_list|(
operator|new
name|Path
argument_list|(
name|candidate
operator|.
name|getPath
argument_list|()
argument_list|,
name|component
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|childStatus
operator|!=
literal|null
condition|)
block|{
name|newCandidates
operator|.
name|add
argument_list|(
name|childStatus
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|candidates
operator|=
name|newCandidates
expr_stmt|;
block|}
for|for
control|(
name|FileStatus
name|status
range|:
name|candidates
control|)
block|{
comment|// HADOOP-3497 semantics: the user-defined filter is applied at the
comment|// end, once the full path is built up.
if|if
condition|(
name|filter
operator|.
name|accept
argument_list|(
name|status
operator|.
name|getPath
argument_list|()
argument_list|)
condition|)
block|{
name|results
operator|.
name|add
argument_list|(
name|status
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/*      * When the input pattern "looks" like just a simple filename, and we      * can't find it, we return null rather than an empty array.      * This is a special case which the shell relies on.      *      * To be more precise: if there were no results, AND there were no      * groupings (aka brackets), and no wildcards in the input (aka stars),      * we return null.      */
if|if
condition|(
operator|(
operator|!
name|sawWildcard
operator|)
operator|&&
name|results
operator|.
name|isEmpty
argument_list|()
operator|&&
operator|(
name|flattenedPatterns
operator|.
name|size
argument_list|()
operator|<=
literal|1
operator|)
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|results
operator|.
name|toArray
argument_list|(
operator|new
name|FileStatus
index|[
literal|0
index|]
argument_list|)
return|;
block|}
block|}
end_class

end_unit

