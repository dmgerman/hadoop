begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.eclipse.dfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|eclipse
operator|.
name|dfs
package|;
end_package

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
name|DataInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutputStream
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
name|lang
operator|.
name|reflect
operator|.
name|InvocationTargetException
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
name|eclipse
operator|.
name|Activator
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
name|eclipse
operator|.
name|ErrorMessageDialog
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
name|Path
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|core
operator|.
name|resources
operator|.
name|IStorage
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|core
operator|.
name|runtime
operator|.
name|CoreException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|core
operator|.
name|runtime
operator|.
name|IPath
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|core
operator|.
name|runtime
operator|.
name|IProgressMonitor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|core
operator|.
name|runtime
operator|.
name|PlatformObject
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|core
operator|.
name|runtime
operator|.
name|Status
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jface
operator|.
name|dialogs
operator|.
name|MessageDialog
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jface
operator|.
name|operation
operator|.
name|IRunnableWithProgress
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|ui
operator|.
name|PlatformUI
import|;
end_import

begin_comment
comment|/**  * File handling methods for the DFS  */
end_comment

begin_class
DECL|class|DFSFile
specifier|public
class|class
name|DFSFile
extends|extends
name|DFSPath
implements|implements
name|DFSContent
block|{
DECL|field|length
specifier|protected
name|long
name|length
decl_stmt|;
DECL|field|replication
specifier|protected
name|short
name|replication
decl_stmt|;
comment|/**    * Constructor to upload a file on the distributed file system    *     * @param parent    * @param path    * @param file    * @param monitor    */
DECL|method|DFSFile (DFSPath parent, Path path, File file, IProgressMonitor monitor)
specifier|public
name|DFSFile
parameter_list|(
name|DFSPath
name|parent
parameter_list|,
name|Path
name|path
parameter_list|,
name|File
name|file
parameter_list|,
name|IProgressMonitor
name|monitor
parameter_list|)
block|{
name|super
argument_list|(
name|parent
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|this
operator|.
name|upload
argument_list|(
name|monitor
argument_list|,
name|file
argument_list|)
expr_stmt|;
block|}
DECL|method|DFSFile (DFSPath parent, Path path)
specifier|public
name|DFSFile
parameter_list|(
name|DFSPath
name|parent
parameter_list|,
name|Path
name|path
parameter_list|)
block|{
name|super
argument_list|(
name|parent
argument_list|,
name|path
argument_list|)
expr_stmt|;
try|try
block|{
name|FileStatus
name|fs
init|=
name|getDFS
argument_list|()
operator|.
name|getFileStatus
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|this
operator|.
name|length
operator|=
name|fs
operator|.
name|getLen
argument_list|()
expr_stmt|;
name|this
operator|.
name|replication
operator|=
name|fs
operator|.
name|getReplication
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Download and view contents of a file    *     * @return a InputStream for the file    */
DECL|method|open ()
specifier|public
name|InputStream
name|open
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|getDFS
argument_list|()
operator|.
name|open
argument_list|(
name|this
operator|.
name|path
argument_list|)
return|;
block|}
comment|/**    * Download this file to the local file system. This creates a download    * status monitor.    *     * @param file    * @throws JSchException    * @throws IOException    * @throws InvocationTargetException    * @throws InterruptedException    *     * @deprecated    */
DECL|method|downloadToLocalFile (final File file)
specifier|public
name|void
name|downloadToLocalFile
parameter_list|(
specifier|final
name|File
name|file
parameter_list|)
throws|throws
name|InvocationTargetException
throws|,
name|InterruptedException
block|{
name|PlatformUI
operator|.
name|getWorkbench
argument_list|()
operator|.
name|getProgressService
argument_list|()
operator|.
name|busyCursorWhile
argument_list|(
operator|new
name|IRunnableWithProgress
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|(
name|IProgressMonitor
name|monitor
parameter_list|)
throws|throws
name|InvocationTargetException
block|{
name|DFSFile
operator|.
name|this
operator|.
name|downloadToLocalFile
argument_list|(
name|monitor
argument_list|,
name|file
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
comment|/* @inheritDoc */
annotation|@
name|Override
DECL|method|downloadToLocalDirectory (IProgressMonitor monitor, File dir)
specifier|public
name|void
name|downloadToLocalDirectory
parameter_list|(
name|IProgressMonitor
name|monitor
parameter_list|,
name|File
name|dir
parameter_list|)
block|{
name|File
name|dfsPath
init|=
operator|new
name|File
argument_list|(
name|this
operator|.
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|File
name|destination
init|=
operator|new
name|File
argument_list|(
name|dir
argument_list|,
name|dfsPath
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|destination
operator|.
name|exists
argument_list|()
condition|)
block|{
name|boolean
name|answer
init|=
name|MessageDialog
operator|.
name|openQuestion
argument_list|(
literal|null
argument_list|,
literal|"Overwrite existing local file?"
argument_list|,
literal|"The file you are attempting to download from the DFS "
operator|+
name|this
operator|.
name|getPath
argument_list|()
operator|+
literal|", already exists in your local directory as "
operator|+
name|destination
operator|+
literal|".\n"
operator|+
literal|"Overwrite the existing file?"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|answer
condition|)
return|return;
block|}
try|try
block|{
name|this
operator|.
name|downloadToLocalFile
argument_list|(
name|monitor
argument_list|,
name|destination
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|MessageDialog
operator|.
name|openWarning
argument_list|(
literal|null
argument_list|,
literal|"Download to local file system"
argument_list|,
literal|"Downloading of file \""
operator|+
name|this
operator|.
name|path
operator|+
literal|"\" to local directory \""
operator|+
name|dir
operator|+
literal|"\" has failed.\n"
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Provides a detailed string for this file    *     * @return the string formatted as    *<tt>&lt;filename&gt; (&lt;size&gt;, r&lt;replication&gt;)</tt>    */
DECL|method|toDetailedString ()
specifier|public
name|String
name|toDetailedString
parameter_list|()
block|{
specifier|final
name|String
index|[]
name|units
init|=
block|{
literal|"b"
block|,
literal|"Kb"
block|,
literal|"Mb"
block|,
literal|"Gb"
block|,
literal|"Tb"
block|}
decl_stmt|;
name|int
name|unit
init|=
literal|0
decl_stmt|;
name|double
name|l
init|=
name|this
operator|.
name|length
decl_stmt|;
while|while
condition|(
operator|(
name|l
operator|>=
literal|1024.0
operator|)
operator|&&
operator|(
name|unit
operator|<
name|units
operator|.
name|length
operator|)
condition|)
block|{
name|unit
operator|+=
literal|1
expr_stmt|;
name|l
operator|/=
literal|1024.0
expr_stmt|;
block|}
return|return
name|String
operator|.
name|format
argument_list|(
literal|"%s (%.1f %s, r%d)"
argument_list|,
name|super
operator|.
name|toString
argument_list|()
argument_list|,
name|l
argument_list|,
name|units
index|[
name|unit
index|]
argument_list|,
name|this
operator|.
name|replication
argument_list|)
return|;
block|}
comment|/* @inheritDoc */
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|this
operator|.
name|path
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/*    *     */
comment|/**    * Download the DfsFile to a local file. Use the given monitor to report    * status of operation.    *     * @param monitor the status monitor    * @param file the local file where to put the downloaded file    * @throws InvocationTargetException    */
DECL|method|downloadToLocalFile (IProgressMonitor monitor, File file)
specifier|public
name|void
name|downloadToLocalFile
parameter_list|(
name|IProgressMonitor
name|monitor
parameter_list|,
name|File
name|file
parameter_list|)
throws|throws
name|InvocationTargetException
block|{
specifier|final
name|int
name|taskSize
init|=
literal|1024
decl_stmt|;
name|monitor
operator|.
name|setTaskName
argument_list|(
literal|"Download file "
operator|+
name|this
operator|.
name|path
argument_list|)
expr_stmt|;
name|BufferedOutputStream
name|ostream
init|=
literal|null
decl_stmt|;
name|DataInputStream
name|istream
init|=
literal|null
decl_stmt|;
try|try
block|{
name|istream
operator|=
name|getDFS
argument_list|()
operator|.
name|open
argument_list|(
name|this
operator|.
name|path
argument_list|)
expr_stmt|;
name|ostream
operator|=
operator|new
name|BufferedOutputStream
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|file
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|bytes
decl_stmt|;
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
name|taskSize
index|]
decl_stmt|;
while|while
condition|(
operator|(
name|bytes
operator|=
name|istream
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
operator|)
operator|>=
literal|0
condition|)
block|{
if|if
condition|(
name|monitor
operator|.
name|isCanceled
argument_list|()
condition|)
return|return;
name|ostream
operator|.
name|write
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
name|monitor
operator|.
name|worked
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|InvocationTargetException
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
comment|// Clean all opened resources
if|if
condition|(
name|istream
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|istream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
comment|// nothing we can do here
block|}
block|}
try|try
block|{
name|ostream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
comment|// nothing we can do here
block|}
block|}
block|}
comment|/**    * Upload a local file to this file on the distributed file system    *     * @param monitor    * @param file    */
DECL|method|upload (IProgressMonitor monitor, File file)
specifier|public
name|void
name|upload
parameter_list|(
name|IProgressMonitor
name|monitor
parameter_list|,
name|File
name|file
parameter_list|)
block|{
specifier|final
name|int
name|taskSize
init|=
literal|1024
decl_stmt|;
name|monitor
operator|.
name|setTaskName
argument_list|(
literal|"Upload file "
operator|+
name|this
operator|.
name|path
argument_list|)
expr_stmt|;
name|BufferedInputStream
name|istream
init|=
literal|null
decl_stmt|;
name|DataOutputStream
name|ostream
init|=
literal|null
decl_stmt|;
try|try
block|{
name|istream
operator|=
operator|new
name|BufferedInputStream
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
argument_list|)
expr_stmt|;
name|ostream
operator|=
name|getDFS
argument_list|()
operator|.
name|create
argument_list|(
name|this
operator|.
name|path
argument_list|)
expr_stmt|;
name|int
name|bytes
decl_stmt|;
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
name|taskSize
index|]
decl_stmt|;
while|while
condition|(
operator|(
name|bytes
operator|=
name|istream
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
operator|)
operator|>=
literal|0
condition|)
block|{
if|if
condition|(
name|monitor
operator|.
name|isCanceled
argument_list|()
condition|)
return|return;
name|ostream
operator|.
name|write
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
name|monitor
operator|.
name|worked
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|ErrorMessageDialog
operator|.
name|display
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Unable to uploade file %s to %s"
argument_list|,
name|file
argument_list|,
name|this
operator|.
name|path
argument_list|)
argument_list|,
name|e
operator|.
name|getLocalizedMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
try|try
block|{
if|if
condition|(
name|istream
operator|!=
literal|null
condition|)
name|istream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
comment|// nothing we can do here
block|}
try|try
block|{
if|if
condition|(
name|ostream
operator|!=
literal|null
condition|)
name|ostream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
comment|// nothing we can do here
block|}
block|}
block|}
comment|/* @inheritDoc */
annotation|@
name|Override
DECL|method|refresh ()
specifier|public
name|void
name|refresh
parameter_list|()
block|{
name|getParent
argument_list|()
operator|.
name|refresh
argument_list|()
expr_stmt|;
block|}
comment|/* @inheritDoc */
annotation|@
name|Override
DECL|method|computeDownloadWork ()
specifier|public
name|int
name|computeDownloadWork
parameter_list|()
block|{
return|return
literal|1
operator|+
call|(
name|int
call|)
argument_list|(
name|this
operator|.
name|length
operator|/
literal|1024
argument_list|)
return|;
block|}
comment|/**    * Creates an adapter for the file to open it in the Editor    *     * @return the IStorage    */
DECL|method|getIStorage ()
specifier|public
name|IStorage
name|getIStorage
parameter_list|()
block|{
return|return
operator|new
name|IStorageAdapter
argument_list|()
return|;
block|}
comment|/**    * IStorage adapter to open the file in the Editor    */
DECL|class|IStorageAdapter
specifier|private
class|class
name|IStorageAdapter
extends|extends
name|PlatformObject
implements|implements
name|IStorage
block|{
comment|/* @inheritDoc */
DECL|method|getContents ()
specifier|public
name|InputStream
name|getContents
parameter_list|()
throws|throws
name|CoreException
block|{
try|try
block|{
return|return
name|DFSFile
operator|.
name|this
operator|.
name|open
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|CoreException
argument_list|(
operator|new
name|Status
argument_list|(
name|Status
operator|.
name|ERROR
argument_list|,
name|Activator
operator|.
name|PLUGIN_ID
argument_list|,
literal|0
argument_list|,
literal|"Unable to open file \""
operator|+
name|DFSFile
operator|.
name|this
operator|.
name|path
operator|+
literal|"\""
argument_list|,
name|ioe
argument_list|)
argument_list|)
throw|;
block|}
block|}
comment|/* @inheritDoc */
DECL|method|getFullPath ()
specifier|public
name|IPath
name|getFullPath
parameter_list|()
block|{
return|return
operator|new
name|org
operator|.
name|eclipse
operator|.
name|core
operator|.
name|runtime
operator|.
name|Path
argument_list|(
name|DFSFile
operator|.
name|this
operator|.
name|path
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
comment|/* @inheritDoc */
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|DFSFile
operator|.
name|this
operator|.
name|path
operator|.
name|getName
argument_list|()
return|;
block|}
comment|/* @inheritDoc */
DECL|method|isReadOnly ()
specifier|public
name|boolean
name|isReadOnly
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
comment|/*    * Implementation of DFSContent    */
comment|/* @inheritDoc */
DECL|method|getChildren ()
specifier|public
name|DFSContent
index|[]
name|getChildren
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/* @inheritDoc */
DECL|method|hasChildren ()
specifier|public
name|boolean
name|hasChildren
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

