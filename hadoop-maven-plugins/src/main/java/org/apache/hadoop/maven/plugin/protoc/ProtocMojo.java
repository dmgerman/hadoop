begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.maven.plugin.protoc
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|maven
operator|.
name|plugin
operator|.
name|protoc
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
name|maven
operator|.
name|plugin
operator|.
name|util
operator|.
name|Exec
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
name|maven
operator|.
name|plugin
operator|.
name|util
operator|.
name|FileSetUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|maven
operator|.
name|model
operator|.
name|FileSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|maven
operator|.
name|plugin
operator|.
name|AbstractMojo
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|maven
operator|.
name|plugin
operator|.
name|MojoExecutionException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|maven
operator|.
name|plugins
operator|.
name|annotations
operator|.
name|LifecyclePhase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|maven
operator|.
name|plugins
operator|.
name|annotations
operator|.
name|Mojo
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|maven
operator|.
name|plugins
operator|.
name|annotations
operator|.
name|Parameter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|maven
operator|.
name|project
operator|.
name|MavenProject
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jackson
operator|.
name|map
operator|.
name|ObjectMapper
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jackson
operator|.
name|type
operator|.
name|TypeReference
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
import|;
end_import

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
name|FileOutputStream
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
name|HashMap
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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|CRC32
import|;
end_import

begin_class
annotation|@
name|Mojo
argument_list|(
name|name
operator|=
literal|"protoc"
argument_list|,
name|defaultPhase
operator|=
name|LifecyclePhase
operator|.
name|GENERATE_SOURCES
argument_list|)
DECL|class|ProtocMojo
specifier|public
class|class
name|ProtocMojo
extends|extends
name|AbstractMojo
block|{
annotation|@
name|Parameter
argument_list|(
name|defaultValue
operator|=
literal|"${project}"
argument_list|,
name|readonly
operator|=
literal|true
argument_list|)
DECL|field|project
specifier|private
name|MavenProject
name|project
decl_stmt|;
annotation|@
name|Parameter
DECL|field|imports
specifier|private
name|File
index|[]
name|imports
decl_stmt|;
annotation|@
name|Parameter
argument_list|(
name|defaultValue
operator|=
literal|"${project.build.directory}/generated-sources/java"
argument_list|)
DECL|field|output
specifier|private
name|File
name|output
decl_stmt|;
annotation|@
name|Parameter
argument_list|(
name|required
operator|=
literal|true
argument_list|)
DECL|field|source
specifier|private
name|FileSet
name|source
decl_stmt|;
annotation|@
name|Parameter
argument_list|(
name|defaultValue
operator|=
literal|"protoc"
argument_list|)
DECL|field|protocCommand
specifier|private
name|String
name|protocCommand
decl_stmt|;
annotation|@
name|Parameter
argument_list|(
name|required
operator|=
literal|true
argument_list|)
DECL|field|protocVersion
specifier|private
name|String
name|protocVersion
decl_stmt|;
annotation|@
name|Parameter
argument_list|(
name|defaultValue
operator|=
literal|"${project.build.directory}/hadoop-maven-plugins-protoc-checksums.json"
argument_list|)
DECL|field|checksumPath
specifier|private
name|String
name|checksumPath
decl_stmt|;
comment|/**    * Compares include and source file checksums against previously computed    * checksums stored in a json file in the build directory.    */
DECL|class|ChecksumComparator
specifier|public
class|class
name|ChecksumComparator
block|{
DECL|field|storedChecksums
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|storedChecksums
decl_stmt|;
DECL|field|computedChecksums
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|computedChecksums
decl_stmt|;
DECL|field|checksumFile
specifier|private
specifier|final
name|File
name|checksumFile
decl_stmt|;
DECL|method|ChecksumComparator (String checksumPath)
name|ChecksumComparator
parameter_list|(
name|String
name|checksumPath
parameter_list|)
throws|throws
name|IOException
block|{
name|checksumFile
operator|=
operator|new
name|File
argument_list|(
name|checksumPath
argument_list|)
expr_stmt|;
comment|// Read in the checksums
if|if
condition|(
name|checksumFile
operator|.
name|exists
argument_list|()
condition|)
block|{
name|ObjectMapper
name|mapper
init|=
operator|new
name|ObjectMapper
argument_list|()
decl_stmt|;
name|storedChecksums
operator|=
name|mapper
operator|.
name|readValue
argument_list|(
name|checksumFile
argument_list|,
operator|new
name|TypeReference
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
argument_list|>
argument_list|()
block|{             }
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|storedChecksums
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
name|computedChecksums
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
block|}
DECL|method|hasChanged (File file)
specifier|public
name|boolean
name|hasChanged
parameter_list|(
name|File
name|file
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|file
operator|.
name|exists
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
literal|"Specified protoc include or source does not exist: "
operator|+
name|file
argument_list|)
throw|;
block|}
if|if
condition|(
name|file
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
return|return
name|hasDirectoryChanged
argument_list|(
name|file
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|file
operator|.
name|isFile
argument_list|()
condition|)
block|{
return|return
name|hasFileChanged
argument_list|(
name|file
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Not a file or directory: "
operator|+
name|file
argument_list|)
throw|;
block|}
block|}
DECL|method|hasDirectoryChanged (File directory)
specifier|private
name|boolean
name|hasDirectoryChanged
parameter_list|(
name|File
name|directory
parameter_list|)
throws|throws
name|IOException
block|{
name|File
index|[]
name|listing
init|=
name|directory
operator|.
name|listFiles
argument_list|()
decl_stmt|;
name|boolean
name|changed
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|listing
operator|==
literal|null
condition|)
block|{
comment|// not changed.
return|return
literal|false
return|;
block|}
comment|// Do not exit early, since we need to compute and save checksums
comment|// for each file within the directory.
for|for
control|(
name|File
name|f
range|:
name|listing
control|)
block|{
if|if
condition|(
name|f
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
if|if
condition|(
name|hasDirectoryChanged
argument_list|(
name|f
argument_list|)
condition|)
block|{
name|changed
operator|=
literal|true
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|f
operator|.
name|isFile
argument_list|()
condition|)
block|{
if|if
condition|(
name|hasFileChanged
argument_list|(
name|f
argument_list|)
condition|)
block|{
name|changed
operator|=
literal|true
expr_stmt|;
block|}
block|}
else|else
block|{
name|getLog
argument_list|()
operator|.
name|debug
argument_list|(
literal|"Skipping entry that is not a file or directory: "
operator|+
name|f
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|changed
return|;
block|}
DECL|method|hasFileChanged (File file)
specifier|private
name|boolean
name|hasFileChanged
parameter_list|(
name|File
name|file
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|computedCsum
init|=
name|computeChecksum
argument_list|(
name|file
argument_list|)
decl_stmt|;
comment|// Return if the generated csum matches the stored csum
name|Long
name|storedCsum
init|=
name|storedChecksums
operator|.
name|get
argument_list|(
name|file
operator|.
name|getCanonicalPath
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|storedCsum
operator|==
literal|null
operator|||
name|storedCsum
operator|.
name|longValue
argument_list|()
operator|!=
name|computedCsum
condition|)
block|{
comment|// It has changed.
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
DECL|method|computeChecksum (File file)
specifier|private
name|long
name|computeChecksum
parameter_list|(
name|File
name|file
parameter_list|)
throws|throws
name|IOException
block|{
comment|// If we've already computed the csum, reuse the computed value
specifier|final
name|String
name|canonicalPath
init|=
name|file
operator|.
name|getCanonicalPath
argument_list|()
decl_stmt|;
if|if
condition|(
name|computedChecksums
operator|.
name|containsKey
argument_list|(
name|canonicalPath
argument_list|)
condition|)
block|{
return|return
name|computedChecksums
operator|.
name|get
argument_list|(
name|canonicalPath
argument_list|)
return|;
block|}
comment|// Compute the csum for the file
name|CRC32
name|crc
init|=
operator|new
name|CRC32
argument_list|()
decl_stmt|;
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
literal|1024
operator|*
literal|64
index|]
decl_stmt|;
try|try
init|(
name|BufferedInputStream
name|in
init|=
operator|new
name|BufferedInputStream
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
argument_list|)
init|)
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|read
init|=
name|in
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
decl_stmt|;
if|if
condition|(
name|read
operator|<=
literal|0
condition|)
block|{
break|break;
block|}
name|crc
operator|.
name|update
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|read
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Save it in the generated map and return
specifier|final
name|long
name|computedCsum
init|=
name|crc
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|computedChecksums
operator|.
name|put
argument_list|(
name|canonicalPath
argument_list|,
name|computedCsum
argument_list|)
expr_stmt|;
return|return
name|crc
operator|.
name|getValue
argument_list|()
return|;
block|}
DECL|method|writeChecksums ()
specifier|public
name|void
name|writeChecksums
parameter_list|()
throws|throws
name|IOException
block|{
name|ObjectMapper
name|mapper
init|=
operator|new
name|ObjectMapper
argument_list|()
decl_stmt|;
try|try
init|(
name|BufferedOutputStream
name|out
init|=
operator|new
name|BufferedOutputStream
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|checksumFile
argument_list|)
argument_list|)
init|)
block|{
name|mapper
operator|.
name|writeValue
argument_list|(
name|out
argument_list|,
name|computedChecksums
argument_list|)
expr_stmt|;
name|getLog
argument_list|()
operator|.
name|info
argument_list|(
literal|"Wrote protoc checksums to file "
operator|+
name|checksumFile
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|execute ()
specifier|public
name|void
name|execute
parameter_list|()
throws|throws
name|MojoExecutionException
block|{
try|try
block|{
name|List
argument_list|<
name|String
argument_list|>
name|command
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|command
operator|.
name|add
argument_list|(
name|protocCommand
argument_list|)
expr_stmt|;
name|command
operator|.
name|add
argument_list|(
literal|"--version"
argument_list|)
expr_stmt|;
name|Exec
name|exec
init|=
operator|new
name|Exec
argument_list|(
name|this
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|out
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|exec
operator|.
name|run
argument_list|(
name|command
argument_list|,
name|out
argument_list|)
operator|==
literal|127
condition|)
block|{
name|getLog
argument_list|()
operator|.
name|error
argument_list|(
literal|"protoc, not found at: "
operator|+
name|protocCommand
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|MojoExecutionException
argument_list|(
literal|"protoc failure"
argument_list|)
throw|;
block|}
else|else
block|{
if|if
condition|(
name|out
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|getLog
argument_list|()
operator|.
name|error
argument_list|(
literal|"stdout: "
operator|+
name|out
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|MojoExecutionException
argument_list|(
literal|"'protoc --version' did not return a version"
argument_list|)
throw|;
block|}
else|else
block|{
if|if
condition|(
operator|!
name|out
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|endsWith
argument_list|(
name|protocVersion
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|MojoExecutionException
argument_list|(
literal|"protoc version is '"
operator|+
name|out
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|+
literal|"', expected version is '"
operator|+
name|protocVersion
operator|+
literal|"'"
argument_list|)
throw|;
block|}
block|}
block|}
if|if
condition|(
operator|!
name|output
operator|.
name|mkdirs
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|output
operator|.
name|exists
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|MojoExecutionException
argument_list|(
literal|"Could not create directory: "
operator|+
name|output
argument_list|)
throw|;
block|}
block|}
comment|// Whether the import or source protoc files have changed.
name|ChecksumComparator
name|comparator
init|=
operator|new
name|ChecksumComparator
argument_list|(
name|checksumPath
argument_list|)
decl_stmt|;
name|boolean
name|importsChanged
init|=
literal|false
decl_stmt|;
name|command
operator|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|command
operator|.
name|add
argument_list|(
name|protocCommand
argument_list|)
expr_stmt|;
name|command
operator|.
name|add
argument_list|(
literal|"--java_out="
operator|+
name|output
operator|.
name|getCanonicalPath
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|imports
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|File
name|i
range|:
name|imports
control|)
block|{
if|if
condition|(
name|comparator
operator|.
name|hasChanged
argument_list|(
name|i
argument_list|)
condition|)
block|{
name|importsChanged
operator|=
literal|true
expr_stmt|;
block|}
name|command
operator|.
name|add
argument_list|(
literal|"-I"
operator|+
name|i
operator|.
name|getCanonicalPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Filter to generate classes for just the changed source files.
name|List
argument_list|<
name|File
argument_list|>
name|changedSources
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|boolean
name|sourcesChanged
init|=
literal|false
decl_stmt|;
for|for
control|(
name|File
name|f
range|:
name|FileSetUtils
operator|.
name|convertFileSetToFiles
argument_list|(
name|source
argument_list|)
control|)
block|{
comment|// Need to recompile if the source has changed, or if any import has
comment|// changed.
if|if
condition|(
name|comparator
operator|.
name|hasChanged
argument_list|(
name|f
argument_list|)
operator|||
name|importsChanged
condition|)
block|{
name|sourcesChanged
operator|=
literal|true
expr_stmt|;
name|changedSources
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|command
operator|.
name|add
argument_list|(
name|f
operator|.
name|getCanonicalPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|sourcesChanged
operator|&&
operator|!
name|importsChanged
condition|)
block|{
name|getLog
argument_list|()
operator|.
name|info
argument_list|(
literal|"No changes detected in protoc files, skipping "
operator|+
literal|"generation."
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|getLog
argument_list|()
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"Generating classes for the following protoc files: ["
argument_list|)
expr_stmt|;
name|String
name|prefix
init|=
literal|""
decl_stmt|;
for|for
control|(
name|File
name|f
range|:
name|changedSources
control|)
block|{
name|b
operator|.
name|append
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
name|f
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|prefix
operator|=
literal|", "
expr_stmt|;
block|}
name|b
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
name|getLog
argument_list|()
operator|.
name|debug
argument_list|(
name|b
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|exec
operator|=
operator|new
name|Exec
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|out
operator|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|err
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|exec
operator|.
name|run
argument_list|(
name|command
argument_list|,
name|out
argument_list|,
name|err
argument_list|)
operator|!=
literal|0
condition|)
block|{
name|getLog
argument_list|()
operator|.
name|error
argument_list|(
literal|"protoc compiler error"
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|s
range|:
name|out
control|)
block|{
name|getLog
argument_list|()
operator|.
name|error
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|s
range|:
name|err
control|)
block|{
name|getLog
argument_list|()
operator|.
name|error
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
throw|throw
operator|new
name|MojoExecutionException
argument_list|(
literal|"protoc failure"
argument_list|)
throw|;
block|}
comment|// Write the new checksum file on success.
name|comparator
operator|.
name|writeChecksums
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|MojoExecutionException
argument_list|(
name|ex
operator|.
name|toString
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
name|project
operator|.
name|addCompileSourceRoot
argument_list|(
name|output
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

