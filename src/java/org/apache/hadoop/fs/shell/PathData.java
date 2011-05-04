begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.shell
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|shell
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
name|fs
operator|.
name|FileStatus
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
name|FileSystem
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
name|Path
import|;
end_import

begin_comment
comment|/**  * Encapsulates a Path (path), its FileStatus (stat), and its FileSystem (fs).  * The stat field will be null if the path does not exist.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|PathData
specifier|public
class|class
name|PathData
block|{
DECL|field|string
specifier|protected
name|String
name|string
init|=
literal|null
decl_stmt|;
DECL|field|path
specifier|public
specifier|final
name|Path
name|path
decl_stmt|;
DECL|field|stat
specifier|public
name|FileStatus
name|stat
decl_stmt|;
DECL|field|fs
specifier|public
specifier|final
name|FileSystem
name|fs
decl_stmt|;
DECL|field|exists
specifier|public
name|boolean
name|exists
decl_stmt|;
comment|/**    * Creates an object to wrap the given parameters as fields.  The string    * used to create the path will be recorded since the Path object does not    * return exactly the same string used to initialize it    * @param pathString a string for a path    * @param conf the configuration file    * @throws IOException if anything goes wrong...    */
DECL|method|PathData (String pathString, Configuration conf)
specifier|public
name|PathData
parameter_list|(
name|String
name|pathString
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|string
operator|=
name|pathString
expr_stmt|;
name|this
operator|.
name|path
operator|=
operator|new
name|Path
argument_list|(
name|pathString
argument_list|)
expr_stmt|;
name|this
operator|.
name|fs
operator|=
name|path
operator|.
name|getFileSystem
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|setStat
argument_list|(
name|getStat
argument_list|(
name|fs
argument_list|,
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates an object to wrap the given parameters as fields.     * @param fs the FileSystem    * @param path a Path    * @param stat the FileStatus (may be null if the path doesn't exist)    */
DECL|method|PathData (FileSystem fs, Path path, FileStatus stat)
specifier|public
name|PathData
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|path
parameter_list|,
name|FileStatus
name|stat
parameter_list|)
block|{
name|this
operator|.
name|string
operator|=
name|path
operator|.
name|toString
argument_list|()
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|fs
operator|=
name|fs
expr_stmt|;
name|setStat
argument_list|(
name|stat
argument_list|)
expr_stmt|;
block|}
comment|/**    * Convenience ctor that looks up the file status for a path.  If the path    * doesn't exist, then the status will be null    * @param fs the FileSystem for the path    * @param path the pathname to lookup     * @throws IOException if anything goes wrong    */
DECL|method|PathData (FileSystem fs, Path path)
specifier|public
name|PathData
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|fs
argument_list|,
name|path
argument_list|,
name|getStat
argument_list|(
name|fs
argument_list|,
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates an object to wrap the given parameters as fields.  The string    * used to create the path will be recorded since the Path object does not    * return exactly the same string used to initialize it.  If the FileStatus    * is not null, then its Path will be used to initialized the path, else    * the string of the path will be used.    * @param fs the FileSystem    * @param pathString a String of the path    * @param stat the FileStatus (may be null if the path doesn't exist)    */
DECL|method|PathData (FileSystem fs, String pathString, FileStatus stat)
specifier|public
name|PathData
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|String
name|pathString
parameter_list|,
name|FileStatus
name|stat
parameter_list|)
block|{
name|this
operator|.
name|string
operator|=
name|pathString
expr_stmt|;
name|this
operator|.
name|path
operator|=
operator|(
name|stat
operator|!=
literal|null
operator|)
condition|?
name|stat
operator|.
name|getPath
argument_list|()
else|:
operator|new
name|Path
argument_list|(
name|pathString
argument_list|)
expr_stmt|;
name|this
operator|.
name|fs
operator|=
name|fs
expr_stmt|;
name|setStat
argument_list|(
name|stat
argument_list|)
expr_stmt|;
block|}
comment|// need a static method for the ctor above
DECL|method|getStat (FileSystem fs, Path path)
specifier|private
specifier|static
name|FileStatus
name|getStat
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|FileStatus
name|status
init|=
literal|null
decl_stmt|;
try|try
block|{
name|status
operator|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{}
comment|// ignore FNF
return|return
name|status
return|;
block|}
DECL|method|setStat (FileStatus theStat)
specifier|private
name|void
name|setStat
parameter_list|(
name|FileStatus
name|theStat
parameter_list|)
block|{
name|stat
operator|=
name|theStat
expr_stmt|;
name|exists
operator|=
operator|(
name|stat
operator|!=
literal|null
operator|)
expr_stmt|;
block|}
comment|/**    * Convenience ctor that extracts the path from the given file status    * @param fs the FileSystem for the FileStatus    * @param stat the FileStatus     */
DECL|method|PathData (FileSystem fs, FileStatus stat)
specifier|public
name|PathData
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|FileStatus
name|stat
parameter_list|)
block|{
name|this
argument_list|(
name|fs
argument_list|,
name|stat
operator|.
name|getPath
argument_list|()
argument_list|,
name|stat
argument_list|)
expr_stmt|;
block|}
comment|/**    * Updates the paths's file status    * @return the updated FileStatus    * @throws IOException if anything goes wrong...    */
DECL|method|refreshStatus ()
specifier|public
name|FileStatus
name|refreshStatus
parameter_list|()
throws|throws
name|IOException
block|{
name|setStat
argument_list|(
name|fs
operator|.
name|getFileStatus
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|stat
return|;
block|}
comment|/**    * Returns a list of PathData objects of the items contained in the given    * directory.    * @return list of PathData objects for its children    * @throws IOException if anything else goes wrong...    */
DECL|method|getDirectoryContents ()
specifier|public
name|PathData
index|[]
name|getDirectoryContents
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|stat
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|path
operator|+
literal|": Not a directory"
argument_list|)
throw|;
block|}
name|FileStatus
index|[]
name|stats
init|=
name|fs
operator|.
name|listStatus
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|PathData
index|[]
name|items
init|=
operator|new
name|PathData
index|[
name|stats
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|stats
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|items
index|[
name|i
index|]
operator|=
operator|new
name|PathData
argument_list|(
name|fs
argument_list|,
name|stats
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|items
return|;
block|}
comment|/**    * Expand the given path as a glob pattern.  Non-existent paths do not    * throw an exception because creation commands like touch and mkdir need    * to create them.  The "stat" field will be null if the path does not    * exist.    * @param pattern the pattern to expand as a glob    * @param conf the hadoop configuration    * @return list of {@link PathData} objects.  if the pattern is not a glob,    * and does not exist, the list will contain a single PathData with a null    * stat     * @throws IOException anything else goes wrong...    */
DECL|method|expandAsGlob (String pattern, Configuration conf)
specifier|public
specifier|static
name|PathData
index|[]
name|expandAsGlob
parameter_list|(
name|String
name|pattern
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|globPath
init|=
operator|new
name|Path
argument_list|(
name|pattern
argument_list|)
decl_stmt|;
name|FileSystem
name|fs
init|=
name|globPath
operator|.
name|getFileSystem
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|FileStatus
index|[]
name|stats
init|=
name|fs
operator|.
name|globStatus
argument_list|(
name|globPath
argument_list|)
decl_stmt|;
name|PathData
index|[]
name|items
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|stats
operator|==
literal|null
condition|)
block|{
comment|// not a glob& file not found, so add the path with a null stat
name|items
operator|=
operator|new
name|PathData
index|[]
block|{
operator|new
name|PathData
argument_list|(
name|fs
argument_list|,
name|pattern
argument_list|,
literal|null
argument_list|)
block|}
expr_stmt|;
block|}
elseif|else
if|if
condition|(
comment|// this is very ugly, but needed to avoid breaking hdfs tests...
comment|// if a path has no authority, then the FileStatus from globStatus
comment|// will add the "-fs" authority into the path, so we need to sub
comment|// it back out to satisfy the tests
name|stats
operator|.
name|length
operator|==
literal|1
operator|&&
name|stats
index|[
literal|0
index|]
operator|.
name|getPath
argument_list|()
operator|.
name|equals
argument_list|(
name|fs
operator|.
name|makeQualified
argument_list|(
name|globPath
argument_list|)
argument_list|)
condition|)
block|{
comment|// if the fq path is identical to the pattern passed, use the pattern
comment|// to initialize the string value
name|items
operator|=
operator|new
name|PathData
index|[]
block|{
operator|new
name|PathData
argument_list|(
name|fs
argument_list|,
name|pattern
argument_list|,
name|stats
index|[
literal|0
index|]
argument_list|)
block|}
expr_stmt|;
block|}
else|else
block|{
comment|// convert stats to PathData
name|items
operator|=
operator|new
name|PathData
index|[
name|stats
operator|.
name|length
index|]
expr_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|FileStatus
name|stat
range|:
name|stats
control|)
block|{
name|items
index|[
name|i
operator|++
index|]
operator|=
operator|new
name|PathData
argument_list|(
name|fs
argument_list|,
name|stat
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|items
return|;
block|}
comment|/**    * Returns the printable version of the path that is either the path    * as given on the commandline, or the full path    * @return String of the path    */
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
operator|(
name|string
operator|!=
literal|null
operator|)
condition|?
name|string
else|:
name|path
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

