begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.maven.plugin.versioninfo
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
name|versioninfo
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|RandomAccessFile
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|MessageDigest
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|NoSuchAlgorithmException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|DateFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|SimpleDateFormat
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
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
name|TimeZone
import|;
end_import

begin_comment
comment|/**  * VersionInfoMojo calculates information about the current version of the  * codebase and exports the information as properties for further use in a Maven  * build.  The version information includes build time, SCM URI, SCM branch, SCM  * commit, and an MD5 checksum of the contents of the files in the codebase.  */
end_comment

begin_class
annotation|@
name|Mojo
argument_list|(
name|name
operator|=
literal|"version-info"
argument_list|)
DECL|class|VersionInfoMojo
specifier|public
class|class
name|VersionInfoMojo
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
literal|"version-info.build.time"
argument_list|)
DECL|field|buildTimeProperty
specifier|private
name|String
name|buildTimeProperty
decl_stmt|;
annotation|@
name|Parameter
argument_list|(
name|defaultValue
operator|=
literal|"version-info.source.md5"
argument_list|)
DECL|field|md5Property
specifier|private
name|String
name|md5Property
decl_stmt|;
annotation|@
name|Parameter
argument_list|(
name|defaultValue
operator|=
literal|"version-info.scm.uri"
argument_list|)
DECL|field|scmUriProperty
specifier|private
name|String
name|scmUriProperty
decl_stmt|;
annotation|@
name|Parameter
argument_list|(
name|defaultValue
operator|=
literal|"version-info.scm.branch"
argument_list|)
DECL|field|scmBranchProperty
specifier|private
name|String
name|scmBranchProperty
decl_stmt|;
annotation|@
name|Parameter
argument_list|(
name|defaultValue
operator|=
literal|"version-info.scm.commit"
argument_list|)
DECL|field|scmCommitProperty
specifier|private
name|String
name|scmCommitProperty
decl_stmt|;
annotation|@
name|Parameter
argument_list|(
name|defaultValue
operator|=
literal|"git"
argument_list|)
DECL|field|gitCommand
specifier|private
name|String
name|gitCommand
decl_stmt|;
annotation|@
name|Parameter
argument_list|(
name|defaultValue
operator|=
literal|"svn"
argument_list|)
DECL|field|svnCommand
specifier|private
name|String
name|svnCommand
decl_stmt|;
DECL|enum|SCM
DECL|enumConstant|NONE
DECL|enumConstant|SVN
DECL|enumConstant|GIT
specifier|private
enum|enum
name|SCM
block|{
name|NONE
block|,
name|SVN
block|,
name|GIT
block|}
annotation|@
name|Override
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
name|SCM
name|scm
init|=
name|determineSCM
argument_list|()
decl_stmt|;
name|project
operator|.
name|getProperties
argument_list|()
operator|.
name|setProperty
argument_list|(
name|buildTimeProperty
argument_list|,
name|getBuildTime
argument_list|()
argument_list|)
expr_stmt|;
name|project
operator|.
name|getProperties
argument_list|()
operator|.
name|setProperty
argument_list|(
name|scmUriProperty
argument_list|,
name|getSCMUri
argument_list|(
name|scm
argument_list|)
argument_list|)
expr_stmt|;
name|project
operator|.
name|getProperties
argument_list|()
operator|.
name|setProperty
argument_list|(
name|scmBranchProperty
argument_list|,
name|getSCMBranch
argument_list|(
name|scm
argument_list|)
argument_list|)
expr_stmt|;
name|project
operator|.
name|getProperties
argument_list|()
operator|.
name|setProperty
argument_list|(
name|scmCommitProperty
argument_list|,
name|getSCMCommit
argument_list|(
name|scm
argument_list|)
argument_list|)
expr_stmt|;
name|project
operator|.
name|getProperties
argument_list|()
operator|.
name|setProperty
argument_list|(
name|md5Property
argument_list|,
name|computeMD5
argument_list|()
argument_list|)
expr_stmt|;
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
block|}
comment|/**    * Returns a string representing current build time.    *     * @return String representing current build time    */
DECL|method|getBuildTime ()
specifier|private
name|String
name|getBuildTime
parameter_list|()
block|{
name|DateFormat
name|dateFormat
init|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"yyyy-MM-dd'T'HH:mm'Z'"
argument_list|)
decl_stmt|;
name|dateFormat
operator|.
name|setTimeZone
argument_list|(
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
literal|"UTC"
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|dateFormat
operator|.
name|format
argument_list|(
operator|new
name|Date
argument_list|()
argument_list|)
return|;
block|}
DECL|field|scmOut
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|scmOut
decl_stmt|;
comment|/**    * Determines which SCM is in use (Subversion, git, or none) and captures    * output of the SCM command for later parsing.    *     * @return SCM in use for this build    * @throws Exception if any error occurs attempting to determine SCM    */
DECL|method|determineSCM ()
specifier|private
name|SCM
name|determineSCM
parameter_list|()
throws|throws
name|Exception
block|{
name|Exec
name|exec
init|=
operator|new
name|Exec
argument_list|(
name|this
argument_list|)
decl_stmt|;
name|SCM
name|scm
init|=
name|SCM
operator|.
name|NONE
decl_stmt|;
name|scmOut
operator|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|int
name|ret
init|=
name|exec
operator|.
name|run
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|svnCommand
argument_list|,
literal|"info"
argument_list|)
argument_list|,
name|scmOut
argument_list|)
decl_stmt|;
if|if
condition|(
name|ret
operator|==
literal|0
condition|)
block|{
name|scm
operator|=
name|SCM
operator|.
name|SVN
expr_stmt|;
block|}
else|else
block|{
name|ret
operator|=
name|exec
operator|.
name|run
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|gitCommand
argument_list|,
literal|"branch"
argument_list|)
argument_list|,
name|scmOut
argument_list|)
expr_stmt|;
if|if
condition|(
name|ret
operator|==
literal|0
condition|)
block|{
name|ret
operator|=
name|exec
operator|.
name|run
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|gitCommand
argument_list|,
literal|"remote"
argument_list|,
literal|"-v"
argument_list|)
argument_list|,
name|scmOut
argument_list|)
expr_stmt|;
if|if
condition|(
name|ret
operator|!=
literal|0
condition|)
block|{
name|scm
operator|=
name|SCM
operator|.
name|NONE
expr_stmt|;
name|scmOut
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|ret
operator|=
name|exec
operator|.
name|run
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|gitCommand
argument_list|,
literal|"log"
argument_list|,
literal|"-n"
argument_list|,
literal|"1"
argument_list|)
argument_list|,
name|scmOut
argument_list|)
expr_stmt|;
if|if
condition|(
name|ret
operator|!=
literal|0
condition|)
block|{
name|scm
operator|=
name|SCM
operator|.
name|NONE
expr_stmt|;
name|scmOut
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|scm
operator|=
name|SCM
operator|.
name|GIT
expr_stmt|;
block|}
block|}
block|}
block|}
if|if
condition|(
name|scmOut
operator|!=
literal|null
condition|)
block|{
name|getLog
argument_list|()
operator|.
name|debug
argument_list|(
name|scmOut
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|getLog
argument_list|()
operator|.
name|info
argument_list|(
literal|"SCM: "
operator|+
name|scm
argument_list|)
expr_stmt|;
return|return
name|scm
return|;
block|}
comment|/**    * Return URI and branch of Subversion repository.    *     * @param str String Subversion info output containing URI and branch    * @return String[] containing URI and branch    */
DECL|method|getSvnUriInfo (String str)
specifier|private
name|String
index|[]
name|getSvnUriInfo
parameter_list|(
name|String
name|str
parameter_list|)
block|{
name|String
index|[]
name|res
init|=
operator|new
name|String
index|[]
block|{
literal|"Unknown"
block|,
literal|"Unknown"
block|}
decl_stmt|;
name|String
name|path
init|=
name|str
decl_stmt|;
name|int
name|index
init|=
name|path
operator|.
name|indexOf
argument_list|(
literal|"trunk"
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|>
operator|-
literal|1
condition|)
block|{
name|res
index|[
literal|0
index|]
operator|=
name|path
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|index
operator|-
literal|1
argument_list|)
expr_stmt|;
name|res
index|[
literal|1
index|]
operator|=
literal|"trunk"
expr_stmt|;
block|}
else|else
block|{
name|index
operator|=
name|path
operator|.
name|indexOf
argument_list|(
literal|"branches"
argument_list|)
expr_stmt|;
if|if
condition|(
name|index
operator|>
operator|-
literal|1
condition|)
block|{
name|res
index|[
literal|0
index|]
operator|=
name|path
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|index
operator|-
literal|1
argument_list|)
expr_stmt|;
name|int
name|branchIndex
init|=
name|index
operator|+
literal|"branches"
operator|.
name|length
argument_list|()
operator|+
literal|1
decl_stmt|;
name|index
operator|=
name|path
operator|.
name|indexOf
argument_list|(
literal|'/'
argument_list|,
name|branchIndex
argument_list|)
expr_stmt|;
if|if
condition|(
name|index
operator|>
operator|-
literal|1
condition|)
block|{
name|res
index|[
literal|1
index|]
operator|=
name|path
operator|.
name|substring
argument_list|(
name|branchIndex
argument_list|,
name|index
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|res
index|[
literal|1
index|]
operator|=
name|path
operator|.
name|substring
argument_list|(
name|branchIndex
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|res
return|;
block|}
comment|/**    * Parses SCM output and returns URI of SCM.    *     * @param scm SCM in use for this build    * @return String URI of SCM    */
DECL|method|getSCMUri (SCM scm)
specifier|private
name|String
name|getSCMUri
parameter_list|(
name|SCM
name|scm
parameter_list|)
block|{
name|String
name|uri
init|=
literal|"Unknown"
decl_stmt|;
switch|switch
condition|(
name|scm
condition|)
block|{
case|case
name|SVN
case|:
for|for
control|(
name|String
name|s
range|:
name|scmOut
control|)
block|{
if|if
condition|(
name|s
operator|.
name|startsWith
argument_list|(
literal|"URL:"
argument_list|)
condition|)
block|{
name|uri
operator|=
name|s
operator|.
name|substring
argument_list|(
literal|4
argument_list|)
operator|.
name|trim
argument_list|()
expr_stmt|;
name|uri
operator|=
name|getSvnUriInfo
argument_list|(
name|uri
argument_list|)
index|[
literal|0
index|]
expr_stmt|;
break|break;
block|}
block|}
break|break;
case|case
name|GIT
case|:
for|for
control|(
name|String
name|s
range|:
name|scmOut
control|)
block|{
if|if
condition|(
name|s
operator|.
name|startsWith
argument_list|(
literal|"origin"
argument_list|)
operator|&&
name|s
operator|.
name|endsWith
argument_list|(
literal|"(fetch)"
argument_list|)
condition|)
block|{
name|uri
operator|=
name|s
operator|.
name|substring
argument_list|(
literal|"origin"
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|uri
operator|=
name|uri
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|uri
operator|.
name|length
argument_list|()
operator|-
literal|"(fetch)"
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
break|break;
block|}
return|return
name|uri
operator|.
name|trim
argument_list|()
return|;
block|}
comment|/**    * Parses SCM output and returns commit of SCM.    *     * @param scm SCM in use for this build    * @return String commit of SCM    */
DECL|method|getSCMCommit (SCM scm)
specifier|private
name|String
name|getSCMCommit
parameter_list|(
name|SCM
name|scm
parameter_list|)
block|{
name|String
name|commit
init|=
literal|"Unknown"
decl_stmt|;
switch|switch
condition|(
name|scm
condition|)
block|{
case|case
name|SVN
case|:
for|for
control|(
name|String
name|s
range|:
name|scmOut
control|)
block|{
if|if
condition|(
name|s
operator|.
name|startsWith
argument_list|(
literal|"Revision:"
argument_list|)
condition|)
block|{
name|commit
operator|=
name|s
operator|.
name|substring
argument_list|(
literal|"Revision:"
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
break|break;
case|case
name|GIT
case|:
for|for
control|(
name|String
name|s
range|:
name|scmOut
control|)
block|{
if|if
condition|(
name|s
operator|.
name|startsWith
argument_list|(
literal|"commit"
argument_list|)
condition|)
block|{
name|commit
operator|=
name|s
operator|.
name|substring
argument_list|(
literal|"commit"
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
break|break;
block|}
return|return
name|commit
operator|.
name|trim
argument_list|()
return|;
block|}
comment|/**    * Parses SCM output and returns branch of SCM.    *     * @param scm SCM in use for this build    * @return String branch of SCM    */
DECL|method|getSCMBranch (SCM scm)
specifier|private
name|String
name|getSCMBranch
parameter_list|(
name|SCM
name|scm
parameter_list|)
block|{
name|String
name|branch
init|=
literal|"Unknown"
decl_stmt|;
switch|switch
condition|(
name|scm
condition|)
block|{
case|case
name|SVN
case|:
for|for
control|(
name|String
name|s
range|:
name|scmOut
control|)
block|{
if|if
condition|(
name|s
operator|.
name|startsWith
argument_list|(
literal|"URL:"
argument_list|)
condition|)
block|{
name|branch
operator|=
name|s
operator|.
name|substring
argument_list|(
literal|4
argument_list|)
operator|.
name|trim
argument_list|()
expr_stmt|;
name|branch
operator|=
name|getSvnUriInfo
argument_list|(
name|branch
argument_list|)
index|[
literal|1
index|]
expr_stmt|;
break|break;
block|}
block|}
break|break;
case|case
name|GIT
case|:
for|for
control|(
name|String
name|s
range|:
name|scmOut
control|)
block|{
if|if
condition|(
name|s
operator|.
name|startsWith
argument_list|(
literal|"*"
argument_list|)
condition|)
block|{
name|branch
operator|=
name|s
operator|.
name|substring
argument_list|(
literal|"*"
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
break|break;
block|}
return|return
name|branch
operator|.
name|trim
argument_list|()
return|;
block|}
comment|/**    * Reads and returns the full contents of the specified file.    *     * @param file File to read    * @return byte[] containing full contents of file    * @throws IOException if there is an I/O error while reading the file    */
DECL|method|readFile (File file)
specifier|private
name|byte
index|[]
name|readFile
parameter_list|(
name|File
name|file
parameter_list|)
throws|throws
name|IOException
block|{
name|RandomAccessFile
name|raf
init|=
operator|new
name|RandomAccessFile
argument_list|(
name|file
argument_list|,
literal|"r"
argument_list|)
decl_stmt|;
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
operator|(
name|int
operator|)
name|raf
operator|.
name|length
argument_list|()
index|]
decl_stmt|;
name|raf
operator|.
name|readFully
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
name|raf
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|buffer
return|;
block|}
comment|/**    * Given a list of files, computes and returns an MD5 checksum of the full    * contents of all files.    *     * @param files List<File> containing every file to input into the MD5 checksum    * @return byte[] calculated MD5 checksum    * @throws IOException if there is an I/O error while reading a file    * @throws NoSuchAlgorithmException if the MD5 algorithm is not supported    */
DECL|method|computeMD5 (List<File> files)
specifier|private
name|byte
index|[]
name|computeMD5
parameter_list|(
name|List
argument_list|<
name|File
argument_list|>
name|files
parameter_list|)
throws|throws
name|IOException
throws|,
name|NoSuchAlgorithmException
block|{
name|MessageDigest
name|md5
init|=
name|MessageDigest
operator|.
name|getInstance
argument_list|(
literal|"MD5"
argument_list|)
decl_stmt|;
for|for
control|(
name|File
name|file
range|:
name|files
control|)
block|{
name|getLog
argument_list|()
operator|.
name|debug
argument_list|(
literal|"Computing MD5 for: "
operator|+
name|file
argument_list|)
expr_stmt|;
name|md5
operator|.
name|update
argument_list|(
name|readFile
argument_list|(
name|file
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|md5
operator|.
name|digest
argument_list|()
return|;
block|}
comment|/**    * Converts bytes to a hexadecimal string representation and returns it.    *     * @param array byte[] to convert    * @return String containing hexadecimal representation of bytes    */
DECL|method|byteArrayToString (byte[] array)
specifier|private
name|String
name|byteArrayToString
parameter_list|(
name|byte
index|[]
name|array
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|byte
name|b
range|:
name|array
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|Integer
operator|.
name|toHexString
argument_list|(
literal|0xff
operator|&
name|b
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|class|MD5Comparator
specifier|static
class|class
name|MD5Comparator
implements|implements
name|Comparator
argument_list|<
name|File
argument_list|>
implements|,
name|Serializable
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
annotation|@
name|Override
DECL|method|compare (File lhs, File rhs)
specifier|public
name|int
name|compare
parameter_list|(
name|File
name|lhs
parameter_list|,
name|File
name|rhs
parameter_list|)
block|{
return|return
name|normalizePath
argument_list|(
name|lhs
argument_list|)
operator|.
name|compareTo
argument_list|(
name|normalizePath
argument_list|(
name|rhs
argument_list|)
argument_list|)
return|;
block|}
DECL|method|normalizePath (File file)
specifier|private
name|String
name|normalizePath
parameter_list|(
name|File
name|file
parameter_list|)
block|{
return|return
name|file
operator|.
name|getPath
argument_list|()
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
operator|.
name|replaceAll
argument_list|(
literal|"\\\\"
argument_list|,
literal|"/"
argument_list|)
return|;
block|}
block|}
comment|/**    * Computes and returns an MD5 checksum of the contents of all files in the    * input Maven FileSet.    *     * @return String containing hexadecimal representation of MD5 checksum    * @throws Exception if there is any error while computing the MD5 checksum    */
DECL|method|computeMD5 ()
specifier|private
name|String
name|computeMD5
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|File
argument_list|>
name|files
init|=
name|FileSetUtils
operator|.
name|convertFileSetToFiles
argument_list|(
name|source
argument_list|)
decl_stmt|;
comment|// File order of MD5 calculation is significant.  Sorting is done on
comment|// unix-format names, case-folded, in order to get a platform-independent
comment|// sort and calculate the same MD5 on all platforms.
name|Collections
operator|.
name|sort
argument_list|(
name|files
argument_list|,
operator|new
name|MD5Comparator
argument_list|()
argument_list|)
expr_stmt|;
name|byte
index|[]
name|md5
init|=
name|computeMD5
argument_list|(
name|files
argument_list|)
decl_stmt|;
name|String
name|md5str
init|=
name|byteArrayToString
argument_list|(
name|md5
argument_list|)
decl_stmt|;
name|getLog
argument_list|()
operator|.
name|info
argument_list|(
literal|"Computed MD5: "
operator|+
name|md5str
argument_list|)
expr_stmt|;
return|return
name|md5str
return|;
block|}
block|}
end_class

end_unit

