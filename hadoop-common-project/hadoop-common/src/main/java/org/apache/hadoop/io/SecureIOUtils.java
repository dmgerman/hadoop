begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.io
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
package|;
end_package

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
name|RandomAccessFile
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
name|FSDataInputStream
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
name|io
operator|.
name|nativeio
operator|.
name|NativeIO
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
name|nativeio
operator|.
name|NativeIO
operator|.
name|POSIX
operator|.
name|Stat
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
name|security
operator|.
name|UserGroupInformation
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
import|;
end_import

begin_comment
comment|/**  * This class provides secure APIs for opening and creating files on the local  * disk. The main issue this class tries to handle is that of symlink traversal.  *<br>  * An example of such an attack is:  *<ol>  *<li> Malicious user removes his task's syslog file, and puts a link to the  * jobToken file of a target user.</li>  *<li> Malicious user tries to open the syslog file via the servlet on the  * tasktracker.</li>  *<li> The tasktracker is unaware of the symlink, and simply streams the contents  * of the jobToken file. The malicious user can now access potentially sensitive  * map outputs, etc. of the target user's job.</li>  *</ol>  * A similar attack is possible involving task log truncation, but in that case  * due to an insecure write to a file.  *<br>  */
end_comment

begin_class
DECL|class|SecureIOUtils
specifier|public
class|class
name|SecureIOUtils
block|{
comment|/**    * Ensure that we are set up to run with the appropriate native support code.    * If security is disabled, and the support code is unavailable, this class    * still tries its best to be secure, but is vulnerable to some race condition    * attacks.    *    * If security is enabled but the support code is unavailable, throws a    * RuntimeException since we don't want to run insecurely.    */
static|static
block|{
name|boolean
name|shouldBeSecure
init|=
name|UserGroupInformation
operator|.
name|isSecurityEnabled
argument_list|()
decl_stmt|;
name|boolean
name|canBeSecure
init|=
name|NativeIO
operator|.
name|isAvailable
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|canBeSecure
operator|&&
name|shouldBeSecure
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Secure IO is not possible without native code extensions."
argument_list|)
throw|;
block|}
comment|// Pre-cache an instance of the raw FileSystem since we sometimes
comment|// do secure IO in a shutdown hook, where this call could fail.
try|try
block|{
name|rawFilesystem
operator|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
operator|.
name|getRaw
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ie
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Couldn't obtain an instance of RawLocalFileSystem."
argument_list|)
throw|;
block|}
comment|// SecureIO just skips security checks in the case that security is
comment|// disabled
name|skipSecurity
operator|=
operator|!
name|canBeSecure
expr_stmt|;
block|}
DECL|field|skipSecurity
specifier|private
specifier|final
specifier|static
name|boolean
name|skipSecurity
decl_stmt|;
DECL|field|rawFilesystem
specifier|private
specifier|final
specifier|static
name|FileSystem
name|rawFilesystem
decl_stmt|;
comment|/**    * Open the given File for random read access, verifying the expected user/    * group constraints if security is enabled.    *     * Note that this function provides no additional security checks if hadoop    * security is disabled, since doing the checks would be too expensive when    * native libraries are not available.    *     * @param f file that we are trying to open    * @param mode mode in which we want to open the random access file    * @param expectedOwner the expected user owner for the file    * @param expectedGroup the expected group owner for the file    * @throws IOException if an IO error occurred or if the user/group does    * not match when security is enabled.    */
DECL|method|openForRandomRead (File f, String mode, String expectedOwner, String expectedGroup)
specifier|public
specifier|static
name|RandomAccessFile
name|openForRandomRead
parameter_list|(
name|File
name|f
parameter_list|,
name|String
name|mode
parameter_list|,
name|String
name|expectedOwner
parameter_list|,
name|String
name|expectedGroup
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|UserGroupInformation
operator|.
name|isSecurityEnabled
argument_list|()
condition|)
block|{
return|return
operator|new
name|RandomAccessFile
argument_list|(
name|f
argument_list|,
name|mode
argument_list|)
return|;
block|}
return|return
name|forceSecureOpenForRandomRead
argument_list|(
name|f
argument_list|,
name|mode
argument_list|,
name|expectedOwner
argument_list|,
name|expectedGroup
argument_list|)
return|;
block|}
comment|/**    * Same as openForRandomRead except that it will run even if security is off.    * This is used by unit tests.    */
annotation|@
name|VisibleForTesting
DECL|method|forceSecureOpenForRandomRead (File f, String mode, String expectedOwner, String expectedGroup)
specifier|protected
specifier|static
name|RandomAccessFile
name|forceSecureOpenForRandomRead
parameter_list|(
name|File
name|f
parameter_list|,
name|String
name|mode
parameter_list|,
name|String
name|expectedOwner
parameter_list|,
name|String
name|expectedGroup
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
name|f
argument_list|,
name|mode
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|Stat
name|stat
init|=
name|NativeIO
operator|.
name|POSIX
operator|.
name|getFstat
argument_list|(
name|raf
operator|.
name|getFD
argument_list|()
argument_list|)
decl_stmt|;
name|checkStat
argument_list|(
name|f
argument_list|,
name|stat
operator|.
name|getOwner
argument_list|()
argument_list|,
name|stat
operator|.
name|getGroup
argument_list|()
argument_list|,
name|expectedOwner
argument_list|,
name|expectedGroup
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
return|return
name|raf
return|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|raf
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Opens the {@link FSDataInputStream} on the requested file on local file    * system, verifying the expected user/group constraints if security is    * enabled.    * @param file absolute path of the file    * @param expectedOwner the expected user owner for the file    * @param expectedGroup the expected group owner for the file    * @throws IOException if an IO Error occurred or the user/group does not    * match if security is enabled    */
DECL|method|openFSDataInputStream (File file, String expectedOwner, String expectedGroup)
specifier|public
specifier|static
name|FSDataInputStream
name|openFSDataInputStream
parameter_list|(
name|File
name|file
parameter_list|,
name|String
name|expectedOwner
parameter_list|,
name|String
name|expectedGroup
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|UserGroupInformation
operator|.
name|isSecurityEnabled
argument_list|()
condition|)
block|{
return|return
name|rawFilesystem
operator|.
name|open
argument_list|(
operator|new
name|Path
argument_list|(
name|file
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
return|return
name|forceSecureOpenFSDataInputStream
argument_list|(
name|file
argument_list|,
name|expectedOwner
argument_list|,
name|expectedGroup
argument_list|)
return|;
block|}
comment|/**    * Same as openFSDataInputStream except that it will run even if security is    * off. This is used by unit tests.    */
annotation|@
name|VisibleForTesting
DECL|method|forceSecureOpenFSDataInputStream ( File file, String expectedOwner, String expectedGroup)
specifier|protected
specifier|static
name|FSDataInputStream
name|forceSecureOpenFSDataInputStream
parameter_list|(
name|File
name|file
parameter_list|,
name|String
name|expectedOwner
parameter_list|,
name|String
name|expectedGroup
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|FSDataInputStream
name|in
init|=
name|rawFilesystem
operator|.
name|open
argument_list|(
operator|new
name|Path
argument_list|(
name|file
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|Stat
name|stat
init|=
name|NativeIO
operator|.
name|POSIX
operator|.
name|getFstat
argument_list|(
name|in
operator|.
name|getFileDescriptor
argument_list|()
argument_list|)
decl_stmt|;
name|checkStat
argument_list|(
name|file
argument_list|,
name|stat
operator|.
name|getOwner
argument_list|()
argument_list|,
name|stat
operator|.
name|getGroup
argument_list|()
argument_list|,
name|expectedOwner
argument_list|,
name|expectedGroup
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
return|return
name|in
return|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Open the given File for read access, verifying the expected user/group    * constraints if security is enabled.    *    * Note that this function provides no additional checks if Hadoop    * security is disabled, since doing the checks would be too expensive    * when native libraries are not available.    *    * @param f the file that we are trying to open    * @param expectedOwner the expected user owner for the file    * @param expectedGroup the expected group owner for the file    * @throws IOException if an IO Error occurred, or security is enabled and    * the user/group does not match    */
DECL|method|openForRead (File f, String expectedOwner, String expectedGroup)
specifier|public
specifier|static
name|FileInputStream
name|openForRead
parameter_list|(
name|File
name|f
parameter_list|,
name|String
name|expectedOwner
parameter_list|,
name|String
name|expectedGroup
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|UserGroupInformation
operator|.
name|isSecurityEnabled
argument_list|()
condition|)
block|{
return|return
operator|new
name|FileInputStream
argument_list|(
name|f
argument_list|)
return|;
block|}
return|return
name|forceSecureOpenForRead
argument_list|(
name|f
argument_list|,
name|expectedOwner
argument_list|,
name|expectedGroup
argument_list|)
return|;
block|}
comment|/**    * Same as openForRead() except that it will run even if security is off.    * This is used by unit tests.    */
annotation|@
name|VisibleForTesting
DECL|method|forceSecureOpenForRead (File f, String expectedOwner, String expectedGroup)
specifier|protected
specifier|static
name|FileInputStream
name|forceSecureOpenForRead
parameter_list|(
name|File
name|f
parameter_list|,
name|String
name|expectedOwner
parameter_list|,
name|String
name|expectedGroup
parameter_list|)
throws|throws
name|IOException
block|{
name|FileInputStream
name|fis
init|=
operator|new
name|FileInputStream
argument_list|(
name|f
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|Stat
name|stat
init|=
name|NativeIO
operator|.
name|POSIX
operator|.
name|getFstat
argument_list|(
name|fis
operator|.
name|getFD
argument_list|()
argument_list|)
decl_stmt|;
name|checkStat
argument_list|(
name|f
argument_list|,
name|stat
operator|.
name|getOwner
argument_list|()
argument_list|,
name|stat
operator|.
name|getGroup
argument_list|()
argument_list|,
name|expectedOwner
argument_list|,
name|expectedGroup
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
return|return
name|fis
return|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|fis
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|insecureCreateForWrite (File f, int permissions)
specifier|private
specifier|static
name|FileOutputStream
name|insecureCreateForWrite
parameter_list|(
name|File
name|f
parameter_list|,
name|int
name|permissions
parameter_list|)
throws|throws
name|IOException
block|{
comment|// If we can't do real security, do a racy exists check followed by an
comment|// open and chmod
if|if
condition|(
name|f
operator|.
name|exists
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|AlreadyExistsException
argument_list|(
literal|"File "
operator|+
name|f
operator|+
literal|" already exists"
argument_list|)
throw|;
block|}
name|FileOutputStream
name|fos
init|=
operator|new
name|FileOutputStream
argument_list|(
name|f
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|rawFilesystem
operator|.
name|setPermission
argument_list|(
operator|new
name|Path
argument_list|(
name|f
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
argument_list|,
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
name|permissions
argument_list|)
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
return|return
name|fos
return|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|fos
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Open the specified File for write access, ensuring that it does not exist.    * @param f the file that we want to create    * @param permissions we want to have on the file (if security is enabled)    *    * @throws AlreadyExistsException if the file already exists    * @throws IOException if any other error occurred    */
DECL|method|createForWrite (File f, int permissions)
specifier|public
specifier|static
name|FileOutputStream
name|createForWrite
parameter_list|(
name|File
name|f
parameter_list|,
name|int
name|permissions
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|skipSecurity
condition|)
block|{
return|return
name|insecureCreateForWrite
argument_list|(
name|f
argument_list|,
name|permissions
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|NativeIO
operator|.
name|getCreateForWriteFileOutputStream
argument_list|(
name|f
argument_list|,
name|permissions
argument_list|)
return|;
block|}
block|}
DECL|method|checkStat (File f, String owner, String group, String expectedOwner, String expectedGroup)
specifier|private
specifier|static
name|void
name|checkStat
parameter_list|(
name|File
name|f
parameter_list|,
name|String
name|owner
parameter_list|,
name|String
name|group
parameter_list|,
name|String
name|expectedOwner
parameter_list|,
name|String
name|expectedGroup
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|success
init|=
literal|true
decl_stmt|;
if|if
condition|(
name|expectedOwner
operator|!=
literal|null
operator|&&
operator|!
name|expectedOwner
operator|.
name|equals
argument_list|(
name|owner
argument_list|)
condition|)
block|{
if|if
condition|(
name|Path
operator|.
name|WINDOWS
condition|)
block|{
name|UserGroupInformation
name|ugi
init|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|expectedOwner
argument_list|)
decl_stmt|;
specifier|final
name|String
name|adminsGroupString
init|=
literal|"Administrators"
decl_stmt|;
name|success
operator|=
name|owner
operator|.
name|equals
argument_list|(
name|adminsGroupString
argument_list|)
operator|&&
name|ugi
operator|.
name|getGroups
argument_list|()
operator|.
name|contains
argument_list|(
name|adminsGroupString
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|success
operator|=
literal|false
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|success
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Owner '"
operator|+
name|owner
operator|+
literal|"' for path "
operator|+
name|f
operator|+
literal|" did not match "
operator|+
literal|"expected owner '"
operator|+
name|expectedOwner
operator|+
literal|"'"
argument_list|)
throw|;
block|}
block|}
comment|/**    * Signals that an attempt to create a file at a given pathname has failed    * because another file already existed at that path.    */
DECL|class|AlreadyExistsException
specifier|public
specifier|static
class|class
name|AlreadyExistsException
extends|extends
name|IOException
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
DECL|method|AlreadyExistsException (String msg)
specifier|public
name|AlreadyExistsException
parameter_list|(
name|String
name|msg
parameter_list|)
block|{
name|super
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
DECL|method|AlreadyExistsException (Throwable cause)
specifier|public
name|AlreadyExistsException
parameter_list|(
name|Throwable
name|cause
parameter_list|)
block|{
name|super
argument_list|(
name|cause
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

