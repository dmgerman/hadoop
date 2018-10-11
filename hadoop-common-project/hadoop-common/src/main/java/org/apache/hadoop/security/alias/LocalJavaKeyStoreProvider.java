begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.security.alias
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|alias
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
name|FileUtil
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
name|permission
operator|.
name|FsPermission
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
name|Shell
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
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
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
name|net
operator|.
name|URISyntaxException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|attribute
operator|.
name|PosixFilePermission
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|attribute
operator|.
name|PosixFilePermissions
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|StringTokenizer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EnumSet
import|;
end_import

begin_comment
comment|/**  * CredentialProvider based on Java's KeyStore file format. The file may be  * stored only on the local filesystem using the following name mangling:  * localjceks://file/home/larry/creds.jceks {@literal ->}  * file:///home/larry/creds.jceks  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|LocalJavaKeyStoreProvider
specifier|public
specifier|final
class|class
name|LocalJavaKeyStoreProvider
extends|extends
name|AbstractJavaKeyStoreProvider
block|{
DECL|field|SCHEME_NAME
specifier|public
specifier|static
specifier|final
name|String
name|SCHEME_NAME
init|=
literal|"localjceks"
decl_stmt|;
DECL|field|file
specifier|private
name|File
name|file
decl_stmt|;
DECL|field|permissions
specifier|private
name|Set
argument_list|<
name|PosixFilePermission
argument_list|>
name|permissions
decl_stmt|;
DECL|method|LocalJavaKeyStoreProvider (URI uri, Configuration conf)
specifier|private
name|LocalJavaKeyStoreProvider
parameter_list|(
name|URI
name|uri
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|uri
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getSchemeName ()
specifier|protected
name|String
name|getSchemeName
parameter_list|()
block|{
return|return
name|SCHEME_NAME
return|;
block|}
annotation|@
name|Override
DECL|method|getOutputStreamForKeystore ()
specifier|protected
name|OutputStream
name|getOutputStreamForKeystore
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"using '"
operator|+
name|file
operator|+
literal|"' for output stream."
argument_list|)
expr_stmt|;
block|}
name|FileOutputStream
name|out
init|=
operator|new
name|FileOutputStream
argument_list|(
name|file
argument_list|)
decl_stmt|;
return|return
name|out
return|;
block|}
annotation|@
name|Override
DECL|method|keystoreExists ()
specifier|protected
name|boolean
name|keystoreExists
parameter_list|()
throws|throws
name|IOException
block|{
comment|/* The keystore loader doesn't handle zero length files. */
return|return
name|file
operator|.
name|exists
argument_list|()
operator|&&
operator|(
name|file
operator|.
name|length
argument_list|()
operator|>
literal|0
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|getInputStreamForFile ()
specifier|protected
name|InputStream
name|getInputStreamForFile
parameter_list|()
throws|throws
name|IOException
block|{
name|FileInputStream
name|is
init|=
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
decl_stmt|;
return|return
name|is
return|;
block|}
annotation|@
name|Override
DECL|method|createPermissions (String perms)
specifier|protected
name|void
name|createPermissions
parameter_list|(
name|String
name|perms
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|mode
init|=
literal|700
decl_stmt|;
try|try
block|{
name|mode
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|perms
argument_list|,
literal|8
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|nfe
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Invalid permissions mode provided while "
operator|+
literal|"trying to createPermissions"
argument_list|,
name|nfe
argument_list|)
throw|;
block|}
name|permissions
operator|=
name|modeToPosixFilePermission
argument_list|(
name|mode
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|stashOriginalFilePermissions ()
specifier|protected
name|void
name|stashOriginalFilePermissions
parameter_list|()
throws|throws
name|IOException
block|{
comment|// save off permissions in case we need to
comment|// rewrite the keystore in flush()
if|if
condition|(
operator|!
name|Shell
operator|.
name|WINDOWS
condition|)
block|{
name|Path
name|path
init|=
name|Paths
operator|.
name|get
argument_list|(
name|file
operator|.
name|getCanonicalPath
argument_list|()
argument_list|)
decl_stmt|;
name|permissions
operator|=
name|Files
operator|.
name|getPosixFilePermissions
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// On Windows, the JDK does not support the POSIX file permission APIs.
comment|// Instead, we can do a winutils call and translate.
name|String
index|[]
name|cmd
init|=
name|Shell
operator|.
name|getGetPermissionCommand
argument_list|()
decl_stmt|;
name|String
index|[]
name|args
init|=
operator|new
name|String
index|[
name|cmd
operator|.
name|length
operator|+
literal|1
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|cmd
argument_list|,
literal|0
argument_list|,
name|args
argument_list|,
literal|0
argument_list|,
name|cmd
operator|.
name|length
argument_list|)
expr_stmt|;
name|args
index|[
name|cmd
operator|.
name|length
index|]
operator|=
name|file
operator|.
name|getCanonicalPath
argument_list|()
expr_stmt|;
name|String
name|out
init|=
name|Shell
operator|.
name|execCommand
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|StringTokenizer
name|t
init|=
operator|new
name|StringTokenizer
argument_list|(
name|out
argument_list|,
name|Shell
operator|.
name|TOKEN_SEPARATOR_REGEX
argument_list|)
decl_stmt|;
comment|// The winutils output consists of 10 characters because of the leading
comment|// directory indicator, i.e. "drwx------".  The JDK parsing method expects
comment|// a 9-character string, so remove the leading character.
name|String
name|permString
init|=
name|t
operator|.
name|nextToken
argument_list|()
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|permissions
operator|=
name|PosixFilePermissions
operator|.
name|fromString
argument_list|(
name|permString
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|initFileSystem (URI uri)
specifier|protected
name|void
name|initFileSystem
parameter_list|(
name|URI
name|uri
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|initFileSystem
argument_list|(
name|uri
argument_list|)
expr_stmt|;
try|try
block|{
name|file
operator|=
operator|new
name|File
argument_list|(
operator|new
name|URI
argument_list|(
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"initialized local file as '"
operator|+
name|file
operator|+
literal|"'."
argument_list|)
expr_stmt|;
if|if
condition|(
name|file
operator|.
name|exists
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"the local file exists and is size "
operator|+
name|file
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
if|if
condition|(
name|file
operator|.
name|canRead
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"we can read the local file."
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|file
operator|.
name|canWrite
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"we can write the local file."
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"the local file does not exist."
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|flush ()
specifier|public
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|flush
argument_list|()
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Resetting permissions to '"
operator|+
name|permissions
operator|+
literal|"'"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|Shell
operator|.
name|WINDOWS
condition|)
block|{
name|Files
operator|.
name|setPosixFilePermissions
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|file
operator|.
name|getCanonicalPath
argument_list|()
argument_list|)
argument_list|,
name|permissions
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// FsPermission expects a 10-character string because of the leading
comment|// directory indicator, i.e. "drwx------". The JDK toString method returns
comment|// a 9-character string, so prepend a leading character.
name|FsPermission
name|fsPermission
init|=
name|FsPermission
operator|.
name|valueOf
argument_list|(
literal|"-"
operator|+
name|PosixFilePermissions
operator|.
name|toString
argument_list|(
name|permissions
argument_list|)
argument_list|)
decl_stmt|;
name|FileUtil
operator|.
name|setPermission
argument_list|(
name|file
argument_list|,
name|fsPermission
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * The factory to create JksProviders, which is used by the ServiceLoader.    */
DECL|class|Factory
specifier|public
specifier|static
class|class
name|Factory
extends|extends
name|CredentialProviderFactory
block|{
annotation|@
name|Override
DECL|method|createProvider (URI providerName, Configuration conf)
specifier|public
name|CredentialProvider
name|createProvider
parameter_list|(
name|URI
name|providerName
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|SCHEME_NAME
operator|.
name|equals
argument_list|(
name|providerName
operator|.
name|getScheme
argument_list|()
argument_list|)
condition|)
block|{
return|return
operator|new
name|LocalJavaKeyStoreProvider
argument_list|(
name|providerName
argument_list|,
name|conf
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
DECL|method|modeToPosixFilePermission ( int mode)
specifier|private
specifier|static
name|Set
argument_list|<
name|PosixFilePermission
argument_list|>
name|modeToPosixFilePermission
parameter_list|(
name|int
name|mode
parameter_list|)
block|{
name|Set
argument_list|<
name|PosixFilePermission
argument_list|>
name|perms
init|=
name|EnumSet
operator|.
name|noneOf
argument_list|(
name|PosixFilePermission
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|mode
operator|&
literal|0001
operator|)
operator|!=
literal|0
condition|)
block|{
name|perms
operator|.
name|add
argument_list|(
name|PosixFilePermission
operator|.
name|OTHERS_EXECUTE
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|mode
operator|&
literal|0002
operator|)
operator|!=
literal|0
condition|)
block|{
name|perms
operator|.
name|add
argument_list|(
name|PosixFilePermission
operator|.
name|OTHERS_WRITE
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|mode
operator|&
literal|0004
operator|)
operator|!=
literal|0
condition|)
block|{
name|perms
operator|.
name|add
argument_list|(
name|PosixFilePermission
operator|.
name|OTHERS_READ
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|mode
operator|&
literal|0010
operator|)
operator|!=
literal|0
condition|)
block|{
name|perms
operator|.
name|add
argument_list|(
name|PosixFilePermission
operator|.
name|GROUP_EXECUTE
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|mode
operator|&
literal|0020
operator|)
operator|!=
literal|0
condition|)
block|{
name|perms
operator|.
name|add
argument_list|(
name|PosixFilePermission
operator|.
name|GROUP_WRITE
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|mode
operator|&
literal|0040
operator|)
operator|!=
literal|0
condition|)
block|{
name|perms
operator|.
name|add
argument_list|(
name|PosixFilePermission
operator|.
name|GROUP_READ
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|mode
operator|&
literal|0100
operator|)
operator|!=
literal|0
condition|)
block|{
name|perms
operator|.
name|add
argument_list|(
name|PosixFilePermission
operator|.
name|OWNER_EXECUTE
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|mode
operator|&
literal|0200
operator|)
operator|!=
literal|0
condition|)
block|{
name|perms
operator|.
name|add
argument_list|(
name|PosixFilePermission
operator|.
name|OWNER_WRITE
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|mode
operator|&
literal|0400
operator|)
operator|!=
literal|0
condition|)
block|{
name|perms
operator|.
name|add
argument_list|(
name|PosixFilePermission
operator|.
name|OWNER_READ
argument_list|)
expr_stmt|;
block|}
return|return
name|perms
return|;
block|}
block|}
end_class

end_unit

